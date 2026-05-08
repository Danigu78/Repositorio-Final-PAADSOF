package Gui.Controladores;

import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;

/** Controlador de la sección de modificación de productos. */
public class ControladorModificarEmpleado {

    private final Empleado empleado;
    private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

    public ControladorModificarEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public ProductoVenta buscarProducto(String idProducto) {
        return productos.buscarProductoVentaPorId(idProducto);
    }

    public ResultadoOperacion guardarDescripcion(String idProducto, String descripcion) {
        if (empleado == null) {
            return ResultadoOperacion.error("No hay empleado activo.");
        }
        if (idProducto == null || idProducto.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del producto.");
        }
        if (descripcion == null || descripcion.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe una descripción.");
        }

        boolean ok = empleado.modificarDescripcionProducto(idProducto.trim(), descripcion.trim());
        guardarSiExito(ok);
        return ok ? ResultadoOperacion.ok("Descripción guardada correctamente.")
                : ResultadoOperacion.error("No se pudo guardar la descripción.");
    }

    public ResultadoOperacion guardarImagen(String idProducto, String imagen) {
        if (empleado == null) {
            return ResultadoOperacion.error("No hay empleado activo.");
        }
        if (idProducto == null || idProducto.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del producto.");
        }
        if (imagen == null || imagen.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe una ruta de imagen.");
        }

        boolean ok = empleado.modificarImagenProducto(idProducto.trim(), imagen.trim());
        guardarSiExito(ok);
        return ok ? ResultadoOperacion.ok("Imagen guardada correctamente.")
                : ResultadoOperacion.error("No se pudo guardar la imagen.");
    }

    private void guardarSiExito(boolean ok) {
        if (ok) {
            GuardadoTienda.guardar(Tienda.getInstancia());
        }
    }
}
