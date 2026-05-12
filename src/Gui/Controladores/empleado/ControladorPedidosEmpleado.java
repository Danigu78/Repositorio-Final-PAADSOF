package Gui.Controladores.empleado;

import Gui.empleado.SeccionPedidosEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;

/** Controlador de gestión de pedidos del empleado. */
public class ControladorPedidosEmpleado implements ActionListener {

	public static final String REFRESCAR = "pedidos.refrescar";
	public static final String FILTRAR = "pedidos.filtrar";
	public static final String VER_PEDIDO = "pedidos.ver";
	public static final String PREPARAR_PEDIDO = "pedidos.preparar";

	private final Empleado empleado;
	private SeccionPedidosEmpleado vista;

	public ControladorPedidosEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public void setVista(SeccionPedidosEmpleado vista) {
		this.vista = vista;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (REFRESCAR.equals(accion) || FILTRAR.equals(accion)) {
			vista.cargarTablaPedidos();
		} else if (VER_PEDIDO.equals(accion)) {
			vista.verPedido();
		} else if (PREPARAR_PEDIDO.equals(accion)) {
			vista.prepararPedido();
		}
	}

	public List<Pedido> getPedidos(String estadoElegido) {
		List<Pedido> pedidos = new ArrayList<>();
		String filtro = estadoElegido == null ? "Todos" : estadoElegido;

		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (!"Todos".equals(filtro) && !pedido.getEstado().name().equals(filtro)) {
				continue;
			}
			pedidos.add(pedido);
		}
		return pedidos;
	}

	public Pedido buscarPedidoPorId(String idPedido) {
		if (idPedido == null || idPedido.trim().isBlank()) {
			return null;
		}
		for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
			if (pedido.getIdPedido().equalsIgnoreCase(idPedido.trim())) {
				return pedido;
			}
		}
		return null;
	}

	public ResultadoOperacion prepararPedido(String idPedido) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (idPedido == null || idPedido.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del pedido.");
		}
		Pedido pedido = buscarPedidoPorId(idPedido);
		if (pedido == null) {
			return ResultadoOperacion.error("No existe ningún pedido con ese ID.");
		}
		if (pedido.getEstado() != EstadoPedido.PAGADO) {
			return ResultadoOperacion.error("No se pudo preparar el pedido. Comprueba que esté pagado.");
		}

		boolean ok = empleado.prepararPedido(idPedido.trim());
		guardarSiExito(ok);
		return ok ? ResultadoOperacion.ok("Pedido preparado correctamente.")
				: ResultadoOperacion.error("No se pudo preparar el pedido. Comprueba que esté pagado.");
	}

	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	public String[] crearOpcionesEstado() {
		EstadoPedido[] estados = EstadoPedido.values();
		String[] opciones = new String[estados.length + 1];
		opciones[0] = "Todos";
		for (int i = 0; i < estados.length; i++) {
			opciones[i + 1] = estados[i].name();
		}
		return opciones;
	}

	public String crearTextoPedido(Pedido pedido) {
		if (pedido == null) {
			return "Pedido no encontrado.";
		}

		StringBuilder texto = new StringBuilder();
		texto.append("Pedido: ").append(pedido.getIdPedido()).append("\n");
		texto.append("Cliente: ").append(pedido.getCliente().getNickname()).append("\n");
		texto.append("Estado: ").append(pedido.getEstado()).append("\n");
		texto.append("Total: ").append(formatearPrecio(pedido.getTotal())).append("\n");
		texto.append("Código recogida: ").append(obtenerCodigoRecogida(pedido)).append("\n");
		texto.append("Recogida solicitada: ").append(obtenerTextoRecogidaSolicitada(pedido)).append("\n");
		texto.append("Fecha creación: ").append(pedido.getFechaCreacion()).append("\n");

		if (pedido.getFechaPreparado() != null) {
			texto.append("Fecha preparado: ").append(pedido.getFechaPreparado()).append("\n");
		}
		if (pedido.getFechaEntregado() != null) {
			texto.append("Fecha entregado: ").append(pedido.getFechaEntregado()).append("\n");
		}

		texto.append("\nProductos del pedido:\n");
		texto.append(crearTextoProductosPedido(pedido));
		return texto.toString();
	}

	private String crearTextoProductosPedido(Pedido pedido) {
		StringBuilder texto = new StringBuilder();
		if (pedido.getLineas().isEmpty()) {
			texto.append("Sin productos.");
			return texto.toString();
		}
		for (LineaPedido linea : pedido.getLineas()) {
			ProductoVenta producto = linea.getProducto();
			texto.append("- ").append(producto.getId()).append(" | ");
			texto.append(producto.getNombre()).append(" | ");
			texto.append("cantidad: ").append(linea.getCantidad()).append(" | ");
			texto.append("precio unidad: ").append(formatearPrecio(linea.getPrecioVenta())).append(" | ");
			texto.append("subtotal: ").append(formatearPrecio(linea.getSubtotal())).append("\n");
		}
		return texto.toString();
	}

	public String obtenerCodigoRecogida(Pedido pedido) {
		if (pedido == null || pedido.getCodigoRecogida() == null || pedido.getCodigoRecogida().isBlank()) {
			return "-";
		}
		return pedido.getCodigoRecogida();
	}

	public String obtenerTextoRecogidaSolicitada(Pedido pedido) {
		if (pedido == null || pedido.getEstado() != EstadoPedido.PAGADO) {
			return "-";
		}
		return pedido.isRecogida_solicitada() ? "Sí" : "No";
	}

	public String formatearPrecio(double precio) {
		return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
	}
}
