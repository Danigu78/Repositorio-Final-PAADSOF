package Gui.Controladores.empleado;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import productos.Categoria;
import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;
import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.Tienda;

/**
* Ayuda a sacar datos de productos para las pantallas del empleado.
* 
* @author Lucas
* @version 1.0
*/
public class ControladorProductosEmpleado implements ActionListener {


	/**
	 * Gestiona las acciones del controlador.
	 * 
	 * @param e evento producido
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// No tiene botones propios, se usa para consultas de productos.
	}

	/**
	 * Obtiene los productos ordenados por stock y nombre.
	 * 
	 * @return lista de productos ordenados
	 */
	public List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
		ArrayList<ProductoVenta> productos = new ArrayList<>(Tienda.getInstancia().getStockVentas());
		productos.sort(new java.util.Comparator<ProductoVenta>() {
			@Override
			public int compare(ProductoVenta p1, ProductoVenta p2) {
				int comparaStock = Integer.compare(p1.getStockDisponible(), p2.getStockDisponible());
				if (comparaStock != 0) {
					return comparaStock;
				}
				return p1.getNombre().compareToIgnoreCase(p2.getNombre());
			}
		});
		return productos;
	}

	/**
	 * Busca un producto de venta por su ID.
	 * 
	 * @param idProducto ID del producto
	 * @return producto encontrado o null
	 */
	public ProductoVenta buscarProductoVentaPorId(String idProducto) {
		if (idProducto == null || idProducto.trim().isBlank()) {
			return null;
		}
		return Tienda.getInstancia().buscarProductoVentaPorId(idProducto.trim());
	}

	/**
	 * Obtiene los nombres de las categorías activas.
	 * 
	 * @return lista de nombres de categorías
	 */
	public List<String> obtenerNombresCategoriasVenta() {
		ArrayList<String> nombres = new ArrayList<>();
		for (Categoria categoria : Tienda.getInstancia().getCategoriasActivas()) {
			if (categoria != null && categoria.getNombre() != null && !categoria.getNombre().isBlank()) {
				String nombre = categoria.getNombre().trim();
				if (!estaEnLista(nombres, nombre)) {
					nombres.add(nombre);
				}
			}
		}

		nombres.sort(new java.util.Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareToIgnoreCase(b);
			}
		});
		return new ArrayList<>(nombres);
	}

	/**
	 * Obtiene el texto con las categorías de un producto.
	 * 
	 * @param producto producto del que sacar categorías
	 * @return texto con las categorías
	 */
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

		nombres.sort(new java.util.Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareToIgnoreCase(b);
			}
		});
		return nombres.isEmpty() ? "-" : String.join(", ", nombres);
	}

	/**
	 * Obtiene el tipo de un producto de venta.
	 * 
	 * @param producto producto a comprobar
	 * @return tipo del producto
	 */
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
	
	/**
	 * Construye una lista de líneas de pack a partir de un texto.
	 * Cada línea del texto debe seguir el formato: ID;UNIDADES.
	 *
	 * @param texto texto con las líneas del pack
	 * @return lista de objetos LineaPack construidos a partir del texto
	 * @throws Exception si el formato es incorrecto o los datos no son válidos
	 */
	public ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		if (texto == null || texto.isBlank()) {
			return lineas;
		}

		// Cada linea del textarea tiene que ser ID;UNIDADES.
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

	/**
	 * Formatea un precio en euros.
	 * 
	 * @param precio precio a formatear
	 * @return texto con el precio formateado
	 */
	public String formatearPrecio(double precio) {
		return String.format(java.util.Locale.US, "%.2f €", precio).replace('.', ',');
	}

	/**
	 * Formatea una puntuación decimal.
	 * 
	 * @param puntuacion puntuación a formatear
	 * @return texto con la puntuación formateada
	 */
	public String formatearPuntuacion(double puntuacion) {
		return String.format(java.util.Locale.US, "%.1f", puntuacion).replace('.', ',');
	}

	/**
	 * Comprueba si un texto ya está en una lista.
	 * 
	 * @param textos lista de textos
	 * @param buscado texto buscado
	 * @return true si existe, false en caso contrario
	 */
	private boolean estaEnLista(List<String> textos, String buscado) {
		for (String texto : textos) {
			if (texto.equalsIgnoreCase(buscado)) {
				return true;
			}
		}
		return false;
	}
}
