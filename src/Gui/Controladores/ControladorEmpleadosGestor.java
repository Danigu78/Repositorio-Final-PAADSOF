package Gui.Controladores;

import Gui.Gestor.SubpanelEmpleadosGestor;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controlador de la gestión de empleados para el gestor.
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorEmpleadosGestor implements ActionListener {

    private SubpanelEmpleadosGestor vista;
    private Gestor gestor;
    private Tienda tienda;

    public ControladorEmpleadosGestor(SubpanelEmpleadosGestor vista, Gestor gestor) {
        this.vista = vista;
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("darDeAlta")) {
            vista.procesarAlta();
        } else if (cmd.startsWith("darDeBaja:")) {
            String id = cmd.substring(10);
            vista.confirmarBaja(id);
        } else if (cmd.startsWith("asignarPermiso:")) {
            String id = cmd.substring(15);
            vista.procesarAsignarPermiso(id);
        } else if (cmd.startsWith("retirarPermiso:")) {
            String id = cmd.substring(15);
            vista.procesarRetirarPermiso(id);
        }
    }

    public boolean darDeAlta(String nickname, String password,
                              List<TipoPermisos> permisos) {
        boolean ok = gestor.darDeAltaEmpleados_Permisos(nickname, password, permisos);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean darDeBaja(String idEmpleado) {
        boolean ok = gestor.darDeBajaAEmpleado(idEmpleado);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean asignarPermiso(String idEmpleado, TipoPermisos permiso) {
        boolean ok = gestor.asignarPermiso(idEmpleado, permiso);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean retirarPermiso(String idEmpleado, TipoPermisos permiso) {
        boolean ok = gestor.retirarPermiso(idEmpleado, permiso);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public List<Empleado> getEmpleados() {
        return tienda.obtenerEmpleadosTienda();
    }
    /**
     * Devuelve el nickname de un empleado por su id.
     */
    public String getNicknameEmpleado(String id) {
        return getEmpleados().stream()
            .filter(e -> e.getId().equals(id))
            .map(e -> e.getNickname())
            .findFirst().orElse(id);
    }
}