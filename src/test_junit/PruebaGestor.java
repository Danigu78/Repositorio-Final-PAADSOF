package test_junit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            tienda.seleccionarCategorias("Anime_ges"), 400, "DC", 1987,
            0, 0, 0, null, null, 0, 0, 0, 0, null);
        emp.añadirProducto_nuevo("C", "Akira_ges", "Manga test", "a.jpg", 12.99, 20,
            tienda.seleccionarCategorias("Anime_ges"), 350, "Kodansha", 1982,
            0, 0, 0, null, null, 0, 0, 0, 0, null);
        emp.añadirProducto_nuevo("F", "Figura_ges", "Figura test", "f.jpg", 39.99, 5,
            tienda.seleccionarCategorias("Anime_ges"), 0, null, 0,
            20.0, 10.0, 8.0, "PVC", "Bandai", 0, 0, 0, 0, null);

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
        assertFalse(gestor.darDeAltaEmpleados_Permisos(null, "Pass@1234",
            gestor.crearListaPermisos()));
    }

    @Test
    @DisplayName("darDeAltaEmpleados_Permisos con password null devuelve false")
    void testDarDeAltaEmpleadoPasswordNull() {
        assertFalse(gestor.darDeAltaEmpleados_Permisos("emp_null_ges", null,
            gestor.crearListaPermisos()));
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
        gestor.darDeAltaEmpleados_Permisos("emp_perm_ges", "Perm@12345",
            gestor.crearListaPermisos());
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
        gestor.darDeAltaEmpleados_Permisos("emp_ret2_ges", "Ret@123456",
            gestor.crearListaPermisos());
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
        assertTrue(gestor.crearDescuentoVolumen("Desc_vol_ges", 50.0, 10.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoVolumen con precio minimo menor de 20 devuelve false")
    void testCrearDescuentoVolumenPrecioMin() {
        assertFalse(gestor.crearDescuentoVolumen("Desc_vol2_ges", 10.0, 10.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoVolumen con fechas invalidas devuelve false")
    void testCrearDescuentoVolumenFechasInvalidas() {
        assertFalse(gestor.crearDescuentoVolumen("Desc_vol3_ges", 50.0, 10.0,
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("crearDescuentoVolumen con nombre null devuelve false")
    void testCrearDescuentoVolumenNombreNull() {
        assertFalse(gestor.crearDescuentoVolumen(null, 50.0, 10.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoCategoria con datos validos devuelve true")
    void testCrearDescuentoCategoriaOk() {
        assertTrue(gestor.crearDescuentoCategoria("Desc_cat_ges", "Anime_ges", 15.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoCategoria con categoria inexistente devuelve false")
    void testCrearDescuentoCategoriaInexistente() {
        assertFalse(gestor.crearDescuentoCategoria("Desc_cat2_ges", "CategoriaFalsa", 15.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoCategoria con nombre null devuelve false")
    void testCrearDescuentoCategoriaNombreNull() {
        assertFalse(gestor.crearDescuentoCategoria(null, "Anime_ges", 15.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoCantidad con datos validos devuelve true")
    void testCrearDescuentoCantidadOk() {
        assertTrue(gestor.crearDescuentoCantidad("Desc_cant_ges", akira.getId(), 3, 20.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoCantidad con cantidad menor o igual a 1 devuelve false")
    void testCrearDescuentoCantidadMinima() {
        assertFalse(gestor.crearDescuentoCantidad("Desc_cant2_ges", akira.getId(), 1, 20.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoCantidad con producto inexistente devuelve false")
    void testCrearDescuentoCantidadProductoInexistente() {
        assertFalse(gestor.crearDescuentoCantidad("Desc_cant3_ges", "ID-FALSO", 3, 20.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoRegalo con datos validos devuelve true")
    void testCrearDescuentoRegaloOk() {
        assertTrue(gestor.crearDescuentoRegalo("Regalo_ges", figura.getId(), 40.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoRegalo con gasto menor o igual a 35 devuelve false")
    void testCrearDescuentoRegaloGastoBajo() {
        assertFalse(gestor.crearDescuentoRegalo("Regalo2_ges", figura.getId(), 35.0,
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusHours(2)));
    }

    @Test
    @DisplayName("crearDescuentoRegalo con producto inexistente devuelve false")
    void testCrearDescuentoRegaloProductoInexistente() {
        assertFalse(gestor.crearDescuentoRegalo("Regalo3_ges", "ID-FALSO", 40.0,
            LocalDateTime.now().minusMinutes(1),
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
        gestor.crearDescuentoVolumen(nombreDesc, 50.0, 10.0,
            LocalDateTime.now().minusMinutes(1),
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
    
    
    
    
    
    
}