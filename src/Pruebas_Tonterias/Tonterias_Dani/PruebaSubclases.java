package Pruebas_Tonterias.Tonterias_Dani;

import java.awt.FlowLayout;

import javax.swing.JFrame;

public class PruebaSubclases {
    public static void main(String[] args) {
        // 1. Crear la ventana base
        JFrame ventana = new JFrame("Uso de Subclases");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Para cerrar el proceso
        ventana.setSize(400, 300);
        ventana.setLayout(new FlowLayout());

        // 2. Crear los paneles (nuestras clases personalizadas)
        MiPantallaB pantallaB = new MiPantallaB();

        // Creamos la A y le pasamos la B por el constructor
        MiPantallaA pantallaA = new MiPantallaA(pantallaB);

        // 3. Añadir ambos al contenedor de la ventana
        ventana.add(pantallaA);
        ventana.add(pantallaB);

        // 4. Hacer visible la ventana
        ventana.setVisible(true);
    }
}