package Gui.cliente;

import Gui.VentanaPrincipal;

import Gui.Controladores.*;
import Gui.Controladores.cliente.ControladorSegundaMano;

import javax.swing.*;
import javax.swing.border.*;
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
 * Subpanel del catálogo de segunda mano. Reutiliza el mismo estilo de tarjetas
 * y filtros que SubpanelCatalogo.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelSegundaMano extends JPanel {

	private VentanaPrincipal ventana;
	private Cliente cliente;
	private ControladorSegundaMano controlador;
	private JPanel panelProductos;
	private JTextField campoBusqueda;
	private JTextField campoUsuario;
	private JSpinner spinnerPrecioMin;
	private JSpinner spinnerPrecioMax;
	private JComboBox<String> comboEstado;
	private JLabel labelContador;
	private CardLayout cardLayout;
	private JPanel panelContenido;
	private SubpanelProducto2Mano subpanelProducto2Mano;

	public SubpanelSegundaMano(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		subpanelProducto2Mano = new SubpanelProducto2Mano(ventana, this);
		panelContenido.add(subpanelProducto2Mano, "PRODUCTO2MANO");
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.add(crearPanelCatalogo(), "CATALOGO");
		cardLayout.show(panelContenido, "CATALOGO");
		add(panelContenido, BorderLayout.CENTER);
	}

	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorSegundaMano(this, cliente);
		comboEstado.removeAllItems();
		for (String s : controlador.getEstados()) {
			comboEstado.addItem(s);
		}
		mostrarProductos(controlador.obtenerTodos());
		cardLayout.show(panelContenido, "CATALOGO");
	}

	private JPanel crearPanelCatalogo() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(crearPanelFiltros(), BorderLayout.NORTH);

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
				return new Dimension(ancho, filas * altTarjeta + VentanaPrincipal.escalar(30));
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
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	private JPanel crearPanelFiltros() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(3), 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15))));

		// Fila superior — filtros de producto
		JPanel filaFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, VentanaPrincipal.escalar(25), 0));
		filaFiltros.setBackground(VentanaPrincipal.COLOR_PANEL);

		// Icono lupa
		filaFiltros.add(crearIcono("/fotos/lupa.jpg", VentanaPrincipal.escalar(30)));

		// Nombre
		JLabel labelNombre = new JLabel("Producto:");
		labelNombre.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		filaFiltros.add(labelNombre);

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

		// Precio
		JLabel labelPrecio = new JLabel("Precio:");
		labelPrecio.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelPrecio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		filaFiltros.add(labelPrecio);

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

		// Estado mínimo
		JLabel labelEstado = new JLabel("Estado mínimo:");
		labelEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEstado.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		filaFiltros.add(labelEstado);

		comboEstado = new JComboBox<>();
		comboEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboEstado.setBackground(Color.WHITE);
		comboEstado.setForeground(Color.BLACK);
		filaFiltros.add(comboEstado);

		JButton botonBuscar = crearBoton("Buscar", true);
		botonBuscar.addActionListener(e -> buscar());
		filaFiltros.add(botonBuscar);

		JButton botonReset = crearBoton("Ver todos", false);
		botonReset.addActionListener(e -> resetear());
		filaFiltros.add(botonReset);

		panel.add(filaFiltros, BorderLayout.NORTH);

		// Fila inferior — búsqueda por usuario
		JPanel filaUsuario = new JPanel(new FlowLayout(FlowLayout.CENTER, VentanaPrincipal.escalar(15), 0));
		filaUsuario.setBackground(VentanaPrincipal.COLOR_PANEL);
		filaUsuario.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));

		JLabel labelUsuario = new JLabel("Ver cartera de:");
		labelUsuario.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelUsuario.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		filaUsuario.add(labelUsuario);

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

		JButton botonCartera = crearBoton("Ver cartera", true);
		botonCartera.addActionListener(e -> buscarCartera());
		filaUsuario.add(botonCartera);

		// Contador y fila usuario juntos abajo
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

	private void buscar() {
		if (controlador == null)
			return;
		String nombre = campoBusqueda.getText().trim();
		double precioMin = ((Number) spinnerPrecioMin.getValue()).doubleValue();
		double precioMax = ((Number) spinnerPrecioMax.getValue()).doubleValue();
		EstadoProducto estado = controlador.textoAEstado((String) comboEstado.getSelectedItem());
		mostrarProductos(controlador.filtrar(nombre, precioMin, precioMax, estado));
	}

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

	private void resetear() {
		campoBusqueda.setText("");
		campoUsuario.setText("");
		spinnerPrecioMin.setValue(0.0);
		spinnerPrecioMax.setValue(9999.0);
		comboEstado.setSelectedIndex(0);
		mostrarProductos(controlador.obtenerTodos());
	}

	private void mostrarProductos(List<Producto2Mano> productos) {
		panelProductos.removeAll();

		if (productos == null || productos.isEmpty()) {
			JLabel labelVacio = new JLabel("No se encontraron productos de segunda mano.");
			labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			panelProductos.add(labelVacio);
			labelContador.setText("0 productos encontrados");
		} else {
			for (Producto2Mano p : productos) {
				panelProductos.add(crearTarjeta(p));
			}
			labelContador.setText(productos.size() + " productos encontrados");
		}

		panelProductos.revalidate();
		panelProductos.repaint();
	}

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

		// Imagen
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(160), VentanaPrincipal.escalar(130)));
		cargarImagen(labelImagen, producto.getImagenRuta());
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
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(3), 0));
		panelInfo.add(labelNombre);

		// Propietario
		JLabel labelPropietario = new JLabel("De: " + producto.getPropietario().getNickname());
		labelPropietario.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelPropietario.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		labelPropietario.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInfo.add(labelPropietario);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(3)));

		// Precio y estado
		if (producto.getValoracion() != null) {
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
		}

		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		// Botón ver información
		JButton botonVer = crearBoton("Ver información", true);
		botonVer.setAlignmentX(Component.CENTER_ALIGNMENT);
		botonVer.setActionCommand("ver:" + producto.getId());
		botonVer.addActionListener(controlador);
		panelInfo.add(botonVer);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

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

	public void verProducto2Mano(Producto2Mano producto) {
		subpanelProducto2Mano.mostrarProducto(producto, cliente);
		cardLayout.show(panelContenido, "PRODUCTO2MANO");
	}

	public void volverDelProducto2Mano() {
		cardLayout.show(panelContenido, "CATALOGO");
	}
}