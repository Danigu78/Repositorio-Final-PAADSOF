package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorPedidosEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import usuarios.Empleado;
import ventas.Pedido;

/**
 * Pantalla para que el empleado consulte y prepare pedidos.
 * 
 * La tabla solo sirve para ver los pedidos de la tienda. Para trabajar con un
 * pedido concreto, se escribe su ID en el campo inferior.
 */
public class SeccionPedidosEmpleado extends SeccionEmpleadoBase {

	private static final long serialVersionUID = 1L;

	private JTable tablaPedidos;
	private DefaultTableModel modeloPedidos;
	private TableRowSorter<DefaultTableModel> ordenadorPedidos;

	private JTextField campoIdPedido;
	private JComboBox<String> comboEstadoPedido;
	private ControladorPedidosEmpleado controlador;

	public SeccionPedidosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorPedidosEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorPedidosEmpleado) {
			this.controlador = (ControladorPedidosEmpleado) controlador;
		}
	}

	private void conectar(AbstractButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Gestión de Pedidos");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloquePedidos());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAccionesPedido());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloquePedidos() {
		JPanel bloque = crearBloque("Pedidos de la tienda");

		modeloPedidos = new DefaultTableModel(new String[] { "ID", "Cliente", "Estado", "Total", "Código recogida",
				"Recogida solicitada", "Productos" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaPedidos = new JTable(modeloPedidos);
		estilizarTablaPedidos(tablaPedidos);
		ordenadorPedidos = ponerOrdenacionTabla(tablaPedidos, modeloPedidos);

		/*
		 * Igual que en stock: la tabla es solo para consultar. No hace nada al pinchar,
		 * ni carga IDs automáticamente.
		 */
		tablaPedidos.setRowSelectionAllowed(false);
		tablaPedidos.setCellSelectionEnabled(false);

		comboEstadoPedido = crearCombo(crearOpcionesEstado());

		JButton botonRefrescar = crearBotonSecundario("Refrescar");
		conectar(botonRefrescar, ControladorPedidosEmpleado.REFRESCAR);

		comboEstadoPedido.setActionCommand(ControladorPedidosEmpleado.FILTRAR);
		comboEstadoPedido.addActionListener(controlador);

		JPanel filaFiltro = new JPanel(new BorderLayout(VentanaPrincipal.escalar(12), 0));
		filaFiltro.setOpaque(false);

		JPanel zonaCombo = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaCombo.setOpaque(false);
		zonaCombo.add(crearLabel("Filtrar por estado"), BorderLayout.NORTH);
		zonaCombo.add(comboEstadoPedido, BorderLayout.CENTER);

		JPanel zonaBusqueda = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaBusqueda.setOpaque(false);
		zonaBusqueda.add(crearLabel("Buscar"), BorderLayout.NORTH);
		zonaBusqueda.add(crearBuscadorTabla(ordenadorPedidos), BorderLayout.CENTER);

		JPanel zonaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		zonaBoton.setOpaque(false);
		zonaBoton.add(botonRefrescar);

		filaFiltro.add(zonaBusqueda, BorderLayout.WEST);
		filaFiltro.add(zonaCombo, BorderLayout.CENTER);
		filaFiltro.add(zonaBoton, BorderLayout.EAST);

		JScrollPane scrollTabla = estilizarScroll(tablaPedidos);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(260)));

		bloque.add(crearLabel("Consulta los pedidos. Para ver el detalle o prepararlo, escribe el ID abajo."),
				gbcCampo(1));
		bloque.add(filaFiltro, gbcCampo(2));
		bloque.add(scrollTabla, gbcCampo(3));

		cargarTablaPedidos();

		return bloque;
	}

	private JPanel crearBloqueAccionesPedido() {
		JPanel bloque = crearBloque("Consultar o preparar pedido");

		campoIdPedido = crearCampo();

		JPanel panelPedido = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelPedido.setOpaque(false);

		panelPedido.add(crearPanelDatosPedido());
		panelPedido.add(crearPanelBotonesPedido());

		bloque.add(panelPedido, gbcCampo(1));

		return bloque;
	}

	private JPanel crearPanelDatosPedido() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos del pedido"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JPanel filaCampo = new JPanel(new BorderLayout());
		filaCampo.setOpaque(false);
		filaCampo.add(crearCampoFormulario("ID pedido", campoIdPedido), BorderLayout.CENTER);

		panel.add(filaCampo);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));

		panel.add(crearLabel("Para prepararlo, el pedido debe estar en estado PAGADO."));

		return panel;
	}

	private JPanel crearPanelBotonesPedido() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

		panel.add(crearLabel("Acciones"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JButton botonVerPedido = crearBotonSecundario("Ver pedido");
		JButton botonPrepararPedido = crearBotonAccion("Preparar pedido");

		JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(12), 0));
		filaBotones.setOpaque(false);
		filaBotones.setMaximumSize(new Dimension(VentanaPrincipal.escalar(430), VentanaPrincipal.escalar(45)));

		filaBotones.add(botonVerPedido);
		filaBotones.add(botonPrepararPedido);

		panel.add(filaBotones);

		conectar(botonVerPedido, ControladorPedidosEmpleado.VER_PEDIDO);
		conectar(botonPrepararPedido, ControladorPedidosEmpleado.PREPARAR_PEDIDO);

		return panel;
	}

	private String[] crearOpcionesEstado() {
		return controlador.crearOpcionesEstado();
	}

	public void cargarTablaPedidos() {
		modeloPedidos.setRowCount(0);

		String estadoElegido = "Todos";

		if (comboEstadoPedido != null && comboEstadoPedido.getSelectedItem() != null) {
			estadoElegido = String.valueOf(comboEstadoPedido.getSelectedItem());
		}

		for (Pedido pedido : controlador.getPedidos(estadoElegido)) {
			modeloPedidos
					.addRow(new Object[] { pedido.getIdPedido(), pedido.getCliente().getNickname(), pedido.getEstado(),
							controlador.formatearPrecio(pedido.getTotal()), controlador.obtenerCodigoRecogida(pedido),
							controlador.obtenerTextoRecogidaSolicitada(pedido), pedido.getLineas().size() });
		}
	}

	public void verPedido() {
		String idPedido = campoIdPedido.getText().trim();

		if (idPedido.isBlank()) {
			mostrarError("Escribe el ID del pedido.");
			return;
		}

		Pedido pedido = buscarPedidoPorId(idPedido);

		if (pedido == null) {
			mostrarError("No existe ningún pedido con ese ID.");
			return;
		}

		mostrarPedidoEnVentana(pedido);
	}

	public void prepararPedido() {
		ResultadoOperacion resultado = controlador.prepararPedido(campoIdPedido.getText());

		if (resultado.isExito()) {
			cargarTablaPedidos();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private Pedido buscarPedidoPorId(String idPedido) {
		return controlador.buscarPedidoPorId(idPedido);
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
		return controlador.crearTextoPedido(pedido);
	}

	private void estilizarTablaPedidos(JTable tabla) {
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

		tabla.getColumnModel().getColumn(0).setPreferredWidth(110); // ID
		tabla.getColumnModel().getColumn(1).setPreferredWidth(140); // Cliente
		tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Estado
		tabla.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
		tabla.getColumnModel().getColumn(4).setPreferredWidth(160); // Código recogida
		tabla.getColumnModel().getColumn(5).setPreferredWidth(160); // Recogida solicitada
		tabla.getColumnModel().getColumn(6).setPreferredWidth(90); // Productos
	}
}
