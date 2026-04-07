package test_junit;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Excepcion.*;
import tienda.*;
import usuarios.*;

class MotorEstadisticoTest {

	private MotorEstadistico motor;
	private Tienda tienda;

	@BeforeEach
	void setUp() {
		motor = new MotorEstadistico();
		tienda = Tienda.getInstancia();
		tienda.obtenerClientesTienda().clear();
		tienda.getHistorialVentas().clear();
		tienda.getIntercambiosFinalizados().clear();
		tienda.getHistorialProductos2Mano().clear();
	}

	@Test
	@DisplayName("Clientes vacio pedidos cancelados")
	void testClientesVacioCancelados() {
		List<Cliente> res = motor.obtenerClientesConMasPedidosCaducados();
		assertTrue(res.isEmpty());
	}

	@Test
	@DisplayName("Clientes vacio compras")
	void testClientesVacioCompras() {
		List<Cliente> res = motor.obtenerClientesConMasCompras();
		assertTrue(res.isEmpty());
	}

	@Test
	@DisplayName("Clientes vacio intercambios")
	void testClientesVacioIntercambios() {
		List<Cliente> res = motor.obtenerClientesConMasIntercambios();
		assertTrue(res.isEmpty());
	}

	@Test
	@DisplayName("Rango fechas invalido")
	void testRangoFechasInvalido() {
		assertThrows(RangoFechasInvalidoException.class, () -> {
			motor.calcularIngresosRangoFechas(LocalDate.now(), LocalDate.now().minusDays(1));
		});
	}

	@Test
	@DisplayName("Rango fechas null")
	void testRangoFechasNull() {
		assertThrows(RangoFechasInvalidoException.class, () -> {
			motor.calcularIngresosRangoFechas(null, LocalDate.now());
		});
	}

	@Test
	@DisplayName("Ingresos vacios")
	void testIngresosVacios() throws Exception {
		double res = motor.calcularIngresosRangoFechas(LocalDate.now().minusDays(10), LocalDate.now());
		assertEquals(0.0, res);
	}

	@Test
	@DisplayName("Año invalido")
	void testAnioInvalido() {
		assertThrows(AñoInvalidoException.class, () -> {
			motor.calcularIngresosMesesAño(0);
		});
	}

	@Test
	@DisplayName("Ingresos año")
	void testIngresosAnio() throws Exception {
		double[] res = motor.calcularIngresosMesesAño(2024);
		assertEquals(12, res.length);
	}

	@Test
	@DisplayName("Ingresos año actual")
	void testIngresosActual() throws Exception {
		double[] res = motor.calcularIngresosMesesAñoActual();
		assertEquals(12, res.length);
	}

	@Test
	@DisplayName("Ingresos ventas vacio")
	void testIngresosVentas() {
		double res = motor.calcularIngresosVenta();
		assertEquals(0.0, res);
	}

	@Test
	@DisplayName("Ingresos tasacion vacio")
	void testIngresosTasacion() {
		double res = motor.calcularIngresosTasacion();
		assertEquals(0.0, res);
	}
}