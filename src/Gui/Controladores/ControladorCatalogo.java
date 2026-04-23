package Gui.Controladores;

import java.util.List;
import java.util.ArrayList;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;

/**
 * Controlador del catálogo de productos. Usa los métodos ya existentes en
 * Tienda y Cliente en vez de reimplementar la lógica.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCatalogo {

	/** Instancia de la tienda */
	private Tienda tienda;

	/** Cliente actualmente logueado */
	private Cliente cliente;

	/**
	 * Constructor del controlador.
	 *
	 * @param cliente El cliente logueado
	 */
	public ControladorCatalogo(Cliente cliente) {
		this.tienda = Tienda.getInstancia();
		this.cliente = cliente;
	}

	/**
	 * Devuelve todos los productos con stock disponible. Usa buscarProductoVenta()
	 * de Tienda que ya filtra por stock.
	 *
	 * @return Lista de productos disponibles
	 */
	public List<ProductoVenta> obtenerTodosLosProductos() {
		// buscarProductoVenta() ya devuelve solo los que tienen stock > 0
		return tienda.buscarProductoVenta();
	}

	/**
	 * Busca productos por nombre. Usa buscarproductoPorNombre() de Tienda.
	 *
	 * @param texto Texto a buscar
	 * @return Lista de productos que contienen el texto
	 */
	public List<ProductoVenta> buscarPorNombre(String texto) {
		if (texto == null || texto.isBlank()) {
			return obtenerTodosLosProductos();
		}
		// Usamos el método ya existente en Tienda
		List<ProductoVenta> resultado = tienda.buscarproductoPorNombre(texto);
		return resultado != null ? resultado : new ArrayList<>();
	}

	/**
	 * Filtra productos por categoría. Usa buscarProductoPorCategoria() de Tienda.
	 *
	 * @param nombreCategoria Nombre de la categoría
	 * @return Lista de productos de esa categoría
	 */
	public List<ProductoVenta> filtrarPorCategoria(String nombreCategoria) {
		if (nombreCategoria == null || nombreCategoria.equals("Todas")) {
			return obtenerTodosLosProductos();
		}
		// Usamos el método ya existente en Tienda
		return tienda.buscarProductoPorCategoria(nombreCategoria);
	}

	/**
	 * Aplica filtros combinados usando los métodos del cliente. Usa
	 * filtrarProductos() de UsuarioRegistrado.
	 *
	 * @param texto           Texto a buscar
	 * @param nombreCategoria Categoría a filtrar
	 * @param precioMin       Precio mínimo
	 * @param precioMax       Precio máximo
	 * @return Lista de productos filtrados
	 */
	public List<ProductoVenta> filtrarProductos(String texto, String nombreCategoria, double precioMin,
			double precioMax) {
		List<ProductoVenta> resultado = new ArrayList<>();

// Recorremos todos los productos de la tienda
		for (ProductoVenta p : tienda.getStockVentas()) {

// Solo productos con stock
			if (p.getStockDisponible() <= 0)
				continue;

// Filtro por nombre — si el texto no está vacío comprobamos
			if (texto != null && !texto.isBlank()) {
				if (!p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
					continue; // No coincide con el nombre, pasamos al siguiente
				}
			}

// Filtro por categoría — si no es "Todas" comprobamos
			if (nombreCategoria != null && !nombreCategoria.equals("Todas")) {
				boolean tieneCategoria = false;
				for (productos.Categoria c : p.getCategorias()) {
					if (c.getNombre().equals(nombreCategoria)) {
						tieneCategoria = true;
						break;
					}
				}
				if (!tieneCategoria)
					continue; // No tiene esa categoría
			}

// Filtro por precio
			if (p.getPrecioOficial() < precioMin || p.getPrecioOficial() > precioMax) {
				continue; // Fuera del rango de precio
			}

// Si pasa todos los filtros lo añadimos
			resultado.add(p);
		}

		return resultado;
	}

	/**
	 * Devuelve los nombres de todas las categorías.
	 *
	 * @return Lista de nombres de categorías con "Todas" al principio
	 */
	public List<String> obtenerNombresCategorias() {
		List<String> nombres = new ArrayList<>();
		nombres.add("Todas");
		tienda.getCategorias().forEach(c -> nombres.add(c.getNombre()));
		return nombres;
	}

	/**
	 * Añade un producto al carrito del cliente. Usa añadirProductoCarrito() de
	 * Cliente.
	 *
	 * @param producto El producto a añadir
	 * @param cantidad La cantidad
	 * @return true si se añadió correctamente
	 */
	public boolean añadirAlCarrito(ProductoVenta producto, int cantidad) {
		if (cliente == null)
			return false;
		// Usamos el método ya existente en Cliente
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