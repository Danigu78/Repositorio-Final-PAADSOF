package productos;

import excepciones.*;

/**
 * Clase que representa una línea de detalle dentro de un pack de productos.
 * Vincula un {@link ProductoVenta} específico con una cantidad determinada de
 * unidades que formarán parte del conjunto.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class LineaPack {
	/**
	 * El producto individual que se incluye dentro del pack.
	 */
	private ProductoVenta producto;
	/**
	 * Cantidad de ejemplares del producto asociado que contiene esta línea del
	 * pack.
	 */
	private int unidades;

	/**
	 * Constructor de la clase LineaPack
	 *
	 * @param producto el producto asociado a la línea
	 * @param unidades la cantidad de unidades de ese producto
	 */
	public LineaPack(ProductoVenta producto, int unidades) {
		if (producto == null) {
			throw new ProductoInvalidoException("El producto de la línea del pack no puede ser null.");
		}
		if (unidades <= 0) {
			throw new ProductoInvalidoException("Las unidades de la línea del pack deben ser mayores que 0.");
		}
		this.producto = producto;
		this.unidades = unidades;
	}

	/**
	 * Recupera el producto de la línea
	 *
	 * @return el producto asociado
	 */
	public ProductoVenta getProducto() {
		return producto;
	}

	/**
	 * Recupera el número de unidades de la línea
	 *
	 * @return las unidades del producto
	 */
	public int getUnidades() {
		return unidades;
	}

	/**
	 * Calcula el total de la línea en función del producto y las unidades
	 *
	 * @return el subtotal correspondiente a la línea
	 */
	public double getSubtotal() {
		return producto.getPrecioOficial() * unidades;
	}

	/**
	 * Modifica el número de unidades de la línea
	 *
	 * @param nuevasUnidades la nueva cantidad de unidades
	 */
	public void setUnidades(int nuevasUnidades) {
		if (nuevasUnidades <= 0) {
			throw new ProductoInvalidoException("Las nuevas unidades deben ser mayores que 0.");
		}
		this.unidades = nuevasUnidades;
	}
}
