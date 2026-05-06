package Gui.Controladores;

import java.util.List;

import usuarios.Empleado;

/** Controlador de la sección de categorías. */
public class ControladorCategoriasEmpleado {

    private final Empleado empleado;
    private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

    public ControladorCategoriasEmpleado(Empleado empleado) {
        this.empleado = empleado;
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
        return ok ? ResultadoOperacion.ok("Categoría añadida correctamente")
                : ResultadoOperacion.error("No se pudo añadir la categoría");
    }

    public ResultadoOperacion quitarCategoria(String idProducto, String categoria) {
        ResultadoOperacion validacion = validar(idProducto, categoria);
        if (!validacion.isExito()) {
            return validacion;
        }
        boolean ok = empleado.eliminarProductoDeCategoria(idProducto.trim(), categoria.trim());
        return ok ? ResultadoOperacion.ok("Categoría quitada correctamente")
                : ResultadoOperacion.error("No se pudo quitar la categoría");
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
