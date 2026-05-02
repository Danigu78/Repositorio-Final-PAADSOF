package Gui;

import java.awt.*;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;

/**
 * Pantalla para entregar pedidos.
 * 
 * Muestra los pedidos que ya están listos para recoger. Para entregar uno, el
 * empleado escribe el código de recogida y pulsa el botón correspondiente.
 */
public class SeccionEntregasEmpleado extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaPedidosListos;
	private DefaultTableModel modeloPedidosListos;

	private JTextField campoCodigoRecogida;

	public SeccionEntregasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Entrega de Pedidos");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloquePedidosListos());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueEntrega());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloquePedidosListos() {
		JPanel bloque = crearBloque("Pedidos listos para recoger");

		modeloPedidosListos = new DefaultTableModel(
				new String[] { "ID", "Cliente", "Código recogida", "Recogida solicitada", "Total", "Productos" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaPedidosListos = new JTable(modeloPedidosListos);
		estilizarTablaEntregas(tablaPedidosListos);

		/*
		 * La tabla es solo para mirar. El empleado escribe el código abajo cuando
		 * quiera consultar o entregar un pedido.
		 */
		tablaPedidosListos.setRowSelectionAllowed(false);
		tablaPedidosListos.setCellSelectionEnabled(false);

		JScrollPane scrollTabla = estilizarScroll(tablaPedidosListos);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(240)));

		JButton botonRefrescar = crearBotonSecundario("Refrescar");
		botonRefrescar.addActionListener(e -> cargarTablaEntregas());

		bloque.add(crearLabel("Consulta los pedidos preparados. Para entregarlo, escribe el código abajo."),
				gbcCampo(1));
		bloque.add(scrollTabla, gbcCampo(2));
		bloque.add(botonRefrescar, gbcBoton(3));

		cargarTablaEntregas();

		return bloque;
	}

	private JPanel crearBloqueEntrega() {
		JPanel bloque = crearBloque("Consultar o entregar pedido");

		campoCodigoRecogida = crearCampo();

		JPanel panelEntrega = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelEntrega.setOpaque(false);

		panelEntrega.add(crearPanelDatosEntrega());
		panelEntrega.add(crearPanelBotonesEntrega());

		bloque.add(panelEntrega, gbcCampo(1));

		return bloque;
	}

	private JPanel crearPanelDatosEntrega() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Datos de entrega");
		titulo.setAlignmentX(0.0f);
		panel.add(titulo);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JPanel filaCodigo = crearCampoFormulario("Código de recogida", campoCodigoRecogida);
		filaCodigo.setAlignmentX(0.0f);
		panel.add(filaCodigo);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));

		JLabel ayuda = crearLabel("Solo se puede entregar si el cliente ya ha solicitado la recogida.");
		ayuda.setAlignmentX(0.0f);
		panel.add(ayuda);

		return panel;
	}

	private JPanel crearPanelBotonesEntrega() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Acciones");
		titulo.setAlignmentX(0.0f);
		panel.add(titulo);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JButton botonVerPedido = crearBotonSecundario("Ver pedido");
		JButton botonEntregarPedido = crearBotonAccion("Entregar pedido");

		ajustarBotonAccion(botonVerPedido);
		ajustarBotonAccion(botonEntregarPedido);

		JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(10), 0));
		filaBotones.setOpaque(false);
		filaBotones.setAlignmentX(0.0f);

		filaBotones.add(botonVerPedido);
		filaBotones.add(botonEntregarPedido);

		Dimension tamanoFila = new Dimension(VentanaPrincipal.escalar(430), VentanaPrincipal.escalar(42));
		filaBotones.setPreferredSize(tamanoFila);
		filaBotones.setMaximumSize(tamanoFila);

		panel.add(filaBotones);

		botonVerPedido.addActionListener(e -> verPedido());
		botonEntregarPedido.addActionListener(e -> entregarPedido());

		return panel;
	}

	private void ajustarBotonAccion(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(210), VentanaPrincipal.escalar(42));

		boton.setPreferredSize(tamano);
		boton.setMinimumSize(tamano);
		boton.setMaximumSize(tamano);
	}

	private void cargarTablaEntregas() {
		modeloPedidosListos.setRowCount(0);

		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (pedido.getEstado() != EstadoPedido.LISTO_PARA_RECOGER) {
				continue;
			}

			modeloPedidosListos.addRow(new Object[] { pedido.getIdPedido(), pedido.getCliente().getNickname(),
					obtenerCodigoRecogida(pedido), pedido.isRecogida_solicitada() ? "Sí" : "No",
					formatearPrecio(pedido.getTotal()), pedido.getLineas().size() });
		}
	}

	private void verPedido() {
		String codigo = campoCodigoRecogida.getText().trim();

		if (codigo.isBlank()) {
			mostrarError("Escribe el código de recogida.");
			return;
		}

		Pedido pedido = buscarPedidoPorCodigo(codigo);

		if (pedido == null) {
			mostrarError("No existe ningún pedido con ese código.");
			return;
		}

		mostrarPedidoEnVentana(pedido);
	}

	private void entregarPedido() {
		String codigo = campoCodigoRecogida.getText().trim();

		if (codigo.isBlank()) {
			mostrarError("Escribe el código de recogida.");
			return;
		}

		Pedido pedido = buscarPedidoPorCodigo(codigo);

		if (pedido == null) {
			mostrarError("No existe ningún pedido con ese código.");
			return;
		}

		if (pedido.getEstado() != EstadoPedido.LISTO_PARA_RECOGER) {
			mostrarError("Este pedido no está listo para recoger.");
			return;
		}

		if (!pedido.isRecogida_solicitada()) {
			mostrarError("El cliente todavía no ha solicitado la recogida.");
			return;
		}

		boolean entregado = empleado.entregarPedido(codigo);

		if (entregado) {
			cargarTablaEntregas();
			campoCodigoRecogida.setText("");
			mostrarMensaje("Pedido entregado correctamente.");
		} else {
			mostrarError("No se pudo entregar el pedido.");
		}
	}

	private Pedido buscarPedidoPorCodigo(String codigo) {
		if (codigo == null || codigo.isBlank()) {
			return null;
		}

		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (pedido.getCodigoRecogida() != null && pedido.getCodigoRecogida().equalsIgnoreCase(codigo.trim())) {
				return pedido;
			}
		}

		return null;
	}

	private void mostrarPedidoEnVentana(Pedido pedido) {
		JTextArea areaPedido = crearArea();
		areaPedido.setEditable(false);
		areaPedido.setText(crearTextoPedido(pedido));
		areaPedido.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(areaPedido);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(680), VentanaPrincipal.escalar(320)));

		JOptionPane.showMessageDialog(this, scroll, "Información del pedido", JOptionPane.INFORMATION_MESSAGE);
	}

	private String crearTextoPedido(Pedido pedido) {
		StringBuilder texto = new StringBuilder();

		texto.append("Pedido: ").append(pedido.getIdPedido()).append("\n");
		texto.append("Cliente: ").append(pedido.getCliente().getNickname()).append("\n");
		texto.append("Estado: ").append(pedido.getEstado()).append("\n");
		texto.append("Total: ").append(formatearPrecio(pedido.getTotal())).append("\n");
		texto.append("Código recogida: ").append(obtenerCodigoRecogida(pedido)).append("\n");
		texto.append("Recogida solicitada: ").append(pedido.isRecogida_solicitada() ? "Sí" : "No").append("\n");

		if (pedido.getFechaPreparado() != null) {
			texto.append("Fecha preparado: ").append(pedido.getFechaPreparado()).append("\n");
		}

		if (pedido.getFechaEntregado() != null) {
			texto.append("Fecha entregado: ").append(pedido.getFechaEntregado()).append("\n");
		}

		texto.append("\nProductos del pedido:\n");
		texto.append(crearTextoProductosPedido(pedido));

		return texto.toString();
	}

	private String crearTextoProductosPedido(Pedido pedido) {
		StringBuilder texto = new StringBuilder();

		if (pedido.getLineas().isEmpty()) {
			texto.append("Sin productos.");
			return texto.toString();
		}

		for (LineaPedido linea : pedido.getLineas()) {
			ProductoVenta producto = linea.getProducto();

			texto.append("- ");
			texto.append(producto.getId()).append(" | ");
			texto.append(producto.getNombre()).append(" | ");
			texto.append("cantidad: ").append(linea.getCantidad()).append(" | ");
			texto.append("precio unidad: ").append(formatearPrecio(linea.getPrecioVenta())).append(" | ");
			texto.append("subtotal: ").append(formatearPrecio(linea.getSubtotal()));
			texto.append("\n");
		}

		return texto.toString();
	}

	private String obtenerCodigoRecogida(Pedido pedido) {
		if (pedido.getCodigoRecogida() == null || pedido.getCodigoRecogida().isBlank()) {
			return "-";
		}

		return pedido.getCodigoRecogida();
	}

	private String formatearPrecio(double precio) {
		return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
	}

	private void estilizarTablaEntregas(JTable tabla) {
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tabla.setRowHeight(VentanaPrincipal.escalar(28));
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		tabla.setBackground(Color.WHITE);
		tabla.setForeground(Color.BLACK);
		tabla.setGridColor(new Color(225, 225, 225));

		tabla.setFillsViewportHeight(true);
		tabla.setShowHorizontalLines(true);
		tabla.setShowVerticalLines(true);

		JTableHeader cabecera = tabla.getTableHeader();
		cabecera.setFont(new Font("Segoe UI", Font.BOLD, 13));
		cabecera.setBackground(new Color(235, 235, 235));
		cabecera.setForeground(Color.BLACK);
		cabecera.setReorderingAllowed(false);

		tabla.getColumnModel().getColumn(0).setPreferredWidth(130);
		tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
		tabla.getColumnModel().getColumn(2).setPreferredWidth(180);
		tabla.getColumnModel().getColumn(3).setPreferredWidth(160);
		tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
		tabla.getColumnModel().getColumn(5).setPreferredWidth(90);
	}
}