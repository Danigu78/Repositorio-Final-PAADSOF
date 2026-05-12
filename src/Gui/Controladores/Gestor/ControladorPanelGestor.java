package Gui.Controladores.Gestor;

import Gui.Gestor.PanelGestor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del panel principal del gestor.
 * Gestiona la navegación entre secciones de la barra de navegación.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorPanelGestor implements ActionListener {

	/** Vista del panel gestor. */
	private PanelGestor vista;

	/**
	 * Constructor del controlador del panel gestor.
	 *
	 * @param vista El panel gestor
	 */
	public ControladorPanelGestor(PanelGestor vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona los eventos de navegación de la barra lateral.
	 * Muestra la sección seleccionada y marca la pestaña activa.
	 *
	 * @param e Evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		vista.mostrarSeccion(cmd);
		vista.marcarPestaña(cmd);
	}
}