package Pruebas_Tonterias.Tonterias_Dani;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class prueba_varias_pantallas {

	public static void main(String[] args) {
        // 1. Crear ventana
        JFrame ventana = new JFrame("Mi GUI con dos pantallas");
        
        // 2. Obtener el contenedor y asignar layout((Organizador)
        Container contenedor = ventana.getContentPane();//Obtenemos la capa principal
        contenedor.setLayout(new FlowLayout());//Organizador simple es el que coloca las cosas en su sitio. Lo hace todo el
        
        
        JPanel pantalla1=new JPanel();
       
        
        JLabel etiqueta = new JLabel("Nombre");
        JTextField campo = new JTextField(20);  //Lo que imprime el tio en la primera barra
        JButton botonSiguiente = new JButton("Ir a la segunda pantalla");
        JLabel mensajeVueltaButton=new JLabel("¡Todavia no has ido y vuelto de la segunda pantalla!");
        
        //Los añadimos al espacio de la pantalla 1.
        pantalla1.add(etiqueta);
        pantalla1.add(campo);
        pantalla1.add(botonSiguiente);
        pantalla1.setVisible(true); // Esta empieza siendo visible,es la que sale justo cuando iniciamos la aplicacion 
        pantalla1.add(mensajeVueltaButton);
        //   CREAR PANTALLA 2(La nueva) 
        JPanel pantalla2 = new JPanel();
        JLabel mensajeFinal = new JLabel("¡Ya estás en la segunda pantalla!");
        JButton botonVolver = new JButton("Volver");
        
        pantalla2.add(mensajeFinal);
        pantalla2.add(botonVolver);
        pantalla2.setVisible(false); // Esta empieza oculta

      
        
        // Acción para ir hacia adelante
        botonSiguiente.addActionListener(new ActionListener() {//Escuchador. Cuando te pulsen haz esto.
            public void actionPerformed(ActionEvent e) {
                pantalla1.setVisible(false); // Escondo la 1
                pantalla2.setVisible(true);  // Muestro la 2
                // Opcional: mostrar lo que escribió antes
                mensajeFinal.setText("Hola " + campo.getText() + ", bienvenido a la pantalla 2");
            }
        });

        // Acción para volver (extra)
        botonVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pantalla2.setVisible(false); // Escondo la 2
                pantalla1.setVisible(true);  // Muestro la 1
                mensajeVueltaButton.setText("Hola "+campo.getText()+" has vuelto a la primera pantala");
            }
        });

        // 4. Añadir las PANTALLAS (los paneles) al contenedor
        contenedor.add(pantalla1);
        contenedor.add(pantalla2);

        // 5. Mostrar ventana
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        ventana.setSize(600, 400); 
        ventana.setVisible(true); 
    }
}