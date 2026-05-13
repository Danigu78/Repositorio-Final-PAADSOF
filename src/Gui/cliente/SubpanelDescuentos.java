package Gui.cliente;

import javax.swing.*;
import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorDescuentos;
import ventas.Descuento;
import ventas.DescuentoCategoria;
import ventas.DescuentoCantidad;
import ventas.DescuentoVolumen;
import ventas.Regalo;
import usuarios.Cliente;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Subpanel de descuentos activos del cliente. Muestra todos los descuentos
 * vigentes con tipo, detalle y período. Informa de que solo se aplica el más
 * ventajoso en el carrito. 
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelDescuentos extends AbstractPanelCliente {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Controlador del subpanel. */
	private ControladorDescuentos controlador;

	/** Botón refrescar  atributo para registrar el controlador. */
	private JButton botonRefrescar;

	/** Panel donde se insertan las tarjetas de descuentos. */
	private JPanel panelLista;

	/**
	 * Constructor del subpanel de descuentos.
	 *
	 * @param ventana La ventana principal
	 */
	public SubpanelDescuentos(VentanaPrincipal ventana) {
		super(ventana);
	}

	/**
	 * Actualiza el subpanel con los descuentos activos. Crea el controlador y lo
	 * registra en los botones.
	 *
	 * @param cliente El cliente logueado
	 */
	@Override
	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		this.controlador = new ControladorDescuentos(this);
		removeAll();

		JPanel panelBase = crearPanelBase("Descuentos");
		JPanel contenido = getContenido(panelBase);

		contenido.add(crearBloqueAviso());
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueDescuentos());

		add(panelBase, BorderLayout.CENTER);

		setControlador(controlador);
		revalidate();
		repaint();
	}

	/**
	 * Registra el controlador en los botones 
	 *
	 * @param c El ActionListener a registrar
	 */
	public void setControlador(ActionListener c) {
		if (botonRefrescar != null) {
			for (ActionListener al : botonRefrescar.getActionListeners())
				botonRefrescar.removeActionListener(al);
			botonRefrescar.setActionCommand("refrescar");
			botonRefrescar.addActionListener(c);
		}
	}

	/**
	 * Recarga las tarjetas de descuentos. 
	 */
	public void cargarDescuentos() {
		if (panelLista == null)
			return;
		panelLista.removeAll();
		rellenarLista();
		panelLista.revalidate();
		panelLista.repaint();
	}

	/**
	 * Crea el bloque de aviso sobre cómo se aplican los descuentos.
	 *
	 * @return Panel del bloque aviso
	 */
	private JPanel crearBloqueAviso() {
		
		JPanel bloque = crearBloque("¿Cómo funcionan los descuentos?");

		JPanel panelInfo = new JPanel();
		panelInfo.setOpaque(false);
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));

		
		panelInfo.add(crearLabel("En CheckPoint se aplica automáticamente el descuento "
				+ "que primero suponga una disminución de precio."));
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(6)));
		panelInfo.add(crearLabel("Solo se puede aplicar un descuento por compra  "));
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(6)));

		bloque.add(panelInfo, gbcCampo(1));
		return bloque;
	}

	/**
	 * Crea el bloque con la lista de descuentos activos y el botón refrescar.
	 *
	 * @return Panel del bloque descuentos
	 */
	private JPanel crearBloqueDescuentos() {

		JPanel bloque = crearBloque("Descuentos activos");

		botonRefrescar = crearBotonSecundario("Refrescar");
		botonRefrescar.setActionCommand("refrescar");

		JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBoton.setOpaque(false);
		filaBoton.add(botonRefrescar);
		bloque.add(filaBoton, gbcCampo(1));

		panelLista = new JPanel();
		panelLista.setOpaque(false);
		panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));

		rellenarLista();
		JScrollPane scroll = estilizarScroll(panelLista);
		scroll.setPreferredSize(new Dimension(VentanaPrincipal.escalar(1050), VentanaPrincipal.escalar(500)));

		bloque.add(scroll, gbcCampo(2));
		return bloque;
	}

	/**
	 * Rellena el panelLista con las tarjetas de descuentos actuales.
	 */
	private void rellenarLista() {
		if (controlador == null)
			return;
		panelLista.removeAll();

		List<Descuento> activos = controlador.getDescuentosActivos();

		if (activos.isEmpty()) {

			panelLista.add(crearLabelVacio("No hay descuentos activos en este momento."));
			return;
		}

		for (Descuento d : activos) {
			panelLista.add(crearTarjetaDescuento(d));
			panelLista.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		}
	}

	/**
	 * Crea una tarjeta visual para un descuento.
	 *
	 * 
	 * @param d El descuento a mostrar
	 * @return Panel con la tarjeta
	 */
	private JPanel crearTarjetaDescuento(Descuento d) {
		
		JPanel tarjeta = crearTarjetaBase(VentanaPrincipal.escalar(110), true);
		tarjeta.setLayout(new BorderLayout(VentanaPrincipal.escalar(15), 0));

		JPanel tipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, VentanaPrincipal.escalar(10)));
		tipo.setOpaque(false);
		tipo.add(crearBadgeTipo(d));
		tarjeta.add(tipo, BorderLayout.WEST);

		JPanel panelInfo = crearPanelInfoTarjeta();
		JLabel labelNombre = new JLabel(d.getNombre()+ " ");
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelInfo.add(labelNombre);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(6)));

		JLabel labelDetalle = crearLabel(controlador.getDetalle(d));
		panelInfo.add(labelDetalle);
		panelInfo.add(Box.createVerticalStrut(VentanaPrincipal.escalar(4)));

		JLabel labelPeriodo = crearLabel(controlador.getPeriodo(d));
		labelPeriodo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelPeriodo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelInfo.add(labelPeriodo);

		tarjeta.add(panelInfo, BorderLayout.CENTER);
		JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, VentanaPrincipal.escalar(10)));
		panelEstado.setOpaque(false);
		JLabel labelActivo = new JLabel(" Activo");
		labelActivo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelActivo.setForeground(new Color(50, 150, 50));
		panelEstado.add(labelActivo);
		tarjeta.add(panelEstado, BorderLayout.EAST);

		return tarjeta;
	}

	/**
	 * Crea el badge del tipo de descuento con color según el tipo.
	 *
	 * @param d El descuento
	 * @return JLabel con el badge
	 */
	private JLabel crearBadgeTipo(Descuento d) {
		String texto;
		Color color;

		if (d instanceof DescuentoCategoria) {
			texto = " CATEGORÍA";
			color = new Color(70, 130, 180);
		} else if (d instanceof DescuentoVolumen) {
			texto = " VOLUMEN";
			color = new Color(60, 150, 80);
		} else if (d instanceof DescuentoCantidad) {
			texto = " CANTIDAD";//morado
			color = new Color(150, 80, 180);
		} else if (d instanceof Regalo) {
			texto = " REGALO";// rojo
			color = new Color(200, 80, 80);
		} else {
			texto = " OFERTA";
			color = VentanaPrincipal.COLOR_ACENTO;
		}

		JLabel badge = new JLabel(texto);
		badge.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		badge.setForeground(Color.WHITE);
		badge.setOpaque(true);
		badge.setBackground(color);
		badge.setHorizontalAlignment(SwingConstants.CENTER);
		badge.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(4), VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(4), VentanaPrincipal.escalar(10)));
		badge.setPreferredSize(new Dimension(VentanaPrincipal.escalar(130), VentanaPrincipal.escalar(32)));
		return badge;
	}
}