package Gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import usuarios.Empleado;
import usuarios.TipoPermisos;

/**
 * Panel principal del empleado.
 *
 * Esta versión solo se encarga de:
 * - construir la navegación
 * - montar las secciones en un CardLayout
 * - delegar cada bloque funcional a una clase distinta
 */
public class PanelEmpleado extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String SEC_STOCK = "STOCK";
    public static final String SEC_CATEGORIAS = "CATEGORIAS";
    public static final String SEC_PACKS = "PACKS";
    public static final String SEC_MODIFICAR = "MODIFICAR";
    public static final String SEC_PEDIDOS = "PEDIDOS";
    public static final String SEC_ENTREGA = "ENTREGA";
    public static final String SEC_TASACION = "TASACION";
    public static final String SEC_INTERCAMBIOS = "INTERCAMBIOS";
    public static final String SEC_NOTIFICACIONES = "NOTIFICACIONES";

    private final VentanaPrincipal ventana;

    private Empleado empleado;
    private CardLayout cardSecciones;
    private JPanel panelSecciones;
    private JLabel labelEmpleado;
    private JButton botonActivo;

    public PanelEmpleado(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
    }

    public void actualizarEmpleado(Empleado empleado) {
        this.empleado = empleado;
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

        boolean primero = true;
        String primeraSeccion = null;

        if (empleado.tienePermiso(TipoPermisos.GESTION_STOCK)) {
            panelSecciones.add(new SeccionStockEmpleado(ventana, empleado), SEC_STOCK);
            if (primero) { primeraSeccion = SEC_STOCK; primero = false; }
        }
        if (empleado.tienePermiso(TipoPermisos.GESTION_CATEGORIAS)) {
            panelSecciones.add(new SeccionCategoriasEmpleado(ventana, empleado), SEC_CATEGORIAS);
            if (primero) { primeraSeccion = SEC_CATEGORIAS; primero = false; }
        }
        if (empleado.tienePermiso(TipoPermisos.GESTION_PACKS)) {
            panelSecciones.add(new SeccionPacksEmpleado(ventana, empleado), SEC_PACKS);
            if (primero) { primeraSeccion = SEC_PACKS; primero = false; }
        }
        if (empleado.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) {
            panelSecciones.add(new SeccionModificarEmpleado(ventana, empleado), SEC_MODIFICAR);
            if (primero) { primeraSeccion = SEC_MODIFICAR; primero = false; }
        }
        if (empleado.tienePermiso(TipoPermisos.GESTION_PEDIDOS)) {
            panelSecciones.add(new SeccionPedidosEmpleado(ventana, empleado), SEC_PEDIDOS);
            if (primero) { primeraSeccion = SEC_PEDIDOS; primero = false; }
        }
        if (empleado.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS)) {
            panelSecciones.add(new SeccionEntregasEmpleado(ventana, empleado), SEC_ENTREGA);
            if (primero) { primeraSeccion = SEC_ENTREGA; primero = false; }
        }
        if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
            panelSecciones.add(new SeccionTasacionEmpleado(ventana, empleado), SEC_TASACION);
            if (primero) { primeraSeccion = SEC_TASACION; primero = false; }
        }
        if (empleado.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
            panelSecciones.add(new SeccionIntercambiosEmpleado(ventana, empleado), SEC_INTERCAMBIOS);
            if (primero) { primeraSeccion = SEC_INTERCAMBIOS; primero = false; }
        }

        panelSecciones.add(new SeccionNotificacionesEmpleado(ventana, empleado), SEC_NOTIFICACIONES);
        if (primero) {
            primeraSeccion = SEC_NOTIFICACIONES;
        }

        add(panelSecciones, BorderLayout.CENTER);

        if (primeraSeccion != null) {
            cardSecciones.show(panelSecciones, primeraSeccion);
        }
    }

    private JPanel crearBarraNavegacion() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(VentanaPrincipal.COLOR_BARRA);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
                BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, VentanaPrincipal.escalar(15))));
        barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));

        JLabel labelLogo = new JLabel("🎮 CheckPoint - Empleado");
        labelLogo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
        barra.add(labelLogo, BorderLayout.WEST);

        JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panelPestanas.setBackground(VentanaPrincipal.COLOR_BARRA);
        panelPestanas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));

        botonActivo = null;

        if (empleado.tienePermiso(TipoPermisos.GESTION_STOCK)) agregarPestana(panelPestanas, "Stock", SEC_STOCK);
        if (empleado.tienePermiso(TipoPermisos.GESTION_CATEGORIAS)) agregarPestana(panelPestanas, "Categorías", SEC_CATEGORIAS);
        if (empleado.tienePermiso(TipoPermisos.GESTION_PACKS)) agregarPestana(panelPestanas, "Packs", SEC_PACKS);
        if (empleado.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) agregarPestana(panelPestanas, "Modificar", SEC_MODIFICAR);
        if (empleado.tienePermiso(TipoPermisos.GESTION_PEDIDOS)) agregarPestana(panelPestanas, "Pedidos", SEC_PEDIDOS);
        if (empleado.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS)) agregarPestana(panelPestanas, "Entregas", SEC_ENTREGA);
        if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) agregarPestana(panelPestanas, "Tasaciones", SEC_TASACION);
        if (empleado.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO)) agregarPestana(panelPestanas, "Intercambios", SEC_INTERCAMBIOS);
        agregarPestana(panelPestanas, "Notificaciones", SEC_NOTIFICACIONES);

        barra.add(panelPestanas, BorderLayout.CENTER);

        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(10), 0));
        panelDerecha.setBackground(VentanaPrincipal.COLOR_BARRA);
        panelDerecha.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), 0, 0, 0));

        labelEmpleado = new JLabel("👤 " + (empleado != null ? empleado.getNickname() : "Empleado"));
        labelEmpleado.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelEmpleado.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
        panelDerecha.add(labelEmpleado);

        JButton botonLogout = new JButton("🚪 Salir");
        botonLogout.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        botonLogout.setForeground(new Color(220, 80, 80));
        botonLogout.setBackground(VentanaPrincipal.COLOR_BARRA);
        botonLogout.setBorderPainted(false);
        botonLogout.setFocusPainted(false);
        botonLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonLogout.addActionListener(e -> ventana.logout());
        panelDerecha.add(botonLogout);

        barra.add(panelDerecha, BorderLayout.EAST);
        return barra;
    }

    private void agregarPestana(JPanel panel, String texto, String seccion) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
        boton.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
        boton.setBackground(VentanaPrincipal.COLOR_BARRA);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
                        VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (boton != botonActivo) boton.setBackground(VentanaPrincipal.COLOR_BARRA_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (boton != botonActivo) boton.setBackground(VentanaPrincipal.COLOR_BARRA);
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
        boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO, 1),
                BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
                        VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));
    }

    private void activarPestana(JButton boton) {
        if (botonActivo != null) {
            botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
            botonActivo.setBackground(VentanaPrincipal.COLOR_BARRA);
            botonActivo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                    BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
                            VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));
        }
        botonActivo = boton;
        marcarActivo(boton);
    }
}
