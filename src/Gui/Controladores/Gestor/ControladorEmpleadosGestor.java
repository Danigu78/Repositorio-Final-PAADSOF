package Gui.controladores.gestor;

import Gui.gestor.SubpanelEmpleadosGestor;
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
            vista.confirmarBaja(cmd.substring(10));
        } else if (cmd.startsWith("asignarPermiso:")) {
            vista.procesarAsignarPermiso(cmd.substring(15));
        } else if (cmd.startsWith("retirarPermiso:")) {
            vista.procesarRetirarPermiso(cmd.substring(15));
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

    /**
     * Busca empleados por nombre o id usando el método del gestor.
     */
    public List<Empleado> buscarEmpleados(String texto) {
        return gestor.buscarEmpleadoPorNombre(texto);
    }

    public List<Empleado> getEmpleados() {
        return tienda.obtenerEmpleadosTienda();
    }

    public String getNicknameEmpleado(String id) {
        return getEmpleados().stream()
            .filter(e -> e.getId().equals(id))
            .map(Empleado::getNickname)
            .findFirst().orElse(id);
    }
}