package Gui.Gestor;

import Gui.TablaProductosVenta;
import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorProductosDescuentosGestor;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

import productos.Categoria;
import productos.ProductoVenta;
import usuarios.Gestor;
import ventas.Descuento;
import ventas.DescuentoCantidad;
import ventas.DescuentoCategoria;
import ventas.DescuentoVolumen;
import ventas.Regalo;

/**
 * Subpanel de gestión de productos y descuentos para el gestor.
 *
 * Permite consultar productos, modificar precios y crear/eliminar descuentos.
 * 
 * @author Antonino
 * @version 1.0
 */
public class SubpanelProductosDescuentosGestor extends AbstractPanelGestor {

	private static final long serialVersionUID = 1L;

	/** Controlador del subpanel */
	private ControladorProductosDescuentosGestor controlador;

	/** Panel que contiene la lista de descuentos */
	private JPanel panelDescuentos;

	/** Panel de parámetros de descuento */
	private JPanel panelParametros;

	/** Layout para alternar tipos de parámetros de descuento */
	private CardLayout cardParametros;

	/** Campo para el nombre del descuento */
	private JTextField campoNombreDescuento;

	/** Duración en días del descuento */
	private JSpinner spinnerDias;

	/** Selector del tipo de descuento */
	private JComboBox<String> comboTipoDescuento;

	/** Mínimo de gasto para descuento por volumen */
	private JSpinner spinnerGastoMin;

	/** Porcentaje de descuento por volumen */
	private JSpinner spinnerPorcentajeVol;

	/** Panel selector de categoría */
	private JPanel panelComboCat;

	/** Porcentaje de descuento por categoría */
	private JSpinner spinnerPorcentajeCat;

	/** Cantidad mínima para descuento por cantidad */
	private JSpinner spinnerCantidadMin;

	/** Selector de producto para descuento por cantidad */
	private JPanel panelComboProdCant;

	/** Porcentaje de descuento por cantidad */
	private JSpinner spinnerPorcentajeCant;

	/** Selector de producto regalo */
	private JPanel panelComboProdRegalo;

	/** Gasto mínimo para obtener regalo */
	private JSpinner spinnerGastoRegalo;

	/** Campo de búsqueda de descuentos */
	private JTextField campoBusquedaDescuentos;

	/** Campo de ID para cambio de precio */
	private JTextField campoIdPrecio;

	/** Spinner de nuevo precio manual */
	private JSpinner spinnerPrecioManual;

	/** Tabla de productos de venta */
	private TablaProductosVenta tablaProductosVenta;

	/** Filtro por tipo de descuento */
	private JComboBox<String> comboFiltroTipoDescuentos;

	/** Botón para cambiar precio de producto */
	private JButton botonCambiarPrecio;

	/** Botón para crear un nuevo descuento */
	private JButton botonCrearDescuento;

	/**
	 * Constructor del subpanel de productos y descuentos.
	 *
	 * @param ventana ventana principal de la aplicación
	 * @param gestor  gestor actualmente autenticado
	 */
	public SubpanelProductosDescuentosGestor(VentanaPrincipal ventana, Gestor gestor) {
		super(ventana, gestor);
		this.controlador = new ControladorProductosDescuentosGestor(this, gestor);
		inicializarUI();
	}

	/**
	 * Inicializa toda la interfaz del subpanel.
	 */
	private void inicializarUI() {
		setLayout(new BorderLayout());

		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		contenido.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

		contenido.add(crearPanelProductosTabla());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearPanelCambiarPrecio());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearPanelDescuentos());

		JScrollPane scroll = new JScrollPane(contenido);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));

		add(scroll, BorderLayout.CENTER);
	}

	/**
	 * Crea el panel con la tabla de productos de venta.
	 *
	 * @return panel con la tabla de productos
	 */
	private JPanel crearPanelProductosTabla() {
		JPanel panel = crearBloque("Productos de venta");

		tablaProductosVenta = new TablaProductosVenta(() -> controlador.getProductos(), false);
		tablaProductosVenta.setAlSeleccionarId(id -> campoIdPrecio.setText(id));

		JButton botonRefrescar = crearBotonNaranja("Refrescar");
		botonRefrescar.setActionCommand("refrescarTablaProductos");
		botonRefrescar.addActionListener(controlador);

		JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBoton.setOpaque(false);
		filaBoton.add(botonRefrescar);

		panel.add(tablaProductosVenta, gbcCampo(1));
		panel.add(filaBoton, gbcBoton(2));

		return panel;
	}

	/**
	 * refresca el contenido de la tabla cuando haya habiado algun cambio
	 * 
	 */
	public void refrescarTablaProductos() {
		if (tablaProductosVenta != null)
			tablaProductosVenta.refrescar();
	}

	/**
	 * Crea el bloque para cambiar manualmente el precio de un producto.
	 *
	 * @return panel con formulario de cambio de precio
	 */
	private JPanel crearPanelCambiarPrecio() {
		JPanel bloque = crearBloque("Cambiar precio de un producto");

		campoIdPrecio = crearCampo();

		spinnerPrecioManual = new JSpinner(new SpinnerNumberModel(1.0, 0.01, 9999.0, 0.5));
		spinnerPrecioManual.setFont(VentanaPrincipal.FUENTE_NORMAL);

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelDatosPrecio());
		panelAcciones.add(crearPanelBotonPrecio());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	/**
	 * Crea el formulario de datos para el cambio de precio.
	 *
	 * @return panel con campos de ID y precio
	 */
	private JPanel crearPanelDatosPrecio() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearCampoFormulario("ID producto", campoIdPrecio));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Nuevo precio", spinnerPrecioManual));

		return panel;
	}

	/**
	 * Crea el botón de acción para actualizar el precio de un producto.
	 *
	 * @return panel con el botón de acción
	 */
	private JPanel crearPanelBotonPrecio() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		botonCambiarPrecio = crearBotonNaranja("Actualizar precio");
		botonCambiarPrecio.setActionCommand(ControladorProductosDescuentosGestor.CAMBIAR_PRECIO_MANUAL);
		botonCambiarPrecio.addActionListener(controlador);

		JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		fila.setOpaque(false);
		fila.add(botonCambiarPrecio);

		panel.add(fila);

		return panel;
	}

	/**
	 * Crea el bloque principal de gestión de descuentos.
	 *
	 * @return panel de descuentos
	 */
	private JPanel crearPanelDescuentos() {
		JPanel bloque = crearBloque("Descuentos");

		JPanel formulario = crearFormularioDescuento();
		formulario.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(270)));
		formulario.setMaximumSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(270)));

		bloque.add(formulario, gbcCampo(1));
		bloque.add(Box.createVerticalStrut(VentanaPrincipal.escalar(14)), gbcCampo(2));
		bloque.add(crearPanelListaDescuentos(), gbcCampo(3));

		actualizarDescuentos();

		return bloque;
	}

	/**
	 * Crea el formulario dividido en dos partes: generales y especificos
	 *
	 * @return panel del formulario de creación
	 */
	private JPanel crearFormularioDescuento() {
		JPanel panel = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panel.setOpaque(false);

		panel.add(crearPanelDatosComunesDescuento());
		panel.add(crearPanelDatosEspecificosDescuento());

		return panel;
	}

	/**
	 * Crea el panel de datos comunes a todos los descuentos.
	 *
	 * @return panel con campos generales
	 */
	private JPanel crearPanelDatosComunesDescuento() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos generales"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		comboTipoDescuento = crearCombo(new String[] { "Volumen", "Categoría", "Cantidad", "Regalo" });
		panel.add(crearCampoFormulario("Tipo de descuento", comboTipoDescuento));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		campoNombreDescuento = crearCampo();
		panel.add(crearCampoFormulario("Nombre", campoNombreDescuento));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		spinnerDias = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
		spinnerDias.setFont(VentanaPrincipal.FUENTE_NORMAL);
		panel.add(crearCampoFormulario("Duración en días", spinnerDias));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(14)));

		botonCrearDescuento = crearBotonNaranja("Crear descuento");
		botonCrearDescuento.setActionCommand(ControladorProductosDescuentosGestor.CREAR_DESCUENTO);
		botonCrearDescuento.addActionListener(controlador);

		JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBoton.setOpaque(false);
		filaBoton.add(botonCrearDescuento);

		panel.add(filaBoton);

		return panel;
	}

	/**
	 * Crea el panel de datos específicos según tipo de descuento.
	 *
	 * @return panel dinámico de parámetros
	 */
	private JPanel crearPanelDatosEspecificosDescuento() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Datos específicos"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		cardParametros = new CardLayout();
		panelParametros = new JPanel(cardParametros);
		panelParametros.setOpaque(false);

		panelParametros.add(crearPanelVolumen(), "Volumen");
		panelParametros.add(crearPanelCategoria(), "Categoría");
		panelParametros.add(crearPanelCantidad(), "Cantidad");
		panelParametros.add(crearPanelRegalo(), "Regalo");

		panel.add(panelParametros);

		comboTipoDescuento.addActionListener(e -> {
			String tipo = String.valueOf(comboTipoDescuento.getSelectedItem());
			cardParametros.show(panelParametros, tipo);
		});

		cardParametros.show(panelParametros, "Volumen");

		return panel;
	}

	/**
	 * Panel de parámetros para descuento por volumen.
	 */
	private JPanel crearPanelVolumen() {
		JPanel panel = crearPanelCamposTipoDescuento();

		spinnerGastoMin = new JSpinner(new SpinnerNumberModel(1.0, 0.01, 9999.0, 1.0));
		spinnerGastoMin.setFont(VentanaPrincipal.FUENTE_NORMAL);

		spinnerPorcentajeVol = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		spinnerPorcentajeVol.setFont(VentanaPrincipal.FUENTE_NORMAL);

		panel.add(crearCampoFormulario("Gasto mínimo (€)", spinnerGastoMin));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Porcentaje (%)", spinnerPorcentajeVol));

		return panel;
	}

	/**
	 * Panel de parámetros para descuento por categoría.
	 */
	private JPanel crearPanelCategoria() {
		JPanel panel = crearPanelCamposTipoDescuento();

		List<Categoria> cats = controlador.getCategorias();
		String[] nombresCats = new String[cats.size()];

		for (int i = 0; i < cats.size(); i++) {
			nombresCats[i] = cats.get(i).getNombre();
		}

		panelComboCat = crearComboConBuscador(nombresCats, VentanaPrincipal.escalar(220));

		spinnerPorcentajeCat = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		spinnerPorcentajeCat.setFont(VentanaPrincipal.FUENTE_NORMAL);

		panel.add(crearCampoFormulario("Categoría afectada", panelComboCat));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Porcentaje (%)", spinnerPorcentajeCat));

		return panel;
	}

	/**
	 * Panel de parámetros para descuento por cantidad.
	 */
	private JPanel crearPanelCantidad() {
		JPanel panel = crearPanelCamposTipoDescuento();

		spinnerCantidadMin = new JSpinner(new SpinnerNumberModel(2, 2, 999, 1));
		spinnerCantidadMin.setFont(VentanaPrincipal.FUENTE_NORMAL);

		panelComboProdCant = crearComboConBuscador(getNombresProductos(), VentanaPrincipal.escalar(220));

		spinnerPorcentajeCant = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		spinnerPorcentajeCant.setFont(VentanaPrincipal.FUENTE_NORMAL);

		panel.add(crearCampoFormulario("Cantidad mínima", spinnerCantidadMin));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Producto", panelComboProdCant));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Porcentaje (%)", spinnerPorcentajeCant));

		return panel;
	}

	/**
	 * Panel de parámetros para descuento tipo regalo.
	 */
	private JPanel crearPanelRegalo() {
		JPanel panel = crearPanelCamposTipoDescuento();

		panelComboProdRegalo = crearComboConBuscador(getNombresProductosSinTodos(), VentanaPrincipal.escalar(220));

		spinnerGastoRegalo = new JSpinner(new SpinnerNumberModel(1.0, 0.01, 9999.0, 1.0));
		spinnerGastoRegalo.setFont(VentanaPrincipal.FUENTE_NORMAL);

		panel.add(crearCampoFormulario("Producto regalado", panelComboProdRegalo));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearCampoFormulario("Gasto mínimo (€)", spinnerGastoRegalo));

		return panel;
	}

	/**
	 * Crea un panel base reutilizable para los formularios de datos específicos de
	 * los distintos tipos de descuento.
	 *
	 * @return JPanel vacío configurado como contenedor vertical reutilizable.
	 */
	private JPanel crearPanelCamposTipoDescuento() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		return panel;
	}

	/**
	 * Crea el panel que muestra la lista de descuentos creados
	 *
	 * @return panel con listado de descuentos
	 */
	private JPanel crearPanelListaDescuentos() {
		JPanel panelListaDescuentos = new JPanel(new BorderLayout());
		panelListaDescuentos.setOpaque(false);
		panelListaDescuentos
				.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(310)));

		JPanel barraBusquedaDesc = new JPanel(
				new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(5)));
		barraBusquedaDesc.setOpaque(false);
		barraBusquedaDesc.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, VentanaPrincipal.COLOR_BORDE));

		barraBusquedaDesc.add(crearLabel("Buscar descuento:"));

		campoBusquedaDescuentos = crearCampoCompacto();
		campoBusquedaDescuentos
				.setPreferredSize(new Dimension(VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(28)));
		escucharCambios(campoBusquedaDescuentos, this::filtrarDescuentos);
		barraBusquedaDesc.add(campoBusquedaDescuentos);

		barraBusquedaDesc.add(crearLabel("Tipo:"));

		comboFiltroTipoDescuentos = crearCombo(new String[] { "Todos", "Volumen", "Categoría", "Cantidad", "Regalo" });
		comboFiltroTipoDescuentos
				.setPreferredSize(new Dimension(VentanaPrincipal.escalar(130), VentanaPrincipal.escalar(28)));
		comboFiltroTipoDescuentos.addActionListener(e -> filtrarDescuentos());
		barraBusquedaDesc.add(comboFiltroTipoDescuentos);

		panelListaDescuentos.add(barraBusquedaDesc, BorderLayout.NORTH);

		panelDescuentos = new JPanel();
		panelDescuentos.setLayout(new BoxLayout(panelDescuentos, BoxLayout.Y_AXIS));
		panelDescuentos.setBackground(VentanaPrincipal.COLOR_FONDO);

		JPanel wrapperDesc = new JPanel(new BorderLayout());
		wrapperDesc.setBackground(VentanaPrincipal.COLOR_FONDO);
		wrapperDesc.add(panelDescuentos, BorderLayout.NORTH);

		JScrollPane scrollDesc = new JScrollPane(wrapperDesc);
		scrollDesc.setBorder(null);
		scrollDesc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollDesc.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);

		panelListaDescuentos.add(scrollDesc, BorderLayout.CENTER);

		return panelListaDescuentos;
	}

	/**
	 * Filtra los descuentos según texto y tipo seleccionado.
	 */
	private void filtrarDescuentos() {
		String texto = normalizarTexto(campoBusquedaDescuentos.getText());

		String tipoFiltro = comboFiltroTipoDescuentos == null ? "Todos"
				: String.valueOf(comboFiltroTipoDescuentos.getSelectedItem());

		panelDescuentos.removeAll();

		JLabel titulo = new JLabel("Descuentos creados:");
		titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		titulo.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
				VentanaPrincipal.escalar(5), 0));
		titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelDescuentos.add(titulo);

		for (Descuento d : controlador.getDescuentos()) {
			boolean coincideTexto = texto.isEmpty() || contieneTexto(d.getNombre(), texto)
					|| contieneTexto(d.getId(), texto);

			boolean coincideTipo = "Todos".equals(tipoFiltro) || tipoFiltro.equals(obtenerTipoDescuento(d));

			if (coincideTexto && coincideTipo) {
				panelDescuentos.add(crearFilaDescuento(d));
			}
		}

		panelDescuentos.revalidate();
		panelDescuentos.repaint();
	}

	/**
	 * Obtiene nombres de productos incluyendo opción "Todos".
	 *
	 * @return array de nombres de productos
	 */
	private String[] getNombresProductos() {
		List<ProductoVenta> productos = controlador.getProductos();
		String[] nombres = new String[productos.size() + 1];

		nombres[0] = "Todos los productos";
		for (int i = 0; i < productos.size(); i++) {
			nombres[i + 1] = productos.get(i).getNombre() + " (" + productos.get(i).getId() + ")";
		}

		return nombres;
	}

	/**
	 * Obtiene nombres de productos sin opción "Todos".
	 *
	 * @return array de nombres de productos
	 */
	private String[] getNombresProductosSinTodos() {
		List<ProductoVenta> productos = controlador.getProductos();
		String[] nombres = new String[productos.size()];

		for (int i = 0; i < productos.size(); i++) {
			nombres[i] = productos.get(i).getNombre() + " (" + productos.get(i).getId() + ")";
		}

		return nombres;
	}

	/**
	 * Actualiza completamente la lista de descuentos sin filtros.
	 */
	private void actualizarDescuentos() {
		panelDescuentos.removeAll();

		JLabel titulo = new JLabel("Descuentos creados:");
		titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		titulo.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
				VentanaPrincipal.escalar(5), 0));
		titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelDescuentos.add(titulo);

		List<Descuento> descuentos = controlador.getDescuentos();

		if (descuentos.isEmpty()) {
			JLabel labelVacio = crearLabel("No hay descuentos creados.");
			labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelDescuentos.add(labelVacio);
		} else {
			for (Descuento d : descuentos) {
				panelDescuentos.add(crearFilaDescuento(d));
			}
		}

		panelDescuentos.revalidate();
		panelDescuentos.repaint();
	}

	/**
	 * Crea la fila visual de un descuento individual.
	 *
	 * @param d descuento a representar
	 * @return panel con la fila del descuento
	 */
	private JPanel crearFilaDescuento(Descuento d) {
		JPanel fila = new JPanel(new BorderLayout());
		fila.setBackground(d.estaActivo() ? VentanaPrincipal.COLOR_TARJETA : new Color(245, 245, 245));
		fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(70)));
		fila.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15))));

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

		JLabel labelDesc = new JLabel(d.getNombre() + " - " + d.getId() + " | " + obtenerTipoDescuento(d) + " | "
				+ (d.estaActivo() ? "Activo" : "Eliminado/Caducado"));
		labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelDesc.setForeground(d.estaActivo() ? VentanaPrincipal.COLOR_TEXTO : VentanaPrincipal.COLOR_TEXTO2);
		info.add(labelDesc);

		JLabel labelDetalle = crearLabel(obtenerDetalleDescuento(d) + " | " + d.getFechaInicio().toLocalDate() + " - "
				+ d.getFechaFin().toLocalDate());
		info.add(labelDetalle);

		fila.add(info, BorderLayout.CENTER);

		// Botón eliminar solo si está activo
		if (d.estaActivo()) {
			JButton botonEliminar = crearBotonRojo("Eliminar");
			botonEliminar.setActionCommand(ControladorProductosDescuentosGestor.ELIMINAR_DESCUENTO + d.getId());
			botonEliminar.addActionListener(controlador);
			fila.add(botonEliminar, BorderLayout.EAST);
		}

		fila.setAlignmentX(Component.LEFT_ALIGNMENT);
		return fila;
	}

	/**
	 * Devuelve el tipo de descuento como String.
	 *
	 * @param descuento descuento a evaluar
	 * @return tipo de descuento
	 */
	private String obtenerTipoDescuento(Descuento descuento) {
		if (descuento instanceof DescuentoVolumen) {
			return "Volumen";
		}
		if (descuento instanceof DescuentoCategoria) {
			return "Categoría";
		}
		if (descuento instanceof DescuentoCantidad) {
			return "Cantidad";
		}
		if (descuento instanceof Regalo) {
			return "Regalo";
		}
		return "Otro";
	}

	/**
	 * Devuelve la descripción detallada del descuento.
	 *
	 * @param descuento descuento a describir
	 * @return texto descriptivo
	 */
	private String obtenerDetalleDescuento(Descuento descuento) {
		if (descuento instanceof DescuentoVolumen) {
			DescuentoVolumen d = (DescuentoVolumen) descuento;
			return "Mínimo " + String.format("%.2f EUR", d.getUmbralMinimo()) + ", descuento "
					+ String.format("%.0f%%", d.getPorcentaje() * 100);
		}

		if (descuento instanceof DescuentoCategoria) {
			DescuentoCategoria d = (DescuentoCategoria) descuento;
			String categoria = d.getCategoria() == null ? "-" : d.getCategoria().getNombre();
			return "Categoría " + categoria + ", descuento " + String.format("%.0f%%", d.getPorcentaje() * 100);
		}

		if (descuento instanceof DescuentoCantidad) {
			DescuentoCantidad d = (DescuentoCantidad) descuento;
			String producto = d.getProducto() == null ? "todos los productos" : d.getProducto().getNombre();
			return "Producto: " + producto + ". Desde " + d.getCantidadMinima() + " uds., descuento "
					+ String.format("%.0f%%", d.getPorcentaje() * 100);
		}

		if (descuento instanceof Regalo) {
			Regalo d = (Regalo) descuento;
			String producto = d.getProductoRegalo() == null ? "-" : d.getProductoRegalo().getNombre();
			return "Regalo " + producto + " desde " + String.format("%.2f EUR", d.getUmbral());
		}

		return "Sin detalle";
	}

	/**
	 * Procesa el cambio manual de precio de un producto.
	 */
	public void procesarCambiarPrecioManual() {
		String idProducto = campoIdPrecio.getText().trim();
		double nuevo = ((Number) spinnerPrecioManual.getValue()).doubleValue();

		if (idProducto.isEmpty()) {
			mostrarError("Introduce el ID del producto.");
			return;
		}

		if (controlador.modificarPrecio(idProducto, nuevo)) {
			mostrarMensaje("Precio actualizado.");

			if (tablaProductosVenta != null) {
				tablaProductosVenta.refrescar();
			}
		} else {
			mostrarError("No se pudo modificar el precio.");
		}
	}

	/**
	 * Procesa la creación de un nuevo descuento según el tipo seleccionado.
	 */
	public void procesarCrearDescuento() {
		String nombre = campoNombreDescuento.getText().trim();

		if (nombre.isEmpty()) {
			mostrarError("El nombre no puede estar vacío.");
			return;
		}

		String tipo = String.valueOf(comboTipoDescuento.getSelectedItem());
		int dias = (int) spinnerDias.getValue();

		LocalDateTime inicio = LocalDateTime.now();
		LocalDateTime fin = inicio.plusDays(dias);

		boolean ok = false;
		List<ProductoVenta> productos = controlador.getProductos();

		switch (tipo) {
		case "Volumen":
			ok = controlador.crearDescuentoVolumen(nombre, ((Number) spinnerGastoMin.getValue()).doubleValue(),
					((Number) spinnerPorcentajeVol.getValue()).doubleValue(), inicio, fin);
			break;

		case "Categoría":
			JComboBox<String> comboCat = getComboDePanel(panelComboCat);
			String cat = comboCat != null ? String.valueOf(comboCat.getSelectedItem()) : "";

			ok = controlador.crearDescuentoCategoria(nombre, cat,
					((Number) spinnerPorcentajeCat.getValue()).doubleValue(), inicio, fin);
			break;

		case "Cantidad":
			JComboBox<String> comboCant = getComboDePanel(panelComboProdCant);
			String selCant = comboCant != null ? String.valueOf(comboCant.getSelectedItem()) : "";
			String idCant = extraerIdDeTexto(selCant, productos);

			ok = controlador.crearDescuentoCantidad(nombre, idCant, (int) spinnerCantidadMin.getValue(),
					((Number) spinnerPorcentajeCant.getValue()).doubleValue(), inicio, fin);
			break;

		case "Regalo":
			JComboBox<String> comboRegalo = getComboDePanel(panelComboProdRegalo);
			String selRegalo = comboRegalo != null ? String.valueOf(comboRegalo.getSelectedItem()) : "";
			String idRegalo = extraerIdDeTexto(selRegalo, productos);
			if (idRegalo.isEmpty()) {
				mostrarError("Selecciona el producto que se va a regalar.");
				return;
			}

			ok = controlador.crearDescuentoRegalo(nombre, idRegalo,
					((Number) spinnerGastoRegalo.getValue()).doubleValue(), inicio, fin);
			break;
		}

		if (ok) {
			mostrarMensaje("Descuento '" + nombre + "' creado correctamente.");
			campoNombreDescuento.setText("");
			actualizarDescuentos();
		} else {
			mostrarError("No se pudo crear el descuento. Comprueba los datos.");
		}
	}

	/**
	 * Procesa la eliminación de un descuento existente.
	 *
	 * @param id identificador del descuento
	 */
	public void procesarEliminarDescuento(String id) {
		if (controlador.eliminarDescuento(id)) {
			mostrarMensaje("Descuento eliminado.");
			actualizarDescuentos();
		} else {
			mostrarError("No se pudo eliminar.");
		}
	}

	/**
	 * Extrae el ID de un producto desde texto seleccionado en combo.
	 *
	 * @param texto     texto seleccionado
	 * @param productos lista de productos
	 * @return ID del producto o cadena vacía
	 */
	private String extraerIdDeTexto(String texto, List<ProductoVenta> productos) {
		if (texto == null) {
			return "";
		}

		String idSeleccionado = extraerIdEntreParentesis(texto);
		if (!idSeleccionado.isEmpty()) {
			for (ProductoVenta p : productos) {
				if (p != null && p.getId() != null && p.getId().equalsIgnoreCase(idSeleccionado)) {
					return p.getId();
				}
			}
			return "";
		}

		for (ProductoVenta p : productos) {
			if (p != null && p.getId() != null && texto.trim().equalsIgnoreCase(p.getId())) {
				return p.getId();
			}
		}

		return "";
	}

	/**
	 * Extrae el ID entre paréntesis en un string.
	 *
	 * @param texto texto con formato "nombre (ID)"
	 * @return ID extraído o vacío
	 */
	private String extraerIdEntreParentesis(String texto) {
		String limpio = texto.trim();
		int abre = limpio.lastIndexOf('(');
		int cierra = limpio.lastIndexOf(')');

		if (abre < 0 || cierra <= abre) {
			return "";
		}

		return limpio.substring(abre + 1, cierra).trim();
	}

	/**
	 * Refresca los filtros de categorías de la tabla de productos. Lo llama
	 * PanelGestor cuando se crea o elimina una categoría.
	 */
	public void refrescarFiltrosCategorias() {
		if (tablaProductosVenta != null)
			tablaProductosVenta.refrescarFiltrosCategorias();
	}
}
