package Gui;

import Gui.Controladores.ControladorEstadisticasGestor;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import usuarios.Cliente;
import usuarios.Gestor;

/**
 * Subpanel de estadísticas para el gestor.
 * Muestra recaudación, actividad de usuarios e ingresos por mes.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelEstadisticasGestor extends JPanel {

    private VentanaPrincipal ventana;
    private Gestor gestor;
    private ControladorEstadisticasGestor controlador;

    /**
     * Constructor del subpanel de estadísticas.
     *
     * @param ventana La ventana principal
     * @param gestor  El gestor logueado
     */
    public SubpanelEstadisticasGestor(VentanaPrincipal ventana, Gestor gestor) {
        this.ventana = ventana;
        this.gestor = gestor;
        this.controlador = new ControladorEstadisticasGestor(gestor);
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    /**
     * Construye la interfaz con todas las estadísticas disponibles.
     */
    private void inicializarUI() {
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

        // Ingresos totales
        panelContenido.add(crearSeccionIngresos());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // Ingresos por mes
        panelContenido.add(crearSeccionMeses());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // Top compras
        panelContenido.add(crearSeccionTopCompras());
        panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // Top intercambios
        panelContenido.add(crearSeccionTopIntercambios());

        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Crea la sección de ingresos totales por ventas y tasaciones.
     *
     * @return Panel con la sección de ingresos
     */
    private JPanel crearSeccionIngresos() {
        JPanel panel = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(15), 0));
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);

        // Tarjeta ventas
        JPanel tarjetaVentas = crearTarjetaEstadistica(
            "Ingresos por ventas",
            String.format("%.2f€", controlador.getIngresosVentas())
        );
        panel.add(tarjetaVentas);

        // Tarjeta tasaciones
        JPanel tarjetaTasaciones = crearTarjetaEstadistica(
            "Ingresos por tasaciones",
            String.format("%.2f€", controlador.getIngresosTasaciones())
        );
        panel.add(tarjetaTasaciones);

        return panel;
    }

    /**
     * Crea la sección de ingresos desglosados por mes del año actual.
     *
     * @return Panel con la sección de meses
     */
    private JPanel crearSeccionMeses() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))
        ));

        JLabel titulo = new JLabel("Recaudación por mes (año actual)");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        double[] ingresos = controlador.getIngresosPorMeses();

        JPanel panelMeses = new JPanel(new GridLayout(3, 4, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));
        panelMeses.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelMeses.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), 0, 0, 0));

        for (int i = 0; i < 12; i++) {
            JPanel tarjeta = new JPanel(new BorderLayout());
            tarjeta.setBackground(VentanaPrincipal.COLOR_FONDO);
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
                BorderFactory.createEmptyBorder(
                    VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8),
                    VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8))
            ));

            JLabel labelMes = new JLabel(meses[i], SwingConstants.CENTER);
            labelMes.setFont(VentanaPrincipal.FUENTE_PEQUENA);
            labelMes.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            tarjeta.add(labelMes, BorderLayout.NORTH);

            JLabel labelValor = new JLabel(String.format("%.2f€", ingresos[i]), SwingConstants.CENTER);
            labelValor.setFont(VentanaPrincipal.FUENTE_BOTON);
            labelValor.setForeground(ingresos[i] > 0 ? VentanaPrincipal.COLOR_ACENTO : VentanaPrincipal.COLOR_TEXTO2);
            tarjeta.add(labelValor, BorderLayout.CENTER);

            panelMeses.add(tarjeta);
        }

        panel.add(panelMeses, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea la sección con el ranking de clientes por compras.
     *
     * @return Panel con la sección de top compras
     */
    private JPanel crearSeccionTopCompras() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))
        ));

        JLabel titulo = new JLabel("Top clientes por compras");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelClientes = new JPanel();
        panelClientes.setLayout(new BoxLayout(panelClientes, BoxLayout.Y_AXIS));
        panelClientes.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelClientes.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), 0, 0, 0));

        List<Cliente> clientes = controlador.getTopCompras();
        if (clientes.isEmpty()) {
            JLabel labelVacio = new JLabel("No hay datos de compras.");
            labelVacio.setFont(VentanaPrincipal.FUENTE_NORMAL);
            labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            panelClientes.add(labelVacio);
        } else {
            int pos = 1;
            for (Cliente c : clientes) {
                JLabel label = new JLabel(pos + ". " + c.getNickname() + " — " + c.contarPedidosCompletados() + " pedidos");
                label.setFont(VentanaPrincipal.FUENTE_NORMAL);
                label.setForeground(pos == 1 ? VentanaPrincipal.COLOR_ACENTO : VentanaPrincipal.COLOR_TEXTO);
                panelClientes.add(label);
                pos++;
            }
        }

        panel.add(panelClientes, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea la sección con el ranking de clientes por intercambios.
     *
     * @return Panel con la sección de top intercambios
     */
    private JPanel crearSeccionTopIntercambios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))
        ));

        JLabel titulo = new JLabel("Top clientes por intercambios");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelClientes = new JPanel();
        panelClientes.setLayout(new BoxLayout(panelClientes, BoxLayout.Y_AXIS));
        panelClientes.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelClientes.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), 0, 0, 0));

        List<Cliente> clientes = controlador.getTopIntercambios();
        if (clientes.isEmpty()) {
            JLabel labelVacio = new JLabel("No hay datos de intercambios.");
            labelVacio.setFont(VentanaPrincipal.FUENTE_NORMAL);
            labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            panelClientes.add(labelVacio);
        } else {
            int pos = 1;
            for (Cliente c : clientes) {
                JLabel label = new JLabel(pos + ". " + c.getNickname() + " — " + c.contarIntercambios() + " intercambios");
                label.setFont(VentanaPrincipal.FUENTE_NORMAL);
                label.setForeground(pos == 1 ? VentanaPrincipal.COLOR_ACENTO : VentanaPrincipal.COLOR_TEXTO);
                panelClientes.add(label);
                pos++;
            }
        }

        panel.add(panelClientes, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea una tarjeta visual con un título y un valor destacado.
     *
     * @param titulo Título de la tarjeta
     * @param valor  Valor a mostrar destacado
     * @return Panel con la tarjeta
     */
    private JPanel crearTarjetaEstadistica(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
                VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20))
        ));

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panel.add(labelTitulo, BorderLayout.NORTH);

        JLabel labelValor = new JLabel(valor, SwingConstants.CENTER);
        labelValor.setFont(VentanaPrincipal.FUENTE_TITULO);
        labelValor.setForeground(VentanaPrincipal.COLOR_ACENTO);
        panel.add(labelValor, BorderLayout.CENTER);

        return panel;
    }
}