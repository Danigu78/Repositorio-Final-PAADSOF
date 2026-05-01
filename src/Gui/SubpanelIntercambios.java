package Gui;

import javax.swing.*;

import Gui.Controladores.ControladorIntercambios;
import intercambios.EstadoOferta;
import intercambios.Oferta;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.PrivateKey;

import usuarios.Cliente;
import productos.*;
import java.util.ArrayList;

public class SubpanelIntercambios extends JPanel {
	private VentanaPrincipal ventana;
	private Cliente cliente;
	private ControladorIntercambios controlador;

	// En vez de guardar los wrappers guardamos directamente los paneles de
	// contenido
	private JPanel contenidoEnviadas;
	private JPanel contenidoRecibidas;
	private JPanel contenidoHistorial;
	// Botones de la barra interna
	private JButton botonEnviados;
	private JButton botonRecibidos;
	private JButton botonHistorial;
	private JButton botonRechazadas;
	private JButton botonActivo;

	// CardLayout para las tres secciones
	private CardLayout cardSecciones;
	private JPanel panelSecciones;

	// Paneles de contenido
	private JPanel panelEnviadas;
	private JPanel panelRecibidos;
	private JPanel panelHistorial;
	private JPanel contenidoRechazadas;

	public SubpanelIntercambios(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);

		add(crearBarraInterna(), BorderLayout.NORTH);

		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelSecciones.add(crearPanelEnviadas(), "ENVIADAS");
		panelSecciones.add(crearPanelRecibidas(), "RECIBIDAS");
		panelSecciones.add(crearPanelHistorial(), "HISTORIAL");
		panelSecciones.add(crearPanelRechazadas(), "RECHAZADAS");

		add(panelSecciones, BorderLayout.CENTER);
		cardSecciones.show(panelSecciones, "ENVIADAS");
	}

	public void setControladores(ActionListener c) {

		botonEnviados.addActionListener(c);
		botonRecibidos.addActionListener(c);
		botonHistorial.addActionListener(c);
		botonRechazadas.addActionListener(c);
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

		botonEnviados = crearBotonPestaña("Ofertas enviadas", "enviadas");
		botonRecibidos = crearBotonPestaña("Ofertas recibidas", "recibidas");
		botonHistorial = crearBotonPestaña("Historial", "historial");
		botonRechazadas = crearBotonPestaña("Rechazadas/caducadas", "rechazadas");
		marcarBotonActivo(botonEnviados);// primero vemos la pantalla de botonenviados
		zonaPestañas.add(botonEnviados);
		zonaPestañas.add(botonRecibidos);
		zonaPestañas.add(botonHistorial);
		zonaPestañas.add(botonRechazadas);
		barra.add(zonaPestañas, BorderLayout.CENTER);
		return barra;

	}

	private JButton crearBotonPestaña(String texto, String comando) {
		JButton boton = new JButton(texto);
		boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		boton.setBackground(VentanaPrincipal.COLOR_PANEL);
		boton.setBorder(BorderFactory.createEmptyBorder(// padding alrededor del texto
				VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(8),
				VentanaPrincipal.escalar(12)));
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setActionCommand(comando);
		;
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (boton != botonActivo) {
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (boton != botonActivo) {
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
				}
			}

		});
		return boton;
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

	private JPanel crearPanelEnviadas() {
		contenidoEnviadas = new JPanel();
		contenidoEnviadas.setLayout(new BoxLayout(contenidoEnviadas, BoxLayout.Y_AXIS));
		contenidoEnviadas.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(contenidoEnviadas);
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

	private JPanel crearPanelRecibidas() {
		contenidoRecibidas = new JPanel();
		contenidoRecibidas.setLayout(new BoxLayout(contenidoRecibidas, BoxLayout.Y_AXIS));
		contenidoRecibidas.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(contenidoRecibidas);
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

	private JPanel crearPanelHistorial() {
		contenidoHistorial = new JPanel();
		contenidoHistorial.setLayout(new BoxLayout(contenidoHistorial, BoxLayout.Y_AXIS));
		contenidoHistorial.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(contenidoHistorial);
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

	private JPanel crearPanelRechazadas() {
		contenidoRechazadas = new JPanel();
		contenidoRechazadas.setLayout(new BoxLayout(contenidoRechazadas, BoxLayout.Y_AXIS));
		contenidoRechazadas.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(contenidoRechazadas);
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

	/**
	 * Rellena el panel de ofertas enviadas.
	 */
	private void rellenarEnviadas() {
		contenidoEnviadas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasEnviadas();
		if (ofertas.isEmpty()) {
			contenidoEnviadas.add(crearLabelVacío("No tienes ofertas enviadas pendientes."));
		} else {
			for (Oferta o : ofertas) {
				contenidoEnviadas.add(crearTarjetaOferta(o, false));
				contenidoEnviadas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}
		contenidoEnviadas.revalidate();
		contenidoEnviadas.repaint();
	}

	/**
	 * Rellena el panel de ofertas recibidas.
	 */
	private void rellenarRecibidas() {
		contenidoRecibidas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasRecibidas();
		if (ofertas.isEmpty()) {
			contenidoRecibidas.add(crearLabelVacío("No tienes ofertas recibidas pendientes."));
		} else {
			for (Oferta o : ofertas) {
				contenidoRecibidas.add(crearTarjetaOferta(o, true));
				contenidoRecibidas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}
		contenidoRecibidas.revalidate();
		contenidoRecibidas.repaint();
	}

	/**
	 * Rellena el panel de historial de intercambios.
	 */
	private void rellenarHistorial() {
		contenidoHistorial.removeAll();
		List<Oferta> ofertas = controlador.getHistorial();
		if (ofertas.isEmpty()) {
			contenidoHistorial.add(crearLabelVacío("No tienes intercambios realizados todavía."));
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
			contenidoRechazadas.add(crearLabelVacío("No tienes ofertas rechazadas ni caducadas."));
		} else {
			for (Oferta o : ofertas) {
				contenidoRechazadas.add(crearTarjetaRechazada(o));
				contenidoRechazadas.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			}
		}
		contenidoRechazadas.revalidate();
		contenidoRechazadas.repaint();
	}

	/**
	 * Crea una tarjeta para una oferta enviada o recibida.
	 *
	 * @param oferta     La oferta a mostrar
	 * @param esRecibida true si es recibida, false si es enviada
	 * @return Panel con la tarjeta
	 */
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
		gbc.anchor = GridBagConstraints.WEST;// alineamos los ocmponentes en la izquierda
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10)); // espacio margenes
		gbc.weightx = 1;// crece en horizontal
		gbc.fill = GridBagConstraints.HORIZONTAL; // se estira
		gbc.gridx = 0; // columna fija
		// ID
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
			// Botón aceptar
			JButton botonAceptar = crearBotonPrincipal("Aceptar");
			botonAceptar.setActionCommand("aceptar:" + oferta.getId());
			botonAceptar.addActionListener(controlador);
			gbcB.gridy = 0;
			panelBotones.add(botonAceptar, gbcB);

			// Botón rechazar
			JButton botonRechazar = crearBotonRojo("Rechazar");
			botonRechazar.setActionCommand("rechazar:" + oferta.getId());
			botonRechazar.addActionListener(controlador);
			gbcB.gridy = 1;
			panelBotones.add(botonRechazar, gbcB);
		} else {
			// Oferta enviada — solo estado en amarillo
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

		// ID + estado en verde
		JLabel labelId = new JLabel(oferta.getId() + "  —  " + controlador.getTextoEstado(oferta));
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(new Color(50, 150, 50));
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		// Con quién
		boolean soyOrigen = oferta.getOrigen().equals(cliente);
		String conQuien = soyOrigen ? "Con: " + oferta.getDestino().getNickname()
				: "Con: " + oferta.getOrigen().getNickname();
		JLabel labelConQuien = new JLabel(conQuien);
		labelConQuien.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelConQuien.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		// Entregados y recibidos según si soy origen o destino
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

		Color colorEstado = oferta.getEstado() == EstadoOferta.CADUCADA ? new Color(150, 100, 0) // amarillo oscuro para
																									// caducada
				: new Color(180, 50, 50); // rojo para rechazada

		JLabel labelId = new JLabel(oferta.getId() + "  —  " + motivo);
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(colorEstado);
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);
		// Con quién
		boolean soyOrigen = oferta.getOrigen().equals(cliente);
		String conQuien = soyOrigen ? "Para: " + oferta.getDestino().getNickname()
				: "De: " + oferta.getOrigen().getNickname();
		JLabel labelConQuien = new JLabel(conQuien);
		labelConQuien.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelConQuien.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		// Productos
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

	/**
	 * Crea una etiqueta de texto vacío centrada.
	 *
	 * @param texto El texto a mostrar
	 * @return La etiqueta configurada
	 */
	private JLabel crearLabelVacío(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	/**
	 * Muestra un mensaje de error. Lo llama el controlador.
	 *
	 * @param mensaje El mensaje de error
	 */
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un mensaje de éxito. Lo llama el controlador.
	 *
	 * @param mensaje El mensaje de éxito
	 */
	public void mostrarExito(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12)));
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

	private JButton crearBotonRojo(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(new Color(180, 50, 50));
		boton.setForeground(Color.WHITE);
		boton.setOpaque(true);
		boton.setBorderPainted(false);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12)));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				boton.setBackground(new Color(200, 60, 60));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				boton.setBackground(new Color(180, 50, 50));
			}
		});
		return boton;
	}

	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorIntercambios(this, cliente);
		setControladores(controlador);
		rellenarEnviadas();
		rellenarRecibidas();
		rellenarHistorial();
		rellenarRechazadas();
		cardSecciones.show(panelSecciones, "ENVIADAS");
		marcarBotonActivo(botonEnviados);
	}
}