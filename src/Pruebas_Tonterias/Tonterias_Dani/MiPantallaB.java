package Pruebas_Tonterias.Tonterias_Dani;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;



class MiPantallaB extends JPanel {
 public MiPantallaB() {
     this.setBackground(Color.CYAN);
     this.add(new JLabel("ESTA ES LA PANTALLA B"));
     
     // Esta pantalla nace apagada
     this.setVisible(false);
 }
}