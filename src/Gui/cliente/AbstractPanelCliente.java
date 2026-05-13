package Gui.cliente;

import usuarios.Cliente;
import javax.swing.*;

import Gui.PanelBaseInterfaz;
import Gui.VentanaPrincipal;

import java.awt.*;

/**
 * Clase base para los paneles de la parte del cliente. Sirve para no repetir
 * código visual y gestionar el scroll y las tarjetas de la interfaz. * @author
 * Daniel
 */
public abstract class AbstractPanelCliente extends PanelBaseInterfaz {

	/**
	 * Identificador de versión para la serialización de la clase.
	 */
	private static final long serialVersionUID = 1L;
	/** El cliente que tiene la sesion abierta */
	protected Cliente cliente;

	/**
	 * Constructor que vincula el panel a la ventana principal.
	 * 
	 * @param ventana La ventana de la aplicacion
	 */
	protected AbstractPanelCliente(VentanaPrincipal ventana) {
		super(ventana);
	}

	/**
	 * Actualiza el panel con los datos actuales del cliente.
	 * 
	 * @param cliente El cliente logueado
	 */
	public abstract void actualizar(Cliente cliente);

	/**
	 * Configura un panel con scroll vertical y fondo oscuro.
	 * 
	 * @param panelContenido El panel donde se meten los datos
	 * @return El scroll ya configurado
	 */
	protected JScrollPane crearScrollContenido(JPanel panelContenido) {
		panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		JScrollPane scroll = new JScrollPane(panelContenido);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		return scroll;
	}

	/**
	 * Crea el diseño de una tarjeta con bordes personalizados.
	 * 
	 * @param alturaMaxima  Altura del panel sin escalar
	 * @param bordeInferior True para poner solo la linea de abajo
	 * @return El panel de la tarjeta
	 */
	protected JPanel crearTarjetaBase(int alturaMaxima, boolean bordeInferior) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(alturaMaxima)));
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (bordeInferior) {
			tarjeta.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
					BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
							VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));
		} else {
			tarjeta.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, VentanaPrincipal.escalar(2)),
					BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
							VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));
		}
		return tarjeta;
	}

	/**
	 * Configura el alineamiento para el texto de la tarjeta.
	 * 
	 * @return Las restricciones del GridBag
	 */
	protected GridBagConstraints crearGbcTarjeta() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		return gbc;
	}

	/**
	 * Configura la posicion de los botones en la tarjeta.
	 * 
	 * @return Las restricciones para los botones
	 */
	protected GridBagConstraints crearGbcBotonesTarjeta() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);
		return gbc;
	}

	/**
	 * Crea el panel para meter los botones derechos.
	 * 
	 * @return El panel de botones
	 */
	protected JPanel crearPanelBotonesTarjeta() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
		return panel;
	}

	/**
	 * Crea el panel para meter la informacion central.
	 * 
	 * @return El panel de info
	 */
	protected JPanel crearPanelInfoTarjeta() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
		return panel;
	}

	/**
	 * Añade una tarjeta al panel y le pone un hueco debajo.
	 * 
	 * @param panel      Donde se añade la tarjeta
	 * 
	 * @param tarjeta    La tarjeta en si
	 * @param separacion Espacio vertical de separacion
	 */
	protected void añadirTarjetaConSeparacion(JPanel panel, JPanel tarjeta, int separacion) {
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(tarjeta);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(separacion)));
	}

	/**
	 * Crea un label de texto para cuando una lista esta vacia.
	 * 
	 * @param texto Mensaje que se quiere mostrar
	 * @return El label configurado
	 */
	protected JLabel crearLabelVacio(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	/**
	 * Muestra un cuadro para confirmar una accion (Si o No).
	 * 
	 * @param pregunta El texto de la pregunta
	 * @param titulo   El titulo de la ventana
	 * @return True si pulsa en Si
	 */
	protected boolean confirmar(String pregunta, String titulo) {
		return JOptionPane.showConfirmDialog(this, pregunta, titulo,
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	/**
	 * Limpia el panel y pone el mensaje de vacio si no hay datos.
	 * 
	 * @param panel        El panel de la lista
	 * @param hayElementos Si hay algo que enseñar o no
	 * @param mensajeVacio Mensaje si se queda vacio
	 */
	protected void prepararPanelLista(JPanel panel, boolean hayElementos, String mensajeVacio) {
		panel.removeAll();
		if (!hayElementos) {
			panel.add(crearLabelVacio(mensajeVacio));
		}
		panel.revalidate();
		panel.repaint();
	}

	@Override
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}
}