package Excepcion;

/**
 * Excepción que se lanza cuando un producto está bloqueado y no puede ser
 * utilizado en operaciones como ofertas o intercambios.
 * 
 * Esto ocurre, en caso si el producto ya está ofrecido en otra oferta.
 */
public class ProductoBloqueadoException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/** Identificador del producto bloqueado */
	private String idProducto;

	/**
	 * Construye la excepción indicando el producto bloqueado.
	 *
	 * @param idProducto identificador del producto que está bloqueado
	 */
	public ProductoBloqueadoException(String idProducto) {
		super("El producto " + idProducto + " está bloqueado porque está ofrecido en otra oferta");
		this.idProducto = idProducto;
	}

	/**
	 * Devuelve el identificador del producto que provocó la excepción.
	 *
	 * @return id del producto bloqueado
	 */
	public String getIdProducto() {
		return idProducto;
	}
}