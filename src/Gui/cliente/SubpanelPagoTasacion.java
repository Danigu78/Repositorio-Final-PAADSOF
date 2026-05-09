package Gui.cliente;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorPagoTasacion;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import productos.Producto2Mano;
import usuarios.Cliente;

/**
 * Subpanel de pago de tasación.
 * Solo gestiona la interfaz — la lógica la delega en ControladorPagoTasacion.
 * Sigue el patrón MVC de los apuntes: expone setControlador(ActionListener).
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPagoTasacion extends JPanel {

    private VentanaPrincipal ventana;
    private PanelCliente panelCliente;
    private ControladorPagoTasacion controlador;

    private JTextField campoTarjeta;
    private JPasswordField campoCVV;
    private JTextField campoFecha;
    private JButton botonPagar;
    private JButton botonVolver;

    public SubpanelPagoTasacion(VentanaPrincipal ventana, PanelCliente panelCliente) {
        this.ventana = ventana;
        this.panelCliente = panelCliente;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
    }

    public void mostrarPago(Producto2Mano producto, Cliente cliente) {
        this.controlador = new ControladorPagoTasacion(this, cliente, producto);
        removeAll();
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelPago(), BorderLayout.CENTER);
        setControlador(controlador);
        revalidate();
        repaint();
    }

    /**
     * Registra el controlador en los botones — patrón de los apuntes.
     */
    public void setControlador(ActionListener c) {
        botonPagar.addActionListener(c);
        botonVolver.addActionListener(c);
    }

    public String getNumeroTarjeta() {
        return campoTarjeta.getText().trim();
    }

    public String getCVV() {
        return new String(campoCVV.getPassword()).trim();
    }

    public String getFechaCaducidad() {
        return campoFecha.getText().trim();
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);
        barra.setBorder(BorderFactory.createMatteBorder(
            0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

        botonVolver = new JButton("← Volver a mi cartera");
        botonVolver.setActionCommand("volver");
        botonVolver.setFont(VentanaPrincipal.FUENTE_NORMAL);
        botonVolver.setForeground(VentanaPrincipal.COLOR_TEXTO);
        botonVolver.setBackground(VentanaPrincipal.COLOR_PANEL);
        botonVolver.setOpaque(true);
        botonVolver.setBorderPainted(true);
        botonVolver.setFocusPainted(false);
        botonVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonVolver.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(15))));
        botonVolver.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botonVolver.setForeground(VentanaPrincipal.COLOR_ACENTO);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botonVolver.setForeground(VentanaPrincipal.COLOR_TEXTO);
            }
        });
        barra.add(botonVolver);
        return barra;
    }

    private JPanel crearPanelPago() {
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(200),
            VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(200)));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40),
                VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40))));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(VentanaPrincipal.escalar(6), 0, VentanaPrincipal.escalar(6), 0);

        // Título
        JLabel titulo = new JLabel("Solicitar tasación");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
        panel.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

        // Producto
        JLabel labelProducto = new JLabel("Producto: " + controlador.getNombreProducto());
        labelProducto.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelProducto.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridy = 1;
        panel.add(labelProducto, gbc);

        // Precio
        JLabel labelPrecio = new JLabel(
            String.format("Coste de tasación: %.2f€", controlador.getPrecioTasacion()));
        labelPrecio.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(22)));
        labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
        panel.add(labelPrecio, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);
        gbc.gridy = 3;
        panel.add(new JSeparator(), gbc);

        // Número de tarjeta
        gbc.gridy = 4;
        panel.add(crearEtiqueta("Número de tarjeta (16 dígitos):"), gbc);
        campoTarjeta = crearCampo();
        gbc.gridy = 5;
        panel.add(campoTarjeta, gbc);

        // CVV
        gbc.gridy = 6;
        panel.add(crearEtiqueta("CVV (3 dígitos):"), gbc);
        campoCVV = new JPasswordField();
        campoCVV.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campoCVV.setForeground(Color.BLACK);
        campoCVV.setBackground(Color.WHITE);
        campoCVV.setCaretColor(Color.BLACK);
        campoCVV.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
        gbc.gridy = 7;
        panel.add(campoCVV, gbc);

        // Fecha caducidad
        gbc.gridy = 8;
        panel.add(crearEtiqueta("Fecha caducidad (MM/AA):"), gbc);
        campoFecha = crearCampo();
        gbc.gridy = 9;
        panel.add(campoFecha, gbc);

        // Botón pagar
        botonPagar = new JButton(
            String.format("Pagar %.2f€", controlador.getPrecioTasacion()));
        botonPagar.setActionCommand("pagar");
        botonPagar.setFont(VentanaPrincipal.FUENTE_BOTON);
        botonPagar.setBackground(VentanaPrincipal.COLOR_ACENTO);
        botonPagar.setForeground(Color.WHITE);
        botonPagar.setOpaque(true);
        botonPagar.setBorderPainted(false);
        botonPagar.setFocusPainted(false);
        botonPagar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPagar.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20)));
        botonPagar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botonPagar.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botonPagar.setBackground(VentanaPrincipal.COLOR_ACENTO);
            }
        });
        gbc.gridy = 10;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panel.add(botonPagar, gbc);

        panelCentral.add(panel, BorderLayout.CENTER);
        return panelCentral;
    }

    public void volverACartera() {
        if (panelCliente != null) {
            panelCliente.volverACartera();
        }
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje,
            "Tasación solicitada", JOptionPane.INFORMATION_MESSAGE);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
        return campo;
    }
}