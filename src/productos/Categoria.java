package productos;

import java.util.*;

import excepciones.ProductoYaEnCategoriaException;

/**
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Categoria {

	private String nombre;
	private String descripcion;
	private ArrayList<ProductoVenta> productos;

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
}
