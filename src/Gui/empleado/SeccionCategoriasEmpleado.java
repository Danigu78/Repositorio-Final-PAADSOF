package Gui.empleado;


import Gui.VentanaPrincipal;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

import Gui.Controladores.empleado.ControladorCategoriasEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;
import usuarios.Empleado;

/**
 * Panel para gestionar las categorías de los productos.
 * 
 * Desde aquí el empleado puede consultar los productos actuales y cambiar sus
 * categorías escribiendo el ID del producto.
 */
public class SeccionCategoriasEmpleado extends SeccionProductosVentaEmpleadoBase {

	private static final long serialVersionUID = 1L;

	private JTextField campoIdProducto;
	private JComboBox<String> comboCategoria;
	private ControladorCategoriasEmpleado controlador;

	/* La usamos como tabla de consulta, igual que en stock */
	private SelectorVenta tablaProductos;

	public SeccionCategoriasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorCategoriasEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorCategoriasEmpleado) {
			this.controlador = (ControladorCategoriasEmpleado) controlador;
		}
	}

	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Gestión de Categorías");
		JPanel contenido = getContenido(panelBase);

		campoIdProducto = crearCampo();

		comboCategoria = crearCombo(new String[] { "Selecciona una categoría" });
		cargarCategorias();

		// La tabla es solo para mirar productos y sus categorías.
		tablaProductos = crearSelectorProductosVenta("Productos actuales",
				"Filtra los productos para consultar sus categorías.", true);

		// No queremos que al pinchar una fila parezca que hace algo especial.
		tablaProductos.tabla.setRowSelectionAllowed(false);
		tablaProductos.tabla.setCellSelectionEnabled(false);

		contenido.add(tablaProductos.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCategorias());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloqueCategorias() {
		JPanel bloque = crearBloque("Cambiar categoría de un producto");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelDatosCategoria());
		panelAcciones.add(crearPanelBotonesCategoria());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	// Parte izquierda: ID del producto y categoría elegida
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

	// Parte derecha: botones para añadir o quitar la categoría
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

	public void anadirCategoria() {
		ResultadoOperacion resultado = controlador.anadirCategoria(campoIdProducto.getText(), obtenerCategoriaSeleccionada());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void quitarCategoria() {
		ResultadoOperacion resultado = controlador.quitarCategoria(campoIdProducto.getText(), obtenerCategoriaSeleccionada());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

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

	private void cargarCategorias() {
		comboCategoria.removeAllItems();
		comboCategoria.addItem("Selecciona una categoría");

		for (String nombreCategoria : controlador.getNombresCategorias()) {
			comboCategoria.addItem(nombreCategoria);
		}
	}
}
