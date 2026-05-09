package Gui.Controladores.cliente;

import Gui.cliente.SubpanelCartera;
import productos.Producto2Mano;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFileChooser;

/**
 * Controlador del subpanel de cartera.
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorCartera implements ActionListener {

    private SubpanelCartera vista;
    private Cliente cliente;
    private Tienda tienda;

    public ControladorCartera(SubpanelCartera vista, Cliente cliente) {
        this.vista = vista;
        this.cliente = cliente;
        this.tienda = Tienda.getInstancia();
    }

    /**
     * Gestiona los eventos de los botones de la vista.
     * Distingue la acción por el actionCommand del botón pulsado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("añadir")) {
            // El usuario pulsó añadir — le decimos a la vista que muestre el diálogo
            vista.mostrarDialogoAñadir();
        } else if (e.getActionCommand().equals("explorador")) {
            // El usuario pulsó seleccionar imagen — abrimos el explorador
            abrirExplorador();
        } else if (e.getActionCommand().startsWith("tasar:")) {
            // El usuario pulsó solicitar tasación — cogemos el id del producto
            String idProducto = e.getActionCommand().substring(6);
            irAPagoTasacion(idProducto);
        }
    }

    /**
     * Añade un producto a la cartera del cliente.
     * Lo llama la vista cuando el usuario confirma el diálogo de añadir.
     *
     * @param nombre      Nombre del producto
     * @param descripcion Descripción del producto
     * @param imagen      Nombre del archivo de imagen
     */
    public void añadirProducto(String nombre, String descripcion, String imagen) {
        if (nombre == null || nombre.isBlank()) {
            vista.mostrarError("El nombre no puede estar vacío.");
            return;
        }
        if (descripcion == null || descripcion.isBlank()) {
            vista.mostrarError("La descripción no puede estar vacía.");
            return;
        }
        String rutaImagen = (imagen == null || imagen.isBlank()) ? "default.png" : imagen;
        cliente.subirProducto(nombre, descripcion, rutaImagen);
        GuardadoTienda.guardar(Tienda.getInstancia());
        vista.mostrarExito("Producto añadido a tu cartera correctamente.");
        vista.actualizar(cliente);
    }

    /**
     * Abre el explorador de archivos para seleccionar una imagen.
     * Cuando el usuario selecciona un archivo, actualiza el campo de imagen en la vista.
     */
    private void abrirExplorador() {
        // Creamos el explorador de archivos
        JFileChooser fc = new JFileChooser();
        // Filtramos para que solo muestre imágenes
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Imágenes", "jpg", "jpeg", "png", "gif"));
        // Mostramos el diálogo
        int resultado = fc.showOpenDialog(null);
        // Si el usuario seleccionó un archivo
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Solo guardamos el nombre del archivo, no la ruta completa
            String nombreArchivo = fc.getSelectedFile().getName();
            // Le decimos a la vista que actualice el campo de imagen
            vista.setImagenSeleccionada(nombreArchivo);
        }
    }

    /**
     * Navega al panel de pago de tasación del producto indicado.
     *
     * @param idProducto El id del producto a tasar
     */
    private void irAPagoTasacion(String idProducto) {
        for (Producto2Mano p : cliente.getCarteraIntercambio()) {
            if (p.getId().equals(idProducto)) {
                vista.navegarAPagoTasacion(p);
                return;
            }
        }
    }

    /**
     * Devuelve todos los productos de la cartera del cliente.
     *
     * @return Lista de productos de la cartera
     */
    public List<Producto2Mano> getProductos() {
        return cliente.getCarteraIntercambio();
    }

    /**
     * Indica si el producto está valorado.
     *
     * @param producto El producto a comprobar
     * @return true si tiene valoración
     */
    public boolean estaValorado(Producto2Mano producto) {
        return producto.getValoracion() != null;
    }

    /**
     * Indica si el producto tiene tasación pendiente.
     *
     * @param producto El producto a comprobar
     * @return true si está pendiente de tasación
     */
    public boolean tieneTasacionPendiente(Producto2Mano producto) {
        return tienda.getPendientesTasacion().contains(producto);
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