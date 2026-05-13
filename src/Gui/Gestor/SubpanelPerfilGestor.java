package Gui.Gestor;

import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorConfiguracionGestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Gestor;

/**
 * Subpanel de perfil del gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelPerfilGestor extends AbstractPanelGestor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Controlador asociado al subpanel.
	 * Gestiona la lógica de actualización del perfil del gestor.
	 */
	private ControladorConfiguracionGestor controlador;

	/**
	 * Campo de texto para introducir el nuevo nickname del gestor.
	 */
	private JTextField campoNick;
	
	/**
	 * Campo de contraseña para introducir la nueva contraseña del gestor.
	 */
	private JPasswordField campoPass;
	
	/**
	 * Etiqueta que muestra el nickname actual del gestor.
	 */
	private JLabel labelNickActual;

	/**
	 * Botón que guarda los cambios realizados en el perfil.
	 */
	private JButton botonGuardar;

	/**
	 * Constructor del subpanel de perfil del gestor.
	 *
	 * @param ventana ventana principal del sistema
	 * @param gestor  usuario gestor loggeado
	 */
	public SubpanelPerfilGestor(VentanaPrincipal ventana, Gestor gestor) {
		super(ventana, gestor);
		this.controlador = new ControladorConfiguracionGestor(this, gestor);
		inicializarUI();
	}

	/**
	 * Inicializa la interfaz gráfica del subpanel de perfil.
	 */
	private void inicializarUI() {
		JPanel panelContenido = new JPanel(new GridBagLayout());
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(50), VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		JLabel titulo = new JLabel("Mi Perfil");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
		panelContenido.add(titulo, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		// crearLabel() de PanelBaseInterfaz
		labelNickActual = crearLabel("Nickname actual: " + controlador.getNickname());
		gbc.gridy = 1;
		panelContenido.add(labelNickActual, gbc);

		gbc.gridy = 2;
		panelContenido.add(crearLabel("Nuevo nickname:"), gbc);
		// crearCampo() de PanelBaseInterfaz
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
				"<html><small>Mínimo 8 caracteres, mayúsculas, minúsculas," + " números y especiales.</small></html>");
		gbc.gridy = 6;
		panelContenido.add(labelInfo, gbc);

		// crearBotonNaranja() de PanelBaseInterfaz
		botonGuardar = crearBotonNaranja("Guardar cambios");
		botonGuardar.setActionCommand("guardarPerfil");
		gbc.gridy = 7;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
		panelContenido.add(botonGuardar, gbc);

		add(panelContenido, BorderLayout.CENTER);
		setControlador(controlador);
	}

	/**
	 * Asigna el controlador como listener del botón de guardado,
	 * 
	 * @param c controlador que manejará las acciones del panel
	 */
	public void setControlador(ActionListener c) {
		if (botonGuardar != null) {
			for (ActionListener al : botonGuardar.getActionListeners())
				botonGuardar.removeActionListener(al);
			botonGuardar.addActionListener(c);
		}
	}

	/**
	 * Procesa la actualización del perfil leyendo los campos de la interfaz.
	 */
	public void procesarGuardar() {
		String nuevoNick = campoNick.getText().trim();
		String nuevaPass = new String(campoPass.getPassword());

		if (controlador.modificarPerfil(nuevoNick, nuevaPass)) {
			mostrarMensaje("Perfil actualizado correctamente.");
			labelNickActual.setText("Nickname actual: " + nuevoNick);
		} else {
			mostrarError("No se pudo actualizar el perfil. Comprueba los datos.");
		}
	}

	/**
	 * Muestra un mensaje de error 
	 *
	 * @param msg mensaje de error a mostrar
	 */
	@Override
	public void mostrarError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
}