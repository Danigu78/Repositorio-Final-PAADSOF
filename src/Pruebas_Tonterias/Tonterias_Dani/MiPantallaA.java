package Pruebas_Tonterias.Tonterias_Dani;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class MiPantallaA extends JPanel {
	// El constructor recibe la "Pantalla B" para poder encenderla luego
	public MiPantallaA(final JPanel panelB) {
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(new FlowLayout());

		JLabel etiqueta = new JLabel("ESTÁS EN LA PANTALLA A");
		JButton botonIrB = new JButton("Ir a la B");

		// Acción del botón usando la sintaxis clásica
		botonIrB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Ocultamos esta pantalla (la A)
				MiPantallaA.this.setVisible(false);
				// Mostramos la que recibimos en el constructor (la B)
				panelB.setVisible(true);
			}
		});

		this.add(etiqueta);
		this.add(botonIrB);
	}
}