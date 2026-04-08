package test_junit_tienda;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import productos.EstadoProducto;
import productos.Producto2Mano;
import tienda.FiltroSegundaMano;
import usuarios.Cliente;
import usuarios.Empleado;

class FiltroSegundaManoTest {

	private FiltroSegundaMano filtro;
	private Producto2Mano producto;
	private Cliente cliente;
	private Empleado empleado;

	@BeforeEach
	void setUp() {
		filtro = new FiltroSegundaMano();
		filtro.setEstadoMinimo(EstadoProducto.USO_EVIDENTE);
		cliente = new Cliente("Jose", "PADSOF2026", "02576624A");
		empleado = new Empleado("emp", "pass");
		producto = new Producto2Mano(cliente, "movil", "desc", "img");
	}

	@Test
	@DisplayName("Producto cumple filtro por defecto")
	void testCumpleFiltroBasico() {
		producto.valorar(100, EstadoProducto.MUY_BUENO, empleado);
		producto.setVisible(true);
		producto.setBloqueado(false);

		assertTrue(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Producto null no cumple")
	void testProductoNull() {
		assertFalse(filtro.cumpleFiltro(null));
	}

	@Test
	@DisplayName("Producto no visible no cumple")
	void testNoVisible() {
		producto.valorar(100, EstadoProducto.MUY_BUENO, empleado);
		producto.setVisible(false);
		producto.setBloqueado(false);

		assertFalse(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Producto bloqueado no cumple")
	void testBloqueado() {
		producto.valorar(100, EstadoProducto.MUY_BUENO, empleado);
		producto.setVisible(true);
		producto.setBloqueado(true);

		assertFalse(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Producto sin valoracion no cumple")
	void testSinValoracion() {
		producto.setVisible(true);
		producto.setBloqueado(false);

		assertFalse(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Precio fuera de rango no cumple")
	void testPrecioFueraRango() {
		producto.valorar(100, EstadoProducto.MUY_BUENO, empleado);
		producto.setVisible(true);
		producto.setBloqueado(false);

		filtro.setValorMinimo(200);

		assertFalse(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Precio dentro de rango cumple")
	void testPrecioDentroRango() {
		producto.valorar(100, EstadoProducto.MUY_BUENO, empleado);
		producto.setVisible(true);
		producto.setBloqueado(false);

		filtro.setValorMinimo(50);
		filtro.setValorMaximo(150);

		assertTrue(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Estado peor que minimo no cumple")
	void testEstadoPeor() {
		producto.valorar(100, EstadoProducto.USO_EVIDENTE, empleado);
		producto.setVisible(true);
		producto.setBloqueado(false);

		filtro.setEstadoMinimo(EstadoProducto.MUY_BUENO);

		assertFalse(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Estado igual o mejor cumple")
	void testEstadoMejor() {
		producto.valorar(100, EstadoProducto.PERFECTO, empleado);
		producto.setVisible(true);
		producto.setBloqueado(false);

		filtro.setEstadoMinimo(EstadoProducto.MUY_BUENO);

		assertTrue(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Estado no aceptado no cumple")
	void testNoAceptado() {
		producto.valorar(100, EstadoProducto.NO_ACEPTADO, empleado);
		if (producto.getValoracion().getEstadoProducto() == EstadoProducto.NO_ACEPTADO) {
			System.out.println("ESTA BIEN AAAAAAAA");
		}
		producto.setVisible(true);
		producto.setBloqueado(false);

		assertFalse(filtro.cumpleFiltro(producto));
	}

	@Test
	@DisplayName("Resetear valores")
	void testResetear() {
		filtro.setValorMinimo(50);
		filtro.setValorMaximo(100);
		filtro.setEstadoMinimo(EstadoProducto.MUY_BUENO);

		filtro.resetear();

		assertEquals(0, filtro.getValorMinimo());
		assertEquals(Double.MAX_VALUE, filtro.getValorMaximo());
		assertNull(filtro.getEstadoMinimo());
	}

	@Test
	@DisplayName("Valor minimo negativo no cambia")
	void testValorMinimoNegativo() {
		filtro.setValorMinimo(-10);
		assertEquals(0, filtro.getValorMinimo());
	}

	@Test
	@DisplayName("Valor maximo menor que minimo no cambia")
	void testValorMaximoIncorrecto() {
		filtro.setValorMinimo(100);
		filtro.setValorMaximo(50);

		assertEquals(Double.MAX_VALUE, filtro.getValorMaximo());
	}
}