package Gui.Controladores;

import java.util.ArrayList;
import java.util.List;
import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;

/**
 * Controlador de la gestión de packs para el gestor.
 * Usa un empleado interno con todos los permisos para gestionar packs.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorPacksGestor {

    private Tienda tienda;
    // Empleado interno con permisos de packs para ejecutar las operaciones
    private Empleado empleadoInterno;

    /**
     * Constructor del controlador de packs.
     * Busca un empleado con permisos de packs o crea uno interno.
     *
     * @param gestor El gestor logueado
     */
    public ControladorPacksGestor(Gestor gestor) {
        this.tienda = Tienda.getInstancia();
        // Buscamos un empleado que tenga permisos de packs
        for (Empleado e : tienda.obtenerEmpleadosTienda()) {
            if (e.tienePermiso(TipoPermisos.GESTION_PACKS) && !e.isDespedido()) {
                this.empleadoInterno = e;
                break;
            }
        }
    }

    /**
     * Devuelve todos los packs de la tienda.
     *
     * @return Lista de packs
     */
    public List<Pack> getPacks() {
        List<Pack> packs = new ArrayList<>();
        for (ProductoVenta p : tienda.getStockVentas()) {
            if (p instanceof Pack) {
                packs.add((Pack) p);
            }
        }
        return packs;
    }

    /**
     * Devuelve todos los productos de la tienda que no son packs.
     *
     * @return Lista de productos
     */
    public List<ProductoVenta> getProductos() {
        List<ProductoVenta> productos = new ArrayList<>();
        for (ProductoVenta p : tienda.getStockVentas()) {
            if (!(p instanceof Pack)) {
                productos.add(p);
            }
        }
        return productos;
    }

    /**
     * Crea un nuevo pack.
     *
     * @param nombre      Nombre del pack
     * @param descripcion Descripción
     * @param imagen      Ruta de imagen
     * @param precio      Precio del pack
     * @param stock       Stock inicial
     * @param lineas      Líneas de productos del pack
     * @return true si se creó correctamente
     */
    public boolean crearPack(String nombre, String descripcion, String imagen,
                              double precio, int stock, ArrayList<LineaPack> lineas) {
        if (empleadoInterno == null) {
            return false;
        }
        return empleadoInterno.crearPack(nombre, descripcion, imagen, precio, stock, lineas);
    }

    /**
     * Añade un producto a un pack existente.
     *
     * @param idProducto Id del producto
     * @param idPack     Id del pack
     * @param unidades   Unidades a añadir
     * @return true si se añadió correctamente
     */
    public boolean añadirProductoAPack(String idProducto, String idPack, int unidades) {
        if (empleadoInterno == null) return false;
        return empleadoInterno.añadirProductoaPack(idProducto, idPack, unidades);
    }

    /**
     * Elimina un producto de un pack.
     *
     * @param idPack     Id del pack
     * @param idProducto Id del producto
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProductoDePack(String idPack, String idProducto) {
        if (empleadoInterno == null) return false;
        return empleadoInterno.eliminarProductoDePack(idPack, idProducto);
    }

    /**
     * Elimina un pack completo de la tienda.
     *
     * @param idPack Id del pack
     * @return true si se eliminó correctamente
     */
    public boolean eliminarPack(String idPack) {
        if (empleadoInterno == null) return false;
        return empleadoInterno.eliminarPack(idPack);
    }

    /**
     * Devuelve si hay un empleado disponible para gestionar packs.
     *
     * @return true si hay empleado con permisos
     */
    public boolean hayEmpleadoDisponible() {
        return empleadoInterno != null;
    }
}