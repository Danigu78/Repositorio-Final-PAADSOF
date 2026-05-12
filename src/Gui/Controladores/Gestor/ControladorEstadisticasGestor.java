package Gui.Controladores.Gestor;

import Gui.Gestor.*;
import productos.ProductoVenta;
import usuarios.Cliente;
import usuarios.Gestor;
import excepciones.AñoInvalidoException;
import excepciones.RangoFechasInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Controlador de estadísticas para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorEstadisticasGestor implements ActionListener {

	/** Vista del subpanel de estadísticas. */
	private SubpanelEstadisticasGestor vista;

	/** Gestor autenticado. */
	private Gestor gestor;

	/**
	 * Clase auxiliar para almacenar ingresos asociados a un producto.
	 */
	public static class IngresoProducto {

		/** Producto asociado. */
		private ProductoVenta producto;

		/** Ingresos generados por el producto. */
		private double ingresos;

		/**
		 * Constructor de la clase IngresoProducto.
		 *
		 * @param producto Producto asociado
		 * @param ingresos Ingresos generados
		 */
		public IngresoProducto(ProductoVenta producto, double ingresos) {
			this.producto = producto;
			this.ingresos = ingresos;
		}

		/**
		 * Devuelve el producto asociado.
		 *
		 * @return Producto
		 */
		public ProductoVenta getProducto() {
			return producto;
		}

		/**
		 * Devuelve los ingresos del producto.
		 *
		 * @return Ingresos generados
		 */
		public double getIngresos() {
			return ingresos;
		}
	}

	/**
	 * Constructor del controlador de estadísticas.
	 *
	 * @param vista  Vista del subpanel de estadísticas
	 * @param gestor Gestor autenticado
	 */
	public ControladorEstadisticasGestor(SubpanelEstadisticasGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
	}

	/**
	 * Gestiona los eventos producidos en la vista.
	 *
	 * @param e Evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("consultarAño")) {
			vista.procesarConsultarAño();
		} else if (cmd.equals("consultarRango")) {
			vista.procesarConsultarRango();
		} else if (cmd.equals("consultarRangoVentas")) {
			vista.procesarConsultarRangoVentas();
		} else if (cmd.equals("consultarRangoTasacion")) {
			vista.procesarConsultarRangoTasacion();
		}
	}

	/**
	 * Devuelve los clientes con más compras.
	 *
	 * @return Lista de clientes ordenados por compras
	 */
	public List<Cliente> getTopCompras() {
		return gestor.verClientesTopCompras();
	}

	/**
	 * Devuelve los clientes con más intercambios.
	 *
	 * @return Lista de clientes ordenados por intercambios
	 */
	public List<Cliente> getTopIntercambios() {
		return gestor.verClientesTopIntercambios();
	}

	/**
	 * Devuelve los ingresos generados por producto.
	 *
	 * @param mayorAMenor true para ordenar de mayor a menor
	 * @return Lista de ingresos por producto
	 */
	public List<IngresoProducto> getIngresosPorProducto(boolean mayorAMenor) {
		Map<ProductoVenta, Double> ingresos = gestor.consultarIngresosPorProducto();
		List<IngresoProducto> resultado = new ArrayList<>();

		for (Map.Entry<ProductoVenta, Double> entrada : ingresos.entrySet()) {
			resultado.add(new IngresoProducto(entrada.getKey(), entrada.getValue()));
		}

		resultado.sort(Comparator.comparingDouble(IngresoProducto::getIngresos));
		if (mayorAMenor) {
			java.util.Collections.reverse(resultado);
		}

		return resultado;
	}

	/**
	 * Devuelve el año actual.
	 *
	 * @return Año actual
	 */
	public int getAnioActual() {
		return LocalDate.now().getYear();
	}

	/**
	 * Devuelve los productos utilizados en la tabla de ingresos.
	 *
	 * @return Lista de productos
	 */
	public List<ProductoVenta> getProductosParaTablaIngresos() {
		return new ArrayList<>(gestor.consultarIngresosPorProducto().keySet());
	}

	/**
	 * Devuelve los ingresos generados por un producto concreto.
	 *
	 * @param producto Producto a consultar
	 * @return Ingresos del producto
	 */
	public double getIngresoProducto(ProductoVenta producto) {
		if (producto == null || producto.getId() == null) {
			return 0.0;
		}
		for (Map.Entry<ProductoVenta, Double> entrada : gestor.consultarIngresosPorProducto().entrySet()) {
			ProductoVenta actual = entrada.getKey();
			if (actual != null && producto.getId().equals(actual.getId())) {
				return entrada.getValue();
			}
		}
		return 0.0;
	}

	/**
	 * Devuelve los ingresos totales por ventas.
	 *
	 * @return Ingresos de ventas
	 */
	public double getIngresosVentas() {
		return gestor.consultarIngresosVenta();
	}

	/**
	 * Devuelve los ingresos totales por tasaciones.
	 *
	 * @return Ingresos de tasaciones
	 */
	public double getIngresosTasaciones() {
		return gestor.consultarIngresosTasacion();
	}

	/**
	 * Devuelve los ingresos mensuales del año actual.
	 *
	 * @return Array con ingresos por mes
	 */
	public double[] getIngresosPorMeses() {
		try {
			return gestor.consultarIngresosPorMesesActual();
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			return new double[12];
		}
	}

	/**
	 * Devuelve los ingresos mensuales de un año concreto.
	 *
	 * @param año Año a consultar
	 * @return Array con ingresos por mes
	 */
	public double[] getIngresosPorAño(int año) {
		try {
			return gestor.consultarIngresosPorMeses(año);
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			return new double[12];
		}
	}

	/**
	 * Devuelve los ingresos totales en un rango de fechas.
	 *
	 * @param inicio Fecha inicial
	 * @param fin    Fecha final
	 * @return Ingresos obtenidos en el rango
	 */
	public double getIngresosRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}

	/**
	 * Devuelve los ingresos por ventas en un rango de fechas.
	 *
	 * @param inicio Fecha inicial
	 * @param fin    Fecha final
	 * @return Ingresos de ventas en el rango
	 */
	public double getIngresosVentasRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosVentaRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}

	/**
	 * Devuelve los ingresos por tasaciones en un rango de fechas.
	 *
	 * @param inicio Fecha inicial
	 * @param fin    Fecha final
	 * @return Ingresos de tasaciones en el rango
	 */
	public double getIngresosTasacionRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosTasacionRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}
}