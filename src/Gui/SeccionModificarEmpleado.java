package Gui;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Sección para modificar datos básicos de productos. Permite cambiar la
 * descripción y la ruta de imagen de un producto existente.
 */
public class SeccionModificarEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	public SeccionModificarEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Modificar Productos");
		JPanel contenido = getContenido(base);

		JTextField campoId = crearCampo();

		SelectorVenta selector = crearSelectorProductosVenta("Buscar producto a modificar",
				"Selecciona una fila para cargar el ID del producto.", true, campoId);

		JPanel bloque = crearBloque("Modificar descripción o imagen");

		JTextArea areaDescripcion = crearArea();
		JTextField campoImagen = crearCampo();

		JButton botonCargarDatos = crearBotonSecundario("Cargar datos actuales");
		JButton botonExaminar = crearBotonSecundario("Examinar imagen...");
		JButton botonDescripcion = crearBotonAccion("Guardar descripción");
		JButton botonImagen = crearBotonAccion("Guardar imagen");

		bloque.add(crearLabel("ID producto"), gbcCampo(1));
		bloque.add(campoId, gbcCampo(2));
		bloque.add(botonCargarDatos, gbcBoton(3));

		bloque.add(crearLabel("Nueva descripción"), gbcCampo(4));
		bloque.add(estilizarScroll(areaDescripcion), gbcCampo(5));
		bloque.add(botonDescripcion, gbcBoton(6));

		bloque.add(crearLabel("Nueva ruta de imagen"), gbcCampo(7));
		bloque.add(campoImagen, gbcCampo(8));

		JPanel filaImagen = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		filaImagen.setOpaque(false);
		filaImagen.add(botonExaminar);
		filaImagen.add(botonImagen);

		bloque.add(filaImagen, gbcBoton(9));

		botonCargarDatos.addActionListener(e -> {
			String id = campoId.getText().trim();

			if (id.isBlank()) {
				mostrarError("Introduce o selecciona un ID de producto.");
				return;
			}

			ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(id);

			if (producto == null) {
				mostrarError("No existe ningún producto con ese ID.");
				return;
			}

			areaDescripcion.setText(producto.getDescripcion());
			campoImagen.setText(producto.getImagenRuta());
		});

		botonExaminar.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			int resultado = chooser.showOpenDialog(this);

			if (resultado == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
				campoImagen.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});

		botonDescripcion.addActionListener(e -> {
			String id = campoId.getText().trim();
			String descripcion = areaDescripcion.getText().trim();

			if (id.isBlank()) {
				mostrarError("Introduce el ID del producto.");
				return;
			}

			if (descripcion.isBlank()) {
				mostrarError("Introduce una descripción.");
				return;
			}

			boolean ok = empleado.modificarDescripcionProducto(id, descripcion);

			if (ok) {
				recargarTablaProductos(selector.tabla);
				mostrarMensaje("Descripción modificada correctamente.");
			} else {
				mostrarError("No se pudo modificar la descripción.");
			}
		});

		botonImagen.addActionListener(e -> {
			String id = campoId.getText().trim();
			String imagen = campoImagen.getText().trim();

			if (id.isBlank()) {
				mostrarError("Introduce el ID del producto.");
				return;
			}

			if (imagen.isBlank()) {
				mostrarError("Introduce una ruta de imagen.");
				return;
			}

			boolean ok = empleado.modificarImagenProducto(id, imagen);

			if (ok) {
				recargarTablaProductos(selector.tabla);
				mostrarMensaje("Imagen modificada correctamente.");
			} else {
				mostrarError("No se pudo modificar la imagen.");
			}
		});

		contenido.add(selector.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(bloque);

		add(base);
	}
}
