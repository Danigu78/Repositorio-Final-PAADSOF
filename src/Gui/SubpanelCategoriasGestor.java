package Gui;

import Gui.Controladores.ControladorCategoriasGestor;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import productos.Categoria;
import productos.ProductoVenta;
import usuarios.Gestor;

/**
 * Subpanel de gestión de categorías para el gestor.
 * Permite crear categorías y gestionar qué productos pertenecen a cada una.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelCategoriasGestor extends JPanel {

    private VentanaPrincipal ventana;
    private Gestor gestor;
    private ControladorCategoriasGestor controlador;
    private JPanel panelLista;

    /**
     * Constructor del subpanel de categorías.
     *
     * @param ventana La ventana principal
     * @param gestor  El gestor logueado
     */
    public SubpanelCategoriasGestor(VentanaPrincipal ventana, Gestor gestor) {
        this.ventana = ventana;
        this.gestor = gestor;
        this.controlador = new ControladorCategoriasGestor(gestor);
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    /**
     * Construye la interfaz con formulario de nueva categoría y lista de categorías.
     */
    private void inicializarUI() {
        add(crearFormularioNuevaCategoria(), BorderLayout.NORTH);

        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setBackground(VentanaPrincipal.COLOR_FONDO);

        JScrollPane scroll = new JScrollPane(panelLista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        add(scroll, BorderLayout.CENTER);

        actualizarLista();
    }

    /**
     * Crea el formulario para crear una nueva categoría.
     *
     * @return Panel con el formulario
     */
    private JPanel crearFormularioNuevaCategoria() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))
        ));

        JLabel titulo = new JLabel("Nueva categoría:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo);

        panel.add(crearEtiqueta("Nombre:"));
        JTextField campoNombre = crearCampo(10);
        panel.add(campoNombre);

        panel.add(crearEtiqueta("Descripción:"));
        JTextField campoDesc = crearCampo(20);
        panel.add(campoDesc);

        JButton botonCrear = crearBoton("Crear categoría");
        botonCrear.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            String desc = campoDesc.getText().trim();
            if (controlador.crearCategoria(nombre, desc)) {
                mostrarExito("Categoría '" + nombre + "' creada.");
                campoNombre.setText("");
                campoDesc.setText("");
                actualizarLista();
            } else {
                mostrarError("No se pudo crear la categoría.");
            }
        });
        panel.add(botonCrear);

        return panel;
    }

    /**
     * Actualiza la lista de categorías mostrada.
     */
    private void actualizarLista() {
        panelLista.removeAll();

        JLabel titulo = new JLabel("Categorías:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(10), 0));
        panelLista.add(titulo);

        for (Categoria cat : controlador.getCategorias()) {
            panelLista.add(crearFilaCategoria(cat));
        }

        panelLista.revalidate();
        panelLista.repaint();
    }

    /**
     * Crea una fila visual para una categoría con sus productos y acciones.
     *
     * @param cat La categoría a mostrar
     * @return Panel con la fila de la categoría
     */
    private JPanel crearFilaCategoria(Categoria cat) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
        fila.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))
        ));

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
        panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

        JLabel labelNombre = new JLabel(cat.getNombre());
        labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panelInfo.add(labelNombre);

        JLabel labelProductos = new JLabel("(" + cat.getProductos().size() + " productos)");
        labelProductos.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelProductos.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelInfo.add(labelProductos);

        fila.add(panelInfo, BorderLayout.CENTER);

        // Botones para añadir/quitar producto
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(5), 0));
        panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

        // Combo con todos los productos
        List<ProductoVenta> productos = controlador.getProductos();
        String[] nombresProductos = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            nombresProductos[i] = productos.get(i).getNombre() + " (" + productos.get(i).getId() + ")";
        }
        JComboBox<String> comboProductos = new JComboBox<>(nombresProductos);
        comboProductos.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        panelBotones.add(comboProductos);

        JButton botonAnadir = crearBoton("+ Añadir");
        botonAnadir.addActionListener(e -> {
            int idx = comboProductos.getSelectedIndex();
            if (idx >= 0) {
                String idProducto = productos.get(idx).getId();
                if (controlador.añadirProductoACategoria(idProducto, cat.getNombre())) {
                    mostrarExito("Producto añadido a " + cat.getNombre());
                    actualizarLista();
                } else {
                    mostrarError("No se pudo añadir el producto.");
                }
            }
        });
        panelBotones.add(botonAnadir);

        JButton botonQuitar = crearBoton("- Quitar");
        botonQuitar.addActionListener(e -> {
            int idx = comboProductos.getSelectedIndex();
            if (idx >= 0) {
                String idProducto = productos.get(idx).getId();
                if (controlador.eliminarProductoDeCategoria(idProducto, cat.getNombre())) {
                    mostrarExito("Producto quitado de " + cat.getNombre());
                    actualizarLista();
                } else {
                    mostrarError("No se pudo quitar el producto.");
                }
            }
        });
        panelBotones.add(botonQuitar);

        fila.add(panelBotones, BorderLayout.EAST);
        return fila;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    private JTextField crearCampo(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))
        ));
        return campo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12),
            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12)
        ));
        return boton;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}