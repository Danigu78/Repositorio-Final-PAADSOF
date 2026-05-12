package Gui.Controladores.cliente;

import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.EstadoPedido;
import ventas.Pedido;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import Gui.cliente.SubpanelPago;
import java.sql.Date;

/**
 * Controlador del subpanel de pago. Gestiona el proceso de pago usando el
 * método pagarCarrito del cliente. Implementa ActionListener según el patrón
 * MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPago implements ActionListener {

	/** Vista del subpanel de pago. */
	private SubpanelPago vista;

	/** Cliente logueado. */
	private Cliente cliente;

	/** Pedido a pagar. */
	private Pedido pedido;

	/**
	 * Constructor del controlador de pago.
	 *
	 * @param vista   El subpanel de pago
	 * @param cliente El cliente logueado
	 * @param pedido  El pedido a pagar
	 */
	public ControladorPago(SubpanelPago vista, Cliente cliente, Pedido pedido) {
		this.vista = vista;
		this.cliente = cliente;
		this.pedido = pedido;
	}

	/**
	 * Gestiona los eventos de los botones de la vista.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("pagar")) {
			revisarTiempos();
			vista.procesarPago();
		} else if (cmd.equals("volver")) {
			vista.volverAPedidos();
		}
	}

	/**
	 * Devuelve el total del pedido a pagar.
	 *
	 * @return Total del pedido
	 */
	public double getTotal() {
		return pedido.getTotal();
	}

	/**
	 * Devuelve el id del pedido.
	 *
	 * @return Id del pedido
	 */
	public String getIdPedido() {
		return pedido.getIdPedido();
	}

	/**
	 * Realiza el pago usando pagarCarrito del cliente. Valida los campos antes de
	 * intentar el pago. Valida que el mes esté entre 01 y 12. Si tiene éxito guarda
	 * y navega de vuelta a pedidos.
	 *
	 * @param numeroTarjeta Número de la tarjeta (16 dígitos)
	 * @param cvvStr        CVV de la tarjeta (3 dígitos)
	 * @param fechaStr      Fecha de caducidad en formato MM/AA
	 */
	public void realizarPago(String numeroTarjeta, String cvvStr, String fechaStr) {
		revisarTiempos();
		if (!pedidoSiguePendiente()) {
			vista.mostrarError("Ese pedido ya no esta pendiente de pago.");
			vista.volverAPedidos();
			return;
		}

		if (numeroTarjeta.isEmpty() || cvvStr.isEmpty() || fechaStr.isEmpty()) {
			vista.mostrarError("Rellena todos los campos.");
			return;
		}
		if (numeroTarjeta.length() != 16) {
			vista.mostrarError("El número de tarjeta debe tener 16 dígitos.");
			return;
		}
		if (cvvStr.length() != 3) {
			vista.mostrarError("El CVV debe tener 3 dígitos.");
			return;
		}

		int cvv;
		try {
			cvv = Integer.parseInt(cvvStr);
		} catch (NumberFormatException e) {
			vista.mostrarError("El CVV debe ser numérico.");
			return;
		}

		// Parseamos MM/AA validamos mes entre 01 y 12
		Date fechaTarjeta;
		try {
			String[] partes = fechaStr.split("/");
			if (partes.length != 2)
				throw new Exception("Formato incorrecto");
			int mes = Integer.parseInt(partes[0].trim());
			int año = 2000 + Integer.parseInt(partes[1].trim());

			if (mes < 1 || mes > 12) {
				vista.mostrarError("El mes debe estar entre 01 y 12.");
				return;
			}

			Calendar cal = Calendar.getInstance();
			cal.set(año, mes - 1, 1);
			fechaTarjeta = new Date(cal.getTimeInMillis());
		} catch (NumberFormatException ex) {
			vista.mostrarError("La fecha debe contener solo números. Usa MM/AA.");
			return;
		} catch (Exception ex) {
			vista.mostrarError("Formato de fecha incorrecto. Usa MM/AA.");
			return;
		}

		boolean ok = cliente.pagarCarrito(pedido, numeroTarjeta, fechaTarjeta, cvv);
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
			vista.mostrarExito("Pago realizado correctamente. " + "Tu pedido está en preparación.");
			vista.volverAPedidos();
		} else {
			vista.mostrarError("Pago rechazado. Comprueba los datos de tu tarjeta.");
		}
	}

	public boolean pedidoSiguePendiente() {
		return pedido != null && pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO && !pedido.isCaducado();
	}

	private void revisarTiempos() {
		Tienda.getInstancia().getComprobadorTiempos().revisarCarritosCaducados();
		Tienda.getInstancia().getComprobadorTiempos().revisarPedidosPendientesCaducados();
		GuardadoTienda.guardar(Tienda.getInstancia());
	}
}
