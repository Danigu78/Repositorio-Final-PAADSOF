package test_junit.test_junit_ventas;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.*;

public class CarritoYRegaloTest {

	private static class ProductoPrueba extends ProductoVenta {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static byte[] datOriginal;

	    @BeforeAll
	    static void guardarDat() throws Exception {
	        File fichero = new File("datos_tienda.dat");
	        if (fichero.exists()) {
	            datOriginal = Files.readAllBytes(fichero.toPath());
	        }
	    }

	    @AfterAll
	    static void restaurarDat() throws Exception {
	        if (datOriginal != null) {
	            Files.write(Paths.get("datos_tienda.dat"), datOriginal);
	            GuardadoTienda.cargar(); // Recarga la tienda con los datos originales
	        }
	    }
		public ProductoPrueba(String nombre, String descripcion, String imagenRuta, double precio, int stock) {
			super(nombre, descripcion, imagenRuta, precio, stock);
		}
	}

	@BeforeEach
	void preparar() {
		Tienda.getInstancia().vaciarTienda();
		Tienda.getInstancia().setTiempoMaxCarrito(10);
		Tienda.getInstancia().setTiempoMaxOferta(10);
		Tienda.getInstancia().setTiempoMaxPago(10);
	}

	@Test
	void carritoEmpiezaVacio() {
		Cliente cliente1 = new Cliente("luis1", "1234", "87654321B");
		Carrito carrito = new Carrito(cliente1);

		assertTrue(carrito.estaVacio());
	}

	@Test
	void carritoAnadirProducto() {
		Cliente cliente1 = new Cliente("luis1", "1234", "87654321B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);

		assertTrue(carrito.añadirProducto(libro1, 2));
		assertEquals(1, carrito.getLineas().size());
		assertEquals(20.0, carrito.calcularSubtotal(), 0.01);
	}

	@Test
	void carritoCambiarCantidadProducto() {
		Cliente cliente1 = new Cliente("luis1", "1234", "87654321B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);

		carrito.añadirProducto(libro1, 2);
		assertTrue(carrito.cambiarCantidadProducto(libro1, 4));

		assertEquals(40.0, carrito.calcularSubtotal(), 0.01);
	}

	@Test
	void carritoEliminarProducto() {
		Cliente cliente1 = new Cliente("luis1", "1234", "87654321B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		Carrito carrito = new Carrito(cliente1);

		carrito.añadirProducto(libro1, 2);
		assertTrue(carrito.eliminarProducto(libro1));
		assertTrue(carrito.estaVacio());
	}

	@Test
	void carritoVaciarCarrito() {
		Cliente cliente1 = new Cliente("luis1", "1234", "87654321B");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 20.0, 10);
		Carrito carrito = new Carrito(cliente1);

		carrito.añadirProducto(libro1, 2);
		carrito.añadirProducto(juego2, 1);
		carrito.vaciarCarrito();

		assertTrue(carrito.estaVacio());
		assertNull(carrito.getDescuentoAplicado());
	}



	@Test
	void regaloAplicaRegaloSiLlegaAlUmbral() {
		Cliente cliente1 = new Cliente("luis1", "1234", "87654321B");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);
		ProductoVenta caja3 = new ProductoPrueba("caja3", "desc", "img", 5.0, 4);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 2); // 60

		Regalo regalo = new Regalo("regalo1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), 50.0,
				caja3);

		assertTrue(regalo.aplicaRegalo(carrito));
		assertEquals(60.0, regalo.aplicarDescuento(carrito), 0.01);
	}

	@Test
	void estadoPedidoTienePendientePago() {
		assertEquals("PENDIENTE_PAGO", EstadoPedido.PENDIENTE_PAGO.name());
		assertNotNull(EstadoPedido.CANCELADO);
	}
}
