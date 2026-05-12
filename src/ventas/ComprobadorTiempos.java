package ventas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;

/**
 * Clase encargada de monitorizar y gestionar la caducidad de carritos y
 * pedidos.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class ComprobadorTiempos {

	/** Mapa que vincula el ID de cada usuario con su carrito de compra actual. */
	private final Map<String, Carrito> carritosPorUsuario;

	/**
	 * Mapa que registra las listas de pedidos que están esperando pago o
	 * procesamiento por usuario.
	 */
	private final Map<String, List<Pedido>> pedidosPendientesPorUsuario;

	/**
	 * Servicio encargado de ejecutar las tareas de revisión de caducidad de forma
	 * periódica en segundo plano.
	 */
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

		revisarCarritosCaducados();
		revisarPedidosPendientesCaducados();

		this.scheduler.scheduleAtFixedRate(() -> {
			revisarCarritosCaducados();
			revisarPedidosPendientesCaducados();
		}, tiempoRevision, tiempoRevision, TimeUnit.MINUTES);
	}

	/**
	 * Revisa los carritos guardados y elimina los que ya han caducado
	 */
	public void revisarCarritosCaducados() {
		boolean cambios = false;
		Iterator<Map.Entry<String, Carrito>> it = carritosPorUsuario.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Carrito> entry = it.next();
			Carrito carrito = entry.getValue();
			if (carrito == null) {
				it.remove();
				cambios = true;
				continue;
			}
			if (carrito.getPropietario() != null && carrito.getPropietario().getCarritoActual() != carrito) {
				it.remove();
				cambios = true;
				continue;
			}
			if (carrito.estaCaducado()) {
				carrito.caducar();
				it.remove();
				cambios = true;
			}
		}
		if (revisarCarritosDeClientes()) {
			cambios = true;
		}
		if (cambios) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	private boolean revisarCarritosDeClientes() {
		boolean cambios = false;
		for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {
			if (cliente == null) {
				continue;
			}
			Carrito carrito = cliente.getCarritoActual();
			if (carrito == null) {
				if (carritosPorUsuario.remove(cliente.getId()) != null) {
					cambios = true;
				}
				continue;
			}
			if (carrito.estaCaducado()) {
				carrito.caducar();
				carritosPorUsuario.remove(cliente.getId());
				cambios = true;
			} else if (carritosPorUsuario.get(cliente.getId()) != carrito) {
				carritosPorUsuario.put(cliente.getId(), carrito);
				cambios = true;
			}
		}
		return cambios;
	}

	/**
	 * Revisa los pedidos pendientes y cancela los que han caducado
	 */
	public void revisarPedidosPendientesCaducados() {
		boolean cambios = revisarPedidosRegistrados();
		if (revisarPedidosDelHistorial()) {
			cambios = true;
		}
		if (cambios) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	private boolean revisarPedidosRegistrados() {
		boolean cambios = false;
		Iterator<Map.Entry<String, List<Pedido>>> itMapa = pedidosPendientesPorUsuario.entrySet().iterator();

		while (itMapa.hasNext()) {
			Map.Entry<String, List<Pedido>> entry = itMapa.next();
			List<Pedido> pedidos = entry.getValue();

			if (pedidos == null) {
				itMapa.remove();
				cambios = true;
				continue;
			}

			Iterator<Pedido> it = pedidos.iterator();

			while (it.hasNext()) {
				Pedido pedido = it.next();

				if (pedido == null) {
					it.remove();
					cambios = true;
					continue;
				}

				if (pedido.isCaducado()) {
					pedido.cancelarPedido();
					it.remove();
					cambios = true;
				}
			}

			if (pedidos.isEmpty()) {
				itMapa.remove();
				cambios = true;
			}
		}
		return cambios;
	}

	private boolean revisarPedidosDelHistorial() {
		boolean cambios = false;
		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (pedido != null && pedido.isCaducado()) {
				if (pedido.cancelarPedido()) {
					cambios = true;
				}
			}
		}
		return cambios;
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
		if (idUsuario == null || pedido == null) {
			return;
		}

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
