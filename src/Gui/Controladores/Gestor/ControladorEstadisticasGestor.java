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
 * Controlador de estadísticas para el gestor. Gestiona las consultas de datos
 * estadísticos relacionados con ingresos, clientes y productos desde la vista
 * de estadísticas.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorEstadisticasGestor implements ActionListener {

	/** Vista asociada a las estadísticas del gestor. */
	private SubpanelEstadisticasGestor vista;

	/** Gestor logueado que realiza las consultas. */
	private Gestor gestor;

	/**
	 * Clase auxiliar que representa los ingresos generados por un producto.
	 */
	public static class IngresoProducto {

		/** Producto asociado a los ingresos. */
		private ProductoVenta producto;

		/** Cantidad de ingresos generados. */
		private double ingresos;

		/**
		 * Constructor de la clase IngresoProducto.
		 *
		 * @param producto Producto asociado
		 * @param ingresos Cantidad de ingresos
		 */
		public IngresoProducto(ProductoVenta producto, double ingresos) {
			this.producto = producto;
			this.ingresos = ingresos;
		}

		/**
		 * Devuelve el producto asociado.
		 *
		 * @return Producto asociado
		 */
		public ProductoVenta getProducto() {
			return producto;
		}

		/**
		 * Devuelve los ingresos generados por el producto.
		 *
		 * @return Cantidad de ingresos
		 */
		public double getIngresos() {
			return ingresos;
		}
	}

	/**
	 * Constructor del controlador de estadísticas del gestor.
	 *
	 * @param vista  Vista asociada al controlador
	 * @param gestor Gestor logueado
	 */
	public ControladorEstadisticasGestor(SubpanelEstadisticasGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
	}

	/**
	 * Gestiona los eventos lanzados desde la vista de estadísticas.
	 *
	 * @param e Evento recibido
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
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
	 * Devuelve los clientes con más compras realizadas.
	 *
	 * @return Lista de clientes ordenados por compras
	 */
	public List<Cliente> getTopCompras() {
		return gestor.verClientesTopCompras();
	}

	/**
	 * Devuelve los clientes con más intercambios realizados.
	 *
	 * @return Lista de clientes ordenados por intercambios
	 */
	public List<Cliente> getTopIntercambios() {
		return gestor.verClientesTopIntercambios();
	}

	/**
	 * Devuelve los ingresos generados por producto ordenados según el criterio
	 * indicado.
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
	 * Devuelve el año actual del sistema.
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
	 * Devuelve los ingresos asociados a un producto concreto.
	 *
	 * @param producto Producto a consultar
	 * @return Ingresos generados por el producto
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
	 * Devuelve los ingresos del año actual desglosados por meses.
	 *
	 * @return Array con los ingresos mensuales
	 */
	public double[] getIngresosPorMeses() {
		try {
			return gestor.consultarIngresosPorMesesActual();
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			return new double[12];
		}
	}

	/**
	 * Devuelve los ingresos de un año concreto desglosados por meses.
	 *
	 * @param año Año a consultar
	 * @return Array con los ingresos mensuales
	 */
	public double[] getIngresosPorAño(int año) {
		try {
			return gestor.consultarIngresosPorMeses(año);
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			return new double[12];
		}
	}

	/**
	 * Devuelve los ingresos obtenidos en un rango de fechas.
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
	 * Devuelve los ingresos por ventas obtenidos en un rango de fechas.
	 *
	 * @param inicio Fecha inicial
	 * @param fin    Fecha final
	 * @return Ingresos por ventas en el rango
	 */
	public double getIngresosVentasRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosVentaRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}

	/**
	 * Devuelve los ingresos por tasaciones obtenidos en un rango de fechas.
	 *
	 * @param inicio Fecha inicial
	 * @param fin    Fecha final
	 * @return Ingresos por tasaciones en el rango
	 */
	public double getIngresosTasacionRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosTasacionRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}
}
