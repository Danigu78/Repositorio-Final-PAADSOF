package Gui;

import javax.swing.*;
import javax.swing.border.*;

import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;

import Gui.Controladores.ControladorPanelInvitado;
import sun.jvm.hotspot.oops.java_lang_Class;
import sun.tools.jar.resources.jar;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.ImageGraphicAttribute;

import usuarios.Cliente;
import usuarios.UsuarioNoRegistrado;

public class PanelInvitado extends JPanel {
	private VentanaPrincipal ventana;
	private UsuarioNoRegistrado invitado;
	/** Identificador de la sección catálogo */
	private static final String SEC_CATALOGO = "CATALOGO";
	/** Identificador de la sección segunda mano */
	private static final String SEC_SEGUNDA_MANO = "SEGUNDA_MANO";
	/** Botón actualmente seleccionado en la barra de navegación */
	private JButton botonActivo;
	private ControladorPanelInvitado controlador;

	public PanelInvitado(VentanaPrincipal ventana) {
		this.ventana = ventana;
		this.controlador = new ControladorPanelInvitado(ventana, this);
		setLayout(new BorderLayout());
	}

	private void inicializarUI() {
		JPanel barraNavegacion = new JPanel(new BorderLayout());
	}

	private void crearBarraDeNavegacion() {
		JPanel barra = new JPanel(new BorderLayout());
		barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));
		int margenEscalado = VentanaPrincipal.escalar(15);
		barra.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, VentanaPrincipal.escalar(2), 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(0, margenEscalado, 0, margenEscalado)));

		// zona de la izquierda

		int tamañoIcono = VentanaPrincipal.escalar(28);
		int tamañoFuente = VentanaPrincipal.escalar(18);
		int gapEscalado = VentanaPrincipal.escalar(10);

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
			JButton boton = crearBotonPestana(p[0], p[1]);

			zonaPestañas.add(boton);

			// Si es la sección de inicio, la activamos visualmente al cargar
			if (SEC_CATALOGO.equals(p[1])) {
				marcarBotonActivo(boton);
			}
		}
		// Suponiendo que tu panel de la barra se llama 'panelBarra'
		barra.add(zonaPestañas, BorderLayout.CENTER);
		
		
		
		
		
		
		
		
		
	}

	private JButton crearBotonPestana(String texto, String seccion) {
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

		boton.addActionListener(e -> {
			// En lugar de hacer la lógica aquí, le pasamos el trabajo al controlador
			controlador.gestionarNavegacion(boton, seccion);
		});
		return boton;
	}

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
}