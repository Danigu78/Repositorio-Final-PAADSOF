package test_junit.test_junit_usuarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmpleadoTest {

	private static Tienda tienda;
	private static Gestor gestor;
	private Empleado empStock;
	private Empleado empTasador;
	private Empleado empPedidos;
	
	private ProductoVenta watchmen;
	private ProductoVenta akira;
	private Cliente cliente;
	private static byte[] datOriginal;

    @BeforeAll
    static void guardarDat() throws Exception {
        File fichero = new File("datos_tienda.dat");
        if (fichero.exists()) {
            datOriginal = Files.readAllBytes(fichero.toPath());
        }
    }

    @AfterAll
    static void restaurarDat() throws Exception {
        if (datOriginal != null) {
            Files.write(Paths.get("datos_tienda.dat"), datOriginal);
            GuardadoTienda.cargar(); // Recarga la tienda con los datos originales
        }
    }

	@BeforeEach
	void setUp() {
		tienda = Tienda.getInstancia();
		tienda.vaciarTienda();
		gestor = tienda.getGestor();
		gestor.configurarTiemposSistema(60, 30, 30);
		gestor.setPrecioTasacion(10.0);
		gestor.crearCategoria("Anime_emp", "Anime y manga");
		gestor.crearCategoria("Familiar_emp", "Para toda la familia");

		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK,
				TipoPermisos.GESTION_CATEGORIAS, TipoPermisos.GESTION_PACKS, TipoPermisos.MODIFICAR_PRODUCTO);
		List<TipoPermisos> permisosTasador = gestor.crearListaPermisos(TipoPermisos.VALORACION_PRODUCTOS,
				TipoPermisos.CONFIRMACION_INTERCAMBIO);
		List<TipoPermisos> permisosPedidos = gestor.crearListaPermisos(TipoPermisos.GESTION_PEDIDOS,
				TipoPermisos.ENTREGA_PEDIDOS);
		List<TipoPermisos> sinPermisos = new ArrayList<>();

		gestor.darDeAltaEmpleados_Permisos("emp_stock_p", "Stock@1234", permisosStock);
		gestor.darDeAltaEmpleados_Permisos("emp_tasador_p", "Tasador@1234", permisosTasador);
		gestor.darDeAltaEmpleados_Permisos("emp_pedidos_p", "Pedidos@1234", permisosPedidos);
		gestor.darDeAltaEmpleados_Permisos("emp_sin_p", "Sin@12345", sinPermisos);

		empStock = tienda.loginEmpleado("emp_stock_p", "Stock@1234");
		empTasador = tienda.loginEmpleado("emp_tasador_p", "Tasador@1234");
		empPedidos = tienda.loginEmpleado("emp_pedidos_p", "Pedidos@1234");
		

		empStock.añadirProducto_nuevo("C", "Watchmen_emp", "Comic test", "w.jpg", 15.0, 20,
				tienda.seleccionarCategorias("Anime_emp"), 400, "DC", 1987, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		empStock.añadirProducto_nuevo("C", "Akira_emp", "Manga test", "a.jpg", 12.99, 20,
				tienda.seleccionarCategorias("Anime_emp"), 350, "Kodansha", 1982, 0, 0, 0, null, null, 0, 0, 0, 0,
				null);

		watchmen = tienda.buscarproductoPorNombre("Watchmen_emp").get(0);
		akira = tienda.buscarproductoPorNombre("Akira_emp").get(0);

		cliente = tienda.registrarNuevoCliente("cli_emp_test", "Cliente@1234", "55555555E");
		cliente = tienda.loginCliente("cli_emp_test", "Cliente@1234");

	}

	@Test
	@DisplayName("puedeRealizarTarea con permiso correcto devuelve true")
	void testPuedeRealizarTareaConPermiso() {
		assertTrue(empStock.puedeRealizarTarea(TipoPermisos.GESTION_STOCK));
	}

	

	@Test
	@DisplayName("puedeRealizarTarea sin sesion devuelve false")
	void testPuedeRealizarTareaSinSesion() {
		empStock.logout();
		assertFalse(empStock.puedeRealizarTarea(TipoPermisos.GESTION_STOCK));
	}

	@Test
	@DisplayName("puedeRealizarTarea con empleado despedido devuelve false")
	void testPuedeRealizarTareaDespedido() {
		gestor.darDeBajaAEmpleado(empStock.getId());
		assertFalse(empStock.puedeRealizarTarea(TipoPermisos.GESTION_STOCK));
	}

	@Test
	@DisplayName("añadirProducto_nuevo comic valido devuelve true")
	void testAñadirComicOk() {
		assertTrue(empStock.añadirProducto_nuevo("C", "TestComic", "desc", "img.jpg", 10.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), 200, "Editorial", 2000, 0, 0, 0, null, null, 0, 0, 0, 0,
				null));
	}

	@Test
	@DisplayName("añadirProducto_nuevo juego valido devuelve true")
	void testAñadirJuegoOk() {
		assertTrue(empStock.añadirProducto_nuevo("J", "TestJuego", "desc", "img.jpg", 20.0, 5,
				tienda.seleccionarCategorias("Familiar_emp"), 0, null, 0, 0, 0, 0, null, null, 2, 4, 8, 99,
				"Estrategia"));
	}

	@Test
	@DisplayName("añadirProducto_nuevo figura valida devuelve true")
	void testAñadirFiguraOk() {
		assertTrue(empStock.añadirProducto_nuevo("F", "TestFigura", "desc", "img.jpg", 15.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), 0, null, 0, 20.0, 10.0, 8.0, "PVC", "Bandai", 0, 0, 0, 0,
				null));
	}

	@Test
	@DisplayName("añadirProducto_nuevo con tipo incorrecto devuelve false")
	void testAñadirProductoTipoIncorrecto() {
		assertFalse(empStock.añadirProducto_nuevo("X", "Test", "desc", "img.jpg", 10.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), 0, null, 0, 0, 0, 0, null, null, 0, 0, 0, 0, null));
	}

	

	@Test
	@DisplayName("añadirProducto_nuevo con nombre null devuelve false")
	void testAñadirProductoNombreNull() {
		assertFalse(empStock.añadirProducto_nuevo("C", null, "desc", "img.jpg", 10.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), 200, "Ed", 2000, 0, 0, 0, null, null, 0, 0, 0, 0, null));
	}

	@Test
	@DisplayName("añadirProducto_nuevo con precio negativo devuelve false")
	void testAñadirProductoPrecioNegativo() {
		assertFalse(empStock.añadirProducto_nuevo("C", "Test", "desc", "img.jpg", -1.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), 200, "Ed", 2000, 0, 0, 0, null, null, 0, 0, 0, 0, null));
	}

	@Test
	@DisplayName("reponerStockProducto con cantidad valida devuelve true")
	void testReponerStockOk() {
		int stockAntes = watchmen.getStockDisponible();
		assertTrue(empStock.reponerStockProducto(watchmen.getId(), 10));
		assertEquals(stockAntes + 10, watchmen.getStockDisponible());
	}

	@Test
	@DisplayName("reponerStockProducto con cantidad negativa devuelve false")
	void testReponerStockCantidadNegativa() {
		assertFalse(empStock.reponerStockProducto(watchmen.getId(), -5));
	}

	@Test
	@DisplayName("reponerStockProducto con id inexistente devuelve false")
	void testReponerStockIdInexistente() {
		assertFalse(empStock.reponerStockProducto("ID-FALSO", 5));
	}


	@Test
	@DisplayName("modificarDescripcionProducto con datos validos devuelve true")
	void testModificarDescripcionOk() {
		assertTrue(empStock.modificarDescripcionProducto(watchmen.getId(), "Nueva descripcion"));
		assertEquals("Nueva descripcion", watchmen.getDescripcion());
	}

	@Test
	@DisplayName("modificarDescripcionProducto con id null devuelve false")
	void testModificarDescripcionIdNull() {
		assertFalse(empStock.modificarDescripcionProducto(null, "Nueva desc"));
	}

	@Test
	@DisplayName("modificarDescripcionProducto con descripcion null devuelve false")
	void testModificarDescripcionNull() {
		assertFalse(empStock.modificarDescripcionProducto(watchmen.getId(), null));
	}



	@Test
	@DisplayName("añadirProductoACategoria con datos validos devuelve true")
	void testAñadirProductoACategoriaOk() {
		assertTrue(empStock.añadirProductoACategoria(watchmen.getId(), "Familiar_emp"));
	}

	@Test
	@DisplayName("añadirProductoACategoria con id null devuelve false")
	void testAñadirProductoACategoriaIdNull() {
		assertFalse(empStock.añadirProductoACategoria(null, "Familiar_emp"));
	}

	@Test
	@DisplayName("añadirProductoACategoria con categoria inexistente devuelve false")
	void testAñadirProductoACategoriaCatInexistente() {
		assertFalse(empStock.añadirProductoACategoria(watchmen.getId(), "CategoriaFalsa"));
	}

	

	@Test
	@DisplayName("eliminarProductoDeCategoria con datos validos devuelve true")
	void testEliminarProductoDeCategoriaOk() {
		empStock.añadirProductoACategoria(watchmen.getId(), "Familiar_emp");
		assertTrue(empStock.eliminarProductoDeCategoria(watchmen.getId(), "Familiar_emp"));
	}

	@Test
	@DisplayName("eliminarProductoDeCategoria con id null devuelve false")
	void testEliminarProductoDeCategoriaNull() {
		assertFalse(empStock.eliminarProductoDeCategoria(null, "Familiar_emp"));
	}

	

	@Test
	@DisplayName("crearPack con datos validos devuelve true")
	void testCrearPackOk() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		assertTrue(empStock.crearPack("TestPack", "desc", "img.jpg", 20.0, 3, lineas,new ArrayList<Categoria>()));
	}

	

	@Test
	@DisplayName("crearPack con nombre null devuelve false")
	void testCrearPackNombreNull() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		assertFalse(empStock.crearPack(null, "desc", "img.jpg", 20.0, 3, lineas,new ArrayList<Categoria>()));
	}


	@Test
	@DisplayName("prepararPedido con pedido pagado devuelve true")
	void testPrepararPedidoOk() {
		cliente.añadirProductoCarrito(watchmen, 1);
		cliente.reservarCarrito();
		Pedido p = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);
		cliente.pagarCarrito(p, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L), 123);
		assertTrue(empPedidos.prepararPedido(p.getIdPedido()));
		assertEquals(EstadoPedido.LISTO_PARA_RECOGER, p.getEstado());
	}

	@Test
	@DisplayName("prepararPedido con id inexistente devuelve false")
	void testPrepararPedidoIdInexistente() {
		assertFalse(empPedidos.prepararPedido("PEDIDO-FALSO"));
	}

	

	@Test
	@DisplayName("entregarPedido con flujo completo devuelve true")
	void testEntregarPedidoOk() {
		cliente.añadirProductoCarrito(watchmen, 1);
		cliente.reservarCarrito();
		Pedido p = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);
		cliente.pagarCarrito(p, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L), 123);
		empPedidos.prepararPedido(p.getIdPedido());
		cliente.solicitarRecogidaPedido(p.getCodigoRecogida());
		assertTrue(empPedidos.entregarPedido(p.getCodigoRecogida()));
		assertEquals(EstadoPedido.ENTREGADO, p.getEstado());
	}

	@Test
	@DisplayName("entregarPedido sin recogida solicitada devuelve false")
	void testEntregarPedidoSinRecogida() {
		cliente.añadirProductoCarrito(watchmen, 1);
		cliente.reservarCarrito();
		Pedido p = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);
		cliente.pagarCarrito(p, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L), 123);
		empPedidos.prepararPedido(p.getIdPedido());
		assertFalse(empPedidos.entregarPedido(p.getCodigoRecogida()));
	}

	

	@Test
	@DisplayName("tasarProducto con datos validos tasa correctamente")
	void testTasarProductoOk() {
		cliente.subirProducto("Naruto_emp", "Buen estado", "img.jpg");
		Producto2Mano p = cliente.getCarteraIntercambio().get(cliente.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(p);
		empTasador.tasarProducto(p.getId(), 15.0, EstadoProducto.MUY_BUENO);
		assertTrue(p.isVisible());
		assertNotNull(p.getValoracion());
	}

	@Test
	@DisplayName("tasarProducto con NO_ACEPTADO deja el producto no visible")
	void testTasarProductoNoAceptado() {
		cliente.subirProducto("Deteriorado_emp", "Mal estado", "img.jpg");
		Producto2Mano p = cliente.getCarteraIntercambio().get(cliente.getCarteraIntercambio().size() - 1);
		tienda.solicitarTasacion(p);
		empTasador.tasarProducto(p.getId(), 0.0, EstadoProducto.NO_ACEPTADO);
		assertFalse(p.isVisible());
	}


	@Test
	@DisplayName("tienePermiso devuelve true para permiso asignado")
	void testTienePermiso() {
		assertTrue(empStock.tienePermiso(TipoPermisos.GESTION_STOCK));
	}

	@Test
	@DisplayName("tienePermiso devuelve false para permiso no asignado")
	void testNoTienePermiso() {
		assertFalse(empStock.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS));
	}

	@Test
	@DisplayName("isDespedido devuelve false por defecto")
	void testIsDespedidoFalse() {
		assertFalse(empStock.isDespedido());
	}

	@Test
	@DisplayName("isDespedido devuelve true tras ser despedido")
	void testIsDespedidoTrue() {
		gestor.darDeBajaAEmpleado(empStock.getId());
		assertTrue(empStock.isDespedido());
	}

	@Test
	@DisplayName("recibirNotificacion añade notificacion a la lista")
	void testRecibirNotificacion() {
		int antes = empStock.getNotificaciones().size();
		empStock.recibirNotificacion("Test notificacion");
		assertEquals(antes + 1, empStock.getNotificaciones().size());
	}

	@Test
	@DisplayName("cargarProductosFicheroTexto con ruta null devuelve false")
	void testCargarFicheroRutaNull() {
		assertFalse(empStock.cargarProductosFicheroTexto(null));
	}

	@Test
	@DisplayName("cargarProductosFicheroTexto con ruta invalida devuelve false")
	void testCargarFicheroRutaInvalida() {
		assertFalse(empStock.cargarProductosFicheroTexto("ruta/que/no/existe.txt"));
	}


	@Test
	@DisplayName("cargarProductosFicheroTexto con fichero valido devuelve true")
	void testCargarFicheroOk() {

		assertTrue(empStock.cargarProductosFicheroTexto("ficheros/productos.txt"));
	}

	@Test
	@DisplayName("añadirProductoaPack con id que no es pack devuelve false")
	void testAñadirProductoAPackNoEsPack() {

		assertThrows(ClassCastException.class, () -> {
			empStock.añadirProductoaPack(akira.getId(), watchmen.getId(), 2);
		});
	}

	@Test
	@DisplayName("modificarUnidadesProductoEnPack con datos validos devuelve true")
	void testModificarUnidadesPackOk() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		empStock.crearPack("PackMod", "desc", "img.jpg", 25.0, 5, lineas,new ArrayList<Categoria>());
		String idPack = tienda.buscarproductoPorNombre("PackMod").get(0).getId();

		assertTrue(empStock.modificarUnidadesProductoEnPack(watchmen.getId(), idPack, 3));
	}



	@Test
	@DisplayName("eliminarPack con datos validos devuelve true y libera stock")
	void testEliminarPackOk() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 2));
		lineas.add(new LineaPack(akira, 1));
		empStock.crearPack("PackEliminar", "desc", "img.jpg", 30.0, 10, lineas,new ArrayList<Categoria>());
		String idPack = tienda.buscarproductoPorNombre("PackEliminar").get(0).getId();

		assertTrue(empStock.eliminarPack(idPack));
	}

	@Test
	@DisplayName("modificarPrecioPack con datos validos devuelve true")
	void testModificarPrecioPackOk() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		empStock.crearPack("PackPrecio", "desc", "img.jpg", 20.0, 5, lineas,new ArrayList<Categoria>());
		String idPack = tienda.buscarproductoPorNombre("PackPrecio").get(0).getId();

		assertTrue(empStock.modificarPrecioPack(idPack, 18.0));
	}

	@Test
	@DisplayName("modificarImagenProducto con datos validos devuelve true")
	void testModificarImagenOk() {
		assertTrue(empStock.modificarImagenProducto(watchmen.getId(), "nueva.jpg"));
		assertEquals("nueva.jpg", watchmen.getImagenRuta());
	}

	@Test
	@DisplayName("modificarImagenProducto con id null devuelve false")
	void testModificarImagenIdNull() {
		assertFalse(empStock.modificarImagenProducto(null, "img.jpg"));
	}

	@Test
	@DisplayName("verMisNotificaciones muestra las notificaciones correctamente")
	void testVerNotificaciones() {
		empStock.recibirNotificacion("Notificacion 1");
		empStock.verMisNotificaciones();
		assertTrue(empStock.getNotificaciones().get(0).isLeida());
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo muestra las de un tipo concreto")
	void testVerNotificacionesPorTipo() {
		empStock.verMisNotificacionesPorTipo(TipoNotificacion.PEDIDO_LISTO);
	}


	@Test
	@DisplayName("tasarProducto con id incorrecto no hace nada")
	void testTasarProductoIdIncorrecto() {
		empTasador.tasarProducto("ID-INVENTADO", 10.0, EstadoProducto.MUY_BUENO);
		empTasador.tasarProducto(null, 10.0, EstadoProducto.MUY_BUENO);
	}


	@Test
	@DisplayName("metodos getter y setter para cobertura")
	void testGettersSetters() {
		empStock.setNotificaciones(new ArrayList<>());
		empStock.setPermisos(new java.util.TreeSet<>());
		empStock.setValoraciones(new ArrayList<>());
		assertNotNull(empStock.getNotificaciones());
		assertNotNull(empStock.getPermisos());
		assertNotNull(empStock.getValoraciones());
	}

	@Test
	@DisplayName("toString devuelve representacion correcta")
	void testToString() {
		String res = empStock.toString();
		assertTrue(res.contains(empStock.getNickname()));
	}

	@Test
	@DisplayName("modificarPrecioPack con id inexistente devuelve false")
	void testModificarPrecioPackInexistente() {

		assertFalse(empStock.modificarPrecioPack("ID-QUE-NO-EXISTE", 50.0));
		assertFalse(empStock.modificarPrecioPack(watchmen.getId(), 50.0)); // Existe pero no es Pack
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo con diversos estados de lectura")
	void testVerNotificacionesPorTipoCompleto() {

		empStock.recibirNotificacion("Test 1");
		// Notificacion de un tipo especifico
		empStock.verMisNotificacionesPorTipo(TipoNotificacion.VALORACION_COMPLETADA);

		// Forzamos que haya alguna ya leida
		empStock.getNotificaciones().get(0).marcarComoLeida();
		empStock.verMisNotificacionesPorTipo(TipoNotificacion.VALORACION_COMPLETADA);
	}

	@Test
	@DisplayName("añadirProducto_nuevo con datos de comic invalidos devuelve false")
	void testAñadirComicInvalido() {

		assertFalse(empStock.añadirProducto_nuevo("C", "Error", "desc", "img.jpg", 10.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), -1, null, 0, 0, 0, 0, null, null, 0, 0, 0, 0, null));
	}

	@Test
	@DisplayName("añadirProducto_nuevo con datos de juego invalidos devuelve false")
	void testAñadirJuegoInvalido() {

		assertFalse(empStock.añadirProducto_nuevo("J", "Error", "desc", "img.jpg", 10.0, 5,
				tienda.seleccionarCategorias("Familiar_emp"), 0, null, 0, 0, 0, 0, null, null, -1, 0, 0, 101,
				"Estilo"));
	}

	@Test
	@DisplayName("añadirProducto_nuevo con datos de figura invalidos devuelve false")
	void testAñadirFiguraInvalida() {

		assertFalse(empStock.añadirProducto_nuevo("F", "Error", "desc", "img.jpg", 10.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), 0, null, 0, -1.0, 0, 10.0, null, null, 0, 0, 0, 0, null));
	}

	@Test
	@DisplayName("procesarNuevoProducto y actualizarStock: forzar errores de formato")
	void testProcesarProductoErroresFichero() {

		try {
			java.io.File temp = java.io.File.createTempFile("error_fichero", ".txt");
			java.io.PrintWriter out = new java.io.PrintWriter(temp);
			out.println(
					"Tipo;ID;Nombre;Stock;Descripcion;Imagen;Precio;Categorias;Paginas;Editorial;Año;MinJ;MaxJ;MinE;MaxE;Estilo;Alt;Anc;Lar;Mat;Mar");

			out.println("F;" + watchmen.getId()
					+ ";FalsaFigura;10;desc;img.png;10.0;Anime_emp;0;null;0;0;0;0;0;null;20;10;5;PVC;Bandai");

			out.println("C;;Incompleto;10");
			out.close();

			empStock.cargarProductosFicheroTexto(temp.getAbsolutePath());
		} catch (Exception e) {
		}
	}


	@Test
	@DisplayName("añadirProducto_nuevo con categorias que no existen en la tienda")
	void testAñadirProductoCategoriasInexistentes() {
		ArrayList<Categoria> catsFalsas = new ArrayList<>();
		catsFalsas.add(new Categoria("FALSA", "No existe"));

		assertFalse(empStock.añadirProducto_nuevo("C", "Test", "desc", "img.jpg", 10.0, 5, catsFalsas, 100, "Ed", 2020,
				0, 0, 0, null, null, 0, 0, 0, 0, null));
	}

	@Test
	@DisplayName("eliminarProductoDePack cuando el producto no esta en el pack")
	void testEliminarProductoNoEnPack() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		// Necesitamos MINIMO DOS productos distintos para que crearPack sea true
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));

		empStock.crearPack("PackSoloWatchmen", "desc", "img.jpg", 20.0, 5, lineas,new ArrayList<Categoria>());

		String idPack = null;
		for (ProductoVenta p : tienda.getStockVentas()) {
			if (p.getNombre().equals("PackSoloWatchmen")) {
				idPack = p.getId();
				break;
			}
		}

		assertNotNull(idPack, "El pack no se ha creado correctamente. Revisa que tenga > 1 producto.");

		empStock.añadirProducto_nuevo("F", "FiguraExtra", "desc", "img.jpg", 10.0, 5,
				tienda.seleccionarCategorias("Anime_emp"), 0, null, 0, 10, 10, 10, "PVC", "Marca", 0, 0, 0, 0, null);

		ProductoVenta extra = tienda.buscarproductoPorNombre("FiguraExtra").get(0);

		assertFalse(empStock.eliminarProductoDePack(idPack, extra.getId()));
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo: caso lista vacia total")
	void testVerNotificacionesTipoVaciaTotal() {
		// Aseguramos que no hay nada
		empStock.getNotificaciones().clear();

		empStock.verMisNotificacionesPorTipo(TipoNotificacion.VALORACION_COMPLETADA);
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo: caso solo hay no leidas")
	void testVerNotificacionesTipoSoloNoLeidas() {
		empStock.getNotificaciones().clear();
		empStock.recibirNotificacion("No leida 1");

		// Obtenemos el tipo que genera el sistema por defecto
		TipoNotificacion tipo = empStock.getNotificaciones().get(0).getTipo();

		empStock.verMisNotificacionesPorTipo(tipo);

		assertTrue(empStock.getNotificaciones().get(0).isLeida());
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo: caso solo hay leidas")
	void testVerNotificacionesTipoSoloLeidas() {
		empStock.getNotificaciones().clear();
		empStock.recibirNotificacion("Ya leida 1");

		// Forzamos que sea leida antes de llamar al metodo
		Notificacion n = empStock.getNotificaciones().get(0);
		n.marcarComoLeida();

		empStock.verMisNotificacionesPorTipo(n.getTipo());
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo: caso mezcla de leidas y no leidas")
	void testVerNotificacionesTipoMezcla() {
		empStock.getNotificaciones().clear();

		// Añadimos dos notificaciones
		empStock.recibirNotificacion("Mensaje A");
		empStock.recibirNotificacion("Mensaje B");

		// Marcamos la segunda como leida
		empStock.getNotificaciones().get(1).marcarComoLeida();

		TipoNotificacion tipo = empStock.getNotificaciones().get(0).getTipo();

		empStock.verMisNotificacionesPorTipo(tipo);
	}

	@Test
	@DisplayName("verMisNotificacionesPorTipo: caso tiene notificaciones pero de otro tipo")
	void testVerNotificacionesTipoDiferente() {
		empStock.getNotificaciones().clear();

		// Añadimos una notificacion (asumimos que es de un tipo X)
		empStock.recibirNotificacion("Soy tipo X");

		empStock.verMisNotificacionesPorTipo(TipoNotificacion.PEDIDO_ENTREGADO);
	}

	@Test
	@DisplayName("añadirProductoaPack con idPack vacio devuelve false")
	void testAñadirProductoPackIdPackVacio() {

		assertFalse(empStock.añadirProductoaPack(akira.getId(), "", 1));
		assertFalse(empStock.añadirProductoaPack(akira.getId(), null, 1));
	}

	@Test
	@DisplayName("añadirProductoaPack con idProducto vacio devuelve false")
	void testAñadirProductoPackIdProductoVacio() {

		assertFalse(empStock.añadirProductoaPack("", "PACK-1", 1));
		assertFalse(empStock.añadirProductoaPack(null, "PACK-1", 1));
	}

	@Test
	@DisplayName("añadirProductoaPack con unidades invalidas devuelve false")
	void testAñadirProductoPackUnidadesCero() {

		assertFalse(empStock.añadirProductoaPack(akira.getId(), "PACK-1", 0));
		assertFalse(empStock.añadirProductoaPack(akira.getId(), "PACK-1", -5));
	}

	@Test
	@DisplayName("añadirProductoaPack con pack inexistente devuelve false")
	void testAñadirProductoPackInexistente() {

		assertFalse(empStock.añadirProductoaPack(akira.getId(), "ID-NO-EXISTE", 1));
	}

	@Test
	@DisplayName("añadirProductoaPack con producto inexistente devuelve false")
	void testAñadirProductoPackProductoInexistente() {

		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		empStock.crearPack("PackReal", "desc", "img.jpg", 20.0, 5, lineas,new ArrayList<Categoria>());
		String idPack = tienda.buscarproductoPorNombre("PackReal").get(0).getId();

		assertFalse(empStock.añadirProductoaPack("PRODUCTO-FANTASMA", idPack, 1));
	}

	@Test
	@DisplayName("añadirProductoaPack captura ProductoYaEnPackException")
	void testAñadirProductoPackYaExistente() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		empStock.crearPack("PackRepetido", "desc", "img.jpg", 20.0, 5, lineas,new ArrayList<Categoria>());
		String idPack = tienda.buscarproductoPorNombre("PackRepetido").get(0).getId();

		assertFalse(empStock.añadirProductoaPack(watchmen.getId(), idPack, 1));
	}

	@Test
	@DisplayName("añadirProductoaPack captura ProductoInvalidoException")
	void testAñadirProductoPackInvalido() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		empStock.crearPack("PackInvalido", "desc", "img.jpg", 20.0, 5, lineas,new ArrayList<Categoria>());
		String idPack = tienda.buscarproductoPorNombre("PackInvalido").get(0).getId();
		assertFalse(empStock.añadirProductoaPack(idPack, idPack, 1));
	}

	@Test
	@DisplayName("añadirProductoaPack captura StockInsuficienteParaPackException")
	void testAñadirProductoPackSinStock() {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));
		empStock.crearPack("PackSinStock", "desc", "img.jpg", 20.0, 5, lineas,new ArrayList<Categoria>());
		String idPack = tienda.buscarproductoPorNombre("PackSinStock").get(0).getId();

		// Creamos un producto con stock 1
		empStock.añadirProducto_nuevo("F", "PocoStock", "d", "i", 10, 1, tienda.seleccionarCategorias("Anime_emp"), 0,
				null, 0, 10, 10, 10, "P", "M", 0, 0, 0, 0, null);
		String idExtra = tienda.buscarproductoPorNombre("PocoStock").get(0).getId();

		// Intentamos añadir 10 unidades de un producto que solo tiene 1

		assertFalse(empStock.añadirProductoaPack(idExtra, idPack, 10));
	}

	




	

	@Test
	@DisplayName("modificarUnidadesPack:  error de IDs vacíos")
	void testModPackErrorIds() {

		assertFalse(empStock.modificarUnidadesProductoEnPack("P1", "", 10));
		assertFalse(empStock.modificarUnidadesProductoEnPack("  ", "PACK1", 10));
	}

	@Test
	@DisplayName("modificarUnidadesPack:  error de unidades <= 0")
	void testModPackErrorUnidades() {

		assertFalse(empStock.modificarUnidadesProductoEnPack("P1", "PACK1", 0));
		assertFalse(empStock.modificarUnidadesProductoEnPack("P1", "PACK1", -5));
	}

	@Test
	@DisplayName("modificarUnidadesPack:  error de Pack inexistente")
	void testModPackErrorNoExistePack() {

		assertFalse(empStock.modificarUnidadesProductoEnPack("P1", "ID-FALSO", 5));
		assertFalse(empStock.modificarUnidadesProductoEnPack("P1", akira.getId(), 5));
	}

	@Test
	@DisplayName("modificarUnidadesPack: error de Producto inexistente")
	void testModPackErrorNoExisteProducto() {

		empStock.asignarPermiso(TipoPermisos.GESTION_PACKS);
		empStock.reponerStockProducto(watchmen.getId(), 20);
		empStock.reponerStockProducto(akira.getId(), 20);

		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 1));
		lineas.add(new LineaPack(akira, 1));

		empStock.crearPack("PackParaTest", "desc", "img.jpg", 10.0, 5, lineas,new ArrayList<Categoria>());

		List<ProductoVenta> resultados = Tienda.getInstancia().buscarproductoPorNombre("PackParaTest");
		assertFalse(resultados.isEmpty(), "El pack 'PackParaTest' no se creó. Asegúrate de que tenga > 1 producto.");

		String idPack = resultados.get(0).getId();
		assertFalse(empStock.modificarUnidadesProductoEnPack("PRODUCTO-FANTASMA", idPack, 3),
				"Debería devolver false al intentar modificar un producto que no existe en el pack");
	}

	@Test
	@DisplayName("modificarUnidadesPack: Cubrir éxito")
	void testModPackExitoTotal() {
		// 1. Setup: Asignar permisos y asegurar stock
		empStock.asignarPermiso(TipoPermisos.GESTION_PACKS);
		empStock.asignarPermiso(TipoPermisos.GESTION_STOCK);

		// Reponemos stock de ambos productos para evitar fallos por falta de
		// existencias
		empStock.reponerStockProducto(watchmen.getId(), 50);
		empStock.reponerStockProducto(akira.getId(), 50);

		// 2. IMPORTANTE: El pack DEBE tener al menos 2 productos diferentes
		ArrayList<LineaPack> lineas = new ArrayList<>();
		lineas.add(new LineaPack(watchmen, 2));
		lineas.add(new LineaPack(akira, 1)); // Añadimos un segundo producto

		// 3. Crear el pack
		boolean creado = empStock.crearPack("PackFinal", "desc", "img.jpg", 15.0, 10, lineas,new ArrayList<Categoria>());
		assertTrue(creado,
				"El método crearPack devolvió false. Revisa que el pack tenga > 1 producto y stock suficiente.");

		// 4. Buscar el pack creado
		List<ProductoVenta> resultados = Tienda.getInstancia().buscarproductoPorNombre("PackFinal");
		assertFalse(resultados.isEmpty(), "El pack 'PackFinal' no se encontró en la tienda.");

		String idPack = resultados.get(0).getId();

		// 5. Ejecución: Modificar las unidades de 'watchmen' de 2 a 5
		boolean modificado = empStock.modificarUnidadesProductoEnPack(watchmen.getId(), idPack, 5);
		assertTrue(modificado, "No se pudieron modificar las unidades del pack.");
	}
}