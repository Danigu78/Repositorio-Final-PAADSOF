package Gui;
import javax.swing.*;
import java.awt.*;
import usuarios.Cliente;

public class SubpanelPerfil extends JPanel {
    private VentanaPrincipal ventana;
    private Cliente cliente;

    public SubpanelPerfil(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Mi Perfil - En construcción", SwingConstants.CENTER);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO);
        add(label, BorderLayout.CENTER);
    }

    public void actualizar(Cliente cliente) {
        this.cliente = cliente;
    }
}