package productos;

/**
 * Define los diferentes grados de conservación física de un producto. Se
 * utiliza principalmente en el proceso de tasación de productos de segunda
 * mano.
 * 
 * @author Lucas
 * @version 1.0
 */
public enum EstadoProducto {
	/** El producto está como nuevo, sin marcas de uso. */
	PERFECTO,

	/** El producto tiene un uso mínimo y está muy bien conservado. */
	MUY_BUENO,

	/** Presenta señales normales de haber sido utilizado. */
	USO_LIGERO,

	/** Marcas de uso claramente visibles pero funcional. */
	USO_EVIDENTE,

	/** Desgaste severo por uso prolongado. */
	MUY_USADO,

	/** El producto tiene defectos físicos o funcionales. */
	DAÑADO,

	/** El estado no cumple los estándares mínimos para entrar en el catálogo. */
	NO_ACEPTADO
}