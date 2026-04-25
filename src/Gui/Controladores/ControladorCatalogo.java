package Gui.Controladores;

import java.util.List;
import java.util.ArrayList;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;
import Gui.SubpanelCatalogo;

/**
 * Controlador del catálogo de productos.
 * Usa los métodos ya existentes en Tienda y Cliente
 * en vez de reimplementar la lógica.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCatalogo {

    /** Instancia de la tienda */
    private Tienda tienda;

    /** Cliente actualmente logueado */
    private Cliente cliente;

    /** Referencia a la vista del catálogo */
    private SubpanelCatalogo vista;

    /**
     * Constructor del controlador.
     *
     * @param cliente El cliente logueado
     * @param vista   El subpanel del catálogo
     */
    public ControladorCatalogo(Cliente cliente, SubpanelCatalogo vista) {
        this.tienda = Tienda.getInstancia();
        this.cliente = cliente;
        this.vista = vista;
    }

    /**
     * Navega a la pantalla de detalle del producto.
     * Delega en la vista para mostrar el producto.
     *
     * @param producto El producto a mostrar
     */
    public void verProducto(ProductoVenta producto) {
        vista.verProducto(producto);
    }

    /**
     * Devuelve todos los productos con stock disponible.
     *
     * @return Lista de productos disponibles
     */
    public List<ProductoVenta> obtenerTodosLosProductos() {
        return tienda.buscarProductoVenta();
    }

    /**
     * Busca productos por nombre.
     *
     * @param texto Texto a buscar
     * @return Lista de productos que contienen el texto
     */
    public List<ProductoVenta> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            return obtenerTodosLosProductos();
        }
        List<ProductoVenta> resultado = tienda.buscarproductoPorNombre(texto);
        return resultado != null ? resultado : new ArrayList<>();
    }

    /**
     * Filtra productos por categoría.
     *
     * @param nombreCategoria Nombre de la categoría
     * @return Lista de productos de esa categoría
     */
    public List<ProductoVenta> filtrarPorCategoria(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.equals("Todas")) {
            return obtenerTodosLosProductos();
        }
        return tienda.buscarProductoPorCategoria(nombreCategoria);
    }

    /**
     * Aplica filtros combinados de nombre, categoría y precio.
     *
     * @param texto           Texto a buscar
     * @param nombreCategoria Categoría a filtrar
     * @param precioMin       Precio mínimo
     * @param precioMax       Precio máximo
     * @return Lista de productos filtrados
     */
    public List<ProductoVenta> filtrarProductos(String texto, String nombreCategoria,
                                                 double precioMin, double precioMax) {
        List<ProductoVenta> resultado = new ArrayList<>();

        for (ProductoVenta p : tienda.getStockVentas()) {
            if (p.getStockDisponible() <= 0) continue;

            if (texto != null && !texto.isBlank()) {
                if (!p.getNombre().toLowerCase().contains(texto.toLowerCase())) continue;
            }

            if (nombreCategoria != null && !nombreCategoria.equals("Todas")) {
                boolean tieneCategoria = false;
                for (productos.Categoria c : p.getCategorias()) {
                    if (c.getNombre().equals(nombreCategoria)) {
                        tieneCategoria = true;
                        break;
                    }
                }
                if (!tieneCategoria) continue;
            }

            if (p.getPrecioOficial() < precioMin || p.getPrecioOficial() > precioMax) continue;

            resultado.add(p);
        }

        return resultado;
    }

    /**
     * Devuelve los nombres de todas las categorías con "Todas" al principio.
     *
     * @return Lista de nombres de categorías
     */
    public List<String> obtenerNombresCategorias() {
        List<String> nombres = new ArrayList<>();
        nombres.add("Todas");
        tienda.getCategorias().forEach(c -> nombres.add(c.getNombre()));
        return nombres;
    }

    /**
     * Añade un producto al carrito del cliente.
     * Si no hay cliente devuelve false.
     *
     * @param producto El producto a añadir
     * @param cantidad La cantidad
     * @return true si se añadió correctamente
     */
    public boolean añadirAlCarrito(ProductoVenta producto, int cantidad) {
        if (cliente == null) return false;
        return cliente.añadirProductoCarrito(producto, cantidad);
    }

    /**
     * Actualiza el cliente del controlador.
     *
     * @param cliente El nuevo cliente logueado
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}