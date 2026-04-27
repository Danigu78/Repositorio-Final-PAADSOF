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

import productos.EstadoProducto;
import productos.Producto2Mano;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Sección para tasar productos de segunda mano.
 * 
 * Muestra solo los productos pendientes de tasación y permite asignarles precio
 * y estado.
 */
public class SeccionTasacionEmpleado extends AbstractPanelEmpleadoSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaPendientes;
	private DefaultTableModel modeloPendientes;

	private JTextField campoId;
	private JTextField campoPrecio;
	private JComboBox<EstadoProducto> comboEstado;
	private JTextArea areaInfoProducto;

	public SeccionTasacionEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Tasación de Productos");
		JPanel contenido = getContenido(base);

		contenido.add(crearBloquePendientes());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueTasar());

		add(base);
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
		tablaPendientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaTasaciones(tablaPendientes);

		areaInfoProducto = crearArea();
		areaInfoProducto.setEditable(false);

		JButton botonRefrescar = crearBotonAccion("Refrescar pendientes");

		cargarTablaPendientes();

		tablaPendientes.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int fila = tablaPendientes.getSelectedRow();

			if (fila < 0) {
				return;
			}

			String idProducto = String.valueOf(tablaPendientes.getValueAt(fila, 0));

			if (campoId != null) {
				campoId.setText(idProducto);
			}

			mostrarInfoProducto(idProducto);
		});

		botonRefrescar.addActionListener(e -> {
			cargarTablaPendientes();
			areaInfoProducto.setText("");
		});

		JScrollPane scrollTabla = estilizarScroll(tablaPendientes);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(240)));

		JScrollPane scrollInfo = estilizarScroll(areaInfoProducto);
		scrollInfo.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(180)));

		bloque.add(crearLabel("Selecciona un producto pendiente para cargar su ID."), gbcCampo(1));
		bloque.add(scrollTabla, gbcCampo(2));

		bloque.add(crearLabel("Información del producto seleccionado"), gbcCampo(3));
		bloque.add(scrollInfo, gbcCampo(4));

		bloque.add(botonRefrescar, gbcBoton(5));

		return bloque;
	}

	private JPanel crearBloqueTasar() {
		JPanel bloque = crearBloque("Tasar producto");

		campoId = crearCampo();
		campoPrecio = crearCampo();
		comboEstado = crearCombo(EstadoProducto.values());

		JButton botonTasar = crearBotonAccion("Tasar");

		bloque.add(crearLabel("ID producto"), gbcCampo(1));
		bloque.add(campoId, gbcCampo(2));

		bloque.add(crearLabel("Precio tasado"), gbcCampo(3));
		bloque.add(campoPrecio, gbcCampo(4));

		bloque.add(crearLabel("Estado del producto"), gbcCampo(5));
		bloque.add(comboEstado, gbcCampo(6));

		bloque.add(botonTasar, gbcBoton(7));

		botonTasar.addActionListener(e -> {
			String id = campoId.getText().trim();
			Double precio = leerDoubleSeguro(campoPrecio.getText());
			EstadoProducto estado = (EstadoProducto) comboEstado.getSelectedItem();

			if (id.isBlank()) {
				mostrarError("Introduce o selecciona un ID de producto.");
				return;
			}

			if (precio == null || precio < 0) {
				mostrarError("Introduce un precio válido.");
				return;
			}

			if (estado == null) {
				mostrarError("Selecciona un estado.");
				return;
			}

			Producto2Mano productoAntes = buscarProductoPendientePorId(id);

			if (productoAntes == null) {
				mostrarError("No existe ningún producto pendiente con ese ID.");
				return;
			}

			empleado.tasarProducto(id, precio, estado);

			Producto2Mano productoDespues = buscarProductoPendientePorId(id);

			if (productoDespues == null) {
				cargarTablaPendientes();
				campoId.setText("");
				campoPrecio.setText("");
				areaInfoProducto.setText("");

				if (estado == EstadoProducto.NO_ACEPTADO) {
					mostrarMensaje("Producto rechazado correctamente.");
				} else {
					mostrarMensaje("Producto tasado y publicado para intercambio.");
				}
			} else {
				mostrarError("No se pudo tasar el producto.");
			}
		});

		return bloque;
	}

	private void cargarTablaPendientes() {
		modeloPendientes.setRowCount(0);

		for (Producto2Mano producto : Tienda.getInstancia().getPendientesTasacion()) {
			String propietario = "Sin propietario";

			if (producto.getPropietario() != null) {
				propietario = producto.getPropietario().getNickname();
			}

			modeloPendientes.addRow(
					new Object[] { producto.getId(), propietario, producto.getNombre(), producto.getImagenRuta() });
		}
	}

	private Producto2Mano buscarProductoPendientePorId(String idProducto) {
		if (idProducto == null || idProducto.isBlank()) {
			return null;
		}

		for (Producto2Mano producto : Tienda.getInstancia().getPendientesTasacion()) {
			if (producto.getId().equals(idProducto.trim())) {
				return producto;
			}
		}

		return null;
	}

	private void mostrarInfoProducto(String idProducto) {
		Producto2Mano producto = buscarProductoPendientePorId(idProducto);

		if (producto == null) {
			areaInfoProducto.setText("No existe ningún producto pendiente con ese ID.");
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Producto: ").append(producto.getId()).append(" - ").append(producto.getNombre()).append("\n");

		if (producto.getPropietario() != null) {
			sb.append("Propietario: ").append(producto.getPropietario().getNickname()).append("\n");
		} else {
			sb.append("Propietario: Sin propietario\n");
		}

		sb.append("Descripción: ").append(producto.getDescripcion()).append("\n");
		sb.append("Imagen: ").append(producto.getImagenRuta()).append("\n");
		sb.append("Visible: ").append(producto.isVisible() ? "Sí" : "No").append("\n");
		sb.append("Bloqueado: ").append(producto.isBloqueado() ? "Sí" : "No").append("\n");
		sb.append("Valoración actual: ");

		if (producto.getValoracion() == null) {
			sb.append("Sin valorar\n");
		} else {
			sb.append(producto.getValoracion().getPrecioTasacion()).append(" € - ")
					.append(producto.getValoracion().getEstadoProducto()).append("\n");
		}

		areaInfoProducto.setText(sb.toString());
		areaInfoProducto.setCaretPosition(0);
	}

	private void estilizarTablaTasaciones(JTable tabla) {
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
