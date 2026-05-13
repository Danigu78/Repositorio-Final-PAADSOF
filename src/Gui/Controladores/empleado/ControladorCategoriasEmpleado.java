package Gui.Controladores.empleado;

import Gui.empleado.SeccionCategoriasEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import productos.Categoria;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;

/**
*Controlador de la sección de categorías.
*
* @author Antonino
* @version 1.0
*/


public class ControladorCategoriasEmpleado implements ActionListener {

	/** Acción para añadir una categoría a un producto. */
	public static final String ANADIR_CATEGORIA = "categorias.anadir";
	
	/** Acción para quitar una categoría de un producto. */
	public static final String QUITAR_CATEGORIA = "categorias.quitar";

	/** Empleado autenticado que ejecuta las operaciones. */
	private final Empleado empleado;
	
	/**
	 * Controlador auxiliar utilizado para acceder a información
	 * de productos y categorías.
	 */
	private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();
	
	/** Vista asociada al controlador. */
	private SeccionCategoriasEmpleado vista;

	/**
	 * Constructor del controlador.
	 *
	 * @param empleado empleado que realiza las operaciones sobre categorías.
	 */
	public ControladorCategoriasEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asocia la vista a este controlador.
	 *
	 * @param vista vista de categorías del empleado.
	 */
	public void setVista(SeccionCategoriasEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona los eventos generados desde la interfaz gráfica.
	 *
	 * @param e evento de acción generado por la interfaz.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (ANADIR_CATEGORIA.equals(accion)) {
			vista.anadirCategoria();
		} else if (QUITAR_CATEGORIA.equals(accion)) {
			vista.quitarCategoria();
		}
	}

	/**
	 * Obtiene la lista de nombres de categorías de productos de venta.
	 *
	 * @return lista de nombres de categorías disponibles.
	 */
	public List<String> getNombresCategorias() {
		return productos.obtenerNombresCategoriasVenta();
	}

	/**
	 * Añade un producto a una categoría.
	 *
	 * @param idProducto ID del producto.
	 * @param categoria nombre de la categoría.
	 * @return resultado de la operación con mensaje descriptivo.
	 */
	public ResultadoOperacion anadirCategoria(String idProducto, String categoria) {
		ResultadoOperacion validacion = validar(idProducto, categoria);
		if (!validacion.isExito()) {
			return validacion;
		}
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto.trim());
		if (producto == null) {
			return ResultadoOperacion.error("No existe ningún producto con ese ID.");
		}
		Categoria categoriaTienda = Tienda.getInstancia().buscarCategoriaPorNombre(categoria.trim());
		if (categoriaTienda == null || categoriaTienda.isEliminada()) {
			return ResultadoOperacion.error("No existe esa categoría.");
		}
		if (producto.getCategorias().contains(categoriaTienda)) {
			return ResultadoOperacion.error("El producto ya tiene esa categoría.");
		}

		boolean ok = empleado.añadirProductoACategoria(idProducto.trim(), categoria.trim());
		guardarSiExito(ok);
		return ok ? ResultadoOperacion.ok("Categoría añadida correctamente")
				: ResultadoOperacion.error("No se pudo añadir la categoría");
	}

	/**
	 * Elimina un producto de una categoría.
	 *
	 * @param idProducto ID del producto.
	 * @param categoria nombre de la categoría.
	 * @return resultado de la operación con mensaje descriptivo.
	 */
	public ResultadoOperacion quitarCategoria(String idProducto, String categoria) {
		ResultadoOperacion validacion = validar(idProducto, categoria);
		if (!validacion.isExito()) {
			return validacion;
		}
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto.trim());
		if (producto == null) {
			return ResultadoOperacion.error("No existe ningún producto con ese ID.");
		}
		Categoria categoriaTienda = Tienda.getInstancia().buscarCategoriaPorNombre(categoria.trim());
		if (categoriaTienda == null || categoriaTienda.isEliminada()) {
			return ResultadoOperacion.error("No existe esa categoría.");
		}
		if (!producto.getCategorias().contains(categoriaTienda)) {
			return ResultadoOperacion.error("El producto no tiene esa categoría.");
		}

		boolean ok = empleado.eliminarProductoDeCategoria(idProducto.trim(), categoria.trim());
		guardarSiExito(ok);
		return ok ? ResultadoOperacion.ok("Categoría quitada correctamente")
				: ResultadoOperacion.error("No se pudo quitar la categoría");
	}

	/**
	 * Guarda el estado actual de la tienda si la operación
	 * realizada ha tenido éxito.
	 *
	 * @param ok indica si la operación fue correcta.
	 */
	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Valida los datos necesarios para operar con categorías.
	 *
	 * @param idProducto ID del producto.
	 * @param categoria nombre de la categoría.
	 * @return resultado de validación.
	 */
	private ResultadoOperacion validar(String idProducto, String categoria) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto");
		}
		if (categoria == null || categoria.trim().isBlank()) {
			return ResultadoOperacion.error("Selecciona una categoría");
		}
		return ResultadoOperacion.ok("Datos válidos");
	}
}
