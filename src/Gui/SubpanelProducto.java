package Gui;

import Gui.Controladores.ControladorCatalogo;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import productos.ProductoVenta;
import productos.Reseña;
import usuarios.Cliente;

/**
 * Subpanel que muestra la información completa de un producto.
 * Se muestra cuando el usuario pulsa "Ver información" en el catálogo.
 * Contiene imagen grande, información del producto, botón de añadir
 * al carrito y las reseñas del producto.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelProducto extends JPanel {

    /** Referencia a la ventana principal */
    private VentanaPrincipal ventana;

    /** Referencia al subpanel catálogo para volver */
    private SubpanelCatalogo subpanelCatalogo;

    /** Controlador del catálogo para añadir al carrito */
    private ControladorCatalogo controlador;

    /** Producto que se está mostrando */
    private ProductoVenta producto;

    /** Cliente actualmente logueado */
    private Cliente cliente;

    /** Panel donde se muestran las reseñas */
    private JPanel panelReseñas;

    /**
     * Constructor del subpanel producto.
     *
     * @param ventana          La ventana principal
     * @param subpanelCatalogo El catálogo para poder volver
     */
    public SubpanelProducto(VentanaPrincipal ventana, SubpanelCatalogo subpanelCatalogo) {
        this.ventana = ventana;
        this.subpanelCatalogo = subpanelCatalogo;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
    }

    /**
     * Carga la información del producto y construye la interfaz.
     * Se llama cada vez que el usuario pincha en "Ver información".
     *
     * @param producto    El producto a mostrar
     * @param cliente     El cliente logueado
     * @param controlador El controlador del catálogo
     */
    public void mostrarProducto(ProductoVenta producto, Cliente cliente,
                                 ControladorCatalogo controlador) {
        this.producto = producto;
        this.cliente = cliente;
        this.controlador = controlador;

        // Limpiamos el panel antes de construirlo
        removeAll();

        // Barra superior con botón de volver
        add(crearBarraSuperior(), BorderLayout.NORTH);

        // Panel central con imagen e info
        add(crearPanelCentral(), BorderLayout.CENTER);

        // Panel inferior con reseñas
        add(crearPanelReseñas(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Crea la barra superior con el botón de volver al catálogo.
     *
     * @return El panel de la barra superior
     */
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);
        barra.setBorder(BorderFactory.createMatteBorder(
            0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));

        // Botón volver
        JButton botonVolver = new JButton("← Volver al catálogo");
        botonVolver.setFont(VentanaPrincipal.FUENTE_NORMAL);
        botonVolver.setForeground(VentanaPrincipal.COLOR_ACENTO);
        botonVolver.setBackground(VentanaPrincipal.COLOR_PANEL);
        botonVolver.setBorderPainted(false);
        botonVolver.setFocusPainted(false);
        botonVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Al pulsar volver, le decimos al SubpanelCatalogo que se muestre de nuevo
        botonVolver.addActionListener(e -> subpanelCatalogo.volverDelProducto());
        barra.add(botonVolver);

        return barra;
    }
    

    /**
     * Crea el panel central con la imagen a la izquierda
     * y la información del producto a la derecha.
     *
     * @return El panel central configurado
     */
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)
        ));

        // ── Imagen a la izquierda ─────────────────────────────────────────────
        JLabel labelImagen = new JLabel();
        labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
        labelImagen.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(300),
            VentanaPrincipal.escalar(300)
        ));
        cargarImagen(labelImagen, producto.getImagenRuta(),
            VentanaPrincipal.escalar(280), VentanaPrincipal.escalar(280));
        panel.add(labelImagen, BorderLayout.WEST);

        // ── Info a la derecha ─────────────────────────────────────────────────
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

        // Precio
        JLabel labelPrecio = new JLabel(String.format("%.2f€", producto.getPrecioOficial()));
        labelPrecio.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(24)));
        labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
        panelInfo.add(labelPrecio);

        panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        // Stock
        JLabel labelStock = new JLabel("Stock disponible: " + producto.getStockDisponible());
        labelStock.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelStock.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelInfo.add(labelStock);

        panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        // Descripción
        JLabel labelDescTitulo = new JLabel("Descripción:");
        labelDescTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelDescTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panelInfo.add(labelDescTitulo);

        // Usamos HTML para que el texto haga salto de línea automático
        JLabel labelDesc = new JLabel(
            "<html><p style='width:400px'>" + producto.getDescripcion() + "</p></html>");
        labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelInfo.add(labelDesc);

        panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        // Categorías
        JLabel labelCatTitulo = new JLabel("Categorías:");
        labelCatTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelCatTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panelInfo.add(labelCatTitulo);

        // Unimos los nombres de las categorías separados por coma
        String categorias = producto.getCategorias().stream()
            .map(c -> c.getNombre())
            .reduce((a, b) -> a + ", " + b)
            .orElse("Sin categoría");
        JLabel labelCat = new JLabel(categorias);
        labelCat.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelCat.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelInfo.add(labelCat);

        panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

        // Puntuación media
        if (!producto.getReseñas().isEmpty()) {
            JLabel labelMedia = new JLabel(
                String.format("Puntuación: %.1f/10", producto.getMediaPuntuacion()));
            labelMedia.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
            labelMedia.setForeground(VentanaPrincipal.COLOR_ACENTO);
            panelInfo.add(labelMedia);
            panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
        }

        // Botón añadir al carrito
        JButton botonAnadir = new JButton("Añadir al carrito");
        botonAnadir.setFont(VentanaPrincipal.FUENTE_BOTON);
        botonAnadir.setBackground(VentanaPrincipal.COLOR_ACENTO);
        botonAnadir.setForeground(Color.WHITE);
        botonAnadir.setOpaque(true);
        botonAnadir.setBorderPainted(false);
        botonAnadir.setFocusPainted(false);
        botonAnadir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonAnadir.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)
        ));
        botonAnadir.addActionListener(e -> mostrarDialogoAnadir());
        botonAnadir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botonAnadir.setBackground(VentanaPrincipal.COLOR_ACENTO2);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botonAnadir.setBackground(VentanaPrincipal.COLOR_ACENTO);
            }
        });
        panelInfo.add(botonAnadir);

        panel.add(panelInfo, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel inferior con las reseñas del producto.
     * Si no hay reseñas muestra un mensaje indicándolo.
     *
     * @return El panel de reseñas con scroll
     */
    private JScrollPane crearPanelReseñas() {
        panelReseñas = new JPanel();
        panelReseñas.setLayout(new BoxLayout(panelReseñas, BoxLayout.Y_AXIS));
        panelReseñas.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelReseñas.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(30)
        ));

        // Título de reseñas
        JLabel labelTitulo = new JLabel("Reseñas");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panelReseñas.add(labelTitulo);
        panelReseñas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

        List<Reseña> reseñas = producto.getReseñas();
        if (reseñas.isEmpty()) {
            JLabel labelSinReseñas = new JLabel("Este producto no tiene reseñas todavía.");
            labelSinReseñas.setFont(VentanaPrincipal.FUENTE_NORMAL);
            labelSinReseñas.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            panelReseñas.add(labelSinReseñas);
        } else {
            // Mostramos cada reseña como una tarjeta
            for (Reseña r : reseñas) {
                panelReseñas.add(crearTarjetaReseña(r));
                panelReseñas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
            }
        }

        // Scroll para las reseñas con altura máxima
        JScrollPane scroll = new JScrollPane(panelReseñas);
        scroll.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, VentanaPrincipal.COLOR_BORDE));
        scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
        scroll.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(200)));
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_PANEL);
        return scroll;
    }

    /**
     * Crea una tarjeta visual para una reseña con la puntuación,
     * el texto y el nombre del autor.
     *
     * @param reseña La reseña a mostrar
     * @return El panel con la tarjeta de la reseña
     */
    private JPanel crearTarjetaReseña(Reseña reseña) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15)
            )
        ));

        // Puntuación y autor arriba
        JLabel labelCabecera = new JLabel(
        	    reseña.getPuntuacion() + "/10  —  " + reseña.getAutor().getNickname());
        labelCabecera.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelCabecera.setForeground(VentanaPrincipal.COLOR_ACENTO);
        tarjeta.add(labelCabecera, BorderLayout.NORTH);

        // Texto de la reseña
        JLabel labelTexto = new JLabel(
        	    "<html><p style='width:600px'>" + reseña.getComentario() + "</p></html>");
        labelTexto.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelTexto.setForeground(VentanaPrincipal.COLOR_TEXTO);
        tarjeta.add(labelTexto, BorderLayout.CENTER);

        return tarjeta;
    }

    /**
     * Muestra el diálogo para elegir cantidad y añadir al carrito.
     */
    private void mostrarDialogoAnadir() {
        JSpinner spinnerCantidad = new JSpinner(
            new SpinnerNumberModel(1, 1, producto.getStockDisponible(), 1));
        spinnerCantidad.setFont(VentanaPrincipal.FUENTE_NORMAL);

        JPanel panelDialogo = new JPanel(new FlowLayout());
        panelDialogo.add(new JLabel("Cantidad (máx. " + producto.getStockDisponible() + "):"));
        panelDialogo.add(spinnerCantidad);

        int opcion = JOptionPane.showConfirmDialog(
            this, panelDialogo,
            "Añadir: " + producto.getNombre(),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (opcion == JOptionPane.OK_OPTION) {
            int cantidad = (int) spinnerCantidad.getValue();
            boolean ok = controlador.añadirAlCarrito(producto, cantidad);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    producto.getNombre() + " x" + cantidad + " añadido al carrito.",
                    "Añadido", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo añadir al carrito.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga una imagen desde src/fotos/ y la escala al tamaño indicado.
     *
     * @param label  JLabel donde cargar la imagen
     * @param nombre Nombre del archivo
     * @param ancho  Ancho deseado
     * @param alto   Alto deseado
     */
    private void cargarImagen(JLabel label, String nombre, int ancho, int alto) {
        try {
            URL url = getClass().getResource("/fotos/" + nombre);
            if (url != null) {
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    Image imgEscalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
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