package productos;

import excepciones.*;

/**
 * Clase abstracta que define la estructura base de cualquier artículo del
 * sistema. Proporciona los atributos comunes de identificación, denominación y
 * descripcion .
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public abstract class Producto {
	/** Identificador único del producto. */
	protected String id;
	/** Nombre del producto. */
	protected String nombre;
	/** Descripción del producto. */
	protected String descripcion;
	/** Ruta del archivo de imagen del producto. */
	protected String imagenRuta;

	/**
	 * Constructor de la clase Producto
	 *
	 * @param nombre      el nombre del producto
	 * @param descripcion una pequeña descripción del producto
	 * @param imagenRuta  la ruta de la imagen asociada
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
	}

	/**
	 * Devuelve el identificador del producto
	 *
	 * @return el id del producto
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Recupera el nombre del producto
	 *
	 * @return el nombre actual
	 */
	public String getNombre() {
		return this.nombre;
	}

	/**
	 * Muestra una representación sencilla del producto
	 *
	 * @return un texto con su id, nombre e imagen
	 */
	@Override
	public String toString() {
		return "[" + this.id + "] " + this.getNombre() + " | Imagen: " + this.getImagenRuta() + " |";
	}

	/**
	 * Recupera la descripción del producto
	 *
	 * @return la descripción guardada
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Cambia la descripción del producto
	 *
	 * @param descripcion la nueva descripción
	 */
	public void setDescripcion(String descripcion) {
		if (descripcion == null || descripcion.isBlank()) {
			throw new ProductoInvalidoException("La descripción del producto no puede estar vacía.");
		}
		this.descripcion = descripcion;
	}

	/**
	 * Recupera la ruta de la imagen del producto
	 *
	 * @return la ruta de la imagen
	 */
	public String getImagenRuta() {
		return imagenRuta;
	}

	/**
	 * Actualiza la ruta de la imagen del producto
	 *
	 * @param imagenRuta la nueva ruta de la imagen
	 */
	public void setImagenRuta(String imagenRuta) {
		if (imagenRuta == null || imagenRuta.isBlank()) {
			throw new ProductoInvalidoException("La ruta de la imagen no puede estar vacía.");
		}
		this.imagenRuta = imagenRuta;
	}
}
