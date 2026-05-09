package Gui.cliente;

import tienda.Notificacion;
import usuarios.Cliente;
import javax.swing.*;

import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorNotificaciones;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Subpanel de notificaciones del cliente. Extiende AbstractPanelCliente para
 * reutilizar helpers visuales del cliente. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelNotificaciones extends AbstractPanelCliente {

	/** Controlador del subpanel. */
	private ControladorNotificaciones controlador;

	/** Modelo de la lista de notificaciones. */
	private DefaultListModel<String> modeloNotificaciones;

	/** Lista visual de notificaciones. */
	private JList<String> listaNotificaciones;

	/** Lista paralela de notificaciones mostradas para recuperar el objeto. */
	private List<Notificacion> notificacionesMostradas;

	/** Combo de filtro por estado de lectura. */
	private JComboBox<String> comboFiltro;

	/** Label con el resumen de notificaciones mostradas. */
	private JLabel labelResumen;

	/** Botón refrescar — atributo para registrar el controlador. */
	private JButton botonRefrescar;

	/** Botón ver notificación — atributo para registrar el controlador. */
	private JButton botonVer;

	/** Botón marcar todas vistas — atributo para registrar el controlador. */
	private JButton botonMarcarTodas;

	/**
	 * Constructor del subpanel de notificaciones.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelNotificaciones(VentanaPrincipal ventana) {
		super(ventana);
		notificacionesMostradas = new ArrayList<>();
	}

	/**
	 * Actualiza el subpanel con los datos del cliente. Crea el controlador y lo
	 * registra en los botones — patrón de los apuntes.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorNotificaciones(this, cliente);
		removeAll();

		// crearPanelBase() y getContenido() de AbstractPanelSection
		JPanel panelBase = crearPanelBase("");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloqueBandeja());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAcciones());

		add(panelBase, BorderLayout.CENTER);

		setControlador(controlador);
		cargarNotificaciones();
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes.
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonRefrescar != null) {
			for (ActionListener al : botonRefrescar.getActionListeners())
				botonRefrescar.removeActionListener(al);
			botonRefrescar.addActionListener(c);
		}
		if (botonVer != null) {
			for (ActionListener al : botonVer.getActionListeners())
				botonVer.removeActionListener(al);
			botonVer.addActionListener(c);
		}
		if (botonMarcarTodas != null) {
			for (ActionListener al : botonMarcarTodas.getActionListeners())
				botonMarcarTodas.removeActionListener(al);
			botonMarcarTodas.addActionListener(c);
		}
	}

	/**
	 * Crea el bloque de la bandeja de notificaciones. Mismo estilo que
	 * SeccionNotificacionesEmpleado.
	 *
	 * @return Panel del bloque bandeja
	 */
	private JPanel crearBloqueBandeja() {
		// crearBloque() de AbstractPanelSection
		JPanel bloque = crearBloque("Bandeja de notificaciones");

		notificacionesMostradas = new ArrayList<>();
		modeloNotificaciones = new DefaultListModel<>();
		listaNotificaciones = new JList<>(modeloNotificaciones);
		estilizarLista();

		// crearCombo() de AbstractPanelSection
		comboFiltro = crearCombo(new String[] { "Todas", "No vistas", "Vistas" });
		comboFiltro.addActionListener(e -> cargarNotificaciones());

		// crearBotonSecundario() de AbstractPanelSection
		botonRefrescar = crearBotonSecundario("Refrescar");
		botonRefrescar.setActionCommand("refrescar");

		// crearLabel() de AbstractPanelSection
		labelResumen = crearLabel("");

		JPanel filaFiltro = new JPanel(new BorderLayout(VentanaPrincipal.escalar(12), 0));
		filaFiltro.setOpaque(false);

		JPanel zonaFiltro = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaFiltro.setOpaque(false);
		zonaFiltro.add(crearLabel("Mostrar"), BorderLayout.NORTH);
		zonaFiltro.add(comboFiltro, BorderLayout.CENTER);

		JPanel zonaBoton = new JPanel(new BorderLayout());
		zonaBoton.setOpaque(false);
		zonaBoton.add(botonRefrescar, BorderLayout.SOUTH);

		filaFiltro.add(zonaFiltro, BorderLayout.CENTER);
		filaFiltro.add(zonaBoton, BorderLayout.EAST);

		// estilizarScroll() de AbstractPanelSection
		JScrollPane scrollLista = estilizarScroll(listaNotificaciones);
		scrollLista.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(330)));

		// gbcCampo() de AbstractPanelSection
		bloque.add(labelResumen, gbcCampo(1));
		bloque.add(filaFiltro, gbcCampo(2));
		bloque.add(scrollLista, gbcCampo(3));

		return bloque;
	}

	/**
	 * Crea el bloque de acciones sobre notificaciones. Mismo estilo que
	 * SeccionNotificacionesEmpleado.
	 *
	 * @return Panel del bloque acciones
	 */
	private JPanel crearBloqueAcciones() {
		// crearBloque() de AbstractPanelSection
		JPanel bloque = crearBloque("Consultar notificación");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelAyuda());
		panelAcciones.add(crearPanelBotones());

		bloque.add(panelAcciones, gbcCampo(1));
		return bloque;
	}

	/**
	 * Crea el panel de ayuda con instrucciones de uso.
	 *
	 * @return Panel de ayuda
	 */
	private JPanel crearPanelAyuda() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Cómo funciona"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearLabel("Selecciona una notificación de la bandeja."));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		panel.add(crearLabel("Al abrirla pasa automáticamente a vista."));

		return panel;
	}

	/**
	 * Crea el panel con los botones de acción sobre notificaciones.
	 *
	 * @return Panel de botones
	 */
	private JPanel crearPanelBotones() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Acciones"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		// crearBotonAccion() y crearBotonSecundario() de AbstractPanelSection
		botonVer = crearBotonAccion("Ver notificación");
		botonVer.setActionCommand("ver");

		botonMarcarTodas = crearBotonSecundario("Marcar todas vistas");
		botonMarcarTodas.setActionCommand("marcarTodas");

		JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(10), 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonVer);
		filaBotones.add(botonMarcarTodas);

		Dimension tamano = new Dimension(VentanaPrincipal.escalar(460), VentanaPrincipal.escalar(42));
		filaBotones.setPreferredSize(tamano);
		filaBotones.setMaximumSize(tamano);

		panel.add(filaBotones);
		return panel;
	}

	/**
	 * Carga y muestra las notificaciones según el filtro seleccionado. Lo llama el
	 * controlador y el combo al cambiar.
	 */
	public void cargarNotificaciones() {
		if (controlador == null)
			return;
		modeloNotificaciones.clear();
		notificacionesMostradas.clear();

		String filtro = comboFiltro != null ? (String) comboFiltro.getSelectedItem() : "Todas";

		List<Notificacion> notificaciones = controlador.getNotificaciones(filtro);
		int noLeidas = controlador.contarNoLeidas();

		for (Notificacion n : notificaciones) {
			notificacionesMostradas.add(n);
			modeloNotificaciones.addElement(crearTextoLista(n));
		}

		actualizarResumen(notificaciones.size(), noLeidas);
		listaNotificaciones.clearSelection();
	}

	/**
	 * Muestra la notificación seleccionada en un diálogo. Lo llama el controlador.
	 */
	public void verNotificacionSeleccionada() {
		int posicion = listaNotificaciones.getSelectedIndex();
		if (posicion < 0) {
			mostrarError("Selecciona una notificación de la bandeja.");
			return;
		}
		Notificacion n = notificacionesMostradas.get(posicion);
		if (n == null) {
			mostrarError("No se pudo abrir la notificación.");
			return;
		}
		controlador.marcarComoLeida(n);
		mostrarNotificacionEnVentana(n);
		cargarNotificaciones();
	}

	/**
	 * Muestra el contenido completo de una notificación en un diálogo.
	 *
	 * @param n La notificación a mostrar
	 */
	private void mostrarNotificacionEnVentana(Notificacion n) {
		// crearArea() y estilizarScroll() de AbstractPanelSection
		JTextArea area = crearArea();
		area.setEditable(false);
		area.setText(crearTextoNotificacion(n));
		area.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(area);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(640), VentanaPrincipal.escalar(260)));

		JOptionPane.showMessageDialog(this, scroll, "Notificación", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Actualiza el label de resumen con el total y no leídas.
	 *
	 * @param totalMostradas Total de notificaciones mostradas
	 * @param noVistas       Número de notificaciones no leídas
	 */
	private void actualizarResumen(int totalMostradas, int noVistas) {
		if (totalMostradas == 0) {
			labelResumen.setText("No hay notificaciones para mostrar con este filtro.");
			return;
		}
		labelResumen.setText("Mostrando " + totalMostradas + " notificaciones. No vistas: " + noVistas + ".");
	}

	/**
	 * Crea el texto que se muestra en la lista para una notificación.
	 *
	 * @param n La notificación
	 * @return Texto formateado para la lista
	 */
	private String crearTextoLista(Notificacion n) {
		String estado = n.isLeida() ? "Vista" : "Nueva";
		String tipo = n.getTipo() != null ? n.getTipo().toString() : "General";
		String fecha = formatearFecha(n);
		return estado + "   |   " + tipo + "   |   " + fecha;
	}

	/**
	 * Crea el texto completo de una notificación para mostrar en el diálogo.
	 *
	 * @param n La notificación
	 * @return Texto completo de la notificación
	 */
	private String crearTextoNotificacion(Notificacion n) {
		StringBuilder sb = new StringBuilder();
		sb.append("Tipo: ").append(n.getTipo() != null ? n.getTipo() : "General").append("\n");
		sb.append("Fecha: ").append(formatearFecha(n)).append("\n");
		sb.append("Vista: Sí\n\n");
		sb.append(n.getMensaje());
		return sb.toString();
	}

	/**
	 * Formatea la fecha de envío de una notificación.
	 *
	 * @param n La notificación
	 * @return Fecha formateada como string o "-" si es nula
	 */
	private String formatearFecha(Notificacion n) {
		if (n == null || n.getFechaEnvio() == null)
			return "-";
		return n.getFechaEnvio().toLocalDate() + " " + n.getFechaEnvio().toLocalTime().withNano(0);
	}

	/**
	 * Aplica el estilo visual a la lista de notificaciones.
	 */
	private void estilizarLista() {
		listaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaNotificaciones.setFixedCellHeight(VentanaPrincipal.escalar(34));
		listaNotificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		listaNotificaciones.setBackground(Color.WHITE);
		listaNotificaciones.setForeground(Color.BLACK);
		listaNotificaciones.setSelectionBackground(new Color(235, 235, 235));
		listaNotificaciones.setSelectionForeground(Color.BLACK);
	}
}