package Gui;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import productos.EstadoProducto;
import usuarios.Empleado;

public class SeccionTasacionEmpleado extends AbstractPanelEmpleadoSegundaManoSection {
    private static final long serialVersionUID = 1L;

    public SeccionTasacionEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Tasación de Productos");
        JPanel contenido = getContenido(base);

        JTextField campoId = crearCampo();

        SelectorSegundaMano selector = crearSelectorProductosSegundaMano(
                "Productos de segunda mano",
                "Filtra por texto, valor tasado y estado mínimo. Pulsa una fila para cargar su ID en la tasación.",
                true,
                campoId);

        JPanel bloque = crearBloque("Tasar producto");
        JTextField campoPrecio = crearCampo();
        JComboBox<EstadoProducto> comboEstado = crearCombo(EstadoProducto.values());
        JButton boton = crearBotonAccion("Tasar");

        bloque.add(crearLabel("ID producto"), gbcCampo(1));
        bloque.add(campoId, gbcCampo(2));
        bloque.add(crearLabel("Precio tasado"), gbcCampo(3));
        bloque.add(campoPrecio, gbcCampo(4));
        bloque.add(crearLabel("Estado"), gbcCampo(5));
        bloque.add(comboEstado, gbcCampo(6));
        bloque.add(boton, gbcBoton(7));

        boton.addActionListener(e -> {
            try {
                String id = campoId.getText().trim();
                Double precio = leerDoubleSeguro(campoPrecio.getText());
                EstadoProducto estado = (EstadoProducto) comboEstado.getSelectedItem();

                if (id.isBlank() || precio == null || precio < 0 || estado == null) {
                    mostrarError("Completa correctamente ID, precio y estado.");
                    return;
                }

                empleado.tasarProducto(id, precio, estado);
                recargarTablaProductos2Mano(selector.tabla);
                mostrarMensaje("Tasación enviada.");
            } catch (Exception ex) {
                mostrarError("No se pudo tasar el producto.");
            }
        });

        contenido.add(selector.bloque);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(bloque);

        add(base);
    }
}
