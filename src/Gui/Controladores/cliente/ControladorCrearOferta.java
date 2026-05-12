package Gui.Controladores.cliente;

import Gui.cliente.SubpanelCrearOferta;
import excepciones.ProductoBloqueadoException;
import productos.Producto2Mano;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de crear oferta. Implementa ActionListener según el
 * patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCrearOferta implements ActionListener {

	private SubpanelCrearOferta vista;
	private Cliente cliente;
	private Producto2Mano productoObjetivo;
	private Tienda tienda;

	public ControladorCrearOferta(SubpanelCrearOferta vista, Cliente cliente, Producto2Mano productoObjetivo) {
		this.vista = vista;
		this.cliente = cliente;
		this.productoObjetivo = productoObjetivo;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos de los botones de la vista.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("volver")) {
			vista.volver();
		} else if (e.getActionCommand().equals("enviar")) {
			enviarOferta();
		}
	}

	/**
	 * Devuelve los productos del cliente disponibles para ofertar. Solo los que
	 * están tasados, visibles y no bloqueados.
	 *
	 * @return Lista de productos disponibles
	 */
	public List<Producto2Mano> getMisProductosDisponibles() {
		List<Producto2Mano> disponibles = new ArrayList<>();
		for (Producto2Mano p : cliente.getCarteraIntercambio()) {
			if (p.getValoracion() != null && p.isVisible() && !p.isBloqueado()) {
				disponibles.add(p);
			}
		}
		return disponibles;
	}

	/**
	 * Devuelve los productos del propietario visibles y no bloqueados.
	 *
	 * @return Lista de productos del propietario
	 */
	public List<Producto2Mano> getProductosPropietario() {
		List<Producto2Mano> productos = new ArrayList<>();
		Cliente propietario = productoObjetivo.getPropietario();
		for (Producto2Mano p : propietario.getCarteraIntercambio()) {
			if (p.getValoracion() != null && p.isVisible() && !p.isBloqueado()) {
				productos.add(p);
			}
		}
		return productos;
	}

	/**
	 * Envía la oferta con los productos seleccionados por el usuario. Guarda el
	 * estado tras enviar correctamente.
	 */
	private void enviarOferta() {
		List<Producto2Mano> misSeleccionados = vista.getMisProductosSeleccionados();
		List<Producto2Mano> susSeleccionados = vista.getSusProductosSeleccionados();

		if (misSeleccionados.isEmpty()) {
			vista.mostrarError("Debes seleccionar al menos un producto tuyo para ofrecer.");
			return;
		}
		if (susSeleccionados.isEmpty()) {
			vista.mostrarError("Debes seleccionar al menos un producto del otro usuario.");
			return;
		}

		try {
			Cliente destinatario = productoObjetivo.getPropietario();
			boolean ok = cliente.proponerOferta(destinatario, misSeleccionados, susSeleccionados);
			if (ok) {
				GuardadoTienda.guardar(tienda);
				vista.mostrarExito("¡Oferta enviada correctamente!");
				vista.volver();
			} else {
				vista.mostrarError(
						"No se pudo enviar la oferta. " + "Comprueba que los productos estén tasados y disponibles.");
			}
		} catch (ProductoBloqueadoException ex) {
			vista.mostrarError("Uno de los productos está bloqueado por otra oferta pendiente.");
		} catch (Exception ex) {
			vista.mostrarError("Error al enviar la oferta: " + ex.getMessage());
		}
	}
}