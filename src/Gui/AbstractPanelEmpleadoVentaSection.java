package Gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import productos.Categoria;
import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;
import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Clase base para las secciones del empleado que trabajan con productos de
 * venta.
 * 
 * Tiene una tabla común de productos, filtros sencillos y métodos auxiliares
 * para trabajar con packs.
 */
public abstract class AbstractPanelEmpleadoVentaSection extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	protected static final int COL_VENTA_ID = 0;
	protected static final int COL_VENTA_NOMBRE = 1;
	protected static final int COL_VENTA_TIPO = 2;
	protected static final int COL_VENTA_CATEGORIAS = 3;
	protected static final int COL_VENTA_PRECIO = 4;
	protected static final int COL_VENTA_STOCK = 5;
	protected static final int COL_VENTA_PUNTUACION = 6;

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

		DefaultTableModel modelo = crearModeloTablaVenta();
		JTable tabla = new JTable(modelo);

		estilizarTabla(tabla);
		cargarModeloProductosVenta(modelo);

		conectarSeleccionId(tabla, camposIdDestino);

		JLabel labelAyuda = crearLabel(ayuda);

		JScrollPane scrollTabla = estilizarScroll(tabla);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(300)));

		bloque.add(labelAyuda, gbcCampo(1));
		bloque.add(crearPanelFiltrosVenta(modelo), gbcCampo(2));
		bloque.add(scrollTabla, gbcCampo(3));

		if (incluirRefrescar) {
			JButton botonRefrescar = crearBotonAccion("Refrescar lista");

			botonRefrescar.addActionListener(e -> cargarModeloProductosVenta(modelo));

			bloque.add(botonRefrescar, gbcBoton(4));
		}

		return new SelectorVenta(bloque, tabla);
	}

	private DefaultTableModel crearModeloTablaVenta() {
		String[] columnas = { "ID", "Nombre", "Tipo", "Categorías", "Precio", "Stock", "Puntuación" };

		return new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};
	}

	private void estilizarTabla(JTable tabla) {
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tabla.setRowHeight(VentanaPrincipal.escalar(30));
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabla.setBackground(Color.WHITE);
		tabla.setForeground(VentanaPrincipal.COLOR_TEXTO);
		tabla.setGridColor(new Color(225, 225, 225));
		tabla.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
		tabla.setSelectionForeground(Color.BLACK);
		tabla.setFillsViewportHeight(true);
		tabla.setShowHorizontalLines(true);
		tabla.setShowVerticalLines(true);

		JTableHeader header = tabla.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(232, 232, 232));
		header.setForeground(VentanaPrincipal.COLOR_TEXTO);
		header.setReorderingAllowed(false);

		tabla.getColumnModel().getColumn(COL_VENTA_ID).setPreferredWidth(90);
		tabla.getColumnModel().getColumn(COL_VENTA_NOMBRE).setPreferredWidth(230);
		tabla.getColumnModel().getColumn(COL_VENTA_TIPO).setPreferredWidth(110);
		tabla.getColumnModel().getColumn(COL_VENTA_CATEGORIAS).setPreferredWidth(260);
		tabla.getColumnModel().getColumn(COL_VENTA_PRECIO).setPreferredWidth(100);
		tabla.getColumnModel().getColumn(COL_VENTA_STOCK).setPreferredWidth(80);
		tabla.getColumnModel().getColumn(COL_VENTA_PUNTUACION).setPreferredWidth(100);
	}

	private void conectarSeleccionId(JTable tabla, JTextField... camposDestino) {
		tabla.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int fila = tabla.getSelectedRow();

			if (fila < 0) {
				return;
			}

			Object id = tabla.getValueAt(fila, COL_VENTA_ID);

			if (id == null) {
				return;
			}

			for (JTextField campo : camposDestino) {
				if (campo != null) {
					campo.setText(String.valueOf(id));
				}
			}
		});
	}

	private JPanel crearPanelFiltrosVenta(DefaultTableModel modelo) {
		JPanel panel = new JPanel(new java.awt.GridBagLayout());
		panel.setOpaque(false);

		JTextField campoBuscar = crearCampoCompacto();

		JComboBox<String> comboTipo = crearCombo(new String[] { "Todos", "Comic", "Juego", "Figura", "Pack" });

		JComboBox<String> comboCategoria = crearCombo(obtenerCategoriasParaCombo());

		JButton botonLimpiar = crearBotonSecundario("Limpiar filtros");

		panel.add(crearCampoFormulario("Buscar", campoBuscar), gbcFiltro(0, 0));
		panel.add(crearCampoFormulario("Tipo", comboTipo), gbcFiltro(1, 0));
		panel.add(crearCampoFormulario("Categoría", comboCategoria), gbcFiltro(2, 0));
		panel.add(crearCampoFormulario(" ", botonLimpiar), gbcFiltro(3, 0));

		Runnable aplicar = () -> cargarModeloProductosVenta(modelo, campoBuscar.getText(),
				String.valueOf(comboTipo.getSelectedItem()), String.valueOf(comboCategoria.getSelectedItem()));

		escucharCambios(campoBuscar, aplicar);

		comboTipo.addActionListener(e -> aplicar.run());
		comboCategoria.addActionListener(e -> aplicar.run());

		botonLimpiar.addActionListener(e -> {
			campoBuscar.setText("");
			comboTipo.setSelectedIndex(0);
			comboCategoria.setSelectedIndex(0);
			cargarModeloProductosVenta(modelo);
		});

		return panel;
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modelo) {
		cargarModeloProductosVenta(modelo, "", "Todos", "Todas");
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modelo, String textoBuscado, String tipoBuscado,
			String categoriaBuscada) {

		modelo.setRowCount(0);

		String texto = normalizarTexto(textoBuscado);
		String tipoFiltro = normalizarTexto(tipoBuscado);
		String categoriaFiltro = normalizarTexto(categoriaBuscada);

		for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
			String id = p.getId();
			String nombre = p.getNombre();
			String tipo = obtenerTipoProductoVenta(p);
			String categorias = obtenerTextoCategorias(p);

			if (!pasaFiltro(id, nombre, tipo, categorias, texto, tipoFiltro, categoriaFiltro)) {
				continue;
			}

			modelo.addRow(new Object[] { id, nombre, tipo, categorias, formatearPrecio(p.getPrecioOficial()),
					p.getStockDisponible(), formatearPuntuacion(p.getMediaPuntuacion()) });
		}
	}

	private boolean pasaFiltro(String id, String nombre, String tipo, String categorias, String texto,
			String tipoFiltro, String categoriaFiltro) {

		if (!texto.isBlank()) {
			boolean coincideTexto = contieneTexto(id, texto) || contieneTexto(nombre, texto)
					|| contieneTexto(tipo, texto) || contieneTexto(categorias, texto);

			if (!coincideTexto) {
				return false;
			}
		}

		if (!"todos".equals(tipoFiltro) && !contieneTexto(tipo, tipoFiltro)) {
			return false;
		}

		if (!"todas".equals(categoriaFiltro) && !contieneTexto(categorias, categoriaFiltro)) {
			return false;
		}

		return true;
	}

	protected void recargarTablaProductos(JTable tabla) {
		DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
		cargarModeloProductosVenta(modelo);
	}

	private String[] obtenerCategoriasParaCombo() {
		List<String> categorias = obtenerNombresCategoriasVenta();

		String[] valores = new String[categorias.size() + 1];
		valores[0] = "Todas";

		for (int i = 0; i < categorias.size(); i++) {
			valores[i + 1] = categorias.get(i);
		}

		return valores;
	}

	private List<String> obtenerNombresCategoriasVenta() {
		TreeSet<String> nombres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		for (ProductoVenta p : Tienda.getInstancia().getStockVentas()) {
			for (Categoria c : p.getCategorias()) {
				if (c != null && c.getNombre() != null && !c.getNombre().isBlank()) {
					nombres.add(c.getNombre().trim());
				}
			}
		}

		return new ArrayList<>(nombres);
	}

	protected String obtenerTextoCategorias(ProductoVenta p) {
		if (p == null || p.getCategorias().isEmpty()) {
			return "-";
		}

		List<String> nombres = new ArrayList<>();

		for (Categoria c : p.getCategorias()) {
			if (c != null && c.getNombre() != null && !c.getNombre().isBlank()) {
				nombres.add(c.getNombre().trim());
			}
		}

		nombres.sort(String.CASE_INSENSITIVE_ORDER);

		if (nombres.isEmpty()) {
			return "-";
		}

		return String.join(", ", nombres);
	}

	protected String obtenerTipoProductoVenta(ProductoVenta p) {
		if (p instanceof Comic) {
			return "Comic";
		}

		if (p instanceof JuegoMesa) {
			return "Juego";
		}

		if (p instanceof Figura) {
			return "Figura";
		}

		if (p instanceof Pack) {
			return "Pack";
		}

		return "Producto";
	}

	protected List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
		ArrayList<ProductoVenta> productos = new ArrayList<>(Tienda.getInstancia().getStockVentas());

		productos.sort(Comparator.comparingInt(ProductoVenta::getStockDisponible)
				.thenComparing(ProductoVenta::getNombre, String.CASE_INSENSITIVE_ORDER));

		return productos;
	}

	protected ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
		ArrayList<LineaPack> lineas = new ArrayList<>();

		String[] filas = texto.split("\\r?\\n");

		for (String fila : filas) {
			if (fila == null || fila.isBlank()) {
				continue;
			}

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

			if (unidades <= 0) {
				throw new IllegalArgumentException("Las unidades deben ser mayores que 0.");
			}

			lineas.add(new LineaPack(producto, unidades));
		}

		return lineas;
	}

	private String formatearPrecio(double precio) {
		return String.format(java.util.Locale.US, "%.2f €", precio).replace('.', ',');
	}

	private String formatearPuntuacion(double puntuacion) {
		return String.format(java.util.Locale.US, "%.1f", puntuacion).replace('.', ',');
	}
}