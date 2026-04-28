package tienda;

import java.io.Serializable;

import productos.EstadoProducto;
import productos.Producto2Mano;

/**
 * Clase encargada de filtrar productos de segunda mano por precio y estado de
 * conservación.
 * 
 * @author Antonino Albarrán
 * @version 1.0
 */
public class FiltroSegundaMano implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Límite inferior del precio de tasación aceptado en la búsqueda. */
	private double valorMinimo;

	/** Límite superior del precio de tasación aceptado en la búsqueda. */
	private double valorMaximo;

	/**
	 * * Calidad mínima requerida. Se basa en el ordinal del enum: a menor número,
	 * mejor estado físico.
	 */
	private EstadoProducto estadoMinimo;

	/**
	 * Constructor de la clase FiltroSegundaMano
	 */
	public FiltroSegundaMano() {
		resetear();
	}

	/**
	 * Comprueba si un producto cumple las condiciones del filtro
	 *
	 * @param p el producto que se quiere comprobar
	 * @return true si pasa el filtro, false en caso contrario
	 */
	public boolean cumpleFiltro(Producto2Mano p) {
		if (p == null)
			return false;
		if (!p.isVisible() || p.isBloqueado())
			return false;

		// Sin valoracion no se puede filtrar por precio ni estado
		if (p.getValoracion() == null)
			return false;

		double valorTasacion = p.getValoracion().getPrecioTasacion();
		if (valorTasacion < valorMinimo || valorTasacion > valorMaximo)
			return false;

		if (estadoMinimo != null) {
			EstadoProducto estadoProducto = p.getValoracion().getEstadoProducto();
			// NO_ACEPTADO nunca pasa el filtro
			if (estadoProducto == EstadoProducto.NO_ACEPTADO)
				return false;
			// ordinal mas bajo = mejor estado; rechazamos si el producto es peor que el
			// minimo
			if (estadoProducto.ordinal() > estadoMinimo.ordinal())
				return false;
		}

		return true;
	}

	/**
	 * Devuelve el filtro a sus valores iniciales
	 */
	public void resetear() {
		this.valorMinimo = 0;
		this.valorMaximo = Double.MAX_VALUE;
		this.estadoMinimo = null;
	}

	/**
	 * Recupera el valor mínimo del filtro
	 *
	 * @return el valor mínimo establecido
	 */
	public double getValorMinimo() {
		return valorMinimo;
	}

	/**
	 * Recupera el valor máximo del filtro
	 *
	 * @return el valor máximo establecido
	 */
	public double getValorMaximo() {
		return valorMaximo;
	}

	/**
	 * Devuelve el estado mínimo configurado en el filtro
	 *
	 * @return el estado mínimo aceptado
	 */
	public EstadoProducto getEstadoMinimo() {
		return estadoMinimo;
	}

	/**
	 * Cambia el valor mínimo del filtro
	 *
	 * @param valorMinimo el nuevo valor mínimo
	 */
	public void setValorMinimo(double valorMinimo) {
		if (valorMinimo < 0) {
			System.out.println("El valor minimo no puede ser negativo.");
			return;
		}
		if (valorMinimo > this.valorMaximo) {
			System.out.println("El valor minimo no puede ser mayor que el maximo.");
			return;
		}
		this.valorMinimo = valorMinimo;
	}

	/**
	 * Cambia el valor máximo del filtro
	 *
	 * @param valorMaximo el nuevo valor máximo
	 */
	public void setValorMaximo(double valorMaximo) {
		if (valorMaximo < this.valorMinimo) {
			System.out.println("El valor maximo no puede ser menor que el minimo.");
			return;
		}
		this.valorMaximo = valorMaximo;
	}

	/**
	 * Establece el estado mínimo que debe tener el producto
	 *
	 * @param estadoMinimo el estado mínimo aceptado
	 */
	public void setEstadoMinimo(EstadoProducto estadoMinimo) {
		this.estadoMinimo = estadoMinimo;
	}

	/**
	 * Devuelve una representación sencilla del filtro
	 *
	 * @return un texto con los valores configurados
	 */
	@Override
	public String toString() {
		return "FiltroSegundaMano [" + "valor: " + valorMinimo + "-"
				+ (valorMaximo == Double.MAX_VALUE ? "MAX" : valorMaximo) + " | estado minimo: "
				+ (estadoMinimo == null ? "cualquiera" : estadoMinimo) + "]";
	}
}