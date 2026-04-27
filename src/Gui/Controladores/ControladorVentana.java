package Gui.Controladores;

import Gui.VentanaPrincipal;
import intercambios.Oferta;
import Gui.PanelCliente;
import Gui.PanelEmpleado;
import Gui.PanelGestor;
import Gui.PantallaLogin;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;
import ventas.EstadoPedido;
import ventas.Pedido;
import productos.Comic;
import productos.EstadoProducto;
import productos.Figura;
import productos.JuegoMesa;
import java.util.List;
import java.util.ArrayList;
import productos.LineaPack;
import productos.Producto2Mano;
import productos.ProductoVenta;

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
		gestor.darDeAltaEmpleados_Permisos("lucasblannco", "35455128Ff*", permisos);

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

		// Packs de prueba para comprobar la sección de packs

		ProductoVenta watchmen = tienda.buscarproductoPorNombre("Watchmen").get(0);
		ProductoVenta akira = tienda.buscarproductoPorNombre("Akira Vol.1").get(0);
		ProductoVenta vVendetta = tienda.buscarproductoPorNombre("V de Vendetta").get(0);

		ArrayList<LineaPack> lineasPackComics = new ArrayList<>();
		lineasPackComics.add(new LineaPack(watchmen, 1));
		lineasPackComics.add(new LineaPack(akira, 1));
		lineasPackComics.add(new LineaPack(vVendetta, 1));

		emp.crearPack("Pack Comics Distopicos", "Pack con comics clasicos de ciencia ficcion y distopia",
				"pack_comics.jpg", 39.99, 2, lineasPackComics);

		ProductoVenta catan = tienda.buscarproductoPorNombre("Catan").get(0);
		ProductoVenta ticketToRide = tienda.buscarproductoPorNombre("Ticket to Ride").get(0);
		ProductoVenta dixit = tienda.buscarproductoPorNombre("Dixit").get(0);

		ArrayList<LineaPack> lineasPackJuegos = new ArrayList<>();
		lineasPackJuegos.add(new LineaPack(catan, 1));
		lineasPackJuegos.add(new LineaPack(ticketToRide, 1));
		lineasPackJuegos.add(new LineaPack(dixit, 1));

		emp.crearPack("Pack Juegos Familiares", "Pack de juegos de mesa para jugar en familia", "pack_juegos.jpg",
				105.00, 2, lineasPackJuegos);

		ProductoVenta goku = tienda.buscarproductoPorNombre("Figura Goku SSJ").get(0);
		ProductoVenta link = tienda.buscarproductoPorNombre("Figura Link").get(0);

		ArrayList<LineaPack> lineasPackFiguras = new ArrayList<>();
		lineasPackFiguras.add(new LineaPack(goku, 1));
		lineasPackFiguras.add(new LineaPack(link, 1));

		emp.crearPack("Pack Figuras Coleccionista", "Pack de figuras para coleccionistas de anime y videojuegos",
				"pack_figuras.jpg", 68.00, 2, lineasPackFiguras);

		ProductoVenta vader = tienda.buscarproductoPorNombre("Figura Darth Vader").get(0);
		ProductoVenta spiderman = tienda.buscarproductoPorNombre("Figura Spider-Man").get(0);

		ArrayList<LineaPack> lineasPackAccion = new ArrayList<>();
		lineasPackAccion.add(new LineaPack(vader, 1));
		lineasPackAccion.add(new LineaPack(spiderman, 1));

		emp.crearPack("Pack Figuras Accion", "Pack de figuras de accion y ciencia ficcion", "pack_accion.jpg", 84.99, 2,
				lineasPackAccion);

		// Datos de prueba para pedidos, entregas, tasaciones, intercambios y
		// notificaciones

		Cliente ana = tienda.registrarNuevoCliente("ana", "Cliente@1234", "11111111A");
		Cliente mario = tienda.registrarNuevoCliente("mario", "Cliente@1234", "22222222B");
		Cliente laura = tienda.registrarNuevoCliente("laura", "Cliente@1234", "33333333C");

		ana.login("Cliente@1234");
		mario.login("Cliente@1234");
		laura.login("Cliente@1234");

		// PEDIDOS DE PRUEBA

		ProductoVenta prodWatchmen = tienda.buscarproductoPorNombre("Watchmen").get(0);
		ProductoVenta prodCatan = tienda.buscarproductoPorNombre("Catan").get(0);
		ProductoVenta prodPandemic = tienda.buscarproductoPorNombre("Pandemic").get(0);
		ProductoVenta prodDixit = tienda.buscarproductoPorNombre("Dixit").get(0);
		ProductoVenta prodMonopoly = tienda.buscarproductoPorNombre("Monopoly").get(0);
		ProductoVenta prodCluedo = tienda.buscarproductoPorNombre("Cluedo").get(0);

		// Pedido PAGADO: aparecerá en pedidos y se podrá preparar
		ana.añadirProductoCarrito(prodWatchmen, 1);
		ana.añadirProductoCarrito(prodCatan, 1);
		ana.reservarCarrito();

		Pedido pedidoPagado = ana.getHistorialPedidos().get(ana.getHistorialPedidos().size() - 1);
		pedidoPagado.setEstado(EstadoPedido.PAGADO);
		pedidoPagado.setCodigoRecogida("PICK-" + pedidoPagado.getIdPedido());

		// Pedido LISTO_PARA_RECOGER con recogida solicitada: se podrá entregar
		mario.añadirProductoCarrito(prodPandemic, 1);
		mario.reservarCarrito();

		Pedido pedidoParaEntregar = mario.getHistorialPedidos().get(mario.getHistorialPedidos().size() - 1);
		pedidoParaEntregar.setEstado(EstadoPedido.LISTO_PARA_RECOGER);
		pedidoParaEntregar.setCodigoRecogida("PICK-" + pedidoParaEntregar.getIdPedido());
		pedidoParaEntregar.setRecogida_solicitada(true);

		// Pedido LISTO_PARA_RECOGER sin recogida solicitada: se verá, pero no debería
		// entregarse
		laura.añadirProductoCarrito(prodDixit, 1);
		laura.reservarCarrito();

		Pedido pedidoListoSinSolicitud = laura.getHistorialPedidos().get(laura.getHistorialPedidos().size() - 1);
		pedidoListoSinSolicitud.setEstado(EstadoPedido.LISTO_PARA_RECOGER);
		pedidoListoSinSolicitud.setCodigoRecogida("PICK-" + pedidoListoSinSolicitud.getIdPedido());
		pedidoListoSinSolicitud.setRecogida_solicitada(false);

		// Pedido ENTREGADO: para que se vea un pedido ya finalizado
		ana.añadirProductoCarrito(prodMonopoly, 1);
		ana.reservarCarrito();

		Pedido pedidoEntregado = ana.getHistorialPedidos().get(ana.getHistorialPedidos().size() - 1);
		pedidoEntregado.setEstado(EstadoPedido.LISTO_PARA_RECOGER);
		pedidoEntregado.setCodigoRecogida("PICK-" + pedidoEntregado.getIdPedido());
		pedidoEntregado.setRecogida_solicitada(true);
		emp.entregarPedido(pedidoEntregado.getCodigoRecogida());

		// Pedido PENDIENTE_PAGO: para ver otro estado distinto en la tabla
		mario.añadirProductoCarrito(prodCluedo, 1);
		mario.reservarCarrito();

		// TASACIONES DE PRUEBA

		ana.subirProducto("Game Boy Color", "Consola retro pendiente de tasar", "gameboy_color.jpg");
		Producto2Mano gameBoy = ana.getCarteraIntercambio().get(ana.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(gameBoy);

		mario.subirProducto("Manga Naruto Vol.1", "Tomo antiguo pendiente de valorar", "naruto_vol1.jpg");
		Producto2Mano naruto = mario.getCarteraIntercambio().get(mario.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(naruto);

		// PRODUCTOS YA TASADOS PARA INTERCAMBIOS

		ana.subirProducto("Consola PSP", "PSP usada pero funcional", "psp.jpg");
		Producto2Mano psp = ana.getCarteraIntercambio().get(ana.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(psp);
		emp.tasarProducto(psp.getId(), 70.00, EstadoProducto.USO_LIGERO);

		ana.subirProducto("Cartas Pokemon antiguas", "Lote de cartas Pokemon de colección", "cartas_pokemon.jpg");
		Producto2Mano cartasPokemon = ana.getCarteraIntercambio().get(ana.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(cartasPokemon);
		emp.tasarProducto(cartasPokemon.getId(), 45.00, EstadoProducto.MUY_BUENO);

		mario.subirProducto("Nintendo GameCube", "Consola GameCube con mando", "gamecube.jpg");
		Producto2Mano gameCube = mario.getCarteraIntercambio().get(mario.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(gameCube);
		emp.tasarProducto(gameCube.getId(), 95.00, EstadoProducto.USO_EVIDENTE);

		mario.subirProducto("Figura One Piece", "Figura de Luffy de colección", "luffy.jpg");
		Producto2Mano figuraLuffy = mario.getCarteraIntercambio().get(mario.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(figuraLuffy);
		emp.tasarProducto(figuraLuffy.getId(), 30.00, EstadoProducto.PERFECTO);

		laura.subirProducto("Tomo Berserk Deluxe", "Edición deluxe en buen estado", "berserk_deluxe.jpg");
		Producto2Mano berserk = laura.getCarteraIntercambio().get(laura.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(berserk);
		emp.tasarProducto(berserk.getId(), 40.00, EstadoProducto.MUY_BUENO);

		// INTERCAMBIOS DE PRUEBA

		// Oferta aceptada por el cliente destino: debería aparecer lista para confirmar
		// por empleado
		boolean oferta1Ok = ana.proponerOferta(mario, ana.crearListaProductos2Mano(psp),
				mario.crearListaProductos2Mano(gameCube));

		if (oferta1Ok && !ana.getOfertasEnEspera().isEmpty()) {
			Oferta ofertaAceptada = ana.getOfertasEnEspera().get(0);
			mario.confirmarIntercambio(ofertaAceptada);
		}

		// Oferta pendiente: sirve para ver una oferta que todavía no está aceptada
		laura.proponerOferta(ana, laura.crearListaProductos2Mano(berserk), ana.crearListaProductos2Mano(cartasPokemon));

		// NOTIFICACIONES DE PRUEBA DEL EMPLEADO

		emp.recibirNotificacion("Datos de prueba cargados para pedidos, entregas, tasaciones e intercambios.");
		emp.recibirNotificacion("Hay un pedido pagado pendiente de preparar: " + pedidoPagado.getIdPedido());
		emp.recibirNotificacion(
				"Hay un pedido listo para entregar con código: " + pedidoParaEntregar.getCodigoRecogida());
		emp.recibirNotificacion("Hay productos pendientes de tasación para comprobar la sección de tasaciones.");
		emp.recibirNotificacion("Hay una oferta aceptada por clientes pendiente de confirmación por empleado.");

		ana.logout();
		mario.logout();
		laura.logout();
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