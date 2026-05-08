package Gui.Controladores;

import java.util.ArrayList;
import java.util.List;

import tienda.GuardadoTienda;
import tienda.Notificacion;
import tienda.Tienda;
import usuarios.Empleado;

/** Controlador de la bandeja de notificaciones del empleado. */
public class ControladorNotificacionesEmpleado {

    private final Empleado empleado;

    public ControladorNotificacionesEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public List<Notificacion> getNotificacionesFiltradas(String filtro) {
        List<Notificacion> resultado = new ArrayList<>();
        List<Notificacion> notificaciones = empleado == null ? null : empleado.getNotificaciones();
        if (notificaciones == null) {
            return resultado;
        }

        for (Notificacion notificacion : notificaciones) {
            if (notificacion != null && pasaFiltro(notificacion, filtro)) {
                resultado.add(notificacion);
            }
        }
        return resultado;
    }

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

    public boolean tieneNotificaciones() {
        List<Notificacion> notificaciones = empleado == null ? null : empleado.getNotificaciones();
        return notificaciones != null && !notificaciones.isEmpty();
    }

    public void marcarComoVista(Notificacion notificacion) {
        if (notificacion != null) {
            notificacion.marcarComoLeida();
            GuardadoTienda.guardar(Tienda.getInstancia());
        }
    }

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

    public String crearTextoLista(Notificacion notificacion) {
        String estado = notificacion.isLeida() ? "Vista" : "Nueva";
        String tipo = obtenerTipo(notificacion);
        String fecha = formatearFecha(notificacion);
        return estado + "   |   " + tipo + "   |   " + fecha;
    }

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

    private boolean pasaFiltro(Notificacion notificacion, String filtro) {
        String f = filtro == null ? "Todas" : filtro;
        if ("No vistas".equals(f) && notificacion.isLeida()) {
            return false;
        }
        if ("Vistas".equals(f) && !notificacion.isLeida()) {
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
