package Gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JButton;
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
 * Sección para entregar pedidos.
 * 
 * Muestra los pedidos listos para recoger y permite entregarlos usando el
 * código de recogida.
 */
public class SeccionEntregasEmpleado extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaEntregas;
	private DefaultTableModel modeloEntregas;

	private JTextField campoCodigo;
	private JTextArea areaInfoPedido;

	public SeccionEntregasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Entrega de Pedidos");
		JPanel contenido = getContenido(base);

		contenido.add(crearBloquePedidosListos());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueEntregarPedido());

		add(base);
	}

	private JPanel crearBloquePedidosListos() {
		JPanel bloque = crearBloque("Pedidos listos para recoger");

		modeloEntregas = new DefaultTableModel(
				new String[] { "ID", "Cliente", "Código recogida", "Recogida solicitada", "Total", "Líneas" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaEntregas = new JTable(modeloEntregas);
		tablaEntregas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaEntregas(tablaEntregas);

		areaInfoPedido = crearArea();
		areaInfoPedido.setEditable(false);

		JButton botonRefrescar = crearBotonAccion("Refrescar entregas");

		cargarTablaEntregas();

		tablaEntregas.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int fila = tablaEntregas.getSelectedRow();

			if (fila < 0) {
				return;
			}

			String idPedido = String.valueOf(tablaEntregas.getValueAt(fila, 0));
			String codigo = String.valueOf(tablaEntregas.getValueAt(fila, 2));

			if (campoCodigo != null && !codigo.equals("-")) {
				campoCodigo.setText(codigo);
			}

			mostrarInfoPedido(idPedido);
		});

		botonRefrescar.addActionListener(e -> {
			cargarTablaEntregas();
			areaInfoPedido.setText("");
		});

		JScrollPane scrollTabla = estilizarScroll(tablaEntregas);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(240)));

		JScrollPane scrollInfo = estilizarScroll(areaInfoPedido);
		scrollInfo.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(180)));

		bloque.add(crearLabel("Selecciona un pedido para cargar su código de recogida."), gbcCampo(1));
		bloque.add(scrollTabla, gbcCampo(2));

		bloque.add(crearLabel("Información del pedido seleccionado"), gbcCampo(3));
		bloque.add(scrollInfo, gbcCampo(4));

		bloque.add(botonRefrescar, gbcBoton(5));

		return bloque;
	}

	private JPanel crearBloqueEntregarPedido() {
		JPanel bloque = crearBloque("Entregar pedido");

		campoCodigo = crearCampo();

		JButton botonEntregar = crearBotonAccion("Entregar pedido");

		bloque.add(crearLabel("Código de recogida"), gbcCampo(1));
		bloque.add(campoCodigo, gbcCampo(2));
		bloque.add(crearLabel("Solo se pueden entregar pedidos listos para recoger y con recogida solicitada."),
				gbcCampo(3));
		bloque.add(botonEntregar, gbcBoton(4));

		botonEntregar.addActionListener(e -> {
			String codigo = campoCodigo.getText().trim();

			if (codigo.isBlank()) {
				mostrarError("Introduce o selecciona un código de recogida.");
				return;
			}

			Pedido pedido = buscarPedidoPorCodigo(codigo);

			if (pedido == null) {
				mostrarError("No existe ningún pedido con ese código de recogida.");
				return;
			}

			if (!pedido.isRecogida_solicitada()) {
				mostrarError("El cliente todavía no ha solicitado la recogida del pedido.");
				return;
			}

			boolean ok = empleado.entregarPedido(codigo);

			if (ok) {
				cargarTablaEntregas();
				areaInfoPedido.setText("Pedido entregado correctamente:\n" + pedido.getIdPedido());
				campoCodigo.setText("");
				mostrarMensaje("Pedido entregado correctamente.");
			} else {
				mostrarError("No se pudo entregar el pedido.");
			}
		});

		return bloque;
	}

	private void cargarTablaEntregas() {
		modeloEntregas.setRowCount(0);

		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (pedido.getEstado() != EstadoPedido.LISTO_PARA_RECOGER) {
				continue;
			}

			String codigo = pedido.getCodigoRecogida();

			if (codigo == null) {
				codigo = "-";
			}

			modeloEntregas.addRow(new Object[] { pedido.getIdPedido(), pedido.getCliente().getNickname(), codigo,
					pedido.isRecogida_solicitada() ? "Sí" : "No",
					String.format(java.util.Locale.US, "%.2f €", pedido.getTotal()).replace('.', ','),
					pedido.getLineas().size() });
		}
	}

	private Pedido buscarPedidoPorCodigo(String codigo) {
		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (pedido.getCodigoRecogida() != null && pedido.getCodigoRecogida().equals(codigo)) {
				return pedido;
			}
		}

		return null;
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

	private void estilizarTablaEntregas(JTable tabla) {
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
