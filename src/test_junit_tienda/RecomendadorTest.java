package test_junit_tienda;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import excepciones.*;
import tienda.*;
import usuarios.*;

import productos.*;

class RecomendadorTest {

	private Recomendador r;

	@BeforeEach
	void setUp() {
		r = new Recomendador();
		Tienda t = Tienda.getInstancia();
		t.getStockVentas().clear();
		t.getUsuarios().clear();
	}

	@Test
	@DisplayName("Cliente null")
	void testClienteNull() throws Exception {
		List<ProductoVenta> res = r.generarSugerencias(null);
		assertTrue(res.isEmpty());
	}

	@Test
	@DisplayName("Recomendador inactivo")
	void testInactivo() {
		r.setConfiguracion(5, false);
		assertThrows(RecomendadorNoActivoException.class, () -> {
			r.generarSugerencias(new Cliente("Sacha", "PADSOF_LOVERS", "02576624A"));
		});
	}

	@Test
	@DisplayName("Sin productos")
	void testSinProductos() throws Exception {
		Cliente c = new Cliente("Sacha", "PADSOF_LOVERS", "02576624A");
		List<ProductoVenta> res = r.generarSugerencias(c);
		assertTrue(res.isEmpty());
	}

	@Test
	@DisplayName("Limite maximo")
	void testLimite() throws Exception {
		r.setConfiguracion(3, true);
		assertEquals(3, r.getLimiteMaximo());
	}

	@Test
	@DisplayName("Activar desactivar")
	void testActivo() {
		r.setConfiguracion(5, false);
		assertFalse(r.isActivo());
		r.setConfiguracion(5, true);
		assertTrue(r.isActivo());
	}

	@Test
	@DisplayName("Pesos correctos")
	void testPesos() throws Exception {
		r.setPesos(1, 1, 1);
		assertEquals(1.0 / 3, r.getPesoValoracion());
	}

	@Test
	@DisplayName("Pesos invalidos")
	void testPesosInvalidos() {
		assertThrows(PesosInvalidosException.class, () -> {
			r.setPesos(0, 0, 0);
		});
	}

	@Test
	@DisplayName("Getters")
	void testGetters() {
		assertNotNull(r);
		assertTrue(r.getLimiteMaximo() > 0);
	}

	@Test
	void testLimiteConfiguracionYEstado() throws Exception {
		Tienda.getInstancia().getStockVentas().clear();
		for (int i = 0; i < 10; i++) {
			Comic c = new Comic("C" + i, "D", "r", 1.0, 10, 10, "E", 2000);
			Tienda.getInstancia().getStockVentas().add(c);
			new Reseña(new Cliente("U" + i, "p", "0000000" + i + "A"), c, 10.0, "K");
		}

		r.setConfiguracion(2, true);
		Cliente temp = new Cliente("User", "p", "99999999Z");
		List<ProductoVenta> sugs = r.generarSugerencias(temp);
		assertEquals(2, sugs.size());

		r.setConfiguracion(2, false);
		assertThrows(RecomendadorNoActivoException.class, () -> r.generarSugerencias(temp));
	}

	@Test
	void testImprimirSugerenciasConsola() {
		Tienda.getInstancia().getStockVentas().clear();
		Comic c = new Comic("PrintTest", "D", "r", 10.0, 5, 50, "E", 2024);
		Tienda.getInstancia().getStockVentas().add(c);
		Cliente autor = new Cliente("A", "p", "12345678A");
		new Reseña(autor, c, 9.0, "G");

		java.io.PrintStream original = System.out;
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		System.setOut(new java.io.PrintStream(out));

		r.setConfiguracion(5, true);
		Cliente cPrueba = new Cliente("Sacha", "p", "02576624A");
		r.imprimirSugerencias(cPrueba);

		System.setOut(original);
		String output = out.toString();
		assertTrue(output.contains("Sugerencias para"));
		assertTrue(output.contains("PrintTest"));
	}
}