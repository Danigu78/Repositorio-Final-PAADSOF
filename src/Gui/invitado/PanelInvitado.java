package Gui.invitado;

import Gui.VentanaPrincipal;
import Gui.Controladores.invitado.ControladorPanelInvitado;

import javax.swing.*;

import Gui.cliente.SubpanelCatalogo;
import Gui.cliente.SubpanelSegundaMano;

import java.awt.*;
import java.awt.event.*;

/**
 * Panel principal para usuarios invitados (sin registro). Permite navegar entre
 * catálogo y segunda mano, además de ofrecer acceso a registro/login y salida
 * de la aplicación.
 *
 * Este panel utiliza CardLayout para gestionar las distintas secciones.
 *
 * @author Daniel
 * @version 1.0
 */
public class PanelInvitado extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Ventana principal de la aplicación */
	private VentanaPrincipal ventana;
	/** Identificador de la sección catálogo */
	private static final String SEC_CATALOGO = "CATALOGO";
	/** Identificador de la sección segunda mano */
	private static final String SEC_SEGUNDA_MANO = "SEGUNDA_MANO";
	/** Botón actualmente seleccionado en la barra de navegación */
	private JButton botonActivo;
	/** Controlador del panel de invitado */
	private ControladorPanelInvitado controlador;
	/** Layout que gestiona el cambio entre secciones */
	private CardLayout cardSecciones;
	/** Panel contenedor de todas las secciones */
	private JPanel panelSecciones;
	/** Panel del catálogo de productos */
	private SubpanelCatalogo subpanelCatalogo;
	/** Panel de productos de segunda mano */
	private SubpanelSegundaMano subpanelSegundaMano;

	/**
	 * Constructor del panel de invitado.
	 *
	 * @param ventana ventana principal de la aplicación
	 */
	public PanelInvitado(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.controlador = new ControladorPanelInvitado(ventana, this);
		setLayout(new BorderLayout());
		inicializarUI();
	}

	/**
	 * Inicializa la interfaz del panel (barra + secciones).
	 */
	private void inicializarUI() {
		JPanel barraNavegacion = crearBarraDeNavegacion();
		add(barraNavegacion, BorderLayout.NORTH);
		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

		subpanelCatalogo = new SubpanelCatalogo(ventana);
		subpanelSegundaMano = new SubpanelSegundaMano(ventana);

		panelSecciones.add(subpanelCatalogo, SEC_CATALOGO);
		panelSecciones.add(subpanelSegundaMano, SEC_SEGUNDA_MANO);
		int margen = VentanaPrincipal.escalar(20);
		panelSecciones.setBorder(BorderFactory.createEmptyBorder(margen, margen, margen, margen));
		add(panelSecciones, BorderLayout.CENTER);

		// Mostrar catálogo por defecto
		mostrarSeccion(SEC_CATALOGO);

	}

	/**
	 * Crea la barra superior de navegación del invitado.
	 *
	 * @return panel de la barra de navegación
	 */
	private JPanel crearBarraDeNavegacion() {
		JPanel barra = new JPanel(new BorderLayout());
		barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));
		int margenEscalado = VentanaPrincipal.escalar(15);
		barra.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(2), 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(0, margenEscalado, 0, margenEscalado)));

		// zona de la izquierda

		int tamañoIcono = VentanaPrincipal.escalar(28);
		java.net.URL imageURL = getClass().getResource("/fotos/logo.png");
		ImageIcon iconoescalado = null;

		if (imageURL != null) {
			ImageIcon iconoOriginal = new ImageIcon(imageURL);
			Image imgResized = iconoOriginal.getImage().getScaledInstance(tamañoIcono, tamañoIcono, Image.SCALE_SMOOTH);
			iconoescalado = new ImageIcon(imgResized);
		} else {
			System.err.println("No se pudo encontrar el archivo en: /fotos/logo.png");
		}

		JLabel labelLogo = new JLabel("CheckPoint");
		if (iconoescalado != null) {
			labelLogo.setIcon(iconoescalado);
		}
		labelLogo.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(18)));
		labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		// centramos verticalmente ambos
		labelLogo.setVerticalTextPosition(SwingConstants.CENTER);
		labelLogo.setIconTextGap(VentanaPrincipal.escalar(5)); // separacion entre el logo y el texto.
		barra.add(labelLogo, BorderLayout.WEST);

		// zona central
		JPanel zonaPestañas = new JPanel(new FlowLayout(FlowLayout.CENTER, VentanaPrincipal.escalar(8), 0));
		int margenSuperior = VentanaPrincipal.escalar(9);
		zonaPestañas.setBorder(BorderFactory.createEmptyBorder(margenSuperior, 0, 0, 0));// controlamos el borde
																							// superior
		// Definimos los datos: { "Texto que ve el usuario", "ID de la sección" }
		String[][] datosPestanas = { { "Catálogo", SEC_CATALOGO }, { "Segunda Mano", SEC_SEGUNDA_MANO } };

		for (String[] p : datosPestanas) {
			// p[0] es el Texto ("Catálogo")
			// p[1] es el ID (SEC_CATALOGO)
			JButton boton = crearBotonPestaña(p[0], p[1]);

			zonaPestañas.add(boton);

			// Si es la sección de inicio, la activamos visualmente al cargar
			if (SEC_CATALOGO.equals(p[1])) {
				marcarBotonActivo(boton);
			}
		}
		// Suponiendo que tu panel de la barra se llama 'panelBarra'
		barra.add(zonaPestañas, BorderLayout.CENTER);

		// zona derecha
		JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(10), 0)); // hueco
																												// horizontal
																												// entre
																												// los
																												// botones
		panelDerecho.setForeground(VentanaPrincipal.COLOR_PANEL);
		panelDerecho.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), 0, 0, 0));// deja un margen
																										// arriba de 12
																										// para que no
																										// este pegado
																										//
		JLabel labelInvitado = new JLabel("Usuario no registrado");
		labelInvitado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelInvitado.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelDerecho.add(labelInvitado);

		// separador
		panelDerecho.add(crearSeparador());
		// boton registrarse
		Color naranjaHover = new Color(230, 120, 0);
		Color naranjaNormal = new Color(255, 140, 0);
		JButton botonRegistro = new JButton("Registrarse | Iniciar sesion");
		botonRegistro.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		botonRegistro.setForeground(Color.WHITE);
		botonRegistro.setBackground(naranjaNormal);
		botonRegistro.setFocusPainted(false);
		botonRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonRegistro.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5),
				VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(15)));

		botonRegistro.setContentAreaFilled(false);
		botonRegistro.setOpaque(true);
		botonRegistro.setBorderPainted(false);

		botonRegistro.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botonRegistro.setBackground(naranjaHover);

			}

			@Override
			public void mouseExited(MouseEvent e) {
				botonRegistro.setBackground(naranjaNormal);
			}
		});
		botonRegistro.setActionCommand(ControladorPanelInvitado.IR_REGISTRO);
		botonRegistro.addActionListener(controlador);

		panelDerecho.add(botonRegistro);
		// añadimos otra vez el mismo separador
		panelDerecho.add(crearSeparador());
		JButton botonCerrar = new JButton("Salir");
		botonCerrar.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		botonCerrar.setForeground(new Color(220, 80, 80));
		botonCerrar.setBackground(VentanaPrincipal.COLOR_PANEL);
		botonCerrar.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10)));
		botonCerrar.setFocusPainted(false);
		botonCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		botonCerrar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				botonCerrar.setForeground(new Color(255, 100, 100));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				botonCerrar.setForeground(new Color(220, 80, 80));
			}
		});
		botonCerrar.setActionCommand(ControladorPanelInvitado.SALIR);
		botonCerrar.addActionListener(controlador);
		panelDerecho.add(botonCerrar);
		barra.add(panelDerecho, BorderLayout.EAST);
		return barra;

	}

	/**
	 * Crea un botón de pestaña para navegación entre secciones.
	 *
	 * @param texto   texto visible del botón
	 * @param seccion identificador de la sección
	 * @return botón configurado
	 */
	private JButton crearBotonPestaña(String texto, String seccion) {
		JButton boton = new JButton(texto);
		boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		boton.setBackground(VentanaPrincipal.COLOR_PANEL);
		boton.setBorder(BorderFactory.createEmptyBorder(// padding alrededor del texto
				VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(8),
				VentanaPrincipal.escalar(12)));
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (boton != botonActivo) {
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (boton != botonActivo) {
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
				}
			}
		});

		boton.setActionCommand(ControladorPanelInvitado.NAVEGAR + seccion);
		boton.addActionListener(controlador);
		return boton;
	}

	/**
	 * Marca visualmente un botón como activo.
	 *
	 * @param boton botón a marcar
	 */
	public void marcarBotonActivo(JButton boton) {
		int v = VentanaPrincipal.escalar(8);
		int h = VentanaPrincipal.escalar(12);
		int linea = VentanaPrincipal.escalar(2);

		if (botonActivo != null) {
			botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			botonActivo.setBorder(BorderFactory.createEmptyBorder(v, h, v, h));
		}
		botonActivo = boton;
		botonActivo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		botonActivo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, linea, 0, VentanaPrincipal.COLOR_ACENTO), // Pone el borde abajo
				BorderFactory.createEmptyBorder(v, h, v - linea, h)// Ponemos donde es pinchable
		));
	}

	/**
	 * Desmarca cualquier botón activo de la barra.
	 */
	public void desmarcarTodo() {
		if (botonActivo != null) {

			botonActivo.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(0),
					VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10)));

			// Volvemos a poner el texto en su color normal
			botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
			// Olvidamos cuál era el botón activo
			botonActivo = null;
		}
	}

	/**
	 * Crea un separador que usaremos en la barra de navegacion a la derecha
	 * 
	 * @return void
	 */
	private JPanel crearSeparador() {
		JPanel sep = new JPanel();
		sep.setBackground(VentanaPrincipal.COLOR_ACENTO);
		sep.setPreferredSize(new Dimension(VentanaPrincipal.escalar(3), VentanaPrincipal.escalar(25)));
		return sep;
	}

	/**
	 * Muestra una sección del panel usando CardLayout.
	 *
	 * @param seccion identificador de la sección
	 */
	public void mostrarSeccion(String seccion) {
		cardSecciones.show(panelSecciones, seccion);
		actualizarSeccion(seccion);
	}
	/**
	 * Actualiza el contenido de la sección activa.
	 *
	 * @param seccion sección a actualizar
	 */
	private void actualizarSeccion(String seccion) {
		switch (seccion) {
		case SEC_CATALOGO:
			subpanelCatalogo.actualizar(null);
			break;
		case SEC_SEGUNDA_MANO:
			subpanelSegundaMano.actualizar(null);
			break;
		}
	}

	/**
	 * Refresca el catálogo de invitado cuando la pantalla vuelve a mostrarse.
	 */
	public void refrescarCatalogo() {
		subpanelCatalogo.actualizar(null);
	}
}
