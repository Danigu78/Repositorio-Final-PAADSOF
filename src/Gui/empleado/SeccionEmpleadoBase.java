package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.PanelBaseInterfaz;
import usuarios.Empleado;

/**
 * Base de las pantallas del empleado.
 *
 * Guarda la ventana y el empleado.
 */
public abstract class SeccionEmpleadoBase extends PanelBaseInterfaz {

	private static final long serialVersionUID = 1L;

	protected final Empleado empleado;

	protected SeccionEmpleadoBase(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana);
		this.empleado = empleado;
	}
}
