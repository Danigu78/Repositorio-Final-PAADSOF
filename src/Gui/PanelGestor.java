package Gui;

import javax.swing.*;
import java.awt.*;
import usuarios.Gestor;

/**
 * Panel principal del gestor en CheckPoint.
 * Contiene todas las funcionalidades de administración:
 * gestión de empleados, categorías, descuentos, estadísticas y recomendador.
 * 
 * @author CheckPoint
 * @version 1.0
 */
public class PanelGestor extends JPanel {

    /** Referencia a la ventana principal */
    private VentanaPrincipal ventana;

    /** Gestor actualmente logueado */
    private Gestor gestor;

    /**
     * Constructor del panel gestor.
     * 
     * @param ventana La ventana principal de la aplicación
     */
    public PanelGestor(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Panel Gestor - En construcción", SwingConstants.CENTER);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO);
        label.setFont(VentanaPrincipal.FUENTE_TITULO);
        add(label, BorderLayout.CENTER);
    }

    /**
     * Actualiza el panel con los datos del gestor logueado.
     * 
     * @param gestor El gestor que ha iniciado sesión
     */
 // En PanelGestor.java
    public void actualizarGestor(Gestor gestor) { // Debe ser public
        this.gestor = gestor;
        System.out.println("Gestor actualizado:") ; // Prueba esto para debug
    }
}
