package Gui.cliente;

import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorPagoTasacion;

import java.awt.*;
import java.awt.event.*;
import productos.Producto2Mano;
import usuarios.Cliente;

/**
 * Subpanel de pago de tasación. Extiende AbstractPanelCliente para reutilizar
 * helpers visuales del cliente. Sigue el patrón MVC de los apuntes: expone
 * setControlador(ActionListener).
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPagoTasacion extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Referencia al panel cliente para navegar. */
	private PanelCliente panelCliente;

	/** Controlador del pago de tasación. */
	private ControladorPagoTasacion controlador;

	/** Campo número de tarjeta. */
	private JTextField campoTarjeta;

	/** Campo CVV. */
	private JPasswordField campoCVV;

	/** Campo fecha de caducidad. */
	private JTextField campoFecha;

	/** Botón pagar — atributo para registrar el controlador. */
	private JButton botonPagar;

	/** Botón volver — atributo para registrar el controlador. */
	private JButton botonVolver;

	/**
	 * Constructor del subpanel de pago de tasación.
	 *
	 * @param ventana      La ventana principal
	 * @param panelCliente El panel cliente para navegar
	 */
	public SubpanelPagoTasacion(VentanaPrincipal ventana, PanelCliente panelCliente) {
		super(ventana);
		this.panelCliente = panelCliente;
	}

	/**
	 * No se usa en este subpanel — la construcción se hace en mostrarPago(). Se
	 * implementa por obligación de AbstractPanelCliente.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * Carga el producto y construye la interfaz de pago de tasación. Crea el
	 * controlador y lo registra en los botones.
	 *
	 * @param producto El producto a tasar
	 * @param cliente  El cliente logueado
	 */
	public void mostrarPago(Producto2Mano producto, Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorPagoTasacion(this, cliente, producto);
		removeAll();
		add(crearBarraSuperior(), BorderLayout.NORTH);
		add(crearPanelPago(), BorderLayout.CENTER);
		setControlador(controlador);
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes. Elimina
	 * listeners anteriores para evitar duplicados.
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonPagar != null) {
			for (ActionListener al : botonPagar.getActionListeners())
				botonPagar.removeActionListener(al);
			botonPagar.addActionListener(c);
		}
		if (botonVolver != null) {
			for (ActionListener al : botonVolver.getActionListeners())
				botonVolver.removeActionListener(al);
			botonVolver.addActionListener(c);
		}
	}

	/**
	 * Devuelve el número de tarjeta introducido. Lo lee el controlador desde
	 * realizarPago().
	 *
	 * @return Número de tarjeta
	 */
	public String getNumeroTarjeta() {
		return campoTarjeta.getText().trim();
	}

	/**
	 * Devuelve el CVV introducido. Lo lee el controlador desde realizarPago().
	 *
	 * @return CVV
	 */
	public String getCVV() {
		return new String(campoCVV.getPassword()).trim();
	}

	/**
	 * Devuelve la fecha de caducidad introducida. Lo lee el controlador desde
	 * realizarPago().
	 *
	 * @return Fecha en formato MM/AA
	 */
	public String getFechaCaducidad() {
		return campoFecha.getText().trim();
	}

	/**
	 * Crea la barra superior con botón volver a la cartera. Usa crearBarraVolver()
	 * y getBotonVolver() de PanelBaseInterfaz.
	 *
	 * @return Panel de la barra superior
	 */
	private JPanel crearBarraSuperior() {
		// crearBarraVolver() de PanelBaseInterfaz
		JPanel barra = crearBarraVolver("← Volver a mi cartera");
		botonVolver = getBotonVolver(barra);
		botonVolver.setActionCommand("volver");
		return barra;
	}

	/**
	 * Crea el panel central con información del producto y formulario de pago.
	 *
	 * @return Panel de pago centrado
	 */
	private JPanel crearPanelPago() {
		JPanel panelCentral = new JPanel(new BorderLayout());
		panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelCentral.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(40),
				VentanaPrincipal.escalar(200), VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(200)));

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40),
						VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(40))));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.insets = new Insets(VentanaPrincipal.escalar(6), 0, VentanaPrincipal.escalar(6), 0);

		JLabel titulo = new JLabel("Solicitar tasación");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
		panel.add(titulo, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);

		// crearLabel() de PanelBaseInterfaz — sustituye crearEtiqueta()
		JLabel labelProducto = crearLabel("Producto: " + controlador.getNombreProducto());
		gbc.gridy = 1;
		panel.add(labelProducto, gbc);

		JLabel labelPrecio = new JLabel(String.format("Coste de tasación: %.2f€", controlador.getPrecioTasacion()));
		labelPrecio.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(22)));
		labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(20), 0);
		panel.add(labelPrecio, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(4), 0, VentanaPrincipal.escalar(4), 0);
		gbc.gridy = 3;
		panel.add(new JSeparator(), gbc);

		gbc.gridy = 4;
		panel.add(crearLabel("Número de tarjeta (16 dígitos):"), gbc);

		// crearCampo() de PanelBaseInterfaz — sustituye crearCampo() propio
		campoTarjeta = crearCampo();
		gbc.gridy = 5;
		panel.add(campoTarjeta, gbc);

		gbc.gridy = 6;
		panel.add(crearLabel("CVV (3 dígitos):"), gbc);

		campoCVV = new JPasswordField();
		campoCVV.setFont(VentanaPrincipal.FUENTE_NORMAL);
		campoCVV.setForeground(Color.BLACK);
		campoCVV.setBackground(Color.WHITE);
		campoCVV.setCaretColor(Color.BLACK);
		campoCVV.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(10))));
		gbc.gridy = 7;
		panel.add(campoCVV, gbc);

		gbc.gridy = 8;
		panel.add(crearLabel("Fecha caducidad (MM/AA):"), gbc);

		campoFecha = crearCampo();
		gbc.gridy = 9;
		panel.add(campoFecha, gbc);

		// crearBotonNaranja() de PanelBaseInterfaz
		botonPagar = crearBotonNaranja(String.format("Pagar %.2f€", controlador.getPrecioTasacion()));
		botonPagar.setActionCommand("pagar");
		botonPagar.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(20)));
		gbc.gridy = 10;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
		panel.add(botonPagar, gbc);

		panelCentral.add(panel, BorderLayout.CENTER);
		return panelCentral;
	}

	/**
	 * Vuelve a la cartera. Lo llama el controlador.
	 */
	public void volverACartera() {
		if (panelCliente != null)
			panelCliente.volverACartera();
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