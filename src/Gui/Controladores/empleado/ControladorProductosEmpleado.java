package Gui.Controladores.empleado;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import productos.Categoria;
import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;
import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.Tienda;

/**
 * Controlador común para consultar productos de venta desde las secciones del
 * empleado.
 */
public class ControladorProductosEmpleado {

    public List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
        ArrayList<ProductoVenta> productos = new ArrayList<>(Tienda.getInstancia().getStockVentas());
        productos.sort(Comparator.comparingInt(ProductoVenta::getStockDisponible)
                .thenComparing(ProductoVenta::getNombre, String.CASE_INSENSITIVE_ORDER));
        return productos;
    }

    public ProductoVenta buscarProductoVentaPorId(String idProducto) {
        if (idProducto == null || idProducto.trim().isBlank()) {
            return null;
        }
        return Tienda.getInstancia().buscarProductoVentaPorId(idProducto.trim());
    }

    public List<String> obtenerNombresCategoriasVenta() {
        TreeSet<String> nombres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Categoria categoria : Tienda.getInstancia().getCategorias()) {
            if (categoria != null && categoria.getNombre() != null && !categoria.getNombre().isBlank()) {
                nombres.add(categoria.getNombre().trim());
            }
        }
        return new ArrayList<>(nombres);
    }

    public String obtenerTextoCategorias(ProductoVenta producto) {
        if (producto == null || producto.getCategorias().isEmpty()) {
            return "-";
        }

        List<String> nombres = new ArrayList<>();
        for (Categoria categoria : producto.getCategorias()) {
            if (categoria != null && categoria.getNombre() != null && !categoria.getNombre().isBlank()) {
                nombres.add(categoria.getNombre().trim());
            }
        }

        nombres.sort(String.CASE_INSENSITIVE_ORDER);
        return nombres.isEmpty() ? "-" : String.join(", ", nombres);
    }

    public String obtenerTipoProductoVenta(ProductoVenta producto) {
        if (producto instanceof Comic) {
            return "Comic";
        }
        if (producto instanceof JuegoMesa) {
            return "Juego";
        }
        if (producto instanceof Figura) {
            return "Figura";
        }
        if (producto instanceof Pack) {
            return "Pack";
        }
        return "Producto";
    }

    public ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
        ArrayList<LineaPack> lineas = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return lineas;
        }

        String[] filas = texto.split("\\r?\\n");
        for (String fila : filas) {
            if (fila == null || fila.isBlank()) {
                continue;
            }

            String[] partes = fila.split(";");
            if (partes.length != 2) {
                throw new IllegalArgumentException("Cada línea debe tener formato ID;UNIDADES");
            }

            String idProducto = partes[0].trim();
            int unidades;
            try {
                unidades = Integer.parseInt(partes[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Las unidades deben ser un número entero.");
            }

            ProductoVenta producto = buscarProductoVentaPorId(idProducto);
            if (producto == null) {
                throw new IllegalArgumentException("No existe producto con id " + idProducto);
            }
            if (unidades <= 0) {
                throw new IllegalArgumentException("Las unidades deben ser mayores que 0.");
            }

            lineas.add(new LineaPack(producto, unidades));
        }
        return lineas;
    }

    public String formatearPrecio(double precio) {
        return String.format(java.util.Locale.US, "%.2f €", precio).replace('.', ',');
    }

    public String formatearPuntuacion(double puntuacion) {
        return String.format(java.util.Locale.US, "%.1f", puntuacion).replace('.', ',');
    }
}
