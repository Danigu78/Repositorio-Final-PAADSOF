package Gui.Gestor;

import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorConfiguracionGestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Gestor;


/**
 * Subpanel de configuración del sistema para el gestor. 
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelConfiguracionGestor extends AbstractPanelGestor {

	private static final long serialVersionUID = 1L;

	/** Controlador asociado al subpanel de configuración. */
	private ControladorConfiguracionGestor controlador;

	/** Spinner para el tiempo máximo del carrito. */
	private JSpinner spinnerCarrito;
	
	/** Spinner para el tiempo máximo de oferta. */
	private JSpinner spinnerOferta;
	
	/** Spinner para el tiempo máximo de pago. */
	private JSpinner spinnerPago;
	
	/** Spinner para el precio de tasación. */
	private JSpinner spinnerTasacion;
	
	/** Spinner para el peso de valoración del recomendador. */
	private JSpinner spinnerPesoValoracion;
	
	/** Spinner para el peso de compras del recomendador. */
	private JSpinner spinnerPesoCompras;
	
	/** Spinner para el peso de categorías del recomendador. */
	private JSpinner spinnerPesoCategorias;

	/** Botón para guardar la configuración del sistema. */
	private JButton botonGuardar;

	/**
	 * Constructor del subpanel de configuración del gestor.
	 *
	 * @param ventana Ventana principal de la aplicación
	 * @param gestor  Gestor logueado en el sistema
	 */
	public SubpanelConfiguracionGestor(VentanaPrincipal ventana, Gestor gestor) {
		super(ventana, gestor);
		this.controlador = new ControladorConfiguracionGestor(this, gestor);
		inicializarUI();
	}

	/**
	 * Inicializa la interfaz gráfica del subpanel de configuración.
	 */
	private void inicializarUI() {
		JPanel panelContenido = new JPanel(new GridBagLayout());
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(30),
				VentanaPrincipal.escalar(50), VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(50)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		JLabel titulo = new JLabel("Configuración del sistema");
		titulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(30), 0);
		panelContenido.add(titulo, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		
		gbc.gridy = 1;
		panelContenido.add(crearLabel("Tiempo máximo carrito (minutos):"), gbc);
		spinnerCarrito = new JSpinner(new SpinnerNumberModel(valorTiempo(controlador.getTiempoCarrito()), 1, 9999, 1));
		spinnerCarrito.setFont(VentanaPrincipal.FUENTE_NORMAL);
		gbc.gridy = 2;
		panelContenido.add(spinnerCarrito, gbc);

		gbc.gridy = 3;
		panelContenido.add(crearLabel("Tiempo máximo oferta (minutos):"), gbc);
		spinnerOferta = new JSpinner(new SpinnerNumberModel(valorTiempo(controlador.getTiempoOferta()), 1, 9999, 1));
		spinnerOferta.setFont(VentanaPrincipal.FUENTE_NORMAL);
		gbc.gridy = 4;
		panelContenido.add(spinnerOferta, gbc);

		gbc.gridy = 5;
		panelContenido.add(crearLabel("Tiempo máximo pago (minutos):"), gbc);
		spinnerPago = new JSpinner(new SpinnerNumberModel(valorTiempo(controlador.getTiempoPago()), 1, 9999, 1));
		spinnerPago.setFont(VentanaPrincipal.FUENTE_NORMAL);
		gbc.gridy = 6;
		panelContenido.add(spinnerPago, gbc);

		gbc.gridy = 7;
		panelContenido.add(crearLabel("Precio de tasación (€):"), gbc);
		spinnerTasacion = new JSpinner(
				new SpinnerNumberModel(valorTasacion(controlador.getPrecioTasacion()), 5.01, 9999.0, 0.5));
		spinnerTasacion.setFont(VentanaPrincipal.FUENTE_NORMAL);
		gbc.gridy = 8;
		panelContenido.add(spinnerTasacion, gbc);

		gbc.gridy = 9;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, VentanaPrincipal.escalar(4), 0);
		JLabel tituloRecomendador = new JLabel("Sistema de recomendación");
		tituloRecomendador.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		tituloRecomendador.setForeground(VentanaPrincipal.COLOR_TEXTO);
		panelContenido.add(tituloRecomendador, gbc);

		gbc.insets = new Insets(VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(4), 0);

		gbc.gridy = 10;
		panelContenido.add(crearLabel("Peso de valoración:"), gbc);
		spinnerPesoValoracion = new JSpinner(new SpinnerNumberModel(controlador.getPesoValoracion(), 0.0, 100.0, 0.1));
		spinnerPesoValoracion.setFont(VentanaPrincipal.FUENTE_NORMAL);
		gbc.gridy = 11;
		panelContenido.add(spinnerPesoValoracion, gbc);

		gbc.gridy = 12;
		panelContenido.add(crearLabel("Peso de compras similares:"), gbc);
		spinnerPesoCompras = new JSpinner(new SpinnerNumberModel(controlador.getPesoCompras(), 0.0, 100.0, 0.1));
		spinnerPesoCompras.setFont(VentanaPrincipal.FUENTE_NORMAL);
		gbc.gridy = 13;
		panelContenido.add(spinnerPesoCompras, gbc);

		gbc.gridy = 14;
		panelContenido.add(crearLabel("Peso de categoría favorita:"), gbc);
		spinnerPesoCategorias = new JSpinner(new SpinnerNumberModel(controlador.getPesoCategorias(), 0.0, 100.0, 0.1));
		spinnerPesoCategorias.setFont(VentanaPrincipal.FUENTE_NORMAL);
		gbc.gridy = 15;
		panelContenido.add(spinnerPesoCategorias, gbc);

		
		botonGuardar = crearBotonNaranja("Guardar configuración");
		botonGuardar.setActionCommand("guardarConfig");
		gbc.gridy = 16;
		gbc.insets = new Insets(VentanaPrincipal.escalar(20), 0, 0, 0);
		panelContenido.add(botonGuardar, gbc);

		add(panelContenido, BorderLayout.CENTER);
		setControlador(controlador);
	}

	/**
	 * Registra el controlador en el botón de guardado.
	 *
	 * @param c Controlador de eventos
	 */
	public void setControlador(ActionListener c) {
		if (botonGuardar != null) {
			for (ActionListener al : botonGuardar.getActionListeners())
				botonGuardar.removeActionListener(al);
			botonGuardar.addActionListener(c);
		}
	}

	/**
	 * Procesa y guarda la configuración del sistema leyendo los valores de los
	 * spinners.
	 */
	public void procesarGuardar() {
		int carrito = (int) spinnerCarrito.getValue();
		int oferta = (int) spinnerOferta.getValue();
		int pago = (int) spinnerPago.getValue();
		double tasacion = ((Number) spinnerTasacion.getValue()).doubleValue();
		double pesoValoracion = ((Number) spinnerPesoValoracion.getValue()).doubleValue();
		double pesoCompras = ((Number) spinnerPesoCompras.getValue()).doubleValue();
		double pesoCategorias = ((Number) spinnerPesoCategorias.getValue()).doubleValue();

		boolean okTiempos = controlador.configurarTiempos(oferta, carrito, pago);
		boolean okTasacion = controlador.setPrecioTasacion(tasacion);
		boolean okPesos = controlador.setPesosRecomendador(pesoValoracion, pesoCompras, pesoCategorias);

		if (okTiempos && okTasacion && okPesos) {
			actualizarPesos();
			mostrarMensaje("Configuración guardada correctamente.");
		} else {
			mostrarError("Algunos valores no se pudieron guardar. Comprueba que sean válidos.");
		}
	}

	/**
	 * Muestra mensaje de error
	 */
	@Override
	public void mostrarError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Ajusta el valor mínimo de tiempo permitido.
	 *
	 * @param valor valor original
	 * @return valor corregido
	 */
	private int valorTiempo(int valor) {
		if (valor < 1) {
			return 1;
		}
		return valor;
	}

	/**
	 * Ajusta el valor mínimo de tasación permitido.
	 *
	 * @param valor valor original
	 * @return valor corregido
	 */
	private double valorTasacion(double valor) {
		if (valor <= 5.0) {
			return 5.01;
		}
		return valor;
	}


	/**
	 * Actualiza los valores de los spinners de pesos con los valores actuales del
	 * sistema.
	 */
	private void actualizarPesos() {
		spinnerPesoValoracion.setValue(controlador.getPesoValoracion());
		spinnerPesoCompras.setValue(controlador.getPesoCompras());
		spinnerPesoCategorias.setValue(controlador.getPesoCategorias());
	}
}
