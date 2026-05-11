package Gui.empleado;

import Gui.PanelAbstractoGeneral;
import Gui.VentanaPrincipal;
import Gui.Controladores.empleado.ControladorPanelEmpleado;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import usuarios.Empleado;
import usuarios.TipoPermisos;

/**
 * Panel principal para los empleados.
 *
 * Desde aqui se cargan las partes que puede ver cada empleado.
 *
 * @author Lucas
 * @version 1.0
 */
public class PanelEmpleado extends PanelAbstractoGeneral {

	public static final String SEC_STOCK = "STOCK";
	public static final String SEC_CATEGORIAS = "CATEGORIAS";
	public static final String SEC_PACKS = "PACKS";
	public static final String SEC_MODIFICAR = "MODIFICAR";
	public static final String SEC_PEDIDOS = "PEDIDOS";
	public static final String SEC_ENTREGA = "ENTREGA";
	public static final String SEC_TASACION = "TASACION";
	public static final String SEC_INTERCAMBIOS = "INTERCAMBIOS";
	public static final String SEC_NOTIFICACIONES = "NOTIFICACIONES";

	/** Empleado logueado. */
	private Empleado empleado;

	/** Controlador del panel. */
	private ControladorPanelEmpleado controlador;

	/** Layout para cambiar de seccion. */
	private CardLayout cardSecciones;

	/** Aqui van las secciones. */
	private JPanel panelSecciones;

	/** Barra de arriba. */
	private JPanel barra;

	/**
	 * Constructor del panel empleado.
	 *
	 * @param ventana La ventana principal
	 */
	public PanelEmpleado(VentanaPrincipal ventana) {
		super(ventana);
	}

	/**
	 * Actualiza el panel con el empleado que ha entrado.
	 *
	 * @param empleado El empleado que ha iniciado sesión
	 */
	public void actualizarEmpleado(Empleado empleado) {
		this.empleado = empleado;
		this.controlador = new ControladorPanelEmpleado(empleado);
		this.controlador.setVista(this);

		removeAll();

		if (!controlador.empleadoPuedeVerPanel()) {
			add(crearPanelAccesoNoValido(), BorderLayout.CENTER);
			revalidate();
			repaint();
			return;
		}

		inicializarUI();
		revalidate();
		repaint();
	}

	/**
	 * Muestra una seccion del panel.
	 *
	 * @param seccion Identificador de la sección
	 */
	public void mostrarSeccion(String seccion) {
		if (seccion == null || cardSecciones == null || panelSecciones == null)
			return;
		cardSecciones.show(panelSecciones, seccion);
	}

	/**
	 * Marca la pestaña activa.
	 *
	 * @param cmd ActionCommand de la pestaña a marcar
	 */
	public void marcarPestaña(String cmd) {
		marcarBotonBarraActivoPorCmd(barra, cmd);
	}

	/**
	 * Cierra la sesión del empleado. Lo llama el controlador.
	 */
	public void salir() {
		ventana.logout();
	}

	/**
	 * Construye la interfaz segun los permisos.
	 */
	private void inicializarUI() {
		// Montamos las pestañas segun los permisos del empleado
		List<String[]> listaPestañas = new ArrayList<>();
		if (controlador.tienePermiso(TipoPermisos.GESTION_STOCK))
			listaPestañas.add(new String[] { "Inventario", SEC_STOCK });
		if (controlador.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO))
			listaPestañas.add(new String[] { "Editar producto", SEC_MODIFICAR });
		if (controlador.tienePermiso(TipoPermisos.GESTION_CATEGORIAS))
			listaPestañas.add(new String[] { "Categorías", SEC_CATEGORIAS });
		if (controlador.tienePermiso(TipoPermisos.GESTION_PACKS))
			listaPestañas.add(new String[] { "Packs", SEC_PACKS });
		if (controlador.tienePermiso(TipoPermisos.GESTION_PEDIDOS))
			listaPestañas.add(new String[] { "Pedidos", SEC_PEDIDOS });
		if (controlador.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS))
			listaPestañas.add(new String[] { "Entregas", SEC_ENTREGA });
		if (controlador.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS))
			listaPestañas.add(new String[] { "Tasaciones", SEC_TASACION });
		if (controlador.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO))
			listaPestañas.add(new String[] { "Intercambios", SEC_INTERCAMBIOS });
		listaPestañas.add(new String[] { "Notificaciones", SEC_NOTIFICACIONES });

		String[][] pestañas = listaPestañas.toArray(new String[0][]);

		barra = crearBarraNavegacion("🎮 CheckPoint - Empleado", controlador.getNicknameEmpleado(), pestañas,
				controlador);
		add(barra, BorderLayout.NORTH);

		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

		String primeraSeccion = cargarSeccionesPermitidas();
		add(panelSecciones, BorderLayout.CENTER);

		if (primeraSeccion != null) {
			cardSecciones.show(panelSecciones, primeraSeccion);
			marcarBotonBarraActivoPorCmd(barra, primeraSeccion);
		}
	}

	/**
	 * Añade las secciones que puede usar el empleado.
	 *
	 * @return El identificador de la primera sección disponible
	 */
	private String cargarSeccionesPermitidas() {
		String primeraSeccion = null;

		if (controlador.tienePermiso(TipoPermisos.GESTION_STOCK)) {
			panelSecciones.add(new SeccionStockEmpleado(ventana, empleado), SEC_STOCK);
			if (primeraSeccion == null)
				primeraSeccion = SEC_STOCK;
		}
		if (controlador.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) {
			panelSecciones.add(new SeccionModificarEmpleado(ventana, empleado), SEC_MODIFICAR);
			if (primeraSeccion == null)
				primeraSeccion = SEC_MODIFICAR;
		}
		if (controlador.tienePermiso(TipoPermisos.GESTION_CATEGORIAS)) {
			panelSecciones.add(new SeccionCategoriasEmpleado(ventana, empleado), SEC_CATEGORIAS);
			if (primeraSeccion == null)
				primeraSeccion = SEC_CATEGORIAS;
		}
		if (controlador.tienePermiso(TipoPermisos.GESTION_PACKS)) {
			panelSecciones.add(new SeccionPacksEmpleado(ventana, empleado), SEC_PACKS);
			if (primeraSeccion == null)
				primeraSeccion = SEC_PACKS;
		}
		if (controlador.tienePermiso(TipoPermisos.GESTION_PEDIDOS)) {
			panelSecciones.add(new SeccionPedidosEmpleado(ventana, empleado), SEC_PEDIDOS);
			if (primeraSeccion == null)
				primeraSeccion = SEC_PEDIDOS;
		}
		if (controlador.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS)) {
			panelSecciones.add(new SeccionEntregasEmpleado(ventana, empleado), SEC_ENTREGA);
			if (primeraSeccion == null)
				primeraSeccion = SEC_ENTREGA;
		}
		if (controlador.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
			panelSecciones.add(new SeccionTasacionEmpleado(ventana, empleado), SEC_TASACION);
			if (primeraSeccion == null)
				primeraSeccion = SEC_TASACION;
		}
		if (controlador.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			panelSecciones.add(new SeccionIntercambiosEmpleado(ventana, empleado), SEC_INTERCAMBIOS);
			if (primeraSeccion == null)
				primeraSeccion = SEC_INTERCAMBIOS;
		}
		panelSecciones.add(new SeccionNotificacionesEmpleado(ventana, empleado), SEC_NOTIFICACIONES);
		if (primeraSeccion == null)
			primeraSeccion = SEC_NOTIFICACIONES;

		return primeraSeccion;
	}

	/**
	 * Panel mostrado cuando el empleado no puede acceder.
	 *
	 * @return Panel de aviso
	 */
	private JPanel crearPanelAccesoNoValido() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(40),
				VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(40)));

		JLabel aviso = new JLabel("<html><center>No se puede mostrar el panel del empleado.<br><br>"
				+ "Comprueba que el empleado tenga sesión iniciada " + "y que no esté despedido.</center></html>",
				SwingConstants.CENTER);
		aviso.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		aviso.setForeground(VentanaPrincipal.COLOR_TEXTO);

		JButton botonVolver = crearBotonNaranja("Volver al login");
		botonVolver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ventana.logout();
			}
		});

		JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelBoton.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelBoton.add(botonVolver);

		panel.add(aviso, BorderLayout.CENTER);
		panel.add(panelBoton, BorderLayout.SOUTH);
		return panel;
	}
}
