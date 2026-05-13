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
 * Controlador de productos y descuentos para el gestor. Gestiona las
 * operaciones relacionadas con la modificación de precios y la creación,
 * eliminación y consulta de descuentos desde la vista correspondiente.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorProductosDescuentosGestor implements ActionListener {

	/** Comando para cambiar manualmente el precio de un producto. */
	public static final String CAMBIAR_PRECIO_MANUAL = "cambiarPrecioManual";

	/** Comando para crear un descuento. */
	public static final String CREAR_DESCUENTO = "crearDescuento";

	/** comando para eliminar un descuento. */
	public static final String ELIMINAR_DESCUENTO = "eliminarDescuento:";

	/** Vista asociada a productos y descuentos. */
	private SubpanelProductosDescuentosGestor vista;

	/** Gestor logueado que realiza las operaciones. */
	private Gestor gestor;

	/** Instancia única de la tienda. */
	private Tienda tienda;

	/**
	 * Constructor del controlador de productos y descuentos.
	 *
	 * @param vista  Vista asociada al controlador
	 * @param gestor Gestor logueado
	 */
	public ControladorProductosDescuentosGestor(SubpanelProductosDescuentosGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos lanzados desde la vista de productos y descuentos.
	 *
	 * @param e Evento recibido
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();

		if (CAMBIAR_PRECIO_MANUAL.equals(cmd)) {
			vista.procesarCambiarPrecioManual();

		} else if (CREAR_DESCUENTO.equals(cmd)) {
			vista.procesarCrearDescuento();

		} else if (cmd != null && cmd.startsWith(ELIMINAR_DESCUENTO)) {
			vista.procesarEliminarDescuento(cmd.substring(ELIMINAR_DESCUENTO.length()));
		}
	}

	/**
	 * Modifica manualmente el precio de un producto.
	 *
	 * @param idProducto  ID del producto
	 * @param nuevoPrecio Nuevo precio del producto
	 * @return true si el precio se modifica correctamente
	 */
	public boolean modificarPrecio(String idProducto, double nuevoPrecio) {
		boolean ok = gestor.modificarPrecioProducto(idProducto, nuevoPrecio);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	/**
	 * Crea un descuento por volumen de compra.
	 *
	 * @param nombre       Nombre del descuento
	 * @param precioMinimo Precio mínimo requerido
	 * @param porcentaje   Porcentaje de descuento
	 * @param inicio       Fecha de inicio
	 * @param fin          Fecha de finalización
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
	 * @param nombre     Nombre del descuento
	 * @param categoria  Categoría asociada
	 * @param porcentaje Porcentaje de descuento
	 * @param inicio     Fecha de inicio
	 * @param fin        Fecha de finalización
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
	 * Crea un descuento basado en una cantidad mínima de producto.
	 *
	 * @param nombre         Nombre del descuento
	 * @param idProducto     ID del producto
	 * @param cantidadMinima Cantidad mínima requerida
	 * @param porcentaje     Porcentaje de descuento
	 * @param inicio         Fecha de inicio
	 * @param fin            Fecha de finalización
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
	 * Crea un descuento con producto de regalo.
	 *
	 * @param nombre         Nombre del descuento
	 * @param idProducto     ID del producto regalo
	 * @param gastoNecesario Gasto mínimo requerido
	 * @param inicio         Fecha de inicio
	 * @param fin            Fecha de finalización
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
	 * @return true si el descuento se elimina correctamente
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
	 * Devuelve los productos de venta disponibles en la tienda.
	 *
	 * @return Lista de productos
	 */
	public List<ProductoVenta> getProductos() {
		return tienda.getStockVentas();
	}

	/**
	 * Devuelve las categorías registradas en la tienda.
	 *
	 * @return Lista de categorías
	 */
	public List<Categoria> getCategorias() {
		return tienda.getCategorias();
	}
}
