package Gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import Gui.Controladores.ControladorLogin;

/**
 * Pantalla de inicio de sesión y registro de la aplicación CheckPoint.
 * Solo gestiona la interfaz gráfica — la lógica la delega en ControladorLogin.
 *
 * @author CheckPoint
 * @version 1.0
 */
public class PantallaLogin extends JPanel {

    /** Referencia a la ventana principal */
    private VentanaPrincipal ventana;

    /** Controlador que gestiona la lógica de login */
    private ControladorLogin controlador;

    /** Campo de texto para el nickname */
    private JTextField campoNickname;

    /** Campo de contraseña */
    private JPasswordField campoPassword;

    /** Selector del tipo de usuario */
    private JComboBox<String> comboTipoUsuario;

    /** Panel del formulario de login */
    private JPanel panelLogin;

    /** Panel del formulario de registro */
    private JPanel panelRegistro;

    /** Campo nickname para registro */
    private JTextField campoNicknameReg;

    /** Campo password para registro */
    private JPasswordField campoPasswordReg;

    /** Campo DNI para registro */
    private JTextField campoDNI;

    /** Selector del tipo de cuenta en registro */
    private JComboBox<String> comboTipoRegistro;

    /** CardLayout para alternar entre login y registro */
    private CardLayout cardFormularios;

    /** Panel que contiene login y registro */
    private JPanel panelFormularios;

    /**
     * Constructor de la pantalla de login.
     *
     * @param ventana La ventana principal de la aplicación
     */
    public PantallaLogin(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.controlador = new ControladorLogin(ventana, this);
        inicializarUI();
    }

    /**
     * Construye la interfaz dividida en panel izquierdo (logo) y
     * panel derecho (formulario) con proporción 33/67.
     */
    private void inicializarUI() {
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel panelIzquierdo = crearPanelLogo();
        JPanel panelDerecho = crearPanelFormularios();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setResizeWeight(0.33);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        splitPane.setEnabled(false);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Crea el panel izquierdo con fondo oscuro, logo y descripción.
     *
     * @return El panel izquierdo configurado
     */
    private JPanel crearPanelLogo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_BARRA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30));
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo
        java.net.URL urlImagen = getClass().getResource("/fotos/logo.png");
        JLabel labelImagen;
        if (urlImagen != null) {
            ImageIcon icono = new ImageIcon(urlImagen);
            Image img = icono.getImage().getScaledInstance(
                VentanaPrincipal.escalar(200), VentanaPrincipal.escalar(200), Image.SCALE_SMOOTH);
            labelImagen = new JLabel(new ImageIcon(img));
        } else {
            labelImagen = new JLabel("🎮");
            labelImagen.setFont(VentanaPrincipal.FUENTE_ICONO);
            labelImagen.setForeground(VentanaPrincipal.COLOR_ACENTO);
        }
        gbc.gridy = 0;
        panel.add(labelImagen, gbc);

        // Nombre de la tienda
        JLabel labelTitulo = new JLabel("CheckPoint");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_LOGO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_ACENTO);
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(labelTitulo, gbc);

        // Eslogan
        JLabel labelEslogan = new JLabel("Tu tienda friki de confianza");
        labelEslogan.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelEslogan.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
        labelEslogan.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        panel.add(labelEslogan, gbc);

        // Separador decorativo
        JSeparator sep = new JSeparator();
        sep.setForeground(VentanaPrincipal.COLOR_ACENTO);
        sep.setPreferredSize(new Dimension(VentanaPrincipal.escalar(220), VentanaPrincipal.escalar(2)));
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30));
        panel.add(sep, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // Líneas descriptivas
        String[] lineas = {"Comics y Manga", "Juegos de Mesa", "Figuras de Colección", "Segunda Mano e Intercambios"};
        for (int i = 0; i < lineas.length; i++) {
            JLabel label = new JLabel(lineas[i]);
            label.setFont(VentanaPrincipal.FUENTE_NORMAL);
            label.setForeground(new Color(210, 210, 210));
            gbc.gridy = 4 + i;
            gbc.insets = new Insets(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(60),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20));
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(label, gbc);
        }
        return panel;
    }

    /**
     * Crea el panel derecho con CardLayout para alternar entre login y registro.
     *
     * @return El panel derecho configurado
     */
    private JPanel crearPanelFormularios() {
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(VentanaPrincipal.COLOR_PANEL);

        panelLogin = crearFormularioLogin();
        panelRegistro = crearFormularioRegistro();

        cardFormularios = new CardLayout();
        panelFormularios = new JPanel(cardFormularios);
        panelFormularios.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelFormularios.add(panelLogin, "LOGIN");
        panelFormularios.add(panelRegistro, "REGISTRO");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelDerecho.add(panelFormularios, gbc);

        return panelDerecho;
    }

    /**
     * Crea el formulario de login con tipo de usuario, nickname, contraseña y botones.
     *
     * @return El panel con el formulario de login
     */
    private JPanel crearFormularioLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60),
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8), 0);

        // Título
        JLabel labelTitulo = new JLabel("Iniciar Sesión");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panel.add(labelTitulo, gbc);

        // Tipo de usuario
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);
        gbc.gridy = 1;
        panel.add(crearEtiqueta("Tipo de usuario"), gbc);
        comboTipoUsuario = new JComboBox<>(new String[]{"Cliente", "Empleado", "Gestor"});
        estilizarCombo(comboTipoUsuario);
        gbc.gridy = 2;
        panel.add(comboTipoUsuario, gbc);

        // Nickname
        gbc.gridy = 3;
        panel.add(crearEtiqueta("Nickname"), gbc);
        campoNickname = crearCampoTexto();
        gbc.gridy = 4;
        panel.add(campoNickname, gbc);

        // Contraseña
        gbc.gridy = 5;
        panel.add(crearEtiqueta("Contraseña"), gbc);
        campoPassword = crearCampoPassword();
        gbc.gridy = 6;
        panel.add(campoPassword, gbc);

        // Botón login — delega en el controlador
        JButton botonLogin = crearBoton("Iniciar Sesión", true);
        botonLogin.addActionListener(e -> controlador.realizarLogin(
            campoNickname.getText().trim(),
            new String(campoPassword.getPassword()),
            (String) comboTipoUsuario.getSelectedItem()
        ));
        campoPassword.addActionListener(e -> controlador.realizarLogin(
            campoNickname.getText().trim(),
            new String(campoPassword.getPassword()),
            (String) comboTipoUsuario.getSelectedItem()
        ));
        campoNickname.addActionListener(e -> campoPassword.requestFocus());
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, VentanaPrincipal.escalar(8), 0);
        panel.add(botonLogin, gbc);

        // Separador
        gbc.gridy = 8;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8), 0);
        panel.add(new JSeparator(), gbc);

        // Botón ir a registro — delega en el controlador
        JButton botonIrRegistro = crearBoton("¿No tienes cuenta? Regístrate", false);
        botonIrRegistro.addActionListener(e -> controlador.irARegistro());
        gbc.gridy = 9;
        panel.add(botonIrRegistro, gbc);

        // Botón continuar como invitado — delega en el controlador
        JButton botonInvitado = crearBoton("Continuar como invitado", true);
        botonInvitado.addActionListener(e -> controlador.continuarComoInvitado());
        gbc.gridy = 10;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8), 0);
        panel.add(botonInvitado, gbc);

        return panel;
    }

    /**
     * Crea el formulario de registro con nickname, contraseña, tipo y DNI.
     *
     * @return El panel con el formulario de registro
     */
    private JPanel crearFormularioRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60),
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

        // Título
        JLabel labelTitulo = new JLabel("Crear Cuenta");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panel.add(labelTitulo, gbc);

        // Tipo de cuenta
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);
        gbc.gridy = 1;
        panel.add(crearEtiqueta("Tipo de cuenta"), gbc);
        comboTipoRegistro = new JComboBox<>(new String[]{"Cliente", "Empleado"});
        estilizarCombo(comboTipoRegistro);
        gbc.gridy = 2;
        panel.add(comboTipoRegistro, gbc);

        // Nickname
        gbc.gridy = 3;
        panel.add(crearEtiqueta("Nickname"), gbc);
        campoNicknameReg = crearCampoTexto();
        gbc.gridy = 4;
        panel.add(campoNicknameReg, gbc);

        // Contraseña
        gbc.gridy = 5;
        panel.add(crearEtiqueta("Contraseña"), gbc);
        campoPasswordReg = crearCampoPassword();
        gbc.gridy = 6;
        panel.add(campoPasswordReg, gbc);

        // Info contraseña
        JLabel labelInfo = new JLabel(
            "<html><small>La contraseña debe tener mayúsculas, minúsculas," +
            "<br>números y caracteres especiales.</small></html>");
        labelInfo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelInfo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(16), 0);
        panel.add(labelInfo, gbc);

        // DNI
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);
        gbc.gridy = 8;
        panel.add(crearEtiqueta("DNI (solo cliente)"), gbc);
        campoDNI = crearCampoTexto();
        gbc.gridy = 9;
        panel.add(campoDNI, gbc);

        // Activar/desactivar DNI según tipo
        comboTipoRegistro.addActionListener(e -> {
            boolean esCliente = "Cliente".equals(comboTipoRegistro.getSelectedItem());
            campoDNI.setEnabled(esCliente);
            if (!esCliente) campoDNI.setText("");
        });

        // Botón registrar — delega en el controlador
        JButton botonRegistrar = crearBoton("Crear Cuenta", true);
        botonRegistrar.addActionListener(e -> controlador.realizarRegistro(
            campoNicknameReg.getText().trim(),
            new String(campoPasswordReg.getPassword()),
            campoDNI.getText().trim().toUpperCase(),
            (String) comboTipoRegistro.getSelectedItem()
        ));
        gbc.gridy = 10;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, VentanaPrincipal.escalar(8), 0);
        panel.add(botonRegistrar, gbc);

        // Separador
        gbc.gridy = 11;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8), 0);
        panel.add(new JSeparator(), gbc);

        // Botón volver al login — delega en el controlador
        JButton botonVolverLogin = crearBoton("¿Ya tienes cuenta? Inicia sesión", false);
        botonVolverLogin.addActionListener(e -> controlador.irALogin());
        gbc.gridy = 12;
        panel.add(botonVolverLogin, gbc);

        return panel;
    }

    // ── Métodos públicos llamados por el controlador ───────────────────────────

    /**
     * Muestra el formulario de registro y limpia sus campos.
     */
    public void mostrarRegistro() {
        cardFormularios.show(panelFormularios, "REGISTRO");
        campoNicknameReg.setText("");
        campoPasswordReg.setText("");
        campoDNI.setText("");
    }

    /**
     * Muestra el formulario de login y limpia sus campos.
     */
    public void mostrarLogin() {
        cardFormularios.show(panelFormularios, "LOGIN");
        campoNickname.setText("");
        campoPassword.setText("");
    }

    /**
     * Limpia los campos y vuelve al login. Se llama al hacer logout.
     */
    public void limpiar() {
        mostrarLogin();
        if (comboTipoUsuario != null) comboTipoUsuario.setSelectedIndex(0);
    }

    /**
     * Muestra un diálogo de error. Lo llama el controlador.
     *
     * @param mensaje El mensaje de error
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un diálogo de éxito. Lo llama el controlador.
     *
     * @param mensaje El mensaje de éxito
     */
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "¡Éxito!", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Métodos de estilo ─────────────────────────────────────────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    private JTextField crearCampoTexto() {
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
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))
        ));
        return campo;
    }

    private JButton crearBoton(String texto, boolean principal) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (principal) {
            boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
            boton.setForeground(Color.WHITE);
            boton.setOpaque(true);
            boton.setBorderPainted(false);
            boton.setBorder(BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));
            boton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { boton.setBackground(VentanaPrincipal.COLOR_ACENTO2); }
                @Override
                public void mouseExited(MouseEvent e) { boton.setBackground(VentanaPrincipal.COLOR_ACENTO); }
            });
        } else {
            boton.setBackground(VentanaPrincipal.COLOR_PANEL);
            boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
            boton.setBorderPainted(false);
            boton.setBorder(BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));
        }
        return boton;
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        combo.setForeground(Color.BLACK);
        combo.setBackground(Color.WHITE);
    }
}