package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorProductosEmpleado;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import productos.LineaPack;
import productos.ProductoVenta;
import usuarios.Empleado;

/**
 * Base para pantallas que usan productos de venta.
 * 
 * Aqui se deja la tabla comun para no repetirla.
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

	protected static class TablaVenta {
		JPanel bloque;
		JTable tabla;

		TablaVenta(JPanel bloque, JTable tabla) {
			this.bloque = bloque;
			this.tabla = tabla;
		}
	}

	protected SeccionProductosVentaEmpleadoBase(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
	}

	protected TablaVenta crearTablaProductosVenta(String titulo, String ayuda, boolean incluirRefrescar,
			JTextField... camposId) {

		JPanel bloque = crearBloque(titulo);

		DefaultTableModel modelo = crearModeloTablaVenta();
		JTable tabla = new JTable(modelo);

		estilizarTabla(tabla);
		cargarModeloProductosVenta(modelo);
		conectarSeleccionId(tabla, camposId);

		JLabel labelAyuda = crearLabel(ayuda);

		JScrollPane scroll = estilizarScroll(tabla);
		Dimension tamanoTabla = new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(240));
		tabla.setPreferredScrollableViewportSize(tamanoTabla);
		scroll.setPreferredSize(tamanoTabla);
		scroll.setMinimumSize(tamanoTabla);
		scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, tamanoTabla.height));

		JPanel panelFiltros = crearPanelFiltrosVenta(modelo);

		bloque.add(labelAyuda, gbcCampo(1));
		bloque.add(panelFiltros, gbcCampo(2));
		bloque.add(scroll, gbcCampo(3));

		if (incluirRefrescar) {
			JButton botonRefrescar = crearBotonSecundario("Refrescar");
			Dimension tamanoBoton = new Dimension(VentanaPrincipal.escalar(220), VentanaPrincipal.escalar(38));
			botonRefrescar.setPreferredSize(tamanoBoton);
			botonRefrescar.setMinimumSize(tamanoBoton);
			botonRefrescar.setMaximumSize(tamanoBoton);

			botonRefrescar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cargarModeloProductosVenta(modelo);
				}
			});

			JPanel filaBoton = new JPanel(new BorderLayout());
			filaBoton.setOpaque(false);
			filaBoton.add(botonRefrescar, BorderLayout.WEST);

			bloque.add(filaBoton, gbcBoton(4));
		}

		return new TablaVenta(bloque, tabla);
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

	private void conectarSeleccionId(JTable tabla, JTextField... camposId) {
		tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
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

				for (JTextField campo : camposId) {
					if (campo != null) {
						campo.setText(String.valueOf(idProducto));
					}
				}
			}
		});
	}

	private JPanel crearPanelFiltrosVenta(DefaultTableModel modelo) {
		JPanel panelFiltros = new JPanel();
		panelFiltros.setOpaque(false);
		panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));

		JTextField campoBuscar = crearCampoCompacto();
		Dimension tamanoCampoBuscar = new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(34));
		campoBuscar.setPreferredSize(tamanoCampoBuscar);
		campoBuscar.setMinimumSize(tamanoCampoBuscar);
		campoBuscar.setMaximumSize(tamanoCampoBuscar);

		JComboBox<String> comboOrden = crearCombo(new String[] { "Sin ordenar", "Nombre A-Z", "Nombre Z-A",
				"Precio: menor a mayor", "Precio: mayor a menor", "Stock: menor a mayor", "Stock: mayor a menor",
				"Puntuación: menor a mayor", "Puntuación: mayor a menor" });
		Dimension tamanoComboOrden = new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(34));
		comboOrden.setPreferredSize(tamanoComboOrden);
		comboOrden.setMinimumSize(tamanoComboOrden);
		comboOrden.setMaximumSize(tamanoComboOrden);

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
		Dimension tamanoBotonLimpiar = new Dimension(VentanaPrincipal.escalar(140), VentanaPrincipal.escalar(34));
		botonLimpiar.setPreferredSize(tamanoBotonLimpiar);
		botonLimpiar.setMinimumSize(tamanoBotonLimpiar);
		botonLimpiar.setMaximumSize(tamanoBotonLimpiar);

		int altoZonaFiltro = VentanaPrincipal.escalar(70);

		JPanel zonaBuscar = new JPanel();
		zonaBuscar.setOpaque(false);
		zonaBuscar.setLayout(new BoxLayout(zonaBuscar, BoxLayout.Y_AXIS));
		zonaBuscar.setPreferredSize(new Dimension(VentanaPrincipal.escalar(650), altoZonaFiltro));
		zonaBuscar.setMaximumSize(new Dimension(VentanaPrincipal.escalar(650), altoZonaFiltro));
		JLabel labelBuscar = crearLabel("Buscar");
		labelBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
		campoBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
		zonaBuscar.add(labelBuscar);
		zonaBuscar.add(Box.createVerticalStrut(VentanaPrincipal.escalar(4)));
		zonaBuscar.add(campoBuscar);

		JPanel zonaOrden = new JPanel();
		zonaOrden.setOpaque(false);
		zonaOrden.setLayout(new BoxLayout(zonaOrden, BoxLayout.Y_AXIS));
		zonaOrden.setPreferredSize(new Dimension(VentanaPrincipal.escalar(230), altoZonaFiltro));
		zonaOrden.setMaximumSize(new Dimension(VentanaPrincipal.escalar(230), altoZonaFiltro));
		JLabel labelOrden = crearLabel("Ordenar por");
		labelOrden.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboOrden.setAlignmentX(Component.LEFT_ALIGNMENT);
		zonaOrden.add(labelOrden);
		zonaOrden.add(Box.createVerticalStrut(VentanaPrincipal.escalar(4)));
		zonaOrden.add(comboOrden);

		JPanel zonaBotonLimpiar = new JPanel();
		zonaBotonLimpiar.setOpaque(false);
		zonaBotonLimpiar.setLayout(new BoxLayout(zonaBotonLimpiar, BoxLayout.Y_AXIS));
		zonaBotonLimpiar.add(Box.createVerticalStrut(VentanaPrincipal.escalar(24)));
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

		Runnable refrescarTabla = new Runnable() {
			@Override
			public void run() {
				List<String> tipos = obtenerChecksMarcados(checksTipo);
				List<String> categorias = obtenerChecksMarcados(checksCategoria);
				String orden = String.valueOf(comboOrden.getSelectedItem());

				cargarModeloProductosVenta(modelo, campoBuscar.getText(), tipos, categorias, orden);
			}
		};

		escucharCambios(campoBuscar, refrescarTabla);

		ActionListener actualizarFiltro = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refrescarTabla.run();
			}
		};

		comboOrden.addActionListener(actualizarFiltro);

		for (JCheckBox check : checksTipo) {
			check.addActionListener(actualizarFiltro);
		}

		for (JCheckBox check : checksCategoria) {
			check.addActionListener(actualizarFiltro);
		}

		botonLimpiar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				campoBuscar.setText("");
				comboOrden.setSelectedIndex(0);

				for (JCheckBox check : checksTipo) {
					check.setSelected(false);
				}

				for (JCheckBox check : checksCategoria) {
					check.setSelected(false);
				}

				cargarModeloProductosVenta(modelo);
			}
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

	protected void cargarModeloProductosVenta(DefaultTableModel modelo) {
		cargarModeloProductosVenta(modelo, "", new ArrayList<>(), new ArrayList<>());
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modelo, String textoBuscado, List<String> tiposBuscados,
			List<String> categoriasBuscadas) {

		cargarModeloProductosVenta(modelo, textoBuscado, tiposBuscados, categoriasBuscadas, "Sin ordenar");
	}

	protected void cargarModeloProductosVenta(DefaultTableModel modelo, String textoBuscado, List<String> tiposBuscados,
			List<String> categoriasBuscadas, String orden) {

		modelo.setRowCount(0);

		String texto = normalizarTexto(textoBuscado);
		List<ProductoVenta> productos = new ArrayList<>();

		for (ProductoVenta producto : obtenerProductosOrdenadosPorStock()) {
			String id = producto.getId();
			String nombre = producto.getNombre();
			String tipo = obtenerTipoProductoVenta(producto);
			String categorias = obtenerTextoCategorias(producto);

			if (productoPasaFiltros(id, nombre, tipo, categorias, texto, tiposBuscados, categoriasBuscadas)) {
				productos.add(producto);
			}
		}

		ordenarProductos(productos, orden);

		for (ProductoVenta producto : productos) {
			String id = producto.getId();
			String nombre = producto.getNombre();
			String tipo = obtenerTipoProductoVenta(producto);
			String categorias = obtenerTextoCategorias(producto);

			modelo.addRow(new Object[] { id, nombre, tipo, categorias, formatearPrecio(producto.getPrecioOficial()),
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

				case "Stock: mayor a menor":
					return Integer.compare(p2.getStockDisponible(), p1.getStockDisponible());

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

	protected void cargarModeloProductosVenta(DefaultTableModel modelo, String textoBuscado, String tipoBuscado,
			String categoriaBuscada) {

		List<String> tipos = new ArrayList<>();
		List<String> categorias = new ArrayList<>();

		if (tipoBuscado != null && !tipoBuscado.equalsIgnoreCase("Todos")) {
			tipos.add(tipoBuscado);
		}

		if (categoriaBuscada != null && !categoriaBuscada.equalsIgnoreCase("Todas")) {
			categorias.add(categoriaBuscada);
		}

		cargarModeloProductosVenta(modelo, textoBuscado, tipos, categorias);
	}

	private boolean productoPasaFiltros(String id, String nombre, String tipo, String categorias, String texto,
			List<String> tiposBuscados, List<String> categoriasBuscadas) {

		if (!texto.isBlank()) {
			boolean encontradoPorTexto = contieneTexto(id, texto) || contieneTexto(nombre, texto)
					|| contieneTexto(tipo, texto) || contieneTexto(categorias, texto);

			if (!encontradoPorTexto) {
				return false;
			}
		}

		if (tiposBuscados != null && !tiposBuscados.isEmpty()) {
			boolean tieneTipo = false;

			for (String tipoBuscado : tiposBuscados) {
				if (normalizarTexto(tipo).equals(normalizarTexto(tipoBuscado))) {
					tieneTipo = true;
					break;
				}
			}

			if (!tieneTipo) {
				return false;
			}
		}

		if (categoriasBuscadas != null && !categoriasBuscadas.isEmpty()) {
			boolean tieneCategoria = false;

			for (String categoriaBuscada : categoriasBuscadas) {
				if (contieneTexto(categorias, categoriaBuscada)) {
					tieneCategoria = true;
					break;
				}
			}

			if (!tieneCategoria) {
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
