package test_junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tienda.*;
import usuarios.*;
import productos.*;
import Excepcion.*;

class RecomendadorTest {

    private Recomendador r;

    @BeforeEach
    void setUp() {
        r = new Recomendador();
        Tienda t = Tienda.getInstancia();
        t.getStockVentas().clear();
        t.getUsuarios().clear();
    }

    @Test
    @DisplayName("Cliente null")
    void testClienteNull() throws Exception {
        List<ProductoVenta> res = r.generarSugerencias(null);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Recomendador inactivo")
    void testInactivo() {
        r.setConfiguracion(5, false);
        assertThrows(RecomendadorNoActivoException.class, () -> {
            r.generarSugerencias(new Cliente("Sacha", "PADSOF_LOVERS", "02576624A"));
        });
    }

    @Test
    @DisplayName("Sin productos")
    void testSinProductos() throws Exception {
        Cliente c = new Cliente("Sacha", "PADSOF_LOVERS", "02576624A");
        List<ProductoVenta> res = r.generarSugerencias(c);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Limite maximo")
    void testLimite() throws Exception {
        r.setConfiguracion(3, true);
        assertEquals(3, r.getLimiteMaximo());
    }

    @Test
    @DisplayName("Activar desactivar")
    void testActivo() {
        r.setConfiguracion(5, false);
        assertFalse(r.isActivo());
        r.setConfiguracion(5, true);
        assertTrue(r.isActivo());
    }

    @Test
    @DisplayName("Pesos correctos")
    void testPesos() throws Exception {
        r.setPesos(1, 1, 1);
        assertEquals(1.0/3, r.getPesoValoracion());
    }

    @Test
    @DisplayName("Pesos invalidos")
    void testPesosInvalidos() {
        assertThrows(PesosInvalidosException.class, () -> {
            r.setPesos(0, 0, 0);
        });
    }

    @Test
    @DisplayName("Getters")
    void testGetters() {
        assertNotNull(r);
        assertTrue(r.getLimiteMaximo() > 0);
    }
}