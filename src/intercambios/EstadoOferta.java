package intercambios;

/**
 * Define las fases por las que atraviesa una propuesta de intercambio entre
 * usuarios. Gestiona el ciclo de vida desde la creación de la oferta hasta su
 * conclusión o cancelacion. 
 * @author Antonino Albarrán
 * @version 1.0
 */
public enum EstadoOferta {
	/** El destinatario ha dado el visto bueno a los términos del intercambio. */
	ACEPTADA,

	/** El destinatario ha declinado formalmente la propuesta. */
	RECHAZADA,

	/**
	 * La oferta ha sido enviada y está a la espera de una respuesta del
	 * destinatario.
	 */
	PENDIENTE,

	/**
	 * Se ha superado el tiempo máximo de respuesta definido por el sistema sin
	 * recibir contestación.
	 */
	CADUCADA,

	/**
	 * El intercambio físico o digital de los productos se ha completado
	 * satisfactoriamente.
	 */
	REALIZADA,

	/**
	 * El usuario que lanzó la propuesta la ha anulado antes de recibir una
	 * respuesta.
	 */
	RETIRADA
}