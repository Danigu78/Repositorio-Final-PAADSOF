package Gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import usuarios.Empleado;

/**
 * Sección de gestión de stock del empleado.
 * 
 * Permite consultar los productos actuales, reponer stock manualmente y cargar
 * productos desde un fichero de texto.
 */
public class SeccionStockEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	private JTextField campoId;
	private JTextField campoCantidad;
	private JTextField campoRuta;

	private SelectorVenta selector;

	public SeccionStockEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Gestión de Stock");
		JPanel contenido = getContenido(base);

		campoId = crearCampo();
		campoCantidad = crearCampo();
		campoRuta = crearCampo();

		selector = crearSelectorProductosVenta("Productos actuales",
				"Filtra los productos y pulsa una fila para cargar su ID.", true, campoId);

		contenido.add(selector.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueReponerStock());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCargarFichero());

		add(base, BorderLayout.CENTER);
	}

	private JPanel crearBloqueReponerStock() {
		JPanel bloque = crearBloque("Reponer stock");

		JButton botonReponer = crearBotonAccion("Reponer");

		bloque.add(crearLabel("ID producto"), gbcCampo(1));
		bloque.add(campoId, gbcCampo(2));

		bloque.add(crearLabel("Cantidad a añadir"), gbcCampo(3));
		bloque.add(campoCantidad, gbcCampo(4));

		bloque.add(botonReponer, gbcBoton(5));

		botonReponer.addActionListener(e -> reponerStock());

		return bloque;
	}

	private JPanel crearBloqueCargarFichero() {
		JPanel bloque = crearBloque("Cargar productos desde fichero");

		JButton botonExaminar = crearBotonSecundario("Examinar...");
		JButton botonCargar = crearBotonAccion("Cargar fichero");

		bloque.add(crearLabel("Ruta del fichero"), gbcCampo(1));
		bloque.add(campoRuta, gbcCampo(2));

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonExaminar);
		filaBotones.add(botonCargar);

		bloque.add(filaBotones, gbcBoton(3));

		botonExaminar.addActionListener(e -> seleccionarFichero());
		botonCargar.addActionListener(e -> cargarFichero());

		return bloque;
	}

	private void reponerStock() {
		String id = campoId.getText().trim();
		Integer cantidad = leerEnteroSeguro(campoCantidad.getText());

		if (id.isBlank()) {
			mostrarError("Introduce el ID del producto.");
			return;
		}

		if (cantidad == null || cantidad <= 0) {
			mostrarError("Introduce una cantidad positiva.");
			return;
		}

		boolean ok = empleado.reponerStockProducto(id, cantidad);

		if (ok) {
			recargarTablaProductos(selector.tabla);
			campoCantidad.setText("");
			mostrarMensaje("Stock repuesto correctamente.");
		} else {
			mostrarError("No se pudo reponer el stock.");
		}
	}

	private void seleccionarFichero() {
		JFileChooser chooser = new JFileChooser();

		int resultado = chooser.showOpenDialog(this);

		if (resultado == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
			campoRuta.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	private void cargarFichero() {
		String ruta = campoRuta.getText().trim();

		if (ruta.isBlank()) {
			mostrarError("Introduce o selecciona una ruta.");
			return;
		}

		boolean ok = empleado.cargarProductosFicheroTexto(ruta);

		if (ok) {
			recargarTablaProductos(selector.tabla);
			mostrarMensaje("Fichero procesado correctamente.");
		} else {
			mostrarError("No se pudo cargar el fichero.");
		}
	}
}