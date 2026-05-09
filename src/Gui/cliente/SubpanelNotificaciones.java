package Gui.cliente;

import Gui.AbstractPanelSection;

import Gui.VentanaPrincipal;

import Gui.controladores.cliente.ControladorNotificaciones;
import tienda.Notificacion;
import usuarios.Cliente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Subpanel de notificaciones del cliente. Mismo estilo que
 * SeccionNotificacionesEmpleado de Lucas. Extiende AbstractPanelSection para
 * reutilizar helpers visuales. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelNotificaciones extends AbstractPanelSection {

	private Cliente cliente;
	private ControladorNotificaciones controlador;

	private DefaultListModel<String> modeloNotificaciones;
	private JList<String> listaNotificaciones;
	private List<Notificacion> notificacionesMostradas;
	private JComboBox<String> comboFiltro;
	private JLabel labelResumen;

	// Botones — atributos para registrar el controlador
	private JButton botonRefrescar;
	private JButton botonVer;
	private JButton botonMarcarTodas;

	public SubpanelNotificaciones(VentanaPrincipal ventana) {
		super(ventana);
		notificacionesMostradas = new ArrayList<>();
	}

	/**
	 * Actualiza el subpanel con los datos del cliente. Crea el controlador y lo
	 * registra en los botones — patrón de los apuntes.
	 */
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorNotificaciones(this, cliente);
		removeAll();

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
	 * Crea el bloque de la bandeja — mismo estilo que
	 * SeccionNotificacionesEmpleado.
	 */
	private JPanel crearBloqueBandeja() {
		JPanel bloque = crearBloque("Bandeja de notificaciones");

		notificacionesMostradas = new ArrayList<>();
		modeloNotificaciones = new DefaultListModel<>();
		listaNotificaciones = new JList<>(modeloNotificaciones);
		estilizarLista();

	
		comboFiltro = crearCombo(new String[] { "Todas", "No vistas", "Vistas" });
		comboFiltro.addActionListener(e -> cargarNotificaciones());

		botonRefrescar = crearBotonSecundario("Refrescar");
		botonRefrescar.setActionCommand("refrescar");
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

		// ScrollPane — usa estilizarScroll() de AbstractPanelSection
		JScrollPane scrollLista = estilizarScroll(listaNotificaciones);
		scrollLista.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(330)));

		// gbcCampo() de AbstractPanelSection — mismo que el empleado
		bloque.add(labelResumen, gbcCampo(1));
		bloque.add(filaFiltro, gbcCampo(2));
		bloque.add(scrollLista, gbcCampo(3));

		return bloque;
	}

	/**
	 * Crea el bloque de acciones — mismo estilo que SeccionNotificacionesEmpleado.
	 */
	private JPanel crearBloqueAcciones() {
		JPanel bloque = crearBloque("Consultar notificación");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelAyuda());
		panelAcciones.add(crearPanelBotones());

		bloque.add(panelAcciones, gbcCampo(1));
		return bloque;
	}

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
	 * Carga y muestra las notificaciones según el filtro. Lo llama el controlador y
	 * el combo al cambiar.
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

	private void actualizarResumen(int totalMostradas, int noVistas) {
		if (totalMostradas == 0) {
			labelResumen.setText("No hay notificaciones para mostrar con este filtro.");
			return;
		}
		labelResumen.setText("Mostrando " + totalMostradas + " notificaciones. No vistas: " + noVistas + ".");
	}

	private String crearTextoLista(Notificacion n) {
		String estado = n.isLeida() ? "Vista" : "Nueva";
		String tipo = n.getTipo() != null ? n.getTipo().toString() : "General";
		String fecha = formatearFecha(n);
		return estado + "   |   " + tipo + "   |   " + fecha;
	}

	private String crearTextoNotificacion(Notificacion n) {
		StringBuilder sb = new StringBuilder();
		sb.append("Tipo: ").append(n.getTipo() != null ? n.getTipo() : "General").append("\n");
		sb.append("Fecha: ").append(formatearFecha(n)).append("\n");
		sb.append("Vista: Sí\n\n");
		sb.append(n.getMensaje());
		return sb.toString();
	}

	private String formatearFecha(Notificacion n) {
		if (n == null || n.getFechaEnvio() == null)
			return "-";
		return n.getFechaEnvio().toLocalDate() + " " + n.getFechaEnvio().toLocalTime().withNano(0);
	}

	private void estilizarLista() {
		listaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaNotificaciones.setFixedCellHeight(VentanaPrincipal.escalar(34));
		listaNotificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		listaNotificaciones.setBackground(Color.WHITE);
		listaNotificaciones.setForeground(Color.BLACK);
		listaNotificaciones.setSelectionBackground(new Color(235, 235, 235));
		listaNotificaciones.setSelectionForeground(Color.BLACK);
	}

	/**
	 * Sobreescribimos mostrarMensaje para que compile ya que AbstractPanelSection
	 * ya lo tiene — aquí lo exponemos público.
	 */
	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}