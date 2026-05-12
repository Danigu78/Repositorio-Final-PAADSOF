package Gui;

import Gui.Controladores.empleado.ControladorProductosEmpleado;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import productos.Categoria;
import productos.ProductoVenta;

/**
 * Tabla reutilizable de productos de venta con el mismo estilo y filtros que se
 * usan en empleado.
 */
public class TablaProductosVenta extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int COL_ID = 0;
	private static final int COL_NOMBRE = 1;
	private static final int COL_TIPO = 2;
	private static final int COL_CATEGORIAS = 3;
	private static final int COL_PRECIO = 4;
	private static final int COL_STOCK = 5;
	private static final int COL_PUNTUACION = 6;

	private final ControladorProductosEmpleado controlador = new ControladorProductosEmpleado();
	private final Supplier<List<ProductoVenta>> proveedorProductos;
	private final List<ColumnaExtra> columnasExtra;
	private final boolean marcarSeleccion;

	private DefaultTableModel modelo;
	private JTable tabla;
	private JTextField campoBuscar;
	private JComboBox<String> comboOrden;
	private JCheckBox[] checksTipo;
	private JCheckBox[] checksCategoria;
	private Consumer<String> alSeleccionarId;

	public static class ColumnaExtra {
		private final String nombre;
		private final Function<ProductoVenta, Object> valor;
		private final String ordenAscendente;
		private final String ordenDescendente;
		private final Comparator<ProductoVenta> comparadorAscendente;

		public ColumnaExtra(String nombre, Function<ProductoVenta, Object> valor, String ordenAscendente,
				String ordenDescendente, Comparator<ProductoVenta> comparadorAscendente) {
			this.nombre = nombre;
			this.valor = valor;
			this.ordenAscendente = ordenAscendente;
			this.ordenDescendente = ordenDescendente;
			this.comparadorAscendente = comparadorAscendente;
		}
	}

	public TablaProductosVenta(Supplier<List<ProductoVenta>> proveedorProductos) {
		this(proveedorProductos, new ArrayList<>());
	}

	public TablaProductosVenta(Supplier<List<ProductoVenta>> proveedorProductos, List<ColumnaExtra> columnasExtra) {
		this(proveedorProductos, columnasExtra, true);
	}

	public TablaProductosVenta(Supplier<List<ProductoVenta>> proveedorProductos, boolean marcarSeleccion) {
		this(proveedorProductos, new ArrayList<>(), marcarSeleccion);
	}

	public TablaProductosVenta(Supplier<List<ProductoVenta>> proveedorProductos, List<ColumnaExtra> columnasExtra,
			boolean marcarSeleccion) {
		this.proveedorProductos = proveedorProductos;
		this.columnasExtra = columnasExtra == null ? new ArrayList<>() : columnasExtra;
		this.marcarSeleccion = marcarSeleccion;
		setLayout(new BorderLayout(0, VentanaPrincipal.escalar(10)));
		setOpaque(false);
		construir();
	}

	public void setAlSeleccionarId(Consumer<String> alSeleccionarId) {
		this.alSeleccionarId = alSeleccionarId;
	}

	public JTable getTabla() {
		return tabla;
	}

	public void refrescar() {
		cargarModelo();
	}

	private void construir() {
		modelo = crearModelo();
		tabla = new JTable(modelo);
		estilizarTabla(tabla);
		tabla.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}
			int fila = tabla.getSelectedRow();
			if (fila >= 0 && alSeleccionarId != null) {
				Object id = tabla.getValueAt(fila, COL_ID);
				if (id != null) {
					alSeleccionarId.accept(String.valueOf(id));
				}
			}
		});

		add(crearPanelFiltros(), BorderLayout.NORTH);
		JScrollPane scroll = new JScrollPane(tabla);
		scroll.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1));
		scroll.getViewport().setBackground(Color.WHITE);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		scroll.getHorizontalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(240)));
		add(scroll, BorderLayout.CENTER);

		cargarModelo();
	}

	private DefaultTableModel crearModelo() {
		List<String> columnas = new ArrayList<>();
		columnas.add("ID");
		columnas.add("Nombre");
		columnas.add("Tipo");
		columnas.add("Categorias");
		columnas.add("Precio");
		columnas.add("Stock");
		columnas.add("Puntuacion");
		for (ColumnaExtra extra : columnasExtra) {
			columnas.add(extra.nombre);
		}

		return new DefaultTableModel(columnas.toArray(new String[0]), 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};
	}

	private void estilizarTabla(JTable tabla) {
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tabla.setRowHeight(VentanaPrincipal.escalar(28));
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabla.setBackground(Color.WHITE);
		tabla.setForeground(Color.BLACK);
		tabla.setGridColor(new Color(225, 225, 225));
		tabla.setSelectionBackground(marcarSeleccion ? VentanaPrincipal.COLOR_ACENTO : Color.WHITE);
		tabla.setSelectionForeground(Color.BLACK);
		tabla.setFillsViewportHeight(true);
		tabla.setShowHorizontalLines(true);
		tabla.setShowVerticalLines(true);

		JTableHeader cabecera = tabla.getTableHeader();
		cabecera.setFont(new Font("Segoe UI", Font.BOLD, 13));
		cabecera.setBackground(new Color(235, 235, 235));
		cabecera.setForeground(Color.BLACK);
		cabecera.setReorderingAllowed(false);

		tabla.getColumnModel().getColumn(COL_ID).setPreferredWidth(80);
		tabla.getColumnModel().getColumn(COL_NOMBRE).setPreferredWidth(220);
		tabla.getColumnModel().getColumn(COL_TIPO).setPreferredWidth(100);
		tabla.getColumnModel().getColumn(COL_CATEGORIAS).setPreferredWidth(250);
		tabla.getColumnModel().getColumn(COL_PRECIO).setPreferredWidth(100);
		tabla.getColumnModel().getColumn(COL_STOCK).setPreferredWidth(80);
		tabla.getColumnModel().getColumn(COL_PUNTUACION).setPreferredWidth(100);
	}

	private JPanel crearPanelFiltros() {
		JPanel panelFiltros = new JPanel();
		panelFiltros.setOpaque(false);
		panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));

		campoBuscar = crearCampoCompacto();
		campoBuscar.setPreferredSize(new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(34)));
		campoBuscar.setMaximumSize(new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(34)));

		List<String> opcionesOrden = new ArrayList<>();
		opcionesOrden.add("Sin ordenar");
		opcionesOrden.add("Nombre A-Z");
		opcionesOrden.add("Nombre Z-A");
		opcionesOrden.add("Precio: menor a mayor");
		opcionesOrden.add("Precio: mayor a menor");
		opcionesOrden.add("Stock: menor a mayor");
		opcionesOrden.add("Stock: mayor a menor");
		opcionesOrden.add("Puntuacion: menor a mayor");
		opcionesOrden.add("Puntuacion: mayor a menor");
		for (ColumnaExtra extra : columnasExtra) {
			if (extra.ordenAscendente != null) {
				opcionesOrden.add(extra.ordenAscendente);
			}
			if (extra.ordenDescendente != null) {
				opcionesOrden.add(extra.ordenDescendente);
			}
		}

		comboOrden = crearCombo(opcionesOrden.toArray(new String[0]));
		comboOrden.setPreferredSize(new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(34)));
		comboOrden.setMaximumSize(new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(34)));

		checksTipo = new JCheckBox[] { crearCheckFiltro("Comic"), crearCheckFiltro("Juego"), crearCheckFiltro("Figura"),
				crearCheckFiltro("Pack") };

		List<String> nombresCategorias = obtenerNombresCategoriasVenta();
		checksCategoria = new JCheckBox[nombresCategorias.size()];
		for (int i = 0; i < nombresCategorias.size(); i++) {
			checksCategoria[i] = crearCheckFiltro(nombresCategorias.get(i));
		}

		JButton botonLimpiar = crearBotonSecundario("Limpiar");
		botonLimpiar.setPreferredSize(new Dimension(VentanaPrincipal.escalar(140), VentanaPrincipal.escalar(34)));

		JPanel filaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(18), 0));
		filaSuperior.setOpaque(false);
		filaSuperior.add(crearZona("Buscar", campoBuscar, VentanaPrincipal.escalar(650)));
		filaSuperior.add(crearZona("Ordenar por", comboOrden, VentanaPrincipal.escalar(230)));

		JPanel zonaBotonLimpiar = new JPanel();
		zonaBotonLimpiar.setOpaque(false);
		zonaBotonLimpiar.setLayout(new BoxLayout(zonaBotonLimpiar, BoxLayout.Y_AXIS));
		zonaBotonLimpiar.add(Box.createVerticalStrut(VentanaPrincipal.escalar(22)));
		zonaBotonLimpiar.add(botonLimpiar);
		filaSuperior.add(zonaBotonLimpiar);

		panelFiltros.add(filaSuperior);
		panelFiltros.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));
		panelFiltros.add(crearFilaFiltro("Tipo", checksTipo));
		panelFiltros.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		panelFiltros.add(crearFilaFiltro("Categoria", checksCategoria));

		escucharCambios(campoBuscar, this::cargarModelo);
		comboOrden.addActionListener(e -> cargarModelo());
		for (JCheckBox check : checksTipo) {
			check.addActionListener(e -> cargarModelo());
		}
		for (JCheckBox check : checksCategoria) {
			check.addActionListener(e -> cargarModelo());
		}
		botonLimpiar.addActionListener(e -> {
			campoBuscar.setText("");
			comboOrden.setSelectedIndex(0);
			for (JCheckBox check : checksTipo) {
				check.setSelected(false);
			}
			for (JCheckBox check : checksCategoria) {
				check.setSelected(false);
			}
			cargarModelo();
		});

		return panelFiltros;
	}

	private JPanel crearZona(String titulo, java.awt.Component componente, int ancho) {
		JPanel zona = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zona.setOpaque(false);
		zona.setPreferredSize(new Dimension(ancho, VentanaPrincipal.escalar(58)));
		zona.setMaximumSize(new Dimension(ancho, VentanaPrincipal.escalar(58)));
		zona.add(crearLabel(titulo), BorderLayout.NORTH);
		zona.add(componente, BorderLayout.CENTER);
		return zona;
	}

	private JPanel crearFilaFiltro(String titulo, JCheckBox[] checks) {
		JPanel fila = new JPanel(new BorderLayout(VentanaPrincipal.escalar(12), 0));
		fila.setOpaque(false);
		JLabel etiqueta = crearLabel(titulo);
		etiqueta.setPreferredSize(new Dimension(VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(28)));

		JPanel panelChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		panelChecks.setOpaque(false);
		for (JCheckBox check : checks) {
			panelChecks.add(check);
		}
		fila.add(etiqueta, BorderLayout.WEST);
		fila.add(panelChecks, BorderLayout.CENTER);
		return fila;
	}

	private void cargarModelo() {
		modelo.setRowCount(0);
		String texto = normalizar(campoBuscar == null ? "" : campoBuscar.getText());
		List<String> tipos = obtenerChecksMarcados(checksTipo);
		List<String> categorias = obtenerChecksMarcados(checksCategoria);
		List<ProductoVenta> productos = new ArrayList<>();

		for (ProductoVenta producto : proveedorProductos.get()) {
			if (producto == null) {
				continue;
			}
			String id = producto.getId();
			String nombre = producto.getNombre();
			String tipo = controlador.obtenerTipoProductoVenta(producto);
			String cats = controlador.obtenerTextoCategorias(producto);
			if (pasaFiltro(id, nombre, tipo, cats, texto, tipos, categorias)) {
				productos.add(producto);
			}
		}

		ordenarProductos(productos, comboOrden == null ? "Sin ordenar" : String.valueOf(comboOrden.getSelectedItem()));

		for (ProductoVenta producto : productos) {
			List<Object> fila = new ArrayList<>();
			fila.add(producto.getId());
			fila.add(producto.getNombre());
			fila.add(controlador.obtenerTipoProductoVenta(producto));
			fila.add(controlador.obtenerTextoCategorias(producto));
			fila.add(controlador.formatearPrecio(producto.getPrecioOficial()));
			fila.add(producto.getStockDisponible());
			fila.add(controlador.formatearPuntuacion(producto.getMediaPuntuacion()));
			for (ColumnaExtra extra : columnasExtra) {
				fila.add(extra.valor.apply(producto));
			}
			modelo.addRow(fila.toArray());
		}
	}

	private boolean pasaFiltro(String id, String nombre, String tipo, String categorias, String texto,
			List<String> tiposBuscados, List<String> categoriasBuscadas) {
		if (!texto.isEmpty() && !contiene(id, texto) && !contiene(nombre, texto) && !contiene(tipo, texto)
				&& !contiene(categorias, texto)) {
			return false;
		}

		if (!tiposBuscados.isEmpty() && !tiposBuscados.contains(tipo)) {
			return false;
		}

		if (!categoriasBuscadas.isEmpty()) {
			for (String categoria : categoriasBuscadas) {
				if (contiene(categorias, normalizar(categoria))) {
					return true;
				}
			}
			return false;
		}

		return true;
	}

	private void ordenarProductos(List<ProductoVenta> productos, String orden) {
		Comparator<ProductoVenta> comparador = Comparator.comparingInt(p -> numeroIdProducto(p.getId()));

		if ("Nombre A-Z".equals(orden)) {
			comparador = Comparator.comparing(p -> textoSeguro(p.getNombre()), String.CASE_INSENSITIVE_ORDER);
		} else if ("Nombre Z-A".equals(orden)) {
			comparador = Comparator
					.comparing((ProductoVenta p) -> textoSeguro(p.getNombre()), String.CASE_INSENSITIVE_ORDER)
					.reversed();
		} else if ("Precio: menor a mayor".equals(orden)) {
			comparador = Comparator.comparingDouble(ProductoVenta::getPrecioOficial);
		} else if ("Precio: mayor a menor".equals(orden)) {
			comparador = Comparator.comparingDouble(ProductoVenta::getPrecioOficial).reversed();
		} else if ("Stock: menor a mayor".equals(orden)) {
			comparador = Comparator.comparingInt(ProductoVenta::getStockDisponible);
		} else if ("Stock: mayor a menor".equals(orden)) {
			comparador = Comparator.comparingInt(ProductoVenta::getStockDisponible).reversed();
		} else if ("Puntuacion: menor a mayor".equals(orden)) {
			comparador = Comparator.comparingDouble(ProductoVenta::getMediaPuntuacion);
		} else if ("Puntuacion: mayor a menor".equals(orden)) {
			comparador = Comparator.comparingDouble(ProductoVenta::getMediaPuntuacion).reversed();
		} else {
			for (ColumnaExtra extra : columnasExtra) {
				if (extra.ordenAscendente != null && extra.ordenAscendente.equals(orden)) {
					comparador = extra.comparadorAscendente;
					break;
				}
				if (extra.ordenDescendente != null && extra.ordenDescendente.equals(orden)) {
					comparador = extra.comparadorAscendente.reversed();
					break;
				}
			}
		}

		productos.sort(comparador);
	}

	private List<String> obtenerNombresCategoriasVenta() {
		TreeSet<String> nombres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Categoria categoria : tienda.Tienda.getInstancia().getCategoriasActivas()) {
			if (categoria != null && categoria.getNombre() != null && !categoria.getNombre().trim().isEmpty()) {
				nombres.add(categoria.getNombre().trim());
			}
		}
		return new ArrayList<>(nombres);
	}

	private List<String> obtenerChecksMarcados(JCheckBox[] checks) {
		List<String> marcados = new ArrayList<>();
		if (checks == null) {
			return marcados;
		}
		for (JCheckBox check : checks) {
			if (check.isSelected()) {
				marcados.add(check.getText());
			}
		}
		return marcados;
	}

	private JCheckBox crearCheckFiltro(String texto) {
		JCheckBox check = new JCheckBox(texto);
		check.setOpaque(false);
		check.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		check.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		check.setFocusPainted(false);
		return check;
	}

	private JTextField crearCampoCompacto() {
		JTextField campo = new JTextField();
		campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		campo.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(34)));
		campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(34)));
		campo.setBackground(Color.WHITE);
		campo.setForeground(Color.BLACK);
		campo.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
		campo.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10))));
		return campo;
	}

	private <T> JComboBox<T> crearCombo(T[] valores) {
		JComboBox<T> combo = new JComboBox<>(valores);
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		combo.setBackground(Color.WHITE);
		combo.setForeground(Color.BLACK);
		combo.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(34)));
		combo.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1));
		return combo;
	}

	private JLabel crearLabel(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_NORMAL);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		return label;
	}

	private JButton crearBotonSecundario(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
		boton.setForeground(Color.BLACK);
		boton.setFocusPainted(false);
		boton.setBorderPainted(false);
		boton.setContentAreaFilled(true);
		boton.setOpaque(true);
		return boton;
	}

	private void escucharCambios(JTextField campo, Runnable accion) {
		campo.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				accion.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				accion.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				accion.run();
			}
		});
	}

	private String normalizar(String texto) {
		return texto == null ? "" : texto.trim().toLowerCase();
	}

	private boolean contiene(String base, String buscado) {
		return normalizar(base).contains(normalizar(buscado));
	}

	private String textoSeguro(String texto) {
		return texto == null ? "" : texto;
	}

	private int numeroIdProducto(String id) {
		if (id == null || !id.startsWith("PV-")) {
			return 0;
		}
		try {
			return Integer.parseInt(id.substring(3));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Recarga los checkboxes de categoría con las categorías actuales de la tienda.
	 * Se llama desde SubpanelCategoriasGestor tras crear o eliminar una categoría.
	 */
	public void refrescarFiltrosCategorias() {
		
		List<String> nombres = obtenerNombresCategoriasVenta();
		checksCategoria = new JCheckBox[nombres.size()];
		for (int i = 0; i < nombres.size(); i++) {
			checksCategoria[i] = crearCheckFiltro(nombres.get(i));
			checksCategoria[i].addActionListener(e -> cargarModelo());
		}
		
		removeAll();
		construir();
		revalidate();
		repaint();
	}
}
