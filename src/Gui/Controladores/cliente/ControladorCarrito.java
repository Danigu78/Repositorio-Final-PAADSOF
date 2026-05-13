package Gui.Controladores.cliente;

import Gui.cliente.SubpanelCarrito;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.Carrito;
import ventas.LineaCarrito;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel del carrito. Implementa ActionListener según el
 * patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCarrito implements ActionListener {

	private SubpanelCarrito vista;
	private Cliente cliente;
	private Tienda tienda;

	public ControladorCarrito(SubpanelCarrito vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
		this.tienda = Tienda.getInstancia();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		revisarTiempos();
		String cmd = e.getActionCommand();
		if ("tramitar".equals(cmd)) {
			tramitar();
		} else if (cmd != null && cmd.startsWith("eliminar:")) {
			String idProducto = cmd.substring(9);
			eliminarProductoPorId(idProducto);
		} else if (cmd != null && cmd.startsWith("ver:")) {
			String idProducto = cmd.substring(4);
			vista.verProductoPorId(idProducto);
		}
	}

	/**
	 * Tramita el pedido mostrando confirmación en la vista.
	 */
	private void tramitar() {
		if (revisarCarritoCaducado()) {
			vista.actualizar(cliente);
			vista.mostrarAviso("Tu carrito ha caducado y se ha vaciado.");
			return;
		}
		if (carritoVacio()) {
			vista.mostrarAviso("Tu carrito está vacío.");
			return;
		}
		vista.mostrarConfirmacionTramitar();
	}

	/**
	 * Confirma la reserva del carrito — lo llama la vista tras confirmar.
	 */
	public void confirmarReserva() {
		boolean ok = cliente.reservarCarrito();
		if (ok) {
			vista.mostrarMensaje("Pedido reservado correctamente.\nVe a Mis Pedidos para pagarlo.");
			vista.actualizar(cliente);
		} else {
			vista.mostrarError("No se pudo reservar el pedido.");
		}
	}

	private void eliminarProductoPorId(String id) {
		for (LineaCarrito l : getLineasCarrito()) {
			if (l.getProducto().getId().equals(id)) {
				vista.mostrarConfirmacionEliminar(l.getProducto());
				return;
			}
		}
	}

	/**
	 * Elimina un producto del carrito — lo llama la vista tras confirmar.
	 */
	public boolean eliminarProducto(ProductoVenta producto) {
		if (revisarCarritoCaducado()) {
			vista.actualizar(cliente);
			return false;
		}
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return false;
		boolean ok = carrito.eliminarProducto(producto);
		if (ok)
			vista.actualizar(cliente);
		return ok;
	}

	/**
	 * Cambia la cantidad de un producto en el carrito.
	 */
	public boolean cambiarCantidad(ProductoVenta producto, int nuevaCantidad) {
		if (revisarCarritoCaducado()) {
			vista.actualizar(cliente);
			return false;
		}
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return false;
		boolean ok = carrito.cambiarCantidadProducto(producto, nuevaCantidad);
		if (ok)
			vista.actualizar(cliente);
		return ok;
	}

	public List<LineaCarrito> getLineasCarrito() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return new ArrayList<>();
		return carrito.getLineas();
	}

	public double getSubtotal() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		return carrito.calcularSubtotal();
	}

	public double getTotal() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		return carrito.getTotal();
	}

	public String getDescuento() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null || carrito.getDescuentoAplicado() == null)
			return null;
		return carrito.getDescuentoAplicado().getNombre();
	}

	public long getMinutosRestantesCarrito() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		LocalDateTime caducidad = carrito.getFechaCreacion().plusMinutes(tienda.getTiempoMaxCarrito());
		return Math.max(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), caducidad));
	}

	public boolean carritoVacio() {
		Carrito carrito = cliente.getCarritoActual();
		return carrito == null || carrito.estaVacio();
	}

	public int getTiempoMaxPago() {
		return tienda.getTiempoMaxPago();
	}

	public void revisarTiempos() {
		tienda.getComprobadorTiempos().revisarCarritosCaducados();
		tienda.getComprobadorTiempos().revisarPedidosPendientesCaducados();
		revisarCarritoCaducado();
		GuardadoTienda.guardar(tienda);
	}

	private boolean revisarCarritoCaducado() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito != null && carrito.estaCaducado()) {
			carrito.caducar();
			tienda.getComprobadorTiempos().quitarCarrito(cliente.getId());
			GuardadoTienda.guardar(tienda);
			return true;
		}
		return false;
	}
}
