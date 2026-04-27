package Gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

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

import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;

/**
 * Sección para gestionar pedidos. Permite consultar pedidos de la tienda,
 * filtrarlos por estado y marcar como preparados los pedidos pagados.
 */
public class SeccionPedidosEmpleado extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaPedidos;
	private DefaultTableModel modeloPedidos;

	private JTextField campoPedido;
	private JTextArea areaInfoPedido;
	private JComboBox<String> comboEstado;

	public SeccionPedidosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Gestión de Pedidos");
		JPanel contenido = getContenido(base);

		contenido.add(crearBloquePedidos());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloquePrepararPedido());

		add(base);
	}

	private JPanel crearBloquePedidos() {
		JPanel bloque = crearBloque("Pedidos de la tienda");

		modeloPedidos = new DefaultTableModel(
				new String[] { "ID", "Cliente", "Estado", "Total", "Código recogida", "Líneas" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaPedidos = new JTable(modeloPedidos);
		tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaPedidos(tablaPedidos);

		areaInfoPedido = crearArea();
		areaInfoPedido.setEditable(false);

		comboEstado = crearCombo(crearOpcionesEstado());
		JButton botonRefrescar = crearBotonAccion("Refrescar pedidos");

		cargarTablaPedidos();

		tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int fila = tablaPedidos.getSelectedRow();

			if (fila < 0) {
				return;
			}

			String idPedido = String.valueOf(tablaPedidos.getValueAt(fila, 0));

			if (campoPedido != null) {
				campoPedido.setText(idPedido);
			}

			mostrarInfoPedido(idPedido);
		});

		comboEstado.addActionListener(e -> {
			cargarTablaPedidos();
			areaInfoPedido.setText("");
		});

		botonRefrescar.addActionListener(e -> {
			cargarTablaPedidos();
			areaInfoPedido.setText("");
		});

		JScrollPane scrollTabla = estilizarScroll(tablaPedidos);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(260)));

		JScrollPane scrollInfo = estilizarScroll(areaInfoPedido);
		scrollInfo.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(180)));

		bloque.add(crearLabel("Filtrar por estado"), gbcCampo(1));
		bloque.add(comboEstado, gbcCampo(2));

		bloque.add(crearLabel("Selecciona un pedido para cargar su ID y ver sus productos."), gbcCampo(3));
		bloque.add(scrollTabla, gbcCampo(4));

		bloque.add(crearLabel("Información del pedido seleccionado"), gbcCampo(5));
		bloque.add(scrollInfo, gbcCampo(6));

		bloque.add(botonRefrescar, gbcBoton(7));

		return bloque;
	}

	private JPanel crearBloquePrepararPedido() {
		JPanel bloque = crearBloque("Preparar pedido");

		campoPedido = crearCampo();

		JButton botonPreparar = crearBotonAccion("Preparar pedido");

		bloque.add(crearLabel("ID pedido"), gbcCampo(1));
		bloque.add(campoPedido, gbcCampo(2));
		bloque.add(crearLabel("Solo se pueden preparar pedidos que estén en estado PAGADO."), gbcCampo(3));
		bloque.add(botonPreparar, gbcBoton(4));

		botonPreparar.addActionListener(e -> {
			String id = campoPedido.getText().trim();

			if (id.isBlank()) {
				mostrarError("Introduce o selecciona un ID de pedido.");
				return;
			}

			Pedido pedido = buscarPedidoPorId(id);

			if (pedido == null) {
				mostrarError("No existe ningún pedido con ese ID.");
				return;
			}

			boolean ok = empleado.prepararPedido(id);

			if (ok) {
				cargarTablaPedidos();
				mostrarInfoPedido(id);
				mostrarMensaje("Pedido preparado correctamente.");
			} else {
				mostrarError("No se pudo preparar el pedido. Comprueba que esté pagado.");
			}
		});

		return bloque;
	}

	private String[] crearOpcionesEstado() {
		EstadoPedido[] estados = EstadoPedido.values();
		String[] opciones = new String[estados.length + 1];

		opciones[0] = "Todos";

		for (int i = 0; i < estados.length; i++) {
			opciones[i + 1] = estados[i].name();
		}

		return opciones;
	}

	private void cargarTablaPedidos() {
		modeloPedidos.setRowCount(0);

		String estadoFiltro = "Todos";

		if (comboEstado != null && comboEstado.getSelectedItem() != null) {
			estadoFiltro = String.valueOf(comboEstado.getSelectedItem());
		}

		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (!"Todos".equals(estadoFiltro) && !pedido.getEstado().name().equals(estadoFiltro)) {
				continue;
			}

			String codigo = pedido.getCodigoRecogida();

			if (codigo == null) {
				codigo = "-";
			}

			modeloPedidos
					.addRow(new Object[] { pedido.getIdPedido(), pedido.getCliente().getNickname(), pedido.getEstado(),
							String.format(java.util.Locale.US, "%.2f €", pedido.getTotal()).replace('.', ','), codigo,
							pedido.getLineas().size() });
		}
	}

	private Pedido buscarPedidoPorId(String idPedido) {
		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (pedido.getIdPedido().equals(idPedido)) {
				return pedido;
			}
		}

		return null;
	}

	private void mostrarInfoPedido(String idPedido) {
		Pedido pedido = buscarPedidoPorId(idPedido);

		if (pedido == null) {
			areaInfoPedido.setText("No existe ningún pedido con ese ID.");
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Pedido: ").append(pedido.getIdPedido()).append("\n");
		sb.append("Cliente: ").append(pedido.getCliente().getNickname()).append("\n");
		sb.append("Estado: ").append(pedido.getEstado()).append("\n");
		sb.append("Total: ").append(pedido.getTotal()).append(" €\n");
		sb.append("Fecha creación: ").append(pedido.getFechaCreacion()).append("\n");

		if (pedido.getFechaPreparado() != null) {
			sb.append("Fecha preparado: ").append(pedido.getFechaPreparado()).append("\n");
		}

		if (pedido.getFechaEntregado() != null) {
			sb.append("Fecha entregado: ").append(pedido.getFechaEntregado()).append("\n");
		}

		if (pedido.getCodigoRecogida() != null) {
			sb.append("Código recogida: ").append(pedido.getCodigoRecogida()).append("\n");
		} else {
			sb.append("Código recogida: -\n");
		}

		sb.append("Recogida solicitada: ");
		sb.append(pedido.isRecogida_solicitada() ? "Sí" : "No");
		sb.append("\n\n");

		sb.append("Productos del pedido:\n");

		if (pedido.getLineas().isEmpty()) {
			sb.append("  Sin productos.\n");
		} else {
			for (LineaPedido linea : pedido.getLineas()) {
				ProductoVenta producto = linea.getProducto();

				sb.append("  - ").append(producto.getId()).append(" | ").append(producto.getNombre())
						.append(" | cantidad: ").append(linea.getCantidad()).append(" | precio unidad: ")
						.append(linea.getPrecioVenta()).append(" € | subtotal: ").append(linea.getSubtotal())
						.append(" €\n");
			}
		}

		areaInfoPedido.setText(sb.toString());
		areaInfoPedido.setCaretPosition(0);
	}

	private void estilizarTablaPedidos(JTable tabla) {
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