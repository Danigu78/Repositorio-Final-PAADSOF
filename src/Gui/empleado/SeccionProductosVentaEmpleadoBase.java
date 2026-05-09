package Gui.empleado;


import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorProductosEmpleado;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import productos.LineaPack;
import productos.ProductoVenta;
import usuarios.Empleado;

/**
 * Clase base para las secciones del empleado que trabajan con productos de
 * venta.
 * 
 * Tiene una tabla común de productos, filtros sencillos, ordenación y métodos
 * auxiliares para trabajar con packs.
 */
public abstract class SeccionProductosVentaEmpleadoBase extends SeccionEmpleadoBase {

	private static final long serialVersionUID = 1L;

	protected static final int COL_VENTA_ID = 0;
	protected static final int COL_VENTA_NOMBRE = 1;
	protected static final int COL_VENTA_TIPO = 2;
	protected static final int COL_VENTA_CATEGORIAS = 3;
	protected static final int COL_VENTA_PRECIO = 4;
	protected static final int COL_VENTA_STOCK = 5;
	protected static final int COL_VENTA_PUNTUACION = 6;

	protected final ControladorProductosEmpleado controladorProductos = new ControladorProductosEmpleado();

	protected static class SelectorVenta {
		JPanel bloque;
		JTable tabla;

		SelectorVenta(JPanel bloque, JTable tabla) {
			this.bloque = bloque;
			this.tabla = tabla;
		}
	}

	protected SeccionProductosVentaEmpleadoBase(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
	}

	protected SelectorVenta crearSelectorProductosVenta(String titulo, String ayuda, boolean incluirRefrescar,
			JTextField... camposIdDestino) {

		JPanel bloque = crearBloque(titulo);

		DefaultTableModel modeloTabla = crearModeloTablaVenta();
		JTable tablaProductos = new JTable(modeloTabla);

		estilizarTabla(tablaProductos);
		cargarModeloProductosVenta(modeloTabla);
		conectarSeleccionId(tablaProductos, camposIdDestino);

		JLabel textoAyuda = crearLabel(ayuda);

		JScrollPane scrollTabla = estilizarScroll(tablaProductos);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(240)));

		JPanel filtros = crearPanelFiltrosVenta(modeloTabla);

		bloque.add(textoAyuda, gbcCampo(1));
		bloque.add(filtros, gbcCampo(2));
		bloque.add(scrollTabla, gbcCampo(3));

		if (incluirRefrescar) {
			JButton botonRefrescar = crearBotonSecundario("Refrescar");

			botonRefrescar.addActionListener(e -> cargarModeloProductosVenta(modeloTabla));

			JPanel filaBoton = new JPanel(new BorderLayout());
			filaBoton.setOpaque(false);
			filaBoton.add(botonRefrescar, BorderLayout.WEST);

			bloque.add(filaBoton, gbcBoton(4));
		}

		return new SelectorVenta(bloque, tablaProductos);
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
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tabla.setRowHeight(VentanaPrincipal.escalar(28));
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		tabla.setBackground(Color.WHITE);
		tabla.setForeground(Color.BLACK);
		tabla.setGridColor(new Color(225, 225, 225));

		tabla.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
		tabla.setSelectionForeground(Color.BLACK);

		tabla.setFillsViewportHeight(true);
		tabla.setShowHorizontalLines(true);
		tabla.setShowVerticalLines(true);

		JTableHeader cabecera = tabla.getTableHeader();
		cabecera.setFont(new Font("Segoe UI", Font.BOLD, 13));
		cabecera.setBackground(new Color(235, 235, 235));
		cabecera.setForeground(Color.BLACK);
		cabecera.setReorderingAllowed(false);

		tabla.getColumnModel().getColumn(COL_VENTA_ID).setPreferredWidth(80);
		tabla.getColumnModel().getColumn(COL_VENTA_NOMBRE).setPreferredWidth(220);
		tabla.getColumnModel().getColumn(COL_VENTA_TIPO).setPreferredWidth(100);
		tabla.getColumnModel().getColumn(COL_VENTA_CATEGORIAS).setPreferredWidth(250);
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

			Object idProducto = tabla.getValueAt(fila, COL_VENTA_ID);

			if (idProducto == null) {
				return;
			}

			for (JTextField campo : camposDestino) {
				if (campo != null) {
					campo.setText(String.valueOf(idProducto));
				}
			}
		});
	}

	private JPanel crearPanelFiltrosVenta(DefaultTableModel modeloTabla) {
		JPanel panelFiltros = new JPanel();
		panelFiltros.setOpaque(false);
		panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));

		JTextField campoBuscar = crearCampoCompacto();
		campoBuscar.setPreferredSize(new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(34)));
		campoBuscar.setMaximumSize(new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(34)));

		JComboBox<String> comboOrden = crearCombo(new String[] { "Sin ordenar", "Nombre A-Z", "Nombre Z-A",
				"Precio: menor a mayor", "Precio: mayor a menor", "Stock: menor a mayor", "Puntuación: menor a mayor",
				"Puntuación: mayor a menor" });
		comboOrden.setPreferredSize(new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(34)));
		comboOrden.setMaximumSize(new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(34)));

		JCheckBox checkComic = crearCheckFiltro("Comic");
		JCheckBox checkJuego = crearCheckFiltro("Juego");
		JCheckBox checkFigura = crearCheckFiltro("Figura");
		JCheckBox checkPack = crearCheckFiltro("Pack");

		JCheckBox[] checksTipo = { checkComic, checkJuego, checkFigura, checkPack };

		List<String> nombresCategorias = obtenerNombresCategoriasVenta();
		JCheckBox[] checksCategoria = new JCheckBox[nombresCategorias.size()];

		for (int i = 0; i < nombresCategorias.size(); i++) {
			checksCategoria[i] = crearCheckFiltro(nombresCategorias.get(i));
		}

		JButton botonLimpiar = crearBotonSecundario("Limpiar");
		botonLimpiar.setPreferredSize(new Dimension(VentanaPrincipal.escalar(140), VentanaPrincipal.escalar(34)));
		botonLimpiar.setMaximumSize(new Dimension(VentanaPrincipal.escalar(140), VentanaPrincipal.escalar(34)));

		JPanel zonaBuscar = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaBuscar.setOpaque(false);
		zonaBuscar.setPreferredSize(new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(58)));
		zonaBuscar.setMaximumSize(new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(58)));
		zonaBuscar.add(crearLabel("Buscar"), BorderLayout.NORTH);
		zonaBuscar.add(campoBuscar, BorderLayout.CENTER);

		JPanel zonaOrden = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaOrden.setOpaque(false);
		zonaOrden.setPreferredSize(new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(58)));
		zonaOrden.setMaximumSize(new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(58)));
		zonaOrden.add(crearLabel("Ordenar por"), BorderLayout.NORTH);
		zonaOrden.add(comboOrden, BorderLayout.CENTER);

		JPanel zonaBotonLimpiar = new JPanel();
		zonaBotonLimpiar.setOpaque(false);
		zonaBotonLimpiar.setLayout(new BoxLayout(zonaBotonLimpiar, BoxLayout.Y_AXIS));
		zonaBotonLimpiar.add(Box.createVerticalStrut(VentanaPrincipal.escalar(22)));
		zonaBotonLimpiar.add(botonLimpiar);

		JPanel filaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(18), 0));
		filaSuperior.setOpaque(false);
		filaSuperior.add(zonaBuscar);
		filaSuperior.add(zonaOrden);
		filaSuperior.add(zonaBotonLimpiar);

		panelFiltros.add(filaSuperior);
		panelFiltros.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));

		panelFiltros.add(crearFilaFiltro("Tipo", checksTipo));
		panelFiltros.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panelFiltros.add(crearFilaFiltro("Categoría", checksCategoria));

		Runnable aplicarFiltros = () -> {
			List<String> tiposMarcados = obtenerChecksMarcados(checksTipo);
			List<String> categoriasMarcadas = obtenerChecksMarcados(checksCategoria);
			String orden = String.valueOf(comboOrden.getSelectedItem());

			cargarModeloProductosVenta(modeloTabla, campoBuscar.getText(), tiposMarcados, categoriasMarcadas, orden);
		};

		escucharCambios(campoBuscar, aplicarFiltros);

		comboOrden.addActionListener(e -> aplicarFiltros.run());

		for (JCheckBox check : checksTipo) {
			check.addActionListener(e -> aplicarFiltros.run());
		}

		for (JCheckBox check : checksCategoria) {
			check.addActionListener(e -> aplicarFiltros.run());
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

			cargarModeloProductosVenta(modeloTabla);
		});

		return panelFiltros;
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

	private JCheckBox crearCheckFiltro(String texto) {
		JCheckBox check = new JCheckBox(texto);

		check.setOpaque(false);
		check.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		check.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		check.setFocusPainted(false);

		return check;
	}

	private List<String> obtenerChecksMarcados(JCheckBox[] checks) {
		List<String> marcados = new ArrayList<>();

		for (JCheckBox check : checks) {
			if (check.isSelected()) {
				marcados.add(check.getText());
			}
		}

		return marcados;
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modeloTabla) {
		cargarModeloProductosVenta(modeloTabla, "", new ArrayList<>(), new ArrayList<>());
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modeloTabla, String textoBuscado,
			List<String> tiposBuscados, List<String> categoriasBuscadas) {

		cargarModeloProductosVenta(modeloTabla, textoBuscado, tiposBuscados, categoriasBuscadas, "Sin ordenar");
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modeloTabla, String textoBuscado,
			List<String> tiposBuscados, List<String> categoriasBuscadas, String orden) {

		modeloTabla.setRowCount(0);

		String texto = normalizarTexto(textoBuscado);
		List<ProductoVenta> productos = new ArrayList<>();

		for (ProductoVenta producto : obtenerProductosOrdenadosPorStock()) {
			String id = producto.getId();
			String nombre = producto.getNombre();
			String tipo = obtenerTipoProductoVenta(producto);
			String categorias = obtenerTextoCategorias(producto);

			if (pasaFiltro(id, nombre, tipo, categorias, texto, tiposBuscados, categoriasBuscadas)) {
				productos.add(producto);
			}
		}

		ordenarProductos(productos, orden);

		for (ProductoVenta producto : productos) {
			String id = producto.getId();
			String nombre = producto.getNombre();
			String tipo = obtenerTipoProductoVenta(producto);
			String categorias = obtenerTextoCategorias(producto);

			modeloTabla
					.addRow(new Object[] { id, nombre, tipo, categorias, formatearPrecio(producto.getPrecioOficial()),
							producto.getStockDisponible(), formatearPuntuacion(producto.getMediaPuntuacion()) });
		}
	}

	private void ordenarProductos(List<ProductoVenta> productos, String orden) {
		final String ordenFinal;

		if (orden == null) {
			ordenFinal = "Sin ordenar";
		} else {
			ordenFinal = orden;
		}

		productos.sort(new Comparator<ProductoVenta>() {
			@Override
			public int compare(ProductoVenta p1, ProductoVenta p2) {
				String nombre1 = p1.getNombre();
				String nombre2 = p2.getNombre();

				if (nombre1 == null) {
					nombre1 = "";
				}

				if (nombre2 == null) {
					nombre2 = "";
				}

				switch (ordenFinal) {
				case "Sin ordenar":
					return Integer.compare(numeroIdProducto(p1.getId()), numeroIdProducto(p2.getId()));

				case "Nombre A-Z":
					return nombre1.compareToIgnoreCase(nombre2);

				case "Nombre Z-A":
					return nombre2.compareToIgnoreCase(nombre1);

				case "Precio: menor a mayor":
					return Double.compare(p1.getPrecioOficial(), p2.getPrecioOficial());

				case "Precio: mayor a menor":
					return Double.compare(p2.getPrecioOficial(), p1.getPrecioOficial());

				case "Stock: menor a mayor":
					return Integer.compare(p1.getStockDisponible(), p2.getStockDisponible());

				case "Puntuación: menor a mayor":
					return Double.compare(p1.getMediaPuntuacion(), p2.getMediaPuntuacion());

				case "Puntuación: mayor a menor":
					return Double.compare(p2.getMediaPuntuacion(), p1.getMediaPuntuacion());

				default:
					return Integer.compare(numeroIdProducto(p1.getId()), numeroIdProducto(p2.getId()));
				}
			}
		});
	}

	private int numeroIdProducto(String id) {
		if (id == null || !id.startsWith("PV-")) {
			return 0;
		}

		return Integer.parseInt(id.substring(3));
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modeloTabla, String textoBuscado, String tipoBuscado,
			String categoriaBuscada) {

		List<String> tipos = new ArrayList<>();
		List<String> categorias = new ArrayList<>();

		if (tipoBuscado != null && !tipoBuscado.equalsIgnoreCase("Todos")) {
			tipos.add(tipoBuscado);
		}

		if (categoriaBuscada != null && !categoriaBuscada.equalsIgnoreCase("Todas")) {
			categorias.add(categoriaBuscada);
		}

		cargarModeloProductosVenta(modeloTabla, textoBuscado, tipos, categorias);
	}

	private boolean pasaFiltro(String id, String nombre, String tipo, String categorias, String texto,
			List<String> tiposBuscados, List<String> categoriasBuscadas) {

		if (!texto.isBlank()) {
			boolean coincideTexto = contieneTexto(id, texto) || contieneTexto(nombre, texto)
					|| contieneTexto(tipo, texto) || contieneTexto(categorias, texto);

			if (!coincideTexto) {
				return false;
			}
		}

		if (tiposBuscados != null && !tiposBuscados.isEmpty()) {
			boolean coincideTipo = false;

			for (String tipoBuscado : tiposBuscados) {
				if (normalizarTexto(tipo).equals(normalizarTexto(tipoBuscado))) {
					coincideTipo = true;
					break;
				}
			}

			if (!coincideTipo) {
				return false;
			}
		}

		if (categoriasBuscadas != null && !categoriasBuscadas.isEmpty()) {
			boolean coincideCategoria = false;

			for (String categoriaBuscada : categoriasBuscadas) {
				if (contieneTexto(categorias, categoriaBuscada)) {
					coincideCategoria = true;
					break;
				}
			}

			if (!coincideCategoria) {
				return false;
			}
		}

		return true;
	}

	protected void recargarTablaProductos(JTable tabla) {
		DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
		cargarModeloProductosVenta(modelo);
	}

	private List<String> obtenerNombresCategoriasVenta() {
		return controladorProductos.obtenerNombresCategoriasVenta();
	}

	protected String obtenerTextoCategorias(ProductoVenta producto) {
		return controladorProductos.obtenerTextoCategorias(producto);
	}

	protected String obtenerTipoProductoVenta(ProductoVenta producto) {
		return controladorProductos.obtenerTipoProductoVenta(producto);
	}

	protected List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
		return controladorProductos.obtenerProductosOrdenadosPorStock();
	}

	protected ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
		return controladorProductos.construirLineasPack(texto);
	}

	private String formatearPrecio(double precio) {
		return controladorProductos.formatearPrecio(precio);
	}

	private String formatearPuntuacion(double puntuacion) {
		return controladorProductos.formatearPuntuacion(puntuacion);
	}
}