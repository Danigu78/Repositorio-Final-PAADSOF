package Gui.Controladores.Gestor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import Gui.Gestor.SubpanelCategoriasGestor;
import Gui.Controladores.empleado.ControladorProductosEmpleado;
import productos.Categoria;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;

/**
 * Controlador de la gestión de categorías para el gestor.
 * 
 * @author Antonino
 * @version 1.0
 */
public class ControladorCategoriasGestor implements ActionListener {

	/** Comando para crear una categoría. */
	public static final String CREAR_CATEGORIA = "crearCategoria";

	/** Comando para añadir una categoría a un producto. */
	public static final String ANADIR_CATEGORIA_PRODUCTO = "anadirCategoriaProducto";

	/** Comando para quitar una categoría de un producto. */
	public static final String QUITAR_CATEGORIA_PRODUCTO = "quitarCategoriaProducto";

	/** Comando para eliminar una categoría. */
	public static final String ELIMINAR_CATEGORIA = "eliminarCategoria";

	/** Vista asociada al subpanel de categorías. */
	private SubpanelCategoriasGestor vista;

	/** Gestor autenticado */
	private Gestor gestor;

	/** Instancia de la tienda. */
	private Tienda tienda;

	/** Controlador auxiliar de productos. */
	private ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

	/**
	 * Constructor del controlador de categorías.
	 * 
	 * @param vista  El subpanel de categorías
	 * @param gestor El gestor autenticado
	 */
	public ControladorCategoriasGestor(SubpanelCategoriasGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos producidos en la vista.
	 * 
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(CREAR_CATEGORIA)) {
			vista.procesarCrearCategoria();

		} else if (cmd.equals(ANADIR_CATEGORIA_PRODUCTO)) {
			vista.procesarAnadirCategoriaProducto();

		} else if (cmd.equals(QUITAR_CATEGORIA_PRODUCTO)) {
			vista.procesarQuitarCategoriaProducto();
		} else if (cmd.startsWith(ELIMINAR_CATEGORIA + ":")) {
			vista.procesarEliminarCategoria(cmd.substring(ELIMINAR_CATEGORIA.length() + 1));
		}
	}

	/**
	 * Crea una nueva categoría en la tienda.
	 * 
	 * @param nombre      Nombre de la categoría
	 * @param descripcion Descripción de la categoría
	 * @return true si se crea correctamente, false en caso contrario
	 */
	public boolean crearCategoria(String nombre, String descripcion) {
		boolean ok = gestor.crearCategoria(nombre, descripcion);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Añade un producto a una categoría
	 * 
	 * @param idProducto ID del producto
	 * @param nombreCat  Nombre de la categoría
	 * @return true si la operación se realiza correctamente
	 */
	public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
		boolean ok = gestor.añadirProductoACategoria(idProducto, nombreCat);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Elimina un producto de una categoría.
	 * 
	 * @param idProducto ID del producto
	 * @param nombreCat  Nombre de la categoría
	 * @return true si la operación se realiza correctamente
	 */
	public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
		boolean ok = gestor.eliminarProductoDeCategoria(idProducto, nombreCat);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Devuelve todas las categorías de la tienda.
	 * 
	 * @return Lista de categorías
	 */
	public List<Categoria> getCategorias() {
		return tienda.getCategorias();
	}

	/**
	 * Devuelve únicamente las categorías activas.
	 * 
	 * @return Lista de categorías activas
	 */
	public List<Categoria> getCategoriasActivas() {
		return tienda.getCategoriasActivas();
	}

	/**
	 * Obtiene los productos ordenados por stock.
	 * 
	 * @return Lista de productos ordenados
	 */
	public List<ProductoVenta> getProductosOrdenados() {
		return productos.obtenerProductosOrdenadosPorStock();
	}

	/**
	 * Devuelve el tipo legible de un producto.
	 * 
	 * @param producto El producto
	 * @return Tipo del producto como String
	 */
	public String obtenerTipoProductoVenta(ProductoVenta producto) {
		return productos.obtenerTipoProductoVenta(producto);
	}

	/**
	 * Devuelve las categorías de un producto formateadas como texto.
	 * 
	 * @param producto El producto
	 * @return Texto con las categorías
	 */
	public String obtenerTextoCategorias(ProductoVenta producto) {
		return productos.obtenerTextoCategorias(producto);
	}

	/**
	 * Comprueba si un producto pertenece a una categoría.
	 * 
	 * @param idProducto      ID del producto
	 * @param nombreCategoria Nombre de la categoría
	 * @return true si el producto pertenece a la categoría
	 */
	public boolean productoTieneCategoria(String idProducto, String nombreCategoria) {
		if (idProducto == null || nombreCategoria == null) {
			return false;
		}

		ProductoVenta producto = tienda.buscarProductoVentaPorId(idProducto.trim());
		Categoria categoria = tienda.buscarCategoriaPorNombre(nombreCategoria.trim());

		return producto != null && categoria != null && producto.getCategorias().contains(categoria);
	}

	/**
	 * Formatea un precio para mostrarlo en la interfaz.
	 * 
	 * @param precio Precio a formatear
	 * @return Precio formateado
	 */
	public String formatearPrecio(double precio) {
		return productos.formatearPrecio(precio);
	}

	/**
	 * Formatea una puntuación para mostrarla en la interfaz.
	 * 
	 * @param puntuacion Puntuación a formatear
	 * @return Puntuación formateada
	 */
	public String formatearPuntuacion(double puntuacion) {
		return productos.formatearPuntuacion(puntuacion);
	}

	/**
	 * Elimina una categoría de la tienda.
	 * 
	 * @param nombreCat Nombre de la categoría
	 * @return true si se elimina correctamente
	 */
	public boolean eliminarCategoria(String nombreCat) {
		boolean ok = gestor.eliminarCategoria(nombreCat);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}
}