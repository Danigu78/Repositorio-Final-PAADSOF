package Gui.Gestor;

import Gui.Controladores.Gestor.ControladorCategoriasGestor;
import Gui.VentanaPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import productos.Categoria;
import productos.ProductoVenta;
import usuarios.Gestor;

/**
 * Subpanel de gestión de categorías para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelCategoriasGestor extends AbstractPanelGestor {

    private ControladorCategoriasGestor controlador;
    private JPanel panelLista;

    private JTextField campoNombre;
    private JTextField campoDesc;
    private JTextField campoBusquedaCategorias;

    // Combos de productos por categoría — nombre categoría → panel combo
    private Map<String, JPanel> combosProductosPanel = new HashMap<>();
    // Productos por categoría para recuperar el id
    private Map<String, List<ProductoVenta>> productosMap = new HashMap<>();

    private JButton botonCrear;

    public SubpanelCategoriasGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana, gestor);
        this.controlador = new ControladorCategoriasGestor(this, gestor);
        inicializarUI();
    }

    private void inicializarUI() {
        add(crearFormularioNuevaCategoria(), BorderLayout.NORTH);

        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setBackground(VentanaPrincipal.COLOR_FONDO);

        JScrollPane scroll = new JScrollPane(panelLista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        add(scroll, BorderLayout.CENTER);

        setControlador(controlador);
        actualizarLista();
    }

    public void setControlador(ActionListener c) {
        if (botonCrear != null) {
            for (ActionListener al : botonCrear.getActionListeners())
                botonCrear.removeActionListener(al);
            botonCrear.addActionListener(c);
        }
    }

    private JPanel crearFormularioNuevaCategoria() {
        JPanel panel = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10),
            VentanaPrincipal.escalar(10)));
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));

        JLabel titulo = new JLabel("Nueva categoría:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panel.add(titulo);

        panel.add(crearLabel("Nombre:"));
        campoNombre = crearCampoColumnas(10);
        panel.add(campoNombre);

        panel.add(crearLabel("Descripción:"));
        campoDesc = crearCampoColumnas(20);
        panel.add(campoDesc);

        botonCrear = crearBotonNaranja("Crear categoría");
        botonCrear.setActionCommand("crearCategoria");
        panel.add(botonCrear);

        return panel;
    }

    private void actualizarLista() {
        panelLista.removeAll();
        combosProductosPanel.clear();
        productosMap.clear();

        JLabel titulo = new JLabel("Categorías:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(5), 0));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLista.add(titulo);

        // Barra de búsqueda de categorías
        JPanel barraBusqueda = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(8)));
        barraBusqueda.setBackground(VentanaPrincipal.COLOR_FONDO);
        barraBusqueda.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraBusqueda.add(crearLabel("Buscar categoría:"));
        campoBusquedaCategorias = crearCampoCompacto();
        campoBusquedaCategorias.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(200), VentanaPrincipal.escalar(28)));
        escucharCambios(campoBusquedaCategorias, this::filtrarCategorias);
        barraBusqueda.add(campoBusquedaCategorias);
        panelLista.add(barraBusqueda);

        for (Categoria cat : controlador.getCategorias()) {
            panelLista.add(crearFilaCategoria(cat));
        }

        panelLista.revalidate();
        panelLista.repaint();
    }

    private void filtrarCategorias() {
        String texto = normalizarTexto(campoBusquedaCategorias.getText());
        while (panelLista.getComponentCount() > 2)
            panelLista.remove(panelLista.getComponentCount() - 1);
        for (Categoria cat : controlador.getCategorias()) {
            if (texto.isEmpty() || contieneTexto(cat.getNombre(), texto)) {
                panelLista.add(crearFilaCategoria(cat));
            }
        }
        panelLista.revalidate();
        panelLista.repaint();
    }

    private JPanel crearFilaCategoria(Categoria cat) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
        fila.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));

        // Info izquierda: nombre + productos actuales con buscador
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

        JLabel labelNombre = new JLabel(cat.getNombre());
        labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
        panelInfo.add(labelNombre);

        // Desplegable con buscador de productos actuales de esta categoría
        if (!cat.getProductos().isEmpty()) {
            String[] productosActuales = cat.getProductos().stream()
                .map(p -> p.getNombre() + " (" + p.getId() + ")")
                .toArray(String[]::new);
            JPanel comboActuales = crearComboConBuscador(
                productosActuales, VentanaPrincipal.escalar(220));
            comboActuales.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelInfo.add(comboActuales);
        } else {
            JLabel labelVacio = crearLabel("Sin productos");
            panelInfo.add(labelVacio);
        }

        fila.add(panelInfo, BorderLayout.CENTER);

        // Botones derecha: combo con buscador para añadir/quitar + botones
        JPanel panelBotones = new JPanel(new FlowLayout(
            FlowLayout.RIGHT, VentanaPrincipal.escalar(5), 0));
        panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

        List<ProductoVenta> productos = controlador.getProductos();
        productosMap.put(cat.getNombre(), productos);

        String[] nombresProductos = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            nombresProductos[i] = productos.get(i).getNombre()
                + " (" + productos.get(i).getId() + ")";
        }
        JPanel panelComboProductos = crearComboConBuscador(
            nombresProductos, VentanaPrincipal.escalar(200));
        combosProductosPanel.put(cat.getNombre(), panelComboProductos);
        panelBotones.add(panelComboProductos);

        JButton botonAnadir = crearBotonNaranja("+ Añadir");
        botonAnadir.setActionCommand("añadirProducto:" + cat.getNombre());
        botonAnadir.addActionListener(controlador);
        panelBotones.add(botonAnadir);

        JButton botonQuitar = crearBotonNaranja("- Quitar");
        botonQuitar.setActionCommand("quitarProducto:" + cat.getNombre());
        botonQuitar.addActionListener(controlador);
        panelBotones.add(botonQuitar);

        fila.add(panelBotones, BorderLayout.EAST);
        return fila;
    }

    public void procesarCrearCategoria() {
        String nombre = campoNombre.getText().trim();
        String desc = campoDesc.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre no puede estar vacío.");
            return;
        }
        if (controlador.crearCategoria(nombre, desc)) {
            mostrarMensaje("Categoría '" + nombre + "' creada.");
            campoNombre.setText("");
            campoDesc.setText("");
            actualizarLista();
        } else {
            mostrarError("No se pudo crear la categoría.");
        }
    }

    public void procesarAñadirProducto(String nombreCat) {
        JPanel panelCombo = combosProductosPanel.get(nombreCat);
        List<ProductoVenta> productos = productosMap.get(nombreCat);
        if (panelCombo == null || productos == null) return;
        JComboBox<String> combo = getComboDePanel(panelCombo);
        if (combo == null || combo.getSelectedItem() == null) return;
        // Buscamos el producto por nombre en el combo
        String seleccionado = (String) combo.getSelectedItem();
        String idProducto = extraerIdDeNombre(seleccionado, productos);
        if (idProducto == null) return;
        if (controlador.añadirProductoACategoria(idProducto, nombreCat)) {
            mostrarMensaje("Producto añadido a " + nombreCat);
            actualizarLista();
        } else {
            mostrarError("No se pudo añadir el producto.");
        }
    }

    public void procesarQuitarProducto(String nombreCat) {
        JPanel panelCombo = combosProductosPanel.get(nombreCat);
        List<ProductoVenta> productos = productosMap.get(nombreCat);
        if (panelCombo == null || productos == null) return;
        JComboBox<String> combo = getComboDePanel(panelCombo);
        if (combo == null || combo.getSelectedItem() == null) return;
        String seleccionado = (String) combo.getSelectedItem();
        String idProducto = extraerIdDeNombre(seleccionado, productos);
        if (idProducto == null) return;
        if (controlador.eliminarProductoDeCategoria(idProducto, nombreCat)) {
            mostrarMensaje("Producto quitado de " + nombreCat);
            actualizarLista();
        } else {
            mostrarError("No se pudo quitar el producto.");
        }
    }

    /**
     * Extrae el id del producto a partir del texto "nombre (id)"
     */
    private String extraerIdDeNombre(String texto, List<ProductoVenta> productos) {
        for (ProductoVenta p : productos) {
            if (texto.contains(p.getId())) return p.getId();
        }
        return null;
    }
}