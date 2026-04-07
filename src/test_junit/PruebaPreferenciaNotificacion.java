package test_junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.*;

import productos.Categoria;
import tienda.*;
import usuarios.*;

public class PruebaPreferenciaNotificacion {

    private static Tienda tienda;
    private static Gestor gestor;
    private PreferenciaNotificacion prefs;

    @BeforeEach
    void setUp() {
        tienda = Tienda.getInstancia();
        tienda.vaciarTienda();
        gestor = tienda.getGestor();
        gestor.crearCategoria("Anime_pref", "Anime y manga");
        gestor.crearCategoria("Familiar_pref", "Para toda la familia");
        prefs = new PreferenciaNotificacion();
    }

    @Test
    @DisplayName("notificaciones obligatorias siempre devuelven true")
    void testObligatoriasTrue() {
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.PAGO_EXITOSO));
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.PEDIDO_LISTO));
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.INTERCAMBIO_REALIZADO));
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.OFERTA_RECIBIDA));
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.CARRITO_CADUCADO));
    }

    @Test
    @DisplayName("EMPLEADOS siempre devuelve false para clientes")
    void testEmpleadosFalse() {
        assertFalse(prefs.debeRecibirNotificacion(TipoNotificacion.EMPLEADOS));
    }

    @Test
    @DisplayName("DESCUENTO activo devuelve true por defecto")
    void testDescuentoActivoPorDefecto() {
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));
    }

    @Test
    @DisplayName("DESCUENTO desactivado devuelve false")
    void testDescuentoDesactivado() {
        prefs.modificarPreferencia(TipoNotificacion.DESCUENTO, false);
        assertFalse(prefs.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));
    }

    @Test
    @DisplayName("PEDIDO_ENTREGADO activo por defecto devuelve true")
    void testPedidoEntregadoActivoPorDefecto() {
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.PEDIDO_ENTREGADO));
    }

    @Test
    @DisplayName("PEDIDO_ENTREGADO desactivado devuelve false")
    void testPedidoEntregadoDesactivado() {
        prefs.modificarPreferencia(TipoNotificacion.PEDIDO_ENTREGADO, false);
        assertFalse(prefs.debeRecibirNotificacion(TipoNotificacion.PEDIDO_ENTREGADO));
    }

    @Test
    @DisplayName("VALORACION_COMPLETADA activo por defecto devuelve true")
    void testValoracionActivaPorDefecto() {
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.VALORACION_COMPLETADA));
    }

    @Test
    @DisplayName("VALORACION_COMPLETADA desactivado devuelve false")
    void testValoracionDesactivada() {
        prefs.modificarPreferencia(TipoNotificacion.VALORACION_COMPLETADA, false);
        assertFalse(prefs.debeRecibirNotificacion(TipoNotificacion.VALORACION_COMPLETADA));
    }

    @Test
    @DisplayName("intentar desactivar notificacion obligatoria no la desactiva")
    void testDesactivarObligatoriaNoSurteEfecto() {
        prefs.modificarPreferencia(TipoNotificacion.PAGO_EXITOSO, false);
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.PAGO_EXITOSO));
    }


    @Test
    @DisplayName("añadirCategoriaInteres con categoria existente devuelve true")
    void testAñadirCategoriaInteresOk() {
        assertTrue(prefs.añadirCategoriaInteres("Anime_pref"));
        assertTrue(prefs.getCategoriasInteres().size() == 1);
    }

    @Test
    @DisplayName("añadirCategoriaInteres con categoria inexistente devuelve false")
    void testAñadirCategoriaInteresNoExiste() {
        assertFalse(prefs.añadirCategoriaInteres("CategoriaFalsa"));
    }

    @Test
    @DisplayName("añadirCategoriaInteres con null devuelve false")
    void testAñadirCategoriaInteresNull() {
        assertFalse(prefs.añadirCategoriaInteres(null));
    }

    @Test
    @DisplayName("añadirCategoriaInteres con vacio devuelve false")
    void testAñadirCategoriaInteresVacio() {
        assertFalse(prefs.añadirCategoriaInteres(""));
    }

    // ── eliminarCategoriaInteres ──────────────────────────────────────────────

    @Test
    @DisplayName("eliminarCategoriaInteres existente devuelve true")
    void testEliminarCategoriaInteresOk() {
        prefs.añadirCategoriaInteres("Anime_pref");
        assertTrue(prefs.eliminarCategoriaInteres("Anime_pref"));
        assertTrue(prefs.getCategoriasInteres().isEmpty());
    }

    @Test
    @DisplayName("eliminarCategoriaInteres no añadida devuelve false")
    void testEliminarCategoriaInteresNoAñadida() {
        assertFalse(prefs.eliminarCategoriaInteres("Anime_pref"));
    }

    @Test
    @DisplayName("eliminarCategoriaInteres con null devuelve false")
    void testEliminarCategoriaInteresNull() {
        assertFalse(prefs.eliminarCategoriaInteres(null));
    }

  
    

    @Test
    @DisplayName("NotificacionesProductosNuevos con categoria de interes devuelve true")
    void testNotificacionesCategoriaInteresTrue() {
        prefs.añadirCategoriaInteres("Anime_pref");
        assertTrue(prefs.NotificacionesProductosNUevosCategoriasInteres("Anime_pref"));
    }

    @Test
    @DisplayName("NotificacionesProductosNuevos con categoria no de interes devuelve false")
    void testNotificacionesCategoriaInteresNoInteres() {
        assertFalse(prefs.NotificacionesProductosNUevosCategoriasInteres("Anime_pref"));
    }

    @Test
    @DisplayName("NotificacionesProductosNuevos con lista vacia devuelve false")
    void testNotificacionesCategoriaInteresListaVacia() {
        assertFalse(prefs.NotificacionesProductosNUevosCategoriasInteres("Anime_pref"));
    }

    @Test
    @DisplayName("NotificacionesProductosNuevos con categoria inexistente devuelve false")
    void testNotificacionesCategoriaInteresInexistente() {
        assertFalse(prefs.NotificacionesProductosNUevosCategoriasInteres("CategoriaFalsa"));
    }
    @Test
    @DisplayName(" Probar Getters y Setters ")
    void testGettersSetters() {
        prefs.setDescuentos(false);
        assertFalse(prefs.isDescuentos());

        prefs.setPedidosCaducados(false);
        assertFalse(prefs.isPedidosCaducados());

        prefs.setNuevos_Intercambios(false);
        assertFalse(prefs.isNuevos_Intercambios());

        prefs.setPedido_entregado(false);
        assertFalse(prefs.isPedido_entregado());

        prefs.setValoracion_completada(false);
        assertFalse(prefs.isValoracion_completada());

        prefs.setOferta_caducada(false);
        assertFalse(prefs.isOferta_caducada());

        ArrayList<Categoria> lista = new ArrayList<>();
        prefs.setCategoriasInteres(lista);
        assertEquals(lista, prefs.getCategoriasInteres());
    }
    @Test
    @DisplayName("C Probar toString incluyendo categorías")
    void testToString() {
     
        gestor.crearCategoria("Comics", "Libros de ilustraciones");
        
       
        assertTrue(prefs.toString().contains("Ninguna"));

     
        prefs.añadirCategoriaInteres("Comics");
        String resultado = prefs.toString();
        assertTrue(resultado.contains("Comics"));
        assertTrue(resultado.contains("Activado"));
    }

    @Test
    @DisplayName(" Errores en añadir/eliminar categorías (Nulos y vacíos)")
    void testErroresCategorias() {
        
        assertFalse(prefs.añadirCategoriaInteres(null));
        assertFalse(prefs.añadirCategoriaInteres(""));
        assertFalse(prefs.añadirCategoriaInteres("Inexistente"));

       
        assertFalse(prefs.eliminarCategoriaInteres(null));
        assertFalse(prefs.eliminarCategoriaInteres("  "));
        
        // Categoría que existe en tienda pero no en mis intereses
        assertFalse(prefs.eliminarCategoriaInteres("Comics"));
    }

    @Test
    @DisplayName("Notificaciones categorías interés")
    void testNotificacionesCategorias() {
     
        gestor.crearCategoria("Comics", "Test");
        
       
        assertFalse(prefs.NotificacionesProductosNUevosCategoriasInteres("Comics"));

        prefs.añadirCategoriaInteres("Comics");
     
        assertFalse(prefs.NotificacionesProductosNUevosCategoriasInteres("NoExiste"));
        assertTrue(prefs.NotificacionesProductosNUevosCategoriasInteres("Comics"));
    }

    @Test
    @DisplayName("Modificar preferencias obligatorias vs configurables")
    void testModificarPreferencias() {
        // Configurable: debe cambiar
        prefs.modificarPreferencia(TipoNotificacion.DESCUENTO, false);
        assertFalse(prefs.isDescuentos());

      
        prefs.modificarPreferencia(TipoNotificacion.PAGO_EXITOSO, false);
        assertTrue(prefs.debeRecibirNotificacion(TipoNotificacion.PAGO_EXITOSO), 
            "La notificación obligatoria no debería haberse desactivado");
    }

}