package Excepcion;

/**
 * Excepción que se lanza cuando los pesos utilizados en el
 * cálculo de recomendaciones son inválidos.
 * 
 * Los pesos no pueden ser negativos ni todos cero al mismo tiempo.
 */
public class PesosInvalidosException extends CheckPointException {

    private static final long serialVersionUID = 1L;

    /** Peso asignado a la valoración */
    private final double pesoValoracion;

    /** Peso asignado a las compras */
    private final double pesoCompras;

    /** Peso asignado a las categorías */
    private final double pesoCategorias;

    /**
     * Construye la excepción indicando los pesos inválidos.
     *
     * @param pesoValoracion peso asignado a la valoración
     * @param pesoCompras peso asignado a las compras
     * @param pesoCategorias peso asignado a las categorías
     */
    public PesosInvalidosException(double pesoValoracion, double pesoCompras, double pesoCategorias) {
        super("Pesos invalidos (" + pesoValoracion + ", " + pesoCompras + ", " + pesoCategorias
                + "): no pueden ser negativos ni todos cero.");
        this.pesoValoracion = pesoValoracion;
        this.pesoCompras = pesoCompras;
        this.pesoCategorias = pesoCategorias;
    }

    /**
     * Devuelve el peso asignado a la valoración.
     *
     * @return peso de valoración
     */
    public double getPesoValoracion() {
        return pesoValoracion;
    }

    /**
     * Devuelve el peso asignado a las compras.
     *
     * @return peso de compras
     */
    public double getPesoCompras() {
        return pesoCompras;
    }

    /**
     * Devuelve el peso asignado a las categorías.
     *
     * @return peso de categorías
     */
    public double getPesoCategorias() {
        return pesoCategorias;
    }
}