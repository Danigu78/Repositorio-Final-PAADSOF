package Gui.Controladores.empleado;

import Gui.empleado.PanelEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import usuarios.Empleado;
import usuarios.TipoPermisos;

/**
 * Controlador del panel principal del empleado.
 *
 * Mira permisos y cambia de seccion.
 */
public class ControladorPanelEmpleado implements ActionListener {

	public static final String LOGOUT = "panel.logout";

	/** Empleado que esta usando el panel. */
	private final Empleado empleado;

	/** Vista asociada al controlador. */
	private PanelEmpleado vista;

	/**
	 * Crea el controlador.
	 *
	 * @param empleado El empleado logueado
	 */
	public ControladorPanelEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Guarda la vista.
	 *
	 * @param vista El panel empleado
	 */
	public void setVista(PanelEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona los botones de la barra.
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
			// El comando coincide con el nombre de la seccion.
			vista.mostrarSeccion(cmd);
			vista.marcarPestaña(cmd);
		}
	}

	/**
	 * Comprueba si puede entrar al panel.
	 *
	 * @return true si hay empleado, no está despedido y tiene sesión iniciada
	 */
	public boolean empleadoPuedeVerPanel() {
		return empleado != null && !empleado.isDespedido() && empleado.isSesionIniciada();
	}

	/**
	 * Mira si tiene un permiso.
	 *
	 * @param permiso El permiso a comprobar
	 * @return true si tiene el permiso
	 */
	public boolean tienePermiso(TipoPermisos permiso) {
		return empleado != null && permiso != null && empleado.tienePermiso(permiso);
	}

	/**
	 * Devuelve el nickname.
	 *
	 * @return Nickname del empleado o "Empleado" si es null
	 */
	public String getNicknameEmpleado() {
		if (empleado == null || empleado.getNickname() == null)
			return "Empleado";
		return empleado.getNickname();
	}

	/**
	 * Devuelve los permisos en una lista.
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
