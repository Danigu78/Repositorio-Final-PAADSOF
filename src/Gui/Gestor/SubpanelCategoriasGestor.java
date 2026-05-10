package Gui.Gestor;

import Gui.TablaProductosVenta;
import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorCategoriasGestor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
    private JTextField campoBusquedaProductos;
    private JTextField campoIdProductoTabla;
    private JComboBox<String> comboCategoriaTabla;
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private TablaProductosVenta tablaProductosVenta;

    private Map<String, JPanel> combosProductosPanel = new HashMap<>();
    private Map<String, List<ProductoVenta>> productosMap = new HashMap<>();

    private JButton botonCrear;
    private JButton botonAnadirTabla;
    private JButton botonQuitarTabla;

    public SubpanelCategoriasGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana, gestor);
        this.controlador = new ControladorCategoriasGestor(this, gestor);
        inicializarUI();
    }

    private void inicializarUI() {
        panelLista = new JPanel(new GridBagLayout());
        panelLista.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(VentanaPrincipal.COLOR_FONDO);
        contenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
            VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

        contenido.add(crearFormularioNuevaCategoria());
        contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
        contenido.add(crearPanelTablaProductosComun());
        contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
        contenido.add(crearPanelCategorias());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
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
        if (botonAnadirTabla != null) {
            for (ActionListener al : botonAnadirTabla.getActionListeners())
                botonAnadirTabla.removeActionListener(al);
            botonAnadirTabla.addActionListener(c);
        }
        if (botonQuitarTabla != null) {
            for (ActionListener al : botonQuitarTabla.getActionListeners())
                botonQuitarTabla.removeActionListener(al);
            botonQuitarTabla.addActionListener(c);
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

    private JPanel crearPanelTablaProductosComun() {
        JPanel panel = crearBloque("Productos de venta");

        tablaProductosVenta = new TablaProductosVenta(
            () -> controlador.getProductosOrdenados());
        tablaProductosVenta.setAlSeleccionarId(id -> campoIdProductoTabla.setText(id));
        panel.add(tablaProductosVenta, gbcCampo(1));

        JPanel acciones = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
        acciones.setOpaque(false);

        acciones.add(crearLabel("ID producto:"));
        campoIdProductoTabla = crearCampoCompacto();
        campoIdProductoTabla.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(120), VentanaPrincipal.escalar(30)));
        acciones.add(campoIdProductoTabla);

        acciones.add(crearLabel("Categoria:"));
        comboCategoriaTabla = crearCombo(obtenerNombresCategorias());
        comboCategoriaTabla.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(190), VentanaPrincipal.escalar(30)));
        acciones.add(comboCategoriaTabla);

        botonAnadirTabla = crearBotonNaranja("Añadir");
        botonAnadirTabla.setActionCommand("anadirProductoTabla");
        acciones.add(botonAnadirTabla);

        botonQuitarTabla = crearBotonNaranja("Quitar");
        botonQuitarTabla.setActionCommand("quitarProductoTabla");
        acciones.add(botonQuitarTabla);

        panel.add(acciones, gbcCampo(2));
        return panel;
    }

    private JPanel crearPanelCategorias() {
        JPanel panel = crearBloque("Categorias");
        panel.add(panelLista, gbcCampo(1));
        return panel;
    }

    private JPanel crearPanelTablaProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15)));

        JPanel barra = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
        barra.setOpaque(false);

        JLabel titulo = new JLabel("Productos de venta");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        barra.add(titulo);

        barra.add(crearLabel("Buscar:"));
        campoBusquedaProductos = crearCampoCompacto();
        campoBusquedaProductos.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(28)));
        escucharCambios(campoBusquedaProductos, this::filtrarProductosTabla);
        barra.add(campoBusquedaProductos);

        barra.add(crearLabel("Categoría:"));
        comboCategoriaTabla = crearCombo(obtenerNombresCategorias());
        comboCategoriaTabla.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(28)));
        barra.add(comboCategoriaTabla);

        campoIdProductoTabla = crearCampoCompacto();
        campoIdProductoTabla.setEditable(false);
        campoIdProductoTabla.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(100), VentanaPrincipal.escalar(28)));
        barra.add(crearLabel("Producto:"));
        barra.add(campoIdProductoTabla);

        botonAnadirTabla = crearBotonNaranja("Añadir");
        botonAnadirTabla.setActionCommand("anadirProductoTabla");
        barra.add(botonAnadirTabla);

        botonQuitarTabla = crearBotonNaranja("Quitar");
        botonQuitarTabla.setActionCommand("quitarProductoTabla");
        barra.add(botonQuitarTabla);

        panel.add(barra, BorderLayout.NORTH);

        modeloProductos = new DefaultTableModel(
            new String[] { "ID", "Nombre", "Tipo", "Categorías", "Precio", "Stock", "Puntuación" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductos.setRowHeight(VentanaPrincipal.escalar(28));
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setBackground(Color.WHITE);
        tablaProductos.setForeground(Color.BLACK);
        tablaProductos.setGridColor(new Color(225, 225, 225));
        tablaProductos.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
        tablaProductos.setSelectionForeground(Color.BLACK);

        JTableHeader cabecera = tablaProductos.getTableHeader();
        cabecera.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cabecera.setBackground(new Color(235, 235, 235));
        cabecera.setForeground(Color.BLACK);
        cabecera.setReorderingAllowed(false);

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tablaProductos.getSelectedRow();
            if (fila >= 0) {
                campoIdProductoTabla.setText(String.valueOf(
                    tablaProductos.getValueAt(fila, 0)));
            }
        });

        JScrollPane scroll = estilizarScroll(tablaProductos);
        scroll.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(1000), VentanaPrincipal.escalar(220)));
        panel.add(scroll, BorderLayout.CENTER);

        cargarProductosTabla("");
        return panel;
    }

    private String[] obtenerNombresCategorias() {
        List<Categoria> categorias = controlador.getCategorias();
        String[] nombres = new String[categorias.size()];
        for (int i = 0; i < categorias.size(); i++) {
            nombres[i] = categorias.get(i).getNombre();
        }
        return nombres;
    }

    private void actualizarComboCategoriasTabla() {
        if (comboCategoriaTabla == null) {
            return;
        }
        comboCategoriaTabla.setModel(new DefaultComboBoxModel<>(obtenerNombresCategorias()));
    }

    private void filtrarProductosTabla() {
        cargarProductosTabla(campoBusquedaProductos.getText());
    }

    private void cargarProductosTabla(String filtro) {
        if (modeloProductos == null) {
            return;
        }

        modeloProductos.setRowCount(0);
        String texto = normalizarTexto(filtro);

        for (ProductoVenta producto : controlador.getProductosOrdenados()) {
            String id = producto.getId();
            String nombre = producto.getNombre();
            String tipo = controlador.obtenerTipoProductoVenta(producto);
            String categorias = controlador.obtenerTextoCategorias(producto);

            if (!texto.isEmpty() && !contieneTexto(id, texto)
                    && !contieneTexto(nombre, texto)
                    && !contieneTexto(tipo, texto)
                    && !contieneTexto(categorias, texto)) {
                continue;
            }

            modeloProductos.addRow(new Object[] {
                id, nombre, tipo, categorias,
                controlador.formatearPrecio(producto.getPrecioOficial()),
                producto.getStockDisponible(),
                controlador.formatearPuntuacion(producto.getMediaPuntuacion())
            });
        }
    }

    private void actualizarLista() {
        panelLista.removeAll();
        combosProductosPanel.clear();
        productosMap.clear();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Título
        JLabel titulo = new JLabel("Categorías:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(5), 0));
        gbc.gridy = 0;
        panelLista.add(titulo, gbc);

        // Barra de búsqueda — en GridBagLayout se queda a la izquierda naturalmente
        JPanel barraBusqueda = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(8)));
        barraBusqueda.setBackground(VentanaPrincipal.COLOR_FONDO);
        barraBusqueda.add(crearLabel("Buscar categoría:"));
        campoBusquedaCategorias = crearCampoCompacto();
        campoBusquedaCategorias.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(200), VentanaPrincipal.escalar(28)));
        escucharCambios(campoBusquedaCategorias, this::filtrarCategorias);
        barraBusqueda.add(campoBusquedaCategorias);
        gbc.gridy = 1;
        panelLista.add(barraBusqueda, gbc);

        // Filas de categorías
        List<Categoria> categorias = controlador.getCategorias();
        int fila = 2;
        if (categorias.isEmpty()) {
            JLabel labelVacio = crearLabel("No hay categorías.");
            gbc.gridy = fila;
            panelLista.add(labelVacio, gbc);
        } else {
            for (Categoria cat : categorias) {
                gbc.gridy = fila++;
                panelLista.add(crearFilaCategoria(cat), gbc);
            }
        }

        // Relleno para empujar todo hacia arriba
        GridBagConstraints gbcRelleno = new GridBagConstraints();
        gbcRelleno.gridx = 0; gbcRelleno.gridy = fila;
        gbcRelleno.weighty = 1; gbcRelleno.fill = GridBagConstraints.VERTICAL;
        panelLista.add(Box.createVerticalGlue(), gbcRelleno);

        panelLista.revalidate();
        panelLista.repaint();
    }

    private void filtrarCategorias() {
        String texto = normalizarTexto(campoBusquedaCategorias.getText());

        // Eliminamos todas las filas desde gridy=2 en adelante
        // mantenemos título (gridy=0) y barra (gridy=1)
        java.awt.Component[] componentes = panelLista.getComponents();
        for (int i = componentes.length - 1; i >= 2; i--)
            panelLista.remove(componentes[i]);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 0, 0);

        int fila = 2;
        for (Categoria cat : controlador.getCategorias()) {
            if (texto.isEmpty() || contieneTexto(cat.getNombre(), texto)) {
                gbc.gridy = fila++;
                panelLista.add(crearFilaCategoria(cat), gbc);
            }
        }

        GridBagConstraints gbcRelleno = new GridBagConstraints();
        gbcRelleno.gridx = 0; gbcRelleno.gridy = fila;
        gbcRelleno.weighty = 1; gbcRelleno.fill = GridBagConstraints.VERTICAL;
        panelLista.add(Box.createVerticalGlue(), gbcRelleno);

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
        // Tamaño máximo limitado para que no ocupe toda la pantalla
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,
            VentanaPrincipal.escalar(110)));

        // Info izquierda — anclada arriba
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelInfo.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel labelNombre = new JLabel(cat.getNombre());
        labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
        labelNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelInfo.add(labelNombre);

        // Desplegable con buscador de productos actuales — tamaño máximo limitado
        if (!cat.getProductos().isEmpty()) {
            String[] productosActuales = cat.getProductos().stream()
                .map(p -> p.getNombre() + " (" + p.getId() + ")")
                .toArray(String[]::new);
            JPanel comboActuales = crearComboConBuscador(
                productosActuales, VentanaPrincipal.escalar(220));
            comboActuales.setAlignmentX(Component.LEFT_ALIGNMENT);
            comboActuales.setMaximumSize(new Dimension(
                VentanaPrincipal.escalar(240), VentanaPrincipal.escalar(58)));
            panelInfo.add(comboActuales);
        } else {
            JLabel labelVacio = crearLabel("Sin productos");
            labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelInfo.add(labelVacio);
        }

        fila.add(panelInfo, BorderLayout.WEST);

        // Botones derecha
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

        JButton botonEliminar = crearBotonRojo("Eliminar");
        botonEliminar.setActionCommand("eliminarCategoria:" + cat.getNombre());
        botonEliminar.addActionListener(controlador);
        panelBotones.add(botonEliminar);
        
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
            actualizarComboCategoriasTabla();
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
        String idProducto = extraerIdDeNombre(
            (String) combo.getSelectedItem(), productos);
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
        String idProducto = extraerIdDeNombre(
            (String) combo.getSelectedItem(), productos);
        if (idProducto == null) return;
        if (controlador.eliminarProductoDeCategoria(idProducto, nombreCat)) {
            mostrarMensaje("Producto quitado de " + nombreCat);
            actualizarLista();
        } else {
            mostrarError("No se pudo quitar el producto.");
        }
    }

    public void procesarAnadirProductoTabla() {
        String idProducto = campoIdProductoTabla.getText().trim();
        String nombreCat = comboCategoriaTabla == null || comboCategoriaTabla.getSelectedItem() == null
            ? "" : String.valueOf(comboCategoriaTabla.getSelectedItem());

        if (idProducto.isEmpty() || nombreCat.isEmpty()) {
            mostrarError("Selecciona un producto y una categoría.");
            return;
        }

        if (controlador.añadirProductoACategoria(idProducto, nombreCat)) {
            mostrarMensaje("Producto añadido a " + nombreCat);
            tablaProductosVenta.refrescar();
            actualizarLista();
            actualizarComboCategoriasTabla();
        } else {
            mostrarError("No se pudo añadir el producto.");
        }
    }

    public void procesarQuitarProductoTabla() {
        String idProducto = campoIdProductoTabla.getText().trim();
        String nombreCat = comboCategoriaTabla == null || comboCategoriaTabla.getSelectedItem() == null
            ? "" : String.valueOf(comboCategoriaTabla.getSelectedItem());

        if (idProducto.isEmpty() || nombreCat.isEmpty()) {
            mostrarError("Selecciona un producto y una categoría.");
            return;
        }

        if (controlador.eliminarProductoDeCategoria(idProducto, nombreCat)) {
            mostrarMensaje("Producto quitado de " + nombreCat);
            tablaProductosVenta.refrescar();
            actualizarLista();
            actualizarComboCategoriasTabla();
        } else {
            mostrarError("No se pudo quitar el producto.");
        }
    }

    private String extraerIdDeNombre(String texto, List<ProductoVenta> productos) {
        for (ProductoVenta p : productos)
            if (texto != null && texto.contains(p.getId())) return p.getId();
        return null;
    }
    
    public void confirmarEliminarCategoria(String nombreCat) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Seguro que quieres eliminar la categoría '" + nombreCat + "'?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controlador.eliminarCategoria(nombreCat)) {
                mostrarMensaje("Categoría eliminada.");
                actualizarLista();
                actualizarComboCategoriasTabla();
            } else {
                mostrarError("No se pudo eliminar la categoría.");
            }
        }
    }
}
