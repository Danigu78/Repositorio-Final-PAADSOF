package Gui.Controladores.Gestor;

import Gui.Gestor.SubpanelProductosDescuentosGestor;
import productos.Categoria;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;
import ventas.Descuento;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador de productos y descuentos para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorProductosDescuentosGestor implements ActionListener {

	/** Comando para cambiar el precio manualmente. */
	public static final String CAMBIAR_PRECIO_MANUAL = "cambiarPrecioManual";

	/** Comando para crear un descuento. */
	public static final String CREAR_DESCUENTO = "crearDescuento";

	/** Prefijo del comando para eliminar un descuento. */
	public static final String ELIMINAR_DESCUENTO = "eliminarDescuento:";

	/** Vista del subpanel de productos y descuentos. */
	private SubpanelProductosDescuentosGestor vista;

	/** Gestor autenticado. */
	private Gestor gestor;

	/** Instancia de la tienda. */
	private Tienda tienda;

	/**
	 * Constructor del controlador de productos y descuentos.
	 *
	 * @param vista  Vista asociada
	 * @param gestor Gestor autenticado
	 */
	public ControladorProductosDescuentosGestor(SubpanelProductosDescuentosGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos producidos en la vista.
	 *
	 * @param e Evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(CAMBIAR_PRECIO_MANUAL)) {
			vista.procesarCambiarPrecioManual();

		} else if (cmd.equals(CREAR_DESCUENTO)) {
			vista.procesarCrearDescuento();

		} else if (cmd.startsWith(ELIMINAR_DESCUENTO)) {
			vista.procesarEliminarDescuento(cmd.substring(ELIMINAR_DESCUENTO.length()));
		}
	}

	/**
	 * Modifica manualmente el precio de un producto.
	 *
	 * @param idProducto  ID del producto
	 * @param nuevoPrecio Nuevo precio
	 * @return true si la operación se realiza correctamente
	 */
	public boolean modificarPrecio(String idProducto, double nuevoPrecio) {
		boolean ok = gestor.modificarPrecioProducto(idProducto, nuevoPrecio);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Crea un descuento por volumen de gasto.
	 *
	 * @param nombre        Nombre del descuento
	 * @param precioMinimo  Gasto mínimo requerido
	 * @param porcentaje    Porcentaje de descuento
	 * @param inicio        Fecha de inicio
	 * @param fin           Fecha de fin
	 * @return true si el descuento se crea correctamente
	 */
	public boolean crearDescuentoVolumen(String nombre, double precioMinimo, double porcentaje, LocalDateTime inicio,
			LocalDateTime fin) {

		boolean ok = gestor.crearDescuentoVolumen(nombre, precioMinimo, porcentaje, inicio, fin);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Crea un descuento asociado a una categoría.
	 *
	 * @param nombre      Nombre del descuento
	 * @param categoria   Categoría afectada
	 * @param porcentaje  Porcentaje de descuento
	 * @param inicio      Fecha de inicio
	 * @param fin         Fecha de fin
	 * @return true si el descuento se crea correctamente
	 */
	public boolean crearDescuentoCategoria(String nombre, String categoria, double porcentaje, LocalDateTime inicio,
			LocalDateTime fin) {

		boolean ok = gestor.crearDescuentoCategoria(nombre, categoria, porcentaje, inicio, fin);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Crea un descuento por cantidad mínima de unidades.
	 *
	 * @param nombre           Nombre del descuento
	 * @param idProducto       ID del producto
	 * @param cantidadMinima   Cantidad mínima requerida
	 * @param porcentaje       Porcentaje de descuento
	 * @param inicio           Fecha de inicio
	 * @param fin              Fecha de fin
	 * @return true si el descuento se crea correctamente
	 */
	public boolean crearDescuentoCantidad(String nombre, String idProducto, int cantidadMinima, double porcentaje,
			LocalDateTime inicio, LocalDateTime fin) {

		boolean ok = gestor.crearDescuentoCantidad(nombre, idProducto, cantidadMinima, porcentaje, inicio, fin);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Crea un descuento de tipo regalo.
	 *
	 * @param nombre          Nombre del descuento
	 * @param idProducto      ID del producto regalo
	 * @param gastoNecesario  Gasto mínimo requerido
	 * @param inicio          Fecha de inicio
	 * @param fin             Fecha de fin
	 * @return true si el descuento se crea correctamente
	 */
	public boolean crearDescuentoRegalo(String nombre, String idProducto, double gastoNecesario, LocalDateTime inicio,
			LocalDateTime fin) {

		boolean ok = gestor.crearDescuentoRegalo(nombre, idProducto, gastoNecesario, inicio, fin);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Elimina un descuento existente.
	 *
	 * @param idDescuento ID del descuento
	 * @return true si la operación se realiza correctamente
	 */
	public boolean eliminarDescuento(String idDescuento) {
		boolean ok = gestor.eliminarDescuento(idDescuento);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Devuelve los descuentos activos de la tienda.
	 *
	 * @return Lista de descuentos activos
	 */
	public List<Descuento> getDescuentosActivos() {
		return tienda.getDescuentosActivos();
	}

	/**
	 * Devuelve el historial completo de descuentos.
	 *
	 * @return Lista de descuentos
	 */
	public List<Descuento> getDescuentos() {
		return tienda.getHistorialDescuentos();
	}

	/**
	 * Devuelve los productos disponibles para la venta.
	 *
	 * @return Lista de productos
	 */
	public List<ProductoVenta> getProductos() {
		return tienda.getStockVentas();
	}

	/**
	 * Devuelve las categorías de la tienda.
	 *
	 * @return Lista de categorías
	 */
	public List<Categoria> getCategorias() {
		return tienda.getCategorias();
	}
}