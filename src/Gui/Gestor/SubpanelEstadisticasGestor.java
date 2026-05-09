package Gui.Gestor;

import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorEstadisticasGestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import usuarios.Cliente;
import usuarios.Gestor;
import ventas.EstadoPedido;

/**
 * Subpanel de estadísticas para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelEstadisticasGestor extends AbstractPanelGestor {

    private ControladorEstadisticasGestor controlador;

    private JSpinner spinnerAño;
    private JPanel panelMesesAño;

    private JSpinner spinnerRangoInicio;
    private JSpinner spinnerRangoFin;
    private JLabel labelResultadoRango;

    private JSpinner spinnerRangoVentasInicio;
    private JSpinner spinnerRangoVentasFin;
    private JLabel labelResultadoVentas;

    private JSpinner spinnerRangoTasacionInicio;
    private JSpinner spinnerRangoTasacionFin;
    private JLabel labelResultadoTasacion;

    private JButton botonConsultarAño;
    private JButton botonConsultarRango;
    private JButton botonConsultarRangoVentas;
    private JButton botonConsultarRangoTasacion;

    public SubpanelEstadisticasGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana, gestor);
        this.controlador = new ControladorEstadisticasGestor(this, gestor);
        inicializarUI();
    }

    private void inicializarUI() {
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelContenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

        panelContenido.add(crearSeccionIngresos());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
        panelContenido.add(crearSeccionMesesActual());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
        panelContenido.add(crearSeccionMesesAño());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
        panelContenido.add(crearSeccionRangoTotal());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));
        panelContenido.add(crearSeccionRangoVentas());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));
        panelContenido.add(crearSeccionRangoTasacion());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
        panelContenido.add(crearSeccionTop(
            "Top clientes por compras",
            controlador.getTopCompras(), "compras"));
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
        panelContenido.add(crearSeccionTop(
            "Top clientes por intercambios",
            controlador.getTopIntercambios(), "intercambios"));
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
        panelContenido.add(crearSeccionTop(
            "Top clientes por pedidos cancelados",
            controlador.getTopCancelados(), "cancelados"));

        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
        add(scroll, BorderLayout.CENTER);

        setControlador(controlador);
    }

    public void setControlador(ActionListener c) {
        if (botonConsultarAño != null) {
            for (ActionListener al : botonConsultarAño.getActionListeners())
                botonConsultarAño.removeActionListener(al);
            botonConsultarAño.addActionListener(c);
        }
        if (botonConsultarRango != null) {
            for (ActionListener al : botonConsultarRango.getActionListeners())
                botonConsultarRango.removeActionListener(al);
            botonConsultarRango.addActionListener(c);
        }
        if (botonConsultarRangoVentas != null) {
            for (ActionListener al : botonConsultarRangoVentas.getActionListeners())
                botonConsultarRangoVentas.removeActionListener(al);
            botonConsultarRangoVentas.addActionListener(c);
        }
        if (botonConsultarRangoTasacion != null) {
            for (ActionListener al : botonConsultarRangoTasacion.getActionListeners())
                botonConsultarRangoTasacion.removeActionListener(al);
            botonConsultarRangoTasacion.addActionListener(c);
        }
    }

    private JPanel crearSeccionIngresos() {
        JPanel panel = new JPanel(
            new GridLayout(1, 2, VentanaPrincipal.escalar(15), 0));
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);
        panel.add(crearTarjetaEstadistica("Ingresos por ventas",
            String.format("%.2f€", controlador.getIngresosVentas())));
        panel.add(crearTarjetaEstadistica("Ingresos por tasaciones",
            String.format("%.2f€", controlador.getIngresosTasaciones())));
        return panel;
    }

    private JPanel crearSeccionMesesActual() {
        return crearPanelMeses(
            "Recaudación por mes (año actual)",
            controlador.getIngresosPorMeses());
    }

    private JPanel crearSeccionMesesAño() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(crearBordeTarjeta());

        JPanel cabecera = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
        cabecera.setBackground(VentanaPrincipal.COLOR_TARJETA);

        JLabel titulo = new JLabel("Recaudación por mes de un año:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        cabecera.add(titulo);

        spinnerAño = new JSpinner(new SpinnerNumberModel(
            LocalDate.now().getYear(), 2000, 2100, 1));
        spinnerAño.setFont(VentanaPrincipal.FUENTE_NORMAL);
        spinnerAño.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(30)));
        cabecera.add(spinnerAño);

        botonConsultarAño = crearBotonNaranja("Consultar");
        botonConsultarAño.setActionCommand("consultarAño");
        cabecera.add(botonConsultarAño);

        panel.add(cabecera, BorderLayout.NORTH);

        panelMesesAño = new JPanel(new GridLayout(
            3, 4, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));
        panelMesesAño.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelMesesAño.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), 0, 0, 0));
        rellenarPanelMeses(panelMesesAño, new double[12]);
        panel.add(panelMesesAño, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearSeccionRangoTotal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(crearBordeTarjeta());

        JLabel titulo = new JLabel("Ingresos totales en rango de fechas:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelControles = crearPanelControlesRango();
        spinnerRangoInicio = (JSpinner) panelControles.getClientProperty("inicio");
        spinnerRangoFin    = (JSpinner) panelControles.getClientProperty("fin");
        botonConsultarRango = (JButton) panelControles.getClientProperty("boton");
        botonConsultarRango.setActionCommand("consultarRango");
        panel.add(panelControles, BorderLayout.CENTER);

        labelResultadoRango = crearLabel("Introduce un rango y pulsa Consultar.");
        labelResultadoRango.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(5), 0, 0, 0));
        panel.add(labelResultadoRango, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearSeccionRangoVentas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(crearBordeTarjeta());

        JLabel titulo = new JLabel("Ingresos por ventas en rango de fechas:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelControles = crearPanelControlesRango();
        spinnerRangoVentasInicio = (JSpinner) panelControles.getClientProperty("inicio");
        spinnerRangoVentasFin    = (JSpinner) panelControles.getClientProperty("fin");
        botonConsultarRangoVentas = (JButton) panelControles.getClientProperty("boton");
        botonConsultarRangoVentas.setActionCommand("consultarRangoVentas");
        panel.add(panelControles, BorderLayout.CENTER);

        labelResultadoVentas = crearLabel("Introduce un rango y pulsa Consultar.");
        labelResultadoVentas.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(5), 0, 0, 0));
        panel.add(labelResultadoVentas, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearSeccionRangoTasacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(crearBordeTarjeta());

        JLabel titulo = new JLabel("Ingresos por tasación en rango de fechas:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelControles = crearPanelControlesRango();
        spinnerRangoTasacionInicio = (JSpinner) panelControles.getClientProperty("inicio");
        spinnerRangoTasacionFin    = (JSpinner) panelControles.getClientProperty("fin");
        botonConsultarRangoTasacion = (JButton) panelControles.getClientProperty("boton");
        botonConsultarRangoTasacion.setActionCommand("consultarRangoTasacion");
        panel.add(panelControles, BorderLayout.CENTER);

        labelResultadoTasacion = crearLabel("Introduce un rango y pulsa Consultar.");
        labelResultadoTasacion.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(5), 0, 0, 0));
        panel.add(labelResultadoTasacion, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea un panel de controles de rango reutilizable.
     * Guarda los spinners y el botón en clientProperty.
     */
    private JPanel crearPanelControlesRango() {
        JPanel panel = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(8)));
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);

        panel.add(crearLabel("Desde:"));
        JSpinner spinnerInicio = new JSpinner(new SpinnerDateModel());
        spinnerInicio.setEditor(new JSpinner.DateEditor(spinnerInicio, "dd/MM/yyyy"));
        spinnerInicio.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(120), VentanaPrincipal.escalar(30)));
        panel.add(spinnerInicio);

        panel.add(crearLabel("Hasta:"));
        JSpinner spinnerFin = new JSpinner(new SpinnerDateModel());
        spinnerFin.setEditor(new JSpinner.DateEditor(spinnerFin, "dd/MM/yyyy"));
        spinnerFin.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(120), VentanaPrincipal.escalar(30)));
        panel.add(spinnerFin);

        JButton boton = crearBotonNaranja("Consultar");
        panel.add(boton);

        // Guardamos referencias para recuperarlas al crear cada sección
        panel.putClientProperty("inicio", spinnerInicio);
        panel.putClientProperty("fin",    spinnerFin);
        panel.putClientProperty("boton",  boton);

        return panel;
    }

    private JPanel crearSeccionTop(String titulo,
                                    List<Cliente> clientes, String tipo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(crearBordeTarjeta());

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(labelTitulo, BorderLayout.NORTH);

        JPanel panelClientes = new JPanel();
        panelClientes.setLayout(new BoxLayout(panelClientes, BoxLayout.Y_AXIS));
        panelClientes.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelClientes.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), 0, 0, 0));

        if (clientes.isEmpty()) {
            panelClientes.add(crearLabel("No hay datos."));
        } else {
            int pos = 1;
            for (Cliente c : clientes) {
                String info;
                if (tipo.equals("compras")) {
                    info = pos + ". " + c.getNickname()
                        + " — " + c.contarPedidosCompletados() + " pedidos";
                } else if (tipo.equals("intercambios")) {
                    info = pos + ". " + c.getNickname()
                        + " — " + c.contarIntercambios() + " intercambios";
                } else {
                    long cancelados = c.getHistorialPedidos().stream()
                        .filter(p -> p.getEstado() == EstadoPedido.CANCELADO)
                        .count();
                    info = pos + ". " + c.getNickname()
                        + " — " + cancelados + " cancelados";
                }
                JLabel label = new JLabel(info);
                label.setFont(VentanaPrincipal.FUENTE_NORMAL);
                label.setForeground(pos == 1
                    ? VentanaPrincipal.COLOR_ACENTO : VentanaPrincipal.COLOR_TEXTO);
                panelClientes.add(label);
                pos++;
            }
        }

        panel.add(panelClientes, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelMeses(String titulo, double[] ingresos) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(crearBordeTarjeta());

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(labelTitulo, BorderLayout.NORTH);

        JPanel panelMeses = new JPanel(new GridLayout(
            3, 4, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));
        panelMeses.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelMeses.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), 0, 0, 0));
        rellenarPanelMeses(panelMeses, ingresos);
        panel.add(panelMeses, BorderLayout.CENTER);

        return panel;
    }

    private void rellenarPanelMeses(JPanel panel, double[] ingresos) {
        panel.removeAll();
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun",
                          "Jul","Ago","Sep","Oct","Nov","Dic"};
        for (int i = 0; i < 12; i++) {
            JPanel tarjeta = new JPanel(new BorderLayout());
            tarjeta.setBackground(VentanaPrincipal.COLOR_FONDO);
            tarjeta.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
                javax.swing.BorderFactory.createEmptyBorder(
                    VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8),
                    VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8))));

            JLabel labelMes = crearLabel(meses[i]);
            labelMes.setHorizontalAlignment(SwingConstants.CENTER);
            tarjeta.add(labelMes, BorderLayout.NORTH);

            JLabel labelValor = new JLabel(
                String.format("%.2f€", ingresos[i]), SwingConstants.CENTER);
            labelValor.setFont(VentanaPrincipal.FUENTE_BOTON);
            labelValor.setForeground(ingresos[i] > 0
                ? VentanaPrincipal.COLOR_ACENTO : VentanaPrincipal.COLOR_TEXTO2);
            tarjeta.add(labelValor, BorderLayout.CENTER);

            panel.add(tarjeta);
        }
        panel.revalidate();
        panel.repaint();
    }

    private JPanel crearTarjetaEstadistica(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(crearBordeTarjeta());

        JLabel labelTitulo = crearLabel(titulo);
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelTitulo, BorderLayout.NORTH);

        JLabel labelValor = new JLabel(valor, SwingConstants.CENTER);
        labelValor.setFont(VentanaPrincipal.FUENTE_TITULO);
        labelValor.setForeground(VentanaPrincipal.COLOR_ACENTO);
        panel.add(labelValor, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el borde estándar de tarjeta — reutilizado en todas las secciones.
     */
    private javax.swing.border.Border crearBordeTarjeta() {
        return javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15)));
    }

    // ── Métodos que llama el controlador ──────────────────────────────────

    public void procesarConsultarAño() {
        int año = (int) spinnerAño.getValue();
        rellenarPanelMeses(panelMesesAño, controlador.getIngresosPorAño(año));
    }

    public void procesarConsultarRango() {
        LocalDate inicio = spinnerALocalDate(spinnerRangoInicio);
        LocalDate fin    = spinnerALocalDate(spinnerRangoFin);
        if (fin.isBefore(inicio)) {
            mostrarError("La fecha de fin debe ser posterior a la de inicio.");
            return;
        }
        double total = controlador.getIngresosRango(inicio, fin);
        labelResultadoRango.setText(
            "Ingresos totales del " + inicio + " al " + fin
            + ":  " + String.format("%.2f€", total));
    }

    public void procesarConsultarRangoVentas() {
        LocalDate inicio = spinnerALocalDate(spinnerRangoVentasInicio);
        LocalDate fin    = spinnerALocalDate(spinnerRangoVentasFin);
        if (fin.isBefore(inicio)) {
            mostrarError("La fecha de fin debe ser posterior a la de inicio.");
            return;
        }
        double total = controlador.getIngresosVentasRango(inicio, fin);
        labelResultadoVentas.setText(
            "Ingresos por ventas del " + inicio + " al " + fin
            + ":  " + String.format("%.2f€", total));
    }

    public void procesarConsultarRangoTasacion() {
        LocalDate inicio = spinnerALocalDate(spinnerRangoTasacionInicio);
        LocalDate fin    = spinnerALocalDate(spinnerRangoTasacionFin);
        if (fin.isBefore(inicio)) {
            mostrarError("La fecha de fin debe ser posterior a la de inicio.");
            return;
        }
        double total = controlador.getIngresosTasacionRango(inicio, fin);
        labelResultadoTasacion.setText(
            "Ingresos por tasación del " + inicio + " al " + fin
            + ":  " + String.format("%.2f€", total));
    }

    /**
     * Convierte un JSpinner de fecha a LocalDate.
     */
    private LocalDate spinnerALocalDate(JSpinner spinner) {
        java.util.Date fecha = (java.util.Date) spinner.getValue();
        return fecha.toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
}