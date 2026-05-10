package Gui.Controladores.Gestor;


import productos.Categoria;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;
import Gui.Controladores.empleado.ControladorProductosEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import Gui.Gestor.*;

/**
 * Controlador de la gestión de categorías para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorCategoriasGestor implements ActionListener {

    private SubpanelCategoriasGestor vista;
    private Gestor gestor;
    private Tienda tienda;
    private ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

    public ControladorCategoriasGestor(SubpanelCategoriasGestor vista, Gestor gestor) {
        this.vista = vista;
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("crearCategoria")) {
            vista.procesarCrearCategoria();
        } else if (cmd.equals("anadirProductoTabla")) {
            vista.procesarAnadirProductoTabla();
        } else if (cmd.equals("quitarProductoTabla")) {
            vista.procesarQuitarProductoTabla();
        } else if (cmd.startsWith("añadirProducto:")) {
            vista.procesarAñadirProducto(cmd.substring(15));
        } else if (cmd.startsWith("quitarProducto:")) {
            vista.procesarQuitarProducto(cmd.substring(15));
        }else if (cmd.startsWith("eliminarCategoria:")) {
            vista.confirmarEliminarCategoria(cmd.substring(18));
        }
    }

    public boolean crearCategoria(String nombre, String descripcion) {
        boolean ok = gestor.crearCategoria(nombre, descripcion);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
        boolean ok = gestor.añadirProductoACategoria(idProducto, nombreCat);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
        boolean ok = gestor.eliminarProductoDeCategoria(idProducto, nombreCat);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public List<Categoria> getCategorias() {
        return tienda.getCategorias();
    }

    public List<ProductoVenta> getProductos() {
        return tienda.getStockVentas();
    }

    public List<ProductoVenta> getProductosOrdenados() {
        return productos.obtenerProductosOrdenadosPorStock();
    }

    public String obtenerTipoProductoVenta(ProductoVenta producto) {
        return productos.obtenerTipoProductoVenta(producto);
    }

    public String obtenerTextoCategorias(ProductoVenta producto) {
        return productos.obtenerTextoCategorias(producto);
    }

    public String formatearPrecio(double precio) {
        return productos.formatearPrecio(precio);
    }

    public String formatearPuntuacion(double puntuacion) {
        return productos.formatearPuntuacion(puntuacion);
    }
    
    public boolean eliminarCategoria(String nombreCat) {
        boolean ok = gestor.eliminarCategoria(nombreCat);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }
}
