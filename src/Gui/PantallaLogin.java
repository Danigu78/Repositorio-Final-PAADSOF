package Gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Gui.Controladores.ControladorLogin;

public class PantallaLogin extends JPanel {

	private static final long serialVersionUID = 1L;

	private VentanaPrincipal ventana;
	private ControladorLogin controlador;

	private JTextField campoNickname;
	private JPasswordField campoPassword;
	private JComboBox<String> comboTipoUsuario;

	private JTextField campoNicknameReg;
	private JPasswordField campoPasswordReg;
	private JTextField campoDNI;
	private JComboBox<String> comboTipoRegistro;

	private CardLayout cardFormularios;
	private JPanel panelFormularios;

	public PantallaLogin(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.controlador = new ControladorLogin(ventana, this);
		inicializarUI();
	}

	private void inicializarUI() {
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		JPanel panelIzquierdo = crearPanelLogo();
		JPanel panelDerecho = crearPanelFormularios();

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
		split.setResizeWeight(0.33);
		split.setDividerSize(0);
		split.setBorder(null);
		split.setEnabled(false);

		add(split, BorderLayout.CENTER);
	}

	private JPanel crearPanelLogo() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_BARRA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30));

		JLabel labelImagen = crearLogo();
		gbc.gridy = 0;
		panel.add(labelImagen, gbc);

		JLabel labelTitulo = new JLabel("CheckPoint");
		labelTitulo.setFont(VentanaPrincipal.FUENTE_LOGO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 1;
		panel.add(labelTitulo, gbc);

		JLabel labelEslogan = new JLabel("Tu tienda friki de confianza");
		labelEslogan.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEslogan.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
		labelEslogan.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 2;
		panel.add(labelEslogan, gbc);

		JSeparator separador = new JSeparator();
		separador.setForeground(VentanaPrincipal.COLOR_ACENTO);
		separador.setPreferredSize(new Dimension(VentanaPrincipal.escalar(220), VentanaPrincipal.escalar(2)));

		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30));
		panel.add(separador, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;

		String[] lineas = { "Comics y Manga", "Juegos de Mesa", "Figuras de Colección", "Segunda Mano e Intercambios" };

		for (int i = 0; i < lineas.length; i++) {
			JLabel label = new JLabel(lineas[i]);
			label.setFont(VentanaPrincipal.FUENTE_NORMAL);
			label.setForeground(new Color(210, 210, 210));

			gbc.gridy = 4 + i;
			gbc.insets = new Insets(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(60),
					VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20));

			panel.add(label, gbc);
		}

		return panel;
	}

	private JLabel crearLogo() {
		java.net.URL urlImagen = getClass().getResource("/fotos/logo.png");

		if (urlImagen != null) {
			ImageIcon icono = new ImageIcon(urlImagen);
			Image imagen = icono.getImage().getScaledInstance(VentanaPrincipal.escalar(440),
					VentanaPrincipal.escalar(270), Image.SCALE_SMOOTH);

			return new JLabel(new ImageIcon(imagen));
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
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		panelDerecho.add(panelFormularios, gbc);

		return panelDerecho;
	}

	private JPanel crearFormularioLogin() {
		JPanel panel = crearPanelFormulario();

		GridBagConstraints gbc = crearGbcFormulario();

		JLabel titulo = crearTitulo("Iniciar Sesión");
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
		panel.add(titulo, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		gbc.gridy = 1;
		panel.add(crearEtiqueta("Tipo de usuario"), gbc);

		comboTipoUsuario = new JComboBox<>(new String[] { "Cliente", "Empleado", "Gestor" });
		estilizarCombo(comboTipoUsuario);
		gbc.gridy = 2;
		panel.add(comboTipoUsuario, gbc);

		gbc.gridy = 3;
		panel.add(crearEtiqueta("Nickname"), gbc);

		campoNickname = crearCampoTexto();
		gbc.gridy = 4;
		panel.add(campoNickname, gbc);

		gbc.gridy = 5;
		panel.add(crearEtiqueta("Contraseña"), gbc);

		campoPassword = crearCampoPassword();
		gbc.gridy = 6;
		panel.add(campoPassword, gbc);

		JButton botonLogin = crearBoton("Iniciar Sesión", true);
		botonLogin.addActionListener(e -> hacerLogin());

		campoPassword.addActionListener(e -> hacerLogin());
		campoNickname.addActionListener(e -> campoPassword.requestFocus());

		gbc.gridy = 7;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, VentanaPrincipal.escalar(8), 0);
		panel.add(botonLogin, gbc);

		gbc.gridy = 8;
		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8), 0);
		panel.add(new JSeparator(), gbc);

		JButton botonRegistro = crearBoton("¿No tienes cuenta? Regístrate", false);
		botonRegistro.addActionListener(e -> controlador.irARegistro());

		gbc.gridy = 9;
		panel.add(botonRegistro, gbc);

		JButton botonInvitado = crearBoton("Continuar como invitado", true);
		botonInvitado.addActionListener(e -> controlador.continuarComoInvitado());

		gbc.gridy = 10;
		panel.add(botonInvitado, gbc);

		return panel;
	}

	private JPanel crearFormularioRegistro() {
		JPanel panel = crearPanelFormulario();

		GridBagConstraints gbc = crearGbcFormulario();

		JLabel titulo = crearTitulo("Crear Cuenta");
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
		panel.add(titulo, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		gbc.gridy = 1;
		panel.add(crearEtiqueta("Tipo de cuenta"), gbc);

		comboTipoRegistro = new JComboBox<>(new String[] { "Cliente" });
		estilizarCombo(comboTipoRegistro);
		comboTipoRegistro.setEnabled(false);
		gbc.gridy = 2;
		panel.add(comboTipoRegistro, gbc);

		gbc.gridy = 3;
		panel.add(crearEtiqueta("Nickname"), gbc);

		campoNicknameReg = crearCampoTexto();
		gbc.gridy = 4;
		panel.add(campoNicknameReg, gbc);

		gbc.gridy = 5;
		panel.add(crearEtiqueta("Contraseña"), gbc);

		campoPasswordReg = crearCampoPassword();
		gbc.gridy = 6;
		panel.add(campoPasswordReg, gbc);

		JLabel infoPassword = new JLabel("<html><small>La contraseña debe tener mayúsculas, minúsculas,"
				+ "<br>números y caracteres especiales.</small></html>");
		infoPassword.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		infoPassword.setForeground(VentanaPrincipal.COLOR_TEXTO2);

		gbc.gridy = 7;
		gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(16), 0);
		panel.add(infoPassword, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		gbc.gridy = 8;
		panel.add(crearEtiqueta("DNI"), gbc);

		campoDNI = crearCampoTexto();
		gbc.gridy = 9;
		panel.add(campoDNI, gbc);

		JButton botonRegistrar = crearBoton("Crear Cuenta", true);
		botonRegistrar.addActionListener(e -> hacerRegistro());

		campoDNI.addActionListener(e -> hacerRegistro());
		campoPasswordReg.addActionListener(e -> campoDNI.requestFocus());
		campoNicknameReg.addActionListener(e -> campoPasswordReg.requestFocus());

		gbc.gridy = 10;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, VentanaPrincipal.escalar(8), 0);
		panel.add(botonRegistrar, gbc);

		gbc.gridy = 11;
		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8), 0);
		panel.add(new JSeparator(), gbc);

		JButton botonVolver = crearBoton("¿Ya tienes cuenta? Inicia sesión", false);
		botonVolver.addActionListener(e -> controlador.irALogin());

		gbc.gridy = 12;
		panel.add(botonVolver, gbc);

		return panel;
	}

	private JPanel crearPanelFormulario() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60),
				VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(60)));

		return panel;
	}

	private GridBagConstraints crearGbcFormulario() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);
		return gbc;
	}

	private void hacerLogin() {
		String nickname = campoNickname.getText().trim();
		String password = new String(campoPassword.getPassword());
		String tipo = String.valueOf(comboTipoUsuario.getSelectedItem());

		controlador.realizarLogin(nickname, password, tipo);
	}

	private void hacerRegistro() {
		String nickname = campoNicknameReg.getText().trim();
		String password = new String(campoPasswordReg.getPassword());
		String dni = campoDNI.getText().trim().toUpperCase();

		controlador.realizarRegistro(nickname, password, dni, "Cliente");
	}

	private void actualizarCampoDNI() {
		if (campoDNI != null) {
			campoDNI.setEnabled(true);
		}
	}

	public void mostrarRegistro() {
		cardFormularios.show(panelFormularios, "REGISTRO");

		campoNicknameReg.setText("");
		campoPasswordReg.setText("");
		campoDNI.setText("");
		actualizarCampoDNI();
	}

	public void mostrarLogin() {
		cardFormularios.show(panelFormularios, "LOGIN");

		campoNickname.setText("");
		campoPassword.setText("");
	}

	public void limpiar() {
		mostrarLogin();

		if (comboTipoUsuario != null) {
			comboTipoUsuario.setSelectedIndex(0);
		}
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void mostrarExito(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
	}

	private JLabel crearTitulo(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_TITULO);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO);
		return label;
	}

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
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
						VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
		return campo;
	}

	private JPasswordField crearCampoPassword() {
		JPasswordField campo = new JPasswordField();
		campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campo.setForeground(Color.BLACK);
		campo.setBackground(Color.WHITE);
		campo.setCaretColor(Color.BLACK);
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
						VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
		return campo;
	}

	private JButton crearBoton(String texto, boolean principal) {
		JButton boton = new JButton(texto);

		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorderPainted(false);
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));

		if (principal) {
			boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
			boton.setForeground(Color.WHITE);
			boton.setOpaque(true);

			boton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					boton.setBackground(VentanaPrincipal.COLOR_ACENTO2);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
				}
			});
		} else {
			boton.setBackground(VentanaPrincipal.COLOR_PANEL);
			boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
			boton.setOpaque(true);
		}

		return boton;
	}

	private void estilizarCombo(JComboBox<String> combo) {
		combo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		combo.setForeground(Color.BLACK);
		combo.setBackground(Color.WHITE);
	}
}