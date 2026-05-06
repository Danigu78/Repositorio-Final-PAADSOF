package Gui.Controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import productos.Producto2Mano;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Empleado;

/** Controlador de confirmación de intercambios. */
public class ControladorIntercambiosEmpleado {

    private final Empleado empleado;

    public ControladorIntercambiosEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public List<Oferta> getOfertas(String estadoElegido) {
        List<Oferta> filtradas = new ArrayList<>();
        String filtro = estadoElegido == null ? "Todos" : estadoElegido;

        for (Oferta oferta : obtenerTodasLasOfertas()) {
            oferta.haCaducado();
            if (!"Todos".equals(filtro) && !oferta.getEstado().name().equals(filtro)) {
                continue;
            }
            filtradas.add(oferta);
        }
        return filtradas;
    }

    public List<Oferta> obtenerTodasLasOfertas() {
        List<Oferta> ofertas = new ArrayList<>();

        for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {
            for (Oferta oferta : cliente.getOfertasPendientes()) {
                if (!ofertas.contains(oferta)) {
                    ofertas.add(oferta);
                }
            }
            for (Oferta oferta : cliente.getHistorialIntercambios()) {
                if (!ofertas.contains(oferta)) {
                    ofertas.add(oferta);
                }
            }
        }

        for (Oferta oferta : Tienda.getInstancia().getIntercambiosFinalizados()) {
            if (!ofertas.contains(oferta)) {
                ofertas.add(oferta);
            }
        }
        return ofertas;
    }

    public Oferta buscarOfertaPorId(String idOferta) {
        if (idOferta == null || idOferta.trim().isBlank()) {
            return null;
        }
        for (Oferta oferta : obtenerTodasLasOfertas()) {
            if (oferta.getId().equalsIgnoreCase(idOferta.trim())) {
                return oferta;
            }
        }
        return null;
    }

    public ResultadoOperacion confirmarOferta(String idOferta) {
        if (empleado == null) {
            return ResultadoOperacion.error("No hay empleado activo.");
        }
        if (idOferta == null || idOferta.trim().isBlank()) {
            return ResultadoOperacion.error("Escribe el ID de la oferta.");
        }
        Oferta oferta = buscarOfertaPorId(idOferta);
        if (oferta == null) {
            return ResultadoOperacion.error("No existe ninguna oferta con ese ID.");
        }
        if (oferta.getEstado() != EstadoOferta.ACEPTADA) {
            return ResultadoOperacion.error("La oferta debe estar en estado ACEPTADA para poder confirmarla.");
        }

        boolean ok = empleado.confirmarIntercambio(oferta);
        return ok ? ResultadoOperacion.ok("Intercambio confirmado correctamente.")
                : ResultadoOperacion.error("No se pudo confirmar el intercambio.");
    }

    public String[] crearOpcionesEstado() {
        EstadoOferta[] estados = EstadoOferta.values();
        String[] opciones = new String[estados.length + 1];
        opciones[0] = "Todos";
        for (int i = 0; i < estados.length; i++) {
            opciones[i + 1] = estados[i].name();
        }
        return opciones;
    }

    public String crearTextoOferta(Oferta oferta) {
        if (oferta == null) {
            return "Oferta no encontrada.";
        }
        StringBuilder texto = new StringBuilder();
        texto.append("Oferta: ").append(oferta.getId()).append("\n");
        texto.append("Estado: ").append(oferta.getEstado()).append("\n");
        texto.append("Fecha: ").append(oferta.getFechaOferta()).append("\n");
        texto.append("Origen: ").append(oferta.getOrigen().getNickname()).append("\n");
        texto.append("Destino: ").append(oferta.getDestino().getNickname()).append("\n\n");

        texto.append("Productos que ofrece ").append(oferta.getOrigen().getNickname()).append(":\n");
        aniadirProductosOferta(texto, oferta.getProductosOfertados());

        texto.append("\nProductos que solicita a ").append(oferta.getDestino().getNickname()).append(":\n");
        aniadirProductosOferta(texto, oferta.getProductosSolicitados());
        return texto.toString();
    }

    private void aniadirProductosOferta(StringBuilder texto, List<Producto2Mano> productos) {
        if (productos == null || productos.isEmpty()) {
            texto.append("Sin productos.\n");
            return;
        }
        for (Producto2Mano producto : productos) {
            texto.append("- ").append(producto.getId()).append(" | ").append(producto.getNombre());
            if (producto.getValoracion() != null) {
                texto.append(" | valor: ").append(formatearPrecio(producto.getValoracion().getPrecioTasacion()));
                texto.append(" | estado: ").append(producto.getValoracion().getEstadoProducto());
            } else {
                texto.append(" | sin valoración");
            }
            texto.append("\n");
        }
    }

    public String formatearPrecio(double precio) {
        return String.format(Locale.US, "%.2f €", precio).replace('.', ',');
    }
}
