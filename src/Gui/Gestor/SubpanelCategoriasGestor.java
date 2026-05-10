package Gui.Gestor;

import Gui.TablaProductosVenta;
import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorCategoriasGestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import productos.Categoria;
import usuarios.Gestor;

/**
 * Subpanel de gestión de categorías para el gestor.
 *
 * Permite crear categorías y añadir o quitar categorías a productos.
 */
public class SubpanelCategoriasGestor extends AbstractPanelGestor {

	private static final long serialVersionUID = 1L;

	private ControladorCategoriasGestor controlador;

	private JTextField campoNombre;
	private JTextField campoDesc;

	private JTextField campoIdProducto;
	private JComboBox<String> comboCategoria;

	private TablaProductosVenta tablaProductosVenta;

	private JButton botonCrear;
	private JButton botonAnadirCategoria;
	private JButton botonQuitarCategoria;

	public SubpanelCategoriasGestor(VentanaPrincipal ventana, Gestor gestor) {
		super(ventana, gestor);
		this.controlador = new ControladorCategoriasGestor(this, gestor);
		inicializarUI();
	}

	private void inicializarUI() {
		setLayout(new BorderLayout());

		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		contenido.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

		campoIdProducto = crearCampo();
		comboCategoria = crearCombo(new String[] { "Selecciona una categoría" });
		cargarCategorias();

		contenido.add(crearFormularioNuevaCategoria());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearPanelTablaProductos());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCambiarCategoriaProducto());

		JScrollPane scroll = new JScrollPane(contenido);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));

		add(scroll, BorderLayout.CENTER);

		setControlador(controlador);
	}

	public void setControlador(ActionListener c) {
		if (botonCrear != null) {
			for (ActionListener al : botonCrear.getActionListeners()) {
				botonCrear.removeActionListener(al);
			}
			botonCrear.addActionListener(c);
		}

		if (botonAnadirCategoria != null) {
			for (ActionListener al : botonAnadirCategoria.getActionListeners()) {
				botonAnadirCategoria.removeActionListener(al);
			}
			botonAnadirCategoria.addActionListener(c);
		}

		if (botonQuitarCategoria != null) {
			for (ActionListener al : botonQuitarCategoria.getActionListeners()) {
				botonQuitarCategoria.removeActionListener(al);
			}
			botonQuitarCategoria.addActionListener(c);
		}
	}

	private JPanel crearFormularioNuevaCategoria() {
		JPanel panel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));

		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));

		JLabel titulo = new JLabel("Nueva categoría:");
		titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panel.add(titulo);

		panel.add(crearLabel("Nombre:"));
		campoNombre = crearCampoColumnas(10);
		panel.add(campoNombre);

		panel.add(crearLabel("Descripción:"));
		campoDesc = crearCampoColumnas(20);
		panel.add(campoDesc);

		botonCrear = crearBotonNaranja("Crear categoría");
		botonCrear.setActionCommand(ControladorCategoriasGestor.CREAR_CATEGORIA);
		panel.add(botonCrear);

		return panel;
	}

	private JPanel crearPanelTablaProductos() {
		JPanel panel = crearBloque("Productos de venta");

		tablaProductosVenta = new TablaProductosVenta(() -> controlador.getProductosOrdenados());
		tablaProductosVenta.setAlSeleccionarId(id -> campoIdProducto.setText(id));

		panel.add(tablaProductosVenta, gbcCampo(1));

		return panel;
	}

	private JPanel crearBloqueCambiarCategoriaProducto() {
		JPanel bloque = crearBloque("Cambiar categoría de un producto");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelDatosCategoria());
		panelAcciones.add(crearPanelBotonesCategoria());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	private JPanel crearPanelDatosCategoria() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearCampoFormulario("ID producto", campoIdProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Categoría", comboCategoria));

		return panel;
	}

	private JPanel crearPanelBotonesCategoria() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		botonAnadirCategoria = crearBotonNaranja("Añadir categoría");
		botonAnadirCategoria.setActionCommand(ControladorCategoriasGestor.ANADIR_CATEGORIA_PRODUCTO);

		botonQuitarCategoria = crearBotonNaranja("Quitar categoría");
		botonQuitarCategoria.setActionCommand(ControladorCategoriasGestor.QUITAR_CATEGORIA_PRODUCTO);

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);

		filaBotones.add(botonAnadirCategoria);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonQuitarCategoria);

		panel.add(filaBotones);

		return panel;
	}

	public void procesarCrearCategoria() {
		String nombre = campoNombre.getText().trim();
		String desc = campoDesc.getText().trim();

		if (nombre.isEmpty()) {
			mostrarError("El nombre no puede estar vacío.");
			return;
		}

		if (controlador.crearCategoria(nombre, desc)) {
			mostrarMensaje("Categoría '" + nombre + "' creada.");

			campoNombre.setText("");
			campoDesc.setText("");

			cargarCategorias();

			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
			}
		} else {
			mostrarError("No se pudo crear la categoría.");
		}
	}

	public void procesarAnadirCategoriaProducto() {
		String idProducto = campoIdProducto.getText().trim();
		String nombreCategoria = obtenerCategoriaSeleccionada();

		if (idProducto.isEmpty()) {
			mostrarError("Selecciona o escribe el ID del producto.");
			return;
		}

		if (nombreCategoria == null) {
			mostrarError("Selecciona una categoría.");
			return;
		}

		if (controlador.añadirProductoACategoria(idProducto, nombreCategoria)) {
			mostrarMensaje("Categoría añadida correctamente.");

			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
			}
		} else {
			mostrarError("No se pudo añadir la categoría.");
		}
	}

	public void procesarQuitarCategoriaProducto() {
		String idProducto = campoIdProducto.getText().trim();
		String nombreCategoria = obtenerCategoriaSeleccionada();

		if (idProducto.isEmpty()) {
			mostrarError("Selecciona o escribe el ID del producto.");
			return;
		}

		if (nombreCategoria == null) {
			mostrarError("Selecciona una categoría.");
			return;
		}

		if (controlador.eliminarProductoDeCategoria(idProducto, nombreCategoria)) {
			mostrarMensaje("Categoría quitada correctamente.");

			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
			}
		} else {
			mostrarError("No se pudo quitar la categoría.");
		}
	}

	private String obtenerCategoriaSeleccionada() {
		Object seleccionado = comboCategoria.getSelectedItem();

		if (seleccionado == null) {
			return null;
		}

		String categoria = String.valueOf(seleccionado);

		if (categoria.equals("Selecciona una categoría")) {
			return null;
		}

		return categoria;
	}

	private void cargarCategorias() {
		comboCategoria.removeAllItems();
		comboCategoria.addItem("Selecciona una categoría");

		List<Categoria> categorias = controlador.getCategorias();

		for (Categoria categoria : categorias) {
			comboCategoria.addItem(categoria.getNombre());
		}
	}
}