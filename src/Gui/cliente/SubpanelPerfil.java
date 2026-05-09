package Gui.cliente;

import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorPerfil;

import java.awt.*;
import java.awt.event.*;
import usuarios.Cliente;

/**
 * Subpanel del perfil del cliente. Permite cambiar nickname y contraseña.
 * Extiende AbstractPanelCliente para reutilizar helpers visuales del cliente.
 * Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPerfil extends AbstractPanelCliente {

	/** Controlador del subpanel. */
	private ControladorPerfil controlador;

	/** Referencia al panel cliente para actualizar el nombre en la barra. */
	private PanelCliente panelCliente;

	/** Campo de nuevo nickname. */
	private JTextField campoNickname;

	/** Campo de nueva contraseña. */
	private JPasswordField campoPassword;

	/** Label con el nickname actual. */
	private JLabel labelNicknameActual;

	/** Botón guardar — atributo para registrar el controlador. */
	private JButton botonGuardar;

	/**
	 * Constructor del subpanel de perfil.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelPerfil(VentanaPrincipal ventana) {
		super(ventana);
	}

	/**
	 * Actualiza el subpanel con los datos del cliente. Crea el controlador y lo
	 * registra en el botón.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorPerfil(this, cliente);
		removeAll();
		add(crearPanelPerfil(), BorderLayout.CENTER);
		setControlador(controlador);
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en el botón guardar — patrón de los apuntes.
	 *
	 * @param c El ActionListener a registrar
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
	 *
	 * @return Panel del perfil
	 */
	private JPanel crearPanelPerfil() {
		JPanel panelCentral = new JPanel(new GridBagLayout());
		panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40),
						VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40))));
		panel.setPreferredSize(new Dimension(VentanaPrincipal.escalar(450), VentanaPrincipal.escalar(400)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.insets = new Insets(VentanaPrincipal.escalar(6), 0, VentanaPrincipal.escalar(6), 0);

		JLabel titulo = new JLabel("Mi Perfil");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
		panel.add(titulo, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

		// crearLabel() de AbstractPanelSection
		labelNicknameActual = crearLabel("Nickname actual: " + cliente.getNickname());
		gbc.gridy = 1;
		panel.add(labelNicknameActual, gbc);

		gbc.gridy = 2;
		panel.add(new JSeparator(), gbc);

		gbc.gridy = 3;
		// crearLabel() de AbstractPanelSection — sustituye crearEtiqueta()
		panel.add(crearLabel("Nuevo nickname (déjalo vacío para no cambiar):"), gbc);

		// crearCampo() de AbstractPanelSection — sustituye crearCampo() propio
		campoNickname = crearCampo();
		gbc.gridy = 4;
		panel.add(campoNickname, gbc);

		gbc.gridy = 5;
		panel.add(crearLabel("Nueva contraseña:"), gbc);

		campoPassword = new JPasswordField();
		campoPassword.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campoPassword.setForeground(Color.BLACK);
		campoPassword.setBackground(Color.WHITE);
		campoPassword.setCaretColor(Color.BLACK);
		campoPassword.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
		gbc.gridy = 6;
		panel.add(campoPassword, gbc);

		JLabel labelInfo = new JLabel(
				"<html><small>Debe tener mayúsculas, minúsculas, " + "números y caracteres especiales.</small></html>");
		labelInfo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelInfo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 7;
		panel.add(labelInfo, gbc);

		// crearBotonNaranja() de AbstractPanelSection — sustituye el botón manual
		botonGuardar = crearBotonNaranja("Guardar cambios");
		botonGuardar.setActionCommand("guardar");
		botonGuardar.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20)));
		gbc.gridy = 8;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
		panel.add(botonGuardar, gbc);

		panelCentral.add(panel);
		return panelCentral;
	}

	/**
	 * Devuelve el nuevo nickname introducido. Lo lee el controlador desde
	 * guardarCambios().
	 *
	 * @return Nuevo nickname o vacío si no se quiere cambiar
	 */
	public String getNuevoNickname() {
		return campoNickname.getText().trim();
	}

	/**
	 * Devuelve la nueva contraseña introducida. Lo lee el controlador desde
	 * guardarCambios().
	 *
	 * @return Nueva contraseña
	 */
	public String getNuevaPassword() {
		return new String(campoPassword.getPassword()).trim();
	}

	/**
	 * Actualiza el nombre de usuario en la barra del panel cliente. Lo llama el
	 * controlador tras guardar correctamente.
	 */
	public void actualizarNombreEnBarra() {
		if (panelCliente != null)
			panelCliente.actualizarNombreUsuario(cliente.getNickname());
	}

	/**
	 * Enlaza el panel cliente para poder actualizar la barra.
	 *
	 * @param panelCliente El panel cliente
	 */
	public void setPanelCliente(PanelCliente panelCliente) {
		this.panelCliente = panelCliente;
	}

	/**
	 * Muestra un mensaje de éxito. Lo llama el controlador.
	 *
	 * @param mensaje El mensaje de éxito
	 */
	public void mostrarExito(String mensaje) {
		mostrarMensaje(mensaje);
	}
}