package Gui.Controladores;

import javax.swing.JButton;

import Gui.PanelInvitado;
import Gui.VentanaPrincipal;

public class ControladorPanelInvitado {
private VentanaPrincipal ventana;
private PanelInvitado vista;

public ControladorPanelInvitado(VentanaPrincipal ventana, PanelInvitado panel) {
	this.ventana=ventana;
	this.vista=panel;
}/**
 * Gestiona el clic en las pestañas de navegación
 * @param botonPulsado El JButton que el usuario ha clicado
 * @param seccion El identificador de la sección (ej: "CATALOGO")
 */
public void gestionarNavegacion(JButton botonPulsado, String seccion) {
    // 1. Lógica Visual: Le pedimos a la vista que use el método que acabamos de hacer
    vista.marcarBotonActivo(botonPulsado);

    // 2. Lógica de Navegación: Cambiamos el panel central de la aplicación
    ventana.mostrarPantalla(seccion);
    
  
}
}
