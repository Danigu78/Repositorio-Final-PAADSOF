package Gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import productos.Categoria;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Panel para gestionar las categorías de los productos.
 * 
 * Desde aquí el empleado puede consultar los productos actuales y cambiar sus
 * categorías escribiendo el ID del producto.
 */
public class SeccionCategoriasEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	private JTextField campoIdProducto;
	private JComboBox<String> comboCategoria;

	private SelectorVenta selectorProductos;

	public SeccionCategoriasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Gestión de Categorías");
		JPanel contenido = getContenido(panelBase);

		campoIdProducto = crearCampo();

		comboCategoria = crearCombo(new String[] { "Selecciona una categoría" });
		cargarCategorias();

		/*
		 * Igual que en stock: la tabla solo se usa para consultar. El ID del producto
		 * se escribe a mano abajo.
		 */
		selectorProductos = crearSelectorProductosVenta("Productos actuales",
				"Filtra los productos para consultar sus categorías.", true);

		/*
		 * Dejamos la tabla como tabla de consulta. Así al pulsar una fila no se marca
		 * en amarillo ni parece que haga algo raro.
		 */
		selectorProductos.tabla.setRowSelectionAllowed(false);
		selectorProductos.tabla.setCellSelectionEnabled(false);

		contenido.add(selectorProductos.bloque);
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

		botonAnadir.addActionListener(e -> anadirCategoria());
		botonEliminar.addActionListener(e -> quitarCategoria());

		return panel;
	}

	private void anadirCategoria() {
		String idProducto = campoIdProducto.getText().trim();
		String categoria = obtenerCategoriaSeleccionada();

		if (!datosValidos(idProducto, categoria)) {
			return;
		}

		boolean cambiado = empleado.añadirProductoACategoria(idProducto, categoria);

		if (cambiado) {
			recargarTablaProductos(selectorProductos.tabla);
			mostrarMensaje("Categoría añadida correctamente");
		} else {
			mostrarError("No se pudo añadir la categoría");
		}
	}

	private void quitarCategoria() {
		String idProducto = campoIdProducto.getText().trim();
		String categoria = obtenerCategoriaSeleccionada();

		if (!datosValidos(idProducto, categoria)) {
			return;
		}

		boolean cambiado = empleado.eliminarProductoDeCategoria(idProducto, categoria);

		if (cambiado) {
			recargarTablaProductos(selectorProductos.tabla);
			mostrarMensaje("Categoría quitada correctamente");
		} else {
			mostrarError("No se pudo quitar la categoría");
		}
	}

	private boolean datosValidos(String idProducto, String categoria) {
		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto");
			return false;
		}

		if (categoria == null) {
			mostrarError("Selecciona una categoría");
			return false;
		}

		return true;
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

		for (Categoria categoria : Tienda.getInstancia().getCategorias()) {
			if (categoria != null && categoria.getNombre() != null && !categoria.getNombre().isBlank()) {
				comboCategoria.addItem(categoria.getNombre());
			}
		}
	}
}