package Gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Pantalla para gestionar packs de productos.
 * 
 * Permite consultar productos, crear packs y modificar packs ya existentes.
 */
public class SeccionPacksEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	private SelectorVenta selectorProductos;

	private JTextArea areaLineasPack;

	private JTextField campoIdPack;
	private JTextField campoIdProducto;
	private JTextField campoUnidades;
	private JTextField campoNuevoPrecio;

	public SeccionPacksEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Gestión de Packs");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloquePacksExistentes());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCrearPack());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueModificarPack());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloquePacksExistentes() {
		selectorProductos = crearSelectorProductosVenta("Packs existentes",
				"Busca productos igual que en el resto de secciones. Por defecto se muestran los packs.", false);

		/*
		 * Esta tabla es solo de consulta. No queremos que al pulsar una fila copie
		 * nada.
		 */
		selectorProductos.tabla.setRowSelectionAllowed(false);
		selectorProductos.tabla.setCellSelectionEnabled(false);

		JButton botonRefrescar = crearBotonSecundario("Refrescar");

		JPanel filaBoton = crearFilaBotones();
		filaBoton.add(botonRefrescar);

		selectorProductos.bloque.add(filaBoton, gbcBoton(4));

		botonRefrescar.addActionListener(e -> dejarSoloPacks());

		dejarSoloPacks();

		return selectorProductos.bloque;
	}

	private void dejarSoloPacks() {
		if (selectorProductos == null) {
			return;
		}

		limpiarCamposDeFiltro(selectorProductos.bloque);
		desmarcarChecks(selectorProductos.bloque);

		JCheckBox checkPack = buscarCheck(selectorProductos.bloque, "Pack");

		if (checkPack != null) {
			checkPack.setSelected(true);
		}

		ArrayList<String> tipos = new ArrayList<>();
		tipos.add("Pack");

		cargarModeloProductosVenta((DefaultTableModel) selectorProductos.tabla.getModel(), "", tipos,
				new ArrayList<>());
	}

	private JCheckBox buscarCheck(Component componente, String texto) {
		if (componente instanceof JCheckBox) {
			JCheckBox check = (JCheckBox) componente;

			if (texto.equals(check.getText())) {
				return check;
			}
		}

		if (componente instanceof JPanel) {
			JPanel panel = (JPanel) componente;

			for (Component hijo : panel.getComponents()) {
				JCheckBox encontrado = buscarCheck(hijo, texto);

				if (encontrado != null) {
					return encontrado;
				}
			}
		}

		return null;
	}

	private void desmarcarChecks(Component componente) {
		if (componente instanceof JCheckBox) {
			((JCheckBox) componente).setSelected(false);
		}

		if (componente instanceof JPanel) {
			JPanel panel = (JPanel) componente;

			for (Component hijo : panel.getComponents()) {
				desmarcarChecks(hijo);
			}
		}
	}

	private void limpiarCamposDeFiltro(Component componente) {
		if (componente instanceof JTextField) {
			((JTextField) componente).setText("");
		}

		if (componente instanceof JPanel) {
			JPanel panel = (JPanel) componente;

			for (Component hijo : panel.getComponents()) {
				limpiarCamposDeFiltro(hijo);
			}
		}
	}

	private JPanel crearBloqueCrearPack() {
		JPanel bloque = crearBloque("Crear pack");

		JTextField campoNombre = crearCampo();
		JTextArea areaDescripcion = crearArea();
		JTextField campoImagen = crearCampo();
		JTextField campoPrecio = crearCampo();
		JTextField campoStock = crearCampo();

		areaLineasPack = crearArea();

		JPanel panelCrear = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelCrear.setOpaque(false);

		panelCrear.add(crearPanelDatosPack(campoNombre, areaDescripcion, campoImagen, campoPrecio, campoStock));
		panelCrear.add(crearPanelProductosPack());

		bloque.add(panelCrear, gbcCampo(1));

		JButton botonVerLineas = crearBotonSecundario("Ver contenido");
		JButton botonLimpiar = crearBotonSecundario("Limpiar");
		JButton botonCrear = crearBotonAccion("Crear pack");

		JPanel filaBotones = crearFilaBotones();
		filaBotones.add(botonVerLineas);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonLimpiar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonCrear);

		bloque.add(filaBotones, gbcBoton(2));

		botonVerLineas.addActionListener(e -> verContenidoEscrito());
		botonLimpiar.addActionListener(
				e -> limpiarFormularioCrear(campoNombre, areaDescripcion, campoImagen, campoPrecio, campoStock));
		botonCrear
				.addActionListener(e -> crearPack(campoNombre, areaDescripcion, campoImagen, campoPrecio, campoStock));

		return bloque;
	}

	private JPanel crearPanelDatosPack(JTextField campoNombre, JTextArea areaDescripcion, JTextField campoImagen,
			JTextField campoPrecio, JTextField campoStock) {

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos generales"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearCampoFormulario("Nombre", campoNombre));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Descripción", estilizarScroll(areaDescripcion)));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Imagen", campoImagen));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Precio", campoPrecio));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Stock", campoStock));

		return panel;
	}

	private JPanel crearPanelProductosPack() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Productos incluidos"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearLabel("Escribe una línea por producto con este formato: ID;UNIDADES"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(6)));

		JScrollPane scrollLineas = estilizarScroll(areaLineasPack);
		scrollLineas.setPreferredSize(new Dimension(VentanaPrincipal.escalar(330), VentanaPrincipal.escalar(190)));

		panel.add(scrollLineas);

		return panel;
	}

	private JPanel crearBloqueModificarPack() {
		JPanel bloque = crearBloque("Modificar pack existente");

		campoIdPack = crearCampo();
		campoIdProducto = crearCampo();
		campoUnidades = crearCampo();
		campoNuevoPrecio = crearCampo();

		JPanel panelModificar = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelModificar.setOpaque(false);

		panelModificar.add(crearPanelCamposModificar());
		panelModificar.add(crearPanelAccionesModificar());

		bloque.add(panelModificar, gbcCampo(1));

		return bloque;
	}

	private JPanel crearPanelCamposModificar() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos para modificar"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearCampoFormulario("ID pack", campoIdPack));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("ID producto", campoIdProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Unidades", campoUnidades));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Nuevo precio", campoNuevoPrecio));

		return panel;
	}

	private JPanel crearPanelAccionesModificar() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Acciones");
		titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(titulo);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JButton botonVerPack = crearBotonAccion("Ver pack");
		JButton botonAnadir = crearBotonAccion("Añadir producto");
		JButton botonCambiarUnidades = crearBotonAccion("Cambiar unidades");
		JButton botonQuitarProducto = crearBotonAccion("Quitar producto");
		JButton botonCambiarPrecio = crearBotonAccion("Cambiar precio");
		JButton botonEliminarPack = crearBotonAccion("Eliminar pack");

		JPanel botones = new JPanel(new GridLayout(3, 2, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));
		botones.setOpaque(false);
		botones.setAlignmentX(Component.LEFT_ALIGNMENT);

		botones.add(botonVerPack);
		botones.add(botonAnadir);
		botones.add(botonCambiarUnidades);
		botones.add(botonQuitarProducto);
		botones.add(botonCambiarPrecio);
		botones.add(botonEliminarPack);

		botones.setMaximumSize(new Dimension(VentanaPrincipal.escalar(480), VentanaPrincipal.escalar(165)));

		panel.add(botones);

		botonVerPack.addActionListener(e -> verPack());
		botonAnadir.addActionListener(e -> anadirProductoAPack());
		botonCambiarUnidades.addActionListener(e -> cambiarUnidadesPack());
		botonQuitarProducto.addActionListener(e -> quitarProductoDelPack());
		botonCambiarPrecio.addActionListener(e -> cambiarPrecioPack());
		botonEliminarPack.addActionListener(e -> eliminarPack());

		return panel;
	}

	private JPanel crearFilaBotones() {
		JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		fila.setOpaque(false);
		return fila;
	}

	private void verPack() {
		String idPack = campoIdPack.getText().trim();

		if (idPack.isBlank()) {
			mostrarError("Escribe el ID del pack.");
			return;
		}

		mostrarPackEnVentana(idPack);
	}

	private void verContenidoEscrito() {
		try {
			ArrayList<LineaPack> lineas = construirLineasPack(areaLineasPack.getText());

			if (lineas.isEmpty()) {
				mostrarMensaje("Todavía no has escrito productos para el pack.");
				return;
			}

			String texto = crearTextoLineas(lineas);
			mostrarTextoLargo("Contenido escrito", texto);

		} catch (Exception e) {
			mostrarError("No se puede mostrar el contenido: " + e.getMessage());
		}
	}

	private void crearPack(JTextField campoNombre, JTextArea areaDescripcion, JTextField campoImagen,
			JTextField campoPrecio, JTextField campoStock) {

		try {
			String nombre = campoNombre.getText().trim();
			String descripcion = areaDescripcion.getText().trim();
			String imagen = campoImagen.getText().trim();
			Double precio = leerDoubleSeguro(campoPrecio.getText());
			Integer stock = leerEnteroSeguro(campoStock.getText());

			if (nombre.isBlank()) {
				mostrarError("Escribe el nombre del pack.");
				return;
			}

			if (descripcion.isBlank()) {
				mostrarError("Escribe una descripción.");
				return;
			}

			if (imagen.isBlank()) {
				mostrarError("Escribe la ruta de la imagen.");
				return;
			}

			if (precio == null || precio <= 0) {
				mostrarError("Escribe un precio válido.");
				return;
			}

			if (stock == null || stock <= 0) {
				mostrarError("Escribe un stock válido.");
				return;
			}

			ArrayList<LineaPack> lineas = construirLineasPack(areaLineasPack.getText());

			if (lineas.size() < 2) {
				mostrarError("Un pack debe tener al menos dos productos.");
				return;
			}

			boolean creado = empleado.crearPack(nombre, descripcion, imagen, precio, stock, lineas);

			if (creado) {
				dejarSoloPacks();
				limpiarFormularioCrear(campoNombre, areaDescripcion, campoImagen, campoPrecio, campoStock);
				mostrarMensaje("Pack creado correctamente.");
			} else {
				mostrarError("No se pudo crear el pack.");
			}

		} catch (Exception e) {
			mostrarError("No se pudo crear el pack: " + e.getMessage());
		}
	}

	private void limpiarFormularioCrear(JTextField campoNombre, JTextArea areaDescripcion, JTextField campoImagen,
			JTextField campoPrecio, JTextField campoStock) {

		campoNombre.setText("");
		areaDescripcion.setText("");
		campoImagen.setText("");
		campoPrecio.setText("");
		campoStock.setText("");
		areaLineasPack.setText("");
	}

	private void anadirProductoAPack() {
		Integer unidades = leerEnteroSeguro(campoUnidades.getText());

		if (!datosProductoPackValidos(unidades)) {
			return;
		}

		try {
			boolean ok = empleado.añadirProductoaPack(campoIdProducto.getText().trim(), campoIdPack.getText().trim(),
					unidades);

			if (ok) {
				dejarSoloPacks();
				mostrarMensaje("Producto añadido al pack.");
			} else {
				mostrarError("No se pudo añadir el producto.");
			}
		} catch (Exception e) {
			mostrarError("No se pudo añadir el producto: " + e.getMessage());
		}
	}

	private void cambiarUnidadesPack() {
		Integer unidades = leerEnteroSeguro(campoUnidades.getText());

		if (!datosProductoPackValidos(unidades)) {
			return;
		}

		try {
			boolean ok = empleado.modificarUnidadesProductoEnPack(campoIdProducto.getText().trim(),
					campoIdPack.getText().trim(), unidades);

			if (ok) {
				dejarSoloPacks();
				mostrarMensaje("Unidades modificadas correctamente.");
			} else {
				mostrarError("No se pudieron modificar las unidades.");
			}
		} catch (Exception e) {
			mostrarError("No se pudieron modificar las unidades: " + e.getMessage());
		}
	}

	private void quitarProductoDelPack() {
		String idPack = campoIdPack.getText().trim();
		String idProducto = campoIdProducto.getText().trim();

		if (idPack.isBlank() || idProducto.isBlank()) {
			mostrarError("Escribe el ID del pack y el ID del producto.");
			return;
		}

		boolean ok = empleado.eliminarProductoDePack(idPack, idProducto);

		if (ok) {
			dejarSoloPacks();
			mostrarMensaje("Producto quitado del pack.");
		} else {
			mostrarError("No se pudo quitar el producto.");
		}
	}

	private void cambiarPrecioPack() {
		String idPack = campoIdPack.getText().trim();
		Double precio = leerDoubleSeguro(campoNuevoPrecio.getText());

		if (idPack.isBlank()) {
			mostrarError("Escribe el ID del pack.");
			return;
		}

		if (precio == null || precio <= 0) {
			mostrarError("Escribe un precio válido.");
			return;
		}

		boolean ok = empleado.modificarPrecioPack(idPack, precio);

		if (ok) {
			dejarSoloPacks();
			mostrarMensaje("Precio modificado correctamente.");
		} else {
			mostrarError("No se pudo modificar el precio.");
		}
	}

	private void eliminarPack() {
		String idPack = campoIdPack.getText().trim();

		if (idPack.isBlank()) {
			mostrarError("Escribe el ID del pack.");
			return;
		}

		boolean ok = empleado.eliminarPack(idPack);

		if (ok) {
			dejarSoloPacks();
			campoIdPack.setText("");
			mostrarMensaje("Pack eliminado correctamente.");
		} else {
			mostrarError("No se pudo eliminar el pack.");
		}
	}

	private boolean datosProductoPackValidos(Integer unidades) {
		if (campoIdPack.getText().trim().isBlank()) {
			mostrarError("Escribe el ID del pack.");
			return false;
		}

		if (campoIdProducto.getText().trim().isBlank()) {
			mostrarError("Escribe el ID del producto.");
			return false;
		}

		if (unidades == null || unidades <= 0) {
			mostrarError("Escribe unidades válidas.");
			return false;
		}

		return true;
	}

	private void mostrarPackEnVentana(String idPack) {
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idPack);

		if (!(producto instanceof Pack)) {
			mostrarError("No existe ningún pack con ese ID.");
			return;
		}

		Pack pack = (Pack) producto;
		String texto = crearTextoPack(pack);

		mostrarTextoLargo("Información del pack", texto);
	}

	private String crearTextoPack(Pack pack) {
		StringBuilder texto = new StringBuilder();

		texto.append("Pack: ").append(pack.getId()).append(" - ").append(pack.getNombre()).append("\n");
		texto.append("Precio: ").append(formatearPrecio(pack.getPrecioOficial())).append("\n");
		texto.append("Stock: ").append(pack.getStockDisponible()).append("\n");
		texto.append("Productos por separado: ").append(formatearPrecio(pack.calcularSumaProductos())).append("\n\n");

		texto.append("Productos incluidos:\n");

		if (pack.getLineas().isEmpty()) {
			texto.append("Sin productos.");
		} else {
			texto.append(crearTextoLineas(new ArrayList<>(pack.getLineas())));
		}

		return texto.toString();
	}

	private String crearTextoLineas(ArrayList<LineaPack> lineas) {
		StringBuilder texto = new StringBuilder();

		for (LineaPack linea : lineas) {
			ProductoVenta producto = linea.getProducto();

			texto.append("- ");
			texto.append(producto.getId()).append(" | ");
			texto.append(producto.getNombre()).append(" | ");
			texto.append("unidades: ").append(linea.getUnidades()).append(" | ");
			texto.append("subtotal: ").append(formatearPrecio(linea.getSubtotal()));
			texto.append("\n");
		}

		return texto.toString();
	}

	private void mostrarTextoLargo(String titulo, String texto) {
		JTextArea area = crearArea();
		area.setEditable(false);
		area.setText(texto);
		area.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(area);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(620), VentanaPrincipal.escalar(260)));

		JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
	}

	private String formatearPrecio(double precio) {
		return String.format(java.util.Locale.US, "%.2f €", precio).replace('.', ',');
	}
}