package Gui.Controladores.cliente;

import Gui.cliente.SubpanelPagoTasacion;
import productos.Producto2Mano;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;


/**
 * Controlador del subpanel de pago de tasación.
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorPagoTasacion implements ActionListener {

    private SubpanelPagoTasacion vista;
    private Cliente cliente;
    private Producto2Mano producto;
    private Tienda tienda;

    public ControladorPagoTasacion(SubpanelPagoTasacion vista,
                                    Cliente cliente, Producto2Mano producto) {
        this.vista = vista;
        this.cliente = cliente;
        this.producto = producto;
        this.tienda = Tienda.getInstancia();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("pagar")) {
            realizarPago();
        } else if (e.getActionCommand().equals("volver")) {
            vista.volverACartera();
        }
    }

    private void realizarPago() {
        String numeroTarjeta = vista.getNumeroTarjeta();
        String cvvStr = vista.getCVV();
        String fechaStr = vista.getFechaCaducidad();

        if (numeroTarjeta.isEmpty() || cvvStr.isEmpty() || fechaStr.isEmpty()) {
            vista.mostrarError("Rellena todos los campos.");
            return;
        }
        if (numeroTarjeta.length() != 16) {
            vista.mostrarError("El número de tarjeta debe tener 16 dígitos.");
            return;
        }
        if (cvvStr.length() != 3) {
            vista.mostrarError("El CVV debe tener 3 dígitos.");
            return;
        }

        int cvv;
        try {
            cvv = Integer.parseInt(cvvStr);
        } catch (NumberFormatException ex) {
            vista.mostrarError("El CVV debe ser numérico.");
            return;
        }

        // Parseamos MM/AA y creamos java.util.Date
        java.sql.Date fechaTarjeta;
        try {
            String[] partes = fechaStr.split("/");
            int mes = Integer.parseInt(partes[0].trim()) - 1;
            int año = 2000 + Integer.parseInt(partes[1].trim());
            Calendar cal = Calendar.getInstance();
            cal.set(año, mes, 1);
            // Convertimos java.util.Date a java.sql.Date correctamente
            fechaTarjeta = new java.sql.Date(cal.getTimeInMillis());
        } catch (Exception ex) {
            vista.mostrarError("Formato de fecha incorrecto. Usa MM/AA.");
            return;
        }

        boolean ok = cliente.solicitarTasacion(producto, numeroTarjeta, cvv, fechaTarjeta);  if (ok) {
            vista.mostrarExito("Tasación solicitada. Un empleado valorará tu producto pronto.");
            vista.volverACartera();
        } else {
            vista.mostrarError("No se pudo procesar el pago de la tasación.");
        }
    }

    public double getPrecioTasacion() {
        return tienda.getPrecioTasacion();
    }

    public String getNombreProducto() {
        return producto.getNombre();
    }
}