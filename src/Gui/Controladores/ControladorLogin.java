package Gui.Controladores;

import Gui.PantallaLogin;
import Gui.VentanaPrincipal;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;

/**
 * Controlador de la pantalla de login y registro. Gestiona toda la lógica de
 * autenticación y navegación desde el login.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorLogin {

	private VentanaPrincipal ventana;
	private PantallaLogin vista;
	private Tienda tienda;

	/**
	 * Constructor del controlador de login.
	 *
	 * @param ventana La ventana principal
	 * @param vista   La pantalla de login
	 */
	public ControladorLogin(VentanaPrincipal ventana, PantallaLogin vista) {
		this.ventana = ventana;
		this.vista = vista;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Realiza el login según el tipo de usuario seleccionado. Navega al panel
	 * correspondiente si tiene éxito.
	 *
	 * @param nickname El nickname introducido
	 * @param password La contraseña introducida
	 * @param tipo     El tipo de usuario seleccionado
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
	 * Realiza el registro de un nuevo usuario. Si tiene éxito hace login
	 * automático.
	 *
	 * @param nickname El nickname elegido
	 * @param password La contraseña elegida
	 * @param dni      El DNI (solo para clientes)
	 * @param tipo     El tipo de cuenta a crear
	 */
	/**
	 * Realiza el registro de un nuevo cliente. Si tiene éxito hace login
	 * automático.
	 *
	 * @param nickname El nickname elegido
	 * @param password La contraseña elegida
	 * @param dni      El DNI del cliente
	 * @param tipo     El tipo de cuenta a crear
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
	 * Navega al panel de invitado sin registro.
	 */
	public void continuarComoInvitado() {
		ventana.loginInvitado();
	}

	/**
	 * Muestra el formulario de registro.
	 */
	public void irARegistro() {
		vista.mostrarRegistro();
	}

	/**
	 * Muestra el formulario de login.
	 */
	public void irALogin() {
		vista.mostrarLogin();
	}
}