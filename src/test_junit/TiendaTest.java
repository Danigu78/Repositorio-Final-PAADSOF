package test_junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.*;
import usuarios.*;
import ventas.*;
import tienda.*;

public class TiendaTest {

    private Tienda tienda;

    @BeforeEach
    public void setUp() {
        tienda = Tienda.getInstancia();
        tienda.vaciarTienda();
    }

    @Test
    public void testUsuarios() {
        Cliente cliente = tienda.registrarNuevoCliente("pepito", "Password123", "12345678A");
        assertNotNull(cliente);
        assertEquals(cliente, tienda.buscarClientePorNickname("pepito"));
        assertEquals(cliente, tienda.buscarCLientePorId(cliente.getId()));
        assertTrue(tienda.existeUsuarioConDNI("12345678A"));
        assertTrue(tienda.existeUsuarioConNickname("pepito"));
        Gestor gestor = tienda.getGestor();
        assertNotNull(gestor);
        assertEquals(gestor, tienda.loginGestor(gestor.getNickname(), "admin"));
        assertEquals(cliente, tienda.loginCliente("pepito", "Password123"));
    }

    @Test
    public void testSesionActiva() {
        Cliente cliente = tienda.registrarNuevoCliente("pepito", "Password123", "12345678A");
        assertTrue(tienda.getUsuariosConSesionActiva().size() >= 1);
        tienda.getUsuariosConSesionActiva().add(cliente);
        assertTrue(tienda.getUsuariosConSesionActiva().contains(cliente));
        tienda.setUsuariosConSesionActiva(new ArrayList<>());
        assertTrue(tienda.getUsuariosConSesionActiva().isEmpty());
    }

    @Test
    public void testStockYPacks() {
        Comic p1 = new Comic("Producto1", "Desc1", "img1.jpg", 10.0, 5, 100, "Editorial1", 2020);
        Comic p2 = new Comic("Producto2", "Desc2", "img2.jpg", 20.0, 5, 150, "Editorial2", 2021);
        tienda.añadirProducto(p1);
        tienda.añadirProducto(p2);
        assertEquals(2, tienda.getStockVentas().size());
        assertEquals(p1, tienda.buscarProductoVentaPorId(p1.getId()));
        assertEquals(p2, tienda.buscarproductoPorNombre("Producto2").get(0));
        Categoria cat = new Categoria("Electrónica","desc");
        p1.getCategorias().add(cat);
        tienda.setCategorias(List.of(cat));
        assertEquals(p1, tienda.buscarProductoPorCategoria("Electrónica").get(0));
        Pack pack = new Pack("Pack1", "desc", "img", 10, 10);
        tienda.getStockVentas().add(pack);
        assertEquals(pack, tienda.buscarPackPorNombre("Pack1"));
    }

    @Test
    public void testSegundaMano() {
        Producto2Mano p = new Producto2Mano("Segunda1");
        tienda.solicitarTasacion(p);
        assertTrue(tienda.getPendientesTasacion().contains(p));
        tienda.publicarParaIntercambio(p);
        assertTrue(tienda.getCatalogoIntercambio().contains(p));
        assertEquals(p, tienda.buscarSegundaManoPorId(p.getId()));
        assertTrue(tienda.buscarSegundaManoPorNombre("Segunda1").contains(p));
    }

    @Test
    public void testFiltros() {
        ProductoVenta p = new ProductoVenta("FiltroTest", 10.0, 5);
        tienda.getStockVentas().add(p);
        List<ProductoVenta> filtrados = tienda.buscarProductosFiltrados(pr -> pr.getPrecio() > 5);
        assertTrue(filtrados.contains(p));
        Producto2Mano sm = new Producto2Mano("Filtro2Mano");
        tienda.getCatalogoIntercambio().add(sm);
        List<Producto2Mano> filtrados2 = tienda.buscarSegundaManoFiltrado(pr -> pr.getNombre().contains("Filtro"));
        assertTrue(filtrados2.contains(sm));
    }

    @Test
    public void testDescuentosYCarrito() {
        Descuento d = new Descuento("Desc1", 0.1);
        tienda.agregarDescuento(d);
        assertTrue(tienda.getDescuentosActivos().contains(d));
        tienda.limpiarDescuentosCaducados();
        Carrito carrito = new Carrito();
        tienda.aplicarDescuentoPrioritario(carrito);
        carrito.setDescuentoAplicado(d);
        assertEquals(d, carrito.getDescuentoAplicado());
    }

    @Test
    public void testRecomendaciones() throws Exception {
        Cliente cliente = tienda.registrarNuevoCliente("pepito", "Password123", "12345678A");
        ProductoVenta p1 = new ProductoVenta("Producto1", 10.0, 5);
        tienda.añadirProducto(p1);
        List<ProductoVenta> sugerencias = tienda.getRecomendador().generarSugerencias(cliente);
        assertTrue(sugerencias.contains(p1));
    }

    @Test
    public void testIntercambiosFinalizados() {
        Oferta o = new Oferta();
        tienda.registrarIntercambioFinalizado(o);
        assertTrue(tienda.getIntercambiosFinalizados().contains(o));
    }

    @Test
    public void testVentasYNotificaciones() {
        Pedido pedido = new Pedido();
        tienda.registrarVenta(pedido);
        assertTrue(tienda.getHistorialVentas().contains(pedido));
        Notificacion n = new Notificacion("Test", TipoNotificacion.CLIENTES);
        tienda.registrarNotificacion(n);
        assertTrue(tienda.getHistorialNotificaciones().contains(n));
        assertTrue(tienda.getNotificacionesNoLeidas().contains(n));
        assertTrue(tienda.getNotificacionesPorTipo(TipoNotificacion.CLIENTES).contains(n));
    }

    @Test
    public void testConfiguracionTienda() {
        tienda.setNombre("NuevaTienda");
        assertEquals("NuevaTienda", tienda.getNombre());
        tienda.setTiempoMaxCarrito(10);
        tienda.setTiempoMaxOferta(5);
        tienda.setTiempoMaxPago(15);
        assertTrue(tienda.isSistemaTiemposConfigurando());
        tienda.setPrecioTasacion(20.0);
        assertEquals(20.0, tienda.getPrecioTasacion());
        tienda.reiniciarComprobadorTiempos();
        assertNotNull(tienda.getComprobadorTiempos());
    }

    @Test
    public void testSeleccionarCategorias() {
        Categoria c1 = new Categoria("Cat1");
        Categoria c2 = new Categoria("Cat2");
        tienda.setCategorias(List.of(c1, c2));
        List<Categoria> seleccion = tienda.seleccionarCategorias("Cat1", "Cat2");
        assertTrue(seleccion.contains(c1) && seleccion.contains(c2));
    }
}