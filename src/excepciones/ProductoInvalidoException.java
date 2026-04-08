package excepciones;

/**
 * Excepción que se lanza cuando los datos de un producto son inválidos.
 * 
 * Esto puede ocurrir al crear o modificar un producto con atributos erróneos o
 * inconsistentes.
 */
public class ProductoInvalidoException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construye la excepción con un mensaje por defecto. El mensaje indica que los
	 * datos del producto no son válidos.
	 */
	public ProductoInvalidoException() {
		super("Los datos del producto no son válidos.");
	}

	/**
	 * Construye la excepción con un mensaje personalizado.
	 *
	 * @param message mensaje descriptivo del error
	 */
	public ProductoInvalidoException(String message) {
		super(message);
	}
}
