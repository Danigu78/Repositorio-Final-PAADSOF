package Gui;

import Gui.Controladores.ControladorConfiguracionGestor;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import usuarios.Gestor;

/**
 * Subpanel de perfil del gestor.
 * Permite modificar el nickname y contraseña del gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelPerfilGestor extends JPanel {

    private VentanaPrincipal ventana;
    private Gestor gestor;
    private ControladorConfiguracionGestor controlador;

    /**
     * Constructor del subpanel de perfil del gestor.
     *
     * @param ventana La ventana principal
     * @param gestor  El gestor logueado
     */
    public SubpanelPerfilGestor(VentanaPrincipal ventana, Gestor gestor) {
        this.ventana = ventana;
        this.gestor = gestor;
        this.controlador = new ControladorConfiguracionGestor(gestor);
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    /**
     * Construye la interfaz con el formulario de modificación de perfil.
     */
    private void inicializarUI() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50),
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

        // Título
        JLabel titulo = new JLabel("Mi Perfil");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panelContenido.add(titulo, gbc);

        // Nickname actual
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);
        JLabel labelNickActual = new JLabel("Nickname actual: " + gestor.getNickname());
        labelNickActual.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelNickActual.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridy = 1;
        panelContenido.add(labelNickActual, gbc);

        // Nuevo nickname
        gbc.gridy = 2;
        panelContenido.add(crearEtiqueta("Nuevo nickname:"), gbc);
        JTextField campoNick = crearCampo();
        campoNick.setText(gestor.getNickname());
        gbc.gridy = 3;
        panelContenido.add(campoNick, gbc);

        // Nueva contraseña
        gbc.gridy = 4;
        panelContenido.add(crearEtiqueta("Nueva contraseña:"), gbc);
        JPasswordField campoPass = new JPasswordField();
        campoPass.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campoPass.setForeground(Color.BLACK);
        campoPass.setBackground(Color.WHITE);
        campoPass.setCaretColor(Color.BLACK);
        campoPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))
        ));
        gbc.gridy = 5;
        panelContenido.add(campoPass, gbc);

        // Info contraseña
        JLabel labelInfo = new JLabel("<html><small>Mínimo 8 caracteres, mayúsculas, minúsculas, números y especiales.</small></html>");
        labelInfo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelInfo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridy = 6;
        panelContenido.add(labelInfo, gbc);

        // Botón guardar
        JButton botonGuardar = crearBoton("Guardar cambios");
        botonGuardar.addActionListener(e -> {
            String nuevoNick = campoNick.getText().trim();
            String nuevaPass = new String(campoPass.getPassword());

            if (controlador.modificarPerfil(nuevoNick, nuevaPass)) {
                JOptionPane.showMessageDialog(this,
                    "Perfil actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Actualizamos la etiqueta del nickname actual
                labelNickActual.setText("Nickname actual: " + gestor.getNickname());
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el perfil. Comprueba los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panelContenido.add(botonGuardar, gbc);

        add(panelContenido, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))
        ));
        return campo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)
        ));
        return boton;
    }
}