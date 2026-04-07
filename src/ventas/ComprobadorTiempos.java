package ventas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tienda.Tienda;
import usuarios.Cliente;

/**
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class ComprobadorTiempos {

	private final Map<String, Carrito> carritosPorUsuario;
	private final Map<String, List<Pedido>> pedidosPendientesPorUsuario;
	private final ScheduledExecutorService scheduler;

	/**
	 * Constructor de la clase ComprobadorTiempos
	 */
	public ComprobadorTiempos() {
		this.carritosPorUsuario = new ConcurrentHashMap<>();
		this.pedidosPendientesPorUsuario = new ConcurrentHashMap<>();
		this.scheduler = Executors.newSingleThreadScheduledExecutor();

		iniciarRevisionPeriodica();
	}

	/**
	 * Inicia la revisión automática de carritos y pedidos pendientes
	 */
	private void iniciarRevisionPeriodica() {
		Tienda tienda = Tienda.getInstancia();

		int tiempoCarrito = tienda.getTiempoMaxCarrito();
		int tiempoOferta = tienda.getTiempoMaxOferta();

		int tiempoRevision;

		if (tiempoCarrito > 0 && tiempoOferta > 0) {
			tiempoRevision = tiempoCarrito <= tiempoOferta ? tiempoCarrito : tiempoOferta;
		} else if (tiempoCarrito > 0) {
			tiempoRevision = tiempoCarrito;
		} else if (tiempoOferta > 0) {
			tiempoRevision = tiempoOferta;
		} else {
			tiempoRevision = 5;
		}

		if (tiempoRevision < 1) {
			tiempoRevision = 1;
		}

		this.scheduler.scheduleAtFixedRate(() -> {
			revisarCarritosCaducados();
			revisarPedidosPendientesCaducados();
		}, tiempoRevision, tiempoRevision, TimeUnit.MINUTES);
	}

	/**
	 * Revisa los carritos guardados y elimina los que ya han caducado
	 */
	public void revisarCarritosCaducados() {
		Iterator<Map.Entry<String, Carrito>> it = carritosPorUsuario.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Carrito> entry = it.next();
			Carrito carrito = entry.getValue();
			if (carrito != null && carrito.estaCaducado()) {
				carrito.caducar();
				it.remove();
			}
		}
	}

	/**
	 * Revisa los pedidos pendientes y cancela los que han caducado
	 */
	public void revisarPedidosPendientesCaducados() {
		Iterator<Map.Entry<String, List<Pedido>>> itMapa = pedidosPendientesPorUsuario.entrySet().iterator();
		while (itMapa.hasNext()) {
			Map.Entry<String, List<Pedido>> entry = itMapa.next();
			List<Pedido> pedidos = entry.getValue();

			if (pedidos == null) {
				itMapa.remove();
				continue;
			}

			Iterator<Pedido> it = pedidos.iterator();
			while (it.hasNext()) {
				Pedido pedido = it.next();
				if (pedido != null && pedido.isCaducado()) {
					pedido.cancelarPedido();
					it.remove();
				}
			}

			if (pedidos.isEmpty()) {
				itMapa.remove();
			}
		}
	}

	/**
	 * Recupera el carrito asociado a un cliente
	 *
	 * @param cliente el cliente del que se quiere obtener el carrito
	 * @return el carrito del cliente o null si no existe
	 */
	public Carrito obtenerCarrito(Cliente cliente) {
		if (cliente == null) {
			return null;
		}
		return carritosPorUsuario.get(cliente.getId());
	}

	/**
	 * Devuelve el carrito asociado a un identificador de usuario
	 *
	 * @param idUsuario el id del usuario
	 * @return el carrito guardado para ese usuario
	 */
	public Carrito getCarrito(String idUsuario) {
		return carritosPorUsuario.get(idUsuario);
	}

	/**
	 * Guarda un carrito asociado a un usuario
	 *
	 * @param idUsuario el id del usuario
	 * @param carrito   el carrito que se quiere registrar
	 */
	public void registrarCarrito(String idUsuario, Carrito carrito) {
		if (idUsuario == null || carrito == null) {
			throw new IllegalArgumentException("Ni idUsuario ni carrito pueden ser null");
		}

		carritosPorUsuario.put(idUsuario, carrito);
	}

	/**
	 * Registra un pedido pendiente para un usuario
	 *
	 * @param idUsuario el id del usuario
	 * @param pedido    el pedido que se quiere guardar
	 */
	public void registrarPedido(String idUsuario, Pedido pedido) {

		List<Pedido> lista = pedidosPendientesPorUsuario.get(idUsuario);

		if (lista == null) {
			lista = new ArrayList<>();
			pedidosPendientesPorUsuario.put(idUsuario, lista);
		}

		lista.add(pedido);
	}

	/**
	 * Elimina el carrito de un usuario y lo caduca si existe
	 *
	 * @param idUsuario el id del usuario
	 */
	public void eliminarCarrito(String idUsuario) {
		Carrito carrito = carritosPorUsuario.remove(idUsuario);

		if (carrito != null) {
			carrito.caducar();
		}
	}

	/**
	 * Quita el carrito de un usuario sin aplicar ninguna otra acción
	 *
	 * @param idUsuario el id del usuario
	 */
	public void quitarCarrito(String idUsuario) {
		carritosPorUsuario.remove(idUsuario);
	}

	/**
	 * Recupera los pedidos pendientes de un usuario
	 *
	 * @param idUsuario el id del usuario
	 * @return una lista con sus pedidos pendientes
	 */
	public List<Pedido> getPedidosPendientesDeUsuario(String idUsuario) {
		List<Pedido> pedidos = pedidosPendientesPorUsuario.get(idUsuario);

		if (pedidos == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(pedidos);
	}

	/**
	 * Detiene el comprobador de tiempos
	 */
	public void cerrarGestorTiempo() {
		scheduler.shutdownNow();
	}

	/**
	 * Elimina todos los carritos guardados
	 */
	public void limpiarCarritos() {
		carritosPorUsuario.clear();
	}
}
