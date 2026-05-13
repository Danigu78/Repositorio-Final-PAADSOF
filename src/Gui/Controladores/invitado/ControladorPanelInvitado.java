package Gui.Controladores.invitado;

import javax.swing.JButton;

import Gui.invitado.PanelInvitado;
import Gui.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorPanelInvitado implements ActionListener {
	public static final String IR_REGISTRO = "invitado.irRegistro";
	public static final String SALIR = "invitado.salir";
	public static final String NAVEGAR = "invitado.navegar:";

	private VentanaPrincipal ventana;
	private PanelInvitado vista;

	public ControladorPanelInvitado(VentanaPrincipal ventana, PanelInvitado panel) {
		this.ventana = ventana;
		this.vista = panel;
	}

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
	 * Gestiona el clic en las pestañas de navegación
	 * 
	 * @param botonPulsado El JButton que el usuario ha clicado
	 * @param seccion      El identificador de la sección (ej: "CATALOGO")
	 */
	public void gestionarNavegacion(JButton botonPulsado, String seccion) {
		vista.marcarBotonActivo(botonPulsado);
		vista.mostrarSeccion(seccion);
	}

	public void irARegistro() {
		ventana.mostrarPantalla(VentanaPrincipal.PANTALLA_LOGIN);
		vista.desmarcarTodo();
	}

	public void salirDeAplicacion() {
		System.exit(0);

	}
}
