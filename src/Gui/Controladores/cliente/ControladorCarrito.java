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
 * Controlador del subpanel del carrito.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCarrito implements ActionListener {

	/** Vista del carrito. */
	private SubpanelCarrito vista;

	/** Cliente asociado al carrito. */
	private Cliente cliente;

	/** Tienda principal del sistema. */
	private Tienda tienda;
	/**
	 * Crea el controlador del carrito.
	 *
	 * @param vista vista asociada al carrito
	 * @param cliente cliente propietario del carrito
	 */
	public ControladorCarrito(SubpanelCarrito vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
		this.tienda = Tienda.getInstancia();
	}
	/**
	 * Gestiona las acciones del carrito.
	 *
	 * @param e evento generado por la interfaz
	 */
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
	 * Confirma la reserva del carrito
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

	/**
	 * elimina un producto
	 */
	private void eliminarProductoPorId(String id) {
		for (LineaCarrito l : getLineasCarrito()) {
			if (l.getProducto().getId().equals(id)) {
				vista.mostrarConfirmacionEliminar(l.getProducto());
				return;
			}
		}
	}

	/**
	 * Elimina un producto del carrito
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

	/*
	 * Obtiene las lienas del carrito
	 */
	public List<LineaCarrito> getLineasCarrito() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return new ArrayList<>();
		return carrito.getLineas();
	}

	/*
	 * Obtiene el subtotal.
	 */
	public double getSubtotal() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		return carrito.calcularSubtotal();
	}

	/*
	 * Obtiene el total
	 */
	public double getTotal() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		return carrito.getTotal();
	}

	/*
	 * Obtiene el descuento que se ha empleado
	 */
	public String getDescuento() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null || carrito.getDescuentoAplicado() == null)
			return null;
		return carrito.getDescuentoAplicado().getNombre();
	}

	/*
	 * Obtiene el tiempo restante
	 */
	public long getMinutosRestantesCarrito() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		LocalDateTime caducidad = carrito.getFechaCreacion().plusMinutes(tienda.getTiempoMaxCarrito());
		return Math.max(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), caducidad));
	}

	/*
	 * Obtiene el carrito vacio
	 */
	public boolean carritoVacio() {
		Carrito carrito = cliente.getCarritoActual();
		return carrito == null || carrito.estaVacio();
	}

	/*
	 * Obtiene el tiempo maximo de pago.
	 */
	public int getTiempoMaxPago() {
		return tienda.getTiempoMaxPago();
	}

	/*
	 * Revisa el tiempo restante de los pedidos
	 */
	public void revisarTiempos() {
		tienda.getComprobadorTiempos().revisarCarritosCaducados();
		tienda.getComprobadorTiempos().revisarPedidosPendientesCaducados();
		revisarCarritoCaducado();
		GuardadoTienda.guardar(tienda);
	}

	/*
	 * Revisa el carrito caducado
	 */
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
