package Gui.cliente;

import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorCartera;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import productos.Producto2Mano;
import usuarios.Cliente;

/**
 * Subpanel de la cartera personal del cliente. Extiende AbstractPanelCliente
 * para reutilizar helpers visuales del cliente. Sigue el patrón MVC de los
 * apuntes: expone setControlador(ActionListener).
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelCartera extends AbstractPanelCliente {

	/** Controlador de la cartera. */
	private ControladorCartera controlador;

	/** Panel donde se muestran los productos de la cartera. */
	private JPanel panelProductos;

	/** Referencia al panel cliente para navegar. */
	private PanelCliente panelCliente;

	/** Subpanel de pago de tasación. */
	private SubpanelPagoTasacion subpanelPagoTasacion;

	/** CardLayout para alternar entre cartera y pago de tasación. */
	private CardLayout cardLayout;

	/** Panel contenedor del CardLayout. */
	private JPanel panelContenido;

	/** Botón añadir — atributo para registrar el controlador. */
	private JButton botonAñadir;

	/**
	 * Campo imagen del diálogo — atributo para que el controlador pueda
	 * actualizarlo tras abrir el explorador de archivos.
	 */
	private JTextField campoImagenDialogo;

	/**
	 * Constructor del subpanel de la cartera.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelCartera(VentanaPrincipal ventana) {
		super(ventana);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.add(crearPanelCartera(), "CARTERA");
		add(panelContenido, BorderLayout.CENTER);
		cardLayout.show(panelContenido, "CARTERA");
	}

	/**
	 * Enlaza el panel cliente y crea el subpanel de pago de tasación.
	 *
	 * @param panelCliente El panel cliente para navegar
	 */
	public void setPanelCliente(PanelCliente panelCliente) {
		this.panelCliente = panelCliente;
		this.subpanelPagoTasacion = new SubpanelPagoTasacion(ventana, panelCliente);
		panelContenido.add(subpanelPagoTasacion, "PAGO_TASACION");
	}

	/**
	 * Actualiza el subpanel con los datos del cliente. Crea el controlador y lo
	 * registra en el botón — patrón de los apuntes.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
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
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		for (ActionListener al : botonAñadir.getActionListeners())
			botonAñadir.removeActionListener(al);
		botonAñadir.addActionListener(c);
	}

	/**
	 * Crea el panel principal de la cartera con cabecera y lista de productos.
	 *
	 * @return Panel de la cartera
	 */
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

		// crearBotonNaranja() de AbstractPanelSection — sustituye crearBotonPrincipal()
		botonAñadir = crearBotonNaranja("+ Añadir producto");
		botonAñadir.setActionCommand("añadir");
		cabecera.add(botonAñadir, BorderLayout.EAST);

		panel.add(cabecera, BorderLayout.NORTH);

		panelProductos = new JPanel();
		// crearScrollContenido() de AbstractPanelCliente — configura BoxLayout y scroll
		JScrollPane scroll = crearScrollContenido(panelProductos);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Rellena el panel de productos con las tarjetas de la cartera del cliente.
	 */
	private void rellenarProductos() {
		panelProductos.removeAll();

		List<Producto2Mano> productos = controlador.getProductos();
		if (productos.isEmpty()) {
			// crearLabelVacio() de AbstractPanelCliente
			panelProductos.add(crearLabelVacio("No tienes productos en tu cartera."));
		} else {
			for (Producto2Mano p : productos) {
				// añadirTarjetaConSeparacion() de AbstractPanelCliente
				añadirTarjetaConSeparacion(panelProductos, crearTarjeta(p), 0);
			}
		}

		panelProductos.revalidate();
		panelProductos.repaint();
	}

	/**
	 * Crea una tarjeta visual para un producto de la cartera. Usa
	 * crearTarjetaBase(), crearGbcTarjeta() y crearPanelBotonesTarjeta() de
	 * AbstractPanelCliente.
	 *
	 * @param producto El producto a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjeta(Producto2Mano producto) {
		// crearTarjetaBase() de AbstractPanelCliente — MatteBorder inferior
		JPanel tarjeta = crearTarjetaBase(110, true);

		// Imagen — cargarImagen() de AbstractPanelSection
		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(80)));
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(70));
		tarjeta.add(labelImagen, BorderLayout.WEST);

		// crearPanelInfoTarjeta() y crearGbcTarjeta() de AbstractPanelCliente
		JPanel panelInfo = crearPanelInfoTarjeta();
		GridBagConstraints gbc = crearGbcTarjeta();

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

		// Estado del producto en fila 2
		JLabel labelEstado = crearLabelEstadoProducto(producto);
		gbc.gridy = 2;
		panelInfo.add(labelEstado, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		// crearPanelBotonesTarjeta() y crearGbcBotonesTarjeta() de AbstractPanelCliente
		JPanel panelBotones = crearPanelBotonesTarjeta();
		GridBagConstraints gbcB = crearGbcBotonesTarjeta();

		if (!controlador.estaValorado(producto) && !controlador.tieneTasacionPendiente(producto)) {
			// crearBotonNaranja() de AbstractPanelSection
			JButton botonTasar = crearBotonNaranja("Solicitar tasación");
			botonTasar.setActionCommand("tasar:" + producto.getId());
			botonTasar.addActionListener(controlador);
			gbcB.gridy = 0;
			panelBotones.add(botonTasar, gbcB);
		}

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Crea el label de estado del producto según si está valorado, bloqueado,
	 * pendiente de tasación o sin tasar.
	 *
	 * @param producto El producto a evaluar
	 * @return JLabel con el estado coloreado
	 */
	private JLabel crearLabelEstadoProducto(Producto2Mano producto) {
		JLabel label;
		if (controlador.estaValorado(producto) && producto.isBloqueado()) {
			label = new JLabel("Bloqueado — Has enviado una oferta "
					+ "ofreciendo este producto. Se desbloqueará cuando " + "se responda a la oferta.");
			label.setForeground(new Color(180, 50, 50));
		} else if (controlador.estaValorado(producto)) {
			label = new JLabel("Estado: " + producto.getValoracion().getEstadoProducto() + "  —  Precio tasado: "
					+ String.format("%.2f€", producto.getValoracion().getPrecioTasacion()));
			label.setForeground(new Color(50, 150, 50));
		} else if (controlador.tieneTasacionPendiente(producto)) {
			label = new JLabel("Tasación solicitada — esperando empleado");
			label.setForeground(new Color(0, 120, 200));
		} else {
			label = new JLabel("Sin tasar — solicita la tasación");
			label.setForeground(new Color(200, 150, 0));
		}
		label.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		return label;
	}

	/**
	 * Muestra el diálogo para añadir un producto a la cartera. Lo llama el
	 * controlador cuando el usuario pulsa el botón añadir. Al confirmar pasa los
	 * datos directamente al controlador.
	 */
	public void mostrarDialogoAñadir() {
		JTextField campoNombre = new JTextField(20);
		JTextField campoDescripcion = new JTextField(20);

		// El campo imagen no es editable — se rellena con el explorador
		campoImagenDialogo = new JTextField(20);
		campoImagenDialogo.setEditable(false);

		JButton botonExplorador = new JButton("Seleccionar imagen...");
		botonExplorador.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		botonExplorador.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonExplorador.setActionCommand("explorador");
		botonExplorador.addActionListener(controlador);

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
			controlador.añadirProducto(campoNombre.getText().trim(), campoDescripcion.getText().trim(),
					campoImagenDialogo.getText().trim());
		}
	}

	/**
	 * Actualiza el campo de imagen del diálogo. Lo llama el controlador tras abrir
	 * el explorador de archivos.
	 *
	 * @param nombreArchivo Nombre del archivo seleccionado
	 */
	public void setImagenSeleccionada(String nombreArchivo) {
		if (campoImagenDialogo != null)
			campoImagenDialogo.setText(nombreArchivo);
	}

	/**
	 * Navega al subpanel de pago de tasación. Lo llama el controlador.
	 *
	 * @param producto El producto a tasar
	 */
	public void navegarAPagoTasacion(Producto2Mano producto) {
		if (subpanelPagoTasacion != null) {
			subpanelPagoTasacion.mostrarPago(producto, cliente);
			cardLayout.show(panelContenido, "PAGO_TASACION");
		}
	}

	/**
	 * Muestra un mensaje de éxito. Lo llama el controlador.
	 *
	 * @param mensaje El mensaje de éxito
	 */
	public void mostrarExito(String mensaje) {
		mostrarMensaje(mensaje);
	}
}