package Gui.Controladores.empleado;

import Gui.empleado.PanelEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import usuarios.Empleado;
import usuarios.TipoPermisos;

/**
 * Controlador del panel principal del empleado. Centraliza las comprobaciones
 * de acceso, permisos y datos básicos del empleado actual. Gestiona la
 * navegación entre secciones. Implementa ActionListener según el patrón MVC de
 * los apuntes.
 *
 * @author Lucas
 * @version 1.0
 */
public class ControladorPanelEmpleado implements ActionListener {

	public static final String LOGOUT = "panel.logout";

	/** Empleado logueado. */
	private final Empleado empleado;

	/** Vista del panel empleado. */
	private PanelEmpleado vista;

	/**
	 * Constructor del controlador del panel empleado.
	 *
	 * @param empleado El empleado logueado
	 */
	public ControladorPanelEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Enlaza la vista al controlador.
	 *
	 * @param vista El panel empleado
	 */
	public void setVista(PanelEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona los clicks de la barra de navegación. Si es logout cierra sesión. Si
	 * no, navega a la sección indicada.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null)
			return;
		String cmd = e.getActionCommand();
		if (LOGOUT.equals(cmd)) {
			vista.salir();
		} else {
			// cmd es directamente el id de sección — viene de la barra de
			// AbstractPanelSection
			vista.mostrarSeccion(cmd);
			vista.marcarPestaña(cmd);
		}
	}

	/**
	 * Comprueba si el empleado puede acceder al panel.
	 *
	 * @return true si hay empleado, no está despedido y tiene sesión iniciada
	 */
	public boolean empleadoPuedeVerPanel() {
		return empleado != null && !empleado.isDespedido() && empleado.isSesionIniciada();
	}

	/**
	 * Comprueba si el empleado tiene un permiso concreto.
	 *
	 * @param permiso El permiso a comprobar
	 * @return true si tiene el permiso
	 */
	public boolean tienePermiso(TipoPermisos permiso) {
		return empleado != null && permiso != null && empleado.tienePermiso(permiso);
	}

	/**
	 * Devuelve el nickname del empleado.
	 *
	 * @return Nickname del empleado o "Empleado" si es null
	 */
	public String getNicknameEmpleado() {
		if (empleado == null || empleado.getNickname() == null)
			return "Empleado";
		return empleado.getNickname();
	}

	/**
	 * Devuelve los permisos del empleado en una lista.
	 *
	 * @return Lista de permisos del empleado
	 */
	public List<TipoPermisos> getPermisosOrdenados() {
		List<TipoPermisos> permisos = new ArrayList<>();
		if (empleado == null || empleado.getPermisos() == null)
			return permisos;
		permisos.addAll(empleado.getPermisos());
		return permisos;
	}
}