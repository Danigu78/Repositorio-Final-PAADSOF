package Gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

import Gui.Empleado.SeccionCategoriasEmpleado;
import Gui.Empleado.SeccionEntregasEmpleado;
import Gui.Empleado.SeccionIntercambiosEmpleado;
import Gui.Empleado.SeccionModificarEmpleado;
import Gui.Empleado.SeccionNotificacionesEmpleado;
import Gui.Empleado.SeccionPacksEmpleado;
import Gui.Empleado.SeccionPedidosEmpleado;
import Gui.Empleado.SeccionStockEmpleado;
import Gui.Empleado.SeccionTasacionEmpleado;
import usuarios.Empleado;
import usuarios.TipoPermisos;

/**
 * Panel principal del empleado.
 *
 * Esta clase solo se encarga de: - comprobar que hay un empleado válido -
 * construir la barra de navegación - montar las secciones en un CardLayout -
 * mostrar únicamente las secciones según permisos - delegar la lógica funcional
 * en cada Seccion...Empleado
 */
public class PanelEmpleado extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final String SEC_STOCK = "STOCK";
	public static final String SEC_CATEGORIAS = "CATEGORIAS";
	public static final String SEC_PACKS = "PACKS";
	public static final String SEC_MODIFICAR = "MODIFICAR";
	public static final String SEC_PEDIDOS = "PEDIDOS";
	public static final String SEC_ENTREGA = "ENTREGA";
	public static final String SEC_TASACION = "TASACION";
	public static final String SEC_INTERCAMBIOS = "INTERCAMBIOS";
	public static final String SEC_NOTIFICACIONES = "NOTIFICACIONES";

	private final VentanaPrincipal ventana;

	private Empleado empleado;
	private CardLayout cardSecciones;
	private JPanel panelSecciones;
	private JLabel labelEmpleado;
	private JButton botonActivo;

	public PanelEmpleado(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);
	}

	/**
	 * Actualiza el panel con el empleado logueado.
	 *
	 * @param empleado empleado actual
	 */
	public void actualizarEmpleado(Empleado empleado) {
		this.empleado = empleado;

		removeAll();

		if (!empleadoPuedeVerPanel()) {
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
	 * Comprueba si el empleado puede acceder al panel.
	 *
	 * @return true si hay empleado, no está despedido y tiene sesión iniciada
	 */
	private boolean empleadoPuedeVerPanel() {
		return empleado != null && !empleado.isDespedido() && empleado.isSesionIniciada();
	}

	/**
	 * Construye toda la interfaz principal del empleado.
	 */
	private void inicializarUI() {
		add(crearBarraNavegacion(), BorderLayout.NORTH);

		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

		String primeraSeccion = cargarSeccionesPermitidas();

		add(panelSecciones, BorderLayout.CENTER);

		if (primeraSeccion != null) {
			cardSecciones.show(panelSecciones, primeraSeccion);
		}
	}

	/**
	 * Añade al CardLayout las secciones permitidas para el empleado.
	 *
	 * @return el identificador de la primera sección disponible
	 */
	private String cargarSeccionesPermitidas() {
		String primeraSeccion = null;

		if (empleado.tienePermiso(TipoPermisos.GESTION_STOCK)) {
			panelSecciones.add(new SeccionStockEmpleado(ventana, empleado), SEC_STOCK);
			primeraSeccion = primeraSeccion == null ? SEC_STOCK : primeraSeccion;
		}

		if (empleado.tienePermiso(TipoPermisos.GESTION_CATEGORIAS)) {
			panelSecciones.add(new SeccionCategoriasEmpleado(ventana, empleado), SEC_CATEGORIAS);
			primeraSeccion = primeraSeccion == null ? SEC_CATEGORIAS : primeraSeccion;
		}

		if (empleado.tienePermiso(TipoPermisos.GESTION_PACKS)) {
			panelSecciones.add(new SeccionPacksEmpleado(ventana, empleado), SEC_PACKS);
			primeraSeccion = primeraSeccion == null ? SEC_PACKS : primeraSeccion;
		}

		if (empleado.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) {
			panelSecciones.add(new SeccionModificarEmpleado(ventana, empleado), SEC_MODIFICAR);
			primeraSeccion = primeraSeccion == null ? SEC_MODIFICAR : primeraSeccion;
		}

		if (empleado.tienePermiso(TipoPermisos.GESTION_PEDIDOS)) {
			panelSecciones.add(new SeccionPedidosEmpleado(ventana, empleado), SEC_PEDIDOS);
			primeraSeccion = primeraSeccion == null ? SEC_PEDIDOS : primeraSeccion;
		}

		if (empleado.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS)) {
			panelSecciones.add(new SeccionEntregasEmpleado(ventana, empleado), SEC_ENTREGA);
			primeraSeccion = primeraSeccion == null ? SEC_ENTREGA : primeraSeccion;
		}

		if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
			panelSecciones.add(new SeccionTasacionEmpleado(ventana, empleado), SEC_TASACION);
			primeraSeccion = primeraSeccion == null ? SEC_TASACION : primeraSeccion;
		}

		if (empleado.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			panelSecciones.add(new SeccionIntercambiosEmpleado(ventana, empleado), SEC_INTERCAMBIOS);
			primeraSeccion = primeraSeccion == null ? SEC_INTERCAMBIOS : primeraSeccion;
		}

		// Las notificaciones siempre se muestran.
		panelSecciones.add(new SeccionNotificacionesEmpleado(ventana, empleado), SEC_NOTIFICACIONES);
		primeraSeccion = primeraSeccion == null ? SEC_NOTIFICACIONES : primeraSeccion;

		return primeraSeccion;
	}

	/**
	 * Crea la barra superior con logo, pestañas y botón de salir.
	 *
	 * @return barra de navegación
	 */
	private JPanel crearBarraNavegacion() {
		JPanel barra = new JPanel(new BorderLayout());
		barra.setBackground(VentanaPrincipal.COLOR_BARRA);
		barra.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, VentanaPrincipal.escalar(15))));
		barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));

		JLabel labelLogo = new JLabel("🎮 CheckPoint - Empleado");
		labelLogo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		barra.add(labelLogo, BorderLayout.WEST);

		JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
		panelPestanas.setBackground(VentanaPrincipal.COLOR_BARRA);
		panelPestanas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));

		botonActivo = null;

		if (empleado.tienePermiso(TipoPermisos.GESTION_STOCK)) {
			agregarPestana(panelPestanas, "Stock", SEC_STOCK);
		}

		if (empleado.tienePermiso(TipoPermisos.GESTION_CATEGORIAS)) {
			agregarPestana(panelPestanas, "Categorías", SEC_CATEGORIAS);
		}

		if (empleado.tienePermiso(TipoPermisos.GESTION_PACKS)) {
			agregarPestana(panelPestanas, "Packs", SEC_PACKS);
		}

		if (empleado.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) {
			agregarPestana(panelPestanas, "Modificar", SEC_MODIFICAR);
		}

		if (empleado.tienePermiso(TipoPermisos.GESTION_PEDIDOS)) {
			agregarPestana(panelPestanas, "Pedidos", SEC_PEDIDOS);
		}

		if (empleado.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS)) {
			agregarPestana(panelPestanas, "Entregas", SEC_ENTREGA);
		}

		if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
			agregarPestana(panelPestanas, "Tasaciones", SEC_TASACION);
		}

		if (empleado.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			agregarPestana(panelPestanas, "Intercambios", SEC_INTERCAMBIOS);
		}

		agregarPestana(panelPestanas, "Notificaciones", SEC_NOTIFICACIONES);

		barra.add(panelPestanas, BorderLayout.CENTER);

		JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(10), 0));
		panelDerecha.setBackground(VentanaPrincipal.COLOR_BARRA);
		panelDerecha.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), 0, 0, 0));

		labelEmpleado = new JLabel("👤 " + empleado.getNickname());
		labelEmpleado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEmpleado.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
		panelDerecha.add(labelEmpleado);

		JButton botonLogout = new JButton("🚪 Salir");
		botonLogout.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		botonLogout.setForeground(new Color(220, 80, 80));
		botonLogout.setBackground(VentanaPrincipal.COLOR_BARRA);
		botonLogout.setOpaque(true);
		botonLogout.setBorderPainted(false);
		botonLogout.setFocusPainted(false);
		botonLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonLogout.addActionListener(e -> ventana.logout());
		panelDerecha.add(botonLogout);

		barra.add(panelDerecha, BorderLayout.EAST);

		return barra;
	}

	/**
	 * Añade una pestaña a la barra de navegación.
	 *
	 * @param panel   panel de pestañas
	 * @param texto   texto visible
	 * @param seccion identificador de sección
	 */
	private void agregarPestana(JPanel panel, String texto, String seccion) {
		JButton boton = new JButton(texto);

		boton.setUI(new BasicButtonUI());
		boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
		boton.setBackground(VentanaPrincipal.COLOR_BARRA);
		boton.setOpaque(true);
		boton.setContentAreaFilled(true);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
						VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));

		boton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if (boton != botonActivo) {
					boton.setBackground(VentanaPrincipal.COLOR_BARRA_HOVER);
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				if (boton != botonActivo) {
					boton.setBackground(VentanaPrincipal.COLOR_BARRA);
				}
			}
		});

		boton.addActionListener(e -> {
			activarPestana(boton);
			cardSecciones.show(panelSecciones, seccion);
		});

		if (botonActivo == null) {
			botonActivo = boton;
			marcarActivo(boton);
		}

		panel.add(boton);
	}

	/**
	 * Marca visualmente una pestaña como activa.
	 *
	 * @param boton botón activo
	 */
	private void marcarActivo(JButton boton) {
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
		boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
		boton.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO, 1),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));
	}

	/**
	 * Cambia la pestaña activa.
	 *
	 * @param boton nueva pestaña activa
	 */
	private void activarPestana(JButton boton) {
		if (botonActivo != null) {
			botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
			botonActivo.setBackground(VentanaPrincipal.COLOR_BARRA);
			botonActivo.setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
							BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
									VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));
		}

		botonActivo = boton;
		marcarActivo(boton);
	}

	/**
	 * Panel mostrado cuando el empleado no puede acceder.
	 *
	 * @return panel de aviso
	 */
	private JPanel crearPanelAccesoNoValido() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(40),
				VentanaPrincipal.escalar(40), VentanaPrincipal.escalar(40)));

		JLabel aviso = new JLabel("<html><center>" + "No se puede mostrar el panel del empleado.<br><br>"
				+ "Comprueba que el empleado tenga sesión iniciada y que no esté despedido." + "</center></html>",
				SwingConstants.CENTER);
		aviso.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		aviso.setForeground(VentanaPrincipal.COLOR_TEXTO);

		JButton botonVolver = new JButton("Volver al login");
		botonVolver.setFont(VentanaPrincipal.FUENTE_BOTON);
		botonVolver.setBackground(VentanaPrincipal.COLOR_ACENTO);
		botonVolver.setForeground(VentanaPrincipal.COLOR_TEXTO);
		botonVolver.setOpaque(true);
		botonVolver.setBorderPainted(false);
		botonVolver.setFocusPainted(false);
		botonVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonVolver.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(20)));
		botonVolver.addActionListener(e -> ventana.logout());

		JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelBoton.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelBoton.add(botonVolver);

		panel.add(aviso, BorderLayout.CENTER);
		panel.add(panelBoton, BorderLayout.SOUTH);

		return panel;
	}
}
