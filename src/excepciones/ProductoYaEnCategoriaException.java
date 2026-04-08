package excepciones;

/**
 * Excepción que se lanza cuando se intenta añadir un producto a una categoría a
 * la que ya pertenece.
 */
public class ProductoYaEnCategoriaException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construye la excepción con un mensaje por defecto. El mensaje indica que el
	 * producto ya pertenece a la categoría.
	 */
	public ProductoYaEnCategoriaException() {
		super("El producto ya pertenece a esa categoría.");
	}

	/**
	 * Construye la excepción con un mensaje personalizado.
	 *
	 * @param message mensaje descriptivo del error
	 */
	public ProductoYaEnCategoriaException(String message) {
		super(message);
	}
}