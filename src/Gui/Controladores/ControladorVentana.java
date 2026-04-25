package Gui.Controladores;

import Gui.VentanaPrincipal;
import Gui.PanelCliente;
import Gui.PanelEmpleado;
import Gui.PanelGestor;
import Gui.PantallaLogin;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;
import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;
import java.util.List;

/**
 * Controlador de la ventana principal de CheckPoint. Gestiona la navegación
 * entre pantallas y la inicialización de datos.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorVentana {

	private VentanaPrincipal ventana;
	private Tienda tienda;
	private PantallaLogin pantallaLogin;
	private PanelCliente panelCliente;
	private PanelEmpleado panelEmpleado;
	private PanelGestor panelGestor;
	private Cliente clienteActual;
	private Empleado empleadoActual;

	/**
	 * Constructor del controlador de la ventana principal.
	 *
	 * @param ventana La ventana principal
	 */
	public ControladorVentana(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Asigna las referencias a los paneles de la aplicación.
	 *
	 * @param pantallaLogin La pantalla de login
	 * @param panelCliente  El panel del cliente
	 * @param panelEmpleado El panel del empleado
	 * @param panelGestor   El panel del gestor
	 */
	public void setPantallas(PantallaLogin pantallaLogin, PanelCliente panelCliente, PanelEmpleado panelEmpleado,
			PanelGestor panelGestor) {
		this.pantallaLogin = pantallaLogin;
		this.panelCliente = panelCliente;
		this.panelEmpleado = panelEmpleado;
		this.panelGestor = panelGestor;
	}

	/**
	 * Inicializa la tienda con datos reales al arrancar la aplicación. Crea
	 * categorías, empleados y productos de ejemplo.
	 */
	public void inicializarDatosTienda() {
		Gestor gestor = tienda.getGestor();
		gestor.login("Admin@1234");

		gestor.configurarTiemposSistema(60, 30, 30);
		gestor.setPrecioTasacion(10);

		gestor.crearCategoria("Familiar", "Juegos para toda la familia");
		gestor.crearCategoria("Estrategia", "Juegos de estrategia");
		gestor.crearCategoria("Anime", "Productos de anime y manga");
		gestor.crearCategoria("Accion", "Productos de accion");
		gestor.crearCategoria("Ciencia-ficcion", "Productos de ciencia ficcion");
		gestor.crearCategoria("Replicas", "Figuras de coleccionista");
		gestor.crearCategoria("Retro-Gaming", "Videojuegos y consolas retro");
		gestor.crearCategoria("Terror", "Productos de terror");

		List<TipoPermisos> permisos = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK,
				TipoPermisos.GESTION_CATEGORIAS, TipoPermisos.GESTION_PACKS, TipoPermisos.MODIFICAR_PRODUCTO,
				TipoPermisos.GESTION_PEDIDOS, TipoPermisos.ENTREGA_PEDIDOS, TipoPermisos.VALORACION_PRODUCTOS,
				TipoPermisos.CONFIRMACION_INTERCAMBIO);
		gestor.darDeAltaEmpleados_Permisos("empleado", "Empleado@1234", permisos);
		Empleado emp = tienda.loginEmpleado("empleado", "Empleado@1234");

		emp.añadirProducto_nuevo("C", "Watchmen", "Clasico del comic", "watchmen.jpg", 15.00, 10,
				tienda.seleccionarCategorias("Accion", "Anime"), 400, "DC Comics", 1987, 0, 0, 0, null, null, 0, 0, 0,
				0, null);
		emp.añadirProducto_nuevo("C", "Akira Vol.1", "Manga de ciencia ficcion", "akira.jpg", 12.99, 20,
				tienda.seleccionarCategorias("Anime", "Ciencia-ficcion"), 350, "Kodansha", 1982, 0, 0, 0, null, null, 0,
				0, 0, 0, null);
		emp.añadirProducto_nuevo("C", "Maus", "Historia del Holocausto en comic", "maus.jpg", 18.00, 8,
				tienda.seleccionarCategorias("Accion"), 296, "Pantheon Books", 1991, 0, 0, 0, null, null, 0, 0, 0, 0,
				null);
		emp.añadirProducto_nuevo("C", "V de Vendetta", "Distopia y anarquismo", "vvendetta.jpg", 16.00, 12,
				tienda.seleccionarCategorias("Accion", "Ciencia-ficcion"), 296, "DC Comics", 1988, 0, 0, 0, null, null,
				0, 0, 0, 0, null);
		emp.añadirProducto_nuevo("J", "Catan", "Juego de estrategia", "catan.jpg", 45.00, 8,
				tienda.seleccionarCategorias("Familiar", "Estrategia"), 0, null, 0, 0, 0, 0, null, null, 2, 4, 8, 99,
				"Estrategia");
		emp.añadirProducto_nuevo("J", "Pandemic", "Juego cooperativo", "pandemic.jpg", 38.00, 6,
				tienda.seleccionarCategorias("Familiar"), 0, null, 0, 0, 0, 0, null, null, 2, 4, 8, 99, "Cooperativo");
		emp.añadirProducto_nuevo("J", "Monopoly", "El clasico de los negocios", "monopoly.jpg", 35.00, 10,
				tienda.seleccionarCategorias("Familiar"), 0, null, 0, 0, 0, 0, null, null, 2, 6, 8, 99, "Economico");
		emp.añadirProducto_nuevo("J", "Cluedo", "Juego de misterio", "cluedo.jpg", 30.00, 7,
				tienda.seleccionarCategorias("Familiar", "Terror"), 0, null, 0, 0, 0, 0, null, null, 2, 6, 8, 99,
				"Misterio");
		emp.añadirProducto_nuevo("J", "Ticket to Ride", "Juego de trenes", "ttr.jpg", 42.00, 5,
				tienda.seleccionarCategorias("Familiar", "Estrategia"), 0, null, 0, 0, 0, 0, null, null, 2, 5, 8, 99,
				"Estrategia");
		emp.añadirProducto_nuevo("J", "Dixit", "Juego de imaginacion", "dixit.jpg", 34.00, 9,
				tienda.seleccionarCategorias("Familiar"), 0, null, 0, 0, 0, 0, null, null, 3, 6, 8, 99, "Creativo");
		emp.añadirProducto_nuevo("J", "Risk", "Conquista el mundo", "risk.jpg", 40.00, 6,
				tienda.seleccionarCategorias("Estrategia"), 0, null, 0, 0, 0, 0, null, null, 2, 6, 10, 99,
				"Estrategia");
		emp.añadirProducto_nuevo("F", "Figura Goku SSJ", "Figura de Dragon Ball", "goku.jpg", 35.00, 5,
				tienda.seleccionarCategorias("Anime", "Replicas"), 0, null, 0, 20.0, 15.0, 12.0, "PVC", "Bandai", 0, 0,
				0, 0, null);
		emp.añadirProducto_nuevo("F", "Figura Darth Vader", "Figura de Star Wars", "vader.jpg", 49.99, 4,
				tienda.seleccionarCategorias("Replicas", "Ciencia-ficcion"), 0, null, 0, 25.0, 12.0, 10.0, "PVC",
				"Hasbro", 0, 0, 0, 0, null);
		emp.añadirProducto_nuevo("F", "Figura Link", "Figura de Zelda", "link.jpg", 39.99, 7,
				tienda.seleccionarCategorias("Replicas", "Retro-Gaming"), 0, null, 0, 18.0, 10.0, 8.0, "PVC",
				"Nintendo", 0, 0, 0, 0, null);
		emp.añadirProducto_nuevo("F", "Figura Spider-Man", "Figura articulada de Spider-Man", "spiderman.jpg", 44.99, 5,
				tienda.seleccionarCategorias("Replicas", "Accion"), 0, null, 0, 22.0, 12.0, 10.0, "PVC", "Marvel", 0, 0,
				0, 0, null);
		emp.logout();
	}

	/**
	 * Navega al panel del cliente y actualiza sus datos.
	 *
	 * @param cliente El cliente que ha iniciado sesión
	 */
	public void loginCliente(Cliente cliente) {
		this.clienteActual = cliente;
		this.empleadoActual = null;
		panelCliente.actualizarCliente(cliente);
		ventana.mostrarPantalla(VentanaPrincipal.PANTALLA_CLIENTE);
	}

	/**
	 * Navega al panel del empleado y actualiza sus datos.
	 *
	 * @param empleado El empleado que ha iniciado sesión
	 */
	public void loginEmpleado(Empleado empleado) {
		this.empleadoActual = empleado;
		this.clienteActual = null;
		panelEmpleado.actualizarEmpleado(empleado);
		ventana.mostrarPantalla(VentanaPrincipal.PANTALLA_EMPLEADO);
	}

	/**
	 * Navega al panel del gestor y actualiza sus datos.
	 *
	 * @param gestor El gestor que ha iniciado sesión
	 */
	public void loginGestor(Gestor gestor) {
		this.clienteActual = null;
		this.empleadoActual = null;
		panelGestor.actualizarGestor(gestor);
		ventana.mostrarPantalla(VentanaPrincipal.PANTALLA_GESTOR);
	}

	/**
	 * Navega al panel de invitado sin necesidad de registro.
	 */
	public void loginInvitado() {
		this.clienteActual = null;
		this.empleadoActual = null;
		ventana.mostrarPantalla(VentanaPrincipal.SEC_INVITADO);
	}

	/**
	 * Cierra la sesión del usuario actual y vuelve al login.
	 */
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
		ventana.mostrarPantalla(VentanaPrincipal.PANTALLA_LOGIN);
	}

	/**
	 * Devuelve el cliente actualmente logueado.
	 *
	 * @return El cliente actual o null si no hay ninguno
	 */
	public Cliente getClienteActual() {
		return clienteActual;
	}

	/**
	 * Devuelve el empleado actualmente logueado.
	 *
	 * @return El empleado actual o null si no hay ninguno
	 */
	public Empleado getEmpleadoActual() {
		return empleadoActual;
	}
}