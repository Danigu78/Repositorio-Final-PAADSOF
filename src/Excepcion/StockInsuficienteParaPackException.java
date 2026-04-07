package Excepcion;

/**
 * Excepción que se lanza cuando se intenta añadir un producto a un pack pero no
 * hay stock suficiente disponible.
 */
public class StockInsuficienteParaPackException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construye la excepción con un mensaje por defecto. Indica que no hay stock
	 * suficiente para añadir el producto al pack.
	 */
	public StockInsuficienteParaPackException() {
		super("No hay stock suficiente para añadir ese producto al pack.");
	}

	/**
	 * Construye la excepción con un mensaje personalizado.
	 *
	 * @param message mensaje descriptivo del error
	 */
	public StockInsuficienteParaPackException(String message) {
		super(message);
	}
}