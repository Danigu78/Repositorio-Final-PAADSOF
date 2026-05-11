package Gui.cliente;

import javax.swing.*;
import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorPago;
import java.awt.*;
import java.awt.event.*;
import usuarios.Cliente;
import ventas.Pedido;

/**
 * Subpanel de pago de CheckPoint.
 * Muestra el resumen del pedido, campos de tarjeta y botón pagar.
 * Tras pago correcto vuelve a Mis Pedidos.
 * Extiende AbstractPanelCliente para reutilizar helpers visuales del cliente.
 * Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPago extends AbstractPanelCliente {

    /** Referencia al panel cliente para volver a pedidos. */
    private PanelCliente panelCliente;

    /** Controlador del pago. */
    private ControladorPago controlador;

    /** Botón volver — atributo para registrar el controlador. */
    private JButton botonVolver;

    /** Botón pagar — atributo para registrar el controlador. */
    private JButton botonPagar;

    /** Campo número de tarjeta. */
    private JTextField campoTarjeta;

    /** Campo CVV. */
    private JPasswordField campoCVV;

    /** Campo fecha de caducidad. */
    private JTextField campoFecha;

    /**
     * Constructor del subpanel de pago.
     *
     * @param ventana      La ventana principal
     * @param panelCliente El panel cliente para navegar a pedidos
     */
    public SubpanelPago(VentanaPrincipal ventana, PanelCliente panelCliente) {
        super(ventana);
        this.panelCliente = panelCliente;
    }

    /**
     * No se usa en este subpanel — la construcción se hace en mostrarPago().
     * Se implementa por obligación de AbstractPanelCliente.
     *
     * @param cliente El cliente logueado
     */
    @Override
    public void actualizar(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Carga el pedido y construye la interfaz de pago.
     * Crea el controlador y lo registra en los botones.
     *
     * @param pedido  El pedido a pagar
     * @param cliente El cliente logueado
     */
    public void mostrarPago(Pedido pedido, Cliente cliente) {
        this.cliente = cliente;
        this.controlador = new ControladorPago(this, cliente, pedido);
        removeAll();
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelPago(), BorderLayout.CENTER);
        setControlador(controlador);
        revalidate();
        repaint();
    }

    /**
     * Registra el controlador en los botones — patrón de los apuntes.
     *
     * @param c El ActionListener a registrar
     */
    public void setControlador(ActionListener c) {
        if (botonVolver != null) {
            for (ActionListener al : botonVolver.getActionListeners())
                botonVolver.removeActionListener(al);
            botonVolver.addActionListener(c);
        }
        if (botonPagar != null) {
            for (ActionListener al : botonPagar.getActionListeners())
                botonPagar.removeActionListener(al);
            botonPagar.addActionListener(c);
        }
        if (campoCVV != null) {
            for (ActionListener al : campoCVV.getActionListeners())
                campoCVV.removeActionListener(al);
            campoCVV.setActionCommand("pagar");
            campoCVV.addActionListener(c);
        }
    }

    /**
     * Lee los campos y llama al controlador para pagar.
     * Lo llama el controlador desde actionPerformed.
     */
    public void procesarPago() {
        controlador.realizarPago(
            campoTarjeta.getText().trim(),
            new String(campoCVV.getPassword()).trim(),
            campoFecha.getText().trim());
    }

    /**
     * Crea la barra superior con botón volver a pedidos.
     *
     * @return Panel de la barra superior
     */
    private JPanel crearBarraSuperior() {
        // crearBarraVolver() de AbstractPanelSection
        JPanel barra = crearBarraVolver("← Volver a mis pedidos");
        botonVolver = getBotonVolver(barra);
        botonVolver.setActionCommand("volver");
        return barra;
    }

    /**
     * Crea el panel central con resumen del pedido y formulario de pago.
     * Idéntico a SubpanelPagoTasacion con campo fecha de caducidad incluido.
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
                VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40))));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = 0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets  = new Insets(
            VentanaPrincipal.escalar(6), 0,
            VentanaPrincipal.escalar(6), 0);

        // Título
        JLabel titulo = new JLabel("Realizar pago");
        titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
        panel.add(titulo, gbc);

        gbc.insets = new Insets(
            VentanaPrincipal.escalar(4), 0,
            VentanaPrincipal.escalar(4), 0);

        // Info pedido
        // crearLabel() de AbstractPanelSection
        gbc.gridy = 1;
        panel.add(crearLabel("Pedido: " + controlador.getIdPedido()), gbc);

        JLabel labelTotal = new JLabel(
            String.format("Total a pagar: %.2f€", controlador.getTotal()));
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD,
            VentanaPrincipal.escalar(22)));
        labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
        gbc.gridy  = 2;
        gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
        panel.add(labelTotal, gbc);

        gbc.insets = new Insets(
            VentanaPrincipal.escalar(4), 0,
            VentanaPrincipal.escalar(4), 0);

        gbc.gridy = 3;
        panel.add(new JSeparator(), gbc);

        // Número de tarjeta
        gbc.gridy = 4;
        panel.add(crearLabel("Número de tarjeta (16 dígitos):"), gbc);

        // crearCampo() de AbstractPanelSection
        campoTarjeta = crearCampo();
        gbc.gridy = 5;
        panel.add(campoTarjeta, gbc);

        // CVV
        gbc.gridy = 6;
        panel.add(crearLabel("CVV (3 dígitos):"), gbc);

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
        campoCVV.setActionCommand("pagar");
        gbc.gridy = 7;
        panel.add(campoCVV, gbc);

        // Fecha caducidad — igual que SubpanelPagoTasacion
        gbc.gridy = 8;
        panel.add(crearLabel("Fecha caducidad (MM/AA):"), gbc);

        campoFecha = crearCampo();
        gbc.gridy = 9;
        panel.add(campoFecha, gbc);

        // Botón pagar
        // crearBotonNaranja() de AbstractPanelSection
        botonPagar = crearBotonNaranja(
            String.format("Pagar %.2f€", controlador.getTotal()));
        botonPagar.setActionCommand("pagar");
        botonPagar.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20)));
        gbc.gridy  = 10;
        gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
        panel.add(botonPagar, gbc);

        panelCentral.add(panel, BorderLayout.CENTER);
        return panelCentral;
    }

    /**
     * Vuelve a Mis Pedidos. Lo llama el controlador.
     */
    public void volverAPedidos() {
        if (panelCliente != null) {
            panelCliente.actualizarSeccionPedidos();
            panelCliente.mostrarSeccion("PEDIDOS");
        }
    }

    /**
     * Muestra un mensaje de éxito. Lo llama el controlador.
     *
     * @param mensaje El mensaje de éxito
     */
    public void mostrarExito(String mensaje) {
        mostrarMensaje(mensaje);
    }
}