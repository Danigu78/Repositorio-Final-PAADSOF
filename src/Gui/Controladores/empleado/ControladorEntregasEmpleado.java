package Gui.controladores.empleado;

import Gui.empleado.SeccionEntregasEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;
import ventas.EstadoPedido;
import ventas.LineaPedido;
import ventas.Pedido;

/** Controlador de entrega de pedidos. */
public class ControladorEntregasEmpleado implements ActionListener {

    public static final String REFRESCAR = "entregas.refrescar";
    public static final String VER_PEDIDO = "entregas.ver";
    public static final String ENTREGAR_PEDIDO = "entregas.entregar";

    private final Empleado empleado;
    private SeccionEntregasEmpleado vista;

    public ControladorEntregasEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setVista(SeccionEntregasEmpleado vista) {
        this.vista = vista;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (vista == null || e == null) {
            return;
        }

        String accion = e.getActionCommand();
        if (REFRESCAR.equals(accion)) {
            vista.cargarTablaEntregas();
        } else if (VER_PEDIDO.equals(accion)) {
            vista.verPedido();
        } else if (ENTREGAR_PEDIDO.equals(accion)) {
            vista.entregarPedido();
        }
    }

    public List<Pedido> getPedidosListosParaRecoger() {
        List<Pedido> pedidos = new ArrayList<>();
        for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
            if (pedido.getEstado() == EstadoPedido.LISTO_PARA_RECOGER) {
                pedidos.add(pedido);
            }
        }
        return pedidos;
    }

    public Pedido buscarPedidoPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isBlank()) {
            return null;
        }
        for (Pedido pedido : Tienda.getInstancia().getHistorialVentas()) {
            if (pedido.getCodigoRecogida() != null && pedido.getCodigoRecogida().equalsIgnoreCase(codigo.trim())) {
                return pedido;
            }
        }
        return null;
    }

    public ResultadoOperacion entregarPedido(String codigo) {
        if (empleado == null) {
            return ResultadoOperacion.error("No hay empleado activo.");
        }
        if (codigo == null || codigo.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el código de recogida.");
        }

        Pedido pedido = buscarPedidoPorCodigo(codigo);
        if (pedido == null) {
            return ResultadoOperacion.error("No existe ningún pedido con ese código.");
        }
        if (pedido.getEstado() != EstadoPedido.LISTO_PARA_RECOGER) {
            return ResultadoOperacion.error("Este pedido no está listo para recoger.");
        }
        if (!pedido.isRecogida_solicitada()) {
            return ResultadoOperacion.error("El cliente todavía no ha solicitado la recogida.");
        }

        boolean ok = empleado.entregarPedido(codigo.trim());
        guardarSiExito(ok);
        return ok ? ResultadoOperacion.ok("Pedido entregado correctamente.")
                : ResultadoOperacion.error("No se pudo entregar el pedido.");
    }

    private void guardarSiExito(boolean ok) {
        if (ok) {
            GuardadoTienda.guardar(Tienda.getInstancia());
        }
    }

    public String crearTextoPedido(Pedido pedido) {
        if (pedido == null) {
            return "Pedido no encontrado.";
        }
        StringBuilder texto = new StringBuilder();
        texto.append("Pedido: ").append(pedido.getIdPedido()).append("\n");
        texto.append("Cliente: ").append(pedido.getCliente().getNickname()).append("\n");
        texto.append("Estado: ").append(pedido.getEstado()).append("\n");
        texto.append("Total: ").append(formatearPrecio(pedido.getTotal())).append("\n");
        texto.append("Código recogida: ").append(obtenerCodigoRecogida(pedido)).append("\n");
        texto.append("Recogida solicitada: ").append(pedido.isRecogida_solicitada() ? "Sí" : "No").append("\n");

        if (pedido.getFechaPreparado() != null) {
            texto.append("Fecha preparado: ").append(pedido.getFechaPreparado()).append("\n");
        }
        if (pedido.getFechaEntregado() != null) {
            texto.append("Fecha entregado: ").append(pedido.getFechaEntregado()).append("\n");
        }

        texto.append("\nProductos del pedido:\n");
        if (pedido.getLineas().isEmpty()) {
            texto.append("Sin productos.");
        } else {
            for (LineaPedido linea : pedido.getLineas()) {
                ProductoVenta producto = linea.getProducto();
                texto.append("- ").append(producto.getId()).append(" | ");
                texto.append(producto.getNombre()).append(" | ");
                texto.append("cantidad: ").append(linea.getCantidad()).append(" | ");
                texto.append("precio unidad: ").append(formatearPrecio(linea.getPrecioVenta())).append(" | ");
                texto.append("subtotal: ").append(formatearPrecio(linea.getSubtotal())).append("\n");
            }
        }
        return texto.toString();
    }

    public String obtenerCodigoRecogida(Pedido pedido) {
        if (pedido == null || pedido.getCodigoRecogida() == null || pedido.getCodigoRecogida().isBlank()) {
            return "-";
        }
        return pedido.getCodigoRecogida();
    }

    public String formatearPrecio(double precio) {
        return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
    }
}
