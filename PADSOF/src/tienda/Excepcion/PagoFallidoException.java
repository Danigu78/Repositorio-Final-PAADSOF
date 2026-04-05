package Excepcion;

public class PagoFallidoException extends CheckPointException {
    
    
    private static final long serialVersionUID = 1L;

   
    public PagoFallidoException() {
        super("El pago ha fallado.");
    }
}