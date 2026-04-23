package Gui;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import productos.LineaPack;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;

public class SeccionPacksEmpleado extends AbstractPanelEmpleadoVentaSection {
    private static final long serialVersionUID = 1L;

    public SeccionPacksEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Gestión de Packs");
        JPanel contenido = getContenido(base);

        JTextField campoIdProductoLinea = crearCampo();
        JTextField campoIdProducto = crearCampo();

        SelectorVenta selector = crearSelectorProductosVenta(
                "Catálogo para construir packs",
                "Filtra y selecciona productos. El ID seleccionado se cargará para añadir líneas o modificar packs.",
                true,
                campoIdProductoLinea, campoIdProducto);

        JPanel bloqueCrear = crearBloque("Crear pack");
        JTextField campoNombre = crearCampo();
        JTextArea areaDescripcionPack = crearArea();
        JTextField campoImagen = crearCampo();
        JTextField campoPrecio = crearCampo();
        JTextField campoStock = crearCampo();
        JTextArea areaLineas = crearArea();
        JTextField campoUnidadesLinea = crearCampo();
        JButton botonAgregarLinea = crearBotonSecundario("Añadir línea");
        JButton botonCrear = crearBotonAccion("Crear pack");

        bloqueCrear.add(crearLabel("Nombre"), gbcCampo(1));
        bloqueCrear.add(campoNombre, gbcCampo(2));
        bloqueCrear.add(crearLabel("Descripción"), gbcCampo(3));
        bloqueCrear.add(estilizarScroll(areaDescripcionPack), gbcCampo(4));
        bloqueCrear.add(crearLabel("Imagen"), gbcCampo(5));
        bloqueCrear.add(campoImagen, gbcCampo(6));
        bloqueCrear.add(crearLabel("Precio"), gbcCampo(7));
        bloqueCrear.add(campoPrecio, gbcCampo(8));
        bloqueCrear.add(crearLabel("Stock"), gbcCampo(9));
        bloqueCrear.add(campoStock, gbcCampo(10));

        JPanel helperLinea = new JPanel(new GridLayout(1, 3, 10, 0));
        helperLinea.setOpaque(false);
        helperLinea.add(crearCampoFormulario("ID producto", campoIdProductoLinea));
        helperLinea.add(crearCampoFormulario("Unidades", campoUnidadesLinea));
        helperLinea.add(crearCampoFormulario(" ", botonAgregarLinea));

        bloqueCrear.add(crearLabel("Añadir línea al pack"), gbcCampo(11));
        bloqueCrear.add(helperLinea, gbcCampo(12));
        bloqueCrear.add(crearLabel("Líneas del pack (una por línea: ID;UNIDADES)"), gbcCampo(13));
        bloqueCrear.add(estilizarScroll(areaLineas), gbcCampo(14));
        bloqueCrear.add(botonCrear, gbcBoton(15));

        botonAgregarLinea.addActionListener(e -> {
            String id = campoIdProductoLinea.getText().trim();
            Integer unidades = leerEnteroSeguro(campoUnidadesLinea.getText());

            if (id.isBlank() || unidades == null || unidades <= 0) {
                mostrarError("Selecciona un producto e introduce unidades válidas.");
                return;
            }

            ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(id);
            if (producto == null) {
                mostrarError("No existe un producto con ese ID.");
                return;
            }

            if (!areaLineas.getText().isBlank() && !areaLineas.getText().endsWith("\n")) {
                areaLineas.append("\n");
            }
            areaLineas.append(id + ";" + unidades);

            campoIdProductoLinea.setText("");
            campoUnidadesLinea.setText("");
        });

        botonCrear.addActionListener(e -> {
            try {
                ArrayList<LineaPack> lineas = construirLineasPack(areaLineas.getText());
                Double precio = leerDoubleSeguro(campoPrecio.getText());
                Integer stock = leerEnteroSeguro(campoStock.getText());

                if (campoNombre.getText().trim().isBlank() || precio == null || stock == null || stock < 0) {
                    mostrarError("Completa correctamente nombre, precio y stock.");
                    return;
                }

                boolean ok = empleado.crearPack(
                        campoNombre.getText().trim(),
                        areaDescripcionPack.getText().trim(),
                        campoImagen.getText().trim(),
                        precio,
                        stock,
                        lineas);

                if (ok) {
                    recargarTablaProductos(selector.tabla);
                    mostrarMensaje("Pack creado correctamente.");
                } else {
                    mostrarError("No se pudo crear el pack.");
                }
            } catch (Exception ex) {
                mostrarError("No se pudo crear el pack: " + ex.getMessage());
            }
        });

        JPanel bloqueGestion = crearBloque("Modificar pack existente");
        JTextField campoIdPack = crearCampo();
        JTextField campoUnidades = crearCampo();
        JTextField campoNuevoPrecio = crearCampo();

        JButton botonAnadir = crearBotonAccion("Añadir producto");
        JButton botonModificarUnidades = crearBotonAccion("Modificar unidades");
        JButton botonEliminarProducto = crearBotonAccion("Eliminar producto");
        JButton botonModificarPrecio = crearBotonAccion("Modificar precio");
        JButton botonEliminarPack = crearBotonAccion("Eliminar pack");

        bloqueGestion.add(crearLabel("ID pack"), gbcCampo(1));
        bloqueGestion.add(campoIdPack, gbcCampo(2));
        bloqueGestion.add(crearLabel("ID producto"), gbcCampo(3));
        bloqueGestion.add(campoIdProducto, gbcCampo(4));
        bloqueGestion.add(crearLabel("Unidades"), gbcCampo(5));
        bloqueGestion.add(campoUnidades, gbcCampo(6));
        bloqueGestion.add(crearLabel("Nuevo precio"), gbcCampo(7));
        bloqueGestion.add(campoNuevoPrecio, gbcCampo(8));

        JPanel botones = new JPanel(new GridLayout(0, 2, 10, 10));
        botones.setOpaque(false);
        botones.add(botonAnadir);
        botones.add(botonModificarUnidades);
        botones.add(botonEliminarProducto);
        botones.add(botonModificarPrecio);
        botones.add(botonEliminarPack);

        bloqueGestion.add(botones, gbcCampo(9));

        botonAnadir.addActionListener(e -> {
            try {
                Integer unidades = leerEnteroSeguro(campoUnidades.getText());
                if (campoIdProducto.getText().trim().isBlank() || campoIdPack.getText().trim().isBlank()
                        || unidades == null || unidades <= 0) {
                    mostrarError("Introduce un pack, un producto y unas unidades válidas.");
                    return;
                }

                boolean ok = empleado.añadirProductoaPack(
                        campoIdProducto.getText().trim(),
                        campoIdPack.getText().trim(),
                        unidades);

                if (ok) {
                    mostrarMensaje("Producto añadido al pack.");
                } else {
                    mostrarError("No se pudo añadir el producto al pack.");
                }
            } catch (Exception ex) {
                mostrarError("Datos inválidos.");
            }
        });

        botonModificarUnidades.addActionListener(e -> {
            try {
                Integer unidades = leerEnteroSeguro(campoUnidades.getText());
                if (campoIdProducto.getText().trim().isBlank() || campoIdPack.getText().trim().isBlank()
                        || unidades == null || unidades <= 0) {
                    mostrarError("Introduce un pack, un producto y unas unidades válidas.");
                    return;
                }

                boolean ok = empleado.modificarUnidadesProductoEnPack(
                        campoIdProducto.getText().trim(),
                        campoIdPack.getText().trim(),
                        unidades);

                if (ok) {
                    mostrarMensaje("Unidades modificadas correctamente.");
                } else {
                    mostrarError("No se pudieron modificar las unidades.");
                }
            } catch (Exception ex) {
                mostrarError("Datos inválidos.");
            }
        });

        botonEliminarProducto.addActionListener(e -> {
            String idPack = campoIdPack.getText().trim();
            String idProducto = campoIdProducto.getText().trim();

            if (idPack.isBlank() || idProducto.isBlank()) {
                mostrarError("Introduce el ID del pack y el del producto.");
                return;
            }

            boolean ok = empleado.eliminarProductoDePack(idPack, idProducto);
            if (ok) {
                mostrarMensaje("Producto eliminado del pack.");
            } else {
                mostrarError("No se pudo eliminar el producto del pack.");
            }
        });

        botonModificarPrecio.addActionListener(e -> {
            try {
                Double precio = leerDoubleSeguro(campoNuevoPrecio.getText());
                if (campoIdPack.getText().trim().isBlank() || precio == null || precio < 0) {
                    mostrarError("Introduce un pack y un precio válidos.");
                    return;
                }

                boolean ok = empleado.modificarPrecioPack(campoIdPack.getText().trim(), precio);
                if (ok) {
                    mostrarMensaje("Precio del pack modificado.");
                } else {
                    mostrarError("No se pudo modificar el precio.");
                }
            } catch (Exception ex) {
                mostrarError("Datos inválidos.");
            }
        });

        botonEliminarPack.addActionListener(e -> {
            String idPack = campoIdPack.getText().trim();
            if (idPack.isBlank()) {
                mostrarError("Introduce el ID del pack.");
                return;
            }

            boolean ok = empleado.eliminarPack(idPack);
            if (ok) {
                recargarTablaProductos(selector.tabla);
                mostrarMensaje("Pack eliminado correctamente.");
            } else {
                mostrarError("No se pudo eliminar el pack.");
            }
        });

        contenido.add(selector.bloque);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(bloqueCrear);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(bloqueGestion);

        add(base);
    }
}
