package Gui.Controladores.cliente;

import Gui.cliente.SubpanelIntercambios;
import intercambios.EstadoOferta;
import intercambios.Oferta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de intercambios del cliente. Gestiona la navegación
 * entre secciones y las acciones de aceptar y rechazar ofertas. Implementa
 * ActionListener según el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorIntercambios implements ActionListener {

	/** Vista del subpanel de intercambios. */
	private SubpanelIntercambios vista;

	/** Cliente logueado. */
	private Cliente cliente;

	/**
	 * Constructor del controlador de intercambios.
	 *
	 * @param vista   El subpanel de intercambios
	 * @param cliente El cliente logueado
	 */
	public ControladorIntercambios(SubpanelIntercambios vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
	}

	/**
	 * Gestiona los eventos de los botones de la vista. Navega entre secciones o
	 * procesa aceptar/rechazar ofertas.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if ("enviadas".equals(cmd)) {
			vista.mostrarSeccion("ENVIADAS");
		} else if ("recibidas".equals(cmd)) {
			vista.mostrarSeccion("RECIBIDAS");
		} else if ("historial".equals(cmd)) {
			vista.mostrarSeccion("HISTORIAL");
		} else if ("rechazadas".equals(cmd)) {
			vista.mostrarSeccion("RECHAZADAS");
		} else if ("aceptadas".equals(cmd)) {
			vista.mostrarSeccion("ACEPTADAS");
		} else if (cmd != null && cmd.startsWith("aceptar:")) {
			vista.procesarAceptarOferta(cmd.substring(8));
		} else if (cmd != null && cmd.startsWith("rechazar:")) {
			vista.procesarRechazarOferta(cmd.substring(9));
		}
	}

	/**
	 * Devuelve las ofertas enviadas por el cliente que están pendientes.
	 *
	 * @return Lista de ofertas enviadas pendientes
	 */
	public List<Oferta> getOfertasEnviadas() {
		List<Oferta> lista = cliente.getOfertasEnEspera();
		System.out.println("Ofertas enviadas: " + lista.size());
		return lista;
	}

	/**
	 * Devuelve las ofertas recibidas por el cliente pendientes de respuesta. Solo
	 * devuelve las que están en estado PENDIENTE.
	 *
	 * @return Lista de ofertas recibidas pendientes
	 */
	public List<Oferta> getOfertasRecibidas() {
		return cliente.getOfertasParaDecidir();
	}

	/**
	 * Devuelve las ofertas aceptadas que están esperando confirmación de un
	 * empleado. Recoge tanto las del origen como las del destino.
	 *
	 * @return Lista de ofertas aceptadas pendientes de confirmación
	 */
	public List<Oferta> getOfertasAceptadasPendientes() {
		List<Oferta> resultado = new ArrayList<>();
		// Las que envié yo y fueron aceptadas
		for (Oferta o : cliente.getOfertasEnEspera()) {
			if (o.getEstado() == EstadoOferta.ACEPTADA)
				resultado.add(o);
		}
		// Las que recibí yo y acepté
		resultado.addAll(cliente.getOfertasAceptadasComoDestino());
		return resultado;
	}

	/**
	 * Devuelve el historial de intercambios realizados del cliente.
	 *
	 * @return Lista de ofertas realizadas
	 */
	public List<Oferta> getHistorial() {
		return cliente.getIntercambiosRealizados();
	}

	/**
	 * Devuelve el historial de ofertas rechazadas o caducadas del cliente.
	 *
	 * @return Lista de ofertas rechazadas o caducadas
	 */
	public List<Oferta> getHistorialRechazadasCaducadas() {
		return cliente.getOfertasRechazadasCaducadas();
	}

	/**
	 * Calcula el tiempo restante de una oferta antes de que caduque.
	 *
	 * @param oferta La oferta a consultar
	 * @return Texto con el tiempo restante o estado de caducidad
	 */
	public String getTiempoRestante(Oferta oferta) {
		int tiempoMax = Tienda.getInstancia().getTiempoMaxOferta();
		if (tiempoMax == 0)
			return "Sin límite";
		if (oferta.haCaducado())
			return "Caducada";
		LocalDateTime caducidad = oferta.getFechaOferta().plusMinutes(tiempoMax);
		long minutos = java.time.Duration.between(LocalDateTime.now(), caducidad).toMinutes();
		long segundos = java.time.Duration.between(LocalDateTime.now(), caducidad).toSeconds() % 60;
		return minutos + "m " + segundos + "s restantes";
	}

	/**
	 * Devuelve el estado de la oferta como texto legible.
	 *
	 * @param oferta La oferta a consultar
	 * @return Texto descriptivo del estado
	 */
	public String getTextoEstado(Oferta oferta) {
		switch (oferta.getEstado()) {
		case PENDIENTE:
			return "Pendiente";
		case ACEPTADA:
			return "Aceptada — esperando confirmación de empleado";
		case RECHAZADA:
			return "Rechazada";
		case CADUCADA:
			return "Caducada";
		case REALIZADA:
			return "Realizada";
		default:
			return oferta.getEstado().toString();
		}
	}

	/**
	 * Acepta una oferta recibida y guarda el estado.
	 *
	 * @param idOferta Id de la oferta a aceptar
	 * @return true si se aceptó correctamente, false si no se encontró
	 */
	public boolean aceptarOferta(String idOferta) {
		for (Oferta o : cliente.getOfertasParaDecidir()) {
			if (o.getId().equals(idOferta)) {
				cliente.confirmarIntercambio(o);
				GuardadoTienda.guardar(Tienda.getInstancia());
				return true;
			}
		}
		return false;
	}

	/**
	 * Rechaza una oferta recibida y guarda el estado.
	 *
	 * @param idOferta Id de la oferta a rechazar
	 * @return true si se rechazó correctamente, false si no se encontró
	 */
	public boolean rechazarOferta(String idOferta) {
		for (Oferta o : cliente.getOfertasParaDecidir()) {
			if (o.getId().equals(idOferta)) {
				boolean ok = cliente.eliminarOfertadeOfertasPendientes(o);
				if (ok)
					GuardadoTienda.guardar(Tienda.getInstancia());
				return ok;
			}
		}
		return false;
	}
}
