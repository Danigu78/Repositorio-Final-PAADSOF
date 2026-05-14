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
 * Controlador de la gestión de categorías para el gestor. Gestiona la creación,
 * eliminación y asignación de categorías a productos desde la vista del gestor.
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

	/** Vista asociada a la gestión de categorías. */
	private SubpanelCategoriasGestor vista;

	/** Gestor logueado que realiza las operaciones. */
	private Gestor gestor;

	/** Instancia única de la tienda. */
	private Tienda tienda;

	/** Controlador para operaciones auxiliares sobre productos. */
	private ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

	/**Comando para refrescar la tabla*/
	public static final String REFRESCAR_TABLA = "refrescarTablaCategoria";

	
	
	
	/**
	 * Constructor del controlador de categorías del gestor.
	 *
	 * @param vista  Vista asociada al controlador
	 * @param gestor Gestor logueado
	 */
	public ControladorCategoriasGestor(SubpanelCategoriasGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos lanzados desde la vista de categorías.
	 *
	 * @param e Evento recibido
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();

		if (CREAR_CATEGORIA.equals(cmd)) {
			vista.procesarCrearCategoria();

		} else if (ANADIR_CATEGORIA_PRODUCTO.equals(cmd)) {
			vista.procesarAnadirCategoriaProducto();

		} else if (QUITAR_CATEGORIA_PRODUCTO.equals(cmd)) {
			vista.procesarQuitarCategoriaProducto();
		} else if (cmd != null && cmd.startsWith(ELIMINAR_CATEGORIA + ":")) {
			vista.procesarEliminarCategoria(cmd.substring(ELIMINAR_CATEGORIA.length() + 1));
		} else if (cmd.equals(REFRESCAR_TABLA)) {
		    vista.refrescarTablaProductos();
		}
	}

	/**
	 * Crea una nueva categoría en la tienda.
	 *
	 * @param nombre      Nombre de la categoría
	 * @param descripcion Descripción de la categoría
	 * @return true si la categoría se crea correctamente
	 */
	public boolean crearCategoria(String nombre, String descripcion) {
		boolean ok = gestor.crearCategoria(nombre, descripcion);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Añade un producto a una categoría existente.
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
	 * Devuelve únicamente las categorías activas de la tienda.
	 *
	 * @return Lista de categorías activas
	 */
	public List<Categoria> getCategoriasActivas() {
		return tienda.getCategoriasActivas();
	}

	/**
	 * Devuelve los productos ordenados por stock.
	 *
	 * @return Lista de productos ordenados
	 */
	public List<ProductoVenta> getProductosOrdenados() {
		return productos.obtenerProductosOrdenadosPorStock();
	}

	/**
	 * Obtiene el tipo textual de un producto de venta.
	 *
	 * @param producto Producto a consultar
	 * @return Tipo del producto en formato texto
	 */
	public String obtenerTipoProductoVenta(ProductoVenta producto) {
		return productos.obtenerTipoProductoVenta(producto);
	}

	/**
	 * Obtiene el texto formateado de las categorías de un producto.
	 *
	 * @param producto Producto a consultar
	 * @return Texto con las categorías del producto
	 */
	public String obtenerTextoCategorias(ProductoVenta producto) {
		return productos.obtenerTextoCategorias(producto);
	}

	/**
	 * Comprueba si un producto pertenece a una categoría concreta.
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
	 * @param nombreCat Nombre de la categoría a eliminar
	 * @return true si la categoría se elimina correctamente
	 */
	public boolean eliminarCategoria(String nombreCat) {
		boolean ok = gestor.eliminarCategoria(nombreCat);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}
}
