package Gui.Controladores.cliente;

import Gui.cliente.SubpanelPago;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.Pedido;
import java.util.Calendar;
import java.sql.Date;

/**
 * Controlador del subpanel de pago. Gestiona el proceso de pago usando el
 * método pagarCarrito del cliente.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPago {

	/** Vista del subpanel pago */
	private SubpanelPago vista;

	/** Cliente logueado */
	private Cliente cliente;

	/** Pedido a pagar */
	private Pedido pedido;

	/**
	 * Constructor del controlador de pago.
	 *
	 * @param vista   El subpanel pago
	 * @param cliente El cliente logueado
	 * @param pedido  El pedido a pagar
	 */
	public ControladorPago(SubpanelPago vista, Cliente cliente, Pedido pedido) {
		this.vista = vista;
		this.cliente = cliente;
		this.pedido = pedido;
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
	 * intentar el pago. Si tiene éxito navega de vuelta a pedidos.
	 *
	 * @param numeroTarjeta Número de la tarjeta (16 dígitos)
	 * @param cvvStr        CVV de la tarjeta (3 dígitos)
	 */
	public void realizarPago(String numeroTarjeta, String cvvStr) {
		// Validaciones básicas
		if (numeroTarjeta.isEmpty() || cvvStr.isEmpty()) {
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

	
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		Date fechaTarjeta = new Date(cal.getTimeInMillis());

		boolean ok = cliente.pagarCarrito(pedido, numeroTarjeta, fechaTarjeta, cvv);
		System.out.println("Resultado pagarCarrito: " + ok);
		System.out.println("Estado pedido tras pago: " + pedido.getEstado());
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
			vista.mostrarExito("Pago realizado correctamente. Tu pedido está en preparación.");
			vista.volverAPedidos();
		} else {
			vista.mostrarError("Pago rechazado. Comprueba los datos de tu tarjeta.");
		}
	}
}