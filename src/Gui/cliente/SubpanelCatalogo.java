package Gui.cliente;

import Gui.VentanaPrincipal;

import Gui.controladores.cliente.ControladorCatalogo;
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
 * Subpanel del catálogo de productos de CheckPoint. Muestra recomendados arriba
 * y el catálogo completo debajo. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCatalogo extends JPanel {

	private ControladorCatalogo controlador;
	private VentanaPrincipal ventana;
	private Cliente cliente;
	private JPanel panelProductos;
	private JPanel panelRecomendados;
	private JTextField campoBusqueda;
	private JComboBox<String> comboCategoria;
	private JSpinner spinnerPrecioMin;
	private JSpinner spinnerPrecioMax;
	private JLabel labelContador;
	private SubpanelProducto subpanelProducto;
	private CardLayout cardLayout;
	private JPanel panelContenido;
	private JButton botonOrdenNombre;
	private JButton botonOrdenPrecioAsc;
	private JButton botonOrdenPrecioDesc;
	private JButton botonBuscar;
	private JButton botonReset;

	public SubpanelCatalogo(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

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
	 */
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorCatalogo(cliente, this);
		setControlador(controlador);

		comboCategoria.removeAllItems();
		controlador.obtenerNombresCategorias().forEach(comboCategoria::addItem);

		mostrarProductos(controlador.obtenerTodosLosProductos());
		mostrarRecomendados();
		cardLayout.show(panelContenido, "CATALOGO");
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes.
	 */
	public void setControlador(ActionListener c) {
		if (botonBuscar != null) {
			for (ActionListener al : botonBuscar.getActionListeners())
				botonBuscar.removeActionListener(al);
			botonBuscar.addActionListener(c);
		}
		if (botonReset != null) {
			for (ActionListener al : botonReset.getActionListeners())
				botonReset.removeActionListener(al);
			botonReset.addActionListener(c);
		}
		if (botonOrdenNombre != null) {
		    for (ActionListener al : botonOrdenNombre.getActionListeners())
		        botonOrdenNombre.removeActionListener(al);
		    botonOrdenNombre.addActionListener(c);
		}
		if (botonOrdenPrecioAsc != null) {
		    for (ActionListener al : botonOrdenPrecioAsc.getActionListeners())
		        botonOrdenPrecioAsc.removeActionListener(al);
		    botonOrdenPrecioAsc.addActionListener(c);
		}
		if (botonOrdenPrecioDesc != null) {
		    for (ActionListener al : botonOrdenPrecioDesc.getActionListeners())
		        botonOrdenPrecioDesc.removeActionListener(al);
		    botonOrdenPrecioDesc.addActionListener(c);
		}
	}

	private JPanel crearPanelCatalogo() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);

		panel.add(crearPanelBusqueda(), BorderLayout.NORTH);

		// Panel central con recomendados + productos
		JPanel panelCentral = new JPanel(new BorderLayout());
		panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);

		// Panel de recomendados — se rellena en actualizar()
		panelRecomendados = new JPanel(new BorderLayout());
		panelRecomendados.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelCentral.add(panelRecomendados, BorderLayout.NORTH);

		// Panel de productos con FlowLayout
		panelProductos = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(25), VentanaPrincipal.escalar(25))) {
			@Override
			public Dimension getPreferredSize() {
				int ancho = getParent() != null ? getParent().getWidth() : 800;
				int anchTarjeta = VentanaPrincipal.escalar(300) + VentanaPrincipal.escalar(10);
				int porFila = Math.max(1, ancho / anchTarjeta);
				int numTarjetas = getComponentCount();
				int filas = (int) Math.ceil((double) numTarjetas / porFila);
				int altTarjeta = VentanaPrincipal.escalar(350) + VentanaPrincipal.escalar(10);
				int altoTotal = filas * altTarjeta + VentanaPrincipal.escalar(30);
				return new Dimension(ancho, altoTotal);
			}
		};
		panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelProductos.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));

		JScrollPane scroll = new JScrollPane(panelProductos);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panelCentral.add(scroll, BorderLayout.CENTER);

		panel.add(panelCentral, BorderLayout.CENTER);
		return panel;
	}

	private JPanel crearPanelBusqueda() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(3), 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, VentanaPrincipal.escalar(25), 0));
		panelFiltros.setBackground(VentanaPrincipal.COLOR_PANEL);

		panelFiltros.add(crearIcono("/fotos/lupa.jpg", VentanaPrincipal.escalar(30)));

		campoBusqueda = new JTextField(15);
		campoBusqueda.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campoBusqueda.setForeground(Color.BLACK);
		campoBusqueda.setBackground(Color.WHITE);
		campoBusqueda.setCaretColor(Color.BLACK);
		campoBusqueda.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))));
		campoBusqueda.addActionListener(e -> buscar());
		panelFiltros.add(campoBusqueda);

		JLabel labelCat = new JLabel("Categoría:");
		labelCat.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelCat.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelFiltros.add(labelCat);

		comboCategoria = new JComboBox<>();
		comboCategoria.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboCategoria.setBackground(Color.WHITE);
		comboCategoria.setForeground(Color.BLACK);
		panelFiltros.add(comboCategoria);

		JLabel labelPrecio = new JLabel("Precio:");
		labelPrecio.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelFiltros.add(labelPrecio);

		spinnerPrecioMin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 9999.0, 1.0));
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

		botonBuscar = crearBoton("Buscar", true);
		botonBuscar.setActionCommand("buscar");
		panelFiltros.add(botonBuscar);

		botonReset = crearBoton("Ver todos", false);
		botonReset.setActionCommand("reset");
		panelFiltros.add(botonReset);

		botonOrdenNombre = crearBoton("Ordenar alfabeticamente", false);
		botonOrdenNombre.setActionCommand("ordenarNombre");
		panelFiltros.add(botonOrdenNombre);

		botonOrdenPrecioAsc = crearBoton("Precio ascendente", false);
		botonOrdenPrecioAsc.setActionCommand("ordenarPrecioAsc");
		panelFiltros.add(botonOrdenPrecioAsc);

		botonOrdenPrecioDesc = crearBoton("Precio descendente", false);
		botonOrdenPrecioDesc.setActionCommand("ordenarPrecioDesc");
		panelFiltros.add(botonOrdenPrecioDesc);

		panel.add(panelFiltros, BorderLayout.CENTER);

		labelContador = new JLabel("Cargando productos...");
		labelContador.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelContador.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelContador.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, 0, 0));
		panel.add(labelContador, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Realiza la búsqueda con los filtros actuales. Lo llama el controlador y
	 * también el Enter del campo.
	 */
	public void buscar() {
		if (controlador == null)
			return;
		String texto = campoBusqueda.getText().trim();
		String categoria = (String) comboCategoria.getSelectedItem();
		double precioMin = ((Number) spinnerPrecioMin.getValue()).doubleValue();
		double precioMax = ((Number) spinnerPrecioMax.getValue()).doubleValue();
		mostrarProductos(controlador.filtrarProductos(texto, categoria, precioMin, precioMax));
	}

	/**
	 * Resetea todos los filtros y muestra todos los productos. Lo llama el
	 * controlador.
	 */
	public void resetearFiltros() {
		campoBusqueda.setText("");
		comboCategoria.setSelectedIndex(0);
		spinnerPrecioMin.setValue(0.0);
		spinnerPrecioMax.setValue(9999.0);
		mostrarProductos(controlador.obtenerTodosLosProductos());
		mostrarRecomendados();
	}

	/**
	 * Rellena el panel de recomendados. Para clientes usa el recomendador
	 * personalizado. Para invitados muestra los mejor valorados.
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

		// Título
		JLabel titulo = new JLabel(" Recomendados para ti");
		titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(10), 0));
		contenedor.add(titulo, BorderLayout.NORTH);

		// Tarjetas pequeñas en horizontal
		JPanel panelTarjetas = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(10)));
		panelTarjetas.setBackground(VentanaPrincipal.COLOR_FONDO);
		for (ProductoVenta p : recomendados) {
			panelTarjetas.add(crearTarjetaPequeña(p));
		}
		contenedor.add(panelTarjetas, BorderLayout.CENTER);

		// Separador
		JSeparator sep = new JSeparator();
		sep.setForeground(VentanaPrincipal.COLOR_BORDE);
		contenedor.add(sep, BorderLayout.SOUTH);

		panelRecomendados.add(contenedor, BorderLayout.CENTER);
		panelRecomendados.revalidate();
		panelRecomendados.repaint();
	}

	/**
	 * Muestra los productos en el panel como tarjetas.
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
			for (ProductoVenta p : productos) {
				panelProductos.add(crearTarjeta(p));
			}
			labelContador.setText(productos.size() + " productos encontrados");
		}

		panelProductos.revalidate();
		panelProductos.repaint();
	}

	/**
	 * Crea una tarjeta grande para el catálogo principal. Incluye nota media si
	 * tiene reseñas.
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

		// Imagen
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(160), VentanaPrincipal.escalar(130)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(150),
				VentanaPrincipal.escalar(130));
		tarjeta.add(labelImagen, BorderLayout.NORTH);

		// Info
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		// Nombre
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

		// Descripción
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

		// Precio
		JLabel labelPrecio = new JLabel(String.format("%.2f€", producto.getPrecioOficial()));
		labelPrecio.setFont(VentanaPrincipal.FUENTE_PRECIO);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelPrecio.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(4), 0));
		panelInfo.add(labelPrecio);

		// Valoración
		JLabel labelNota = producto.getReseñas().isEmpty() ? new JLabel("Sin reseñas")
				: new JLabel(String.format("%.1f/10", producto.getMediaPuntuacion()));
		labelNota.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelNota.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelNota.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelNota.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(6), 0));
		panelInfo.add(labelNota);

		// Botón ver información
		JButton botonVer = crearBoton("Ver información", true);
		botonVer.setAlignmentX(Component.CENTER_ALIGNMENT);
		botonVer.setActionCommand("ver:" + producto.getId());
		botonVer.addActionListener(controlador);
		panelInfo.add(botonVer);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Crea una tarjeta pequeña para la sección de recomendados.
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

		// Imagen pequeña
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(90), VentanaPrincipal.escalar(80)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(70));
		tarjeta.add(labelImagen, BorderLayout.NORTH);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		// Nombre
		String nombre = producto.getNombre();
		if (nombre.length() > 14)
			nombre = nombre.substring(0, 12) + "...";
		JLabel labelNombre = new JLabel(nombre);
		labelNombre.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelNombre);

		// Precio
		JLabel labelPrecio = new JLabel(String.format("%.2f€", producto.getPrecioOficial()));
		labelPrecio.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelPrecio);

		// Valoración
		JLabel labelNota = producto.getReseñas().isEmpty() ? new JLabel("Sin reseñas")
				: new JLabel(String.format("⭐ %.1f/10", producto.getMediaPuntuacion()));
		labelNota.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelNota.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelNota.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelNota);

		// Botón ver
		JButton botonVer = crearBoton("Ver", true);
		botonVer.setAlignmentX(Component.CENTER_ALIGNMENT);
		botonVer.setActionCommand("ver:" + producto.getId());
		botonVer.addActionListener(controlador);
		panelInfo.add(botonVer);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Navega a la pantalla de detalle del producto.
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

	private void cargarImagen(JLabel label, String nombreImagen, int ancho, int alto) {
		try {
			URL url = getClass().getResource("/fotos/" + nombreImagen);
			if (url != null) {
				BufferedImage img = ImageIO.read(url);
				if (img != null) {
					Image imgEscalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
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
			// vacío
		}
		return label;
	}

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
	public void ordenarYMostrar(List<ProductoVenta> productos) {
	    mostrarProductos(productos);
	}
}