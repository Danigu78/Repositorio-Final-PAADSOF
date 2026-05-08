package Gui.empleado;


import Gui.VentanaPrincipal;
import Gui.AbstractPanelSection;
import usuarios.Empleado;

/**
 * Clase base para las secciones del panel de empleado.
 * 
 * Hereda todos los métodos visuales comunes de AbstractPanelSection y añade
 * únicamente la referencia al empleado que está usando la interfaz.
 */
public abstract class SeccionEmpleadoBase extends AbstractPanelSection {

	private static final long serialVersionUID = 1L;

	protected final Empleado empleado;

	protected SeccionEmpleadoBase(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana);
		this.empleado = empleado;
	}
}