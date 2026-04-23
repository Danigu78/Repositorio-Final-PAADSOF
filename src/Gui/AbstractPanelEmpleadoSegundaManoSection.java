package Gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import productos.EstadoProducto;
import productos.Producto2Mano;
import tienda.FiltroSegundaMano;
import tienda.Tienda;
import usuarios.Empleado;

public abstract class AbstractPanelEmpleadoSegundaManoSection extends AbstractPanelEmpleadoSection {
    private static final long serialVersionUID = 1L;

    protected static final int COL_2MANO_ID = 0;
    protected static final int COL_2MANO_NOMBRE = 1;
    protected static final int COL_2MANO_VALOR = 2;
    protected static final int COL_2MANO_ESTADO = 3;
    protected static final int COL_2MANO_VISIBLE = 4;
    protected static final int COL_2MANO_BLOQUEADO = 5;

    protected static class TablaSegundaManoData {
        JTable tabla;
        DefaultTableModel modelo;
        TableRowSorter<DefaultTableModel> sorter;

        TablaSegundaManoData(JTable tabla, DefaultTableModel modelo, TableRowSorter<DefaultTableModel> sorter) {
            this.tabla = tabla;
            this.modelo = modelo;
            this.sorter = sorter;
        }
    }

    protected static class SelectorSegundaMano {
        JPanel bloque;
        JTable tabla;

        SelectorSegundaMano(JPanel bloque, JTable tabla) {
            this.bloque = bloque;
            this.tabla = tabla;
        }
    }

    protected AbstractPanelEmpleadoSegundaManoSection(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
    }

    protected SelectorSegundaMano crearSelectorProductosSegundaMano(String titulo, String ayuda, boolean incluirRefrescar,
            JTextField... camposIdDestino) {

        JPanel bloque = crearBloque(titulo);
        TablaSegundaManoData data = crearTablaProductos2ManoData();

        JLabel ayudaLabel = crearLabel(ayuda);
        ayudaLabel.setForeground(VentanaPrincipal.COLOR_TEXTO2);

        javax.swing.JScrollPane scrollTabla = estilizarScroll(data.tabla);
        scrollTabla.setPreferredSize(new Dimension(1050, 300));

        conectarSeleccionId(data.tabla, COL_2MANO_ID, camposIdDestino);

        bloque.add(ayudaLabel, gbcCampo(1));
        bloque.add(crearPanelFiltros2Mano(data.sorter), gbcCampo(2));
        bloque.add(scrollTabla, gbcCampo(3));

        if (incluirRefrescar) {
            JButton botonRefrescar = crearBotonAccion("Refrescar lista");
            botonRefrescar.addActionListener(e -> recargarTablaProductos2Mano(data.tabla));
            bloque.add(botonRefrescar, gbcBoton(4));
        }

        return new SelectorSegundaMano(bloque, data.tabla);
    }

    protected void conectarSeleccionId(JTable tabla, int columnaId, JTextField... camposDestino) {
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int filaVista = tabla.getSelectedRow();
            if (filaVista < 0) return;

            int filaModelo = tabla.convertRowIndexToModel(filaVista);
            Object valor = tabla.getModel().getValueAt(filaModelo, columnaId);
            if (valor == null) return;

            String id = String.valueOf(valor);
            for (JTextField campo : camposDestino) {
                if (campo != null) campo.setText(id);
            }
        });
    }

    protected TablaSegundaManoData crearTablaProductos2ManoData() {
        String[] columnas = { "ID", "Nombre", "Valor tasación", "Estado", "Visible", "Bloqueado" };

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override public boolean isCellEditable(int row, int column) { return false; }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == COL_2MANO_VALOR ? Double.class : String.class;
            }
        };

        cargarModeloProductos2Mano(modelo);

        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        estilizarTablaBase(tabla);

        DefaultTableCellRenderer textoRenderer = crearRendererTabla(SwingConstants.LEFT, false);
        DefaultTableCellRenderer precioRenderer = crearRendererTabla(SwingConstants.RIGHT, true);
        DefaultTableCellRenderer centroRenderer = crearRendererTabla(SwingConstants.CENTER, false);

        tabla.setDefaultRenderer(String.class, textoRenderer);
        tabla.setDefaultRenderer(Double.class, precioRenderer);

        tabla.getColumnModel().getColumn(COL_2MANO_VALOR).setCellRenderer(precioRenderer);
        tabla.getColumnModel().getColumn(COL_2MANO_ESTADO).setCellRenderer(centroRenderer);
        tabla.getColumnModel().getColumn(COL_2MANO_VISIBLE).setCellRenderer(centroRenderer);
        tabla.getColumnModel().getColumn(COL_2MANO_BLOQUEADO).setCellRenderer(centroRenderer);

        tabla.getColumnModel().getColumn(COL_2MANO_ID).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(COL_2MANO_NOMBRE).setPreferredWidth(260);
        tabla.getColumnModel().getColumn(COL_2MANO_VALOR).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(COL_2MANO_ESTADO).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(COL_2MANO_VISIBLE).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(COL_2MANO_BLOQUEADO).setPreferredWidth(100);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        return new TablaSegundaManoData(tabla, modelo, sorter);
    }

    protected void estilizarTablaBase(JTable tabla) {
        tabla.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        tabla.setRowHeight(30);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(VentanaPrincipal.COLOR_TEXTO);
        tabla.setGridColor(new Color(225, 225, 225));
        tabla.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
        tabla.setSelectionForeground(VentanaPrincipal.COLOR_TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.setShowVerticalLines(true);
        tabla.setShowHorizontalLines(true);
        tabla.setOpaque(true);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        header.setBackground(new Color(232, 232, 232));
        header.setForeground(VentanaPrincipal.COLOR_TEXTO);
        header.setBorder(javax.swing.BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
    }

    protected DefaultTableCellRenderer crearRendererTabla(int alignment, boolean formatoEuro) {
        return new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setOpaque(true);
                c.setHorizontalAlignment(alignment);

                if (formatoEuro && value instanceof Number) {
                    c.setText(String.format(Locale.US, "%.2f €", ((Number) value).doubleValue()).replace('.', ','));
                } else {
                    c.setText(value == null ? "" : String.valueOf(value));
                }

                if (isSelected) {
                    c.setBackground(VentanaPrincipal.COLOR_ACENTO);
                    c.setForeground(VentanaPrincipal.COLOR_TEXTO);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(246, 246, 246));
                    c.setForeground(VentanaPrincipal.COLOR_TEXTO);
                }

                c.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
    }

    protected JPanel crearPanelFiltros2Mano(TableRowSorter<DefaultTableModel> sorter) {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setOpaque(false);

        JTextField campoBuscar = crearCampoCompacto();
        JTextField campoValorMin = crearCampoCompacto();
        JTextField campoValorMax = crearCampoCompacto();

        JComboBox<Object> comboEstado = new JComboBox<>();
        comboEstado.addItem("Cualquiera");
        for (EstadoProducto estado : EstadoProducto.values()) comboEstado.addItem(estado);
        comboEstado.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        comboEstado.setBackground(new Color(24, 24, 24));
        comboEstado.setForeground(VentanaPrincipal.COLOR_TEXTO);
        comboEstado.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(85, 85, 85), 1));

        JCheckBox checkIncluirSinValorar = new JCheckBox("Incluir sin valorar");
        checkIncluirSinValorar.setOpaque(false);
        checkIncluirSinValorar.setForeground(VentanaPrincipal.COLOR_TEXTO);
        checkIncluirSinValorar.setSelected(true);

        JButton botonLimpiar = crearBotonSecundario("Limpiar filtros");

        panel.add(crearCampoFormulario("Buscar (ID, nombre, estado...)", campoBuscar), gbcFiltro(0, 0));
        panel.add(crearCampoFormulario("Valor mínimo", campoValorMin), gbcFiltro(1, 0));
        panel.add(crearCampoFormulario("Valor máximo", campoValorMax), gbcFiltro(2, 0));
        panel.add(crearCampoFormulario("Estado mínimo", comboEstado), gbcFiltro(3, 0));
        panel.add(crearCampoFormulario(" ", checkIncluirSinValorar), gbcFiltro(0, 1));
        panel.add(crearCampoFormulario(" ", botonLimpiar), gbcFiltro(1, 1));

        Runnable aplicar = () -> aplicarFiltroSegundaMano(sorter, campoBuscar, campoValorMin, campoValorMax,
                comboEstado, checkIncluirSinValorar);

        escucharCambios(campoBuscar, aplicar);
        escucharCambios(campoValorMin, aplicar);
        escucharCambios(campoValorMax, aplicar);
        comboEstado.addActionListener(e -> aplicar.run());
        checkIncluirSinValorar.addActionListener(e -> aplicar.run());

        botonLimpiar.addActionListener(e -> {
            campoBuscar.setText("");
            campoValorMin.setText("");
            campoValorMax.setText("");
            comboEstado.setSelectedIndex(0);
            checkIncluirSinValorar.setSelected(true);
            aplicar.run();
        });

        aplicar.run();
        return panel;
    }

    protected void aplicarFiltroSegundaMano(TableRowSorter<DefaultTableModel> sorter, JTextField campoBuscar,
            JTextField campoValorMin, JTextField campoValorMax, JComboBox<Object> comboEstado,
            JCheckBox checkIncluirSinValorar) {

        String texto = normalizarTexto(campoBuscar.getText());
        Double valorMin = leerDoubleSeguro(campoValorMin.getText());
        Double valorMax = leerDoubleSeguro(campoValorMax.getText());
        boolean incluirSinValorar = checkIncluirSinValorar.isSelected();

        FiltroSegundaMano filtro = new FiltroSegundaMano();
        if (valorMin != null) filtro.setValorMinimo(valorMin);
        if (valorMax != null) filtro.setValorMaximo(valorMax);

        Object sel = comboEstado.getSelectedItem();
        if (sel instanceof EstadoProducto) filtro.setEstadoMinimo((EstadoProducto) sel);

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String id = String.valueOf(entry.getValue(COL_2MANO_ID));
                Producto2Mano producto = buscarProducto2ManoPorId(id);
                if (producto == null) return false;
                if (!texto.isBlank() && !coincideBusquedaProducto2Mano(producto, texto)) return false;
                if (!producto.isVisible() || producto.isBloqueado()) return false;
                if (producto.getValoracion() == null) return incluirSinValorar;
                return filtro.cumpleFiltro(producto);
            }
        });
    }

    protected boolean coincideBusquedaProducto2Mano(Producto2Mano producto, String texto) {
        return contieneTexto(producto.getId(), texto)
                || contieneTexto(producto.getNombre(), texto)
                || contieneTexto(obtenerEstadoTextoProducto2Mano(producto), texto);
    }

    protected List<Producto2Mano> obtenerProductosSegundaManoOrdenados() {
        ArrayList<Producto2Mano> productos = new ArrayList<>(obtenerProductosSegundaMano());
        productos.sort(Comparator.comparing((Producto2Mano p) -> p.getValoracion() != null)
                .thenComparing(Producto2Mano::getNombre, String.CASE_INSENSITIVE_ORDER));
        return productos;
    }

    protected List<Producto2Mano> obtenerProductosSegundaMano() {
        return new ArrayList<>(Tienda.getInstancia().buscarSegundaMano());
    }

    protected Producto2Mano buscarProducto2ManoPorId(String id) {
        if (id == null || id.isBlank()) return null;
        for (Producto2Mano p : obtenerProductosSegundaMano()) {
            if (p.getId() != null && p.getId().equalsIgnoreCase(id.trim())) return p;
        }
        return null;
    }

    protected Double obtenerValorTasacionProducto2Mano(Producto2Mano p) {
        if (p == null || p.getValoracion() == null) return null;
        return p.getValoracion().getPrecioTasacion();
    }

    protected String obtenerEstadoTextoProducto2Mano(Producto2Mano p) {
        if (p == null || p.getValoracion() == null) return "Sin valorar";
        EstadoProducto estado = p.getValoracion().getEstadoProducto();
        return estado == null ? "Sin estado" : estado.name();
    }

    protected void cargarModeloProductos2Mano(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Producto2Mano p : obtenerProductosSegundaManoOrdenados()) {
            modelo.addRow(new Object[] {
                    p.getId(),
                    p.getNombre(),
                    obtenerValorTasacionProducto2Mano(p),
                    obtenerEstadoTextoProducto2Mano(p),
                    p.isVisible() ? "Sí" : "No",
                    p.isBloqueado() ? "Sí" : "No"
            });
        }
    }

    protected void recargarTablaProductos2Mano(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        cargarModeloProductos2Mano(modelo);
    }
}
