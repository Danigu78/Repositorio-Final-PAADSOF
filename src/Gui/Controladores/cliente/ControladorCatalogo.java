package Gui.Controladores.cliente;

import java.util.List;
import java.util.ArrayList;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;
import Gui.cliente.SubpanelCatalogo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del catálogo de productos. Implementa ActionListener según el
 * patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCatalogo implements ActionListener {

	private Tienda tienda;
	private Cliente cliente;
	private SubpanelCatalogo vista;

	public ControladorCatalogo(Cliente cliente, SubpanelCatalogo vista) {
		this.tienda = Tienda.getInstancia();
		this.cliente = cliente;
		this.vista = vista;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("buscar")) {
			vista.buscar();
		} else if (cmd.equals("reset")) {
			vista.resetearFiltros();
		} else if (cmd.startsWith("ver:")) {
			String idProducto = cmd.substring(4);
			for (ProductoVenta p : tienda.getStockVentas()) {
				if (p.getId().equals(idProducto)) {
					vista.verProducto(p);
					return;
				}
			}
		}
	}

	/**
	 * Devuelve todos los productos con stock disponible.
	 */
	public List<ProductoVenta> obtenerTodosLosProductos() {
		return tienda.buscarProductoVenta();
	}

	/**
	 * Aplica filtros combinados de nombre, categoría y precio.
	 */
	public List<ProductoVenta> filtrarProductos(String texto, String nombreCategoria, double precioMin,
			double precioMax) {
		List<ProductoVenta> resultado = new ArrayList<>();
		for (ProductoVenta p : tienda.getStockVentas()) {
			if (p.getStockDisponible() <= 0)
				continue;
			if (texto != null && !texto.isBlank()) {
				if (!p.getNombre().toLowerCase().contains(texto.toLowerCase()))
					continue;
			}
			if (nombreCategoria != null && !nombreCategoria.equals("Todas")) {
				boolean tieneCategoria = false;
				for (productos.Categoria c : p.getCategorias()) {
					if (c.getNombre().equals(nombreCategoria)) {
						tieneCategoria = true;
						break;
					}
				}
				if (!tieneCategoria)
					continue;
			}
			if (p.getPrecioOficial() < precioMin || p.getPrecioOficial() > precioMax)
				continue;
			resultado.add(p);
		}
		return resultado;
	}

	/**
	 * Devuelve los nombres de todas las categorías con "Todas" al principio.
	 */
	public List<String> obtenerNombresCategorias() {
		List<String> nombres = new ArrayList<>();
		nombres.add("Todas");
		tienda.getCategorias().forEach(c -> nombres.add(c.getNombre()));
		return nombres;
	}

	/**
	 * Añade un producto al carrito del cliente.
	 */
	public boolean añadirAlCarrito(ProductoVenta producto, int cantidad) {
		if (cliente == null)
			return false;
		return cliente.añadirProductoCarrito(producto, cantidad);
	}

	/**
	 * Ordena una lista por ID ascendente.
	 */
	public List<ProductoVenta> ordenarPorId(List<ProductoVenta> productos) {
		List<ProductoVenta> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> {
			int idA = extraerNumeroId(a.getId());
			int idB = extraerNumeroId(b.getId());
			return Integer.compare(idA, idB);
		});
		return resultado;
	}

	/**
	 * Ordena una lista por nombre A-Z.
	 */
	public List<ProductoVenta> ordenarPorNombreAsc(List<ProductoVenta> productos) {
		List<ProductoVenta> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
		return resultado;
	}

	/**
	 * Ordena una lista por nombre Z-A.
	 */
	public List<ProductoVenta> ordenarPorNombreDesc(List<ProductoVenta> productos) {
		List<ProductoVenta> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> b.getNombre().compareToIgnoreCase(a.getNombre()));
		return resultado;
	}

	/**
	 * Ordena una lista por precio menor a mayor.
	 */
	public List<ProductoVenta> ordenarPorPrecioAsc(List<ProductoVenta> productos) {
		List<ProductoVenta> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> Double.compare(a.getPrecioOficial(), b.getPrecioOficial()));
		return resultado;
	}

	/**
	 * Ordena una lista por precio mayor a menor.
	 */
	public List<ProductoVenta> ordenarPorPrecioDesc(List<ProductoVenta> productos) {
		List<ProductoVenta> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> Double.compare(b.getPrecioOficial(), a.getPrecioOficial()));
		return resultado;
	}

	/**
	 * Ordena una lista por puntuación menor a mayor.
	 */
	public List<ProductoVenta> ordenarPorPuntuacionAsc(List<ProductoVenta> productos) {
		List<ProductoVenta> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> Double.compare(a.getMediaPuntuacion(), b.getMediaPuntuacion()));
		return resultado;
	}

	/**
	 * Ordena una lista por puntuación mayor a menor.
	 */
	public List<ProductoVenta> ordenarPorPuntuacionDesc(List<ProductoVenta> productos) {
		List<ProductoVenta> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> Double.compare(b.getMediaPuntuacion(), a.getMediaPuntuacion()));
		return resultado;
	}

	/**
	 * Extrae el número del ID de un producto (ej: "PV-3" → 3).
	 */
	private int extraerNumeroId(String id) {
		if (id == null)
			return 0;
		try {
			String[] partes = id.split("-");
			return Integer.parseInt(partes[partes.length - 1]);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Devuelve los productos recomendados. Para clientes usa el recomendador
	 * personalizado. Para invitados devuelve los mejor valorados.
	 */
	public List<ProductoVenta> getRecomendados() {
		if (cliente == null) {
			List<ProductoVenta> todos = new ArrayList<>(tienda.getStockVentas());
			todos.removeIf(p -> p.getStockDisponible() <= 0);
			todos.sort((a, b) -> Double.compare(b.getMediaPuntuacion(), a.getMediaPuntuacion()));
			return todos.subList(0, Math.min(5, todos.size()));
		}
		try {
			return tienda.getRecomendador().generarSugerencias(cliente);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	/**
	 * Indica si hay cliente logueado.
	 */
	public boolean hayCliente() {
		return cliente != null;
	}

	/**
	 * Establece el cliente logueado.
	 */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}