package Gui;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import usuarios.Empleado;

public class SeccionModificarEmpleado extends AbstractPanelEmpleadoVentaSection {
    private static final long serialVersionUID = 1L;

    public SeccionModificarEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Modificar Productos");
        JPanel contenido = getContenido(base);

        JTextField campoId = crearCampo();

        SelectorVenta selector = crearSelectorProductosVenta(
                "Buscar producto a modificar",
                "Selecciona una fila para cargar el ID y luego modifica descripción o imagen.",
                true,
                campoId);

        JPanel bloque = crearBloque("Modificar descripción o imagen");
        JTextArea areaDescripcion = crearArea();
        JTextField campoImagen = crearCampo();
        JButton botonDescripcion = crearBotonAccion("Guardar descripción");
        JButton botonImagen = crearBotonAccion("Guardar imagen");

        bloque.add(crearLabel("ID producto"), gbcCampo(1));
        bloque.add(campoId, gbcCampo(2));
        bloque.add(crearLabel("Nueva descripción"), gbcCampo(3));
        bloque.add(estilizarScroll(areaDescripcion), gbcCampo(4));
        bloque.add(botonDescripcion, gbcBoton(5));
        bloque.add(crearLabel("Nueva ruta de imagen"), gbcCampo(6));
        bloque.add(campoImagen, gbcCampo(7));
        bloque.add(botonImagen, gbcBoton(8));

        botonDescripcion.addActionListener(e -> {
            String id = campoId.getText().trim();
            String descripcion = areaDescripcion.getText().trim();

            if (id.isBlank() || descripcion.isBlank()) {
                mostrarError("Introduce un ID y una descripción.");
                return;
            }

            boolean ok = empleado.modificarDescripcionProducto(id, descripcion);
            if (ok) {
                mostrarMensaje("Descripción modificada correctamente.");
            } else {
                mostrarError("No se pudo modificar la descripción.");
            }
        });

        botonImagen.addActionListener(e -> {
            String id = campoId.getText().trim();
            String imagen = campoImagen.getText().trim();

            if (id.isBlank() || imagen.isBlank()) {
                mostrarError("Introduce un ID y una ruta de imagen.");
                return;
            }

            boolean ok = empleado.modificarImagenProducto(id, imagen);
            if (ok) {
                mostrarMensaje("Imagen modificada correctamente.");
            } else {
                mostrarError("No se pudo modificar la imagen.");
            }
        });

        contenido.add(selector.bloque);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(bloque);

        add(base);
    }
}
