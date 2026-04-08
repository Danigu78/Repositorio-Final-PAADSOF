package test_junit_productos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import Excepcion.ProductoInvalidoException;
import Excepcion.ValoracionInvalidaException;
import usuarios.Cliente;
import usuarios.Empleado;
import ventas.Pago;
import productos.*;

public class SegundaManoTest {

	@Test
	void producto2ManoConPropietarioValidoSeCreaCorrectamente() {
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");

		Producto2Mano producto = new Producto2Mano(cliente, "producto1", "descripcion1", "imagen1.jpg");

		assertEquals("producto1", producto.getNombre());
		assertEquals(cliente, producto.getPropietario());
		assertTrue(producto.isBloqueado());
		assertFalse(producto.isVisible());
		assertNull(producto.getValoracion());
	}

	@Test
	void producto2ManoSinPropietarioLanzaExcepcion() {
		assertThrows(ProductoInvalidoException.class,
				() -> new Producto2Mano(null, "producto1", "descripcion1", "imagen1.jpg"));
	}

	@Test
	void valorarProductoAceptadoLoHaceVisibleYBloqueado() {
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");
		Empleado empleado = new Empleado("empleado1", "1234");
		Producto2Mano producto = new Producto2Mano(cliente, "producto1", "descripcion1", "imagen1.jpg");

		boolean resultado = producto.valorar(30.0, EstadoProducto.MUY_BUENO, empleado);

		assertTrue(resultado);
		assertNotNull(producto.getValoracion());
		assertTrue(producto.isVisible());
		assertEquals(EstadoProducto.MUY_BUENO, producto.getValoracion().getEstadoProducto());
	}

	@Test
	void valorarProductoNoAceptadoDevuelveFalseYNoEsVisible() {
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");
		Empleado empleado = new Empleado("empleado1", "1234");
		Producto2Mano producto = new Producto2Mano(cliente, "producto1", "descripcion1", "imagen1.jpg");

		boolean resultado = producto.valorar(10.0, EstadoProducto.NO_ACEPTADO, empleado);

		assertFalse(resultado);
		assertFalse(producto.isVisible());
		assertTrue(producto.isBloqueado());
		assertNotNull(producto.getValoracion());
	}

	@Test
	void valorarConParametrosInvalidosLanzaExcepcion() {
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");
		Empleado empleado = new Empleado("empleado1", "1234");
		Producto2Mano producto = new Producto2Mano(cliente, "producto1", "descripcion1", "imagen1.jpg");

		assertThrows(ValoracionInvalidaException.class,
				() -> producto.valorar(-1.0, EstadoProducto.MUY_BUENO, empleado));
		assertThrows(ValoracionInvalidaException.class, () -> producto.valorar(10.0, null, empleado));
		assertThrows(ValoracionInvalidaException.class, () -> producto.valorar(10.0, EstadoProducto.MUY_BUENO, null));
	}

	@Test
	void settersDeProducto2ManoActualizanEstado() {
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");
		Empleado empleado = new Empleado("empleado1", "1234");
		Producto2Mano producto = new Producto2Mano(cliente, "producto1", "descripcion1", "imagen1.jpg");
		Valoracion valoracion = new Valoracion(20.0, EstadoProducto.PERFECTO, empleado);

		producto.setBloqueado(false);
		producto.setVisible(true);
		producto.setValoracion(valoracion);

		assertFalse(producto.isBloqueado());
		assertTrue(producto.isVisible());
		assertEquals(valoracion, producto.getValoracion());
	}

	@Test
	void reseñaSeCreaYGuardaFechaYProducto() {
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");
		Comic comic = new Comic("comic1", "descripcion2", "imagen2.jpg", 10.0, 20, 100, "editorial1", 2020);

		Reseña reseña = new Reseña(cliente, comic, 7.5, "comentario1");

		assertEquals(cliente, reseña.getAutor());
		assertEquals(comic, reseña.getProducto());
		assertNotNull(reseña.getFecha());
		assertTrue(reseña.toString().contains("comentario1"));
	}

	@Test
	void reseñaConPuntuacionInvalidaLanzaExcepcion() {
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");
		Comic comic = new Comic("comic1", "descripcion2", "imagen2.jpg", 10.0, 20, 100, "editorial1", 2020);

		assertThrows(ProductoInvalidoException.class, () -> new Reseña(cliente, comic, -1.0, "comentario1"));
		assertThrows(ProductoInvalidoException.class, () -> new Reseña(cliente, comic, 11.0, "comentario1"));
	}

	@Test
	void valoracionBasicaSeCreaConEstadoPendienteDePago() {
		Empleado empleado = new Empleado("empleado1", "1234");

		Valoracion valoracion = new Valoracion(50.0, EstadoProducto.USO_LIGERO, empleado);

		assertEquals(50.0, valoracion.getPrecioTasacion());
		assertEquals(EstadoProducto.USO_LIGERO, valoracion.getEstadoProducto());
		assertEquals(EstadoValoracion.PENDIENTE_DE_PAGO, valoracion.getEstadoValoracion());
		assertEquals(empleado, valoracion.getEmpleado());
		assertNotNull(valoracion.getFecha());
	}

	@Test
	void valoracionCompletaSeCreaCorrectamente() {
		Empleado empleado = new Empleado("empleado1", "1234");

		Valoracion valoracion = new Valoracion(LocalDateTime.now(), 40.0, EstadoProducto.MUY_BUENO,
				EstadoValoracion.PAGADO, empleado, (Pago) null);

		assertEquals(40.0, valoracion.getPrecioTasacion());
		assertEquals(EstadoProducto.MUY_BUENO, valoracion.getEstadoProducto());
		assertEquals(EstadoValoracion.PAGADO, valoracion.getEstadoValoracion());
	}

	@Test
	void settersDeValoracionActualizanValores() {
		Empleado empleado = new Empleado("empleado1", "1234");
		Valoracion valoracion = new Valoracion(50.0, EstadoProducto.USO_LIGERO, empleado);

		valoracion.setPrecioTasacion(60.0);
		valoracion.setEstadoValoracion(EstadoValoracion.REALIZADA);
		valoracion.setPrecioPagado(15.0);

		assertEquals(60.0, valoracion.getPrecioTasacion());
		assertEquals(EstadoValoracion.REALIZADA, valoracion.getEstadoValoracion());
		assertEquals(15.0, valoracion.getPrecioPagado());
	}

	@Test
	void settersInvalidosDeValoracionLanzanExcepcion() {
		Empleado empleado = new Empleado("empleado1", "1234");
		Valoracion valoracion = new Valoracion(50.0, EstadoProducto.USO_LIGERO, empleado);

		assertThrows(ValoracionInvalidaException.class, () -> valoracion.setPrecioTasacion(-2.0));
		assertThrows(ValoracionInvalidaException.class, () -> valoracion.setEstadoValoracion(null));
	}
}
