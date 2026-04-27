package Gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

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

import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Sección para gestionar packs. Permite crear packs, consultar su contenido y
 * modificar packs existentes.
 */
public class SeccionPacksEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	private JTable tablaPacks;
	private DefaultTableModel modeloPacks;

	private JTextArea areaInfoPack;
	private JTextArea areaInfoPackGestion;
	private JTextArea areaLineas;
	private JTextArea areaVistaPrevia;

	private JTextField campoIdPack;
	private JTextField campoIdProducto;
	private JTextField campoUnidades;
	private JTextField campoNuevoPrecio;

	public SeccionPacksEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	private void construirUI() {
		JPanel base = crearPanelBase("Gestión de Packs");
		JPanel contenido = getContenido(base);

		contenido.add(crearBloquePacksExistentes());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCrearPack());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueModificarPack());

		add(base);
	}

	private JPanel crearBloquePacksExistentes() {
		JPanel bloque = crearBloque("Packs existentes");

		modeloPacks = new DefaultTableModel(new String[] { "ID", "Nombre", "Precio", "Stock", "Productos incluidos" },
				0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tablaPacks = new JTable(modeloPacks);
		tablaPacks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaPacks(tablaPacks);

		areaInfoPack = crearArea();
		areaInfoPack.setEditable(false);

		JButton botonRefrescar = crearBotonAccion("Refrescar packs");

		cargarTablaPacks();

		tablaPacks.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int fila = tablaPacks.getSelectedRow();

			if (fila < 0) {
				return;
			}

			String idPack = String.valueOf(tablaPacks.getValueAt(fila, 0));

			if (campoIdPack != null) {
				campoIdPack.setText(idPack);
			}

			mostrarInfoPack(idPack);
		});

		botonRefrescar.addActionListener(e -> cargarTablaPacks());

		JScrollPane scrollTabla = estilizarScroll(tablaPacks);
		scrollTabla.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(230)));

		JScrollPane scrollInfo = estilizarScroll(areaInfoPack);
		scrollInfo.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(150)));

		bloque.add(crearLabel("Selecciona un pack para ver sus productos y cantidades."), gbcCampo(1));
		bloque.add(scrollTabla, gbcCampo(2));
		bloque.add(crearLabel("Información del pack seleccionado"), gbcCampo(3));
		bloque.add(scrollInfo, gbcCampo(4));
		bloque.add(botonRefrescar, gbcBoton(5));

		return bloque;
	}

	private JPanel crearBloqueCrearPack() {
		JPanel bloque = crearBloque("Crear pack");

		JTextField campoNombre = crearCampo();
		JTextArea areaDescripcionPack = crearArea();
		JTextField campoImagen = crearCampo();
		JTextField campoPrecio = crearCampo();
		JTextField campoStock = crearCampo();

		JTextField campoIdProductoLinea = crearCampo();
		JTextField campoUnidadesLinea = crearCampo();

		areaLineas = crearArea();
		areaVistaPrevia = crearArea();
		areaVistaPrevia.setEditable(false);

		JButton botonAgregarLinea = crearBotonSecundario("Añadir línea");
		JButton botonVistaPrevia = crearBotonSecundario("Ver contenido");
		JButton botonLimpiarLineas = crearBotonSecundario("Limpiar líneas");
		JButton botonCrear = crearBotonAccion("Crear pack");

		bloque.add(crearLabel("Nombre"), gbcCampo(1));
		bloque.add(campoNombre, gbcCampo(2));

		bloque.add(crearLabel("Descripción"), gbcCampo(3));
		bloque.add(estilizarScroll(areaDescripcionPack), gbcCampo(4));

		bloque.add(crearLabel("Imagen"), gbcCampo(5));
		bloque.add(campoImagen, gbcCampo(6));

		bloque.add(crearLabel("Precio"), gbcCampo(7));
		bloque.add(campoPrecio, gbcCampo(8));

		bloque.add(crearLabel("Stock"), gbcCampo(9));
		bloque.add(campoStock, gbcCampo(10));

		JPanel helperLinea = new JPanel(new GridLayout(1, 3, 10, 0));
		helperLinea.setOpaque(false);
		helperLinea.add(crearCampoFormulario("ID producto", campoIdProductoLinea));
		helperLinea.add(crearCampoFormulario("Unidades", campoUnidadesLinea));
		helperLinea.add(crearCampoFormulario(" ", botonAgregarLinea));

		bloque.add(crearLabel("Añadir producto al pack"), gbcCampo(11));
		bloque.add(helperLinea, gbcCampo(12));

		bloque.add(crearLabel("Líneas del pack: una por línea con formato ID;UNIDADES"), gbcCampo(13));
		bloque.add(estilizarScroll(areaLineas), gbcCampo(14));

		JPanel botonesLineas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		botonesLineas.setOpaque(false);
		botonesLineas.add(botonVistaPrevia);
		botonesLineas.add(botonLimpiarLineas);

		bloque.add(botonesLineas, gbcBoton(15));

		bloque.add(crearLabel("Vista previa del contenido del pack"), gbcCampo(16));
		bloque.add(estilizarScroll(areaVistaPrevia), gbcCampo(17));

		bloque.add(botonCrear, gbcBoton(18));

		botonAgregarLinea.addActionListener(e -> {
			String id = campoIdProductoLinea.getText().trim();
			Integer unidades = leerEnteroSeguro(campoUnidadesLinea.getText());

			if (id.isBlank()) {
				mostrarError("Introduce el ID del producto.");
				return;
			}

			if (unidades == null || unidades <= 0) {
				mostrarError("Introduce unidades válidas.");
				return;
			}

			ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(id);

			if (producto == null) {
				mostrarError("No existe un producto con ese ID.");
				return;
			}

			if (!areaLineas.getText().isBlank() && !areaLineas.getText().endsWith("\n")) {
				areaLineas.append("\n");
			}

			areaLineas.append(id + ";" + unidades);

			campoIdProductoLinea.setText("");
			campoUnidadesLinea.setText("");

			actualizarVistaPrevia();
		});

		botonVistaPrevia.addActionListener(e -> actualizarVistaPrevia());

		botonLimpiarLineas.addActionListener(e -> {
			areaLineas.setText("");
			areaVistaPrevia.setText("");
		});

		botonCrear.addActionListener(e -> {
			try {
				String nombre = campoNombre.getText().trim();
				String descripcion = areaDescripcionPack.getText().trim();
				String imagen = campoImagen.getText().trim();

				Double precio = leerDoubleSeguro(campoPrecio.getText());
				Integer stock = leerEnteroSeguro(campoStock.getText());

				if (nombre.isBlank()) {
					mostrarError("Introduce el nombre del pack.");
					return;
				}

				if (descripcion.isBlank()) {
					mostrarError("Introduce la descripción del pack.");
					return;
				}

				if (imagen.isBlank()) {
					mostrarError("Introduce una ruta de imagen.");
					return;
				}

				if (precio == null || precio <= 0) {
					mostrarError("Introduce un precio válido.");
					return;
				}

				if (stock == null || stock <= 0) {
					mostrarError("Introduce un stock válido.");
					return;
				}

				ArrayList<LineaPack> lineas = construirLineasPack(areaLineas.getText());

				if (lineas.size() < 2) {
					mostrarError("Un pack debe tener al menos dos productos.");
					return;
				}

				boolean ok = empleado.crearPack(nombre, descripcion, imagen, precio, stock, lineas);

				if (ok) {
					cargarTablaPacks();
					mostrarMensaje("Pack creado correctamente.");

					campoNombre.setText("");
					areaDescripcionPack.setText("");
					campoImagen.setText("");
					campoPrecio.setText("");
					campoStock.setText("");
					areaLineas.setText("");
					areaVistaPrevia.setText("");
				} else {
					mostrarError("No se pudo crear el pack.");
				}

			} catch (Exception ex) {
				mostrarError("No se pudo crear el pack: " + ex.getMessage());
			}
		});

		return bloque;
	}

	private JPanel crearBloqueModificarPack() {
		JPanel bloque = crearBloque("Modificar pack existente");

		campoIdPack = crearCampo();
		campoIdProducto = crearCampo();
		campoUnidades = crearCampo();
		campoNuevoPrecio = crearCampo();

		areaInfoPackGestion = crearArea();
		areaInfoPackGestion.setEditable(false);

		JButton botonVerPack = crearBotonSecundario("Ver pack");
		JButton botonAnadir = crearBotonAccion("Añadir producto");
		JButton botonModificarUnidades = crearBotonAccion("Modificar unidades");
		JButton botonEliminarProducto = crearBotonPeligro("Eliminar producto");
		JButton botonModificarPrecio = crearBotonAccion("Modificar precio");
		JButton botonEliminarPack = crearBotonPeligro("Eliminar pack");

		bloque.add(crearLabel("ID pack"), gbcCampo(1));
		bloque.add(campoIdPack, gbcCampo(2));
		bloque.add(botonVerPack, gbcBoton(3));

		bloque.add(crearLabel("Información del pack que se está modificando"), gbcCampo(4));
		JScrollPane scrollInfoGestion = estilizarScroll(areaInfoPackGestion);
		scrollInfoGestion
				.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(150)));
		bloque.add(scrollInfoGestion, gbcCampo(5));

		bloque.add(crearLabel("ID producto"), gbcCampo(6));
		bloque.add(campoIdProducto, gbcCampo(7));

		bloque.add(crearLabel("Unidades"), gbcCampo(8));
		bloque.add(campoUnidades, gbcCampo(9));

		bloque.add(crearLabel("Nuevo precio"), gbcCampo(10));
		bloque.add(campoNuevoPrecio, gbcCampo(11));

		JPanel botones = new JPanel(new GridLayout(0, 2, 10, 10));
		botones.setOpaque(false);
		botones.add(botonAnadir);
		botones.add(botonModificarUnidades);
		botones.add(botonEliminarProducto);
		botones.add(botonModificarPrecio);
		botones.add(botonEliminarPack);

		bloque.add(botones, gbcCampo(12));

		botonVerPack.addActionListener(e -> {
			String idPack = campoIdPack.getText().trim();

			if (idPack.isBlank()) {
				mostrarError("Introduce o selecciona un ID de pack.");
				return;
			}

			mostrarInfoPack(idPack);
		});

		botonAnadir.addActionListener(e -> {
			Integer unidades = leerEnteroSeguro(campoUnidades.getText());

			if (!datosProductoPackValidos(unidades)) {
				return;
			}

			try {
				boolean ok = empleado.añadirProductoaPack(campoIdProducto.getText().trim(),
						campoIdPack.getText().trim(), unidades);

				if (ok) {
					cargarTablaPacks();
					mostrarInfoPack(campoIdPack.getText().trim());
					mostrarMensaje("Producto añadido al pack.");
				} else {
					mostrarError("No se pudo añadir el producto al pack.");
				}
			} catch (Exception ex) {
				mostrarError("No se pudo añadir el producto al pack: " + ex.getMessage());
			}
		});

		botonModificarUnidades.addActionListener(e -> {
			Integer unidades = leerEnteroSeguro(campoUnidades.getText());

			if (!datosProductoPackValidos(unidades)) {
				return;
			}

			try {
				boolean ok = empleado.modificarUnidadesProductoEnPack(campoIdProducto.getText().trim(),
						campoIdPack.getText().trim(), unidades);

				if (ok) {
					cargarTablaPacks();
					mostrarInfoPack(campoIdPack.getText().trim());
					mostrarMensaje("Unidades modificadas correctamente.");
				} else {
					mostrarError("No se pudieron modificar las unidades.");
				}
			} catch (Exception ex) {
				mostrarError("No se pudieron modificar las unidades: " + ex.getMessage());
			}
		});

		botonEliminarProducto.addActionListener(e -> {
			String idPack = campoIdPack.getText().trim();
			String idProducto = campoIdProducto.getText().trim();

			if (idPack.isBlank() || idProducto.isBlank()) {
				mostrarError("Introduce el ID del pack y el ID del producto.");
				return;
			}

			boolean ok = empleado.eliminarProductoDePack(idPack, idProducto);

			if (ok) {
				cargarTablaPacks();
				mostrarInfoPack(idPack);
				mostrarMensaje("Producto eliminado del pack.");
			} else {
				mostrarError("No se pudo eliminar el producto del pack.");
			}
		});

		botonModificarPrecio.addActionListener(e -> {
			String idPack = campoIdPack.getText().trim();
			Double precio = leerDoubleSeguro(campoNuevoPrecio.getText());

			if (idPack.isBlank()) {
				mostrarError("Introduce el ID del pack.");
				return;
			}

			if (precio == null || precio <= 0) {
				mostrarError("Introduce un precio válido.");
				return;
			}

			try {
				boolean ok = empleado.modificarPrecioPack(idPack, precio);

				if (ok) {
					cargarTablaPacks();
					mostrarInfoPack(idPack);
					mostrarMensaje("Precio del pack modificado.");
				} else {
					mostrarError("No se pudo modificar el precio.");
				}
			} catch (Exception ex) {
				mostrarError("No se pudo modificar el precio: " + ex.getMessage());
			}
		});

		botonEliminarPack.addActionListener(e -> {
			String idPack = campoIdPack.getText().trim();

			if (idPack.isBlank()) {
				mostrarError("Introduce el ID del pack.");
				return;
			}

			boolean ok = empleado.eliminarPack(idPack);

			if (ok) {
				cargarTablaPacks();
				limpiarInfoPack();
				campoIdPack.setText("");
				mostrarMensaje("Pack eliminado correctamente.");
			} else {
				mostrarError("No se pudo eliminar el pack.");
			}
		});

		return bloque;
	}

	private void cargarTablaPacks() {
		modeloPacks.setRowCount(0);

		for (ProductoVenta producto : Tienda.getInstancia().getStockVentas()) {
			if (producto instanceof Pack) {
				Pack pack = (Pack) producto;

				modeloPacks.addRow(new Object[] { pack.getId(), pack.getNombre(),
						String.format(java.util.Locale.US, "%.2f €", pack.getPrecioOficial()).replace('.', ','),
						pack.getStockDisponible(), pack.getLineas().size() });
			}
		}
	}

	private void mostrarInfoPack(String idPack) {
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idPack);

		if (!(producto instanceof Pack)) {
			actualizarInfoPack("No existe ningún pack con ese ID.");
			return;
		}

		Pack pack = (Pack) producto;

		StringBuilder sb = new StringBuilder();

		sb.append("Pack: ").append(pack.getId()).append(" - ").append(pack.getNombre()).append("\n");
		sb.append("Precio pack: ").append(pack.getPrecioOficial()).append(" €\n");
		sb.append("Stock pack: ").append(pack.getStockDisponible()).append("\n");
		sb.append("Suma productos por separado: ").append(pack.calcularSumaProductos()).append(" €\n\n");

		sb.append("Productos incluidos:\n");

		if (pack.getLineas().isEmpty()) {
			sb.append("  Sin productos.\n");
		} else {
			for (LineaPack linea : pack.getLineas()) {
				ProductoVenta p = linea.getProducto();

				sb.append("  - ").append(p.getId()).append(" | ").append(p.getNombre()).append(" | unidades: ")
						.append(linea.getUnidades()).append(" | precio unidad: ").append(p.getPrecioOficial())
						.append(" € | subtotal: ").append(linea.getSubtotal()).append(" €\n");
			}
		}

		actualizarInfoPack(sb.toString());
	}

	private void actualizarInfoPack(String texto) {
		if (areaInfoPack != null) {
			areaInfoPack.setText(texto);
			areaInfoPack.setCaretPosition(0);
		}

		if (areaInfoPackGestion != null) {
			areaInfoPackGestion.setText(texto);
			areaInfoPackGestion.setCaretPosition(0);
		}
	}

	private void limpiarInfoPack() {
		actualizarInfoPack("");
	}

	private void actualizarVistaPrevia() {
		try {
			ArrayList<LineaPack> lineas = construirLineasPack(areaLineas.getText());

			StringBuilder sb = new StringBuilder();

			if (lineas.isEmpty()) {
				sb.append("No hay líneas añadidas.");
			} else {
				for (LineaPack linea : lineas) {
					ProductoVenta p = linea.getProducto();

					sb.append("- ").append(p.getId()).append(" | ").append(p.getNombre()).append(" | unidades: ")
							.append(linea.getUnidades()).append(" | subtotal: ").append(linea.getSubtotal())
							.append(" €\n");
				}
			}

			areaVistaPrevia.setText(sb.toString());
			areaVistaPrevia.setCaretPosition(0);

		} catch (Exception ex) {
			areaVistaPrevia.setText("No se puede mostrar la vista previa: " + ex.getMessage());
		}
	}

	private boolean datosProductoPackValidos(Integer unidades) {
		if (campoIdPack.getText().trim().isBlank()) {
			mostrarError("Introduce el ID del pack.");
			return false;
		}

		if (campoIdProducto.getText().trim().isBlank()) {
			mostrarError("Introduce el ID del producto.");
			return false;
		}

		if (unidades == null || unidades <= 0) {
			mostrarError("Introduce unidades válidas.");
			return false;
		}

		return true;
	}

	private void estilizarTablaPacks(JTable tabla) {
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