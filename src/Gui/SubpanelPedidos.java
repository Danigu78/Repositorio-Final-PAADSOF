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
 * Subpanel de mis pedidos de CheckPoint. Reutiliza el mismo estilo de tarjetas
 * que SubpanelCarrito.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPedidos extends JPanel {

	private VentanaPrincipal ventana;
	private Cliente cliente;
	private ControladorPedidos controlador;
	private CardLayout cardLayout;
	private JPanel panelContenido;
	private JPanel panelListaPedidos;
	private SubpanelProducto subpanelProducto;
	private JPanel panelDetallePedido;
	private PanelCliente panelCliente;

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

		panelContenido.add(crearPanelLista(), "LISTA");

		panelDetallePedido = new JPanel(new BorderLayout());
		panelDetallePedido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.add(panelDetallePedido, "DETALLE");

		subpanelProducto = new SubpanelProducto(ventana, null);
		panelContenido.add(subpanelProducto, "PRODUCTO");

		add(panelContenido, BorderLayout.CENTER);
		cardLayout.show(panelContenido, "LISTA");
	}

	/**
	 * Crea el panel con título y lista de pedidos con scroll.
	 *
	 * @return Panel de lista
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
	 * Crea una tarjeta para un pedido — mismo estilo que las tarjetas del carrito.
	 * Contiene id, estado, total, tiempo restante y botones.
	 *
	 * @param pedido El pedido a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaPedido(Pedido pedido) {
		// Mismo layout y estilo que crearTarjetaProducto en SubpanelCarrito
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(120)));
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		// Info central — mismo GridBagLayout que en carrito
		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		// ID del pedido — como el nombre en carrito
		JLabel labelId = new JLabel(pedido.getIdPedido());
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		// Estado — como la descripción en carrito
		JLabel labelEstado = new JLabel(controlador.getTextoEstado(pedido));
		labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelEstado.setForeground(getColorEstado(pedido.getEstado()));
		gbc.gridy = 1;
		panelInfo.add(labelEstado, gbc);

		// Total — como el precio en carrito
		JLabel labelTotal = new JLabel(String.format("Total: %.2f€", pedido.getTotal()));
		labelTotal.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		panelInfo.add(labelTotal, gbc);

		// Tiempo restante si está pendiente
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

		// Botones derecha — mismo estilo que en carrito
		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		JButton botonVer = crearBotonSecundario("Ver pedido");
		botonVer.addActionListener(e -> verDetallePedido(pedido));
		gbcB.gridy = 0;
		panelBotones.add(botonVer, gbcB);

		if (controlador.estaPendientePago(pedido)) {
			JButton botonPagar = crearBotonPrincipal("Pagar ahora");
			botonPagar.addActionListener(e -> {
				if (panelCliente != null) {
					panelCliente.mostrarPago(pedido, cliente);
				}
			});
			gbcB.gridy = 1;
			panelBotones.add(botonPagar, gbcB);
		}

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Muestra el detalle completo de un pedido con sus líneas. Reutiliza el mismo
	 * estilo de barra superior que SubpanelProducto.
	 *
	 * @param pedido El pedido a mostrar
	 */
	private void verDetallePedido(Pedido pedido) {
		panelDetallePedido.removeAll();
		panelDetallePedido.setLayout(new BorderLayout());
		panelDetallePedido.setBackground(VentanaPrincipal.COLOR_FONDO);

		// Barra superior — mismo estilo que SubpanelProducto
		JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
		barra.setBackground(VentanaPrincipal.COLOR_PANEL);
		barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

		JButton botonVolver = new JButton("← Volver a mis pedidos");
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

		// Contenido
		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		contenido.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		// Cabecera
		JLabel labelId = new JLabel(pedido.getIdPedido());
		labelId.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelId.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelId);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		JLabel labelEstado = new JLabel("Estado: " + controlador.getTextoEstado(pedido));
		labelEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEstado.setForeground(getColorEstado(pedido.getEstado()));
		labelEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelEstado);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		if (pedido.getDescuentoAplicado() != null) {
			JLabel labelDesc = new JLabel("Descuento: " + pedido.getDescuentoAplicado().getNombre());
			labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
			labelDesc.setForeground(VentanaPrincipal.COLOR_ACENTO);
			labelDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
			contenido.add(labelDesc);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));
		}

		if (controlador.estaPendientePago(pedido)) {
			long minutos = controlador.getMinutosRestantesPago(pedido);
			JLabel labelTiempo = new JLabel(minutos <= 5 ? "⚠ Solo quedan " + minutos + " min para pagar"
					: "Tienes " + minutos + " min para pagar");
			labelTiempo.setFont(VentanaPrincipal.FUENTE_NORMAL);
			labelTiempo.setForeground(minutos <= 5 ? new Color(200, 50, 50) : VentanaPrincipal.COLOR_TEXTO2);
			labelTiempo.setAlignmentX(Component.LEFT_ALIGNMENT);
			contenido.add(labelTiempo);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

			JButton botonPagar = crearBotonPrincipal("Pagar ahora");
			botonPagar.setAlignmentX(Component.LEFT_ALIGNMENT);
			botonPagar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Pantalla de pago próximamente.",
					"En construcción", JOptionPane.INFORMATION_MESSAGE));
			contenido.add(botonPagar);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));
		}

		JSeparator sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		sep.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(sep);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JLabel labelProductosTitulo = new JLabel("Productos:");
		labelProductosTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelProductosTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelProductosTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelProductosTitulo);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		// Líneas del pedido — mismo estilo que tarjetas del carrito
		for (LineaPedido linea : pedido.getLineas()) {
			JPanel fila = crearFilaProducto(linea);
			fila.setAlignmentX(Component.LEFT_ALIGNMENT);
			contenido.add(fila);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		}

		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		JLabel labelTotal = new JLabel(String.format("Total: %.2f€", pedido.getTotal()));
		labelTotal.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(20)));
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelTotal);

		JScrollPane scroll = new JScrollPane(contenido);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panelDetallePedido.add(scroll, BorderLayout.CENTER);

		panelDetallePedido.revalidate();
		panelDetallePedido.repaint();
		cardLayout.show(panelContenido, "DETALLE");
	}

	/**
	 * Crea una fila para un producto del pedido. Mismo estilo que
	 * crearTarjetaProducto en SubpanelCarrito.
	 *
	 * @param linea La línea del pedido
	 * @return Panel con la fila
	 */
	private JPanel crearFilaProducto(LineaPedido linea) {
		ProductoVenta producto = linea.getProducto();

		// Mismo layout que tarjeta del carrito
		JPanel fila = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
		fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(100)));
		fila.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));

		// Imagen — mismo tamaño que en carrito
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(80)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(70));
		fila.add(labelImagen, BorderLayout.WEST);

		// Info — mismo GridBagLayout que en carrito
		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
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

		// Botones derecha — mismo estilo que en carrito
		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		JButton botonVerProducto = crearBotonSecundario("Ver producto");
		botonVerProducto.addActionListener(e -> verProducto(producto));
		gbcB.gridy = 0;
		panelBotones.add(botonVerProducto, gbcB);

		fila.add(panelBotones, BorderLayout.EAST);
		return fila;
	}

	/**
	 * Navega al detalle del producto.
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
	 * Vuelve al detalle del pedido desde la pantalla de producto.
	 */
	public void volverDelProducto() {
		cardLayout.show(panelContenido, "DETALLE");
	}

	/**
	 * Devuelve el color del estado del pedido.
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
			JLabel labelVacio = new JLabel("No tienes pedidos todavía.");
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

	public void setPanelCliente(PanelCliente panelCliente) {
		this.panelCliente = panelCliente;
	}
}