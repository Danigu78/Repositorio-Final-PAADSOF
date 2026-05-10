package Gui.cliente;

import tienda.Notificacion;
import tienda.TipoNotificacion;
import usuarios.Cliente;
import javax.swing.*;
import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorNotificaciones;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Subpanel de notificaciones del cliente. Muestra la bandeja, acciones,
 * preferencias de notificación y categorías de interés. Extiende
 * AbstractPanelCliente para reutilizar helpers visuales del cliente. Sigue el
 * patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelNotificaciones extends AbstractPanelCliente {

	/** Controlador del subpanel. */
	private ControladorNotificaciones controlador;

	/** Modelo de la lista de notificaciones. */
	private DefaultListModel<String> modeloNotificaciones;

	/** Lista visual de notificaciones. */
	private JList<String> listaNotificaciones;

	/** Lista paralela de notificaciones mostradas para recuperar el objeto. */
	private List<Notificacion> notificacionesMostradas;

	/** Combo de filtro por estado de lectura. */
	private JComboBox<String> comboFiltro;

	/** Combo de tipos de notificación configurables. */
	private JComboBox<String> comboTipos;

	/** Combo de categorías de la tienda. */
	private JComboBox<String> comboCategoriasTienda;

	/** Label que muestra las categorías de interés activas en línea. */
	private JLabel labelCategoriasActivas;

	/** Label que muestra el estado actual de la preferencia seleccionada. */
	private JLabel labelEstadoPreferencia;

	/** Label con el resumen de notificaciones mostradas. */
	private JLabel labelResumen;

	/** Botones — atributos para registrar el controlador. */
	private JButton botonRefrescar;
	private JButton botonVer;
	private JButton botonMarcarTodas;
	private JButton botonActivar;
	private JButton botonDesactivar;
	private JButton botonAñadirCategoria;
	private JButton botonQuitarCategoria;

	/**
	 * Tipos configurables — los obligatorios no aparecen.
	 */
	private static final TipoNotificacion[] TIPOS_CONFIGURABLES = { TipoNotificacion.DESCUENTO,
			TipoNotificacion.PEDIDO_CADUCADO, TipoNotificacion.PRODUCTO_INTERCAMBIO_NUEVO,
			TipoNotificacion.PEDIDO_ENTREGADO, TipoNotificacion.VALORACION_COMPLETADA,
			TipoNotificacion.OFERTA_CADUCADA };

	/** Nombres legibles para el combo de tipos configurables. */
	private static final String[] NOMBRES_TIPOS = { "Descuentos y promociones", "Pedidos caducados sin pagar",
			"Nuevos productos de intercambio", "Pedido entregado", "Tasación completada", "Oferta caducada" };

	/**
	 * Constructor del subpanel de notificaciones.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelNotificaciones(VentanaPrincipal ventana) {
		super(ventana);
		notificacionesMostradas = new ArrayList<>();
	}

	/**
	 * Actualiza el subpanel con los datos del cliente. Crea el controlador y lo
	 * registra en los botones.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorNotificaciones(this, cliente);
		removeAll();

		// crearPanelBase() y getContenido() de AbstractPanelSection
		JPanel panelBase = crearPanelBase("");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloqueBandeja());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAcciones());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloquePreferencias());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueCategorias());

		add(panelBase, BorderLayout.CENTER);

		setControlador(controlador);
		cargarNotificaciones();
		actualizarListaCategorias(controlador.getCategoriasTienda(), controlador.getCategoriasInteres());
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en los botones — patrón de los apuntes.
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		registrar(botonRefrescar, c, "refrescar");
		registrar(botonVer, c, "ver");
		registrar(botonMarcarTodas, c, "marcarTodas");
		registrar(botonActivar, c, "activar");
		registrar(botonDesactivar, c, "desactivar");
		registrar(botonAñadirCategoria, c, "añadirCategoria");
		registrar(botonQuitarCategoria, c, "quitarCategoria");
	}

	/**
	 * Registra un ActionListener en un botón quitando los anteriores.
	 *
	 * @param boton El botón a registrar
	 * @param c     El listener a añadir
	 * @param cmd   El ActionCommand a asignar
	 */
	private void registrar(JButton boton, ActionListener c, String cmd) {
		if (boton == null)
			return;
		for (ActionListener al : boton.getActionListeners())
			boton.removeActionListener(al);
		boton.setActionCommand(cmd);
		boton.addActionListener(c);
	}

	/**
	 * Crea el bloque de la bandeja de notificaciones.
	 *
	 * @return Panel del bloque bandeja
	 */
	private JPanel crearBloqueBandeja() {
		// crearBloque() de AbstractPanelSection
		JPanel bloque = crearBloque("Bandeja de notificaciones");

		notificacionesMostradas = new ArrayList<>();
		modeloNotificaciones = new DefaultListModel<>();
		listaNotificaciones = new JList<>(modeloNotificaciones);
		estilizarLista();

		// crearCombo() de AbstractPanelSection
		comboFiltro = crearCombo(new String[] { "Todas", "No vistas", "Vistas" });
		comboFiltro.addActionListener(e -> cargarNotificaciones());

		// crearBotonSecundario() de AbstractPanelSection
		botonRefrescar = crearBotonSecundario("Refrescar");
		botonRefrescar.setActionCommand("refrescar");

		// crearLabel() de AbstractPanelSection
		labelResumen = crearLabel("");

		JPanel filaFiltro = new JPanel(new BorderLayout(VentanaPrincipal.escalar(12), 0));
		filaFiltro.setOpaque(false);

		JPanel zonaFiltro = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(4)));
		zonaFiltro.setOpaque(false);
		zonaFiltro.add(crearLabel("Mostrar"), BorderLayout.NORTH);
		zonaFiltro.add(comboFiltro, BorderLayout.CENTER);

		JPanel zonaBoton = new JPanel(new BorderLayout());
		zonaBoton.setOpaque(false);
		zonaBoton.add(botonRefrescar, BorderLayout.SOUTH);

		filaFiltro.add(zonaFiltro, BorderLayout.CENTER);
		filaFiltro.add(zonaBoton, BorderLayout.EAST);

		// estilizarScroll() de AbstractPanelSection
		JScrollPane scrollLista = estilizarScroll(listaNotificaciones);
		scrollLista.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(330)));

		// gbcCampo() de AbstractPanelSection
		bloque.add(labelResumen, gbcCampo(1));
		bloque.add(filaFiltro, gbcCampo(2));
		bloque.add(scrollLista, gbcCampo(3));

		return bloque;
	}

	/**
	 * Crea el bloque de acciones sobre notificaciones.
	 *
	 * @return Panel del bloque acciones
	 */
	private JPanel crearBloqueAcciones() {
		// crearBloque() de AbstractPanelSection
		JPanel bloque = crearBloque("Consultar notificación");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelAcciones.setOpaque(false);
		panelAcciones.add(crearPanelAyuda());
		panelAcciones.add(crearPanelBotones());

		bloque.add(panelAcciones, gbcCampo(1));
		return bloque;
	}

	/**
	 * Crea el bloque de preferencias de notificación. Combo con tipos configurables
	 * y botones activar/desactivar.
	 *
	 * @return Panel del bloque preferencias
	 */
	private JPanel crearBloquePreferencias() {
		// crearBloque() de AbstractPanelSection
		JPanel bloque = crearBloque("Preferencias de notificación");

		JPanel panelPreferencias = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(30), 0));
		panelPreferencias.setOpaque(false);

		// Columna izquierda — explicación
		JPanel panelInfo = new JPanel();
		panelInfo.setOpaque(false);
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.add(crearLabel("Configura qué notificaciones quieres recibir."));
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		panelInfo.add(crearLabel("Las notificaciones obligatorias no se pueden desactivar."));
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		panelInfo.add(crearLabel("(pagos, recogidas, intercambios confirmados...)"));

		// Columna derecha — combo + estado + botones
		JPanel panelControles = new JPanel();
		panelControles.setOpaque(false);
		panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));

		panelControles.add(crearLabel("Tipo de notificación:"));
		panelControles.add(Box.createVerticalStrut(VentanaPrincipal.escalar(6)));

		// crearCombo() de AbstractPanelSection
		comboTipos = crearCombo(NOMBRES_TIPOS);
		comboTipos.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboTipos.addActionListener(e -> actualizarEstadoCombo());
		panelControles.add(comboTipos);
		panelControles.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		labelEstadoPreferencia = crearLabel("");
		labelEstadoPreferencia.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelControles.add(labelEstadoPreferencia);
		panelControles.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(10), 0));
		filaBotones.setOpaque(false);
		filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);

		Dimension tam = new Dimension(VentanaPrincipal.escalar(460), VentanaPrincipal.escalar(42));
		filaBotones.setPreferredSize(tam);
		filaBotones.setMaximumSize(tam);

		// crearBotonNaranja() de AbstractPanelSection
		botonActivar = crearBotonNaranja("✔ Activar");
		botonActivar.setActionCommand("activar");

		// crearBotonRojo() de AbstractPanelSection
		botonDesactivar = crearBotonRojo("✖ Desactivar");
		botonDesactivar.setActionCommand("desactivar");

		filaBotones.add(botonActivar);
		filaBotones.add(botonDesactivar);
		panelControles.add(filaBotones);

		panelPreferencias.add(panelInfo);
		panelPreferencias.add(panelControles);

		bloque.add(panelPreferencias, gbcCampo(1));

		actualizarEstadoCombo();
		return bloque;
	}

	/**
	 * Crea el bloque de categorías de interés. Mismo estilo que permisos en
	 * SubpanelEmpleadosGestor: combo + botones arriba, categorías activas en línea
	 * abajo.
	 *
	 * @return Panel del bloque categorías
	 */
	private JPanel crearBloqueCategorias() {

		JPanel bloque = crearBloque("Categorías de interés");

		JPanel filaControles = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
		filaControles.setOpaque(false);
		filaControles.setAlignmentX(Component.LEFT_ALIGNMENT);

		filaControles.add(crearLabel("Categoría:"));

		comboCategoriasTienda = crearCombo(new String[] {});
		comboCategoriasTienda
				.setPreferredSize(new Dimension(VentanaPrincipal.escalar(200), VentanaPrincipal.escalar(30)));
		filaControles.add(comboCategoriasTienda);

		botonAñadirCategoria = crearBotonNaranja("+ Añadir");
		botonAñadirCategoria.setActionCommand("añadirCategoria");
		filaControles.add(botonAñadirCategoria);

		botonQuitarCategoria = crearBotonRojo("- Quitar");
		botonQuitarCategoria.setActionCommand("quitarCategoria");
		filaControles.add(botonQuitarCategoria);

		JPanel filaInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaInfo.setOpaque(false);
		filaInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel labelInfo = crearLabel(
				"Recibirás una notificación cuando se añada un producto a alguna de tus categorías de interés.");
		labelInfo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		filaInfo.add(labelInfo);

		labelCategoriasActivas = crearLabel("Tus categorías favoritas: ninguna");
		labelCategoriasActivas.setAlignmentX(Component.LEFT_ALIGNMENT);

		bloque.add(filaControles, gbcCampo(1));
		bloque.add(filaInfo, gbcCampo(2));
		bloque.add(labelCategoriasActivas, gbcCampo(3));

		return bloque;
	}

	/**
	 * Actualiza el combo de categorías y el label de categorías activas en línea.
	 * Lo llama el controlador tras añadir o quitar una categoría.
	 *
	 * @param categoriasTienda  Nombres de todas las categorías de la tienda
	 * @param categoriasInteres Nombres de las categorías de interés del cliente
	 */
	public void actualizarListaCategorias(List<String> categoriasTienda, List<String> categoriasInteres) {
		if (comboCategoriasTienda != null) {
			comboCategoriasTienda.removeAllItems();
			for (String nombre : categoriasTienda)
				comboCategoriasTienda.addItem(nombre);
		}

		if (labelCategoriasActivas != null) {
			if (categoriasInteres == null || categoriasInteres.isEmpty()) {
				labelCategoriasActivas.setText("Tus categorías: ninguna");
				labelCategoriasActivas.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			} else {
				StringBuilder sb = new StringBuilder("Tus categorías de interes: ");
				for (int i = 0; i < categoriasInteres.size(); i++) {
					if (i > 0)
						sb.append("  - ");
					sb.append(categoriasInteres.get(i));
				}
				labelCategoriasActivas.setText(sb.toString());
				labelCategoriasActivas.setForeground(VentanaPrincipal.COLOR_ACENTO);
			}
		}
	}

	/**
	 * Devuelve el nombre de la categoría seleccionada en el combo. Lo usan tanto
	 * añadir como quitar — el usuario selecciona en el combo.
	 *
	 * @return Nombre de la categoría o null
	 */
	public String getCategoriaSeleccionada() {
		if (comboCategoriasTienda == null)
			return null;
		return (String) comboCategoriasTienda.getSelectedItem();
	}

	/**
	 * Devuelve la categoría seleccionada para quitar. Usa el mismo combo que para
	 * añadir.
	 *
	 * @return Nombre de la categoría o null
	 */
	public String getCategoriaIntereSeleccionada() {
		if (comboCategoriasTienda == null)
			return null;
		return (String) comboCategoriasTienda.getSelectedItem();
	}

	/**
	 * Devuelve el TipoNotificacion seleccionado en el combo de preferencias. Lo lee
	 * el controlador desde cambiarPreferencia().
	 *
	 * @return El tipo seleccionado o null
	 */
	public TipoNotificacion getTipoSeleccionado() {
		if (comboTipos == null)
			return null;
		int idx = comboTipos.getSelectedIndex();
		if (idx < 0 || idx >= TIPOS_CONFIGURABLES.length)
			return null;
		return TIPOS_CONFIGURABLES[idx];
	}

	/**
	 * Actualiza el label de estado al cambiar el combo de tipos.
	 */
	private void actualizarEstadoCombo() {
		if (controlador == null || comboTipos == null || labelEstadoPreferencia == null)
			return;
		TipoNotificacion tipo = getTipoSeleccionado();
		if (tipo == null)
			return;
		boolean activa = controlador.getEstadoPreferencia(tipo);
		labelEstadoPreferencia.setText("Estado actual: " + (activa ? " Activada" : " Desactivada"));
		labelEstadoPreferencia.setForeground(activa ? new Color(50, 150, 50) : new Color(180, 50, 50));
	}

	/**
	 * Actualiza el label de estado para un tipo concreto. Lo llama el controlador
	 * tras cambiar una preferencia.
	 *
	 * @param tipo   El tipo de notificación actualizado
	 * @param activa true si está activada
	 */
	public void actualizarEstadoPreferencia(TipoNotificacion tipo, boolean activa) {
		if (labelEstadoPreferencia == null)
			return;
		labelEstadoPreferencia.setText("Estado actual: " + (activa ? "✔ Activada" : "✖ Desactivada"));
		labelEstadoPreferencia.setForeground(activa ? new Color(50, 150, 50) : new Color(180, 50, 50));
	}

	/**
	 * Carga y muestra las notificaciones según el filtro seleccionado. Lo llama el
	 * controlador y el combo al cambiar.
	 */
	public void cargarNotificaciones() {
		if (controlador == null)
			return;
		modeloNotificaciones.clear();
		notificacionesMostradas.clear();

		String filtro = comboFiltro != null ? (String) comboFiltro.getSelectedItem() : "Todas";

		List<Notificacion> notificaciones = controlador.getNotificaciones(filtro);
		int noLeidas = controlador.contarNoLeidas();

		for (Notificacion n : notificaciones) {
			notificacionesMostradas.add(n);
			modeloNotificaciones.addElement(crearTextoLista(n));
		}

		actualizarResumen(notificaciones.size(), noLeidas);
		listaNotificaciones.clearSelection();
	}

	/**
	 * Muestra la notificación seleccionada en un diálogo. Lo llama el controlador.
	 */
	public void verNotificacionSeleccionada() {
		int posicion = listaNotificaciones.getSelectedIndex();
		if (posicion < 0) {
			mostrarError("Selecciona una notificación de la bandeja.");
			return;
		}
		Notificacion n = notificacionesMostradas.get(posicion);
		if (n == null) {
			mostrarError("No se pudo abrir la notificación.");
			return;
		}
		controlador.marcarComoLeida(n);
		mostrarNotificacionEnVentana(n);
		cargarNotificaciones();
	}

	/**
	 * Muestra el contenido completo de una notificación en un diálogo.
	 *
	 * @param n La notificación a mostrar
	 */
	private void mostrarNotificacionEnVentana(Notificacion n) {
		JTextArea area = crearArea();
		area.setEditable(false);
		area.setText(crearTextoNotificacion(n));
		area.setCaretPosition(0);

		JScrollPane scroll = estilizarScroll(area);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(640), VentanaPrincipal.escalar(260)));

		JOptionPane.showMessageDialog(this, scroll, "Notificación", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Actualiza el label de resumen con el total y no leídas.
	 *
	 * @param totalMostradas Total de notificaciones mostradas
	 * @param noVistas       Número de notificaciones no leídas
	 */
	private void actualizarResumen(int totalMostradas, int noVistas) {
		if (totalMostradas == 0) {
			labelResumen.setText("No hay notificaciones para mostrar con este filtro.");
			return;
		}
		labelResumen.setText("Mostrando " + totalMostradas + " notificaciones. No vistas: " + noVistas + ".");
	}

	/**
	 * Crea el texto que se muestra en la lista para una notificación.
	 *
	 * @param n La notificación
	 * @return Texto formateado para la lista
	 */
	private String crearTextoLista(Notificacion n) {
		String estado = n.isLeida() ? "Vista" : "Nueva";
		String tipo = n.getTipo() != null ? n.getTipo().toString() : "General";
		String fecha = formatearFecha(n);
		return estado + "   |   " + tipo + "   |   " + fecha;
	}

	/**
	 * Crea el texto completo de una notificación para mostrar en el diálogo.
	 *
	 * @param n La notificación
	 * @return Texto completo de la notificación
	 */
	private String crearTextoNotificacion(Notificacion n) {
		StringBuilder sb = new StringBuilder();
		sb.append("Tipo: ").append(n.getTipo() != null ? n.getTipo() : "General").append("\n");
		sb.append("Fecha: ").append(formatearFecha(n)).append("\n");
		sb.append("Vista: Sí\n\n");
		sb.append(n.getMensaje());
		return sb.toString();
	}

	/**
	 * Formatea la fecha de envío de una notificación.
	 *
	 * @param n La notificación
	 * @return Fecha formateada como string o "-" si es nula
	 */
	private String formatearFecha(Notificacion n) {
		if (n == null || n.getFechaEnvio() == null)
			return "-";
		return n.getFechaEnvio().toLocalDate() + " " + n.getFechaEnvio().toLocalTime().withNano(0);
	}

	/**
	 * Aplica el estilo visual a la lista de notificaciones.
	 */
	private void estilizarLista() {
		listaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaNotificaciones.setFixedCellHeight(VentanaPrincipal.escalar(34));
		listaNotificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		listaNotificaciones.setBackground(Color.WHITE);
		listaNotificaciones.setForeground(Color.BLACK);
		listaNotificaciones.setSelectionBackground(new Color(235, 235, 235));
		listaNotificaciones.setSelectionForeground(Color.BLACK);
	}

	/**
	 * Crea el panel de ayuda con instrucciones de uso.
	 *
	 * @return Panel de ayuda
	 */
	private JPanel crearPanelAyuda() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(crearLabel("Cómo funciona"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		panel.add(crearLabel("Selecciona una notificación de la bandeja."));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		panel.add(crearLabel("Al abrirla pasa automáticamente a vista."));
		return panel;
	}

	/**
	 * Crea el panel con los botones de acción sobre notificaciones.
	 *
	 * @return Panel de botones
	 */
	private JPanel crearPanelBotones() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(crearLabel("Acciones"));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		botonVer = crearBotonAccion("Ver notificación");
		botonVer.setActionCommand("ver");

		botonMarcarTodas = crearBotonSecundario("Marcar todas vistas");
		botonMarcarTodas.setActionCommand("marcarTodas");

		JPanel filaBotones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(10), 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonVer);
		filaBotones.add(botonMarcarTodas);

		Dimension tamaÑo = new Dimension(VentanaPrincipal.escalar(460), VentanaPrincipal.escalar(42));
		filaBotones.setPreferredSize(tamaÑo);
		filaBotones.setMaximumSize(tamaÑo);

		panel.add(filaBotones);
		return panel;
	}
}