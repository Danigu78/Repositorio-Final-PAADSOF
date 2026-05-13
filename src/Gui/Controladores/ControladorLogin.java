package Gui.Controladores;

import Gui.PantallaLogin;
import Gui.VentanaPrincipal;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de la pantalla de login y registro.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorLogin implements ActionListener {
	/** Ventana principal de la aplicación. */
	private VentanaPrincipal ventana;
	/** Vista de login y registro. */
	private PantallaLogin vista;
	/** Instancia principal de la tienda. */
	private Tienda tienda;

	/**
	 * Construye el controlador de login.
	 *
	 * @param ventana ventana principal
	 * @param vista   pantalla de login
	 * @return void
	 */
	public ControladorLogin(VentanaPrincipal ventana, PantallaLogin vista) {
		this.ventana = ventana;
		this.vista = vista;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos de la vista.
	 *
	 * @param e evento de acción
	 * @return void
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if ("login".equals(cmd)) {
			vista.hacerLogin();
		} else if ("registro".equals(cmd)) {
			vista.hacerRegistro();
		} else if ("irRegistro".equals(cmd)) {
			irARegistro();
		} else if ("irLogin".equals(cmd)) {
			irALogin();
		} else if ("invitado".equals(cmd)) {
			continuarComoInvitado();
		}
	}

	/**
	 * Realiza el login según el tipo de usuario.
	 *
	 * @param nickname nombre de usuario
	 * @param password contraseña del usuario
	 * @param tipo     tipo de usuario (Cliente, Empleado o Gestor)
	 * @return void
	 */
	public void realizarLogin(String nickname, String password, String tipo) {
		if (nickname.isEmpty() || password.isEmpty()) {
			vista.mostrarError("Por favor, rellena todos los campos.");
			return;
		}
		try {
			if ("Cliente".equals(tipo)) {
				Cliente cliente = tienda.loginCliente(nickname, password);
				if (cliente != null) {
					ventana.loginCliente(cliente);
				} else {
					vista.mostrarError("Nickname o contraseña incorrectos.");
				}
			} else if ("Empleado".equals(tipo)) {
				Empleado empleado = tienda.loginEmpleado(nickname, password);
				if (empleado != null) {
					ventana.loginEmpleado(empleado);
				} else {
					vista.mostrarError("Nickname o contraseña incorrectos o empleado despedido.");
				}
			} else if ("Gestor".equals(tipo)) {
				Gestor gestor = tienda.loginGestor(nickname, password);
				if (gestor != null) {
					ventana.loginGestor(gestor);
				} else {
					vista.mostrarError("Credenciales de gestor incorrectas.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			vista.mostrarError("Error al iniciar sesión: " + ex.getMessage());
		}
	}

	/**
	 * Registra un nuevo cliente en el sistema.
	 *
	 * @param nickname nombre de usuario
	 * @param password contraseña del usuario
	 * @param dni      documento del cliente
	 * @param tipo     tipo de usuario (debe ser Cliente)
	 * @return void
	 */
	public void realizarRegistro(String nickname, String password, String dni, String tipo) {
		if (!"Cliente".equalsIgnoreCase(tipo)) {
			vista.mostrarError("Desde esta pantalla solo se pueden registrar clientes.");
			return;
		}
		if (nickname.isEmpty() || password.isEmpty()) {
			vista.mostrarError("Por favor, rellena nickname y contraseña.");
			return;
		}
		if (dni == null || dni.isEmpty()) {
			vista.mostrarError("El DNI es obligatorio para crear una cuenta de cliente.");
			return;
		}
		try {
			Cliente nuevo = tienda.registrarNuevoCliente(nickname, password, dni);
			if (nuevo != null) {
				Cliente logueado = tienda.loginCliente(nickname, password);
				if (logueado != null) {
					vista.mostrarExito("¡Bienvenido a CheckPoint, " + nickname + "!");
					ventana.loginCliente(logueado);
				}
			} else {
				vista.mostrarError("No se pudo crear la cuenta. Comprueba que:\n" + "- El nickname no esté en uso\n"
						+ "- El DNI no esté registrado\n" + "- La contraseña sea segura");
			}
		} catch (Exception ex) {
			vista.mostrarError("Error al registrarse: " + ex.getMessage());
		}
	}

	/**
	 * Inicia sesión como invitado.
	 *
	 * @return void
	 */
	public void continuarComoInvitado() {
		ventana.loginInvitado();
	}

	/**
	 * Muestra la pantalla de registro.
	 *
	 * @return void
	 */
	public void irARegistro() {
		vista.mostrarRegistro();
	}

	/**
	 * Muestra la pantalla de login.
	 *
	 * @return void
	 */
	public void irALogin() {
		vista.mostrarLogin();
	}
}
