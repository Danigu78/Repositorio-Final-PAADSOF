package pruebas;

import productos.*;
import tienda.*;
import usuarios.*;

/**
 * Clase de prueba para validar el sistema de alertas y avisos de la tienda. Se
 * encarga de verificar la creación de notificaciones, su correcta recepción
 * según el tipo, el filtrado de mensajes no leídos y la gestión de preferencias
 * y categorías de interés del cliente. Prueba que se hizo antes del demostrador
 * como una especie de test para comprobar que la logica es correcta, similar a
 * los junit
 * 
 * @author Daniel
 * @version 1.0
 */
public class PruebaNotificacion {

	static int correctos = 0;
	static int fallos = 0;

	/**
	 * Evalúa una condición de prueba y actualiza los contadores globales de éxito y
	 * error. Muestra el resultado de la validación por consola.
	 *
	 * @param nombre    Descripción breve de la funcionalidad que se comprueba.
	 * @param condicion Expresión booleana que determina si la prueba es válida.
	 */
	static void check(String nombre, boolean condicion) {
		if (condicion) {
			System.out.println("\tCORRECTO -> " + nombre);
			correctos++;
		} else {
			System.out.println("\tFALLO -> " + nombre);
			fallos++;
		}
	}

	/**
	 * Método principal que ejecuta la batería de pruebas de notificaciones. Simula
	 * el envío de mensajes obligatorios y configurables, valida que las
	 * preferencias de privacidad funcionen correctamente y comprueba la suscripción
	 * a novedades por categorías.
	 *
	 * @param args Argumentos de configuración de la línea de comandos.
	 */
	public static void main(String[] args) {

		/*
		 * Montamos lo minimo necesario: un cliente y una categoria. El cliente tiene
		 * preferencias por defecto (todas las configurables activas).
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		Cliente cliente1 = new Cliente("cliente1", "Clave1@", "11111111A");
		tienda.getUsuarios().add(cliente1);

		Categoria categoria1 = new Categoria("categoria1", "desc");
		tienda.getCategorias().add(categoria1);

		System.out.println("Montaje listo.");

		/*
		 * Comprobamos la clase Notificacion directamente: creacion, estado inicial,
		 * marcarComoLeida y toString.
		 */
		System.out.println("\n============= Notificacion =============");

		Notificacion n1 = new Notificacion("mensaje1", TipoNotificacion.PEDIDO_LISTO);
		Notificacion n2 = new Notificacion("mensaje2");

		check("n1 tiene id asignado", n1.getId() != null && !n1.getId().isEmpty());
		check("n1 empieza como no leida", !n1.isLeida());
		check("n1 tiene el tipo correcto", n1.getTipo() == TipoNotificacion.PEDIDO_LISTO);
		check("n1 tiene el mensaje correcto", n1.getMensaje().equals("mensaje1"));
		check("n1 tiene fecha de envio", n1.getFechaEnvio() != null);
		check("n2 tiene tipo EMPLEADOS por defecto", n2.getTipo() == TipoNotificacion.EMPLEADOS);

		n1.marcarComoLeida();
		check("n1 queda como leida tras marcarComoLeida", n1.isLeida());

		check("toString contiene el id", n1.toString().contains(n1.getId()));
		check("toString contiene el mensaje", n1.toString().contains("mensaje1"));
		check("toString contiene el tipo", n1.toString().contains("PEDIDO_LISTO"));
		check("toString indica que esta leida", n1.toString().contains("leida"));

		// Las notificaciones se registran en el historial de la tienda
		check("n1 registrada en historial de tienda", tienda.getHistorialNotificaciones().contains(n1));

		/*
		 * Comprobamos recibirNotificacionTipo en Cliente. Las obligatorias siempre
		 * llegan, las configurables dependen de preferencias.
		 */
		System.out.println("\n============= recibirNotificacionTipo =============");

		int antes = cliente1.getNotificaciones().size();
		cliente1.recibirNotificacionTipo("mensaje3", TipoNotificacion.PAGO_EXITOSO);
		check("Notificacion obligatoria (PAGO_EXITOSO) llega siempre",
				cliente1.getNotificaciones().size() == antes + 1);

		antes = cliente1.getNotificaciones().size();
		cliente1.recibirNotificacionTipo("mensaje4", TipoNotificacion.DESCUENTO);
		check("Notificacion configurable (DESCUENTO) llega si esta activa (por defecto activa)",
				cliente1.getNotificaciones().size() == antes + 1);

		// EMPLEADOS nunca llega a un cliente
		antes = cliente1.getNotificaciones().size();
		cliente1.recibirNotificacionTipo("mensaje5", TipoNotificacion.EMPLEADOS);
		check("Notificacion tipo EMPLEADOS no llega a un cliente", cliente1.getNotificaciones().size() == antes);

		/*
		 * Comprobamos getNotificacionesNoLeidas en Cliente. Marcamos una como leida y
		 * comprobamos que el filtro funciona.
		 */
		System.out.println("\n============= getNotificacionesNoLeidas =============");

		int totalAntes = cliente1.getNotificaciones().size();
		int noLeidasAntes = cliente1.getNotificacionesNoLeidas().size();

		// Marcamos la primera notificacion como leida
		cliente1.getNotificaciones().get(0).marcarComoLeida();

		check("Despues de marcar 1 como leida, las no leidas bajan en 1",
				cliente1.getNotificacionesNoLeidas().size() == noLeidasAntes - 1);
		check("El total de notificaciones no cambia", cliente1.getNotificaciones().size() == totalAntes);

		/*
		 * Comprobamos PreferenciaNotificacion: debeRecibirNotificacion,
		 * modificarPreferencia y que las obligatorias no se pueden desactivar.
		 */
		System.out.println("\n============= PreferenciaNotificacion =============");

		PreferenciaNotificacion pref = cliente1.getPreferencias();

		// Obligatorias siempre devuelven true
		check("CODIGO_RECOGIDA es obligatoria", pref.debeRecibirNotificacion(TipoNotificacion.CODIGO_RECOGIDA));
		check("PEDIDO_LISTO es obligatoria", pref.debeRecibirNotificacion(TipoNotificacion.PEDIDO_LISTO));
		check("OFERTA_RECIBIDA es obligatoria", pref.debeRecibirNotificacion(TipoNotificacion.OFERTA_RECIBIDA));
		check("PAGO_EXITOSO es obligatoria", pref.debeRecibirNotificacion(TipoNotificacion.PAGO_EXITOSO));
		check("CARRITO_CADUCADO es obligatoria", pref.debeRecibirNotificacion(TipoNotificacion.CARRITO_CADUCADO));
		check("OFERTA_RECHAZADA es obligatoria", pref.debeRecibirNotificacion(TipoNotificacion.OFERTA_RECHAZADA));
		check("EMPLEADOS no llega a cliente", !pref.debeRecibirNotificacion(TipoNotificacion.EMPLEADOS));

		// Configurables activas por defecto
		check("DESCUENTO activo por defecto", pref.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));
		check("PEDIDO_CADUCADO activo por defecto", pref.debeRecibirNotificacion(TipoNotificacion.PEDIDO_CADUCADO));
		check("PRODUCTO_INTERCAMBIO_NUEVO activo",
				pref.debeRecibirNotificacion(TipoNotificacion.PRODUCTO_INTERCAMBIO_NUEVO));
		check("PEDIDO_ENTREGADO activo por defecto", pref.debeRecibirNotificacion(TipoNotificacion.PEDIDO_ENTREGADO));
		check("VALORACION_COMPLETADA activo", pref.debeRecibirNotificacion(TipoNotificacion.VALORACION_COMPLETADA));
		check("OFERTA_CADUCADA activo por defecto", pref.debeRecibirNotificacion(TipoNotificacion.OFERTA_CADUCADA));

		// Desactivar una configurable
		cliente1.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, false);
		check("DESCUENTO desactivado tras modificarPreferencia",
				!pref.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));

		// Verificar que ya no llega al cliente
		antes = cliente1.getNotificaciones().size();
		cliente1.recibirNotificacionTipo("mensaje6", TipoNotificacion.DESCUENTO);
		check("Con DESCUENTO desactivado la notificacion no llega", cliente1.getNotificaciones().size() == antes);

		// Reactivar
		cliente1.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, true);
		check("DESCUENTO activo de nuevo tras reactivar", pref.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));

		// Intentar desactivar una obligatoria no tiene efecto
		cliente1.configurarPreferenciaNotificacion(TipoNotificacion.PAGO_EXITOSO, false);
		check("Intentar desactivar PAGO_EXITOSO (obligatoria) no tiene efecto",
				pref.debeRecibirNotificacion(TipoNotificacion.PAGO_EXITOSO));

		/*
		 * Comprobamos categorias de interes en PreferenciaNotificacion. Añadir, recibir
		 * notificacion de categoria y eliminar.
		 */
		System.out.println("\n============= categorias de interes =============");

		check("Sin categorias de interes, notificarProductoNuevoCategoria no llega",
				!pref.NotificacionesProductosNUevosCategoriasInteres("categoria1"));

		cliente1.añadirCategoriaInteresParaRecibirInfo("categoria1");
		check("Tras añadir categoria1, notificacion de categoria1 llega",
				pref.NotificacionesProductosNUevosCategoriasInteres("categoria1"));

		// La notificacion llega al cliente
		antes = cliente1.getNotificaciones().size();
		cliente1.notificarProductoNuevoCategoria("mensaje7", "categoria1");
		check("notificarProductoNuevoCategoria llega si la categoria esta en intereses",
				cliente1.getNotificaciones().size() == antes + 1);

		// Una categoria que no esta en intereses no llega
		tienda.getCategorias().add(new Categoria("categoria2", "desc"));
		antes = cliente1.getNotificaciones().size();
		cliente1.notificarProductoNuevoCategoria("mensaje8", "categoria2");
		check("notificarProductoNuevoCategoria no llega si la categoria no esta en intereses",
				cliente1.getNotificaciones().size() == antes);

		cliente1.eliminarCategoriaInteres("categoria1");
		check("Tras eliminar categoria1 de intereses, ya no llega",
				!pref.NotificacionesProductosNUevosCategoriasInteres("categoria1"));

		// Eliminar una categoria que no existe
		check("Eliminar categoria no existente devuelve false", !cliente1.eliminarCategoriaInteres("categoria1"));

		// Añadir categoria null o vacia
		check("Añadir categoria con nombre null devuelve false", !cliente1.añadirCategoriaInteresParaRecibirInfo(null));
		check("Añadir categoria con nombre vacio devuelve false", !cliente1.añadirCategoriaInteresParaRecibirInfo(""));
		check("Añadir categoria que no existe en tienda devuelve false",
				!cliente1.añadirCategoriaInteresParaRecibirInfo("categoriaInexistente"));

		/*
		 * Comprobamos toString de PreferenciaNotificacion.
		 */
		System.out.println("\n============= toString PreferenciaNotificacion =============");

		String prefStr = pref.toString();
		check("toString contiene informacion de Descuentos", prefStr.contains("Descuentos"));
		check("toString contiene informacion de Intercambios", prefStr.contains("intercambios"));

		/*
		 * Imprimimos el resultado del test.
		 */
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}