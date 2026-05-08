package Gui;

import Gui.Controladores.ControladorIntercambios;
import intercambios.EstadoOferta;
import intercambios.Oferta;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import productos.Producto2Mano;
import usuarios.Cliente;

/**
 * Subpanel de intercambios del cliente. Extiende AbstractPanelSection para
 * reutilizar helpers visuales. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelIntercambios extends AbstractPanelSection {

	private Cliente cliente;
	private ControladorIntercambios controlador;

	private JPanel contenidoEnviadas;
	private JPanel contenidoRecibidas;
	private JPanel contenidoHistorial;
	private JPanel contenidoRechazadas;
	private JPanel contenidoAceptadas;

	// Botones de la barra interna
	private JButton botonEnviados;
	private JButton botonRecibidos;
	private JButton botonHistorial;
	private JButton botonRechazadas;
	private JButton botonAceptadas;
	private JButton botonActivo;

	private CardLayout cardSecciones;
	private JPanel panelSecciones;

	public SubpanelIntercambios(VentanaPrincipal ventana) {
		super(ventana);

		add(crearBarraInterna(), BorderLayout.NORTH);

		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelSecciones.add(crearPanelContenido("enviadas"), "ENVIADAS");
		panelSecciones.add(crearPanelContenido("recibidas"), "RECIBIDAS");
		panelSecciones.add(crearPanelContenido("historial"), "HISTORIAL");
		panelSecciones.add(crearPanelContenido("rechazadas"), "RECHAZADAS");
		panelSecciones.add(crearPanelContenido("aceptadas"), "ACEPTADAS");

		add(panelSecciones, BorderLayout.CENTER);
		cardSecciones.show(panelSecciones, "ENVIADAS");
	}

	/**
	 * Registra el controlador en los botones de la barra — patrón de los apuntes.
	 */
	public void setControlador(ActionListener c) {
		registrarBoton(botonEnviados, c, "enviadas");
		registrarBoton(botonRecibidos, c, "recibidas");
		registrarBoton(botonHistorial, c, "historial");
		registrarBoton(botonRechazadas, c, "rechazadas");
		registrarBoton(botonAceptadas, c, "aceptadas");
	}

	private void registrarBoton(JButton boton, ActionListener c, String cmd) {
		if (boton == null)
			return;
		for (ActionListener al : boton.getActionListeners())
			boton.removeActionListener(al);
		boton.setActionCommand(cmd);
		boton.addActionListener(c);
	}

	private JPanel crearBarraInterna() {
		JPanel barra = new JPanel(new BorderLayout());
		barra.setBackground(VentanaPrincipal.COLOR_PANEL);
		barra.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(2), 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, VentanaPrincipal.escalar(15))));
		barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(50)));

		JPanel zonaPestañas = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(8), 0));
		zonaPestañas.setBackground(VentanaPrincipal.COLOR_PANEL);
		zonaPestañas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(9), 0, 0, 0));

		botonEnviados = crearBotonPestaña("Ofertas enviadas");
		botonRecibidos = crearBotonPestaña("Ofertas recibidas");
		botonAceptadas = crearBotonPestaña("Aceptadas / pendientes empleado");
		botonHistorial = crearBotonPestaña("Historial");
		botonRechazadas = crearBotonPestaña("Rechazadas/caducadas");

		marcarBotonActivo(botonEnviados);

		zonaPestañas.add(botonEnviados);
		zonaPestañas.add(botonRecibidos);
		zonaPestañas.add(botonAceptadas);
		zonaPestañas.add(botonHistorial);
		zonaPestañas.add(botonRechazadas);

		barra.add(zonaPestañas, BorderLayout.CENTER);
		return barra;
	}

	private JButton crearBotonPestaña(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		boton.setBackground(VentanaPrincipal.COLOR_PANEL);
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12)));
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (boton != botonActivo)
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (boton != botonActivo)
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			}
		});
		return boton;
	}

	/**
	 * Crea un panel con scroll para una sección. Guarda la referencia al contenido
	 * en clientProperty.
	 */
	private JPanel crearPanelContenido(String seccion) {
		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);

		switch (seccion) {
		case "enviadas":
			contenidoEnviadas = contenido;
			break;
		case "recibidas":
			contenidoRecibidas = contenido;
			break;
		case "historial":
			contenidoHistorial = contenido;
			break;
		case "rechazadas":
			contenidoRechazadas = contenido;
			break;
		case "aceptadas":
			contenidoAceptadas = contenido;
			break;
		}

		JScrollPane scroll = new JScrollPane(contenido);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);

		JPanel pan = new JPanel(new BorderLayout());
		pan.setBackground(VentanaPrincipal.COLOR_FONDO);
		pan.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));
		pan.add(scroll, BorderLayout.CENTER);
		return pan;
	}

	public void mostrarSeccion(String seccion) {
		cardSecciones.show(panelSecciones, seccion);
		switch (seccion) {
		case "ENVIADAS":
			marcarBotonActivo(botonEnviados);
			break;
		case "RECIBIDAS":
			marcarBotonActivo(botonRecibidos);
			break;
		case "HISTORIAL":
			marcarBotonActivo(botonHistorial);
			break;
		case "RECHAZADAS":
			marcarBotonActivo(botonRechazadas);
			break;
		case "ACEPTADAS":
			marcarBotonActivo(botonAceptadas);
			break;
		}
	}

	private void marcarBotonActivo(JButton boton) {
		int v = VentanaPrincipal.escalar(8);
		int h = VentanaPrincipal.escalar(12);
		int linea = VentanaPrincipal.escalar(3);
		if (botonActivo != null) {
			botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			botonActivo.setBorder(BorderFactory.createEmptyBorder(v, h, v, h));
		}
		botonActivo = boton;
		botonActivo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		botonActivo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, linea, 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(v, h, v - linea, h)));
	}

	private void rellenarEnviadas() {
		contenidoEnviadas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasEnviadas();
		if (ofertas.isEmpty()) {
			contenidoEnviadas.add(crearLabelVacio("No tienes ofertas enviadas pendientes."));
		} else {
			for (Oferta o : ofertas) {
				contenidoEnviadas.add(crearTarjetaOferta(o, false));
				contenidoEnviadas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}
		contenidoEnviadas.revalidate();
		contenidoEnviadas.repaint();
	}

	private void rellenarRecibidas() {
		contenidoRecibidas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasRecibidas();
		if (ofertas.isEmpty()) {
			contenidoRecibidas.add(crearLabelVacio("No tienes ofertas recibidas pendientes."));
		} else {
			for (Oferta o : ofertas) {
				// Solo mostramos las PENDIENTE con botones aceptar/rechazar
				if (o.getEstado() == EstadoOferta.PENDIENTE) {
					contenidoRecibidas.add(crearTarjetaOferta(o, true));
					contenidoRecibidas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
				}
			}
		}
		contenidoRecibidas.revalidate();
		contenidoRecibidas.repaint();
	}

	private void rellenarAceptadas() {
		contenidoAceptadas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasAceptadasPendientes();
		if (ofertas.isEmpty()) {
			contenidoAceptadas.add(crearLabelVacio("No tienes intercambios pendientes de confirmación."));
		} else {
			for (Oferta o : ofertas) {
				contenidoAceptadas.add(crearTarjetaAceptada(o));
				contenidoAceptadas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}
		contenidoAceptadas.revalidate();
		contenidoAceptadas.repaint();
	}

	private void rellenarHistorial() {
		contenidoHistorial.removeAll();
		List<Oferta> ofertas = controlador.getHistorial();
		if (ofertas.isEmpty()) {
			contenidoHistorial.add(crearLabelVacio("No tienes intercambios realizados todavía."));
		} else {
			for (Oferta o : ofertas) {
				contenidoHistorial.add(crearTarjetaHistorial(o));
				contenidoHistorial.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}
		contenidoHistorial.revalidate();
		contenidoHistorial.repaint();
	}

	private void rellenarRechazadas() {
		contenidoRechazadas.removeAll();
		List<Oferta> ofertas = controlador.getHistorialRechazadasCaducadas();
		if (ofertas.isEmpty()) {
			contenidoRechazadas.add(crearLabelVacio("No tienes ofertas rechazadas ni caducadas."));
		} else {
			for (Oferta o : ofertas) {
				contenidoRechazadas.add(crearTarjetaRechazada(o));
				contenidoRechazadas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}
		contenidoRechazadas.revalidate();
		contenidoRechazadas.repaint();
	}

	private JPanel crearTarjetaOferta(Oferta oferta, boolean esRecibida) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(140)));
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(2), 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JLabel labelId = new JLabel(oferta.getId());
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		String conQuien = esRecibida ? "De: " + oferta.getOrigen().getNickname()
				: "Para: " + oferta.getDestino().getNickname();
		JLabel labelConQuien = new JLabel(conQuien);
		labelConQuien.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelConQuien.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		JLabel labelOfrece = new JLabel("Ofrece: " + getListaProductos(oferta.getProductosOfertados()));
		labelOfrece.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelOfrece.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 2;
		panelInfo.add(labelOfrece, gbc);

		JLabel labelSolicita = new JLabel("Solicita: " + getListaProductos(oferta.getProductosSolicitados()));
		labelSolicita.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelSolicita.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 3;
		panelInfo.add(labelSolicita, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);
		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		if (esRecibida) {
			// crearBotonNaranja() de AbstractPanelSection
			JButton botonAceptar = crearBotonNaranja("Aceptar");
			botonAceptar.setActionCommand("aceptar:" + oferta.getId());
			botonAceptar.addActionListener(controlador);
			gbcB.gridy = 0;
			panelBotones.add(botonAceptar, gbcB);

			// crearBotonRojo() de AbstractPanelSection
			JButton botonRechazar = crearBotonRojo("Rechazar");
			botonRechazar.setActionCommand("rechazar:" + oferta.getId());
			botonRechazar.addActionListener(controlador);
			gbcB.gridy = 1;
			panelBotones.add(botonRechazar, gbcB);
		} else {
			// crearLabel() de AbstractPanelSection
			JLabel labelEstado = new JLabel(controlador.getTextoEstado(oferta));
			labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelEstado.setForeground(new Color(200, 150, 0));
			labelEstado.setHorizontalAlignment(SwingConstants.CENTER);
			gbcB.gridy = 0;
			panelBotones.add(labelEstado, gbcB);
		}

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	/**
	 * Tarjeta para ofertas aceptadas pendientes de confirmación por empleado.
	 */
	private JPanel crearTarjetaAceptada(Oferta oferta) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(140)));
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JLabel labelId = new JLabel(oferta.getId() + "  —  ⏳ Esperando confirmación de empleado");
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(new Color(0, 120, 200));
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		boolean soyOrigen = oferta.getOrigen().equals(cliente);
		String conQuien = soyOrigen ? "Con: " + oferta.getDestino().getNickname()
				: "Con: " + oferta.getOrigen().getNickname();
		JLabel labelConQuien = new JLabel(conQuien);
		labelConQuien.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelConQuien.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		JLabel labelOfrece = new JLabel("Ofrece: " + getListaProductos(oferta.getProductosOfertados()));
		labelOfrece.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelOfrece.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 2;
		panelInfo.add(labelOfrece, gbc);

		JLabel labelSolicita = new JLabel("Solicita: " + getListaProductos(oferta.getProductosSolicitados()));
		labelSolicita.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelSolicita.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 3;
		panelInfo.add(labelSolicita, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	private JPanel crearTarjetaHistorial(Oferta oferta) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(120)));
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JLabel labelId = new JLabel(oferta.getId() + "  —  " + controlador.getTextoEstado(oferta));
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(new Color(50, 150, 50));
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		boolean soyOrigen = oferta.getOrigen().equals(cliente);
		String conQuien = soyOrigen ? "Con: " + oferta.getDestino().getNickname()
				: "Con: " + oferta.getOrigen().getNickname();
		JLabel labelConQuien = new JLabel(conQuien);
		labelConQuien.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelConQuien.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		List<Producto2Mano> entregados = soyOrigen ? oferta.getProductosOfertados() : oferta.getProductosSolicitados();
		List<Producto2Mano> recibidos = soyOrigen ? oferta.getProductosSolicitados() : oferta.getProductosOfertados();

		JLabel labelEntregados = new JLabel("Entregaste: " + getListaProductos(entregados));
		labelEntregados.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelEntregados.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 2;
		panelInfo.add(labelEntregados, gbc);

		JLabel labelRecibidos = new JLabel("Recibiste: " + getListaProductos(recibidos));
		labelRecibidos.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelRecibidos.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 3;
		panelInfo.add(labelRecibidos, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	private JPanel crearTarjetaRechazada(Oferta oferta) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(120)));
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		String motivo = oferta.getEstado() == EstadoOferta.CADUCADA ? "Caducada" : "Rechazada";
		Color colorEstado = oferta.getEstado() == EstadoOferta.CADUCADA ? new Color(150, 100, 0)
				: new Color(180, 50, 50);

		JLabel labelId = new JLabel(oferta.getId() + "  —  " + motivo);
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(colorEstado);
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		boolean soyOrigen = oferta.getOrigen().equals(cliente);
		String conQuien = soyOrigen ? "Para: " + oferta.getDestino().getNickname()
				: "De: " + oferta.getOrigen().getNickname();
		JLabel labelConQuien = new JLabel(conQuien);
		labelConQuien.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelConQuien.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		JLabel labelOfrece = new JLabel("Ofrecía: " + getListaProductos(oferta.getProductosOfertados()));
		labelOfrece.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelOfrece.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 2;
		panelInfo.add(labelOfrece, gbc);

		JLabel labelSolicita = new JLabel("Solicitaba: " + getListaProductos(oferta.getProductosSolicitados()));
		labelSolicita.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelSolicita.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 3;
		panelInfo.add(labelSolicita, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	private String getListaProductos(List<Producto2Mano> productos) {
		if (productos == null || productos.isEmpty())
			return "ninguno";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < productos.size(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(productos.get(i).getNombre());
		}
		return sb.toString();
	}

	private JLabel crearLabelVacio(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	/**
	 * Procesa aceptar una oferta — lo llama el controlador.
	 */
	public void procesarAceptarOferta(String idOferta) {
		boolean ok = controlador.aceptarOferta(idOferta);
		if (ok) {
			mostrarMensaje("Oferta aceptada. Un empleado confirmará el intercambio.");
			actualizar(cliente);
			// Navegamos a la pestaña de aceptadas para que lo vea
			mostrarSeccion("ACEPTADAS");
		} else {
			mostrarError("No se encontró la oferta.");
		}
	}

	/**
	 * Procesa rechazar una oferta — lo llama el controlador.
	 */
	public void procesarRechazarOferta(String idOferta) {
		boolean ok = controlador.rechazarOferta(idOferta);
		if (ok) {
			mostrarMensaje("Oferta rechazada correctamente.");
			actualizar(cliente);
		} else {
			mostrarError("No se pudo rechazar la oferta.");
		}
	}

	@Override
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorIntercambios(this, cliente);
		setControlador(controlador);
		rellenarEnviadas();
		rellenarRecibidas();
		rellenarAceptadas();
		rellenarHistorial();
		rellenarRechazadas();
		cardSecciones.show(panelSecciones, "ENVIADAS");
		marcarBotonActivo(botonEnviados);
	}
}