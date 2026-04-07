package Excepcion;

/**
 * Excepción que se lanza cuando una valoración de producto es inválida.
 * 
 * Esto ocurre cuando la puntuación otorgada no está dentro del rango permitido.
 */
public class ValoracionInvalidaException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construye la excepción con un mensaje por defecto. Indica que la valoración
	 * no es válida y está fuera del rango permitido.
	 */
	public ValoracionInvalidaException() {
		super("La valoración no es válida. La puntuación otorgada no está dentro del rango permitido.");
	}

	/**
	 * Construye la excepción con un mensaje personalizado.
	 *
	 * @param message mensaje descriptivo del error
	 */
	public ValoracionInvalidaException(String message) {
		super(message);
	}
}