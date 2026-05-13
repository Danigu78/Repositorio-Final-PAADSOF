package Gui.Controladores.cliente;

import Gui.cliente.SubpanelDescuentos;
import tienda.Tienda;
import ventas.Descuento;
import ventas.DescuentoCategoria;
import ventas.DescuentoCantidad;
import ventas.DescuentoVolumen;
import ventas.Regalo;
import productos.ProductoVenta;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de descuentos del cliente. 
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorDescuentos implements ActionListener {

	/** Vista del subpanel de descuentos. */
	private SubpanelDescuentos vista;

	/** Instancia de la tienda. */
	private Tienda tienda;

	/**
	 * Constructor del controlador de descuentos.
	 *
	 * @param vista El subpanel de descuentos
	 */
	public ControladorDescuentos(SubpanelDescuentos vista) {
		this.vista = vista;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos de los botones de la vista.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if ("refrescar".equals(cmd)) {
			vista.cargarDescuentos();
		}
	}

	/**
	 * Devuelve todos los descuentos activos en este momento.
	 *
	 * @return Lista de descuentos activos
	 */
	public List<Descuento> getDescuentosActivos() {
		List<Descuento> activos = new ArrayList<>();
		for (Descuento d : tienda.getDescuentosActivos()) {
			if (d instanceof Regalo) {
				Regalo regalo = (Regalo) d;
				if (regalo.getProductoRegalo() == null || regalo.getProductoRegalo().getStockDisponible() <= 0) {
					continue;
				}
			}
			if (d != null && d.estaActivo())
				activos.add(d);
		}
		return activos;
	}

	/**
	 * Devuelve el tipo legible de un descuento.
	 *
	 * @param d El descuento
	 * @return Tipo como String
	 */
	public String getTipo(Descuento d) {
		if (d instanceof DescuentoCategoria)
			return "Por categoría";
		if (d instanceof DescuentoVolumen)
			return "Por volumen de gasto";
		if (d instanceof DescuentoCantidad)
			return "Por cantidad";
		if (d instanceof Regalo)
			return "Regalo";

		else {
			return "";
		}
	}

	/**
	 * Devuelve el detalle legible de un descuento.
	 *
	 * @param d El descuento
	 * @return Detalle como String
	 */
	public String getDetalle(Descuento d) {
		if (d instanceof DescuentoCategoria) {
			DescuentoCategoria dc = (DescuentoCategoria) d;
			String cat = dc.getCategoria() != null ? dc.getCategoria().getNombre() : "-";
			return String.format("%d%% de descuento en productos de la categoría \"%s\"",
					(int) (dc.getPorcentaje() * 100), cat);
		}
		if (d instanceof DescuentoVolumen) {
			DescuentoVolumen dv = (DescuentoVolumen) d;
			return String.format("%d%% de descuento al superar %.2f€ en tu compra", (int) (dv.getPorcentaje() * 100),
					dv.getUmbralMinimo());
		}
		if (d instanceof DescuentoCantidad) {
			DescuentoCantidad dc = (DescuentoCantidad) d;
			String producto = dc.getProducto() == null ? "un mismo producto" : dc.getProducto().getNombre();
			return String.format("%d%% de descuento al comprar %d o más unidades de %s",
					(int) (dc.getPorcentaje() * 100), dc.getCantidadMinima(), producto);
		}
		if (d instanceof Regalo) {
			Regalo r = (Regalo) d;
			String prod = r.getProductoRegalo() != null ? r.getProductoRegalo().getNombre() : "-";
			return String.format("¡Te regalamos \"%s\" al superar %.2f€ en tu compra!", prod, r.getUmbral());
		}
		return d.getNombre();
	}

	/**
	 * Devuelve el período de validez de un descuento formateado.
	 *
	 * @param d El descuento
	 * @return Período como String
	 */
	public String getPeriodo(Descuento d) {
		if (d.getFechaInicio() == null || d.getFechaFin() == null)
			return "";
		return "Válido hasta: " + d.getFechaFin().toLocalDate();
	}

	/**
	 * Comprueba si hay algún DescuentoCategoria activo que afecte a un producto. Lo
	 * usa SubpanelCatalogo para el badge.
	 *
	 * @param producto El producto a comprobar
	 * @return El DescuentoCategoria que aplica o null si no hay ninguno
	 */
	public DescuentoCategoria getDescuentoCategoriaParaProducto(ProductoVenta producto) {
		for (Descuento d : getDescuentosActivos()) {
			if (d instanceof DescuentoCategoria) {
				DescuentoCategoria dc = (DescuentoCategoria) d;
				if (dc.getCategoria() != null && producto.getCategorias().contains(dc.getCategoria()))
					return dc;
			}
		}
		return null;
	}
}
