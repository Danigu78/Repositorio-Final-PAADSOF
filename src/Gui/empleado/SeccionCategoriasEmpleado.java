package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorCategoriasEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

import usuarios.Empleado;

/**
 * Panel para gestionar las categorías de los productos.
 * 
 * @author Lucas
 * @version 1.0
 */
public class SeccionCategoriasEmpleado extends SeccionProductosVentaEmpleadoBase {

	private static final long serialVersionUID = 1L;

	/** Campo donde se introduce el ID del producto. */
	private JTextField campoIdProducto;

	/** Combo desplegable con las categorías disponibles. */
	private JComboBox<String> comboCategoria;

	/** Controlador de la sección de categorías del empleado. */
	private ControladorCategoriasEmpleado controlador;

	/** Tabla de productos usada como vista de consulta. */
	private TablaVenta tablaProductos;

	/**
	 * Constructor del panel de categorías del empleado.
	 * 
	 * @param ventana  ventana principal de la aplicación
	 * @param empleado empleado que utiliza la sección
	 */
	public SeccionCategoriasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorCategoriasEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	/**
	 * Asigna el controlador de la vista.
	 * 
	 * @param controlador controlador de categorías
	 */
	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorCategoriasEmpleado) {
			this.controlador = (ControladorCategoriasEmpleado) controlador;
		}
	}

	/**
	 * Asocia un botón con una acción del controlador.
	 * 
	 * @param boton  botón a configurar
	 * @param accion acción del controlador
	 */
	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	/**
	 * Construye toda la interfaz del panel.
	 */
	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Gestión de Categorías");
		JPanel contenido = getContenido(panelBase);

		campoIdProducto = crearCampo();

		comboCategoria = crearCombo(new String[] { "Selecciona una categoría" });
		cargarCategorias();

		// La tabla es solo para mirar productos y sus categorías.
		tablaProductos = crearTablaProductosVenta("Productos actuales",
				"Filtra los productos para consultar sus categorías.", true);

		// No queremos que al pinchar una fila parezca que hace algo especial.
		tablaProductos.tabla.setRowSelectionAllowed(false);
		tablaProductos.tabla.setCellSelectionEnabled(false);

		contenido.add(tablaProductos.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCategorias());

		add(panelBase, BorderLayout.CENTER);
	}

	/**
	 * Crea el bloque principal de gestión de categorías.
	 */
	private JPanel crearBloqueCategorias() {
		JPanel bloque = crearBloque("Cambiar categoría de un producto");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelDatosCategoria());
		panelAcciones.add(crearPanelBotonesCategoria());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	/**
	 * Panel izquierdo con los datos del formulario.
	 */
	private JPanel crearPanelDatosCategoria() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearCampoFormulario("ID producto", campoIdProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Categoría", comboCategoria));

		return panel;
	}

	/**
	 * Panel derecho con los botones de acción.
	 */
	private JPanel crearPanelBotonesCategoria() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		JButton botonAnadir = crearBotonAccion("Añadir categoría");
		JButton botonEliminar = crearBotonPeligro("Quitar categoría");

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);

		filaBotones.add(botonAnadir);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonEliminar);

		panel.add(filaBotones);

		conectar(botonAnadir, ControladorCategoriasEmpleado.ANADIR_CATEGORIA);
		conectar(botonEliminar, ControladorCategoriasEmpleado.QUITAR_CATEGORIA);

		return panel;
	}

	/**
	 * Añade una categoría al producto indicado.
	 */
	public void anadirCategoria() {
		ResultadoOperacion resultado = controlador.anadirCategoria(campoIdProducto.getText(),
				obtenerCategoriaSeleccionada());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	/**
	 * Quita una categoría del producto indicado.
	 */
	public void quitarCategoria() {
		ResultadoOperacion resultado = controlador.quitarCategoria(campoIdProducto.getText(),
				obtenerCategoriaSeleccionada());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	/**
	 * Obtiene la categoría seleccionada en el combo.
	 * 
	 * @return nombre de categoría o null si no es válida
	 */
	private String obtenerCategoriaSeleccionada() {
		Object seleccionado = comboCategoria.getSelectedItem();

		if (seleccionado == null) {
			return null;
		}

		String categoria = String.valueOf(seleccionado);

		if (categoria.equals("Selecciona una categoría")) {
			return null;
		}

		return categoria;
	}

	/**
	 * Carga las categorías disponibles en el combo.
	 */
	private void cargarCategorias() {
		comboCategoria.removeAllItems();
		comboCategoria.addItem("Selecciona una categoría");

		for (String nombreCategoria : controlador.getNombresCategorias()) {
			comboCategoria.addItem(nombreCategoria);
		}
	}
}
