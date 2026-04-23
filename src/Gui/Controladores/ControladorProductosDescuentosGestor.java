package Gui.Controladores;

import java.time.LocalDateTime;
import tienda.Tienda;
import usuarios.Gestor;
import ventas.Descuento;
import java.util.List;

import productos.Categoria;

/**
 * Controlador de productos y descuentos para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorProductosDescuentosGestor {

    private Gestor gestor;
    private Tienda tienda;

    /**
     * Constructor del controlador de productos y descuentos.
     *
     * @param gestor El gestor logueado
     */
    public ControladorProductosDescuentosGestor(Gestor gestor) {
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    /**
     * Modifica el precio de un producto.
     *
     * @param idProducto  Id del producto
     * @param nuevoPrecio Nuevo precio
     * @return true si se modificó correctamente
     */
    public boolean modificarPrecio(String idProducto, double nuevoPrecio) {
        return gestor.modificarPrecioProducto(idProducto, nuevoPrecio);
    }

    /**
     * Crea un descuento por volumen de gasto.
     *
     * @param nombre       Nombre del descuento
     * @param precioMinimo Precio mínimo para aplicar
     * @param porcentaje   Porcentaje de descuento
     * @param inicio       Fecha de inicio
     * @param fin          Fecha de fin
     * @return true si se creó correctamente
     */
    public boolean crearDescuentoVolumen(String nombre, double precioMinimo,
                                          double porcentaje, LocalDateTime inicio, LocalDateTime fin) {
        return gestor.crearDescuentoVolumen(nombre, precioMinimo, porcentaje, inicio, fin);
    }

    /**
     * Crea un descuento por categoría.
     *
     * @param nombre      Nombre del descuento
     * @param categoria   Nombre de la categoría
     * @param porcentaje  Porcentaje de descuento
     * @param inicio      Fecha de inicio
     * @param fin         Fecha de fin
     * @return true si se creó correctamente
     */
    public boolean crearDescuentoCategoria(String nombre, String categoria,
                                            double porcentaje, LocalDateTime inicio, LocalDateTime fin) {
        return gestor.crearDescuentoCategoria(nombre, categoria, porcentaje, inicio, fin);
    }

    /**
     * Crea un descuento por cantidad de unidades.
     *
     * @param nombre         Nombre del descuento
     * @param idProducto     Id del producto
     * @param cantidadMinima Cantidad mínima
     * @param porcentaje     Porcentaje de descuento
     * @param inicio         Fecha de inicio
     * @param fin            Fecha de fin
     * @return true si se creó correctamente
     */
    public boolean crearDescuentoCantidad(String nombre, String idProducto, int cantidadMinima,
                                           double porcentaje, LocalDateTime inicio, LocalDateTime fin) {
        return gestor.crearDescuentoCantidad(nombre, idProducto, cantidadMinima, porcentaje, inicio, fin);
    }

    /**
     * Crea un descuento de tipo regalo.
     *
     * @param nombre          Nombre del descuento
     * @param idProducto      Id del producto regalado
     * @param gastoNecesario  Gasto mínimo para el regalo
     * @param inicio          Fecha de inicio
     * @param fin             Fecha de fin
     * @return true si se creó correctamente
     */
    public boolean crearDescuentoRegalo(String nombre, String idProducto,
                                         double gastoNecesario, LocalDateTime inicio, LocalDateTime fin) {
        return gestor.crearDescuentoRegalo(nombre, idProducto, gastoNecesario, inicio, fin);
    }

    /**
     * Elimina un descuento por su id.
     *
     * @param idDescuento Id del descuento
     * @return true si se eliminó correctamente
     */
    public boolean eliminarDescuento(String idDescuento) {
        return gestor.eliminarDescuento(idDescuento);
    }

    /**
     * Devuelve los descuentos activos.
     *
     * @return Lista de descuentos activos
     */
    public List<Descuento> getDescuentosActivos() {
        return tienda.getDescuentosActivos();
    }

    /**
     * Devuelve todos los productos de la tienda.
     *
     * @return Lista de productos
     */
    public List<productos.ProductoVenta> getProductos() {
        return tienda.getStockVentas();
    }
    
    /**
     * Devuelve todas las categorías de la tienda.
     *
     * @return Lista de categorías
     */
    public List<Categoria> getCategorias() {
        return tienda.getCategorias();
    }
}