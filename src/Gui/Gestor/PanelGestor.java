package Gui.Gestor;

import javax.swing.*;
import javax.swing.border.*;

import Gui.VentanaPrincipal;

import java.awt.*;
import java.awt.event.*;
import usuarios.Gestor;

/**
 * Panel principal del gestor en CheckPoint.
 * Contiene una barra de navegación superior con todas las secciones
 * disponibles para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class PanelGestor extends JPanel {

    private static final String SEC_EMPLEADOS = "EMPLEADOS";
    private static final String SEC_CATEGORIAS = "CATEGORIAS";
    private static final String SEC_PRODUCTOS_DESCUENTOS = "PRODUCTOS_DESCUENTOS";
    private static final String SEC_PACKS = "PACKS";
    private static final String SEC_ESTADISTICAS = "ESTADISTICAS";
    private static final String SEC_CONFIGURACION = "CONFIGURACION";
    private static final String SEC_PERFIL = "PERFIL";

    private VentanaPrincipal ventana;
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

    /**
     * Constructor del panel gestor.
     *
     * @param ventana La ventana principal
     */
    public PanelGestor(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
    }

    /**
     * Actualiza el panel con el gestor logueado y reconstruye la interfaz.
     *
     * @param gestor El gestor que ha iniciado sesión
     */
    public void actualizarGestor(Gestor gestor) {
        this.gestor = gestor;
        removeAll();
        inicializarUI();
        revalidate();
        repaint();
    }

    /**
     * Construye la barra de navegación y todas las secciones del panel.
     */
    private void inicializarUI() {
        add(crearBarraNavegacion(), BorderLayout.NORTH);

        cardSecciones = new CardLayout();
        panelSecciones = new JPanel(cardSecciones);
        panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

        subpanelEmpleados = new SubpanelEmpleadosGestor(ventana, gestor);
        subpanelCategorias = new SubpanelCategoriasGestor(ventana, gestor);
        subpanelProductosDescuentos = new SubpanelProductosDescuentosGestor(ventana, gestor);
 
        subpanelEstadisticas = new SubpanelEstadisticasGestor(ventana, gestor);
        subpanelConfiguracion = new SubpanelConfiguracionGestor(ventana, gestor);
        subpanelPerfil = new SubpanelPerfilGestor(ventana, gestor);

        panelSecciones.add(subpanelEmpleados, SEC_EMPLEADOS);
        panelSecciones.add(subpanelCategorias, SEC_CATEGORIAS);
        panelSecciones.add(subpanelProductosDescuentos, SEC_PRODUCTOS_DESCUENTOS);

        panelSecciones.add(subpanelEstadisticas, SEC_ESTADISTICAS);
        panelSecciones.add(subpanelConfiguracion, SEC_CONFIGURACION);
        panelSecciones.add(subpanelPerfil, SEC_PERFIL);

        add(panelSecciones, BorderLayout.CENTER);
        cardSecciones.show(panelSecciones, SEC_EMPLEADOS);
    }

    /**
     * Crea la barra superior de navegación con las pestañas del gestor.
     *
     * @return El panel de la barra de navegación
     */
    private JPanel crearBarraNavegacion() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);
        barra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
            BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, VentanaPrincipal.escalar(15))
        ));
        barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));

        JLabel labelLogo = new JLabel("🎮 CheckPoint - Gestor");
        labelLogo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
        barra.add(labelLogo, BorderLayout.WEST);

        JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panelPestanas.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelPestanas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));

        botonActivo = null;

        String[][] pestanas = {
            {"Empleados", SEC_EMPLEADOS},
            {"Categorias", SEC_CATEGORIAS},
            {"Productos y Descuentos", SEC_PRODUCTOS_DESCUENTOS},
            {"Packs", SEC_PACKS},
            {"Estadisticas", SEC_ESTADISTICAS},
            {"Configuracion", SEC_CONFIGURACION},
            {"Mi Perfil", SEC_PERFIL}
        };

        for (String[] pestana : pestanas) {
            agregarPestana(panelPestanas, pestana[0], pestana[1]);
        }

        barra.add(panelPestanas, BorderLayout.CENTER);

        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(10), 0));
        panelDerecha.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelDerecha.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), 0, 0, 0));

        labelGestor = new JLabel("👤 " + (gestor != null ? gestor.getNickname() : "Gestor"));
        labelGestor.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelGestor.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelDerecha.add(labelGestor);

        JButton botonLogout = new JButton("🚪 Salir");
        botonLogout.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        botonLogout.setForeground(new Color(220, 80, 80));
        botonLogout.setBackground(VentanaPrincipal.COLOR_PANEL);
        botonLogout.setBorderPainted(false);
        botonLogout.setFocusPainted(false);
        botonLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonLogout.addActionListener(e -> ventana.logout());
        panelDerecha.add(botonLogout);

        barra.add(panelDerecha, BorderLayout.EAST);
        return barra;
    }

    /**
     * Crea y añade un botón de pestaña a la barra de navegación.
     *
     * @param panel   Panel donde añadir la pestaña
     * @param texto   Texto del botón
     * @param seccion Identificador de la sección
     */
    private void agregarPestana(JPanel panel, String texto, String seccion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
        boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        boton.setBackground(VentanaPrincipal.COLOR_PANEL);
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
            VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (boton != botonActivo) boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (boton != botonActivo) boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
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

    /**
     * Aplica el estilo visual de activo a un botón de pestaña.
     *
     * @param boton El botón a marcar como activo
     */
    private void marcarActivo(JButton boton) {
        boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12)
            )
        ));
    }

    /**
     * Cambia el botón activo al nuevo botón seleccionado.
     *
     * @param boton El nuevo botón activo
     */
    private void activarPestana(JButton boton) {
        if (botonActivo != null) {
            botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            botonActivo.setBorder(BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12)
            ));
        }
        botonActivo = boton;
        marcarActivo(boton);
    }
}