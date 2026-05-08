package Gui.Gestor;

import Gui.Controladores.Gestor.ControladorEstadisticasGestor;
import Gui.VentanaPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import usuarios.Cliente;
import usuarios.Gestor;

/**
 * Subpanel de estadísticas para el gestor.
 * Muestra tops de clientes, ingresos por mes, por año y por rango.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelEstadisticasGestor extends AbstractPanelGestor {

    private ControladorEstadisticasGestor controlador;

    // Spinners para consulta por año
    private JSpinner spinnerAño;

    // Spinners para consulta por rango
    private JSpinner spinnerRangoInicio;
    private JSpinner spinnerRangoFin;

    // Panel donde se muestran los ingresos por año (se actualiza dinámicamente)
    private JPanel panelMesesAño;

    // Panel donde se muestra el resultado del rango
    private JLabel labelResultadoRango;

    // Botones
    private JButton botonConsultarAño;
    private JButton botonConsultarRango;

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

        // 1. Ingresos totales (ventas + tasaciones)
        panelContenido.add(crearSeccionIngresos());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // 2. Ingresos por mes año actual
        panelContenido.add(crearSeccionMesesActual());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // 3. Ingresos por mes de un año concreto
        panelContenido.add(crearSeccionMesesAño());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // 4. Ingresos en rango de fechas
        panelContenido.add(crearSeccionRango());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // 5. Top clientes compras
        panelContenido.add(crearSeccionTop(
            "Top clientes por compras",
            controlador.getTopCompras(), "compras"));
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // 6. Top clientes intercambios
        panelContenido.add(crearSeccionTop(
            "Top clientes por intercambios",
            controlador.getTopIntercambios(), "intercambios"));
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // 7. Top clientes pedidos cancelados
        panelContenido.add(crearSeccionTop(
            "Top clientes por pedidos cancelados",
            controlador.getTopCancelados(), "cancelados"));

        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
        add(scroll, BorderLayout.CENTER);

        // Registramos el controlador en los botones
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
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

        // Cabecera con spinner de año y botón
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

        // Panel de meses — se rellena al pulsar el botón
        panelMesesAño = new JPanel(new GridLayout(
            3, 4, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));
        panelMesesAño.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelMesesAño.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), 0, 0, 0));
        rellenarPanelMeses(panelMesesAño, new double[12]);
        panel.add(panelMesesAño, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearSeccionRango() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

        JLabel titulo = new JLabel("Ingresos en rango de fechas:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelControles = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(8)));
        panelControles.setBackground(VentanaPrincipal.COLOR_TARJETA);

        panelControles.add(crearLabel("Desde:"));
        // Usamos SpinnerDateModel para elegir fechas
        spinnerRangoInicio = new JSpinner(new SpinnerDateModel());
        spinnerRangoInicio.setEditor(new JSpinner.DateEditor(spinnerRangoInicio, "dd/MM/yyyy"));
        spinnerRangoInicio.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(120), VentanaPrincipal.escalar(30)));
        panelControles.add(spinnerRangoInicio);

        panelControles.add(crearLabel("Hasta:"));
        spinnerRangoFin = new JSpinner(new SpinnerDateModel());
        spinnerRangoFin.setEditor(new JSpinner.DateEditor(spinnerRangoFin, "dd/MM/yyyy"));
        spinnerRangoFin.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(120), VentanaPrincipal.escalar(30)));
        panelControles.add(spinnerRangoFin);

        botonConsultarRango = crearBotonNaranja("Consultar");
        botonConsultarRango.setActionCommand("consultarRango");
        panelControles.add(botonConsultarRango);

        panel.add(panelControles, BorderLayout.CENTER);

        labelResultadoRango = crearLabel("Introduce un rango y pulsa Consultar.");
        labelResultadoRango.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(5), 0, 0, 0));
        panel.add(labelResultadoRango, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearSeccionTop(String titulo,
                                    List<Cliente> clientes, String tipo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

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
                    // cancelados
                    long cancelados = c.getHistorialPedidos().stream()
                        .filter(p -> p.getEstado() ==
                            ventas.EstadoPedido.CANCELADO)
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
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

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
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
                VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20))));

        JLabel labelTitulo = crearLabel(titulo);
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelTitulo, BorderLayout.NORTH);

        JLabel labelValor = new JLabel(valor, SwingConstants.CENTER);
        labelValor.setFont(VentanaPrincipal.FUENTE_TITULO);
        labelValor.setForeground(VentanaPrincipal.COLOR_ACENTO);
        panel.add(labelValor, BorderLayout.CENTER);

        return panel;
    }

    // ── Métodos que llama el controlador ──────────────────────────────────

    public void procesarConsultarAño() {
        int año = (int) spinnerAño.getValue();
        double[] ingresos = controlador.getIngresosPorAño(año);
        rellenarPanelMeses(panelMesesAño, ingresos);
    }

    public void procesarConsultarRango() {
        java.util.Date fechaInicio = (java.util.Date) spinnerRangoInicio.getValue();
        java.util.Date fechaFin    = (java.util.Date) spinnerRangoFin.getValue();

        LocalDate inicio = fechaInicio.toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate fin = fechaFin.toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        if (fin.isBefore(inicio)) {
            mostrarError("La fecha de fin debe ser posterior a la de inicio.");
            return;
        }

        double totalRango = controlador.getIngresosRango(inicio, fin);
        labelResultadoRango.setText(
            "Ingresos del " + inicio + " al " + fin
            + ":  " + String.format("%.2f€", totalRango));
    }
}