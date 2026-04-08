package tienda;

import java.util.ArrayList;
import java.util.List;
import productos.Categoria;
import productos.ProductoVenta;

/**
 * Clase encargada de filtrar productos de venta nueva por precio, puntuación y
 * categorías.
 * 
 * @author Danie Gonzalez
 * @version 1.0
 */
public class FiltroVenta {
	/** Límite inferior del precio de venta aceptado. */
	private double precioMinimo;

	/** Límite superior del precio de venta aceptado. */
	private double precioMaximo;

	/** Calificación media mínima que debe tener el producto (basada en reseñas). */
	private double puntuacionMinima;

	/** Lista de categorías permitidas; si está vacía, se aceptan todas. */
	private List<Categoria> categorias;

	/**
	 * Constructor de la clase FiltroVenta
	 */
	public FiltroVenta() {
		resetear();
	}

	/**
	 * Comprueba si un producto cumple las condiciones del filtro
	 *
	 * @param p el producto que se quiere revisar
	 * @return true si pasa el filtro, false en caso contrario
	 */
	public boolean productoCumpleFiltro(ProductoVenta p) {
		if (p == null)
			return false;

		if (p.getPrecioOficial() < precioMinimo || p.getPrecioOficial() > precioMaximo)
			return false;

		if (p.getMediaPuntuacion() < puntuacionMinima)
			return false;

		if (!categorias.isEmpty()) {
			boolean tieneCategoria = false;
			for (Categoria cat : categorias) {
				if (p.getCategorias().contains(cat)) {
					tieneCategoria = true;
					break;
				}
			}
			if (!tieneCategoria)
				return false;
		}

		return true;
	}

	/**
	 * Devuelve el filtro a sus valores iniciales
	 */
	public void resetear() {
		this.precioMinimo = 0;
		this.precioMaximo = Double.MAX_VALUE;
		this.puntuacionMinima = 0;
		this.categorias = new ArrayList<>();
	}

	/**
	 * Recupera el precio mínimo del filtro
	 *
	 * @return el precio mínimo configurado
	 */
	public double getPrecioMinimo() {
		return precioMinimo;
	}

	/**
	 * Recupera el precio máximo del filtro
	 *
	 * @return el precio máximo configurado
	 */
	public double getPrecioMaximo() {
		return precioMaximo;
	}

	/**
	 * Devuelve la puntuación mínima del filtro
	 *
	 * @return la puntuación mínima establecida
	 */
	public double getPuntuacionMinima() {
		return puntuacionMinima;
	}

	/**
	 * Recupera las categorías del filtro
	 *
	 * @return la lista de categorías añadidas
	 */
	public List<Categoria> getCategorias() {
		return categorias;
	}

	/**
	 * Cambia el precio mínimo del filtro
	 *
	 * @param precioMinimo el nuevo precio mínimo
	 */
	public void setPrecioMinimo(double precioMinimo) {
		if (precioMinimo < 0) {
			System.out.println("El precio minimo no puede ser negativo.");
			return;
		}
		if (precioMinimo > this.precioMaximo) {
			System.out.println("El precio minimo no puede ser mayor que el maximo.");
			return;
		}
		this.precioMinimo = precioMinimo;
	}

	/**
	 * Cambia el precio máximo del filtro
	 *
	 * @param precioMaximo el nuevo precio máximo
	 */
	public void setPrecioMaximo(double precioMaximo) {
		if (precioMaximo < this.precioMinimo) {
			System.out.println("El precio maximo no puede ser menor que el minimo.");
			return;
		}
		this.precioMaximo = precioMaximo;
	}

	/**
	 * Establece la puntuación mínima del filtro
	 *
	 * @param puntuacionMinima la nueva puntuación mínima
	 */
	public void setPuntuacionMinima(double puntuacionMinima) {
		if (puntuacionMinima < 0 || puntuacionMinima > 10) {
			System.out.println("La puntuacion minima debe estar entre 0 y 10.");
			return;
		}
		this.puntuacionMinima = puntuacionMinima;
	}

	/**
	 * Añade una categoría al filtro
	 *
	 * @param c la categoría que se quiere añadir
	 */
	public void añadirCategoria(Categoria c) {
		if (c != null && !categorias.contains(c))
			categorias.add(c);
	}

	/**
	 * Quita una categoría del filtro
	 *
	 * @param c la categoría que se quiere eliminar
	 */
	public void eliminarCategoria(Categoria c) {
		categorias.remove(c);
	}

	/**
	 * Devuelve un texto con la configuración actual del filtro
	 *
	 * @return una cadena con sus valores principales
	 */
	@Override
	public String toString() {
		String cats = categorias.isEmpty() ? "todas"
				: categorias.stream().map(Categoria::getNombre).reduce((a, b) -> a + ", " + b).orElse("");
		return "FiltroVenta [" + "precio: " + precioMinimo + "-"
				+ (precioMaximo == Double.MAX_VALUE ? "MAX" : precioMaximo) + " | puntuacion min: " + puntuacionMinima
				+ " | categorias: " + cats + "]";
	}
}