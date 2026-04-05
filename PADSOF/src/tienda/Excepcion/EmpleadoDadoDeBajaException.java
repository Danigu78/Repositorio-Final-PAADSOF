package Excepcion;

public class EmpleadoDadoDeBajaException extends CheckPointException {
    private static final long serialVersionUID = 1L;
    private String nickname;

    public EmpleadoDadoDeBajaException(String nickname) {
        // ERROR SOLUCIONADO: Ahora le pasamos el mensaje a la clase padre
        super("El empleado " + nickname + " está dado de baja y no puede iniciar sesión.");
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
