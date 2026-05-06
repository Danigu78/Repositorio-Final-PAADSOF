package Gui;

import java.awt.*;

import javax.swing.*;

import Gui.Controladores.ControladorModificarEmpleado;
import Gui.Controladores.ResultadoOperacion;
import productos.ProductoVenta;
import usuarios.Empleado;

/**
 * Pantalla para modificar algunos datos básicos de los productos.
 * 
 * Desde aquí el empleado puede consultar los productos y cambiar la descripción
 * o la imagen de un producto concreto.
 */
public class SeccionModificarEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	private SelectorVenta selectorProductos;

	private JTextField campoIdProducto;
	private JTextArea areaDescripcion;
	private JTextField campoImagen;
	private ControladorModificarEmpleado controlador;

	public SeccionModificarEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorModificarEmpleado(empleado);
		construirUI();
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Modificar Productos");
		JPanel contenido = getContenido(panelBase);

		campoIdProducto = crearCampo();
		areaDescripcion = crearArea();
		campoImagen = crearCampo();

		/*
		 * La tabla se queda como consulta, igual que en stock. El ID se escribe abajo a
		 * mano para que no haga cosas raras al pulsar una fila.
		 */
		selectorProductos = crearSelectorProductosVenta("Productos actuales",
				"Filtra los productos para consultar sus datos antes de modificarlos.", true);

		selectorProductos.tabla.setRowSelectionAllowed(false);
		selectorProductos.tabla.setCellSelectionEnabled(false);

		contenido.add(selectorProductos.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueModificarProducto());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloqueModificarProducto() {
		JPanel bloque = crearBloque("Modificar datos del producto");

		JPanel panelCambios = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(35), 0));
		panelCambios.setOpaque(false);

		panelCambios.add(crearPanelDatosProducto());
		panelCambios.add(crearPanelCambiosProducto());

		bloque.add(panelCambios, gbcCampo(1));

		return bloque;
	}

	private JPanel crearPanelDatosProducto() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Producto");
		titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel campoId = crearCampoFormulario("ID producto", campoIdProducto);
		campoId.setAlignmentX(Component.LEFT_ALIGNMENT);

		JButton botonCargar = crearBotonAccion("Cargar datos");

		JPanel filaBoton = crearFilaBotones();
		filaBoton.setAlignmentX(Component.LEFT_ALIGNMENT);
		filaBoton.add(botonCargar);

		panel.add(titulo);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(campoId);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));
		panel.add(filaBoton);

		botonCargar.addActionListener(e -> cargarDatosProducto());

		return panel;
	}

	private JPanel crearPanelCambiosProducto() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Cambios");
		titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

		JScrollPane scrollDescripcion = estilizarScroll(areaDescripcion);
		scrollDescripcion.setPreferredSize(new Dimension(VentanaPrincipal.escalar(420), VentanaPrincipal.escalar(130)));

		JPanel campoDescripcion = crearCampoFormulario("Descripción", scrollDescripcion);
		campoDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

		JButton botonGuardarDescripcion = crearBotonAccion("Guardar descripción");

		JPanel filaDescripcion = crearFilaBotones();
		filaDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
		filaDescripcion.add(botonGuardarDescripcion);

		JPanel campoRuta = crearCampoFormulario("Ruta de imagen", campoImagen);
		campoRuta.setAlignmentX(Component.LEFT_ALIGNMENT);

		JButton botonAbrirImagen = crearBotonSecundario("Abrir...");
		JButton botonGuardarImagen = crearBotonAccion("Guardar imagen");

		JPanel filaImagen = crearFilaBotones();
		filaImagen.setAlignmentX(Component.LEFT_ALIGNMENT);
		filaImagen.add(botonAbrirImagen);
		filaImagen.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaImagen.add(botonGuardarImagen);

		panel.add(titulo);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(campoDescripcion);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(filaDescripcion);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		panel.add(campoRuta);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(filaImagen);

		botonGuardarDescripcion.addActionListener(e -> guardarDescripcion());
		botonAbrirImagen.addActionListener(e -> seleccionarImagen());
		botonGuardarImagen.addActionListener(e -> guardarImagen());

		return panel;
	}

	private JPanel crearFilaBotones() {
		JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		fila.setOpaque(false);
		return fila;
	}

	private void cargarDatosProducto() {
		ProductoVenta producto = buscarProductoEscrito();

		if (producto == null) {
			return;
		}

		areaDescripcion.setText(producto.getDescripcion());
		campoImagen.setText(producto.getImagenRuta());

		mostrarMensaje("Datos cargados.");
	}

	private ProductoVenta buscarProductoEscrito() {
		String idProducto = campoIdProducto.getText().trim();

		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto.");
			return null;
		}

		ProductoVenta producto = controlador.buscarProducto(idProducto);

		if (producto == null) {
			mostrarError("No existe ningún producto con ese ID.");
			return null;
		}

		return producto;
	}

	private void seleccionarImagen() {
		JFileChooser selectorImagen = new JFileChooser();

		int opcion = selectorImagen.showOpenDialog(this);

		if (opcion == JFileChooser.APPROVE_OPTION && selectorImagen.getSelectedFile() != null) {
			campoImagen.setText(selectorImagen.getSelectedFile().getAbsolutePath());
		}
	}

	private void guardarDescripcion() {
		String idProducto = campoIdProducto.getText().trim();
		String descripcion = areaDescripcion.getText().trim();

		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto.");
			return;
		}

		if (descripcion.isBlank()) {
			mostrarError("Escribe una descripción.");
			return;
		}

		ResultadoOperacion resultado = controlador.guardarDescripcion(idProducto, descripcion);

		if (resultado.isExito()) {
			recargarTablaProductos(selectorProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private void guardarImagen() {
		String idProducto = campoIdProducto.getText().trim();
		String imagen = campoImagen.getText().trim();

		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto.");
			return;
		}

		if (imagen.isBlank()) {
			mostrarError("Escribe una ruta de imagen.");
			return;
		}

		ResultadoOperacion resultado = controlador.guardarImagen(idProducto, imagen);

		if (resultado.isExito()) {
			recargarTablaProductos(selectorProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}
}