package Gui.Controladores;

import Gui.SubpanelProducto2Mano;
import productos.Producto2Mano;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador del subpanel de detalle de producto de segunda mano.
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorProducto2Mano implements ActionListener {

    private SubpanelProducto2Mano vista;
    private Producto2Mano producto;
    private Cliente cliente;

    public ControladorProducto2Mano(SubpanelProducto2Mano vista,
                                     Producto2Mano producto, Cliente cliente) {
        this.vista = vista;
        this.producto = producto;
        this.cliente = cliente;
    }

    /**
     * Gestiona los eventos de los botones de la vista.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("volver")) {
            vista.volver();
        } else if (e.getActionCommand().equals("ofertar")) {
            vista.navegarACrearOferta();
        }
    }

    /**
     * Indica si hay cliente logueado y no es el propietario del producto.
     * Solo en ese caso se muestra el botón de ofertar.
     *
     * @return true si puede ofertar
     */
    public boolean puedeOfertar() {
        if (cliente == null) return false;
        return !producto.getPropietario().getNickname().equals(cliente.getNickname());
    }

    /**
     * Devuelve el producto que se está mostrando.
     *
     * @return El producto de segunda mano
     */
    public Producto2Mano getProducto() {
        return producto;
    }

    /**
     * Devuelve el cliente logueado.
     *
     * @return El cliente
     */
    public Cliente getCliente() {
        return cliente;
    }
}