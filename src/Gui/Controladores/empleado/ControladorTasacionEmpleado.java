package Gui.Controladores.empleado;

import Gui.empleado.SeccionTasacionEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import productos.EstadoProducto;
import productos.Producto2Mano;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;

/**
 * Controlador de tasaciones de productos de segunda mano.
 * 
 * @author Lucas
 * @version 1.0
 */
public class ControladorTasacionEmpleado implements ActionListener {

	/** Acción para refrescar la lista de productos pendientes. */
	public static final String REFRESCAR = "tasacion.refrescar";

	/** Acción para ver la información de un producto. */
	public static final String VER_PRODUCTO = "tasacion.verProducto";

	/** Acción para visualizar la imagen del producto. */
	public static final String VER_IMAGEN = "tasacion.verImagen";

	/** Acción para realizar la tasación de un producto. */
	public static final String TASAR_PRODUCTO = "tasacion.tasar";

	/** Empleado que realiza las operaciones de tasación. */
	private final Empleado empleado;

	/** Vista asociada a la sección de tasaciones. */
	private SeccionTasacionEmpleado vista;

	/**
	 * Crea el controlador de tasaciones.
	 * 
	 * @param empleado empleado que utilizará el controlador
	 */
	public ControladorTasacionEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asigna la vista asociada al controlador.
	 * 
	 * @param vista vista de tasaciones del empleado
	 */
	public void setVista(SeccionTasacionEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona las acciones realizadas desde la interfaz.
	 * 
	 * @param e evento producido en la interfaz
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (REFRESCAR.equals(accion)) {
			vista.cargarTablaPendientes();
		} else if (VER_PRODUCTO.equals(accion)) {
			vista.verProducto();
		} else if (VER_IMAGEN.equals(accion)) {
			vista.verImagenProducto();
		} else if (TASAR_PRODUCTO.equals(accion)) {
			vista.tasarProducto();
		}
	}

	/**
	 * Obtiene los productos pendientes de tasación.
	 * 
	 * @return lista de productos pendientes
	 */
	public List<Producto2Mano> getPendientesTasacion() {
		return new ArrayList<>(Tienda.getInstancia().getPendientesTasacion());
	}

	/**
	 * Busca un producto pendiente de tasación por su identificador.
	 * 
	 * @param idProducto identificador del producto
	 * @return producto encontrado o null si no existe
	 */
	public Producto2Mano buscarProductoPendientePorId(String idProducto) {
		if (idProducto == null || idProducto.trim().isBlank()) {
			return null;
		}
		for (Producto2Mano producto : Tienda.getInstancia().getPendientesTasacion()) {
			if (producto.getId().equalsIgnoreCase(idProducto.trim())) {
				return producto;
			}
		}
		return null;
	}

	/**
	 * Realiza la tasación de un producto de segunda mano.
	 * 
	 * @param idProducto  identificador del producto
	 * @param precioTexto precio de tasación
	 * @param estado      estado asignado al producto
	 * @return resultado de la operación
	 */
	public ResultadoOperacion tasarProducto(String idProducto, String precioTexto, EstadoProducto estado) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto.");
		}
		Double precio = leerDouble(precioTexto);
		if (precio == null || precio < 0) {
			return ResultadoOperacion.error("Escribe un precio válido.");
		}
		if (estado == null) {
			return ResultadoOperacion.error("Selecciona un estado.");
		}

		Producto2Mano producto = buscarProductoPendientePorId(idProducto);
		if (producto == null) {
			return ResultadoOperacion.error("No existe ningún producto pendiente con ese ID.");
		}

		empleado.tasarProducto(idProducto.trim(), precio, estado);
		boolean ok = buscarProductoPendientePorId(idProducto) == null;
		guardarSiExito(ok);

		if (ok) {
			if (estado == EstadoProducto.NO_ACEPTADO) {
				return ResultadoOperacion.ok("Producto rechazado correctamente.");
			}
			return ResultadoOperacion.ok("Producto tasado y publicado para intercambio.");
		}

		return ResultadoOperacion.error("No se pudo tasar el producto.");
	}

	/**
	 * Guarda los cambios en disco si la operación fue correcta.
	 * 
	 * @param ok indica si la operación se realizó correctamente
	 */
	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Crea un texto descriptivo de un producto pendiente de tasación.
	 * 
	 * @param producto producto a mostrar
	 * @return texto descriptivo del producto
	 */
	public String crearTextoProducto(Producto2Mano producto) {
		if (producto == null) {
			return "Producto no encontrado.";
		}

		StringBuilder texto = new StringBuilder();
		texto.append("Producto: ").append(producto.getId()).append(" - ").append(producto.getNombre()).append("\n");
		texto.append("Propietario: ").append(obtenerNombrePropietario(producto)).append("\n");
		texto.append("Descripción: ").append(producto.getDescripcion()).append("\n");
		texto.append("Imagen: ").append(obtenerRutaImagen(producto)).append("\n");
		texto.append("Visible: ").append(producto.isVisible() ? "Sí" : "No").append("\n");
		texto.append("Bloqueado: ").append(producto.isBloqueado() ? "Sí" : "No").append("\n");
		texto.append("Valoración actual: ");

		if (producto.getValoracion() == null) {
			texto.append("Sin valorar");
		} else {
			texto.append(formatearPrecio(producto.getValoracion().getPrecioTasacion())).append(" - ")
					.append(producto.getValoracion().getEstadoProducto());
		}
		return texto.toString();
	}

	/**
	 * Obtiene el nombre del propietario de un producto.
	 * 
	 * @param producto producto consultado
	 * @return nombre del propietario
	 */
	public String obtenerNombrePropietario(Producto2Mano producto) {
		if (producto == null || producto.getPropietario() == null) {
			return "Sin propietario";
		}
		return producto.getPropietario().getNickname();
	}

	/**
	 * Obtiene la ruta de la imagen de un producto.
	 * 
	 * @param producto producto consultado
	 * @return ruta de la imagen
	 */
	public String obtenerRutaImagen(Producto2Mano producto) {
		if (producto == null || producto.getImagenRuta() == null || producto.getImagenRuta().isBlank()) {
			return "-";
		}
		return producto.getImagenRuta();
	}

	/**
	 * Formatea un precio en euros.
	 * 
	 * @param precio precio a formatear
	 * @return precio formateado
	 */
	public String formatearPrecio(double precio) {
		return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
	}

	/**
	 * Convierte un texto en un número decimal.
	 * 
	 * @param texto texto a convertir
	 * @return número decimal o null si no es válido
	 */
	private Double leerDouble(String texto) {
		if (texto == null || texto.trim().isBlank()) {
			return null;
		}
		try {
			return Double.parseDouble(texto.trim().replace(",", "."));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
