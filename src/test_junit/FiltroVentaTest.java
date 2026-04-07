package test_junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import productos.Categoria;
import productos.Comic;
import productos.ProductoVenta;
import tienda.FiltroVenta;

class FiltroVentaTest {

    private FiltroVenta filtro;
    private ProductoVenta producto;
    private Categoria cat1;
    private Categoria cat2;

    @BeforeEach
    void setUp() {
        filtro = new FiltroVenta();
        cat1 = new Categoria("DRAGON BALL","DESC");
        cat2 = new Categoria("ONE PIECE", "DESC");
        
        producto = new Comic("prod", "desc", "img", 100, 10, 250, "Santillana", 2000);
        		
        producto.addCategoria(cat1);

    }

    @Test
    @DisplayName("Cumple filtro basico")
    void testCumpleFiltro() {
        assertTrue(filtro.productoCumpleFiltro(producto));
    }

    @Test
    @DisplayName("Producto null")
    void testNull() {
        assertFalse(filtro.productoCumpleFiltro(null));
    }

    @Test
    @DisplayName("Precio fuera rango")
    void testPrecioFuera() {
        filtro.setPrecioMinimo(200);
        assertFalse(filtro.productoCumpleFiltro(producto));
    }

    @Test
    @DisplayName("Precio dentro rango")
    void testPrecioDentro() {
        filtro.setPrecioMinimo(50);
        filtro.setPrecioMaximo(150);
        assertTrue(filtro.productoCumpleFiltro(producto));
    }

    @Test
    @DisplayName("Puntuacion insuficiente")
    void testPuntuacionBaja() {
        filtro.setPuntuacionMinima(9);
        assertFalse(filtro.productoCumpleFiltro(producto));
    }

    @Test
    @DisplayName("Puntuacion suficiente")
    void testPuntuacionOk() {
        filtro.setPuntuacionMinima(7);
        assertTrue(filtro.productoCumpleFiltro(producto));
    }

    @Test
    @DisplayName("Categoria coincide")
    void testCategoriaOk() {
        filtro.añadirCategoria(cat1);
        assertTrue(filtro.productoCumpleFiltro(producto));
    }

    @Test
    @DisplayName("Categoria no coincide")
    void testCategoriaNo() {
        filtro.añadirCategoria(cat2);
        assertFalse(filtro.productoCumpleFiltro(producto));
    }

    @Test
    @DisplayName("Añadir categoria")
    void testAñadirCategoria() {
        filtro.añadirCategoria(cat1);
        List<Categoria> cats = filtro.getCategorias();
        assertTrue(cats.contains(cat1));
    }

    @Test
    @DisplayName("Eliminar categoria")
    void testEliminarCategoria() {
        filtro.añadirCategoria(cat1);
        filtro.eliminarCategoria(cat1);
        assertFalse(filtro.getCategorias().contains(cat1));
    }

    @Test
    @DisplayName("Resetear")
    void testResetear() {
        filtro.setPrecioMinimo(10);
        filtro.setPrecioMaximo(20);
        filtro.setPuntuacionMinima(5);
        filtro.añadirCategoria(cat1);

        filtro.resetear();

        assertEquals(0, filtro.getPrecioMinimo());
        assertEquals(Double.MAX_VALUE, filtro.getPrecioMaximo());
        assertEquals(0, filtro.getPuntuacionMinima());
        assertTrue(filtro.getCategorias().isEmpty());
    }

    @Test
    @DisplayName("Precio minimo negativo")
    void testPrecioMinNegativo() {
        filtro.setPrecioMinimo(-10);
        assertEquals(0, filtro.getPrecioMinimo());
    }

    @Test
    @DisplayName("Precio maximo incorrecto")
    void testPrecioMaxIncorrecto() {
        filtro.setPrecioMinimo(100);
        filtro.setPrecioMaximo(50);
        assertEquals(Double.MAX_VALUE, filtro.getPrecioMaximo());
    }

    @Test
    @DisplayName("Puntuacion fuera rango")
    void testPuntuacionFuera() {
        filtro.setPuntuacionMinima(20);
        assertEquals(0, filtro.getPuntuacionMinima());
    }
}