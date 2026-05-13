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

/** Controlador de gestión de pedidos del empleado. 
 * 
 * @author Lucas
 * @version 1.0
 * */
public class ControladorPedidosEmpleado implements ActionListener {

	/** Acción para refrescar la tabla de pedidos. */
	public static final String REFRESCAR = "pedidos.refrescar";
	
	/** Acción para filtrar pedidos por estado. */
	public static final String FILTRAR = "pedidos.filtrar";
	
	/** Acción para visualizar un pedido. */
	public static final String VER_PEDIDO = "pedidos.ver";
	
	/** Acción para preparar un pedido. */
	public static final String PREPARAR_PEDIDO = "pedidos.preparar";

	/** Empleado que realiza la gestión de pedidos. */
	private final Empleado empleado;
	

	/** Vista asociada a la sección de pedidos. */
	private SeccionPedidosEmpleado vista;


	/**
	 * Crea el controlador de pedidos del empleado.
	 * 
	 * @param empleado empleado que gestionará los pedidos
	 */
	public ControladorPedidosEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asocia la vista de gestión de pedidos con el controlador.
	 *
	 * @param vista vista de pedidos del empleado
	 */
	public void setVista(SeccionPedidosEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona las acciones realizadas desde la interfaz de pedidos.
	 *
	 * @param e evento lanzado por la interfaz
	 * 
	 */
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

	/**
	 * Obtiene la lista de pedidos filtrados por estado.
	 *
	 * @param estadoElegido estado por el que se desea filtrar
	 * @return lista de pedidos filtrados
	 * 
	 */
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

	/**
	 * Busca un pedido a partir de su identificador.
	 *
	 * @param idPedido identificador del pedido
	 * @return pedido encontrado o null si no existe
	 */
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

	/**
	 * Prepara un pedido pagado para su posterior recogida o entrega.
	 *
	 * @param idPedido identificador del pedido
	 * @return resultado de la operación
	 * 
	 */
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

	/**
	 * Guarda el estado de la tienda si la operación se realizó correctamente.
	 *
	 * @param ok indica si la operación fue exitosa
	 */
	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Genera un array con las opciones de estados disponibles para pedidos.
	 *
	 * @return array de estados disponibles
	 */
	public String[] crearOpcionesEstado() {
		EstadoPedido[] estados = EstadoPedido.values();
		String[] opciones = new String[estados.length + 1];
		opciones[0] = "Todos";
		for (int i = 0; i < estados.length; i++) {
			opciones[i + 1] = estados[i].name();
		}
		return opciones;
	}


	/**
	 * Genera un texto descriptivo con toda la información de un pedido.
	 *
	 * @param pedido pedido del que se desea obtener la información
	 * @return texto descriptivo del pedido

	 */
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


	/**
	 * Genera un texto con los productos incluidos en un pedido.
	 * 
	 * @param pedido pedido del que se obtienen los productos
	 * @return texto descriptivo de las líneas del pedido
	 */
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


	/**
	 * Obtiene el código de recogida de un pedido.
	 *
	 * @param pedido pedido del que se obtiene el código
	 * @return código de recogida o "-" si no existe
	 */
	public String obtenerCodigoRecogida(Pedido pedido) {
		if (pedido == null || pedido.getCodigoRecogida() == null || pedido.getCodigoRecogida().isBlank()) {
			return "-";
		}
		return pedido.getCodigoRecogida();
	}


	/**
	 * Obtiene un texto indicando si la recogida del pedido ha sido solicitada.
	 *
	 * @param pedido pedido a comprobar
	 * @return "Sí", "No" o "-" según el estado del pedido
	 */
	public String obtenerTextoRecogidaSolicitada(Pedido pedido) {
		if (pedido == null || pedido.getEstado() != EstadoPedido.PAGADO) {
			return "-";
		}
		return pedido.isRecogida_solicitada() ? "Sí" : "No";
	}

	/**
	 * Formatea un precio en euros con dos decimales.
	 *
	 * @param precio precio a formatear
	 * @return texto con el precio formateado
	 */
	public String formatearPrecio(double precio) {
		return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
	}
}
