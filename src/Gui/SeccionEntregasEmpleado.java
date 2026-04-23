package Gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import usuarios.Empleado;

public class SeccionEntregasEmpleado extends AbstractPanelEmpleadoSection {
    private static final long serialVersionUID = 1L;

    public SeccionEntregasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Entrega de Pedidos");
        JPanel contenido = getContenido(base);

        JPanel bloque = crearBloque("Entregar pedido");
        JTextField campoCodigo = crearCampo();
        JButton boton = crearBotonAccion("Entregar pedido");

        bloque.add(crearLabel("Código de recogida"), gbcCampo(1));
        bloque.add(campoCodigo, gbcCampo(2));
        bloque.add(boton, gbcBoton(3));

        boton.addActionListener(e -> {
            String codigo = campoCodigo.getText().trim();
            if (codigo.isBlank()) {
                mostrarError("Introduce un código de recogida.");
                return;
            }

            boolean ok = empleado.entregarPedido(codigo);
            if (ok) {
                mostrarMensaje("Pedido entregado correctamente.");
            } else {
                mostrarError("No se pudo entregar el pedido.");
            }
        });

        contenido.add(bloque);
        add(base);
    }
}
