package Gui;

import javax.swing.*;
import java.awt.*;
import usuarios.Empleado;

/**
 * Panel principal del empleado en CheckPoint.
 * Contiene las funcionalidades disponibles para un empleado:
 * gestión de stock, packs, pedidos y tasaciones.
 * 
 * @author CheckPoint
 * @version 1.0
 */
public class PanelEmpleado extends JPanel {

    /** Referencia a la ventana principal */
    private VentanaPrincipal ventana;

    /** Empleado actualmente logueado */
    private Empleado empleado;

    /**
     * Constructor del panel empleado.
     * 
     * @param ventana La ventana principal de la aplicación
     */
    public PanelEmpleado(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Panel Empleado - En construcción", SwingConstants.CENTER);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO);
        label.setFont(VentanaPrincipal.FUENTE_TITULO);
        add(label, BorderLayout.CENTER);
    }

    /**
     * Actualiza el panel con los datos del empleado logueado.
     * 
     * @param empleado El empleado que ha iniciado sesión
     */
    public void actualizarEmpleado(Empleado empleado) {
        this.empleado = empleado;
        // TODO: cargar datos del empleado
    }
}
