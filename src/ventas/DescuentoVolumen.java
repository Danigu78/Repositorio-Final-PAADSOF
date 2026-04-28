package ventas;

import java.time.LocalDateTime;

/**
 * Clase que aplica una reducción de precio al total del carrito si se supera un
 * importe mínimo de compra.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class DescuentoVolumen extends Descuento {

	private static final long serialVersionUID = 1L;

	/**
	 * Importe total mínimo que debe alcanzar el carrito para activar el descuento.
	 */
	private double umbralMinimo;

	/** Factor de descuento aplicado sobre el total del subtotal. */
	private double porcentaje;

	/**
	 * Constructor de la clase DescuentoVolumen
	 *
	 * @param nombre       el nombre del descuento
	 * @param inicio       la fecha en la que empieza a aplicarse
	 * @param fin          la fecha en la que deja de estar activo
	 * @param umbralMinimo el importe mínimo necesario para que se aplique
	 * @param porcentaje   el porcentaje de descuento
	 */
	public DescuentoVolumen(String nombre, LocalDateTime inicio, LocalDateTime fin, double umbralMinimo,
			double porcentaje) {
		super(nombre, inicio, fin);
		this.umbralMinimo = umbralMinimo;
		if (porcentaje > 1)
			porcentaje /= 100;
		if (porcentaje < 0)
			porcentaje = 0;
		this.porcentaje = porcentaje;
	}

	/**
	 * Aplica el descuento si el carrito alcanza el importe mínimo
	 *
	 * @param carrito el carrito sobre el que se calcula el descuento
	 * @return el total final tras aplicar el descuento, o el subtotal si no
	 *         corresponde
	 */
	@Override
	public double aplicarDescuento(Carrito carrito) {
		if (!estaActivo()) {
			return carrito.calcularSubtotal();
		}

		double subtotal = carrito.calcularSubtotal();

		if (subtotal < umbralMinimo) {
			return subtotal;
		}

		return subtotal * (1 - porcentaje);
	}

	/**
	 * Recupera el importe mínimo necesario para aplicar el descuento
	 *
	 * @return el umbral mínimo
	 */
	public double getUmbralMinimo() {
		return umbralMinimo;
	}

	/**
	 * Devuelve el porcentaje del descuento
	 *
	 * @return el porcentaje aplicado
	 */
	public double getPorcentaje() {
		return porcentaje;
	}
}