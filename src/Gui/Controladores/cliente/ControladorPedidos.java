package Gui.Controladores.cliente;

import Gui.cliente.SubpanelPedidos;
import productos.ProductoVenta;
import productos.Reseña;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import excepciones.*;

/**
 * Controlador del subpanel de pedidos. Implementa ActionListener según el
 * patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPedidos implements ActionListener {
	/** Vista del subpanel de pedidos. */
	private SubpanelPedidos vista;
	/** Cliente logueado. */
	private Cliente cliente;
	/** Tienda principal del sistema. */
	private Tienda tienda;

	/**
	 * Controla el perfil del cliente.
	 *
	 * @param subpanelPerfil vista del perfil
	 * @param cliente        cliente que edita su perfil
	 */
	public ControladorPedidos(SubpanelPedidos vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona las acciones de la vista.
	 *
	 * @param e evento de acción
	 * @return void
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		revisarTiempos();
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		if ("volver".equals(cmd)) {
			vista.mostrarLista();
		} else if (cmd != null && cmd.startsWith("verPedido:")) {
			buscarPedidoYEjecutar(cmd.substring(10), p -> vista.verDetallePedido(p));
		} else if (cmd != null && cmd.startsWith("verProducto")) {
			String idProducto = cmd.substring(12);
			vista.verProducto(idProducto);
		} else if (cmd != null && cmd.startsWith("pagar:")) {
			buscarPedidoYEjecutar(cmd.substring(6), p -> {
				if (estaPendientePago(p)) {
					vista.irAPago(p);
				} else {
					vista.mostrarError("Ese pedido ya no esta pendiente de pago.");
					vista.actualizar(cliente);
				}
			});
		} else if (cmd != null && cmd.startsWith("recoger:")) {
			buscarPedidoYEjecutar(cmd.substring(8), p -> {
				if (p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER) {
					vista.mostrarDialogoRecogida(p);
				} else {
					vista.mostrarError("Ese pedido no esta listo para recoger.");
					vista.actualizar(cliente);
				}
			});
		} else if (cmd.startsWith("reseña:")) {
			buscarProductoYEjecutar(cmd.substring(7), p -> vista.mostrarFormularioReseña(p));
		}
	}

	/**
	 * Revisa tiempos de carritos y pedidos.
	 *
	 * @return void
	 */
	private void revisarTiempos() {
		tienda.getComprobadorTiempos().revisarCarritosCaducados();
		tienda.getComprobadorTiempos().revisarPedidosPendientesCaducados();
		GuardadoTienda.guardar(tienda);
	}

	/**
	 * Busca un pedido por id y ejecuta una acción.
	 *
	 * @param id     identificador del pedido
	 * @param accion acción a ejecutar con el pedido encontrado
	 * @return void
	 */
	private void buscarPedidoYEjecutar(String id, Consumer<Pedido> accion) {
		for (Pedido p : cliente.getHistorialPedidos()) {
			if (p.getIdPedido().equals(id)) {
				accion.accept(p);
				return;
			}
		}
	}

	/**
	 * Busca un producto por id dentro del historial de pedidos.
	 *
	 * @param idProducto identificador del producto
	 * @param accion     acción a ejecutar con el producto encontrado
	 * @return void
	 */
	private void buscarProductoYEjecutar(String idProducto, Consumer<ProductoVenta> accion) {
		for (Pedido p : cliente.getHistorialPedidos()) {
			for (LineaPedido l : p.getLineas()) {
				if (l.getProducto().getId().equals(idProducto)) {
					accion.accept(l.getProducto());
					return;
				}
			}
		}
	}

	/**
	 * Devuelve la lista de pedidos del cliente.
	 *
	 * @return lista de pedidos
	 */
	public List<Pedido> getPedidos() {
		List<Pedido> todos = new ArrayList<>(cliente.getHistorialPedidos());
		java.util.Collections.reverse(todos);
		return todos;
	}

	/**
	 * Devuelve los minutos restantes para pagar un pedido.
	 *
	 * @param pedido pedido a consultar
	 * @return minutos restantes (0 si no aplica)
	 */
	public long getMinutosRestantesPago(Pedido pedido) {
		if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO)
			return 0;
		LocalDateTime caducidad = pedido.getFechaCreacion().plusMinutes(tienda.getTiempoMaxPago());
		return Math.max(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), caducidad));
	}

	/**
	 * Devuelve el texto del estado de un pedido.
	 *
	 * @param pedido pedido a consultar
	 * @return estado del pedido en formato texto
	 */
	public String getTextoEstado(Pedido pedido) {
		switch (pedido.getEstado()) {
		case PENDIENTE_PAGO:
			return "Pendiente de pago";
		case PAGADO:
			return "Pagado";
		case LISTO_PARA_RECOGER:
			return pedido.isRecogida_solicitada() ? "Listo (Recogida Solicitada)" : "Listo para recoger";
		case ENTREGADO:
			return "Entregado";
		case CANCELADO:
			return "Cancelado";
		default:
			return pedido.getEstado().toString();
		}
	}

	/**
	 * Indica si un pedido está pendiente de pago.
	 *
	 * @param pedido pedido a comprobar
	 * @return true si está pendiente de pago y no ha caducado
	 */
	public boolean estaPendientePago(Pedido pedido) {
		return pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO && !pedido.isCaducado();
	}

	/**
	 * Gestiona la solicitud de recogida de un pedido.
	 *
	 * @param pedido pedido afectado
	 * @param codigo código de recogida
	 * @return true si la operación fue correcta
	 */
	public boolean gestionarSolicitudRecogida(Pedido pedido, String codigo) {
		boolean exito = cliente.solicitarRecogidaPedido(codigo);
		if (exito)
			GuardadoTienda.guardar(Tienda.getInstancia());
		return exito;
	}

	/**
	 * Permite escribir una reseña de un producto.
	 *
	 * @param producto   producto a reseñar
	 * @param pts        puntuación dada
	 * @param comentario comentario de la reseña
	 * @return true si se guardó correctamente
	 */
	public boolean escribirReseña(ProductoVenta producto, int pts, String comentario) {
		try {
			boolean ok = cliente.escribirReseña(producto, pts, comentario);

			if (ok) {
				GuardadoTienda.guardar(tienda);

			}
			return ok;
		} catch (ReseñaDuplicadaException e) {
			vista.mostrarError("Ya has escrito una reseña para este producto.");
			return false;
		} catch (Exception e) {
			vista.mostrarError("Error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Indica si el cliente ya ha reseñado un producto.
	 *
	 * @param producto producto a comprobar
	 * @return true si ya existe reseña del cliente
	 */
	public boolean yaReseñó(ProductoVenta producto) {
		for (Reseña r : producto.getReseñas()) {
			if (r.getAutor() != null && r.getAutor().getNickname().equals(cliente.getNickname())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Devuelve el cliente asociado al controlador.
	 *
	 * @return cliente logueado
	 */
	public Cliente getCliente() {
		return cliente;
	}

	/**
	 * Devuelve un producto por su id.
	 *
	 * @param idProducto identificador del producto
	 * @return producto encontrado o null si no existe
	 */
	public ProductoVenta getProductoPorId(String idProducto) {
		return Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
	}
}
