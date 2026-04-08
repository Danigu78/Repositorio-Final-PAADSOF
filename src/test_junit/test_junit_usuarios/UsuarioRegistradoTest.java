package test_junit.test_junit_usuarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;

import productos.*;
import tienda.*;
import usuarios.*;

public class UsuarioRegistradoTest {

	private static Tienda tienda;
	private static Gestor gestor;
	private Cliente alice;
	private Cliente bob;
	private ProductoVenta watchmen;
	private Producto2Mano p2m;

	@BeforeEach
	void setUp() {
		tienda = Tienda.getInstancia();
		tienda.vaciarTienda();
		gestor = tienda.getGestor();
		gestor.configurarTiemposSistema(60, 30, 30);
		gestor.crearCategoria("Anime_ur", "Anime y manga");
		gestor.crearCategoria("Familiar_ur", "Para toda la familia");

		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK);
		gestor.darDeAltaEmpleados_Permisos("emp_ur_test", "Stock@1234", permisosStock);
		Empleado emp = tienda.loginEmpleado("emp_ur_test", "Stock@1234");

		emp.añadirProducto_nuevo("C", "Watchmen_ur", "Comic test", "w.jpg", 15.0, 20,
				tienda.seleccionarCategorias("Anime_ur"), 400, "DC", 1987, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		emp.añadirProducto_nuevo("J", "Catan_ur", "Juego test", "c.jpg", 45.0, 5,
				tienda.seleccionarCategorias("Familiar_ur"), 0, null, 0, 0, 0, 0, null, null, 2, 4, 8, 99,
				"Estrategia");

		watchmen = tienda.buscarproductoPorNombre("Watchmen_ur").get(0);

		alice = tienda.registrarNuevoCliente("alice_ur", "Alice@1234", "11111111A");
		bob = tienda.registrarNuevoCliente("bob_ur", "Bob@1234", "22222222B");
		alice = tienda.loginCliente("alice_ur", "Alice@1234");
		bob = tienda.loginCliente("bob_ur", "Bob@1234");

		bob.subirProducto("Naruto_ur", "Buen estado", "img.jpg");
		p2m = bob.getCarteraIntercambio().get(bob.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(p2m);
		p2m.valorar(10.0, EstadoProducto.MUY_BUENO, emp);
		tienda.publicarParaIntercambio(p2m);
	}

	@Test
	@DisplayName("login con contrasena correcta devuelve true")
	void testLoginCorrecto() {
		alice.logout();
		assertTrue(alice.login("Alice@1234"));
		assertTrue(alice.isSesionIniciada());
	}

	@Test
	@DisplayName("login con contrasena incorrecta devuelve false")
	void testLoginIncorrecto() {
		alice.logout();
		assertFalse(alice.login("wrongpass"));
		assertFalse(alice.isSesionIniciada());
	}

	@Test
	@DisplayName("logout cierra la sesion correctamente")
	void testLogout() {
		assertTrue(alice.isSesionIniciada());
		alice.logout();
		assertFalse(alice.isSesionIniciada());
		assertFalse(tienda.getUsuariosConSesionActiva().contains(alice));
	}

	@Test
	@DisplayName("validarPassword con password segura devuelve true")
	void testValidarPasswordOk() {
		assertTrue(UsuarioRegistrado.validarPassword("Segura@1234"));
	}

	@Test
	@DisplayName("validarPassword sin mayuscula devuelve false")
	void testValidarPasswordSinMayuscula() {
		assertFalse(UsuarioRegistrado.validarPassword("insegura@1234"));
	}

	@Test
	@DisplayName("validarPassword sin minuscula devuelve false")
	void testValidarPasswordSinMinuscula() {
		assertFalse(UsuarioRegistrado.validarPassword("INSEGURA@1234"));
	}

	@Test
	@DisplayName("validarPassword sin numero devuelve false")
	void testValidarPasswordSinNumero() {
		assertFalse(UsuarioRegistrado.validarPassword("Insegura@Pass"));
	}

	@Test
	@DisplayName("validarPassword sin caracter especial devuelve false")
	void testValidarPasswordSinEspecial() {
		assertFalse(UsuarioRegistrado.validarPassword("Insegura1234"));
	}

	@Test
	@DisplayName("validarPassword con menos de 8 caracteres devuelve false")
	void testValidarPasswordCorta() {
		assertFalse(UsuarioRegistrado.validarPassword("Ab@1"));
	}

	@Test
	@DisplayName("validarPassword con null devuelve false")
	void testValidarPasswordNull() {
		assertFalse(UsuarioRegistrado.validarPassword(null));
	}

	@Test
	@DisplayName("buscarProductosPorNombre con nombre existente devuelve resultados")
	void testBuscarPorNombreOk() {
		List<ProductoVenta> resultado = alice.buscarProductosPorNombre("Watchmen_ur");
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductosPorNombre con nombre inexistente devuelve lista vacia")
	void testBuscarPorNombreInexistente() {
		List<ProductoVenta> resultado = alice.buscarProductosPorNombre("ProductoFalso");
		assertTrue(resultado == null || resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductoPorId con id valido devuelve producto")
	void testBuscarPorIdOk() {
		ProductoVenta p = alice.buscarProductoPorId(watchmen.getId());
		assertNotNull(p);
		assertEquals(watchmen.getNombre(), p.getNombre());
	}

	@Test
	@DisplayName("buscarProductoPorId con id inexistente devuelve null")
	void testBuscarPorIdInexistente() {
		assertNull(alice.buscarProductoPorId("ID-FALSO"));
	}

	@Test
	@DisplayName("buscarProductosPorCategoria con categoria existente devuelve resultados")
	void testBuscarPorCategoriaOk() {
		List<ProductoVenta> resultado = alice.buscarProductosPorCategoria("Anime_ur");
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductosPorCategoria con categoria inexistente devuelve lista vacia")
	void testBuscarPorCategoriaInexistente() {
		List<ProductoVenta> resultado = alice.buscarProductosPorCategoria("CategoriaFalsa");
		assertTrue(resultado == null || resultado.isEmpty());
	}

	@Test
	@DisplayName("verReseñasProducto con null devuelve null")
	void testVerReseñasNull() {
		assertNull(alice.verReseñasProducto(null));
	}

	@Test
	@DisplayName("verReseñasProducto sin reseñas devuelve lista vacia")
	void testVerReseñasSinReseñas() {
		List<?> resultado = alice.verReseñasProducto(watchmen);
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductosSegundaMano devuelve productos visibles")
	void testBuscarSegundaMano() {
		List<Producto2Mano> resultado = alice.buscarProductosSegundaMano();
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
		assertTrue(resultado.contains(p2m));
	}

	@Test
	@DisplayName("buscarProducto2ManoNombre con nombre existente devuelve resultados")
	void testBuscarSegundaManoNombreOk() {
		List<Producto2Mano> resultado = alice.buscarProducto2ManoNombre("Naruto_ur");
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProducto2ManoNombre con nombre inexistente devuelve lista vacia")
	void testBuscarSegundaManoNombreInexistente() {
		List<Producto2Mano> resultado = alice.buscarProducto2ManoNombre("ProductoFalso");
		assertTrue(resultado == null || resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProducto2ManoPorid con id valido devuelve producto")
	void testBuscarSegundaManoIdOk() {
		Producto2Mano resultado = alice.buscarProducto2ManoPorid(p2m.getId());
		assertNotNull(resultado);
		assertEquals(p2m.getNombre(), resultado.getNombre());
	}

	@Test
	@DisplayName("buscarProducto2ManoPorid con id inexistente devuelve null")
	void testBuscarSegundaManoIdInexistente() {
		assertNull(alice.buscarProducto2ManoPorid("ID-FALSO"));
	}

	@Test
	@DisplayName("verCarteraCliente con nickname valido devuelve productos visibles")
	void testVerCarteraClienteOk() {
		List<Producto2Mano> resultado = alice.verCarteraCliente("bob_ur");
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
		assertTrue(resultado.contains(p2m));
	}

	@Test
	@DisplayName("verCarteraCliente con propio nickname devuelve lista vacia")
	void testVerCarteraClientePropio() {
		List<Producto2Mano> resultado = alice.verCarteraCliente("alice_ur");
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());
	}

	@Test
	@DisplayName("verCarteraCliente con null devuelve null")
	void testVerCarteraClienteNull() {
		assertNull(alice.verCarteraCliente(null));
	}

	@Test
	@DisplayName("verCarteraCliente con nickname inexistente devuelve lista vacia")
	void testVerCarteraClienteInexistente() {
		List<Producto2Mano> resultado = alice.verCarteraCliente("nicknameFalso");
		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());
	}

	@Test
	@DisplayName("filtrarPorPrecio no lanza excepcion")
	void testFiltrarPorPrecio() {
		assertDoesNotThrow(() -> alice.filtrarPorPrecio(10.0, 20.0));
	}

	@Test
	@DisplayName("filtrarPorCategoria con categoria existente no lanza excepcion")
	void testFiltrarPorCategoria() {
		assertDoesNotThrow(() -> alice.filtrarPorCategoria("Anime_ur"));
	}

	@Test
	@DisplayName("filtrarPorPuntuacion no lanza excepcion")
	void testFiltrarPorPuntuacion() {
		assertDoesNotThrow(() -> alice.filtrarPorPuntuacion(7.0));
	}

	@Test
	@DisplayName("filtrarProductos con todos los parametros no lanza excepcion")
	void testFiltrarProductos() {
		assertDoesNotThrow(() -> alice.filtrarProductos(10.0, 50.0, 0.0, "Anime_ur"));
	}

	@Test
	@DisplayName("filtrar2ManoPorValor no lanza excepcion")
	void testFiltrar2ManoPorValor() {
		assertDoesNotThrow(() -> alice.filtrar2ManoPorValor(5.0, 20.0));
	}

	@Test
	@DisplayName("filtrar2ManoPorEstado no lanza excepcion")
	void testFiltrar2ManoPorEstado() {
		assertDoesNotThrow(() -> alice.filtrar2ManoPorEstado(EstadoProducto.MUY_BUENO));
	}

	@Test
	@DisplayName("filtrar2Mano con todos los parametros no lanza excepcion")
	void testFiltrar2Mano() {
		assertDoesNotThrow(() -> alice.filtrar2Mano(5.0, 20.0, EstadoProducto.MUY_BUENO));
	}

	@Test
	@DisplayName("setSesionIniciada actualiza el estado de sesión")
	void testSetSesionIniciada() {
		alice.setSesionIniciada(false);
		assertFalse(alice.isSesionIniciada());
		alice.setSesionIniciada(true);
		assertTrue(alice.isSesionIniciada());
	}

	@Test
	@DisplayName("buscarProductoPorId con null devuelve null")
	void testBuscarPorIdNull() {
		assertNull(alice.buscarProductoPorId(null));
	}

	@Test
	@DisplayName("buscarProductosPorCategoria con null devuelve lista vacía")
	void testBuscarPorCategoriaNull() {
		List<ProductoVenta> resultado = alice.buscarProductosPorCategoria(null);
		assertTrue(resultado == null || resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProducto2ManoNombre con null devuelve lista vacía")
	void testBuscar2ManoNombreNull() {
		List<Producto2Mano> resultado = alice.buscarProducto2ManoNombre(null);
		assertTrue(resultado == null || resultado.isEmpty());
	}
}