package productos;

import java.time.LocalDateTime;
import java.util.Date;

import Excepcion.ValoracionInvalidaException;
import tienda.Tienda;
import usuarios.Empleado;
import ventas.Pago;

public class Valoracion {
	private LocalDateTime fecha;
	private EstadoProducto estadoProducto;
	private EstadoValoracion estadoValoracion = EstadoValoracion.PENDIENTE_DE_PAGO;
	private Empleado empleado;
	private Pago pago;
	private double precioTasacion;
	private double precioPagado;

	/**
	 * Constructor de la clase Valoracion
	 *
	 * @param precioTasacion el importe estimado para el producto
	 * @param estadoProducto el estado en el que se encuentra el producto
	 * @param empleado       el empleado que realiza la valoración
	 */
	public Valoracion(double precioTasacion, EstadoProducto estadoProducto, Empleado empleado) {
		if (precioTasacion < 0) {
			throw new ValoracionInvalidaException("El precio de tasación no puede ser negativo.");
		}
		if (estadoProducto == null) {
			throw new ValoracionInvalidaException("El estado del producto no puede ser null.");
		}
		if (empleado == null) {
			throw new ValoracionInvalidaException("El empleado tasador no puede ser null.");
		}

		this.fecha = LocalDateTime.now();
		this.estadoProducto = estadoProducto;
		this.empleado = empleado;
		this.precioTasacion = precioTasacion;
		this.precioPagado = Tienda.getInstancia().getPrecioTasacion();
	}

	/**
	 * Constructor de la clase Valoracion con todos sus datos
	 *
	 * @param fecha            la fecha en la que se hizo la valoración
	 * @param precioTasacion   el precio estimado del producto
	 * @param estadoProducto   el estado del producto valorado
	 * @param estadoValoracion el estado actual de la valoración
	 * @param empleado         el empleado que la ha realizado
	 * @param pago             el pago asociado a la valoración
	 */
	public Valoracion(LocalDateTime fecha, double precioTasacion, EstadoProducto estadoProducto,
			EstadoValoracion estadoValoracion, Empleado empleado, Pago pago) {
		if (fecha == null) {
			throw new ValoracionInvalidaException("La fecha de la valoración no puede ser null.");
		}
		if (precioTasacion < 0) {
			throw new ValoracionInvalidaException("El precio de tasación no puede ser negativo.");
		}
		if (estadoProducto == null) {
			throw new ValoracionInvalidaException("El estado del producto no puede ser null.");
		}
		if (estadoValoracion == null) {
			throw new ValoracionInvalidaException("El estado de la valoración no puede ser null.");
		}
		if (empleado == null) {
			throw new ValoracionInvalidaException("El empleado tasador no puede ser null.");
		}

		this.fecha = fecha;
		this.precioTasacion = precioTasacion;
		this.estadoProducto = estadoProducto;
		this.estadoValoracion = estadoValoracion;
		this.empleado = empleado;
		this.pago = pago;
	}

	/**
	 * Intenta registrar el pago de la valoración
	 *
	 * @param tarjeta   el número de la tarjeta
	 * @param cvv       el código de seguridad de la tarjeta
	 * @param caducidad la fecha de caducidad de la tarjeta
	 * @return true si el pago se realiza bien, false en caso contrario
	 */
	public boolean pagar(String tarjeta, int cvv, Date caducidad) {
		Pago pagoTarjeta = new Pago(tarjeta, this.precioTasacion, caducidad, cvv);

		if (!pagoTarjeta.getExito()) {
			return false;
		}

		this.pago = pagoTarjeta;
		this.estadoValoracion = EstadoValoracion.PAGADO;
		return true;
	}

	/**
	 * Recupera la fecha de la valoración
	 *
	 * @return la fecha en la que se hizo
	 */
	public LocalDateTime getFecha() {
		return fecha;
	}

	/**
	 * Devuelve el estado del producto valorado
	 *
	 * @return el estado del producto
	 */
	public EstadoProducto getEstadoProducto() {
		return estadoProducto;
	}

	/**
	 * Recupera el estado actual de la valoración
	 *
	 * @return el estado en el que se encuentra la valoración
	 */
	public EstadoValoracion getEstadoValoracion() {
		return estadoValoracion;
	}

	/**
	 * Obtiene el empleado que realizó la valoración
	 *
	 * @return el empleado asociado
	 */
	public Empleado getEmpleado() {
		return empleado;
	}

	/**
	 * Recupera el pago asociado a la valoración
	 *
	 * @return el pago registrado
	 */
	public Pago getPago() {
		return pago;
	}

	/**
	 * Cambia el estado de la valoración
	 *
	 * @param estadoValoracion el nuevo estado
	 */
	public void setEstadoValoracion(EstadoValoracion estadoValoracion) {
		if (estadoValoracion == null) {
			throw new ValoracionInvalidaException("El estado de la valoración no puede ser null.");
		}
		this.estadoValoracion = estadoValoracion;
	}

	/**
	 * Devuelve una representación sencilla de la valoración
	 *
	 * @return un texto con su estado, empleado, fecha y estado del producto
	 */
	@Override
	public String toString() {
		return "[" + this.estadoValoracion + "] " + (this.empleado != null ? this.empleado.getNickname() : "null")
				+ " | " + this.fecha + " | " + this.estadoProducto + " |";
	}

	/**
	 * Recupera el precio de tasación
	 *
	 * @return el importe estimado del producto
	 */
	public double getPrecioTasacion() {
		return precioTasacion;
	}

	/**
	 * Modifica el precio de tasación
	 *
	 * @param precioTasacion el nuevo importe de tasación
	 */
	public void setPrecioTasacion(double precioTasacion) {
		if (precioTasacion < 0) {
			throw new ValoracionInvalidaException("El precio de tasación no puede ser negativo.");
		}
		this.precioTasacion = precioTasacion;
	}

	/**
	 * Recupera el precio pagado por la valoración
	 *
	 * @return el importe pagado
	 */
	public double getPrecioPagado() {
		return precioPagado;
	}

	/**
	 * Cambia el precio pagado de la valoración
	 *
	 * @param precioPagado el nuevo importe pagado
	 */
	public void setPrecioPagado(double precioPagado) {
		this.precioPagado = precioPagado;
	}

}
