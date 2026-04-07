package Excepcion;

/**
 * Excepción que se lanza cuando una línea de un fichero no cumple el formato
 * esperado.
 * 
 * Incluye información sobre la línea donde se ha producido el error y su
 * contenido para facilitar la depuración.
 */
public class FicheroFormatoInvalidoException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/** Número de línea donde ocurre el error */
	private int numeroLinea;

	/** Contenido de la línea con formato inválido */
	private String linea;

	/**
	 * Construye la excepción indicando la línea y el error.
	 *
	 * @param numeroLinea número de línea donde ocurre el error
	 * @param linea       contenido de la línea errónea
	 * @param mensaje     descripción del problema detectado
	 */
	public FicheroFormatoInvalidoException(int numeroLinea, String linea, String mensaje) {
		super(mensaje);
		this.numeroLinea = numeroLinea;
		this.linea = linea;
	}

	/**
	 * Devuelve el mensaje completo de la excepción incluyendo la línea y su número.
	 *
	 * @return mensaje detallado del error
	 */
	@Override
	public String getMessage() {
		return "Error en línea " + numeroLinea + ": " + this.linea + " " + super.getMessage();
	}
}