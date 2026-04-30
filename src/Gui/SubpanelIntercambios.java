package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import usuarios.Cliente;

public class SubpanelIntercambios extends JPanel {
	private VentanaPrincipal ventana;
	private Cliente cliente;
	private JButton botonEnviados;
	private JButton botonRecibidos;
	private JButton botonHistorial;

	public SubpanelIntercambios(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setBackground(VentanaPrincipal.COLOR_FONDO);
		setLayout(new BorderLayout());
		JLabel label = new JLabel("Intercambios - En construcción", SwingConstants.CENTER);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO);
		add(label, BorderLayout.CENTER);
	}

	public void setControladores(ActionListener c) {

		botonEnviados.addActionListener(c);
		botonRecibidos.addActionListener(c);
		botonHistorial.addActionListener(c);
	}

	private JPanel crearBarraInterna() {
	}

	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
	}
}