package Gui.Gestor;

import Gui.Controladores.Gestor.ControladorProductosDescuentosGestor;
import Gui.VentanaPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import productos.Categoria;
import productos.ProductoVenta;
import usuarios.Gestor;
import ventas.Descuento;

/**
 * Subpanel de gestión de productos y descuentos para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelProductosDescuentosGestor extends AbstractPanelGestor {

    private ControladorProductosDescuentosGestor controlador;
    private JPanel panelProductos;
    private JPanel panelDescuentos;
    private JPanel panelParametros;
    private CardLayout cardParametros;

    private JTextField campoNombreDescuento;
    private JSpinner spinnerDias;
    private JComboBox<String> comboTipoDescuento;

    private JSpinner spinnerGastoMin;
    private JSpinner spinnerPorcentajeVol;
    private JPanel panelComboCat;
    private JSpinner spinnerPorcentajeCat;
    private JSpinner spinnerCantidadMin;
    private JPanel panelComboProdCant;
    private JSpinner spinnerPorcentajeCant;
    private JPanel panelComboProdRegalo;
    private JSpinner spinnerGastoRegalo;

    private JTextField campoBusquedaProductos;
    private JTextField campoBusquedaDescuentos;
    private Map<String, JSpinner> spinnersPrecio = new HashMap<>();

    private JButton botonCrearDescuento;

    public SubpanelProductosDescuentosGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana, gestor);
        this.controlador = new ControladorProductosDescuentosGestor(this, gestor);
        inicializarUI();
    }

    private void inicializarUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.5);
        split.setDividerSize(VentanaPrincipal.escalar(5));
        split.setBorder(null);
        split.setLeftComponent(crearPanelProductos());
        split.setRightComponent(crearPanelDescuentos());
        add(split, BorderLayout.CENTER);
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel titulo = new JLabel("Productos y precios");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(15)));
        panelNorth.add(titulo, BorderLayout.NORTH);

        JPanel barraBusqueda = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(5)));
        barraBusqueda.setBackground(VentanaPrincipal.COLOR_FONDO);
        barraBusqueda.add(crearLabel("Buscar producto:"));
        campoBusquedaProductos = crearCampoCompacto();
        campoBusquedaProductos.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(28)));
        escucharCambios(campoBusquedaProductos, this::filtrarProductos);
        barraBusqueda.add(campoBusquedaProductos);
        panelNorth.add(barraBusqueda, BorderLayout.CENTER);
        panel.add(panelNorth, BorderLayout.NORTH);

        panelProductos = new JPanel();
        panelProductos.setLayout(new BoxLayout(panelProductos, BoxLayout.Y_AXIS));
        panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(VentanaPrincipal.COLOR_FONDO);
        wrapper.add(panelProductos, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        panel.add(scroll, BorderLayout.CENTER);

        actualizarProductos();
        return panel;
    }

    private void filtrarProductos() {
        String texto = normalizarTexto(campoBusquedaProductos.getText());
        panelProductos.removeAll();
        spinnersPrecio.clear();
        for (ProductoVenta p : controlador.getProductos()) {
            if (texto.isEmpty() || contieneTexto(p.getNombre(), texto))
                panelProductos.add(crearFilaProducto(p));
        }
        panelProductos.revalidate();
        panelProductos.repaint();
    }

    private JPanel crearPanelDescuentos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel titulo = new JLabel("Descuentos");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15)));
        panel.add(titulo, BorderLayout.NORTH);

        // Split vertical: formulario arriba (65%), lista abajo (35%)
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setResizeWeight(0.2);
        splitVertical.setDividerSize(VentanaPrincipal.escalar(5));
        splitVertical.setBorder(null);

        JScrollPane scrollFormulario = new JScrollPane(crearFormularioDescuento());
        scrollFormulario.setBorder(null);
        scrollFormulario.getViewport().setBackground(VentanaPrincipal.COLOR_PANEL);
        splitVertical.setTopComponent(scrollFormulario);

        // Panel lista con buscador
        JPanel panelListaDescuentos = new JPanel(new BorderLayout());
        panelListaDescuentos.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel barraBusquedaDesc = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(5)));
        barraBusquedaDesc.setBackground(VentanaPrincipal.COLOR_FONDO);
        barraBusquedaDesc.setBorder(javax.swing.BorderFactory.createMatteBorder(
            1, 0, 0, 0, VentanaPrincipal.COLOR_BORDE));
        barraBusquedaDesc.add(crearLabel("Buscar descuento:"));
        campoBusquedaDescuentos = crearCampoCompacto();
        campoBusquedaDescuentos.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(28)));
        escucharCambios(campoBusquedaDescuentos, this::filtrarDescuentos);
        barraBusquedaDesc.add(campoBusquedaDescuentos);
        panelListaDescuentos.add(barraBusquedaDesc, BorderLayout.NORTH);

        panelDescuentos = new JPanel();
        panelDescuentos.setLayout(new BoxLayout(panelDescuentos, BoxLayout.Y_AXIS));
        panelDescuentos.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel wrapperDesc = new JPanel(new BorderLayout());
        wrapperDesc.setBackground(VentanaPrincipal.COLOR_FONDO);
        wrapperDesc.add(panelDescuentos, BorderLayout.NORTH);

        JScrollPane scrollDesc = new JScrollPane(wrapperDesc);
        scrollDesc.setBorder(null);
        scrollDesc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollDesc.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        panelListaDescuentos.add(scrollDesc, BorderLayout.CENTER);

        splitVertical.setBottomComponent(panelListaDescuentos);
        panel.add(splitVertical, BorderLayout.CENTER);

        actualizarDescuentos();
        return panel;
    }

    private void filtrarDescuentos() {
        String texto = normalizarTexto(campoBusquedaDescuentos.getText());
        panelDescuentos.removeAll();
        for (Descuento d : controlador.getDescuentosActivos()) {
            if (texto.isEmpty() || contieneTexto(d.getNombre(), texto)
                    || contieneTexto(d.getId(), texto))
                panelDescuentos.add(crearFilaDescuento(d));
        }
        panelDescuentos.revalidate();
        panelDescuentos.repaint();
    }

    private JPanel crearFormularioDescuento() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0,
            VentanaPrincipal.escalar(4), 0);

        gbc.gridy = 0;
        panel.add(crearLabel("Tipo de descuento:"), gbc);
        comboTipoDescuento = crearCombo(
            new String[]{"Volumen", "Categoría", "Cantidad", "Regalo"});
        gbc.gridy = 1;
        panel.add(comboTipoDescuento, gbc);

        gbc.gridy = 2;
        panel.add(crearLabel("Nombre del descuento:"), gbc);
        campoNombreDescuento = crearCampo();
        gbc.gridy = 3;
        panel.add(campoNombreDescuento, gbc);

        gbc.gridy = 4;
        panel.add(crearLabel("Duración (días desde hoy):"), gbc);
        spinnerDias = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        spinnerDias.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 5;
        panel.add(spinnerDias, gbc);

        cardParametros = new CardLayout();
        panelParametros = new JPanel(cardParametros);
        panelParametros.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelParametros.add(crearPanelVolumen(),   "Volumen");
        panelParametros.add(crearPanelCategoria(), "Categoría");
        panelParametros.add(crearPanelCantidad(),  "Cantidad");
        panelParametros.add(crearPanelRegalo(),    "Regalo");

        comboTipoDescuento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardParametros.show(panelParametros,
                    (String) comboTipoDescuento.getSelectedItem());
            }
        });

        gbc.gridy = 6;
        panel.add(panelParametros, gbc);

        botonCrearDescuento = crearBotonNaranja("Crear descuento");
        botonCrearDescuento.setActionCommand("crearDescuento");
        botonCrearDescuento.addActionListener(controlador);
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(15), 0, 0, 0);
        panel.add(botonCrearDescuento, gbc);

        return panel;
    }

    private JPanel crearPanelVolumen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints g = crearGbcParam();
        g.gridy = 0; panel.add(crearLabel("Gasto mínimo (€):"), g);
        spinnerGastoMin = new JSpinner(
            new SpinnerNumberModel(21.0, 20.01, 9999.0, 1.0));
        spinnerGastoMin.setFont(VentanaPrincipal.FUENTE_NORMAL);
        g.gridy = 1; panel.add(spinnerGastoMin, g);
        g.gridy = 2; panel.add(crearLabel("Porcentaje (%):"), g);
        spinnerPorcentajeVol = new JSpinner(
            new SpinnerNumberModel(10, 1, 100, 1));
        spinnerPorcentajeVol.setFont(VentanaPrincipal.FUENTE_NORMAL);
        g.gridy = 3; panel.add(spinnerPorcentajeVol, g);
        return panel;
    }

    private JPanel crearPanelCategoria() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints g = crearGbcParam();
        g.gridy = 0; panel.add(crearLabel("Categoría afectada:"), g);
        List<Categoria> cats = controlador.getCategorias();
        String[] nombresCats = new String[cats.size()];
        for (int i = 0; i < cats.size(); i++) nombresCats[i] = cats.get(i).getNombre();
        panelComboCat = crearComboConBuscador(nombresCats, VentanaPrincipal.escalar(200));
        g.gridy = 1; panel.add(panelComboCat, g);
        g.gridy = 2; panel.add(crearLabel("Porcentaje (%):"), g);
        spinnerPorcentajeCat = new JSpinner(
            new SpinnerNumberModel(10, 1, 100, 1));
        spinnerPorcentajeCat.setFont(VentanaPrincipal.FUENTE_NORMAL);
        g.gridy = 3; panel.add(spinnerPorcentajeCat, g);
        return panel;
    }

    private JPanel crearPanelCantidad() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints g = crearGbcParam();
        g.gridy = 0; panel.add(crearLabel("Cantidad mínima:"), g);
        spinnerCantidadMin = new JSpinner(
            new SpinnerNumberModel(2, 2, 999, 1));
        spinnerCantidadMin.setFont(VentanaPrincipal.FUENTE_NORMAL);
        g.gridy = 1; panel.add(spinnerCantidadMin, g);
        g.gridy = 2; panel.add(crearLabel("Producto al que aplica:"), g);
        panelComboProdCant = crearComboConBuscador(
            getNombresProductos(), VentanaPrincipal.escalar(200));
        g.gridy = 3; panel.add(panelComboProdCant, g);
        g.gridy = 4; panel.add(crearLabel("Porcentaje (%):"), g);
        spinnerPorcentajeCant = new JSpinner(
            new SpinnerNumberModel(10, 1, 100, 1));
        spinnerPorcentajeCant.setFont(VentanaPrincipal.FUENTE_NORMAL);
        g.gridy = 5; panel.add(spinnerPorcentajeCant, g);
        return panel;
    }

    private JPanel crearPanelRegalo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints g = crearGbcParam();
        g.gridy = 0; panel.add(crearLabel("Producto regalado:"), g);
        panelComboProdRegalo = crearComboConBuscador(
            getNombresProductos(), VentanaPrincipal.escalar(200));
        g.gridy = 1; panel.add(panelComboProdRegalo, g);
        g.gridy = 2; panel.add(crearLabel("Gasto mínimo (€):"), g);
        spinnerGastoRegalo = new JSpinner(
            new SpinnerNumberModel(36.0, 35.01, 9999.0, 1.0));
        spinnerGastoRegalo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        g.gridy = 3; panel.add(spinnerGastoRegalo, g);
        return panel;
    }

    private GridBagConstraints crearGbcParam() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0,
            VentanaPrincipal.escalar(4), 0);
        return gbc;
    }

    private String[] getNombresProductos() {
        List<ProductoVenta> productos = controlador.getProductos();
        String[] nombres = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++)
            nombres[i] = productos.get(i).getNombre()
                + " (" + productos.get(i).getId() + ")";
        return nombres;
    }

    private void actualizarProductos() {
        panelProductos.removeAll();
        spinnersPrecio.clear();
        for (ProductoVenta p : controlador.getProductos())
            panelProductos.add(crearFilaProducto(p));
        panelProductos.revalidate();
        panelProductos.repaint();
    }

    private JPanel crearFilaProducto(ProductoVenta p) {
        JPanel fila = new JPanel(new GridBagLayout());
        fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,
            VentanaPrincipal.escalar(55)));
        fila.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15))));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, VentanaPrincipal.escalar(5),
            0, VentanaPrincipal.escalar(5));
        gbc.anchor = GridBagConstraints.WEST;

        JLabel labelNombre = new JLabel(p.getNombre());
        labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fila.add(labelNombre, gbc);

        JLabel labelPrecio = new JLabel(
            String.format("%.2f€", p.getPrecioOficial()));
        labelPrecio.setFont(VentanaPrincipal.FUENTE_PRECIO);
        labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
        gbc.gridx = 1; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        fila.add(labelPrecio, gbc);

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(
            p.getPrecioOficial(), 0.01, 9999.0, 0.5));
        spinner.setFont(VentanaPrincipal.FUENTE_NORMAL);
        spinner.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(90), VentanaPrincipal.escalar(28)));
        spinnersPrecio.put(p.getId(), spinner);
        gbc.gridx = 2;
        fila.add(spinner, gbc);

        JButton boton = crearBotonNaranja("Cambiar");
        boton.setActionCommand("cambiarPrecio:" + p.getId());
        boton.addActionListener(controlador);
        gbc.gridx = 3;
        fila.add(boton, gbc);

        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        return fila;
    }

    private void actualizarDescuentos() {
        panelDescuentos.removeAll();

        JLabel titulo = new JLabel("Descuentos activos:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(5), 0));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDescuentos.add(titulo);

        List<Descuento> descuentos = controlador.getDescuentosActivos();
        if (descuentos.isEmpty()) {
            JLabel labelVacio = crearLabel("No hay descuentos activos.");
            labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelDescuentos.add(labelVacio);
        } else {
            for (Descuento d : descuentos)
                panelDescuentos.add(crearFilaDescuento(d));
        }

        panelDescuentos.revalidate();
        panelDescuentos.repaint();
    }

    private JPanel crearFilaDescuento(Descuento d) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,
            VentanaPrincipal.escalar(45)));
        fila.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15))));

        JLabel labelDesc = new JLabel(d.getNombre() + " — " + d.getId()
            + " | hasta: " + d.getFechaFin().toLocalDate());
        labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO);
        fila.add(labelDesc, BorderLayout.CENTER);

        JButton botonEliminar = crearBotonRojo("Eliminar");
        botonEliminar.setActionCommand("eliminarDescuento:" + d.getId());
        botonEliminar.addActionListener(controlador);
        fila.add(botonEliminar, BorderLayout.EAST);

        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        return fila;
    }

    public void procesarCambiarPrecio(String idProducto) {
        JSpinner spinner = spinnersPrecio.get(idProducto);
        if (spinner == null) return;
        double nuevo = ((Number) spinner.getValue()).doubleValue();
        if (controlador.modificarPrecio(idProducto, nuevo)) {
            mostrarMensaje("Precio actualizado.");
            actualizarProductos();
        } else {
            mostrarError("No se pudo modificar el precio.");
        }
    }

    public void procesarCrearDescuento() {
        String nombre = campoNombreDescuento.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre no puede estar vacío.");
            return;
        }
        String tipo = (String) comboTipoDescuento.getSelectedItem();
        int dias = (int) spinnerDias.getValue();
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(dias);
        boolean ok = false;
        List<ProductoVenta> productos = controlador.getProductos();

        switch (tipo) {
            case "Volumen":
                ok = controlador.crearDescuentoVolumen(nombre,
                    ((Number) spinnerGastoMin.getValue()).doubleValue(),
                    ((Number) spinnerPorcentajeVol.getValue()).doubleValue(),
                    inicio, fin);
                break;
            case "Categoría":
                JComboBox<String> comboCat = getComboDePanel(panelComboCat);
                String cat = comboCat != null
                    ? (String) comboCat.getSelectedItem() : "";
                ok = controlador.crearDescuentoCategoria(nombre, cat,
                    ((Number) spinnerPorcentajeCat.getValue()).doubleValue(),
                    inicio, fin);
                break;
            case "Cantidad":
                JComboBox<String> comboCant = getComboDePanel(panelComboProdCant);
                String selCant = comboCant != null
                    ? (String) comboCant.getSelectedItem() : "";
                String idCant = extraerIdDeTexto(selCant, productos);
                ok = controlador.crearDescuentoCantidad(nombre, idCant,
                    (int) spinnerCantidadMin.getValue(),
                    ((Number) spinnerPorcentajeCant.getValue()).doubleValue(),
                    inicio, fin);
                break;
            case "Regalo":
                JComboBox<String> comboRegalo = getComboDePanel(panelComboProdRegalo);
                String selRegalo = comboRegalo != null
                    ? (String) comboRegalo.getSelectedItem() : "";
                String idRegalo = extraerIdDeTexto(selRegalo, productos);
                ok = controlador.crearDescuentoRegalo(nombre, idRegalo,
                    ((Number) spinnerGastoRegalo.getValue()).doubleValue(),
                    inicio, fin);
                break;
        }

        if (ok) {
            mostrarMensaje("Descuento '" + nombre + "' creado correctamente.");
            campoNombreDescuento.setText("");
            actualizarDescuentos();
        } else {
            mostrarError("No se pudo crear el descuento. Comprueba los datos.");
        }
    }

    public void procesarEliminarDescuento(String id) {
        if (controlador.eliminarDescuento(id)) {
            mostrarMensaje("Descuento eliminado.");
            actualizarDescuentos();
        } else {
            mostrarError("No se pudo eliminar.");
        }
    }

    private String extraerIdDeTexto(String texto, List<ProductoVenta> productos) {
        if (texto == null) return "";
        for (ProductoVenta p : productos)
            if (texto.contains(p.getId())) return p.getId();
        return "";
    }
}