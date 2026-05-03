	package Gui;
	
	import java.awt.*;
	import java.util.ArrayList;
	import java.util.List;
	
	import javax.swing.*;
	
	import tienda.Notificacion;
	import usuarios.Empleado;
	
	/**
	 * Pantalla de notificaciones del empleado.
	 * 
	 * Se muestra como una bandeja sencilla. La lista solo enseña un resumen de cada
	 * aviso. Para ver el mensaje completo, el empleado selecciona una notificación
	 * y pulsa el botón de ver.
	 */
	public class SeccionNotificacionesEmpleado extends AbstractPanelEmpleadoSection {
	
		private static final long serialVersionUID = 1L;
	
		private DefaultListModel<String> modeloNotificaciones;
		private JList<String> listaNotificaciones;
	
		private List<Notificacion> notificacionesMostradas;
	
		private JComboBox<String> comboFiltro;
		private JLabel labelResumen;
	
		public SeccionNotificacionesEmpleado(VentanaPrincipal ventana, Empleado empleado) {
			super(ventana, empleado);
			construirUI();
		}
	
		private void construirUI() {
			setLayout(new BorderLayout());
	
			JPanel panelBase = crearPanelBase("Notificaciones");
			JPanel contenido = getContenido(panelBase);
	
			contenido.add(crearBloqueBandeja());
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
			contenido.add(crearBloqueAcciones());
	
			add(panelBase, BorderLayout.CENTER);
		}
	
		private JPanel crearBloqueBandeja() {
			JPanel bloque = crearBloque("Bandeja de notificaciones");
	
			notificacionesMostradas = new ArrayList<>();
	
			modeloNotificaciones = new DefaultListModel<>();
			listaNotificaciones = new JList<>(modeloNotificaciones);
			estilizarLista();
	
			comboFiltro = crearCombo(new String[] { "Todas", "No vistas", "Vistas" });
			comboFiltro.addActionListener(e -> cargarNotificaciones());
	
			JButton botonRefrescar = crearBotonSecundario("Refrescar");
			botonRefrescar.addActionListener(e -> cargarNotificaciones());
	
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
	
			JScrollPane scrollLista = estilizarScroll(listaNotificaciones);
			scrollLista.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(330)));
	
			bloque.add(labelResumen, gbcCampo(1));
			bloque.add(filaFiltro, gbcCampo(2));
			bloque.add(scrollLista, gbcCampo(3));
	
			cargarNotificaciones();
	
			return bloque;
		}
	
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
	
			JLabel titulo = crearLabel("Cómo funciona");
			panel.add(titulo);
			panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
	
			panel.add(crearLabel("Selecciona una notificación de la bandeja."));
			panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
			panel.add(crearLabel("Al abrirla, pasa automáticamente a vista."));
	
			return panel;
		}
	
		private JPanel crearPanelBotones() {
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	
			panel.add(crearLabel("Acciones"));
			panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
	
			JButton botonVer = crearBotonAccion("Ver notificación");
			JButton botonMarcarTodas = crearBotonSecundario("Marcar todas vistas");
	
			JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(10), 0));
			filaBotones.setOpaque(false);
	
			filaBotones.add(botonVer);
			filaBotones.add(botonMarcarTodas);
	
			Dimension tamanoFila = new Dimension(VentanaPrincipal.escalar(460), VentanaPrincipal.escalar(42));
			filaBotones.setPreferredSize(tamanoFila);
			filaBotones.setMaximumSize(tamanoFila);
	
			panel.add(filaBotones);
	
			botonVer.addActionListener(e -> verNotificacionSeleccionada());
			botonMarcarTodas.addActionListener(e -> marcarTodasComoVistas());
	
			return panel;
		}
	
		private void cargarNotificaciones() {
			modeloNotificaciones.clear();
			notificacionesMostradas.clear();
	
			List<Notificacion> notificaciones = empleado.getNotificaciones();
	
			if (notificaciones == null || notificaciones.isEmpty()) {
				labelResumen.setText("No tienes notificaciones.");
				return;
			}
	
			String filtro = obtenerFiltroActual();
	
			int totalMostradas = 0;
			int noVistas = 0;
	
			for (Notificacion notificacion : notificaciones) {
				if (notificacion == null) {
					continue;
				}
	
				if (!notificacion.isLeida()) {
					noVistas++;
				}
	
				if (!pasaFiltro(notificacion, filtro)) {
					continue;
				}
	
				notificacionesMostradas.add(notificacion);
				modeloNotificaciones.addElement(crearTextoLista(notificacion));
				totalMostradas++;
			}
	
			actualizarResumen(totalMostradas, noVistas);
			listaNotificaciones.clearSelection();
		}
	
		private String obtenerFiltroActual() {
			if (comboFiltro == null || comboFiltro.getSelectedItem() == null) {
				return "Todas";
			}
	
			return String.valueOf(comboFiltro.getSelectedItem());
		}
	
		private boolean pasaFiltro(Notificacion notificacion, String filtro) {
			if ("No vistas".equals(filtro) && notificacion.isLeida()) {
				return false;
			}
	
			if ("Vistas".equals(filtro) && !notificacion.isLeida()) {
				return false;
			}
	
			return true;
		}
	
		private String crearTextoLista(Notificacion notificacion) {
			String estado = notificacion.isLeida() ? "Vista" : "Nueva";
			String tipo = obtenerTipo(notificacion);
			String fecha = formatearFecha(notificacion);
	
			return estado + "   |   " + tipo + "   |   " + fecha;
		}
	
		private void actualizarResumen(int totalMostradas, int noVistas) {
			if (totalMostradas == 0) {
				labelResumen.setText("No hay notificaciones para mostrar con este filtro.");
				return;
			}
	
			labelResumen.setText("Mostrando " + totalMostradas + " notificaciones. No vistas: " + noVistas + ".");
		}
	
		private void verNotificacionSeleccionada() {
			int posicion = listaNotificaciones.getSelectedIndex();
	
			if (posicion < 0) {
				mostrarError("Selecciona una notificación de la bandeja.");
				return;
			}
	
			Notificacion notificacion = notificacionesMostradas.get(posicion);
	
			if (notificacion == null) {
				mostrarError("No se pudo abrir la notificación.");
				return;
			}
	
			notificacion.marcarComoLeida();
			mostrarNotificacionEnVentana(notificacion);
			cargarNotificaciones();
		}
	
		private void marcarTodasComoVistas() {
			List<Notificacion> notificaciones = empleado.getNotificaciones();
	
			if (notificaciones == null || notificaciones.isEmpty()) {
				mostrarError("No hay notificaciones.");
				return;
			}
	
			for (Notificacion notificacion : notificaciones) {
				if (notificacion != null) {
					notificacion.marcarComoLeida();
				}
			}
	
			cargarNotificaciones();
			mostrarMensaje("Todas las notificaciones se han marcado como vistas.");
		}
	
		private void mostrarNotificacionEnVentana(Notificacion notificacion) {
			JTextArea areaNotificacion = crearArea();
			areaNotificacion.setEditable(false);
			areaNotificacion.setText(crearTextoNotificacion(notificacion));
			areaNotificacion.setCaretPosition(0);
	
			JScrollPane scroll = estilizarScroll(areaNotificacion);
			scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(640), VentanaPrincipal.escalar(260)));
	
			JOptionPane.showMessageDialog(this, scroll, "Notificación", JOptionPane.INFORMATION_MESSAGE);
		}
	
		private String crearTextoNotificacion(Notificacion notificacion) {
			StringBuilder texto = new StringBuilder();
	
			texto.append("Tipo: ").append(obtenerTipo(notificacion)).append("\n");
			texto.append("Fecha: ").append(formatearFecha(notificacion)).append("\n");
			texto.append("Vista: Sí").append("\n\n");
	
			texto.append(notificacion.getMensaje());
	
			return texto.toString();
		}
	
		private String obtenerTipo(Notificacion notificacion) {
			if (notificacion.getTipo() == null) {
				return "General";
			}
	
			return String.valueOf(notificacion.getTipo());
		}
	
		private String formatearFecha(Notificacion notificacion) {
			if (notificacion == null || notificacion.getFechaEnvio() == null) {
				return "-";
			}
	
			return notificacion.getFechaEnvio().toLocalDate() + " "
					+ notificacion.getFechaEnvio().toLocalTime().withNano(0);
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
	}