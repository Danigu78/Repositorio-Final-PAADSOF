package productos;

import java.io.*;
import java.util.*;

import excepciones.*;
import tienda.Estadistica;

/**
 * Clase abstracta para productos destinados a la venta directa.
 * 
 * Añade a Producto los datos propios de los productos que se venden en la
 * tienda: precio, stock y reseñas.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public abstract class ProductoVenta extends Producto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Precio base de venta al público. */
	protected double precioOficial;

	/** Cantidad de unidades disponibles en el inventario. */
	protected int stockDisponible;

	/** Opiniones y puntuaciones que han dejado los clientes. */
	protected ArrayList<Reseña> reseñas;

	/** Indica si el producto se ha quitado de la tienda. */
	private boolean eliminado = false;

	/**
	 * Constructor de la clase ProductoVenta.
	 *
	 * @param nombre          nombre del producto
	 * @param descripcion     descripción del producto
	 * @param imagenRuta      ruta de la imagen asociada
	 * @param precioOficial   precio base del producto
	 * @param stockDisponible cantidad disponible en stock
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
		this.reseñas = new ArrayList<>();
	}

	/**
	 * Calcula la puntuación media a partir de las reseñas del producto.
	 *
	 * @return media de puntuaciones, o 0 si no tiene reseñas
	 */
	public double getMediaPuntuacion() {
		if (this.reseñas.isEmpty()) {
			return 0;
		}

		double suma = 0;

		for (Reseña r : this.reseñas) {
			suma += r.getPuntuacion();
		}

		return suma / this.reseñas.size();
	}

	/**
	 * Recupera las reseñas del producto.
	 *
	 * @return lista de reseñas asociadas
	 */
	public ArrayList<Reseña> getReseñas() {
		return this.reseñas;
	}

	/**
	 * Devuelve el stock disponible del producto.
	 *
	 * @return unidades que quedan disponibles
	 */
	public int getStockDisponible() {
		return this.stockDisponible;
	}

	/**
	 * Actualiza la cantidad disponible del producto.
	 *
	 * @param cantidad nuevo stock
	 */
	public void setStockDisponible(int cantidad) {
		if (cantidad < 0) {
			throw new ProductoInvalidoException("El stock disponible no puede ser negativo.");
		}

		this.stockDisponible = cantidad;
	}

	public boolean isEliminado() {
		return eliminado;
	}

	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}

	/**
	 * Añade una categoría al producto y actualiza también la categoría.
	 *
	 * @param c categoría que se quiere añadir
	 * @return true si se añade correctamente
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
	 * Elimina una categoría del producto y actualiza también la categoría.
	 *
	 * @param c categoría que se quiere quitar
	 * @return true si se elimina correctamente
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
	 * Añade internamente una categoría al producto.
	 * 
	 * Se usa para evitar llamadas infinitas al mantener la relación producto -
	 * categoría por los dos lados.
	 *
	 * @param c categoría a añadir
	 * @return true si se añade, false si no
	 */
	protected boolean addCategoriaInterno(Categoria c) {
		if (c == null || this.categorias.contains(c)) {
			return false;
		}

		this.categorias.add(c);
		return true;
	}

	/**
	 * Elimina internamente una categoría del producto.
	 *
	 * @param c categoría a eliminar
	 * @return true si se elimina, false si no
	 */
	protected boolean deleteCategoriaInterno(Categoria c) {
		if (c == null || !this.categorias.contains(c)) {
			return false;
		}

		this.categorias.remove(c);
		return true;
	}

	/**
	 * Añade una reseña al producto.
	 *
	 * @param r reseña que se quiere guardar
	 * @return true si se añade correctamente
	 */
	public boolean addReseña(Reseña r) {
		if (r == null) {
			return false;
		}

		if (this.reseñas.contains(r)) {
			return false;
		}

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
	 * Elimina una reseña del producto.
	 *
	 * @param r reseña que se quiere borrar
	 * @return true si se elimina, false si no existe
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
	 * Recupera el precio oficial del producto.
	 *
	 * @return precio actual
	 */
	public double getPrecioOficial() {
		return this.precioOficial;
	}

	/**
	 * Cambia el precio oficial del producto.
	 *
	 * @param precioOficial nuevo precio
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
	 * Genera un resumen breve del producto.
	 *
	 * @return cadena con la información principal del producto
	 */
	public String resumen() {
		String cats = "";

		for (Categoria c : categorias) {
			cats += c.getNombre() + " ";
		}

		String valoracion = reseñas.isEmpty() ? "Sin reseñas" : String.format("%.1f", getMediaPuntuacion()) + "/10";

		return "[" + id + "] " + nombre + " | Precio: " + precioOficial + "€" + " | Stock: " + stockDisponible
				+ " | Puntuacion: " + valoracion + " | Categorias: " + (cats.isBlank() ? "ninguna" : cats.trim());
	}

	/**
	 * Muestra por pantalla las categorías a las que pertenece el producto.
	 */
	public void imprimirCategorias() {
		if (categorias.isEmpty()) {
			System.out.println("  [" + id + "] " + nombre + " -> sin categorias");
			return;
		}

		String cats = "";

		for (Categoria c : categorias) {
			if (!cats.equals("")) {
				cats += ", ";
			}

			cats += c.getNombre();
		}

		System.out.println("  [" + id + "] " + nombre + " -> " + cats);
	}

	/**
	 * Devuelve una representación general del producto con sus datos más
	 * importantes.
	 *
	 * @return texto con tipo, precio, stock, puntuación y categorías
	 */
	@Override
	public String toString() {
		String tipo = "Producto";

		if (this instanceof Comic) {
			tipo = "CÓMIC";
		} else if (this instanceof Figura) {
			tipo = "FIGURA";
		} else if (this instanceof JuegoMesa) {
			tipo = "JUEGO";
		} else if (this instanceof Pack) {
			tipo = "PACK";
		}

		String cats = "";

		for (Categoria c : categorias) {
			cats += c.getNombre() + " ";
		}

		String valoracion = reseñas.isEmpty() ? "Sin reseñas" : String.format("%.1f", getMediaPuntuacion()) + "/5";

		return "[" + tipo + "][" + id + "] " + nombre + " | Precio: " + precioOficial + "€" + " | Stock: "
				+ stockDisponible + " | Puntuación: " + valoracion + " | Categorías: "
				+ (cats.isBlank() ? "ninguna" : cats.trim());
	}

	/**
	 * Método llamado automáticamente cuando se guarda un ProductoVenta en fichero.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		inicializarCamposNulos();
		out.defaultWriteObject();
	}

	/**
	 * Método llamado automáticamente cuando se carga un ProductoVenta desde
	 * fichero.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		inicializarCamposNulos();
	}

	/**
	 * Evita que las listas queden a null al guardar o cargar.
	 */
	private void inicializarCamposNulos() {
		if (this.reseñas == null) {
			this.reseñas = new ArrayList<>();
		}

		if (this.categorias == null) {
			this.categorias = new ArrayList<>();
		}
	}
}
