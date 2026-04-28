package Gui;

import Gui.Controladores.ControladorCatalogo;
import Gui.Controladores.ControladorPedidos;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import productos.ProductoVenta;
import usuarios.Cliente;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;
import tienda.*;

/**
 * Subpanel de mis pedidos de CheckPoint. Muestra todos los pedidos del cliente
 * como tarjetas. Cada tarjeta tiene el estado, total, tiempo restante si está
 * pendiente y botones de ver detalle y pagar. Al ver detalle muestra las líneas
 * del pedido con botón ver producto.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPedidos extends JPanel {

	/** Referencia a la ventana principal */
	private VentanaPrincipal ventana;

	/** Cliente actualmente logueado */
	private Cliente cliente;

	/** Controlador de pedidos */
	private ControladorPedidos controlador;

	/** CardLayout para alternar entre lista y detalle */
	private CardLayout cardLayout;

	/** Panel contenedor */
	private JPanel panelContenido;

	/** Panel con la lista de pedidos */
	private JPanel panelListaPedidos;

	/** SubpanelProducto para ver detalle de producto */
	private SubpanelProducto subpanelProducto;

	/** Panel de detalle del pedido seleccionado */
	private JPanel panelDetallePedido;

	/**
	 * Constructor del subpanel de pedidos.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelPedidos(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);

		// Panel lista de pedidos
		panelContenido.add(crearPanelLista(), "LISTA");

		// Panel detalle pedido se rellena al pulsar ver pedido
		panelDetallePedido = new JPanel(new BorderLayout());
		panelDetallePedido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.add(panelDetallePedido, "DETALLE");

		// SubpanelProducto para ver detalle de producto desde pedido
		subpanelProducto = new SubpanelProducto(ventana, null);
		panelContenido.add(subpanelProducto, "PRODUCTO");

		add(panelContenido, BorderLayout.CENTER);
		cardLayout.show(panelContenido, "LISTA");// inicialmente muestras la lista de pedidos
	}

	/**
	 * Crea el panel principal con título y lista de pedidos con scroll.
	 *
	 * @return Panel de lista de pedidos
	 */
	private JPanel crearPanelLista() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		JLabel titulo = new JLabel("Mis Pedidos");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(15), 0));
		panel.add(titulo, BorderLayout.NORTH);

		panelListaPedidos = new JPanel();
		panelListaPedidos.setLayout(new BoxLayout(panelListaPedidos, BoxLayout.Y_AXIS));
		panelListaPedidos.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(panelListaPedidos);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Crea una tarjeta para un pedido con su estado, total, tiempo restante si está
	 * pendiente y botones de acción.
	 *
	 * @param pedido El pedido a mostrar
	 * @return Panel con la tarjeta del pedido
	 */
	private JPanel crearTarjetaPedido(Pedido pedido) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(10), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(110)));
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		// Info izquierda
		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(20));
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;

		// ID del pedido
		JLabel labelId = new JLabel(pedido.getIdPedido());
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		// Estado con color según el estado
		JLabel labelEstado = new JLabel(controlador.getTextoEstado(pedido));
		labelEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEstado.setForeground(getColorEstado(pedido.getEstado()));
		gbc.gridy = 1;
		panelInfo.add(labelEstado, gbc);

		// Total
		JLabel labelTotal = new JLabel(String.format("Total: %.2f€", pedido.getTotal()));
		labelTotal.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		panelInfo.add(labelTotal, gbc);

		// Tiempo restante si está pendiente de pago
		if (controlador.estaPendientePago(pedido)) {
			long minutos = controlador.getMinutosRestantesPago(pedido);
			JLabel labelTiempo = new JLabel(minutos <= 5 ? "⚠ Solo quedan " + minutos + " min para pagar"
					: "Tiempo para pagar: " + minutos + " min");
			labelTiempo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelTiempo.setForeground(minutos <= 5 ? new Color(200, 50, 50) : VentanaPrincipal.COLOR_TEXTO2);
			gbc.gridy = 3;
			panelInfo.add(labelTiempo, gbc);
		}

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		// Botones derecha
		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		// Botón ver pedido
		JButton botonVer = crearBotonSecundario("Ver pedido");
		botonVer.addActionListener(e -> verDetallePedido(pedido));
		gbcB.gridy = 0;
		panelBotones.add(botonVer, gbcB);

		// Botón pagar — solo si está pendiente de pago
		if (controlador.estaPendientePago(pedido)) {
			JButton botonPagar = crearBotonPrincipal("Pagar ahora");
			botonPagar.addActionListener(e -> {
				// TODO: navegar a pantalla de pago
				JOptionPane.showMessageDialog(this, "Pantalla de pago próximamente.", "En construcción",
						JOptionPane.INFORMATION_MESSAGE);
			});
			gbcB.gridy = 1;
			panelBotones.add(botonPagar, gbcB);
		}

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Muestra el detalle completo de un pedido con sus líneas y botones para ver
	 * cada producto.
	 *
	 * @param pedido El pedido a mostrar en detalle
	 */
	private void verDetallePedido(Pedido pedido) {
		panelDetallePedido.removeAll();
		panelDetallePedido.setLayout(new BorderLayout());
		panelDetallePedido.setBackground(VentanaPrincipal.COLOR_FONDO);

		// Barra superior con botón volver
		JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
		barra.setBackground(VentanaPrincipal.COLOR_PANEL);
		barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

		JButton botonVolver = new JButton(" Volver a mis pedidos");
		botonVolver.setFont(VentanaPrincipal.FUENTE_NORMAL);
		botonVolver.setForeground(VentanaPrincipal.COLOR_TEXTO);
		botonVolver.setBackground(VentanaPrincipal.COLOR_PANEL);
		botonVolver.setOpaque(true);
		botonVolver.setBorderPainted(true);
		botonVolver.setFocusPainted(false);
		botonVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonVolver.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(15),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(15))));
		botonVolver.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botonVolver.setForeground(VentanaPrincipal.COLOR_ACENTO);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				botonVolver.setForeground(VentanaPrincipal.COLOR_TEXTO);
			}
		});
		botonVolver.addActionListener(e -> cardLayout.show(panelContenido, "LISTA"));
		barra.add(botonVolver);
		panelDetallePedido.add(barra, BorderLayout.NORTH);

		// Contenido del detalle
		JPanel panelContenidoDetalle = new JPanel();
		panelContenidoDetalle.setLayout(new BoxLayout(panelContenidoDetalle, BoxLayout.Y_AXIS));
		panelContenidoDetalle.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenidoDetalle.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		// Cabecera del pedido
		JLabel labelId = new JLabel(pedido.getIdPedido());
		labelId.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelId.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContenidoDetalle.add(labelId);
		panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		JLabel labelEstado = new JLabel("Estado: " + controlador.getTextoEstado(pedido));
		labelEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEstado.setForeground(getColorEstado(pedido.getEstado()));
		labelEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContenidoDetalle.add(labelEstado);
		panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		// Descuento si lo hay
		if (pedido.getDescuentoAplicado() != null) {
			JLabel labelDescuento = new JLabel("Descuento aplicado: " + pedido.getDescuentoAplicado().getNombre());
			labelDescuento.setFont(VentanaPrincipal.FUENTE_NORMAL);
			labelDescuento.setForeground(VentanaPrincipal.COLOR_ACENTO);
			labelDescuento.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelContenidoDetalle.add(labelDescuento);
			panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));
		}

		// Tiempo restante si está pendiente
		if (controlador.estaPendientePago(pedido)) {
			long minutos = controlador.getMinutosRestantesPago(pedido);
			JLabel labelTiempo = new JLabel(minutos <= 5 ? "⚠ Solo quedan " + minutos + " min para pagar"
					: "Tienes " + minutos + " min para pagar");
			labelTiempo.setFont(VentanaPrincipal.FUENTE_NORMAL);
			labelTiempo.setForeground(minutos <= 5 ? new Color(200, 50, 50) : VentanaPrincipal.COLOR_TEXTO2);
			labelTiempo.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelContenidoDetalle.add(labelTiempo);
			panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

			// Botón pagar
			JButton botonPagar = crearBotonPrincipal("Pagar ahora");
			botonPagar.setAlignmentX(Component.LEFT_ALIGNMENT);
			botonPagar.addActionListener(e -> {
				// TODO: navegar a pantalla de pago
				JOptionPane.showMessageDialog(this, "Pantalla de pago próximamente.", "En construcción",
						JOptionPane.INFORMATION_MESSAGE);
			});
			panelContenidoDetalle.add(botonPagar);
			panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));
		}

		// Separador
		JSeparator sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		sep.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContenidoDetalle.add(sep);
		panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		// Título productos
		JLabel labelProductosTitulo = new JLabel("Productos del pedido:");
		labelProductosTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelProductosTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelProductosTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContenidoDetalle.add(labelProductosTitulo);
		panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		// Tarjeta por cada línea del pedido
		for (LineaPedido linea : pedido.getLineas()) {
			JPanel filaProducto = crearFilaProductoPedido(linea);
			filaProducto.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelContenidoDetalle.add(filaProducto);
			panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		}

		// Total al final
		panelContenidoDetalle.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		JLabel labelTotal = new JLabel(String.format("Total: %.2f€", pedido.getTotal()));
		labelTotal.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(20)));
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContenidoDetalle.add(labelTotal);

		JScrollPane scroll = new JScrollPane(panelContenidoDetalle);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panelDetallePedido.add(scroll, BorderLayout.CENTER);

		panelDetallePedido.revalidate();
		panelDetallePedido.repaint();
		cardLayout.show(panelContenido, "DETALLE");
	}

	/**
	 * Crea una fila para un producto del pedido con imagen, nombre, cantidad,
	 * precio y botón ver información.
	 *
	 * @param linea La línea del pedido a mostrar
	 * @return Panel con la fila del producto
	 */
	private JPanel crearFilaProductoPedido(LineaPedido linea) {
		ProductoVenta producto = linea.getProducto();

		JPanel fila = new JPanel(new BorderLayout(VentanaPrincipal.escalar(10), 0));
		fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
		fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(90)));
		fila.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10),
						VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10))));

		// Imagen pequeña
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(70)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(60), VentanaPrincipal.escalar(60));
		fila.add(labelImagen, BorderLayout.WEST);

		// Info
		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2), 0);

		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panelInfo.add(labelNombre, gbc);

		JLabel labelCantidad = new JLabel(
				"x" + linea.getCantidad() + "  —  " + String.format("%.2f€ / ud", linea.getPrecioVenta()));
		labelCantidad.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelCantidad.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelCantidad, gbc);

		JLabel labelSubtotal = new JLabel(String.format("%.2f€", linea.getSubtotal()));
		labelSubtotal.setFont(VentanaPrincipal.FUENTE_PRECIO);
		labelSubtotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		panelInfo.add(labelSubtotal, gbc);

		fila.add(panelInfo, BorderLayout.CENTER);

		// Botón ver información del producto
		JButton botonVerProducto = crearBotonSecundario("Ver producto");
		botonVerProducto.addActionListener(e -> verProducto(producto));
		fila.add(botonVerProducto, BorderLayout.EAST);

		return fila;
	}

	/**
	 * Navega a la pantalla de detalle del producto desde el pedido.
	 *
	 * @param producto El producto a mostrar
	 */
	private void verProducto(ProductoVenta producto) {
		ControladorCatalogo controladorCatalogo = new ControladorCatalogo(cliente, null);
		subpanelProducto.setSubpanelOrigen(this);
		subpanelProducto.mostrarProducto(producto, cliente, controladorCatalogo);
		cardLayout.show(panelContenido, "PRODUCTO");
	}

	/**
	 * Devuelve el color correspondiente al estado del pedido.
	 *
	 * @param estado El estado del pedido
	 * @return El color asociado
	 */
	private Color getColorEstado(EstadoPedido estado) {
		switch (estado) {
		case PENDIENTE_PAGO:
			return new Color(200, 150, 0);
		case PAGADO:
			return new Color(50, 150, 50);
		case LISTO_PARA_RECOGER:
			return new Color(0, 120, 200);
		case ENTREGADO:
			return new Color(50, 180, 50);
		case CANCELADO:
			return new Color(180, 50, 50);
		default:
			return VentanaPrincipal.COLOR_TEXTO2;
		}
	}

	/**
	 * Actualiza el subpanel con los pedidos del cliente.
	 *
	 * @param cliente El cliente logueado
	 */
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;

		Tienda.getInstancia().getComprobadorTiempos().revisarCarritosCaducados();
		Tienda.getInstancia().getComprobadorTiempos().revisarPedidosPendientesCaducados();

		this.controlador = new ControladorPedidos(this, cliente);

		panelListaPedidos.removeAll();

		List<Pedido> pedidos = controlador.getPedidos();
		if (pedidos.isEmpty()) {
			JLabel labelVacio = new JLabel("No tienes pedidos todavía.", SwingConstants.CENTER);
			labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelListaPedidos.add(labelVacio);
		} else {
			for (Pedido p : pedidos) {
				JPanel tarjeta = crearTarjetaPedido(p);
				tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
				panelListaPedidos.add(tarjeta);
			}
		}

		panelListaPedidos.revalidate();
		panelListaPedidos.repaint();
		cardLayout.show(panelContenido, "LISTA");
	}

	/**
	 * Carga una imagen desde src/fotos/.
	 *
	 * @param label  JLabel donde cargar la imagen
	 * @param nombre Nombre del archivo
	 * @param ancho  Ancho deseado
	 * @param alto   Alto deseado
	 */
	private void cargarImagen(JLabel label, String nombre, int ancho, int alto) {
		try {
			URL url = getClass().getResource("/fotos/" + nombre);
			if (url != null) {
				BufferedImage img = ImageIO.read(url);
				if (img != null) {
					Image imgEscalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
					label.setIcon(new ImageIcon(imgEscalada));
				} else {
					label.setText("Sin imagen");
					label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
				}
			} else {
				label.setText("Sin imagen");
				label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			}
		} catch (IOException e) {
			label.setText("Sin imagen");
			label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		}
	}

	private JButton crearBotonPrincipal(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
		boton.setForeground(Color.WHITE);
		boton.setOpaque(true);
		boton.setBorderPainted(false);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
				VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15)));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				boton.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
			}
		});
		return boton;
	}

	private JButton crearBotonSecundario(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(VentanaPrincipal.COLOR_PANEL);
		boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
		boton.setBorderPainted(true);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12))));
		return boton;
	}

	/**
	 * Vuelve al detalle del pedido desde la pantalla de producto.
	 */
	public void volverDelProducto() {
		cardLayout.show(panelContenido, "DETALLE");
	}
}