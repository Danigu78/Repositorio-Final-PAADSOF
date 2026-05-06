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
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorLogin implements ActionListener {

    private VentanaPrincipal ventana;
    private PantallaLogin vista;
    private Tienda tienda;

    public ControladorLogin(VentanaPrincipal ventana, PantallaLogin vista) {
        this.ventana = ventana;
        this.vista = vista;
        this.tienda = Tienda.getInstancia();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("login")) {
            vista.hacerLogin();
        } else if (cmd.equals("registro")) {
            vista.hacerRegistro();
        } else if (cmd.equals("irRegistro")) {
            irARegistro();
        } else if (cmd.equals("irLogin")) {
            irALogin();
        } else if (cmd.equals("invitado")) {
            continuarComoInvitado();
        }
    }

    /**
     * Realiza el login según el tipo de usuario seleccionado.
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
                    vista.mostrarError(
                        "Nickname o contraseña incorrectos o empleado despedido.");
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
     * Realiza el registro de un nuevo cliente.
     */
    public void realizarRegistro(String nickname, String password,
                                  String dni, String tipo) {
        if (!"Cliente".equalsIgnoreCase(tipo)) {
            vista.mostrarError(
                "Desde esta pantalla solo se pueden registrar clientes.");
            return;
        }
        if (nickname.isEmpty() || password.isEmpty()) {
            vista.mostrarError("Por favor, rellena nickname y contraseña.");
            return;
        }
        if (dni == null || dni.isEmpty()) {
            vista.mostrarError(
                "El DNI es obligatorio para crear una cuenta de cliente.");
            return;
        }
        try {
            Cliente nuevo = tienda.registrarNuevoCliente(nickname, password, dni);
            if (nuevo != null) {
                Cliente logueado = tienda.loginCliente(nickname, password);
                if (logueado != null) {
                    vista.mostrarExito(
                        "¡Bienvenido a CheckPoint, " + nickname + "!");
                    ventana.loginCliente(logueado);
                }
            } else {
                vista.mostrarError(
                    "No se pudo crear la cuenta. Comprueba que:\n"
                    + "- El nickname no esté en uso\n"
                    + "- El DNI no esté registrado\n"
                    + "- La contraseña sea segura");
            }
        } catch (Exception ex) {
            vista.mostrarError("Error al registrarse: " + ex.getMessage());
        }
    }

    public void continuarComoInvitado() {
        ventana.loginInvitado();
    }

    public void irARegistro() {
        vista.mostrarRegistro();
    }

    public void irALogin() {
        vista.mostrarLogin();
    }
}