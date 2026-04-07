package intercambios;

import java.time.LocalDateTime;
import java.util.List;

import Excepcion.*;
import tienda.Estadistica;
import tienda.Tienda;
import tienda.TipoNotificacion;
import productos.*;
import usuarios.*;

public class Oferta {
	private String id;
	private LocalDateTime fechaOferta;
	private EstadoOferta estado;
	private Cliente origen;
	private Cliente destino;
	private List<Producto2Mano> productosOfertados;
	private List<Producto2Mano> productosSolicitados;

	/**
	 * Constructor de la clase Oferta
	 *
	 * @param origen               el cliente que propone el intercambio
	 * @param destino              el cliente al que va dirigida la oferta
	 * @param productosOfertados   los productos que ofrece el cliente origen
	 * @param productosSolicitados los productos que se piden al cliente destino
	 * @throws ProductoNoTasadoException si alguno de los productos no ha sido
	 *                                   tasado
	 */
	public Oferta(Cliente origen, Cliente destino, List<Producto2Mano> productosOfertados,
			List<Producto2Mano> productosSolicitados) throws ProductoNoTasadoException {

		for (Producto2Mano p : productosOfertados) {
			if (p.getEstado() == null) { // Si el estado es null, es que no ha sido tasado
				throw new ProductoNoTasadoException(p.getId(), p.getNombre());
			}
		}
		for (Producto2Mano p : productosSolicitados) {
			if (p.getEstado() == null) {
				throw new ProductoNoTasadoException(p.getId(), p.getNombre());
			}
		}

		Estadistica est = Estadistica.getInstancia();
		this.id = "OFER-" + String.valueOf(est.getnIntercambiosFinalizados());
		est.setnIntercambiosFinalizados(est.getnIntercambiosFinalizados() + 1);
		this.fechaOferta = LocalDateTime.now();
		this.estado = EstadoOferta.PENDIENTE;
		this.origen = origen;
		this.destino = destino;
		this.productosOfertados = productosOfertados;
		this.productosSolicitados = productosSolicitados;
	}

	/**
	 * Rechaza la oferta si todavía sigue disponible
	 *
	 * @throws OfertaNoDisponibleException si la oferta ya no puede rechazarse
	 */
	public void rechazar() throws OfertaNoDisponibleException {
		if (this.estado != EstadoOferta.PENDIENTE || haCaducado()) {
			throw new OfertaNoDisponibleException(this.id);
		}

		this.estado = EstadoOferta.RECHAZADA;
		for (Producto2Mano p : productosOfertados)
			p.setBloqueado(false);
		this.origen.getOfertasPendientes().remove(this);
		this.destino.getOfertasPendientes().remove(this);
		this.origen.recibirNotificacionTipo("Tu oferta con ID " + this.getId() + " ha sido RECHAZADA por el cliente "
				+ this.destino.getNickname() + ".", TipoNotificacion.OFERTA_RECHAZADA);
	}

	/**
	 * Marca la oferta como aceptada
	 *
	 * @throws OfertaNoDisponibleException si la oferta ya no está disponible
	 */
	public void aceptarOferta() throws OfertaNoDisponibleException {
		// Validamos disponibilidad
		if (this.estado != EstadoOferta.PENDIENTE || haCaducado()) {
			throw new OfertaNoDisponibleException(this.id);
		}
		this.estado = EstadoOferta.ACEPTADA;
	}

	/**
	 * Acepta la oferta y realiza el intercambio
	 *
	 * @throws OfertaNoDisponibleException si la oferta ya no puede ejecutarse
	 */
	public void aceptarYEjecutar() throws OfertaNoDisponibleException {
		if (this.estado != EstadoOferta.PENDIENTE && this.estado != EstadoOferta.ACEPTADA) {
			throw new OfertaNoDisponibleException(this.id);
		}
		if (haCaducado()) {
			throw new OfertaNoDisponibleException(this.id);
		}

		origen.getHistorialIntercambios().add(this);
		destino.getHistorialIntercambios().add(this);
		origen.getOfertasPendientes().remove(this);
		destino.getOfertasPendientes().remove(this);

		for (Producto2Mano p : this.productosOfertados) {
			origen.getCarteraIntercambio().remove(p);
			p.setBloqueado(false);

		}
		for (Producto2Mano p : productosSolicitados) {
			destino.getCarteraIntercambio().remove(p);

		}
		Tienda.getInstancia().registrarIntercambioFinalizado(this);
		origen.recibirNotificacionTipo("¡Intercambio ID " + this.id + " aceptado por el usuario "
				+ this.getDestino().getNickname() + "! Preparando envío.", TipoNotificacion.INTERCAMBIO_REALIZADO);
		destino.recibirNotificacionTipo("Has aceptado el intercambio con el usuario " + this.origen.getNickname()
				+ ". Los productos han salido de tu inventario.", TipoNotificacion.INTERCAMBIO_REALIZADO);
		this.estado = EstadoOferta.REALIZADA;
	}

	/**
	 * Comprueba si la oferta ha superado el tiempo máximo disponible
	 *
	 * @return true si ha caducado, false si sigue vigente
	 */
	public boolean haCaducado() {
		int tiempoMax = Tienda.getInstancia().getTiempoMaxOferta();
		if (tiempoMax == 0)
			return false;
		boolean caducada = LocalDateTime.now().isAfter(fechaOferta.plusMinutes(tiempoMax));
		if (caducada && this.estado == EstadoOferta.PENDIENTE) {
			this.estado = EstadoOferta.CADUCADA;
		}
		return caducada;
	}

	/**
	 * Muestra por pantalla un resumen de la oferta y sus productos
	 */
	public void imprimirResumen() {
		System.out.println("Resumen de la oferta:");
		System.out.println("  [" + id + "]" + " | estado: " + estado + " | " + origen.getNickname() + " -> "
				+ destino.getNickname());
		System.out.println("  Productos ofertados por " + origen.getNickname() + ":");
		for (Producto2Mano p : productosOfertados) {
			System.out.println("   -> " + p.resumen());
		}
		System.out.println("  Productos solicitados a " + destino.getNickname() + ":");
		for (Producto2Mano p : productosSolicitados) {
			System.out.println("   -> " + p.resumen());
		}
	}

	/**
	 * Devuelve el identificador de la oferta
	 *
	 * @return el id de la oferta
	 */
	public String getId() {
		return id;
	}

	/**
	 * Recupera la fecha en la que se creó la oferta
	 *
	 * @return la fecha de creación
	 */
	public LocalDateTime getFechaOferta() {
		return fechaOferta;
	}

	/**
	 * Devuelve el estado actual de la oferta
	 *
	 * @return el estado en el que se encuentra
	 */
	public EstadoOferta getEstado() {
		return estado;
	}

	/**
	 * Cambia el estado de la oferta
	 *
	 * @param estado el nuevo estado
	 */
	public void setEstado(EstadoOferta estado) {
		this.estado = estado;
	}

	/**
	 * Recupera los productos que se ofrecen en el intercambio
	 *
	 * @return la lista de productos ofertados
	 */
	public List<Producto2Mano> getProductosOfertados() {
		return productosOfertados;
	}

	/**
	 * Recupera los productos que se solicitan en el intercambio
	 *
	 * @return la lista de productos pedidos
	 */
	public List<Producto2Mano> getProductosSolicitados() {
		return productosSolicitados;
	}

	/**
	 * Devuelve el cliente que inició la oferta
	 *
	 * @return el cliente origen
	 */
	public Cliente getOrigen() {
		return this.origen;
	}

	/**
	 * Devuelve el cliente al que va dirigida la oferta
	 *
	 * @return el cliente destino
	 */
	public Cliente getDestino() {
		return this.destino;
	}
}