package Gui;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import productos.Categoria;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Sección para gestionar las categorías de los productos. Permite añadir o
 * quitar un producto de una categoría existente.
 */
public class SeccionCategoriasEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	public SeccionCategoriasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Gestión de Categorías");
		JPanel contenido = getContenido(base);

		JTextField campoId = crearCampo();

		JComboBox<String> comboCategoria = crearCombo(new String[] { "Selecciona una categoría" });
		cargarCategorias(comboCategoria);

		SelectorVenta selector = crearSelectorProductosVenta("Buscar producto para categoría",
				"Selecciona una fila para cargar el ID del producto.", true, campoId);

		JPanel bloque = crearBloque("Añadir o eliminar producto de categoría");

		JButton botonAnadir = crearBotonAccion("Añadir a categoría");
		JButton botonEliminar = crearBotonPeligro("Eliminar de categoría");

		bloque.add(crearLabel("ID producto"), gbcCampo(1));
		bloque.add(campoId, gbcCampo(2));

		bloque.add(crearLabel("Categoría"), gbcCampo(3));
		bloque.add(comboCategoria, gbcCampo(4));

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonAnadir);
		filaBotones.add(botonEliminar);

		bloque.add(filaBotones, gbcBoton(5));

		botonAnadir.addActionListener(e -> {
			String id = campoId.getText().trim();
			String categoria = String.valueOf(comboCategoria.getSelectedItem());

			if (id.isBlank()) {
				mostrarError("Introduce o selecciona un ID de producto.");
				return;
			}

			if (categoria == null || categoria.equals("Selecciona una categoría")) {
				mostrarError("Selecciona una categoría.");
				return;
			}

			boolean ok = empleado.añadirProductoACategoria(id, categoria);

			if (ok) {
				recargarTablaProductos(selector.tabla);
				mostrarMensaje("Producto añadido a la categoría.");
			} else {
				mostrarError("No se pudo añadir el producto a la categoría.");
			}
		});

		botonEliminar.addActionListener(e -> {
			String id = campoId.getText().trim();
			String categoria = String.valueOf(comboCategoria.getSelectedItem());

			if (id.isBlank()) {
				mostrarError("Introduce o selecciona un ID de producto.");
				return;
			}

			if (categoria == null || categoria.equals("Selecciona una categoría")) {
				mostrarError("Selecciona una categoría.");
				return;
			}

			boolean ok = empleado.eliminarProductoDeCategoria(id, categoria);

			if (ok) {
				recargarTablaProductos(selector.tabla);
				mostrarMensaje("Producto eliminado de la categoría.");
			} else {
				mostrarError("No se pudo eliminar el producto de la categoría.");
			}
		});

		contenido.add(selector.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(bloque);

		add(base);
	}

	private void cargarCategorias(JComboBox<String> comboCategoria) {
		comboCategoria.removeAllItems();
		comboCategoria.addItem("Selecciona una categoría");

		for (Categoria categoria : Tienda.getInstancia().getCategorias()) {
			if (categoria != null && categoria.getNombre() != null) {
				comboCategoria.addItem(categoria.getNombre());
			}
		}
	}
}
