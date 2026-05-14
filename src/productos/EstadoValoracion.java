package productos;

/**
 * Enumeración que representa los posibles estados de una valoración dentro del
 * sistema.
 *
 * @author dani
 * @version 1.0
 */
public enum EstadoValoracion {
	/** La valoración está pendiente de pago. */
	PENDIENTE_DE_PAGO,
	/** El pago de la valoración ya se ha realizado. */
	PAGADO,
	/** La valoración ya ha sido completada. */
	REALIZADA
}