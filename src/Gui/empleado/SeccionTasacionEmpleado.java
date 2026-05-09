package Gui.empleado;


import Gui.VentanaPrincipal;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import productos.EstadoProducto;
import productos.Producto2Mano;
import Gui.controladores.empleado.ControladorTasacionEmpleado;
import Gui.controladores.empleado.ResultadoOperacion;
import usuarios.Empleado;

/**
 * Pantalla para tasar productos de segunda mano.
 * 
 * Muestra los productos pendientes de tasación. Para tasar uno, el empleado
 * escribe el ID, revisa la información o la imagen si quiere, y después pone el
 * precio y el estado.
 */
public class SeccionTasacionEmpleado extends SeccionEmpleadoBase {

	private static final long serialVersionUID = 1L;

	private JTable tablaPendientes;
	private DefaultTableModel modeloPendientes;

	private JTextField campoIdProducto;
	private JTextField campoPrecioTasado;
	private JComboBox<EstadoProducto> comboEstadoProducto;
	private ControladorTasacionEmpleado controlador;

	public SeccionTasacionEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorTasacionEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorTasacionEmpleado) {
			this.controlador = (ControladorTasacionEmpleado) controlador;
		}
	}

	private void conectar(JButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Tasación de Productos");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloquePendientes());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueTasarProducto());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloquePendientes() {
		JPanel bloque = crearBloque("Productos pendientes de tasación");

		modeloPendientes = new DefaultTableModel(new String[] { "ID", "Propietario", "Nombre", "Imagen" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaPendientes = new JTable(modeloPendientes);
		estilizarTablaTasaciones(tablaPendientes);

		/*
		 * Como en stock y pedidos: la tabla es solo para consultar. El ID se escribe
		 * abajo a mano.
		 */
		tablaPendientes.setRowSelectionAllowed(false);
		tablaPendientes.setCellSelectionEnabled(false);

		JScrollPane scrollTabla = estilizarScroll(tablaPendientes);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(240)));

		JButton botonRefrescar = crearBotonSecundario("Refrescar");
		conectar(botonRefrescar, ControladorTasacionEmpleado.REFRESCAR);

		bloque.add(crearLabel("Consulta los productos pendientes. Para tasar uno, escribe su ID abajo."), gbcCampo(1));
		bloque.add(scrollTabla, gbcCampo(2));
		bloque.add(botonRefrescar, gbcBoton(3));

		cargarTablaPendientes();

		return bloque;
	}

	private JPanel crearBloqueTasarProducto() {
		JPanel bloque = crearBloque("Consultar o tasar producto");

		campoIdProducto = crearCampo();
		campoPrecioTasado = crearCampo();
		comboEstadoProducto = crearCombo(EstadoProducto.values());

		JPanel panelTasar = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelTasar.setOpaque(false);

		panelTasar.add(crearPanelDatosTasacion());
		panelTasar.add(crearPanelAccionesTasacion());

		bloque.add(panelTasar, gbcCampo(1));

		return bloque;
	}

	private JPanel crearPanelDatosTasacion() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Datos de tasación");
		titulo.setAlignmentX(0.0f);
		panel.add(titulo);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JPanel filaId = crearCampoFormulario("ID producto", campoIdProducto);
		filaId.setAlignmentX(0.0f);
		panel.add(filaId);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JPanel filaPrecio = crearCampoFormulario("Precio tasado", campoPrecioTasado);
		filaPrecio.setAlignmentX(0.0f);
		panel.add(filaPrecio);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JPanel filaEstado = crearCampoFormulario("Estado del producto", comboEstadoProducto);
		filaEstado.setAlignmentX(0.0f);
		panel.add(filaEstado);

		return panel;
	}

	private JPanel crearPanelAccionesTasacion() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Acciones");
		titulo.setAlignmentX(0.0f);
		panel.add(titulo);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JButton botonVerProducto = crearBotonSecundario("Ver producto");
		JButton botonVerImagen = crearBotonSecundario("Ver imagen");
		JButton botonTasar = crearBotonAccion("Tasar producto");

		ajustarBotonTasacion(botonVerProducto);
		ajustarBotonTasacion(botonVerImagen);
		ajustarBotonTasacion(botonTasar);

		JPanel botones = new JPanel(new GridLayout(3, 1, 0, VentanaPrincipal.escalar(10)));
		botones.setOpaque(false);
		botones.setAlignmentX(0.0f);

		botones.add(botonVerProducto);
		botones.add(botonVerImagen);
		botones.add(botonTasar);

		Dimension tamanoBotones = new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(150));
		botones.setPreferredSize(tamanoBotones);
		botones.setMaximumSize(tamanoBotones);

		panel.add(botones);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));

		conectar(botonVerProducto, ControladorTasacionEmpleado.VER_PRODUCTO);
		conectar(botonVerImagen, ControladorTasacionEmpleado.VER_IMAGEN);
		conectar(botonTasar, ControladorTasacionEmpleado.TASAR_PRODUCTO);

		return panel;
	}

	private void ajustarBotonTasacion(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(230), VentanaPrincipal.escalar(40));

		boton.setPreferredSize(tamano);
		boton.setMinimumSize(tamano);
		boton.setMaximumSize(tamano);
	}

	public void cargarTablaPendientes() {
		modeloPendientes.setRowCount(0);

		for (Producto2Mano producto : controlador.getPendientesTasacion()) {
			modeloPendientes.addRow(new Object[] { producto.getId(), controlador.obtenerNombrePropietario(producto),
					producto.getNombre(), controlador.obtenerRutaImagen(producto) });
		}
	}

	public void verProducto() {
		String idProducto = campoIdProducto.getText().trim();

		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto.");
			return;
		}

		Producto2Mano producto = buscarProductoPendientePorId(idProducto);

		if (producto == null) {
			mostrarError("No existe ningún producto pendiente con ese ID.");
			return;
		}

		mostrarProductoEnVentana(producto);
	}

	public void verImagenProducto() {
		String idProducto = campoIdProducto.getText().trim();

		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto.");
			return;
		}

		Producto2Mano producto = buscarProductoPendientePorId(idProducto);

		if (producto == null) {
			mostrarError("No existe ningún producto pendiente con ese ID.");
			return;
		}

		abrirImagenProducto(producto);
	}

	public void tasarProducto() {
		EstadoProducto estado = (EstadoProducto) comboEstadoProducto.getSelectedItem();
		ResultadoOperacion resultado = controlador.tasarProducto(campoIdProducto.getText(), campoPrecioTasado.getText(),
				estado);

		if (resultado.isExito()) {
			cargarTablaPendientes();
			limpiarCamposTasacion();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private Producto2Mano buscarProductoPendientePorId(String idProducto) {
		return controlador.buscarProductoPendientePorId(idProducto);
	}

	private void mostrarProductoEnVentana(Producto2Mano producto) {
		JTextArea areaProducto = crearArea();
		areaProducto.setEditable(false);
		areaProducto.setText(crearTextoProducto(producto));
		areaProducto.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(areaProducto);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(650), VentanaPrincipal.escalar(280)));

		JOptionPane.showMessageDialog(this, scroll, "Información del producto", JOptionPane.INFORMATION_MESSAGE);
	}

	private String crearTextoProducto(Producto2Mano producto) {
		return controlador.crearTextoProducto(producto);
	}

	private void abrirImagenProducto(Producto2Mano producto) {
		if (producto == null) {
			mostrarError("El producto no puede ser null.");
			return;
		}

		String rutaImagen = obtenerRutaImagen(producto);

		if (rutaImagen == null || rutaImagen.isBlank()) {
			mostrarError("Este producto no tiene ruta de imagen.");
			return;
		}

		UtilidadesImagenProducto.mostrarImagenProducto(this, rutaImagen);
	}

	private String obtenerRutaImagen(Producto2Mano producto) {
		return controlador.obtenerRutaImagen(producto);
	}

	private void limpiarCamposTasacion() {
		campoIdProducto.setText("");
		campoPrecioTasado.setText("");

		if (comboEstadoProducto.getItemCount() > 0) {
			comboEstadoProducto.setSelectedIndex(0);
		}
	}

	private void estilizarTablaTasaciones(JTable tabla) {
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

		tabla.getColumnModel().getColumn(0).setPreferredWidth(120);
		tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
		tabla.getColumnModel().getColumn(2).setPreferredWidth(260);
		tabla.getColumnModel().getColumn(3).setPreferredWidth(420);
	}
}
