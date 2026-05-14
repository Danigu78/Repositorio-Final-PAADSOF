package Gui;

import javax.swing.*;
import java.awt.*;

import Gui.Controladores.ControladorVentana;
import Gui.cliente.PanelCliente;
import Gui.Gestor.*;
import Gui.empleado.PanelEmpleado;
import Gui.invitado.PanelInvitado;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;


/**
 * Ventana principal de la aplicación CheckPoint.
 * Gestiona las distintas pantallas de la aplicación,
 * así como la carga y guardado de los datos de la tienda.
 *
 * @author dani
 * @version 1.0
 */
public class VentanaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	/** Color de fondo principal de la aplicación. */
	public static final Color COLOR_FONDO = new Color(241, 240, 236);
	/** Color de acento principal. */
	public static final Color COLOR_ACENTO = new Color(225, 206, 136);
	/** Segundo color de acento. */
	public static final Color COLOR_ACENTO2 = new Color(236, 221, 170);
	/** Color principal del texto. */
	public static final Color COLOR_TEXTO = new Color(47, 47, 47);
	/** Color secundario del texto. */
	public static final Color COLOR_TEXTO2 = new Color(103, 103, 103);
	/** Color de los paneles. */
	public static final Color COLOR_PANEL = new Color(250, 249, 246);
	/** Color de las tarjetas. */
	public static final Color COLOR_TARJETA = new Color(255, 255, 252);
	/** Color de los bordes. */
	public static final Color COLOR_BORDE = new Color(205, 201, 191);
	/** Color de la barra superior. */
	public static final Color COLOR_BARRA = new Color(45, 45, 45);
	/** Color de la barra al pasar el ratón. */
	public static final Color COLOR_BARRA_HOVER = new Color(62, 62, 62);
	/** Color del texto de la barra. */
	public static final Color COLOR_TEXTO_BARRA = new Color(232, 232, 232);
	/** Fuente para títulos principales. */
	public static final Font FUENTE_TITULO;
	/** Fuente para subtítulos. */
	public static final Font FUENTE_SUBTITULO;
	/** Fuente normal de texto. */
	public static final Font FUENTE_NORMAL;
	/** Fuente pequeña de texto. */
	public static final Font FUENTE_PEQUENA;
	/** Fuente utilizada para el logo. */
	public static final Font FUENTE_LOGO;
	/** Fuente utilizada en botones. */
	public static final Font FUENTE_BOTON;
	/** Fuente utilizada para precios. */
	public static final Font FUENTE_PRECIO;
	/** Fuente utilizada para iconos. */
	public static final Font FUENTE_ICONO;

	static {
		FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, escalar(28));
		FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, escalar(16));
		FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, escalar(14));
		FUENTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, escalar(12));
		FUENTE_LOGO = new Font("Segoe UI", Font.BOLD, escalar(36));
		FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, escalar(13));
		FUENTE_PRECIO = new Font("Segoe UI", Font.BOLD, escalar(15));
		FUENTE_ICONO = new Font("Segoe UI Emoji", Font.PLAIN, escalar(60));
	}

	/** Identificador de la pantalla de login. */
	public static final String PANTALLA_LOGIN = "LOGIN";

	/** Identificador de la pantalla de cliente. */
	public static final String PANTALLA_CLIENTE = "CLIENTE";

	/** Identificador de la pantalla de empleado. */
	public static final String PANTALLA_EMPLEADO = "EMPLEADO";

	/** Identificador de la pantalla de gestor. */
	public static final String PANTALLA_GESTOR = "GESTOR";

	/** Identificador de la pantalla de invitado. */
	public static final String SEC_INVITADO = "INVITADO";
	/** Layout encargado de cambiar entre pantallas. */
	private CardLayout cardLayout;

	/** Panel contenedor de todas las pantallas. */
	private JPanel panelContenedor;
	/** Controlador principal de la ventana. */
	private ControladorVentana controlador;

	/**
	 * Constructor de la ventana principal.
	 */
	public VentanaPrincipal() {
		GuardadoTienda.cargar();

		this.controlador = new ControladorVentana(this);

		if (tiendaNecesitaDatosIniciales()) {
			controlador.inicializarDatosTienda();
			GuardadoTienda.guardar(Tienda.getInstancia());
		}

		inicializarVentana();
		inicializarPantallas();
		configurarGuardadoAlCerrar();

		mostrarPantalla(PANTALLA_LOGIN);

		setMinimumSize(new Dimension(800, 600));

		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(pantalla.width * 3 / 4, pantalla.height * 3 / 4);
		setLocationRelativeTo(null);
	}

	/**
	 * Comprueba si la tienda necesita cargar datos iniciales.
	 *
	 * @return true si la tienda está vacía, false en caso contrario
	 */
	private boolean tiendaNecesitaDatosIniciales() {
		Tienda tienda = Tienda.getInstancia();

		return tienda.getStockVentas().isEmpty() && tienda.getCategorias().isEmpty()
				&& tienda.obtenerClientesTienda().isEmpty() && tienda.obtenerEmpleadosTienda().isEmpty();
	}

	/**
	 * Configura la ventana principal con título y CardLayout.
	 */
	private void inicializarVentana() {
		setTitle("CheckPoint - Tienda Friki");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);

		getContentPane().setBackground(COLOR_FONDO);

		cardLayout = new CardLayout();
		panelContenedor = new JPanel(cardLayout);
		panelContenedor.setBackground(COLOR_FONDO);

		add(panelContenedor);
	}

	/**
	 * Crea e inicializa todas las pantallas de la aplicación.
	 */
	private void inicializarPantallas() {
		PantallaLogin pantallaLogin = new PantallaLogin(this);
		PanelCliente panelCliente = new PanelCliente(this);
		PanelEmpleado panelEmpleado = new PanelEmpleado(this);
		PanelGestor panelGestor = new PanelGestor(this);
		PanelInvitado panelInvitado = new PanelInvitado(this);

		controlador.setPantallas(pantallaLogin, panelCliente, panelEmpleado, panelGestor);

		panelContenedor.add(pantallaLogin, PANTALLA_LOGIN);
		panelContenedor.add(panelCliente, PANTALLA_CLIENTE);
		panelContenedor.add(panelEmpleado, PANTALLA_EMPLEADO);
		panelContenedor.add(panelGestor, PANTALLA_GESTOR);
		panelContenedor.add(panelInvitado, SEC_INVITADO);
	}

	/**
	 * Guarda la tienda antes de cerrar la aplicación.
	 */
	private void configurarGuardadoAlCerrar() {
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				GuardadoTienda.guardar(Tienda.getInstancia());
				dispose();
				System.exit(0);
			}
		});
	}

	/**
	 * Muestra la pantalla indicada por su identificador.
	 *
	 * @param nombrePantalla identificador de la pantalla
	 */
	public void mostrarPantalla(String nombrePantalla) {
		cardLayout.show(panelContenedor, nombrePantalla);
	}

	/**
	 * Delega el login de cliente al controlador.
	 *
	 * @param cliente cliente que ha iniciado sesión
	 */
	public void loginCliente(Cliente cliente) {
		controlador.loginCliente(cliente);
	}

	/**
	 * Delega el login de empleado al controlador.
	 *
	 * @param empleado empleado que ha iniciado sesión
	 */
	public void loginEmpleado(Empleado empleado) {
		controlador.loginEmpleado(empleado);
	}

	/**
	 * Delega el login de gestor al controlador.
	 *
	 * @param gestor gestor que ha iniciado sesión
	 */
	public void loginGestor(Gestor gestor) {
		controlador.loginGestor(gestor);
	}

	/**
	 * Delega el login de invitado al controlador.
	 */
	public void loginInvitado() {
		controlador.loginInvitado();
	}

	/**
	 * Guarda los datos y cierra la sesión actual.
	 */
	public void logout() {
		GuardadoTienda.guardar(Tienda.getInstancia());
		controlador.logout();
	}

	/**
	 * Devuelve el cliente actualmente logueado.
	 *
	 * @return cliente actual
	 */
	public Cliente getClienteActual() {
		return controlador.getClienteActual();
	}

	/**
	 * Devuelve el empleado actualmente logueado.
	 *
	 * @return empleado actual
	 */
	public Empleado getEmpleadoActual() {
		return controlador.getEmpleadoActual();
	}

	/**
	 * Devuelve la instancia de la tienda.
	 *
	 * @return tienda
	 */
	public Tienda getTienda() {
		return Tienda.getInstancia();
	}

	/**
	 * Escala un tamaño según el DPI de la pantalla.
	 *
	 * @param tamano tamaño base
	 * @return tamaño escalado
	 */
	public static int escalar(int tamano) {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		double escalaReal = gd.getDefaultConfiguration().getDefaultTransform().getScaleX();

		if (escalaReal > 1.0) {
			return tamano;
		}

		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		double escala = dpi / 96.0;

		return (int) (tamano * escala);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// Se usa el estilo por defecto si no se puede cargar el del sistema.
			}

			new VentanaPrincipal().setVisible(true);
		});
	}
	/**
	 * Refresca el combo de categorías del catálogo cliente tras cambios del gestor.
	 */
	public void refrescarCategoriasCatalogo() {
	    controlador.refrescarCategoriasCatalogo();
	}

}