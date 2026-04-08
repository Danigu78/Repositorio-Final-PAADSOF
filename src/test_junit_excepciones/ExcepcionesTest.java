package test_junit_excepciones;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import excepciones.*;

public class ExcepcionesTest {

	@Test
	public void testCheckPointException() {
		CheckPointException e = new CheckPointException("Error base");
		assertEquals("Error base", e.getMessage());
		assertTrue(e instanceof RuntimeException);
	}

	@Test
	public void testAñoInvalidoException() {
		AñoInvalidoException e = new AñoInvalidoException(-5);

		assertEquals(-5, e.getAño());
		assertTrue(e.getMessage().contains("-5"));
		assertTrue(e instanceof CheckPointException);
	}

	@Test
	public void testFicheroFormatoInvalidoExceptionAtravesDeHija() {

		TipoProductoDesconocidoException e = new TipoProductoDesconocidoException(10, "L;PRODUCTO;X", "X");

		assertTrue(e.getMessage().contains("Error en línea 10"));
		assertTrue(e.getMessage().contains("Tipo de producto desconocido: X"));
		assertTrue(e instanceof FicheroFormatoInvalidoException);
	}

	@Test
	public void testOfertaNoDisponibleException() {
		OfertaNoDisponibleException e = new OfertaNoDisponibleException("OFER-1");

		assertEquals("OFER-1", e.getIdOferta());
		assertTrue(e instanceof CheckPointException);
	}

	@Test
	public void testPesosInvalidosException() {
		PesosInvalidosException e = new PesosInvalidosException(-1.0, 0.5, 0.5);

		assertNotNull(e.getMessage());
		assertTrue(e instanceof CheckPointException);
	}

	@Test
	public void testProductoYaEnCategoriaException() {

		ProductoYaEnCategoriaException e1 = new ProductoYaEnCategoriaException();
		assertTrue(e1.getMessage().contains("categoría"));

		ProductoYaEnCategoriaException e2 = new ProductoYaEnCategoriaException("Error personalizado");
		assertEquals("Error personalizado", e2.getMessage());
	}

	@Test
	public void testReseñaDuplicadaException() {
		ReseñaDuplicadaException e = new ReseñaDuplicadaException("PROD-100");
		assertTrue(e.getMessage().contains("PROD-100"));
		assertTrue(e instanceof CheckPointException);
	}

	@Test
	public void testValoracionInvalidaException() {
		ValoracionInvalidaException e = new ValoracionInvalidaException("Nota 11 fuera de rango");
		assertTrue(e.getMessage().contains("11"));
		assertTrue(e instanceof CheckPointException);
	}

	@Test
	public void testLanzamientoAñoInvalido() {

		assertThrows(AñoInvalidoException.class, () -> {
			throw new AñoInvalidoException(-2026);
		});
	}

	@Test
	public void testLanzamientoProductoBloqueado() {
		assertThrows(ProductoBloqueadoException.class, () -> {
			throw new ProductoBloqueadoException("PROD-001");
		});
	}

	@Test
	public void testLanzamientoValoracionInvalida() {
		assertThrows(ValoracionInvalidaException.class, () -> {
			throw new ValoracionInvalidaException("Nota 15 no permitida");
		});
	}

	@Test
	public void testLanzamientoReseñaDuplicada() {
		assertThrows(ReseñaDuplicadaException.class, () -> {
			throw new ReseñaDuplicadaException("ID-USER-123");
		});
	}

	@Test
	public void testProductoNoTasadoException() {
		ProductoNoTasadoException e = new ProductoNoTasadoException("ID1", "ProductoTest");

		assertEquals("ID1", e.getIdProducto());
		assertEquals("ProductoTest", e.getNombreProducto());
		assertTrue(e.getMessage().contains("no puede participar en un intercambio"));
		assertTrue(e instanceof CheckPointException);
	}

	@Test
	public void testProductoYaEnPackException() {
		ProductoYaEnPackException e1 = new ProductoYaEnPackException();
		assertTrue(e1.getMessage().contains("ya está incluido"));

		ProductoYaEnPackException e2 = new ProductoYaEnPackException("Mensaje custom");
		assertEquals("Mensaje custom", e2.getMessage());
	}

	@Test
	public void testRangoFechasInvalidoException() {
		java.time.LocalDate inicio = java.time.LocalDate.of(2025, 1, 10);
		java.time.LocalDate fin = java.time.LocalDate.of(2025, 1, 5);

		RangoFechasInvalidoException e = new RangoFechasInvalidoException(inicio, fin);

		assertEquals(inicio, e.getInicio());
		assertEquals(fin, e.getFin());
		assertTrue(e.getMessage().contains("Rango de fechas invalido"));
	}

	@Test
	public void testRecomendadorNoActivoException() {
		RecomendadorNoActivoException e = new RecomendadorNoActivoException();

		assertTrue(e.getMessage().contains("recomendador"));
		assertTrue(e instanceof CheckPointException);
	}

	@Test
	public void testStockInsuficienteParaPackException() {
		StockInsuficienteParaPackException e1 = new StockInsuficienteParaPackException();
		assertTrue(e1.getMessage().contains("stock suficiente"));

		StockInsuficienteParaPackException e2 = new StockInsuficienteParaPackException("Custom");
		assertEquals("Custom", e2.getMessage());
	}

	@Test
	public void testProductoInvalidoException() {
		ProductoInvalidoException e1 = new ProductoInvalidoException();
		assertTrue(e1.getMessage().contains("no son válidos"));

		ProductoInvalidoException e2 = new ProductoInvalidoException("Error custom");
		assertEquals("Error custom", e2.getMessage());
	}

	@Test
	public void testLanzamientoProductoNoTasado() {
		assertThrows(ProductoNoTasadoException.class, () -> {
			throw new ProductoNoTasadoException("ID1", "ProductoTest");
		});
	}

	@Test
	public void testLanzamientoRangoFechasInvalido() {
		assertThrows(RangoFechasInvalidoException.class, () -> {
			throw new RangoFechasInvalidoException(java.time.LocalDate.now(), java.time.LocalDate.now().minusDays(1));
		});
	}

}