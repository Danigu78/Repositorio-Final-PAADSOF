package pruebas;

import java.util.*;

import excepciones.ProductoBloqueadoException;
import excepciones.ValoracionInvalidaException;
import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;

/**
 * Clase de prueba para validar el sistema de intercambios entre clientes. Se
 * encarga de verificar el flujo completo de productos de segunda mano: desde la
 * subida a la cartera y la tasación por empleados, hasta la propuesta,
 * aceptación y ejecución física de ofertas de intercambio.
 *
 * Prueba que se hizo antes del demostrador como una especie de test para
 * comprobar que la logica es correcta, similar a los junit
 * 
 * @author Antonino
 * @version 1.0
 */
public class PruebaIntercambios {

	static int correctos = 0;
	static int fallos = 0;

	/**
	 * Valida una condición específica de la prueba y actualiza los contadores de
	 * éxito y error. Imprime el resultado detallado por consola.
	 *
	 * @param nombre    Descripción de la funcionalidad que se está verificando.
	 * @param condicion Resultado booleano esperado de la prueba.
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
	 * Método principal que coordina la batería de pruebas de intercambio. Configura
	 * el entorno con clientes y empleados, simula valoraciones de productos,
	 * gestiona el bloqueo de artículos en ofertas y confirma el traspaso de
	 * propiedad entre carteras.
	 *
	 * @param args Argumentos de configuración de la línea de comandos.
	 */
	public static void main(String[] args) {

		/*
		 * Montaje: - Gestor configura tiempos del sistema (obligatorio para
		 * proponerOferta) - Empleado tasador con permiso de valoracion y confirmacion -
		 * cliente1 y cliente2 como clientes intercambiadores - cliente3 como tercer
		 * cliente para casos de error - Cada cliente sube un producto a su cartera y lo
		 * tasa directamente (sin pasar por el pago de tasacion que requiere TeleCharge)
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		Gestor gestor = null;
		for (UsuarioRegistrado u : tienda.getUsuarios()) {
			if (u instanceof Gestor) {
				gestor = (Gestor) u;
				break;
			}
		}

		// El sistema de tiempos debe estar configurado para que proponerOferta funcione
		gestor.configurarTiemposSistema(60, 30, 30); // oferta=60min, carrito=30min, pago=30min
		gestor.setPrecioTasacion(10.0);

		List<TipoPermisos> permisosTasador = new ArrayList<>();
		permisosTasador.add(TipoPermisos.VALORACION_PRODUCTOS);
		permisosTasador.add(TipoPermisos.CONFIRMACION_INTERCAMBIO);
		gestor.darDeAltaEmpleados_Permisos("empleado1", "Clave1@", permisosTasador);
		Empleado empleado1 = tienda.obtenerEmpleadosTienda().get(0);
		empleado1.login("Clave1@");

		Cliente cliente1 = new Cliente("cliente1", "Clave2@", "11111111A");
		Cliente cliente2 = new Cliente("cliente2", "Clave3@", "22222222B");
		Cliente cliente3 = new Cliente("cliente3", "Clave4@", "33333333C");
		tienda.getUsuarios().add(cliente1);
		tienda.getUsuarios().add(cliente2);
		tienda.getUsuarios().add(cliente3);

		System.out.println("\tMontaje listo.");

		/*
		 * Comprobamos que un cliente puede subir un producto a su cartera. El producto
		 * empieza sin valorar, no visible y bloqueado.
		 */
		System.out.println("\n============= subir producto a cartera =============");

		cliente1.subirProducto("objeto1", "descripcion1", "imagen1.png");
		Producto2Mano p_cliente1 = cliente1.getCarteraIntercambio().get(0);

		check("El producto aparece en la cartera de cliente1", cliente1.getCarteraIntercambio().contains(p_cliente1));
		check("El producto empieza sin valoracion", p_cliente1.getValoracion() == null);
		check("El producto empieza no visible", !p_cliente1.isVisible());
		check("El producto empieza bloqueado", p_cliente1.isBloqueado());
		check("El propietario del producto es cliente1", p_cliente1.getPropietario().equals(cliente1));

		// Tambien sube cliente2 y cliente3
		cliente2.subirProducto("objeto2", "descripcion2", "imagen2.png");
		cliente2.subirProducto("objeto3", "descripcion3", "imagen3.png");
		Producto2Mano p_cliente2_1 = cliente2.getCarteraIntercambio().get(0);
		Producto2Mano p_cliente2_2 = cliente2.getCarteraIntercambio().get(1);
		cliente3.subirProducto("objeto4", "descripcion4", "imagen4.png");
		Producto2Mano p_cliente3 = cliente3.getCarteraIntercambio().get(0);

		/*
		 * Simulamos el flujo de tasacion sin pasar por el pago con tarjeta (que
		 * requiere TeleChargeAndPaySystem8). Llamamos directamente a p.valorar() y
		 * tienda.publicarParaIntercambio() como hace el empleado tras recibir el pago.
		 *
		 * También probamos que valorar con NO_ACEPTADO deja el producto no visible.
		 */
		System.out.println("\n============= tasacion de productos =============");

		// Primero añadimos a pendientes como si el cliente hubiera pagado
		tienda.solicitarTasacion(p_cliente1);
		tienda.solicitarTasacion(p_cliente2_1);
		tienda.solicitarTasacion(p_cliente2_2);
		tienda.solicitarTasacion(p_cliente3);

		check("p_cliente1 aparece en pendientes de tasacion", tienda.getPendientesTasacion().contains(p_cliente1));

		boolean valoradoCliente1 = p_cliente1.valorar(20.0, EstadoProducto.MUY_BUENO, empleado1);
		p_cliente2_1.valorar(15.0, EstadoProducto.PERFECTO, empleado1);
		p_cliente2_2.valorar(12.0, EstadoProducto.USO_LIGERO, empleado1);

		check("valorar devuelve true si el estado es valido", valoradoCliente1);
		check("tras valorar, el producto tiene valoracion", p_cliente1.getValoracion() != null);
		check("la valoracion tiene el precio correcto", p_cliente1.getValoracion().getPrecioTasacion() == 20.0);
		check("la valoracion tiene el estado correcto",
				p_cliente1.getValoracion().getEstadoProducto() == EstadoProducto.MUY_BUENO);
		check("la valoracion tiene el empleado correcto", p_cliente1.getValoracion().getEmpleado().equals(empleado1));

		// Publicar en catalogo
		tienda.publicarParaIntercambio(p_cliente1);
		tienda.publicarParaIntercambio(p_cliente2_1);
		tienda.publicarParaIntercambio(p_cliente2_2);

		check("tras publicar, p_cliente1 es visible", p_cliente1.isVisible());
		check("tras publicar, p_cliente1 no esta bloqueado", !p_cliente1.isBloqueado());
		check("p_cliente1 aparece en el catalogo de intercambio", tienda.getCatalogoIntercambio().contains(p_cliente1));

		boolean valoradoCliente3 = p_cliente3.valorar(0.0, EstadoProducto.NO_ACEPTADO, empleado1);
		check("valorar con NO_ACEPTADO devuelve false", !valoradoCliente3);
		check("producto NO_ACEPTADO sigue sin ser visible", !p_cliente3.isVisible());
		check("producto NO_ACEPTADO sigue bloqueado", p_cliente3.isBloqueado());

		// Error: estado null
		try {
			p_cliente3.valorar(5.0, null, empleado1);
			check("valorar con estado null lanza excepcion", false);
		} catch (ValoracionInvalidaException e) {
			check("valorar con estado null lanza excepcion", true);
		}

		// Error: empleado null
		try {
			p_cliente3.valorar(5.0, EstadoProducto.MUY_BUENO, null);
			check("valorar con empleado null lanza excepcion", false);
		} catch (ValoracionInvalidaException e) {
			check("valorar con empleado null lanza excepcion", true);
		}

		// Error: precio negativo
		try {
			p_cliente3.valorar(-1.0, EstadoProducto.MUY_BUENO, empleado1);
			check("valorar con precio negativo lanza excepcion", false);
		} catch (ValoracionInvalidaException e) {
			check("valorar con precio negativo lanza excepcion", true);
		}
		System.out.println("\n============= ver cartera de otro cliente =============");

		List<Producto2Mano> carteraCliente2Visible = cliente1.verCarteraCliente("cliente2");
		check("cliente1 ve los productos de cliente2 visibles y no bloqueados",
				carteraCliente2Visible.contains(p_cliente2_1) && carteraCliente2Visible.contains(p_cliente2_2));
		check("p_cliente3 (no visible) no aparece en ninguna cartera visible",
				!cliente1.verCarteraCliente("cliente3").contains(p_cliente3));

		// Intentar ver la propia cartera con verCarteraCliente devuelve lista vacia
		List<Producto2Mano> propiaCartera = cliente1.verCarteraCliente("cliente1");
		check("verCarteraCliente con propio nickname devuelve lista vacia", propiaCartera.isEmpty());

		// Nickname null o vacio devuelve null/vacio
		check("verCarteraCliente con nickname null devuelve null", cliente1.verCarteraCliente(null) == null);

		/*
		 * Comprobamos proponerOferta: cliente1 ofrece su objeto a cliente2 a cambio de
		 * su objeto2. Verificamos bloqueo, notificaciones y listas.
		 */
		System.out.println("\n============= proponerOferta =============");

		List<Producto2Mano> ofrecidos = new ArrayList<>(Arrays.asList(p_cliente1));
		List<Producto2Mano> solicitados = new ArrayList<>(Arrays.asList(p_cliente2_1));

		boolean ofertaCreada = cliente1.proponerOferta(cliente2, ofrecidos, solicitados);

		check("proponerOferta devuelve true", ofertaCreada);
		check("la oferta aparece en pendientes de cliente1", !cliente1.getOfertasPendientes().isEmpty());
		check("la oferta aparece en pendientes de cliente2", !cliente2.getOfertasPendientes().isEmpty());
		check("p_cliente1 queda bloqueado tras proponer oferta", p_cliente1.isBloqueado());
		check("cliente2 tiene la oferta en getOfertasParaDecidir", !cliente2.getOfertasParaDecidir().isEmpty());
		check("cliente1 tiene la oferta en getOfertasEnEspera", !cliente1.getOfertasEnEspera().isEmpty());

		Oferta oferta = cliente1.getOfertasPendientes().get(0);
		check("la oferta tiene estado PENDIENTE", oferta.getEstado() == EstadoOferta.PENDIENTE);
		check("el origen de la oferta es cliente1", oferta.getOrigen().equals(cliente1));
		check("el destino de la oferta es cliente2", oferta.getDestino().equals(cliente2));
		check("los productos ofertados son correctos", oferta.getProductosOfertados().contains(p_cliente1));
		check("los productos solicitados son correctos", oferta.getProductosSolicitados().contains(p_cliente2_1));

		/*
		 * Errores en proponerOferta.
		 */
		System.out.println("\n============= errores en proponerOferta =============");

		// No se puede hacer una oferta a uno mismo
		check("proponerOferta a si mismo devuelve false", !cliente1.proponerOferta(cliente1, ofrecidos, solicitados));

		// Producto ya bloqueado (p_cliente1 ya esta en una oferta)
		List<Producto2Mano> ofrecidosBloqueados = new ArrayList<>(Arrays.asList(p_cliente1));
		try {
			cliente1.proponerOferta(cliente2, ofrecidosBloqueados, solicitados);
			check("no se puede ofertar un producto ya bloqueado", false);
		} catch (ProductoBloqueadoException e) {
			check("no se puede ofertar un producto ya bloqueado", true);
		}

		// Destinatario null
		check("proponerOferta con destinatario null devuelve false",
				!cliente1.proponerOferta(null, ofrecidos, solicitados));

		// Lista vacia
		check("proponerOferta con lista de productos vacia devuelve false",
				!cliente1.proponerOferta(cliente2, new ArrayList<>(), solicitados));

		/*
		 * Comprobamos rechazar una oferta: creamos una segunda oferta entre cliente2 y
		 * cliente1. Tras rechazar, el producto se desbloquea y la oferta sale de
		 * pendientes.
		 */
		System.out.println("\n============= rechazar oferta =============");

		// Creamos oferta directamente para poder rechazarla
		List<Producto2Mano> ofrecidosCliente2 = new ArrayList<>(Arrays.asList(p_cliente2_2));
		List<Producto2Mano> solicitadosCliente1 = new ArrayList<>(Arrays.asList(p_cliente1)); // simulado
		Oferta ofertaParaRechazar = new Oferta(cliente2, cliente1, ofrecidosCliente2, solicitadosCliente1);
		p_cliente2_2.setBloqueado(true);
		cliente2.getOfertasPendientes().add(ofertaParaRechazar);
		cliente1.getOfertasPendientes().add(ofertaParaRechazar);

		check("ofertaParaRechazar empieza PENDIENTE", ofertaParaRechazar.getEstado() == EstadoOferta.PENDIENTE);

		ofertaParaRechazar.rechazar();

		check("tras rechazar, el estado es RECHAZADA", ofertaParaRechazar.getEstado() == EstadoOferta.RECHAZADA);
		check("tras rechazar, p_cliente2_2 se desbloquea", !p_cliente2_2.isBloqueado());
		check("tras rechazar, la oferta sale de pendientes de cliente2",
				!cliente2.getOfertasPendientes().contains(ofertaParaRechazar));
		check("tras rechazar, la oferta sale de pendientes de cliente1",
				!cliente1.getOfertasPendientes().contains(ofertaParaRechazar));

		/*
		 * Comprobamos aceptarOferta y confirmarIntercambio (empleado). Cliente2 acepta
		 * la oferta original de cliente1. El empleado la confirma. Tras
		 * aceptarYEjecutar: los productos cambian de cartera, la oferta entra en
		 * historial y sale de pendientes.
		 */
		System.out.println("\n============= aceptar y confirmar intercambio =============");

		// Cliente2 acepta la oferta de cliente1
		cliente2.confirmarIntercambio(oferta);
		check("tras confirmarIntercambio, el estado es ACEPTADA", oferta.getEstado() == EstadoOferta.ACEPTADA);

		// El empleado confirma fisicamente el intercambio
		boolean confirmado = empleado1.confirmarIntercambio(oferta);
		check("empleado confirma el intercambio correctamente", confirmado);
		check("tras confirmar, el estado es REALIZADA", oferta.getEstado() == EstadoOferta.REALIZADA);

		// Los productos salen de las carteras originales
		check("p_cliente1 sale de la cartera de cliente1", !cliente1.getCarteraIntercambio().contains(p_cliente1));
		check("p_cliente2_1 sale de la cartera de cliente2", !cliente2.getCarteraIntercambio().contains(p_cliente2_1));

		// La oferta entra en el historial de intercambios de ambos
		check("la oferta entra en el historial de cliente1", cliente1.getHistorialIntercambios().contains(oferta));
		check("la oferta entra en el historial de cliente2", cliente2.getHistorialIntercambios().contains(oferta));

		// La oferta sale de pendientes
		check("la oferta sale de pendientes de cliente1", !cliente1.getOfertasPendientes().contains(oferta));
		check("la oferta sale de pendientes de cliente2", !cliente2.getOfertasPendientes().contains(oferta));

		// La oferta se registra en intercambios finalizados de la tienda
		check("la oferta se registra en intercambiosFinalizados de la tienda",
				tienda.getIntercambiosFinalizados().contains(oferta));

		// Los productos se eliminan del catalogo de intercambio
		check("p_cliente1 sale del catalogo de intercambio", !tienda.getCatalogoIntercambio().contains(p_cliente1));
		check("p_cliente2_1 sale del catalogo de intercambio", !tienda.getCatalogoIntercambio().contains(p_cliente2_1));

		/*
		 * Comprobamos verIntercambiosCon: cliente1 puede ver los intercambios que ha
		 * tenido con cliente2 y ninguno con cliente3.
		 */
		System.out.println("\n============= verIntercambiosCon =============");

		List<Oferta> intercambiosConCliente2 = cliente1.verIntercambioscon(cliente2);
		check("cliente1 ve el intercambio con cliente2", intercambiosConCliente2.contains(oferta));
		check("cliente1 no tiene intercambios con cliente3", cliente1.verIntercambioscon(cliente3).isEmpty());
		check("verIntercambiosCon null devuelve null", cliente1.verIntercambioscon(null) == null);

		/*
		 * Comprobamos haCaducado: con tiempoMaxOferta muy alto no caduca, con
		 * tiempoMaxOferta 0 (simulado directamente) caduca.
		 */
		System.out.println("\n============= haCaducado =============");

		// Con tiempo generoso (60 min) una oferta recien creada no caduca
		List<Producto2Mano> ofrecidosCliente2_2 = new ArrayList<>(Arrays.asList(p_cliente2_2));
		List<Producto2Mano> solicitadosCliente1_2 = new ArrayList<>(Arrays.asList(p_cliente1));
		Oferta ofertaNueva = new Oferta(cliente2, cliente1, ofrecidosCliente2_2, solicitadosCliente1_2);
		check("una oferta recien creada no ha caducado (tiempoMax=60min)", !ofertaNueva.haCaducado());

		// Con tiempo 1 minuto tampoco caduca de inmediato
		tienda.setTiempoMaxOferta(1);
		check("oferta recien creada no caduca aunque el tiempo sea 1 minuto", !ofertaNueva.haCaducado());

		// Restauramos
		tienda.setTiempoMaxOferta(60);

		/*
		 * Comprobamos eliminarOfertadeOfertasPendientes en Cliente (permite al cliente
		 * retirar su propia oferta).
		 */
		System.out.println("\n============= eliminar oferta pendiente =============");

		// Preparamos una oferta fresca para poder retirarla
		p_cliente2_2.setBloqueado(false);
		tienda.publicarParaIntercambio(p_cliente2_2);
		List<Producto2Mano> ofrecidosRetiro = new ArrayList<>(Arrays.asList(p_cliente2_2));
		Oferta ofertaParaRetirar = new Oferta(cliente2, cliente1, ofrecidosRetiro, new ArrayList<>());
		p_cliente2_2.setBloqueado(true);
		cliente2.getOfertasPendientes().add(ofertaParaRetirar);
		cliente1.getOfertasPendientes().add(ofertaParaRetirar);

		boolean retirada = cliente2.eliminarOfertadeOfertasPendientes(ofertaParaRetirar);
		check("eliminarOferta devuelve true", retirada);
		check("tras retirar, el estado es RECHAZADA", ofertaParaRetirar.getEstado() == EstadoOferta.RECHAZADA);
		check("tras retirar, p_cliente2_2 se desbloquea", !p_cliente2_2.isBloqueado());

		// Error: oferta null
		check("eliminarOferta con null devuelve false", !cliente2.eliminarOfertadeOfertasPendientes(null));

		// Error: oferta que no esta en pendientes
		check("eliminarOferta que no esta en pendientes devuelve false",
				!cliente2.eliminarOfertadeOfertasPendientes(oferta)); // oferta ya esta en historial

		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}