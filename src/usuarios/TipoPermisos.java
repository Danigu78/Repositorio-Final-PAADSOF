package usuarios;

/**
 * Enum que representa los distintos tipos de permisos que puede tener un
 * empleado en el sistema.
 * 
 * @author Daniel Gonzalez Ureta
 * @version 1.0
 */

public enum TipoPermisos {
	/** Permite gestionar el stock de productos */
	GESTION_STOCK,
	/** Permite gestionar el stock de productos */
	GESTION_CATEGORIAS,
	/** Permite gestionar los packs de productos */
	GESTION_PACKS,
	/** Permite modificar productos */
	MODIFICAR_PRODUCTO,
	/** Permite gestionar pedidos */
	GESTION_PEDIDOS,
	/** Permite valorar productos */
	VALORACION_PRODUCTOS,
	/** Permite valorar productos */
	CONFIRMACION_INTERCAMBIO,
	/** Permite realizar la entrega de pedidos */
	ENTREGA_PEDIDOS
}
