package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorNotificacionesEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import tienda.Notificacion;
import usuarios.Empleado;

/** Pantalla de notificaciones del empleado. */
public class SeccionNotificacionesEmpleado extends AbstractPanelEmpleadoSection {

    private static final long serialVersionUID = 1L;

    private ControladorNotificacionesEmpleado controlador;
    private DefaultListModel<String> modeloNotificaciones;
    private JList<String> listaNotificaciones;
    private List<Notificacion> notificacionesMostradas;
    private JComboBox<String> comboFiltro;
    private JLabel labelResumen;

    public SeccionNotificacionesEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        this.controlador = new ControladorNotificacionesEmpleado(empleado);
        this.controlador.setVista(this);
        setControlador(this.controlador);
        construirUI();
    }

    public void setControlador(ActionListener controlador) {
        if (controlador instanceof ControladorNotificacionesEmpleado) {
            this.controlador = (ControladorNotificacionesEmpleado) controlador;
        }
    }

    private void conectar(JButton boton, String accion) {
        boton.setActionCommand(accion);
        boton.addActionListener(controlador);
    }

    private void construirUI() {
        setLayout(new BorderLayout());

        JPanel panelBase = crearPanelBase("Notificaciones");
        JPanel contenido = getContenido(panelBase);

        contenido.add(crearBloqueBandeja());
        contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
        contenido.add(crearBloqueAcciones());

        add(panelBase, BorderLayout.CENTER);
    }

    private JPanel crearBloqueBandeja() {
        JPanel bloque = crearBloque("Bandeja de notificaciones");

        notificacionesMostradas = new ArrayList<>();

        modeloNotificaciones = new DefaultListModel<>();
        listaNotificaciones = new JList<>(modeloNotificaciones);
        estilizarLista();

        comboFiltro = crearCombo(new String[] { "Todas", "No vistas", "Vistas" });
        comboFiltro.setActionCommand(ControladorNotificacionesEmpleado.FILTRAR);
        comboFiltro.addActionListener(controlador);

        JButton botonRefrescar = crearBotonSecundario("Refrescar");
        conectar(botonRefrescar, ControladorNotificacionesEmpleado.REFRESCAR);

        labelResumen = crearLabel("");

        JPanel filaFiltro = new JPanel(new BorderLayout(VentanaPrincipal.escalar(12), 0));
        filaFiltro.setOpaque(false);

        JPanel zonaFiltro = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
        zonaFiltro.setOpaque(false);
        zonaFiltro.add(crearLabel("Mostrar"), BorderLayout.NORTH);
        zonaFiltro.add(comboFiltro, BorderLayout.CENTER);

        JPanel zonaBoton = new JPanel(new BorderLayout());
        zonaBoton.setOpaque(false);
        zonaBoton.add(botonRefrescar, BorderLayout.SOUTH);

        filaFiltro.add(zonaFiltro, BorderLayout.CENTER);
        filaFiltro.add(zonaBoton, BorderLayout.EAST);

        JScrollPane scrollLista = estilizarScroll(listaNotificaciones);
        scrollLista.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(330)));

        bloque.add(labelResumen, gbcCampo(1));
        bloque.add(filaFiltro, gbcCampo(2));
        bloque.add(scrollLista, gbcCampo(3));

        cargarNotificaciones();

        return bloque;
    }

    private JPanel crearBloqueAcciones() {
        JPanel bloque = crearBloque("Consultar notificacion");

        JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
        panelAcciones.setOpaque(false);

        panelAcciones.add(crearPanelAyuda());
        panelAcciones.add(crearPanelBotones());

        bloque.add(panelAcciones, gbcCampo(1));

        return bloque;
    }

    private JPanel crearPanelAyuda() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo = crearLabel("Como funciona");
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        panel.add(crearLabel("Selecciona una notificacion de la bandeja."));
        panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
        panel.add(crearLabel("Al abrirla, pasa automaticamente a vista."));

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(crearLabel("Acciones"));
        panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        JButton botonVer = crearBotonAccion("Ver notificacion");
        JButton botonMarcarTodas = crearBotonSecundario("Marcar todas vistas");

        JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(10), 0));
        filaBotones.setOpaque(false);

        filaBotones.add(botonVer);
        filaBotones.add(botonMarcarTodas);

        Dimension tamanoFila = new Dimension(VentanaPrincipal.escalar(460), VentanaPrincipal.escalar(42));
        filaBotones.setPreferredSize(tamanoFila);
        filaBotones.setMaximumSize(tamanoFila);

        panel.add(filaBotones);

        conectar(botonVer, ControladorNotificacionesEmpleado.VER_NOTIFICACION);
        conectar(botonMarcarTodas, ControladorNotificacionesEmpleado.MARCAR_TODAS);

        return panel;
    }

    public void cargarNotificaciones() {
        modeloNotificaciones.clear();
        notificacionesMostradas.clear();

        if (!controlador.tieneNotificaciones()) {
            labelResumen.setText("No tienes notificaciones.");
            listaNotificaciones.clearSelection();
            return;
        }

        List<Notificacion> notificaciones = controlador.getNotificacionesFiltradas(obtenerFiltroActual());

        for (Notificacion notificacion : notificaciones) {
            notificacionesMostradas.add(notificacion);
            modeloNotificaciones.addElement(controlador.crearTextoLista(notificacion));
        }

        actualizarResumen(notificaciones.size(), controlador.contarNoVistas());
        listaNotificaciones.clearSelection();
    }

    private String obtenerFiltroActual() {
        if (comboFiltro == null || comboFiltro.getSelectedItem() == null) {
            return "Todas";
        }
        return String.valueOf(comboFiltro.getSelectedItem());
    }

    private void actualizarResumen(int totalMostradas, int noVistas) {
        if (totalMostradas == 0) {
            labelResumen.setText("No hay notificaciones para mostrar con este filtro.");
            return;
        }
        labelResumen.setText("Mostrando " + totalMostradas + " notificaciones. No vistas: " + noVistas + ".");
    }

    public void verNotificacionSeleccionada() {
        int posicion = listaNotificaciones.getSelectedIndex();

        if (posicion < 0) {
            mostrarError("Selecciona una notificacion de la bandeja.");
            return;
        }

        Notificacion notificacion = notificacionesMostradas.get(posicion);

        if (notificacion == null) {
            mostrarError("No se pudo abrir la notificacion.");
            return;
        }

        controlador.marcarComoVista(notificacion);
        mostrarNotificacionEnVentana(notificacion);
        cargarNotificaciones();
    }

    public void marcarTodasComoVistas() {
        ResultadoOperacion resultado = controlador.marcarTodasComoVistas();
        if (!resultado.isExito()) {
            mostrarError(resultado.getMensaje());
            return;
        }

        cargarNotificaciones();
        mostrarMensaje(resultado.getMensaje());
    }

    private void mostrarNotificacionEnVentana(Notificacion notificacion) {
        JTextArea areaNotificacion = crearArea();
        areaNotificacion.setEditable(false);
        areaNotificacion.setText(controlador.crearTextoNotificacion(notificacion));
        areaNotificacion.setCaretPosition(0);

        JScrollPane scroll = estilizarScroll(areaNotificacion);
        scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(640), VentanaPrincipal.escalar(260)));

        JOptionPane.showMessageDialog(this, scroll, "Notificacion", JOptionPane.INFORMATION_MESSAGE);
    }

    private void estilizarLista() {
        listaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaNotificaciones.setFixedCellHeight(VentanaPrincipal.escalar(34));

        listaNotificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaNotificaciones.setBackground(Color.WHITE);
        listaNotificaciones.setForeground(Color.BLACK);

        listaNotificaciones.setSelectionBackground(new Color(235, 235, 235));
        listaNotificaciones.setSelectionForeground(Color.BLACK);
    }
}
