package tienda;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import excepciones.AñoInvalidoException;
import excepciones.RangoFechasInvalidoException;
import intercambios.*;
import usuarios.*;
import ventas.*;
import productos.*;

/**
 * Clase encargada de procesar datos de la tienda para generar informes y
 * ránkings. Proporciona métodos para ordenar clientes según su actividad y
 * calcular ingresos económicos en diferentes rangos temporales.
 * 
 * @author Antonino Albarrán
 * @version 1.0
 */
public class MotorEstadistico {

	/**
	 * Obtiene los clientes ordenados según la cantidad de pedidos cancelados
	 *
	 * @return la lista de clientes ordenada de mayor a menor
	 */
	public List<Cliente> obtenerClientesConMasPedidosCaducados() {
		Tienda tienda = Tienda.getInstancia();

		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}
		// sort hace q ordenemos la lista
		// Comparator.comparingInt indica q debemos ordenarlos segun un int, q
		// calculamos despues de ->
		clientes.sort(Comparator.comparingInt((Cliente c) -> {
			int count = 0;
			for (Pedido p : c.getHistorialPedidos()) {
				if (p.getEstado() == EstadoPedido.CANCELADO) {
					count++;
				}
			}
			return count;
		}).reversed());

		return clientes;
	}

	/**
	 * Devuelve los clientes ordenados por número de compras realizadas
	 *
	 * @return la lista de clientes de mayor a menor número de compras
	 */
	public List<Cliente> obtenerClientesConMasCompras() {
		Tienda tienda = Tienda.getInstancia();

		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}
		// lo mismo
		clientes.sort(Comparator.comparingInt((Cliente c) -> {
			int count = 0;
			for (Pedido p : c.getHistorialPedidos()) {
				if (p.getEstado() == EstadoPedido.PAGADO || p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER
						|| p.getEstado() == EstadoPedido.ENTREGADO) {
					count++;
				}
			}
			return count;
		}).reversed()); // hacemos una lista ascendente

		return clientes;
	}

	/**
	 * Obtiene los clientes que más intercambios han realizado
	 *
	 * @return la lista de clientes ordenada de mayor a menor número de intercambios
	 */
	public List<Cliente> obtenerClientesConMasIntercambios() {

		Tienda tienda = Tienda.getInstancia();

		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}

		List<Oferta> intercambios = tienda.getIntercambiosFinalizados();

		// usams exactamente la misma logics
		clientes.sort(Comparator.comparingInt((Cliente c) -> {

			int count = 0;
			for (Oferta o : intercambios) {
				if (o.getOrigen().equals(c) || o.getDestino().equals(c)) {
					count++;
				}
			}

			return count;

		}).reversed());
		return clientes;
	}

	/**
	 * Calcula los ingresos totales de la tienda entre dos fechas
	 *
	 * @param inicio la fecha inicial del rango
	 * @param fin    la fecha final del rango
	 * @return el total de ingresos entre esas fechas
	 * @throws RangoFechasInvalidoException si el rango no es válido
	 */
	public double calcularIngresosRangoFechas(LocalDate inicio, LocalDate fin) throws RangoFechasInvalidoException {
		if (inicio == null || fin == null || fin.isBefore(inicio)) {
			throw new RangoFechasInvalidoException(inicio, fin);
		}

		Tienda tienda = Tienda.getInstancia();
		if (tienda == null) {
			System.out.println("La tienda no ha sido inicializada.");
			return 0.0;
		} // tenemos en cuenta los tasados y los no tasados
		return this.calcularIngresosVentaRango(inicio, fin) + this.calcularTasacionesEnRango(inicio, fin);
	}

	/**
	 * Calcula los ingresos de cada mes de un año concreto
	 *
	 * @param año el año que se quiere consultar
	 * @return un array con los ingresos de los 12 meses
	 * @throws AñoInvalidoException         si el año no es válido
	 * @throws RangoFechasInvalidoException si alguna fecha del cálculo no es válida
	 */
	public double[] calcularIngresosMesesAño(int año) throws AñoInvalidoException, RangoFechasInvalidoException {
		double[] ingresosPorMes = new double[12];
		if (año <= 0) {
			throw new AñoInvalidoException(año);
		}

		for (int mes = 1; mes <= 12; mes++) {
			YearMonth ym = YearMonth.of(año, mes);// devuelve el mes en la posicion mes del año año
			LocalDate inicio = ym.atDay(1);// primer dia
			LocalDate fin = ym.atEndOfMonth();// ultimo dia
			ingresosPorMes[mes - 1] = this.calcularIngresosRangoFechas(inicio, fin);
		}

		return ingresosPorMes;
	}

	/**
	 * Calcula los ingresos mensuales del año actual
	 *
	 * @return un array con los ingresos de cada mes del año en curso
	 * @throws AñoInvalidoException         si el año actual no fuese válido
	 * @throws RangoFechasInvalidoException si ocurre un problema con las fechas
	 */
	public double[] calcularIngresosMesesAñoActual() throws AñoInvalidoException, RangoFechasInvalidoException {
		return this.calcularIngresosMesesAño(LocalDate.now().getYear());
	}

	/**
	 * Calcula los ingresos obtenidos por ventas entre dos fechas
	 *
	 * @param inicio la fecha inicial
	 * @param fin    la fecha final
	 * @return el total de ventas en ese rango
	 * @throws RangoFechasInvalidoException si el rango no es correcto
	 */
	public double calcularIngresosVentaRango(LocalDate inicio, LocalDate fin) throws RangoFechasInvalidoException {
		if (inicio == null || fin == null || fin.isBefore(inicio)) {
			throw new RangoFechasInvalidoException(inicio, fin);
		}

		Tienda tienda = Tienda.getInstancia();
		if (tienda == null) {
			System.out.println("La tienda no ha sido inicializada.");
		}

		double total = 0.0;
		for (Pedido p : tienda.getHistorialVentas()) {
			if (p.getEstado() == EstadoPedido.CANCELADO)
				continue;// confirmamos q el pedido se realizo
			LocalDate fechaPedido = p.getFechaCreacion().toLocalDate();
			if (!fechaPedido.isBefore(inicio) && !fechaPedido.isAfter(fin)) {
				total += p.getTotal();
			}
		}
		return total;
	}

	/**
	 * Calcula todos los ingresos obtenidos por ventas
	 *
	 * @return el total acumulado de ventas
	 */
	public double calcularIngresosVenta() {
		try {
			return calcularIngresosVentaRango(LocalDate.MIN, LocalDate.MAX);
		} catch (RangoFechasInvalidoException e) {
			return 0.0;
		}
	}

	/**
	 * Calcula los ingresos obtenidos por tasaciones
	 *
	 * @return el total acumulado de tasaciones cobradas
	 */
	public double calcularIngresosTasacion() {

		return calcularTasacionesEnRango(LocalDate.MIN, LocalDate.MAX);
	}

	/**
	 * Suma el importe de las tasaciones realizadas en un rango de fechas
	 *
	 * @param inicio la fecha inicial
	 * @param fin    la fecha final
	 * @return el total cobrado por tasaciones en ese periodo
	 */
	private double calcularTasacionesEnRango(LocalDate inicio, LocalDate fin) {
		double total = 0.0;

		List<Producto2Mano> historial = new ArrayList<>(Tienda.getInstancia().getHistorialProductos2Mano());

		for (Producto2Mano p : historial) {
			Valoracion v = p.getValoracion();
			if (v != null && v.getFecha() != null) {
				LocalDate fechaVal = v.getFecha().toLocalDate();
				if (!fechaVal.isBefore(inicio) && !fechaVal.isAfter(fin)) {

					total += v.getPrecioPagado();
				}
			}
		}
		return total;
	}
}