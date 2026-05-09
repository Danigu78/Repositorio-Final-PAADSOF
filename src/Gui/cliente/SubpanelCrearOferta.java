package Gui.cliente;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCrearOferta;
import productos.Producto2Mano;
import usuarios.Cliente;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Subpanel para crear una oferta de intercambio. Muestra dos columnas: tus
 * productos disponibles y los del propietario. El usuario selecciona con
 * checkboxes qué productos incluir. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCrearOferta extends JPanel {

	private VentanaPrincipal ventana;
	private SubpanelProducto2Mano subpanelOrigen;
	private ControladorCrearOferta controlador;
	private Producto2Mano productoObjetivo;
	private Cliente cliente;

	// Botones — atributos para registrar el controlador
	private JButton botonVolver;
	private JButton botonEnviar;

	// Checkboxes y productos — paralelos para leer selección
	private List<JCheckBox> checkboxesMios;
	private List<JCheckBox> checkboxesSuyos;
	private List<Producto2Mano> misProductos;
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
		this.ventana = ventana;
		this.subpanelOrigen = subpanelOrigen;
		this.productoObjetivo = productoObjetivo;
		this.cliente = cliente;
		this.controlador = new ControladorCrearOferta(this, cliente, productoObjetivo);

		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		construirUI();
		setControlador(controlador);
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes.
	 *
	 * @param c El controlador a registrar
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
	 */
	private JPanel crearBarra() {
		JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
		barra.setBackground(VentanaPrincipal.COLOR_PANEL);
		barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

		botonVolver = new JButton(" Volver al producto");
		botonVolver.setActionCommand("volver");
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
		barra.add(botonVolver);
		return barra;
	}

	/**
	 * Crea el panel central con las dos columnas de productos.
	 */
	private JPanel crearPanelOferta() {
		JPanel panel = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(20), 0));
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));

		// Columna izquierda — mis productos disponibles
		misProductos = controlador.getMisProductosDisponibles();
		checkboxesMios = new ArrayList<>();
		panel.add(crearColumnaProductos("Mis productos para ofrecer", misProductos, checkboxesMios,
				"No tienes productos tasados y visibles para ofertar."));

		// Columna derecha — productos del propietario
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
	 * @param mensajeVacío Mensaje si no hay productos
	 * @return Panel de la columna
	 */
	private JPanel crearColumnaProductos(String titulo, List<Producto2Mano> productos, List<JCheckBox> checkboxes,
			String mensajeVacío) {
		JPanel columna = new JPanel(new BorderLayout());
		columna.setBackground(VentanaPrincipal.COLOR_PANEL);
		columna.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
								VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		// Título de la columna
		JLabel labelTitulo = new JLabel(titulo);
		labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(10), 0));
		columna.add(labelTitulo, BorderLayout.NORTH);

		// Lista de productos con checkboxes
		JPanel panelLista = new JPanel();
		panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
		panelLista.setBackground(VentanaPrincipal.COLOR_PANEL);

		if (productos.isEmpty()) {
			JLabel labelVacío = new JLabel(mensajeVacío);
			labelVacío.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelVacío.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			panelLista.add(labelVacío);
		} else {
			for (Producto2Mano p : productos) {

				JPanel fila = new JPanel(new BorderLayout(VentanaPrincipal.escalar(10), 0));
				fila.setBackground(VentanaPrincipal.COLOR_PANEL);
				fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(40)));
				fila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

				String info = p.getNombre() + "(" + p.getEstadoProducto() + ")";
				JCheckBox checkbox = new JCheckBox(info);
				checkbox.setFont(VentanaPrincipal.FUENTE_NORMAL);
				checkbox.setForeground(VentanaPrincipal.COLOR_TEXTO);
				checkbox.setBackground(VentanaPrincipal.COLOR_PANEL);
				checkboxes.add(checkbox);
				fila.add(checkbox, BorderLayout.WEST);

				// Precio tasado a la derecha
				if (p.getValoracion() != null) {
					JLabel labelPrecio = new JLabel(String.format("%.2f€", p.getValoracion().getPrecioTasacion()));
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
	 */
	private JPanel crearPanelBotonEnviar() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), 0, VentanaPrincipal.escalar(20), 0));

		botonEnviar = new JButton("Enviar oferta");
		botonEnviar.setActionCommand("enviar");
		botonEnviar.setFont(VentanaPrincipal.FUENTE_BOTON);
		botonEnviar.setBackground(VentanaPrincipal.COLOR_ACENTO);
		botonEnviar.setForeground(Color.WHITE);
		botonEnviar.setOpaque(true);
		botonEnviar.setBorderPainted(false);
		botonEnviar.setFocusPainted(false);
		botonEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonEnviar.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(30)));
		botonEnviar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botonEnviar.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				botonEnviar.setBackground(VentanaPrincipal.COLOR_ACENTO);
			}
		});
		panel.add(botonEnviar);
		return panel;
	}

	/**
	 * Devuelve los productos del cliente seleccionados. Lo lee el controlador para
	 * enviar la oferta.
	 *
	 * @return Lista de productos seleccionados
	 */
	public List<Producto2Mano> getMisProductosSeleccionados() {
		List<Producto2Mano> seleccionados = new ArrayList<>();
		for (int i = 0; i < checkboxesMios.size(); i++) {
			if (checkboxesMios.get(i).isSelected()) {
				seleccionados.add(misProductos.get(i));
			}
		}
		return seleccionados;
	}

	/**
	 * Devuelve los productos del propietario seleccionados. Lo lee el controlador
	 * para enviar la oferta.
	 *
	 * @return Lista de productos seleccionados
	 */
	public List<Producto2Mano> getSusProductosSeleccionados() {
		List<Producto2Mano> seleccionados = new ArrayList<>();
		for (int i = 0; i < checkboxesSuyos.size(); i++) {
			if (checkboxesSuyos.get(i).isSelected()) {
				seleccionados.add(susProductos.get(i));
			}
		}
		return seleccionados;
	}

	/**
	 * Vuelve al detalle del producto. Lo llama el controlador.
	 */
	public void volver() {
		subpanelOrigen.volverAlDetalle();
	}

	/**
	 * Muestra un mensaje de error. Lo llama el controlador.
	 */
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un mensaje de éxito. Lo llama el controlador.
	 */
	public void mostrarExito(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Oferta enviada", JOptionPane.INFORMATION_MESSAGE);
	}
}