package ventas;

import java.io.*;
import java.time.LocalDateTime;

import tienda.Estadistica;

/**
 * Clase base para la gestión de promociones temporales aplicables a carritos.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public abstract class Descuento implements Serializable {

	private static final long serialVersionUID = 1L;
	/** Identificador alfanumérico único de la promoción. */
	protected String id;

	/** Nombre comercial o descriptivo del descuento. */
	protected String nombre;

	/** Fecha y hora a partir de la cual la promoción empieza a ser válida. */
	protected LocalDateTime fechaInicio;

	/** Fecha y hora en la que la promoción deja de tener validez. */
	protected LocalDateTime fechaFin;

	/**
	 * Constructor de la clase Descuento
	 *
	 * @param nombre el nombre del descuento
	 * @param inicio la fecha en la que empieza a estar disponible
	 * @param fin    la fecha en la que deja de estar activo
	 */
	public Descuento(String nombre, LocalDateTime inicio, LocalDateTime fin) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "DESC-" + String.valueOf(est.getnDescuentos());
		est.setnDescuentos(est.getnDescuentos() + 1);
		this.nombre = nombre;
		this.fechaInicio = inicio;
		this.fechaFin = fin;
	}

	/**
	 * Comprueba si el descuento está activo en este momento
	 *
	 * @return true si está dentro de su periodo de validez, false en caso contrario
	 */
	public boolean estaActivo() {
		LocalDateTime ahora = LocalDateTime.now();
		return ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin);
	}

	/**
	 * Aplica el descuento al carrito indicado
	 *
	 * @param carrito el carrito sobre el que se quiere aplicar
	 * @return el total resultante tras aplicar el descuento
	 */
	public abstract double aplicarDescuento(Carrito carrito);

	/**
	 * Recupera la fecha de inicio del descuento
	 *
	 * @return la fecha desde la que empieza a estar activo
	 */
	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	/**
	 * Cambia la fecha de inicio del descuento
	 *
	 * @param fechaInicio la nueva fecha de inicio
	 */
	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	/**
	 * Recupera la fecha de fin del descuento
	 *
	 * @return la fecha en la que deja de estar activo
	 */
	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	/**
	 * Cambia la fecha de fin del descuento
	 *
	 * @param fechaFin la nueva fecha de fin
	 */
	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}

	/**
	 * Devuelve el identificador del descuento
	 *
	 * @return el id del descuento
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Recupera el nombre del descuento
	 *
	 * @return el nombre asignado
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Modifica el nombre del descuento
	 *
	 * @param nombre el nuevo nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
