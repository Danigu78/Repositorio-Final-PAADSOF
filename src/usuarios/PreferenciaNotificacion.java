package usuarios;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import productos.Categoria;

import tienda.Tienda;
import tienda.TipoNotificacion;

/**
 * Clase que representa las preferencias de notificación de un cliente.
 * 
 * Permite configurar qué tipos de notificaciones desea recibir el cliente, así
 * como gestionar categorías de interés para recibir avisos sobre nuevos
 * productos.
 * 
 * Algunas notificaciones son obligatorias y no pueden desactivarse.
 * 
 * @author Daniel Gonzalez Ureta
 * @version 1.0
 */
public class PreferenciaNotificacion implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Indica si el usuario desea recibir notificaciones de descuentos */
	private boolean descuentos;
	/** Indica si el usuario desea recibir notificaciones de pedidos caducados */
	private boolean pedidosCaducados;
	/** Indica si el usuario desea recibir notificaciones de nuevos intercambios */
	private boolean nuevos_Intercambios;
	/** Indica si el usuario desea recibir notificaciones de pedidos entregados */
	private boolean pedido_entregado;
	/**
	 * Indica si el usuario desea recibir notificaciones de valoraciones completadas
	 */
	private boolean valoracion_completada;
	/** Indica si el usuario desea recibir notificaciones de ofertas caducadas */
	private boolean oferta_caducada;
	/** Lista de categorías en las que el usuario tiene interés */
	private List<Categoria> categoriasInteres;

	/**
	 * Constructor por defecto. Inicializa todas las preferencias configurables como
	 * activadas y crea una lista vacía de categorías de interés.
	 */
	public PreferenciaNotificacion() {
		this.descuentos = true;
		this.pedidosCaducados = true;
		this.nuevos_Intercambios = true;
		this.pedido_entregado = true;
		this.valoracion_completada = true;
		this.oferta_caducada = true; // Inicializamos las categorias de interes a nulll. El cliente las podra meter
		this.categoriasInteres = new ArrayList<>();
	}

	/**
	 * Determina si el usuario debe recibir una notificación concreta.
	 * 
	 * @param tipo Tipo de notificación
	 * @return true si debe recibirla, false en caso contrario
	 */
	public boolean debeRecibirNotificacion(TipoNotificacion tipo) {
		switch (tipo) {
		// Obligatorias
		case CODIGO_RECOGIDA:
		case PEDIDO_LISTO:
		case OFERTA_RECIBIDA:
		case PAGO_EXITOSO:
		case Pago_FALLIDO:
		case CARRITO_CADUCADO:
		case OFERTA_RECHAZADA:
		case INTERCAMBIO_REALIZADO:
		case CATEGORIA_INTERES:
		case CONFIRMACION_RESERVA_CARRITO:
			return true;

		case EMPLEADOS:// el cliente no recibe notificaciones de empleaos
			return false;
		// Configurables
		case DESCUENTO:
			return descuentos;
		case PEDIDO_CADUCADO:
			return pedidosCaducados;
		case PRODUCTO_INTERCAMBIO_NUEVO:
			return nuevos_Intercambios;
		case PEDIDO_ENTREGADO:
			return pedido_entregado;
		case VALORACION_COMPLETADA:
			return valoracion_completada;
		case OFERTA_CADUCADA:
			return oferta_caducada;
		default:
			return false;
		}
	}

	/**
	 * Modifica una preferencia de notificación configurable.
	 * 
	 * @param tipo  Tipo de notificación
	 * @param valor Nuevo valor (true = activado, false = desactivado)
	 */
	public void modificarPreferencia(TipoNotificacion tipo, boolean valor) {
		switch (tipo) {
		case DESCUENTO:
			this.descuentos = valor;
			break;
		case PEDIDO_CADUCADO:
			this.pedidosCaducados = valor;
			break;
		case PRODUCTO_INTERCAMBIO_NUEVO:
			this.nuevos_Intercambios = valor;
			break;
		case PEDIDO_ENTREGADO:
			this.pedido_entregado = valor;
			break;
		case VALORACION_COMPLETADA:
			this.valoracion_completada = valor;
			break;
		case OFERTA_CADUCADA:
			this.oferta_caducada = valor;
			break;
		default:
			System.out.println("Esta notificación es obligatoria y no se puede desactivar.");
			break;
		}
	}

	/**
	 * Añade una categoría a la lista de intereses del cliente.
	 * 
	 * @param nombreCategoria Nombre de la categoría
	 * @return true si se añadió correctamente, false en caso contrario
	 */
	public boolean añadirCategoriaInteres(String nombreCategoria) {
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			System.out.println("El nombre de la categoria no puede estar vacio");
			return false;
		}
		Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (categoria == null) {
			System.out.println("No hay ninguna categoria llamada " + nombreCategoria + ".");
			return false;
		}
		categoriasInteres.add(categoria);
		System.out.println("Categoría '" + nombreCategoria + "' añadida a tus intereses.");
		return true;
	}

	/**
	 * Comprueba si el usuario debe recibir notificaciones sobre productos nuevos de
	 * una categoría concreta.
	 * 
	 * @param nombreCategoria Nombre de la categoría
	 * @return true si la categoría está entre sus intereses
	 */
	public boolean NotificacionesProductosNUevosCategoriasInteres(String nombreCategoria) {
		if (categoriasInteres.isEmpty()) {
			return false;
		}
		Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (c == null) {
			return false;
		}
		return (this.categoriasInteres.contains(c));

	}

	/**
	 * Elimina una categoría de la lista de intereses del usuario.
	 * 
	 * @param nombreCategoria Nombre de la categoría
	 * @return true si se eliminó correctamente
	 */
	public boolean eliminarCategoriaInteres(String nombreCategoria) {
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			System.out.println("El nombre de la categoria no puede estar vacio");
			return false;
		}
		Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (categoria == null) {
			System.out.println("No hay ninguna categoria llamada " + nombreCategoria + ".");
			return false;
		}
		if (!categoriasInteres.contains(categoria)) {
			System.out.println(
					"La categoria con nombre " + nombreCategoria + " no estaba entre tus categorias de interes.");
			return false;
		}
		boolean quitar = categoriasInteres.remove(categoria);
		if (quitar) {
			System.out.println("Categoría '" + nombreCategoria + "' eliminada de tus intereses.");
		}
		return quitar;
	}

	/**
	 * Indica si las notificaciones de descuentos están habilitadas.
	 * 
	 * @return true si están activadas, false en caso contrario
	 */
	public boolean isDescuentos() {
		return descuentos;
	}

	/**
	 * Activa o desactiva las notificaciones de descuentos.
	 * 
	 * @param descuentos nuevo valor
	 */
	public void setDescuentos(boolean descuentos) {
		this.descuentos = descuentos;
	}

	/**
	 * Indica si las notificaciones de pedidos caducados están habilitadas.
	 * 
	 * @return true si están activadas, false en caso contrario
	 */
	public boolean isPedidosCaducados() {
		return pedidosCaducados;
	}

	/**
	 * Activa o desactiva las notificaciones de pedidos caducados.
	 * 
	 * @param pedidosCaducados nuevo valor de la preferencia
	 */
	public void setPedidosCaducados(boolean pedidosCaducados) {
		this.pedidosCaducados = pedidosCaducados;
	}

	/**
	 * Indica si las notificaciones de nuevos intercambios están habilitadas.
	 * 
	 * @return true si están activadas, false en caso contrario
	 */
	public boolean isNuevos_Intercambios() {
		return nuevos_Intercambios;
	}

	/**
	 * Activa o desactiva las notificaciones de nuevos intercambios.
	 * 
	 * @param nuevos_Intercambios nuevo valor de la preferencia
	 */
	public void setNuevos_Intercambios(boolean nuevos_Intercambios) {
		this.nuevos_Intercambios = nuevos_Intercambios;
	}

	/**
	 * Indica si las notificaciones de pedido entregado están habilitadas.
	 * 
	 * @return true si están activadas, false en caso contrario
	 */
	public boolean isPedido_entregado() {
		return pedido_entregado;
	}

	/**
	 * Activa o desactiva las notificaciones de pedido entregado.
	 * 
	 * @param pedido_entregado nuevo valor de la preferencia
	 */
	public void setPedido_entregado(boolean pedido_entregado) {
		this.pedido_entregado = pedido_entregado;
	}

	/**
	 * Indica si las notificaciones de valoración completada están habilitadas.
	 * 
	 * @return true si están activadas, false en caso contrario
	 */
	public boolean isValoracion_completada() {
		return valoracion_completada;
	}

	/**
	 * Activa o desactiva las notificaciones de valoración completada.
	 * 
	 * @param valoracion_completada nuevo valor de la preferencia
	 */
	public void setValoracion_completada(boolean valoracion_completada) {
		this.valoracion_completada = valoracion_completada;
	}

	/**
	 * Indica si las notificaciones de oferta caducada están habilitadas.
	 * 
	 * @return true si están activadas, false en caso contrario
	 */
	public boolean isOferta_caducada() {
		return oferta_caducada;
	}

	/**
	 * Activa o desactiva las notificaciones de oferta caducada.
	 * 
	 * @param oferta_caducada nuevo valor de la preferencia
	 */
	public void setOferta_caducada(boolean oferta_caducada) {
		this.oferta_caducada = oferta_caducada;
	}

	/**
	 * Obtiene la lista de categorías de interés.
	 * 
	 * @return lista de categorías
	 */
	public List<Categoria> getCategoriasInteres() {
		return categoriasInteres;
	}

	/**
	 * Establece una nueva lista de categorías de interés.
	 * 
	 * @param categoriasInteres nueva lista
	 */
	public void setCategoriasInteres(List<Categoria> categoriasInteres) {
		if (categoriasInteres == null) {
			this.categoriasInteres = new ArrayList<>();
		} else {
			this.categoriasInteres = categoriasInteres;
		}
	}

	/**
	 * Devuelve una representación en texto de las preferencias del usuario.
	 * 
	 * @return Cadena con el estado de todas las preferencias
	 */
	@Override
	public String toString() {
		ArrayList<String> nombresCats = new ArrayList<>();
		for (Categoria c : categoriasInteres) {
			nombresCats.add(c.getNombre());
		}

		// Los unimos con una coma y un espacio
		String cats = String.join(", ", nombresCats);

		return "Descuentos: " + (descuentos ? "Activado" : "Desactivado") + "\n" + "Pedidos caducados: "
				+ (pedidosCaducados ? "Activado" : "Desactivado") + "\n" + "Nuevos intercambios: "
				+ (nuevos_Intercambios ? "Activado" : "Desactivado") + "\n" + "Pedido entregado: "
				+ (pedido_entregado ? "Activado" : "Desactivado") + "\n" + "Valoración completada: "
				+ (valoracion_completada ? "Activado" : "Desactivado") + "\n" + "Oferta caducada: "
				+ (oferta_caducada ? "Activado" : "Desactivado") + "\n"
				+ "Categorías de interés sobre las que recibir informacion respecto a los productos: "
				+ (cats.isEmpty() ? "Ninguna" : cats);
	}

	/**
	 * Método llamado automáticamente cuando se guarda una PreferenciaNotificacion.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		inicializarCamposNulos();
		out.defaultWriteObject();
	}

	/**
	 * Método llamado automáticamente cuando se carga una PreferenciaNotificacion.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		inicializarCamposNulos();
	}

	/**
	 * Evita que la lista de categorías de interés quede a null al guardar/cargar.
	 */
	private void inicializarCamposNulos() {
		if (this.categoriasInteres == null) {
			this.categoriasInteres = new ArrayList<>();
		}
	}

}
