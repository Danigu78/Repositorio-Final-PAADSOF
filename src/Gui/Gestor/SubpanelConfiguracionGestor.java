package Gui.Gestor;

import Gui.Controladores.ControladorConfiguracionGestor;
import Gui.VentanaPrincipal;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import usuarios.Gestor;

/**
 * Subpanel de configuración del sistema para el gestor.
 * Permite configurar los tiempos del sistema y el precio de tasación.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelConfiguracionGestor extends JPanel {

    private VentanaPrincipal ventana;
    private Gestor gestor;
    private ControladorConfiguracionGestor controlador;

    /**
     * Constructor del subpanel de configuración.
     *
     * @param ventana La ventana principal
     * @param gestor  El gestor logueado
     */
    public SubpanelConfiguracionGestor(VentanaPrincipal ventana, Gestor gestor) {
        this.ventana = ventana;
        this.gestor = gestor;
        this.controlador = new ControladorConfiguracionGestor(gestor);
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    /**
     * Construye la interfaz con los formularios de configuración.
     */
    private void inicializarUI() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50),
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(10), 0, VentanaPrincipal.escalar(10), 0);

        // Título
        JLabel titulo = new JLabel("Configuración del sistema");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panelContenido.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

        // Tiempo máximo carrito
        gbc.gridy = 1;
        panelContenido.add(crearEtiqueta("Tiempo máximo carrito (minutos):"), gbc);
        JSpinner spinnerCarrito = new JSpinner(new SpinnerNumberModel(
            controlador.getTiempoCarrito(), 1, 9999, 1));
        spinnerCarrito.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 2;
        panelContenido.add(spinnerCarrito, gbc);

        // Tiempo máximo oferta
        gbc.gridy = 3;
        panelContenido.add(crearEtiqueta("Tiempo máximo oferta (minutos):"), gbc);
        JSpinner spinnerOferta = new JSpinner(new SpinnerNumberModel(
            controlador.getTiempoOferta(), 1, 9999, 1));
        spinnerOferta.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 4;
        panelContenido.add(spinnerOferta, gbc);

        // Tiempo máximo pago
        gbc.gridy = 5;
        panelContenido.add(crearEtiqueta("Tiempo máximo pago (minutos):"), gbc);
        JSpinner spinnerPago = new JSpinner(new SpinnerNumberModel(
            controlador.getTiempoPago(), 1, 9999, 1));
        spinnerPago.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 6;
        panelContenido.add(spinnerPago, gbc);

        // Precio tasación
        gbc.gridy = 7;
        panelContenido.add(crearEtiqueta("Precio de tasación (€):"), gbc);
        JSpinner spinnerTasacion = new JSpinner(new SpinnerNumberModel(
            controlador.getPrecioTasacion(), 5.01, 9999.0, 0.5));
        spinnerTasacion.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 8;
        panelContenido.add(spinnerTasacion, gbc);

        // Botón guardar
        JButton botonGuardar = crearBoton("Guardar configuración");
        botonGuardar.addActionListener(e -> {
            int carrito = (int) spinnerCarrito.getValue();
            int oferta = (int) spinnerOferta.getValue();
            int pago = (int) spinnerPago.getValue();
            double tasacion = ((Number) spinnerTasacion.getValue()).doubleValue();

            boolean okTiempos = controlador.configurarTiempos(oferta, carrito, pago);
            boolean okTasacion = controlador.setPrecioTasacion(tasacion);

            if (okTiempos && okTasacion) {
                JOptionPane.showMessageDialog(this, "Configuración guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Algunos valores no se pudieron guardar. Comprueba que sean válidos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        gbc.gridy = 9;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panelContenido.add(botonGuardar, gbc);

        add(panelContenido, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)
        ));
        return boton;
    }
}