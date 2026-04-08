package tienda;
/**
 * Define las diferentes categorías de avisos que el sistema puede enviar a los usuarios.
 * Se divide entre notificaciones críticas (obligatorias) y notificaciones informativas 
 * (configurables por el cliente).
 * * @author Daniel Gonzalez
 * @version 1.0
 */
public enum TipoNotificacion {
	
	/*  NOTIFICACIONES OBLIGATORIAS (SISTEMA) */

	/** Código necesario para retirar un pedido en tienda física. */
	CODIGO_RECOGIDA, 
	
	/** Aviso de que el pedido ha sido preparado y está disponible. */
	PEDIDO_LISTO, 
	
	/** Notificación al recibir una propuesta de intercambio de otro usuario. */
	OFERTA_RECIBIDA, 
	
	/** Confirmación de que la transacción económica se ha realizado correctamente. */
	PAGO_EXITOSO, 
	
	/** Aviso de error en el proceso de pago. */
	Pago_FALLIDO, 
	
	/** Aviso de que el tiempo de reserva de los productos en el carrito ha expirado. */
	CARRITO_CADUCADO, 
	
	/** Notificación cuando una propuesta de intercambio propia no es aceptada. */
	OFERTA_RECHAZADA, 
	
	/** Confirmación de que el intercambio de productos se ha cerrado con éxito. */
	INTERCAMBIO_REALIZADO, 
	
	/** Confirmación de reserva de productos al añadir al carrito. */
	CONFIRMACION_RESERVA_CARRITO, 
	
	/** Canal de notificaciones internas dirigidas exclusivamente al personal. */
	EMPLEADOS, 
	
	/** Avisos relacionados con categorías marcadas como favoritas. */
	CATEGORIA_INTERES, 
	
	/** Mensajes generales de seguridad o estado de la cuenta (ej. login). */
	SISTEMA,

	/*NOTIFICACIONES CONFIGURABLES (PREFERENCIAS)  */

	/** Información sobre nuevas promociones o rebajas de precios. */
	DESCUENTO, 
	
	/** Aviso de que un pedido pendiente no ha sido abonado a tiempo. */
	PEDIDO_CADUCADO, 
	
	/** Notificación de que un producto de interés ha sido puesto para intercambio. */
	PRODUCTO_INTERCAMBIO_NUEVO, 
	
	/** Confirmación final de que el pedido ha llegado a su destino. */
	PEDIDO_ENTREGADO, 
	
	/** Aviso de que el proceso de tasación de un producto de segunda mano ha finalizado. */
	VALORACION_COMPLETADA, 
	
	/** Notificación de que una oferta enviada/recibida ha expirado por tiempo. */
	OFERTA_CADUCADA
}