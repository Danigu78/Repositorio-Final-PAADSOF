package Gui.cliente;

import Gui.AbstractPanelSection;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCatalogo;
import Gui.Controladores.cliente.ControladorProducto;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import productos.ProductoVenta;
import productos.Reseña;
import tienda.Tienda;
import usuarios.Cliente;

/**
 * Subpanel que muestra la información completa de un producto. Extiende
 * AbstractPanelSection para reutilizar helpers visuales. Sigue el patrón MVC de
 * los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelProducto extends AbstractPanelSection {

	private SubpanelCarrito subpanelCarrito;
	private SubpanelCatalogo subpanelCatalogo;
	private SubpanelPedidos subpanelPedidos;
	private ControladorProducto controlador;
	private ProductoVenta producto;
	private Cliente cliente;

	// Botones — atributos para registrar el controlador
	private JButton botonVolver;
	private JButton botonAñadir;

	public SubpanelProducto(VentanaPrincipal ventana, SubpanelCatalogo subpanelCatalogo) {
		super(ventana);
		this.subpanelCatalogo = subpanelCatalogo;
	}

	/**
	 * Carga el producto y construye la interfaz. Crea el controlador y lo registra
	 * en los botones.
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
		panelCompleto.add(crearPanelReseñas(), BorderLayout.CENTER);

		JScrollPane scrollCompleto = new JScrollPane(panelCompleto);
		scrollCompleto.setBorder(null);
		scrollCompleto.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		scrollCompleto.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollCompleto.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		add(scrollCompleto, BorderLayout.CENTER);

		setControlador(controlador);
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes.
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

	private JPanel crearBarraSuperior() {
		String textoVolver = subpanelCarrito != null ? "← Volver al carrito"
				: subpanelPedidos != null ? "← Volver al pedido" : "← Volver al catálogo";

		// crearBarraVolver() de AbstractPanelSection
		JPanel barra = crearBarraVolver(textoVolver);
		botonVolver = getBotonVolver(barra);
		botonVolver.setActionCommand("volver");
		return barra;
	}

	private JPanel crearPanelCentral() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(300), VentanaPrincipal.escalar(300)));
		// cargarImagen() de AbstractPanelSection
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(280),
				VentanaPrincipal.escalar(280));
		panel.add(labelImagen, BorderLayout.WEST);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelInfo.setBorder(BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(30), 0, 0));

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

		// crearLabel() de AbstractPanelSection
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

		JLabel labelCatTitulo = new JLabel("Categorías:");
		labelCatTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelCatTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelInfo.add(labelCatTitulo);

		String categorias = producto.getCategorias().stream().map(c -> c.getNombre()).reduce((a, b) -> a + ", " + b)
				.orElse("Sin categoría");
		JLabel labelCat = crearLabel(categorias);
		panelInfo.add(labelCat);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		if (!producto.getReseñas().isEmpty()) {
			JLabel labelMedia = new JLabel(String.format("⭐ %.1f/10 (%d reseñas)", producto.getMediaPuntuacion(),
					producto.getReseñas().size()));
			labelMedia.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelMedia.setForeground(VentanaPrincipal.COLOR_ACENTO);
			panelInfo.add(labelMedia);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		} else {
			panelInfo.add(crearLabel("Sin reseñas todavía"));
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		}

		if (controlador.hayCliente()) {
			// crearBotonNaranja() de AbstractPanelSection
			botonAñadir = crearBotonNaranja("Añadir al carrito");
			botonAñadir.setActionCommand("añadirCarrito");
			panelInfo.add(botonAñadir);
		}

		panel.add(panelInfo, BorderLayout.CENTER);
		return panel;
	}

	private JPanel crearPanelReseñas() {
		JPanel panelReseñas = new JPanel();
		panelReseñas.setLayout(new BoxLayout(panelReseñas, BoxLayout.Y_AXIS));
		panelReseñas.setBackground(VentanaPrincipal.COLOR_PANEL);
		panelReseñas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15),
				VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30)));

		// Título con número de reseñas
		JLabel labelTitulo = new JLabel("Reseñas (" + producto.getReseñas().size() + ")");
		labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelReseñas.add(labelTitulo);
		panelReseñas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		// Lista de reseñas o mensaje vacío
		if (producto.getReseñas().isEmpty()) {
			JLabel labelVacio = crearLabel("Este producto no tiene reseñas todavía.");
			labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelReseñas.add(labelVacio);
		} else {
			for (Reseña r : producto.getReseñas()) {
				panelReseñas.add(crearTarjetaReseña(r));
				panelReseñas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}

		return panelReseñas;
	}

	private JPanel crearTarjetaReseña(Reseña reseña) {
		JPanel tarjeta = new JPanel(new BorderLayout());
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		tarjeta.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
								VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(80)));
		JLabel labelCabecera = new JLabel(String.format(" %.1f/10", reseña.getPuntuacion()) + "  —  "
				+ (reseña.getAutor() != null ? reseña.getAutor().getNickname() : "Anónimo") + "  —  "
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

	public void volver() {
		if (subpanelCarrito != null) {
			subpanelCarrito.volverDelProducto();
		} else if (subpanelPedidos != null) {
			subpanelPedidos.volverDelProducto();
		} else if (subpanelCatalogo != null) {
			subpanelCatalogo.volverDelProducto();
		}
	}

	public void mostrarExito(String mensaje) {
		mostrarMensaje(mensaje);
	}

	@Override
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

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