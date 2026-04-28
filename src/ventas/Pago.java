package ventas;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import es.uam.eps.padsof.telecard.*;

/**
 * Clase que gestiona la transacción bancaria de un pedido mediante tarjeta.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Pago implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Número identificativo de la tarjeta de crédito o débito. No se guarda. */
	private transient String numeroTarjeta;

	/** Registro de la fecha y hora exacta en la que se intentó el cobro. */
	private LocalDateTime fechaTransaccion;

	/** Fecha de caducidad de la tarjeta empleada. No se guarda. */
	private transient Date fechaTarjeta;

	/** Código de seguridad de la tarjeta. No se guarda. */
	private transient int CVV;

	/** Cantidad económica total a cargar en la cuenta. */
	private double importe;

	/** Indica si la operación fue autorizada y completada por el banco. */
	private boolean exito;

	/**
	 * Constructor de la clase Pago.
	 *
	 * @param numeroTarjeta el número de la tarjeta con la que se paga
	 * @param importe       la cantidad que se quiere cobrar
	 * @param fechaTarjeta  la fecha de caducidad de la tarjeta
	 * @param CVV           el código de seguridad de la tarjeta
	 */
	public Pago(String numeroTarjeta, double importe, Date fechaTarjeta, int CVV) {
		this.fechaTransaccion = LocalDateTime.now();
		this.fechaTarjeta = fechaTarjeta;
		this.CVV = CVV;
		this.importe = importe;
		this.numeroTarjeta = numeroTarjeta;
		this.exito = procesarConBanco();
	}

	/**
	 * Comprueba los datos de la tarjeta e intenta realizar el cobro.
	 *
	 * @return true si el pago se procesa bien, false si falla
	 */
	private boolean procesarConBanco() {
		String cvvString = String.valueOf(this.CVV);
		Date hoy = new Date();

		if (this.numeroTarjeta == null || this.numeroTarjeta.length() != 16) {
			return false;
		}

		if (cvvString.length() != 3) {
			System.out.println("El CVV debe tener 3 dígitos");
			return false;
		}

		if (this.fechaTarjeta == null || this.fechaTarjeta.before(hoy)) {
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
	 * Indica si el pago se realizó correctamente.
	 *
	 * @return true si el pago tuvo éxito, false en caso contrario
	 */
	public boolean getExito() {
		return this.exito;
	}

	/**
	 * Recupera el momento exacto en el que se efectuó la transacción.
	 * 
	 * @return fecha y hora del registro de la operación
	 */
	public LocalDateTime getFechaTransaccion() {
		return fechaTransaccion;
	}

	/**
	 * Establece o modifica la fecha y hora asociadas a la transacción.
	 * 
	 * @param fechaTransaccion el nuevo sello temporal de la operación
	 */
	public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}

	public double getImporte() {
		return importe;
	}
}
