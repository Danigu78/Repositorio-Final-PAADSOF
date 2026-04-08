package excepciones;

/**
 * Excepción que se lanza cuando se encuentra un tipo de producto desconocido al
 * procesar un fichero de datos de productos.
 * 
 * Hereda de {@link FicheroFormatoInvalidoException} y permite indicar la línea
 * y el contenido que provocaron el error.
 */
public class TipoProductoDesconocidoException extends FicheroFormatoInvalidoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construye la excepción indicando la línea, el contenido y el tipo
	 * desconocido.
	 *
	 * @param numLinea número de línea donde se detectó el tipo desconocido
	 * @param linea    contenido completo de la línea
	 * @param tipo     tipo de producto que no se reconoce
	 */
	public TipoProductoDesconocidoException(int numLinea, String linea, String tipo) {
		super(numLinea, linea, "Tipo de producto desconocido: " + tipo);
	}
}