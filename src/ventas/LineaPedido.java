package ventas;

import java.io.Serializable;

import productos.ProductoVenta;

/**
 * Clase que representa el detalle de un producto dentro de un pedido ya
 * realizado.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class LineaPedido implements Serializable {

	private static final long serialVersionUID = 1L;

	/** El artículo de venta que forma parte del pedido. */
	private final ProductoVenta producto;

	/** Número de unidades adquiridas del producto. */
	private final int cantidad;

	/** Precio unitario del producto fijado en el instante de la compra. */
	private final double precioVenta;

	public LineaPedido(ProductoVenta producto, int cantidad, double precioVenta) {
		this.producto = producto;
		this.cantidad = cantidad;
		this.precioVenta = precioVenta;
	}

	public double getSubtotal() {
		return this.precioVenta * cantidad;
	}

	public boolean productoPertenece(ProductoVenta p) {
		if (p != null && producto.getId().equals(p.getId())) {
			return true;
		}
		return false;
	}

	public ProductoVenta getProducto() {
		return this.producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public double getPrecioVenta() {
		return this.precioVenta;
	}
}