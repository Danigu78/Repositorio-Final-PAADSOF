package test_junit;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import intercambios.*;
import productos.*;
import usuarios.*;
import tienda.*;
import Excepcion.*;

public class OfertaTest {
    private Cliente origen;
    private Cliente destino;
    private Empleado taser;
    private Producto2Mano pOrigen;
    private Producto2Mano pDestino;
    private List<Producto2Mano> ofertados;
    private List<Producto2Mano> solicitados;

    @BeforeEach
    void setUp() {
        Tienda.getInstancia().setTiempoMaxOferta(10);
        
        origen = new Cliente("juan77", "pass123", "12345678Z");
        destino = new Cliente("marta88", "pass456", "87654321X");
        taser = new Empleado("taser", "pass");

        pOrigen = new Producto2Mano("PS5", "Consola", "r", null, origen, false, true);
        pDestino = new Producto2Mano("IPhone", "Movil", "r", null, destino, false, true);

        pOrigen.setValoracion(new Valoracion(400.0, EstadoProducto.PERFECTO, taser));
        pDestino.setValoracion(new Valoracion(500.0, EstadoProducto.MUY_BUENO, taser));

        origen.getCarteraIntercambio().add(pOrigen);
        destino.getCarteraIntercambio().add(pDestino);

        ofertados = new ArrayList<>();
        ofertados.add(pOrigen);
        solicitados = new ArrayList<>();
        solicitados.add(pDestino);
    }

    @Test
    void testConstructorYEstadoInicial() throws ProductoNoTasadoException {
        Oferta oferta = new Oferta(origen, destino, ofertados, solicitados);
        assertNotNull(oferta.getId());
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstado());
        assertEquals(origen, oferta.getOrigen());
        assertEquals(destino, oferta.getDestino());
    }

    @Test
    void testErrorProductoNoTasado() {
        pOrigen.setValoracion(null);
        assertThrows(Exception.class, () -> {
            new Oferta(origen, destino, ofertados, solicitados);
        });
    }

    @Test
    void testAceptarYEjecutar() throws Exception {
        Oferta oferta = new Oferta(origen, destino, ofertados, solicitados);
        oferta.aceptarYEjecutar();
        
        assertEquals(EstadoOferta.REALIZADA, oferta.getEstado());
        assertFalse(origen.getCarteraIntercambio().contains(pOrigen));
        assertFalse(destino.getCarteraIntercambio().contains(pDestino));
        assertTrue(origen.getHistorialIntercambios().contains(oferta));
    }

    @Test
    void testAceptarSinEjecutar() throws Exception {
        Oferta oferta = new Oferta(origen, destino, ofertados, solicitados);
        oferta.aceptarOferta();
  
        assertEquals(EstadoOferta.ACEPTADA, oferta.getEstado());
    }

    @Test
    void testRechazar() throws Exception {
        Oferta oferta = new Oferta(origen, destino, ofertados, solicitados);
        origen.getOfertasPendientes().add(oferta);
        destino.getOfertasPendientes().add(oferta);
        
        oferta.rechazar();
        
        assertEquals(EstadoOferta.RECHAZADA, oferta.getEstado());
        assertFalse(origen.getOfertasPendientes().contains(oferta));
    }

    

    @Test
    void testCaducidad() throws ProductoNoTasadoException {
        Tienda.getInstancia().setTiempoMaxOferta(-1);
        Oferta oferta = new Oferta(origen, destino, ofertados, solicitados);
        
        assertTrue(oferta.haCaducado());
        assertEquals(EstadoOferta.CADUCADA, oferta.getEstado());
    }

    @Test
    void testGettersListas() throws ProductoNoTasadoException {
        Oferta oferta = new Oferta(origen, destino, ofertados, solicitados);
        assertEquals(1, oferta.getProductosOfertados().size());
        assertEquals(1, oferta.getProductosSolicitados().size());
        assertTrue(oferta.getProductosOfertados().contains(pOrigen));
    }

    @Test
    void testFechaCreacion() throws ProductoNoTasadoException {
        Oferta oferta = new Oferta(origen, destino, ofertados, solicitados);
        assertNotNull(oferta.getFechaOferta());
        assertTrue(oferta.getFechaOferta().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}