package Gui.Controladores.empleado;

import Gui.empleado.SeccionNotificacionesEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import tienda.GuardadoTienda;
import tienda.Notificacion;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Controlador encargado de gestionar las notificaciones de un empleado.
 */


public class ControladorNotificacionesEmpleado implements ActionListener {

	/** Acción para recargar la lista de notificaciones. */
	public static final String REFRESCAR = "notificaciones.refrescar";
	
	/** Acción para aplicar filtros sobre las notificaciones. */
	public static final String FILTRAR = "notificaciones.filtrar";
	
	/** Acción para visualizar una notificación concreta. */
	public static final String VER_NOTIFICACION = "notificaciones.ver";
	
	/** Acción para marcar todas las notificaciones como vistas. */
	public static final String MARCAR_TODAS = "notificaciones.marcarTodas";

	/** Empleado asociado al controlador. */
	private final Empleado empleado;
	
	/** Vista vinculada al controlador. */
	private SeccionNotificacionesEmpleado vista;

	/**
	 * Constructor del controlador.
	 *
	 * @param empleado empleado que gestionará las notificaciones
	 */
	public ControladorNotificacionesEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asocia la vista al controlador.
	 *
	 * @param vista vista de notificaciones del empleado
	 */
	public void setVista(SeccionNotificacionesEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona las acciones generadas desde la interfaz.
	 *
	 * @param e evento producido por la interfaz
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (REFRESCAR.equals(accion) || FILTRAR.equals(accion)) {
			vista.cargarNotificaciones();
		} else if (VER_NOTIFICACION.equals(accion)) {
			vista.verNotificacionSeleccionada();
		} else if (MARCAR_TODAS.equals(accion)) {
			vista.marcarTodasComoVistas();
		}
	}

	/**
	 * Obtiene las notificaciones del empleado aplicando un filtro.
	 *
	 * @param filtro filtro seleccionado 
	 * @return lista de notificaciones filtradas y ordenadas por fecha descendente
	 */
	public List<Notificacion> getNotificacionesFiltradas(String filtro) {
		List<Notificacion> resultado = new ArrayList<>();
		List<Notificacion> notificaciones = empleado == null ? null : empleado.getNotificaciones();
		if (notificaciones == null) {
			return resultado;
		}

		for (Notificacion notificacion : notificaciones) {
			if (notificacion != null && cumpleFiltro(notificacion, filtro)) {
				resultado.add(notificacion);
			}
		}
		resultado.sort(new java.util.Comparator<Notificacion>() {
			@Override
			public int compare(Notificacion n1, Notificacion n2) {
				if (n1.getFechaEnvio() == null && n2.getFechaEnvio() == null) {
					return 0;
				}
				if (n1.getFechaEnvio() == null) {
					return 1;
				}
				if (n2.getFechaEnvio() == null) {
					return -1;
				}
				return n2.getFechaEnvio().compareTo(n1.getFechaEnvio());
			}
		});
		return resultado;
	}

	/**
	 * Cuenta cuántas notificaciones no han sido leídas.
	 *
	 * @return número de notificaciones pendientes de leer
	 */
	public int contarNoVistas() {
		int noVistas = 0;
		List<Notificacion> notificaciones = empleado == null ? null : empleado.getNotificaciones();
		if (notificaciones == null) {
			return 0;
		}
		for (Notificacion notificacion : notificaciones) {
			if (notificacion != null && !notificacion.isLeida()) {
				noVistas++;
			}
		}
		return noVistas;
	}

	/**
	 * Comprueba si el empleado tiene notificaciones.
	 *
	 * @return true si existen notificaciones
	 */
	public boolean tieneNotificaciones() {
		List<Notificacion> notificaciones = empleado == null ? null : empleado.getNotificaciones();
		return notificaciones != null && !notificaciones.isEmpty();
	}

	/**
	 * Marca una notificación concreta como leída.
	 *
	 * @param notificacion notificación a marcar
	 */
	public void marcarComoVista(Notificacion notificacion) {
		if (notificacion != null) {
			notificacion.marcarComoLeida();
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Marca todas las notificaciones del empleado como leídas.
	 *
	 * @return resultado de la operación
	 */
	public ResultadoOperacion marcarTodasComoVistas() {
		List<Notificacion> notificaciones = empleado == null ? null : empleado.getNotificaciones();
		if (notificaciones == null || notificaciones.isEmpty()) {
			return ResultadoOperacion.error("No hay notificaciones.");
		}
		for (Notificacion notificacion : notificaciones) {
			if (notificacion != null) {
				notificacion.marcarComoLeida();
			}
		}
		GuardadoTienda.guardar(Tienda.getInstancia());
		return ResultadoOperacion.ok("Todas las notificaciones se han marcado como vistas.");
	}

	/**
	 * Genera una línea resumen de una notificación para mostrar en listas.
	 *
	 * @param notificacion notificación a resumir
	 * @return texto resumido 
	 */
	public String crearTextoLista(Notificacion notificacion) {
		String estado = notificacion.isLeida() ? "Vista" : "Nueva";
		String tipo = obtenerTipo(notificacion);
		String fecha = formatearFecha(notificacion);
		return estado + "   |   " + tipo + "   |   " + fecha + "   |   " + crearResumenMensaje(notificacion);
	}

	/**
	 * Genera un resumen corto del mensaje de una notificación.
	 *
	 * @param notificacion notificación a resumir
	 * @return resumen reducido del mensaje
	 */
	private String crearResumenMensaje(Notificacion notificacion) {
		if (notificacion == null || notificacion.getMensaje() == null || notificacion.getMensaje().trim().isBlank()) {
			return "Sin detalle";
		}
		String mensaje = notificacion.getMensaje().trim().replaceAll("\\s+", " ");
		return mensaje.length() > 75 ? mensaje.substring(0, 72) + "..." : mensaje;
	}

	/**
	 * Genera el texto completo de una notificación.
	 *
	 * @param notificacion notificación a mostrar
	 * @return texto detallado de la notificación
	 */
	public String crearTextoNotificacion(Notificacion notificacion) {
		if (notificacion == null) {
			return "Notificación no encontrada.";
		}
		StringBuilder texto = new StringBuilder();
		texto.append("Tipo: ").append(obtenerTipo(notificacion)).append("\n");
		texto.append("Fecha: ").append(formatearFecha(notificacion)).append("\n");
		texto.append("Vista: Sí").append("\n\n");
		texto.append(notificacion.getMensaje());
		return texto.toString();
	}

	/**
	 * Comprueba si una notificación cumple el filtro seleccionado.
	 *
	 * @param notificacion notificación a comprobar
	 * @param filtro filtro seleccionado
	 * @return  true si la notificación cumple el filtro
	 */
	private boolean cumpleFiltro(Notificacion notificacion, String filtro) {
		String filtroElegido = filtro == null ? "Todas" : filtro;
		if ("No vistas".equals(filtroElegido) && notificacion.isLeida()) {
			return false;
		}
		if ("Vistas".equals(filtroElegido) && !notificacion.isLeida()) {
			return false;
		}
		return true;
	}

	private String obtenerTipo(Notificacion notificacion) {
		if (notificacion == null || notificacion.getTipo() == null) {
			return "General";
		}
		return String.valueOf(notificacion.getTipo());
	}

	private String formatearFecha(Notificacion notificacion) {
		if (notificacion == null || notificacion.getFechaEnvio() == null) {
			return "-";
		}
		return notificacion.getFechaEnvio().toLocalDate() + " "
				+ notificacion.getFechaEnvio().toLocalTime().withNano(0);
	}
}
