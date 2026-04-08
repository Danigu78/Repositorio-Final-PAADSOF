package ventas;

import java.time.LocalDateTime;

/**
 * Clase que aplica una reducción de precio basada en el volumen de unidades de
 * un mismo producto que se van a comprar .
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class DescuentoCantidad extends Descuento {
	/**
	 * Número de unidades de un mismo artículo necesarias para activar la promoción.
	 */
	private int cantidadMinima;

	/**
	 * Factor de descuento a aplicar ( 0.1 para un 10%) sobre las líneas que
	 * cumplan el mínimo.
	 */
	private double porcentaje;

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
		super(nombre, inicio, fin);
		this.cantidadMinima = cantidadMinima;
		if (porcentaje > 1)
			porcentaje /= 100;
		if (porcentaje < 0)
			porcentaje = 0;
		this.porcentaje = porcentaje;
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

			if (linea.getCantidad() >= cantidadMinima) {
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
	 * @return el porcentaje aplicado
	 */
	public double getPorcentaje() {
		return porcentaje;
	}
}