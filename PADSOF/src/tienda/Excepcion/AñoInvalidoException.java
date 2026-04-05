package Excepcion;

public class AñoInvalidoException extends CheckPointException {

    private static final long serialVersionUID = 1L;
	private final int año;

    public AñoInvalidoException(int año) {
        super("Año invalido: " + año + ". El anio debe ser mayor que 0.");
        this.año = año;
    }

    public int getAnio() { return año; }
}