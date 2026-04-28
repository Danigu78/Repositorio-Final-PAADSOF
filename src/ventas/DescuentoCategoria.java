package ventas;

import java.time.LocalDateTime;
import productos.Categoria;

/**
 * Clase que aplica una reducción de precio a todos los productos pertenecientes
 * a una categoría específica.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class DescuentoCategoria extends Descuento {

	private static final long serialVersionUID = 1L;

	/** Categoría de productos sobre la cual se aplicará la promoción. */
	private Categoria categoria;

	/** Factor de descuento aplicado a los productos de la categoría. */
	private double porcentaje;

	/**
	 * Constructor de la clase DescuentoCategoria
	 *
	 * @param nombre     el nombre del descuento
	 * @param inicio     la fecha desde la que empieza a aplicarse
	 * @param fin        la fecha en la que deja de estar disponible
	 * @param categoria  la categoría a la que afecta el descuento
	 * @param porcentaje el porcentaje que se rebaja
	 */
	public DescuentoCategoria(String nombre, LocalDateTime inicio, LocalDateTime fin, Categoria categoria,
			double porcentaje) {
		super(nombre, inicio, fin);
		this.categoria = categoria;
		if (porcentaje > 1)
			porcentaje /= 100;
		if (porcentaje < 0)
			porcentaje = 0;
		this.porcentaje = porcentaje;
	}

	/**
	 * Aplica el descuento a los productos del carrito que pertenezcan a la
	 * categoría
	 *
	 * @param carrito el carrito sobre el que se quiere calcular
	 * @return el total final una vez aplicado el descuento
	 */
	@Override
	public double aplicarDescuento(Carrito carrito) {
		if (!estaActivo())
			return carrito.calcularSubtotal();

		double total = 0;
		for (LineaCarrito linea : carrito.getLineas()) {
			double subtotalLinea = linea.getSubtotal();

			if (linea.getProducto().getCategorias().contains(categoria)) {
				subtotalLinea = subtotalLinea * (1 - porcentaje);
			}

			total += subtotalLinea;
		}
		return total;
	}

	/**
	 * Recupera la categoría asociada al descuento
	 *
	 * @return la categoría sobre la que actúa
	 */
	public Categoria getCategoria() {
		return categoria;
	}

	/**
	 * Devuelve el porcentaje del descuento
	 *
	 * @return el porcentaje que se aplica
	 */
	public double getPorcentaje() {
		return porcentaje;
	}
}
