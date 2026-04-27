package Gui.Controladores;

import Gui.SubpanelPedidos;
import tienda.Tienda;
import usuarios.Cliente;
import ventas.EstadoPedido;
import ventas.Pedido;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de pedidos.
 * Gestiona la lógica de obtener pedidos y calcular tiempos restantes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPedidos {

    /** Vista del subpanel pedidos */
    private SubpanelPedidos vista;

    /** Cliente logueado */
    private Cliente cliente;

    /** Instancia de la tienda */
    private Tienda tienda;

    /**
     * Constructor del controlador de pedidos.
     *
     * @param vista   El subpanel pedidos
     * @param cliente El cliente logueado
     */
    public ControladorPedidos(SubpanelPedidos vista, Cliente cliente) {
        this.vista = vista;
        this.cliente = cliente;
        this.tienda = Tienda.getInstancia();
    }

    /**
     * Devuelve todos los pedidos del cliente ordenados del más reciente al más antiguo.
     *
     * @return Lista de pedidos del cliente
     */
    public List<Pedido> getPedidos() {
        List<Pedido> todos = new ArrayList<>(cliente.getHistorialPedidos());
        // Invertimos para mostrar el más reciente primero
        java.util.Collections.reverse(todos);
        return todos;
    }

    /**
     * Devuelve los minutos restantes para pagar un pedido pendiente.
     *
     * @param pedido El pedido pendiente
     * @return Minutos restantes o 0 si no está pendiente
     */
    public long getMinutosRestantesPago(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO) return 0;
        int tiempoMax = tienda.getTiempoMaxPago();
        LocalDateTime caducidad = pedido.getFechaCreacion().plusMinutes(tiempoMax);
        return Math.max(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), caducidad));
    }

    /**
     * Devuelve un texto descriptivo del estado del pedido.
     *
     * @param pedido El pedido
     * @return Texto del estado
     */
    public String getTextoEstado(Pedido pedido) {
        switch (pedido.getEstado()) {
            case PENDIENTE_PAGO:    return "Pendiente de pago";
            case PAGADO:            return "Pagado";
            case LISTO_PARA_RECOGER: return "Listo para recoger";
            case ENTREGADO:         return "Entregado";
            case CANCELADO:         return "Cancelado";
            default:                return pedido.getEstado().toString();
        }
    }

    /**
     * Indica si el pedido está pendiente de pago y no ha caducado.
     *
     * @param pedido El pedido
     * @return true si está pendiente de pago
     */
    public boolean estaPendientePago(Pedido pedido) {
        return pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO && !pedido.isCaducado();
    }
}