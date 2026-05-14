package ventas;

import java.time.LocalDateTime;
import productos.ProductoVenta;

/**
 * Clase que aplica una reducción de precio basada en el volumen de unidades de
 * un mismo producto que se van a comprar .
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class DescuentoCantidad extends Descuento {

	private static final long serialVersionUID = 1L;

	/**
	 * Número de unidades de un mismo artículo necesarias para activar la promoción.
	 */
	private int cantidadMinima;

	/**
	 * Factor de descuento a aplicar ( 0.1 para un 10%) sobre las líneas que cumplan
	 * el mínimo.
	 */
	private double porcentaje;
	private ProductoVenta producto;

	/**
	 * Constructor de la clase DescuentoCantidad
	 *
	 * @param nombre         el nombre del descuento
	 * @param inicio         la fecha en la que comienza
	 * @param fin            la fecha en la que termina
	 * @param cantidadMinima la cantidad mínima necesaria para aplicar el descuento
	 * @param porcentaje     el porcentaje de descuento
	 */
	public DescuentoCantidad(String nombre, LocalDateTime inicio, LocalDateTime fin, int cantidadMinima,
			double porcentaje) {
		this(nombre, inicio, fin, cantidadMinima, porcentaje, null);
	}

	/**
	 * Constructor del descuento por cantidad.
	 *
	 * @param nombre         nombre del descuento
	 * @param inicio         fecha y hora de inicio del descuento
	 * @param fin            fecha y hora de fin del descuento
	 * @param cantidadMinima cantidad mínima necesaria para aplicar el descuento
	 * @param porcentaje     porcentaje de descuento (puede venir en formato 0-1 o
	 *                       0-100)
	 * @param producto       producto al que se aplica el descuento
	 */
	public DescuentoCantidad(String nombre, LocalDateTime inicio, LocalDateTime fin, int cantidadMinima,
			double porcentaje, ProductoVenta producto) {
		super(nombre, inicio, fin);
		this.cantidadMinima = cantidadMinima;
		if (porcentaje > 1)
			porcentaje /= 100;
		if (porcentaje < 0)
			porcentaje = 0;
		this.porcentaje = porcentaje;
		this.producto = producto;
	}

	/**
	 * Aplica el descuento a las líneas que cumplen la cantidad mínima
	 *
	 * @param carrito el carrito sobre el que se calcula el descuento
	 * @return el total final después de aplicar el descuento
	 */
	@Override
	public double aplicarDescuento(Carrito carrito) {
		if (!estaActivo())
			return carrito.calcularSubtotal();

		double total = 0;
		for (LineaCarrito linea : carrito.getLineas()) {
			double subtotalLinea = linea.getSubtotal();

			if ((producto == null || linea.productoPertence(producto)) && linea.getCantidad() >= cantidadMinima) {
				subtotalLinea = subtotalLinea * (1 - porcentaje);
			}

			total += subtotalLinea;
		}
		return total;
	}

	/**
	 * Devuelve la cantidad mínima necesaria para que se aplique el descuento
	 *
	 * @return la cantidad mínima de productos
	 */
	public int getCantidadMinima() {
		return cantidadMinima;
	}

	/**
	 * Recupera el porcentaje del descuento
	 *
	 * @return porcentaje aplicado
	 */
	public double getPorcentaje() {
		return porcentaje;
	}

	/**
	 * recupera el producto
	 * 
	 * @return producto
	 */
	public ProductoVenta getProducto() {
		return producto;
	}
}
