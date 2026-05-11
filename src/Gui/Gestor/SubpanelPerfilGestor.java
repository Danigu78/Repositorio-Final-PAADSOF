package Gui.Gestor;

import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorConfiguracionGestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Gestor;

/**
 * Subpanel de perfil del gestor.
 * Extiende AbstractPanelGestor para reutilizar helpers visuales.
 * Sigue el patrón MVC de los apuntes.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelPerfilGestor extends AbstractPanelGestor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ControladorConfiguracionGestor controlador;

    // Campos — atributos para que el controlador pueda leerlos
    private JTextField campoNick;
    private JPasswordField campoPass;
    private JLabel labelNickActual;

    // Botón — atributo para registrar el controlador
    private JButton botonGuardar;

    public SubpanelPerfilGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana, gestor);
        this.controlador = new ControladorConfiguracionGestor(this, gestor);
        inicializarUI();
    }

    private void inicializarUI() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelContenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50),
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);

        JLabel titulo = new JLabel("Mi Perfil");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panelContenido.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);

        // crearLabel() de AbstractPanelSection
        labelNickActual = crearLabel(
            "Nickname actual: " + controlador.getNickname());
        gbc.gridy = 1;
        panelContenido.add(labelNickActual, gbc);

        gbc.gridy = 2;
        panelContenido.add(crearLabel("Nuevo nickname:"), gbc);
        // crearCampo() de AbstractPanelSection
        campoNick = crearCampo();
        campoNick.setText(controlador.getNickname());
        gbc.gridy = 3;
        panelContenido.add(campoNick, gbc);

        gbc.gridy = 4;
        panelContenido.add(crearLabel("Nueva contraseña:"), gbc);
        // crearCampoPasswordGestor() de AbstractPanelGestor
        campoPass = crearCampoPasswordGestor();
        gbc.gridy = 5;
        panelContenido.add(campoPass, gbc);

        JLabel labelInfo = crearLabel(
            "<html><small>Mínimo 8 caracteres, mayúsculas, minúsculas,"
            + " números y especiales.</small></html>");
        gbc.gridy = 6;
        panelContenido.add(labelInfo, gbc);

        // crearBotonNaranja() de AbstractPanelSection
        botonGuardar = crearBotonNaranja("Guardar cambios");
        botonGuardar.setActionCommand("guardarPerfil");
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panelContenido.add(botonGuardar, gbc);

        add(panelContenido, BorderLayout.CENTER);
        setControlador(controlador);
    }

    public void setControlador(ActionListener c) {
        if (botonGuardar != null) {
            for (ActionListener al : botonGuardar.getActionListeners())
                botonGuardar.removeActionListener(al);
            botonGuardar.addActionListener(c);
        }
    }

    /**
     * Lee los campos y guarda el perfil.
     * Lo llama el controlador.
     */
    public void procesarGuardar() {
        String nuevoNick = campoNick.getText().trim();
        String nuevaPass = new String(campoPass.getPassword());

        if (controlador.modificarPerfil(nuevoNick, nuevaPass)) {
            mostrarMensaje("Perfil actualizado correctamente.");
            labelNickActual.setText("Nickname actual: " + nuevoNick);
        } else {
            mostrarError(
                "No se pudo actualizar el perfil. Comprueba los datos.");
        }
    }

    @Override
    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}