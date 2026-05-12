package Gui.Gestor;

import Gui.TablaProductosVenta;
import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorCategoriasGestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import productos.Categoria;
import usuarios.Gestor;

/**
 * Subpanel de gestión de categorías para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelCategoriasGestor extends AbstractPanelGestor {

	private static final long serialVersionUID = 1L;

	/** Controlador asociado al subpanel. */
	private ControladorCategoriasGestor controlador;

	/** Campo para el nombre de la nueva categoría. */
	private JTextField campoNombre;

	/** Campo para la descripción de la categoría. */
	private JTextField campoDesc;

	/** Campo del ID del producto. */
	private JTextField campoIdProducto;

	/** Combo con las categorías disponibles. */
	private JComboBox<String> comboCategoria;

	/** Tabla de productos de venta. */
	private TablaProductosVenta tablaProductosVenta;

	/** Panel de listado de categorías. */
	private JPanel panelListaCategorias;

	/** Campo de búsqueda de categorías. */
	private JTextField campoBusquedaCategorias;

	/** Botón para crear categoría. */
	private JButton botonCrear;

	/** Botón para añadir categoría a producto. */
	private JButton botonAnadirCategoria;

	/** Botón para quitar categoría de producto. */
	private JButton botonQuitarCategoria;

	/** Referencia al panel gestor principal. */
	private PanelGestor panelGestor;

	/**
	 * Constructor del subpanel de categorías.
	 *
	 * @param ventana      Ventana principal
	 * @param gestor       Gestor autenticado
	 * @param panelGestor  Panel gestor principal
	 */
	public SubpanelCategoriasGestor(VentanaPrincipal ventana, Gestor gestor, PanelGestor panelGestor) {
		super(ventana, gestor);
		this.panelGestor = panelGestor;
		this.controlador = new ControladorCategoriasGestor(this, gestor);
		inicializarUI();
	}

	/**
	 * Inicializa la interfaz del subpanel.
	 */
	private void inicializarUI() {
		setLayout(new BorderLayout());

		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		contenido.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

		campoIdProducto = crearCampo();
		comboCategoria = crearCombo(new String[] { "Selecciona una categoría" });
		cargarCategorias();

		contenido.add(crearFormularioNuevaCategoria());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueListaCategorias());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearPanelTablaProductos());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCambiarCategoriaProducto());

		JScrollPane scroll = new JScrollPane(contenido);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));

		add(scroll, BorderLayout.CENTER);
		setControlador(controlador);
	}

	/**
	 * Asigna el controlador a los botones del subpanel.
	 *
	 * @param c Controlador de acciones
	 */
	public void setControlador(ActionListener c) {
		if (botonCrear != null) {
			for (ActionListener al : botonCrear.getActionListeners())
				botonCrear.removeActionListener(al);
			botonCrear.addActionListener(c);
		}
		if (botonAnadirCategoria != null) {
			for (ActionListener al : botonAnadirCategoria.getActionListeners())
				botonAnadirCategoria.removeActionListener(al);
			botonAnadirCategoria.addActionListener(c);
		}
		if (botonQuitarCategoria != null) {
			for (ActionListener al : botonQuitarCategoria.getActionListeners())
				botonQuitarCategoria.removeActionListener(al);
			botonQuitarCategoria.addActionListener(c);
		}
	}

	/**
	 * Procesa la creación de una nueva categoría.
	 */
	public void procesarCrearCategoria() {
		String nombre = campoNombre.getText().trim();
		String desc = campoDesc.getText().trim();
		if (nombre.isEmpty()) {
			mostrarError("El nombre no puede estar vacío.");
			return;
		}
		if (controlador.crearCategoria(nombre, desc)) {
			mostrarMensaje("Categoría '" + nombre + "' creada.");
			campoNombre.setText("");
			campoDesc.setText("");
			cargarCategorias();
			actualizarListaCategorias();
			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
				tablaProductosVenta.refrescarFiltrosCategorias();
			}
		} else {
			mostrarError("No se pudo crear la categoría.");
		}
		if (panelGestor != null)
			panelGestor.refrescarFiltrosCategorias();
	}

	/**
	 * Procesa la eliminación de una categoría.
	 *
	 * @param nombre Nombre de la categoría
	 */
	public void procesarEliminarCategoria(String nombre) {
		int confirm = JOptionPane.showConfirmDialog(this,
				"¿Seguro que quieres eliminar la categoría \"" + nombre + "\"?\nSe quitará de todos los productos.",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION)
			return;
		if (controlador.eliminarCategoria(nombre)) {
			mostrarMensaje("Categoría '" + nombre + "' eliminada.");
			cargarCategorias();
			actualizarListaCategorias();
			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
				tablaProductosVenta.refrescarFiltrosCategorias();
			}
		} else {
			mostrarError("No se pudo eliminar la categoría.");
		}
		if (panelGestor != null)
			panelGestor.refrescarFiltrosCategorias();
	}

	/**
	 * Añade una categoría a un producto.
	 */
	public void procesarAnadirCategoriaProducto() {
		String idProducto = campoIdProducto.getText().trim();
		String nombreCategoria = obtenerCategoriaSeleccionada();
		if (idProducto.isEmpty()) {
			mostrarError("Selecciona o escribe el ID del producto.");
			return;
		}
		if (nombreCategoria == null) {
			mostrarError("Selecciona una categoría.");
			return;
		}
		if (controlador.añadirProductoACategoria(idProducto, nombreCategoria)) {
			mostrarMensaje("Categoría añadida correctamente.");
			if (tablaProductosVenta != null)
				tablaProductosVenta.refrescar();
		} else {
			mostrarError("No se pudo añadir la categoría.");
		}
	}

	/**
	 * Quita una categoría de un producto.
	 */
	public void procesarQuitarCategoriaProducto() {
		String idProducto = campoIdProducto.getText().trim();
		String nombreCategoria = obtenerCategoriaSeleccionada();
		if (idProducto.isEmpty()) {
			mostrarError("Selecciona o escribe el ID del producto.");
			return;
		}
		if (nombreCategoria == null) {
			mostrarError("Selecciona una categoría.");
			return;
		}
		if (!controlador.productoTieneCategoria(idProducto, nombreCategoria)) {
			mostrarError("Ese producto no tiene esa categoría.");
			return;
		}
		if (controlador.eliminarProductoDeCategoria(idProducto, nombreCategoria)) {
			mostrarMensaje("Categoría quitada correctamente.");
			if (tablaProductosVenta != null)
				tablaProductosVenta.refrescar();
		} else {
			mostrarError("No se pudo quitar la categoría.");
		}
	}

	/**
	 * Obtiene la categoría seleccionada del combo.
	 *
	 * @return Nombre de la categoría o null
	 */
	private String obtenerCategoriaSeleccionada() {
		Object sel = comboCategoria.getSelectedItem();
		if (sel == null)
			return null;
		String cat = String.valueOf(sel);
		return cat.equals("Selecciona una categoría") ? null : cat;
	}

	/**
	 * Recarga el combo de categorías con las activas.
	 */
	public void cargarCategorias() {
		comboCategoria.removeAllItems();
		comboCategoria.addItem("Selecciona una categoría");
		for (Categoria c : controlador.getCategoriasActivas())
			comboCategoria.addItem(c.getNombre());
	}
}