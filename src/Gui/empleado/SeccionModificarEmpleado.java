package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorModificarEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;
import productos.ProductoVenta;
import usuarios.Empleado;

/**
 * Sección para modificar productos de venta.
 * 
 * Permite cargar y editar la información modificable de cómics,
 * juegos y figuras.
 * 
 * @author Lucas
 * @version 1.0
 */
public class SeccionModificarEmpleado extends SeccionProductosVentaEmpleadoBase {

	private static final long serialVersionUID = 1L;

	/**Tipo cómic.*/
	private static final String TIPO_COMIC = "Comic";
	
	/**Tipo juego de mesa.*/
	private static final String TIPO_JUEGO = "Juego";
	
	/**Tipo figura.*/
	private static final String TIPO_FIGURA = "Figura";
	
	/**Panel sin atributos específicos.*/
	private static final String TIPO_SIN_ESPECIFICOS = "Sin específicos";

	/**Tabla de productos.*/
	private TablaVenta tablaProductos;

	/**Campo ID producto.*/
	private JTextField campoIdProducto;
	
	/**Campo tipo producto.*/
	private JTextField campoTipoProducto;
	
	/**Campo nombre producto.*/
	private JTextField campoNombre;
	
	/**Área descripción producto.*/
	private JTextArea areaDescripcion;
	
	/**Campo ruta imagen.*/
	private JTextField campoImagen;

	/**Gestor de tarjetas para tipos.*/
	private CardLayout cardCamposTipo;
	
	/**Panel de campos específicos.*/
	private JPanel panelCamposTipo;

	/**Campo páginas cómic.*/
	private JTextField campoPaginasComic;
	
	/**Campo editorial cómic.*/
	private JTextField campoEditorialComic;
	
	/**Campo año cómic.*/
	private JTextField campoAnioComic;

	/**Campo mínimo jugadores.*/
	private JTextField campoMinJugadores;
	
	/**Campo máximo jugadores.*/
	private JTextField campoMaxJugadores;
	
	/**Campo edad mínima.*/
	private JTextField campoMinEdad;
	
	/**Campo edad máxima.*/
	private JTextField campoMaxEdad;
	
	/**Campo estilo juego.*/
	private JTextField campoEstiloJuego;

	/**Campo altura figura.*/
	private JTextField campoAlturaFigura;
	
	/**Campo ancho figura.*/
	private JTextField campoAnchoFigura;
	
	/**Campo largo figura.*/
	private JTextField campoLargoFigura;
	
	/**Campo material figura.*/
	private JTextField campoMaterialFigura;
	
	/**Campo marca figura.*/
	private JTextField campoMarcaFigura;

	/**Controlador de modificación.*/
	private ControladorModificarEmpleado controlador;

	/**
	 * Constructor de la sección de modificación.
	 * 
	 * @param ventana Ventana principal.
	 * @param empleado Empleado activo.
	 */
	public SeccionModificarEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorModificarEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	/**
	 * Asigna el controlador de la sección.
	 * 
	 * @param controlador Nuevo controlador.
	 */
	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorModificarEmpleado) {
			this.controlador = (ControladorModificarEmpleado) controlador;
		}
	}

	/**
	 * Conecta un botón con una acción.
	 * 
	 * @param boton Botón a conectar.
	 * @param accion Acción asociada.
	 */
	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	/**Construye toda la interfaz.*/
	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Editar producto");
		JPanel contenido = getContenido(panelBase);

		inicializarCampos();

		tablaProductos = crearTablaProductosVenta("Productos actuales",
				"Filtra los productos para consultar sus datos antes de editarlos.", true);

		tablaProductos.tabla.setRowSelectionAllowed(false);
		tablaProductos.tabla.setCellSelectionEnabled(false);

		contenido.add(tablaProductos.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueEditarProducto());

		add(panelBase, BorderLayout.CENTER);
	}

	/**Inicializa todos los campos de formulario.*/
	private void inicializarCampos() {
		campoIdProducto = crearCampo();
		campoTipoProducto = crearCampo();
		campoTipoProducto.setEditable(false);
		campoNombre = crearCampo();
		areaDescripcion = crearArea();
		campoImagen = crearCampo();
		campoImagen.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				normalizarCampoImagen();
			}
		});

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
	 * Crea el bloque principal de edición.
	 * 
	 * @return Bloque de edición.
	 */
	private JPanel crearBloqueEditarProducto() {
		JPanel bloque = crearBloque("Editar datos del producto");

		JPanel panelEdicion = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelEdicion.setOpaque(false);

		panelEdicion.add(crearPanelDatosComunes());
		panelEdicion.add(crearPanelDatosEspecificos());

		bloque.add(panelEdicion, gbcCampo(1));

		JButton botonCargar = crearBotonSecundario("Cargar datos");
		JButton botonGuardar = crearBotonAccion("Guardar cambios");

		JPanel filaBotones = crearFilaBotones();
		filaBotones.add(botonCargar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonGuardar);

		bloque.add(filaBotones, gbcBoton(2));

		conectar(botonCargar, ControladorModificarEmpleado.CARGAR_DATOS);
		conectar(botonGuardar, ControladorModificarEmpleado.GUARDAR_CAMBIOS);

		return bloque;
	}

	/**
	 * Crea el panel de datos comunes.
	 * 
	 * @return Panel de datos comunes.
	 */
	private JPanel crearPanelDatosComunes() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos comunes"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		panel.add(crearCampoFormulario("ID producto", campoIdProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Tipo", campoTipoProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Nombre", campoNombre));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JScrollPane scrollDescripcion = estilizarScroll(areaDescripcion);
		scrollDescripcion.setPreferredSize(new Dimension(VentanaPrincipal.escalar(420), VentanaPrincipal.escalar(120)));
		panel.add(crearCampoFormulario("Descripción", scrollDescripcion));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Ruta de imagen", crearSelectorImagen()));

		return panel;
	}


	/**
	 * Crea el selector de imagen.
	 * 
	 * @return Panel selector de imagen.
	 */
	private JPanel crearSelectorImagen() {
		JButton botonSeleccionarImagen = crearBotonSecundario("Abrir...");
		JButton botonVerImagen = crearBotonSecundario("Ver imagen");

		ajustarBotonImagen(botonSeleccionarImagen);
		ajustarBotonImagen(botonVerImagen);

		conectar(botonSeleccionarImagen, ControladorModificarEmpleado.SELECCIONAR_IMAGEN);
		conectar(botonVerImagen, ControladorModificarEmpleado.VER_IMAGEN);

		JPanel selector = new JPanel();
		selector.setOpaque(false);
		selector.setLayout(new BoxLayout(selector, BoxLayout.Y_AXIS));
		selector.add(campoImagen);
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
	 * Ajusta el tamaño de un botón de imagen.
	 * 
	 * @param boton Botón a ajustar.
	 */
	private void ajustarBotonImagen(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(115), VentanaPrincipal.escalar(36));
		boton.setPreferredSize(tamano);
		boton.setMaximumSize(tamano);
		boton.setMinimumSize(tamano);
	}

	/**
	 * Crea el panel de datos específicos.
	 * 
	 * @return Panel de datos específicos.
	 */
	private JPanel crearPanelDatosEspecificos() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos específicos"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		cardCamposTipo = new CardLayout();
		panelCamposTipo = new JPanel(cardCamposTipo);
		panelCamposTipo.setOpaque(false);
		panelCamposTipo.add(crearCamposComic(), TIPO_COMIC);
		panelCamposTipo.add(crearCamposJuego(), TIPO_JUEGO);
		panelCamposTipo.add(crearCamposFigura(), TIPO_FIGURA);
		panelCamposTipo.add(crearPanelSinEspecificos(), TIPO_SIN_ESPECIFICOS);

		panel.add(panelCamposTipo);
		cardCamposTipo.show(panelCamposTipo, TIPO_SIN_ESPECIFICOS);

		return panel;
	}

	/**
	 * Crea los campos de cómic.
	 * 
	 * @return Panel de cómic.
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
	 * Crea los campos de juego.
	 * 
	 * @return Panel de juego.
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
	 * Crea los campos de figura.
	 * 
	 * @return Panel de figura.
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
	 * Crea el panel sin atributos específicos.
	 * 
	 * @return Panel vacío.
	 */
	private JPanel crearPanelSinEspecificos() {
		JPanel panel = crearPanelCamposTipo();
		panel.add(crearLabel("Este producto no tiene atributos específicos editables aquí."));
		return panel;
	}

	/**
	 * Crea un panel base para atributos específicos.
	 * 
	 * @return Panel de atributos.
	 */
	private JPanel crearPanelCamposTipo() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	/**
	 * Crea una fila para botones.
	 * 
	 * @return Fila de botones.
	 */
	private JPanel crearFilaBotones() {
		JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		fila.setOpaque(false);
		return fila;
	}

	/**Carga los datos del producto indicado.*/
	public void cargarDatosProducto() {
		ProductoVenta producto = buscarProductoEscrito();
		if (producto == null) {
			return;
		}

		campoTipoProducto.setText(controlador.obtenerTipoProducto(producto));
		campoNombre.setText(producto.getNombre());
		areaDescripcion.setText(producto.getDescripcion());
		campoImagen.setText(UtilidadesImagenProducto.normalizarRutaImagen(producto.getImagenRuta()));
		limpiarCamposEspecificos();

		if (producto instanceof Comic) {
			Comic comic = (Comic) producto;
			campoPaginasComic.setText(String.valueOf(comic.getNumeroPaginas()));
			campoEditorialComic.setText(comic.getEditorial());
			campoAnioComic.setText(String.valueOf(comic.getAñoPublicacion()));
			cardCamposTipo.show(panelCamposTipo, TIPO_COMIC);
		} else if (producto instanceof JuegoMesa) {
			JuegoMesa juego = (JuegoMesa) producto;
			campoMinJugadores.setText(String.valueOf(juego.getMinJugadores()));
			campoMaxJugadores.setText(String.valueOf(juego.getMaxJugadores()));
			campoMinEdad.setText(String.valueOf(juego.getMinEdad()));
			campoMaxEdad.setText(String.valueOf(juego.getMaxEdad()));
			campoEstiloJuego.setText(juego.getTipoJuego());
			cardCamposTipo.show(panelCamposTipo, TIPO_JUEGO);
		} else if (producto instanceof Figura) {
			Figura figura = (Figura) producto;
			campoAlturaFigura.setText(String.valueOf(figura.getAltura()));
			campoAnchoFigura.setText(String.valueOf(figura.getAncho()));
			campoLargoFigura.setText(String.valueOf(figura.getLargo()));
			campoMaterialFigura.setText(figura.getMaterial());
			campoMarcaFigura.setText(figura.getMarca());
			cardCamposTipo.show(panelCamposTipo, TIPO_FIGURA);
		} else {
			cardCamposTipo.show(panelCamposTipo, TIPO_SIN_ESPECIFICOS);
		}

		mostrarMensaje("Datos cargados.");
	}

	/**
	 * Busca el producto escrito en el campo ID.
	 * 
	 * @return Producto encontrado o null.
	 */
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

	/**Abre el selector de imagen.*/
	public void seleccionarImagen() {
		JFileChooser selectorImagen = new JFileChooser();
		int opcion = selectorImagen.showOpenDialog(this);

		if (opcion == JFileChooser.APPROVE_OPTION && selectorImagen.getSelectedFile() != null) {
			campoImagen
					.setText(UtilidadesImagenProducto.normalizarRutaImagen(selectorImagen.getSelectedFile().getPath()));
		}
	}

	/**Muestra la imagen del producto.*/
	public void verImagenProducto() {
		normalizarCampoImagen();
		String rutaImagen = campoImagen.getText().trim();

		if (rutaImagen.isBlank()) {
			ProductoVenta producto = buscarProductoEscrito();
			if (producto == null) {
				return;
			}
			rutaImagen = producto.getImagenRuta();
		}

		UtilidadesImagenProducto.mostrarImagenProducto(this, rutaImagen);
	}

	/**Guarda los cambios realizados en el producto.*/
	public void guardarCambios() {
		normalizarCampoImagen();
		ResultadoOperacion resultado = controlador.guardarProducto(campoIdProducto.getText(), campoNombre.getText(),
				areaDescripcion.getText(), campoImagen.getText(), campoPaginasComic.getText(),
				campoEditorialComic.getText(), campoAnioComic.getText(), campoMinJugadores.getText(),
				campoMaxJugadores.getText(), campoMinEdad.getText(), campoMaxEdad.getText(), campoEstiloJuego.getText(),
				campoAlturaFigura.getText(), campoAnchoFigura.getText(), campoLargoFigura.getText(),
				campoMaterialFigura.getText(), campoMarcaFigura.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	/**Normaliza la ruta escrita en el campo imagen.*/
	private void normalizarCampoImagen() {
		campoImagen.setText(UtilidadesImagenProducto.normalizarRutaImagen(campoImagen.getText()));
	}

	/**Limpia todos los campos específicos.*/
	private void limpiarCamposEspecificos() {
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
	}
}
