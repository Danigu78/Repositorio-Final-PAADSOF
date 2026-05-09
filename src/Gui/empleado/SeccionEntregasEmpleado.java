package Gui.empleado;


import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorEntregasEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


import usuarios.Empleado;
import ventas.Pedido;

/**
 * Pantalla para entregar pedidos.
 * 
 * Muestra los pedidos que ya están listos para recoger. Para entregar uno, el
 * empleado escribe el código de recogida y pulsa el botón correspondiente.
 */
public class SeccionEntregasEmpleado extends SeccionEmpleadoBase {

	private static final long serialVersionUID = 1L;

	private JTable tablaPedidosListos;
	private DefaultTableModel modeloPedidosListos;

	private JTextField campoCodigoRecogida;
	private ControladorEntregasEmpleado controlador;

	public SeccionEntregasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorEntregasEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorEntregasEmpleado) {
			this.controlador = (ControladorEntregasEmpleado) controlador;
		}
	}

	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
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
		conectar(botonRefrescar, ControladorEntregasEmpleado.REFRESCAR);

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

		conectar(botonVerPedido, ControladorEntregasEmpleado.VER_PEDIDO);
		conectar(botonEntregarPedido, ControladorEntregasEmpleado.ENTREGAR_PEDIDO);

		return panel;
	}

	private void ajustarBotonAccion(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(210), VentanaPrincipal.escalar(42));

		boton.setPreferredSize(tamano);
		boton.setMinimumSize(tamano);
		boton.setMaximumSize(tamano);
	}

	public void cargarTablaEntregas() {
		modeloPedidosListos.setRowCount(0);

		for (Pedido pedido : controlador.getPedidosListosParaRecoger()) {
			modeloPedidosListos.addRow(new Object[] { pedido.getIdPedido(), pedido.getCliente().getNickname(),
					controlador.obtenerCodigoRecogida(pedido), pedido.isRecogida_solicitada() ? "Sí" : "No",
					controlador.formatearPrecio(pedido.getTotal()), pedido.getLineas().size() });
		}
	}

	public void verPedido() {
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

	public void entregarPedido() {
		ResultadoOperacion resultado = controlador.entregarPedido(campoCodigoRecogida.getText());

		if (resultado.isExito()) {
			cargarTablaEntregas();
			campoCodigoRecogida.setText("");
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private Pedido buscarPedidoPorCodigo(String codigo) {
		return controlador.buscarPedidoPorCodigo(codigo);
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
