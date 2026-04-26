package Gui.Controladores;

import Gui.SubpanelCarrito;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.Carrito;
import ventas.LineaCarrito;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel del carrito. Gestiona la lógica del carrito activo:
 * obtener líneas, eliminar productos, cambiar cantidades, calcular totales y
 * reservar.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCarrito {

	/** Vista del subpanel carrito */
	private SubpanelCarrito vista;

	/** Cliente logueado */
	private Cliente cliente;

	/** Instancia de la tienda */
	private Tienda tienda;

	/**
	 * Constructor del controlador del carrito.
	 *
	 * @param vista   El subpanel carrito
	 * @param cliente El cliente logueado
	 */
	public ControladorCarrito(SubpanelCarrito vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Devuelve las líneas del carrito actual del cliente. Si no hay carrito
	 * devuelve lista vacía.
	 *
	 * @return Lista de líneas del carrito
	 */
	public List<LineaCarrito> getLineasCarrito() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return new ArrayList<>();
		return carrito.getLineas();
	}

	/**
	 * Devuelve el subtotal del carrito sin descuentos.
	 *
	 * @return Subtotal del carrito
	 */
	public double getSubtotal() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		return carrito.calcularSubtotal();
	}

	/**
	 * Devuelve el total del carrito con descuentos aplicados.
	 *
	 * @return Total del carrito
	 */
	public double getTotal() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		return carrito.getTotal();
	}

	/**
	 * Devuelve el nombre del descuento aplicado o null si no hay ninguno.
	 *
	 * @return Nombre del descuento o null
	 */
	public String getDescuento() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null || carrito.getDescuentoAplicado() == null)
			return null;
		return carrito.getDescuentoAplicado().getNombre();
	}

	/**
	 * Devuelve los minutos restantes antes de que caduque el carrito.
	 *
	 * @return Minutos restantes
	 */
	public long getMinutosRestantesCarrito() {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return 0;
		int tiempoMax = tienda.getTiempoMaxCarrito();
		LocalDateTime caducidad = carrito.getFechaCreacion().plusMinutes(tiempoMax);
		return Math.max(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), caducidad));
	}

	/**
	 * Indica si el carrito está vacío o no existe.
	 *
	 * @return true si está vacío
	 */
	public boolean carritoVacio() {
		Carrito carrito = cliente.getCarritoActual();
		return carrito == null || carrito.estaVacio();
	}

	/**
	 * Elimina un producto del carrito y actualiza la vista completa.
	 *
	 * @param producto El producto a eliminar
	 * @return true si se eliminó correctamente
	 */
	public boolean eliminarProducto(ProductoVenta producto) {
		Carrito carrito = cliente.getCarritoActual();
		if (carrito == null)
			return false;
		boolean ok = carrito.eliminarProducto(producto);
		if (ok)
			vista.actualizar(cliente);
		return ok;
	}

	/**
	 * Cambia la cantidad de un producto en el carrito y actualiza el resumen.
	 *
	 * @param producto      El producto a modificar
	 * @param nuevaCantidad La nueva cantidad
	 * @return true si se cambió correctamente
	 */
	public boolean cambiarCantidad(ProductoVenta producto, int nuevaCantidad) {
	    Carrito carrito = cliente.getCarritoActual();
	    if (carrito == null) return false;
	    boolean ok = carrito.cambiarCantidadProducto(producto, nuevaCantidad);
	    // Actualizamos todo el carrito para que se redibuje el subtotal de la tarjeta
	    if (ok) vista.actualizar(cliente);
	    return ok;
	}

	/**
	 * Reserva el carrito convirtiéndolo en pedido pendiente de pago.
	 *
	 * @return true si se reservó correctamente
	 */
	public boolean reservarCarrito() {
		return cliente.reservarCarrito();
	}
}