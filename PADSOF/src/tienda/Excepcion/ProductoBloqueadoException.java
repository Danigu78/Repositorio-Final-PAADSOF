package Excepcion;


public class ProductoBloqueadoException extends CheckPointException {
    
    
    private static final long serialVersionUID = 1L;
    private String idProducto;

    public ProductoBloqueadoException(String idProducto) {
       
        super("El producto " + idProducto + " está bloqueado porque está ofrecido en otra oferta");
        this.idProducto = idProducto;
    }

    public String getIdProducto() {
        return idProducto;
    }
}