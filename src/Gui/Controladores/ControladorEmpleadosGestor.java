package Gui.Controladores;

import java.util.List;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;

/**
 * Controlador de la gestión de empleados para el gestor.
 * Maneja dar de alta, baja y gestión de permisos.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorEmpleadosGestor {

    private Gestor gestor;
    private Tienda tienda;

    /**
     * Constructor del controlador de empleados.
     *
     * @param gestor El gestor logueado
     */
    public ControladorEmpleadosGestor(Gestor gestor) {
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    /**
     * Da de alta un nuevo empleado con los permisos indicados.
     *
     * @param nickname Nickname del empleado
     * @param password Contraseña del empleado
     * @param permisos Lista de permisos a asignar
     * @return true si se creó correctamente
     */
    public boolean darDeAlta(String nickname, String password, List<TipoPermisos> permisos) {
        return gestor.darDeAltaEmpleados_Permisos(nickname, password, permisos);
    }

    /**
     * Da de baja a un empleado por su id.
     *
     * @param idEmpleado Id del empleado
     * @return true si se dio de baja correctamente
     */
    public boolean darDeBaja(String idEmpleado) {
        return gestor.darDeBajaAEmpleado(idEmpleado);
    }

    /**
     * Asigna un permiso a un empleado.
     *
     * @param idEmpleado Id del empleado
     * @param permiso    Permiso a asignar
     * @return true si se asignó correctamente
     */
    public boolean asignarPermiso(String idEmpleado, TipoPermisos permiso) {
        return gestor.asignarPermiso(idEmpleado, permiso);
    }

    /**
     * Retira un permiso a un empleado.
     *
     * @param idEmpleado Id del empleado
     * @param permiso    Permiso a retirar
     * @return true si se retiró correctamente
     */
    public boolean retirarPermiso(String idEmpleado, TipoPermisos permiso) {
        return gestor.retirarPermiso(idEmpleado, permiso);
    }

    /**
     * Devuelve la lista de empleados de la tienda.
     *
     * @return Lista de empleados
     */
    public List<Empleado> getEmpleados() {
        return tienda.obtenerEmpleadosTienda();
    }
}