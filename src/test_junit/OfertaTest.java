package test_junit;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import tienda.*;
import productos.*;
import usuarios.*;
import Excepcion.*;
import intercambios.*;


public class OfertaTest {

    private Tienda tienda;
    private Cliente origen;
    private Cliente destino;

    private Producto2Mano p1;
    private Producto2Mano p2;

    @BeforeEach
    public void setup() throws Exception {
        tienda = Tienda.getInstancia();
        tienda.vaciarTienda();
        
        Empleado e = new Empleado("Sacha", "CONTRASEÑA");
       
     
        origen = tienda.registrarNuevoCliente("origen", "pass1", "11111111A");
        destino = tienda.registrarNuevoCliente("destino", "pass2", "22222222B");

       
        p1 = new Producto2Mano(origen, "Producto1", "Desc1", "img1.jpg");
        p1.valorar(50, EstadoProducto.MUY_BUENO, e);
        p2 = new Producto2Mano(destino, "Producto2", "Desc2", "img2.jpg");
        p2.valorar(31, EstadoProducto.PERFECTO, e);

        origen.getCarteraIntercambio().add(p1);
        destino.getCarteraIntercambio().add(p2);
    }

    @Test
    public void testCreacionOfertaValida() throws Exception {
        Oferta oferta = new Oferta(origen, destino, List.of(p1), List.of(p2));
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstado());
        assertEquals(origen, oferta.getOrigen());
        assertEquals(destino, oferta.getDestino());
        assertTrue(oferta.getProductosOfertados().contains(p1));
        assertTrue(oferta.getProductosSolicitados().contains(p2));
    }

    @Test
    public void testRechazarOferta() throws Exception {
        Oferta oferta = new Oferta(origen, destino, List.of(p1), List.of(p2));
        origen.getOfertasPendientes().add(oferta);
        destino.getOfertasPendientes().add(oferta);

        oferta.rechazar();
        assertEquals(EstadoOferta.RECHAZADA, oferta.getEstado());
        assertFalse(origen.getOfertasPendientes().contains(oferta));
        assertFalse(destino.getOfertasPendientes().contains(oferta));
        assertFalse(p1.isBloqueado());
    }

    @Test
    public void testAceptarOferta() throws Exception {
        Oferta oferta = new Oferta(origen, destino, List.of(p1), List.of(p2));
        oferta.aceptarOferta();
        assertEquals(EstadoOferta.ACEPTADA, oferta.getEstado());
    }

    @Test
    public void testAceptarYEjecutarOferta() throws Exception {
        Oferta oferta = new Oferta(origen, destino, List.of(p1), List.of(p2));
        origen.getOfertasPendientes().add(oferta);
        destino.getOfertasPendientes().add(oferta);

        oferta.aceptarYEjecutar();
        assertEquals(EstadoOferta.REALIZADA, oferta.getEstado());
        assertFalse(origen.getOfertasPendientes().contains(oferta));
        assertFalse(destino.getOfertasPendientes().contains(oferta));
        assertFalse(origen.getCarteraIntercambio().contains(p1));
        assertFalse(destino.getCarteraIntercambio().contains(p2));
        assertTrue(tienda.getIntercambiosFinalizados().contains(oferta));
    }

    @Test
    public void testOfertaCaducada() throws Exception {
        Oferta oferta = new Oferta(origen, destino, List.of(p1), List.of(p2));

        // Forzar caducidad
        oferta.getFechaOferta().minusMinutes(tienda.getTiempoMaxOferta() + 1);

        boolean caducada = oferta.haCaducado();
        // El estado debe cambiar a CADUCADA si estaba PENDIENTE
        if (tienda.getTiempoMaxOferta() > 0) {
            assertEquals(EstadoOferta.CADUCADA, oferta.getEstado());
            assertTrue(caducada);
        } else {
            assertFalse(caducada);
        }
    }

    @Test
    public void testExcepcionProductoNoTasado() {
        Producto2Mano pNoTasado = new Producto2Mano(origen, "SinTasacion", "Desc", "img.jpg");
        Exception ex = assertThrows(ProductoNoTasadoException.class, () -> {
            new Oferta(origen, destino, List.of(pNoTasado), List.of(p2));
        });
        assertTrue(ex.getMessage().contains("SinTasacion"));
    }
}