package Gui.cliente;

import javax.swing.*;
import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCatalogo;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import productos.Pack;
import productos.ProductoVenta;
import usuarios.Cliente;

/**
 * Subpanel del catálogo de productos de CheckPoint. Muestra recomendados arriba
 * y el catálogo completo debajo. Extiende AbstractPanelCliente para reutilizar
 * helpers visuales del cliente. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCatalogo extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Controlador del catálogo. */
	private ControladorCatalogo controlador;

	/** Panel donde se muestran las tarjetas del catálogo. */
	private JPanel panelProductos;

	/** Panel donde se muestran los recomendados. */
	private JPanel panelRecomendados;

	/** Campo de búsqueda por nombre. */
	private JTextField campoBusqueda;

	/** Combo de filtro por categoría. */
	private JComboBox<String> comboCategoria;

	/** Spinner de precio mínimo. */
	private JSpinner spinnerPrecioMin;

	/** Spinner de precio máximo. */
	private JSpinner spinnerPrecioMax;

	/** Combo de orden. */
	private JComboBox<String> comboOrden;

	/** Checkboxes de tipo de producto. */
	private JCheckBox checkJuego;
	private JCheckBox checkPack;
	private JCheckBox checkFigura;
	private JCheckBox checkComic;

	/** Label con el contador de productos encontrados. */
	private JLabel labelContador;

	/** Subpanel de detalle de producto. */
	private SubpanelProducto subpanelProducto;

	/** CardLayout para alternar entre catálogo y detalle. */
	private CardLayout cardLayout;

	/** Panel contenedor del CardLayout. */
	private JPanel panelContenido;

	/** Botones de acción — atributos para registrar el controlador. */
	private JButton botonBuscar;
	private JButton botonReset;

	/** Opciones del combo de orden — paralelo a los métodos del controlador. */
	private static final String ORDEN_ID = "Por ID";
	private static final String ORDEN_NOMBRE_ASC = "Nombre A-Z";
	private static final String ORDEN_NOMBRE_DESC = "Nombre Z-A";
	private static final String ORDEN_PRECIO_ASC = "Precio: menor a mayor";
	private static final String ORDEN_PRECIO_DESC = "Precio: mayor a menor";
	private static final String ORDEN_PUNT_ASC = "Puntuación: menor a mayor";
	private static final String ORDEN_PUNT_DESC = "Puntuación: mayor a menor";

	/**
	 * Constructor del subpanel del catálogo.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelCatalogo(VentanaPrincipal ventana) {
		super(ventana);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelContenido.add(crearPanelCatalogo(), "CATALOGO");

		subpanelProducto = new SubpanelProducto(ventana, this);
		panelContenido.add(subpanelProducto, "PRODUCTO");

		add(panelContenido, BorderLayout.CENTER);
		cardLayout.show(panelContenido, "CATALOGO");
	}

	/**
	 * Actualiza el catálogo con el cliente logueado. Crea el controlador y lo
	 * registra en los botones.
	 *
	 * @param cliente El cliente logueado o null si es invitado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorCatalogo(cliente, this);
		setControlador(controlador);

		comboCategoria.removeAllItems();
		controlador.obtenerNombresCategorias().forEach(comboCategoria::addItem);

		// Ordenar por ID desde el principio
		mostrarProductos(ordenarProductos(new ArrayList<>(controlador.obtenerTodosLosProductos())));
		mostrarRecomendados();
		cardLayout.show(panelContenido, "CATALOGO");
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes.
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		registrar(botonBuscar, c, "buscar");
		registrar(botonReset, c, "reset");
	}

	/**
	 * Registra un ActionListener en un botón quitando los anteriores.
	 *
	 * @param boton El botón a registrar
	 * @param c     El listener a añadir
	 * @param cmd   El ActionCommand a asignar
	 */
	private void registrar(JButton boton, ActionListener c, String cmd) {
		if (boton == null)
			return;
		for (ActionListener al : boton.getActionListeners())
			boton.removeActionListener(al);
		boton.setActionCommand(cmd);
		boton.addActionListener(c);
	}

	/**
	 * Crea el panel principal del catálogo con filtros y área de productos.
	 *
	 * @return Panel del catálogo
	 */
	private JPanel crearPanelCatalogo() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(crearPanelBusqueda(), BorderLayout.NORTH);

		JPanel panelCentral = new JPanel(new BorderLayout());
		panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelRecomendados = new JPanel(new BorderLayout());
		panelRecomendados.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelCentral.add(panelRecomendados, BorderLayout.NORTH);

		panelProductos = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(25), VentanaPrincipal.escalar(25)));
		panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelProductos.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));

		JScrollPane scroll = new JScrollPane(panelProductos);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);

		// Fix scroll — recalcula la altura cuando cambia el ancho del viewport
		scroll.getViewport().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int anchoViewport = scroll.getViewport().getWidth();
				int anchTarjeta = VentanaPrincipal.escalar(300) + VentanaPrincipal.escalar(25);
				int porFila = Math.max(1, anchoViewport / anchTarjeta);
				int numTarjetas = panelProductos.getComponentCount();
				int filas = (int) Math.ceil((double) numTarjetas / porFila);
				int altTarjeta = VentanaPrincipal.escalar(350) + VentanaPrincipal.escalar(25);
				int altoTotal = filas * altTarjeta + VentanaPrincipal.escalar(20);
				panelProductos.setPreferredSize(new Dimension(anchoViewport, altoTotal));
				panelProductos.revalidate();
			}
		});

		panelCentral.add(scroll, BorderLayout.CENTER);

		panel.add(panelCentral, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Crea la barra de filtros superior. Fila 1: búsqueda, categoría, precio,
	 * orden, buscar, limpiar. Fila 2: checkboxes de tipo + contador.
	 *
	 * @return Panel de búsqueda y filtros
	 */
	private JPanel crearPanelBusqueda() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(3), 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		// ── Fila 1 ───────────────────────────────────────────────────────────
		JPanel filaFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(12), 0));
		filaFiltros.setBackground(VentanaPrincipal.COLOR_PANEL);

		filaFiltros.add(crearIcono("/fotos/lupa.jpg", VentanaPrincipal.escalar(30)));

		campoBusqueda = new JTextField(12);
		campoBusqueda.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campoBusqueda.setForeground(Color.BLACK);
		campoBusqueda.setBackground(Color.WHITE);
		campoBusqueda.setCaretColor(Color.BLACK);
		campoBusqueda.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))));
		campoBusqueda.addActionListener(e -> buscar());
		filaFiltros.add(campoBusqueda);
		filaFiltros.add(crearLabel("Categoría:"));
		comboCategoria = new JComboBox<>();
		comboCategoria.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboCategoria.setBackground(Color.WHITE);
		comboCategoria.setForeground(Color.BLACK);
		filaFiltros.add(comboCategoria);

		filaFiltros.add(crearLabel("Precio:"));
		spinnerPrecioMin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 9999.0, 1.0));
		spinnerPrecioMin.setFont(VentanaPrincipal.FUENTE_NORMAL);
		spinnerPrecioMin.setPreferredSize(new Dimension(VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(30)));
		filaFiltros.add(spinnerPrecioMin);

		JLabel labelHasta = new JLabel("—");
		labelHasta.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		filaFiltros.add(labelHasta);

		spinnerPrecioMax = new JSpinner(new SpinnerNumberModel(9999.0, 0.0, 9999.0, 1.0));
		spinnerPrecioMax.setFont(VentanaPrincipal.FUENTE_NORMAL);
		spinnerPrecioMax.setPreferredSize(new Dimension(VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(30)));
		filaFiltros.add(spinnerPrecioMax);

		filaFiltros.add(crearLabel("Ordenar:"));
		comboOrden = new JComboBox<>(new String[] { ORDEN_ID, ORDEN_NOMBRE_ASC, ORDEN_NOMBRE_DESC, ORDEN_PRECIO_ASC,
				ORDEN_PRECIO_DESC, ORDEN_PUNT_ASC, ORDEN_PUNT_DESC });
		comboOrden.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboOrden.setBackground(Color.WHITE);
		comboOrden.setForeground(Color.BLACK);
		comboOrden.setPreferredSize(new Dimension(VentanaPrincipal.escalar(200), VentanaPrincipal.escalar(30)));
		comboOrden.addActionListener(e -> buscar());
		filaFiltros.add(comboOrden);

		// crearBotonNaranja() de PanelBaseInterfaz
		botonBuscar = crearBotonNaranja("Buscar");
		filaFiltros.add(botonBuscar);

		// crearBotonOutline() de PanelBaseInterfaz
		botonReset = crearBotonOutline("Limpiar");
		filaFiltros.add(botonReset);

		panel.add(filaFiltros, BorderLayout.NORTH);

		JPanel panelSur = new JPanel(new BorderLayout());
		panelSur.setBackground(VentanaPrincipal.COLOR_PANEL);

		JPanel filaTipos = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
		filaTipos.setBackground(VentanaPrincipal.COLOR_PANEL);
		filaTipos.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));

		filaTipos.add(crearLabel("Tipo:"));

		checkJuego = crearCheckTipo("Juego");
		checkPack = crearCheckTipo("Pack");
		checkFigura = crearCheckTipo("Figura");
		checkComic = crearCheckTipo("Comic");

		checkJuego.addActionListener(e -> buscar());
		checkPack.addActionListener(e -> buscar());
		checkFigura.addActionListener(e -> buscar());
		checkComic.addActionListener(e -> buscar());

		filaTipos.add(checkJuego);
		filaTipos.add(checkPack);
		filaTipos.add(checkFigura);
		filaTipos.add(checkComic);
		panelSur.add(filaTipos, BorderLayout.NORTH);

		labelContador = new JLabel("Cargando productos...");
		labelContador.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelContador.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelContador.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, 0, 0));
		panelSur.add(labelContador, BorderLayout.SOUTH);

		panel.add(panelSur, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Crea un checkbox de tipo con el estilo de filtro.
	 *
	 * @param texto El texto del checkbox
	 * @return JCheckBox configurado
	 */
	private JCheckBox crearCheckTipo(String texto) {
		JCheckBox check = new JCheckBox(texto);
		check.setFont(VentanaPrincipal.FUENTE_NORMAL);
		check.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		check.setBackground(VentanaPrincipal.COLOR_PANEL);
		check.setFocusPainted(false);
		return check;
	}

	/**
	 * Realiza la búsqueda con los filtros actuales. Aplica nombre, categoría,
	 * precio, tipo y orden. El orden lo delega en el controlador. Lo llama el
	 * controlador y el Enter del campo.
	 */
	public void buscar() {
		if (controlador == null)
			return;
		String texto = campoBusqueda.getText().trim();
		String categoria = (String) comboCategoria.getSelectedItem();
		double precioMin = ((Number) spinnerPrecioMin.getValue()).doubleValue();
		double precioMax = ((Number) spinnerPrecioMax.getValue()).doubleValue();

		List<ProductoVenta> productos = controlador.filtrarProductos(texto, categoria, precioMin, precioMax);

		// Filtrar por tipo si hay alguno seleccionado
		List<String> tiposSeleccionados = new ArrayList<>();
		if (checkJuego != null && checkJuego.isSelected())
			tiposSeleccionados.add("Juego");
		if (checkPack != null && checkPack.isSelected())
			tiposSeleccionados.add("Pack");
		if (checkFigura != null && checkFigura.isSelected())
			tiposSeleccionados.add("Figura");
		if (checkComic != null && checkComic.isSelected())
			tiposSeleccionados.add("Comic");

		if (!tiposSeleccionados.isEmpty()) {
			List<ProductoVenta> filtrados = new ArrayList<>();
			for (ProductoVenta p : productos) {
				if (tiposSeleccionados.contains(obtenerTipoProducto(p)))
					filtrados.add(p);
			}
			productos = filtrados;
		}

		// Delega el orden en el controlador
		mostrarProductos(ordenarProductos(productos));
	}

	/**
	 * Devuelve el tipo de un producto como String legible. Solo se usa aquí para
	 * filtrar por tipo en la vista.
	 *
	 * @param p El producto
	 * @return "Pack", "Juego", "Figura", "Comic" o "Otro"
	 */
	private String obtenerTipoProducto(ProductoVenta p) {
		if (p instanceof Pack)
			return "Pack";
		String nombre = p.getClass().getSimpleName().toLowerCase();
		if (nombre.contains("juego"))
			return "Juego";
		if (nombre.contains("figura"))
			return "Figura";
		if (nombre.contains("comic"))
			return "Comic";
		return "Otro";
	}

	/**
	 * Delega el orden en el controlador según la opción del combo.
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	private List<ProductoVenta> ordenarProductos(List<ProductoVenta> productos) {
		if (controlador == null || comboOrden == null)
			return productos;
		String orden = (String) comboOrden.getSelectedItem();
		if (orden == null)
			return productos;

		switch (orden) {
		case ORDEN_NOMBRE_ASC:
			return controlador.ordenarPorNombreAsc(productos);
		case ORDEN_NOMBRE_DESC:
			return controlador.ordenarPorNombreDesc(productos);
		case ORDEN_PRECIO_ASC:
			return controlador.ordenarPorPrecioAsc(productos);
		case ORDEN_PRECIO_DESC:
			return controlador.ordenarPorPrecioDesc(productos);
		case ORDEN_PUNT_ASC:
			return controlador.ordenarPorPuntuacionAsc(productos);
		case ORDEN_PUNT_DESC:
			return controlador.ordenarPorPuntuacionDesc(productos);
		default: // ORDEN_ID
			return controlador.ordenarPorId(productos);
		}
	}

	/**
	 * Resetea todos los filtros y muestra todos los productos ordenados por ID. Lo
	 * llama el controlador.
	 */
	public void resetearFiltros() {
		campoBusqueda.setText("");
		comboCategoria.setSelectedIndex(0);
		spinnerPrecioMin.setValue(0.0);
		spinnerPrecioMax.setValue(9999.0);
		if (comboOrden != null)
			comboOrden.setSelectedIndex(0);
		if (checkJuego != null)
			checkJuego.setSelected(false);
		if (checkPack != null)
			checkPack.setSelected(false);
		if (checkFigura != null)
			checkFigura.setSelected(false);
		if (checkComic != null)
			checkComic.setSelected(false);
		mostrarProductos(controlador.ordenarPorId(new ArrayList<>(controlador.obtenerTodosLosProductos())));
		mostrarRecomendados();
	}

	/**
	 * Muestra los productos recomendados en la sección superior.
	 */
	private void mostrarRecomendados() {
		panelRecomendados.removeAll();
		if (controlador == null) {
			panelRecomendados.revalidate();
			panelRecomendados.repaint();
			return;
		}

		List<ProductoVenta> recomendados = controlador.getRecomendados();
		if (recomendados.isEmpty()) {
			panelRecomendados.revalidate();
			panelRecomendados.repaint();
			return;
		}

		JPanel contenedor = new JPanel(new BorderLayout());
		contenedor.setBackground(VentanaPrincipal.COLOR_FONDO);
		contenedor.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(10),
				0, VentanaPrincipal.escalar(10)));

		JLabel titulo = new JLabel(" Recomendados para ti");
		titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(10), 0));
		contenedor.add(titulo, BorderLayout.NORTH);

		JPanel panelTarjetas = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(10)));
		panelTarjetas.setBackground(VentanaPrincipal.COLOR_FONDO);
		for (ProductoVenta p : recomendados)
			panelTarjetas.add(crearTarjetaPequeña(p));
		contenedor.add(panelTarjetas, BorderLayout.CENTER);

		JSeparator sep = new JSeparator();
		sep.setForeground(VentanaPrincipal.COLOR_BORDE);
		contenedor.add(sep, BorderLayout.SOUTH);

		panelRecomendados.add(contenedor, BorderLayout.CENTER);
		panelRecomendados.revalidate();
		panelRecomendados.repaint();
	}

	/**
	 * Muestra los productos en el panel como tarjetas.
	 *
	 * @param productos Lista de productos a mostrar
	 */
	private void mostrarProductos(List<ProductoVenta> productos) {
		panelProductos.removeAll();

		if (productos == null || productos.isEmpty()) {
			JLabel labelVacio = new JLabel("No se encontraron productos");
			labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			panelProductos.add(labelVacio);
			labelContador.setText("0 productos encontrados");
		} else {
			for (ProductoVenta p : productos)
				panelProductos.add(crearTarjeta(p));
			labelContador.setText(productos.size() + " productos encontrados");
		}

		panelProductos.revalidate();
		panelProductos.repaint();

		// Forzar recálculo del tamaño preferido tras añadir tarjetas
		SwingUtilities.invokeLater(() -> {
			panelProductos.firePropertyChange("preferredSize", 0, 1);
			panelProductos.revalidate();
			panelProductos.repaint();
		});
	}

	/**
	 * Crea una tarjeta grande para el catálogo principal. Si es Pack muestra badge
	 * y ahorro.
	 *
	 * @param producto El producto a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjeta(ProductoVenta producto) {
		JPanel tarjeta = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(10)));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setPreferredSize(new Dimension(VentanaPrincipal.escalar(300), VentanaPrincipal.escalar(350)));
		tarjeta.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(35), VentanaPrincipal.escalar(8),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8))));

		tarjeta.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				tarjeta.setBorder(BorderFactory
						.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO),
								BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(45),
										VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8),
										VentanaPrincipal.escalar(8))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tarjeta.setBorder(
						BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
								BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(35),
										VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8),
										VentanaPrincipal.escalar(8))));
			}
		});

		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(160), VentanaPrincipal.escalar(130)));
		// cargarImagen() de PanelBaseInterfaz
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(150),
				VentanaPrincipal.escalar(130));
		tarjeta.add(labelImagen, BorderLayout.NORTH);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		// Badge PACK
		if (producto instanceof Pack) {
			JLabel badge = new JLabel("PACK");
			badge.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			badge.setForeground(Color.WHITE);
			badge.setOpaque(true);
			badge.setBackground(VentanaPrincipal.COLOR_ACENTO);
			badge.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(2), VentanaPrincipal.escalar(6),
					VentanaPrincipal.escalar(2), VentanaPrincipal.escalar(6)));
			badge.setAlignmentX(Component.CENTER_ALIGNMENT);
			panelInfo.add(badge);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(3)));
		}

		String nombre = producto.getNombre();
		if (nombre.length() > 18)
			nombre = nombre.substring(0, 16) + "...";
		JLabel labelNombre = new JLabel(nombre);
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelNombre.setBorder(
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(5), 0));
		panelInfo.add(labelNombre);

		String desc = producto.getDescripcion();
		if (desc.length() > 30)
			desc = desc.substring(0, 28) + "...";
		JLabel labelDesc = new JLabel(desc);
		labelDesc.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelDesc.setBorder(
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(8), 0));
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(3)));
		panelInfo.add(labelDesc);

		JLabel labelPrecio = new JLabel(String.format("%.2f€", producto.getPrecioOficial()));
		labelPrecio.setFont(VentanaPrincipal.FUENTE_PRECIO);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelPrecio.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(4), 0));
		panelInfo.add(labelPrecio);

		
		if (producto.getStockDisponible() <= 0) {
		    JLabel badgeAgotado = new JLabel("AGOTADO");
		    badgeAgotado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		    badgeAgotado.setForeground(Color.WHITE);
		    badgeAgotado.setOpaque(true);
		    badgeAgotado.setBackground(new Color(180, 50, 50));
		    badgeAgotado.setBorder(BorderFactory.createEmptyBorder(
		        VentanaPrincipal.escalar(2), VentanaPrincipal.escalar(6),
		        VentanaPrincipal.escalar(2), VentanaPrincipal.escalar(6)));
		    badgeAgotado.setAlignmentX(Component.CENTER_ALIGNMENT);
		    panelInfo.add(badgeAgotado);
		    panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(3)));
		}

		JLabel labelNota = producto.getReseñas().isEmpty()
		    ? new JLabel("Sin reseñas")
		    : new JLabel(String.format("%.1f/10", producto.getMediaPuntuacion()));
		labelNota.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelNota.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelNota.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelNota.setBorder(BorderFactory.createEmptyBorder(
		    0, 0, VentanaPrincipal.escalar(6), 0));
		panelInfo.add(labelNota);

		JButton botonVer = crearBotonNaranja("Ver información");
		botonVer.setAlignmentX(Component.CENTER_ALIGNMENT);
		botonVer.setActionCommand("ver:" + producto.getId());
		botonVer.addActionListener(controlador);
		// Si está agotado deshabilitamos el botón
		if (producto.getStockDisponible() <= 0) {
		    botonVer.setEnabled(false);
		    botonVer.setToolTipText("Producto agotado");
		}
		panelInfo.add(botonVer);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Crea una tarjeta pequeña para la sección de recomendados.
	 *
	 * @param producto El producto a mostrar
	 * @return Panel con la tarjeta pequeña
	 */
	private JPanel crearTarjetaPequeña(ProductoVenta producto) {
		JPanel tarjeta = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(5)));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setPreferredSize(new Dimension(VentanaPrincipal.escalar(160), VentanaPrincipal.escalar(210)));
		tarjeta.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(6),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(6))));

		tarjeta.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				tarjeta.setBorder(BorderFactory
						.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO),
								BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8),
										VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
										VentanaPrincipal.escalar(6))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tarjeta.setBorder(
						BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
								BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8),
										VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
										VentanaPrincipal.escalar(6))));
			}
		});

		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(90), VentanaPrincipal.escalar(80)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(70));
		tarjeta.add(labelImagen, BorderLayout.NORTH);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		String nombre = producto.getNombre();
		if (nombre.length() > 14)
			nombre = nombre.substring(0, 12) + "...";
		JLabel labelNombre = new JLabel(nombre);
		labelNombre.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelNombre);

		JLabel labelPrecio = new JLabel(String.format("%.2f€", producto.getPrecioOficial()));
		labelPrecio.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelPrecio);

		JLabel labelNota = producto.getReseñas().isEmpty() ? new JLabel("Sin reseñas")
				: new JLabel(String.format(" %.1f/10", producto.getMediaPuntuacion()));
		labelNota.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelNota.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelNota.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelNota);

		JButton botonVer = crearBotonNaranja("Ver");
		botonVer.setAlignmentX(Component.CENTER_ALIGNMENT);
		botonVer.setActionCommand("ver:" + producto.getId());
		botonVer.addActionListener(controlador);
		panelInfo.add(botonVer);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Navega a la pantalla de detalle del producto.
	 *
	 * @param producto El producto a ver
	 */
	public void verProducto(ProductoVenta producto) {
		subpanelProducto.mostrarProducto(producto, cliente, controlador);
		cardLayout.show(panelContenido, "PRODUCTO");
	}

	/**
	 * Vuelve al catálogo desde la pantalla de detalle.
	 */
	public void volverDelProducto() {
		cardLayout.show(panelContenido, "CATALOGO");
	}

	/**
	 * Ordena y muestra los productos indicados delegando en el controlador.
	 *
	 * @param productos Lista de productos
	 */
	public void ordenarYMostrar(List<ProductoVenta> productos) {
		mostrarProductos(ordenarProductos(new ArrayList<>(productos)));
	}

	/**
	 * Crea un icono desde la carpeta de recursos.
	 *
	 * @param ruta   Ruta relativa del icono
	 * @param tamano Tamaño en píxeles escalados
	 * @return JLabel con el icono
	 */
	private JLabel crearIcono(String ruta, int tamano) {
		JLabel label = new JLabel();
		try {
			URL url = getClass().getResource(ruta);
			if (url != null) {
				BufferedImage img = ImageIO.read(url);
				if (img != null) {
					Image imgEscalada = img.getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
					label.setIcon(new ImageIcon(imgEscalada));
				}
			}
		} catch (IOException e) {

		}
		return label;
	}
	/**
	 * Recarga el combo de categorías con las activas actuales.
	 * Lo llama PanelGestor tras crear o eliminar una categoría.
	 */
	public void refrescarComboCategorias() {
	    if (controlador == null) return;
	    comboCategoria.removeAllItems();
	    controlador.obtenerNombresCategorias().forEach(comboCategoria::addItem);
	}
}