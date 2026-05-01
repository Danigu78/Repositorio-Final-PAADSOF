package Gui.Controladores;

import Gui.SubpanelIntercambios;
import intercambios.EstadoOferta;
import intercambios.Oferta;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**

 * @author Daniel
 * @version 1.0
 */
public class ControladorIntercambios implements ActionListener {

	private SubpanelIntercambios vista;
	private Cliente cliente;

	public ControladorIntercambios(SubpanelIntercambios vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("enviadas")) {
			vista.mostrarSeccion("ENVIADAS");
		} else if (cmd.equals("recibidas")) {
			vista.mostrarSeccion("RECIBIDAS");
		} else if (cmd.equals("historial")) {
			vista.mostrarSeccion("HISTORIAL");
		} else if (cmd.startsWith("rechazar:")) {
			rechazarOferta(cmd.substring(9));
		} else if (cmd.startsWith("aceptar:")) {
			aceptarOferta(cmd.substring(8));
		} else if (cmd.equals("rechazadas")) {
		    vista.mostrarSeccion("RECHAZADAS");
		}
	}

	public List<Oferta> getOfertasEnviadas() {
		return cliente.getOfertasEnEspera();
	}

	public List<Oferta> getOfertasRecibidas() {
		return cliente.getOfertasParaDecidir();
	}

	public List<Oferta> getHistorial() {
		return cliente.getIntercambiosRealizados();
	}

	public List<Oferta> getHistorialRechazadasCaducadas() {
		return cliente.getOfertasRechazadasCaducadas();
	}

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
	 * Devuelve las ofertas recibidas por el cliente pendientes de respuesta.
	 */

	/**
	 * Devuelve el historial de intercambios finalizados del cliente.
	 */

	/**
	 * Devuelve el estado de la oferta como texto.
	 */
	public String getTextoEstado(Oferta oferta) {
		switch (oferta.getEstado()) {
		case PENDIENTE:
			return "Pendiente";
		case ACEPTADA:
			return "Aceptada";
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
	 * Acepta una oferta recibida.
	 */
	private void aceptarOferta(String idOferta) {
		for (Oferta o : cliente.getOfertasParaDecidir()) {
			if (o.getId().equals(idOferta)) {
				cliente.confirmarIntercambio(o);
				vista.mostrarExito("Oferta aceptada. Un empleado confirmará el intercambio.");
				vista.actualizar(cliente);
				return;
			}
		}
		vista.mostrarError("No se encontró la oferta.");
	}

	/**
	 * Rechaza una oferta recibida.
	 */
	private void rechazarOferta(String idOferta) {
		for (Oferta o : cliente.getOfertasParaDecidir()) {
			if (o.getId().equals(idOferta)) {
				boolean ok = cliente.eliminarOfertadeOfertasPendientes(o);
				if (ok) {
					vista.mostrarExito("Oferta rechazada correctamente.");
					vista.actualizar(cliente);
				} else {
					vista.mostrarError("No se pudo rechazar la oferta.");
				}
				return;
			}
		}
		vista.mostrarError("No se encontró la oferta.");
	}
}