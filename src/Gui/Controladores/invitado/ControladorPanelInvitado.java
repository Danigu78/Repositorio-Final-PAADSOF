package Gui.Controladores.invitado;

import javax.swing.JButton;

import Gui.invitado.PanelInvitado;
import Gui.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del panel de invitado. Gestiona la navegación dentro de la vista
 * de invitado y las acciones básicas como ir al registro o salir de la
 * aplicación.
 *
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPanelInvitado implements ActionListener {
	/** Acción para ir a la pantalla de registro/login */
	public static final String IR_REGISTRO = "invitado.irRegistro";

	/** Acción para salir de la aplicación */
	public static final String SALIR = "invitado.salir";
	/** Prefijo para acciones de navegación entre secciones */
	public static final String NAVEGAR = "invitado.navegar:";
	/** Ventana principal de la aplicación */
	private VentanaPrincipal ventana;
	/** Vista del panel de invitado */
	private PanelInvitado vista;

	/**
	 * Constructor del controlador del panel de invitado.
	 *
	 * @param ventana ventana principal de la aplicación
	 * @param panel   vista del invitado
	 */
	public ControladorPanelInvitado(VentanaPrincipal ventana, PanelInvitado panel) {
		this.ventana = ventana;
		this.vista = panel;
	}

	/**
	 * Gestiona los eventos de la interfaz del invitado.
	 *
	 * @param e evento de acción generado por botones u otros componentes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;

		String cmd = e.getActionCommand();
		if (IR_REGISTRO.equals(cmd)) {
			irARegistro();
		} else if (SALIR.equals(cmd)) {
			salirDeAplicacion();
		} else if (cmd != null && cmd.startsWith(NAVEGAR) && e.getSource() instanceof JButton) {
			gestionarNavegacion((JButton) e.getSource(), cmd.substring(NAVEGAR.length()));
		}
	}

	/**
	 * Gestiona la navegación entre secciones del panel de invitado.
	 *
	 * @param botonPulsado botón que ha sido pulsado
	 * @param seccion      identificador de la sección a mostrar
	 */
	public void gestionarNavegacion(JButton botonPulsado, String seccion) {
		vista.marcarBotonActivo(botonPulsado);
		vista.mostrarSeccion(seccion);
	}

	/**
	 * Navega a la pantalla de registro/login y resetea el estado visual del panel.
	 */
	public void irARegistro() {
		ventana.mostrarPantalla(VentanaPrincipal.PANTALLA_LOGIN);
		vista.desmarcarTodo();
	}

	/**
	 * Cierra completamente la aplicación.
	 */

	public void salirDeAplicacion() {
		System.exit(0);

	}
}
