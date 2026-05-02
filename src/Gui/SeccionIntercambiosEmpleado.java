package Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.*;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import productos.Producto2Mano;
import tienda.Tienda;
import usuarios.Cliente;
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

	public SeccionIntercambiosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
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
		botonRefrescar.addActionListener(e -> cargarTablaOfertas());

		comboEstadoOferta.addActionListener(e -> cargarTablaOfertas());

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

		botonVerOferta.addActionListener(e -> verOferta());
		botonConfirmar.addActionListener(e -> confirmarOferta());

		return panel;
	}

	private void ajustarBotonOferta(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(225), VentanaPrincipal.escalar(42));

		boton.setPreferredSize(tamano);
		boton.setMinimumSize(tamano);
		boton.setMaximumSize(tamano);
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

		String estadoElegido = "Todos";

		if (comboEstadoOferta != null && comboEstadoOferta.getSelectedItem() != null) {
			estadoElegido = String.valueOf(comboEstadoOferta.getSelectedItem());
		}

		for (Oferta oferta : obtenerTodasLasOfertas()) {
			oferta.haCaducado();

			if (!"Todos".equals(estadoElegido) && !oferta.getEstado().name().equals(estadoElegido)) {
				continue;
			}

			modeloOfertas.addRow(new Object[] { oferta.getId(), oferta.getOrigen().getNickname(),
					oferta.getDestino().getNickname(), oferta.getEstado(), oferta.getProductosOfertados().size(),
					oferta.getProductosSolicitados().size() });
		}
	}

	private void verOferta() {
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

	private void confirmarOferta() {
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

		if (oferta.getEstado() != EstadoOferta.ACEPTADA) {
			mostrarError("La oferta debe estar en estado ACEPTADA para poder confirmarla.");
			return;
		}

		boolean confirmada = empleado.confirmarIntercambio(oferta);

		if (confirmada) {
			cargarTablaOfertas();
			mostrarMensaje("Intercambio confirmado correctamente.");
		} else {
			mostrarError("No se pudo confirmar el intercambio.");
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
		StringBuilder texto = new StringBuilder();

		texto.append("Oferta: ").append(oferta.getId()).append("\n");
		texto.append("Estado: ").append(oferta.getEstado()).append("\n");
		texto.append("Fecha: ").append(oferta.getFechaOferta()).append("\n");
		texto.append("Origen: ").append(oferta.getOrigen().getNickname()).append("\n");
		texto.append("Destino: ").append(oferta.getDestino().getNickname()).append("\n\n");

		texto.append("Productos que ofrece ").append(oferta.getOrigen().getNickname()).append(":\n");
		aniadirProductosOferta(texto, oferta.getProductosOfertados());

		texto.append("\nProductos que solicita a ").append(oferta.getDestino().getNickname()).append(":\n");
		aniadirProductosOferta(texto, oferta.getProductosSolicitados());

		return texto.toString();
	}

	private void aniadirProductosOferta(StringBuilder texto, List<Producto2Mano> productos) {
		if (productos == null || productos.isEmpty()) {
			texto.append("Sin productos.\n");
			return;
		}

		for (Producto2Mano producto : productos) {
			texto.append("- ");
			texto.append(producto.getId()).append(" | ");
			texto.append(producto.getNombre());

			if (producto.getValoracion() != null) {
				texto.append(" | valor: ");
				texto.append(formatearPrecio(producto.getValoracion().getPrecioTasacion()));
				texto.append(" | estado: ");
				texto.append(producto.getValoracion().getEstadoProducto());
			} else {
				texto.append(" | sin valoración");
			}

			texto.append("\n");
		}
	}

	private String formatearPrecio(double precio) {
		return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
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