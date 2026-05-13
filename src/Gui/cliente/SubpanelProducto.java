package Gui.cliente;

import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCatalogo;
import Gui.Controladores.cliente.ControladorProducto;
import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import productos.Reseña;
import usuarios.Cliente;

import java.awt.*;
import java.awt.event.*;

/**
 * Subpanel que muestra la información completa de un producto. Si el producto
 * es un Pack muestra además su contenido y el ahorro. 
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelProducto extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Subpanel de carrito para volver si se llegó desde él. */
	private SubpanelCarrito subpanelCarrito;

	/** Subpanel de catálogo para volver si se llegó desde él. */
	private SubpanelCatalogo subpanelCatalogo;

	/** Subpanel de pedidos para volver si se llegó desde él. */
	private SubpanelPedidos subpanelPedidos;

	/** Controlador del subpanel. */
	private ControladorProducto controlador;

	/** Producto que se está mostrando. */
	private ProductoVenta producto;

	/** Botón volver  atributo para registrar el controlador. */
	private JButton botonVolver;

	/** Botón añadir al carrito  atributo para registrar el controlador. */
	private JButton botonAñadir;

	/**
	 * Constructor del subpanel de detalle de producto.
	 *
	 * @param ventana          La ventana principal
	 * @param subpanelCatalogo El subpanel de catálogo para volver
	 */
	public SubpanelProducto(VentanaPrincipal ventana, SubpanelCatalogo subpanelCatalogo) {
		super(ventana);
		this.subpanelCatalogo = subpanelCatalogo;
	}

	/**
	 * actualzar
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * Carga el producto y construye la interfaz. Crea el controlador y lo registra
	 * en los botones. La info del producto queda fija arriba y solo las reseñas
	 * tienen scroll.
	 *
	 * @param producto            El producto a mostrar
	 * @param cliente             El cliente logueado o null
	 * @param controladorCatalogo El controlador del catálogo para añadir al carrito
	 */
	public void mostrarProducto(ProductoVenta producto, Cliente cliente, ControladorCatalogo controladorCatalogo) {
		this.producto = producto;
		this.cliente = cliente;
		this.controlador = new ControladorProducto(this, controladorCatalogo, cliente);

		removeAll();
		add(crearBarraSuperior(), BorderLayout.NORTH);

		
		JPanel panelCompleto = new JPanel(new BorderLayout());
		panelCompleto.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelCompleto.add(crearPanelCentral(), BorderLayout.NORTH);

		// Solo las reseñas tienen scroll
		panelCompleto.add(crearScrollReseñas(), BorderLayout.CENTER);

		add(panelCompleto, BorderLayout.CENTER);

		setControlador(controlador);
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en los botones 
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonVolver != null) {
			for (ActionListener al : botonVolver.getActionListeners())
				botonVolver.removeActionListener(al);
			botonVolver.addActionListener(c);
		}
		if (botonAñadir != null) {
			for (ActionListener al : botonAñadir.getActionListeners())
				botonAñadir.removeActionListener(al);
			botonAñadir.addActionListener(c);
		}
	}

	/**
	 * Crea la barra superior con botón volver. El texto varía según el subpanel de
	 * origen.
	 *
	 * @return Panel de la barra superior
	 */
	private JPanel crearBarraSuperior() {
		String textoVolver = subpanelCarrito != null ? " Volver al carrito"
				: subpanelPedidos != null ? " Volver al pedido" : " Volver al catálogo";

		
		JPanel barra = crearBarraVolver(textoVolver);
		botonVolver = getBotonVolver(barra);
		botonVolver.setActionCommand("volver");
		return barra;
	}

	/**
	 * Crea el panel central con imagen e información del producto. Si es un Pack
	 * muestra además el contenido
	 *
	 * @return Panel central del producto
	 */
	private JPanel crearPanelCentral() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(300), VentanaPrincipal.escalar(300)));
		
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(280),
				VentanaPrincipal.escalar(280));
		panel.add(labelImagen, BorderLayout.WEST);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelInfo.setBorder(BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(30), 0, 0));

		// etiqueta PACK si es un pack
		if (producto instanceof Pack) {
			JLabel labelBadge = new JLabel("PACK");
			labelBadge.setFont(VentanaPrincipal.FUENTE_BOTON);
			labelBadge.setForeground(Color.WHITE);
			labelBadge.setOpaque(true);
			labelBadge.setBackground(VentanaPrincipal.COLOR_ACENTO);
			labelBadge.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(3),
					VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(3), VentanaPrincipal.escalar(8)));
			panelInfo.add(labelBadge);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		}

		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelInfo.add(labelNombre);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));

		JLabel labelPrecio = new JLabel(String.format("%.2f€", producto.getPrecioOficial()));
		labelPrecio.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(24)));
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		panelInfo.add(labelPrecio);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		// Info específica de pack
		if (producto instanceof Pack) {
			Pack pack = (Pack) producto;
			double sumaIndividual = pack.calcularSumaProductos();
			double ahorro = sumaIndividual - pack.calcularPrecioFinal();

			
			JLabel labelPrecioOriginal = crearLabel(String.format("Precio individual: %.2f€", sumaIndividual));
			panelInfo.add(labelPrecioOriginal);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(4)));

			JLabel labelAhorro = new JLabel(String.format(" Ahorras %.2f€ comprando el pack", ahorro));
			labelAhorro.setFont(VentanaPrincipal.FUENTE_BOTON);// verde
			labelAhorro.setForeground(new Color(50, 150, 50));
			panelInfo.add(labelAhorro);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));

			// Contenido del pack
			JLabel labelContenidoTitulo = new JLabel("Contenido del pack:");
			labelContenidoTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelContenidoTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
			panelInfo.add(labelContenidoTitulo);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(6)));

			for (LineaPack linea : pack.getLineas()) {
				JPanel filaLinea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
				filaLinea.setBackground(VentanaPrincipal.COLOR_FONDO);
				filaLinea.setAlignmentX(Component.LEFT_ALIGNMENT);
				filaLinea.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(28)));

				JLabel labelLinea = crearLabel("- " + linea.getProducto().getNombre() + "  x" + linea.getUnidades()
						+ "  -  " + String.format("%.2f€", linea.getSubtotal()));
				filaLinea.add(labelLinea);
				panelInfo.add(filaLinea);
				panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(3)));
			}

			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		} else {

			JLabel labelCatTitulo = new JLabel("Categorías:");
			labelCatTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelCatTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
			panelInfo.add(labelCatTitulo);

			String categorias = producto.getCategorias().stream().map(c -> c.getNombre()).reduce((a, b) -> a + ", " + b)
					.orElse("Sin categoría");
			JLabel labelCat = crearLabel(categorias);
			panelInfo.add(labelCat);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		}

		JLabel labelStock = crearLabel("Stock disponible: " + producto.getStockDisponible());
		panelInfo.add(labelStock);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JLabel labelDescTitulo = new JLabel("Descripción:");
		labelDescTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelDescTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelInfo.add(labelDescTitulo);

		JLabel labelDesc = new JLabel("<html><p style='width:400px'>" + producto.getDescripcion() + "</p></html>");
		labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelInfo.add(labelDesc);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		
		if (!producto.getReseñas().isEmpty()) {
			JLabel labelMedia = new JLabel(String.format(" %.1f/10 (%d reseñas)", producto.getMediaPuntuacion(),
					producto.getReseñas().size()));
			labelMedia.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelMedia.setForeground(VentanaPrincipal.COLOR_ACENTO);
			panelInfo.add(labelMedia);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		} else {
			panelInfo.add(crearLabel("Sin reseñas todavía"));
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		}

		// Botón añadir al carrito
		if (controlador.hayCliente()) {
			
			botonAñadir = crearBotonNaranja("Añadir al carrito");
			botonAñadir.setActionCommand("añadirCarrito");
			panelInfo.add(botonAñadir);
		}

		panel.add(panelInfo, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Crea el scroll que contiene las reseñas del producto. La info del producto
	 * queda fija arriba y solo las reseñas deslizan.
	 *
	 * @return JScrollPane con las reseñas
	 */
	private JScrollPane crearScrollReseñas() {
		JPanel panelReseñas = new JPanel();
		panelReseñas.setLayout(new BoxLayout(panelReseñas, BoxLayout.Y_AXIS));
		panelReseñas.setBackground(VentanaPrincipal.COLOR_PANEL);
		panelReseñas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15),
				VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30)));

		JLabel labelTitulo = new JLabel("Reseñas (" + producto.getReseñas().size() + ")");
		labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelReseñas.add(labelTitulo);
		panelReseñas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		if (producto.getReseñas().isEmpty()) {
			JLabel labelVacio = crearLabel("Este producto no tiene reseñas todavía.");
			labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelReseñas.add(labelVacio);
		} else {
			for (Reseña r : producto.getReseñas()) {
				JPanel tarjeta = crearTarjetaReseña(r);
				tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
				panelReseñas.add(tarjeta);
				panelReseñas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}

		JScrollPane scroll = new JScrollPane(panelReseñas);
		scroll.setBorder(null);
		scroll.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(260)));
		scroll.setMinimumSize(new Dimension(0, VentanaPrincipal.escalar(140)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_PANEL);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		return scroll;
	}

	/**
	 * Crea una tarjeta visual para una reseña del producto.
	 *
	 * @param reseña La reseña a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaReseña(Reseña reseña) {
		JPanel tarjeta = new JPanel(new BorderLayout());
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		tarjeta.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
								VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(80)));

		JLabel labelCabecera = new JLabel(String.format(" %.1f/10", reseña.getPuntuacion()) + "  -  "
				+ (reseña.getAutor() != null ? reseña.getAutor().getNickname() : "Anónimo") + "  -  "
				+ reseña.getFecha());
		labelCabecera.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelCabecera.setForeground(VentanaPrincipal.COLOR_ACENTO);
		tarjeta.add(labelCabecera, BorderLayout.NORTH);

		JLabel labelTexto = new JLabel("<html><p style='width:600px'>" + reseña.getComentario() + "</p></html>");
		labelTexto.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelTexto.setForeground(VentanaPrincipal.COLOR_TEXTO);
		tarjeta.add(labelTexto, BorderLayout.CENTER);

		return tarjeta;
	}

	/**
	 * Muestra el diálogo para seleccionar la cantidad a añadir al carrito. 
	 */
	public void seleccionarUnidades() {
		JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, producto.getStockDisponible(), 1));
		spinnerCantidad.setFont(VentanaPrincipal.FUENTE_NORMAL);

		JPanel panelDialogo = new JPanel(new FlowLayout());
		
		panelDialogo.add(crearLabel("Cantidad (máx. " + producto.getStockDisponible() + "):"));
		panelDialogo.add(spinnerCantidad);

		int opcion = JOptionPane.showConfirmDialog(this, panelDialogo, "Añadir: " + producto.getNombre(),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (opcion == JOptionPane.OK_OPTION) {
			int cantidad = (int) spinnerCantidad.getValue();
			controlador.añadirAlCarrito(producto, cantidad);
		}
	}

	/**
	 * Vuelve al subpanel de origen.
	 */
	public void volver() {
		if (subpanelCarrito != null) {
			subpanelCarrito.volverDelProducto();
		} else if (subpanelPedidos != null) {
			subpanelPedidos.volverDelProducto();
		} else if (subpanelCatalogo != null) {
			subpanelCatalogo.volverDelProducto();
		}
	}

	/**
	 * Muestra un mensaje de éxito.
	 *
	 * @param mensaje El mensaje de éxito
	 */
	public void mostrarExito(String mensaje) {
		mostrarMensaje(mensaje);
	}

	/**
	 * Establece el subpanel de origen para saber a dónde volver.
	 *
	 * @param origen El subpanel desde el que se navega al producto
	 */
	public void setSubpanelOrigen(JPanel origen) {
		if (origen instanceof SubpanelCatalogo) {
			this.subpanelCatalogo = (SubpanelCatalogo) origen;
			this.subpanelCarrito = null;
			this.subpanelPedidos = null;
		} else if (origen instanceof SubpanelCarrito) {
			this.subpanelCarrito = (SubpanelCarrito) origen;
			this.subpanelCatalogo = null;
			this.subpanelPedidos = null;
		} else if (origen instanceof SubpanelPedidos) {
			this.subpanelPedidos = (SubpanelPedidos) origen;
			this.subpanelCatalogo = null;
			this.subpanelCarrito = null;
		}
	}
}
