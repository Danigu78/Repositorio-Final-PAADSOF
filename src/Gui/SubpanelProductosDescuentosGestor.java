package Gui;

import Gui.Controladores.ControladorProductosDescuentosGestor;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.List;
import productos.Categoria;
import productos.ProductoVenta;
import usuarios.Gestor;
import ventas.Descuento;

/**
 * Subpanel de gestión de productos y descuentos para el gestor.
 * Permite modificar precios de productos y crear/eliminar descuentos.
 * Los parámetros del formulario de descuento cambian según el tipo elegido.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelProductosDescuentosGestor extends JPanel {

    private VentanaPrincipal ventana;
    private Gestor gestor;
    private ControladorProductosDescuentosGestor controlador;
    private JPanel panelProductos;
    private JPanel panelDescuentos;
    // Panel dinámico que cambia según el tipo de descuento
    private JPanel panelParametros;
    private CardLayout cardParametros;

    /**
     * Constructor del subpanel de productos y descuentos.
     *
     * @param ventana La ventana principal
     * @param gestor  El gestor logueado
     */
    public SubpanelProductosDescuentosGestor(VentanaPrincipal ventana, Gestor gestor) {
        this.ventana = ventana;
        this.gestor = gestor;
        this.controlador = new ControladorProductosDescuentosGestor(gestor);
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    /**
     * Construye la interfaz dividida: productos a la izquierda, descuentos a la derecha.
     */
    private void inicializarUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.5);
        split.setDividerSize(VentanaPrincipal.escalar(5));
        split.setBorder(null);
        split.setLeftComponent(crearPanelProductos());
        split.setRightComponent(crearPanelDescuentos());
        add(split, BorderLayout.CENTER);
    }

    /**
     * Crea el panel izquierdo con la lista de productos y sus precios.
     *
     * @return Panel de productos con scroll
     */
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel titulo = new JLabel("Productos y precios");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15)));
        panel.add(titulo, BorderLayout.NORTH);

        panelProductos = new JPanel();
        panelProductos.setLayout(new BoxLayout(panelProductos, BoxLayout.Y_AXIS));
        panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);

        JScrollPane scroll = new JScrollPane(panelProductos);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        panel.add(scroll, BorderLayout.CENTER);

        actualizarProductos();
        return panel;
    }

    /**
     * Crea el panel derecho con el formulario de descuentos y lista de activos.
     * El formulario tiene parámetros dinámicos según el tipo de descuento.
     *
     * @return Panel de descuentos
     */
    private JPanel crearPanelDescuentos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel titulo = new JLabel("Descuentos");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15)));
        panel.add(titulo, BorderLayout.NORTH);

        panel.add(crearFormularioDescuento(), BorderLayout.CENTER);

        panelDescuentos = new JPanel();
        panelDescuentos.setLayout(new BoxLayout(panelDescuentos, BoxLayout.Y_AXIS));
        panelDescuentos.setBackground(VentanaPrincipal.COLOR_FONDO);

        JScrollPane scroll = new JScrollPane(panelDescuentos);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, VentanaPrincipal.COLOR_BORDE));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        scroll.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(200)));
        panel.add(scroll, BorderLayout.SOUTH);

        actualizarDescuentos();
        return panel;
    }

    /**
     * Crea el formulario de nuevo descuento con parámetros dinámicos.
     * Cuando el usuario cambia el tipo, el panel de parámetros se actualiza
     * mostrando solo los campos necesarios para ese tipo.
     *
     * @return Panel con el formulario completo
     */
    private JPanel crearFormularioDescuento() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(
            VentanaPrincipal.escalar(4), 0,
            VentanaPrincipal.escalar(4), 0);

        // Tipo de descuento
        gbc.gridy = 0;
        panel.add(crearEtiqueta("Tipo de descuento:"), gbc);
        String[] tipos = {"Volumen", "Categoría", "Cantidad", "Regalo"};
        JComboBox<String> comboTipo = new JComboBox<>(tipos);
        comboTipo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        comboTipo.setBackground(Color.WHITE);
        gbc.gridy = 1;
        panel.add(comboTipo, gbc);

        // Nombre del descuento
        gbc.gridy = 2;
        panel.add(crearEtiqueta("Nombre del descuento:"), gbc);
        JTextField campoNombre = crearCampo();
        gbc.gridy = 3;
        panel.add(campoNombre, gbc);

        // Duración en días
        gbc.gridy = 4;
        panel.add(crearEtiqueta("Duración (días desde hoy):"), gbc);
        JSpinner spinnerDias = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        spinnerDias.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbc.gridy = 5;
        panel.add(spinnerDias, gbc);

        // Panel dinámico de parámetros según el tipo
        cardParametros = new CardLayout();
        panelParametros = new JPanel(cardParametros);
        panelParametros.setBackground(VentanaPrincipal.COLOR_PANEL);

        // ── Parámetros Volumen ────────────────────────────────────────────────
        // Necesita: gasto mínimo y porcentaje
        JPanel panelVolumen = new JPanel(new GridBagLayout());
        panelVolumen.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints gbcV = new GridBagConstraints();
        gbcV.gridx = 0; gbcV.fill = GridBagConstraints.HORIZONTAL;
        gbcV.weightx = 1;
        gbcV.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);
        gbcV.gridy = 0;
        panelVolumen.add(crearEtiqueta("Gasto mínimo (€):"), gbcV);
        JSpinner spinnerGastoMin = new JSpinner(new SpinnerNumberModel(21.0, 20.01, 9999.0, 1.0));
        spinnerGastoMin.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbcV.gridy = 1;
        panelVolumen.add(spinnerGastoMin, gbcV);
        gbcV.gridy = 2;
        panelVolumen.add(crearEtiqueta("Porcentaje de descuento (%):"), gbcV);
        JSpinner spinnerPorcentajeVol = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        spinnerPorcentajeVol.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbcV.gridy = 3;
        panelVolumen.add(spinnerPorcentajeVol, gbcV);
        panelParametros.add(panelVolumen, "Volumen");

        // ── Parámetros Categoría ──────────────────────────────────────────────
        // Necesita: categoría (desplegable) y porcentaje
        JPanel panelCategoria = new JPanel(new GridBagLayout());
        panelCategoria.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.gridx = 0; gbcC.fill = GridBagConstraints.HORIZONTAL;
        gbcC.weightx = 1;
        gbcC.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);
        gbcC.gridy = 0;
        panelCategoria.add(crearEtiqueta("Categoría afectada:"), gbcC);
        // Rellenamos el combo con las categorías de la tienda
        List<Categoria> categorias = controlador.getCategorias();
        String[] nombresCats = new String[categorias.size()];
        for (int i = 0; i < categorias.size(); i++) {
            nombresCats[i] = categorias.get(i).getNombre();
        }
        JComboBox<String> comboCat = new JComboBox<>(nombresCats);
        comboCat.setFont(VentanaPrincipal.FUENTE_NORMAL);
        comboCat.setBackground(Color.WHITE);
        gbcC.gridy = 1;
        panelCategoria.add(comboCat, gbcC);
        gbcC.gridy = 2;
        panelCategoria.add(crearEtiqueta("Porcentaje de descuento (%):"), gbcC);
        JSpinner spinnerPorcentajeCat = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        spinnerPorcentajeCat.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbcC.gridy = 3;
        panelCategoria.add(spinnerPorcentajeCat, gbcC);
        panelParametros.add(panelCategoria, "Categoría");

        // ── Parámetros Cantidad ───────────────────────────────────────────────
        // Necesita: cantidad mínima y porcentaje
        JPanel panelCantidad = new JPanel(new GridBagLayout());
        panelCantidad.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints gbcCant = new GridBagConstraints();
        gbcCant.gridx = 0; gbcCant.fill = GridBagConstraints.HORIZONTAL;
        gbcCant.weightx = 1;
        gbcCant.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);
        gbcCant.gridy = 0;
        panelCantidad.add(crearEtiqueta("Cantidad mínima de unidades:"), gbcCant);
        JSpinner spinnerCantidadMin = new JSpinner(new SpinnerNumberModel(2, 2, 999, 1));
        spinnerCantidadMin.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbcCant.gridy = 1;
        panelCantidad.add(spinnerCantidadMin, gbcCant);
        gbcCant.gridy = 2;
        // Combo para elegir el producto al que aplica
        panelCantidad.add(crearEtiqueta("Producto al que aplica:"), gbcCant);
        List<ProductoVenta> productos = controlador.getProductos();
        String[] nombresProds = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            nombresProds[i] = productos.get(i).getNombre() + " (" + productos.get(i).getId() + ")";
        }
        JComboBox<String> comboProdCant = new JComboBox<>(nombresProds);
        comboProdCant.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        comboProdCant.setBackground(Color.WHITE);
        gbcCant.gridy = 3;
        panelCantidad.add(comboProdCant, gbcCant);
        gbcCant.gridy = 4;
        panelCantidad.add(crearEtiqueta("Porcentaje de descuento (%):"), gbcCant);
        JSpinner spinnerPorcentajeCant = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        spinnerPorcentajeCant.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbcCant.gridy = 5;
        panelCantidad.add(spinnerPorcentajeCant, gbcCant);
        panelParametros.add(panelCantidad, "Cantidad");

        // ── Parámetros Regalo ─────────────────────────────────────────────────
        // Necesita: producto regalado (desplegable) y gasto mínimo
        JPanel panelRegalo = new JPanel(new GridBagLayout());
        panelRegalo.setBackground(VentanaPrincipal.COLOR_PANEL);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.gridx = 0; gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.weightx = 1;
        gbcR.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);
        gbcR.gridy = 0;
        panelRegalo.add(crearEtiqueta("Producto regalado:"), gbcR);
        JComboBox<String> comboProdRegalo = new JComboBox<>(nombresProds);
        comboProdRegalo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        comboProdRegalo.setBackground(Color.WHITE);
        gbcR.gridy = 1;
        panelRegalo.add(comboProdRegalo, gbcR);
        gbcR.gridy = 2;
        panelRegalo.add(crearEtiqueta("Gasto mínimo para el regalo (€):"), gbcR);
        JSpinner spinnerGastoRegalo = new JSpinner(new SpinnerNumberModel(36.0, 35.01, 9999.0, 1.0));
        spinnerGastoRegalo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        gbcR.gridy = 3;
        panelRegalo.add(spinnerGastoRegalo, gbcR);
        panelParametros.add(panelRegalo, "Regalo");

        // Cuando cambia el tipo, mostramos los parámetros correspondientes
        comboTipo.addActionListener(e -> {
            String tipo = (String) comboTipo.getSelectedItem();
            cardParametros.show(panelParametros, tipo);
        });

        gbc.gridy = 6;
        panel.add(panelParametros, gbc);

        // Botón crear descuento
        JButton botonCrear = crearBoton("Crear descuento");
        botonCrear.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            String tipo = (String) comboTipo.getSelectedItem();
            int dias = (int) spinnerDias.getValue();
            LocalDateTime inicio = LocalDateTime.now();
            LocalDateTime fin = inicio.plusDays(dias);
            boolean ok = false;

            switch (tipo) {
                case "Volumen":
                    double gastoMin = ((Number) spinnerGastoMin.getValue()).doubleValue();
                    double pctVol = ((Number) spinnerPorcentajeVol.getValue()).doubleValue();
                    ok = controlador.crearDescuentoVolumen(nombre, gastoMin, pctVol, inicio, fin);
                    break;
                case "Categoría":
                    String cat = (String) comboCat.getSelectedItem();
                    double pctCat = ((Number) spinnerPorcentajeCat.getValue()).doubleValue();
                    ok = controlador.crearDescuentoCategoria(nombre, cat, pctCat, inicio, fin);
                    break;
                case "Cantidad":
                    int cantMin = (int) spinnerCantidadMin.getValue();
                    double pctCant = ((Number) spinnerPorcentajeCant.getValue()).doubleValue();
                    int idxProdCant = comboProdCant.getSelectedIndex();
                    String idProdCant = idxProdCant >= 0 ? productos.get(idxProdCant).getId() : "";
                    ok = controlador.crearDescuentoCantidad(nombre, idProdCant, cantMin, pctCant, inicio, fin);
                    break;
                case "Regalo":
                    int idxProdRegalo = comboProdRegalo.getSelectedIndex();
                    String idProdRegalo = idxProdRegalo >= 0 ? productos.get(idxProdRegalo).getId() : "";
                    double gastoRegalo = ((Number) spinnerGastoRegalo.getValue()).doubleValue();
                    ok = controlador.crearDescuentoRegalo(nombre, idProdRegalo, gastoRegalo, inicio, fin);
                    break;
            }

            if (ok) {
                mostrarExito("Descuento '" + nombre + "' creado correctamente.");
                campoNombre.setText("");
                actualizarDescuentos();
            } else {
                mostrarError("No se pudo crear el descuento. Comprueba los datos.");
            }
        });
        gbc.gridy = 7;
        gbc.insets = new Insets(VentanaPrincipal.escalar(15), 0, 0, 0);
        panel.add(botonCrear, gbc);

        return panel;
    }

    /**
     * Actualiza la lista de productos con sus precios actuales.
     */
    private void actualizarProductos() {
        panelProductos.removeAll();
        for (ProductoVenta p : controlador.getProductos()) {
            JPanel fila = new JPanel(new GridBagLayout());
            fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(55)));
            fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
                BorderFactory.createEmptyBorder(
                    VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
                    VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15))
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(5));
            gbc.anchor = GridBagConstraints.WEST;

            // Nombre
            JLabel labelNombre = new JLabel(p.getNombre());
            labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
            labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
            gbc.gridx = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            fila.add(labelNombre, gbc);

            // Precio actual
            JLabel labelPrecio = new JLabel(String.format("%.2f€", p.getPrecioOficial()));
            labelPrecio.setFont(VentanaPrincipal.FUENTE_PRECIO);
            labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
            gbc.gridx = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            fila.add(labelPrecio, gbc);

            // Spinner nuevo precio
            JSpinner spinnerPrecio = new JSpinner(
                new SpinnerNumberModel(p.getPrecioOficial(), 0.01, 9999.0, 0.5));
            spinnerPrecio.setFont(VentanaPrincipal.FUENTE_NORMAL);
            spinnerPrecio.setPreferredSize(new Dimension(
                VentanaPrincipal.escalar(90), VentanaPrincipal.escalar(28)));
            gbc.gridx = 2;
            fila.add(spinnerPrecio, gbc);

            // Botón cambiar precio
            JButton boton = crearBoton("Cambiar");
            boton.addActionListener(e -> {
                double nuevo = ((Number) spinnerPrecio.getValue()).doubleValue();
                if (controlador.modificarPrecio(p.getId(), nuevo)) {
                    mostrarExito("Precio de " + p.getNombre() + " actualizado.");
                    actualizarProductos();
                } else {
                    mostrarError("No se pudo modificar el precio.");
                }
            });
            gbc.gridx = 3;
            fila.add(boton, gbc);

            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelProductos.add(fila);
        }
        panelProductos.revalidate();
        panelProductos.repaint();
    }

    /**
     * Actualiza la lista de descuentos activos mostrada.
     */
    private void actualizarDescuentos() {
        panelDescuentos.removeAll();

        JLabel titulo = new JLabel("Descuentos activos:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(5), 0));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDescuentos.add(titulo);

        List<Descuento> descuentos = controlador.getDescuentosActivos();
        if (descuentos.isEmpty()) {
            JLabel labelVacio = new JLabel("No hay descuentos activos.");
            labelVacio.setFont(VentanaPrincipal.FUENTE_NORMAL);
            labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            labelVacio.setBorder(BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, 0));
            labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelDescuentos.add(labelVacio);
        } else {
            for (Descuento d : descuentos) {
                JPanel fila = new JPanel(new BorderLayout());
                fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(45)));
                fila.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
                    BorderFactory.createEmptyBorder(
                        VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
                        VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15))
                ));

                JLabel labelDesc = new JLabel(d.getNombre() + " — " + d.getId()
                    + " | hasta: " + d.getFechaFin().toLocalDate());
                labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
                labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO);
                fila.add(labelDesc, BorderLayout.CENTER);

                JButton botonEliminar = crearBotonRojo("Eliminar");
                botonEliminar.addActionListener(e -> {
                    if (controlador.eliminarDescuento(d.getId())) {
                        mostrarExito("Descuento eliminado.");
                        actualizarDescuentos();
                    } else {
                        mostrarError("No se pudo eliminar.");
                    }
                });
                fila.add(botonEliminar, BorderLayout.EAST);

                fila.setAlignmentX(Component.LEFT_ALIGNMENT);
                panelDescuentos.add(fila);
            }
        }

        panelDescuentos.revalidate();
        panelDescuentos.repaint();
    }

    /**
     * Devuelve las categorías de la tienda para el controlador de descuentos.
     */
    private List<Categoria> getCategorias() {
        return controlador.getCategorias();
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))
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
            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12)));
        return boton;
    }

    private JButton crearBotonRojo(String texto) {
        JButton boton = crearBoton(texto);
        boton.setBackground(new Color(180, 50, 50));
        return boton;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}