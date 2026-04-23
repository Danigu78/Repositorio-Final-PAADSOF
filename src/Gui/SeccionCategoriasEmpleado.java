package Gui;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import usuarios.Empleado;

public class SeccionCategoriasEmpleado extends AbstractPanelEmpleadoVentaSection {
    private static final long serialVersionUID = 1L;

    public SeccionCategoriasEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Gestión de Categorías");
        JPanel contenido = getContenido(base);

        JTextField campoId = crearCampo();
        JTextField campoCategoria = crearCampo();

        SelectorVenta selector = crearSelectorProductosVenta(
                "Buscar producto para categoría",
                "Selecciona una fila para cargar el ID del producto antes de añadir o quitar categorías.",
                true,
                campoId);

        JPanel bloque = crearBloque("Añadir o eliminar producto de categoría");
        JButton botonAnadir = crearBotonAccion("Añadir a categoría");
        JButton botonEliminar = crearBotonAccion("Eliminar de categoría");

        bloque.add(crearLabel("ID producto"), gbcCampo(1));
        bloque.add(campoId, gbcCampo(2));
        bloque.add(crearLabel("Nombre categoría"), gbcCampo(3));
        bloque.add(campoCategoria, gbcCampo(4));

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonAnadir);
        filaBotones.add(botonEliminar);
        bloque.add(filaBotones, gbcBoton(5));

        botonAnadir.addActionListener(e -> {
            String id = campoId.getText().trim();
            String categoria = campoCategoria.getText().trim();

            if (id.isBlank() || categoria.isBlank()) {
                mostrarError("Introduce un ID y una categoría.");
                return;
            }

            boolean ok = empleado.añadirProductoACategoria(id, categoria);
            if (ok) {
                recargarTablaProductos(selector.tabla);
                mostrarMensaje("Producto añadido a la categoría.");
            } else {
                mostrarError("No se pudo añadir el producto a la categoría.");
            }
        });

        botonEliminar.addActionListener(e -> {
            String id = campoId.getText().trim();
            String categoria = campoCategoria.getText().trim();

            if (id.isBlank() || categoria.isBlank()) {
                mostrarError("Introduce un ID y una categoría.");
                return;
            }

            boolean ok = empleado.eliminarProductoDeCategoria(id, categoria);
            if (ok) {
                recargarTablaProductos(selector.tabla);
                mostrarMensaje("Producto eliminado de la categoría.");
            } else {
                mostrarError("No se pudo eliminar el producto de la categoría.");
            }
        });

        contenido.add(selector.bloque);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(bloque);

        add(base);
    }
}
