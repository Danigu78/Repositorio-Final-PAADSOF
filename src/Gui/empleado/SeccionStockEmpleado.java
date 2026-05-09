package Gui.empleado;


import Gui.VentanaPrincipal;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

import Gui.controladores.empleado.ControladorStockEmpleado;
import Gui.controladores.empleado.ResultadoOperacion;
import usuarios.Empleado;

/**
 * Panel de gestión de stock para el empleado.
 * 
 * Desde aquí se pueden consultar productos, reponer stock y cargar productos
 * desde un fichero.
 */
public class SeccionStockEmpleado extends SeccionProductosVentaEmpleadoBase {

	private static final long serialVersionUID = 1L;

	private static final String TIPO_COMIC = "Comic";
	private static final String TIPO_JUEGO = "Juego";
	private static final String TIPO_FIGURA = "Figura";

	private JTextField campoIdProducto;
	private JTextField campoUnidades;
	private JTextField campoFichero;
	private ControladorStockEmpleado controlador;

	/* La guardamos para poder refrescarla después de cambiar algo */
	private SelectorVenta tablaProductos;
	private java.util.List<JCheckBox> checksCategoriasProducto;
	private JPanel panelCamposTipo;
	private CardLayout cardCamposTipo;
	private JComboBox<String> comboTipoProducto;

	private JTextField campoNombreProducto;
	private JTextArea areaDescripcionProducto;
	private JTextField campoImagenProducto;
	private JTextField campoPrecioProducto;
	private JTextField campoStockProducto;

	private JTextField campoPaginasComic;
	private JTextField campoEditorialComic;
	private JTextField campoAnioComic;

	private JTextField campoMinJugadores;
	private JTextField campoMaxJugadores;
	private JTextField campoMinEdad;
	private JTextField campoMaxEdad;
	private JTextField campoEstiloJuego;

	private JTextField campoAlturaFigura;
	private JTextField campoAnchoFigura;
	private JTextField campoLargoFigura;
	private JTextField campoMaterialFigura;
	private JTextField campoMarcaFigura;

	public SeccionStockEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorStockEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorStockEmpleado) {
			this.controlador = (ControladorStockEmpleado) controlador;
		}
	}

	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Inventario");
		JPanel contenido = getContenido(panelBase);

		campoIdProducto = crearCampo();
		campoUnidades = crearCampo();
		campoFichero = crearCampo();

		// La tabla es solo para mirar el stock. El ID se escribe a mano abajo.
		tablaProductos = crearSelectorProductosVenta("Productos actuales",
				"Filtra los productos para consultar el stock disponible.", true);

		// No queremos que al pinchar una fila parezca que hace algo especial.
		tablaProductos.tabla.setRowSelectionAllowed(false);
		tablaProductos.tabla.setCellSelectionEnabled(false);

		contenido.add(tablaProductos.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAccionesStock());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCrearProducto());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloqueAccionesStock() {
		JPanel bloque = crearBloque("Acciones de stock");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(40), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelReponerStock());
		panelAcciones.add(crearPanelCargarFichero());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	// Panel de la izquierda: sirve para sumar unidades a un producto
	private JPanel crearPanelReponerStock() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(10), 0);

		JLabel titulo = crearLabel("Ajustar unidades");
		gbc.gridy = 0;
		panel.add(titulo, gbc);

		JPanel campoId = crearCampoFormulario("ID producto", campoIdProducto);
		ajustarAnchoFormulario(campoId);
		gbc.gridy = 1;
		panel.add(campoId, gbc);

		JPanel campoCantidad = crearCampoFormulario("Cantidad", campoUnidades);
		ajustarAnchoFormulario(campoCantidad);
		gbc.gridy = 2;
		panel.add(campoCantidad, gbc);

		JButton botonSumar = crearBotonAccion("Sumar stock");
		JButton botonRestar = crearBotonSecundario("Restar stock");

		ajustarBotonStock(botonSumar);
		ajustarBotonStock(botonRestar);

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonSumar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonRestar);

		gbc.gridy = 3;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, 0, 0);
		panel.add(filaBotones, gbc);

		gbc.gridy = 4;
		gbc.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), gbc);

		conectar(botonSumar, ControladorStockEmpleado.SUMAR_STOCK);
		conectar(botonRestar, ControladorStockEmpleado.RESTAR_STOCK);

		return panel;
	}

	// Panel de la derecha: cargar productos desde un txt
	private JPanel crearPanelCargarFichero() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(10), 0);

		JLabel titulo = crearLabel("Cargar productos desde fichero");
		gbc.gridy = 0;
		panel.add(titulo, gbc);

		JPanel campoRuta = crearCampoFormulario("Ruta del fichero", campoFichero);
		ajustarAnchoFormulario(campoRuta);
		gbc.gridy = 1;
		panel.add(campoRuta, gbc);

		JButton botonExaminar = crearBotonSecundario("Abrir...");
		JButton botonCargar = crearBotonAccion("Cargar fichero");

		ajustarBotonStock(botonExaminar);
		ajustarBotonStock(botonCargar);

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonExaminar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonCargar);

		gbc.gridy = 2;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, 0, 0);
		panel.add(filaBotones, gbc);

		gbc.gridy = 3;
		gbc.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), gbc);

		conectar(botonExaminar, ControladorStockEmpleado.SELECCIONAR_FICHERO);
		conectar(botonCargar, ControladorStockEmpleado.CARGAR_FICHERO);

		return panel;
	}

	private JPanel crearBloqueCrearProducto() {
		JPanel bloque = crearBloque("Crear producto");

		inicializarCamposCrearProducto();

		JPanel panelCrear = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelCrear.setOpaque(false);
		panelCrear.add(crearPanelDatosProducto());
		panelCrear.add(crearPanelTipoProducto());

		bloque.add(panelCrear, gbcCampo(1));

		JButton botonLimpiar = crearBotonSecundario("Limpiar");
		JButton botonCrear = crearBotonAccion("Crear producto");

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonLimpiar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonCrear);

		bloque.add(filaBotones, gbcBoton(2));

		conectar(botonLimpiar, ControladorStockEmpleado.LIMPIAR_PRODUCTO);
		conectar(botonCrear, ControladorStockEmpleado.CREAR_PRODUCTO);

		return bloque;
	}

	private void inicializarCamposCrearProducto() {
		checksCategoriasProducto = new java.util.ArrayList<>();

		comboTipoProducto = crearCombo(new String[] { TIPO_COMIC, TIPO_JUEGO, TIPO_FIGURA });
		campoNombreProducto = crearCampo();
		areaDescripcionProducto = crearArea();
		campoImagenProducto = crearCampo();
		campoImagenProducto.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				normalizarCampoImagenProducto();
			}
		});
		campoPrecioProducto = crearCampo();
		campoStockProducto = crearCampo();

		campoPaginasComic = crearCampo();
		campoEditorialComic = crearCampo();
		campoAnioComic = crearCampo();

		campoMinJugadores = crearCampo();
		campoMaxJugadores = crearCampo();
		campoMinEdad = crearCampo();
		campoMaxEdad = crearCampo();
		campoEstiloJuego = crearCampo();

		campoAlturaFigura = crearCampo();
		campoAnchoFigura = crearCampo();
		campoLargoFigura = crearCampo();
		campoMaterialFigura = crearCampo();
		campoMarcaFigura = crearCampo();
	}

	private JPanel crearPanelDatosProducto() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos generales"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		panel.add(crearCampoFormulario("Tipo", comboTipoProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Nombre", campoNombreProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Descripción", estilizarScroll(areaDescripcionProducto)));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Imagen", crearSelectorImagenProducto()));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Precio", campoPrecioProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Stock", campoStockProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearLabel("Categorías"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));
		panel.add(crearPanelCategoriasProducto());

		return panel;
	}

	private JPanel crearSelectorImagenProducto() {
		JButton botonSeleccionarImagen = crearBotonSecundario("Abrir...");
		JButton botonVerImagen = crearBotonSecundario("Ver imagen");

		ajustarBotonImagen(botonSeleccionarImagen);
		ajustarBotonImagen(botonVerImagen);

		conectar(botonSeleccionarImagen, ControladorStockEmpleado.SELECCIONAR_IMAGEN);
		conectar(botonVerImagen, ControladorStockEmpleado.VER_IMAGEN);

		JPanel selector = new JPanel();
		selector.setOpaque(false);
		selector.setLayout(new BoxLayout(selector, BoxLayout.Y_AXIS));
		selector.add(campoImagenProducto);
		selector.add(Box.createVerticalStrut(VentanaPrincipal.escalar(6)));

		JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		botones.setOpaque(false);
		botones.add(botonSeleccionarImagen);
		botones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(8)));
		botones.add(botonVerImagen);
		selector.add(botones);

		return selector;
	}

	private void ajustarBotonImagen(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(115), VentanaPrincipal.escalar(36));
		boton.setPreferredSize(tamano);
		boton.setMaximumSize(tamano);
		boton.setMinimumSize(tamano);
	}

	private JPanel crearPanelCategoriasProducto() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
		panel.setOpaque(false);
		checksCategoriasProducto.clear();

		for (String nombreCategoria : controlador.getNombresCategorias()) {
			JCheckBox check = new JCheckBox(nombreCategoria);
			check.setOpaque(false);
			check.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			check.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			check.setFocusPainted(false);

			checksCategoriasProducto.add(check);
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

	private JPanel crearPanelTipoProducto() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos del tipo"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		cardCamposTipo = new CardLayout();
		panelCamposTipo = new JPanel(cardCamposTipo);
		panelCamposTipo.setOpaque(false);
		panelCamposTipo.add(crearCamposComic(), TIPO_COMIC);
		panelCamposTipo.add(crearCamposJuego(), TIPO_JUEGO);
		panelCamposTipo.add(crearCamposFigura(), TIPO_FIGURA);

		panel.add(panelCamposTipo);

		comboTipoProducto.addActionListener(
				e -> cardCamposTipo.show(panelCamposTipo, String.valueOf(comboTipoProducto.getSelectedItem())));
		cardCamposTipo.show(panelCamposTipo, TIPO_COMIC);

		return panel;
	}

	private JPanel crearCamposComic() {
		JPanel panel = crearPanelCamposTipo();
		panel.add(crearCampoFormulario("Páginas", campoPaginasComic));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Editorial", campoEditorialComic));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Año publicación", campoAnioComic));
		return panel;
	}

	private JPanel crearCamposJuego() {
		JPanel panel = crearPanelCamposTipo();
		panel.add(crearCampoFormulario("Mín. jugadores", campoMinJugadores));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Máx. jugadores", campoMaxJugadores));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Edad mínima", campoMinEdad));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Edad máxima", campoMaxEdad));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Estilo", campoEstiloJuego));
		return panel;
	}

	private JPanel crearCamposFigura() {
		JPanel panel = crearPanelCamposTipo();
		panel.add(crearCampoFormulario("Altura", campoAlturaFigura));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Ancho", campoAnchoFigura));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Largo", campoLargoFigura));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Material", campoMaterialFigura));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Marca", campoMarcaFigura));
		return panel;
	}

	private JPanel crearPanelCamposTipo() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	private void ajustarAnchoFormulario(JPanel panelFormulario) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(520), VentanaPrincipal.escalar(68));
		panelFormulario.setPreferredSize(tamano);
		panelFormulario.setMaximumSize(tamano);
		panelFormulario.setMinimumSize(tamano);
	}

	private void ajustarBotonStock(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(210), VentanaPrincipal.escalar(42));
		boton.setPreferredSize(tamano);
		boton.setMaximumSize(tamano);
		boton.setMinimumSize(tamano);
	}

	public void reponerStock() {
		ResultadoOperacion resultado = controlador.reponerStock(campoIdProducto.getText(), campoUnidades.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			campoUnidades.setText("");
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void retirarStock() {
		ResultadoOperacion resultado = controlador.retirarStock(campoIdProducto.getText(), campoUnidades.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			campoUnidades.setText("");
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void seleccionarFichero() {
		JFileChooser selectorFichero = new JFileChooser(); // Para abrir archivos del ordenador

		int opcion = selectorFichero.showOpenDialog(this);

		// Si cancela, no hacemos nada.
		if (opcion == JFileChooser.APPROVE_OPTION && selectorFichero.getSelectedFile() != null) {
			campoFichero.setText(selectorFichero.getSelectedFile().getAbsolutePath());
		}
	}

	public void seleccionarImagenProducto() {
		JFileChooser selectorImagen = new JFileChooser();
		int opcion = selectorImagen.showOpenDialog(this);

		if (opcion == JFileChooser.APPROVE_OPTION && selectorImagen.getSelectedFile() != null) {
			campoImagenProducto
					.setText(UtilidadesImagenProducto.normalizarRutaImagen(selectorImagen.getSelectedFile().getPath()));
		}
	}

	public void verImagenProducto() {
		normalizarCampoImagenProducto();
		String rutaImagen = campoImagenProducto.getText().trim();

		if (rutaImagen.isBlank()) {
			mostrarError("Selecciona o escribe la imagen del producto.");
			return;
		}

		UtilidadesImagenProducto.mostrarImagenProducto(this, rutaImagen);
	}

	public void cargarFichero() {
		ResultadoOperacion resultado = controlador.cargarProductosDesdeFichero(campoFichero.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	public void crearProducto() {
		normalizarCampoImagenProducto();
		ResultadoOperacion resultado = controlador.crearProducto(String.valueOf(comboTipoProducto.getSelectedItem()),
				campoNombreProducto.getText(), areaDescripcionProducto.getText(), campoImagenProducto.getText(),
				campoPrecioProducto.getText(), campoStockProducto.getText(), obtenerCategoriasSeleccionadas(),
				campoPaginasComic.getText(), campoEditorialComic.getText(), campoAnioComic.getText(),
				campoMinJugadores.getText(), campoMaxJugadores.getText(), campoMinEdad.getText(),
				campoMaxEdad.getText(), campoEstiloJuego.getText(), campoAlturaFigura.getText(),
				campoAnchoFigura.getText(), campoLargoFigura.getText(), campoMaterialFigura.getText(),
				campoMarcaFigura.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			limpiarFormularioProducto();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private String obtenerCategoriasSeleccionadas() {
		String texto = "";

		for (JCheckBox check : checksCategoriasProducto) {
			if (check.isSelected()) {
				if (!texto.isEmpty()) {
					texto += ",";
				}
				texto += check.getText();
			}
		}

		return texto;
	}

	public void limpiarFormularioProducto() {
		comboTipoProducto.setSelectedIndex(0);
		campoNombreProducto.setText("");
		areaDescripcionProducto.setText("");
		campoImagenProducto.setText("");
		campoPrecioProducto.setText("");
		campoStockProducto.setText("");

		campoPaginasComic.setText("");
		campoEditorialComic.setText("");
		campoAnioComic.setText("");

		campoMinJugadores.setText("");
		campoMaxJugadores.setText("");
		campoMinEdad.setText("");
		campoMaxEdad.setText("");
		campoEstiloJuego.setText("");

		campoAlturaFigura.setText("");
		campoAnchoFigura.setText("");
		campoLargoFigura.setText("");
		campoMaterialFigura.setText("");
		campoMarcaFigura.setText("");

		for (JCheckBox check : checksCategoriasProducto) {
			check.setSelected(false);
		}
	}

	private void normalizarCampoImagenProducto() {
		campoImagenProducto.setText(UtilidadesImagenProducto.normalizarRutaImagen(campoImagenProducto.getText()));
	}
}
