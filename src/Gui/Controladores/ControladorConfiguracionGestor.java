package Gui.Controladores;

import tienda.Tienda;
import usuarios.Gestor;

/**
 * Controlador de configuración del sistema para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorConfiguracionGestor {

    private Gestor gestor;
    private Tienda tienda;

    /**
     * Constructor del controlador de configuración.
     *
     * @param gestor El gestor logueado
     */
    public ControladorConfiguracionGestor(Gestor gestor) {
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    /**
     * Configura los tres tiempos del sistema.
     *
     * @param tOferta  Tiempo máximo de oferta en minutos
     * @param tCarrito Tiempo máximo de carrito en minutos
     * @param tPago    Tiempo máximo de pago en minutos
     * @return true si se configuró correctamente
     */
    public boolean configurarTiempos(int tOferta, int tCarrito, int tPago) {
        return gestor.configurarTiemposSistema(tOferta, tCarrito, tPago);
    }

    /**
     * Establece el precio de tasación.
     *
     * @param precio Nuevo precio de tasación
     * @return true si se estableció correctamente
     */
    public boolean setPrecioTasacion(double precio) {
        return gestor.setPrecioTasacion(precio);
    }

    /**
     * Devuelve el tiempo máximo actual del carrito.
     *
     * @return Tiempo máximo del carrito en minutos
     */
    public int getTiempoCarrito() {
        return tienda.getTiempoMaxCarrito();
    }

    /**
     * Devuelve el tiempo máximo actual de la oferta.
     *
     * @return Tiempo máximo de oferta en minutos
     */
    public int getTiempoOferta() {
        return tienda.getTiempoMaxOferta();
    }

    /**
     * Devuelve el tiempo máximo actual de pago.
     *
     * @return Tiempo máximo de pago en minutos
     */
    public int getTiempoPago() {
        return tienda.getTiempoMaxPago();
    }

    /**
     * Devuelve el precio actual de tasación.
     *
     * @return Precio de tasación
     */
    public double getPrecioTasacion() {
        return tienda.getPrecioTasacion();
    }

    /**
     * Modifica el perfil del gestor.
     *
     * @param nuevoNickname Nuevo nickname
     * @param nuevaPassword Nueva contraseña
     * @return true si se modificó correctamente
     */
    public boolean modificarPerfil(String nuevoNickname, String nuevaPassword) {
        return gestor.modificarPerfil(nuevoNickname, nuevaPassword);
    }
}