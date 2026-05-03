package Gui;

import Gui.Controladores.ControladorPerfil;

import javax.sound.midi.VoiceStatus;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Cliente;

/**
 * Subpanel del perfil del cliente.
 * Permite cambiar nickname y contraseña.

 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPerfil extends JPanel {

    private VentanaPrincipal ventana;
    private Cliente cliente;
    private ControladorPerfil controlador;
    private PanelCliente panelCliente;

    private JTextField campoNickname;
    private JPasswordField campoPassword;
    private JLabel labelNicknameActual;
    private JButton botonGuardar;

    public SubpanelPerfil(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
    }

    /**
     * Actualiza el subpanel con los datos del cliente.
     * Crea el controlador y lo registra en el botón.
     */
    public void actualizar(Cliente cliente) {
        System.out.println("SUBPANEL.PERFIL.ACTUALIZAR");
    	this.cliente = cliente;
        this.controlador = new ControladorPerfil(this, cliente);
        removeAll();
        add(crearPanelPerfil(), BorderLayout.CENTER);
        setControlador(controlador);
        revalidate();
        repaint();
    }

    /**
     * Registra el controlador en el botón — patrón de los apuntes.
     */
    public void setControlador(ActionListener c) {
        if (botonGuardar != null) {
            for (ActionListener al : botonGuardar.getActionListeners())
                botonGuardar.removeActionListener(al);
            botonGuardar.addActionListener(c);
        }
    }

    /**
     * Crea el panel principal del perfil centrado.
     */
    private JPanel crearPanelPerfil() {
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40),
                VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40))));
        panel.setPreferredSize(new Dimension(VentanaPrincipal.escalar(450), VentanaPrincipal.escalar(400)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(VentanaPrincipal.escalar(6), 0, VentanaPrincipal.escalar(6), 0);

        // Título
        JLabel titulo = new JLabel("Mi Perfil");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
        panel.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

        // Nickname actual
        labelNicknameActual = new JLabel("Nickname actual: " + cliente.getNickname());
        labelNicknameActual.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelNicknameActual.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridy = 1;
        panel.add(labelNicknameActual, gbc);

        gbc.gridy = 2;
        panel.add(new JSeparator(), gbc);

        // Nuevo nickname
        gbc.gridy = 3;
        panel.add(crearEtiqueta("Nuevo nickname (déjalo vacío para no cambiar):"), gbc);
        campoNickname = crearCampo();
        gbc.gridy = 4;
        panel.add(campoNickname, gbc);

        // Nueva contraseña
        gbc.gridy = 5;
        panel.add(crearEtiqueta("Nueva contraseña:"), gbc);
        campoPassword = new JPasswordField();
        campoPassword.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campoPassword.setForeground(Color.BLACK);
        campoPassword.setBackground(Color.WHITE);
        campoPassword.setCaretColor(Color.BLACK);
        campoPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
        gbc.gridy = 6;
        panel.add(campoPassword, gbc);

        // Info contraseña
        JLabel labelInfo = new JLabel(
            "<html><small>Debe tener mayúsculas, minúsculas, números y caracteres especiales.</small></html>");
        labelInfo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelInfo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridy = 7;
        panel.add(labelInfo, gbc);

        // Botón guardar
        botonGuardar = new JButton("Guardar cambios");
        botonGuardar.setActionCommand("guardar");
        botonGuardar.setFont(VentanaPrincipal.FUENTE_BOTON);
        botonGuardar.setBackground(VentanaPrincipal.COLOR_ACENTO);
        botonGuardar.setForeground(Color.WHITE);
        botonGuardar.setOpaque(true);
        botonGuardar.setBorderPainted(false);
        botonGuardar.setFocusPainted(false);
        botonGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonGuardar.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20)));
        botonGuardar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botonGuardar.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botonGuardar.setBackground(VentanaPrincipal.COLOR_ACENTO);
            }
        });
        gbc.gridy = 8;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panel.add(botonGuardar, gbc);

        panelCentral.add(panel);
        return panelCentral;
    }

    // ── Getters para que el controlador lea los campos ────────────────────────

    public String getNuevoNickname() {
        return campoNickname.getText().trim();
    }

    public String getNuevaPassword() {
        return new String(campoPassword.getPassword()).trim();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
        return campo;
    }
    public void setPanelCliente(PanelCliente panelCliente) {
    	this.panelCliente=panelCliente;
    }
    public void actualizarNombreEnBarra() {
    	if (panelCliente!=null) {
			panelCliente.actualizarNombreUsuario(cliente.getNickname());
		}
    }
}