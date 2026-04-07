package Excepcion;

/**
 * Excepción que se lanza cuando se intenta utilizar el recomendador y este se
 * encuentra desactivado.
 * 
 * Indica que el gestor del sistema debe activarlo antes de poder usarlo.
 */
public class RecomendadorNoActivoException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construye la excepción con un mensaje por defecto. Indica que el recomendador
	 * está desactivado.
	 */
	public RecomendadorNoActivoException() {
		super("El recomendador esta desactivado. Contacte con el gestor para activarlo.");
	}
}