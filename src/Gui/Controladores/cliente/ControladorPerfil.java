package Gui.Controladores.cliente;

import Gui.cliente.SubpanelPerfil;

import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del subpanel de perfil. Implementa ActionListener según el patrón
 * MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPerfil implements ActionListener {

	private SubpanelPerfil vista;
	private Cliente cliente;

	public ControladorPerfil(SubpanelPerfil subpanelPerfil, Cliente cliente) {
		this.vista = subpanelPerfil;
		this.cliente = cliente;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("guardar")) {
			guardarCambios();
		}
	}

	/**
	 * Guarda los cambios del perfil del cliente. Si los campos están vacíos
	 * mantiene los valores actuales.
	 */
	private void guardarCambios() {
		String nuevoNickname = vista.getNuevoNickname();
		String nuevaPassword = vista.getNuevaPassword();

		// Si ambos están vacíos no hacemos nada
		if (nuevoNickname.isBlank() && nuevaPassword.isBlank()) {
			vista.mostrarError("Rellena al menos uno de los campos.");
			return;
		}

		// Si el nickname está vacío mantenemos el actual
		if (nuevoNickname.isBlank()) {
			nuevoNickname = cliente.getNickname();
		}

		// Si la contraseña está vacía mantenemos la actual
		// pero modificarPerfil requiere contraseña — usamos la actual
		if (nuevaPassword.isBlank()) {
			vista.mostrarError("Debes introducir la contraseña aunque no la cambies.");
			return;
		}

		boolean ok = cliente.modificarPerfil(nuevoNickname, nuevaPassword);
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
			vista.mostrarExito("Perfil actualizado correctamente.");
			vista.actualizarNombreEnBarra();
			vista.actualizar(cliente);
		} else {
			vista.mostrarError("No se pudo actualizar el perfil. Comprueba que:\n" + "- El nickname no esté en uso\n"
					+ "- La contraseña tenga mayúsculas, minúsculas, números y caracteres especiales");
		}
	}
}