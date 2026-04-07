package test_junit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.Date;

import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.*;

public class PedidoTiempoYPagoTest {

	private static class ProductoPrueba extends ProductoVenta {
		public ProductoPrueba(String nombre, String descripcion, String imagenRuta, double precio, int stock) {
			super(nombre, descripcion, imagenRuta, precio, stock);
		}
	}

	private ComprobadorTiempos comprobador;

	@BeforeEach
	void preparar() {
		Tienda.getInstancia().vaciarTienda();
		Tienda.getInstancia().setTiempoMaxCarrito(10);
		Tienda.getInstancia().setTiempoMaxOferta(10);
		Tienda.getInstancia().setTiempoMaxPago(10);
		comprobador = new ComprobadorTiempos();
	}

	@AfterEach
	void cerrar() {
		comprobador.cerrarGestorTiempo();
	}

	@Test
	void pedidoSeCreaBien() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(EstadoPedido.PENDIENTE_PAGO, pedido.getEstado());
		assertEquals(60.0, pedido.getTotal(), 0.01);
		assertEquals(1, pedido.getLineas().size());
	}

	@Test
	void pedidoCuentaUnidades() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(2, pedido.contarUnidadesDe(juego2.getId()));
	}

	@Test
	void pedidoDevuelvePrecioDeProducto() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(30.0, pedido.getPrecioDeProducto(juego2.getId()), 0.01);
	}

	@Test
	void pedidoTotalBruto() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(60.0, pedido.getTotalBruto(), 0.01);
	}

	@Test
	void pedidoCancelarPedido() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertTrue(pedido.cancelarPedido());
		assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
	}

	@Test
	void pedidoActualizarEstadoCancelado() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertTrue(pedido.actualizarEstado(EstadoPedido.CANCELADO));
		assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
	}

	@Test
	void comprobadorGuardaCarritoYPedido() {
		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");
		ProductoVenta libro4 = new ProductoPrueba("libro4", "desc", "img", 12.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro4, 2);
		Pedido pedido = new Pedido(cliente1, carrito);

		comprobador.registrarCarrito(cliente1.getId(), carrito);
		comprobador.registrarPedido(cliente1.getId(), pedido);

		assertNotNull(comprobador.getCarrito(cliente1.getId()));
		assertEquals(1, comprobador.getPedidosPendientesDeUsuario(cliente1.getId()).size());
	}

	@Test
	void pagoInvalidoFalla() {
		Date fechaFutura = new Date(System.currentTimeMillis() + 1000000000);
		Pago pago1 = new Pago("1234", 20.0, fechaFutura, 123);

		assertFalse(pago1.getExito());
		assertNotNull(pago1.getFechaTransaccion());
	}
}
