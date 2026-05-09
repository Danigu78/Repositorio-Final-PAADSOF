package Gui.cliente;

import Gui.AbstractPanelSection;

import Gui.VentanaPrincipal;

import Gui.controladores.cliente.ControladorCatalogo;
import Gui.controladores.cliente.ControladorPedidos;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import productos.ProductoVenta;
import usuarios.Cliente;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;
import tienda.*;

/**
 * Subpanel de mis pedidos de CheckPoint. Extiende AbstractPanelSection para
 * reutilizar helpers visuales. Sigue el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class SubpanelPedidos extends AbstractPanelSection {

	private Cliente cliente;
	private ControladorPedidos controlador;
	private CardLayout cardLayout;
	private JPanel panelContenido;
	private JPanel panelListaPedidos;
	private SubpanelProducto subpanelProducto;
	private JPanel panelDetallePedido;
	private PanelCliente panelCliente;
	private JButton botonVolverDetalle;

	public SubpanelPedidos(VentanaPrincipal ventana) {
		super(ventana);

		cardLayout = new CardLayout();
		panelContenido = new JPanel(cardLayout);
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);

		panelContenido.add(crearPanelLista(), "LISTA");

		panelDetallePedido = new JPanel(new BorderLayout());
		panelDetallePedido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.add(panelDetallePedido, "DETALLE");

		subpanelProducto = new SubpanelProducto(ventana, null);
		panelContenido.add(subpanelProducto, "PRODUCTO");

		add(panelContenido, BorderLayout.CENTER);
		cardLayout.show(panelContenido, "LISTA");
	}

	public void actualizar(Cliente cliente) {
		this.cliente = cliente;
		Tienda.getInstancia().getComprobadorTiempos().revisarCarritosCaducados();
		Tienda.getInstancia().getComprobadorTiempos().revisarPedidosPendientesCaducados();
		this.controlador = new ControladorPedidos(this, cliente);
		rellenarLista();
		cardLayout.show(panelContenido, "LISTA");
	}

	public void mostrarLista() {
		rellenarLista();
		cardLayout.show(panelContenido, "LISTA");
	}

	public void irAPago(Pedido pedido) {
		if (panelCliente != null)
			panelCliente.mostrarPago(pedido, cliente);
	}

	private JPanel crearPanelLista() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		JLabel titulo = new JLabel("Mis Pedidos");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(15), 0));
		panel.add(titulo, BorderLayout.NORTH);

		panelListaPedidos = new JPanel();
		panelListaPedidos.setLayout(new BoxLayout(panelListaPedidos, BoxLayout.Y_AXIS));
		panelListaPedidos.setBackground(VentanaPrincipal.COLOR_FONDO);

		JScrollPane scroll = new JScrollPane(panelListaPedidos);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	private void rellenarLista() {
		panelListaPedidos.removeAll();
		List<Pedido> pedidos = controlador.getPedidos();
		if (pedidos.isEmpty()) {
			JLabel labelVacio = new JLabel("No tienes pedidos todavía.");
			labelVacio.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
			labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelListaPedidos.add(labelVacio);
		} else {
			for (Pedido p : pedidos) {
				JPanel tarjeta = crearTarjetaPedido(p);
				tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
				panelListaPedidos.add(tarjeta);
			}
		}
		panelListaPedidos.revalidate();
		panelListaPedidos.repaint();
	}

	private JPanel crearTarjetaPedido(Pedido pedido) {
		JPanel tarjeta = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		tarjeta.setBackground(VentanaPrincipal.COLOR_TARJETA);
		tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(120)));
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(15))));

		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JLabel labelId = new JLabel(pedido.getIdPedido());
		labelId.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		panelInfo.add(labelId, gbc);

		JLabel labelEstado = new JLabel(controlador.getTextoEstado(pedido));
		labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelEstado.setForeground(getColorEstado(pedido.getEstado()));
		gbc.gridy = 1;
		panelInfo.add(labelEstado, gbc);

		JLabel labelTotal = new JLabel(String.format("Total: %.2f€", pedido.getTotal()));
		labelTotal.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		panelInfo.add(labelTotal, gbc);

		if (controlador.estaPendientePago(pedido)) {
			long minutos = controlador.getMinutosRestantesPago(pedido);
			JLabel labelTiempo = new JLabel(minutos <= 5 ? "⚠ Solo quedan " + minutos + " min para pagar"
					: "Tiempo para pagar: " + minutos + " min");
			labelTiempo.setFont(VentanaPrincipal.FUENTE_PEQUENA);
			labelTiempo.setForeground(minutos <= 5 ? new Color(200, 50, 50) : VentanaPrincipal.COLOR_TEXTO2);
			gbc.gridy = 3;
			panelInfo.add(labelTiempo, gbc);
		}

		tarjeta.add(panelInfo, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		// crearBotonOutline() de AbstractPanelSection
		JButton botonVer = crearBotonOutline("Ver pedido");
		botonVer.setActionCommand("verPedido:" + pedido.getIdPedido());
		botonVer.addActionListener(controlador);
		gbcB.gridy = 0;
		panelBotones.add(botonVer, gbcB);

		if (controlador.estaPendientePago(pedido)) {
			// crearBotonNaranja() de AbstractPanelSection
			JButton botonPagar = crearBotonNaranja("Pagar ahora");
			botonPagar.setActionCommand("pagar:" + pedido.getIdPedido());
			botonPagar.addActionListener(controlador);
			gbcB.gridy = 1;
			panelBotones.add(botonPagar, gbcB);
		} else if (pedido.getEstado() == EstadoPedido.LISTO_PARA_RECOGER && !pedido.isRecogida_solicitada()) {
			JButton botonRecoger = crearBotonNaranja("Solicitar recogida");
			botonRecoger.setActionCommand("recoger:" + pedido.getIdPedido());
			botonRecoger.addActionListener(controlador);
			gbcB.gridy = 1;
			panelBotones.add(botonRecoger, gbcB);
		}

		tarjeta.add(panelBotones, BorderLayout.EAST);
		return tarjeta;
	}

	public void verDetallePedido(Pedido pedido) {
		panelDetallePedido.removeAll();
		panelDetallePedido.setLayout(new BorderLayout());
		panelDetallePedido.setBackground(VentanaPrincipal.COLOR_FONDO);

		// crearBarraVolver() de AbstractPanelSection
		JPanel barra = crearBarraVolver("← Volver a mis pedidos");
		botonVolverDetalle = getBotonVolver(barra);
		botonVolverDetalle.setActionCommand("volver");
		botonVolverDetalle.addActionListener(controlador);
		panelDetallePedido.add(barra, BorderLayout.NORTH);

		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		contenido.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(20)));

		JLabel labelId = new JLabel(pedido.getIdPedido());
		labelId.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelId.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelId.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelId);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		JLabel labelEstado = new JLabel("Estado: " + controlador.getTextoEstado(pedido));
		labelEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEstado.setForeground(getColorEstado(pedido.getEstado()));
		labelEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelEstado);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));

		if (pedido.getDescuentoAplicado() != null) {
			JLabel labelDesc = new JLabel("Descuento: " + pedido.getDescuentoAplicado().getNombre());
			labelDesc.setFont(VentanaPrincipal.FUENTE_NORMAL);
			labelDesc.setForeground(VentanaPrincipal.COLOR_ACENTO);
			labelDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
			contenido.add(labelDesc);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(5)));
		}

		if (controlador.estaPendientePago(pedido)) {
			long minutos = controlador.getMinutosRestantesPago(pedido);
			JLabel labelTiempo = new JLabel(minutos <= 5 ? "⚠ Solo quedan " + minutos + " min para pagar"
					: "Tienes " + minutos + " min para pagar");
			labelTiempo.setFont(VentanaPrincipal.FUENTE_NORMAL);
			labelTiempo.setForeground(minutos <= 5 ? new Color(200, 50, 50) : VentanaPrincipal.COLOR_TEXTO2);
			labelTiempo.setAlignmentX(Component.LEFT_ALIGNMENT);
			contenido.add(labelTiempo);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

			JButton botonPagar = crearBotonNaranja("Pagar ahora");
			botonPagar.setAlignmentX(Component.LEFT_ALIGNMENT);
			botonPagar.setActionCommand("pagar:" + pedido.getIdPedido());
			botonPagar.addActionListener(controlador);
			contenido.add(botonPagar);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));
		}

		JSeparator sep = new JSeparator();
		sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		sep.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(sep);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		// crearLabel() de AbstractPanelSection
		JLabel labelProductosTitulo = new JLabel("Productos:");
		labelProductosTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelProductosTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelProductosTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelProductosTitulo);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		for (LineaPedido linea : pedido.getLineas()) {
			JPanel fila = crearFilaProducto(linea, pedido);
			fila.setAlignmentX(Component.LEFT_ALIGNMENT);
			contenido.add(fila);
			contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));
		}

		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));
		JLabel labelTotal = new JLabel(String.format("Total: %.2f€", pedido.getTotal()));
		labelTotal.setFont(new Font("Segoe UI", Font.BOLD, VentanaPrincipal.escalar(20)));
		labelTotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		labelTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(labelTotal);

		JScrollPane scroll = new JScrollPane(contenido);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		panelDetallePedido.add(scroll, BorderLayout.CENTER);

		panelDetallePedido.revalidate();
		panelDetallePedido.repaint();
		cardLayout.show(panelContenido, "DETALLE");
	}

	private JPanel crearFilaProducto(LineaPedido linea, Pedido pedido) {
		ProductoVenta producto = linea.getProducto();

		JPanel fila = new JPanel(new BorderLayout(VentanaPrincipal.escalar(15), 0));
		fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
		fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(110)));
		fila.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
						VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));

		JLabel labelImagen = new JLabel();
		labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(80), VentanaPrincipal.escalar(80)));
		// cargarImagen() de AbstractPanelSection
		cargarImagen(labelImagen, producto.getImagenRuta(), VentanaPrincipal.escalar(70), VentanaPrincipal.escalar(70));
		fila.add(labelImagen, BorderLayout.WEST);

		JPanel panelInfo = new JPanel(new GridBagLayout());
		panelInfo.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, VentanaPrincipal.escalar(2),
				VentanaPrincipal.escalar(10));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		JLabel labelNombre = new JLabel(producto.getNombre());
		labelNombre.setFont(VentanaPrincipal.FUENTE_BOTON);
		labelNombre.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		panelInfo.add(labelNombre, gbc);

		JLabel labelCantidad = new JLabel(
				"x" + linea.getCantidad() + "  —  " + String.format("%.2f€ / ud", linea.getPrecioVenta()));
		labelCantidad.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		labelCantidad.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		gbc.gridy = 1;
		panelInfo.add(labelCantidad, gbc);

		JLabel labelSubtotal = new JLabel(String.format("%.2f€", linea.getSubtotal()));
		labelSubtotal.setFont(VentanaPrincipal.FUENTE_PRECIO);
		labelSubtotal.setForeground(VentanaPrincipal.COLOR_ACENTO);
		gbc.gridy = 2;
		panelInfo.add(labelSubtotal, gbc);

		fila.add(panelInfo, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

		GridBagConstraints gbcB = new GridBagConstraints();
		gbcB.gridx = 0;
		gbcB.fill = GridBagConstraints.HORIZONTAL;
		gbcB.insets = new Insets(VentanaPrincipal.escalar(3), 0, VentanaPrincipal.escalar(3), 0);

		// crearBotonOutline() de AbstractPanelSection
		JButton botonVerProducto = crearBotonOutline("Ver producto");
		botonVerProducto.addActionListener(e -> verProducto(producto));
		gbcB.gridy = 0;
		panelBotones.add(botonVerProducto, gbcB);

		if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
			if (!controlador.yaReseñó(producto)) {
				// crearBotonNaranja() de AbstractPanelSection
				JButton botonReseña = crearBotonNaranja("Escribir reseña");
				botonReseña.setActionCommand("reseña:" + producto.getId());
				botonReseña.addActionListener(controlador);
				gbcB.gridy = 1;
				panelBotones.add(botonReseña, gbcB);
			} else {
				JLabel labelYa = new JLabel("✓ Reseñado");
				labelYa.setFont(VentanaPrincipal.FUENTE_PEQUENA);
				labelYa.setForeground(new Color(50, 150, 50));
				labelYa.setHorizontalAlignment(SwingConstants.CENTER);
				gbcB.gridy = 1;
				panelBotones.add(labelYa, gbcB);
			}
		}

		fila.add(panelBotones, BorderLayout.EAST);
		return fila;
	}

	public void mostrarDialogoRecogida(Pedido pedido) {
		String codigo = JOptionPane.showInputDialog(this,
				"Introduce el código de recogida para el pedido: " + pedido.getIdPedido(), "Confirmar Recogida",
				JOptionPane.QUESTION_MESSAGE);

		if (codigo != null && !codigo.trim().isEmpty()) {
			if (controlador.gestionarSolicitudRecogida(pedido, codigo)) {
				mostrarMensaje("¡Solicitud enviada! Ya puedes pasar a por tu pedido.");
				actualizar(cliente);
			} else {
				mostrarError("El código no coincide o el pedido no es válido.");
			}
		}
	}

	public void mostrarFormularioReseña(ProductoVenta producto) {
	    // SpinnerNumberModel con int — porque Cliente.escribirReseña recibe int
	    JSpinner spinnerPuntuacion = new JSpinner(
	        new SpinnerNumberModel(5, 0, 10, 1));
	    spinnerPuntuacion.setFont(VentanaPrincipal.FUENTE_NORMAL);

	    JTextArea areaComentario = crearArea();
	    areaComentario.setRows(4);

	    JPanel panelForm = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.weightx = 1;
	    gbc.insets = new Insets(VentanaPrincipal.escalar(5), 0,
	        VentanaPrincipal.escalar(5), 0);

	    gbc.gridy = 0;
	    panelForm.add(crearLabel("Producto: " + producto.getNombre()), gbc);
	    gbc.gridy = 1;
	    panelForm.add(crearLabel("Puntuación (0-10):"), gbc);
	    gbc.gridy = 2;
	    panelForm.add(spinnerPuntuacion, gbc);
	    gbc.gridy = 3;
	    panelForm.add(crearLabel("Comentario:"), gbc);
	    gbc.gridy = 4;
	    panelForm.add(new JScrollPane(areaComentario), gbc);

	    int opcion = JOptionPane.showConfirmDialog(
	        this, panelForm, "Escribir reseña",
	        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

	    if (opcion == JOptionPane.OK_OPTION) {
	        String comentario = areaComentario.getText().trim();
	        if (comentario.isBlank()) {
	            mostrarError("El comentario no puede estar vacío.");
	            return;
	        }
	        // Cogemos el valor como int
	        int puntuacion = (int) spinnerPuntuacion.getValue();
	        boolean ok = controlador.escribirReseña(producto, puntuacion, comentario);
	        if (ok) {
	            mostrarMensaje("¡Reseña publicada correctamente!");
	            actualizar(cliente);
	        } else {
	            mostrarError("No se pudo publicar la reseña.");
	        }
	    }
	}

	private void verProducto(ProductoVenta producto) {
		ControladorCatalogo controladorCatalogo = new ControladorCatalogo(cliente, null);
		subpanelProducto.setSubpanelOrigen(this);
		subpanelProducto.mostrarProducto(producto, cliente, controladorCatalogo);
		cardLayout.show(panelContenido, "PRODUCTO");
	}

	public void volverDelProducto() {
		cardLayout.show(panelContenido, "DETALLE");
	}

	private Color getColorEstado(EstadoPedido estado) {
		switch (estado) {
		case PENDIENTE_PAGO:
			return new Color(200, 150, 0);
		case PAGADO:
			return new Color(50, 150, 50);
		case LISTO_PARA_RECOGER:
			return new Color(0, 120, 200);
		case ENTREGADO:
			return new Color(50, 180, 50);
		case CANCELADO:
			return new Color(180, 50, 50);
		default:
			return VentanaPrincipal.COLOR_TEXTO2;
		}
	}

	public void setPanelCliente(PanelCliente panelCliente) {
		this.panelCliente = panelCliente;
	}
	@Override
	public void mostrarError(String mensaje) {
	    JOptionPane.showMessageDialog(this, mensaje, "Error",
	        JOptionPane.ERROR_MESSAGE);
	}
}