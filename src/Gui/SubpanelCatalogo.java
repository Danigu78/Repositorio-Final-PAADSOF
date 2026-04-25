package Gui;

import Gui.Controladores.ControladorCatalogo;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import productos.ProductoVenta;
import usuarios.Cliente;

/**
 * Subpanel del catálogo de productos de CheckPoint. Muestra los productos en
 * tarjetas con imagen, nombre, descripción, precio y botón de ver información.
 * Al pulsar ver información navega a SubpanelProducto. Las tarjetas tienen
 * tamaño fijo escalado según el DPI y se organizan en filas automáticamente con
 * FlowLayout.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCatalogo extends JPanel {

	/** Controlador que gestiona la lógica del catálogo */
	private ControladorCatalogo controlador;

	/** Referencia a la ventana principal */
	private VentanaPrincipal ventana;

	/** Cliente actualmente logueado */
	private Cliente cliente;

	/** Panel donde se muestran las tarjetas de productos */
	private JPanel panelProductos;

	/** Campo de texto para buscar por nombre */
	private JTextField campoBusqueda;

	/** Combo para filtrar por categoría */
	private JComboBox<String> comboCategoria;

	/** Spinner para el precio mínimo */
	private JSpinner spinnerPrecioMin;

	/** Spinner para el precio máximo */
	private JSpinner spinnerPrecioMax;

	/** Etiqueta que muestra cuántos productos hay */
	private JLabel labelContador;

	/** Subpanel que muestra el detalle de un producto */
	private SubpanelProducto subpanelProducto;

	/** CardLayout para alternar entre catálogo y detalle */
	private CardLayout cardLayout;

	/** Panel que contiene catálogo y detalle */
	private JPanel panelContenido;

	/**
	 * Constructor del subpanel catálogo.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelCatalogo(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		// CardLayout para alternar entre catálogo y detalle producto
		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);

		// Panel del catálogo
		JPanel panelCatalogo = crearPanelCatalogo();
		panelContenido.add(panelCatalogo, "CATALOGO");

		// Panel del detalle del producto
		subpanelProducto = new SubpanelProducto(ventana, this);
		panelContenido.add(subpanelProducto, "PRODUCTO");

		add(panelContenido, BorderLayout.CENTER);

		// Mostrar catálogo por defecto
		cardLayout.show(panelContenido, "CATALOGO");
	}

	/**
	 * Crea el panel principal del catálogo con barra de búsqueda y panel de
	 * productos con scroll.
	 *
	 * @return El panel del catálogo
	 */
	private JPanel crearPanelCatalogo() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);

		panel.add(crearPanelBusqueda(), BorderLayout.NORTH);

		// Panel de productos con FlowLayout
		panelProductos = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(25), VentanaPrincipal.escalar(25))) { // separeacion
																												// horizontal
																												// y
																												// separacion
																												// vertical

			@Override
			public Dimension getPreferredSize() {
				int ancho = getParent() != null ? getParent().getWidth() : 800;
				// Calculamos cuántas tarjetas caben por fila
				int anchTarjeta = VentanaPrincipal.escalar(300) + VentanaPrincipal.escalar(10);
				int porFila = Math.max(1, ancho / anchTarjeta);
				// Calculamos cuántas filas necesitamos
				int numTarjetas = getComponentCount();
				int filas = (int) Math.ceil((double) numTarjetas / porFila);
				// Calculamos el alto total con un pequeño margen al final
				int altTarjeta = VentanaPrincipal.escalar(350) + VentanaPrincipal.escalar(10);
				int altoTotal = filas * altTarjeta + VentanaPrincipal.escalar(30);
				return new Dimension(ancho, altoTotal);
			}
		};
		panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelProductos.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10))); // padding

		JScrollPane scroll = new JScrollPane(panelProductos);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));// cuando baja al mover la rueda
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// no hay barra horizontal
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Crea el panel de búsqueda y filtros. Contiene campo de texto, combo de
	 * categorías, rango de precios y botones de buscar y resetear.
	 *
	 * @return El panel de búsqueda configurado
	 */
	private JPanel crearPanelBusqueda() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(3), 0, VentanaPrincipal.COLOR_ACENTO), // linea
																														// separatoria
																														// en
																														// la
																														// parte
																														// de
																														// abajo
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15), // padding
																											// interno
						VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, VentanaPrincipal.escalar(25), 0));
		panelFiltros.setBackground(VentanaPrincipal.COLOR_PANEL);

		// Icono lupa
		panelFiltros.add(crearIcono("/fotos/lupa.jpg", VentanaPrincipal.escalar(30)));

		// Campo de búsqueda por nombre
		campoBusqueda = new JTextField(15);
		campoBusqueda.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campoBusqueda.setForeground(Color.BLACK);// color letra
		campoBusqueda.setBackground(Color.WHITE);
		campoBusqueda.setCaretColor(Color.BLACK);// palito que parpadea
		campoBusqueda.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))));
		// Al pulsar Enter busca directamente
		campoBusqueda.addActionListener(e -> buscar());
		panelFiltros.add(campoBusqueda);

		// Combo de categorías
		JLabel labelCat = new JLabel("Categoría:");
		labelCat.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelCat.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelFiltros.add(labelCat);

		// Se rellena en actualizar() cuando el cliente hace login
		comboCategoria = new JComboBox<>();
		comboCategoria.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboCategoria.setBackground(Color.WHITE);
		comboCategoria.setForeground(Color.BLACK);
		panelFiltros.add(comboCategoria);

		// Precio mínimo y máximo
		JLabel labelPrecio = new JLabel("Precio:");
		labelPrecio.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelFiltros.add(labelPrecio);

		// SpinnerNumberModel(valor inicial, mínimo, máximo, paso)
		spinnerPrecioMin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 9999.0, 1.0));// cada vez que pulsas una flecha
																						// sube o baja 1
		spinnerPrecioMin.setFont(VentanaPrincipal.FUENTE_NORMAL);
		spinnerPrecioMin.setPreferredSize(new Dimension(VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(30)));
		panelFiltros.add(spinnerPrecioMin);

		JLabel labelHasta = new JLabel("—");
		labelHasta.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelFiltros.add(labelHasta);

		spinnerPrecioMax = new JSpinner(new SpinnerNumberModel(9999.0, 0.0, 9999.0, 1.0));
		spinnerPrecioMax.setFont(VentanaPrincipal.FUENTE_NORMAL);
		spinnerPrecioMax.setPreferredSize(new Dimension(VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(30)));
		panelFiltros.add(spinnerPrecioMax);

		JButton botonBuscar = crearBoton("Buscar", true);
		botonBuscar.addActionListener(e -> buscar());
		panelFiltros.add(botonBuscar);

		// Botón ver todos
		JButton botonReset = crearBoton("Ver todos", false);
		botonReset.addActionListener(e -> resetearFiltros());
		panelFiltros.add(botonReset);

		panel.add(panelFiltros, BorderLayout.CENTER);

		// Contador de productos debajo de los filtros
		labelContador = new JLabel("Cargando productos...");
		labelContador.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelContador.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelContador.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, 0, 0));
		panel.add(labelContador, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Realiza la búsqueda con los filtros actuales. Recoge los valores de los
	 * campos y llama al controlador.
	 */
	private void buscar() {
		if (controlador == null)
			return;

		String texto = campoBusqueda.getText().trim();
		String categoria = (String) comboCategoria.getSelectedItem();
		// Obtenemos los precios como double
		double precioMin = ((Number) spinnerPrecioMin.getValue()).doubleValue();
		double precioMax = ((Number) spinnerPrecioMax.getValue()).doubleValue();

		// Pedimos los productos filtrados al controlador
		List<ProductoVenta> productos = controlador.filtrarProductos(texto, categoria, precioMin, precioMax);
		mostrarProductos(productos);
	}

	/**
	 * Resetea todos los filtros y muestra todos los productos.
	 */
	private void resetearFiltros() {
		campoBusqueda.setText("");
		comboCategoria.setSelectedIndex(0);
		spinnerPrecioMin.setValue(0.0);
		spinnerPrecioMax.setValue(9999.0);
		mostrarProductos(controlador.obtenerTodosLosProductos());
	}

	/**
	 * Muestra los productos en el panel como tarjetas. Limpia el panel y añade una
	 * tarjeta por producto. Con FlowLayout las tarjetas bajan de fila
	 * automáticamente.
	 *
	 * @param productos Lista de productos a mostrar
	 */
	private void mostrarProductos(List<ProductoVenta> productos) {
		// Limpiamos el panel antes de añadir las nuevas tarjetas
		panelProductos.removeAll();

		if (productos == null || productos.isEmpty()) {
			JLabel labelVacio = new JLabel("No se encontraron productos");
			labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			panelProductos.add(labelVacio);
			labelContador.setText("0 productos encontrados");
		} else {
			for (ProductoVenta p : productos) {
				panelProductos.add(crearTarjeta(p));
			}
			labelContador.setText(productos.size() + " productos encontrados");
		}

		// Forzamos que el panel se redibuje
		panelProductos.revalidate();
		panelProductos.repaint();
	}

	/**
	 * Crea una tarjeta de producto con tamaño fijo escalado. Contiene imagen,
	 * nombre, descripción, precio y botón ver información. El tamaño fijo escalado
	 * garantiza que se vea bien en todas las pantallas.
	 *
	 * @param producto El producto para crear la tarjeta
	 * @return El panel con la tarjeta
	 */
	private JPanel crearTarjeta(ProductoVenta producto) {
		JPanel tarjeta = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(10)));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		// Tamaño fijo escalado
		tarjeta.setPreferredSize(new Dimension(VentanaPrincipal.escalar(300), // largo
				VentanaPrincipal.escalar(350)));
		tarjeta.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(35), VentanaPrincipal.escalar(8),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(8))));

		// Borde naranja al pasar el ratón por encima
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

		// Imagen del producto
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(160), VentanaPrincipal.escalar(130)));
		cargarImagen(labelImagen, producto.getImagenRuta());
		tarjeta.add(labelImagen, BorderLayout.NORTH);

		// Panel de información
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
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(5), 0));
		panelInfo.add(labelNombre);

		// Descripción — cortada si es muy larga
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
		labelPrecio.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(8), 0));
		panelInfo.add(labelPrecio);

		// Botón ver información — abre SubpanelProducto
		JButton botonVer = crearBoton("Ver información", true);
		botonVer.setAlignmentX(Component.CENTER_ALIGNMENT);
		botonVer.addActionListener(e -> controlador.verProducto(producto));
		panelInfo.add(botonVer);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Navega a la pantalla de detalle del producto.
	 *
	 * @param producto El producto a mostrar en detalle
	 */
	public void verProducto(ProductoVenta producto) {
		subpanelProducto.mostrarProducto(producto, cliente, controlador);
		cardLayout.show(panelContenido, "PRODUCTO");
	}

	/**
	 * Vuelve al catálogo desde la pantalla de detalle. Lo llama SubpanelProducto
	 * cuando el usuario pulsa volver.
	 */
	public void volverDelProducto() {
		cardLayout.show(panelContenido, "CATALOGO");
	}

	/**
	 * Carga una imagen desde src/fotos/. Si no la encuentra muestra "Sin imagen".
	 *
	 * @param label        JLabel donde cargar la imagen
	 * @param nombreImagen Nombre del archivo
	 */
	private void cargarImagen(JLabel label, String nombreImagen) {
		try {
			URL url = getClass().getResource("/fotos/" + nombreImagen);
			if (url != null) {
				BufferedImage img = ImageIO.read(url);
				if (img != null) {
					Image imgEscalada = img.getScaledInstance(VentanaPrincipal.escalar(150),
							VentanaPrincipal.escalar(130), Image.SCALE_SMOOTH);
					label.setIcon(new ImageIcon(imgEscalada));
				} else {
					label.setText("Sin imagen");
					label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
				}
			} else {
				label.setText("Sin imagen");
				label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			}
		} catch (IOException e) {
			label.setText("Sin imagen");
			label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		}
	}

	/**
	 * Carga un icono desde la ruta indicada y lo escala.
	 *
	 * @param ruta   Ruta del icono
	 * @param tamano Tamaño en píxeles escalado
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
			// Dejamos la etiqueta vacía si no carga
		}
		return label;
	}

	/**
	 * Crea un botón naranja (principal) o secundario.
	 *
	 * @param texto     Texto del botón
	 * @param principal true para naranja, false para secundario
	 * @return El botón estilizado
	 */
	private JButton crearBoton(String texto, boolean principal) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		if (principal) {
			Color colorNormal = VentanaPrincipal.COLOR_ACENTO;
			Color colorHover = VentanaPrincipal.COLOR_ACENTO.darker();
			boton.setBackground(colorNormal);
			boton.setForeground(Color.WHITE);
			boton.setContentAreaFilled(false);
			boton.setOpaque(true);
			boton.setBorderPainted(false);
			boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10),
					VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10)));
			boton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					boton.setBackground(colorHover);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					boton.setBackground(colorNormal);
				}
			});

		} else {
			boton.setBackground(VentanaPrincipal.COLOR_PANEL);
			boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
			boton.setBorderPainted(false);
			boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10),
					VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10)));
		}
		return boton;
	}

	/**
	 * Actualiza el catálogo con el cliente logueado. Crea el controlador, rellena
	 * categorías y carga productos.
	 *
	 * @param cliente El cliente logueado
	 */
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorCatalogo(cliente, this);

		// Rellenamos el combo con las categorías de la tienda
		comboCategoria.removeAllItems();
		controlador.obtenerNombresCategorias().forEach(comboCategoria::addItem);

		// Cargamos todos los productos al entrar
		mostrarProductos(controlador.obtenerTodosLosProductos());

		// Volvemos al catálogo por si estábamos en el detalle
		cardLayout.show(panelContenido, "CATALOGO");
	}
}