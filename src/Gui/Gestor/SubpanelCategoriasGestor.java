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
 * Subpanel de gestión de categorías para el gestor. Permite crear, eliminar,
 * listar y buscar categorías, además de añadir o quitar categorías a
 * productos.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelCategoriasGestor extends AbstractPanelGestor {

	private static final long serialVersionUID = 1L;

	/** Controlador asociado al subpanel. */
	private ControladorCategoriasGestor controlador;

	/** Campo de texto para el nombre de la categoría. */
	private JTextField campoNombre;
	
	/** Campo de texto para la descripción de la categoría. */
	private JTextField campoDesc;
	
	/** Campo de texto para el ID del producto. */
	private JTextField campoIdProducto;
	
	/** ComboBox de selección de categorías. */
	private JComboBox<String> comboCategoria;
	
	/** Tabla de productos de venta. */
	private TablaProductosVenta tablaProductosVenta;

	/** Panel que contiene la lista de categorías. */
	private JPanel panelListaCategorias;
	
	/** Campo de búsqueda de categorías. */
	private JTextField campoBusquedaCategorias;

	/** Botón para crear categorías. */
	private JButton botonCrear;
	
	/** Botón para añadir categoría a un producto. */
	private JButton botonAnadirCategoria;
	
	/** Botón para quitar categoría de un producto. */
	private JButton botonQuitarCategoria;

	/** Referencia al panel principal del gestor. */
	private PanelGestor panelGestor;

	/**
	 * Constructor del subpanel de categorías del gestor.
	 *
	 * @param ventana      Ventana principal
	 * @param gestor       Gestor logueado
	 * @param panelGestor  Panel principal del gestor
	 */
	public SubpanelCategoriasGestor(VentanaPrincipal ventana, Gestor gestor, PanelGestor panelGestor) {
		super(ventana, gestor);
		this.panelGestor = panelGestor;
		this.controlador = new ControladorCategoriasGestor(this, gestor);
		inicializarUI();
	}

	/**
	 * Construye la interfaz gráfica del subpanel.
	 */
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
		contenido.add(crearBloqueListaCategorias());
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

	/**
	 * Registra el controlador en los botones del subpanel.
	 *
	 * @param c Controlador a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonCrear != null) {
			for (ActionListener al : botonCrear.getActionListeners())
				botonCrear.removeActionListener(al);
			botonCrear.addActionListener(c);
		}
		if (botonAnadirCategoria != null) {
			for (ActionListener al : botonAnadirCategoria.getActionListeners())
				botonAnadirCategoria.removeActionListener(al);
			botonAnadirCategoria.addActionListener(c);
		}
		if (botonQuitarCategoria != null) {
			for (ActionListener al : botonQuitarCategoria.getActionListeners())
				botonQuitarCategoria.removeActionListener(al);
			botonQuitarCategoria.addActionListener(c);
		}
	}

	/**
	 * Crea el formulario de creación de nuevas categorías.
	 *
	 * @return Panel del formulario
	 */
	private JPanel crearFormularioNuevaCategoria() {
		JPanel panel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(10)));
		panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
		panel.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1),
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

	/**
	 * Crea el bloque visual de listado de categorías con buscador.
	 *
	 * @return Panel del bloque de categorías
	 */
	private JPanel crearBloqueListaCategorias() {
		JPanel bloque = crearBloque("Categorías creadas");

		
		JPanel barraBusqueda = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(5)));
		barraBusqueda.setOpaque(false);
		barraBusqueda.add(crearLabel("Buscar:"));
		campoBusquedaCategorias = crearCampoCompacto();
		campoBusquedaCategorias
				.setPreferredSize(new Dimension(VentanaPrincipal.escalar(220), VentanaPrincipal.escalar(28)));
		escucharCambios(campoBusquedaCategorias, this::filtrarCategorias);
		barraBusqueda.add(campoBusquedaCategorias);

		
		panelListaCategorias = new JPanel();
		panelListaCategorias.setLayout(new BoxLayout(panelListaCategorias, BoxLayout.Y_AXIS));
		panelListaCategorias.setBackground(VentanaPrincipal.COLOR_FONDO);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(VentanaPrincipal.COLOR_FONDO);
		wrapper.add(panelListaCategorias, BorderLayout.NORTH);

		JScrollPane scrollCats = new JScrollPane(wrapper);
		scrollCats.setBorder(null);
		scrollCats.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollCats.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		scrollCats.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(220)));

		JPanel panelContenido = new JPanel(new BorderLayout());
		panelContenido.setOpaque(false);
		panelContenido.add(barraBusqueda, BorderLayout.NORTH);
		panelContenido.add(scrollCats, BorderLayout.CENTER);

		bloque.add(panelContenido, gbcCampo(1));

		actualizarListaCategorias();
		return bloque;
	}

	/**
	 * Filtra la lista de categorías por nombre 
	 */
	private void filtrarCategorias() {
		String texto = campoBusquedaCategorias.getText().trim().toLowerCase();
		panelListaCategorias.removeAll();
		boolean hayAlguna = false;
		for (Categoria c : controlador.getCategorias()) {
			if (texto.isEmpty() || c.getNombre().toLowerCase().contains(texto)) {
				panelListaCategorias.add(crearFilaCategoria(c));
				hayAlguna = true;
			}
		}
		if (!hayAlguna) {
			JLabel lbl = crearLabel("No se encontraron categorías.");
			lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelListaCategorias.add(lbl);
		}
		panelListaCategorias.revalidate();
		panelListaCategorias.repaint();
	}

	/**
	 * Recarga la lista completa de categorías.
	 */
	public void actualizarListaCategorias() {
		panelListaCategorias.removeAll();
		List<Categoria> cats = controlador.getCategorias();
		if (cats.isEmpty()) {
			JLabel lbl = crearLabel("No hay categorías creadas.");
			lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelListaCategorias.add(lbl);
		} else {
			for (Categoria c : cats)
				panelListaCategorias.add(crearFilaCategoria(c));
		}
		panelListaCategorias.revalidate();
		panelListaCategorias.repaint();
	}

	/**
	 * Crea una fila visual para representar una categoría.
	 *
	 * @param c Categoría a mostrar
	 * @return Panel de la fila de categoría
	 */
	private JPanel crearFilaCategoria(Categoria c) {
		boolean eliminada = c.isEliminada();

		JPanel fila = new JPanel(new BorderLayout());
		fila.setBackground(eliminada ? new Color(245, 245, 245) : VentanaPrincipal.COLOR_TARJETA);
		fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(55)));
		fila.setAlignmentX(Component.LEFT_ALIGNMENT);
		fila.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15))));

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

		JLabel labelNombre = new JLabel(c.getNombre() + (eliminada ? "  -  Eliminada" : ""));
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(eliminada ? VentanaPrincipal.COLOR_TEXTO2 : VentanaPrincipal.COLOR_TEXTO);
		info.add(labelNombre);

		JLabel labelDesc = crearLabel(
				c.getDescripcion() != null && !c.getDescripcion().isBlank() ? c.getDescripcion() : "Sin descripción");
		info.add(labelDesc);

		fila.add(info, BorderLayout.CENTER);

		// Botón eliminar solo si no está eliminada
		if (!eliminada) {
			JButton botonEliminar = crearBotonRojo("Eliminar");
			botonEliminar.setActionCommand(ControladorCategoriasGestor.ELIMINAR_CATEGORIA + ":" + c.getNombre());
			botonEliminar.addActionListener(controlador);
			fila.add(botonEliminar, BorderLayout.EAST);
		}

		return fila;
	}

	/**
	 * Crea el panel que contiene la tabla de productos de venta.
	 *
	 * @return Panel con la tabla
	 */
	private JPanel crearPanelTablaProductos() {
	    JPanel panel = crearBloque("Productos de venta");
	    tablaProductosVenta = new TablaProductosVenta(
	        () -> controlador.getProductosOrdenados(), false);

	    JButton botonRefrescar = crearBotonNaranja("Refrescar");
	    botonRefrescar.setActionCommand("refrescarTablaCategoria");
	    botonRefrescar.addActionListener(controlador);

	    JPanel filaBoton = new JPanel(
	        new FlowLayout(FlowLayout.LEFT, 0, 0));
	    filaBoton.setOpaque(false);
	    filaBoton.add(botonRefrescar);

	    panel.add(tablaProductosVenta, gbcCampo(1));
	    panel.add(filaBoton, gbcBoton(2));
	    return panel;
	}

	/**
	 * Refresca la tabla 
	 */
	public void refrescarTablaProductos() {
	    if (tablaProductosVenta != null)
	        tablaProductosVenta.refrescar();
	}

	/**
	 * Crea el bloque de cambio de categorías para productos.
	 *
	 * @return Panel del bloque
	 */
	private JPanel crearBloqueCambiarCategoriaProducto() {
		JPanel bloque = crearBloque("Cambiar categoría de un producto");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelAcciones.setOpaque(false);
		panelAcciones.add(crearPanelDatosCategoria());
		panelAcciones.add(crearPanelBotonesCategoria());

		bloque.add(panelAcciones, gbcCampo(1));
		return bloque;
	}

	/**
	 * Crea el panel de datos de categorías 
	 *
	 * @return Panel de datos
	 */
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

	/**
	 * Crea el panel de botones de modificación de categorías.
	 *
	 * @return Panel de botones
	 */
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

	/**
	 * Procesa la creación de una nueva categoría.
	 */
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
			actualizarListaCategorias();
			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
				tablaProductosVenta.refrescarFiltrosCategorias();
			}
		} else {
			mostrarError("No se pudo crear la categoría.");
		}
		if (panelGestor != null)
			panelGestor.refrescarFiltrosCategorias();
	}

	/**
	 * Procesa la eliminación de una categoría.
	 *
	 * @param nombre Nombre de la categoría
	 */
	public void procesarEliminarCategoria(String nombre) {
		int confirm = JOptionPane.showConfirmDialog(this,
				"¿Seguro que quieres eliminar la categoría \"" + nombre + "\"?\nSe quitará de todos los productos.",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION)
			return;
		if (controlador.eliminarCategoria(nombre)) {
			mostrarMensaje("Categoría '" + nombre + "' eliminada.");
			// Actualizar combo, lista y tabla
			cargarCategorias();
			actualizarListaCategorias();
			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
				tablaProductosVenta.refrescarFiltrosCategorias();
			}
		} else {
			mostrarError("No se pudo eliminar la categoría.");
		}
		if (panelGestor != null)
			panelGestor.refrescarFiltrosCategorias();
	}

	/**
	 * Procesa la asignación de una categoría a un producto.
	 */
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
			if (tablaProductosVenta != null)
				tablaProductosVenta.refrescar();
		} else {
			mostrarError("No se pudo añadir la categoría.");
		}
	}

	/**
	 * Procesa la eliminación de una categoría de un producto.
	 */
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
		if (!controlador.productoTieneCategoria(idProducto, nombreCategoria)) {
			mostrarError("Ese producto no tiene esa categoría.");
			return;
		}
		if (controlador.eliminarProductoDeCategoria(idProducto, nombreCategoria)) {
			mostrarMensaje("Categoría quitada correctamente.");
			if (tablaProductosVenta != null)
				tablaProductosVenta.refrescar();
		} else {
			mostrarError("No se pudo quitar la categoría.");
		}
	}

	/**
	 * Devuelve la categoría actualmente seleccionada en el combo.
	 *
	 * @return Nombre de la categoría seleccionada o null
	 */
	private String obtenerCategoriaSeleccionada() {
		Object sel = comboCategoria.getSelectedItem();
		if (sel == null)
			return null;
		String cat = String.valueOf(sel);
		return cat.equals("Selecciona una categoría") ? null : cat;
	}

	/**
	 * Recarga el combo de categorías con los datos actuales de la tienda. Se llama
	 * tras crear o eliminar una categoría para actualizar el combo sin reiniciar.
	 */
	public void cargarCategorias() {
		comboCategoria.removeAllItems();
		comboCategoria.addItem("Selecciona una categoría");
		for (Categoria c : controlador.getCategoriasActivas())
			comboCategoria.addItem(c.getNombre());
	}
}
