package Excepcion;

/**
 * Excepción que se lanza cuando se intenta añadir un producto
 * a un pack en el que ya está incluido.
 */
public class ProductoYaEnPackException extends CheckPointException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción con un mensaje por defecto.
     * Indica que el producto ya está incluido en el pack.
     */
    public ProductoYaEnPackException() {
        super("Ese producto ya está incluido en el pack.");
    }

    /**
     * Construye la excepción con un mensaje personalizado.
     *
     * @param message mensaje descriptivo del error
     */
    public ProductoYaEnPackException(String message) {
        super(message);
    }
}