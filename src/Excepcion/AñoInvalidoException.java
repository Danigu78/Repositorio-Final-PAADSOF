package Excepcion;

/**
 * Excepción que se lanza cuando se introduce un año inválido.
 * Se considera inválido cualquier año menor o igual que 0.
 */
public class AñoInvalidoException extends CheckPointException {

    private static final long serialVersionUID = 1L;

    /** Año que ha provocado la excepción */
    private final int año;

    /**
     * Construye la excepción indicando el año inválido.
     *
     * @param año año que no cumple las condiciones (debe ser mayor que 0)
     */
    public AñoInvalidoException(int año) {
        super("Año invalido: " + año + ". El anio debe ser mayor que 0.");
        this.año = año;
    }

    /**
     * Devuelve el año que ha provocado la excepción.
     *
     * @return año inválido
     */
    public int getAño() { 
        return año; 
    }
}