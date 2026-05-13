package Gui.cliente;

import productos.Producto2Mano;
import usuarios.Cliente;
import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCrearOferta;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Subpanel para crear una oferta de intercambio. Muestra dos columnas: los
 * productos del cliente y los del propietario. El usuario selecciona con
 * checkboxes qué productos incluir en la oferta.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCrearOferta extends AbstractPanelCliente {

	
	private static final long serialVersionUID = 1L;

	/** Subpanel de detalle del producto para volver. */
	private SubpanelProducto2Mano subpanelOrigen;

	/** Controlador del subpanel. */
	private ControladorCrearOferta controlador;

	/** Producto del propietario al que se hace la oferta. */
	private Producto2Mano productoObjetivo;

	/** Botón volver  atributo para registrar el controlador. */
	private JButton botonVolver;

	/** Botón enviar oferta  atributo para registrar el controlador. */
	private JButton botonEnviar;

	/** Checkboxes de mis productos  paralelo a misProductos. */
	private List<JCheckBox> checkboxesMios;

	/** Checkboxes de los productos del propietario paralelo a susProductos. */
	private List<JCheckBox> checkboxesSuyos;

	/** Lista de mis productos disponibles para ofertar. */
	private List<Producto2Mano> misProductos;

	/** Lista de productos del propietario disponibles. */
	private List<Producto2Mano> susProductos;

	/**
	 * Constructor del subpanel de crear oferta.
	 *
	 * @param ventana          La ventana principal
	 * @param subpanelOrigen   El subpanel de detalle para volver
	 * @param productoObjetivo El producto del propietario
	 * @param cliente          El cliente logueado
	 */
	public SubpanelCrearOferta(VentanaPrincipal ventana, SubpanelProducto2Mano subpanelOrigen,
			Producto2Mano productoObjetivo, Cliente cliente) {
		super(ventana);
		this.subpanelOrigen = subpanelOrigen;
		this.productoObjetivo = productoObjetivo;
		this.cliente = cliente;
		this.controlador = new ControladorCrearOferta(this, cliente, productoObjetivo);

		construirUI();
		setControlador(controlador);
	}

	/**
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * Registra el controlador en los botones  .
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonVolver != null) {
			for (ActionListener al : botonVolver.getActionListeners())
				botonVolver.removeActionListener(al);
			botonVolver.addActionListener(c);
		}
		if (botonEnviar != null) {
			for (ActionListener al : botonEnviar.getActionListeners())
				botonEnviar.removeActionListener(al);
			botonEnviar.addActionListener(c);
		}
	}

	/**
	 * Construye toda la interfaz del subpanel.
	 */
	private void construirUI() {
		removeAll();
		add(crearBarra(), BorderLayout.NORTH);
		add(crearPanelOferta(), BorderLayout.CENTER);
		add(crearPanelBotonEnviar(), BorderLayout.SOUTH);
		revalidate();
		repaint();
	}

	/**
	 * Crea la barra superior con botón volver. 
	 *
	 * @return Panel de la barra superior
	 */
	private JPanel crearBarra() {
		
		JPanel barra = crearBarraVolver(" Volver al producto");
		botonVolver = getBotonVolver(barra);
		botonVolver.setActionCommand("volver");
		return barra;
	}

	/**
	 * Crea el panel central con las dos columnas de productos.
	 *
	 * @return Panel con las dos columnas
	 */
	private JPanel crearPanelOferta() {
		JPanel panel = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(20), 0));
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));

		misProductos = controlador.getMisProductosDisponibles();
		checkboxesMios = new ArrayList<>();
		panel.add(crearColumnaProductos("Mis productos para ofrecer", misProductos, checkboxesMios,
				"No tienes productos tasados y visibles para ofertar."));

		susProductos = controlador.getProductosPropietario();
		checkboxesSuyos = new ArrayList<>();
		panel.add(crearColumnaProductos("Productos de " + productoObjetivo.getPropietario().getNickname(), susProductos,
				checkboxesSuyos, "Este usuario no tiene productos disponibles."));

		return panel;
	}

	/**
	 * Crea una columna con título, checkboxes y scroll.
	 *
	 * @param titulo       Título de la columna
	 * @param productos    Lista de productos a mostrar
	 * @param checkboxes   Lista donde guardar los checkboxes creados
	 * @param mensajeVacio Mensaje si no hay productos
	 * @return Panel de la columna
	 */
	private JPanel crearColumnaProductos(String titulo, List<Producto2Mano> productos, List<JCheckBox> checkboxes,
			String mensajeVacio) {
		JPanel columna = new JPanel(new BorderLayout());
		columna.setBackground(VentanaPrincipal.COLOR_PANEL);
		columna.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
								VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		JLabel labelTitulo = new JLabel(titulo);
		labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(10), 0));
		columna.add(labelTitulo, BorderLayout.NORTH);

		JPanel panelLista = new JPanel();
		panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
		panelLista.setBackground(VentanaPrincipal.COLOR_PANEL);

		if (productos.isEmpty()) {
			
			JLabel labelVacio = crearLabel(mensajeVacio);
			labelVacio.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			panelLista.add(labelVacio);
		} else {
			for (Producto2Mano p : productos) {
				JPanel fila = new JPanel(new BorderLayout(VentanaPrincipal.escalar(10), 0));
				fila.setBackground(VentanaPrincipal.COLOR_PANEL);
				fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(40)));
				fila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

				String info = p.getNombre() + " (" + p.getEstadoProducto() + ")";
				JCheckBox checkbox = new JCheckBox(info);
				checkbox.setFont(VentanaPrincipal.FUENTE_NORMAL);
				checkbox.setForeground(VentanaPrincipal.COLOR_TEXTO);
				checkbox.setBackground(VentanaPrincipal.COLOR_PANEL);
				checkboxes.add(checkbox);
				fila.add(checkbox, BorderLayout.WEST);

				if (p.getValoracion() != null) {
				
					JLabel labelPrecio = crearLabel(String.format("%.2f€", p.getValoracion().getPrecioTasacion()));
					labelPrecio.setFont(VentanaPrincipal.FUENTE_PEQUENA);
					labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
					fila.add(labelPrecio, BorderLayout.EAST);
				}

				panelLista.add(fila);
				panelLista.add(Box.createVerticalStrut(VentanaPrincipal.escalar(4)));
			}
		}

		JScrollPane scroll = new JScrollPane(panelLista);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_PANEL);
		columna.add(scroll, BorderLayout.CENTER);

		return columna;
	}

	/**
	 * Crea el panel inferior con el botón de enviar oferta.
	 *
	 * @return Panel del botón enviar
	 */
	private JPanel crearPanelBotonEnviar() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), 0, VentanaPrincipal.escalar(20), 0));

		
		botonEnviar = crearBotonNaranja("Enviar oferta");
		botonEnviar.setActionCommand("enviar");
		botonEnviar.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(30)));
		panel.add(botonEnviar);

		return panel;
	}

	/**
	 * Devuelve los productos del cliente seleccionados. 
	 *
	 * @return Lista de productos seleccionados
	 */
	public List<Producto2Mano> getMisProductosSeleccionados() {
		List<Producto2Mano> seleccionados = new ArrayList<>();
		for (int i = 0; i < checkboxesMios.size(); i++)
			if (checkboxesMios.get(i).isSelected())
				seleccionados.add(misProductos.get(i));
		return seleccionados;
	}

	/**
	 * Devuelve los productos del propietario seleccionados.
	 *
	 * @return Lista de productos seleccionados
	 */
	public List<Producto2Mano> getSusProductosSeleccionados() {
		List<Producto2Mano> seleccionados = new ArrayList<>();
		for (int i = 0; i < checkboxesSuyos.size(); i++)
			if (checkboxesSuyos.get(i).isSelected())
				seleccionados.add(susProductos.get(i));
		return seleccionados;
	}

	/**
	 * Vuelve al detalle del producto.
	 */
	public void volver() {
		subpanelOrigen.volverAlDetalle();
	}

	/**
	 * Muestra un mensaje de éxito.
	 *
	 * @param mensaje El mensaje de éxito
	 */
	public void mostrarExito(String mensaje) {
		mostrarMensaje(mensaje);
	}
}