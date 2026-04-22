package Gui;

import javax.swing.*;
import java.awt.*;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;

public class VentanaPrincipal extends JFrame {
	public static final int ANCHO = 1200;
	public static final int ALTO = 800;
	public static final Color COLOR_FONDO = new Color(241, 240, 236); // gris cálido claro
	public static final Color COLOR_ACENTO = new Color(225, 206, 136); // amarillo pastel / mostaza suave
	public static final Color COLOR_ACENTO2 = new Color(236, 221, 170); // hover
	public static final Color COLOR_TEXTO = new Color(47, 47, 47); // texto principal
	public static final Color COLOR_TEXTO2 = new Color(103, 103, 103); // texto secundario
	public static final Color COLOR_PANEL = new Color(250, 249, 246); // paneles
	public static final Color COLOR_TARJETA = new Color(255, 255, 252); // tarjetas
	public static final Color COLOR_BORDE = new Color(205, 201, 191); // bordes suaves

	// Nuevos para barra superior y navegación
	public static final Color COLOR_BARRA = new Color(45, 45, 45); // gris oscuro elegante
	public static final Color COLOR_BARRA_HOVER = new Color(62, 62, 62); // hover barra
	public static final Color COLOR_TEXTO_BARRA = new Color(232, 232, 232);

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
		FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
		FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
		FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
		FUENTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);
		FUENTE_LOGO = new Font("Segoe UI", Font.BOLD, 36);
		FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 13);
		FUENTE_PRECIO = new Font("Segoe UI", Font.BOLD, 15);
		FUENTE_ICONO = new Font("Segoe UI Emoji", Font.PLAIN, 60);
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

	private static void cargarDatosDemo() {
		Tienda tienda = Tienda.getInstancia();

		if (tienda.getStockVentas() != null && !tienda.getStockVentas().isEmpty()) {
			return;
		}

		// Comics
		tienda.añadirProducto(new Comic("One Piece Vol. 1", "Primer volumen del manga One Piece", "default.png", 8.95,
				12, 192, "Planeta", 2021));

		tienda.añadirProducto(new Comic("Batman Año Uno", "Cómic clásico de Batman", "default.png", 15.50, 7, 144,
				"DC Comics", 2019));

		tienda.añadirProducto(
				new Comic("Naruto Vol. 3", "Manga shonen clásico", "default.png", 8.95, 3, 192, "Planeta", 2020));

		tienda.añadirProducto(new Comic("Chainsaw Man Vol. 2", "Manga de acción y terror", "default.png", 9.50, 15, 200,
				"Norma", 2022));

		tienda.añadirProducto(
				new Comic("Watchmen", "Novela gráfica imprescindible", "default.png", 22.95, 4, 416, "ECC", 2018));

		tienda.añadirProducto(
				new Comic("Sandman Vol. 1", "Edición recopilatoria", "default.png", 19.90, 6, 256, "ECC", 2021));

		// Juegos de mesa
		tienda.añadirProducto(new JuegoMesa("Catan", "Juego de estrategia y comercio", "default.png", 34.95, 5, 3, 4,
				10, 99, "Estrategia"));

		tienda.añadirProducto(new JuegoMesa("Virus!", "Juego de cartas rápido y divertido", "default.png", 14.95, 18, 2,
				6, 8, 99, "Cartas"));

		tienda.añadirProducto(new JuegoMesa("Carcassonne", "Juego de colocación de losetas", "default.png", 29.95, 9, 2,
				5, 7, 99, "Familiar"));

		tienda.añadirProducto(new JuegoMesa("Azul", "Juego abstracto de estrategia", "default.png", 32.95, 2, 2, 4, 8,
				99, "Abstracto"));

		tienda.añadirProducto(new JuegoMesa("Terraforming Mars", "Juego de estrategia avanzada", "default.png", 54.95,
				4, 1, 5, 12, 99, "Estrategia"));

		tienda.añadirProducto(new JuegoMesa("Dixit", "Juego creativo e imaginativo", "default.png", 31.50, 11, 3, 6, 8,
				99, "Creativo"));

		// Figuras
		tienda.añadirProducto(new Figura("Goku Super Saiyan", "Figura de colección de Dragon Ball", "default.png",
				29.99, 6, 25.0, 10.0, 8.0, "PVC", "Banpresto"));

		tienda.añadirProducto(new Figura("Spider-Man Marvel", "Figura articulada de Spider-Man", "default.png", 24.99,
				9, 18.0, 8.0, 6.0, "ABS", "Hasbro"));

		tienda.añadirProducto(new Figura("Link Breath of the Wild", "Figura coleccionable de Zelda", "default.png",
				39.95, 3, 22.0, 9.0, 7.0, "PVC", "Good Smile"));

		tienda.añadirProducto(new Figura("Darth Vader", "Figura de colección Star Wars", "default.png", 34.95, 8, 24.0,
				10.0, 8.0, "PVC", "Hasbro"));

		tienda.añadirProducto(new Figura("Luffy Gear 5", "Figura especial de One Piece", "default.png", 44.95, 1, 27.0,
				11.0, 9.0, "PVC", "Banpresto"));

		tienda.añadirProducto(new Figura("Iron Man Mark 85", "Figura articulada Marvel", "default.png", 49.95, 5, 20.0,
				8.0, 7.0, "ABS", "Hasbro"));

		tienda.añadirProducto(new Figura("Totoro", "Figura decorativa de Studio Ghibli", "default.png", 21.95, 10, 15.0,
				9.0, 9.0, "Resina", "Ensky"));
	}

	public VentanaPrincipal() {
		this.tienda = Tienda.getInstancia();
		inicializarVentana();
		inicializarPantallas();
		mostrarPantalla(PANTALLA_LOGIN);
		this.setMinimumSize(new Dimension(800, 600));
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
		System.out.println("ENTRANDO EN PANEL CLIENTE");
		this.clienteActual = cliente;
		this.empleadoActual = null;
		panelCliente.actualizarCliente(cliente);
		mostrarPantalla(PANTALLA_CLIENTE);
	}

	public void loginEmpleado(Empleado empleado) {
		System.out.println("ENTRANDO EN PANEL EMPLEADO");
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
			cargarDatosDemo();
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