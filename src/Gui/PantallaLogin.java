package Gui;

import Gui.controladores.ControladorLogin;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de login y registro de CheckPoint.
 * Extiende AbstractPanelSection para reutilizar helpers visuales.
 * Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class PantallaLogin extends AbstractPanelSection {

    private static final long serialVersionUID = 1L;

    private ControladorLogin controlador;

    private JTextField campoNickname;
    private JPasswordField campoPassword;
    private JComboBox<String> comboTipoUsuario;

    private JTextField campoNicknameReg;
    private JPasswordField campoPasswordReg;
    private JTextField campoDNI;

    private CardLayout cardFormularios;
    private JPanel panelFormularios;

    // Botones — atributos para registrar el controlador
    private JButton botonLogin;
    private JButton botonRegistrar;
    private JButton botonIrRegistro;
    private JButton botonIrLogin;
    private JButton botonInvitado;

    public PantallaLogin(VentanaPrincipal ventana) {
        super(ventana);
        this.controlador = new ControladorLogin(ventana, this);
        inicializarUI();
    }

    private void inicializarUI() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            crearPanelLogo(),
            crearPanelFormularios());
        split.setResizeWeight(0.33);
        split.setDividerSize(0);
        split.setBorder(null);
        split.setEnabled(false);
        add(split, BorderLayout.CENTER);
        setControlador(controlador);
    }

    /**
     * Registra el controlador en los botones — patrón de los apuntes.
     */
    public void setControlador(ActionListener c) {
        registrar(botonLogin,      c, "login");
        registrar(botonRegistrar,  c, "registro");
        registrar(botonIrRegistro, c, "irRegistro");
        registrar(botonIrLogin,    c, "irLogin");
        registrar(botonInvitado,   c, "invitado");
    }

    private void registrar(JButton boton, ActionListener c, String cmd) {
        if (boton == null) return;
        for (ActionListener al : boton.getActionListeners())
            boton.removeActionListener(al);
        boton.setActionCommand(cmd);
        boton.addActionListener(c);
    }

    private JPanel crearPanelLogo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_BARRA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(30));

        gbc.gridy = 0;
        panel.add(crearLogo(), gbc);

        JLabel labelTitulo = new JLabel("CheckPoint");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_LOGO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_ACENTO);
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(labelTitulo, gbc);

        JLabel labelEslogan = crearLabel("Tu tienda friki de confianza");
        labelEslogan.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
        labelEslogan.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        panel.add(labelEslogan, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(VentanaPrincipal.COLOR_ACENTO);
        sep.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(220), VentanaPrincipal.escalar(2)));
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(30));
        panel.add(sep, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        String[] lineas = {
            "Comics y Manga", "Juegos de Mesa",
            "Figuras de Colección", "Segunda Mano e Intercambios"
        };
        for (int i = 0; i < lineas.length; i++) {
            // crearLabel() de AbstractPanelSection
            JLabel label = crearLabel(lineas[i]);
            label.setForeground(new Color(210, 210, 210));
            gbc.gridy = 4 + i;
            gbc.insets = new Insets(VentanaPrincipal.escalar(10),
                VentanaPrincipal.escalar(60), VentanaPrincipal.escalar(10),
                VentanaPrincipal.escalar(20));
            panel.add(label, gbc);
        }

        return panel;
    }

    private JLabel crearLogo() {
        java.net.URL url = getClass().getResource("/fotos/logo.png");
        if (url != null) {
            ImageIcon icono = new ImageIcon(url);
            Image img = icono.getImage().getScaledInstance(
                VentanaPrincipal.escalar(440), VentanaPrincipal.escalar(270),
                Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(img));
        }
        JLabel label = new JLabel("🎮");
        label.setFont(VentanaPrincipal.FUENTE_ICONO);
        label.setForeground(VentanaPrincipal.COLOR_ACENTO);
        return label;
    }

    private JPanel crearPanelFormularios() {
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(VentanaPrincipal.COLOR_PANEL);

        cardFormularios = new CardLayout();
        panelFormularios = new JPanel(cardFormularios);
        panelFormularios.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelFormularios.add(crearFormularioLogin(), "LOGIN");
        panelFormularios.add(crearFormularioRegistro(), "REGISTRO");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        panelDerecho.add(panelFormularios, gbc);

        return panelDerecho;
    }

    private JPanel crearFormularioLogin() {
        JPanel panel = crearPanelFormulario();
        GridBagConstraints gbc = crearGbc();

        JLabel titulo = new JLabel("Iniciar Sesión");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panel.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);

        // crearLabel() de AbstractPanelSection
        gbc.gridy = 1;
        panel.add(crearLabel("Tipo de usuario"), gbc);

        // crearCombo() de AbstractPanelSection
        comboTipoUsuario = crearCombo(
            new String[]{"Cliente", "Empleado", "Gestor"});
        gbc.gridy = 2;
        panel.add(comboTipoUsuario, gbc);

        gbc.gridy = 3;
        panel.add(crearLabel("Nickname"), gbc);

        // crearCampo() de AbstractPanelSection
        campoNickname = crearCampo();
        gbc.gridy = 4;
        panel.add(campoNickname, gbc);

        gbc.gridy = 5;
        panel.add(crearLabel("Contraseña"), gbc);

        campoPassword = crearCampoPassword();
        gbc.gridy = 6;
        panel.add(campoPassword, gbc);

        campoNickname.addActionListener(e -> campoPassword.requestFocus());
        campoPassword.addActionListener(e -> hacerLogin());

        botonLogin = crearBotonLogin("Iniciar Sesión");
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0,
            VentanaPrincipal.escalar(8), 0);
        panel.add(botonLogin, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(8), 0);
        panel.add(new JSeparator(), gbc);

        botonIrRegistro = crearBotonLoginSecundario(
            "¿No tienes cuenta? Regístrate");
        gbc.gridy = 9;
        panel.add(botonIrRegistro, gbc);

        botonInvitado = crearBotonLogin("Continuar como invitado");
        gbc.gridy = 10;
        panel.add(botonInvitado, gbc);

        return panel;
    }

    private JPanel crearFormularioRegistro() {
        JPanel panel = crearPanelFormulario();
        GridBagConstraints gbc = crearGbc();

        JLabel titulo = new JLabel("Crear Cuenta");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panel.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);

        gbc.gridy = 1;
        panel.add(crearLabel("Tipo de cuenta"), gbc);

        JComboBox<String> comboTipoRegistro = crearCombo(
            new String[]{"Cliente"});
        comboTipoRegistro.setEnabled(false);
        gbc.gridy = 2;
        panel.add(comboTipoRegistro, gbc);

        gbc.gridy = 3;
        panel.add(crearLabel("Nickname"), gbc);

        campoNicknameReg = crearCampo();
        gbc.gridy = 4;
        panel.add(campoNicknameReg, gbc);

        gbc.gridy = 5;
        panel.add(crearLabel("Contraseña"), gbc);

        campoPasswordReg = crearCampoPassword();
        gbc.gridy = 6;
        panel.add(campoPasswordReg, gbc);

        // crearLabel() de AbstractPanelSection
        JLabel infoPassword = crearLabel(
            "<html><small>La contraseña debe tener mayúsculas, minúsculas,"
            + "<br>números y caracteres especiales.</small></html>");
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0,
            VentanaPrincipal.escalar(16), 0);
        panel.add(infoPassword, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);

        gbc.gridy = 8;
        panel.add(crearLabel("DNI"), gbc);

        campoDNI = crearCampo();
        gbc.gridy = 9;
        panel.add(campoDNI, gbc);

        campoNicknameReg.addActionListener(e -> campoPasswordReg.requestFocus());
        campoPasswordReg.addActionListener(e -> campoDNI.requestFocus());
        campoDNI.addActionListener(e -> hacerRegistro());

        botonRegistrar = crearBotonLogin("Crear Cuenta");
        gbc.gridy = 10;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0,
            VentanaPrincipal.escalar(8), 0);
        panel.add(botonRegistrar, gbc);

        gbc.gridy = 11;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(8), 0);
        panel.add(new JSeparator(), gbc);

        botonIrLogin = crearBotonLoginSecundario(
            "¿Ya tienes cuenta? Inicia sesión");
        gbc.gridy = 12;
        panel.add(botonIrLogin, gbc);

        return panel;
    }

    public void hacerLogin() {
        controlador.realizarLogin(
            campoNickname.getText().trim(),
            new String(campoPassword.getPassword()),
            String.valueOf(comboTipoUsuario.getSelectedItem()));
    }

    public void hacerRegistro() {
        controlador.realizarRegistro(
            campoNicknameReg.getText().trim(),
            new String(campoPasswordReg.getPassword()),
            campoDNI.getText().trim().toUpperCase(),
            "Cliente");
    }

    public void mostrarRegistro() {
        cardFormularios.show(panelFormularios, "REGISTRO");
        campoNicknameReg.setText("");
        campoPasswordReg.setText("");
        campoDNI.setText("");
    }

    public void mostrarLogin() {
        cardFormularios.show(panelFormularios, "LOGIN");
        campoNickname.setText("");
        campoPassword.setText("");
    }

    public void limpiar() {
        mostrarLogin();
        if (comboTipoUsuario != null) comboTipoUsuario.setSelectedIndex(0);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helpers propios del login ──────────────────────────────────────────

    /**
     * Botón principal del login — naranja con hover COLOR_ACENTO2.
     * Distinto de crearBotonNaranja() porque usa COLOR_ACENTO2 en hover.
     */
    private JButton crearBotonLogin(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));
        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                boton.setBackground(VentanaPrincipal.COLOR_ACENTO2);
            }
            @Override public void mouseExited(MouseEvent e) {
                boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
            }
        });
        return boton;
    }

    /**
     * Botón secundario del login — fondo panel, texto naranja.
     */
    private JButton crearBotonLoginSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setBackground(VentanaPrincipal.COLOR_PANEL);
        boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));
        return boton;
    }

    /**
     * Campo de contraseña — único de login, no está en AbstractPanelSection.
     */
    private JPasswordField crearCampoPassword() {
        JPasswordField campo = new JPasswordField();
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

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60),
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60)));
        return panel;
    }

    private GridBagConstraints crearGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);
        return gbc;
    }
}