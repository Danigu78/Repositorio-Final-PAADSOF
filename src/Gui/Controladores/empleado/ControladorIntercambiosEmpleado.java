package Gui.Controladores.empleado;

import Gui.empleado.SeccionIntercambiosEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import productos.Producto2Mano;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;


/**
 * Controlador de confirmación de intercambios.
 *
 */

public class ControladorIntercambiosEmpleado implements ActionListener {

	/** Acción para refrescar la tabla de ofertas. */
	public static final String REFRESCAR = "intercambios.refrescar";
	
	/** Acción para aplicar filtros sobre las ofertas. */
	public static final String FILTRAR = "intercambios.filtrar";
	
	/** Acción para visualizar una oferta concreta. */
	public static final String VER_OFERTA = "intercambios.ver";
	
	/** Acción para confirmar una oferta aceptada. */
	public static final String CONFIRMAR_OFERTA = "intercambios.confirmar";

	/** Empleado autenticado que realiza las operaciones. */
	private final Empleado empleado;
	
	/** Vista asociada al controlador. */
	private SeccionIntercambiosEmpleado vista;

	/**
	 * Constructor del controlador.
	 *
	 * @param empleado empleado que utilizará la sección
	 */
	public ControladorIntercambiosEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asocia la vista al controlador.
	 *
	 * @param vista panel de intercambios del empleado
	 */
	public void setVista(SeccionIntercambiosEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona las acciones lanzadas desde la interfaz.
	 *
	 * @param e evento recibido
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (REFRESCAR.equals(accion) || FILTRAR.equals(accion)) {
			vista.cargarTablaOfertas();
		} else if (VER_OFERTA.equals(accion)) {
			vista.verOferta();
		} else if (CONFIRMAR_OFERTA.equals(accion)) {
			vista.confirmarOferta();
		}
	}

	/**
	 * Obtiene las ofertas filtradas por estado.
	 *
	 * @param estadoElegido estado seleccionado
	 * @return lista de ofertas filtradas
	 */
	public List<Oferta> getOfertas(String estadoElegido) {
		List<Oferta> filtradas = new ArrayList<>();
		String filtro = estadoElegido == null ? "Todos" : estadoElegido;

		for (Oferta oferta : obtenerTodasLasOfertas()) {
			oferta.haCaducado();
			if (!"Todos".equals(filtro) && !oferta.getEstado().name().equals(filtro)) {
				continue;
			}
			filtradas.add(oferta);
		}
		return filtradas;
	}

	/**
	 * Recupera todas las ofertas registradas en la tienda.
	 *
	 * @return lista completa de ofertas
	 */
	public List<Oferta> obtenerTodasLasOfertas() {
		List<Oferta> ofertas = new ArrayList<>();

		for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {
			for (Oferta oferta : cliente.getOfertasPendientes()) {
				if (!ofertas.contains(oferta)) {
					ofertas.add(oferta);
				}
			}
			for (Oferta oferta : cliente.getHistorialIntercambios()) {
				if (!ofertas.contains(oferta)) {
					ofertas.add(oferta);
				}
			}
		}

		for (Oferta oferta : Tienda.getInstancia().getIntercambiosFinalizados()) {
			if (!ofertas.contains(oferta)) {
				ofertas.add(oferta);
			}
		}
		return ofertas;
	}

	/**
	 * Busca una oferta mediante su identificador.
	 *
	 * @param idOferta ID de la oferta
	 * @return oferta encontrada o null si no existe
	 */
	public Oferta buscarOfertaPorId(String idOferta) {
		if (idOferta == null || idOferta.trim().isBlank()) {
			return null;
		}
		for (Oferta oferta : obtenerTodasLasOfertas()) {
			if (oferta.getId().equalsIgnoreCase(idOferta.trim())) {
				return oferta;
			}
		}
		return null;
	}

	/**
	 * Confirma un intercambio aceptado.
	 *
	 * @param idOferta identificador de la oferta
	 * @return resultado de la operación
	 */
	public ResultadoOperacion confirmarOferta(String idOferta) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (idOferta == null || idOferta.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID de la oferta.");
		}
		Oferta oferta = buscarOfertaPorId(idOferta);
		if (oferta == null) {
			return ResultadoOperacion.error("No existe ninguna oferta con ese ID.");
		}
		if (oferta.getEstado() != EstadoOferta.ACEPTADA) {
			return ResultadoOperacion.error("La oferta debe estar en estado ACEPTADA para poder confirmarla.");
		}

		boolean ok = empleado.confirmarIntercambio(oferta);
		guardarSiExito(ok);
		return ok ? ResultadoOperacion.ok("Intercambio confirmado correctamente.")
				: ResultadoOperacion.error("No se pudo confirmar el intercambio.");
	}

	/**
	 * Guarda el estado de la tienda si la operación fue correcta.
	 *
	 * @param ok true si la operación tuvo éxito
	 */
	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Crea las opciones disponibles para el filtro de estados.
	 *
	 * @return array con los nombres de los estados
	 */
	public String[] crearOpcionesEstado() {
		EstadoOferta[] estados = EstadoOferta.values();
		String[] opciones = new String[estados.length + 1];
		opciones[0] = "Todos";
		for (int i = 0; i < estados.length; i++) {
			opciones[i + 1] = estados[i].name();
		}
		return opciones;
	}

	/**
	 * Genera un texto descriptivo con los datos completos de una oferta.
	 *  
	 * @param oferta oferta a mostrar
	 * @return texto descriptivo
	 */
	public String crearTextoOferta(Oferta oferta) {
		if (oferta == null) {
			return "Oferta no encontrada.";
		}
		StringBuilder texto = new StringBuilder();
		texto.append("Oferta: ").append(oferta.getId()).append("\n");
		texto.append("Estado: ").append(oferta.getEstado()).append("\n");
		texto.append("Fecha: ").append(oferta.getFechaOferta()).append("\n");
		texto.append("Origen: ").append(oferta.getOrigen().getNickname()).append("\n");
		texto.append("Destino: ").append(oferta.getDestino().getNickname()).append("\n\n");

		texto.append("Productos que ofrece ").append(oferta.getOrigen().getNickname()).append(":\n");
		aniadirProductosOferta(texto, oferta.getProductosOfertados());

		texto.append("\nProductos que solicita a ").append(oferta.getDestino().getNickname()).append(":\n");
		aniadirProductosOferta(texto, oferta.getProductosSolicitados());
		return texto.toString();
	}

	/**
	 * Añade información de los productos de una oferta al texto descriptivo.
	 *
	 * @param texto buffer de texto
	 * @param productos lista de productos
	 */
	private void aniadirProductosOferta(StringBuilder texto, List<Producto2Mano> productos) {
		if (productos == null || productos.isEmpty()) {
			texto.append("Sin productos.\n");
			return;
		}
		for (Producto2Mano producto : productos) {
			texto.append("- ").append(producto.getId()).append(" | ").append(producto.getNombre());
			if (producto.getValoracion() != null) {
				texto.append(" | valor: ").append(formatearPrecio(producto.getValoracion().getPrecioTasacion()));
				texto.append(" | estado: ").append(producto.getValoracion().getEstadoProducto());
			} else {
				texto.append(" | sin valoración");
			}
			texto.append("\n");
		}
	}

	/**
	 * Formatea un precio en euros con dos decimales.
	 *
	 * @param precio valor numérico
	 * @return precio formateado
	 */
	public String formatearPrecio(double precio) {
		return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
	}
}
