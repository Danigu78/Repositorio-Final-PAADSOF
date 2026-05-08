package Gui.Controladores;

import Gui.SubpanelIntercambios;
import intercambios.EstadoOferta;
import intercambios.Oferta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador del subpanel de intercambios.
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
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
        } else if (cmd.equals("rechazadas")) {
            vista.mostrarSeccion("RECHAZADAS");
        } else if (cmd.startsWith("aceptar:")) {
            vista.procesarAceptarOferta(cmd.substring(8));
        } else if (cmd.startsWith("rechazar:")) {
            vista.procesarRechazarOferta(cmd.substring(9));
        }
    }

    /**
     * Devuelve las ofertas enviadas pendientes.
     * Solo PENDIENTE — excluye ACEPTADA para que no aparezcan en enviadas
     * una vez aceptadas por el destinatario.
     */
    public List<Oferta> getOfertasEnviadas() {
        return cliente.getOfertasEnEspera();
    }

    /**
     * Devuelve las ofertas recibidas pendientes de respuesta.
     * Solo PENDIENTE — una vez aceptada desaparece de aquí.
     */
    public List<Oferta> getOfertasRecibidas() {
        return cliente.getOfertasParaDecidir();
    }

    /**
     * Devuelve las ofertas aceptadas pendientes de confirmación por empleado.
     */
    public List<Oferta> getOfertasAceptadasPendientes() {
        List<Oferta> resultado = new java.util.ArrayList<>();
        for (Oferta o : cliente.getOfertasParaDecidir()) {
            if (o.getEstado() == EstadoOferta.ACEPTADA) resultado.add(o);
        }
        // También las del origen
        for (Oferta o : cliente.getOfertasEnEspera()) {
            if (o.getEstado() == EstadoOferta.ACEPTADA) resultado.add(o);
        }
        return resultado;
    }

    public List<Oferta> getHistorial() {
        return cliente.getIntercambiosRealizados();
    }

    public List<Oferta> getHistorialRechazadasCaducadas() {
        return cliente.getOfertasRechazadasCaducadas();
    }

    public String getTiempoRestante(Oferta oferta) {
        int tiempoMax = Tienda.getInstancia().getTiempoMaxOferta();
        if (tiempoMax == 0) return "Sin límite";
        if (oferta.haCaducado()) return "Caducada";
        LocalDateTime caducidad = oferta.getFechaOferta().plusMinutes(tiempoMax);
        long minutos = java.time.Duration.between(
            LocalDateTime.now(), caducidad).toMinutes();
        long segundos = java.time.Duration.between(
            LocalDateTime.now(), caducidad).toSeconds() % 60;
        return minutos + "m " + segundos + "s restantes";
    }

    public String getTextoEstado(Oferta oferta) {
        switch (oferta.getEstado()) {
            case PENDIENTE:  return "Pendiente";
            case ACEPTADA:   return "Aceptada — esperando confirmación de empleado";
            case RECHAZADA:  return "Rechazada";
            case CADUCADA:   return "Caducada";
            case REALIZADA:  return "Realizada";
            default:         return oferta.getEstado().toString();
        }
    }

    /**
     * Acepta una oferta recibida y guarda.
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
     * Rechaza una oferta recibida y guarda.
     */
    public boolean rechazarOferta(String idOferta) {
        for (Oferta o : cliente.getOfertasParaDecidir()) {
            if (o.getId().equals(idOferta)) {
                boolean ok = cliente.eliminarOfertadeOfertasPendientes(o);
                if (ok) GuardadoTienda.guardar(Tienda.getInstancia());
                return ok;
            }
        }
        return false;
    }
}