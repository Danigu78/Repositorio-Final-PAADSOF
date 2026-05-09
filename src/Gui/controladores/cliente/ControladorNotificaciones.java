package Gui.controladores.cliente;

import Gui.cliente.SubpanelNotificaciones;
import tienda.GuardadoTienda;
import tienda.Notificacion;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de notificaciones del cliente.
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorNotificaciones implements ActionListener {

    private SubpanelNotificaciones vista;
    private Cliente cliente;

    public ControladorNotificaciones(SubpanelNotificaciones vista, Cliente cliente) {
        this.vista = vista;
        this.cliente = cliente;
    }

    /**
     * Gestiona los eventos de los botones de la vista.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("refrescar")) {
            vista.cargarNotificaciones();
        } else if (cmd.equals("ver")) {
            vista.verNotificacionSeleccionada();
        } else if (cmd.equals("marcarTodas")) {
            marcarTodasComoLeidas();
        }
    }

    /**
     * Devuelve las notificaciones del cliente filtradas.
     *
     * @param filtro "Todas", "No vistas" o "Vistas"
     * @return Lista filtrada
     */
    public List<Notificacion> getNotificaciones(String filtro) {
        List<Notificacion> notificaciones = cliente.getNotificaciones();
        if (notificaciones == null) return new ArrayList<>();

        List<Notificacion> resultado = new ArrayList<>();
        for (Notificacion n : notificaciones) {
            if (n == null) continue;
            if ("No vistas".equals(filtro) && n.isLeida()) continue;
            if ("Vistas".equals(filtro) && !n.isLeida()) continue;
            resultado.add(n);
        }
        return resultado;
    }

    /**
     * Cuenta las notificaciones no leídas del cliente.
     */
    public int contarNoLeidas() {
        int count = 0;
        List<Notificacion> notificaciones = cliente.getNotificaciones();
        if (notificaciones == null) return 0;
        for (Notificacion n : notificaciones) {
            if (n != null && !n.isLeida()) count++;
        }
        return count;
    }

    /**
     * Marca una notificación como leída usando el método del cliente.
     */
    public void marcarComoLeida(Notificacion n) {
        if (n != null) {
            cliente.verNotificacion(n);
            GuardadoTienda.guardar(Tienda.getInstancia()); // 
        }
    }

    private void marcarTodasComoLeidas() {
        List<Notificacion> notificaciones = cliente.getNotificaciones();
        if (notificaciones == null || notificaciones.isEmpty()) {
            vista.mostrarError("No hay notificaciones.");
            return;
        }
        for (Notificacion n : notificaciones) {
            if (n != null) cliente.verNotificacion(n);
        }
        GuardadoTienda.guardar(Tienda.getInstancia()); 
        vista.cargarNotificaciones();
        vista.mostrarMensaje("Todas las notificaciones se han marcado como vistas.");
    }
}