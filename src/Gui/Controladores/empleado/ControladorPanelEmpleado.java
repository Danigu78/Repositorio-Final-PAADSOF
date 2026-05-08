package Gui.Controladores.empleado;

import Gui.empleado.PanelEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

import usuarios.Empleado;
import usuarios.TipoPermisos;

/**
 * Controlador del panel principal del empleado. Centraliza las comprobaciones de
 * acceso, permisos y datos básicos del empleado actual.
 */
public class ControladorPanelEmpleado implements ActionListener {

    public static final String LOGOUT = "panel.logout";
    public static final String CAMBIAR_SECCION = "panel.seccion.";

    private final Empleado empleado;
    private PanelEmpleado vista;

    public ControladorPanelEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setVista(PanelEmpleado vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (vista == null || e == null) {
            return;
        }

        String accion = e.getActionCommand();
        if (LOGOUT.equals(accion)) {
            vista.salir();
        } else if (accion != null && accion.startsWith(CAMBIAR_SECCION)) {
            if (e.getSource() instanceof JButton) {
                vista.activarPestana((JButton) e.getSource());
            }
            vista.mostrarSeccion(accion.substring(CAMBIAR_SECCION.length()));
        }
    }

    public boolean empleadoPuedeVerPanel() {
        return empleado != null && !empleado.isDespedido() && empleado.isSesionIniciada();
    }

    public boolean tienePermiso(TipoPermisos permiso) {
        return empleado != null && permiso != null && empleado.tienePermiso(permiso);
    }

    public String getNicknameEmpleado() {
        if (empleado == null || empleado.getNickname() == null) {
            return "Empleado";
        }
        return empleado.getNickname();
    }

    public List<TipoPermisos> getPermisosOrdenados() {
        List<TipoPermisos> permisos = new ArrayList<>();
        if (empleado == null || empleado.getPermisos() == null) {
            return permisos;
        }
        permisos.addAll(empleado.getPermisos());
        return permisos;
    }
}
