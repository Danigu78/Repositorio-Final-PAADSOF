package Gui.Controladores.Gestor;

import Gui.Gestor.SubpanelCategoriasGestor;
import productos.Categoria;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
    
    public boolean eliminarCategoria(String nombreCat) {
        boolean ok = gestor.eliminarCategoria(nombreCat);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }
}