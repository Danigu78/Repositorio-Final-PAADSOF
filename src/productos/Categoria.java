package productos;

import java.io.*;
import java.util.*;

import excepciones.ProductoYaEnCategoriaException;

/**
 * Representa una categoría de productos en el sistema de la tienda. Permite
 * agrupar productos de venta y gestionar la relación bidireccional entre las
 * categorías y los productos.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Categoria implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Nombre identificativo de la categoría .
	 */
	private String nombre;
	/**
	 * Descripción detallada sobre el tipo de productos que engloba esta categoría.
	 */
	private String descripcion;
	/**
	 * Lista de productos asociados a esta categoría. Mantiene la integridad de la
	 * relación bidireccional con ProductoVenta.
	 */
	private ArrayList<ProductoVenta> productos;
	
	/** Indica si la categoría ha sido eliminada del sistema. */
	private boolean eliminada = false;

	/**
	 * Constructor de la clase categoría
	 *
	 * @param nombre      el nombre de la categoría
	 * @param descripcion breve descripción de la categoría
	 */
	public Categoria(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.productos = new ArrayList<>();
	}

	/**
	 * Añade un producto a la categoría
	 *
	 * @param p el producto que se añade a la categoría
	 * @return true si se añade correctamente, fallo en caso contrario
	 */
	public boolean addProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (this.productos.contains(p)) {
			throw new ProductoYaEnCategoriaException(
					"La categoria " + this.getNombre() + " ya contiene al producto " + p.getNombre() + ".");
		}

		this.productos.add(p);
		p.addCategoriaInterno(this);

		return true;
	}

	/**
	 * Elimina un producto de la categoría
	 *
	 * @param p el producto a eliminar
	 * @return true si se elimina correctamente, false en caso contrario
	 */
	public boolean deleteProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (!this.productos.contains(p)) {
			System.out.println("La categoria " + this.getNombre() + " no contiene al producto " + p.getNombre()
					+ ". No se puede eliminar");
			return false;
		}

		this.productos.remove(p);
		p.deleteCategoriaInterno(this);
		return true;
	}

	/**
	 * Se encarga que la relacion entre categoría y producto es bidireccional, en
	 * este caso, el agregar un producto
	 *
	 * @param p el producto a agregar
	 * @return true si se agrega correctamente, falso en cualquier otro caso
	 */
	protected boolean addProductoInterno(ProductoVenta p) {
		if (p == null || this.productos.contains(p)) {
			return false;
		}
		this.productos.add(p);
		return true;
	}

	/**
	 * Se encarga que la relacion entre categoría y producto es bidireccional, en
	 * este caso, el eliminar un producto
	 *
	 * @param p el producto a eliminar
	 * @return true si se agrega correctamente, falso en cualquier otro caso
	 */
	protected boolean deleteProductoInterno(ProductoVenta p) {
		if (p == null || !this.productos.contains(p)) {
			return false;
		}
		this.productos.remove(p);
		return true;
	}

	/**
	 * Devuelve un string con información del objeto relevante
	 *
	 * @return la cadena de caracteres con la información
	 */
	@Override
	public String toString() {
		String textoProductos = "";

		if (this.productos.isEmpty()) {
			textoProductos = "ninguno";
		} else {
			for (ProductoVenta p : this.productos) {
				textoProductos += "[" + p.getId() + " " + p.getNombre() + "], ";
			}
			textoProductos = textoProductos.substring(0, textoProductos.length() - 2);
		}

		return "| Nombre: " + this.nombre + " | Descripción: " + this.descripcion + " | Productos: " + textoProductos
				+ " |";
	}

	/**
	 * Recupera el nombre de la categoría
	 *
	 * @return el nombre de la categoría
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Cambia el nombre de la categoría
	 *
	 * @param nombre el nuevo nombre de la categoría
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Recupera la lista de los productos de la categoría
	 *
	 * @return la lista de lso productos
	 */
	public ArrayList<ProductoVenta> getProductos() {
		return productos;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		inicializarCamposNulos();
		out.defaultWriteObject();
	}
	/**
	 * Método llamado automáticamente cuando se carga una Categoria desde fichero.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		inicializarCamposNulos();
	}

	/**
	 * Evita que la lista de productos quede a null al guardar/cargar.
	 */
	private void inicializarCamposNulos() {
		if (this.productos == null) {
			this.productos = new ArrayList<>();
		}
	}

	public String getDescripcion() {
		return this.descripcion;
	}
	
	public boolean isEliminada() {
	    return eliminada;
	}

	public void setEliminada(boolean eliminada) {
	    this.eliminada = eliminada;
	}

}
