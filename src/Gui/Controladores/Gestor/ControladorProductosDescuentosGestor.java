package Gui.Controladores.Gestor;

import Gui.Gestor.*;
import productos.Categoria;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;
import ventas.Descuento;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador de productos y descuentos para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorProductosDescuentosGestor implements ActionListener {

    private SubpanelProductosDescuentosGestor vista;
    private Gestor gestor;
    private Tienda tienda;

    public ControladorProductosDescuentosGestor(
            SubpanelProductosDescuentosGestor vista, Gestor gestor) {
        this.vista = vista;
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.startsWith("cambiarPrecio:")) {
            vista.procesarCambiarPrecio(cmd.substring(14));
        } else if (cmd.equals("cambiarPrecioManual")) {
            vista.procesarCambiarPrecioManual();
        } else if (cmd.equals("crearDescuento")) {
            vista.procesarCrearDescuento();
        } else if (cmd.startsWith("eliminarDescuento:")) {
            vista.procesarEliminarDescuento(cmd.substring(18));
        }
    }

    public boolean modificarPrecio(String idProducto, double nuevoPrecio) {
        boolean ok = gestor.modificarPrecioProducto(idProducto, nuevoPrecio);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean crearDescuentoVolumen(String nombre, double precioMinimo,
            double porcentaje, LocalDateTime inicio, LocalDateTime fin) {
        boolean ok = gestor.crearDescuentoVolumen(nombre, precioMinimo,
            porcentaje, inicio, fin);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean crearDescuentoCategoria(String nombre, String categoria,
            double porcentaje, LocalDateTime inicio, LocalDateTime fin) {
        boolean ok = gestor.crearDescuentoCategoria(nombre, categoria,
            porcentaje, inicio, fin);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean crearDescuentoCantidad(String nombre, String idProducto,
            int cantidadMinima, double porcentaje,
            LocalDateTime inicio, LocalDateTime fin) {
        boolean ok = gestor.crearDescuentoCantidad(nombre, idProducto,
            cantidadMinima, porcentaje, inicio, fin);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean crearDescuentoRegalo(String nombre, String idProducto,
            double gastoNecesario, LocalDateTime inicio, LocalDateTime fin) {
        boolean ok = gestor.crearDescuentoRegalo(nombre, idProducto,
            gastoNecesario, inicio, fin);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean eliminarDescuento(String idDescuento) {
        boolean ok = gestor.eliminarDescuento(idDescuento);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public List<Descuento> getDescuentosActivos() {
        return tienda.getDescuentosActivos();
    }

    public List<Descuento> getDescuentos() {
        return tienda.getHistorialDescuentos();
    }

    public List<ProductoVenta> getProductos() {
        return tienda.getStockVentas();
    }

    public List<Categoria> getCategorias() {
        return tienda.getCategorias();
    }
}
