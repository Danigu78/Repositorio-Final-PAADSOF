package Gui;

import Gui.Controladores.ControladorPacksGestor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import usuarios.Gestor;

public class SubpanelGestionPack extends JPanel {

    private static final long serialVersionUID = 1L;
    private VentanaPrincipal ventana;
    private Gestor gestor;
    private ControladorPacksGestor controlador;
    private JPanel panelPacks;
    private List<LineaPack> lineasTemporales = new ArrayList<>();
    private DefaultTableModel modeloTablaTemporal;
    private JTable tablaTemporal;

    /**
     * Constructor del subpanel de gestión de packs.
     *
     * @param ventana La ventana principal
     * @param gestor  El gestor logueado
     */
    public SubpanelGestionPack(VentanaPrincipal ventana, Gestor gestor) {
        this.ventana = ventana;
        this.gestor = gestor;
        this.controlador = new ControladorPacksGestor(gestor);
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    /**
     * Construye la interfaz con formulario arriba y lista de packs abajo.
     * Si no hay empleado con permisos de packs muestra un aviso.
     */
    private void inicializarUI() {
        if (!controlador.hayEmpleadoDisponible()) {
            JLabel aviso = new JLabel(
                "<html><center>No hay empleados con permisos de GESTION_PACKS.<br>"
                + "Asigna el permiso a un empleado desde la sección Empleados.</center></html>",
                SwingConstants.CENTER);
            aviso.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
            aviso.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            add(aviso, BorderLayout.CENTER);
            return;
        }

        add(crearSeccionCreacion(), BorderLayout.NORTH);

        panelPacks = new JPanel();
        panelPacks.setLayout(new BoxLayout(panelPacks, BoxLayout.Y_AXIS));
        panelPacks.setBackground(VentanaPrincipal.COLOR_FONDO);

        JScrollPane scroll = new JScrollPane(panelPacks);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        add(scroll, BorderLayout.CENTER);

        actualizarPacks();
    }

    private JPanel crearSeccionCreacion() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(VentanaPrincipal.COLOR_PANEL);

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField campoNombre = crearCampo(10);
        JTextField campoDesc = crearCampo(10);
        JTextField campoImagen = crearCampo(10);
        JSpinner spinnerPrecio = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 9999.0, 0.5));
        JSpinner spinnerStock = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0; panelSuperior.add(crearEtiqueta("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelSuperior.add(campoNombre, gbc);
        gbc.gridx = 2; gbc.weightx = 0; panelSuperior.add(crearEtiqueta("Descripción:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1; panelSuperior.add(campoDesc, gbc);
        gbc.gridx = 4; gbc.weightx = 0; panelSuperior.add(crearEtiqueta("Imagen:"), gbc);
        gbc.gridx = 5; gbc.weightx = 1; panelSuperior.add(campoImagen, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0; panelSuperior.add(crearEtiqueta("Precio (€):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panelSuperior.add(spinnerPrecio, gbc);
        gbc.gridx = 2; gbc.weightx = 0; panelSuperior.add(crearEtiqueta("Stock:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1; panelSuperior.add(spinnerStock, gbc);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentral.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        panelIzquierdo.setBackground(VentanaPrincipal.COLOR_TARJETA);
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE), "Añadir al Pack"));
        
        List<ProductoVenta> productos = controlador.getProductos();
        JComboBox<ProductoVenta> combo = new JComboBox<>(new DefaultComboBoxModel<>(productos.toArray(new ProductoVenta[0])));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductoVenta) {
                    ProductoVenta p = (ProductoVenta) value;
                    setText(p.getNombre() + " (" + p.getId() + ")");
                }
                return this;
            }
        });

        JSpinner spinnerUnidades = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JButton btnAnadir = crearBoton("Añadir Producto");

        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.insets = new Insets(8, 8, 8, 8);
        gbcL.fill = GridBagConstraints.HORIZONTAL;
        gbcL.gridx = 0; gbcL.gridy = 0; panelIzquierdo.add(crearEtiqueta("Producto:"), gbcL);
        gbcL.gridx = 1; gbcL.weightx = 1; panelIzquierdo.add(combo, gbcL);
        gbcL.gridx = 0; gbcL.gridy = 1; gbcL.weightx = 0; panelIzquierdo.add(crearEtiqueta("Unidades:"), gbcL);
        gbcL.gridx = 1; gbcL.weightx = 1; panelIzquierdo.add(spinnerUnidades, gbcL);
        gbcL.gridx = 0; gbcL.gridy = 2; gbcL.gridwidth = 2; panelIzquierdo.add(btnAnadir, gbcL);

        modeloTablaTemporal = new DefaultTableModel(new String[]{"Producto", "Unidades", "Editar"}, 0);
        tablaTemporal = new JTable(modeloTablaTemporal);
        JScrollPane scrollTabla = new JScrollPane(tablaTemporal);
        scrollTabla.setPreferredSize(new Dimension(300, 150));

        panelCentral.add(panelIzquierdo);
        panelCentral.add(scrollTabla);

        JButton btnCrear = crearBoton("Crear pack");
        btnCrear.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if(!nombre.isEmpty() && !lineasTemporales.isEmpty()){
                if(controlador.crearPack(nombre, campoDesc.getText().trim(), campoImagen.getText().trim().isEmpty() ? "pack.jpg" : campoImagen.getText().trim(), (double)spinnerPrecio.getValue(), (int)spinnerStock.getValue(), new ArrayList<>(lineasTemporales))){
                    mostrarExito("Pack creado.");
                    lineasTemporales.clear();
                    modeloTablaTemporal.setRowCount(0);
                    campoNombre.setText("");
                    actualizarPacks();
                }
            } else {
                mostrarError("Rellena el nombre y añade productos.");
            }
        });

        btnAnadir.addActionListener(e -> {
            ProductoVenta p = (ProductoVenta) combo.getSelectedItem();
            int cant = (int) spinnerUnidades.getValue();
            if (p != null) {
                lineasTemporales.add(new LineaPack(p, cant));
                modeloTablaTemporal.addRow(new Object[]{p.getNombre(), cant, "Quitar"});
            }
        });

        contenedor.add(panelSuperior, BorderLayout.NORTH);
        contenedor.add(panelCentral, BorderLayout.CENTER);
        contenedor.add(btnCrear, BorderLayout.SOUTH);

        return contenedor;
    }

    /**
     * Actualiza la lista de packs mostrada en el panel central.
     */
    private void actualizarPacks() {
        panelPacks.removeAll();
        JLabel titulo = new JLabel("Packs actuales:");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        panelPacks.add(titulo);

        for (Pack p : controlador.getPacks()) {
            panelPacks.add(crearFilaPack(p));
        }
        panelPacks.revalidate();
        panelPacks.repaint();
    }

    /**
     * Crea una fila visual para un pack con su información y botones de acción.
     * Usa GridBagLayout para que todo sea legible y no se corte.
     *
     * @param pack El pack a mostrar
     * @return Panel con la fila del pack
     */
    private JPanel crearFilaPack(Pack pack) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
        fila.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE), BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        fila.add(new JLabel(pack.getNombre() + " - " + pack.getPrecioOficial() + "€"), BorderLayout.CENTER);
        
        JButton btnEliminar = crearBotonRojo("Eliminar pack");
        btnEliminar.addActionListener(e -> {
            if(controlador.eliminarPack(pack.getId())) actualizarPacks();
        });
        fila.add(btnEliminar, BorderLayout.EAST);
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
        campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return campo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setForeground(Color.WHITE);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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