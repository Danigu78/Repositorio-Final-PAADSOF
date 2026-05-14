package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorIntercambiosEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.*;

import intercambios.Oferta;
import usuarios.Empleado;

/**
 * Pantalla para confirmar intercambios.
 * 
 * Muestra las ofertas de intercambio de la tienda. Para consultar o confirmar
 * una oferta concreta, el empleado escribe su ID abajo.
 * 
 * @author Lucas
 * @version 1.0
 */
public class SeccionIntercambiosEmpleado extends SeccionEmpleadoBase {

	private static final long serialVersionUID = 1L;

	/** Tabla con las ofertas de intercambio. */
	private JTable tablaOfertas;

	/** Modelo de datos de la tabla de ofertas. */
	private DefaultTableModel modeloOfertas;

	/** Ordenador y filtro de la tabla. */
	private TableRowSorter<DefaultTableModel> ordenadorOfertas;

	/** Campo para escribir el ID de la oferta. */
	private JTextField campoIdOferta;

	/** Combo para filtrar por estado. */
	private JComboBox<String> comboEstadoOferta;

	/** Controlador de la sección. */
	private ControladorIntercambiosEmpleado controlador;

	/**
	 * Constructor de la sección de intercambios.
	 * 
	 * @param ventana  Ventana principal de la aplicación.
	 * @param empleado Empleado que usa la sección.
	 */
	public SeccionIntercambiosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorIntercambiosEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	/**
	 * Cambia el controlador de la sección.
	 * 
	 * @param controlador Nuevo controlador.
	 */
	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorIntercambiosEmpleado) {
			this.controlador = (ControladorIntercambiosEmpleado) controlador;
		}
	}

	/**
	 * Conecta un botón con una acción del controlador.
	 * 
	 * @param boton  Botón a conectar.
	 * @param accion Acción asociada.
	 */
	private void conectar(AbstractButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	/** Construye toda la interfaz gráfica. */
	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Confirmación de Intercambios");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloqueOfertas());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAccionesOferta());

		add(panelBase, BorderLayout.CENTER);
	}

	/**
	 * Crea el bloque con la tabla de ofertas.
	 * 
	 * @return Panel con las ofertas.
	 */
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
		estilizarTablaOfertas(tablaOfertas);
		ordenadorOfertas = ponerOrdenacionTabla(tablaOfertas, modeloOfertas);

		/*
		 * Como en las otras pantallas: la tabla solo sirve para mirar. El ID se escribe
		 * abajo cuando se quiera hacer algo.
		 */
		tablaOfertas.setRowSelectionAllowed(false);
		tablaOfertas.setCellSelectionEnabled(false);

		comboEstadoOferta = crearCombo(crearOpcionesEstado());

		JButton botonRefrescar = crearBotonSecundario("Refrescar");
		conectar(botonRefrescar, ControladorIntercambiosEmpleado.REFRESCAR);

		comboEstadoOferta.setActionCommand(ControladorIntercambiosEmpleado.FILTRAR);
		comboEstadoOferta.addActionListener(controlador);

		JPanel filaFiltro = new JPanel(new BorderLayout(VentanaPrincipal.escalar(12), 0));
		filaFiltro.setOpaque(false);

		JPanel zonaCombo = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaCombo.setOpaque(false);
		zonaCombo.add(crearLabel("Filtrar por estado"), BorderLayout.NORTH);
		zonaCombo.add(comboEstadoOferta, BorderLayout.CENTER);

		JPanel zonaBusqueda = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaBusqueda.setOpaque(false);
		zonaBusqueda.add(crearLabel("Buscar"), BorderLayout.NORTH);
		zonaBusqueda.add(crearBuscadorTabla(ordenadorOfertas), BorderLayout.CENTER);

		JPanel zonaBoton = new JPanel(new BorderLayout());
		zonaBoton.setOpaque(false);
		zonaBoton.add(botonRefrescar, BorderLayout.SOUTH);

		filaFiltro.add(zonaBusqueda, BorderLayout.WEST);
		filaFiltro.add(zonaCombo, BorderLayout.CENTER);
		filaFiltro.add(zonaBoton, BorderLayout.EAST);

		JScrollPane scrollTabla = estilizarScroll(tablaOfertas);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(260)));

		bloque.add(crearLabel("Consulta las ofertas. Para ver o confirmar una, escribe su ID abajo."), gbcCampo(1));
		bloque.add(filaFiltro, gbcCampo(2));
		bloque.add(scrollTabla, gbcCampo(3));

		cargarTablaOfertas();

		return bloque;
	}

	/**
	 * Crea el bloque de acciones sobre ofertas.
	 * 
	 * @return Panel de acciones.
	 */
	private JPanel crearBloqueAccionesOferta() {
		JPanel bloque = crearBloque("Consultar o confirmar intercambio");

		campoIdOferta = crearCampo();

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelDatosOferta());
		panelAcciones.add(crearPanelBotonesOferta());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	/**
	 * Crea el panel con los datos de la oferta.
	 * 
	 * @return Panel de datos.
	 */
	private JPanel crearPanelDatosOferta() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Datos de la oferta");
		titulo.setAlignmentX(0.0f);
		panel.add(titulo);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JPanel filaId = crearCampoFormulario("ID oferta", campoIdOferta);
		filaId.setAlignmentX(0.0f);
		panel.add(filaId);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));

		JLabel ayuda = crearLabel("Solo se pueden confirmar ofertas en estado ACEPTADA.");
		ayuda.setAlignmentX(0.0f);
		panel.add(ayuda);

		return panel;
	}

	/**
	 * Crea el panel de botones de acciones.
	 * 
	 * @return Panel de botones.
	 */
	private JPanel crearPanelBotonesOferta() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Acciones");
		titulo.setAlignmentX(0.0f);
		panel.add(titulo);

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JButton botonVerOferta = crearBotonSecundario("Ver oferta");
		JButton botonConfirmar = crearBotonAccion("Confirmar intercambio");

		ajustarBotonOferta(botonVerOferta);
		ajustarBotonOferta(botonConfirmar);

		JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(10), 0));
		filaBotones.setOpaque(false);
		filaBotones.setAlignmentX(0.0f);

		filaBotones.add(botonVerOferta);
		filaBotones.add(botonConfirmar);

		Dimension tamanoFila = new Dimension(VentanaPrincipal.escalar(460), VentanaPrincipal.escalar(42));
		filaBotones.setPreferredSize(tamanoFila);
		filaBotones.setMaximumSize(tamanoFila);

		panel.add(filaBotones);

		conectar(botonVerOferta, ControladorIntercambiosEmpleado.VER_OFERTA);
		conectar(botonConfirmar, ControladorIntercambiosEmpleado.CONFIRMAR_OFERTA);

		return panel;
	}

	/**
	 * Ajusta el tamaño de un botón.
	 * 
	 * @param boton Botón a ajustar.
	 */
	private void ajustarBotonOferta(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(225), VentanaPrincipal.escalar(42));

		boton.setPreferredSize(tamano);
		boton.setMinimumSize(tamano);
		boton.setMaximumSize(tamano);
	}

	/**
	 * Crea las opciones del filtro de estados.
	 * 
	 * @return Array con los estados.
	 */
	private String[] crearOpcionesEstado() {
		return controlador.crearOpcionesEstado();
	}

	/** Carga la tabla de ofertas. */
	public void cargarTablaOfertas() {
		modeloOfertas.setRowCount(0);

		String estadoElegido = "Todos";

		if (comboEstadoOferta != null && comboEstadoOferta.getSelectedItem() != null) {
			estadoElegido = String.valueOf(comboEstadoOferta.getSelectedItem());
		}

		for (Oferta oferta : controlador.getOfertas(estadoElegido)) {
			modeloOfertas.addRow(new Object[] { oferta.getId(), oferta.getOrigen().getNickname(),
					oferta.getDestino().getNickname(), oferta.getEstado(), oferta.getProductosOfertados().size(),
					oferta.getProductosSolicitados().size() });
		}
	}

	/** Muestra la información de una oferta. */
	public void verOferta() {
		String idOferta = campoIdOferta.getText().trim();

		if (idOferta.isBlank()) {
			mostrarError("Escribe el ID de la oferta.");
			return;
		}

		Oferta oferta = buscarOfertaPorId(idOferta);

		if (oferta == null) {
			mostrarError("No existe ninguna oferta con ese ID.");
			return;
		}

		mostrarOfertaEnVentana(oferta);
	}

	/** Confirma una oferta de intercambio. */
	public void confirmarOferta() {
		ResultadoOperacion resultado = controlador.confirmarOferta(campoIdOferta.getText());

		if (resultado.isExito()) {
			cargarTablaOfertas();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	/**
	 * Busca una oferta por su ID.
	 * 
	 * @param idOferta ID de la oferta.
	 * @return Oferta encontrada o null.
	 */
	private Oferta buscarOfertaPorId(String idOferta) {
		return controlador.buscarOfertaPorId(idOferta);
	}

	/**
	 * Muestra la información de una oferta en una ventana.
	 * 
	 * @param oferta Oferta a mostrar.
	 */
	private void mostrarOfertaEnVentana(Oferta oferta) {
		JTextArea areaOferta = crearArea();
		areaOferta.setEditable(false);
		areaOferta.setText(crearTextoOferta(oferta));
		areaOferta.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(areaOferta);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(680), VentanaPrincipal.escalar(340)));

		JOptionPane.showMessageDialog(this, scroll, "Información de la oferta", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Crea el texto informativo de una oferta.
	 * 
	 * @param oferta Oferta a mostrar.
	 * @return Texto con la información.
	 */
	private String crearTextoOferta(Oferta oferta) {
		return controlador.crearTextoOferta(oferta);
	}

	/**
	 * Aplica el estilo visual a la tabla.
	 * 
	 * @param tabla Tabla a estilizar.
	 */
	private void estilizarTablaOfertas(JTable tabla) {
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
		tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
		tabla.getColumnModel().getColumn(2).setPreferredWidth(160);
		tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
		tabla.getColumnModel().getColumn(4).setPreferredWidth(110);
		tabla.getColumnModel().getColumn(5).setPreferredWidth(110);
	}
}
