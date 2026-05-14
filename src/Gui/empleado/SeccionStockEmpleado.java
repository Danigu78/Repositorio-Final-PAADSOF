package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorStockEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import usuarios.Empleado;

/**
 * Panel de gestión de stock para el empleado.
 * 
 * Permite consultar productos, modificar stock, eliminar productos, cargar
 * datos desde fichero y crear nuevos productos.
 *
 * @author Lucas
 * @version 1.0
 */
public class SeccionStockEmpleado extends SeccionProductosVentaEmpleadoBase {

	private static final long serialVersionUID = 1L;

	/** Tipos de producto disponibles en el sistema */
	private static final String TIPO_COMIC = "Comic";
	private static final String TIPO_JUEGO = "Juego";
	private static final String TIPO_FIGURA = "Figura";

	/** Campo para introducir el ID del producto */
	private JTextField campoIdProducto;

	/** Campo para introducir unidades a modificar */
	private JTextField campoUnidades;

	/** Campo para ruta del fichero de carga */
	private JTextField campoFichero;

	/** Controlador principal de stock del empleado */
	private ControladorStockEmpleado controlador;

	/** Tabla de productos mostrada en pantalla */
	private TablaVenta tablaProductos;

	/** Lista de categorías seleccionadas para un producto */
	private java.util.List<JCheckBox> checksCategoriasProducto;

	/** Panel con CardLayout para campos específicos por tipo */
	private JPanel panelCamposTipo;

	/** Layout para alternar formularios de tipo */
	private CardLayout cardCamposTipo;

	/** Combo para seleccionar tipo de producto */
	private JComboBox<String> comboTipoProducto;

	/** Campos generales de un producto */
	private JTextField campoNombreProducto;
	private JTextArea areaDescripcionProducto;
	private JTextField campoImagenProducto;
	private JTextField campoPrecioProducto;
	private JTextField campoStockProducto;

	/** Campos especificos de un comic */
	private JTextField campoPaginasComic;
	private JTextField campoEditorialComic;
	private JTextField campoAnioComic;

	/** Campos especificos de un juego de mesa */
	private JTextField campoMinJugadores;
	private JTextField campoMaxJugadores;
	private JTextField campoMinEdad;
	private JTextField campoMaxEdad;
	private JTextField campoEstiloJuego;

	/** Campos especificos de una figura */
	private JTextField campoAlturaFigura;
	private JTextField campoAnchoFigura;
	private JTextField campoLargoFigura;
	private JTextField campoMaterialFigura;
	private JTextField campoMarcaFigura;

	/**
	 * Constructor del panel de stock del empleado.
	 *
	 * @param ventana  ventana principal de la aplicación
	 * @param empleado empleado autenticado
	 */
	public SeccionStockEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorStockEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	/**
	 * Asigna el controlador de acciones al panel.
	 *
	 * @param controlador controlador que manejará los eventos de la vista
	 */
	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorStockEmpleado) {
			this.controlador = (ControladorStockEmpleado) controlador;
		}
	}

	/**
	 * Conecta un botón con una acción del controlador.
	 *
	 * @param boton  botón a configurar
	 * @param accion comando de acción asociado
	 */
	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	/**
	 * Construye toda la interfaz gráfica del panel.
	 */
	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Inventario");
		JPanel contenido = getContenido(panelBase);

		campoIdProducto = crearCampo();
		campoUnidades = crearCampo();
		campoFichero = crearCampo();

		// La tabla es solo para mirar el stock. El ID se escribe a mano abajo.
		tablaProductos = crearTablaProductosVenta("Productos actuales",
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

	/**
	 * Crea el bloque de acciones de stock (sumar, restar, eliminar y carga de
	 * fichero).
	 *
	 * @return panel con las acciones de stock
	 */
	private JPanel crearBloqueAccionesStock() {
		JPanel bloque = crearBloque("Acciones de stock");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(40), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelReponerStock());
		panelAcciones.add(crearPanelCargarFichero());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	/**
	 * Panel izquierdo de acciones de stock: sumar, restar y eliminar producto.
	 *
	 * @return panel de reponer stock
	 */
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
		JButton botonEliminar = crearBotonPeligro("Eliminar producto");

		ajustarBotonStock(botonSumar);
		ajustarBotonStock(botonRestar);
		ajustarBotonStock(botonEliminar);

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonSumar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonRestar);

		gbc.gridy = 3;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, 0, 0);
		panel.add(filaBotones, gbc);

		JPanel filaEliminar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaEliminar.setOpaque(false);
		filaEliminar.add(botonEliminar);

		gbc.gridy = 4;
		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, 0, 0);
		panel.add(filaEliminar, gbc);

		gbc.gridy = 5;
		gbc.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), gbc);

		conectar(botonSumar, ControladorStockEmpleado.SUMAR_STOCK);
		conectar(botonRestar, ControladorStockEmpleado.RESTAR_STOCK);
		conectar(botonEliminar, ControladorStockEmpleado.ELIMINAR_PRODUCTO);

		return panel;
	}

	/**
	 * Panel derecho para cargar productos desde un fichero externo.
	 *
	 * @return panel de carga de fichero
	 */
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

	/**
	 * Bloque principal para creación de productos nuevos.
	 *
	 * @return panel con formulario de creación
	 */
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

	/**
	 * Inicializa todos los campos del formulario de creación de producto.
	 */
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

	/**
	 * Construye el panel de datos generales del producto
	 *
	 * @return panel con todos los campos de datos generales del producto
	 */
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

	/**
	 * Crea el selector de imagen del producto.
	 *
	 * @return panel con campo de texto y botones de selección de imagen
	 */
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

	/**
	 * Ajusta el tamaño visual de los botones usados en el selector de imagen.
	 *
	 * @param boton botón al que se le aplica el tamaño fijo
	 */
	private void ajustarBotonImagen(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(115), VentanaPrincipal.escalar(36));
		boton.setPreferredSize(tamano);
		boton.setMaximumSize(tamano);
		boton.setMinimumSize(tamano);
	}

	/**
	 * Crea el panel de selección de categorías del producto.
	 *
	 * @return panel con checkboxes dentro de un scroll
	 */
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

	/**
	 * Crea el panel derecho con los campos específicos del tipo de producto.
	 *
	 * @return panel con los formularios específicos por tipo
	 */
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

		comboTipoProducto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardCamposTipo.show(panelCamposTipo, String.valueOf(comboTipoProducto.getSelectedItem()));
			}
		});
		cardCamposTipo.show(panelCamposTipo, TIPO_COMIC);

		return panel;
	}

	/**
	 * Crea los campos específicos del tipo Comic.
	 *
	 * @return panel con formulario de Comic
	 */
	private JPanel crearCamposComic() {
		JPanel panel = crearPanelCamposTipo();
		panel.add(crearCampoFormulario("Páginas", campoPaginasComic));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Editorial", campoEditorialComic));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Año publicación", campoAnioComic));
		return panel;
	}

	/**
	 * Crea los campos específicos del tipo Juego.
	 *
	 * @return panel con formulario de Juego
	 */
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

	/**
	 * Crea los campos específicos del tipo Figura.
	 *
	 * @return panel con formulario de Figura
	 */
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

	/**
	 * Crea un panel base vacío para alojar los campos dinámicos de tipo.
	 *
	 * @return panel contenedor base
	 */
	private JPanel crearPanelCamposTipo() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	/**
	 * Ajusta el tamaño del formulario para mantener consistencia visual.
	 *
	 * @param panelFormulario panel al que se le aplica tamaño fijo
	 */
	private void ajustarAnchoFormulario(JPanel panelFormulario) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(520), VentanaPrincipal.escalar(68));
		panelFormulario.setPreferredSize(tamano);
		panelFormulario.setMaximumSize(tamano);
		panelFormulario.setMinimumSize(tamano);
	}

	/**
	 * Ajusta el tamaño de los botones usados en acciones de stock.
	 *
	 * @param boton botón al que se le aplica el tamaño fijo
	 */
	private void ajustarBotonStock(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(210), VentanaPrincipal.escalar(42));
		boton.setPreferredSize(tamano);
		boton.setMaximumSize(tamano);
		boton.setMinimumSize(tamano);
	}

	/**
	 * Aumenta el stock de un producto utilizando el ID y la cantidad indicada.
	 */
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

	/**
	 * Reduce el stock de un producto utilizando el ID y la cantidad indicada.
	 */
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

	/**
	 * Elimina un producto del sistema estableciendo su stock a 0 y ocultándolo de
	 * la tienda.
	 */
	public void eliminarProducto() {
		String idProducto = campoIdProducto.getText().trim();

		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto.");
			return;
		}

		int opcion = JOptionPane.showConfirmDialog(this,
				"Se pondra el stock del producto a 0 y dejara de aparecer en la tienda.\nQuieres continuar?",
				"Eliminar producto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (opcion != JOptionPane.YES_OPTION) {
			return;
		}

		ResultadoOperacion resultado = controlador.eliminarProducto(idProducto);

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			campoIdProducto.setText("");
			campoUnidades.setText("");
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	/**
	 * Abre un selector de archivos para elegir un fichero del sistema.
	 */
	public void seleccionarFichero() {
		JFileChooser selectorFichero = new JFileChooser(); // Para abrir archivos del ordenador

		int opcion = selectorFichero.showOpenDialog(this);

		// Si cancela, no hacemos nada.
		if (opcion == JFileChooser.APPROVE_OPTION && selectorFichero.getSelectedFile() != null) {
			campoFichero.setText(selectorFichero.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Abre un selector de archivos para seleccionar una imagen del producto.
	 */
	public void seleccionarImagenProducto() {
		JFileChooser selectorImagen = new JFileChooser();
		int opcion = selectorImagen.showOpenDialog(this);

		if (opcion == JFileChooser.APPROVE_OPTION && selectorImagen.getSelectedFile() != null) {
			campoImagenProducto
					.setText(UtilidadesImagenProducto.normalizarRutaImagen(selectorImagen.getSelectedFile().getPath()));
		}
	}

	/**
	 * Muestra la imagen del producto actualmente indicada en el campo de texto.
	 */
	public void verImagenProducto() {
		normalizarCampoImagenProducto();
		String rutaImagen = campoImagenProducto.getText().trim();

		if (rutaImagen.isBlank()) {
			mostrarError("Selecciona o escribe la imagen del producto.");
			return;
		}

		UtilidadesImagenProducto.mostrarImagenProducto(this, rutaImagen);
	}

	/**
	 * Carga productos desde un fichero externo utilizando la ruta indicada.
	 */
	public void cargarFichero() {
		ResultadoOperacion resultado = controlador.cargarProductosDesdeFichero(campoFichero.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	/**
	 * Crea un nuevo producto con todos los datos introducidos en el formulario.
	 */
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

	/**
	 * Obtiene las categorías seleccionadas en el formulario de creación de
	 * producto.
	 *
	 * @return cadena de categorías separadas por comas
	 */
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

	/**
	 * Limpia completamente el formulario de creación de producto.
	 */
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

	/**
	 * Normaliza la ruta de la imagen introducida en el campo correspondiente.
	 */
	private void normalizarCampoImagenProducto() {
		campoImagenProducto.setText(UtilidadesImagenProducto.normalizarRutaImagen(campoImagenProducto.getText()));
	}
}
