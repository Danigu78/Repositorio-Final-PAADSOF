package Gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.JButton;
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

import productos.Categoria;
import productos.LineaPack;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;

public abstract class AbstractPanelEmpleadoVentaSection extends AbstractPanelEmpleadoSection {
    private static final long serialVersionUID = 1L;

    protected static final int COL_VENTA_ID = 0;
    protected static final int COL_VENTA_NOMBRE = 1;
    protected static final int COL_VENTA_TIPO = 2;
    protected static final int COL_VENTA_CATEGORIAS = 3;
    protected static final int COL_VENTA_PRECIO = 4;
    protected static final int COL_VENTA_STOCK = 5;
    protected static final int COL_VENTA_PUNTUACION = 6;

    protected static class TablaVentaData {
        JTable tabla;
        DefaultTableModel modelo;
        TableRowSorter<DefaultTableModel> sorter;

        TablaVentaData(JTable tabla, DefaultTableModel modelo, TableRowSorter<DefaultTableModel> sorter) {
            this.tabla = tabla;
            this.modelo = modelo;
            this.sorter = sorter;
        }
    }

    protected static class SelectorVenta {
        JPanel bloque;
        JTable tabla;

        SelectorVenta(JPanel bloque, JTable tabla) {
            this.bloque = bloque;
            this.tabla = tabla;
        }
    }

    protected AbstractPanelEmpleadoVentaSection(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
    }

    protected SelectorVenta crearSelectorProductosVenta(String titulo, String ayuda, boolean incluirRefrescar,
            JTextField... camposIdDestino) {

        JPanel bloque = crearBloque(titulo);
        TablaVentaData data = crearTablaProductosVentaData();

        JLabel ayudaLabel = crearLabel(ayuda);
        ayudaLabel.setForeground(VentanaPrincipal.COLOR_TEXTO2);

        javax.swing.JScrollPane scrollTabla = estilizarScroll(data.tabla);
        scrollTabla.setPreferredSize(new Dimension(1050, 300));

        conectarSeleccionId(data.tabla, COL_VENTA_ID, camposIdDestino);

        bloque.add(ayudaLabel, gbcCampo(1));
        bloque.add(crearPanelFiltrosVenta(data.sorter), gbcCampo(2));
        bloque.add(scrollTabla, gbcCampo(3));

        if (incluirRefrescar) {
            JButton botonRefrescar = crearBotonAccion("Refrescar lista");
            botonRefrescar.addActionListener(e -> recargarTablaProductos(data.tabla));
            bloque.add(botonRefrescar, gbcBoton(4));
        }

        return new SelectorVenta(bloque, data.tabla);
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

    protected TablaVentaData crearTablaProductosVentaData() {
        String[] columnas = { "ID", "Nombre", "Tipo", "Categorías", "Precio", "Stock", "Puntuación" };

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) { return false; }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case COL_VENTA_PRECIO:
                    case COL_VENTA_PUNTUACION:
                        return Double.class;
                    case COL_VENTA_STOCK:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };

        cargarModeloProductosVenta(modelo);

        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        estilizarTablaBase(tabla);

        DefaultTableCellRenderer textoRenderer = crearRendererTabla(SwingConstants.LEFT, false, false);
        DefaultTableCellRenderer precioRenderer = crearRendererTabla(SwingConstants.RIGHT, true, false);
        DefaultTableCellRenderer enteroRenderer = crearRendererTabla(SwingConstants.CENTER, false, false);
        DefaultTableCellRenderer decimalRenderer = crearRendererTabla(SwingConstants.CENTER, false, true);

        tabla.setDefaultRenderer(String.class, textoRenderer);
        tabla.setDefaultRenderer(Integer.class, enteroRenderer);
        tabla.setDefaultRenderer(Double.class, decimalRenderer);

        tabla.getColumnModel().getColumn(COL_VENTA_PRECIO).setCellRenderer(precioRenderer);
        tabla.getColumnModel().getColumn(COL_VENTA_STOCK).setCellRenderer(enteroRenderer);
        tabla.getColumnModel().getColumn(COL_VENTA_PUNTUACION).setCellRenderer(decimalRenderer);

        tabla.getColumnModel().getColumn(COL_VENTA_ID).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(COL_VENTA_NOMBRE).setPreferredWidth(240);
        tabla.getColumnModel().getColumn(COL_VENTA_TIPO).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(COL_VENTA_CATEGORIAS).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(COL_VENTA_PRECIO).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(COL_VENTA_STOCK).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(COL_VENTA_PUNTUACION).setPreferredWidth(90);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        return new TablaVentaData(tabla, modelo, sorter);
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

    protected DefaultTableCellRenderer crearRendererTabla(int alignment, boolean formatoEuro, boolean formatoDecimal) {
        return new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setOpaque(true);
                c.setHorizontalAlignment(alignment);

                if (formatoEuro) {
                    if (value instanceof Number) {
                        c.setText(String.format(Locale.US, "%.2f €", ((Number) value).doubleValue()).replace('.', ','));
                    } else {
                        c.setText(value == null ? "Sin valorar" : String.valueOf(value));
                    }
                } else if (formatoDecimal) {
                    if (value instanceof Number) {
                        c.setText(String.format(Locale.US, "%.2f", ((Number) value).doubleValue()).replace('.', ','));
                    } else {
                        c.setText(value == null ? "" : String.valueOf(value));
                    }
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

    protected JPanel crearPanelFiltrosVenta(TableRowSorter<DefaultTableModel> sorter) {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setOpaque(false);

        JTextField campoBuscar = crearCampoCompacto();
        JComboBox<String> comboTipo = crearCombo(new String[] { "Todos", "Comic", "Juego", "Figura", "Pack" });

        JTextField campoPrecioMin = crearCampoCompacto();
        JTextField campoPrecioMax = crearCampoCompacto();
        JTextField campoStockMin = crearCampoCompacto();
        JTextField campoStockMax = crearCampoCompacto();
        JTextField campoPuntuacionMin = crearCampoCompacto();

        List<String> nombresCategorias = obtenerNombresCategoriasVenta();
        String[] valoresCombo = new String[nombresCategorias.size() + 1];
        valoresCombo[0] = "Todas";
        for (int i = 0; i < nombresCategorias.size(); i++) valoresCombo[i + 1] = nombresCategorias.get(i);
        JComboBox<String> comboCategoria = crearCombo(valoresCombo);

        JButton botonLimpiar = crearBotonSecundario("Limpiar filtros");

        panel.add(crearCampoFormulario("Buscar (ID, nombre...)", campoBuscar), gbcFiltro(0, 0));
        panel.add(crearCampoFormulario("Tipo", comboTipo), gbcFiltro(1, 0));
        panel.add(crearCampoFormulario("Categoría", comboCategoria), gbcFiltro(2, 0));
        panel.add(crearCampoFormulario("Precio mínimo", campoPrecioMin), gbcFiltro(3, 0));
        panel.add(crearCampoFormulario("Precio máximo", campoPrecioMax), gbcFiltro(4, 0));

        panel.add(crearCampoFormulario("Stock mínimo", campoStockMin), gbcFiltro(0, 1));
        panel.add(crearCampoFormulario("Stock máximo", campoStockMax), gbcFiltro(1, 1));
        panel.add(crearCampoFormulario("Puntuación mínima", campoPuntuacionMin), gbcFiltro(2, 1));
        panel.add(crearCampoFormulario(" ", botonLimpiar), gbcFiltro(3, 1));

        Runnable aplicar = () -> aplicarFiltroVenta(sorter, campoBuscar, comboTipo, comboCategoria,
                campoPrecioMin, campoPrecioMax, campoStockMin, campoStockMax, campoPuntuacionMin);

        escucharCambios(campoBuscar, aplicar);
        escucharCambios(campoPrecioMin, aplicar);
        escucharCambios(campoPrecioMax, aplicar);
        escucharCambios(campoStockMin, aplicar);
        escucharCambios(campoStockMax, aplicar);
        escucharCambios(campoPuntuacionMin, aplicar);

        comboTipo.addActionListener(e -> aplicar.run());
        comboCategoria.addActionListener(e -> aplicar.run());

        botonLimpiar.addActionListener(e -> {
            campoBuscar.setText("");
            comboTipo.setSelectedIndex(0);
            campoPrecioMin.setText("");
            campoPrecioMax.setText("");
            campoStockMin.setText("");
            campoStockMax.setText("");
            campoPuntuacionMin.setText("");
            comboCategoria.setSelectedIndex(0);
            sorter.setRowFilter(null);
        });

        aplicar.run();
        return panel;
    }

    protected void aplicarFiltroVenta(TableRowSorter<DefaultTableModel> sorter, JTextField campoBuscar,
            JComboBox<String> comboTipo, JComboBox<String> comboCategoria, JTextField campoPrecioMin,
            JTextField campoPrecioMax, JTextField campoStockMin, JTextField campoStockMax,
            JTextField campoPuntuacionMin) {

        final String texto = normalizarTexto(campoBuscar.getText());
        final String tipoSeleccionado = comboTipo.getSelectedItem() != null
                ? normalizarTexto(comboTipo.getSelectedItem().toString())
                : "todos";

        final Double precioMin = leerDoubleSeguro(campoPrecioMin.getText());
        final Double precioMax = leerDoubleSeguro(campoPrecioMax.getText());
        final Double puntuacionMin = leerDoubleSeguro(campoPuntuacionMin.getText());
        final Integer stockMin = leerEnteroSeguro(campoStockMin.getText());
        final Integer stockMax = leerEnteroSeguro(campoStockMax.getText());

        final String categoriaSeleccionada = comboCategoria.getSelectedItem() != null
                ? normalizarTexto(comboCategoria.getSelectedItem().toString())
                : "todas";

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {

                String id = valorTexto(entry.getValue(COL_VENTA_ID));
                String nombre = valorTexto(entry.getValue(COL_VENTA_NOMBRE));
                String tipo = valorTexto(entry.getValue(COL_VENTA_TIPO));
                String categorias = valorTexto(entry.getValue(COL_VENTA_CATEGORIAS));

                Double precio = valorDouble(entry.getValue(COL_VENTA_PRECIO));
                Integer stock = valorInteger(entry.getValue(COL_VENTA_STOCK));
                Double puntuacion = valorDouble(entry.getValue(COL_VENTA_PUNTUACION));

                if (!texto.isBlank()) {
                    boolean coincide = contieneTexto(id, texto)
                            || contieneTexto(nombre, texto)
                            || contieneTexto(tipo, texto)
                            || contieneTexto(categorias, texto);
                    if (!coincide) return false;
                }

                if (!"todos".equals(tipoSeleccionado) && !contieneTexto(tipo, tipoSeleccionado)) return false;
                if (!"todas".equals(categoriaSeleccionada) && !contieneTexto(categorias, categoriaSeleccionada)) return false;

                if (precioMin != null && (precio == null || precio < precioMin)) return false;
                if (precioMax != null && (precio == null || precio > precioMax)) return false;

                if (stockMin != null && (stock == null || stock < stockMin)) return false;
                if (stockMax != null && (stock == null || stock > stockMax)) return false;

                if (puntuacionMin != null && (puntuacion == null || puntuacion < puntuacionMin)) return false;

                return true;
            }
        });
    }

    protected List<String> obtenerNombresCategoriasVenta() {
        TreeSet<String> nombres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
            if (p.getCategorias() == null) continue;
            for (Categoria c : p.getCategorias()) {
                if (c != null && c.getNombre() != null && !c.getNombre().isBlank()) {
                    nombres.add(c.getNombre().trim());
                }
            }
        }
        return new ArrayList<>(nombres);
    }

    protected String obtenerTextoCategorias(ProductoVenta p) {
        if (p == null || p.getCategorias() == null || p.getCategorias().isEmpty()) return "-";

        List<String> nombres = new ArrayList<>();
        for (Categoria c : p.getCategorias()) {
            if (c != null && c.getNombre() != null && !c.getNombre().isBlank()) {
                nombres.add(c.getNombre().trim());
            }
        }
        nombres.sort(String.CASE_INSENSITIVE_ORDER);
        return nombres.isEmpty() ? "-" : String.join(", ", nombres);
    }

    protected String obtenerTipoProductoVenta(ProductoVenta p) {
        if (p == null) return "";

        String tipo = p.getClass().getSimpleName();
        if ("JuegoMesa".equalsIgnoreCase(tipo)) return "Juego";
        if ("Comic".equalsIgnoreCase(tipo)) return "Comic";
        if ("Figura".equalsIgnoreCase(tipo)) return "Figura";
        if ("Pack".equalsIgnoreCase(tipo)) return "Pack";
        return tipo;
    }

    protected List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
        ArrayList<ProductoVenta> productos = new ArrayList<>(Tienda.getInstancia().getStockVentas());
        productos.sort(Comparator.comparingInt(ProductoVenta::getStockDisponible)
                .thenComparing(ProductoVenta::getNombre, String.CASE_INSENSITIVE_ORDER));
        return productos;
    }

    protected void cargarModeloProductosVenta(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
            modelo.addRow(new Object[] {
                    p.getId(),
                    p.getNombre(),
                    obtenerTipoProductoVenta(p),
                    obtenerTextoCategorias(p),
                    p.getPrecioOficial(),
                    p.getStockDisponible(),
                    p.getMediaPuntuacion()
            });
        }
    }

    protected void recargarTablaProductos(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        cargarModeloProductosVenta(modelo);
    }

    protected ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
        ArrayList<LineaPack> lineas = new ArrayList<>();
        String[] filas = texto.split("\\r?\\n");

        for (String fila : filas) {
            if (fila == null || fila.isBlank()) continue;

            String[] partes = fila.split(";");
            if (partes.length != 2) {
                throw new IllegalArgumentException("Cada línea debe tener formato ID;UNIDADES");
            }

            String idProducto = partes[0].trim();
            int unidades = Integer.parseInt(partes[1].trim());

            ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
            if (producto == null) {
                throw new IllegalArgumentException("No existe producto con id " + idProducto);
            }

            lineas.add(crearLineaPack(producto, unidades));
        }

        return lineas;
    }

    protected LineaPack crearLineaPack(ProductoVenta producto, int unidades) throws Exception {
        for (Constructor<?> c : LineaPack.class.getConstructors()) {
            Class<?>[] tipos = c.getParameterTypes();

            if (tipos.length == 2 && tipos[0].isAssignableFrom(producto.getClass())
                    && (tipos[1] == int.class || tipos[1] == Integer.class)) {
                return (LineaPack) c.newInstance(producto, unidades);
            }
            if (tipos.length == 2 && ProductoVenta.class.isAssignableFrom(tipos[0])
                    && (tipos[1] == int.class || tipos[1] == Integer.class)) {
                return (LineaPack) c.newInstance(producto, unidades);
            }
        }
        throw new IllegalStateException("No se encontró constructor válido para LineaPack.");
    }
}
