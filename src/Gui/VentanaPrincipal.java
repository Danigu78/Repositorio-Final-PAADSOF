package Gui;

import javax.swing.*;
import java.awt.*;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;
import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;
import java.util.List;

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

	
		/**
		 * Inicializa la tienda con datos reales al arrancar la aplicación.
		 * Crea categorías, empleados y productos de ejemplo.
		 */
		private void inicializarDatosTienda() {
		    Gestor gestor = tienda.getGestor();
		    gestor.login( "Admin@1234");

		    // Configurar tiempos
		    gestor.configurarTiemposSistema(60, 30, 30);
		    gestor.setPrecioTasacion(10);

		    // Categorías
		    gestor.crearCategoria("Familiar", "Juegos para toda la familia");
		    gestor.crearCategoria("Estrategia", "Juegos de estrategia");
		    gestor.crearCategoria("Anime", "Productos de anime y manga");
		    gestor.crearCategoria("Accion", "Productos de accion");
		    gestor.crearCategoria("Ciencia-ficcion", "Productos de ciencia ficcion");
		    gestor.crearCategoria("Replicas", "Figuras de coleccionista");
		    gestor.crearCategoria("Retro-Gaming", "Videojuegos y consolas retro");
		    gestor.crearCategoria("Terror", "Productos de terror");

		    // Empleado
		    List<TipoPermisos> permisos = gestor.crearListaPermisos(
		        TipoPermisos.GESTION_STOCK,
		        TipoPermisos.GESTION_CATEGORIAS,
		        TipoPermisos.GESTION_PACKS,
		        TipoPermisos.MODIFICAR_PRODUCTO,
		        TipoPermisos.GESTION_PEDIDOS,
		        TipoPermisos.ENTREGA_PEDIDOS,
		        TipoPermisos.VALORACION_PRODUCTOS,
		        TipoPermisos.CONFIRMACION_INTERCAMBIO
		    );
		    gestor.darDeAltaEmpleados_Permisos("empleado", "Empleado@1234", permisos);
		    Empleado emp = tienda.loginEmpleado("empleado", "Empleado@1234");

		    // Productos - Comics
		    emp.añadirProducto_nuevo("C", "Watchmen", "Clasico del comic", "watchmen.jpg",
		        15.00, 10, tienda.seleccionarCategorias("Accion", "Anime"),
		        400, "DC Comics", 1987, 0,0,0,null,null,0,0,0,0,null);

		    emp.añadirProducto_nuevo("C", "Akira Vol.1", "Manga de ciencia ficcion", "akira.jpg",
		        12.99, 20, tienda.seleccionarCategorias("Anime", "Ciencia-ficcion"),
		        350, "Kodansha", 1982, 0,0,0,null,null,0,0,0,0,null);

		    emp.añadirProducto_nuevo("C", "Maus", "Historia del Holocausto en comic", "maus.jpg",
		        18.00, 8, tienda.seleccionarCategorias("Accion"),
		        296, "Pantheon Books", 1991, 0,0,0,null,null,0,0,0,0,null);

		    emp.añadirProducto_nuevo("C", "V de Vendetta", "Distopia y anarquismo", "vvendetta.jpg",
		        16.00, 12, tienda.seleccionarCategorias("Accion", "Ciencia-ficcion"),
		        296, "DC Comics", 1988, 0,0,0,null,null,0,0,0,0,null);

		    // Productos - Juegos de mesa
		    emp.añadirProducto_nuevo("J", "Catan", "Juego de estrategia", "catan.jpg",
		        45.00, 8, tienda.seleccionarCategorias("Familiar", "Estrategia"),
		        0,null,0,0,0,0,null,null,2,4,8,99,"Estrategia");

		    emp.añadirProducto_nuevo("J", "Pandemic", "Juego cooperativo", "pandemic.jpg",
		        38.00, 6, tienda.seleccionarCategorias("Familiar"),
		        0,null,0,0,0,0,null,null,2,4,8,99,"Cooperativo");

		    emp.añadirProducto_nuevo("J", "Monopoly", "El clasico de los negocios", "monopoly.jpg",
		        35.00, 10, tienda.seleccionarCategorias("Familiar"),
		        0,null,0,0,0,0,null,null,2,6,8,99,"Economico");

		    emp.añadirProducto_nuevo("J", "Cluedo", "Juego de misterio", "cluedo.jpg",
		        30.00, 7, tienda.seleccionarCategorias("Familiar", "Terror"),
		        0,null,0,0,0,0,null,null,2,6,8,99,"Misterio");

		    emp.añadirProducto_nuevo("J", "Ticket to Ride", "Juego de trenes", "ttr.jpg",
		        42.00, 5, tienda.seleccionarCategorias("Familiar", "Estrategia"),
		        0,null,0,0,0,0,null,null,2,5,8,99,"Estrategia");

		    emp.añadirProducto_nuevo("J", "Dixit", "Juego de imaginacion", "dixit.jpg",
		        34.00, 9, tienda.seleccionarCategorias("Familiar"),
		        0,null,0,0,0,0,null,null,3,6,8,99,"Creativo");

		    emp.añadirProducto_nuevo("J", "Risk", "Conquista el mundo", "risk.jpg",
		        40.00, 6, tienda.seleccionarCategorias("Estrategia"),
		        0,null,0,0,0,0,null,null,2,6,10,99,"Estrategia");

		    // Productos - Figuras
		    emp.añadirProducto_nuevo("F", "Figura Goku SSJ", "Figura de Dragon Ball", "goku.jpg",
		        35.00, 5, tienda.seleccionarCategorias("Anime", "Replicas"),
		        0,null,0,20.0,15.0,12.0,"PVC","Bandai",0,0,0,0,null);

		    emp.añadirProducto_nuevo("F", "Figura Darth Vader", "Figura de Star Wars", "vader.jpg",
		        49.99, 4, tienda.seleccionarCategorias("Replicas", "Ciencia-ficcion"),
		        0,null,0,25.0,12.0,10.0,"PVC","Hasbro",0,0,0,0,null);

		    emp.añadirProducto_nuevo("F", "Figura Link", "Figura de Zelda", "link.jpg",
		        39.99, 7, tienda.seleccionarCategorias("Replicas", "Retro-Gaming"),
		        0,null,0,18.0,10.0,8.0,"PVC","Nintendo",0,0,0,0,null);

		    emp.añadirProducto_nuevo("F", "Figura Spider-Man", "Figura articulada de Spider-Man", "spiderman.jpg",
		        44.99, 5, tienda.seleccionarCategorias("Replicas", "Accion"),
		        0,null,0,22.0,12.0,10.0,"PVC","Marvel",0,0,0,0,null);

		    emp.logout();
		}
	

	public VentanaPrincipal() {
		this.tienda = Tienda.getInstancia();
		inicializarDatosTienda();
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