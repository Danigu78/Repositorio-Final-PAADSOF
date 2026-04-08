package ventas;

import java.time.LocalDateTime;
import java.util.*;
import es.uam.eps.padsof.telecard.*;
/**
 * Clase que gestiona la transacción bancaria de un pedido mediante tarjeta.
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Pago {
	/** Número identificativo de la tarjeta de crédito o débito. */
	private String numeroTarjeta;

	/** Registro de la fecha y hora exacta en la que se intentó el cobro. */
	private LocalDateTime fechaTransaccion;

	/** Fecha de caducidad de la tarjeta empleada. */
	private Date fechaTarjeta;

	/** Código de seguridad  de la tarjeta. */
	private int CVV;

	/** Cantidad económica total a cargar en la cuenta. */
	private double importe;

	/** Indica si la operación fue autorizada y completada por el banco. */
	private boolean exito;

	/**
	 * Constructor de la clase Pago
	 *
	 * @param numeroTarjeta el número de la tarjeta con la que se paga
	 * @param importe       la cantidad que se quiere cobrar
	 * @param fechaTarjeta  la fecha de caducidad de la tarjeta
	 * @param CVV           el código de seguridad de la tarjeta
	 */
	public Pago(String numeroTarjeta, double importe, Date fechaTarjeta, int CVV) {
		this.setFechaTransaccion(LocalDateTime.now());
		this.fechaTarjeta = fechaTarjeta;
		this.CVV = CVV;
		this.importe = importe;
		this.numeroTarjeta = numeroTarjeta;
		this.exito = procesarConBanco();
	}

	/**
	 * Comprueba los datos de la tarjeta e intenta realizar el cobro
	 *
	 * @return true si el pago se procesa bien, false si falla
	 */
	private boolean procesarConBanco() {
		String cvvString = String.valueOf(this.CVV);// convertimos a string
		Date hoy = new Date();

		if (this.numeroTarjeta.length() != 16) {
			return false;
		}

		if (cvvString.length() != 3) {
			System.out.println("El CVV debe tener 3 dígitos");
			return false;
		}

		if (this.fechaTarjeta.before(hoy)) {
			System.out.println("La tarjeta está caducada");
			return false;
		}
		try {
			TeleChargeAndPaySystem.charge(this.numeroTarjeta, "Pago CheckPoint", this.importe, true);
			return true;

		} catch (InvalidCardNumberException e) {
			System.out.println("Tarjeta inválida");
		} catch (FailedInternetConnectionException e) {
			System.out.println("Error de conexión al procesar el pago");
		} catch (OrderRejectedException e) {
			System.out.println("Pago rechazado por el banco");
		}
		return false;

	}

	/**
	 * Indica si el pago se realizó correctamente
	 *
	 * @return true si el pago tuvo éxito, false en caso contrario
	 */
	public boolean getExito() {
		return this.exito;
	}

	public LocalDateTime getFechaTransaccion() {
		return fechaTransaccion;
	}

	public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}
}
