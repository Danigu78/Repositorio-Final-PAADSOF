package Gui.cliente;

import productos.Producto2Mano;
import usuarios.Cliente;
import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorProducto2Mano;

import java.awt.*;
import java.awt.event.*;

/**
 * Subpanel de detalle de un producto de segunda mano. Extiende
 * AbstractPanelCliente para reutilizar helpers visuales del cliente. Sigue el
 * patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelProducto2Mano extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Subpanel de segunda mano para volver. */
	private SubpanelSegundaMano subpanelOrigen;

	/** Controlador del subpanel. */
	private ControladorProducto2Mano controlador;

	/** Subpanel de crear oferta. */
	private SubpanelCrearOferta subpanelCrearOferta;

	/** CardLayout para alternar entre detalle y crear oferta. */
	private CardLayout cardLayout;

	/** Panel contenedor del CardLayout. */
	private JPanel panelContenido;

	/** Botón volver — atributo para registrar el controlador. */
	private JButton botonVolver;

	/** Botón ofertar — atributo para registrar el controlador. */
	private JButton botonOfertar;

	/**
	 * Constructor del subpanel de detalle de producto de segunda mano.
	 *
	 * @param ventana        La ventana principal
	 * @param subpanelOrigen El subpanel de segunda mano para volver
	 */
	public SubpanelProducto2Mano(VentanaPrincipal ventana, SubpanelSegundaMano subpanelOrigen) {
		super(ventana);
		this.subpanelOrigen = subpanelOrigen;

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		add(panelContenido, BorderLayout.CENTER);
	}

	/**
	 * No se usa en este subpanel — la construcción se hace en mostrarProducto(). Se
	 * implementa por obligación de AbstractPanelCliente.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
	}

	/**
	 * Carga el producto y construye la interfaz. Crea el controlador y lo registra
	 * en los botones — patrón de los apuntes.
	 *
	 * @param producto El producto a mostrar
	 * @param cliente  El cliente logueado o null
	 */
	public void mostrarProducto(Producto2Mano producto, Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorProducto2Mano(this, producto, cliente);

		// SubpanelCrearOferta ya extiende AbstractPanelCliente
		subpanelCrearOferta = new SubpanelCrearOferta(ventana, this, producto, cliente);

		panelContenido.removeAll();
		panelContenido.add(crearPanelDetalle(producto), "DETALLE");
		panelContenido.add(subpanelCrearOferta, "OFERTA");

		setControlador(controlador);
		cardLayout.show(panelContenido, "DETALLE");
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes.
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonVolver != null) {
			for (ActionListener al : botonVolver.getActionListeners())
				botonVolver.removeActionListener(al);
			botonVolver.addActionListener(c);
		}
		if (botonOfertar != null) {
			for (ActionListener al : botonOfertar.getActionListeners())
				botonOfertar.removeActionListener(al);
			botonOfertar.addActionListener(c);
		}
	}

	/**
	 * Crea el panel de detalle del producto con imagen e información. Usa
	 * crearBarraVolver(), cargarImagen() y crearBotonNaranja() de
	 * PanelBaseInterfaz.
	 *
	 * @param producto El producto a mostrar
	 * @return Panel con el detalle
	 */
	private JPanel crearPanelDetalle(Producto2Mano producto) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);

		// crearBarraVolver() de PanelBaseInterfaz
		JPanel barra = crearBarraVolver("← Volver a segunda mano");
		botonVolver = getBotonVolver(barra);
		botonVolver.setActionCommand("volver");
		panel.add(barra, BorderLayout.NORTH);

		JPanel panelCentral = new JPanel(new BorderLayout());
		panelCentral.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelCentral.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(300), VentanaPrincipal.escalar(300)));
		// cargarImagen() de PanelBaseInterfaz
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(280),
				VentanaPrincipal.escalar(280));
		panelCentral.add(labelImagen, BorderLayout.WEST);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelInfo.setBorder(BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(30), 0, 0));

		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelInfo.add(labelNombre);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));

		// crearLabel() de PanelBaseInterfaz
		JLabel labelPropietario = crearLabel("Propietario: " + producto.getPropietario().getNickname());
		panelInfo.add(labelPropietario);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JLabel labelDescTitulo = new JLabel("Descripción:");
		labelDescTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelDescTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelInfo.add(labelDescTitulo);

		JLabel labelDesc = new JLabel("<html><p style='width:400px'>" + producto.getDescripcion() + "</p></html>");
		labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelDesc.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelInfo.add(labelDesc);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		if (producto.getValoracion() != null) {
			JLabel labelPrecio = new JLabel(
					String.format("Precio tasado: %.2f€", producto.getValoracion().getPrecioTasacion()));
			labelPrecio.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(22)));
			labelPrecio.setForeground(VentanaPrincipal.COLOR_ACENTO);
			panelInfo.add(labelPrecio);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

			JLabel labelEstado = crearLabel("Estado: " + producto.getValoracion().getEstadoProducto().toString());
			panelInfo.add(labelEstado);
			panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
		}

		if (controlador.puedeOfertar()) {
			// crearBotonNaranja() de PanelBaseInterfaz
			botonOfertar = crearBotonNaranja("Hacer oferta");
			botonOfertar.setActionCommand("ofertar");
			botonOfertar.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10),
					VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));
			panelInfo.add(botonOfertar);
		}

		panelCentral.add(panelInfo, BorderLayout.CENTER);
		panel.add(panelCentral, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Vuelve a segunda mano. Lo llama el controlador.
	 */
	public void volver() {
		subpanelOrigen.volverDelProducto2Mano();
	}

	/**
	 * Navega al subpanel de crear oferta. Lo llama el controlador.
	 */
	public void navegarACrearOferta() {
		cardLayout.show(panelContenido, "OFERTA");
	}

	/**
	 * Vuelve al detalle del producto desde crear oferta.
	 */
	public void volverAlDetalle() {
		cardLayout.show(panelContenido, "DETALLE");
	}
}