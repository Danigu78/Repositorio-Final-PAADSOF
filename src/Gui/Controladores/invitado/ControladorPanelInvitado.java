package Gui.Controladores.invitado;

import javax.swing.JButton;

import Gui.invitado.PanelInvitado;
import Gui.VentanaPrincipal;

public class ControladorPanelInvitado {
	private VentanaPrincipal ventana;
	private PanelInvitado vista;

	public ControladorPanelInvitado(VentanaPrincipal ventana, PanelInvitado panel) {
		this.ventana = ventana;
		this.vista = panel;
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
