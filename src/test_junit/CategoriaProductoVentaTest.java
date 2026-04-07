package test_junit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Excepcion.ProductoYaEnCategoriaException;
import Excepcion.ReseñaDuplicadaException;
import usuarios.Cliente;
import productos.*;

public class CategoriaProductoVentaTest {

	@Test
	void addProductoSincronizaCategoriaYProducto() {
		Categoria categoria = new Categoria("categoria1", "descripcion1");
		Comic comic = new Comic("comic1", "descripcionComic1", "imagen1.jpg", 12.0, 5, 100, "editorial1", 2020);

		assertTrue(categoria.addProducto(comic));

		assertTrue(categoria.getProductos().contains(comic));
		assertTrue(comic.getCategorias().contains(categoria));
	}

	@Test
	void addProductoDuplicadoLanzaExcepcion() {
		Categoria categoria = new Categoria("categoria1", "descripcion1");
		Comic comic = new Comic("comic1", "descripcionComic1", "imagen1.jpg", 12.0, 5, 100, "editorial1", 2020);

		categoria.addProducto(comic);

		assertThrows(ProductoYaEnCategoriaException.class, () -> categoria.addProducto(comic));
	}

	@Test
	void addProductoNullDevuelveFalse() {
		Categoria categoria = new Categoria("categoria1", "descripcion1");

		assertFalse(categoria.addProducto(null));
	}

	@Test
	void deleteProductoEliminaEnAmbosLados() {
		Categoria categoria = new Categoria("categoria1", "descripcion1");
		Comic comic = new Comic("comic1", "descripcionComic1", "imagen1.jpg", 12.0, 5, 100, "editorial1", 2020);
		categoria.addProducto(comic);

		assertTrue(categoria.deleteProducto(comic));

		assertFalse(categoria.getProductos().contains(comic));
		assertFalse(comic.getCategorias().contains(categoria));
	}

	@Test
	void deleteProductoNoExistenteDevuelveFalse() {
		Categoria categoria = new Categoria("categoria1", "descripcion1");
		Comic comic = new Comic("comic1", "descripcionComic1", "imagen1.jpg", 12.0, 5, 100, "editorial1", 2020);

		assertFalse(categoria.deleteProducto(comic));
	}

	@Test
	void addCategoriaDesdeProductoSincronizaAmbosLados() {
		Categoria categoria = new Categoria("categoria2", "descripcion2");
		JuegoMesa juego = new JuegoMesa("juego1", "descripcionJuego1", "imagen2.jpg", 35.0, 4, 3, 4, 10, 99, "tipo1");

		assertTrue(juego.addCategoria(categoria));

		assertTrue(juego.getCategorias().contains(categoria));
		assertTrue(categoria.getProductos().contains(juego));
	}

	@Test
	void deleteCategoriaDesdeProductoEliminaEnAmbosLados() {
		Categoria categoria = new Categoria("categoria2", "descripcion2");
		JuegoMesa juego = new JuegoMesa("juego1", "descripcionJuego1", "imagen2.jpg", 35.0, 4, 3, 4, 10, 99, "tipo1");
		juego.addCategoria(categoria);

		assertTrue(juego.deleteCategoria(categoria));

		assertFalse(juego.getCategorias().contains(categoria));
		assertFalse(categoria.getProductos().contains(juego));
	}

	@Test
	void mediaPuntuacionSinReseñasEsCero() {
		Comic comic = new Comic("comic2", "descripcionComic2", "imagen3.jpg", 11.0, 3, 90, "editorial2", 2021);

		assertEquals(0.0, comic.getMediaPuntuacion());
	}

	@Test
	void addReseñaAñadeYAsociaProducto() {
		Comic comic = new Comic("comic2", "descripcionComic2", "imagen3.jpg", 11.0, 3, 90, "editorial2", 2021);
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");

		Reseña reseña = new Reseña(cliente, comic, 8.0, "comentario1");

		assertEquals(1, comic.getReseñas().size());
		assertEquals(comic, reseña.getProducto());
		assertEquals(8.0, comic.getMediaPuntuacion());
	}

	@Test
	void addReseñaDuplicadaPorMismoAutorLanzaExcepcion() {
		Comic comic = new Comic("comic2", "descripcionComic2", "imagen3.jpg", 11.0, 3, 90, "editorial2", 2021);
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");

		new Reseña(cliente, comic, 8.0, "comentario1");

		assertThrows(ReseñaDuplicadaException.class, () -> new Reseña(cliente, comic, 9.0, "comentario2"));
	}

	@Test
	void deleteReseñaEliminaCorrectamente() {
		Comic comic = new Comic("comic2", "descripcionComic2", "imagen3.jpg", 11.0, 3, 90, "editorial2", 2021);
		Cliente cliente = new Cliente("cliente1", "1234", "11111111A");
		Reseña reseña = new Reseña(cliente, comic, 8.0, "comentario1");

		assertTrue(comic.deleteReseña(reseña));
		assertTrue(comic.getReseñas().isEmpty());
		assertEquals(0.0, comic.getMediaPuntuacion());
	}

	@Test
	void categoriaToStringMuestraProductos() {
		Categoria categoria = new Categoria("categoria1", "descripcion1");
		Comic comic = new Comic("comic1", "descripcionComic1", "imagen1.jpg", 12.0, 5, 100, "editorial1", 2020);
		categoria.addProducto(comic);

		String texto = categoria.toString();

		assertTrue(texto.contains("categoria1"));
		assertTrue(texto.contains("comic1"));
	}
}
