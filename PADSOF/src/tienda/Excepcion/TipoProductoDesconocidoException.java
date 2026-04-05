package Excepcion;

public class TipoProductoDesconocidoException extends FicheroFormatoInvalidoException {

	private static final long serialVersionUID = 1L;

	public TipoProductoDesconocidoException(int numLinea, String linea, String tipo) {
		super(numLinea, linea, "Tipo de producto desconocido: " + tipo);
	}
}