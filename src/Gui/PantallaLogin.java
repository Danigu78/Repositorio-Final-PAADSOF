package Gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;

/**
 * Pantalla de inicio de sesión y registro de la aplicación CheckPoint.
 * 
 * <p>
 * Permite a los usuarios:
 * </p>
 * <ul>
 * <li>Iniciar sesión como cliente, empleado o gestor</li>
 * <li>Registrarse como nuevo cliente</li>
 * </ul>
 * 
 * <p>
 * El diseño usa un panel dividido en dos mitades: izquierda con el logo y
 * derecha con el formulario.
 * </p>
 * 
 * @author CheckPoint
 * @version 1.0
 */
public class PantallaLogin extends JPanel {

	// ── Atributos ─────────────────────────────────────────────────────────────

	/** Referencia a la ventana principal para navegar entre pantallas */
	private VentanaPrincipal ventana;

	/** Instancia de la tienda para hacer login */
	private Tienda tienda;

	// ── Componentes del formulario de login ───────────────────────────────────

	/** Campo de texto para el nickname */
	private JTextField campoNickname;

	/** Campo de contraseña */
	private JPasswordField campoPassword;

	/** Selector del tipo de usuario */
	private JComboBox<String> comboTipoUsuario;

	/** Botón de login */
	private JButton botonLogin;

	/** Botón para ir al formulario de registro */
	private JButton botonIrRegistro;

	// ── Componentes del formulario de registro ────────────────────────────────

	/** Panel que contiene el formulario de registro */
	private JPanel panelRegistro;

	/** Panel que contiene el formulario de login */
	private JPanel panelLogin;

	/** Campo nickname para registro */
	private JTextField campoNicknameReg;

	/** Campo password para registro */
	private JPasswordField campoPasswordReg;

	/** Campo DNI para registro */
	private JTextField campoDNI;

	/** Botón para registrarse */
	private JButton botonRegistrar;

	/** Botón para volver al login */
	private JButton botonVolverLogin;

	/** Panel que alterna entre login y registro (CardLayout) */
	private JPanel panelFormularios;

	/** CardLayout para alternar entre login y registro */
	private CardLayout cardFormularios;

	/**
	 * Constructor de la pantalla de login.
	 * 
	 * @param ventana La ventana principal de la aplicación
	 */
	public PantallaLogin(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.tienda = Tienda.getInstancia();
		inicializarUI();
	}

	/**
	 * Construye y organiza todos los componentes de la pantalla. La pantalla se
	 * divide en dos paneles: izquierdo (logo) y derecho (formulario).
	 */
	private void inicializarUI() {
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		// Panel izquierdo con el logo
		JPanel panelIzquierdo = crearPanelLogo();

		// Panel derecho con los formularios
		JPanel panelDerecho = crearPanelFormularios();

		// Dividir la pantalla en dos mitades
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
		splitPane.setDividerLocation(450);// Posicion donde dividimos
		splitPane.setDividerSize(0);// Liena de separacion entre las dos pantallas
		splitPane.setBorder(null);
		splitPane.setEnabled(false); // No redimensionable

		add(splitPane, BorderLayout.CENTER);
	}

	// PANEL DE LA IZQUIERDA
	/**
	 * Crea el panel izquierdo con el logo y descripción de la tienda.
	 * 
	 * @return El panel izquierdo configurado
	 */
	private JPanel crearPanelLogo() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(60, 30, 5)); // color naranja oscuro sólido
		panel.setPreferredSize(new Dimension(450, VentanaPrincipal.ALTO));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;// columna unica
		gbc.insets = new Insets(15, 30, 15, 30);// separacion entre los objetos
		gbc.anchor = GridBagConstraints.CENTER;

		java.net.URL urlImagen = getClass().getResource("/fotos/logo.png");// busca la ruta
		JLabel labelImagen;

		if (urlImagen != null) {
			// Creamos el icono y lo escalamos para que no sea gigante
			ImageIcon iconoOriginal = new ImageIcon(urlImagen);
			Image imgEscalada = iconoOriginal.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
			labelImagen = new JLabel(new ImageIcon(imgEscalada));
		} else {
			// Por si la imagen no carga, ponemos un texto de error para que no explote
			labelImagen = new JLabel("Imagen no encontrada");
		}

		gbc.gridy = 0;// añadimos en la fila 0
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(labelImagen, gbc);

		// Nombre de la tienda
		JLabel labelTitulo = new JLabel("CheckPoint");
		labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 42));
		labelTitulo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 1;
		panel.add(labelTitulo, gbc);

		// Eslogan
		JLabel labelEslogan = new JLabel("Tu tienda friki de confianza");
		labelEslogan.setFont(new Font("Segoe UI", Font.ITALIC, 16));
		labelEslogan.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelEslogan.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 2;
		panel.add(labelEslogan, gbc);

		// Separador decorativo
		JSeparator sep = new JSeparator();
		sep.setForeground(VentanaPrincipal.COLOR_ACENTO);
		sep.setPreferredSize(new Dimension(200, 2));
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(sep, gbc);
		gbc.fill = GridBagConstraints.NONE;

		// Descripción
		// Definimos los nombres de los archivos de imagen que deberías tener en
		// /resources/
		// Definimos solo los textos (sin emojis ni iconos)
		String[] lineas = { "Comics y Manga", "Juegos de Mesa", "Figuras de Colección", "Segunda Mano e Intercambios" };

		for (int i = 0; i < lineas.length; i++) {
			JLabel label = new JLabel(lineas[i]);
			label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
			label.setForeground(VentanaPrincipal.COLOR_TEXTO2);

			// El gridy sigue siendo dinámico para que no se solapen (Tema 4.3) [cite: 955,
			// 968]
			gbc.gridy = 4 + i;
			gbc.gridx = 0; // Columna única

			// Ajustamos los márgenes (Insets) para que el texto esté centrado visualmente
			// Insets(arriba, izquierda, abajo, derecha)
			gbc.insets = new Insets(10, 60, 10, 10);

			// Forzamos la alineación al Oeste (izquierda)
			gbc.anchor = GridBagConstraints.WEST;

			panel.add(label, gbc); // Añadimos el componente al contenedor [cite: 2079]
		}
		return panel;
	}

	/**
	 * Crea el panel derecho con los formularios de login y registro. Usa un
	 * CardLayout para alternar entre ambos formularios.
	 * 
	 * @return El panel derecho configurado
	 */
	private JPanel crearPanelFormularios() {
		JPanel panelDerecho = new JPanel(new GridBagLayout());
		panelDerecho.setBackground(VentanaPrincipal.COLOR_PANEL);

		// Crear formularios
		panelLogin = crearFormularioLogin();
		panelRegistro = crearFormularioRegistro();

		// CardLayout para alternar entre login y registro
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
	 * Crea el formulario de inicio de sesión con campos de nickname, contraseña y
	 * selector de tipo de usuario.
	 * 
	 * @return El panel con el formulario de login
	 */
	private JPanel crearFormularioLogin() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(8, 0, 8, 0);

		// Título
		JLabel labelTitulo = new JLabel("Iniciar Sesión");
		labelTitulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 30, 0);
		panel.add(labelTitulo, gbc);

		// Tipo de usuario
		gbc.insets = new Insets(8, 0, 4, 0);
		JLabel labelTipo = crearEtiqueta("Tipo de usuario");
		gbc.gridy = 1;
		panel.add(labelTipo, gbc);

		comboTipoUsuario = new JComboBox<>(new String[] { "Cliente", "Empleado", "Gestor" });
		estilizarCombo(comboTipoUsuario);
		gbc.gridy = 2;
		panel.add(comboTipoUsuario, gbc);

		// Nickname
		JLabel labelNick = crearEtiqueta("Nickname");
		gbc.gridy = 3;
		panel.add(labelNick, gbc);

		campoNickname = crearCampoTexto("Introduce tu nickname");
		gbc.gridy = 4;
		panel.add(campoNickname, gbc);

		// Contraseña
		JLabel labelPass = crearEtiqueta("Contraseña");
		gbc.gridy = 5;
		panel.add(labelPass, gbc);

		campoPassword = crearCampoPassword("Introduce tu contraseña");
		gbc.gridy = 6;
		panel.add(campoPassword, gbc);

		// Espacio
		gbc.gridy = 7;
		gbc.insets = new Insets(20, 0, 8, 0);
		panel.add(Box.createVerticalStrut(10), gbc);

		// Botón login
		botonLogin = crearBoton("Iniciar Sesión", true);
		botonLogin.addActionListener(e -> realizarLogin());
		gbc.gridy = 8;
		gbc.insets = new Insets(8, 0, 8, 0);
		panel.add(botonLogin, gbc);

		// Separador
		gbc.gridy = 9;
		panel.add(new JSeparator(), gbc);

		// Botón ir a registro
		botonIrRegistro = crearBoton("¿No tienes cuenta? Regístrate", false);
		botonIrRegistro.addActionListener(e -> mostrarRegistro());
		gbc.gridy = 10;
		panel.add(botonIrRegistro, gbc);

		// Añadir acción Enter en los campos
		campoPassword.addActionListener(e -> realizarLogin());
		campoNickname.addActionListener(e -> campoPassword.requestFocus());

		return panel;
	}

	/**
	 * Crea el formulario de registro de nuevo cliente con campos de nickname,
	 * contraseña y DNI.
	 * 
	 * @return El panel con el formulario de registro
	 */
	private JPanel crearFormularioRegistro() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(8, 0, 4, 0);

		// Título
		JLabel labelTitulo = new JLabel("Crear Cuenta");
		labelTitulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 30, 0);
		panel.add(labelTitulo, gbc);

		// Nickname
		gbc.insets = new Insets(8, 0, 4, 0);
		JLabel labelNick = crearEtiqueta("Nickname");
		gbc.gridy = 1;
		panel.add(labelNick, gbc);

		campoNicknameReg = crearCampoTexto("Elige un nickname único");
		gbc.gridy = 2;
		panel.add(campoNicknameReg, gbc);

		// Contraseña
		JLabel labelPass = crearEtiqueta("Contraseña");
		gbc.gridy = 3;
		panel.add(labelPass, gbc);

		campoPasswordReg = crearCampoPassword("Mínimo 8 caracteres, mayúsculas y números");
		gbc.gridy = 4;
		panel.add(campoPasswordReg, gbc);

		// DNI
		JLabel labelDNI = crearEtiqueta("DNI");
		gbc.gridy = 5;
		panel.add(labelDNI, gbc);

		campoDNI = crearCampoTexto("Ej: 12345678A");
		gbc.gridy = 6;
		panel.add(campoDNI, gbc);

		// Info sobre contraseña segura
		JLabel labelInfo = new JLabel(
				"<html><small>La contraseña debe tener mayúsculas, minúsculas,<br>números y caracteres especiales.</small></html>");
		labelInfo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelInfo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 7;
		gbc.insets = new Insets(4, 0, 16, 0);
		panel.add(labelInfo, gbc);

		// Botón registrar
		botonRegistrar = crearBoton("Crear Cuenta", true);
		botonRegistrar.addActionListener(e -> realizarRegistro());
		gbc.gridy = 8;
		gbc.insets = new Insets(8, 0, 8, 0);
		panel.add(botonRegistrar, gbc);

		// Separador
		gbc.gridy = 9;
		panel.add(new JSeparator(), gbc);

		// Botón volver al login
		botonVolverLogin = crearBoton("¿Ya tienes cuenta? Inicia sesión", false);
		botonVolverLogin.addActionListener(e -> mostrarLogin());
		gbc.gridy = 10;
		panel.add(botonVolverLogin, gbc);

		return panel;
	}

	// ── Acciones ──────────────────────────────────────────────────────────────

	/**
	 * Realiza el proceso de login según el tipo de usuario seleccionado. Si el
	 * login es exitoso, navega al panel correspondiente. Si falla, muestra un
	 * mensaje de error.
	 */
	private void realizarLogin() {
		String nickname = campoNickname.getText().trim();
		String password = new String(campoPassword.getPassword());
		String tipo = (String) comboTipoUsuario.getSelectedItem();

		// Validar que los campos no estén vacíos
		if (nickname.isEmpty() || password.isEmpty()) {
			mostrarError("Por favor, rellena todos los campos.");
			return;
		}

		// Intentar login según el tipo de usuario
		try {
			if ("Cliente".equals(tipo)) {
				Cliente cliente = tienda.loginCliente(nickname, password);
				if (cliente != null) {
					ventana.loginCliente(cliente);
				} else {
					mostrarError("Nickname o contraseña incorrectos.");
				}
			} else if ("Empleado".equals(tipo)) {
				Empleado empleado = tienda.loginEmpleado(nickname, password);
				if (empleado != null) {
					ventana.loginEmpleado(empleado);
				} else {
					mostrarError("Nickname o contraseña incorrectos, o empleado despedido.");
				}
			} else if ("Gestor".equals(tipo)) {
				Gestor gestor = tienda.loginGestor(nickname, password);
				if (gestor != null) {
					ventana.loginGestor(gestor);
				} else {
					mostrarError("Credenciales de gestor incorrectas.");
				}
			}
		} catch (Exception ex) {
			mostrarError("Error al iniciar sesión: " + ex.getMessage());
		}
	}

	/**
	 * Realiza el proceso de registro de un nuevo cliente. Si el registro es
	 * exitoso, hace login automáticamente. Si falla, muestra un mensaje de error
	 * descriptivo.
	 */
	private void realizarRegistro() {
		String nickname = campoNicknameReg.getText().trim();
		String password = new String(campoPasswordReg.getPassword());
		String dni = campoDNI.getText().trim().toUpperCase();

		// Validar campos vacíos
		if (nickname.isEmpty() || password.isEmpty() || dni.isEmpty()) {
			mostrarError("Por favor, rellena todos los campos.");
			return;
		}

		// Intentar registrar
		try {
			Cliente nuevo = tienda.registrarNuevoCliente(nickname, password, dni);
			if (nuevo != null) {
				// Registro exitoso — hacer login automático
				Cliente logueado = tienda.loginCliente(nickname, password);
				if (logueado != null) {
					mostrarExito("¡Bienvenido a CheckPoint, " + nickname + "!");
					ventana.loginCliente(logueado);
				}
			} else {
				mostrarError("No se pudo crear la cuenta. Comprueba que:\n" + "- El nickname no esté en uso\n"
						+ "- El DNI no esté registrado\n"
						+ "- La contraseña sea segura (mayúsculas, minúsculas, números y especiales)");
			}
		} catch (Exception ex) {
			mostrarError("Error al registrarse: " + ex.getMessage());
		}
	}

	/**
	 * Muestra el formulario de registro ocultando el de login.
	 */
	private void mostrarRegistro() {
		cardFormularios.show(panelFormularios, "REGISTRO");
		limpiarCamposRegistro();
	}

	/**
	 * Muestra el formulario de login ocultando el de registro.
	 */
	private void mostrarLogin() {
		cardFormularios.show(panelFormularios, "LOGIN");
		limpiarCampos();
	}

	// ── Métodos públicos ──────────────────────────────────────────────────────

	/**
	 * Limpia todos los campos del formulario de login. Se llama cuando el usuario
	 * hace logout.
	 */
	public void limpiar() {
		limpiarCampos();
		mostrarLogin();
	}

	// ── Métodos auxiliares de UI ──────────────────────────────────────────────

	/**
	 * Limpia los campos del formulario de login.
	 */
	private void limpiarCampos() {
		campoNickname.setText("");
		campoPassword.setText("");
		campoTipoUsuario = null; // reset
		if (comboTipoUsuario != null)
			comboTipoUsuario.setSelectedIndex(0);
	}

	/**
	 * Limpia los campos del formulario de registro.
	 */
	private void limpiarCamposRegistro() {
		campoNicknameReg.setText("");
		campoPasswordReg.setText("");
		campoDNI.setText("");
	}

	/**
	 * Muestra un diálogo de error con el mensaje indicado.
	 * 
	 * @param mensaje El mensaje de error a mostrar
	 */
	private void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un diálogo de éxito con el mensaje indicado.
	 * 
	 * @param mensaje El mensaje de éxito a mostrar
	 */
	private void mostrarExito(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "¡Éxito!", JOptionPane.INFORMATION_MESSAGE);
	}

	// ── Métodos de estilo ─────────────────────────────────────────────────────

	/**
	 * Crea una etiqueta de formulario con el estilo de CheckPoint.
	 * 
	 * @param texto El texto de la etiqueta
	 * @return La etiqueta estilizada
	 */
	private JLabel crearEtiqueta(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_NORMAL);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		return label;
	}

	/**
	 * Crea un campo de texto estilizado con texto de placeholder.
	 * 
	 * @param placeholder Texto de ayuda que se muestra cuando el campo está vacío
	 * @return El campo de texto estilizado
	 */
	private JTextField crearCampoTexto(String placeholder) {
		JTextField campo = new JTextField();
		campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campo.setForeground(Color.BLACK);
		campo.setBackground(Color.WHITE);
		campo.setCaretColor(Color.BLACK);
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(10, 12, 10, 12)));
		campo.setPreferredSize(new Dimension(0, 42));
		return campo;
	}

	/**
	 * Crea un campo de contraseña estilizado.
	 * 
	 * @param placeholder Texto de ayuda (no mostrado en campos de password)
	 * @return El campo de contraseña estilizado
	 */
	private JPasswordField crearCampoPassword(String placeholder) {
		JPasswordField campo = new JPasswordField();
		campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campo.setForeground(Color.BLACK);
		campo.setBackground(VentanaPrincipal.COLOR_TARJETA);
		campo.setCaretColor(Color.black);
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(10, 12, 10, 12)));
		campo.setPreferredSize(new Dimension(0, 42));
		return campo;
	}

	/**
	 * Crea un botón estilizado con dos modos: principal (relleno) y secundario
	 * (borde).
	 * 
	 * @param texto     El texto del botón
	 * @param principal Si true, usa el estilo principal (naranja relleno); si
	 *                  false, usa el estilo secundario (borde)
	 * @return El botón estilizado
	 */
	private JButton crearBoton(String texto, boolean principal) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_NORMAL);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setPreferredSize(new Dimension(0, 44));

		if (principal) {
			// Estilo principal: naranja relleno
			boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
			boton.setForeground(Color.WHITE);
			boton.setOpaque(true);
			boton.setBorderPainted(false);
			boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			// Efecto hover
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
			// Estilo secundario: texto naranja sin relleno
			boton.setBackground(VentanaPrincipal.COLOR_PANEL);
			boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
			boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		}

		return boton;
	}

	/**
	 * Aplica el estilo de CheckPoint a un JComboBox.
	 * 
	 * @param combo El JComboBox a estilizar
	 */
	private void estilizarCombo(JComboBox<String> combo) {
		combo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		combo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		combo.setBackground(VentanaPrincipal.COLOR_TARJETA);
		combo.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE));
		combo.setPreferredSize(new Dimension(0, 42));
	}

	// campo tipo usuario (para el limpiar)
	private JComboBox<String> campoTipoUsuario = null;
}
