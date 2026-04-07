package test_junit.productos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Excepcion.ProductoInvalidoException;
import Excepcion.ProductoYaEnPackException;
import Excepcion.StockInsuficienteParaPackException;
import productos.*;

public class PackTest {

	@Test
	void addProductoAñadeLineaYDescuentaStock() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 8.0, 2);

		assertTrue(pack.addProducto(comic, 3));

		assertEquals(1, pack.getLineas().size());
		assertTrue(pack.contieneProducto(comic));
		assertEquals(14, comic.getStockDisponible());
	}

	@Test
	void addProductoConUnaUnidadFunciona() {
		Figura figura = new Figura("figura1", "descripcion2", "imagen2.jpg", 25.0, 10, 20, 10, 8, "material1",
				"marca1");
		Pack pack = new Pack("pack2", "descripcionPack2", "imagenPack2.jpg", 15.0, 2);

		assertTrue(pack.addProducto_conunaUnidad(figura));

		assertEquals(1, pack.getLineas().size());
		assertEquals(8, figura.getStockDisponible());
	}

	@Test
	void addProductoDuplicadoLanzaExcepcion() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 8.0, 2);
		pack.addProducto(comic, 2);

		assertThrows(ProductoYaEnPackException.class, () -> pack.addProducto(comic, 1));
	}

	@Test
	void packNoPuedeContenerseASiMismo() {
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 8.0, 2);

		assertThrows(ProductoInvalidoException.class, () -> pack.addProducto(pack, 1));
	}

	@Test
	void addProductoConStockInsuficienteLanzaExcepcion() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 3, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 8.0, 2);

		assertThrows(StockInsuficienteParaPackException.class, () -> pack.addProducto(comic, 2));
	}

	@Test
	void eliminarLineaDevuelveStockAlProducto() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 8.0, 2);
		pack.addProducto(comic, 3);

		assertTrue(pack.eliminarLinea(comic));

		assertFalse(pack.contieneProducto(comic));
		assertEquals(20, comic.getStockDisponible());
	}

	@Test
	void modificarUnidadesAumentaYRecalculaStock() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 15.0, 2);
		pack.addProducto(comic, 2);

		assertTrue(pack.modificarUnidades(comic, 4));

		assertEquals(12, comic.getStockDisponible());
		assertEquals(40.0, pack.calcularSumaProductos());
	}

	@Test
	void modificarUnidadesACeroEliminaLinea() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 15.0, 2);
		pack.addProducto(comic, 2);

		assertTrue(pack.modificarUnidades(comic, 0));

		assertFalse(pack.contieneProducto(comic));
		assertEquals(20, comic.getStockDisponible());
	}

	@Test
	void modificarUnidadesConStockInsuficienteLanzaExcepcion() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 5, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 15.0, 2);
		pack.addProducto(comic, 2);

		assertThrows(StockInsuficienteParaPackException.class, () -> pack.modificarUnidades(comic, 5));
	}

	@Test
	void setPrecioOficialValidoActualizaPrecio() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 15.0, 2);
		pack.addProducto(comic, 1);

		assertTrue(pack.setPrecioOficial(8.0));
		assertEquals(8.0, pack.getPrecioOficial());
	}

	@Test
	void setPrecioOficialInvalidoLanzaExcepcion() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Pack pack = new Pack("pack1", "descripcionPack1", "imagenPack1.jpg", 15.0, 2);
		pack.addProducto(comic, 1);

		assertThrows(ProductoInvalidoException.class, () -> pack.setPrecioOficial(9.5));
	}

	@Test
	void calcularSumaProductosYPrecioFinalFuncionan() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 10.0, 20, 100, "editorial1", 2020);
		Figura figura = new Figura("figura1", "descripcion2", "imagen2.jpg", 25.0, 10, 20, 10, 8, "material1",
				"marca1");
		Pack pack = new Pack("pack3", "descripcionPack3", "imagenPack3.jpg", 20.0, 1);

		pack.addProducto(comic, 2);
		pack.addProducto(figura, 1);

		assertEquals(45.0, pack.calcularSumaProductos());
		assertEquals(20.0, pack.calcularPrecioFinal());
		assertTrue(pack.toString().contains("pack3"));
	}
}
