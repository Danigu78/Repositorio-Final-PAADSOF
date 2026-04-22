package Gui;
import javax.swing.*;
import java.awt.*;
import usuarios.Cliente;

public class SubpanelPedidos extends JPanel {
    private VentanaPrincipal ventana;
    private Cliente cliente;

    public SubpanelPedidos(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Mis Pedidos - En construcción", SwingConstants.CENTER);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO);
        add(label, BorderLayout.CENTER);
    }

    public void actualizar(Cliente cliente) {
        this.cliente = cliente;
    }
}