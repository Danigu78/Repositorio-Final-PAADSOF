package Gui.Controladores;

import java.util.List;
import java.util.ArrayList;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Cliente;
import Gui.SubpanelCatalogo;
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
	 * Devuelve los productos recomendados. Para clientes usa el recomendador
	 * personalizado. Para invitados devuelve los mejor valorados.
	 */
	public List<ProductoVenta> getRecomendados() {
		if (cliente == null) {
			// Invitado — el recomendador no acepta null
			// mostramos los mejor valorados directamente
			List<ProductoVenta> todos = new ArrayList<>(tienda.getStockVentas());
			todos.removeIf(p -> p.getStockDisponible() <= 0);
			todos.sort((a, b) -> Double.compare(b.getMediaPuntuacion(), a.getMediaPuntuacion()));
			return todos.subList(0, Math.min(5, todos.size()));
		}
		// Cliente registrado — recomendador personalizado
		// si no tiene compras ya devuelve los mejor valorados internamente
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

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}