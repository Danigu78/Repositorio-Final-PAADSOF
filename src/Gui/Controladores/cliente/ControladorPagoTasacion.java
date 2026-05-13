package Gui.Controladores.cliente;

import Gui.cliente.SubpanelPagoTasacion;
import productos.Producto2Mano;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Calendar;

/**
 * Controlador del subpanel de pago de tasación. 
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPagoTasacion implements ActionListener {

	/** Vista del subpanel de pago de tasación. */
	private SubpanelPagoTasacion vista;

	/** Cliente logueado. */
	private Cliente cliente;

	/** Producto a tasar. */
	private Producto2Mano producto;

	/** Instancia de la tienda. */
	private Tienda tienda;

	/**
	 * Constructor del controlador de pago de tasación.
	 *
	 * @param vista    El subpanel de pago de tasación
	 * @param cliente  El cliente logueado
	 * @param producto El producto a tasar
	 */
	public ControladorPagoTasacion(SubpanelPagoTasacion vista, Cliente cliente, Producto2Mano producto) {
		this.vista = vista;
		this.cliente = cliente;
		this.producto = producto;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos de los botones de la vista.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if ("pagar".equals(cmd)) {
			realizarPago();
		} else if ("volver".equals(cmd)) {
			vista.volverACartera();
		}
	}

	/**
	 * Valida los campos y realiza el pago de la tasación. Valida que el mes esté
	 * entre 01 y 12.
	 */
	private void realizarPago() {
		String numeroTarjeta = vista.getNumeroTarjeta();
		String cvvStr = vista.getCVV();
		String fechaStr = vista.getFechaCaducidad();

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
		} catch (NumberFormatException ex) {
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

		boolean ok = cliente.solicitarTasacion(producto, numeroTarjeta, cvv, fechaTarjeta);
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
			vista.mostrarExito("Tasación solicitada. Un empleado valorará " + "tu producto pronto.");
			vista.volverACartera();
		} else {
			vista.mostrarError("No se pudo procesar el pago de la tasación.");
		}
	}

	/**
	 * Devuelve el precio de la tasación.
	 *
	 * @return Precio de tasación
	 */
	public double getPrecioTasacion() {
		return tienda.getPrecioTasacion();
	}

	/**
	 * Devuelve el nombre del producto a tasar.
	 *
	 * @return Nombre del producto
	 */
	public String getNombreProducto() {
		return producto.getNombre();
	}
}
