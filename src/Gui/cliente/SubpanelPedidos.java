package Gui.cliente;

import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCatalogo;
import Gui.Controladores.cliente.ControladorPedidos;

import java.awt.*;
import java.util.List;
import productos.ProductoVenta;
import usuarios.Cliente;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;
import tienda.*;

/**
 * Subpanel de mis pedidos de CheckPoint. Extiende AbstractPanelCliente para
 * reutilizar helpers visuales del cliente. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPedidos extends AbstractPanelCliente {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Controlador del subpanel. */
	private ControladorPedidos controlador;

	/** CardLayout para alternar entre lista, detalle y producto. */
	private CardLayout cardLayout;

	/** Panel contenedor del CardLayout. */
	private JPanel panelContenido;

	/** Panel donde se muestran las tarjetas de los pedidos. */
	private JPanel panelListaPedidos;

	/** Subpanel de detalle de producto. */
	private SubpanelProducto subpanelProducto;

	/** Panel de detalle de un pedido concreto. */
	private JPanel panelDetallePedido;

	/** Referencia al panel cliente para navegar al pago. */
	private PanelCliente panelCliente;

	/** Botón volver del panel de detalle. */
	private JButton botonVolverDetalle;

	/**
	 * Constructor del subpanel de pedidos.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelPedidos(VentanaPrincipal ventana) {
		super(ventana);

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
	 * Actualiza el subpanel con el cliente logueado. Revisa tiempos caducados, crea
	 * el controlador y rellena la lista.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		Tienda.getInstancia().getComprobadorTiempos().revisarCarritosCaducados();
		Tienda.getInstancia().getComprobadorTiempos().revisarPedidosPendientesCaducados();
		GuardadoTienda.guardar(Tienda.getInstancia());
		this.controlador = new ControladorPedidos(this, cliente);
		rellenarLista();
		cardLayout.show(panelContenido, "LISTA");
	}

	/**
	 * Muestra la lista de pedidos. Lo llama el controlador al volver.
	 */
	public void mostrarLista() {
		rellenarLista();
		cardLayout.show(panelContenido, "LISTA");
	}

	/**
	 * Navega al pago del pedido. Lo llama el controlador.
	 *
	 * @param pedido El pedido a pagar
	 */
	public void irAPago(Pedido pedido) {
		if (panelCliente != null)
			panelCliente.mostrarPago(pedido, cliente);
	}

	/**
	 * Enlaza el panel cliente para poder navegar al pago.
	 *
	 * @param panelCliente El panel cliente
	 */
	public void setPanelCliente(PanelCliente panelCliente) {
		this.panelCliente = panelCliente;
	}

	/**
	 * Crea el panel de la lista de pedidos con scroll.
	 *
	 * @return Panel de la lista
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
		// crearScrollContenido() de AbstractPanelCliente
		JScrollPane scroll = crearScrollContenido(panelListaPedidos);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Rellena el panel de lista con las tarjetas de los pedidos del cliente.
	 */
	private void rellenarLista() {
		panelListaPedidos.removeAll();
		List<Pedido> pedidos = controlador.getPedidos();
		if (pedidos.isEmpty()) {
			// crearLabelVacio() de AbstractPanelCliente
			panelListaPedidos.add(crearLabelVacio("No tienes pedidos todavía."));
		} else {
			for (Pedido p : pedidos)
				// añadirTarjetaConSeparacion() de AbstractPanelCliente
				añadirTarjetaConSeparacion(panelListaPedidos, crearTarjetaPedido(p), 0);
		}
		panelListaPedidos.revalidate();
		panelListaPedidos.repaint();
	}

	/**
	 * Crea una tarjeta visual para un pedido de la lista. Usa crearTarjetaBase(),
	 * crearGbcTarjeta() y crearPanelBotonesTarjeta() de AbstractPanelCliente.
	 *
	 * @param pedido El pedido a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaPedido(Pedido pedido) {
		// crearTarjetaBase() de AbstractPanelCliente — MatteBorder inferior
		JPanel tarjeta = crearTarjetaBase(120, true);

		// crearPanelInfoTarjeta() y crearGbcTarjeta() de AbstractPanelCliente
		JPanel panelInfo = crearPanelInfoTarjeta();
		GridBagConstraints gbc = crearGbcTarjeta();

		JLabel labelId = new JLabel(pedido.getIdPedido());
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		JLabel labelEstado = new JLabel(controlador.getTextoEstado(pedido));
		labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelEstado.setForeground(getColorEstado(pedido.getEstado()));
		gbc.gridy = 1;
		panelInfo.add(labelEstado, gbc);

		JLabel labelTotal = new JLabel(String.format("Total: %.2f€", pedido.getTotal()));
		labelTotal.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		panelInfo.add(labelTotal, gbc);

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

		// crearPanelBotonesTarjeta() y crearGbcBotonesTarjeta() de AbstractPanelCliente
		JPanel panelBotones = crearPanelBotonesTarjeta();
		GridBagConstraints gbcB = crearGbcBotonesTarjeta();

		// crearBotonOutline() de PanelBaseInterfaz
		JButton botonVer = crearBotonOutline("Ver pedido");
		botonVer.setActionCommand("verPedido:" + pedido.getIdPedido());
		botonVer.addActionListener(controlador);
		gbcB.gridy = 0;
		panelBotones.add(botonVer, gbcB);

		if (controlador.estaPendientePago(pedido)) {
			// crearBotonNaranja() de PanelBaseInterfaz
			JButton botonPagar = crearBotonNaranja("Pagar ahora");
			botonPagar.setActionCommand("pagar:" + pedido.getIdPedido());
			botonPagar.addActionListener(controlador);
			gbcB.gridy = 1;
			panelBotones.add(botonPagar, gbcB);
		} else if (pedido.getEstado() == EstadoPedido.LISTO_PARA_RECOGER && !pedido.isRecogida_solicitada()) {
			JButton botonRecoger = crearBotonNaranja("Solicitar recogida");
			botonRecoger.setActionCommand("recoger:" + pedido.getIdPedido());
			botonRecoger.addActionListener(controlador);
			gbcB.gridy = 1;
			panelBotones.add(botonRecoger, gbcB);
		}

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Muestra el detalle de un pedido concreto. Lo llama el controlador desde
	 * actionPerformed.
	 *
	 * @param pedido El pedido a mostrar
	 */
	public void verDetallePedido(Pedido pedido) {
		panelDetallePedido.removeAll();
		panelDetallePedido.setLayout(new BorderLayout());
		panelDetallePedido.setBackground(VentanaPrincipal.COLOR_FONDO);

		// crearBarraVolver() de PanelBaseInterfaz
		JPanel barra = crearBarraVolver("← Volver a mis pedidos");
		botonVolverDetalle = getBotonVolver(barra);
		botonVolverDetalle.setActionCommand("volver");
		botonVolverDetalle.addActionListener(controlador);
		panelDetallePedido.add(barra, BorderLayout.NORTH);

		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		contenido.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

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

			// crearBotonNaranja() de PanelBaseInterfaz
			JButton botonPagar = crearBotonNaranja("Pagar ahora");
			botonPagar.setAlignmentX(Component.LEFT_ALIGNMENT);
			botonPagar.setActionCommand("pagar:" + pedido.getIdPedido());
			botonPagar.addActionListener(controlador);
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

		for (LineaPedido linea : pedido.getLineas()) {
			JPanel fila = crearFilaProducto(linea, pedido);
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
	 * Crea una fila visual para un producto dentro del detalle del pedido. Usa
	 * cargarImagen(), crearBotonOutline() y crearBotonNaranja() de
	 * PanelBaseInterfaz.
	 *
	 * @param linea  La línea del pedido
	 * @param pedido El pedido al que pertenece la línea
	 * @return Panel con la fila del producto
	 */
	private JPanel crearFilaProducto(LineaPedido linea, Pedido pedido) {
		ProductoVenta producto = linea.getProducto();

		JPanel fila = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
		fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(110)));
		fila.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));

		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(80)));
		// cargarImagen() de PanelBaseInterfaz
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(70));
		fila.add(labelImagen, BorderLayout.WEST);

		// crearPanelInfoTarjeta() y crearGbcTarjeta() de AbstractPanelCliente
		JPanel panelInfo = crearPanelInfoTarjeta();
		GridBagConstraints gbc = crearGbcTarjeta();

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

		// crearPanelBotonesTarjeta() y crearGbcBotonesTarjeta() de AbstractPanelCliente
		JPanel panelBotones = crearPanelBotonesTarjeta();
		GridBagConstraints gbcB = crearGbcBotonesTarjeta();

		// crearBotonOutline() de PanelBaseInterfaz
		JButton botonVerProducto = crearBotonOutline("Ver producto");
		botonVerProducto.addActionListener(e -> verProducto(producto));
		gbcB.gridy = 0;
		panelBotones.add(botonVerProducto, gbcB);

		if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
			if (!controlador.yaReseñó(producto)) {
				// crearBotonNaranja() de PanelBaseInterfaz
				JButton botonReseña = crearBotonNaranja("Escribir reseña");
				botonReseña.setActionCommand("reseña:" + producto.getId());
				botonReseña.addActionListener(controlador);
				gbcB.gridy = 1;
				panelBotones.add(botonReseña, gbcB);
			} else {
				JLabel labelYa = new JLabel("✓ Reseñado");
				labelYa.setFont(VentanaPrincipal.FUENTE_PEQUENA);
				labelYa.setForeground(new Color(50, 150, 50));
				labelYa.setHorizontalAlignment(SwingConstants.CENTER);
				gbcB.gridy = 1;
				panelBotones.add(labelYa, gbcB);
			}
		}

		fila.add(panelBotones, BorderLayout.EAST);
		return fila;
	}

	/**
	 * Muestra un diálogo para introducir el código de recogida. Lo llama el
	 * controlador desde actionPerformed.
	 *
	 * @param pedido El pedido a recoger
	 */
	public void mostrarDialogoRecogida(Pedido pedido) {
		String codigo = JOptionPane.showInputDialog(this,
				"Introduce el código de recogida para el pedido: " + pedido.getIdPedido(), "Confirmar Recogida",
				JOptionPane.QUESTION_MESSAGE);

		if (codigo != null && !codigo.trim().isEmpty()) {
			if (controlador.gestionarSolicitudRecogida(pedido, codigo)) {
				mostrarMensaje("¡Solicitud enviada! Ya puedes pasar a por tu pedido.");
				actualizar(cliente);
			} else {
				mostrarError("El código no coincide o el pedido no es válido.");
			}
		}
	}

	/**
	 * Muestra el formulario para escribir una reseña de un producto. Lo llama el
	 * controlador desde actionPerformed.
	 *
	 * @param producto El producto a reseñar
	 */
	public void mostrarFormularioReseña(ProductoVenta producto) {
		JSpinner spinnerPuntuacion = new JSpinner(new SpinnerNumberModel(5, 0, 10, 1));
		spinnerPuntuacion.setFont(VentanaPrincipal.FUENTE_NORMAL);

		// crearArea() de PanelBaseInterfaz
		JTextArea areaComentario = crearArea();
		areaComentario.setRows(4);

		JPanel panelForm = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.insets = new Insets(VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(5), 0);

		// crearLabel() de PanelBaseInterfaz
		gbc.gridy = 0;
		panelForm.add(crearLabel("Producto: " + producto.getNombre()), gbc);
		gbc.gridy = 1;
		panelForm.add(crearLabel("Puntuación (0-10):"), gbc);
		gbc.gridy = 2;
		panelForm.add(spinnerPuntuacion, gbc);
		gbc.gridy = 3;
		panelForm.add(crearLabel("Comentario:"), gbc);
		gbc.gridy = 4;
		panelForm.add(new JScrollPane(areaComentario), gbc);

		int opcion = JOptionPane.showConfirmDialog(this, panelForm, "Escribir reseña", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (opcion == JOptionPane.OK_OPTION) {
			String comentario = areaComentario.getText().trim();
			if (comentario.isBlank()) {
				mostrarError("El comentario no puede estar vacío.");
				return;
			}
			int puntuacion = (int) spinnerPuntuacion.getValue();
			boolean ok = controlador.escribirReseña(producto, puntuacion, comentario);
			if (ok) {
				mostrarMensaje("¡Reseña publicada correctamente!");
				actualizar(cliente);
			} else {
				mostrarError("No se pudo publicar la reseña.");
			}
		}
	}

	/**
	 * Navega al detalle de un producto desde el detalle del pedido.
	 *
	 * @param producto El producto a ver
	 */
	private void verProducto(ProductoVenta producto) {
		ControladorCatalogo controladorCatalogo = new ControladorCatalogo(cliente, null);
		subpanelProducto.setSubpanelOrigen(this);
		subpanelProducto.mostrarProducto(producto, cliente, controladorCatalogo);
		cardLayout.show(panelContenido, "PRODUCTO");
	}

	/**
	 * Vuelve al detalle del pedido desde el detalle del producto.
	 */
	public void volverDelProducto() {
		cardLayout.show(panelContenido, "DETALLE");
	}

	/**
	 * Devuelve el color asociado al estado de un pedido.
	 *
	 * @param estado El estado del pedido
	 * @return Color correspondiente al estado
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
}
