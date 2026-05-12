package Gui.cliente;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorIntercambios;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import productos.Producto2Mano;
import usuarios.Cliente;

/**
 * Subpanel de intercambios del cliente. Muestra cinco pestañas: ofertas
 * enviadas, recibidas, aceptadas pendientes de confirmación por empleado,
 * historial y rechazadas/caducadas. Extiende AbstractPanelCliente para
 * reutilizar helpers visuales del cliente. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelIntercambios extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Controlador del subpanel. */
	private ControladorIntercambios controlador;

	/** Panel de contenido de ofertas enviadas. */
	private JPanel contenidoEnviadas;

	/** Panel de contenido de ofertas recibidas. */
	private JPanel contenidoRecibidas;

	/** Panel de contenido de historial de intercambios. */
	private JPanel contenidoHistorial;

	/** Panel de contenido de ofertas rechazadas o caducadas. */
	private JPanel contenidoRechazadas;

	/** Panel de contenido de ofertas aceptadas pendientes de empleado. */
	private JPanel contenidoAceptadas;

	/** Botón de la pestaña de ofertas enviadas. */
	private JButton botonEnviados;

	/** Botón de la pestaña de ofertas recibidas. */
	private JButton botonRecibidos;

	/** Botón de la pestaña de historial. */
	private JButton botonHistorial;

	/** Botón de la pestaña de rechazadas/caducadas. */
	private JButton botonRechazadas;

	/** Botón de la pestaña de aceptadas pendientes de empleado. */
	private JButton botonAceptadas;

	/** Botón actualmente marcado como activo en la barra. */
	private JButton botonActivo;

	/** CardLayout para alternar entre las secciones. */
	private CardLayout cardSecciones;

	/** Panel que contiene todas las secciones con CardLayout. */
	private JPanel panelSecciones;

	/**
	 * Constructor del subpanel de intercambios.
	 *
	 * @param ventana La ventana principal
	 */
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
	 * Actualiza el subpanel con el cliente logueado. Crea el controlador, registra
	 * los botones y rellena todas las secciones.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
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

	/**
	 * Registra el controlador en los botones de la barra. Patrón MVC de los
	 * apuntes.
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		registrarBoton(botonEnviados, c, "enviadas");
		registrarBoton(botonRecibidos, c, "recibidas");
		registrarBoton(botonHistorial, c, "historial");
		registrarBoton(botonRechazadas, c, "rechazadas");
		registrarBoton(botonAceptadas, c, "aceptadas");
	}

	/**
	 * Registra un ActionListener en un botón con su comando.
	 *
	 * @param boton El botón a registrar
	 * @param c     El listener a añadir
	 * @param cmd   El ActionCommand a asignar
	 */
	private void registrarBoton(JButton boton, ActionListener c, String cmd) {
		if (boton == null)
			return;
		for (ActionListener al : boton.getActionListeners())
			boton.removeActionListener(al);
		boton.setActionCommand(cmd);
		boton.addActionListener(c);
	}

	/**
	 * Crea la barra superior de navegación con las pestañas del subpanel.
	 *
	 * @return Panel con la barra de navegación
	 */
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

	/**
	 * Crea un botón de pestaña con el estilo visual de la barra.
	 *
	 * @param texto El texto del botón
	 * @return El botón configurado
	 */
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
	 * Crea un panel con scroll para una sección y guarda la referencia al
	 * contenido.
	 *
	 * @param seccion Identificador de la sección
	 * @return Panel con scroll listo para añadir al CardLayout
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

	/**
	 * Muestra la sección indicada y marca su botón como activo.
	 *
	 * @param seccion Identificador de la sección a mostrar
	 */
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

	/**
	 * Aplica el estilo visual de activo al botón indicado y quita el estilo al
	 * botón previamente activo.
	 *
	 * @param boton El botón a marcar como activo
	 */
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

	/**
	 * Rellena el panel de ofertas enviadas.
	 */
	private void rellenarEnviadas() {
		contenidoEnviadas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasEnviadas();
		if (ofertas.isEmpty()) {
			// crearLabelVacio() de AbstractPanelCliente
			contenidoEnviadas.add(crearLabelVacio("No tienes ofertas enviadas pendientes."));
		} else {
			for (Oferta o : ofertas)
				// añadirTarjetaConSeparacion() de AbstractPanelCliente
				añadirTarjetaConSeparacion(contenidoEnviadas, crearTarjetaOferta(o, false), 8);
		}
		contenidoEnviadas.revalidate();
		contenidoEnviadas.repaint();
	}

	/**
	 * Rellena el panel de ofertas recibidas. Solo muestra las PENDIENTE con botones
	 * de acción.
	 */
	private void rellenarRecibidas() {
		contenidoRecibidas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasRecibidas();
		boolean hayPendientes = false;
		for (Oferta o : ofertas) {
			if (o.getEstado() == EstadoOferta.PENDIENTE) {
				añadirTarjetaConSeparacion(contenidoRecibidas, crearTarjetaOferta(o, true), 8);
				hayPendientes = true;
			}
		}
		if (!hayPendientes)
			contenidoRecibidas.add(crearLabelVacio("No tienes ofertas recibidas pendientes."));
		contenidoRecibidas.revalidate();
		contenidoRecibidas.repaint();
	}

	/**
	 * Rellena el panel de ofertas aceptadas pendientes de confirmación por
	 * empleado.
	 */
	private void rellenarAceptadas() {
		contenidoAceptadas.removeAll();
		List<Oferta> ofertas = controlador.getOfertasAceptadasPendientes();
		if (ofertas.isEmpty()) {
			contenidoAceptadas.add(crearLabelVacio("No tienes intercambios pendientes de confirmación."));
		} else {
			for (Oferta o : ofertas)
				añadirTarjetaConSeparacion(contenidoAceptadas, crearTarjetaAceptada(o), 8);
		}
		contenidoAceptadas.revalidate();
		contenidoAceptadas.repaint();
	}

	/**
	 * Rellena el panel del historial de intercambios realizados.
	 */
	private void rellenarHistorial() {
		contenidoHistorial.removeAll();
		List<Oferta> ofertas = controlador.getHistorial();
		if (ofertas.isEmpty()) {
			contenidoHistorial.add(crearLabelVacio("No tienes intercambios realizados todavía."));
		} else {
			for (Oferta o : ofertas)
				añadirTarjetaConSeparacion(contenidoHistorial, crearTarjetaHistorial(o), 8);
		}
		contenidoHistorial.revalidate();
		contenidoHistorial.repaint();
	}

	/**
	 * Rellena el panel de ofertas rechazadas o caducadas.
	 */
	private void rellenarRechazadas() {
		contenidoRechazadas.removeAll();
		List<Oferta> ofertas = controlador.getHistorialRechazadasCaducadas();
		if (ofertas.isEmpty()) {
			contenidoRechazadas.add(crearLabelVacio("No tienes ofertas rechazadas ni caducadas."));
		} else {
			for (Oferta o : ofertas)
				añadirTarjetaConSeparacion(contenidoRechazadas, crearTarjetaRechazada(o), 8);
		}
		contenidoRechazadas.revalidate();
		contenidoRechazadas.repaint();
	}

	/**
	 * Crea el panel de info interior compartido por todas las tarjetas de oferta.
	 * Reutiliza crearPanelInfoTarjeta() y crearGbcTarjeta() de
	 * AbstractPanelCliente.
	 *
	 * @return Par {panelInfo, gbc} listo para añadir filas
	 */
	private JPanel crearPanelInfoOferta() {
		// crearPanelInfoTarjeta() de AbstractPanelCliente
		return crearPanelInfoTarjeta();
	}

	/**
	 * Devuelve un GridBagConstraints preconfigurado para filas de tarjeta de
	 * oferta.
	 *
	 * @return GridBagConstraints con gridy=0, listo para incrementar
	 */
	private GridBagConstraints gbcOferta() {
		// crearGbcTarjeta() de AbstractPanelCliente
		return crearGbcTarjeta();
	}

	/**
	 * Crea una tarjeta para una oferta enviada o recibida. Si es recibida muestra
	 * botones aceptar/rechazar. Si es enviada muestra el estado y el tiempo
	 * restante si está PENDIENTE.
	 *
	 * @param oferta     La oferta a mostrar
	 * @param esRecibida true si es recibida, false si es enviada
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaOferta(Oferta oferta, boolean esRecibida) {
		// crearTarjetaBase() de AbstractPanelCliente — MatteBorder inferior
		JPanel tarjeta = crearTarjetaBase(165, true);

		JPanel panelInfo = crearPanelInfoOferta();
		GridBagConstraints gbc = gbcOferta();

		JLabel labelId = new JLabel(oferta.getId());
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		String conQuien = esRecibida ? "De: " + oferta.getOrigen().getNickname()
				: "Para: " + oferta.getDestino().getNickname();
		// crearLabel() de PanelBaseInterfaz
		JLabel labelConQuien = crearLabel(conQuien);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		JLabel labelOfrece = crearLabel("Ofrece: " + getListaProductos(oferta.getProductosOfertados()));
		labelOfrece.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 2;
		panelInfo.add(labelOfrece, gbc);

		JLabel labelSolicita = crearLabel("Solicita: " + getListaProductos(oferta.getProductosSolicitados()));
		labelSolicita.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 3;
		panelInfo.add(labelSolicita, gbc);

		// Tiempo restante — solo si está PENDIENTE
		if (oferta.getEstado() == EstadoOferta.PENDIENTE) {
			JLabel labelTiempo = crearLabel(controlador.getTiempoRestante(oferta));
			labelTiempo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelTiempo.setForeground(new Color(200, 150, 0));
			gbc.gridy = 4;
			panelInfo.add(labelTiempo, gbc);
		}

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		// crearPanelBotonesTarjeta() y crearGbcBotonesTarjeta() de AbstractPanelCliente
		JPanel panelBotones = crearPanelBotonesTarjeta();
		GridBagConstraints gbcB = crearGbcBotonesTarjeta();

		if (esRecibida) {
			// crearBotonNaranja() de PanelBaseInterfaz
			JButton botonAceptar = crearBotonNaranja("Aceptar");
			botonAceptar.setActionCommand("aceptar:" + oferta.getId());
			botonAceptar.addActionListener(controlador);
			gbcB.gridy = 0;
			panelBotones.add(botonAceptar, gbcB);

			// crearBotonRojo() de PanelBaseInterfaz
			JButton botonRechazar = crearBotonRojo("Rechazar");
			botonRechazar.setActionCommand("rechazar:" + oferta.getId());
			botonRechazar.addActionListener(controlador);
			gbcB.gridy = 1;
			panelBotones.add(botonRechazar, gbcB);
		} else {
			JLabel labelEstado = crearLabel(controlador.getTextoEstado(oferta));
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
	 * Crea una tarjeta para una oferta aceptada pendiente de confirmación. Muestra
	 * el estado en azul sin botones.
	 *
	 * @param oferta La oferta aceptada
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaAceptada(Oferta oferta) {
		JPanel tarjeta = crearTarjetaBase(140, true);

		JPanel panelInfo = crearPanelInfoOferta();
		GridBagConstraints gbc = gbcOferta();

		JLabel labelId = new JLabel(oferta.getId() + "  ⏳ Esperando confirmación de empleado");
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(new Color(0, 120, 200));
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		boolean soyOrigen = oferta.getOrigen().equals(cliente);
		String conQuien = soyOrigen ? "Con: " + oferta.getDestino().getNickname()
				: "Con: " + oferta.getOrigen().getNickname();
		JLabel labelConQuien = crearLabel(conQuien);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		JLabel labelOfrece = crearLabel("Ofrece: " + getListaProductos(oferta.getProductosOfertados()));
		labelOfrece.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 2;
		panelInfo.add(labelOfrece, gbc);

		JLabel labelSolicita = crearLabel("Solicita: " + getListaProductos(oferta.getProductosSolicitados()));
		labelSolicita.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 3;
		panelInfo.add(labelSolicita, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Crea una tarjeta para un intercambio del historial. Muestra quién entregó y
	 * quién recibió según si el cliente es origen o destino.
	 *
	 * @param oferta La oferta realizada
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaHistorial(Oferta oferta) {
		JPanel tarjeta = crearTarjetaBase(120, true);

		JPanel panelInfo = crearPanelInfoOferta();
		GridBagConstraints gbc = gbcOferta();

		JLabel labelId = new JLabel(oferta.getId() + "  —  " + controlador.getTextoEstado(oferta));
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(new Color(50, 150, 50));
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		boolean soyOrigen = oferta.getOrigen().equals(cliente);
		String conQuien = soyOrigen ? "Con: " + oferta.getDestino().getNickname()
				: "Con: " + oferta.getOrigen().getNickname();
		JLabel labelConQuien = crearLabel(conQuien);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		List<Producto2Mano> entregados = soyOrigen ? oferta.getProductosOfertados() : oferta.getProductosSolicitados();
		List<Producto2Mano> recibidos = soyOrigen ? oferta.getProductosSolicitados() : oferta.getProductosOfertados();

		JLabel labelEntregados = crearLabel("Entregaste: " + getListaProductos(entregados));
		labelEntregados.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 2;
		panelInfo.add(labelEntregados, gbc);

		JLabel labelRecibidos = crearLabel("Recibiste: " + getListaProductos(recibidos));
		labelRecibidos.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 3;
		panelInfo.add(labelRecibidos, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Crea una tarjeta para una oferta rechazada o caducada. Muestra el motivo en
	 * rojo si fue rechazada o en amarillo si caducó.
	 *
	 * @param oferta La oferta rechazada o caducada
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaRechazada(Oferta oferta) {
		JPanel tarjeta = crearTarjetaBase(120, true);

		JPanel panelInfo = crearPanelInfoOferta();
		GridBagConstraints gbc = gbcOferta();

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
		JLabel labelConQuien = crearLabel(conQuien);
		gbc.gridy = 1;
		panelInfo.add(labelConQuien, gbc);

		JLabel labelOfrece = crearLabel("Ofrecía: " + getListaProductos(oferta.getProductosOfertados()));
		labelOfrece.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 2;
		panelInfo.add(labelOfrece, gbc);

		JLabel labelSolicita = crearLabel("Solicitaba: " + getListaProductos(oferta.getProductosSolicitados()));
		labelSolicita.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		gbc.gridy = 3;
		panelInfo.add(labelSolicita, gbc);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		return tarjeta;
	}

	/**
	 * Construye un texto con los nombres de los productos separados por comas.
	 *
	 * @param productos Lista de productos de segunda mano
	 * @return Texto con los nombres o "ninguno" si la lista está vacía
	 */
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
	 * Procesa la aceptación de una oferta. Lo llama el controlador desde
	 * actionPerformed. Tras aceptar navega a la pestaña de aceptadas.
	 *
	 * @param idOferta Id de la oferta a aceptar
	 */
	public void procesarAceptarOferta(String idOferta) {
		boolean ok = controlador.aceptarOferta(idOferta);
		if (ok) {
			mostrarMensaje("Oferta aceptada. Un empleado confirmará el intercambio.");
			actualizar(cliente);
			mostrarSeccion("ACEPTADAS");
		} else {
			mostrarError("No se encontró la oferta.");
		}
	}

	/**
	 * Procesa el rechazo de una oferta. Lo llama el controlador desde
	 * actionPerformed.
	 *
	 * @param idOferta Id de la oferta a rechazar
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
}