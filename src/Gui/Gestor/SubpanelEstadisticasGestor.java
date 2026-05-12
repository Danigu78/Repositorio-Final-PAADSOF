
package Gui.Gestor;

import Gui.TablaProductosVenta;
import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorEstadisticasGestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import usuarios.Cliente;
import usuarios.Gestor;

/**
 * Subpanel de estadísticas para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelEstadisticasGestor extends AbstractPanelGestor {

	private static final long serialVersionUID = 1L;

	/** Controlador asociado al panel de estadísticas */
	private ControladorEstadisticasGestor controlador;

	/** Spinner para selección de año */
	private JSpinner spinnerAño;

	/** Panel dinámico que muestra los ingresos mensuales del año */
	private JPanel panelMesesAño;

	/** Tabla de ingresos por producto */
	private TablaProductosVenta tablaIngresosProductosVenta;

	/** Spinners para consulta de rango total */
	private JSpinner spinnerRangoInicio;
	private JSpinner spinnerRangoFin;

	/** Etiqueta de resultado del rango total */
	private JLabel labelResultadoRango;

	/** Spinners para consulta de rango de ventas */
	private JSpinner spinnerRangoVentasInicio;
	private JSpinner spinnerRangoVentasFin;

	/** Etiqueta de resultado del rango de ventas */
	private JLabel labelResultadoVentas;

	/** Spinners para consulta de rango de tasación */
	private JSpinner spinnerRangoTasacionInicio;
	private JSpinner spinnerRangoTasacionFin;

	/** Etiqueta de resultado del rango de tasación */
	private JLabel labelResultadoTasacion;

	/** Botones de consulta */
	private JButton botonConsultarAño;
	private JButton botonConsultarRango;
	private JButton botonConsultarRangoVentas;
	private JButton botonConsultarRangoTasacion;

	/**
	 * Constructor del panel de estadísticas.
	 *
	 * @param ventana ventana principal de la aplicación
	 * @param gestor  usuario gestor autenticado
	 */
	public SubpanelEstadisticasGestor(VentanaPrincipal ventana, Gestor gestor) {
		super(ventana, gestor);
		this.controlador = new ControladorEstadisticasGestor(this, gestor);
		inicializarUI();
	}

	/**
	 * Inicializa la interfaz del panel
	 */
	private void inicializarUI() {
		JPanel panelContenido = new JPanel();
		panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
		panelContenido.setBackground(VentanaPrincipal.COLOR_FONDO);
		panelContenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(20),
				VentanaPrincipal.escalar(30), VentanaPrincipal.escalar(20), VentanaPrincipal.escalar(30)));

		panelContenido.add(crearSeccionIngresos());
		panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
		panelContenido.add(crearSeccionIngresosProductosComun());
		panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
		panelContenido.add(crearSeccionMesesAño());
		panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
		panelContenido.add(crearSeccionRangoTotal());
		panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));
		panelContenido.add(crearSeccionRangoVentas());
		panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(15)));
		panelContenido.add(crearSeccionRangoTasacion());
		panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
		panelContenido.add(crearSeccionTop("Top clientes por compras", controlador.getTopCompras(), "compras"));
		panelContenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));
		panelContenido.add(
				crearSeccionTop("Top clientes por intercambios", controlador.getTopIntercambios(), "intercambios"));

		JScrollPane scroll = new JScrollPane(panelContenido);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		add(scroll, BorderLayout.CENTER);

		setControlador(controlador);
	}

	/**
	 * Asigna el controlador de eventos a los botones del panel.
	 *
	 * @param c controlador de acciones
	 */
	public void setControlador(ActionListener c) {
		if (botonConsultarAño != null) {
			for (ActionListener al : botonConsultarAño.getActionListeners())
				botonConsultarAño.removeActionListener(al);
			botonConsultarAño.addActionListener(c);
		}
		if (botonConsultarRango != null) {
			for (ActionListener al : botonConsultarRango.getActionListeners())
				botonConsultarRango.removeActionListener(al);
			botonConsultarRango.addActionListener(c);
		}
		if (botonConsultarRangoVentas != null) {
			for (ActionListener al : botonConsultarRangoVentas.getActionListeners())
				botonConsultarRangoVentas.removeActionListener(al);
			botonConsultarRangoVentas.addActionListener(c);
		}
		if (botonConsultarRangoTasacion != null) {
			for (ActionListener al : botonConsultarRangoTasacion.getActionListeners())
				botonConsultarRangoTasacion.removeActionListener(al);
			botonConsultarRangoTasacion.addActionListener(c);
		}
	}

	/**
	 * Sección de ingresos globales del sistema.
	 */
	private JPanel crearSeccionIngresos() {
		JPanel panel = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(15), 0));
		panel.setBackground(VentanaPrincipal.COLOR_FONDO);
		panel.add(crearTarjetaEstadistica("Ingresos por ventas",
				String.format("%.2f EUR", controlador.getIngresosVentas())));
		panel.add(crearTarjetaEstadistica("Ingresos por tasaciones",
				String.format("%.2f EUR", controlador.getIngresosTasaciones())));
		return panel;
	}

	/**
	 * Sección de ingresos por producto.
	 */
	private JPanel crearSeccionIngresosProductosComun() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(VentanaPrincipal.COLOR_TARJETA);
		panel.setBorder(crearBordeTarjeta());

		JLabel titulo = new JLabel("Ingresos por producto");
		titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		titulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, VentanaPrincipal.escalar(10), 0));
		panel.add(titulo, BorderLayout.NORTH);

		List<TablaProductosVenta.ColumnaExtra> extras = new ArrayList<>();
		extras.add(new TablaProductosVenta.ColumnaExtra("Ingresos",
				p -> String.format(Locale.US, "%.2f EUR", controlador.getIngresoProducto(p)), "Ingresos: menor a mayor",
				"Ingresos: mayor a menor", Comparator.comparingDouble(p -> controlador.getIngresoProducto(p))));

		tablaIngresosProductosVenta = new TablaProductosVenta(() -> controlador.getProductosParaTablaIngresos(), extras,
				false);
		panel.add(tablaIngresosProductosVenta, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Convierte un spinner de fecha a LocalDate.
	 *
	 * @param spinner spinner de fecha
	 * @return fecha convertida a LocalDate
	 */
	private LocalDate spinnerALocalDate(JSpinner spinner) {
		java.util.Date fecha = (java.util.Date) spinner.getValue();
		return fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
	}
}