package Gui.Controladores;

import java.util.ArrayList;
import java.util.List;

import usuarios.Empleado;
import usuarios.TipoPermisos;

/**
 * Controlador del panel principal del empleado. Centraliza las comprobaciones de
 * acceso, permisos y datos básicos del empleado actual.
 */
public class ControladorPanelEmpleado {

    private final Empleado empleado;

    public ControladorPanelEmpleado(Empleado empleado) {
        this.empleado = empleado;
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
