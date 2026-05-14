package test_junit.test_junit_tienda;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tienda.*;

class NotificacionTest {

	private Notificacion notif;
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
		Estadistica.getInstancia().setnNotificaciones(1);
	}

	@Test
	@DisplayName("Constructor con tipo")
	void testConstructorTipo() {
		notif = new Notificacion("mensaje", TipoNotificacion.DESCUENTO);
		assertEquals("mensaje", notif.getMensaje());
		assertEquals(TipoNotificacion.DESCUENTO, notif.getTipo());
		assertFalse(notif.isLeida());
	}

	@Test
	@DisplayName("Constructor empleados")
	void testConstructorEmpleado() {
		notif = new Notificacion("mensaje");
		assertEquals(TipoNotificacion.EMPLEADOS, notif.getTipo());
	}

	@Test
	@DisplayName("Marcar como leida")
	void testMarcarLeida() {
		notif = new Notificacion("mensaje");
		notif.marcarComoLeida();
		assertTrue(notif.isLeida());
	}

	@Test
	@DisplayName("Set mensaje")
	void testSetMensaje() {
		notif = new Notificacion("mensaje");
		notif.setMensaje("nuevo");
		assertEquals("nuevo", notif.getMensaje());
	}

	@Test
	@DisplayName("Set leida")
	void testSetLeida() {
		notif = new Notificacion("mensaje");
		notif.setLeida(true);
		assertTrue(notif.isLeida());
	}

	@Test
	@DisplayName("Set tipo")
	void testSetTipo() {
		notif = new Notificacion("mensaje");
		notif.setTipo(TipoNotificacion.CATEGORIA_INTERES);
		assertEquals(TipoNotificacion.CATEGORIA_INTERES, notif.getTipo());
	}

	@Test
	@DisplayName("Id autoincremental")
	void testId() {
		Notificacion n1 = new Notificacion("a");
		Notificacion n2 = new Notificacion("b");
		assertNotEquals(n1.getId(), n2.getId());
	}

	@Test
	@DisplayName("ToString no null")
	void testToString() {
		notif = new Notificacion("mensaje");
		assertNotNull(notif.toString());
	}
}