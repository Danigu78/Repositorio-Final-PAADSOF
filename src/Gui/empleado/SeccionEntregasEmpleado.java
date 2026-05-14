package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorEntregasEmpleado;
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
 * Pantalla para entregar pedidos.
 * 
 * @author Lucas
 * @version 1.0
 */
public class SeccionEntregasEmpleado extends SeccionEmpleadoBase {

	private static final long serialVersionUID = 1L;

	/** Tabla con los pedidos listos para recoger. */
	private JTable tablaPedidosListos;
	
	/** Modelo de datos de la tabla de pedidos listos. */
	private DefaultTableModel modeloPedidosListos;
	
	/** Ordenador y filtro de la tabla de pedidos listos. */
	private TableRowSorter<DefaultTableModel> ordenadorPedidosListos;

	/** Campo donde se introduce el código de recogida. */
	private JTextField campoCodigoRecogida;
	
	/** Controlador de la sección de entregas. */
	private ControladorEntregasEmpleado controlador;

	/**
	 * Constructor de la sección de entregas.
	 * 
	 * @param ventana ventana principal de la aplicación
	 * @param empleado empleado autenticado
	 */
	public SeccionEntregasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorEntregasEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	/**
	 * Asigna el controlador de la vista.
	 * 
	 * @param controlador controlador de entregas
	 */
	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorEntregasEmpleado) {
			this.controlador = (ControladorEntregasEmpleado) controlador;
		}
	}

	/**
	 * Conecta un botón con una acción del controlador.
	 * 
	 * @param boton botón a conectar
	 * @param accion comando de acción
	 */
	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	/**
	 * Construye toda la interfaz gráfica de la sección.
	 */
	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Entrega de Pedidos");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloquePedidosListos());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueEntrega());

		add(panelBase, BorderLayout.CENTER);
	}

	/**
	 * Crea el bloque de pedidos listos para recoger.
	 * 
	 * @return panel con la tabla de pedidos
	 */
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
		ordenadorPedidosListos = ponerOrdenacionTabla(tablaPedidosListos, modeloPedidosListos);

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

		JPanel filaBusqueda = new JPanel(new BorderLayout(VentanaPrincipal.escalar(12), 0));
		filaBusqueda.setOpaque(false);

		JPanel zonaBusqueda = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaBusqueda.setOpaque(false);
		zonaBusqueda.add(crearLabel("Buscar"), BorderLayout.NORTH);
		zonaBusqueda.add(crearBuscadorTabla(ordenadorPedidosListos), BorderLayout.CENTER);

		JPanel zonaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		zonaBoton.setOpaque(false);
		zonaBoton.add(botonRefrescar);

		filaBusqueda.add(zonaBusqueda, BorderLayout.WEST);
		filaBusqueda.add(zonaBoton, BorderLayout.EAST);

		bloque.add(crearLabel("Consulta los pedidos preparados. Para entregarlo, escribe el código abajo."),
				gbcCampo(1));
		bloque.add(filaBusqueda, gbcCampo(2));
		bloque.add(scrollTabla, gbcCampo(3));

		cargarTablaEntregas();

		return bloque;
	}

	/**
	 * Crea el bloque de acciones de entrega.
	 * 
	 * @return panel de entrega
	 */
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

	/**
	 * Crea el panel de datos de entrega.
	 * 
	 * @return panel con los campos
	 */
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

	/**
	 * Crea el panel de botones de acción.
	 * 
	 * @return panel de botones
	 */
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

	/**
	 * Ajusta el tamaño de un botón de acción.
	 * 
	 * @param boton botón a ajustar
	 */
	private void ajustarBotonAccion(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(210), VentanaPrincipal.escalar(42));

		boton.setPreferredSize(tamano);
		boton.setMinimumSize(tamano);
		boton.setMaximumSize(tamano);
	}

	/**
	 * Carga la tabla de pedidos listos para recoger.
	 */
	public void cargarTablaEntregas() {
		modeloPedidosListos.setRowCount(0);

		for (Pedido pedido : controlador.getPedidosListosParaRecoger()) {
			modeloPedidosListos.addRow(new Object[] { pedido.getIdPedido(), pedido.getCliente().getNickname(),
					controlador.obtenerCodigoRecogida(pedido), pedido.isRecogida_solicitada() ? "Sí" : "No",
					controlador.formatearPrecio(pedido.getTotal()), pedido.getLineas().size() });
		}
	}

	/**
	 * Muestra la información de un pedido.
	 */
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

	/**
	 * Entrega un pedido usando el código introducido.
	 */
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


	/**
	 * Busca un pedido usando el código de recogida.
	 * 
	 * @param codigo código de recogida
	 * @return pedido encontrado o null
	 */
	private Pedido buscarPedidoPorCodigo(String codigo) {
		return controlador.buscarPedidoPorCodigo(codigo);
	}

	/**
	 * Muestra un pedido en una ventana emergente.
	 * 
	 * @param pedido pedido a mostrar
	 */
	private void mostrarPedidoEnVentana(Pedido pedido) {
		JTextArea areaPedido = crearArea();
		areaPedido.setEditable(false);
		areaPedido.setText(crearTextoPedido(pedido));
		areaPedido.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(areaPedido);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(680), VentanaPrincipal.escalar(320)));

		JOptionPane.showMessageDialog(this, scroll, "Información del pedido", JOptionPane.INFORMATION_MESSAGE);
	}


	/**
	 * Genera el texto descriptivo de un pedido.
	 * 
	 * @param pedido pedido a mostrar
	 * @return texto descriptivo
	 */
	private String crearTextoPedido(Pedido pedido) {
		return controlador.crearTextoPedido(pedido);
	}

	/**
	 * Aplica estilos visuales a la tabla de entregas.
	 * 
	 * @param tabla tabla a estilizar
	 */
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
