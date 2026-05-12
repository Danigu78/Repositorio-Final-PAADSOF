package Gui.Controladores.Gestor;

import Gui.Gestor.*;
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

	/** Vista del subpanel de empleados. */
	private SubpanelEmpleadosGestor vista;

	/** Gestor autenticado. */
	private Gestor gestor;

	/** Instancia de la tienda. */
	private Tienda tienda;

	/**
	 * Constructor del controlador de empleados.
	 *
	 * @param vista  Vista del subpanel de empleados
	 * @param gestor Gestor autenticado
	 */
	public ControladorEmpleadosGestor(SubpanelEmpleadosGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos producidos en la vista.
	 *
	 * @param e Evento de acción
	 */
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

	/**
	 * Da de alta un nuevo empleado con permisos asignados.
	 *
	 * @param nickname Nombre de usuario del empleado
	 * @param password Contraseña del empleado
	 * @param permisos Lista de permisos asignados
	 * @return true si el alta se realiza correctamente
	 */
	public boolean darDeAlta(String nickname, String password, List<TipoPermisos> permisos) {
		if (!usuarios.UsuarioRegistrado.validarPassword(password)) {
			return false;
		}
		if (permisos == null || permisos.isEmpty()) {
			return false;
		}
		boolean ok = gestor.darDeAltaEmpleados_Permisos(nickname, password, permisos);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Da de baja a un empleado.
	 *
	 * @param idEmpleado ID del empleado
	 * @return true si la baja se realiza correctamente
	 */
	public boolean darDeBaja(String idEmpleado) {
		boolean ok = gestor.darDeBajaAEmpleado(idEmpleado);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Asigna un permiso a un empleado.
	 *
	 * @param idEmpleado ID del empleado
	 * @param permiso    Permiso a asignar
	 * @return true si la operación se realiza correctamente
	 */
	public boolean asignarPermiso(String idEmpleado, TipoPermisos permiso) {
		boolean ok = gestor.asignarPermiso(idEmpleado, permiso);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Retira un permiso a un empleado.
	 *
	 * @param idEmpleado ID del empleado
	 * @param permiso    Permiso a retirar
	 * @return true si la operación se realiza correctamente
	 */
	public boolean retirarPermiso(String idEmpleado, TipoPermisos permiso) {
		boolean ok = gestor.retirarPermiso(idEmpleado, permiso);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Busca empleados por nombre
	 *
	 * @param texto Texto de búsqueda
	 * @return Lista de empleados encontrados
	 */
	public List<Empleado> buscarEmpleados(String texto) {
		return gestor.buscarEmpleadoPorNombre(texto);
	}

	/**
	 * Devuelve todos los empleados de la tienda.
	 *
	 * @return Lista de empleados
	 */
	public List<Empleado> getEmpleados() {
		return tienda.obtenerEmpleadosTienda();
	}

	/**
	 * Devuelve el nickname de un empleado a partir de su ID.
	 *
	 * @param id ID del empleado
	 * @return Nickname del empleado o el ID si no se encuentra
	 */
	public String getNicknameEmpleado(String id) {
		return getEmpleados().stream().filter(e -> e.getId().equals(id)).map(Empleado::getNickname).findFirst()
				.orElse(id);
	}
}