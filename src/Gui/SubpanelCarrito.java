package Gui;

import Gui.Controladores.ControladorCarrito;
import Gui.Controladores.ControladorCatalogo;
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
import tienda.Tienda;
import usuarios.Cliente;
import ventas.LineaCarrito;

/**
 * Subpanel del carrito de compra de CheckPoint. Muestra dos estados: carrito
 * vacío o carrito activo con productos. Al tramitar el pedido el carrito queda
 * vacío y se muestra un mensaje.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCarrito extends JPanel {

	/** Referencia a la ventana principal */
	private VentanaPrincipal ventana;

	/** Cliente actualmente logueado */
	private Cliente cliente;

	/** Controlador del carrito */
	private ControladorCarrito controlador;

	/** Panel donde se muestran las tarjetas de productos */
	private JPanel panelProductos;

	/** CardLayout para alternar entre carrito y detalle producto */
	private CardLayout cardLayout;

	/** Panel que contiene carrito y detalle producto */
	private JPanel panelContenido;

	/** Subpanel que muestra el detalle de un producto */
	private SubpanelProducto subpanelProducto;

	/** Etiqueta del subtotal */
	private JLabel labelSubtotal;

	/** Etiqueta del descuento */
	private JLabel labelDescuento;

	/** Etiqueta del total */
	private JLabel labelTotal;

	/** Etiqueta del tiempo restante */
	private JLabel labelTiempo;

	/** CardLayout para alternar entre vacío y activo */
	private CardLayout cardEstado;

	/** Panel que alterna entre estado vacío y activo */
	private JPanel panelEstado;

	/**
	 * Constructor del subpanel carrito.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelCarrito(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelContenido.add(crearPanelPrincipal(), "CARRITO");

		subpanelProducto = new SubpanelProducto(ventana, null);
		panelContenido.add(subpanelProducto, "PRODUCTO");

		add(panelContenido, BorderLayout.CENTER);
		cardLayout.show(panelContenido, "CARRITO");
	}

	/**
	 * Crea el panel principal con lista de productos a la izquierda y resumen a la
	 * derecha.
	 *
	 * @return El panel principal
	 */
	private JPanel crearPanelPrincipal() {
		JPanel panel = new JPanel(new BorderLayout(VentanaPrincipal.escalar(20), 0));
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		JPanel panelIzquierdo = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(10)));
		panelIzquierdo.setBackground(VentanaPrincipal.COLOR_FONDO);

		JLabel titulo = new JLabel("Tu Cesta");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelIzquierdo.add(titulo, BorderLayout.NORTH);

		cardEstado = new CardLayout();
		panelEstado = new JPanel(cardEstado);
		panelEstado.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelEstado.add(crearPanelVacio(), "VACIO");
		panelEstado.add(crearPanelProductos(), "ACTIVO");

		panelIzquierdo.add(panelEstado, BorderLayout.CENTER);
		panel.add(panelIzquierdo, BorderLayout.CENTER);
		panel.add(crearPanelResumen(), BorderLayout.EAST);

		return panel;
	}

	/**
	 * Crea el panel que se muestra cuando el carrito está vacío.
	 *
	 * @return Panel carrito vacío
	 */
	private JPanel crearPanelVacio() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);

		JLabel labelVacio = new JLabel("Tu carrito está vacío.", SwingConstants.CENTER);
		labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panel.add(labelVacio, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Crea el panel con la lista de productos del carrito activo con scroll.
	 *
	 * @return Panel con scroll de productos
	 */
	private JPanel crearPanelProductos() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelProductos = new JPanel();
		panelProductos.setLayout(new BoxLayout(panelProductos, BoxLayout.Y_AXIS));
		panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(panelProductos);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Crea el panel de resumen con subtotal, descuento, total, tiempo restante y
	 * botón tramitar.
	 *
	 * @return El panel de resumen
	 */
	private JPanel crearPanelResumen() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setPreferredSize(new Dimension(VentanaPrincipal.escalar(260), 0));
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
						VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20))));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

		// Título
		JLabel tituloResumen = new JLabel("Resumen del pedido");
		tituloResumen.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		tituloResumen.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(15), 0);
		panel.add(tituloResumen, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

		gbc.gridy = 1;
		panel.add(new JSeparator(), gbc);

		// Subtotal
		JLabel labelSubtotalTitulo = new JLabel("Subtotal:");
		labelSubtotalTitulo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelSubtotalTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 2;
		panel.add(labelSubtotalTitulo, gbc);

		labelSubtotal = new JLabel("0.00€");
		labelSubtotal.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelSubtotal.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 3;
		panel.add(labelSubtotal, gbc);

		// Descuento
		JLabel labelDescuentoTitulo = new JLabel("Descuento:");
		labelDescuentoTitulo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelDescuentoTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 4;
		panel.add(labelDescuentoTitulo, gbc);

		labelDescuento = new JLabel("Ninguno");
		labelDescuento.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelDescuento.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 5;
		panel.add(labelDescuento, gbc);

		gbc.gridy = 6;
		panel.add(new JSeparator(), gbc);

		// Total
		JLabel labelTotalTitulo = new JLabel("Total:");
		labelTotalTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelTotalTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 7;
		panel.add(labelTotalTitulo, gbc);

		labelTotal = new JLabel("0.00€");
		labelTotal.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(22)));
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 8;
		panel.add(labelTotal, gbc);

		gbc.gridy = 9;
		panel.add(new JSeparator(), gbc);

		// Tiempo restante
		labelTiempo = new JLabel("No hay carrito activo");
		labelTiempo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelTiempo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 10;
		panel.add(labelTiempo, gbc);

		// Botón tramitar
		JButton botonTramitar = new JButton("Tramitar pedido");
		botonTramitar.setFont(VentanaPrincipal.FUENTE_BOTON);
		botonTramitar.setBackground(VentanaPrincipal.COLOR_ACENTO);
		botonTramitar.setForeground(Color.WHITE);
		botonTramitar.setOpaque(true);
		botonTramitar.setBorderPainted(false);
		botonTramitar.setFocusPainted(false);
		botonTramitar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonTramitar.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15)));
		botonTramitar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botonTramitar.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				botonTramitar.setBackground(VentanaPrincipal.COLOR_ACENTO);
			}
		});
		botonTramitar.addActionListener(e -> {
			if (controlador == null || controlador.carritoVacio()) {
				JOptionPane.showMessageDialog(this, "Tu carrito está vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this,
					"¿Confirmas la reserva del pedido por " + String.format("%.2f€", controlador.getTotal()) + "?\n"
							+ "Tendrás " + Tienda.getInstancia().getTiempoMaxPago() + " minutos para pagarlo.",
					"Confirmar pedido", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				boolean ok = controlador.reservarCarrito();
				if (ok) {
					JOptionPane.showMessageDialog(this,
							"Pedido reservado correctamente.\nVe a Mis Pedidos para pagarlo.", "Pedido reservado",
							JOptionPane.INFORMATION_MESSAGE);
					// Solo actualizamos el carrito — queda vacío
					actualizar(cliente);
				} else {
					JOptionPane.showMessageDialog(this, "No se pudo reservar el pedido.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		gbc.gridy = 11;
		gbc.insets = new Insets(VentanaPrincipal.escalar(15), 0, 0, 0);
		panel.add(botonTramitar, gbc);

		// Relleno
		gbc.gridy = 12;
		gbc.weighty = 1;
		panel.add(Box.createVerticalGlue(), gbc);

		return panel;
	}

	/**
	 * Crea una tarjeta horizontal para un producto del carrito. Contiene imagen,
	 * nombre, descripción, precio unitario, spinner de cantidad, subtotal de línea,
	 * botón ver info y botón eliminar.
	 *
	 * @param linea La línea del carrito a mostrar
	 * @return El panel con la tarjeta
	 */
	private JPanel crearTarjetaProducto(LineaCarrito linea) {
		ProductoVenta producto = linea.getProducto();

		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(140)));
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		// Imagen
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(100), VentanaPrincipal.escalar(100)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(90), VentanaPrincipal.escalar(90));
		tarjeta.add(labelImagen, BorderLayout.WEST);

		// Info central
		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));

		// Nombre
		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panelInfo.add(labelNombre, gbc);

		// Descripción
		String desc = producto.getDescripcion();
		if (desc.length() > 50)
			desc = desc.substring(0, 48) + "...";
		JLabel labelDesc = new JLabel(desc);
		labelDesc.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelDesc, gbc);

		// Precio unitario
		JLabel labelPrecio = new JLabel(String.format("%.2f€ / ud", producto.getPrecioOficial()));
		labelPrecio.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panelInfo.add(labelPrecio, gbc);

		// Spinner cantidad
		int stockMaximo = linea.getCantidad() + producto.getStockDisponible();
		JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(linea.getCantidad(), 1, stockMaximo, 1));
		spinnerCantidad.setFont(VentanaPrincipal.FUENTE_NORMAL);
		spinnerCantidad.setPreferredSize(new Dimension(VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(28)));
		spinnerCantidad.addChangeListener(e -> controlador.cambiarCantidad(producto, (int) spinnerCantidad.getValue()));
		gbc.gridx = 1;
		gbc.gridy = 2;
		panelInfo.add(spinnerCantidad, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		// Botones derecha
		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		// Subtotal línea
		JLabel labelSubtotalLinea = new JLabel(String.format("%.2f€", linea.getSubtotal()), SwingConstants.RIGHT);
		labelSubtotalLinea.setFont(VentanaPrincipal.FUENTE_PRECIO);
		labelSubtotalLinea.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbcB.gridy = 0;
		panelBotones.add(labelSubtotalLinea, gbcB);

		// Botón ver información
		JButton botonVerInfo = crearBotonSecundario("Ver información");
		botonVerInfo.addActionListener(e -> verProducto(producto));
		gbcB.gridy = 1;
		panelBotones.add(botonVerInfo, gbcB);

		// Botón eliminar
		JButton botonEliminar = crearBotonRojo("Eliminar");
		botonEliminar.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar " + producto.getNombre() + " del carrito?",
					"Confirmar", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				controlador.eliminarProducto(producto);
			}
		});
		gbcB.gridy = 2;
		panelBotones.add(botonEliminar, gbcB);

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Navega a la pantalla de detalle del producto desde el carrito.
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
	 * Vuelve al carrito desde la pantalla de detalle del producto.
	 */
	public void volverDelProducto() {
		cardLayout.show(panelContenido, "CARRITO");
		actualizar(cliente);
	}

	/**
	 * Actualiza el carrito con los datos actuales del cliente. Muestra el estado
	 * vacío o activo según corresponda.
	 *
	 * @param cliente El cliente logueado
	 */
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorCarrito(this, cliente);

		if (!controlador.carritoVacio()) {
			// Carrito activo — rellenamos las tarjetas
			panelProductos.removeAll();
			List<LineaCarrito> lineas = controlador.getLineasCarrito();
			for (LineaCarrito l : lineas) {
				JPanel tarjeta = crearTarjetaProducto(l);
				tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
				panelProductos.add(tarjeta);
			}
			panelProductos.revalidate();
			panelProductos.repaint();
			cardEstado.show(panelEstado, "ACTIVO");
			actualizarResumen();
		} else {
			// Carrito vacío
			cardEstado.show(panelEstado, "VACIO");
			labelSubtotal.setText("0.00€");
			labelDescuento.setText("Ninguno");
			labelTotal.setText("0.00€");
			labelTiempo.setText("No hay carrito activo");
			labelTiempo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		}

		cardLayout.show(panelContenido, "CARRITO");
	}

	/**
	 * Actualiza las etiquetas del resumen con los valores actuales. Lo llama el
	 * controlador tras cambiar cantidades.
	 */
	public void actualizarResumen() {
		if (controlador == null)
			return;
		labelSubtotal.setText(String.format("%.2f€", controlador.getSubtotal()));
		String desc = controlador.getDescuento();
		labelDescuento.setText(desc != null ? desc : "Ninguno");
		labelTotal.setText(String.format("%.2f€", controlador.getTotal()));
		long minutos = controlador.getMinutosRestantesCarrito();
		if (controlador.carritoVacio()) {
			labelTiempo.setText("No hay carrito activo");
			labelTiempo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		} else if (minutos <= 5) {
			labelTiempo.setText("⚠ Caduca en " + minutos + " min");
			labelTiempo.setForeground(new Color(200, 50, 50));
		} else {
			labelTiempo.setText("Caduca en " + minutos + " min");
			labelTiempo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		}
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
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10),
								VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10))));
		return boton;
	}

	private JButton crearBotonRojo(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(new Color(180, 50, 50));
		boton.setForeground(Color.WHITE);
		boton.setOpaque(true);
		boton.setBorderPainted(false);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10)));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				boton.setBackground(new Color(200, 60, 60));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				boton.setBackground(new Color(180, 50, 50));
			}
		});
		return boton;
	}
}