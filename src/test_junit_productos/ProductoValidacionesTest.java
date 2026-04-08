package test_junit_productos;

import static org.junit.jupiter.api.Assertions.*;
import productos.*;
import org.junit.jupiter.api.Test;

import excepciones.ProductoInvalidoException;

public class ProductoValidacionesTest {

	@Test
	void comicValidoSeCreaCorrectamente() {
		Comic comic = new Comic("comic1", "descripcion1", "imagen1.jpg", 12.5, 8, 120, "editorial1", 2020);

		assertEquals("comic1", comic.getNombre());
		assertEquals(12.5, comic.getPrecioOficial());
		assertEquals(8, comic.getStockDisponible());
		assertTrue(comic.toString().contains("editorial1"));
	}

	@Test
	void comicConNumeroPaginasInvalidoLanzaExcepcion() {
		assertThrows(ProductoInvalidoException.class,
				() -> new Comic("comic1", "descripcion1", "imagen1.jpg", 12.5, 8, 0, "editorial1", 2020));
	}

	@Test
	void comicConEditorialVaciaLanzaExcepcion() {
		assertThrows(ProductoInvalidoException.class,
				() -> new Comic("comic1", "descripcion1", "imagen1.jpg", 12.5, 8, 120, " ", 2020));
	}

	@Test
	void figuraValidaSeCreaCorrectamente() {
		Figura figura = new Figura("figura1", "descripcion2", "imagen2.jpg", 30.0, 5, 20.0, 10.0, 8.0, "material1",
				"marca1");

		assertEquals("figura1", figura.getNombre());
		assertEquals(30.0, figura.getPrecioOficial());
		assertEquals(5, figura.getStockDisponible());
		assertTrue(figura.toString().contains("material1"));
	}

	@Test
	void figuraConDimensionesInvalidasLanzaExcepcion() {
		assertThrows(ProductoInvalidoException.class, () -> new Figura("figura1", "descripcion2", "imagen2.jpg", 30.0,
				5, -1.0, 10.0, 8.0, "material1", "marca1"));
	}

	@Test
	void juegoMesaValidoSeCreaCorrectamente() {
		JuegoMesa juego = new JuegoMesa("juego1", "descripcion3", "imagen3.jpg", 40.0, 6, 3, 4, 10, 99, "tipo1");

		assertEquals("juego1", juego.getNombre());
		assertEquals(40.0, juego.getPrecioOficial());
		assertEquals(6, juego.getStockDisponible());
		assertTrue(juego.toString().contains("tipo1"));
	}

	@Test
	void juegoMesaConMaxJugadoresMenorQueMinLanzaExcepcion() {
		assertThrows(ProductoInvalidoException.class,
				() -> new JuegoMesa("juego1", "descripcion3", "imagen3.jpg", 40.0, 6, 4, 3, 10, 99, "tipo1"));
	}

	@Test
	void lineaPackValidaCalculaSubtotalCorrectamente() {
		Comic comic = new Comic("comic2", "descripcion4", "imagen4.jpg", 10.0, 10, 80, "editorial2", 2021);
		LineaPack linea = new LineaPack(comic, 3);

		assertEquals(comic, linea.getProducto());
		assertEquals(3, linea.getUnidades());
		assertEquals(30.0, linea.getSubtotal());
	}

	@Test
	void lineaPackConProductoNullLanzaExcepcion() {
		assertThrows(ProductoInvalidoException.class, () -> new LineaPack(null, 2));
	}

	@Test
	void lineaPackConUnidadesInvalidasLanzaExcepcion() {
		Comic comic = new Comic("comic2", "descripcion4", "imagen4.jpg", 10.0, 10, 80, "editorial2", 2021);

		assertThrows(ProductoInvalidoException.class, () -> new LineaPack(comic, 0));
	}

	@Test
	void setUnidadesActualizaCorrectamente() {
		Comic comic = new Comic("comic2", "descripcion4", "imagen4.jpg", 10.0, 10, 80, "editorial2", 2021);
		LineaPack linea = new LineaPack(comic, 2);

		linea.setUnidades(5);

		assertEquals(5, linea.getUnidades());
		assertEquals(50.0, linea.getSubtotal());
	}

	@Test
	void settersDeProductoValidanDescripcionImagenStockYPrecio() {
		Comic comic = new Comic("comic3", "descripcion5", "imagen5.jpg", 15.0, 7, 90, "editorial3", 2022);

		comic.setDescripcion("descripcionNueva");
		comic.setImagenRuta("imagenNueva.jpg");
		comic.setStockDisponible(12);
		comic.setPrecioOficial(18.0);

		assertEquals("descripcionNueva", comic.getDescripcion());
		assertEquals("imagenNueva.jpg", comic.getImagenRuta());
		assertEquals(12, comic.getStockDisponible());
		assertEquals(18.0, comic.getPrecioOficial());
	}

	@Test
	void settersInvalidosDeProductoLanzanExcepcion() {
		Comic comic = new Comic("comic3", "descripcion5", "imagen5.jpg", 15.0, 7, 90, "editorial3", 2022);

		assertThrows(ProductoInvalidoException.class, () -> comic.setDescripcion(" "));
		assertThrows(ProductoInvalidoException.class, () -> comic.setImagenRuta(null));
		assertThrows(ProductoInvalidoException.class, () -> comic.setStockDisponible(-1));
		assertThrows(ProductoInvalidoException.class, () -> comic.setPrecioOficial(-0.1));
	}
}
