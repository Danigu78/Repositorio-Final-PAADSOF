package productos;

import java.io.Serializable;
import java.util.ArrayList;

import excepciones.*;

/**
 * Clase abstracta que define la estructura base de cualquier producto del
 * sistema.
 * 
 * Guarda los datos comunes que van a tener tanto los productos de venta como
 * los productos de segunda mano.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public abstract class Producto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Identificador único del producto. */
	protected String id;

	/** Nombre del producto. */
	protected String nombre;

	/** Descripción del producto. */
	protected String descripcion;

	/** Ruta del archivo de imagen del producto. */
	protected String imagenRuta;

	/** Categorías a las que pertenece el producto. */
	protected ArrayList<Categoria> categorias;

	/**
	 * Constructor de la clase Producto.
	 *
	 * @param nombre      nombre del producto
	 * @param descripcion descripción del producto
	 * @param imagenRuta  ruta o nombre del archivo de imagen
	 */
	public Producto(String nombre, String descripcion, String imagenRuta) {
		if (nombre == null || nombre.isBlank()) {
			throw new ProductoInvalidoException("El nombre del producto no puede estar vacío.");
		}

		if (descripcion == null || descripcion.isBlank()) {
			throw new ProductoInvalidoException("La descripción del producto no puede estar vacía.");
		}

		if (imagenRuta == null || imagenRuta.isBlank()) {
			throw new ProductoInvalidoException("La ruta de la imagen no puede estar vacía.");
		}

		this.id = "0";
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.imagenRuta = imagenRuta;
		this.categorias = new ArrayList<>();
	}

	/**
	 * Devuelve el identificador del producto.
	 *
	 * @return id del producto
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Devuelve el nombre del producto.
	 *
	 * @return nombre del producto
	 */
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			throw new ProductoInvalidoException("El nombre del producto no puede estar vacío.");
		}

		this.nombre = nombre;
	}

	/**
	 * Devuelve la descripción del producto.
	 *
	 * @return descripción guardada
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

	/**
	 * Cambia la descripción del producto.
	 *
	 * @param descripcion nueva descripción
	 */
	public void setDescripcion(String descripcion) {
		if (descripcion == null || descripcion.isBlank()) {
			throw new ProductoInvalidoException("La descripción del producto no puede estar vacía.");
		}

		this.descripcion = descripcion;
	}

	/**
	 * Devuelve la ruta de la imagen del producto.
	 *
	 * @return ruta de la imagen
	 */
	public String getImagenRuta() {
		return this.imagenRuta;
	}

	/**
	 * Cambia la ruta de la imagen del producto.
	 *
	 * @param imagenRuta nueva ruta de la imagen
	 */
	public void setImagenRuta(String imagenRuta) {
		if (imagenRuta == null || imagenRuta.isBlank()) {
			throw new ProductoInvalidoException("La ruta de la imagen no puede estar vacía.");
		}

		this.imagenRuta = imagenRuta;
	}

	/**
	 * Devuelve las categorías del producto.
	 *
	 * @return lista de categorías
	 */
	public ArrayList<Categoria> getCategorias() {
		return this.categorias;
	}

	/**
	 * Cambia la lista de categorías del producto.
	 *
	 * @param categorias nuevas categorías
	 */
	public void setCategorias(ArrayList<Categoria> categorias) {
		if (categorias == null) {
			this.categorias = new ArrayList<>();
		} else {
			this.categorias = categorias;
		}
	}

	/**
	 * Añade una categoría al producto si todavía no la tiene.
	 *
	 * @param categoria categoría que se quiere añadir
	 * @return true si se añade, false si no se puede
	 */
	public boolean añadirCategoria(Categoria categoria) {
		if (categoria == null) {
			return false;
		}

		if (this.categorias.contains(categoria)) {
			return false;
		}

		this.categorias.add(categoria);
		return true;
	}

	/**
	 * Quita una categoría del producto.
	 *
	 * @param categoria categoría que se quiere quitar
	 * @return true si se ha quitado, false si no estaba
	 */
	public boolean eliminarCategoria(Categoria categoria) {
		if (categoria == null) {
			return false;
		}

		return this.categorias.remove(categoria);
	}

	/**
	 * Muestra una representación sencilla del producto.
	 *
	 * @return texto con id, nombre e imagen
	 */
	@Override
	public String toString() {
		return "[" + this.id + "] " + this.nombre + " | Imagen: " + this.imagenRuta + " |";
	}
}
