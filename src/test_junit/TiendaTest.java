package test_junit;

import org.junit.jupiter.api.*;

import intercambios.Oferta;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.*;
import usuarios.*;
import ventas.*;
import productos.*;
import tienda.*;

public class TiendaTest {

	private Tienda tienda;
	private Cliente clienteTest;
	private Cliente clienteDestino;

	@BeforeEach
	public void setup() {
		tienda = Tienda.getInstancia();
		tienda.vaciarTienda();
		// Clientes de prueba para productos de 2ª mano y carritos/intercambios
		clienteTest = tienda.registrarNuevoCliente("clienteTest", "Password1", "99999999Z");
		clienteDestino = tienda.registrarNuevoCliente("clienteDestino", "Password2", "88888888Y");
	}

	@Test
	public void testNuevoUsuarioNoRegistrado() {
		UsuarioNoRegistrado u = tienda.nuevoUsuarioNoRegistrado();
		assertNotNull(u);
	}

	@Test
	public void testExisteUsuarioConNicknameYConDNI() {
		Cliente c = tienda.registrarNuevoCliente("juan", "Password1", "12345678A");
		assertTrue(tienda.existeUsuarioConNickname("juan"));
		assertTrue(tienda.existeUsuarioConDNI("12345678A"));
		assertFalse(tienda.existeUsuarioConNickname("pepe"));
		assertFalse(tienda.existeUsuarioConDNI("87654321B"));
	}

	@Test
	public void testBuscarClientePorIdYPorNickname() {
		Cliente c = tienda.registrarNuevoCliente("maria", "Password1", "11111111A");
		assertEquals(c, tienda.buscarCLientePorId(c.getId()));
		assertEquals(c, tienda.buscarClientePorNickname("maria"));
		assertNull(tienda.buscarCLientePorId("noexiste"));
		assertNull(tienda.buscarClientePorNickname("noexiste"));
	}

	@Test
	public void testBuscarProductosVentaYPorIdNombreCategoria() {
		Figura f = new Figura("Fig1", "Desc", "img.jpg", 100.0, 5, 10.0, 5.0, 3.0, "Plástico", "Marca1");
		Categoria cat = new Categoria("Figuras", "Figuras coleccionables");
		f.getCategorias().add(cat);
		tienda.setCategorias(List.of(cat));
		tienda.añadirProducto(f);

		assertTrue(tienda.buscarProductoVenta().contains(f));
		assertEquals(f, tienda.buscarProductoVentaPorId(f.getId()));
		assertTrue(tienda.buscarproductoPorNombre("Fig1").contains(f));
		assertTrue(tienda.buscarProductoPorCategoria("Figuras").contains(f));
	}

	@Test
	public void testBuscarPackPorNombre() {
		Pack pack = new Pack("Pack1", "Pack de prueba", "imgpack.jpg", 50.0, 10);
		tienda.getStockVentas().add(pack);
		assertEquals(pack, tienda.buscarPackPorNombre("Pack1"));
		assertNull(tienda.buscarPackPorNombre("NoExiste"));
	}

	@Test
	public void testLoginUsuarios() {
		Cliente c = tienda.registrarNuevoCliente("cliente1", "Password1", "22222222B");
		Gestor g = tienda.getGestor();
		Empleado e = new Empleado("emp1", "Password1");
		tienda.getUsuarios().add(e);

		assertEquals(c, tienda.loginCliente("cliente1", "Password1"));
		assertEquals(g, tienda.loginGestor(g.getNickname(), g.getPassword()));
		assertEquals(e, tienda.loginEmpleado("emp1", "Password1"));
		assertNull(tienda.loginCliente("cliente1", "wrong"));
	}

	@Test
	public void testNotificaciones() {
		Cliente c = tienda.registrarNuevoCliente("noti", "Password1", "33333333C");
		Notificacion n = new Notificacion("Mensaje", TipoNotificacion.DESCUENTO);
		tienda.registrarNotificacion(n);
		assertTrue(tienda.getNotificacionesNoLeidas().contains(n));
		assertTrue(tienda.getNotificacionesPorTipo(TipoNotificacion.DESCUENTO).contains(n));
	}

	@Test
	public void testRegistrarIntercambioFinalizado() {
		Producto2Mano p1 = new Producto2Mano(clienteTest, "P2M1", "Desc1", "img1.jpg");
		Producto2Mano p2 = new Producto2Mano(clienteDestino, "P2M2", "Desc2", "img2.jpg");
		tienda.publicarParaIntercambio(p1);
		tienda.publicarParaIntercambio(p2);

		Oferta of = new Oferta(clienteTest, clienteDestino, List.of(p1), List.of(p2));

		tienda.registrarIntercambioFinalizado(of);
		assertFalse(tienda.getCatalogoIntercambio().contains(p1));
		assertFalse(tienda.getCatalogoIntercambio().contains(p2));
		assertTrue(tienda.getIntercambiosFinalizados().contains(of));
	}

	@Test
	public void testSolicitarTasacionYPublicarParaIntercambio() {
		Empleado e = new Empleado("Sacha", "CONTRASEÑA");
		Producto2Mano p = new Producto2Mano(clienteTest, "P2M3", "Desc3", "img3.jpg");
		tienda.solicitarTasacion(p);
		assertTrue(tienda.getPendientesTasacion().contains(p));
		Valoracion v = new Valoracion(50, EstadoProducto.MUY_BUENO, e);
		p.setValoracion(v);
		tienda.publicarParaIntercambio(p);
		assertTrue(tienda.getCatalogoIntercambio().contains(p));
	}

	@Test
	public void testAgregarYLimpiarDescuentos() {
		LocalDateTime ahora = LocalDateTime.now();
		Regalo d = new Regalo("RegaloTest", ahora.minusDays(1), ahora.plusDays(1), 50.0, null);
		
		tienda.agregarDescuento(d);
		assertTrue(tienda.getDescuentosActivos().contains(d));
	
		tienda.limpiarDescuentosCaducados();
		assertFalse(tienda.getDescuentosActivos().contains(d));
	}

	@Test
	public void testAplicarDescuentoPrioritario() {
		Carrito carrito = new Carrito(clienteTest);
		Figura f = new Figura("Fig2", "Desc", "img.jpg", 100.0, 5, 10.0, 5.0, 3.0, "Plástico", "Marca1");
		carrito.añadirProducto(f, 2);
		

		LocalDateTime ahora = LocalDateTime.now();
		Regalo r = new Regalo("RegaloPrioritario", ahora.minusDays(1), ahora.plusDays(1), 50.0, f);
		

		tienda.agregarDescuento(r);
		tienda.aplicarDescuentoPrioritario(carrito);
		assertEquals(r, carrito.getDescuentoAplicado());
	}

	@Test
	public void testRegistrarVenta() {
		Cliente c = new Cliente("Sacha", "PADSOF_HASTA_LA_MUERTE", "02576624A");
		Carrito carr = new Carrito(c);
		Pedido p = new Pedido(c, carr);
		tienda.registrarVenta(p);
		assertTrue(tienda.getHistorialVentas().contains(p));
	}

	@Test
	public void testSeleccionarCategorias() {
		Categoria c1 = new Categoria("C1", "Desc1");
		Categoria c2 = new Categoria("C2", "Desc2");
		tienda.setCategorias(List.of(c1, c2));
		List<Categoria> seleccionadas = tienda.seleccionarCategorias("C1", "C2", "NoExiste");
		assertTrue(seleccionadas.contains(c1));
		assertTrue(seleccionadas.contains(c2));
		assertEquals(2, seleccionadas.size());
	}

	@Test
	public void testHistorialProductos2Mano() {
		Producto2Mano p = new Producto2Mano(clienteTest, "HistP2M", "DescHist", "imgHist.jpg");
		tienda.solicitarTasacion(p);
		assertTrue(tienda.getHistorialProductos2Mano().contains(p));
	}

	@Test
	public void testComprobadorTiempos() {
		ComprobadorTiempos ct1 = tienda.getComprobadorTiempos();
		tienda.reiniciarComprobadorTiempos();
		ComprobadorTiempos ct2 = tienda.getComprobadorTiempos();
		assertNotNull(ct1);
		assertNotNull(ct2);
		assertNotSame(ct1, ct2);
	}
}