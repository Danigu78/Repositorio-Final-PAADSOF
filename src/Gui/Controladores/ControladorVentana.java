package Gui.Controladores;

import Gui.VentanaPrincipal;
import Gui.Gestor.PanelGestor;
import Gui.PanelCliente;
import Gui.empleado.PanelEmpleado;
import Gui.PantallaLogin;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;
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
	 * categorías, empleados, productos de ejemplo y algunos packs de prueba.
	 */
	public void inicializarDatosTienda() {
		// Intentamos cargar el estado completo desde el fichero .dat
		Tienda tiendaCargada = GuardadoTienda.cargar();

		// Si el fichero existía ya tiene todo — clientes, empleados, productos, etc.
		// GuardadoTienda.cargar() devuelve una tienda nueva si no existe el fichero
		// Una tienda nueva no tiene stock, así que eso nos sirve para distinguir
		if (!tiendaCargada.getStockVentas().isEmpty()) {
			return;
		}

		// Primera vez — no hay .dat, inicializamos desde cero
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

		// El empleado carga los productos desde productos.txt
		Empleado emp = tienda.loginEmpleado("empleado", "Empleado@1234");
		emp.cargarProductosFicheroTexto("ficheros/productos.txt");
		emp.logout();
		gestor.logout();

		// Guardamos el estado inicial en datos_tienda.dat
		GuardadoTienda.guardar(tienda);
	}
	//

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
