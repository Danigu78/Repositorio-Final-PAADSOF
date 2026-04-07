package Excepcion;

/**
 * Excepción que se lanza cuando se intenta acceder a una oferta que no está
 * disponible.
 * 
 * La oferta puede no estar disponible por haber sido resuelta, caducada o no
 * pertenecer al usuario que la solicita.
 */
public class OfertaNoDisponibleException extends CheckPointException {

	private static final long serialVersionUID = 1L;

	/** Identificador de la oferta que provoca la excepción */
	private final String idOferta;

	/**
	 * Construye la excepción indicando el identificador de la oferta.
	 *
	 * @param idOferta identificador de la oferta no disponible
	 */
	public OfertaNoDisponibleException(String idOferta) {
		super("La oferta " + idOferta
				+ " no esta disponible: ya ha sido resuelta, caducada o no pertenece a este usuario.");
		this.idOferta = idOferta;
	}

	/**
	 * Devuelve el identificador de la oferta que provocó la excepción.
	 *
	 * @return id de la oferta
	 */
	public String getIdOferta() {
		return idOferta;
	}
}