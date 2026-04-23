package Gui.Controladores;

import java.util.List;
import productos.Categoria;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Gestor;

/**
 * Controlador de la gestión de categorías para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorCategoriasGestor {

    private Gestor gestor;
    private Tienda tienda;

    /**
     * Constructor del controlador de categorías.
     *
     * @param gestor El gestor logueado
     */
    public ControladorCategoriasGestor(Gestor gestor) {
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    /**
     * Crea una nueva categoría.
     *
     * @param nombre      Nombre de la categoría
     * @param descripcion Descripción
     * @return true si se creó correctamente
     */
    public boolean crearCategoria(String nombre, String descripcion) {
        return gestor.crearCategoria(nombre, descripcion);
    }

    /**
     * Añade un producto a una categoría.
     *
     * @param idProducto Id del producto
     * @param nombreCat  Nombre de la categoría
     * @return true si se añadió correctamente
     */
    public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
        return gestor.añadirProductoACategoria(idProducto, nombreCat);
    }

    /**
     * Elimina un producto de una categoría.
     *
     * @param idProducto Id del producto
     * @param nombreCat  Nombre de la categoría
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
        return gestor.eliminarProductoDeCategoria(idProducto, nombreCat);
    }

    /**
     * Devuelve todas las categorías de la tienda.
     *
     * @return Lista de categorías
     */
    public List<Categoria> getCategorias() {
        return tienda.getCategorias();
    }

    /**
     * Devuelve todos los productos de la tienda.
     *
     * @return Lista de productos
     */
    public List<ProductoVenta> getProductos() {
        return tienda.getStockVentas();
    }
}