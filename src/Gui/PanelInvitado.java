package Gui;

import javax.swing.*;
import javax.swing.border.*;

import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;

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

	public PanelInvitado(VentanaPrincipal ventana) {
		this.ventana = ventana;
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
		
	//zona de la izquierda
		
		int tamañoIcono=VentanaPrincipal.escalar(28);
		int tamañoFuente=VentanaPrincipal.escalar(18);
		int gapEscalado=VentanaPrincipal.escalar(10);
		
		java.net.URL imageURL=getClass().getResource("/fotos/logo.png");
		ImageIcon iconoescalado=null;
		
		if (imageURL!=null) {
			ImageIcon iconoOriginal=new ImageIcon(imageURL);
			Image imgResized = iconoOriginal.getImage().getScaledInstance(tamañoIcono, tamañoIcono, Image.SCALE_SMOOTH);
			iconoescalado = new ImageIcon(imgResized);
		}else {
			System.err.println("No se pudo encontrar el archivo en: /fotos/logo.png");
		}
		
	
		
		JLabel labelLogo=new JLabel("CheckPoint");
		if (iconoescalado != null) {
		    labelLogo.setIcon(iconoescalado);
		}
		
		labelLogo.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(18)));
		labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		
		//centramos verticalmente ambos
		labelLogo.setVerticalTextPosition(SwingConstants.CENTER);
		
		labelLogo.setIconTextGap(VentanaPrincipal.escalar(5)); //separacion entre el logo y el texto.
		barra.add(labelLogo, BorderLayout.WEST);
		
		
	//zona central 
		JLabel zonaPestañas=new JLabel(new FlowLayout())
	}
}