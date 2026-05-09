package Gui.gestor;

import Gui.controladores.gestor.ControladorConfiguracionGestor;
import Gui.VentanaPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Gestor;

/**
 * Subpanel de configuración del sistema para el gestor.
 * Extiende AbstractPanelGestor para reutilizar helpers visuales.
 * Sigue el patrón MVC de los apuntes.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelConfiguracionGestor extends AbstractPanelGestor {

    private ControladorConfiguracionGestor controlador;

    // Spinners — atributos para que el controlador pueda leerlos
    private JSpinner spinnerCarrito;
    private JSpinner spinnerOferta;
    private JSpinner spinnerPago;
    private JSpinner spinnerTasacion;

    // Botón — atributo para registrar el controlador
    private JButton botonGuardar;

    public SubpanelConfiguracionGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana, gestor);
        this.controlador = new ControladorConfiguracionGestor(this, gestor);
        inicializarUI();
    }

    private void inicializarUI() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelContenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50),
            VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);

        JLabel titulo = new JLabel("Configuración del sistema");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
        panelContenido.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0,
            VentanaPrincipal.escalar(4), 0);

        // crearLabel() de AbstractPanelSection
        gbc.gridy = 1;
        panelContenido.add(crearLabel("Tiempo máximo carrito (minutos):"), gbc);
        spinnerCarrito = new JSpinner(new SpinnerNumberModel(
            controlador.getTiempoCarrito(), 1, 9999, 1));
        spinnerCarrito.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 2;
        panelContenido.add(spinnerCarrito, gbc);

        gbc.gridy = 3;
        panelContenido.add(crearLabel("Tiempo máximo oferta (minutos):"), gbc);
        spinnerOferta = new JSpinner(new SpinnerNumberModel(
            controlador.getTiempoOferta(), 1, 9999, 1));
        spinnerOferta.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 4;
        panelContenido.add(spinnerOferta, gbc);

        gbc.gridy = 5;
        panelContenido.add(crearLabel("Tiempo máximo pago (minutos):"), gbc);
        spinnerPago = new JSpinner(new SpinnerNumberModel(
            controlador.getTiempoPago(), 1, 9999, 1));
        spinnerPago.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 6;
        panelContenido.add(spinnerPago, gbc);

        gbc.gridy = 7;
        panelContenido.add(crearLabel("Precio de tasación (€):"), gbc);
        spinnerTasacion = new JSpinner(new SpinnerNumberModel(
            controlador.getPrecioTasacion(), 5.01, 9999.0, 0.5));
        spinnerTasacion.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 8;
        panelContenido.add(spinnerTasacion, gbc);

        // crearBotonNaranja() de AbstractPanelSection
        botonGuardar = crearBotonNaranja("Guardar configuración");
        botonGuardar.setActionCommand("guardarConfig");
        gbc.gridy = 9;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panelContenido.add(botonGuardar, gbc);

        add(panelContenido, BorderLayout.CENTER);
        setControlador(controlador);
    }

    public void setControlador(ActionListener c) {
        if (botonGuardar != null) {
            for (ActionListener al : botonGuardar.getActionListeners())
                botonGuardar.removeActionListener(al);
            botonGuardar.addActionListener(c);
        }
    }

    /**
     * Lee los spinners y guarda la configuración.
     * Lo llama el controlador.
     */
    public void procesarGuardar() {
        int carrito  = (int) spinnerCarrito.getValue();
        int oferta   = (int) spinnerOferta.getValue();
        int pago     = (int) spinnerPago.getValue();
        double tasacion = ((Number) spinnerTasacion.getValue()).doubleValue();

        boolean okTiempos   = controlador.configurarTiempos(oferta, carrito, pago);
        boolean okTasacion  = controlador.setPrecioTasacion(tasacion);

        if (okTiempos && okTasacion) {
            mostrarMensaje("Configuración guardada correctamente.");
        } else {
            mostrarError(
                "Algunos valores no se pudieron guardar. Comprueba que sean válidos.");
        }
    }

    @Override
    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}