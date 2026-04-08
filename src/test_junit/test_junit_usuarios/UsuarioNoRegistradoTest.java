package test_junit.test_junit_usuarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;

import productos.*;
import tienda.*;
import usuarios.*;

public class UsuarioNoRegistradoTest {

	private static Tienda tienda;
	private static Gestor gestor;
	private UsuarioNoRegistrado invitado;
	private ProductoVenta watchmen;

	@BeforeEach
	void setUp() {
		tienda = Tienda.getInstancia();
		tienda.vaciarTienda();
		gestor = tienda.getGestor();
		gestor.configurarTiemposSistema(60, 30, 30);
		gestor.crearCategoria("Anime_inv", "Anime y manga");
		gestor.crearCategoria("Familiar_inv", "Para toda la familia");

		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK);
		gestor.darDeAltaEmpleados_Permisos("emp_inv_test", "Stock@1234", permisosStock);
		Empleado emp = tienda.loginEmpleado("emp_inv_test", "Stock@1234");

		emp.añadirProducto_nuevo("C", "Watchmen_inv", "Comic test", "w.jpg", 15.0, 10,
				tienda.seleccionarCategorias("Anime_inv"), 400, "DC", 1987, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		emp.añadirProducto_nuevo("J", "Catan_inv", "Juego test", "c.jpg", 45.0, 5,
				tienda.seleccionarCategorias("Familiar_inv"), 0, null, 0, 0, 0, 0, null, null, 2, 4, 8, 99,
				"Estrategia");

		watchmen = tienda.buscarproductoPorNombre("Watchmen_inv").get(0);
		invitado = tienda.nuevoUsuarioNoRegistrado();
	}

	@Test
	@DisplayName("getSessionId devuelve un id con prefijo INVITADO")
	void testGetSessionId() {
		assertNotNull(invitado.getSessionId());
		assertTrue(invitado.getSessionId().startsWith("INVITADO-"));
	}

	@Test
	@DisplayName("dos invitados tienen sessionIds distintos")
	void testSessionIdsDistintos() {
		UsuarioNoRegistrado invitado2 = tienda.nuevoUsuarioNoRegistrado();
		assertNotEquals(invitado.getSessionId(), invitado2.getSessionId());
	}

	@Test
	@DisplayName("buscarProductos devuelve lista no vacia")
	void testBuscarProductos() {
		List<ProductoVenta> resultado = invitado.buscarProductos();
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductosPorNombre con nombre existente devuelve resultados")
	void testBuscarPorNombreOk() {
		List<ProductoVenta> resultado = invitado.buscarProductosPorNombre("Watchmen_inv");
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductosPorNombre con nombre inexistente devuelve lista vacia")
	void testBuscarPorNombreInexistente() {
		List<ProductoVenta> resultado = invitado.buscarProductosPorNombre("ProductoFalso");
		assertTrue(resultado == null || resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductoPorId con id valido devuelve producto")
	void testBuscarPorIdOk() {
		ProductoVenta p = invitado.buscarProductoPorId(watchmen.getId());
		assertNotNull(p);
		assertEquals(watchmen.getNombre(), p.getNombre());
	}

	@Test
	@DisplayName("buscarProductoPorId con id inexistente devuelve null")
	void testBuscarPorIdInexistente() {
		assertNull(invitado.buscarProductoPorId("ID-FALSO"));
	}

	@Test
	@DisplayName("buscarProductosPorCategoria con categoria existente devuelve resultados")
	void testBuscarPorCategoriaOk() {
		List<ProductoVenta> resultado = invitado.buscarProductosPorCategoria("Anime_inv");
		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("buscarProductosPorCategoria con categoria inexistente devuelve lista vacia")
	void testBuscarPorCategoriaInexistente() {
		List<ProductoVenta> resultado = invitado.buscarProductosPorCategoria("CategoriaFalsa");
		assertTrue(resultado == null || resultado.isEmpty());
	}

	@Test
	@DisplayName("registrarse con datos validos devuelve cliente")
	void testRegistrarseOk() {
		Cliente c = invitado.registrarse("invitado_test", "Invitado@1234", "77777777G");
		assertNotNull(c);
		assertEquals("invitado_test", c.getNickname());
	}

	@Test
	@DisplayName("registrarse con nickname duplicado devuelve null")
	void testRegistrarseNicknameDuplicado() {
		invitado.registrarse("invitado_dup", "Invitado@1234", "88888888H");
		Cliente c = invitado.registrarse("invitado_dup", "Invitado@1234", "99999999I");
		assertNull(c);
	}

	@Test
	@DisplayName("registrarse con password insegura devuelve null")
	void testRegistrarsePasswordInsegura() {
		Cliente c = invitado.registrarse("invitado_weak", "hola", "66666666F");
		assertNull(c);
	}

	@Test
	@DisplayName("registrarse con dni duplicado devuelve null")
	void testRegistrarseDniDuplicado() {
		invitado.registrarse("invitado_dni1", "Invitado@1234", "11111111A");
		Cliente c = invitado.registrarse("invitado_dni2", "Invitado@5678", "11111111A");
		assertNull(c);
	}

	@Test
	@DisplayName("filtrarPorPrecio no rompe el sistema y devuelve lista valida")
	void testFiltrarPorPrecio() {
		invitado.filtrarPorPrecio(10, 20);

		List<ProductoVenta> resultado = invitado.buscarProductos();

		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("filtrarPorCategoria devuelve productos de esa categoria")
	void testFiltrarPorCategoria() {
		invitado.filtrarPorCategoria("Anime_inv");

		List<ProductoVenta> resultado = invitado.buscarProductosVentaFiltrados();

		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
	}

	@Test
	@DisplayName("filtrarPorCategoria inexistente no rompe el sistema")
	void testFiltrarPorCategoriaInexistente() {
		invitado.filtrarPorCategoria("CategoriaFake");

		List<ProductoVenta> resultado = invitado.buscarProductosVentaFiltrados();

		assertNotNull(resultado); // no debería ser null
	}

	@Test
	@DisplayName("filtrarPorPuntuacion devuelve productos con puntuacion minima")
	void testFiltrarPorPuntuacion() {
		invitado.filtrarPorPuntuacion(0);

		List<ProductoVenta> resultado = invitado.buscarProductosVentaFiltrados();

		assertNotNull(resultado);
	}

	@Test
	@DisplayName("filtrarProductos combinado funciona correctamente")
	void testFiltrarProductosCombinado() {
		invitado.filtrarProductos(10, 50, 0, "Anime_inv");

		List<ProductoVenta> resultado = invitado.buscarProductosVentaFiltrados();

		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());

		for (ProductoVenta p : resultado) {
			assertTrue(p.getPrecioOficial() >= 10 && p.getPrecioOficial() <= 50);
		}
	}

}