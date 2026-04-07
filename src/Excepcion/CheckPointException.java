package Excepcion;

/**
 * Excepción base del sistema para gestionar errores de tipo checkpoint.
 * 
 * Todas las excepciones personalizadas del sistema heredan de esta clase,
 * permitiendo un manejo unificado de errores.
 */
public class CheckPointException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción con un mensaje descriptivo.
     *
     * @param mensaje descripción del error
     */
    public CheckPointException(String mensaje) {
        super(mensaje);
    }
}