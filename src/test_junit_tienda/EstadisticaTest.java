package test_junit_tienda;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tienda.Estadistica;

class EstadisticaTest {

	private Estadistica estadistica;

	@BeforeEach
	void setUp() {
		estadistica = Estadistica.getInstancia();
		estadistica.setnProductosVentas(1);
		estadistica.setnUsuarioRegistrado(1);
		estadistica.setnUsuarioNoRegistrado(1);
		estadistica.setnProducto2Mano(1);
		estadistica.setnVentas(1);
		estadistica.setnDescuentos(1);
		estadistica.setnIntercambiosFinalizados(1);
		estadistica.setnCategorias(1);
		estadistica.setnCarritos(1);
		estadistica.setnReseñas(1);
		estadistica.setnTasacionesCobradas(0);
		estadistica.setnNotificaciones(1);
	}

	@Test
	@DisplayName("Singleton devuelve misma instancia")
	void testSingleton() {
		Estadistica otra = Estadistica.getInstancia();
		assertSame(estadistica, otra);
	}

	@Test
	@DisplayName("Productos ventas")
	void testProductosVentas() {
		estadistica.setnProductosVentas(10);
		assertEquals(10, estadistica.getnProductosVentas());
	}

	@Test
	@DisplayName("Usuarios registrados")
	void testUsuariosRegistrados() {
		estadistica.setnUsuarioRegistrado(5);
		assertEquals(5, estadistica.getnUsuarioRegistrado());
	}

	@Test
	@DisplayName("Usuarios no registrados")
	void testUsuariosNoRegistrados() {
		estadistica.setnUsuarioNoRegistrado(3);
		assertEquals(3, estadistica.getnUsuarioNoRegistrado());
	}

	@Test
	@DisplayName("Producto segunda mano")
	void testProducto2Mano() {
		estadistica.setnProducto2Mano(7);
		assertEquals(7, estadistica.getnProducto2Mano());
	}

	@Test
	@DisplayName("Ventas")
	void testVentas() {
		estadistica.setnVentas(20);
		assertEquals(20, estadistica.getnVentas());
	}

	@Test
	@DisplayName("Descuentos")
	void testDescuentos() {
		estadistica.setnDescuentos(4);
		assertEquals(4, estadistica.getnDescuentos());
	}

	@Test
	@DisplayName("Intercambios")
	void testIntercambios() {
		estadistica.setnIntercambiosFinalizados(2);
		assertEquals(2, estadistica.getnIntercambiosFinalizados());
	}

	@Test
	@DisplayName("Categorias")
	void testCategorias() {
		estadistica.setnCategorias(6);
		assertEquals(6, estadistica.getnCategorias());
	}

	@Test
	@DisplayName("Carritos")
	void testCarritos() {
		estadistica.setnCarritos(8);
		assertEquals(8, estadistica.getnCarritos());
	}

	@Test
	@DisplayName("Reseñas")
	void testResenias() {
		estadistica.setnReseñas(9);
		assertEquals(9, estadistica.getnReseñas());
	}

	@Test
	@DisplayName("Tasaciones cobradas")
	void testTasaciones() {
		estadistica.setnTasacionesCobradas(15);
		assertEquals(15, estadistica.getnTasacionesCobradas());
	}

	@Test
	@DisplayName("Notificaciones")
	void testNotificaciones() {
		estadistica.setnNotificaciones(11);
		assertEquals(11, estadistica.getnNotificaciones());
	}

	@Test
	@DisplayName("Valores negativos")
	void testValoresNegativos() {
		estadistica.setnVentas(-5);
		assertEquals(-5, estadistica.getnVentas());
	}
}