package Gui.Controladores.cliente;

import Gui.cliente.SubpanelProducto;
import productos.ProductoVenta;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del subpanel de detalle de producto. 
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorProducto implements ActionListener {
	/** Vista del subpanel de producto. */
	private SubpanelProducto vista;
	/** Controlador del catálogo asociado. */
	private ControladorCatalogo controladorCatalogo;
	/** Cliente logueado. */
	private Cliente cliente;
	/**
	 * Construye el controlador del producto.
	 *
	 * @param vista vista del producto
	 * @param controladorCatalogo controlador del catálogo
	 * @param cliente cliente logueado
	 * @return void
	 */
	public ControladorProducto(SubpanelProducto vista, ControladorCatalogo controladorCatalogo, Cliente cliente) {
		this.vista = vista;
		this.controladorCatalogo = controladorCatalogo;
		this.cliente = cliente;
	}

/**
 * Gestiona los eventos de la vista.
 *
 * @param e evento de acción
 * @return void
 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		if ("volver".equals(cmd)) {
			volver();
		} else if (cmd.equals("añadirCarrito")) {
			vista.seleccionarUnidades();
		}
	}

/**
 * Indica si hay cliente logueado.
 *
 * @return true si hay cliente, false en caso contrario
 */
	public boolean hayCliente() {
		return cliente != null;
	}
	/**
	 * Añade un producto al carrito.
	 *
	 * @param producto producto a añadir
	 * @param cantidad unidades a añadir
	 * @return void
	 */
	public void añadirAlCarrito(ProductoVenta producto, int cantidad) {
		boolean ok = controladorCatalogo.añadirAlCarrito(producto, cantidad);
		if (ok) {
			vista.mostrarExito(producto.getNombre() + " x" + cantidad + " añadido al carrito.");
			vista.mostrarProducto(producto, cliente, controladorCatalogo);
		} else {
			vista.mostrarError("No se pudo añadir al carrito.");
		}
	}
	/**
	 * Comprueba si el cliente ya ha reseñado el producto.
	 *
	 * @param producto producto a comprobar
	 * @return true si ya existe reseña del cliente
	 */
	public boolean yaReseñó(ProductoVenta producto) {
		if (cliente == null)
			return false;
		return producto.getReseñas().stream()
				.anyMatch(r -> r.getAutor() != null && r.getAutor().getNickname().equals(cliente.getNickname()));
	}
	/**
	 * Vuelve a la vista anterior.
	 *
	 * @return void
	 */
	public void volver() {
		vista.volver();
	}
}
