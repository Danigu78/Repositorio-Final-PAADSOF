package Gui.Controladores.cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Gui.cliente.PanelCliente;

/**
 * Controlador del panel principal del cliente. Gestiona la navegación entre
 * secciones de la barra de navegación. Implementa ActionListener según el
 * patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPanelCliente implements ActionListener {

	/** Vista del panel cliente. */
	private PanelCliente vista;

	/**
	 * Constructor del controlador del panel cliente.
	 *
	 * @param vista El panel cliente
	 */
	public ControladorPanelCliente(PanelCliente vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona los clicks de las pestañas de la barra de navegación. Muestra la
	 * sección, marca la pestaña activa y actualiza los datos.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		vista.mostrarSeccion(cmd);
		vista.marcarPestaña(cmd);
		vista.actualizarSeccion(cmd);
	}
}