package Gui.cliente;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCartera;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import productos.Producto2Mano;
import usuarios.Cliente;

/**
 * Subpanel de la cartera personal del cliente. Solo gestiona la interfaz — la
 * lógica la delega en ControladorCartera. Sigue el patrón MVC de los apuntes:
 * expone setControlador(ActionListener).
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCartera extends JPanel {

	private VentanaPrincipal ventana;
	private Cliente cliente;
	private ControladorCartera controlador;
	private JPanel panelProductos;
	private PanelCliente panelCliente;
	private SubpanelPagoTasacion subpanelPagoTasacion;
	private CardLayout cardLayout;
	private JPanel panelContenido;

	/** Botón añadir — atributo para registrar el controlador */
	private JButton botonAñadir;

	/**
	 * Campo imagen del diálogo — atributo para que el controlador pueda
	 * actualizarlo
	 */
	private JTextField campoImagenDialogo;

	public SubpanelCartera(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.add(crearPanelCartera(), "CARTERA");
		add(panelContenido, BorderLayout.CENTER);
		cardLayout.show(panelContenido, "CARTERA");
	}

	public void setPanelCliente(PanelCliente panelCliente) {
		this.panelCliente = panelCliente;
		this.subpanelPagoTasacion = new SubpanelPagoTasacion(ventana, panelCliente);
		panelContenido.add(subpanelPagoTasacion, "PAGO_TASACION");
	}

	/**
	 * Actualiza el subpanel con los datos del cliente. Crea el controlador y lo
	 * registra en el botón — patrón de los apuntes.
	 */
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorCartera(this, cliente);
		setControlador(controlador);
		rellenarProductos();
		cardLayout.show(panelContenido, "CARTERA");
	}

	/**
	 * Registra el controlador en el botón añadir — patrón de los apuntes. Elimina
	 * listeners anteriores para evitar duplicados.
	 */
	public void setControlador(ActionListener c) {
		for (ActionListener al : botonAñadir.getActionListeners()) {
			botonAñadir.removeActionListener(al);
		}
		botonAñadir.addActionListener(c);
	}

	private JPanel crearPanelCartera() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		JPanel cabecera = new JPanel(new BorderLayout());
		cabecera.setBackground(VentanaPrincipal.COLOR_FONDO);
		cabecera.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(15), 0));

		JLabel titulo = new JLabel("Mi Cartera");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		cabecera.add(titulo, BorderLayout.WEST);

		// Botón añadir — el controlador se registra en setControlador()
		botonAñadir = crearBotonPrincipal("+ Añadir producto");
		botonAñadir.setActionCommand("añadir");
		cabecera.add(botonAñadir, BorderLayout.EAST);

		panel.add(cabecera, BorderLayout.NORTH);

		panelProductos = new JPanel();
		panelProductos.setLayout(new BoxLayout(panelProductos, BoxLayout.Y_AXIS));
		panelProductos.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(panelProductos);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	private void rellenarProductos() {
		panelProductos.removeAll();

		List<Producto2Mano> productos = controlador.getProductos();
		if (productos.isEmpty()) {
			JLabel labelVacio = new JLabel("No tienes productos en tu cartera.");
			labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelProductos.add(labelVacio);
		} else {
			for (Producto2Mano p : productos) {
				JPanel tarjeta = crearTarjeta(p);
				tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
				panelProductos.add(tarjeta);
			}
		}

		panelProductos.revalidate();
		panelProductos.repaint();
	}

	private JPanel crearTarjeta(Producto2Mano producto) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(110)));
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		// Imagen
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(80)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(70));
		tarjeta.add(labelImagen, BorderLayout.WEST);

		// Info central
		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		panelInfo.add(labelNombre, gbc);

		String desc = producto.getDescripcion();
		if (desc != null && desc.length() > 50)
			desc = desc.substring(0, 48) + "...";
		JLabel labelDesc = new JLabel(desc != null ? desc : "");
		labelDesc.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelDesc, gbc);
		if (controlador.estaValorado(producto) && producto.isBloqueado()) {
		    JLabel labelBloqueado = new JLabel("Bloqueado — Has enviado una oferta ofreciendo este producto. Se desbloqueará cuando se responda a la oferta.");
		    labelBloqueado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		    labelBloqueado.setForeground(new Color(180, 50, 50));
		    gbc.gridy = 2;
		    panelInfo.add(labelBloqueado, gbc);}
		else if (controlador.estaValorado(producto)) {
			JLabel labelEstado = new JLabel("Estado: " + producto.getValoracion().getEstadoProducto()
					+ "  —  Precio tasado: " + String.format("%.2f€", producto.getValoracion().getPrecioTasacion()));
			labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelEstado.setForeground(new Color(50, 150, 50));
			gbc.gridy = 2;
			panelInfo.add(labelEstado, gbc);
		} else if (controlador.tieneTasacionPendiente(producto)) {
			JLabel labelPendiente = new JLabel("Tasación solicitada — esperando empleado");
			labelPendiente.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelPendiente.setForeground(new Color(0, 120, 200));
			gbc.gridy = 2;
			panelInfo.add(labelPendiente, gbc);
		} else {
			JLabel labelSinTasar = new JLabel("Sin tasar — solicita la tasación");
			labelSinTasar.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelSinTasar.setForeground(new Color(200, 150, 0));
			gbc.gridy = 2;
			panelInfo.add(labelSinTasar, gbc);
		}

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		if (!controlador.estaValorado(producto) && !controlador.tieneTasacionPendiente(producto)) {
			JButton botonTasar = crearBotonPrincipal("Solicitar tasación");
			botonTasar.setActionCommand("tasar:" + producto.getId());
			botonTasar.addActionListener(controlador);
			gbcB.gridy = 0;
			panelBotones.add(botonTasar, gbcB);
		}

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Muestra el diálogo para añadir un producto. Lo llama el controlador cuando el
	 * usuario pulsa el botón añadir. Al confirmar pasa los datos directamente al
	 * controlador.
	 */
	public void mostrarDialogoAñadir() {
		// Creamos los campos locales del diálogo
		JTextField campoNombre = new JTextField(20);
		JTextField campoDescripcion = new JTextField(20);

		// El campo imagen no es editable — se rellena con el explorador
		campoImagenDialogo = new JTextField(20);
		campoImagenDialogo.setEditable(false);

		// Botón explorador — registramos el controlador
		JButton botonExplorador = new JButton("Seleccionar imagen...");
		botonExplorador.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		botonExplorador.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonExplorador.setActionCommand("explorador");
		botonExplorador.addActionListener(controlador);

		// Formulario
		JPanel panelForm = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.insets = new Insets(VentanaPrincipal.escalar(5), 0, VentanaPrincipal.escalar(5), 0);

		gbc.gridy = 0;
		panelForm.add(new JLabel("Nombre:"), gbc);
		gbc.gridy = 1;
		panelForm.add(campoNombre, gbc);
		gbc.gridy = 2;
		panelForm.add(new JLabel("Descripción:"), gbc);
		gbc.gridy = 3;
		panelForm.add(campoDescripcion, gbc);
		gbc.gridy = 4;
		panelForm.add(new JLabel("Imagen:"), gbc);

		JPanel panelImagen = new JPanel(new BorderLayout(VentanaPrincipal.escalar(8), 0));
		panelImagen.add(campoImagenDialogo, BorderLayout.CENTER);
		panelImagen.add(botonExplorador, BorderLayout.EAST);
		gbc.gridy = 5;
		panelForm.add(panelImagen, gbc);

		int opcion = JOptionPane.showConfirmDialog(this, panelForm, "Añadir producto a mi cartera",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (opcion == JOptionPane.OK_OPTION) {
			// Pasamos los datos directamente al controlador
			controlador.añadirProducto(campoNombre.getText().trim(), campoDescripcion.getText().trim(),
					campoImagenDialogo.getText().trim());
		}
	}

	/**
	 * Actualiza el campo de imagen del diálogo. Lo llama el controlador tras abrir
	 * el explorador.
	 */
	public void setImagenSeleccionada(String nombreArchivo) {
		if (campoImagenDialogo != null) {
			campoImagenDialogo.setText(nombreArchivo);
		}
	}

	/**
	 * Navega al subpanel de pago de tasación. Lo llama el controlador.
	 */
	public void navegarAPagoTasacion(Producto2Mano producto) {
		if (subpanelPagoTasacion != null) {
			subpanelPagoTasacion.mostrarPago(producto, cliente);
			cardLayout.show(panelContenido, "PAGO_TASACION");
		}
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void mostrarExito(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
	}

	private void cargarImagen(JLabel label, String nombre, int ancho, int alto) {
		try {
			URL url = getClass().getResource("/fotos/" + nombre);
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

	private JButton crearBotonPrincipal(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
		boton.setForeground(Color.WHITE);
		boton.setOpaque(true);
		boton.setBorderPainted(false);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
				VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15)));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				boton.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
			}
		});
		return boton;
	}
}