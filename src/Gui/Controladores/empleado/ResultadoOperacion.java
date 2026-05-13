package Gui.Controladores.empleado;

/**
 * Resultado pequeño para avisar a la vista.
 * 
 * @author Lucas
 * @version 1.0
 */
public class ResultadoOperacion {

	/** Indica si la operación se realizó correctamente. */
	private final boolean exito;
	
	/** Mensaje asociado al resultado de la operación. */
	private final String mensaje;

	/**
	 * Crea un nuevo resultado de operación.
	 * 
	 * @param exito indica si la operación fue correcta
	 * @param mensaje mensaje descriptivo del resultado
	 */
	private ResultadoOperacion(boolean exito, String mensaje) {
		this.exito = exito;
		this.mensaje = mensaje;
	}

	/**
	 * Crea un resultado correcto.
	 * 
	 * @param mensaje mensaje asociado al éxito
	 * @return resultado correcto
	 */
	public static ResultadoOperacion ok(String mensaje) {
		return new ResultadoOperacion(true, mensaje);
	}

	/**
	 * Crea un resultado de error.
	 * 
	 * @param mensaje mensaje asociado al error
	 * @return resultado de error
	 */
	public static ResultadoOperacion error(String mensaje) {
		return new ResultadoOperacion(false, mensaje);
	}

	/**
	 * Indica si la operación fue correcta.
	 * 
	 * @return true si fue correcta
	 */
	public boolean isExito() {
		return exito;
	}

	/**
	 * Obtiene el mensaje asociado al resultado.
	 * 
	 * @return mensaje del resultado
	 */
	public String getMensaje() {
		return mensaje;
	}
}
