package Gui;

import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import usuarios.Empleado;

public class SeccionStockEmpleado extends AbstractPanelEmpleadoVentaSection {
    private static final long serialVersionUID = 1L;

    public SeccionStockEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Gestión de Stock");
        JPanel contenido = getContenido(base);

        JTextField campoId = crearCampo();
        JTextField campoCantidad = crearCampo();

        SelectorVenta selector = crearSelectorProductosVenta(
                "Productos actuales",
                "Filtra por texto, categoría, precio, stock y puntuación. Pulsa una fila para cargar su ID.",
                true,
                campoId);

        JPanel bloqueReponer = crearBloque("Reponer stock");
        JButton botonReponer = crearBotonAccion("Reponer");

        bloqueReponer.add(crearLabel("ID producto"), gbcCampo(1));
        bloqueReponer.add(campoId, gbcCampo(2));
        bloqueReponer.add(crearLabel("Cantidad"), gbcCampo(3));
        bloqueReponer.add(campoCantidad, gbcCampo(4));
        bloqueReponer.add(botonReponer, gbcBoton(5));

        botonReponer.addActionListener(e -> {
            try {
                String id = campoId.getText().trim();
                Integer cantidad = leerEnteroSeguro(campoCantidad.getText());

                if (id.isBlank() || cantidad == null || cantidad <= 0) {
                    mostrarError("Introduce un ID válido y una cantidad positiva.");
                    return;
                }

                boolean ok = empleado.reponerStockProducto(id, cantidad);
                if (ok) {
                    recargarTablaProductos(selector.tabla);
                    mostrarMensaje("Stock repuesto correctamente.");
                    campoCantidad.setText("");
                } else {
                    mostrarError("No se pudo reponer el stock.");
                }
            } catch (Exception ex) {
                mostrarError("Datos inválidos.");
            }
        });

        JPanel bloqueFichero = crearBloque("Cargar productos desde fichero");
        JTextField campoRuta = crearCampo();
        JButton botonExaminar = crearBotonSecundario("Examinar...");
        JButton botonCargar = crearBotonAccion("Cargar fichero");

        bloqueFichero.add(crearLabel("Ruta del fichero"), gbcCampo(1));
        bloqueFichero.add(campoRuta, gbcCampo(2));

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonExaminar);
        filaBotones.add(botonCargar);
        bloqueFichero.add(filaBotones, gbcBoton(3));

        botonExaminar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int resultado = chooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                campoRuta.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        botonCargar.addActionListener(e -> {
            String ruta = campoRuta.getText().trim();
            if (ruta.isBlank()) {
                mostrarError("Introduce o selecciona una ruta.");
                return;
            }

            boolean ok = empleado.cargarProductosFicheroTexto(ruta);
            if (ok) {
                recargarTablaProductos(selector.tabla);
                mostrarMensaje("Fichero procesado correctamente.");
            } else {
                mostrarError("No se pudo cargar el fichero.");
            }
        });

        contenido.add(selector.bloque);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(bloqueReponer);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(bloqueFichero);

        add(base);
    }
}
