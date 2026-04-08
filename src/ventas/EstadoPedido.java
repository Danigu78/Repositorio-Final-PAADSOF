package ventas;

/**
 * Define las diferentes etapas del ciclo de vida de un pedido en el sistema.
 * Permite realizar el seguimiento desde que se crea la solicitud hasta su
 * entrega o anulación.
 * 
 * @author Lucas
 * @version 1.0
 */
public enum EstadoPedido {
	/** El pedido ha sido creado pero el cliente aún no ha realizado el abono. */
	PENDIENTE_PAGO,

	/** El pago se ha confirmado correctamente. */
	PAGADO,

	/** El personal de almacén está localizando y empaquetando los productos. */
	EN_PREPARACION,

	/**
	 * El pedido está listo para que el cliente pase a buscarlo por la tienda
	 * física.
	 */
	LISTO_PARA_RECOGER,

	/** El proceso ha finalizado y el cliente ya tiene los productos en su poder. */
	ENTREGADO,

	/**
	 * El pedido ha sido anulado, ya sea por el cliente, por falta de pago o por el
	 * gestor.
	 */
	CANCELADO
}