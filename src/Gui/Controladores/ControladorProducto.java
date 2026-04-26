package Gui.Controladores;

import Gui.SubpanelProducto;
import productos.ProductoVenta;
import usuarios.Cliente;

/**
 * Controlador del subpanel de detalle de producto. Gestiona la lógica de añadir
 * al carrito.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorProducto {

	/** Vista del subpanel producto */
	private SubpanelProducto vista;

	/** Controlador del catálogo para añadir al carrito */
	private ControladorCatalogo controladorCatalogo;

	/** Cliente actualmente logueado, null si es invitado */
	private Cliente cliente;

	/**
	 * Constructor del controlador de producto.
	 *
	 * @param vista               El subpanel producto
	 * @param controladorCatalogo El controlador del catálogo
	 * @param cliente             El cliente logueado o null si es invitado
	 */
	public ControladorProducto(SubpanelProducto vista, ControladorCatalogo controladorCatalogo, Cliente cliente) {
		this.vista = vista;
		this.controladorCatalogo = controladorCatalogo;
		this.cliente = cliente;
	}

	/**
	 * Indica si hay un cliente logueado. Si es false el botón de carrito no debe
	 * mostrarse.
	 *
	 * @return true si hay cliente, false si es invitado
	 */
	public boolean hayCliente() {
		return cliente != null;
	}

	/**
     * Añade el producto al carrito del cliente.
     * Delega en el controlador del catálogo.
     *
     * @param producto El producto a añadir
     * @param cantidad La cantidad a añadir
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
	 * Vuelve al catálogo desde la pantalla de detalle.
	 */
	public void volver() {
		vista.volver();
	}
}