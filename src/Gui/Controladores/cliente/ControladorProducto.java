package Gui.Controladores.cliente;

import Gui.cliente.SubpanelProducto;
import productos.ProductoVenta;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del subpanel de detalle de producto. Implementa ActionListener
 * según el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorProducto implements ActionListener {

	private SubpanelProducto vista;
	private ControladorCatalogo controladorCatalogo;
	private Cliente cliente;

	public ControladorProducto(SubpanelProducto vista, ControladorCatalogo controladorCatalogo, Cliente cliente) {
		this.vista = vista;
		this.controladorCatalogo = controladorCatalogo;
		this.cliente = cliente;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("volver")) {
			volver();
		} else if (cmd.equals("añadirCarrito")) {
			vista.seleccionarUnidades();
		}
	}

	public boolean hayCliente() {
		return cliente != null;
	}

	public void añadirAlCarrito(ProductoVenta producto, int cantidad) {
		boolean ok = controladorCatalogo.añadirAlCarrito(producto, cantidad);
		if (ok) {
			vista.mostrarExito(producto.getNombre() + " x" + cantidad + " añadido al carrito.");
			vista.mostrarProducto(producto, cliente, controladorCatalogo);
		} else {
			vista.mostrarError("No se pudo añadir al carrito.");
		}
	}

	public boolean yaReseñó(ProductoVenta producto) {
		if (cliente == null)
			return false;
		return producto.getReseñas().stream()
				.anyMatch(r -> r.getAutor() != null && r.getAutor().getNickname().equals(cliente.getNickname()));
	}

	public void volver() {
		vista.volver();
	}
}