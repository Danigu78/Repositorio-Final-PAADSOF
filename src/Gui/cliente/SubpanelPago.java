package Gui.cliente;

import Gui.VentanaPrincipal;

import Gui.controladores.cliente.ControladorPago;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Cliente;
import ventas.Pedido;

/**
 * Subpanel de pago de CheckPoint.
 * Muestra el resumen del pedido, campos de tarjeta y botón pagar.
 * Tras pago correcto vuelve a Mis Pedidos.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPago extends JPanel {

    /** Referencia a la ventana principal */
    private VentanaPrincipal ventana;

    /** Referencia al panel cliente para volver a pedidos */
    private PanelCliente panelCliente;

    /** Controlador del pago */
    private ControladorPago controlador;

    /**
     * Constructor del subpanel de pago.
     *
     * @param ventana      La ventana principal
     * @param panelCliente El panel cliente para navegar a pedidos
     */
    public SubpanelPago(VentanaPrincipal ventana, PanelCliente panelCliente) {
        this.ventana = ventana;
        this.panelCliente = panelCliente;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
    }

    /**
     * Carga el pedido y construye la interfaz de pago.
     *
     * @param pedido  El pedido a pagar
     * @param cliente El cliente logueado
     */
    public void mostrarPago(Pedido pedido, Cliente cliente) {
        this.controlador = new ControladorPago(this, cliente, pedido);
        removeAll();
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelPago(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Crea la barra superior con botón volver a pedidos.
     *
     * @return Panel de la barra superior
     */
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);
        barra.setBorder(BorderFactory.createMatteBorder(
            0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

        JButton botonVolver = new JButton("← Volver a mis pedidos");
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
        botonVolver.addActionListener(e -> volverAPedidos());
        barra.add(botonVolver);
        return barra;
    }

    /**
     * Crea el panel central con resumen del pedido y formulario de pago.
     *
     * @return Panel de pago centrado
     */
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
                VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40))
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(VentanaPrincipal.escalar(6), 0, VentanaPrincipal.escalar(6), 0);

        // Título
        JLabel titulo = new JLabel("Realizar pago");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
        panel.add(titulo, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

        // ID pedido
        JLabel labelId = new JLabel("Pedido: " + controlador.getIdPedido());
        labelId.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelId.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridy = 1;
        panel.add(labelId, gbc);

        // Total
        JLabel labelTotal = new JLabel(
            String.format("Total a pagar: %.2f€", controlador.getTotal()));
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(22)));
        labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
        panel.add(labelTotal, gbc);

        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);
        gbc.gridy = 3;
        panel.add(new JSeparator(), gbc);

        // Número de tarjeta
        gbc.gridy = 4;
        panel.add(crearEtiqueta("Número de tarjeta (16 dígitos):"), gbc);
        JTextField campoTarjeta = crearCampo();
        gbc.gridy = 5;
        panel.add(campoTarjeta, gbc);

        // CVV
        gbc.gridy = 6;
        panel.add(crearEtiqueta("CVV (3 dígitos):"), gbc);
        JPasswordField campoCVV = new JPasswordField();
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

        // Botón pagar
        JButton botonPagar = new JButton(
            String.format("Pagar %.2f€", controlador.getTotal()));
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
        botonPagar.addActionListener(e -> controlador.realizarPago(
            campoTarjeta.getText().trim(),
            new String(campoCVV.getPassword()).trim()
        ));
        campoCVV.addActionListener(e -> controlador.realizarPago(
            campoTarjeta.getText().trim(),
            new String(campoCVV.getPassword()).trim()
        ));
        gbc.gridy = 8;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panel.add(botonPagar, gbc);

        panelCentral.add(panel, BorderLayout.CENTER);
        return panelCentral;
    }

    /**
     * Vuelve a Mis Pedidos. Lo llama el controlador tras pago correcto.
     */
    public void volverAPedidos() {
        if (panelCliente != null) {
            panelCliente.actualizarSeccionPedidos();
            panelCliente.mostrarSeccion("PEDIDOS");
        }
    }
    /**
     * Muestra un mensaje de error. Lo llama el controlador.
     *
     * @param mensaje El mensaje de error
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje de éxito. Lo llama el controlador.
     *
     * @param mensaje El mensaje de éxito
     */
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Pago realizado", JOptionPane.INFORMATION_MESSAGE);
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