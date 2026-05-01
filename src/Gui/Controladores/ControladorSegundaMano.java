package Gui.Controladores;

import Gui.SubpanelSegundaMano;
import productos.EstadoProducto;
import productos.Producto2Mano;
import tienda.FiltroSegundaMano;
import tienda.Tienda;
import usuarios.Cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de segunda mano.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorSegundaMano implements ActionListener{

    private SubpanelSegundaMano vista;
    private Cliente cliente;
    private Tienda tienda;
private Producto2Mano productoSeleccionado;
    
    public ControladorSegundaMano(SubpanelSegundaMano vista, Cliente cliente) {
        this.vista = vista;
        this.cliente = cliente;
        this.tienda = Tienda.getInstancia();
    }
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.startsWith("ver:")) {
            String idProducto = cmd.substring(4);
            for (Producto2Mano p : tienda.buscarSegundaMano()) {
                if (p.getId().equals(idProducto)) {
                    productoSeleccionado = p;
                    vista.verProducto2Mano(p);
                    return;
                }
            }
        }
    }
    /**
     * Devuelve todos los productos de segunda mano disponibles
     * excluyendo los del propio cliente.
     */
    public List<Producto2Mano> obtenerTodos() {
        List<Producto2Mano> todos = tienda.buscarSegundaMano();
        if (cliente == null) return todos;
        List<Producto2Mano> resultado = new ArrayList<>();
        for (Producto2Mano p : todos) {
            if (!p.getPropietario().getNickname().equals(cliente.getNickname())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Filtra productos usando FiltroSegundaMano con nombre, precio y estado.
     * Excluye los productos del propio cliente.
     */
    public List<Producto2Mano> filtrar(String nombre, double precioMin,
                                        double precioMax, EstadoProducto estadoMin) {
        FiltroSegundaMano filtro = new FiltroSegundaMano();
        filtro.setValorMinimo(precioMin);
        filtro.setValorMaximo(precioMax);
        if (estadoMin != null) filtro.setEstadoMinimo(estadoMin);

        List<Producto2Mano> resultado = new ArrayList<>();
        for (Producto2Mano p : tienda.buscarSegundaManoFiltrado(filtro)) {
            if (cliente != null &&
                p.getPropietario().getNickname().equals(cliente.getNickname())) continue;
            if (nombre == null || nombre.isBlank() ||
                p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Devuelve la cartera visible de un usuario por su nickname.
     * Devuelve null si el usuario no existe.
     */
    public List<Producto2Mano> verCarteraDeUsuario(String nickname) {
        if (nickname == null || nickname.isBlank()) return new ArrayList<>();
        List<Producto2Mano> cartera = tienda.verCartera(nickname);
        if (cartera == null) return null;
        List<Producto2Mano> resultado = new ArrayList<>();
        for (Producto2Mano p : cartera) {
            if (p.isVisible() && !p.isBloqueado()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Devuelve los nombres de los estados para el combo — sin NO_ACEPTADO.
     */
    public String[] getEstados() {
        return new String[]{
            "Cualquiera",
            EstadoProducto.PERFECTO.toString(),
            EstadoProducto.MUY_BUENO.toString(),
            EstadoProducto.USO_LIGERO.toString(),
            EstadoProducto.USO_EVIDENTE.toString(),
            EstadoProducto.MUY_USADO.toString(),
            EstadoProducto.DAÑADO.toString()
        };
    }

    /**
     * Convierte el texto del combo al enum EstadoProducto.
     * Devuelve null si es "Cualquiera".
     */
    public EstadoProducto textoAEstado(String texto) {
        if (texto == null || texto.equals("Cualquiera")) return null;
        for (EstadoProducto e : EstadoProducto.values()) {
            if (e.toString().equals(texto)) return e;
        }
        return null;
    }

    /**
     * Indica si hay cliente logueado.
     */
    public boolean hayCliente() {
        return cliente != null;
    }
}