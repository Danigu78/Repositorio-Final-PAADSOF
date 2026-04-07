package tienda;

import java.time.LocalDateTime;

public class Notificacion {

	private String id;
	private String mensaje;
	private LocalDateTime fechaEnvio;
	private boolean leida;
	private TipoNotificacion tipo;

	/**
	 * Constructor de la clase Notificacion
	 *
	 * @param mensaje el texto de la notificación
	 * @param tipo    el tipo de notificación que se quiere crear
	 */
	public Notificacion(String mensaje, TipoNotificacion tipo) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "NOTIF" + est.getnNotificaciones();
		est.setnNotificaciones(est.getnNotificaciones() + 1);
		this.mensaje = mensaje;
		this.fechaEnvio = LocalDateTime.now();
		this.leida = false;
		this.tipo = tipo;
		Tienda.getInstancia().registrarNotificacion(this);
	}

	/**
	 * Constructor de la clase Notificacion para avisos dirigidos a empleados
	 *
	 * @param mensaje el texto de la notificación
	 */
	public Notificacion(String mensaje) {
		this(mensaje, TipoNotificacion.EMPLEADOS);
	}

	/**
	 * Marca la notificación como leída
	 */
	public void marcarComoLeida() {
		this.leida = true;
	}

	/**
	 * Devuelve el identificador de la notificación
	 *
	 * @return el id de la notificación
	 */
	public String getId() {
		return id;
	}

	/**
	 * Recupera el mensaje de la notificación
	 *
	 * @return el texto del aviso
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * Cambia el mensaje de la notificación
	 *
	 * @param mensaje el nuevo texto
	 */
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	/**
	 * Recupera la fecha de envío de la notificación
	 *
	 * @return la fecha y hora de envío
	 */
	public LocalDateTime getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * Indica si la notificación ya ha sido leída
	 *
	 * @return true si ya fue leída, false en caso contrario
	 */
	public boolean isLeida() {
		return leida;
	}

	/**
	 * Cambia el estado de lectura de la notificación
	 *
	 * @param leida el nuevo estado de lectura
	 */
	public void setLeida(boolean leida) {
		this.leida = leida;
	}

	/**
	 * Devuelve el tipo de la notificación
	 *
	 * @return el tipo asociado a la notificación
	 */
	public TipoNotificacion getTipo() {
		return tipo;
	}

	/**
	 * Cambia el tipo de la notificación
	 *
	 * @param tipo el nuevo tipo
	 */
	public void setTipo(TipoNotificacion tipo) {
		this.tipo = tipo;
	}

	/**
	 * Devuelve un texto con los datos principales de la notificación
	 *
	 * @return una cadena con su id, tipo, estado, mensaje y fecha
	 */
	@Override
	public String toString() {
		return "[" + id + "] " + "[" + tipo + "] " + (leida ? "(leida)  " : "(no leida) ") + mensaje + "  ["
				+ fechaEnvio.toLocalDate() + " " + fechaEnvio.toLocalTime().withNano(0) + "]";
	}
}