package ventas;

import java.time.*;
import java.util.*;

import productos.ProductoVenta;
import tienda.Estadistica;
import tienda.Tienda;
import tienda.TipoNotificacion;
import usuarios.Cliente;

public class Pedido {
	private boolean recogidaSolicitada;
	private final String idPedido;
	private final LocalDateTime fechaCreacion;
	private LocalDateTime fechaPreparado;
	private LocalDateTime fechaEntregado;

	private final Cliente cliente;
	private final List<LineaPedido> lineas;

	private Pago pago;
	private double total;
	private EstadoPedido estado;
	private String codigoRecogida;
	private Descuento descuentoAplicado;

	/**
	 * Constructor de la clase Pedido
	 *
	 * @param cliente el cliente que realiza el pedido
	 * @param carrito el carrito a partir del que se crea
	 */
	public Pedido(Cliente cliente, Carrito carrito) {
		if (cliente == null) {
			throw new IllegalArgumentException("El cliente no puede ser null");
		}

		if (carrito == null) {
			throw new IllegalArgumentException("El carrito no puede ser null");
		}

		if (carrito.estaCaducado()) {
			throw new IllegalArgumentException("No se puede crear un pedido desde un carrito caducado");
		}

		if (carrito.estaVacio()) {
			throw new IllegalArgumentException("No se puede crear un pedido con un carrito vacío");
		}

		Estadistica est = Estadistica.getInstancia();
		this.idPedido = "PEDIDO-" + String.valueOf(est.getnVentas());
		est.setnVentas(est.getnVentas() + 1);
		this.fechaCreacion = LocalDateTime.now();
		this.fechaPreparado = null;
		this.fechaEntregado = null;
		this.recogidaSolicitada = false;
		this.cliente = cliente;
		this.lineas = new ArrayList<>();
		this.pago = null;
		this.estado = EstadoPedido.PENDIENTE_PAGO;
		this.codigoRecogida = null;
		this.descuentoAplicado = carrito.getDescuentoAplicado();

		for (LineaCarrito linea : carrito.getLineas()) {
			ProductoVenta producto = linea.getProducto();
			int cantidad = linea.getCantidad();
			double precioUnitarioFijado = producto.getPrecioOficial();

			this.lineas.add(new LineaPedido(producto, cantidad, precioUnitarioFijado));
		}

		this.total = recalcularTotal(carrito);

		if (carrito.getDescuentoAplicado() instanceof Regalo) {
			Regalo regalo = (Regalo) carrito.getDescuentoAplicado();
			if (regalo.aplicaRegalo(carrito)) {
				ProductoVenta prod = regalo.getProductoRegalo();
				this.lineas.add(new LineaPedido(prod, 1, 0.0));
				prod.setStockDisponible(prod.getStockDisponible() - 1);
			}
		}
	}

	/**
	 * Recalcula el importe total del pedido
	 *
	 * @param carrito el carrito original, si se quiere usar para recalcular
	 *                descuentos
	 * @return el total resultante
	 */
	private double recalcularTotal(Carrito carrito) {
		double subtotal = 0.0;
		for (LineaPedido linea : this.lineas) {
			subtotal += linea.getSubtotal();
		}

		if (this.descuentoAplicado == null) {
			return subtotal;
		}

		if (carrito != null) {
			return this.descuentoAplicado.aplicarDescuento(carrito);
		}

		if (this.descuentoAplicado instanceof DescuentoVolumen) {
			DescuentoVolumen dv = (DescuentoVolumen) this.descuentoAplicado;
			if (!dv.estaActivo() || subtotal < dv.getUmbralMinimo())
				return subtotal;
			return subtotal * (1 - dv.getPorcentaje());
		}

		if (this.descuentoAplicado instanceof DescuentoCategoria) {
			DescuentoCategoria dc = (DescuentoCategoria) this.descuentoAplicado;
			if (!dc.estaActivo())
				return subtotal;
			double total = 0.0;
			for (LineaPedido linea : this.lineas) {
				double sub = linea.getSubtotal();
				if (linea.getProducto().getCategorias().contains(dc.getCategoria())) {
					sub *= (1 - dc.getPorcentaje());
				}
				total += sub;
			}
			return total;
		}

		if (this.descuentoAplicado instanceof DescuentoCantidad) {
			DescuentoCantidad dca = (DescuentoCantidad) this.descuentoAplicado;
			if (!dca.estaActivo())
				return subtotal;
			double total = 0.0;
			for (LineaPedido linea : this.lineas) {
				double sub = linea.getSubtotal();
				if (linea.getCantidad() >= dca.getCantidadMinima()) {
					sub *= (1 - dca.getPorcentaje());
				}
				total += sub;
			}
			return total;
		}

		return subtotal;
	}

	/**
	 * Intenta pagar el pedido
	 *
	 * @param tarjeta   el número de la tarjeta
	 * @param cvv       el código de seguridad
	 * @param caducidad la fecha de caducidad de la tarjeta
	 * @return true si el pago sale bien, false en caso contrario
	 */
	public boolean pagar(String tarjeta, int cvv, Date caducidad) {
		if (isCaducado()) {
			return false;
		}
		if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
			System.out.println("El pedido no está pendiente de pago");
			return false;
		}

		Pago nuevoPago = new Pago(tarjeta, this.total, caducidad, cvv);
		this.pago = nuevoPago;

		if (this.pago.getExito()) {
			this.estado = EstadoPedido.PAGADO;
			this.codigoRecogida = "PICK-" + this.idPedido;
			this.cliente.recibirNotificacionTipo("Pago confirmado. Tu código de recogida es: " + this.codigoRecogida,
					TipoNotificacion.PAGO_EXITOSO);
			return true;
		}
		System.out.println("El pago no se ha podido procesar");
		return false;
	}

	/**
	 * Marca el pedido como listo para recoger
	 *
	 * @return true si se actualiza bien, false si no corresponde ese cambio
	 */
	public boolean marcarPreparado() {
		if (this.estado != EstadoPedido.PAGADO) {
			return false;
		}

		this.estado = EstadoPedido.LISTO_PARA_RECOGER;
		this.fechaPreparado = LocalDateTime.now();
		return true;
	}

	/**
	 * Marca el pedido como entregado
	 *
	 * @return true si se completa el cambio, false en caso contrario
	 */
	public boolean marcarEntregado() {
		if (this.estado != EstadoPedido.LISTO_PARA_RECOGER) {
			return false;
		}

		this.estado = EstadoPedido.ENTREGADO;
		this.fechaEntregado = LocalDateTime.now();
		return true;
	}

	/**
	 * Cancela el pedido y devuelve el stock de sus productos
	 *
	 * @return true si se cancela, false si ya no se puede cancelar
	 */
	public boolean cancelarPedido() {
		if (this.estado == EstadoPedido.CANCELADO || this.estado == EstadoPedido.ENTREGADO) {
			return false;
		}

		for (LineaPedido linea : this.lineas) {
			ProductoVenta producto = linea.getProducto();
			producto.setStockDisponible(producto.getStockDisponible() + linea.getCantidad());
		}

		this.estado = EstadoPedido.CANCELADO;
		this.codigoRecogida = null;
		return true;
	}

	/**
	 * Actualiza el estado del pedido según el nuevo valor indicado
	 *
	 * @param nuevoEstado el estado al que se quiere pasar
	 * @return true si el cambio se hace, false si no es válido
	 */
	public boolean actualizarEstado(EstadoPedido nuevoEstado) {
		if (nuevoEstado == null) {
			return false;
		}

		if (nuevoEstado == this.estado) {
			return true;
		}

		switch (nuevoEstado) {
		case LISTO_PARA_RECOGER:
			return marcarPreparado();
		case ENTREGADO:
			return marcarEntregado();
		case CANCELADO:
			return cancelarPedido();
		default:
			return false;
		}
	}

	/**
	 * Comprueba si un producto forma parte del pedido
	 *
	 * @param p el producto que se quiere buscar
	 * @return true si está incluido, false en caso contrario
	 */
	public boolean productoPertenece(ProductoVenta p) {
		if (p == null) {
			return false;
		}

		for (LineaPedido l : this.lineas) {
			if (l.productoPertenece(p)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Cuenta cuántas unidades hay de un producto concreto en el pedido
	 *
	 * @param idProductoBuscado el id del producto
	 * @return el número total de unidades de ese producto
	 */
	public int contarUnidadesDe(String idProductoBuscado) {
		if (idProductoBuscado == null) {
			return 0;
		}

		int totalUnidades = 0;

		for (LineaPedido linea : this.lineas) {
			if (linea.getProducto().getId().equals(idProductoBuscado)) {
				totalUnidades += linea.getCantidad();
			}
		}

		return totalUnidades;
	}

	/**
	 * Devuelve el precio de venta guardado para un producto del pedido
	 *
	 * @param idProductoBuscado el id del producto
	 * @return el precio de venta, 0.0 si no se encuentra y -1.0 si el id es null
	 */
	public double getPrecioDeProducto(String idProductoBuscado) {
		if (idProductoBuscado == null) {
			return -1.0;
		}

		for (LineaPedido linea : this.lineas) {
			if (linea.getProducto().getId().equals(idProductoBuscado)) {
				if (linea.getCantidad() == 0) {
					return 0.0;
				}
				return linea.getPrecioVenta();
			}
		}

		return 0.0;
	}

	/**
	 * Calcula el total bruto del pedido sin aplicar descuentos
	 *
	 * @return la suma de todas las líneas del pedido
	 */
	public double getTotalBruto() {
		double totalBruto = 0.0;

		for (LineaPedido linea : this.lineas) {
			totalBruto += linea.getSubtotal();
		}

		return totalBruto;
	}

	/**
	 * Cambia el descuento aplicado al pedido mientras siga pendiente de pago
	 *
	 * @param descuentoAplicado el nuevo descuento
	 * @return true si se cambia correctamente, false en caso contrario
	 */
	public boolean setDescuentoAplicado(Descuento descuentoAplicado) {
		if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
			return false;
		}

		this.descuentoAplicado = descuentoAplicado;
		this.total = recalcularTotal(null);
		return true;
	}

	/**
	 * Comprueba si el pedido ha superado el tiempo máximo para pagarse
	 *
	 * @return true si ha caducado, false en caso contrario
	 */
	public boolean isCaducado() {
		if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
			return false;
		}
		return LocalDateTime.now().isAfter(this.fechaCreacion.plusMinutes(Tienda.getInstancia().getTiempoMaxPago()));
	}

	/**
	 * Devuelve el identificador del pedido
	 *
	 * @return el id del pedido
	 */
	public String getIdPedido() {
		return this.idPedido;
	}

	/**
	 * Recupera la fecha de creación del pedido
	 *
	 * @return la fecha en la que se creó
	 */
	public LocalDateTime getFechaCreacion() {
		return this.fechaCreacion;
	}

	/**
	 * Devuelve la fecha en la que el pedido quedó preparado
	 *
	 * @return la fecha de preparación
	 */
	public LocalDateTime getFechaPreparado() {
		return this.fechaPreparado;
	}

	/**
	 * Recupera la fecha de entrega del pedido
	 *
	 * @return la fecha de entrega
	 */
	public LocalDateTime getFechaEntregado() {
		return this.fechaEntregado;
	}

	/**
	 * Devuelve el cliente asociado al pedido
	 *
	 * @return el cliente que hizo el pedido
	 */
	public Cliente getCliente() {
		return this.cliente;
	}

	/**
	 * Recupera las líneas del pedido
	 *
	 * @return una copia de la lista de líneas
	 */
	public List<LineaPedido> getLineas() {
		return new ArrayList<>(this.lineas);
	}

	/**
	 * Devuelve el pago asociado al pedido
	 *
	 * @return el pago registrado
	 */
	public Pago getPago() {
		return this.pago;
	}

	/**
	 * Recupera el importe total del pedido
	 *
	 * @return el total final
	 */
	public double getTotal() {
		return this.total;
	}

	/**
	 * Devuelve el estado actual del pedido
	 *
	 * @return el estado en el que se encuentra
	 */
	public EstadoPedido getEstado() {
		return this.estado;
	}

	/**
	 * Recupera el código de recogida del pedido
	 *
	 * @return el código de recogida
	 */
	public String getCodigoRecogida() {
		return this.codigoRecogida;
	}

	/**
	 * Devuelve el descuento aplicado al pedido
	 *
	 * @return el descuento actual
	 */
	public Descuento getDescuentoAplicado() {
		return this.descuentoAplicado;
	}

	/**
	 * Indica si se ha solicitado la recogida del pedido
	 *
	 * @return true si se ha solicitado, false en caso contrario
	 */
	public boolean isRecogida_solicitada() {
		return recogidaSolicitada;
	}

	/**
	 * Cambia el valor de recogida solicitada
	 *
	 * @param recogida_solicitada el nuevo valor
	 */
	public void setRecogida_solicitada(boolean recogida_solicitada) {
		this.recogidaSolicitada = recogida_solicitada;
	}

	/**
	 * Cambia directamente el estado del pedido
	 *
	 * @param estado el nuevo estado
	 */
	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}

	/**
	 * Cambia el código de recogida del pedido
	 *
	 * @param codigoRecogida el nuevo código
	 */
	public void setCodigoRecogida(String codigoRecogida) {
		this.codigoRecogida = codigoRecogida;
	}
}