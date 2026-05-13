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

	private SubpanelEstadisticasGestor vista;
	private Gestor gestor;

	public static class IngresoProducto {
		private ProductoVenta producto;
		private double ingresos;

		public IngresoProducto(ProductoVenta producto, double ingresos) {
			this.producto = producto;
			this.ingresos = ingresos;
		}

		public ProductoVenta getProducto() {
			return producto;
		}

		public double getIngresos() {
			return ingresos;
		}
	}

	public ControladorEstadisticasGestor(SubpanelEstadisticasGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
	}

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

	public List<Cliente> getTopCompras() {
		return gestor.verClientesTopCompras();
	}

	public List<Cliente> getTopIntercambios() {
		return gestor.verClientesTopIntercambios();
	}

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

	public int getAnioActual() {
		return LocalDate.now().getYear();
	}

	public List<ProductoVenta> getProductosParaTablaIngresos() {
		return new ArrayList<>(gestor.consultarIngresosPorProducto().keySet());
	}

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

	public double getIngresosVentas() {
		return gestor.consultarIngresosVenta();
	}

	public double getIngresosTasaciones() {
		return gestor.consultarIngresosTasacion();
	}

	public double[] getIngresosPorMeses() {
		try {
			return gestor.consultarIngresosPorMesesActual();
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			return new double[12];
		}
	}

	public double[] getIngresosPorAño(int año) {
		try {
			return gestor.consultarIngresosPorMeses(año);
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			return new double[12];
		}
	}

	public double getIngresosRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}

	public double getIngresosVentasRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosVentaRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}

	public double getIngresosTasacionRango(LocalDate inicio, LocalDate fin) {
		try {
			return gestor.consultarIngresosTasacionRango(inicio, fin);
		} catch (RangoFechasInvalidoException e) {
			return 0;
		}
	}
}
