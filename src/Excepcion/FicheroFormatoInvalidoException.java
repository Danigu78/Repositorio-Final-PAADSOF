package Excepcion;

public class FicheroFormatoInvalidoException extends CheckPointException {
	private static final long serialVersionUID = 1L;
	private int numeroLinea;
	private String linea;

	public FicheroFormatoInvalidoException(int numeroLinea, String linea, String mensaje) {
		super(mensaje);
		this.numeroLinea = numeroLinea;
		this.linea = linea;
	}

	@Override
	public String getMessage() {

		return "Error en línea " + numeroLinea + ": " + this.linea + " " + super.getMessage();
	}
}