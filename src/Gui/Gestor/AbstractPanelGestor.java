package Gui.Gestor;

import Gui.AbstractPanelSection;
import Gui.VentanaPrincipal;
import usuarios.Gestor;
import javax.swing.*;
import java.awt.*;

/**
 * Clase base para las secciones del panel del gestor.
 * Extiende AbstractPanelSection para reutilizar helpers visuales.
 *
 * @author Antonino
 * @version 1.0
 */
public abstract class AbstractPanelGestor extends AbstractPanelSection {

    protected final Gestor gestor;

    protected AbstractPanelGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana);
        this.gestor = gestor;
    }

    /**
     * Crea un campo de texto con columnas — útil en formularios del gestor.
     */
    protected JTextField crearCampoColumnas(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))));
        return campo;
    }

    /**
     * Crea un campo de contraseña estilizado.
     */
    protected JPasswordField crearCampoPasswordGestor() {
        JPasswordField campo = new JPasswordField(15);
        campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))));
        return campo;
    }

    @Override
    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String msg) {
        mostrarMensaje(msg);
    }
}