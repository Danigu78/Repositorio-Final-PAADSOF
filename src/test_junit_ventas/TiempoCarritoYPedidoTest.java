package test_junit_ventas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.*;

public class TiempoCarritoYPedidoTest {

	private ComprobadorTiempos comprobador;

	private static class ProductoPrueba extends ProductoVenta {
		public ProductoPrueba(String nombre, String descripcion, String imagenRuta, double precio, int stock) {
			super(nombre, descripcion, imagenRuta, precio, stock);
		}
	}

	@BeforeEach
	void preparar() {
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
	void carritoEmpiezaVacio() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		Carrito carrito = new Carrito(cliente1);

		assertTrue(carrito.estaVacio());
		assertEquals(0, carrito.getLineas().size());
		assertNull(carrito.getDescuentoAplicado());
	}

	@Test
	void carritoAnadirProductoYCalcularSubtotal() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "libro normal", "img1", 10.0, 10);

		Carrito carrito = new Carrito(cliente1);
		boolean metido = carrito.añadirProducto(libro1, 2);

		assertTrue(metido);
		assertEquals(1, carrito.getLineas().size());
		assertEquals(20.0, carrito.calcularSubtotal(), 0.01);
		assertEquals(8, libro1.getStockDisponible());
	}

	@Test
	void carritoCambiarCantidadProducto() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "libro normal", "img1", 10.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 2);

		boolean cambiado = carrito.cambiarCantidadProducto(libro1, 4);

		assertTrue(cambiado);
		assertEquals(40.0, carrito.calcularSubtotal(), 0.01);
		assertEquals(6, libro1.getStockDisponible());
	}

	@Test
	void carritoEliminarProducto() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img2", 25.0, 8);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 1);

		boolean quitado = carrito.eliminarProducto(juego2);

		assertTrue(quitado);
		assertTrue(carrito.estaVacio());
		assertEquals(8, juego2.getStockDisponible());
	}

	@Test
	void carritoVaciarCarritoDevuelveStock() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "libro normal", "img1", 10.0, 10);
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img2", 20.0, 5);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 2);
		carrito.añadirProducto(juego2, 1);

		carrito.vaciarCarrito();

		assertTrue(carrito.estaVacio());
		assertEquals(10, libro1.getStockDisponible());
		assertEquals(5, juego2.getStockDisponible());
		assertNull(carrito.getDescuentoAplicado());
	}

	@Test
	void carritoGetTotalConDescuentoVolumen() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img2", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 3); // 90

		DescuentoVolumen descuento1 = new DescuentoVolumen("descuento1", LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(1), 60.0, 10);

		carrito.setDescuentoAplicado(descuento1);

		assertEquals(81.0, carrito.getTotal(), 0.01);
	}

	@Test
	void carritoCaducadoNoDejaAnadir() {
		Tienda.getInstancia().setTiempoMaxCarrito(-1);

		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "libro normal", "img1", 10.0, 10);

		Carrito carrito = new Carrito(cliente1);
		boolean metido = carrito.añadirProducto(libro1, 1);

		assertFalse(metido);
		assertTrue(carrito.estaVacio());
	}

	@Test
	void pedidoSeCreaBienDesdeCarrito() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img1", 30.0, 7);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(EstadoPedido.PENDIENTE_PAGO, pedido.getEstado());
		assertEquals(60.0, pedido.getTotal(), 0.01);
		assertEquals(60.0, pedido.getTotalBruto(), 0.01);
		assertEquals(1, pedido.getLineas().size());
	}

	@Test
	void pedidoConRegaloAnadeLineaExtra() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img1", 30.0, 7);
		ProductoVenta caja3 = new ProductoPrueba("caja3", "caja regalo", "img2", 5.0, 4);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2); // 60

		Regalo regalo1 = new Regalo("regalo1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), 50.0,
				caja3);

		carrito.setDescuentoAplicado(regalo1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(2, pedido.getLineas().size());
		assertTrue(pedido.productoPertenece(juego2));
		assertTrue(pedido.productoPertenece(caja3));
		assertEquals(0.0, pedido.getPrecioDeProducto(caja3.getId()), 0.01);
	}

	@Test
	void pedidoContarUnidadesYPrecioDeProducto() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "libro normal", "img1", 15.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 3);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(3, pedido.contarUnidadesDe(libro1.getId()));
		assertEquals(15.0, pedido.getPrecioDeProducto(libro1.getId()), 0.01);
	}

	@Test
	void pedidoCancelarPedidoCambiaEstado() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img1", 30.0, 7);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);
		boolean cancelado = pedido.cancelarPedido();

		assertTrue(cancelado);
		assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
	}

	@Test
	void pedidoActualizarEstadoCancelado() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img1", 30.0, 7);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);
		boolean cambiado = pedido.actualizarEstado(EstadoPedido.CANCELADO);

		assertTrue(cambiado);
		assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
	}

	@Test
	void pedidoMarcarPreparadoYEntregadoConEstadoManual() {
		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "juego normal", "img1", 30.0, 7);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		pedido.setEstado(EstadoPedido.PAGADO);
		assertTrue(pedido.marcarPreparado());
		assertEquals(EstadoPedido.LISTO_PARA_RECOGER, pedido.getEstado());
		assertNotNull(pedido.getFechaPreparado());

		assertTrue(pedido.marcarEntregado());
		assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
		assertNotNull(pedido.getFechaEntregado());
	}

	@Test
	void pedidoCaducadoSiTiempoPagoNegativo() {
		Tienda.getInstancia().setTiempoMaxPago(-1);

		Cliente cliente1 = new Cliente("maria1", "1234", "11111111A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "libro normal", "img1", 15.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertTrue(pedido.isCaducado());
	}

	@Test
	void comprobadorRegistrarYObtenerCarrito() {
		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");
		ProductoVenta libro4 = new ProductoPrueba("libro4", "libro normal", "img4", 12.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro4, 2);

		comprobador.registrarCarrito(cliente1.getId(), carrito);

		assertNotNull(comprobador.obtenerCarrito(cliente1));
		assertNotNull(comprobador.getCarrito(cliente1.getId()));
	}

	@Test
	void comprobadorRegistrarPedidoYVerPendientes() {
		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");
		ProductoVenta libro4 = new ProductoPrueba("libro4", "libro normal", "img4", 12.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro4, 2);
		Pedido pedido = new Pedido(cliente1, carrito);

		comprobador.registrarPedido(cliente1.getId(), pedido);

		assertEquals(1, comprobador.getPedidosPendientesDeUsuario(cliente1.getId()).size());
	}

	@Test
	void comprobadorQuitarCarrito() {
		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");
		ProductoVenta libro4 = new ProductoPrueba("libro4", "libro normal", "img4", 12.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro4, 2);

		comprobador.registrarCarrito(cliente1.getId(), carrito);
		comprobador.quitarCarrito(cliente1.getId());

		assertNull(comprobador.getCarrito(cliente1.getId()));
	}

	@Test
	void comprobadorEliminarCarrito() {
		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");
		ProductoVenta libro4 = new ProductoPrueba("libro4", "libro normal", "img4", 12.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro4, 2);

		comprobador.registrarCarrito(cliente1.getId(), carrito);
		comprobador.eliminarCarrito(cliente1.getId());

		assertNull(comprobador.getCarrito(cliente1.getId()));
	}

	@Test
	void comprobadorLimpiarCarritos() {
		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");
		Cliente cliente2 = new Cliente("lucia1", "1234", "33333333C");

		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img1", 10.0, 10);
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img2", 20.0, 10);

		Carrito carrito1 = new Carrito(cliente1);
		Carrito carrito2 = new Carrito(cliente2);

		carrito1.añadirProducto(libro1, 1);
		carrito2.añadirProducto(juego2, 1);

		comprobador.registrarCarrito(cliente1.getId(), carrito1);
		comprobador.registrarCarrito(cliente2.getId(), carrito2);

		comprobador.limpiarCarritos();

		assertNull(comprobador.getCarrito(cliente1.getId()));
		assertNull(comprobador.getCarrito(cliente2.getId()));
	}

	@Test
	void comprobadorRevisaCarritosCaducados() {
		Tienda.getInstancia().setTiempoMaxCarrito(-1);

		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");

		Carrito carrito = new Carrito(cliente1);
		comprobador.registrarCarrito(cliente1.getId(), carrito);

		comprobador.revisarCarritosCaducados();

		assertNull(comprobador.getCarrito(cliente1.getId()));
	}

	@Test
	void comprobadorRevisaPedidosCaducados() {
		Tienda.getInstancia().setTiempoMaxPago(-1);

		Cliente cliente1 = new Cliente("pablo1", "1234", "22222222B");
		ProductoVenta libro4 = new ProductoPrueba("libro4", "libro normal", "img4", 12.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro4, 2);

		Pedido pedido = new Pedido(cliente1, carrito);
		comprobador.registrarPedido(cliente1.getId(), pedido);

		comprobador.revisarPedidosPendientesCaducados();

		assertTrue(comprobador.getPedidosPendientesDeUsuario(cliente1.getId()).isEmpty());
		assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
	}

	@Test
	void carritoGetTotalSinDescuento() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 2);

		assertEquals(20.0, carrito.getTotal(), 0.01);
	}

	@Test
	void carritoAnadirProductoNullDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		Carrito carrito = new Carrito(cliente1);

		assertFalse(carrito.añadirProducto(null, 2));
	}

	@Test
	void carritoAnadirProductoCantidadIncorrectaDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);

		assertFalse(carrito.añadirProducto(libro1, 0));
	}

	@Test
	void carritoAnadirProductoSinStockDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 1);
		Carrito carrito = new Carrito(cliente1);

		assertFalse(carrito.añadirProducto(libro1, 3));
	}

	@Test
	void carritoAnadirMismoProductoSumaCantidad() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);

		assertTrue(carrito.añadirProducto(libro1, 2));
		assertTrue(carrito.añadirProducto(libro1, 3));

		assertEquals(1, carrito.getLineas().size());
		assertEquals(5, carrito.getLineas().get(0).getCantidad());
		assertEquals(50.0, carrito.calcularSubtotal(), 0.01);
	}

	@Test
	void carritoEliminarProductoNullDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		Carrito carrito = new Carrito(cliente1);

		assertFalse(carrito.eliminarProducto(null));
	}

	@Test
	void carritoEliminarProductoNoExistenteDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 20.0, 10);
		Carrito carrito = new Carrito(cliente1);

		carrito.añadirProducto(libro1, 1);

		assertFalse(carrito.eliminarProducto(juego2));
	}

	@Test
	void carritoCambiarCantidadNullDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		Carrito carrito = new Carrito(cliente1);

		assertFalse(carrito.cambiarCantidadProducto(null, 2));
	}

	@Test
	void carritoCambiarCantidadNegativaDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);

		assertFalse(carrito.cambiarCantidadProducto(libro1, -1));
	}

	@Test
	void carritoCambiarCantidadACeroEliminaProducto() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);

		carrito.añadirProducto(libro1, 2);
		assertTrue(carrito.cambiarCantidadProducto(libro1, 0));

		assertTrue(carrito.estaVacio());
	}

	@Test
	void carritoCambiarCantidadSinStockSuficienteDevuelveFalse() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 3);
		Carrito carrito = new Carrito(cliente1);

		carrito.añadirProducto(libro1, 2);
		assertFalse(carrito.cambiarCantidadProducto(libro1, 5));
	}

	@Test
	void carritoGetTotalConRegaloDevuelveSubtotal() {
		Cliente cliente1 = new Cliente("ana2", "1234", "12345678B");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);
		ProductoVenta caja3 = new ProductoPrueba("caja3", "desc", "img", 5.0, 4);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Regalo regalo1 = new Regalo("regalo1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), 50.0,
				caja3);

		carrito.setDescuentoAplicado(regalo1);

		assertEquals(60.0, carrito.getTotal(), 0.01);
	}

	@Test
	void pedidoNoSePuedeCrearConClienteNull() {
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		assertThrows(IllegalArgumentException.class, () -> new Pedido(null, carrito));
	}

	@Test
	void pedidoNoSePuedeCrearConCarritoNull() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");

		assertThrows(IllegalArgumentException.class, () -> new Pedido(cliente1, null));
	}

	@Test
	void pedidoNoSePuedeCrearConCarritoVacio() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		Carrito carrito = new Carrito(cliente1);

		assertThrows(IllegalArgumentException.class, () -> new Pedido(cliente1, carrito));
	}

	@Test
	void pedidoNoSePuedeCrearConCarritoCaducado() {
		Tienda.getInstancia().setTiempoMaxCarrito(-1);

		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		Carrito carrito = new Carrito(cliente1);

		assertThrows(IllegalArgumentException.class, () -> new Pedido(cliente1, carrito));

		Tienda.getInstancia().setTiempoMaxCarrito(10);
	}

	@Test
	void pedidoProductoPerteneceNullDevuelveFalse() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertFalse(pedido.productoPertenece(null));
	}

	@Test
	void pedidoContarUnidadesConIdNullDevuelveCero() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(0, pedido.contarUnidadesDe(null));
	}

	@Test
	void pedidoGetPrecioDeProductoConIdNullDevuelveMenosUno() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(-1.0, pedido.getPrecioDeProducto(null), 0.01);
	}

	@Test
	void pedidoGetPrecioDeProductoNoEncontradoDevuelveCero() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertEquals(0.0, pedido.getPrecioDeProducto("NO-EXISTE"), 0.01);
	}

	@Test
	void pedidoSetDescuentoAplicadoMientrasEstaPendiente() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);

		DescuentoVolumen descuento1 = new DescuentoVolumen("descuento1", LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(1), 50.0, 10);

		assertTrue(pedido.setDescuentoAplicado(descuento1));
		assertEquals(54.0, pedido.getTotal(), 0.01);
	}

	@Test
	void pedidoSetDescuentoAplicadoNoFuncionaSiNoEstaPendiente() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2);

		Pedido pedido = new Pedido(cliente1, carrito);
		pedido.setEstado(EstadoPedido.PAGADO);

		DescuentoVolumen descuento1 = new DescuentoVolumen("descuento1", LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(1), 50.0, 10);

		assertFalse(pedido.setDescuentoAplicado(descuento1));
	}

	@Test
	void pedidoMarcarPreparadoFallaSiNoEstaPagado() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertFalse(pedido.marcarPreparado());
	}

	@Test
	void pedidoMarcarEntregadoFallaSiNoEstaListo() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertFalse(pedido.marcarEntregado());
	}

	@Test
	void pedidoActualizarEstadoNullDevuelveFalse() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertFalse(pedido.actualizarEstado(null));
	}

	@Test
	void pedidoActualizarEstadoIgualDevuelveTrue() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);

		assertTrue(pedido.actualizarEstado(EstadoPedido.PENDIENTE_PAGO));
	}

	@Test
	void pedidoNoCaducaSiNoEstaPendiente() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);
		pedido.setEstado(EstadoPedido.PAGADO);

		assertFalse(pedido.isCaducado());
	}

	@Test
	void pedidoPagarDevuelveFalseSiNoEstaPendiente() {
		Cliente cliente1 = new Cliente("maria2", "1234", "11111112A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 1);

		Pedido pedido = new Pedido(cliente1, carrito);
		pedido.setEstado(EstadoPedido.PAGADO);

		boolean pagado = pedido.pagar("1234567812345678", 123,
				new java.util.Date(System.currentTimeMillis() + 1000000000));

		assertFalse(pagado);
	}
}
