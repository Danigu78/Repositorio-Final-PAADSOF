package ventas;

import java.io.*;

import productos.ProductoVenta;

/**
 * Clase que representa el detalle de un producto específico dentro de un
 * carrito.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class LineaCarrito implements Serializable {

	private static final long serialVersionUID = 1L;

	/** El artículo de venta asociado a esta línea del carrito. */
	private ProductoVenta producto;

	/** Número de ejemplares reservados del producto. */
	private int cantidad;

	/**
	 * Constructor de la clase LineaCarrito
	 *
	 * @param producto el producto asociado a la línea
	 * @param cantidad la cantidad de unidades de ese producto
	 */
	public LineaCarrito(ProductoVenta producto, int cantidad) {
		if (producto == null || cantidad <= 0) {
			throw new IllegalArgumentException("Argumentos de linea carrito inválidos");
		}
		this.producto = producto;
		this.cantidad = cantidad;
	}

	/**
	 * Comprueba si el producto recibido es el mismo que el de la línea
	 *
	 * @param p el producto que se quiere comprobar
	 * @return true si coincide, false en caso contrario
	 */
	public boolean productoPertence(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (producto.getId().equals(p.getId())) {
			return true;
		}
		return false;
	}

	/**
	 * Cambia la cantidad de unidades de la línea
	 *
	 * @param cantidad la nueva cantidad
	 */
	public void setCantidad(int cantidad) {
		if (cantidad < 0) {
			return;
		}
		this.cantidad = cantidad;
	}

	/**
	 * Recupera la cantidad de productos de la línea
	 *
	 * @return el número de unidades
	 */
	public int getCantidad() {
		return this.cantidad;
	}

	/**
	 * Devuelve el producto asociado a la línea
	 *
	 * @return el producto de la línea
	 */
	public ProductoVenta getProducto() {
		return this.producto;
	}

	/**
	 * Calcula el subtotal de la línea
	 *
	 * @return el precio total según cantidad y producto
	 */
	public double getSubtotal() {
		return this.producto.getPrecioOficial() * this.cantidad;
	}

}
