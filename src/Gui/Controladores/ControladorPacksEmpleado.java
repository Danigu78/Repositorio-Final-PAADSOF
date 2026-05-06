package Gui.Controladores;

import java.util.ArrayList;

import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;

/** Controlador de gestión de packs. */
public class ControladorPacksEmpleado {

    private final Empleado empleado;
    private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

    public ControladorPacksEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
        return productos.construirLineasPack(texto);
    }

    public Pack buscarPack(String idPack) {
        ProductoVenta producto = productos.buscarProductoVentaPorId(idPack);
        if (producto instanceof Pack) {
            return (Pack) producto;
        }
        return null;
    }

    public ResultadoOperacion crearPack(String nombre, String descripcion, String imagen, String precioTexto,
            String stockTexto, String textoLineas) {
        if (empleado == null) {
            return ResultadoOperacion.error("No hay empleado activo.");
        }
        if (nombre == null || nombre.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el nombre del pack.");
        }
        if (descripcion == null || descripcion.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe una descripción.");
        }
        if (imagen == null || imagen.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe la ruta de la imagen.");
        }

        Double precio = leerDouble(precioTexto);
        if (precio == null || precio <= 0) {
            return ResultadoOperacion.error("Escribe un precio válido.");
        }

        Integer stock = leerEntero(stockTexto);
        if (stock == null || stock <= 0) {
            return ResultadoOperacion.error("Escribe un stock válido.");
        }

        try {
            ArrayList<LineaPack> lineas = construirLineasPack(textoLineas);
            if (lineas.size() < 2) {
                return ResultadoOperacion.error("Un pack debe tener al menos dos productos.");
            }
            boolean ok = empleado.crearPack(nombre.trim(), descripcion.trim(), imagen.trim(), precio, stock, lineas);
            return ok ? ResultadoOperacion.ok("Pack creado correctamente.")
                    : ResultadoOperacion.error("No se pudo crear el pack.");
        } catch (Exception e) {
            return ResultadoOperacion.error("No se pudo crear el pack: " + e.getMessage());
        }
    }

    public ResultadoOperacion anadirProductoAPack(String idProducto, String idPack, String unidadesTexto) {
        Integer unidades = leerEntero(unidadesTexto);
        ResultadoOperacion validacion = validarProductoPack(idProducto, idPack, unidades);
        if (!validacion.isExito()) {
            return validacion;
        }
        try {
            boolean ok = empleado.añadirProductoaPack(idProducto.trim(), idPack.trim(), unidades);
            return ok ? ResultadoOperacion.ok("Producto añadido al pack.")
                    : ResultadoOperacion.error("No se pudo añadir el producto.");
        } catch (Exception e) {
            return ResultadoOperacion.error("No se pudo añadir el producto: " + e.getMessage());
        }
    }

    public ResultadoOperacion cambiarUnidadesPack(String idProducto, String idPack, String unidadesTexto) {
        Integer unidades = leerEntero(unidadesTexto);
        ResultadoOperacion validacion = validarProductoPack(idProducto, idPack, unidades);
        if (!validacion.isExito()) {
            return validacion;
        }
        try {
            boolean ok = empleado.modificarUnidadesProductoEnPack(idProducto.trim(), idPack.trim(), unidades);
            return ok ? ResultadoOperacion.ok("Unidades modificadas correctamente.")
                    : ResultadoOperacion.error("No se pudieron modificar las unidades.");
        } catch (Exception e) {
            return ResultadoOperacion.error("No se pudieron modificar las unidades: " + e.getMessage());
        }
    }

    public ResultadoOperacion quitarProductoDelPack(String idPack, String idProducto) {
        if (idPack == null || idPack.trim().isBlank() || idProducto == null || idProducto.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del pack y el ID del producto.");
        }
        boolean ok = empleado.eliminarProductoDePack(idPack.trim(), idProducto.trim());
        return ok ? ResultadoOperacion.ok("Producto quitado del pack.")
                : ResultadoOperacion.error("No se pudo quitar el producto.");
    }

    public ResultadoOperacion cambiarPrecioPack(String idPack, String precioTexto) {
        if (idPack == null || idPack.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del pack.");
        }
        Double precio = leerDouble(precioTexto);
        if (precio == null || precio <= 0) {
            return ResultadoOperacion.error("Escribe un precio válido.");
        }
        boolean ok = empleado.modificarPrecioPack(idPack.trim(), precio);
        return ok ? ResultadoOperacion.ok("Precio modificado correctamente.")
                : ResultadoOperacion.error("No se pudo modificar el precio.");
    }

    public ResultadoOperacion eliminarPack(String idPack) {
        if (idPack == null || idPack.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del pack.");
        }
        boolean ok = empleado.eliminarPack(idPack.trim());
        return ok ? ResultadoOperacion.ok("Pack eliminado correctamente.")
                : ResultadoOperacion.error("No se pudo eliminar el pack.");
    }

    public String crearTextoPack(Pack pack) {
        if (pack == null) {
            return "No existe ningún pack con ese ID.";
        }
        StringBuilder texto = new StringBuilder();
        texto.append("Pack: ").append(pack.getId()).append(" - ").append(pack.getNombre()).append("\n");
        texto.append("Precio: ").append(productos.formatearPrecio(pack.getPrecioOficial())).append("\n");
        texto.append("Stock: ").append(pack.getStockDisponible()).append("\n");
        texto.append("Productos por separado: ").append(productos.formatearPrecio(pack.calcularSumaProductos()))
                .append("\n\n");
        texto.append("Productos incluidos:\n");
        if (pack.getLineas().isEmpty()) {
            texto.append("Sin productos.");
        } else {
            texto.append(crearTextoLineas(new ArrayList<>(pack.getLineas())));
        }
        return texto.toString();
    }

    public String crearTextoLineas(ArrayList<LineaPack> lineas) {
        StringBuilder texto = new StringBuilder();
        for (LineaPack linea : lineas) {
            ProductoVenta producto = linea.getProducto();
            texto.append("- ").append(producto.getId()).append(" | ");
            texto.append(producto.getNombre()).append(" | ");
            texto.append("unidades: ").append(linea.getUnidades()).append(" | ");
            texto.append("subtotal: ").append(productos.formatearPrecio(linea.getSubtotal())).append("\n");
        }
        return texto.toString();
    }

    public String formatearPrecio(double precio) {
        return productos.formatearPrecio(precio);
    }

    private ResultadoOperacion validarProductoPack(String idProducto, String idPack, Integer unidades) {
        if (empleado == null) {
            return ResultadoOperacion.error("No hay empleado activo.");
        }
        if (idPack == null || idPack.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del pack.");
        }
        if (idProducto == null || idProducto.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID del producto.");
        }
        if (unidades == null || unidades <= 0) {
            return ResultadoOperacion.error("Escribe unidades válidas.");
        }
        return ResultadoOperacion.ok("Datos válidos");
    }

    private Integer leerEntero(String texto) {
        if (texto == null || texto.trim().isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double leerDouble(String texto) {
        if (texto == null || texto.trim().isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
