package test_junit.test_junit_ventas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.Categoria;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.*;

public class LineasYDescuentosTest {

	private static class ProductoPrueba extends ProductoVenta {
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
	void lineaCarritoSubtotal() {
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		LineaCarrito linea = new LineaCarrito(libro1, 3);

		assertEquals(30.0, linea.getSubtotal(), 0.01);
	}

	@Test
	void lineaCarritoProductoPertenece() {
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);
		LineaCarrito linea = new LineaCarrito(libro1, 2);

		assertTrue(linea.productoPertence(libro1));
	}

	@Test
	void lineaPedidoSubtotal() {
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 25.0, 8);
		LineaPedido linea = new LineaPedido(juego2, 2, 25.0);

		assertEquals(50.0, linea.getSubtotal(), 0.01);
	}

	@Test
	void lineaPedidoPrecioVenta() {
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 25.0, 8);
		LineaPedido linea = new LineaPedido(juego2, 2, 25.0);

		assertEquals(25.0, linea.getPrecioVenta(), 0.01);
	}

	@Test
	void descuentoCantidadSeAplicaBien() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta libro1 = new ProductoPrueba("libro1", "desc", "img", 10.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(libro1, 3);

		DescuentoCantidad descuento = new DescuentoCantidad("descuento1", LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(1), 3, 10);

		assertEquals(27.0, descuento.aplicarDescuento(carrito), 0.01);
	}

	@Test
	void descuentoCategoriaSeAplicaBien() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta imagen5 = new ProductoPrueba("imagen5", "desc", "img", 20.0, 10);
		Categoria categoria1 = new Categoria("categoria1", "simple");
		imagen5.addCategoria(categoria1);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(imagen5, 2);

		DescuentoCategoria descuento = new DescuentoCategoria("descuento2", LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(1), categoria1, 20);

		assertEquals(32.0, descuento.aplicarDescuento(carrito), 0.01);
	}

	@Test
	void descuentoVolumenSeAplicaBien() {
		Cliente cliente1 = new Cliente("ana1", "1234", "12345678A");
		ProductoVenta juego2 = new ProductoPrueba("juego2", "desc", "img", 30.0, 10);

		Carrito carrito = new Carrito(cliente1);
		carrito.añadirProducto(juego2, 3); // 90

		DescuentoVolumen descuento = new DescuentoVolumen("descuento3", LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(1), 60.0, 10);

		assertEquals(81.0, descuento.aplicarDescuento(carrito), 0.01);
	}

	@Test
	void descuentoBaseTieneNombreYId() {
		DescuentoCantidad descuento = new DescuentoCantidad("rebaja1", LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(1), 2, 10);

		assertNotNull(descuento.getId());
		assertEquals("rebaja1", descuento.getNombre());
		assertTrue(descuento.estaActivo());
	}
}