package excepciones;

import java.time.LocalDate;

/**
 * Excepción que se lanza cuando un rango de fechas es inválido.
 * 
 * Un rango es inválido si la fecha de fin es anterior a la fecha de inicio, o
 * si alguna de las fechas es null.
 */
public class RangoFechasInvalidoException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/** Fecha de inicio del rango inválido */
	private final LocalDate inicio;

	/** Fecha de fin del rango inválido */
	private final LocalDate fin;

	/**
	 * Construye la excepción indicando el rango de fechas inválido.
	 *
	 * @param inicio fecha de inicio del rango
	 * @param fin    fecha de fin del rango
	 */
	public RangoFechasInvalidoException(LocalDate inicio, LocalDate fin) {
		super("Rango de fechas invalido: inicio=" + inicio + ", fin=" + fin
				+ ". La fecha de fin debe ser igual o posterior a la de inicio, y ninguna puede ser null.");
		this.inicio = inicio;
		this.fin = fin;
	}

	/**
	 * Devuelve la fecha de inicio del rango inválido.
	 *
	 * @return fecha de inicio
	 */
	public LocalDate getInicio() {
		return inicio;
	}

	/**
	 * Devuelve la fecha de fin del rango inválido.
	 *
	 * @return fecha de fin
	 */
	public LocalDate getFin() {
		return fin;
	}
}