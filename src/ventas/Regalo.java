package ventas;

import java.time.LocalDateTime;
import productos.ProductoVenta;

/**
 * Clase que representa una promoción donde se regala un producto al superar un
 * gasto mínimo.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Regalo extends Descuento {

	private static final long serialVersionUID = 1L;

	/** Importe mínimo de compra requerido para obtener el artículo de obsequio. */
	private double umbral;

	/**
	 * Artículo específico que se añade gratuitamente al pedido si se cumple la
	 * condición.
	 */
	private ProductoVenta producto;

	/**
	 * Constructor de la clase Regalo
	 *
	 * @param nombre   el nombre de la promoción
	 * @param inicio   la fecha en la que empieza
	 * @param fin      la fecha en la que termina
	 * @param umbral   el importe mínimo necesario para conseguir el regalo
	 * @param producto el producto que se entrega como regalo
	 */
	public Regalo(String nombre, LocalDateTime inicio, LocalDateTime fin, double umbral, ProductoVenta producto) {
		super(nombre, inicio, fin);
		this.umbral = umbral;
		this.producto = producto;
	}

	/**
	 * Comprueba si el carrito cumple las condiciones para recibir el regalo
	 *
	 * @param carrito el carrito que se quiere comprobar
	 * @return true si corresponde añadir el regalo, false en caso contrario
	 */
	public boolean aplicaRegalo(Carrito carrito) {
		return estaActivo() && carrito.calcularSubtotal() >= umbral && producto != null
				&& producto.getStockDisponible() > 0;
	}

	/**
	 * Recupera el producto que se da como regalo
	 *
	 * @return el producto regalo
	 */
	public ProductoVenta getProductoRegalo() {
		return producto;
	}

	public double getUmbral() {
		return umbral;
	}

	/**
	 * Devuelve el subtotal del carrito, ya que esta promoción no rebaja el precio
	 *
	 * @param carrito el carrito sobre el que se calcula
	 * @return el subtotal sin cambios
	 */
	@Override
	public double aplicarDescuento(Carrito carrito) {
		return carrito.calcularSubtotal();
	}
}