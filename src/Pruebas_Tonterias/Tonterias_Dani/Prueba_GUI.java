package Pruebas_Tonterias.Tonterias_Dani;
import javax.swing.*; // Importa JFrame, JLabel, etc.
import java.awt.*;    // Importa Container y FlowLayout
import java.awt.event.*; // Importa ActionListener y ActionEvent

public class Prueba_GUI {

    public static void main(String[] args) {
        // 1. Crear ventana
        JFrame ventana = new JFrame("Mi GUI");
        // 2. Obtener el contenedor y asignar layout
        Container contenedor = ventana.getContentPane();
        contenedor.setLayout(new FlowLayout());

        // 3. Crear componentes
        JLabel etiqueta = new JLabel("Nombre");
        JTextField campo = new JTextField(50); //Lo largo que es 
        JButton boton = new JButton("Haz click");

        
        // 3.1 Definir acciones
        boton.addActionListener(new ActionListener() {// quedate atento a lo que te digan 
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, campo.getText()); // extrae lo que se haya escrito en el campo de etxto 
            } //Abre una pequeña ventana emergente para mostrar el mensaje.
        });

        // 4. Añadir componentes al contenedor
        contenedor.add(etiqueta);
        contenedor.add(campo);
        contenedor.add(boton);

        // 5. Mostrar ventana
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // CUANDO SE PULSA LA X , el programa debe detenerse por completo 
        ventana.setSize(1500, 550); //Define el tamaño de la ventana 
        ventana.setVisible(true); //Muestra la ventana
        
        

    }
}