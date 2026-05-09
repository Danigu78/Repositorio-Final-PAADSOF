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

	private SubpanelPedidos vista;
	private Cliente cliente;
	private Tienda tienda;

	public ControladorPedidos(SubpanelPedidos vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
		this.tienda = Tienda.getInstancia();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("volver")) {
			vista.mostrarLista();
		} else if (cmd.startsWith("verPedido:")) {
			buscarPedidoYEjecutar(cmd.substring(10), p -> vista.verDetallePedido(p));
		} else if (cmd.startsWith("pagar:")) {
			buscarPedidoYEjecutar(cmd.substring(6), p -> vista.irAPago(p));
		} else if (cmd.startsWith("recoger:")) {
			buscarPedidoYEjecutar(cmd.substring(8), p -> vista.mostrarDialogoRecogida(p));
		} else if (cmd.startsWith("reseña:")) {
			buscarProductoYEjecutar(cmd.substring(7), p -> vista.mostrarFormularioReseña(p));
		}
	}

	private void buscarPedidoYEjecutar(String id, Consumer<Pedido> accion) {
		for (Pedido p : cliente.getHistorialPedidos()) {
			if (p.getIdPedido().equals(id)) {
				accion.accept(p);
				return;
			}
		}
	}

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

	public List<Pedido> getPedidos() {
		List<Pedido> todos = new ArrayList<>(cliente.getHistorialPedidos());
		java.util.Collections.reverse(todos);
		return todos;
	}

	public long getMinutosRestantesPago(Pedido pedido) {
		if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO)
			return 0;
		LocalDateTime caducidad = pedido.getFechaCreacion().plusMinutes(tienda.getTiempoMaxPago());
		return Math.max(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), caducidad));
	}

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

	public boolean estaPendientePago(Pedido pedido) {
		return pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO && !pedido.isCaducado();
	}

	public boolean gestionarSolicitudRecogida(Pedido pedido, String codigo) {
		boolean exito = cliente.solicitarRecogidaPedido(codigo);
		if (exito)
			GuardadoTienda.guardar(Tienda.getInstancia());
		return exito;
	}

	
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

	public boolean yaReseñó(ProductoVenta producto) {
	    for (Reseña r : producto.getReseñas()) {
	        if (r.getAutor() != null && r.getAutor().getNickname().equals(cliente.getNickname())) {
	            return true;
	        }
	    }
	    return false;
	}

	public Cliente getCliente() {
		return cliente;
	}
}