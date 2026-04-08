package test_junit_tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import excepciones.ProductoInvalidoException;
import excepciones.ProductoYaEnPackException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import intercambios.Oferta;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TiendaTest {

	private static Tienda tienda;
	private Gestor gestor;
	private ProductoVenta watchmen;
	private ProductoVenta akira;
	private ProductoVenta figura;
	// Atributos para los clientes necesarios en Carrito
	private Cliente clienteA;
	private Cliente clienteB;

	@BeforeEach
	void setUp() {
		tienda = Tienda.getInstancia();
		tienda.vaciarTienda();
		gestor = tienda.getGestor();
		gestor.configurarTiemposSistema(60, 30, 30);
		gestor.setPrecioTasacion(10.0);
		gestor.crearCategoria("Anime_ges", "Anime y manga");
		gestor.crearCategoria("Familiar_ges", "Para toda la familia");

		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK);
		gestor.darDeAltaEmpleados_Permisos("emp_ges_test", "Stock@1234", permisosStock);
		Empleado emp = tienda.loginEmpleado("emp_ges_test", "Stock@1234");

		// Registro de clientes para poder crear carritos
		clienteA = tienda.registrarNuevoCliente("alice", "Alice@1234", "11111111A");
		clienteB = tienda.registrarNuevoCliente("bob", "Bob@1234", "22222222B");

		emp.añadirProducto_nuevo("C", "Watchmen_ges", "Comic test", "w.jpg", 15.0, 100,
				tienda.seleccionarCategorias("Anime_ges"), 400, "DC", 1987, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		emp.añadirProducto_nuevo("C", "Akira_ges", "Manga test", "a.jpg", 12.99, 100,
				tienda.seleccionarCategorias("Anime_ges"), 350, "Kodansha", 1982, 0, 0, 0, null, null, 0, 0, 0, 0,
				null);
		emp.añadirProducto_nuevo("F", "Figura_ges", "Figura test", "f.jpg", 39.99, 100,
				tienda.seleccionarCategorias("Anime_ges"), 0, null, 0, 20.0, 10.0, 8.0, "PVC", "Bandai", 0, 0, 0, 0,
				null);

		watchmen = tienda.buscarproductoPorNombre("Watchmen_ges").get(0);
		akira = tienda.buscarproductoPorNombre("Akira_ges").get(0);
		figura = tienda.buscarproductoPorNombre("Figura_ges").get(0);
	}

	@Test
	@Order(1)
	@DisplayName("buscarCLientePorId: Cobertura de nulos, vacíos y búsqueda real")
	void testBuscarClientePorIdCompleto() {
		assertNull(tienda.buscarCLientePorId(null));
		assertNull(tienda.buscarCLientePorId("   "));
		assertNull(tienda.buscarCLientePorId("USR-9999"));

		Cliente nuevo = tienda.registrarNuevoCliente("id_tester", "Pass@1234", "11112222K");
		assertNotNull(nuevo);
		assertEquals(nuevo, tienda.buscarCLientePorId(nuevo.getId()));

		assertNull(tienda.buscarCLientePorId(gestor.getId()), "No debe devolver al gestor aunque el ID coincida");
	}

	@Test
	@Order(3)
	@DisplayName("setInstancia: Cobertura del método estático")
	void testSetInstanciaEstatico() {
		Tienda original = Tienda.getInstancia();
		Tienda.setInstancia(original);
		assertSame(original, Tienda.getInstancia());

		Tienda.setInstancia(null);
		Tienda nueva = Tienda.getInstancia();
		assertNotNull(nueva);
		assertNotSame(original, nueva);

		Tienda.setInstancia(original);
	}

	@Test
	@Order(4)
	@DisplayName("Setters: Cobertura de listas de usuarios y sesiones")
	void testSettersUsuarios() {
		List<UsuarioRegistrado> nuevaListaUsu = new ArrayList<>();
		nuevaListaUsu.add(gestor);
		tienda.setUsuarios(nuevaListaUsu);
		assertSame(nuevaListaUsu, tienda.getUsuarios());

		List<UsuarioRegistrado> nuevaListaSesion = new ArrayList<>();
		nuevaListaSesion.add(gestor);
		tienda.setUsuariosConSesionActiva(nuevaListaSesion);
		assertSame(nuevaListaSesion, tienda.getUsuariosConSesionActiva());
	}

	@Test
	@Order(5)
	@DisplayName("Setters: Cobertura de stock y recomendador")
	void testSettersStockYRecomendador() {
		List<ProductoVenta> nuevoStock = new ArrayList<>();
		tienda.setStockVentas(nuevoStock);
		assertSame(nuevoStock, tienda.getStockVentas());

		Recomendador nuevoRec = new Recomendador();
		tienda.setRecomendador(nuevoRec);
		assertSame(nuevoRec, tienda.getRecomendador());
	}

	@Test
	@Order(6)
	@DisplayName("Setters: Cobertura de productos de segunda mano y tasación")
	void testSettersSegundaMano() {
		List<Producto2Mano> listaPendientes = new ArrayList<>();
		tienda.setPendientes_Tasacion(listaPendientes);
		assertSame(listaPendientes, tienda.getPendientesTasacion());

		List<Producto2Mano> listaHistorial2M = new ArrayList<>();
		tienda.setHistorialProductos2Mano(listaHistorial2M);
		assertSame(listaHistorial2M, tienda.getHistorialProductos2Mano());
	}

	@Test
	@Order(7)
	@DisplayName("Setters: Cobertura de nombre e historiales de ventas/intercambios")
	void testSettersNombreEHistoriales() {
		tienda.setNombre("Tienda Test 100");
		assertEquals("Tienda Test 100", tienda.getNombre());

		List<Oferta> listaIntercambios = new ArrayList<>();
		tienda.setIntercambiosFinalizados(listaIntercambios);
		assertSame(listaIntercambios, tienda.getIntercambiosFinalizados());

		List<Pedido> listaVentas = new ArrayList<>();
		tienda.setHistorialVentas(listaVentas);
		assertSame(listaVentas, tienda.getHistorialVentas());
	}

	@Test
	@Order(8)
	@DisplayName("reiniciarComprobadorTiempos: Verifica creación de nueva instancia")
	void testReiniciarComprobadorTiempos() {
		ComprobadorTiempos original = tienda.getComprobadorTiempos();
		assertNotNull(original);
		tienda.reiniciarComprobadorTiempos();
		ComprobadorTiempos nuevo = tienda.getComprobadorTiempos();
		assertNotNull(nuevo);
		assertNotSame(original, nuevo);
	}

	@Test
	@Order(9)
	@DisplayName("loginGestor: Cobertura de éxito y fallos")
	void testLoginGestorCompleto() {
		Gestor logueado = tienda.loginGestor("admin_Gestor", "Admin@1234");
		assertNotNull(logueado);
		assertSame(gestor, logueado);
		assertNull(tienda.loginGestor("admin_Gestor", "password_incorrecta"));
		assertNull(tienda.loginGestor("usuario_comun", "Pass@1234"));
	}

	@Test
	@Order(10)
	@DisplayName("DescuentoVolumen: Cobertura con constructor de Cliente")
	void testDescuentoVolumenCorrecto() {
		LocalDateTime inicio = LocalDateTime.now().minusDays(1);
		LocalDateTime fin = LocalDateTime.now().plusDays(1);
		DescuentoVolumen desc = new DescuentoVolumen("Promo Vol", inicio, fin, 100.0, 10.0);

		Carrito carrito = new Carrito(clienteA);
		carrito.añadirProducto(watchmen, 2); // 15 * 2 = 30
		assertEquals(30.0, desc.aplicarDescuento(carrito));

		Carrito carritoCaro = new Carrito(clienteA);
		carritoCaro.añadirProducto(watchmen, 10); // 150
		assertEquals(135.0, desc.aplicarDescuento(carritoCaro));
	}

	@Test
	@Order(11)
	@DisplayName("DescuentoCantidad: Cobertura con constructor de Cliente")
	void testDescuentoCantidadCorrecto() {
		LocalDateTime inicio = LocalDateTime.now().minusDays(1);
		LocalDateTime fin = LocalDateTime.now().plusDays(1);
		DescuentoCantidad desc = new DescuentoCantidad("Promo Cant", inicio, fin, 5, 20.0);

		Carrito carrito = new Carrito(clienteB);
		carrito.añadirProducto(watchmen, 5); // (15 * 5) * 0.8 = 60
		carrito.añadirProducto(akira, 1); // 12.99
		assertEquals(72.99, desc.aplicarDescuento(carrito), 0.01);
	}

	@Test
	@Order(12)
	@DisplayName("DescuentoCategoria: Cobertura con constructor de Cliente")
	void testDescuentoCategoriaCorrecto() {
		Categoria catAnime = tienda.buscarCategoriaPorNombre("Anime_ges");
		LocalDateTime inicio = LocalDateTime.now().minusDays(1);
		LocalDateTime fin = LocalDateTime.now().plusDays(1);
		DescuentoCategoria desc = new DescuentoCategoria("Promo Anime", inicio, fin, catAnime, 50.0);

		Carrito carrito = new Carrito(clienteA);
		carrito.añadirProducto(watchmen, 1); // 15 * 0.5 = 7.5
		assertEquals(7.5, desc.aplicarDescuento(carrito));
	}

	@Test
	@Order(13)
	@DisplayName("Regalo: Cobertura con constructor de Cliente")
	void testRegaloPromoCorrecto() {
		LocalDateTime inicio = LocalDateTime.now().minusDays(1);
		LocalDateTime fin = LocalDateTime.now().plusDays(1);
		Regalo promo = new Regalo("Regalo Figura", inicio, fin, 50.0, figura);

		Carrito carrito = new Carrito(clienteB);
		carrito.añadirProducto(watchmen, 4); // 15 * 4 = 60
		assertTrue(promo.aplicaRegalo(carrito));
		assertEquals(60.0, promo.aplicarDescuento(carrito));
	}

	@Test
	@Order(14)
	@DisplayName("Pack: Crear, añadir productos y validar precio")
	void testLogicaPackCompleta() {

		Pack pack = new Pack("Super Pack", "Desc", "img.jpg", 20.0, 5);
		int stockPrevio = watchmen.getStockDisponible();
		assertTrue(pack.addProducto(watchmen, 2));
		assertEquals(stockPrevio - (2 * 5), watchmen.getStockDisponible(),
				"El stock debe bajar (unidades * stockPack)");
		assertThrows(ProductoYaEnPackException.class, () -> pack.addProducto(watchmen, 1));
		assertThrows(ProductoInvalidoException.class, () -> pack.addProducto(pack, 1));
		assertThrows(ProductoInvalidoException.class, () -> pack.setPrecioOficial(29.5));
		assertNotNull(pack.toString());
		assertDoesNotThrow(() -> pack.resumenPrecios());
	}

	@Test
	@Order(15)
	@DisplayName("Pack: Eliminar y modificar unidades (Stock y Precios)")
	void testModificarEliminarPack() {
		Pack pack = new Pack("Pack Mod", "Desc", "img.jpg", 10.0, 2);
		pack.addProducto(watchmen, 2);
		assertTrue(pack.modificarUnidades(watchmen, 1));
		assertEquals(1, pack.getLineas().get(0).getUnidades());
		assertThrows(ProductoInvalidoException.class, () -> pack.modificarUnidades(watchmen, -1));
		assertTrue(pack.eliminarLinea(watchmen));
		assertTrue(pack.getLineas().isEmpty());
		assertFalse(pack.eliminarLinea(null));
	}

	@Test
	@Order(16)
	@DisplayName("Notificaciones: Registro, filtrado y leídas")
	void testNotificacionesTienda() {
		tienda.getHistorialNotificaciones().clear();
		Notificacion n1 = new Notificacion("Aviso Descuento", TipoNotificacion.DESCUENTO);
		@SuppressWarnings("unused")
		Notificacion n2 = new Notificacion("Aviso Empleado", TipoNotificacion.EMPLEADOS);

		assertEquals(2, tienda.getHistorialNotificaciones().size(), "Debe haber 2 notificaciones en el historial");
		assertEquals(2, tienda.getNotificacionesNoLeidas().size(), "Ambas deben estar sin leer");

		assertTrue(tienda.getHistorialNotificaciones().contains(n1));
		n1.marcarComoLeida();
		assertEquals(1, tienda.getNotificacionesNoLeidas().size(), "Solo debe quedar 1 sin leer");

		assertEquals(1, tienda.getNotificacionesPorTipo(TipoNotificacion.DESCUENTO).size());
	}

	@Test
	@Order(17)
	@DisplayName("Tienda: Imprimir descuentos y historiales")
	void testImpresionesYHistoriales() {

		assertDoesNotThrow(() -> tienda.imprimirDescuentosActivos());
		LocalDateTime hoy = LocalDateTime.now();
		Descuento d = new DescuentoVolumen("Promo", hoy.minusDays(1), hoy.plusDays(1), 10.0, 10.0);
		tienda.getDescuentosActivos().add(d);
		tienda.getHistorialDescuentos().add(d);

		assertDoesNotThrow(() -> tienda.imprimirDescuentosActivos());

		assertNotNull(tienda.getHistorialDescuentos());
		assertTrue(tienda.getHistorialDescuentos().contains(d));
	}

	@Test
	@Order(18)
	@DisplayName("Setters Finales: Cobertura de Descuentos, Categorías e Intercambio")
	void testSettersFinalesListas() {

		List<Descuento> listaDescAct = new ArrayList<>();
		tienda.setDescuentosActivos(listaDescAct);
		assertSame(listaDescAct, tienda.getDescuentosActivos());
		List<Descuento> listaHistDesc = new ArrayList<>();
		tienda.setHistorialDescuentos(listaHistDesc);
		assertSame(listaHistDesc, tienda.getHistorialDescuentos());
		List<Categoria> listaCats = new ArrayList<>();
		tienda.setCategorias(listaCats);
		assertSame(listaCats, tienda.getCategorias());
		List<Producto2Mano> listaInt = new ArrayList<>();
		tienda.setCatalogoIntercambio(listaInt);
		assertSame(listaInt, tienda.getCatalogoIntercambio());
	}

	@Test
	@Order(19)
	@DisplayName("limpiarDescuentosCaducados: Cobertura completa de la lógica de limpieza")
	void testLimpiarDescuentosCaducadosRamas() {
		LocalDateTime ahora = LocalDateTime.now();

		Descuento dActivo = new DescuentoVolumen("D_Activo", ahora.minusDays(1), ahora.plusDays(1), 10.0, 10.0);

		Descuento dCaducado = new DescuentoVolumen("D_Caducado", ahora.minusDays(5), ahora.minusHours(1), 10.0, 10.0);

		List<Descuento> listaParaSet = new ArrayList<>();
		listaParaSet.add(dActivo);
		listaParaSet.add(dCaducado);

		tienda.setDescuentosActivos(listaParaSet);
		assertEquals(2, tienda.getDescuentosActivos().size());

		tienda.limpiarDescuentosCaducados();

		assertEquals(1, tienda.getDescuentosActivos().size());
		assertTrue(tienda.getDescuentosActivos().contains(dActivo));
		assertFalse(tienda.getDescuentosActivos().contains(dCaducado));
	}

	@Test
	@Order(20)
	@DisplayName("imprimirUsuariosConSesionActiva: Cobertura de tipos de usuario")
	void testImprimirUsuariosSesionTipos() {

		List<UsuarioRegistrado> sesiones = new ArrayList<>();
		sesiones.add(gestor); // GESTOR

		Empleado e = new Empleado("emp_test", "Emp@1234");
		sesiones.add(e);
		sesiones.add(clienteA);
		tienda.setUsuariosConSesionActiva(sesiones);

		assertDoesNotThrow(() -> tienda.imprimirUsuariosConSesionActiva());
	}

	@Test
	@Order(21)
	@DisplayName("imprimirHistorialDescuentos: Cobertura lista vacía y con datos")
	void testImprimirHistorialDescuentos() {

		tienda.setHistorialDescuentos(new ArrayList<>());
		assertDoesNotThrow(() -> tienda.imprimirHistorialDescuentos());

		Descuento d = new DescuentoVolumen("Historial", LocalDateTime.now(), LocalDateTime.now().plusDays(1), 5.0, 5.0);
		tienda.getHistorialDescuentos().add(d);
		assertDoesNotThrow(() -> tienda.imprimirHistorialDescuentos());
	}

	@Test
	@Order(22)
	@DisplayName("buscarPackPorNombre: Cobertura nulos y no existentes")
	void testBuscarPackPorNombreRamas() {
		assertNull(tienda.buscarPackPorNombre(null));
		assertNull(tienda.buscarPackPorNombre("   "));
		assertNull(tienda.buscarPackPorNombre("Watchmen_ges"));
		assertNull(tienda.buscarPackPorNombre("Nombre_Fantasma"));
	}
}