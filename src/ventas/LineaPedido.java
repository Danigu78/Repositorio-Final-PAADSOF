package ventas;

import productos.ProductoVenta;

/**
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class LineaPedido {
	private final ProductoVenta producto;
	private final int cantidad;
	private final double precioVenta; // Precio en el momento de la compra

	/**
	 * Constructor de la clase LineaPedido
	 *
	 * @param producto    el producto asociado a la línea
	 * @param cantidad    la cantidad comprada
	 * @param precioVenta el precio del producto en el momento de la compra
	 */
	public LineaPedido(ProductoVenta producto, int cantidad, double precioVenta) {
		this.producto = producto;
		this.cantidad = cantidad;
		this.precioVenta = precioVenta;
	}

	/**
	 * Calcula el subtotal de la línea del pedido
	 *
	 * @return el importe total de esa línea
	 */
	public double getSubtotal() {
		return this.precioVenta * cantidad;
	}

	/**
	 * Comprueba si el producto indicado corresponde con el de la línea
	 *
	 * @param p el producto que se quiere comparar
	 * @return true si es el mismo producto, false en caso contrario
	 */
	public boolean productoPertenece(ProductoVenta p) {
		if (p != null && producto.getId().equals(p.getId())) {
			return true;
		}
		return false;
	}

	/**
	 * Recupera el producto de la línea
	 *
	 * @return el producto asociado
	 */
	public ProductoVenta getProducto() {
		return this.producto;
	}

	/**
	 * Devuelve la cantidad comprada de ese producto
	 *
	 * @return la cantidad de unidades
	 */
	public int getCantidad() {
		return cantidad;
	}

	/**
	 * Recupera el precio de venta guardado en la línea
	 *
	 * @return el precio aplicado en la compra
	 */
	public double getPrecioVenta() {
		return this.precioVenta;
	}
}
