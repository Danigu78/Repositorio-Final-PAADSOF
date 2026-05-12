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
 * Pantalla para editar los datos modificables de productos de venta.
 *
 * No permite cambiar ID, categorías, reseñas, precio oficial ni stock.
 */
public class SeccionModificarEmpleado extends SeccionProductosVentaEmpleadoBase {

	private static final long serialVersionUID = 1L;

	private static final String TIPO_COMIC = "Comic";
	private static final String TIPO_JUEGO = "Juego";
	private static final String TIPO_FIGURA = "Figura";
	private static final String TIPO_SIN_ESPECIFICOS = "Sin específicos";

	private TablaVenta tablaProductos;

	private JTextField campoIdProducto;
	private JTextField campoTipoProducto;
	private JTextField campoNombre;
	private JTextArea areaDescripcion;
	private JTextField campoImagen;

	private CardLayout cardCamposTipo;
	private JPanel panelCamposTipo;

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

	private ControladorModificarEmpleado controlador;

	public SeccionModificarEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorModificarEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorModificarEmpleado) {
			this.controlador = (ControladorModificarEmpleado) controlador;
		}
	}

	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

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

	private void ajustarBotonImagen(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(115), VentanaPrincipal.escalar(36));
		boton.setPreferredSize(tamano);
		boton.setMaximumSize(tamano);
		boton.setMinimumSize(tamano);
	}

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

	private JPanel crearPanelSinEspecificos() {
		JPanel panel = crearPanelCamposTipo();
		panel.add(crearLabel("Este producto no tiene atributos específicos editables aquí."));
		return panel;
	}

	private JPanel crearPanelCamposTipo() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	private JPanel crearFilaBotones() {
		JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		fila.setOpaque(false);
		return fila;
	}

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

	public void seleccionarImagen() {
		JFileChooser selectorImagen = new JFileChooser();
		int opcion = selectorImagen.showOpenDialog(this);

		if (opcion == JFileChooser.APPROVE_OPTION && selectorImagen.getSelectedFile() != null) {
			campoImagen
					.setText(UtilidadesImagenProducto.normalizarRutaImagen(selectorImagen.getSelectedFile().getPath()));
		}
	}

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

	private void normalizarCampoImagen() {
		campoImagen.setText(UtilidadesImagenProducto.normalizarRutaImagen(campoImagen.getText()));
	}

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
