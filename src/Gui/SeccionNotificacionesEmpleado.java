package Gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import tienda.Notificacion;
import usuarios.Empleado;

/**
 * Sección de notificaciones del empleado.
 * 
 * Permite ver las notificaciones, filtrarlas por leídas/no leídas y marcarlas
 * como leídas.
 */
public class SeccionNotificacionesEmpleado extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaNotificaciones;
	private DefaultTableModel modeloNotificaciones;
	private JTextArea areaDetalle;
	private JComboBox<String> comboFiltro;

	public SeccionNotificacionesEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Notificaciones");
		JPanel contenido = getContenido(base);

		contenido.add(crearBloqueNotificaciones());

		add(base);
	}

	private JPanel crearBloqueNotificaciones() {
		JPanel bloque = crearBloque("Mis notificaciones");

		modeloNotificaciones = new DefaultTableModel(new String[] { "ID", "Tipo", "Leída", "Fecha", "Mensaje" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaNotificaciones = new JTable(modeloNotificaciones);
		tablaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaNotificaciones(tablaNotificaciones);

		areaDetalle = crearArea();
		areaDetalle.setEditable(false);

		comboFiltro = crearCombo(new String[] { "Todas", "No leídas", "Leídas" });

		JButton botonRefrescar = crearBotonAccion("Refrescar");
		JButton botonMarcarLeida = crearBotonSecundario("Marcar seleccionada como leída");
		JButton botonMarcarTodas = crearBotonSecundario("Marcar todas como leídas");

		cargarTablaNotificaciones();

		tablaNotificaciones.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int fila = tablaNotificaciones.getSelectedRow();

			if (fila < 0) {
				return;
			}

			String id = String.valueOf(tablaNotificaciones.getValueAt(fila, 0));
			mostrarDetalleNotificacion(id);
		});

		comboFiltro.addActionListener(e -> {
			cargarTablaNotificaciones();
			areaDetalle.setText("");
		});

		botonRefrescar.addActionListener(e -> {
			cargarTablaNotificaciones();
			areaDetalle.setText("");
		});

		botonMarcarLeida.addActionListener(e -> {
			int fila = tablaNotificaciones.getSelectedRow();

			if (fila < 0) {
				mostrarError("Selecciona una notificación.");
				return;
			}

			String id = String.valueOf(tablaNotificaciones.getValueAt(fila, 0));
			Notificacion notificacion = buscarNotificacionPorId(id);

			if (notificacion == null) {
				mostrarError("No se encontró la notificación.");
				return;
			}

			notificacion.marcarComoLeida();
			cargarTablaNotificaciones();
			mostrarDetalleNotificacion(id);
			mostrarMensaje("Notificación marcada como leída.");
		});

		botonMarcarTodas.addActionListener(e -> {
			if (empleado.getNotificaciones() == null || empleado.getNotificaciones().isEmpty()) {
				mostrarError("No hay notificaciones.");
				return;
			}

			for (Notificacion n : empleado.getNotificaciones()) {
				if (n != null) {
					n.marcarComoLeida();
				}
			}

			cargarTablaNotificaciones();
			areaDetalle.setText("");
			mostrarMensaje("Todas las notificaciones se han marcado como leídas.");
		});

		JScrollPane scrollTabla = estilizarScroll(tablaNotificaciones);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(280)));

		JScrollPane scrollDetalle = estilizarScroll(areaDetalle);
		scrollDetalle.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(170)));

		JPanel filaBotones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonRefrescar);
		filaBotones.add(botonMarcarLeida);
		filaBotones.add(botonMarcarTodas);

		bloque.add(crearLabel("Filtrar notificaciones"), gbcCampo(1));
		bloque.add(comboFiltro, gbcCampo(2));

		bloque.add(crearLabel("Listado de notificaciones"), gbcCampo(3));
		bloque.add(scrollTabla, gbcCampo(4));

		bloque.add(crearLabel("Detalle de la notificación seleccionada"), gbcCampo(5));
		bloque.add(scrollDetalle, gbcCampo(6));

		bloque.add(filaBotones, gbcBoton(7));

		return bloque;
	}

	private void cargarTablaNotificaciones() {
		modeloNotificaciones.setRowCount(0);

		List<Notificacion> notificaciones = empleado.getNotificaciones();

		if (notificaciones == null) {
			return;
		}

		String filtro = "Todas";

		if (comboFiltro != null && comboFiltro.getSelectedItem() != null) {
			filtro = String.valueOf(comboFiltro.getSelectedItem());
		}

		for (Notificacion n : notificaciones) {
			if (n == null) {
				continue;
			}

			if ("No leídas".equals(filtro) && n.isLeida()) {
				continue;
			}

			if ("Leídas".equals(filtro) && !n.isLeida()) {
				continue;
			}

			modeloNotificaciones.addRow(new Object[] { n.getId(), n.getTipo(), n.isLeida() ? "Sí" : "No",
					n.getFechaEnvio().toLocalDate() + " " + n.getFechaEnvio().toLocalTime().withNano(0),
					n.getMensaje() });
		}
	}

	private Notificacion buscarNotificacionPorId(String id) {
		if (id == null || empleado.getNotificaciones() == null) {
			return null;
		}

		for (Notificacion n : empleado.getNotificaciones()) {
			if (n != null && n.getId().equals(id)) {
				return n;
			}
		}

		return null;
	}

	private void mostrarDetalleNotificacion(String id) {
		Notificacion n = buscarNotificacionPorId(id);

		if (n == null) {
			areaDetalle.setText("No se encontró la notificación.");
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("ID: ").append(n.getId()).append("\n");
		sb.append("Tipo: ").append(n.getTipo()).append("\n");
		sb.append("Leída: ").append(n.isLeida() ? "Sí" : "No").append("\n");
		sb.append("Fecha: ").append(n.getFechaEnvio().toLocalDate()).append(" ")
				.append(n.getFechaEnvio().toLocalTime().withNano(0)).append("\n\n");
		sb.append("Mensaje:\n");
		sb.append(n.getMensaje());

		areaDetalle.setText(sb.toString());
		areaDetalle.setCaretPosition(0);
	}

	private void estilizarTablaNotificaciones(JTable tabla) {
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tabla.setRowHeight(VentanaPrincipal.escalar(30));
		tabla.setBackground(Color.WHITE);
		tabla.setForeground(VentanaPrincipal.COLOR_TEXTO);
		tabla.setGridColor(new Color(225, 225, 225));
		tabla.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
		tabla.setSelectionForeground(Color.BLACK);
		tabla.setFillsViewportHeight(true);

		tabla.getColumnModel().getColumn(0).setPreferredWidth(90);
		tabla.getColumnModel().getColumn(1).setPreferredWidth(140);
		tabla.getColumnModel().getColumn(2).setPreferredWidth(70);
		tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
		tabla.getColumnModel().getColumn(4).setPreferredWidth(520);

		JTableHeader header = tabla.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(232, 232, 232));
		header.setForeground(VentanaPrincipal.COLOR_TEXTO);
		header.setReorderingAllowed(false);
	}
}