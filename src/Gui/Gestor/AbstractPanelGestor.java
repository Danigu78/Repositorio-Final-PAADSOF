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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Gestor logueado actualmente. */
	protected final Gestor gestor;

	/**
	 * Constructor de la clase base de paneles del gestor.
	 *
	 * @param ventana Ventana principal
	 * @param gestor  Gestor logueado
	 */
	protected AbstractPanelGestor(VentanaPrincipal ventana, Gestor gestor) {
		super(ventana);
		this.gestor = gestor;
	}

	/**
	 * Crea un campo de texto estilizado con el número de columnas indicado.
	 *
	 * @param columnas Número de columnas del campo
	 * @return JTextField configurado
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
	 * Crea un campo de contraseña estilizado
	 *
	 * @return JPasswordField configurado
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
	 * Muestra un diálogo de error estándar.
	 *
	 * @param msg Mensaje de error
	 */
	@Override
	public void mostrarError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un diálogo de éxito
	 *
	 * @param msg Mensaje de éxito
	 */
	public void mostrarExito(String msg) {
		mostrarMensaje(msg);
	}
}