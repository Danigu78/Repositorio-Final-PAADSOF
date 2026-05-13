package Gui.cliente;

import javax.swing.*;
import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorSegundaMano;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import productos.EstadoProducto;
import productos.Producto2Mano;
import usuarios.Cliente;

/**
 * Subpanel del catálogo de segunda mano. Extiende AbstractPanelCliente para
 * reutilizar helpers visuales del cliente. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelSegundaMano extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Controlador del subpanel. */
	private ControladorSegundaMano controlador;

	/** Panel donde se muestran las tarjetas de productos. */
	private JPanel panelProductos;

	private JScrollPane scrollProductos;

	/** Campo de búsqueda por nombre de producto. */
	private JTextField campoBusqueda;

	/** Campo de búsqueda por nickname de usuario. */
	private JTextField campoUsuario;

	/** Spinner de precio mínimo. */
	private JSpinner spinnerPrecioMin;

	/** Spinner de precio máximo. */
	private JSpinner spinnerPrecioMax;

	/** Combo de filtro por estado mínimo. */
	private JComboBox<String> comboEstado;

	/** Combo de orden. */
	private JComboBox<String> comboOrden;

	/** Label con el contador de productos encontrados. */
	private JLabel labelContador;

	/** CardLayout para alternar entre catálogo y detalle. */
	private CardLayout cardLayout;

	/** Panel contenedor del CardLayout. */
	private JPanel panelContenido;

	/** Subpanel de detalle de producto de segunda mano. */
	private SubpanelProducto2Mano subpanelProducto2Mano;

	/** Opciones del combo de orden. */
	private static final String ORDEN_NOMBRE_ASC = "Nombre A-Z";
	private static final String ORDEN_NOMBRE_DESC = "Nombre Z-A";
	private static final String ORDEN_PRECIO_ASC = "Precio: menor a mayor";
	private static final String ORDEN_PRECIO_DESC = "Precio: mayor a menor";
	private static final String ORDEN_ESTADO_DESC = "Estado: mejor primero";
	private static final String ORDEN_ESTADO_ASC = "Estado: peor primero";

	/**
	 * Constructor del subpanel de segunda mano.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelSegundaMano(VentanaPrincipal ventana) {
		super(ventana);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		subpanelProducto2Mano = new SubpanelProducto2Mano(ventana, this);
		panelContenido.add(subpanelProducto2Mano, "PRODUCTO2MANO");
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.add(crearPanelCatalogo(), "CATALOGO");
		cardLayout.show(panelContenido, "CATALOGO");
		add(panelContenido, BorderLayout.CENTER);
	}

	/**
	 * Actualiza el subpanel con el cliente logueado.
	 *
	 * @param cliente El cliente logueado o null
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorSegundaMano(this, cliente);
		comboEstado.removeAllItems();
		for (String s : controlador.getEstados())
			comboEstado.addItem(s);
		mostrarProductos(controlador.obtenerTodos());
		cardLayout.show(panelContenido, "CATALOGO");
	}

	/**
	 * Crea el panel principal del catálogo con filtros y área de productos.
	 *
	 * @return Panel del catálogo
	 */
	private JPanel crearPanelCatalogo() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(crearPanelFiltros(), BorderLayout.NORTH);

		panelProductos = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(25), VentanaPrincipal.escalar(25)));
		panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelProductos.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));

		scrollProductos = new JScrollPane(panelProductos);
		scrollProductos.setBorder(null);
		scrollProductos.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		scrollProductos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollProductos.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);

		// Fix scroll recalcula la altura cuando cambia el ancho del viewport
		scrollProductos.getViewport().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				ajustarTamanoPanelProductos();
			}
		});

		panel.add(scrollProductos, BorderLayout.CENTER);
		return panel;
	}

	private void ajustarTamanoPanelProductos() {
		if (scrollProductos == null || panelProductos == null)
			return;
		int anchoViewport = scrollProductos.getViewport().getWidth();
		if (anchoViewport <= 0)
			return;
		int anchTarjeta = VentanaPrincipal.escalar(300) + VentanaPrincipal.escalar(25);
		int porFila = Math.max(1, anchoViewport / anchTarjeta);
		int numTarjetas = Math.max(1, panelProductos.getComponentCount());
		int filas = (int) Math.ceil((double) numTarjetas / porFila);
		int altTarjeta = VentanaPrincipal.escalar(350) + VentanaPrincipal.escalar(25);
		int altoTotal = filas * altTarjeta + VentanaPrincipal.escalar(20);
		panelProductos.setPreferredSize(new Dimension(anchoViewport, altoTotal));
		panelProductos.revalidate();
	}

	/**
	 * Crea la barra de filtros superior. Fila 1: búsqueda, precio, estado, orden,
	 * buscar, limpiar. Fila 2: búsqueda por usuario + contador.
	 *
	 * @return Panel de filtros
	 */
	private JPanel crearPanelFiltros() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(3), 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		JPanel filaFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, VentanaPrincipal.escalar(12), 0));
		filaFiltros.setBackground(VentanaPrincipal.COLOR_PANEL);

		filaFiltros.add(crearIcono("/fotos/lupa.jpg", VentanaPrincipal.escalar(30)));
		filaFiltros.add(crearLabel("Producto:"));

		campoBusqueda = new JTextField(10);
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

		filaFiltros.add(crearLabel("Estado mínimo:"));
		comboEstado = new JComboBox<>();
		comboEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboEstado.setBackground(Color.WHITE);
		comboEstado.setForeground(Color.BLACK);
		filaFiltros.add(comboEstado);

		filaFiltros.add(crearLabel("Ordenar:"));
		comboOrden = new JComboBox<>(new String[] { "Sin ordenar", ORDEN_NOMBRE_ASC, ORDEN_NOMBRE_DESC,
				ORDEN_PRECIO_ASC, ORDEN_PRECIO_DESC, ORDEN_ESTADO_DESC, ORDEN_ESTADO_ASC });
		comboOrden.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboOrden.setBackground(Color.WHITE);
		comboOrden.setForeground(Color.BLACK);
		comboOrden.setPreferredSize(new Dimension(VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(30)));
		comboOrden.addActionListener(e -> buscar());
		filaFiltros.add(comboOrden);

		JButton botonBuscar = crearBotonNaranja("Buscar");
		botonBuscar.addActionListener(e -> buscar());
		filaFiltros.add(botonBuscar);

		JButton botonReset = crearBotonOutline("Limpiar");
		botonReset.addActionListener(e -> resetear());
		filaFiltros.add(botonReset);

		panel.add(filaFiltros, BorderLayout.NORTH);

		JPanel filaUsuario = new JPanel(new FlowLayout(FlowLayout.CENTER, VentanaPrincipal.escalar(15), 0));
		filaUsuario.setBackground(VentanaPrincipal.COLOR_PANEL);
		filaUsuario.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));

		filaUsuario.add(crearLabel("Ver cartera de:"));

		campoUsuario = new JTextField(12);
		campoUsuario.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campoUsuario.setForeground(Color.BLACK);
		campoUsuario.setBackground(Color.WHITE);
		campoUsuario.setCaretColor(Color.BLACK);
		campoUsuario.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))));
		campoUsuario.addActionListener(e -> buscarCartera());
		filaUsuario.add(campoUsuario);

		JButton botonCartera = crearBotonNaranja("Ver cartera");
		botonCartera.addActionListener(e -> buscarCartera());
		filaUsuario.add(botonCartera);

		labelContador = new JLabel("Cargando productos...");
		labelContador.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelContador.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelContador.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, 0, 0));

		JPanel panelSur = new JPanel(new BorderLayout());
		panelSur.setBackground(VentanaPrincipal.COLOR_PANEL);
		panelSur.add(filaUsuario, BorderLayout.NORTH);
		panelSur.add(labelContador, BorderLayout.SOUTH);
		panel.add(panelSur, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Realiza la búsqueda con los filtros actuales y aplica el orden.
	 */
	private void buscar() {
		if (controlador == null)
			return;
		String nombre = campoBusqueda.getText().trim();
		double precioMin = ((Number) spinnerPrecioMin.getValue()).doubleValue();
		double precioMax = ((Number) spinnerPrecioMax.getValue()).doubleValue();
		EstadoProducto estado = controlador.textoAEstado((String) comboEstado.getSelectedItem());
		List<Producto2Mano> productos = controlador.filtrar(nombre, precioMin, precioMax, estado);
		mostrarProductos(ordenarProductos(productos));
	}

	/**
	 * Delega el orden en el controlador según la opción del combo.
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	private List<Producto2Mano> ordenarProductos(List<Producto2Mano> productos) {
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
		case ORDEN_ESTADO_DESC:
			return controlador.ordenarPorEstadoDesc(productos);
		case ORDEN_ESTADO_ASC:
			return controlador.ordenarPorEstadoAsc(productos);
		default:
			return productos;
		}
	}

	/**
	 * Busca y muestra la cartera de un usuario por nickname.
	 */
	private void buscarCartera() {
		if (controlador == null)
			return;
		String nickname = campoUsuario.getText().trim();
		if (nickname.isBlank()) {
			mostrarProductos(controlador.obtenerTodos());
			return;
		}
		List<Producto2Mano> productos = controlador.verCarteraDeUsuario(nickname);
		if (productos == null) {
			JOptionPane.showMessageDialog(this, "No existe ningún usuario con nickname: " + nickname,
					"Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (productos.isEmpty()) {
			JOptionPane.showMessageDialog(this, nickname + " no tiene productos disponibles en su cartera.",
					"Cartera vacía", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		mostrarProductos(productos);
		labelContador.setText("Cartera de " + nickname + ": " + productos.size() + " productos");
	}

	/**
	 * Resetea todos los filtros y muestra todos los productos.
	 */
	private void resetear() {
		campoBusqueda.setText("");
		campoUsuario.setText("");
		spinnerPrecioMin.setValue(0.0);
		spinnerPrecioMax.setValue(9999.0);
		comboEstado.setSelectedIndex(0);
		if (comboOrden != null)
			comboOrden.setSelectedIndex(0);
		mostrarProductos(controlador.obtenerTodos());
	}

	/**
	 * Muestra los productos en el panel como tarjetas.
	 *
	 * @param productos Lista de productos a mostrar
	 */
	private void mostrarProductos(List<Producto2Mano> productos) {
		panelProductos.removeAll();

		if (productos == null || productos.isEmpty()) {
			JLabel labelVacio = new JLabel("No se encontraron productos de segunda mano.");
			labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			panelProductos.add(labelVacio);
			labelContador.setText("0 productos encontrados");
		} else {
			for (Producto2Mano p : productos)
				panelProductos.add(crearTarjeta(p));
			labelContador.setText(productos.size() + " productos encontrados");
		}

		panelProductos.revalidate();
		panelProductos.repaint();

		SwingUtilities.invokeLater(() -> {
			ajustarTamanoPanelProductos();
			panelProductos.revalidate();
			panelProductos.repaint();
		});
	}

	/**
	 * Crea una tarjeta visual para un producto de segunda mano.
	 *
	 * @param producto El producto a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjeta(Producto2Mano producto) {
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

		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(150),
				VentanaPrincipal.escalar(130));
		tarjeta.add(labelImagen, BorderLayout.NORTH);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		String nombre = producto.getNombre();
		if (nombre.length() > 18)
			nombre = nombre.substring(0, 16) + "...";
		JLabel labelNombre = new JLabel(nombre);
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelNombre.setBorder(
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(3), 0));
		panelInfo.add(labelNombre);

		JLabel labelPropietario = new JLabel("De: " + producto.getPropietario().getNickname());
		labelPropietario.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelPropietario.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelPropietario.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelPropietario);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(3)));

		// Todos los productos que llegan aquí tienen valoración
		JLabel labelPrecio = new JLabel(String.format("%.2f€", producto.getValoracion().getPrecioTasacion()));
		labelPrecio.setFont(VentanaPrincipal.FUENTE_PRECIO);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelPrecio.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(3), 0));
		panelInfo.add(labelPrecio);

		JLabel labelEstado = new JLabel(producto.getValoracion().getEstadoProducto().toString());
		labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelEstado.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelEstado);

		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		// crearBotonNaranja() de PanelBaseInterfaz
		JButton botonVer = crearBotonNaranja("Ver información");
		botonVer.setAlignmentX(Component.CENTER_ALIGNMENT);
		botonVer.setActionCommand("ver:" + producto.getId());
		botonVer.addActionListener(controlador);
		panelInfo.add(botonVer);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
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
			// sin icono
		}
		return label;
	}

	/**
	 * Navega al detalle de un producto de segunda mano. Lo llama el controlador
	 * desde actionPerformed.
	 *
	 * @param producto El producto a ver
	 */
	public void verProducto2Mano(Producto2Mano producto) {
		subpanelProducto2Mano.mostrarProducto(producto, cliente);
		cardLayout.show(panelContenido, "PRODUCTO2MANO");
	}

	/**
	 * Vuelve al catálogo desde el detalle del producto.
	 */
	public void volverDelProducto2Mano() {
		cardLayout.show(panelContenido, "CATALOGO");
	}
}
