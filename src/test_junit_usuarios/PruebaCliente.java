package test_junit_usuarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import Excepcion.ProductoBloqueadoException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class PruebaCliente {

	private static Tienda tienda;
	private static Gestor gestor;
	private Cliente alice;
	private Cliente bob;
	private Cliente carlos;
	private ProductoVenta watchmen;
	@SuppressWarnings("unused")
	private ProductoVenta akira;
	private Empleado empTasador;
	private Empleado empPedidos;

	@BeforeEach
	void setUp() {

		tienda = Tienda.getInstancia();
		tienda.vaciarTienda();
		gestor = tienda.getGestor();
		gestor.configurarTiemposSistema(60, 30, 30);
		gestor.setPrecioTasacion(10.0);
		gestor.crearCategoria("Anime", "Anime y manga");
		gestor.crearCategoria("Familiar", "Para toda la familia");

		List<TipoPermisos> permisosTasador = gestor.crearListaPermisos(TipoPermisos.VALORACION_PRODUCTOS,
				TipoPermisos.CONFIRMACION_INTERCAMBIO);
		List<TipoPermisos> permisosPedidos = gestor.crearListaPermisos(TipoPermisos.GESTION_PEDIDOS,
				TipoPermisos.ENTREGA_PEDIDOS);
		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK);

		gestor.darDeAltaEmpleados_Permisos("tasador_test", "Tasador@1234", permisosTasador);
		gestor.darDeAltaEmpleados_Permisos("pedidos_test", "Pedidos@1234", permisosPedidos);
		gestor.darDeAltaEmpleados_Permisos("stock_test", "Stock@1234", permisosStock);

		// iniciar sesion empleados
		empTasador = tienda.loginEmpleado("tasador_test", "Tasador@1234");
		empPedidos = tienda.loginEmpleado("pedidos_test", "Pedidos@1234");
		Empleado empStock = tienda.loginEmpleado("stock_test", "Stock@1234");

		empStock.añadirProducto_nuevo("C", "Watchmen_test", "Comic test", "w.jpg", 15.0, 20,
				tienda.seleccionarCategorias("Anime"), 400, "DC", 1987, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		empStock.añadirProducto_nuevo("C", "Akira_test", "Manga test", "a.jpg", 12.99, 20,
				tienda.seleccionarCategorias("Anime"), 350, "Kodansha", 1982, 0, 0, 0, null, null, 0, 0, 0, 0, null);

		watchmen = tienda.buscarproductoPorNombre("Watchmen_test").get(0);
		akira = tienda.buscarproductoPorNombre("Akira_test").get(0);

		alice = tienda.registrarNuevoCliente("alice_test", "Alice@1234", "11111111A");
		bob = tienda.registrarNuevoCliente("bob_test", "Bob@1234", "22222222B");
		carlos = tienda.registrarNuevoCliente("carlos_test", "Carlos@123", "33333333C");

		alice = tienda.loginCliente("alice_test", "Alice@1234");
		bob = tienda.loginCliente("bob_test", "Bob@1234");
		carlos = tienda.loginCliente("carlos_test", "Carlos@123");
	}

	@Test
	@DisplayName("añadirProductoCarrito con producto valido devuelve true")
	void testAñadirProductoCarritoOk() {
		assertTrue(alice.añadirProductoCarrito(watchmen, 1));
		assertNotNull(alice.getCarritoActual());
	}

	@Test
	@DisplayName("añadirProductoCarrito con producto null devuelve false")
	void testAñadirProductoCarritoNull() {
		assertFalse(alice.añadirProductoCarrito(null, 1));
	}

	@Test
	@DisplayName("añadirProductoCarrito con cantidad 0 devuelve false")
	void testAñadirProductoCarritoCantidadCero() {
		assertFalse(alice.añadirProductoCarrito(watchmen, 0));
	}

	@Test
	@DisplayName("añadirProductoCarrito con cantidad negativa devuelve false")
	void testAñadirProductoCarritoCantidadNegativa() {
		assertFalse(alice.añadirProductoCarrito(watchmen, -1));
	}

	@Test
	@DisplayName("añadirProductoCarrito con mas unidades de las disponibles devuelve false")
	void testAñadirProductoCarritoStockInsuficiente() {
		assertFalse(alice.añadirProductoCarrito(watchmen, 9999));
	}

	@Test
	@DisplayName("reservarCarrito con carrito vacio devuelve false")
	void testReservarCarritoVacio() {
		assertFalse(alice.reservarCarrito());
	}

	@Test
	@DisplayName("reservarCarrito con productos devuelve true y crea pedido")
	void testReservarCarritoOk() {
		alice.añadirProductoCarrito(watchmen, 1);
		assertTrue(alice.reservarCarrito());
		assertFalse(alice.getHistorialPedidos().isEmpty());
		assertNull(alice.getCarritoActual());
	}

	@Test
	@DisplayName("pagarCarrito con pedido valido devuelve true")
	void testPagarCarritoOk() {
		alice.añadirProductoCarrito(watchmen, 1);
		alice.reservarCarrito();
		Pedido p = alice.getHistorialPedidos().get(0);
		assertTrue(alice.pagarCarrito(p, "1234567890123456", new Date(System.currentTimeMillis() + 100000000L), 123));
		assertEquals(EstadoPedido.PAGADO, p.getEstado());
	}

	@Test
	@DisplayName("pagarCarrito con tarjeta caducada devuelve false")
	void testPagarCarritoTarjetaCaducada() {
		alice.añadirProductoCarrito(watchmen, 1);
		alice.reservarCarrito();
		Pedido p = alice.getHistorialPedidos().get(0);
		assertFalse(alice.pagarCarrito(p, "1234567890123456", Date.valueOf("2020-01-01"), 123));
	}

	@Test
	@DisplayName("pagarCarrito con pedido ajeno devuelve false")
	void testPagarCarritoPedidoAjeno() {
		bob.añadirProductoCarrito(watchmen, 1);
		bob.reservarCarrito();
		Pedido p = bob.getHistorialPedidos().get(0);
		assertFalse(alice.pagarCarrito(p, "1234567890123456", new Date(System.currentTimeMillis() + 100000000L), 123));
	}

	@Test
	@DisplayName("modificarPerfil con datos validos devuelve true")
	void testModificarPerfilOk() {
		assertTrue(alice.modificarPerfil("alicia_test", "Alicia@5678"));
		assertEquals("alicia_test", alice.getNickname());
	}

	@Test
	@DisplayName("modificarPerfil con nickname ya existente devuelve false")
	void testModificarPerfilNicknameExistente() {
		assertFalse(alice.modificarPerfil("bob_test", "Bob@1234"));
	}

	@Test
	@DisplayName("modificarPerfil con nickname null devuelve false")
	void testModificarPerfilNicknameNull() {
		assertFalse(alice.modificarPerfil(null, "Alice@1234"));
	}

	@Test
	@DisplayName("modificarPerfil con contraseña insegura devuelve false")
	void testModificarPerfilPasswordInsegura() {
		assertFalse(alice.modificarPerfil("alice_test", "hola"));
	}

	@Test
	@DisplayName("escribirReseña sin haber comprado el producto devuelve false")
	void testEscribirReseñaSinComprar() {
		assertFalse(alice.escribirReseña(watchmen, 9, "Muy bueno"));
	}

	@Test
	@DisplayName("escribirReseña tras haber comprado y recibido el producto devuelve true")
	void testEscribirReseñaOk() {
		alice.añadirProductoCarrito(watchmen, 1);
		alice.reservarCarrito();
		Pedido p = alice.getHistorialPedidos().get(0);
		alice.pagarCarrito(p, "1234567890123456", new Date(System.currentTimeMillis() + 100000000L), 123);
		empPedidos.prepararPedido(p.getIdPedido());
		alice.solicitarRecogidaPedido(p.getCodigoRecogida());
		empPedidos.entregarPedido(p.getCodigoRecogida());
		assertTrue(alice.escribirReseña(watchmen, 9, "Muy bueno"));
	}

	@Test
	@DisplayName("subirProducto añade producto a la cartera")
	void testSubirProducto() {
		alice.subirProducto("Naruto", "Buen estado", "img.jpg");
		assertEquals(1, alice.getCarteraIntercambio().size());
	}

	@Test
	@DisplayName("tieneProductoenSuCartera devuelve true si el producto es suyo")
	void testTieneProductoEnSuCartera() {
		alice.subirProducto("Naruto", "Buen estado", "img.jpg");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		assertTrue(alice.tieneProductoenSuCartera(p));
		assertFalse(bob.tieneProductoenSuCartera(p));
	}

	@Test
	@DisplayName("tieneProductoenSuCartera con null devuelve false")
	void testTieneProductoEnSuCarteraNull() {
		assertFalse(alice.tieneProductoenSuCartera(null));
	}

	@Test
	@DisplayName("solicitarTasacion con producto null devuelve false")
	void testSolicitarTasacionNull() {
		assertFalse(alice.solicitarTasacion(null, "1111222233334444", 111, Date.valueOf("2029-01-01")));
	}

	@Test
	@DisplayName("solicitarTasacion con producto ajeno devuelve false")
	void testSolicitarTasacionProductoAjeno() {
		bob.subirProducto("Comic", "Buen estado", "img.jpg");
		Producto2Mano p = bob.getCarteraIntercambio().get(0);
		assertFalse(alice.solicitarTasacion(p, "1111222233334444", 111, Date.valueOf("2029-01-01")));
	}

	@Test
	@DisplayName("solicitarTasacion con tarjeta caducada devuelve false")
	void testSolicitarTasacionTarjetaCaducada() {
		alice.subirProducto("Naruto", "Buen estado", "img.jpg");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		assertFalse(alice.solicitarTasacion(p, "1111222233334444", 111, Date.valueOf("2020-01-01")));
	}

	@Test
	@DisplayName("proponerOferta con datos validos devuelve true")
	void testProponerOfertaOk() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);

		assertTrue(alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb)));
		assertTrue(pa.isBloqueado());
	}

	@Test
	@DisplayName("proponerOferta a si mismo devuelve false")
	void testProponerOfertaASiMismo() {
		alice.subirProducto("A", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		assertFalse(
				alice.proponerOferta(alice, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pa)));
	}

	@Test
	@DisplayName("proponerOferta con destinatario null devuelve false")
	void testProponerOfertaDestinatarioNull() {
		alice.subirProducto("A", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		assertFalse(alice.proponerOferta(null, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pa)));
	}

	@Test
	@DisplayName("proponerOferta con lista vacia devuelve false")
	void testProponerOfertaListaVacia() {
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		assertFalse(alice.proponerOferta(bob, alice.crearListaProductos2Mano(), alice.crearListaProductos2Mano(pb)));
	}

	@Test
	@DisplayName("eliminarOfertadeOfertasPendientes con null devuelve false")
	void testEliminarOfertaNull() {
		assertFalse(alice.eliminarOfertadeOfertasPendientes(null));
	}

	@Test
	@DisplayName("verIntercambiosCon null devuelve null")
	void testVerIntercambiosConNull() {
		assertNull(alice.verIntercambioscon(null));
	}

	@Test
	@DisplayName("verIntercambiosCon cliente sin intercambios devuelve lista vacia")
	void testVerIntercambiosConSinIntercambios() {
		assertTrue(alice.verIntercambioscon(bob).isEmpty());
	}

	@Test
	@DisplayName("configurarPreferenciaNotificacion con tipo null devuelve false")
	void testConfigurarPreferenciaNull() {
		assertFalse(alice.configurarPreferenciaNotificacion(null, false));
	}

	@Test
	@DisplayName("configurarPreferenciaNotificacion con tipo opcional devuelve true")
	void testConfigurarPreferenciaOk() {
		assertTrue(alice.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, false));
	}

	@Test
	@DisplayName("añadirCategoriaInteresParaRecibirInfo con categoria existente devuelve true")
	void testAñadirCategoriaInteresOk() {
		assertTrue(alice.añadirCategoriaInteresParaRecibirInfo("Anime"));
	}

	@Test
	@DisplayName("añadirCategoriaInteresParaRecibirInfo con categoria inexistente devuelve false")
	void testAñadirCategoriaInteresNoExiste() {
		assertFalse(alice.añadirCategoriaInteresParaRecibirInfo("CategoriaFalsa"));
	}

	@Test
	@DisplayName("añadirCategoriaInteresParaRecibirInfo con null devuelve false")
	void testAñadirCategoriaInteresNull() {
		assertFalse(alice.añadirCategoriaInteresParaRecibirInfo(null));
	}

	@Test
	@DisplayName("eliminarCategoriaInteres existente devuelve true")
	void testEliminarCategoriaInteresOk() {
		alice.añadirCategoriaInteresParaRecibirInfo("Anime");
		assertTrue(alice.eliminarCategoriaInteres("Anime"));
	}

	@Test
	@DisplayName("eliminarCategoriaInteres no añadida devuelve false")
	void testEliminarCategoriaInteresNoExiste() {
		assertFalse(alice.eliminarCategoriaInteres("Anime"));
	}

	@Test
	@DisplayName("getNotificacionesNoLeidas devuelve solo las no leidas")
	void testGetNotificacionesNoLeidas() {
		alice.recibirNotificacionTipo("test", TipoNotificacion.DESCUENTO);
		assertFalse(alice.getNotificacionesNoLeidas().isEmpty());
	}

	@Test
	@DisplayName("eliminarNotificacion con null devuelve false")
	void testEliminarNotificacionNull() {
		assertFalse(alice.eliminarNotifacion(null));
	}

	@Test
	@DisplayName("eliminarNotificacion propia devuelve true")
	void testEliminarNotificacionOk() {
		alice.recibirNotificacionTipo("test", TipoNotificacion.DESCUENTO);
		Notificacion n = alice.getNotificaciones().get(0);
		assertTrue(alice.eliminarNotifacion(n));
	}

	@Test
	@DisplayName("contarPedidosCompletados sin pedidos devuelve 0")
	void testContarPedidosCompletadosVacio() {
		assertEquals(0, alice.contarPedidosCompletados());
	}

	@Test
	@DisplayName("contarPedidosCancelados sin pedidos devuelve 0")
	void testContarPedidosCanceladosVacio() {
		assertEquals(0, alice.contarPedidosCancelados());
	}

	@Test
	@DisplayName("contarIntercambios sin intercambios devuelve 0")
	void testContarIntercambiosVacio() {
		assertEquals(0, alice.contarIntercambios());
	}

	@Test
	@DisplayName("productoHasidoPedidoYentregado con null devuelve false")
	void testProductoHasidoPedidoYentregadoNull() {
		assertFalse(alice.productoHasidoPedidoYentregado(null));
	}

	@Test
	@DisplayName("productoHasidoPedidoYentregado sin haberlo comprado devuelve false")
	void testProductoHasidoPedidoYentregadoSinComprar() {
		assertFalse(alice.productoHasidoPedidoYentregado(watchmen));
	}

	@Test
	@DisplayName("determinarCategoriaFavorita sin pedidos devuelve null")
	void testDeterminarCategoriaFavoritaSinPedidos() {
		assertNull(alice.determinarCategoriaFavorita());
	}

	@Test
	@DisplayName("getOfertasParaDecidir identifica correctamente al destinatario")
	void testOfertasParaDecidir() {

		alice.getCarteraIntercambio().clear();
		bob.getCarteraIntercambio().clear();
		alice.getOfertasPendientes().clear();
		bob.getOfertasPendientes().clear();

		alice.subirProducto("Comic A", "Desc", "img.jpg");
		bob.subirProducto("Comic B", "Desc", "img.jpg");

		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);

		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);

		pa.setVisible(true);
		pb.setVisible(true);
		pa.setBloqueado(false);
		pb.setBloqueado(false);

		List<Producto2Mano> misProd = new ArrayList<>();
		misProd.add(pa);
		List<Producto2Mano> susProd = new ArrayList<>();
		susProd.add(pb);

		boolean enviado = alice.proponerOferta(bob, misProd, susProd);

		assertTrue(enviado, "La oferta NO se envió. Revisa si proponerOferta devuelve false.");

		assertEquals(1, bob.getOfertasParaDecidir().size(),
				"Bob debería tener 1 oferta por decidir (él es el destino)");
		assertEquals(1, alice.getOfertasEnEspera().size(),
				"Alice debería tener 1 oferta en espera (ella es la origen)");
	}

	@Test
	@DisplayName("No se puede proponer oferta con producto bloqueado")
	void testProductoBloqueadoEnOferta() {

		alice.subirProducto("Comic Alice", "Muy Bueno", "imgA.jpg");
		bob.subirProducto("Comic Bob", "Muy Bueno", "imgB.jpg");

		Producto2Mano pa = alice.getCarteraIntercambio().get(alice.getCarteraIntercambio().size() - 1);
		Producto2Mano pb = bob.getCarteraIntercambio().get(bob.getCarteraIntercambio().size() - 1);

		pa.valorar(15.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(15.0, EstadoProducto.MUY_BUENO, empTasador);
		pa.setVisible(true);
		pb.setVisible(true);

		boolean exito = alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), bob.crearListaProductos2Mano(pb));

		assertTrue(exito, "La primera oferta debe enviarse porque el producto estaba libre.");
		assertTrue(pa.isBloqueado(), "Ahora el producto DEBE estar bloqueado por la oferta pendiente.");

		assertThrows(ProductoBloqueadoException.class, () -> {
			alice.proponerOferta(carlos, alice.crearListaProductos2Mano(pa), bob.crearListaProductos2Mano(pb));
		});
	}

	void testPreferenciasNotificaciones() {
		alice.getNotificaciones().clear(); // Limpiamos la bienvenida
		alice.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, false);
		alice.recibirNotificacionTipo("Oferta 50%", TipoNotificacion.DESCUENTO);

		assertTrue(alice.getNotificaciones().isEmpty());
	}

	@Test
	void testMetodosVisualizacion() {
		alice.subirProducto("Test", "Desc", "img.jpg");
		alice.recibirNotificacionTipo("Hola", TipoNotificacion.DESCUENTO);

		alice.verMisNotificaciones();
		alice.verMisNotificacionesPorTipo(TipoNotificacion.DESCUENTO);
		alice.verMiCartera();
		alice.verHistorialPedidos();
		alice.verMiHistorialIntercambios();
		alice.verMisOfertasEnviadas();
		alice.verMisOfertasPorResponder();
		alice.verMisPreferencias();

		assertNotNull(alice.getNickname());
	}

	@Test
	@DisplayName(" Metodo verMiHistorialIntercambios %")
	void testVerMiHistorialIntercambiosCompleto() {

		alice.subirProducto("Naruto", "Buen estado", "n.jpg");
		bob.subirProducto("Akira", "Manga", "a.jpg");

		Producto2Mano pAlice = alice.getCarteraIntercambio().get(0);
		Producto2Mano pBob = bob.getCarteraIntercambio().get(0);

		Tienda.getInstancia().solicitarTasacion(pAlice);
		Tienda.getInstancia().solicitarTasacion(pBob);
		empTasador.tasarProducto(pAlice.getId(), 10.0, EstadoProducto.MUY_BUENO);
		empTasador.tasarProducto(pBob.getId(), 10.0, EstadoProducto.MUY_BUENO);

		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pAlice), alice.crearListaProductos2Mano(pBob));

		Oferta oferta = bob.getOfertasParaDecidir().get(0);

		bob.confirmarIntercambio(oferta);

		if (alice.getHistorialIntercambios().isEmpty()) {
			alice.getHistorialIntercambios().add(oferta);
		}

		alice.verMiHistorialIntercambios();
		bob.verMiHistorialIntercambios();

		assertFalse(alice.getHistorialIntercambios().isEmpty(), "El historial debería tener 1 oferta");
	}

	@Test
	@DisplayName("determinarCategoriaFavorita con compras reales")
	void testDeterminarCategoriaFavoritaCompleto() {

		alice.añadirProductoCarrito(watchmen, 1);

		assertTrue(alice.reservarCarrito(), "El carrito debería reservarse");

		// Recuperamos el pedido recién creado
		Pedido pedidoAlice = alice.getHistorialPedidos().get(0);

		Date fechaFutura = new Date(System.currentTimeMillis() + 100000000L);
		boolean pagado = alice.pagarCarrito(pedidoAlice, "1234567890123456", fechaFutura, 123);
		assertTrue(pagado, "El pedido debe estar pagado para contar en la lógica");

		Categoria favorita = alice.determinarCategoriaFavorita();

		assertNotNull(favorita, "La categoría favorita no debería ser null tras una compra");
		assertEquals("Anime", favorita.getNombre(), "La favorita debería ser Anime");
		assertNull(carlos.determinarCategoriaFavorita(), "Un cliente sin pedidos debe devolver null");
	}

	@Test
	@DisplayName("eliminarOfertadeOfertasPendientes con oferta no en pendientes devuelve false")
	void testEliminarOfertaNoEnPendientes() {
		// Creamos una oferta pero no la añadimos a pendientes
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		Oferta oferta = new Oferta(alice, bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb));
		// No la añadimos a pendientes
		assertFalse(alice.eliminarOfertadeOfertasPendientes(oferta));
	}

	@Test
	@DisplayName("eliminarOfertadeOfertasPendientes con oferta ya aceptada lanza excepcion y devuelve false")
	void testEliminarOfertaYaAceptada() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb));
		Oferta oferta = alice.getOfertasPendientes().get(0);
		// Bob acepta — la oferta ya no está PENDIENTE
		bob.confirmarIntercambio(oferta);
		// Alice intenta eliminarla — rechazar() lanzará OfertaNoDisponibleException
		assertFalse(alice.eliminarOfertadeOfertasPendientes(oferta));
	}

	@Test
	@DisplayName("eliminarOfertadeOfertasPendientes con oferta valida devuelve true y desbloquea productos")
	void testEliminarOfertaOk() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb));
		Oferta oferta = alice.getOfertasPendientes().get(0);
		assertTrue(alice.eliminarOfertadeOfertasPendientes(oferta));
		assertEquals(EstadoOferta.RECHAZADA, oferta.getEstado());
		assertFalse(pa.isBloqueado());
		assertTrue(alice.getOfertasPendientes().isEmpty());
		assertTrue(bob.getOfertasPendientes().isEmpty());
	}

	@Test
	@DisplayName("verHistorialPedidos sin pedidos no lanza excepcion")
	void testVerHistorialPedidosVacio() {
		assertDoesNotThrow(() -> alice.verHistorialPedidos());
	}

	@Test
	@DisplayName("verHistorialPedidos con pedidos no lanza excepcion")
	void testVerHistorialPedidosConPedidos() {
		alice.añadirProductoCarrito(watchmen, 1);
		alice.reservarCarrito();
		assertDoesNotThrow(() -> alice.verHistorialPedidos());
	}

	@Test
	@DisplayName("verMiCartera sin productos no lanza excepcion")
	void testVerMiCarteraVacia() {
		assertDoesNotThrow(() -> alice.verMiCartera());
	}

	@Test
	@DisplayName("verMiCartera con producto sin tasar no lanza excepcion")
	void testVerMiCarteraProductoSinTasar() {
		alice.subirProducto("Naruto", "Buen estado", "img.jpg");
		assertDoesNotThrow(() -> alice.verMiCartera());
	}

	@Test
	@DisplayName("verMiCartera con producto tasado no lanza excepcion")
	void testVerMiCarteraProductoTasado() {
		alice.subirProducto("Naruto", "Buen estado", "img.jpg");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(p);
		p.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		assertDoesNotThrow(() -> alice.verMiCartera());
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo sin notificaciones de ese tipo no lanza excepcion")
	void testVerNotificacionesPorTipoSin() {
		assertDoesNotThrow(() -> alice.verMisNotificacionesPorTipo(TipoNotificacion.DESCUENTO));
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo con notificaciones no leidas no lanza excepcion")
	void testVerNotificacionesPorTipoNoLeidas() {
		alice.recibirNotificacionTipo("desc", TipoNotificacion.DESCUENTO);
		assertDoesNotThrow(() -> alice.verMisNotificacionesPorTipo(TipoNotificacion.DESCUENTO));
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo con notificaciones leidas no lanza excepcion")
	void testVerNotificacionesPorTipoLeidas() {
		alice.recibirNotificacionTipo("desc", TipoNotificacion.DESCUENTO);
		alice.getNotificaciones().get(0).marcarComoLeida();
		assertDoesNotThrow(() -> alice.verMisNotificacionesPorTipo(TipoNotificacion.DESCUENTO));
	}

	@Test
	@DisplayName("verMisNotificaciones sin notificaciones no lanza excepcion")
	void testVerMisNotificacionesVacio() {
		assertDoesNotThrow(() -> alice.verMisNotificaciones());
	}

	@Test
	@DisplayName("verMisNotificaciones con notificaciones no leidas no lanza excepcion")
	void testVerMisNotificacionesNoLeidas() {
		alice.recibirNotificacionTipo("test", TipoNotificacion.DESCUENTO);
		assertDoesNotThrow(() -> alice.verMisNotificaciones());
	}

	@Test
	@DisplayName("verMisNotificaciones con notificaciones leidas no lanza excepcion")
	void testVerMisNotificacionesLeidas() {
		alice.recibirNotificacionTipo("test", TipoNotificacion.DESCUENTO);
		alice.getNotificaciones().get(0).marcarComoLeida();
		assertDoesNotThrow(() -> alice.verMisNotificaciones());
	}

	@Test
	@DisplayName("notificarProductoNuevoCategoria sin categoria de interes no añade notificacion")
	void testNotificarCategoriaSinInteres() {
		int antes = alice.getNotificaciones().size();
		alice.notificarProductoNuevoCategoria("test", "Anime");
		assertEquals(antes, alice.getNotificaciones().size());
	}

	@Test
	@DisplayName("notificarProductoNuevoCategoria con categoria de interes añade notificacion")
	void testNotificarCategoriaConInteres() {
		alice.añadirCategoriaInteresParaRecibirInfo("Anime");
		int antes = alice.getNotificaciones().size();
		alice.notificarProductoNuevoCategoria("test", "Anime");
		assertEquals(antes + 1, alice.getNotificaciones().size());
	}

	@Test
	@DisplayName("getNotificacionesdeTipo sin notificaciones de ese tipo devuelve lista vacia")
	void testGetNotificacionesdeTipoVacio() {
		assertTrue(alice.getNotificacionesdeTipo(TipoNotificacion.DESCUENTO).isEmpty());
	}

	@Test
	@DisplayName("getNotificacionesdeTipo con notificaciones devuelve solo las de ese tipo")
	void testGetNotificacionesdeTipoOk() {
		alice.recibirNotificacionTipo("desc", TipoNotificacion.DESCUENTO);
		alice.recibirNotificacionTipo("pago", TipoNotificacion.PAGO_EXITOSO);
		List<Notificacion> resultado = alice.getNotificacionesdeTipo(TipoNotificacion.DESCUENTO);
		assertEquals(1, resultado.size());
		assertEquals(TipoNotificacion.DESCUENTO, resultado.get(0).getTipo());
	}

	@Test
	@DisplayName("proponerOferta con producto no en cartera propia devuelve false")
	void testProponerOfertaProductoNoEnCartera() {
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pb);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pb);
		// alice intenta ofertar un producto de bob como si fuera suyo
		assertFalse(alice.proponerOferta(bob, alice.crearListaProductos2Mano(pb), alice.crearListaProductos2Mano(pb)));
	}

	@Test
	@DisplayName("proponerOferta con producto del destinatario no en su cartera devuelve false")
	void testProponerOfertaProductoDestinatarioNoEnCartera() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		// carlos intenta pedir un producto de bob que no es suyo
		assertFalse(
				alice.proponerOferta(carlos, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb)));
	}

	@DisplayName("verIntercambioscon con intercambio realizado devuelve la oferta")
	void testVerIntercambiosConOk() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb));
		Oferta oferta = alice.getOfertasPendientes().get(0);
		bob.confirmarIntercambio(oferta);
		empTasador.confirmarIntercambio(oferta);
		List<Oferta> resultado = alice.verIntercambioscon(bob);
		assertTrue(resultado.contains(oferta));
	}

	@Test
	@DisplayName("solicitarTasacion con producto ya pendiente devuelve false")
	void testSolicitarTasacionYaPendiente() {
		alice.subirProducto("A", "desc", "img.jpg");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		alice.solicitarTasacion(p, "1111222233334444", 111, Date.valueOf("2029-01-01"));
		assertFalse(alice.solicitarTasacion(p, "1111222233334444", 111, Date.valueOf("2029-01-01")));
	}

	@Test
	@DisplayName("solicitarTasacion con producto ya visible devuelve false")
	void testSolicitarTasacionYaVisible() {
		alice.subirProducto("A", "desc", "img.jpg");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(p);
		p.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(p);
		assertFalse(alice.solicitarTasacion(p, "1111222233334444", 111, Date.valueOf("2029-01-01")));
	}

	@Test
	@DisplayName("verMisOfertasEnviadas sin ofertas no lanza excepcion")
	void testVerMisOfertasEnviadasVacio() {
		assertDoesNotThrow(() -> alice.verMisOfertasEnviadas());
	}

	@Test
	@DisplayName("verMisOfertasEnviadas con ofertas no lanza excepcion")
	void testVerMisOfertasEnviadasConOfertas() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb));
		assertDoesNotThrow(() -> alice.verMisOfertasEnviadas());
	}

	@Test
	@DisplayName("verMisOfertasPorResponder sin ofertas no lanza excepcion")
	void testVerMisOfertasPorResponderVacio() {
		assertDoesNotThrow(() -> alice.verMisOfertasPorResponder());
	}

	@Test
	@DisplayName("verMisOfertasPorResponder con ofertas no lanza excepcion")
	void testVerMisOfertasPorResponderConOfertas() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb));
		assertDoesNotThrow(() -> bob.verMisOfertasPorResponder());
	}

	@Test
	@DisplayName("contarPedidosCancelados sin pedidos cancelados devuelve 0")
	void testContarPedidosCanceladosCero() {
		assertEquals(0, alice.contarPedidosCancelados());
	}

	@Test
	@DisplayName("contarPedidosCancelados con pedido cancelado devuelve 1")
	void testContarPedidosCanceladosUno() {
		alice.añadirProductoCarrito(watchmen, 1);
		alice.reservarCarrito();
		Pedido p = alice.getHistorialPedidos().get(alice.getHistorialPedidos().size() - 1);
		p.cancelarPedido();
		assertEquals(1, alice.contarPedidosCancelados());
	}

	@Test
	@DisplayName("verNotificacion con null no lanza excepcion")
	void testVerNotificacionNull() {
		assertDoesNotThrow(() -> alice.verNotificacion(null));
	}

	@Test
	@DisplayName("verNotificacion ajena no lanza excepcion")
	void testVerNotificacionAjena() {
		bob.recibirNotificacionTipo("test", TipoNotificacion.DESCUENTO);
		Notificacion n = bob.getNotificaciones().get(0);
		assertDoesNotThrow(() -> alice.verNotificacion(n));
	}

	@Test
	@DisplayName("verNotificacion propia la marca como leida")
	void testVerNotificacionPropia() {
		alice.recibirNotificacionTipo("test", TipoNotificacion.DESCUENTO);
		Notificacion n = alice.getNotificaciones().get(0);
		assertFalse(n.isLeida());
		alice.verNotificacion(n);
		assertTrue(n.isLeida());
	}

	@Test
	@DisplayName("eliminarProductodeCategoria con producto en cartera devuelve true")
	void testEliminarProductodeCategoriaOk() {
		alice.subirProducto("A", "desc", "img.jpg");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		assertTrue(alice.eliminarProductodeCategoria(p));
		assertFalse(alice.getCarteraIntercambio().contains(p));
	}

	@Test
	@DisplayName("eliminarProductodeCategoria con producto no en cartera devuelve false")
	void testEliminarProductodeCategoriaNoEnCartera() {
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano p = bob.getCarteraIntercambio().get(0);
		assertFalse(alice.eliminarProductodeCategoria(p));
	}

	@Test
	@DisplayName("pagarCarrito con pedido ya pagado devuelve false")
	void testPagarCarritoYaPagado() {
		alice.añadirProductoCarrito(watchmen, 1);
		alice.reservarCarrito();
		Pedido p = alice.getHistorialPedidos().get(alice.getHistorialPedidos().size() - 1);
		alice.pagarCarrito(p, "1234567890123456", new Date(System.currentTimeMillis() + 100000000L), 123);
		assertFalse(alice.pagarCarrito(p, "1234567890123456", new Date(System.currentTimeMillis() + 100000000L), 123));
	}

	@Test
	@DisplayName("getOfertasEnEspera sin ofertas enviadas devuelve lista vacia")
	void testGetOfertasEnEsperaVacio() {
		assertTrue(alice.getOfertasEnEspera().isEmpty());
	}

	@Test
	@DisplayName("getOfertasEnEspera con oferta enviada devuelve la oferta")
	void testGetOfertasEnEsperaConOferta() {
		alice.subirProducto("A", "desc", "img.jpg");
		bob.subirProducto("B", "desc", "img.jpg");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		pa.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		pb.valorar(10.0, EstadoProducto.MUY_BUENO, empTasador);
		tienda.publicarParaIntercambio(pa);
		tienda.publicarParaIntercambio(pb);
		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pa), alice.crearListaProductos2Mano(pb));
		assertEquals(1, alice.getOfertasEnEspera().size());
		assertTrue(bob.getOfertasEnEspera().isEmpty());
	}

	@Test
	@DisplayName("pagarCarrito con pedido null devuelve false")
	void testPagarCarritoNull() {
		assertFalse(
				alice.pagarCarrito(null, "1234567890123456", new Date(System.currentTimeMillis() + 100000000L), 123));
	}
}