package Gui;

import javax.swing.*;
import java.awt.*;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;

public class VentanaPrincipal extends JFrame {
	public static final int ANCHO = 1200;
	public static final int ALTO = 800;
	public static final Color COLOR_FONDO = new Color(18, 18, 18);// Color negro. Fondo de la ventana
	public static final Color COLOR_ACENTO = new Color(255, 107, 0);// color naranja
	public static final Color COLOR_ACENTO2 = new Color(255, 140, 50);// color naranja suave
	public static final Color COLOR_TEXTO = new Color(240, 240, 240);// color blanco lo usamos en el texto
	public static final Color COLOR_TEXTO2 = new Color(160, 160, 160);// gris
	public static final Color COLOR_PANEL = new Color(28, 28, 28);// gris
	public static final Color COLOR_TARJETA = new Color(38, 38, 38);// gris borde de los productos
	public static final Color COLOR_BORDE = new Color(60, 60, 60);

	// FUENTES NUMERICAS

	// Bloque estático — se ejecuta una sola vez al cargar la clase
	public static final Font FUENTE_TITULO;
	public static final Font FUENTE_SUBTITULO;
	public static final Font FUENTE_NORMAL;
	public static final Font FUENTE_PEQUENA;
	public static final Font FUENTE_LOGO;
	public static final Font FUENTE_BOTON;
	public static final Font FUENTE_PRECIO;
	public static final Font FUENTE_ICONO;

	static {
	    FUENTE_TITULO    = new Font("Segoe UI", Font.BOLD,  28);
	    FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD,  16);
	    FUENTE_NORMAL    = new Font("Segoe UI", Font.PLAIN, 14);
	    FUENTE_PEQUENA   = new Font("Segoe UI", Font.PLAIN, 12);
	    FUENTE_LOGO      = new Font("Segoe UI", Font.BOLD,  36);
	    FUENTE_BOTON     = new Font("Segoe UI", Font.BOLD,  13);
	    FUENTE_PRECIO    = new Font("Segoe UI", Font.BOLD,  15);
	    FUENTE_ICONO     = new Font("Segoe UI Emoji", Font.PLAIN, 60);
	}
	public static final String PANTALLA_LOGIN = "LOGIN";
	public static final String PANTALLA_CLIENTE = "CLIENTE";
	public static final String PANTALLA_EMPLEADO = "EMPLEADO";
	public static final String PANTALLA_GESTOR = "GESTOR";

	private CardLayout cardLayout;
	private JPanel panelContenedor;
	private Tienda tienda;
	private Cliente clienteActual;
	private Empleado empleadoActual;

	private PantallaLogin pantallaLogin;
	private PanelCliente panelCliente;
	private PanelEmpleado panelEmpleado;
	private PanelGestor panelGestor;

	public VentanaPrincipal() {
		this.tienda = Tienda.getInstancia();
		inicializarVentana();
		inicializarPantallas();
		mostrarPantalla(PANTALLA_LOGIN);
		// Calcular tamaño según la pantalla del usuario
		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(pantalla.width * 3 / 4, pantalla.height * 3 / 4);
		setLocationRelativeTo(null);
	}

	// Esta es la clase de la ventana grande la que tiene cerrar u minimizar
	private void inicializarVentana() {
		setTitle("CheckPoint - Tienda Friki");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setBackground(COLOR_FONDO);
		cardLayout = new CardLayout();
		panelContenedor = new JPanel(cardLayout);
		panelContenedor.setBackground(COLOR_FONDO);
		add(panelContenedor);
	}

	private void inicializarPantallas() {
		pantallaLogin = new PantallaLogin(this);
		panelContenedor.add(pantallaLogin, PANTALLA_LOGIN);
//Creamos los ventanas que nos saldran cuando nos registremos
		panelCliente = new PanelCliente(this);
		panelContenedor.add(panelCliente, PANTALLA_CLIENTE);
		panelEmpleado = new PanelEmpleado(this);
		panelContenedor.add(panelEmpleado, PANTALLA_EMPLEADO);
		panelGestor = new PanelGestor(this);
		panelContenedor.add(panelGestor, PANTALLA_GESTOR);
	}

	public void mostrarPantalla(String nombrePantalla) {
		cardLayout.show(panelContenedor, nombrePantalla);
	}

	public void loginCliente(Cliente cliente) {
		this.clienteActual = cliente;
		this.empleadoActual = null;
		panelCliente.actualizarCliente(cliente);
		mostrarPantalla(PANTALLA_CLIENTE);
	}

	public void loginEmpleado(Empleado empleado) {
		this.empleadoActual = empleado;
		this.clienteActual = null;
		panelEmpleado.actualizarEmpleado(empleado);
		mostrarPantalla(PANTALLA_EMPLEADO);
	}

	public void loginGestor(Gestor gestor) {
		this.clienteActual = null;
		this.empleadoActual = null;
		panelGestor.actualizarGestor(gestor);
		mostrarPantalla(PANTALLA_GESTOR);
	}

	public void logout() {
		if (clienteActual != null) {
			clienteActual.logout();
			clienteActual = null;
		}
		if (empleadoActual != null) {
			empleadoActual.logout();
			empleadoActual = null;
		}
		if (pantallaLogin != null) {
			pantallaLogin.limpiar();
		}
		mostrarPantalla(PANTALLA_LOGIN);
	}

	public Tienda getTienda() {
		return tienda;
	}

	public Cliente getClienteActual() {
		return clienteActual;
	}

	public Empleado getEmpleadoActual() {
		return empleadoActual;
	}

	public static void main(String[] args) {
		// 1. Evita que Windows "estire" la ventana y la emborrone
		System.setProperty("sun.java2d.uiScale", "1.0");

		SwingUtilities.invokeLater(() -> {
			try {
				// 2. Intenta poner el LookAndFeel pero con gestión de errores
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// Si falla, Swing usará el estilo "Metal" por defecto, que es igual en todos
				// lados
			}
			new VentanaPrincipal().setVisible(true);
		});
	}

	/**
	 * Escala un tamaño según el DPI de la pantalla del usuario. El escalado máximo
	 * es 1.5 para evitar que se vea demasiado grande en pantallas con DPI muy alto.
	 *
	 * @param tamano El tamaño base en píxeles
	 * @return El tamaño escalado
	 */
	public static int escalar(int tamano) {
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		double escala = dpi / 96.0;
		// Limitamos el escalado máximo a 1.5
		escala = Math.min(escala, 1.5);
		return (int) (tamano * escala);
	}

}