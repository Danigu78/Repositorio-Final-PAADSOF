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
 * Base para pantallas del empleado que trabajan con productos de venta.
 *
 * @author Lucas
 * @version 1.0
 */
public abstract class SeccionProductosVentaEmpleadoBase extends SeccionEmpleadoBase {

	private static final long serialVersionUID = 1L;

	/**
	 * Índices de columnas de la tabla de productos de venta.
	 */
	protected static final int COL_VENTA_ID = 0;

	/**
	 * Columna del nombre del producto.
	 */
	protected static final int COL_VENTA_NOMBRE = 1;

	/**
	 * Columna del tipo del producto.
	 */
	protected static final int COL_VENTA_TIPO = 2;

	/**
	 * Columna de categorías del producto.
	 */
	protected static final int COL_VENTA_CATEGORIAS = 3;

	/**
	 * Columna del precio del producto.
	 */
	protected static final int COL_VENTA_PRECIO = 4;

	/**
	 * Columna del stock disponible.
	 */
	protected static final int COL_VENTA_STOCK = 5;

	/**
	 * Columna de puntuación media.
	 */
	protected static final int COL_VENTA_PUNTUACION = 6;

	/**
	 * Controlador común de productos de venta.
	 */
	protected final ControladorProductosEmpleado controladorProductos = new ControladorProductosEmpleado();

	/**
	 * Contenedor que agrupa la tabla y su panel contenedor.
	 */
	protected static class TablaVenta {
		/** Panel raíz del bloque visual de la tabla */
		JPanel bloque;

		/** Tabla de productos */
		JTable tabla;

		/**
		 * Constructor del contenedor de tabla.
		 *
		 * @param bloque panel contenedor de la sección
		 * @param tabla  tabla de productos
		 */
		TablaVenta(JPanel bloque, JTable tabla) {
			this.bloque = bloque;
			this.tabla = tabla;
		}
	}

	/**
	 * Constructor base de la sección de productos de venta.
	 *
	 * @param ventana  ventana principal de la aplicación
	 * @param empleado empleado autenticado que usa la interfaz
	 */
	protected SeccionProductosVentaEmpleadoBase(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
	}

	/**
	 * Crea una tabla completa de productos de venta con filtros y buscador.
	 *
	 * @param titulo           título del bloque visual
	 * @param ayuda            texto explicativo para el usuario
	 * @param incluirRefrescar indica si se muestra botón de refresco manual
	 * @param camposId         campos de texto que se rellenarán automáticamente al
	 *                         seleccionar un producto
	 * @return contenedor con la tabla y su panel asociado
	 */
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

	/**
	 * Crea el modelo de la tabla de productos de venta.
	 *
	 * @return modelo de tabla no editable
	 */
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

	/**
	 * Aplica estilo visual a la tabla de productos.
	 *
	 * @param tabla tabla a estilizar
	 */
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

	/**
	 * Conecta la selección de una fila con campos de texto destino.
	 *
	 * @param tabla    tabla de productos
	 * @param camposId campos que recibirán el ID seleccionado
	 */
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

	/**
	 * Crea el panel de filtros (búsqueda, ordenación, tipos y categorías).
	 *
	 * @param modelo modelo de tabla a actualizar
	 * @return panel de filtros
	 */
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

	/**
	 * Crea una fila visual de filtros con checkboxes.
	 *
	 * @param titulo título del grupo de filtros
	 * @param checks array de checkboxes
	 * @return panel de la fila construida
	 */
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

	/**
	 * Crea un checkbox estilizado para filtros.
	 *
	 * @param texto texto del checkbox
	 * @return checkbox configurado
	 */
	private JCheckBox crearCheckFiltro(String texto) {
		JCheckBox check = new JCheckBox(texto);

		check.setOpaque(false);
		check.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		check.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		check.setFocusPainted(false);

		return check;
	}

	/**
	 * Obtiene los textos de los checkboxes seleccionados.
	 *
	 * @param checks array de checkboxes
	 * @return lista de textos seleccionados
	 */
	private List<String> obtenerChecksMarcados(JCheckBox[] checks) {
		List<String> marcados = new ArrayList<>();

		for (JCheckBox check : checks) {
			if (check.isSelected()) {
				marcados.add(check.getText());
			}
		}

		return marcados;
	}

	/**
	 * Carga todos los productos sin aplicar filtros.
	 *
	 * @param modelo modelo de tabla donde se insertan los datos
	 */
	protected void cargarModeloProductosVenta(DefaultTableModel modelo) {
		cargarModeloProductosVenta(modelo, "", new ArrayList<>(), new ArrayList<>());
	}

	/**
	 * Carga productos aplicando texto, tipos y categorías.
	 *
	 * @param modelo             modelo de tabla
	 * @param textoBuscado       texto de búsqueda
	 * @param tiposBuscados      tipos seleccionados
	 * @param categoriasBuscadas categorías seleccionadas
	 */
	protected void cargarModeloProductosVenta(DefaultTableModel modelo, String textoBuscado, List<String> tiposBuscados,
			List<String> categoriasBuscadas) {

		cargarModeloProductosVenta(modelo, textoBuscado, tiposBuscados, categoriasBuscadas, "Sin ordenar");
	}

	/**
	 * Carga productos aplicando filtros completos y ordenación.
	 *
	 * @param modelo             modelo de tabla
	 * @param textoBuscado       texto de búsqueda
	 * @param tiposBuscados      tipos seleccionados
	 * @param categoriasBuscadas categorías seleccionadas
	 * @param orden              criterio de ordenación
	 */
	protected void cargarModeloProductosVenta(DefaultTableModel modelo, String textoBuscado, List<String> tiposBuscados,
			List<String> categoriasBuscadas, String orden) {

		modelo.setRowCount(0);

		String texto = normalizarTexto(textoBuscado);
		List<ProductoVenta> productos = new ArrayList<>();

		for (ProductoVenta producto : obtenerProductosOrdenadosPorStock()) {
			if (producto.isEliminado()) {
				continue;
			}

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

	/**
	 * Ordena la lista de productos según el criterio indicado.
	 *
	 * @param productos lista de productos a ordenar
	 * @param orden     criterio de ordenación seleccionado
	 */
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

	/**
	 * Convierte un ID de producto (PV-XXX) a número entero.
	 *
	 * @param id identificador del producto
	 * @return número extraído del ID o 0 si es inválido
	 */
	private int numeroIdProducto(String id) {
		if (id == null || !id.startsWith("PV-")) {
			return 0;
		}

		return Integer.parseInt(id.substring(3));
	}

	/**
	 * Carga los productos de venta en el modelo aplicando filtros simples de tipo y categoría.
	 *
	 * @param modelo modelo de la tabla donde se insertarán los productos
	 * @param textoBuscado texto de búsqueda aplicado sobre ID, nombre, tipo y categorías
	 * @param tipoBuscado tipo de producto seleccionado (puede ser "Todos" o null para no filtrar)
	 * @param categoriaBuscada categoría seleccionada (puede ser "Todas" o null para no filtrar)
	 */
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

	/**
	 * Evalúa si un producto cumple los filtros aplicados.
	 *
	 * @param id                 id del producto
	 * @param nombre             nombre del producto
	 * @param tipo               tipo del producto
	 * @param categorias         categorías del producto
	 * @param texto              texto de búsqueda
	 * @param tiposBuscados      tipos seleccionados
	 * @param categoriasBuscadas categorías seleccionadas
	 * @return true si el producto cumple los filtros
	 */
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

	/**
	 * Recarga la tabla a partir del modelo actual.
	 *
	 * @param tabla tabla que se desea recargar
	 */
	protected void recargarTablaProductos(JTable tabla) {
		DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
		cargarModeloProductosVenta(modelo);
	}

	/**
	 * Obtiene nombres de categorías disponibles.
	 *
	 * @return lista de nombres de categorías
	 */
	private List<String> obtenerNombresCategoriasVenta() {
		return controladorProductos.obtenerNombresCategoriasVenta();
	}

	/**
	 * Obtiene categorías formateadas de un producto.
	 *
	 * @param producto producto de venta
	 * @return texto de categorías
	 */
	protected String obtenerTextoCategorias(ProductoVenta producto) {
		return controladorProductos.obtenerTextoCategorias(producto);
	}

	/**
	 * Obtiene el tipo del producto de venta.
	 *
	 * @param producto producto de venta
	 * @return tipo del producto
	 */
	protected String obtenerTipoProductoVenta(ProductoVenta producto) {
		return controladorProductos.obtenerTipoProductoVenta(producto);
	}

	/**
	 * Obtiene lista de productos ordenados por stock.
	 *
	 * @return lista de productos
	 */
	protected List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
		return controladorProductos.obtenerProductosOrdenadosPorStock();
	}

	/**
	 * Construye líneas de pack a partir de texto.
	 *
	 * @param texto texto de entrada
	 * @return lista de líneas de pack
	 * @throws Exception si el formato es inválido
	 */
	protected ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
		return controladorProductos.construirLineasPack(texto);
	}

	/**
	 * Formatea un precio a formato visual.
	 *
	 * @param precio valor del precio
	 * @return precio formateado
	 */
	private String formatearPrecio(double precio) {
		return controladorProductos.formatearPrecio(precio);
	}

	/**
	 * Formatea una puntuación media.
	 *
	 * @param puntuacion valor de la puntuación
	 * @return puntuación formateada
	 */
	private String formatearPuntuacion(double puntuacion) {
		return controladorProductos.formatearPuntuacion(puntuacion);
	}
}
