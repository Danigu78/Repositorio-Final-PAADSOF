package Gui.Gestor;

import Gui.PanelBaseInterfaz;
import Gui.VentanaPrincipal;
import usuarios.Gestor;
import javax.swing.*;
import java.awt.*;

/**
 * Clase base para las secciones del panel del gestor.
 * 
 * @author Antonino
 * @version 1.0
 */
public abstract class AbstractPanelGestor extends PanelBaseInterfaz {

	/** Serial UID de la clase. */
	private static final long serialVersionUID = 1L;

	/** Gestor autenticado asociado al panel. */
	protected final Gestor gestor;

	/**
	 * Constructor del panel base del gestor.
	 *
	 * @param ventana Ventana principal
	 * @param gestor  Gestor autenticado
	 */
	protected AbstractPanelGestor(VentanaPrincipal ventana, Gestor gestor) {
		super(ventana);
		this.gestor = gestor;
	}

	/**
	 * Crea un campo de texto estilizado con un número de columnas determinado.
	 *
	 * @param columnas Número de columnas del campo
	 * @return Campo de texto configurado
	 */
	protected JTextField crearCampoColumnas(int columnas) {
		JTextField campo = new JTextField(columnas);
		campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campo.setForeground(Color.BLACK);
		campo.setBackground(Color.WHITE);
		campo.setCaretColor(Color.BLACK);
		campo.setBorder(javax.swing.BorderFactory
				.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						javax.swing.BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(6),
								VentanaPrincipal.escalar(8))));
		return campo;
	}

	/**
	 * Crea un campo de contraseña estilizado para el gestor.
	 *
	 * @return Campo de contraseña configurado
	 */
	protected JPasswordField crearCampoPasswordGestor() {
		JPasswordField campo = new JPasswordField(15);
		campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campo.setForeground(Color.BLACK);
		campo.setBackground(Color.WHITE);
		campo.setCaretColor(Color.BLACK);
		campo.setBorder(javax.swing.BorderFactory
				.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						javax.swing.BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(6),
								VentanaPrincipal.escalar(8))));
		return campo;
	}

	/**
	 * Muestra un mensaje de error en pantalla.
	 *
	 * @param msg Mensaje de error
	 */
	@Override
	public void mostrarError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un mensaje de éxito en pantalla.
	 *
	 * @param msg Mensaje de éxito
	 */
	public void mostrarExito(String msg) {
		mostrarMensaje(msg);
	}
}