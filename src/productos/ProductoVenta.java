package productos;

import java.util.*;

import excepciones.*;
import tienda.Estadistica;

/**
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public abstract class ProductoVenta extends Producto {
	protected double precioOficial;
	protected int stockDisponible;
	protected ArrayList<Reseña> reseñas;
	protected ArrayList<Categoria> categorias;

	/* CONSTRUCTORES DEL PRODUCTO CON DIFERENTES PARAMETROS */

	/**
	 * Constructor de la clase ProductoVenta
	 *
	 * @param nombre          el nombre del producto
	 * @param descripcion     la descripción del producto
	 * @param imagenRuta      la ruta de la imagen asociada
	 * @param precioOficial   el precio base del producto
	 * @param stockDisponible la cantidad disponible en stock
	 */
	public ProductoVenta(String nombre, String descripcion, String imagenRuta, double precioOficial,
			int stockDisponible) {

		super(nombre, descripcion, imagenRuta);

		if (precioOficial < 0) {
			throw new ProductoInvalidoException("El precio oficial no puede ser negativo.");
		}
		if (stockDisponible < 0) {
			throw new ProductoInvalidoException("El stock disponible no puede ser negativo.");
		}

		Estadistica est = Estadistica.getInstancia();
		this.id = "PV-" + est.getnProductosVentas();
		est.setnProductosVentas(est.getnProductosVentas() + 1);
		this.precioOficial = precioOficial;
		this.stockDisponible = stockDisponible;
		this.reseñas = new ArrayList<Reseña>();
		this.categorias = new ArrayList<Categoria>();
	}

	/**
	 * Calcula la puntuación media a partir de las reseñas del producto
	 *
	 * @return la media de puntuaciones o 0 si no tiene reseñas
	 */
	public double getMediaPuntuacion() {
		double suma = 0;
		if (this.reseñas.size() == 0) {
			return 0;
		}

		for (Reseña r : this.reseñas) {
			suma += r.getPuntuacion();
		}

		suma = suma / this.reseñas.size();

		return suma;
	}

	/**
	 * Recupera las reseñas del producto
	 *
	 * @return la lista de reseñas asociadas
	 */
	public ArrayList<Reseña> getReseñas() {
		return this.reseñas;
	}

	/**
	 * Devuelve el stock disponible del producto
	 *
	 * @return las unidades que quedan disponibles
	 */
	public int getStockDisponible() {
		return this.stockDisponible;
	}

	/**
	 * Actualiza la cantidad disponible del producto
	 *
	 * @param cantidad el nuevo stock
	 */
	public void setStockDisponible(int cantidad) {
		if (cantidad < 0) {
			throw new ProductoInvalidoException("El stock disponible no puede ser negativo.");
		}
		this.stockDisponible = cantidad;
	}

	/**
	 * Recupera las categorías del producto
	 *
	 * @return la lista de categorías a las que pertenece
	 */
	public ArrayList<Categoria> getCategorias() {
		return this.categorias;
	}

	/**
	 * Añade una categoría al producto
	 *
	 * @param c la categoría que se quiere añadir
	 * @return true si se añade correctamente, false en caso contrario
	 */
	public boolean addCategoria(Categoria c) {
		if (c == null) {
			return false;
		}
		if (this.categorias.contains(c)) {
			throw new ProductoYaEnCategoriaException(
					"El producto " + this.getNombre() + " ya pertenece a la categoría " + c.getNombre() + ".");
		}

		this.categorias.add(c);
		c.addProductoInterno(this);
		return true;
	}

	/**
	 * Elimina una categoría del producto
	 *
	 * @param c la categoría que se quiere quitar
	 * @return true si se elimina correctamente, false en cualquier otro caso
	 */
	public boolean deleteCategoria(Categoria c) {
		if (c == null) {
			return false;
		}
		if (!this.categorias.contains(c)) {
			return false;
		}

		this.categorias.remove(c);
		c.deleteProductoInterno(this);
		return true;
	}

	/**
	 * Añade internamente una categoría al producto para mantener la relación
	 * bidireccional
	 *
	 * @param c la categoría a añadir
	 * @return true si se añade, false si no se puede hacer
	 */
	protected boolean addCategoriaInterno(Categoria c) {
		if (c == null || this.categorias.contains(c)) {
			return false;
		}
		this.categorias.add(c);
		return true;
	}

	/**
	 * Elimina internamente una categoría del producto para mantener la relación
	 * bidireccional
	 *
	 * @param c la categoría a eliminar
	 * @return true si se elimina, false en caso contrario
	 */
	protected boolean deleteCategoriaInterno(Categoria c) {
		if (c == null || !this.categorias.contains(c)) {
			return false;
		}
		this.categorias.remove(c);
		return true;
	}

	/**
	 * Añade una reseña al producto
	 *
	 * @param r la reseña que se quiere guardar
	 * @return true si se añade correctamente, false si no se puede añadir
	 */
	public boolean addReseña(Reseña r) {
		if (r == null)
			return false;
		if (this.reseñas.contains(r))
			return false;

		for (Reseña existente : this.reseñas) {
			if (existente.getAutor() != null && existente.getAutor().equals(r.getAutor())) {
				throw new ReseñaDuplicadaException("Este cliente ya ha reseñado este producto.");
			}
		}

		this.reseñas.add(r);
		r.setProducto(this);
		return true;
	}

	/**
	 * Elimina una reseña del producto
	 *
	 * @param r la reseña que se quiere borrar
	 * @return true si se elimina, false si no existe o no es válida
	 */
	public boolean deleteReseña(Reseña r) {
		if (r == null) {
			return false;
		}
		if (!this.reseñas.contains(r)) {
			return false;
		}

		this.reseñas.remove(r);
		return true;
	}

	/**
	 * Devuelve una representación general del producto con sus datos más
	 * importantes
	 *
	 * @return un texto con el tipo, precio, stock, puntuación y categorías
	 */
	@Override
	public String toString() {
		String tipo = "Producto";
		if (this instanceof Comic)
			tipo = "CÓMIC";
		else if (this instanceof Figura)
			tipo = "FIGURA";
		else if (this instanceof JuegoMesa)
			tipo = "JUEGO";
		else if (this instanceof Pack)
			tipo = "PACK";

		String cats = "";
		for (Categoria c : categorias)
			cats += c.getNombre() + " ";

		String valoracion = reseñas.isEmpty() ? "Sin reseñas" : String.format("%.1f", getMediaPuntuacion()) + "/5";

		return "[" + tipo + "][" + id + "] " + nombre + " | Precio: " + precioOficial + "€" + " | Stock: "
				+ stockDisponible + " | Puntuación: " + valoracion + " | Categorías: "
				+ (cats.isBlank() ? "ninguna" : cats);
	}

	/**
	 * Recupera el precio oficial del producto
	 *
	 * @return el precio actual
	 */
	public double getPrecioOficial() {
		return precioOficial;
	}

	/**
	 * Cambia el precio oficial del producto
	 *
	 * @param precioOficial el nuevo precio
	 * @return true si se actualiza correctamente
	 */
	public boolean setPrecioOficial(double precioOficial) {
		if (precioOficial < 0) {
			throw new ProductoInvalidoException("El precio oficial no puede ser negativo.");
		}
		this.precioOficial = precioOficial;
		return true;
	}

	/**
	 * Genera un resumen breve del producto
	 *
	 * @return una cadena con la información principal del producto
	 */
	public String resumen() {
		String cats = "";
		for (Categoria c : categorias)
			cats += c.getNombre() + " ";

		String valoracion = reseñas.isEmpty() ? "Sin reseñas" : String.format("%.1f", getMediaPuntuacion()) + "/10";

		return "[" + id + "] " + nombre + " | Precio: " + precioOficial + "€" + " | Stock: " + stockDisponible
				+ " | Puntuacion: " + valoracion + " | Categorias: " + (cats.isBlank() ? "ninguna" : cats.trim());
	}

	/**
	 * Muestra por pantalla las categorías a las que pertenece el producto
	 */
	public void imprimirCategorias() {
		if (categorias.isEmpty()) {
			System.out.println("  [" + id + "] " + nombre + " -> sin categorias");
			return;
		}
		String cats = "";
		for (Categoria c : categorias) {
			if (!cats.equals(""))
				cats += ", ";
			cats += c.getNombre();
		}
		System.out.println("  [" + id + "] " + nombre + " -> " + cats);
	}
}
