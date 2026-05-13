package Gui.Controladores.cliente;

import Gui.cliente.SubpanelProducto2Mano;
import productos.Producto2Mano;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del subpanel de detalle de producto de segunda mano. 
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorProducto2Mano implements ActionListener {
	/** Vista del subpanel de producto de segunda mano. */
	private SubpanelProducto2Mano vista;
	/** Producto de segunda mano mostrado. */
	private Producto2Mano producto;
	/** Cliente logueado. */
	private Cliente cliente;
	/**
	 * Construye el controlador del producto de segunda mano.
	 *
	 * @param vista vista del producto de segunda mano
	 * @param producto producto de segunda mano a mostrar
	 * @param cliente cliente logueado
	 * @return void
	 */
	public ControladorProducto2Mano(SubpanelProducto2Mano vista, Producto2Mano producto, Cliente cliente) {
		this.vista = vista;
		this.producto = producto;
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
		if ("volver".equals(cmd)) {
			vista.volver();
		} else if ("ofertar".equals(cmd)) {
			vista.navegarACrearOferta();
		}
	}

	/**
	 * Indica si el cliente puede realizar una oferta.
	 *
	 * @return true si el cliente está logueado y no es el propietario del producto
	 */
	public boolean puedeOfertar() {
		if (cliente == null)
			return false;
		return !producto.getPropietario().getNickname().equals(cliente.getNickname());
	}

	/**
	 * Devuelve el producto que se está mostrando.
	 *
	 * @return El producto de segunda mano
	 */
	public Producto2Mano getProducto() {
		return producto;
	}

	/**
	 * Devuelve el cliente logueado.
	 *
	 * @return El cliente
	 */
	public Cliente getCliente() {
		return cliente;
	}
}
