package Gui.cliente;

import Gui.VentanaPrincipal;

import Gui.Controladores.cliente.*;
import productos.Producto2Mano;
import usuarios.Cliente;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Subpanel de detalle de un producto de segunda mano.
 * Mismo estilo que SubpanelProducto pero para productos de segunda mano.
 * Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelProducto2Mano extends JPanel {

    private VentanaPrincipal ventana;
    private SubpanelSegundaMano subpanelOrigen;
    private ControladorProducto2Mano controlador;
    private SubpanelCrearOferta subpanelCrearOferta;
    private CardLayout cardLayout;
    private JPanel panelContenido;

    // Botones — atributos para registrar el controlador
    private JButton botonVolver;
    private JButton botonOfertar;

    /**
     * Constructor del subpanel de detalle de producto de segunda mano.
     *
     * @param ventana        La ventana principal
     * @param subpanelOrigen El subpanel de segunda mano para volver
     */
    public SubpanelProducto2Mano(VentanaPrincipal ventana,
                                   SubpanelSegundaMano subpanelOrigen) {
        this.ventana = ventana;
        this.subpanelOrigen = subpanelOrigen;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        add(panelContenido, BorderLayout.CENTER);
    }

    /**
     * Carga el producto y construye la interfaz.
     * Crea el controlador y lo registra en los botones — patrón de los apuntes.
     *
     * @param producto El producto a mostrar
     * @param cliente  El cliente logueado o null
     */
    public void mostrarProducto(Producto2Mano producto, Cliente cliente) {
        this.controlador = new ControladorProducto2Mano(this, producto, cliente);

        // Creamos el subpanel de crear oferta
        subpanelCrearOferta = new SubpanelCrearOferta(ventana, this, producto, cliente);

        panelContenido.removeAll();
        panelContenido.add(crearPanelDetalle(producto), "DETALLE");
        panelContenido.add(subpanelCrearOferta, "OFERTA");

        // Registramos el controlador en los botones
        setControlador(controlador);
        cardLayout.show(panelContenido, "DETALLE");
        revalidate();
        repaint();
    }

    /**
     * Registra el controlador en los botones — patrón de los apuntes.
     *
     * @param c El controlador a registrar
     */
    public void setControlador(ActionListener c) {
        if (botonVolver != null) {
            for (ActionListener al : botonVolver.getActionListeners())
                botonVolver.removeActionListener(al);
            botonVolver.addActionListener(c);
        }
        if (botonOfertar != null) {
            for (ActionListener al : botonOfertar.getActionListeners())
                botonOfertar.removeActionListener(al);
            botonOfertar.addActionListener(c);
        }
    }

    /**
     * Crea el panel de detalle del producto con imagen e información.
     *
     * @param producto El producto a mostrar
     * @return Panel con el detalle
     */
    private JPanel crearPanelDetalle(Producto2Mano producto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);

        // Barra superior con botón volver
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);
        barra.setBorder(BorderFactory.createMatteBorder(
            0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

        botonVolver = new JButton("← Volver a segunda mano");
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
        panel.add(barra, BorderLayout.NORTH);

        // Panel central con imagen e info
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

        // Imagen
        JLabel labelImagen = new JLabel();
        labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
        labelImagen.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(300), VentanaPrincipal.escalar(300)));
        cargarImagen(labelImagen, producto.getImagenRuta(),
            VentanaPrincipal.escalar(280), VentanaPrincipal.escalar(280));
        panelCentral.add(labelImagen, BorderLayout.WEST);

        // Info derecha
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(VentanaPrincipal.COLOR_FONDO);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(
            0, VentanaPrincipal.escalar(30), 0, 0));

        // Nombre
        JLabel labelNombre = new JLabel(producto.getNombre());
        labelNombre.setFont(VentanaPrincipal.FUENTE_TITULO);
        labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panelInfo.add(labelNombre);
        panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));

        // Propietario
        JLabel labelPropietario = new JLabel(
            "Propietario: " + producto.getPropietario().getNickname());
        labelPropietario.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelPropietario.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelInfo.add(labelPropietario);
        panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        // Descripción
        JLabel labelDescTitulo = new JLabel("Descripción:");
        labelDescTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelDescTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panelInfo.add(labelDescTitulo);

        JLabel labelDesc = new JLabel(
            "<html><p style='width:400px'>"
            + producto.getDescripcion() + "</p></html>");
        labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelInfo.add(labelDesc);
        panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        // Precio y estado si está valorado
        if (producto.getValoracion() != null) {
            JLabel labelPrecio = new JLabel(String.format(
                "Precio tasado: %.2f€",
                producto.getValoracion().getPrecioTasacion()));
            labelPrecio.setFont(new Font("Segoe UI", Font.BOLD,
                VentanaPrincipal.escalar(22)));
            labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
            panelInfo.add(labelPrecio);
            panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

            JLabel labelEstado = new JLabel(
                "Estado: " + producto.getValoracion().getEstadoProducto().toString());
            labelEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
            labelEstado.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            panelInfo.add(labelEstado);
            panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
        }

        // Botón ofertar — solo si puede ofertar
        if (controlador.puedeOfertar()) {
            botonOfertar = new JButton("Hacer oferta");
            botonOfertar.setActionCommand("ofertar");
            botonOfertar.setFont(VentanaPrincipal.FUENTE_BOTON);
            botonOfertar.setBackground(VentanaPrincipal.COLOR_ACENTO);
            botonOfertar.setForeground(Color.WHITE);
            botonOfertar.setOpaque(true);
            botonOfertar.setBorderPainted(false);
            botonOfertar.setFocusPainted(false);
            botonOfertar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botonOfertar.setBorder(BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));
            botonOfertar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    botonOfertar.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    botonOfertar.setBackground(VentanaPrincipal.COLOR_ACENTO);
                }
            });
            panelInfo.add(botonOfertar);
        }

        panelCentral.add(panelInfo, BorderLayout.CENTER);
        panel.add(panelCentral, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Vuelve a segunda mano. Lo llama el controlador.
     */
    public void volver() {
        subpanelOrigen.volverDelProducto2Mano();
    }

    /**
     * Navega al subpanel de crear oferta. Lo llama el controlador.
     */
    public void navegarACrearOferta() {
        cardLayout.show(panelContenido, "OFERTA");
    }

    /**
     * Vuelve al detalle del producto desde crear oferta.
     */
    public void volverAlDetalle() {
        cardLayout.show(panelContenido, "DETALLE");
    }

    private void cargarImagen(JLabel label, String nombre, int ancho, int alto) {
        try {
            URL url = getClass().getResource("/fotos/" + nombre);
            if (url != null) {
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    Image imgEscalada = img.getScaledInstance(
                        ancho, alto, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(imgEscalada));
                } else {
                    label.setText("Sin imagen");
                    label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
                }
            } else {
                label.setText("Sin imagen");
                label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            }
        } catch (IOException e) {
            label.setText("Sin imagen");
            label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        }
    }
}