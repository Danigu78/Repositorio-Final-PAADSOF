package Gui.cliente;

import usuarios.Cliente;
import javax.swing.*;

import Gui.PanelBaseInterfaz;
import Gui.VentanaPrincipal;

import java.awt.*;

/**
 * Clase base para los subpaneles del cliente en CheckPoint. Extiende
 * PanelBaseInterfaz para reutilizar todos sus helpers visuales. Añade helpers
 * específicos del cliente: tarjetas, paneles con scroll y gestión del cliente
 * logueado.
 *
 * @author Daniel
 * @version 1.0
 */
public abstract class AbstractPanelCliente extends PanelBaseInterfaz {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Cliente logueado actualmente. */
	protected Cliente cliente;

	/**
	 * Constructor de la clase base del panel cliente.
	 *
	 * @param ventana La ventana principal
	 */
	protected AbstractPanelCliente(VentanaPrincipal ventana) {
		super(ventana);
	}

	/**
	 * Actualiza el subpanel con el cliente logueado. Cada subpanel implementa su
	 * lógica de actualización.
	 *
	 * @param cliente El cliente logueado
	 */
	public abstract void actualizar(Cliente cliente);

	/**
	 * Configura el panel de contenido con BoxLayout Y_AXIS y lo envuelve en un
	 * JScrollPane sin borde. Reutilizado en carrito, pedidos, intercambios y
	 * cartera.
	 *
	 * @param panelContenido Panel al que se aplica el BoxLayout
	 * @return JScrollPane listo para añadir al layout
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
	 * Crea la estructura base de una tarjeta horizontal con BorderLayout. Usada en
	 * pedidos, carrito, intercambios y cartera. Con bordeInferior=true usa
	 * MatteBorder inferior (listas). Con bordeInferior=false usa LineBorder
	 * (tarjetas individuales).
	 *
	 * @param alturaMaxima  Altura máxima de la tarjeta en píxeles sin escalar
	 * @param bordeInferior true para MatteBorder inferior, false para LineBorder
	 * @return Panel de la tarjeta configurado
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
	 * Crea un GridBagConstraints preconfigurado para el panel de info interior de
	 * una tarjeta. Ancla oeste, se estira horizontal, columna fija 0.
	 *
	 * @return GridBagConstraints listo para usar en filas de tarjeta
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
	 * Crea un GridBagConstraints preconfigurado para el panel de botones derecho de
	 * una tarjeta.
	 *
	 * @return GridBagConstraints listo para usar en botones de tarjeta
	 */
	protected GridBagConstraints crearGbcBotonesTarjeta() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);
		return gbc;
	}

	/**
	 * Crea el panel de botones derecho de una tarjeta con GridBagLayout y fondo
	 * COLOR_TARJETA.
	 *
	 * @return Panel de botones configurado
	 */
	protected JPanel crearPanelBotonesTarjeta() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
		return panel;
	}

	/**
	 * Crea el panel de info interior de una tarjeta con GridBagLayout y fondo
	 * COLOR_TARJETA.
	 *
	 * @return Panel de info configurado
	 */
	protected JPanel crearPanelInfoTarjeta() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
		return panel;
	}

	/**
	 * Añade una tarjeta al panel con separación vertical debajo. Reutiliza el
	 * patrón repetido en todos los rellenos de listas.
	 *
	 * @param panel      Panel donde añadir la tarjeta
	 * @param tarjeta    Tarjeta a añadir
	 * @param separacion Separación en píxeles sin escalar
	 */
	protected void añadirTarjetaConSeparacion(JPanel panel, JPanel tarjeta, int separacion) {
		tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(tarjeta);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(separacion)));
	}

	/**
	 * Crea un label de lista vacía con el estilo estándar del cliente. Fuente
	 * subtítulo, color texto2, alineado a la izquierda.
	 *
	 * @param texto El texto a mostrar
	 * @return JLabel configurado
	 */
	protected JLabel crearLabelVacio(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	/**
	 * Muestra un diálogo de confirmación YES/NO estándar.
	 *
	 * @param pregunta Texto de la pregunta
	 * @param titulo   Título del diálogo
	 * @return true si el usuario pulsa Sí
	 */
	protected boolean confirmar(String pregunta, String titulo) {
		return JOptionPane.showConfirmDialog(this, pregunta, titulo,
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	/**
	 * Rellena un panel de lista con las tarjetas de una lista. Si la lista está
	 * vacía muestra el mensaje de vacío. Revalida y repinta el panel al terminar.
	 *
	 * @param panel        Panel de lista a rellenar
	 * @param hayElementos true si hay elementos que mostrar
	 * @param mensajeVacio Mensaje a mostrar si no hay elementos
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