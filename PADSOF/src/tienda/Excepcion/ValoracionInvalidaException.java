package Excepcion;

public class ValoracionInvalidaException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	public ValoracionInvalidaException() {
		super("La valoración no es válida.La puntuacionn otorgada no está dentro del rango permitido.");
	}

	public ValoracionInvalidaException(String message) {
		super(message);
	}
}
