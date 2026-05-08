package Gui.empleado;


import Gui.VentanaPrincipal;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.*;

import intercambios.Oferta;
import Gui.Controladores.empleado.ControladorIntercambiosEmpleado;
import Gui.Controladores.empleado.ResultadoOperacion;
import usuarios.Empleado;

/**
 * Pantalla para confirmar intercambios.
 * 
 * Muestra las ofertas de intercambio de la tienda. Para consultar o confirmar
 * una oferta concreta, el empleado escribe su ID abajo.
 */
public class SeccionIntercambiosEmpleado extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaOfertas;
	private DefaultTableModel modeloOfertas;

	private JTextField campoIdOferta;
	private JComboBox<String> comboEstadoOferta;
	private ControladorIntercambiosEmpleado controlador;

	public SeccionIntercambiosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorIntercambiosEmpleado(empleado);
		this.controlador.setVista(this);
		setControlador(this.controlador);
		construirUI();
	}

	public void setControlador(ActionListener controlador) {
		if (controlador instanceof ControladorIntercambiosEmpleado) {
			this.controlador = (ControladorIntercambiosEmpleado) controlador;
		}
	}

	private void conectar(AbstractButton boton, String accion) {
		boton.setActionCommand(accion);
		boton.addActionListener(controlador);
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Confirmación de Intercambios");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloqueOfertas());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAccionesOferta());

		add(panelBase, BorderLayout.CENTER);
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
		estilizarTablaOfertas(tablaOfertas);

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

		JPanel zonaBoton = new JPanel(new BorderLayout());
		zonaBoton.setOpaque(false);
		zonaBoton.add(botonRefrescar, BorderLayout.SOUTH);

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

	private void ajustarBotonOferta(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(225), VentanaPrincipal.escalar(42));

		boton.setPreferredSize(tamano);
		boton.setMinimumSize(tamano);
		boton.setMaximumSize(tamano);
	}

	private String[] crearOpcionesEstado() {
		return controlador.crearOpcionesEstado();
	}

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

	public void confirmarOferta() {
		ResultadoOperacion resultado = controlador.confirmarOferta(campoIdOferta.getText());

		if (resultado.isExito()) {
			cargarTablaOfertas();
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private Oferta buscarOfertaPorId(String idOferta) {
		return controlador.buscarOfertaPorId(idOferta);
	}

	private void mostrarOfertaEnVentana(Oferta oferta) {
		JTextArea areaOferta = crearArea();
		areaOferta.setEditable(false);
		areaOferta.setText(crearTextoOferta(oferta));
		areaOferta.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(areaOferta);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(680), VentanaPrincipal.escalar(340)));

		JOptionPane.showMessageDialog(this, scroll, "Información de la oferta", JOptionPane.INFORMATION_MESSAGE);
	}

	private String crearTextoOferta(Oferta oferta) {
		return controlador.crearTextoOferta(oferta);
	}

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
