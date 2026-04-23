package Gui;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import intercambios.Oferta;
import tienda.Tienda;
import usuarios.Empleado;

public class SeccionIntercambiosEmpleado extends AbstractPanelEmpleadoSection {
    private static final long serialVersionUID = 1L;

    public SeccionIntercambiosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Confirmación de Intercambios");
        JPanel contenido = getContenido(base);

        JPanel bloque = crearBloque("Confirmar intercambio");
        JTextField campoTextoOferta = crearCampo();
        JButton boton = crearBotonAccion("Confirmar");

        javax.swing.JLabel ayuda = crearLabel("Escribe ID o texto identificativo de la oferta");
        ayuda.setForeground(VentanaPrincipal.COLOR_TEXTO2);

        bloque.add(ayuda, gbcCampo(1));
        bloque.add(campoTextoOferta, gbcCampo(2));
        bloque.add(boton, gbcBoton(3));

        boton.addActionListener(e -> {
            Oferta oferta = buscarOfertaPorTexto(campoTextoOferta.getText().trim());
            if (oferta == null) {
                mostrarError("No se encontró ninguna oferta que coincida.");
                return;
            }

            boolean ok = empleado.confirmarIntercambio(oferta);
            if (ok) {
                mostrarMensaje("Intercambio confirmado.");
            } else {
                mostrarError("No se pudo confirmar el intercambio.");
            }
        });

        contenido.add(bloque);
        add(base);
    }

    /**
     * Mantiene la lógica actual del panel original.
     * Conviene reemplazarlo más adelante por un getter claro en Tienda.
     */
    private Oferta buscarOfertaPorTexto(String texto) {
        if (texto == null || texto.isBlank()) return null;

        Tienda tienda = Tienda.getInstancia();
        try {
            for (Method m : tienda.getClass().getMethods()) {
                if (m.getParameterCount() != 0) continue;

                Object resultado = m.invoke(tienda);
                if (resultado == null) continue;

                if (resultado instanceof Collection<?>) {
                    Oferta encontrada = buscarEnColeccion((Collection<?>) resultado, texto);
                    if (encontrada != null) return encontrada;
                } else if (resultado.getClass().isArray()) {
                    Object[] array = (Object[]) resultado;
                    for (Object o : array) {
                        if (o instanceof Oferta && coincideOferta((Oferta) o, texto)) {
                            return (Oferta) o;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Oferta buscarEnColeccion(Collection<?> col, String texto) {
        for (Object o : col) {
            if (o instanceof Oferta && coincideOferta((Oferta) o, texto)) return (Oferta) o;
        }
        return null;
    }

    private boolean coincideOferta(Oferta oferta, String texto) {
        if (oferta == null) return false;

        String buscado = texto.trim().toLowerCase(Locale.ROOT);
        if (String.valueOf(oferta).toLowerCase(Locale.ROOT).contains(buscado)) return true;

        String[] getters = { "getId", "getCodigo", "getIdOferta" };
        for (String getter : getters) {
            try {
                Method m = oferta.getClass().getMethod(getter);
                Object valor = m.invoke(oferta);
                if (valor != null && String.valueOf(valor).trim().equalsIgnoreCase(texto.trim())) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
