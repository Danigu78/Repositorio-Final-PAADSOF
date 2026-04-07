package Excepcion;

/**
 * Excepción que se lanza cuando un producto aún no ha sido tasado y se intenta
 * usar en un intercambio o transacción.
 * 
 * Permite identificar qué producto específico ha provocado el error.
 */
public class ProductoNoTasadoException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/** Identificador del producto que no ha sido tasado */
	private final String idProducto;

	/** Nombre del producto que no ha sido tasado */
	private final String nombreProducto;

	/**
	 * Construye la excepción indicando el producto que no ha sido tasado.
	 *
	 * @param idProducto     identificador del producto
	 * @param nombreProducto nombre del producto
	 */
	public ProductoNoTasadoException(String idProducto, String nombreProducto) {
		super("El producto " + nombreProducto + " (id: " + idProducto
				+ ") no puede participar en un intercambio porque aun no ha sido tasado.");
		this.idProducto = idProducto;
		this.nombreProducto = nombreProducto;
	}

	/**
	 * Devuelve el identificador del producto que provocó la excepción.
	 *
	 * @return id del producto
	 */
	public String getIdProducto() {
		return idProducto;
	}

	/**
	 * Devuelve el nombre del producto que provocó la excepción.
	 *
	 * @return nombre del producto
	 */
	public String getNombreProducto() {
		return nombreProducto;
	}
}