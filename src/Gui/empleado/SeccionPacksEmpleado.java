package Gui.empleado;


import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorPacksEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import productos.LineaPack;
import productos.Pack;
import usuarios.Empleado;

/**
 * Pantalla para gestionar packs de productos.
 * 
 * Permite consultar productos, crear packs y modificar packs ya existentes.
 */
public class SeccionPacksEmpleado extends SeccionProductosVentaEmpleadoBase {

	private static final long serialVersionUID = 1L;

	private TablaVenta tablaProductos;

	private JTextArea areaLineasPack;

	private ArrayList<JCheckBox> checksCategoriasPack;
	private JTextField campoNombrePack;
	private JTextArea areaDescripcionPack;
	private JTextField campoImagenPack;
	private JTextField campoPrecioPack;
	private JTextField campoStockPack;

	private JTextField campoIdPack;
	private JTextField campoIdProducto;
	private JTextField campoUnidades;
	private JTextField campoNuevoPrecio;

	private ControladorPacksEmpleado controlador;

	public SeccionPacksEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorPacksEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorPacksEmpleado) {
			this.controlador = (ControladorPacksEmpleado) controlador;
		}
	}

	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
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
		tablaProductos = crearTablaProductosVenta("Packs existentes",
				"Busca productos igual que en el resto de secciones. Por defecto se muestran los packs.", false);

		tablaProductos.tabla.setRowSelectionAllowed(false);
		tablaProductos.tabla.setCellSelectionEnabled(false);

		JButton botonRefrescar = crearBotonSecundario("Refrescar");

		JPanel filaBoton = crearFilaBotones();
		filaBoton.add(botonRefrescar);

		tablaProductos.bloque.add(filaBoton, gbcBoton(4));

		conectar(botonRefrescar, ControladorPacksEmpleado.REFRESCAR_PACKS);

		dejarSoloPacks();

		return tablaProductos.bloque;
	}

	public void dejarSoloPacks() {
		if (tablaProductos == null) {
			return;
		}

		limpiarCamposDeFiltro(tablaProductos.bloque);
		desmarcarChecks(tablaProductos.bloque);

		JCheckBox checkPack = buscarCheck(tablaProductos.bloque, "Pack");

		if (checkPack != null) {
			checkPack.setSelected(true);
		}

		ArrayList<String> tipos = new ArrayList<>();
		tipos.add("Pack");

		cargarModeloProductosVenta((DefaultTableModel) tablaProductos.tabla.getModel(), "", tipos,
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

		campoNombrePack = crearCampo();
		areaDescripcionPack = crearArea();
		campoImagenPack = crearCampo();
		campoPrecioPack = crearCampo();
		campoStockPack = crearCampo();

		checksCategoriasPack = new ArrayList<>();
		areaLineasPack = crearArea();

		JPanel panelCrear = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelCrear.setOpaque(false);

		panelCrear.add(crearPanelDatosPack(campoNombrePack, areaDescripcionPack, campoImagenPack, campoPrecioPack,
				campoStockPack));
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

		conectar(botonVerLineas, ControladorPacksEmpleado.VER_CONTENIDO);
		conectar(botonLimpiar, ControladorPacksEmpleado.LIMPIAR_CREAR);
		conectar(botonCrear, ControladorPacksEmpleado.CREAR_PACK);

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
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearLabel("Categorías"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));
		panel.add(crearPanelCategoriasPack());

		return panel;
	}

	private JPanel crearPanelCategoriasPack() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
		panel.setOpaque(false);

		checksCategoriasPack.clear();

		for (String nombreCategoria : controlador.getNombresCategorias()) {
			JCheckBox check = new JCheckBox(nombreCategoria);

			check.setOpaque(false);
			check.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			check.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			check.setFocusPainted(false);

			checksCategoriasPack.add(check);
			panel.add(check);
		}

		JScrollPane scroll = new JScrollPane(panel);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setBorder(null);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(390), VentanaPrincipal.escalar(80)));

		JPanel contenedor = new JPanel(new BorderLayout());
		contenedor.setOpaque(false);
		contenedor.add(scroll, BorderLayout.CENTER);

		return contenedor;
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

		conectar(botonVerPack, ControladorPacksEmpleado.VER_PACK);
		conectar(botonAnadir, ControladorPacksEmpleado.ANADIR_PRODUCTO);
		conectar(botonCambiarUnidades, ControladorPacksEmpleado.CAMBIAR_UNIDADES);
		conectar(botonQuitarProducto, ControladorPacksEmpleado.QUITAR_PRODUCTO);
		conectar(botonCambiarPrecio, ControladorPacksEmpleado.CAMBIAR_PRECIO);
		conectar(botonEliminarPack, ControladorPacksEmpleado.ELIMINAR_PACK);

		return panel;
	}

	private JPanel crearFilaBotones() {
		JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		fila.setOpaque(false);
		return fila;
	}

	public void verPack() {
		String idPack = campoIdPack.getText().trim();

		if (idPack.isBlank()) {
			mostrarError("Escribe el ID del pack.");
			return;
		}

		mostrarPackEnVentana(idPack);
	}

	public void verContenidoEscrito() {
		try {
			ArrayList<LineaPack> lineas = controlador.construirLineasPack(areaLineasPack.getText());

			if (lineas.isEmpty()) {
				mostrarMensaje("Todavía no has escrito productos para el pack.");
				return;
			}

			Pack packTemporal = new Pack("Pack temporal", "Temporal", "sin_imagen.png", 1, 1);
			packTemporal.setLineas(lineas);

			double precioTotal = packTemporal.calcularSumaProductos();

			String texto = crearTextoLineas(lineas);
			texto += "\n\nPrecio total de los productos: " + formatearPrecio(precioTotal);

			mostrarTextoLargo("Contenido escrito", texto);

		} catch (Exception e) {
			mostrarError("No se puede mostrar el contenido: " + e.getMessage());
		}
	}

	public void crearPack() {
		crearPack(campoNombrePack, areaDescripcionPack, campoImagenPack, campoPrecioPack, campoStockPack);
	}

	private void crearPack(JTextField campoNombre, JTextArea areaDescripcion, JTextField campoImagen,
			JTextField campoPrecio, JTextField campoStock) {

		ResultadoOperacion resultado = controlador.crearPack(campoNombre.getText(), areaDescripcion.getText(),
				campoImagen.getText(), campoPrecio.getText(), campoStock.getText(), areaLineasPack.getText(),
				obtenerCategoriasSeleccionadas());

		if (resultado.isExito()) {
			dejarSoloPacks();
			limpiarFormularioCrear(campoNombre, areaDescripcion, campoImagen, campoPrecio, campoStock);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private String obtenerCategoriasSeleccionadas() {
		String texto = "";

		if (checksCategoriasPack == null) {
			return texto;
		}

		for (JCheckBox check : checksCategoriasPack) {
			if (check.isSelected()) {
				if (!texto.isEmpty()) {
					texto += ",";
				}

				texto += check.getText();
			}
		}

		return texto;
	}

	public void limpiarFormularioCrear() {
		limpiarFormularioCrear(campoNombrePack, areaDescripcionPack, campoImagenPack, campoPrecioPack, campoStockPack);
	}

	private void limpiarFormularioCrear(JTextField campoNombre, JTextArea areaDescripcion, JTextField campoImagen,
			JTextField campoPrecio, JTextField campoStock) {

		campoNombre.setText("");
		areaDescripcion.setText("");
		campoImagen.setText("");
		campoPrecio.setText("");
		campoStock.setText("");
		areaLineasPack.setText("");

		if (checksCategoriasPack != null) {
			for (JCheckBox check : checksCategoriasPack) {
				check.setSelected(false);
			}
		}
	}

	public void anadirProductoAPack() {
		ResultadoOperacion resultado = controlador.anadirProductoAPack(campoIdProducto.getText(), campoIdPack.getText(),
				campoUnidades.getText());

		if (resultado.isExito()) {
			dejarSoloPacks();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void cambiarUnidadesPack() {
		ResultadoOperacion resultado = controlador.cambiarUnidadesPack(campoIdProducto.getText(), campoIdPack.getText(),
				campoUnidades.getText());

		if (resultado.isExito()) {
			dejarSoloPacks();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void quitarProductoDelPack() {
		ResultadoOperacion resultado = controlador.quitarProductoDelPack(campoIdPack.getText(),
				campoIdProducto.getText());

		if (resultado.isExito()) {
			dejarSoloPacks();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void cambiarPrecioPack() {
		ResultadoOperacion resultado = controlador.cambiarPrecioPack(campoIdPack.getText(), campoNuevoPrecio.getText());

		if (resultado.isExito()) {
			dejarSoloPacks();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void eliminarPack() {
		ResultadoOperacion resultado = controlador.eliminarPack(campoIdPack.getText());

		if (resultado.isExito()) {
			dejarSoloPacks();
			campoIdPack.setText("");
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private void mostrarPackEnVentana(String idPack) {
		Pack pack = controlador.buscarPack(idPack);

		if (pack == null) {
			mostrarError("No existe ningún pack con ese ID.");
			return;
		}

		mostrarTextoLargo("Información del pack", controlador.crearTextoPack(pack));
	}

	private String crearTextoLineas(ArrayList<LineaPack> lineas) {
		return controlador.crearTextoLineas(lineas);
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
		return controlador.formatearPrecio(precio);
	}
}
