package Gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import usuarios.Empleado;

public class SeccionPedidosEmpleado extends AbstractPanelEmpleadoSection {
    private static final long serialVersionUID = 1L;

    public SeccionPedidosEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Gestión de Pedidos");
        JPanel contenido = getContenido(base);

        JPanel bloque = crearBloque("Preparar pedido");
        JTextField campoPedido = crearCampo();
        JButton boton = crearBotonAccion("Preparar pedido");

        bloque.add(crearLabel("ID pedido"), gbcCampo(1));
        bloque.add(campoPedido, gbcCampo(2));
        bloque.add(boton, gbcBoton(3));

        boton.addActionListener(e -> {
            String id = campoPedido.getText().trim();
            if (id.isBlank()) {
                mostrarError("Introduce un ID de pedido.");
                return;
            }

            boolean ok = empleado.prepararPedido(id);
            if (ok) {
                mostrarMensaje("Pedido preparado correctamente.");
            } else {
                mostrarError("No se pudo preparar el pedido.");
            }
        });

        contenido.add(bloque);
        add(base);
    }
}
