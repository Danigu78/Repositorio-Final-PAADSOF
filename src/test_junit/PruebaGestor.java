package test_junit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Excepcion.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PruebaGestor {

	private static Tienda tienda;
	private Gestor gestor;
	private ProductoVenta watchmen;
	private ProductoVenta akira;
	private ProductoVenta figura;

	@BeforeEach
	void setUp() {
		tienda = Tienda.getInstancia();
		gestor = tienda.getGestor();
		gestor.configurarTiemposSistema(60, 30, 30);
		gestor.setPrecioTasacion(10.0);
		gestor.crearCategoria("Anime_ges", "Anime y manga");
		gestor.crearCategoria("Familiar_ges", "Para toda la familia");

		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK);
		gestor.darDeAltaEmpleados_Permisos("emp_ges_test", "Stock@1234", permisosStock);
		Empleado emp = tienda.loginEmpleado("emp_ges_test", "Stock@1234");

		emp.añadirProducto_nuevo("C", "Watchmen_ges", "Comic test", "w.jpg", 15.0, 20,
				tienda.seleccionarCategorias("Anime_ges"), 400, "DC", 1987, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		emp.añadirProducto_nuevo("C", "Akira_ges", "Manga test", "a.jpg", 12.99, 20,
				tienda.seleccionarCategorias("Anime_ges"), 350, "Kodansha", 1982, 0, 0, 0, null, null, 0, 0, 0, 0,
				null);
		emp.añadirProducto_nuevo("F", "Figura_ges", "Figura test", "f.jpg", 39.99, 5,
				tienda.seleccionarCategorias("Anime_ges"), 0, null, 0, 20.0, 10.0, 8.0, "PVC", "Bandai", 0, 0, 0, 0,
				null);

		watchmen = tienda.buscarproductoPorNombre("Watchmen_ges").get(0);
		akira = tienda.buscarproductoPorNombre("Akira_ges").get(0);
		figura = tienda.buscarproductoPorNombre("Figura_ges").get(0);
	}

	@Test
	@DisplayName("darDeAltaEmpleados_Permisos con datos validos devuelve true")
	void testDarDeAltaEmpleadoOk() {
		assertTrue(gestor.darDeAltaEmpleados_Permisos("nuevo_emp_ges", "Nuevo@1234",
				gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK)));
	}

	@Test
	@DisplayName("darDeAltaEmpleados_Permisos con nickname duplicado devuelve false")
	void testDarDeAltaEmpleadoNicknameDuplicado() {
		gestor.darDeAltaEmpleados_Permisos("emp_dup_ges", "Emp@12345",
				gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK));
		assertFalse(gestor.darDeAltaEmpleados_Permisos("emp_dup_ges", "Emp@12345",
				gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK)));
	}

	@Test
	@DisplayName("darDeAltaEmpleados_Permisos con nickname null devuelve false")
	void testDarDeAltaEmpleadoNicknameNull() {
		assertFalse(gestor.darDeAltaEmpleados_Permisos(null, "Pass@1234", gestor.crearListaPermisos()));
	}

	@Test
	@DisplayName("darDeAltaEmpleados_Permisos con password null devuelve false")
	void testDarDeAltaEmpleadoPasswordNull() {
		assertFalse(gestor.darDeAltaEmpleados_Permisos("emp_null_ges", null, gestor.crearListaPermisos()));
	}

	@Test
	@DisplayName("darDeAltaEmpleados: Error por parámetros null")
	void testDarDeAltaEmpleadosNull() {

		assertFalse(gestor.darDeAltaEmpleados(null, "Pass@1234"));
		assertFalse(gestor.darDeAltaEmpleados("nuevo_emp", null));
	}

	@Test
	@DisplayName("darDeAltaEmpleados: Error por nickname ya en uso")
	void testDarDeAltaEmpleadosNicknameDuplicado() {

		assertFalse(gestor.darDeAltaEmpleados("emp_ges_test", "Otra@1234"));
	}

	@Test
	@DisplayName("darDeAltaEmpleados: Éxito en la creación")
	void testDarDeAltaEmpleadosOk() {

		boolean resultado = gestor.darDeAltaEmpleados("empleado_nuevo_sin_permisos", "Nuevo@1234");

		assertTrue(resultado, "El empleado debería haberse creado correctamente");
		assertTrue(Tienda.getInstancia().existeUsuarioConNickname("empleado_nuevo_sin_permisos"));
	}

	@Test
	@DisplayName("darDeBajaAEmpleado con id valido devuelve true")
	void testDarDeBajaEmpleadoOk() {
		gestor.darDeAltaEmpleados_Permisos("emp_baja_ges", "Baja@12345",
				gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK));
		Empleado e = tienda.loginEmpleado("emp_baja_ges", "Baja@12345");
		assertTrue(gestor.darDeBajaAEmpleado(e.getId()));
		assertTrue(e.isDespedido());
		assertFalse(e.isSesionIniciada());
	}

	@Test
	@DisplayName("darDeBajaAEmpleado con id null devuelve false")
	void testDarDeBajaEmpleadoIdNull() {
		assertFalse(gestor.darDeBajaAEmpleado(null));
	}

	@Test
	@DisplayName("darDeBajaAEmpleado con id inexistente devuelve false")
	void testDarDeBajaEmpleadoIdInexistente() {
		assertFalse(gestor.darDeBajaAEmpleado("ID-FALSO"));
	}

	@Test
	@DisplayName("asignarPermiso con datos validos devuelve true")
	void testAsignarPermisoOk() {
		gestor.darDeAltaEmpleados_Permisos("emp_perm_ges", "Perm@12345", gestor.crearListaPermisos());
		Empleado e = tienda.loginEmpleado("emp_perm_ges", "Perm@12345");
		assertTrue(gestor.asignarPermiso(e.getId(), TipoPermisos.GESTION_STOCK));
		assertTrue(e.tienePermiso(TipoPermisos.GESTION_STOCK));
	}

	@Test
	@DisplayName("asignarPermiso ya existente devuelve false")
	void testAsignarPermisoYaExiste() {
		gestor.darDeAltaEmpleados_Permisos("emp_perm2_ges", "Perm@12345",
				gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK));
		Empleado e = tienda.loginEmpleado("emp_perm2_ges", "Perm@12345");
		assertFalse(gestor.asignarPermiso(e.getId(), TipoPermisos.GESTION_STOCK));
	}

	@Test
	@DisplayName("retirarPermiso existente devuelve true")
	void testRetirarPermisoOk() {
		gestor.darDeAltaEmpleados_Permisos("emp_ret_ges", "Ret@123456",
				gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK));
		Empleado e = tienda.loginEmpleado("emp_ret_ges", "Ret@123456");
		assertTrue(gestor.retirarPermiso(e.getId(), TipoPermisos.GESTION_STOCK));
		assertFalse(e.tienePermiso(TipoPermisos.GESTION_STOCK));
	}

	@Test
	@DisplayName("retirarPermiso no existente devuelve false")
	void testRetirarPermisoNoExiste() {
		gestor.darDeAltaEmpleados_Permisos("emp_ret2_ges", "Ret@123456", gestor.crearListaPermisos());
		Empleado e = tienda.loginEmpleado("emp_ret2_ges", "Ret@123456");
		assertFalse(gestor.retirarPermiso(e.getId(), TipoPermisos.GESTION_STOCK));
	}

	@Test
	@DisplayName("configurarTiemposSistema con valores validos devuelve true")
	void testConfigurarTiemposOk() {
		assertTrue(gestor.configurarTiemposSistema(60, 30, 30));
		assertEquals(60, tienda.getTiempoMaxOferta());
		assertEquals(30, tienda.getTiempoMaxCarrito());
		assertEquals(30, tienda.getTiempoMaxPago());
	}

	@Test
	@DisplayName("configurarTiemposSistema con tiempo negativo devuelve false")
	void testConfigurarTiemposNegativo() {
		assertFalse(gestor.configurarTiemposSistema(-1, 30, 30));
	}

	@Test
	@DisplayName("configurarTiemposSistema con tiempo cero devuelve false")
	void testConfigurarTiemposCero() {
		assertFalse(gestor.configurarTiemposSistema(0, 30, 30));
	}

	@Test
	@DisplayName("setPrecioTasacion con precio valido devuelve true")
	void testSetPrecioTasacionOk() {
		assertTrue(gestor.setPrecioTasacion(15.0));
		assertEquals(15.0, tienda.getPrecioTasacion());
	}

	@Test
	@DisplayName("setPrecioTasacion con precio menor o igual a 5 devuelve false")
	void testSetPrecioTasacionMenorA5() {
		assertFalse(gestor.setPrecioTasacion(5.0));
	}

	@Test
	@DisplayName("setPrecioTasacion con precio negativo devuelve false")
	void testSetPrecioTasacionNegativo() {
		assertFalse(gestor.setPrecioTasacion(-10.0));
	}

	@Test
	@DisplayName("crearCategoria con datos validos devuelve true")
	void testCrearCategoriaOk() {
		assertTrue(gestor.crearCategoria("TestCat_ges", "desc"));
	}

	@Test
	@DisplayName("crearCategoria con nombre null devuelve false")
	void testCrearCategoriaNombreNull() {
		assertFalse(gestor.crearCategoria(null, "desc"));
	}

	@Test
	@DisplayName("crearCategoria con descripcion null devuelve false")
	void testCrearCategoriaDescNull() {
		assertFalse(gestor.crearCategoria("TestCat2_ges", null));
	}

	@Test
	@DisplayName("crearCategoria con nombre vacio devuelve false")
	void testCrearCategoriaNombreVacio() {
		assertFalse(gestor.crearCategoria("", "desc"));
	}

	@Test
	@DisplayName("crearDescuentoVolumen con datos validos devuelve true")
	void testCrearDescuentoVolumenOk() {
		assertTrue(gestor.crearDescuentoVolumen("Desc_vol_ges", 50.0, 10.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoVolumen con precio minimo menor de 20 devuelve false")
	void testCrearDescuentoVolumenPrecioMin() {
		assertFalse(gestor.crearDescuentoVolumen("Desc_vol2_ges", 10.0, 10.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoVolumen con fechas invalidas devuelve false")
	void testCrearDescuentoVolumenFechasInvalidas() {
		assertFalse(gestor.crearDescuentoVolumen("Desc_vol3_ges", 50.0, 10.0, LocalDateTime.now().plusHours(2),
				LocalDateTime.now().minusMinutes(1)));
	}

	@Test
	@DisplayName("crearDescuentoVolumen con nombre null devuelve false")
	void testCrearDescuentoVolumenNombreNull() {
		assertFalse(gestor.crearDescuentoVolumen(null, 50.0, 10.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoCategoria con datos validos devuelve true")
	void testCrearDescuentoCategoriaOk() {
		assertTrue(gestor.crearDescuentoCategoria("Desc_cat_ges", "Anime_ges", 15.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoCategoria con categoria inexistente devuelve false")
	void testCrearDescuentoCategoriaInexistente() {
		assertFalse(gestor.crearDescuentoCategoria("Desc_cat2_ges", "CategoriaFalsa", 15.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoCategoria con nombre null devuelve false")
	void testCrearDescuentoCategoriaNombreNull() {
		assertFalse(gestor.crearDescuentoCategoria(null, "Anime_ges", 15.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoCantidad con datos validos devuelve true")
	void testCrearDescuentoCantidadOk() {
		assertTrue(gestor.crearDescuentoCantidad("Desc_cant_ges", akira.getId(), 3, 20.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoCantidad con cantidad menor o igual a 1 devuelve false")
	void testCrearDescuentoCantidadMinima() {
		assertFalse(gestor.crearDescuentoCantidad("Desc_cant2_ges", akira.getId(), 1, 20.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoCantidad con producto inexistente devuelve false")
	void testCrearDescuentoCantidadProductoInexistente() {
		assertFalse(gestor.crearDescuentoCantidad("Desc_cant3_ges", "ID-FALSO", 3, 20.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoRegalo con datos validos devuelve true")
	void testCrearDescuentoRegaloOk() {
		assertTrue(gestor.crearDescuentoRegalo("Regalo_ges", figura.getId(), 40.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoRegalo con gasto menor o igual a 35 devuelve false")
	void testCrearDescuentoRegaloGastoBajo() {
		assertFalse(gestor.crearDescuentoRegalo("Regalo2_ges", figura.getId(), 35.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("crearDescuentoRegalo con producto inexistente devuelve false")
	void testCrearDescuentoRegaloProductoInexistente() {
		assertFalse(gestor.crearDescuentoRegalo("Regalo3_ges", "ID-FALSO", 40.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2)));
	}

	@Test
	@DisplayName("eliminarDescuento: Error por ID nulo o vacío")
	void testEliminarDescuentoNullVacio() {

		assertFalse(gestor.eliminarDescuento(null));
		assertFalse(gestor.eliminarDescuento("   "));
	}

	@Test
	@DisplayName("eliminarDescuento: Descuento no encontrado")
	void testEliminarDescuentoNoExiste() {

		assertFalse(gestor.eliminarDescuento("ID-NO-EXISTE-123"));
	}

	@Test
	@DisplayName("eliminarDescuento: Éxito al eliminar")
	void testEliminarDescuentoOk() {

		String nombreDesc = "Desc_Para_Borrar";
		gestor.crearDescuentoVolumen(nombreDesc, 50.0, 10.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2));

		Descuento d = Tienda.getInstancia().getDescuentosActivos().get(0);
		String idDesc = d.getId();
		assertTrue(gestor.eliminarDescuento(idDesc));

	}

	@Test
	@DisplayName("modificarPrecioProducto con precio valido devuelve true")
	void testModificarPrecioOk() {
		assertTrue(gestor.modificarPrecioProducto(watchmen.getId(), 20.0));
		assertEquals(20.0, watchmen.getPrecioOficial());
	}

	@Test
	@DisplayName("modificarPrecioProducto con precio negativo devuelve false")
	void testModificarPrecioNegativo() {
		assertFalse(gestor.modificarPrecioProducto(watchmen.getId(), -5.0));
	}

	@Test
	@DisplayName("modificarPrecioProducto con id null devuelve false")
	void testModificarPrecioIdNull() {
		assertFalse(gestor.modificarPrecioProducto(null, 20.0));
	}

	@Test
	@DisplayName("modificarPrecioProducto con id inexistente devuelve false")
	void testModificarPrecioIdInexistente() {
		assertFalse(gestor.modificarPrecioProducto("ID-FALSO", 20.0));
	}

	@Test
	@DisplayName("modificarPerfil con datos validos devuelve true")
	void testModificarPerfilOk() {
		assertTrue(gestor.modificarPerfil("admin_Gestor", "Admin@5678"));
	}

	@Test
	@DisplayName("modificarPerfil con nickname null devuelve false")
	void testModificarPerfilNicknameNull() {
		assertFalse(gestor.modificarPerfil(null, "Admin@1234"));
	}

	@Test
	@DisplayName("modificarPerfil con password insegura devuelve false")
	void testModificarPerfilPasswordInsegura() {
		assertFalse(gestor.modificarPerfil("admin_Gestor", "hola"));
	}

	@Test
	@DisplayName("añadirProductoACategoria con parámetros null devuelve false")
	void testAñadirProductoACategoriaNull() {
		assertFalse(gestor.añadirProductoACategoria(null, "Anime_ges"));
		assertFalse(gestor.añadirProductoACategoria(watchmen.getId(), null));
	}

	@Test
	@DisplayName("añadirProductoACategoria con ID de producto inexistente devuelve false")
	void testAñadirProductoACategoriaProductoInexistente() {
		assertFalse(gestor.añadirProductoACategoria("ID-FANTASMA", "Anime_ges"));
	}

	@Test
	@DisplayName("añadirProductoACategoria con categoría inexistente devuelve false")
	void testAñadirProductoACategoriaCategoriaInexistente() {
		assertFalse(gestor.añadirProductoACategoria(watchmen.getId(), "CategoriaInventada"));
	}

	@Test
	@DisplayName("añadirProductoACategoria captura ProductoYaEnCategoriaException")
	void testAñadirProductoACategoriaYaExiste() {

		assertFalse(gestor.añadirProductoACategoria(watchmen.getId(), "Anime_ges"));
	}

	@Test
	@DisplayName("añadirProductoACategoria éxito total")
	void testAñadirProductoACategoriaOk() {

		assertTrue(gestor.añadirProductoACategoria(figura.getId(), "Familiar_ges"));

		// Verificamos que la categoría ahora contiene el producto
		Categoria cat = Tienda.getInstancia().buscarCategoriaPorNombre("Familiar_ges");
		assertTrue(cat.getProductos().contains(figura));
	}

	@Test
	@DisplayName("eliminarProductoDeCategoria: Error por parámetros null")
	void testEliminarProductoDeCategoriaNull() {

		assertFalse(gestor.eliminarProductoDeCategoria(null, "Anime_ges"));
		assertFalse(gestor.eliminarProductoDeCategoria(watchmen.getId(), null));
	}

	@Test
	@DisplayName("eliminarProductoDeCategoria: Error producto inexistente")
	void testEliminarProductoDeCategoriaProductoInexistente() {
		assertFalse(gestor.eliminarProductoDeCategoria("ID-FANTASMA-99", "Anime_ges"));
	}

	@Test
	@DisplayName("eliminarProductoDeCategoria: Error categoría inexistente")
	void testEliminarProductoDeCategoriaCategoriaInexistente() {
		assertFalse(gestor.eliminarProductoDeCategoria(watchmen.getId(), "CategoriaNoExiste"));
	}

	@Test
	@DisplayName("eliminarProductoDeCategoria: Éxito al eliminar")
	void testEliminarProductoDeCategoriaOk() {

		boolean eliminado = gestor.eliminarProductoDeCategoria(watchmen.getId(), "Anime_ges");

		assertTrue(eliminado, "El producto debería haberse eliminado de la categoría");
		Categoria cat = Tienda.getInstancia().buscarCategoriaPorNombre("Anime_ges");
		assertFalse(cat.getProductos().contains(watchmen), "La categoría ya no debería tener el producto");
	}

	@Test
	@DisplayName("consultarIngresosPorMeses: Éxito con año válido")
	void testConsultarIngresosPorMesesOk() {
		// Al ser un año actual o pasado válido, no debe lanzar excepciones
		assertDoesNotThrow(() -> {
			double[] ingresos = gestor.consultarIngresosPorMeses(2024);
			assertNotNull(ingresos);
			assertEquals(12, ingresos.length, "Debe devolver un array de 12 posiciones (una por mes)");
		});
	}

	@Test
	@DisplayName("setTiempoMaxCarrito: Error con tiempo negativo o cero")
	void testSetTiempoMaxCarritoInvalido() {

		assertFalse(gestor.setTiempoMaxCarrito(-5), "No debería permitir tiempos negativos");
		assertFalse(gestor.setTiempoMaxCarrito(0), "No debería permitir tiempo cero");
	}

	@Test
	@DisplayName("setTiempoMaxCarrito: Éxito al actualizar")
	void testSetTiempoMaxCarritoOk() {

		int nuevoTiempo = 45;

		boolean resultado = gestor.setTiempoMaxCarrito(nuevoTiempo);
		assertTrue(resultado, "El tiempo debería haberse actualizado correctamente");
		assertEquals(nuevoTiempo, Tienda.getInstancia().getTiempoMaxCarrito(),
				"El tiempo en la Tienda no coincide con el asignado");
	}

	@Test
	@DisplayName("setTiempoMaxPago: Validación y actualización")
	void testSetTiempoMaxPago() {

		assertFalse(gestor.setTiempoMaxPago(0));
		assertFalse(gestor.setTiempoMaxPago(-10));

		assertTrue(gestor.setTiempoMaxPago(20));
		assertEquals(20, Tienda.getInstancia().getTiempoMaxPago());
	}

	@Test
	@DisplayName("setTiempoMaxOferta: Validación y actualización")
	void testSetTiempoMaxOferta() {

		assertFalse(gestor.setTiempoMaxOferta(0));
		assertFalse(gestor.setTiempoMaxOferta(-1));

		assertTrue(gestor.setTiempoMaxOferta(120));
		assertEquals(120, Tienda.getInstancia().getTiempoMaxOferta());
	}
	

	@Test
	@DisplayName("consultarIngresosPorMeses: Año inválido lanza AñoInvalidoException")
	void testConsultarIngresosPorMesesAñoInvalido() {
		// El motorEstadistico lanza AñoInvalidoException si año <= 0
		assertThrows(AñoInvalidoException.class, () -> {
			gestor.consultarIngresosPorMeses(0);
		});

		assertThrows(AñoInvalidoException.class, () -> {
			gestor.consultarIngresosPorMeses(-2024);
		});
	}

	@Test
	@DisplayName("calcularIngresosMesesAñoActual: Éxito en el año actual")
	void testCalcularIngresosAñoActual() {

		assertDoesNotThrow(() -> {
			double[] ingresos = gestor.consultarIngresosPorMesesActual();

			assertNotNull(ingresos, "El array de ingresos no debería ser null");
			assertEquals(12, ingresos.length, "Debe tener 12 posiciones, una por cada mes del año");
		});
	}

	@Test
	@DisplayName("consultarIngresosRango: Lanza RangoFechasInvalidoException si fin < inicio")
	void testConsultarIngresosRangoInvalido() {
		LocalDate hoy = LocalDate.now();
		LocalDate ayer = hoy.minusDays(1);
		assertThrows(RangoFechasInvalidoException.class, () -> {
			gestor.consultarIngresosRango(hoy, ayer);
		});

		assertThrows(RangoFechasInvalidoException.class, () -> {
			gestor.consultarIngresosRango(null, hoy);
		});
	}

	@Test
	@DisplayName("consultarIngresosRango: Éxito con rango válido")
	void testConsultarIngresosRangoOk() {
		LocalDate inicio = LocalDate.now().minusDays(7);
		LocalDate fin = LocalDate.now();

		assertDoesNotThrow(() -> {
			double total = gestor.consultarIngresosRango(inicio, fin);

			assertTrue(total >= 0, "Los ingresos no pueden ser negativos");
		});
	}

	@Test
	@DisplayName("consultarIngresosRango: Mismo día es un rango válido")
	void testConsultarIngresosRangoMismoDia() {
		LocalDate hoy = LocalDate.now();

		assertDoesNotThrow(() -> {
			gestor.consultarIngresosRango(hoy, hoy);
		});
	}

	@Test
	@DisplayName("consultarIngresosTasacion: Flujo completo con login de cliente")
	void testConsultarIngresosTasacionOk() {

		tienda.registrarNuevoCliente("user_tas", "User@1234", "99999999Z");

		Cliente clienteTest = tienda.loginCliente("user_tas", "User@1234");

		List<TipoPermisos> permisosTas = gestor.crearListaPermisos(TipoPermisos.VALORACION_PRODUCTOS);
		gestor.darDeAltaEmpleados_Permisos("tasador_ges", "Tasar@1234", permisosTas);
		Empleado tasador = tienda.loginEmpleado("tasador_ges", "Tasar@1234");
		clienteTest.subirProducto("Comic Caro", "Nuevo", "foto.jpg");
		Producto2Mano p2m = clienteTest.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(p2m);
		tasador.tasarProducto(p2m.getId(), 10.0, EstadoProducto.MUY_BUENO);
		double ingresos = gestor.consultarIngresosTasacion();
		assertTrue(ingresos >= 10.0, "Los ingresos de tasación deberían haber sumado los 10€");
		clienteTest.logout();
		tasador.logout();
	}

	@Test
	@DisplayName("consultarIngresosVenta: Cubrir la función")
	void testConsultarIngresosVentaOk() {

		double totalVentas = gestor.consultarIngresosVenta();
		assertTrue(totalVentas >= 0);
	}
	@Test
    @DisplayName("verClientesConMasPedidosCancelados: Tienda sin clientes")
    void testClientesCanceladosVacio() {
        // 1. Limpiamos los clientes de la tienda para forzar la lista vacía
        Tienda.getInstancia().getUsuarios().clear();
        
        // 2. Ejecución
        List<Cliente> resultado = gestor.verClientesConMasPedidosCancelados();
        
        // 3. Verificación (Cubre el return new ArrayList<>())
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
/*
	@Test
	@DisplayName("verClientesConMasPedidosCancelados: Ordenación correcta con flujo real")
	void testClientesCanceladosOrden() {
	    // 1. SETUP DE CLIENTES
	    tienda.registrarNuevoCliente("malo", "Pass@1234", "11111111M");
	    Cliente malo = tienda.loginCliente("malo", "Pass@1234");
	    
	    tienda.registrarNuevoCliente("bueno", "Pass@1234", "22222222B");
	    Cliente bueno = tienda.loginCliente("bueno", "Pass@1234");

	    // 2. ESCENARIO CLIENTE "MALO" (2 Pedidos Cancelados)
	    // Pedido 1
	    malo.añadirAlCarrito(watchmen, 1);
	    malo.tramitarPedido(); // Esto crea el objeto Pedido y lo mete en su historial
	    // Cancelamos el último pedido creado usando el método oficial de la clase Pedido
	    malo.getHistorialPedidos().get(malo.getHistorialPedidos().size() - 1).cancelarPedido();

	    // Pedido 2
	    malo.añadirAlCarrito(akira, 1);
	    malo.tramitarPedido();
	    malo.getHistorialPedidos().get(malo.getHistorialPedidos().size() - 1).cancelarPedido();

	    // 3. ESCENARIO CLIENTE "BUENO" (1 Pedido Cancelado)
	    bueno.añadirAlCarrito(figura, 1);
	    bueno.tramitarPedido();
	    bueno.getHistorialPedidos().get(bueno.getHistorialPedidos().size() - 1).cancelarPedido();

	    // 4. EJECUCIÓN
	    List<Cliente> ranking = gestor.verClientesConMasPedidosCancelados();

	    // 5. VERIFICACIONES
	    assertNotNull(ranking);
	    assertFalse(ranking.isEmpty());
	    
	    // El primero debe ser 'malo' (2 cancelados) y el segundo 'bueno' (1 cancelado)
	    assertEquals("malo", ranking.get(0).getNickname(), "El cliente con más cancelaciones debe ir primero");
	    assertEquals("bueno", ranking.get(1).getNickname(), "El cliente con menos cancelaciones debe ir después");
	}*/
}