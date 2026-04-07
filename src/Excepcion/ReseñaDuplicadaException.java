package Excepcion;

/**
 * Excepción que se lanza cuando un cliente intenta realizar
 * una reseña de un producto que ya ha reseñado anteriormente.
 */
public class ReseñaDuplicadaException extends CheckPointException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción con un mensaje por defecto.
     * Indica que el cliente ya ha reseñado el producto.
     */
    public ReseñaDuplicadaException() {
        super("Este cliente ya ha reseñado este producto.");
    }

    /**
     * Construye la excepción con un mensaje personalizado.
     *
     * @param message mensaje descriptivo del error
     */
    public ReseñaDuplicadaException(String message) {
        super(message);
    }
}