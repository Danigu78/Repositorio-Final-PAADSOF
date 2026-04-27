package Gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import productos.Producto2Mano;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;

/**
 * Sección para confirmar intercambios.
 * 
 * Muestra las ofertas de intercambio y permite al empleado confirmar las que ya
 * han sido aceptadas por los clientes.
 */
public class SeccionIntercambiosEmpleado extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaOfertas;
	private DefaultTableModel modeloOfertas;

	private JTextField campoIdOferta;
	private JTextArea areaInfoOferta;
	private JComboBox<String> comboEstado;

	public SeccionIntercambiosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Confirmación de Intercambios");
		JPanel contenido = getContenido(base);

		contenido.add(crearBloqueOfertas());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueConfirmar());

		add(base);
	}

	private JPanel crearBloqueOfertas() {
		JPanel bloque = crearBloque("Ofertas de intercambio");

		modeloOfertas = new DefaultTableModel(
				new String[] { "ID", "Origen", "Destino", "Estado", "Ofertados", "Solicitados" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaOfertas = new JTable(modeloOfertas);
		tablaOfertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaOfertas(tablaOfertas);

		areaInfoOferta = crearArea();
		areaInfoOferta.setEditable(false);

		comboEstado = crearCombo(crearOpcionesEstado());
		JButton botonRefrescar = crearBotonAccion("Refrescar ofertas");

		cargarTablaOfertas();

		tablaOfertas.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int fila = tablaOfertas.getSelectedRow();

			if (fila < 0) {
				return;
			}

			String idOferta = String.valueOf(tablaOfertas.getValueAt(fila, 0));

			if (campoIdOferta != null) {
				campoIdOferta.setText(idOferta);
			}

			mostrarInfoOferta(idOferta);
		});

		comboEstado.addActionListener(e -> {
			cargarTablaOfertas();
			areaInfoOferta.setText("");
		});

		botonRefrescar.addActionListener(e -> {
			cargarTablaOfertas();
			areaInfoOferta.setText("");
		});

		JScrollPane scrollTabla = estilizarScroll(tablaOfertas);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(260)));

		JScrollPane scrollInfo = estilizarScroll(areaInfoOferta);
		scrollInfo.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(210)));

		bloque.add(crearLabel("Filtrar por estado"), gbcCampo(1));
		bloque.add(comboEstado, gbcCampo(2));

		bloque.add(crearLabel("Selecciona una oferta para cargar su ID y ver sus productos."), gbcCampo(3));
		bloque.add(scrollTabla, gbcCampo(4));

		bloque.add(crearLabel("Información de la oferta seleccionada"), gbcCampo(5));
		bloque.add(scrollInfo, gbcCampo(6));

		bloque.add(botonRefrescar, gbcBoton(7));

		return bloque;
	}

	private JPanel crearBloqueConfirmar() {
		JPanel bloque = crearBloque("Confirmar intercambio");

		campoIdOferta = crearCampo();

		JButton botonConfirmar = crearBotonAccion("Confirmar intercambio");

		bloque.add(crearLabel("ID oferta"), gbcCampo(1));
		bloque.add(campoIdOferta, gbcCampo(2));
		bloque.add(crearLabel("Solo se pueden confirmar ofertas en estado ACEPTADA."), gbcCampo(3));
		bloque.add(botonConfirmar, gbcBoton(4));

		botonConfirmar.addActionListener(e -> {
			String idOferta = campoIdOferta.getText().trim();

			if (idOferta.isBlank()) {
				mostrarError("Introduce o selecciona un ID de oferta.");
				return;
			}

			Oferta oferta = buscarOfertaPorId(idOferta);

			if (oferta == null) {
				mostrarError("No existe ninguna oferta con ese ID.");
				return;
			}

			if (oferta.getEstado() != EstadoOferta.ACEPTADA) {
				mostrarError("La oferta debe estar en estado ACEPTADA para poder confirmarla.");
				return;
			}

			boolean ok = empleado.confirmarIntercambio(oferta);

			if (ok) {
				cargarTablaOfertas();
				mostrarInfoOferta(idOferta);
				mostrarMensaje("Intercambio confirmado correctamente.");
			} else {
				mostrarError("No se pudo confirmar el intercambio.");
			}
		});

		return bloque;
	}

	private String[] crearOpcionesEstado() {
		EstadoOferta[] estados = EstadoOferta.values();
		String[] opciones = new String[estados.length + 1];

		opciones[0] = "Todos";

		for (int i = 0; i < estados.length; i++) {
			opciones[i + 1] = estados[i].name();
		}

		return opciones;
	}

	private void cargarTablaOfertas() {
		modeloOfertas.setRowCount(0);

		String estadoFiltro = "Todos";

		if (comboEstado != null && comboEstado.getSelectedItem() != null) {
			estadoFiltro = String.valueOf(comboEstado.getSelectedItem());
		}

		for (Oferta oferta : obtenerTodasLasOfertas()) {
			oferta.haCaducado();

			if (!"Todos".equals(estadoFiltro) && !oferta.getEstado().name().equals(estadoFiltro)) {
				continue;
			}

			modeloOfertas.addRow(new Object[] { oferta.getId(), oferta.getOrigen().getNickname(),
					oferta.getDestino().getNickname(), oferta.getEstado(), oferta.getProductosOfertados().size(),
					oferta.getProductosSolicitados().size() });
		}
	}

	private List<Oferta> obtenerTodasLasOfertas() {
		List<Oferta> ofertas = new ArrayList<>();

		for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {
			for (Oferta oferta : cliente.getOfertasPendientes()) {
				if (!ofertas.contains(oferta)) {
					ofertas.add(oferta);
				}
			}

			for (Oferta oferta : cliente.getHistorialIntercambios()) {
				if (!ofertas.contains(oferta)) {
					ofertas.add(oferta);
				}
			}
		}

		for (Oferta oferta : Tienda.getInstancia().getIntercambiosFinalizados()) {
			if (!ofertas.contains(oferta)) {
				ofertas.add(oferta);
			}
		}

		return ofertas;
	}

	private Oferta buscarOfertaPorId(String idOferta) {
		if (idOferta == null || idOferta.isBlank()) {
			return null;
		}

		for (Oferta oferta : obtenerTodasLasOfertas()) {
			if (oferta.getId().equalsIgnoreCase(idOferta.trim())) {
				return oferta;
			}
		}

		return null;
	}

	private void mostrarInfoOferta(String idOferta) {
		Oferta oferta = buscarOfertaPorId(idOferta);

		if (oferta == null) {
			areaInfoOferta.setText("No existe ninguna oferta con ese ID.");
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Oferta: ").append(oferta.getId()).append("\n");
		sb.append("Estado: ").append(oferta.getEstado()).append("\n");
		sb.append("Fecha: ").append(oferta.getFechaOferta()).append("\n");
		sb.append("Origen: ").append(oferta.getOrigen().getNickname()).append("\n");
		sb.append("Destino: ").append(oferta.getDestino().getNickname()).append("\n\n");

		sb.append("Productos que ofrece ").append(oferta.getOrigen().getNickname()).append(":\n");
		añadirProductosOferta(sb, oferta.getProductosOfertados());

		sb.append("\nProductos que solicita a ").append(oferta.getDestino().getNickname()).append(":\n");
		añadirProductosOferta(sb, oferta.getProductosSolicitados());

		areaInfoOferta.setText(sb.toString());
		areaInfoOferta.setCaretPosition(0);
	}

	private void añadirProductosOferta(StringBuilder sb, List<Producto2Mano> productos) {
		if (productos == null || productos.isEmpty()) {
			sb.append("  Sin productos.\n");
			return;
		}

		for (Producto2Mano producto : productos) {
			sb.append("  - ").append(producto.getId()).append(" | ").append(producto.getNombre());

			if (producto.getValoracion() != null) {
				sb.append(" | valor: ").append(producto.getValoracion().getPrecioTasacion()).append(" € | estado: ")
						.append(producto.getValoracion().getEstadoProducto());
			} else {
				sb.append(" | sin valoración");
			}

			sb.append("\n");
		}
	}

	private void estilizarTablaOfertas(JTable tabla) {
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tabla.setRowHeight(VentanaPrincipal.escalar(30));
		tabla.setBackground(Color.WHITE);
		tabla.setForeground(VentanaPrincipal.COLOR_TEXTO);
		tabla.setGridColor(new Color(225, 225, 225));
		tabla.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
		tabla.setSelectionForeground(Color.BLACK);
		tabla.setFillsViewportHeight(true);

		JTableHeader header = tabla.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(232, 232, 232));
		header.setForeground(VentanaPrincipal.COLOR_TEXTO);
		header.setReorderingAllowed(false);
	}
}