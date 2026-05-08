package Gui.Gestor;

import Gui.AbstractPanelSection;
import Gui.VentanaPrincipal;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Gestor;

/**
 * Panel principal del gestor en CheckPoint.
 * Extiende AbstractPanelSection para reutilizar helpers visuales.
 *
 * @author Antonino
 * @version 1.0
 */
public class PanelGestor extends AbstractPanelSection {

    private static final String SEC_EMPLEADOS            = "EMPLEADOS";
    private static final String SEC_CATEGORIAS           = "CATEGORIAS";
    private static final String SEC_PRODUCTOS_DESCUENTOS = "PRODUCTOS_DESCUENTOS";
    private static final String SEC_ESTADISTICAS         = "ESTADISTICAS";
    private static final String SEC_CONFIGURACION        = "CONFIGURACION";
    private static final String SEC_PERFIL               = "PERFIL";

    private Gestor gestor;
    private CardLayout cardSecciones;
    private JPanel panelSecciones;
    private JLabel labelGestor;
    private JButton botonActivo;

    private SubpanelEmpleadosGestor subpanelEmpleados;
    private SubpanelCategoriasGestor subpanelCategorias;
    private SubpanelProductosDescuentosGestor subpanelProductosDescuentos;
    private SubpanelEstadisticasGestor subpanelEstadisticas;
    private SubpanelConfiguracionGestor subpanelConfiguracion;
    private SubpanelPerfilGestor subpanelPerfil;

    public PanelGestor(VentanaPrincipal ventana) {
        super(ventana);
    }

    public void actualizarGestor(Gestor gestor) {
        this.gestor = gestor;
        removeAll();
        inicializarUI();
        revalidate();
        repaint();
    }

    private void inicializarUI() {
        add(crearBarraNavegacion(), BorderLayout.NORTH);

        cardSecciones = new CardLayout();
        panelSecciones = new JPanel(cardSecciones);
        panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

        subpanelEmpleados           = new SubpanelEmpleadosGestor(ventana, gestor);
        subpanelCategorias          = new SubpanelCategoriasGestor(ventana, gestor);
        subpanelProductosDescuentos = new SubpanelProductosDescuentosGestor(ventana, gestor);
        subpanelEstadisticas        = new SubpanelEstadisticasGestor(ventana, gestor);
        subpanelConfiguracion       = new SubpanelConfiguracionGestor(ventana, gestor);
        subpanelPerfil              = new SubpanelPerfilGestor(ventana, gestor);

        panelSecciones.add(subpanelEmpleados,           SEC_EMPLEADOS);
        panelSecciones.add(subpanelCategorias,          SEC_CATEGORIAS);
        panelSecciones.add(subpanelProductosDescuentos, SEC_PRODUCTOS_DESCUENTOS);
        panelSecciones.add(subpanelEstadisticas,        SEC_ESTADISTICAS);
        panelSecciones.add(subpanelConfiguracion,       SEC_CONFIGURACION);
        panelSecciones.add(subpanelPerfil,              SEC_PERFIL);

        add(panelSecciones, BorderLayout.CENTER);
        cardSecciones.show(panelSecciones, SEC_EMPLEADOS);
    }

    private JPanel crearBarraNavegacion() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);
        barra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
            BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15),
                0, VentanaPrincipal.escalar(15))));
        barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));

        // crearLabel() de AbstractPanelSection
        JLabel labelLogo = new JLabel("🎮 CheckPoint - Gestor");
        labelLogo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
        barra.add(labelLogo, BorderLayout.WEST);

        JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panelPestanas.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelPestanas.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(8), 0, 0, 0));

        botonActivo = null;

        String[][] pestanas = {
            {"Empleados",              SEC_EMPLEADOS},
            {"Categorías",             SEC_CATEGORIAS},
            {"Productos y Descuentos", SEC_PRODUCTOS_DESCUENTOS},
            {"Estadísticas",           SEC_ESTADISTICAS},
            {"Configuración",          SEC_CONFIGURACION},
            {"Mi Perfil",              SEC_PERFIL}
        };

        for (String[] pestana : pestanas) {
            agregarPestana(panelPestanas, pestana[0], pestana[1]);
        }

        barra.add(panelPestanas, BorderLayout.CENTER);

        JPanel panelDerecha = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(10), 0));
        panelDerecha.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelDerecha.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(12), 0, 0, 0));

        labelGestor = new JLabel("👤 "
            + (gestor != null ? gestor.getNickname() : "Gestor"));
        labelGestor.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelGestor.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelDerecha.add(labelGestor);

        // crearBotonRojo() de AbstractPanelSection
        JButton botonLogout = crearBotonRojo("🚪 Salir");
        botonLogout.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        botonLogout.addActionListener(e -> ventana.logout());
        panelDerecha.add(botonLogout);

        barra.add(panelDerecha, BorderLayout.EAST);
        return barra;
    }

    private void agregarPestana(JPanel panel, String texto, String seccion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN,
            VentanaPrincipal.escalar(13)));
        boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        boton.setBackground(VentanaPrincipal.COLOR_PANEL);
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
            VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12)));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (boton != botonActivo)
                    boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (boton != botonActivo)
                    boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            }
        });

        boton.addActionListener(e -> {
            activarPestana(boton);
            cardSecciones.show(panelSecciones, seccion);
        });

        if (botonActivo == null) {
            botonActivo = boton;
            marcarActivo(boton);
        }

        panel.add(boton);
    }

    private void marcarActivo(JButton boton) {
        boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12))));
    }

    private void activarPestana(JButton boton) {
        if (botonActivo != null) {
            botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            botonActivo.setBorder(BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12)));
        }
        botonActivo = boton;
        marcarActivo(boton);
    }
}