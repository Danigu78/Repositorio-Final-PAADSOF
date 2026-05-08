package Gui.Controladores.empleado;

import Gui.empleado.SeccionCategoriasEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;

/** Controlador de la sección de categorías. */
public class ControladorCategoriasEmpleado implements ActionListener {

    public static final String ANADIR_CATEGORIA = "categorias.anadir";
    public static final String QUITAR_CATEGORIA = "categorias.quitar";

    private final Empleado empleado;
    private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();
    private SeccionCategoriasEmpleado vista;

    public ControladorCategoriasEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setVista(SeccionCategoriasEmpleado vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (vista == null || e == null) {
            return;
        }

        String accion = e.getActionCommand();
        if (ANADIR_CATEGORIA.equals(accion)) {
            vista.anadirCategoria();
        } else if (QUITAR_CATEGORIA.equals(accion)) {
            vista.quitarCategoria();
        }
    }

    public List<String> getNombresCategorias() {
        return productos.obtenerNombresCategoriasVenta();
    }

    public ResultadoOperacion anadirCategoria(String idProducto, String categoria) {
        ResultadoOperacion validacion = validar(idProducto, categoria);
        if (!validacion.isExito()) {
            return validacion;
        }
        boolean ok = empleado.añadirProductoACategoria(idProducto.trim(), categoria.trim());
        guardarSiExito(ok);
        return ok ? ResultadoOperacion.ok("Categoría añadida correctamente")
                : ResultadoOperacion.error("No se pudo añadir la categoría");
    }

    public ResultadoOperacion quitarCategoria(String idProducto, String categoria) {
        ResultadoOperacion validacion = validar(idProducto, categoria);
        if (!validacion.isExito()) {
            return validacion;
        }
        boolean ok = empleado.eliminarProductoDeCategoria(idProducto.trim(), categoria.trim());
        guardarSiExito(ok);
        return ok ? ResultadoOperacion.ok("Categoría quitada correctamente")
                : ResultadoOperacion.error("No se pudo quitar la categoría");
    }

    private void guardarSiExito(boolean ok) {
        if (ok) {
            GuardadoTienda.guardar(Tienda.getInstancia());
        }
    }

    private ResultadoOperacion validar(String idProducto, String categoria) {
        if (empleado == null) {
            return ResultadoOperacion.error("No hay empleado activo.");
        }
        if (idProducto == null || idProducto.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del producto");
        }
        if (categoria == null || categoria.trim().isBlank()) {
            return ResultadoOperacion.error("Selecciona una categoría");
        }
        return ResultadoOperacion.ok("Datos válidos");
    }
}
