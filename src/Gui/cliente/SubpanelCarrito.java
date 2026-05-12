package Gui.cliente;

import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCarrito;
import Gui.Controladores.cliente.ControladorCatalogo;

import java.awt.*;
import java.awt.event.*;
import productos.ProductoVenta;
import usuarios.Cliente;
import ventas.LineaCarrito;

/**
 * Subpanel del carrito de compra de CheckPoint. Extiende AbstractPanelCliente
 * para reutilizar helpers visuales del cliente. Sigue el patrón MVC de los
 * apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCarrito extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Controlador del carrito. */
	private ControladorCarrito controlador;

	/** Panel donde se muestran los productos del carrito. */
	private JPanel panelProductos;

	/** CardLayout para alternar entre carrito y detalle de producto. */
	private CardLayout cardLayout;

	/** Panel contenedor del CardLayout principal. */
	private JPanel panelContenido;

	/** Subpanel de detalle de producto. */
	private SubpanelProducto subpanelProducto;

	/** Labels del resumen del pedido. */
	private JLabel labelSubtotal;
	private JLabel labelDescuento;
	private JLabel labelTotal;
	private JLabel labelTiempo;

	/** CardLayout para alternar entre carrito vacío y activo. */
	private CardLayout cardEstado;

	/** Panel contenedor del estado del carrito. */
	private JPanel panelEstado;

	/** Botón tramitar — atributo para registrar el controlador. */
	private JButton botonTramitar;

	/**
	 * Constructor del subpanel del carrito.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelCarrito(VentanaPrincipal ventana) {
		super(ventana);

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
	 * Actualiza el carrito con los datos del cliente. Crea el controlador y lo
	 * registra en los botones.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorCarrito(this, cliente);
		controlador.revisarTiempos();
		setControlador(controlador);

		if (!controlador.carritoVacio()) {
			panelProductos.removeAll();
			for (LineaCarrito l : controlador.getLineasCarrito()) {
				// añadirTarjetaConSeparacion() de AbstractPanelCliente
				panelProductos.add(crearTarjetaProducto(l));
			}
			panelProductos.revalidate();
			panelProductos.repaint();
			cardEstado.show(panelEstado, "ACTIVO");
			actualizarResumen();
		} else {
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
	 * Registra el controlador en el botón tramitar — patrón de los apuntes.
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonTramitar != null) {
			for (ActionListener al : botonTramitar.getActionListeners())
				botonTramitar.removeActionListener(al);
			botonTramitar.addActionListener(c);
		}
	}

	/**
	 * Crea el panel principal con lista de productos y resumen.
	 *
	 * @return Panel principal del carrito
	 */
	private JPanel crearPanelPrincipal() {
		JPanel panel = new JPanel(new BorderLayout(VentanaPrincipal.escalar(20), 0));
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		JPanel panelIzquierdo = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(30)));
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
	 * @return Panel de carrito vacío
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
	 * Crea el panel con scroll para los productos del carrito.
	 *
	 * @return Panel de productos con scroll
	 */
	private JPanel crearPanelProductos() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelProductos = new JPanel();
		// crearScrollContenido() de AbstractPanelCliente configura BoxLayout y scroll
		JScrollPane scroll = crearScrollContenido(panelProductos);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Crea el panel lateral de resumen del pedido.
	 *
	 * @return Panel de resumen
	 */
	private JPanel crearPanelResumen() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setPreferredSize(new Dimension(VentanaPrincipal.escalar(260), 0));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, VentanaPrincipal.escalar(3)),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
						VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20))));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;

		JLabel tituloResumen = new JLabel("Resumen del pedido");
		tituloResumen.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		tituloResumen.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(15), 0);
		panel.add(tituloResumen, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

		gbc.gridy = 1;
		panel.add(new JSeparator(), gbc);

		// crearLabel() de PanelBaseInterfaz
		gbc.gridy = 2;
		panel.add(crearLabel("Subtotal:"), gbc);

		labelSubtotal = new JLabel("0.00€");
		labelSubtotal.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelSubtotal.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 3;
		panel.add(labelSubtotal, gbc);

		gbc.gridy = 4;
		panel.add(crearLabel("Descuento:"), gbc);

		labelDescuento = new JLabel("Ninguno");
		labelDescuento.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelDescuento.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 5;
		panel.add(labelDescuento, gbc);

		gbc.gridy = 6;
		panel.add(new JSeparator(), gbc);

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

		labelTiempo = new JLabel("No hay carrito activo");
		labelTiempo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelTiempo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 10;
		panel.add(labelTiempo, gbc);

		// crearBotonNaranja() de PanelBaseInterfaz
		botonTramitar = crearBotonNaranja("Tramitar pedido");
		botonTramitar.setActionCommand("tramitar");
		gbc.gridy = 11;
		gbc.insets = new Insets(VentanaPrincipal.escalar(15), 0, 0, 0);
		panel.add(botonTramitar, gbc);

		gbc.gridy = 12;
		gbc.weighty = 1;
		panel.add(Box.createVerticalGlue(), gbc);

		return panel;
	}

	/**
	 * Crea una tarjeta visual para una línea del carrito. Usa crearTarjetaBase(),
	 * crearGbcTarjeta() y crearPanelBotonesTarjeta() de AbstractPanelCliente.
	 *
	 * @param linea La línea del carrito a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaProducto(LineaCarrito linea) {
		ProductoVenta producto = linea.getProducto();

		// crearTarjetaBase() de AbstractPanelCliente — LineBorder para carrito
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(140)));
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, VentanaPrincipal.escalar(2)),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(20),
						VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(20))));

		// Imagen — cargarImagen() de PanelBaseInterfaz
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(100), VentanaPrincipal.escalar(100)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(90), VentanaPrincipal.escalar(90));
		tarjeta.add(labelImagen, BorderLayout.WEST);

		// crearPanelInfoTarjeta() de AbstractPanelCliente
		JPanel panelInfo = crearPanelInfoTarjeta();

		// crearGbcTarjeta() de AbstractPanelCliente
		GridBagConstraints gbc = crearGbcTarjeta();

		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		panelInfo.add(labelNombre, gbc);

		String desc = producto.getDescripcion();
		if (desc.length() > 50)
			desc = desc.substring(0, 48) + "...";
		JLabel labelDesc = new JLabel(desc);
		labelDesc.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelDesc, gbc);

		JLabel labelPrecio = new JLabel(String.format("%.2f€ / ud", producto.getPrecioOficial()));
		labelPrecio.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panelInfo.add(labelPrecio, gbc);

		int stockMaximo = linea.getCantidad() + producto.getStockDisponible();
		JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(linea.getCantidad(), 1, stockMaximo, 1));
		spinnerCantidad.setFont(VentanaPrincipal.FUENTE_NORMAL);
		spinnerCantidad.setPreferredSize(new Dimension(VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(28)));
		// ChangeListener — lógica de presentación pura
		spinnerCantidad.addChangeListener(e -> controlador.cambiarCantidad(producto, (int) spinnerCantidad.getValue()));
		gbc.gridx = 1;
		gbc.gridy = 2;
		panelInfo.add(spinnerCantidad, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		// crearPanelBotonesTarjeta() y crearGbcBotonesTarjeta() de AbstractPanelCliente
		JPanel panelBotones = crearPanelBotonesTarjeta();
		GridBagConstraints gbcB = crearGbcBotonesTarjeta();

		JLabel labelSubtotalLinea = new JLabel(String.format("%.2f€", linea.getSubtotal()), SwingConstants.RIGHT);
		labelSubtotalLinea.setFont(VentanaPrincipal.FUENTE_PRECIO);
		labelSubtotalLinea.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbcB.gridy = 0;
		panelBotones.add(labelSubtotalLinea, gbcB);

		// crearBotonOutline() de PanelBaseInterfaz
		JButton botonVerInfo = crearBotonOutline("Ver información");
		botonVerInfo.setActionCommand("ver:" + producto.getId());
		botonVerInfo.addActionListener(controlador);
		gbcB.gridy = 1;
		panelBotones.add(botonVerInfo, gbcB);

		// crearBotonRojo() de PanelBaseInterfaz
		JButton botonEliminar = crearBotonRojo("Eliminar");
		botonEliminar.setActionCommand("eliminar:" + producto.getId());
		botonEliminar.addActionListener(controlador);
		gbcB.gridy = 2;
		panelBotones.add(botonEliminar, gbcB);

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Navega al detalle de un producto por su id. Lo llama el controlador.
	 *
	 * @param id Id del producto a ver
	 */
	public void verProductoPorId(String id) {
		for (LineaCarrito l : controlador.getLineasCarrito()) {
			if (l.getProducto().getId().equals(id)) {
				ControladorCatalogo cc = new ControladorCatalogo(cliente, null);
				subpanelProducto.setSubpanelOrigen(this);
				subpanelProducto.mostrarProducto(l.getProducto(), cliente, cc);
				cardLayout.show(panelContenido, "PRODUCTO");
				return;
			}
		}
	}

	/**
	 * Vuelve al carrito desde el detalle del producto.
	 */
	public void volverDelProducto() {
		cardLayout.show(panelContenido, "CARRITO");
		actualizar(cliente);
	}

	/**
	 * Muestra confirmación para tramitar el pedido. Lo llama el controlador.
	 */
	public void mostrarConfirmacionTramitar() {
		// confirmar() de AbstractPanelCliente
		if (confirmar("¿Confirmas la reserva del pedido por " + String.format("%.2f€", controlador.getTotal()) + "?\n"
				+ "Tendrás " + controlador.getTiempoMaxPago() + " minutos para pagarlo.", "Confirmar pedido")) {
			controlador.confirmarReserva();
		}
	}

	/**
	 * Muestra confirmación para eliminar un producto del carrito. Lo llama el
	 * controlador.
	 *
	 * @param producto El producto a eliminar
	 */
	public void mostrarConfirmacionEliminar(ProductoVenta producto) {
		// confirmar() de AbstractPanelCliente
		if (confirmar("¿Eliminar " + producto.getNombre() + " del carrito?", "Confirmar")) {
			controlador.eliminarProducto(producto);
		}
	}

	/**
	 * Actualiza los labels del resumen del pedido. Lo llama el controlador y el
	 * método actualizar.
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
			labelTiempo.setText("Caduca en " + minutos + " min");
			labelTiempo.setForeground(new Color(200, 50, 50));
		} else {
			labelTiempo.setText("Caduca en " + minutos + " min");
			labelTiempo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		}
	}

	/**
	 * Muestra un aviso informativo. Lo llama el controlador.
	 *
	 * @param mensaje El mensaje de aviso
	 */
	public void mostrarAviso(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
	}
}
