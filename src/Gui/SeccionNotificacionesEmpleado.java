package Gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import usuarios.Empleado;

public class SeccionNotificacionesEmpleado extends AbstractPanelEmpleadoSection {
    private static final long serialVersionUID = 1L;

    public SeccionNotificacionesEmpleado(VentanaPrincipal ventana, Empleado empleado) {
        super(ventana, empleado);
        construirUI();
    }

    private void construirUI() {
        JPanel base = crearPanelBase("Notificaciones");
        JPanel contenido = getContenido(base);

        JPanel bloque = crearBloque("Mis notificaciones");
        JTextArea area = crearArea();
        area.setEditable(false);
        area.setRows(18);

        JButton botonRefrescar = crearBotonAccion("Refrescar");

        bloque.add(estilizarScroll(area), gbcCampo(1));
        bloque.add(botonRefrescar, gbcBoton(2));

        Runnable cargar = () -> {
            StringBuilder sb = new StringBuilder();
            if (empleado.getNotificaciones() == null || empleado.getNotificaciones().isEmpty()) {
                sb.append("No hay notificaciones.");
            } else {
                for (Object n : empleado.getNotificaciones()) {
                    sb.append("• ").append(String.valueOf(n)).append("\n\n");
                }
            }
            area.setText(sb.toString());
            area.setCaretPosition(0);
        };

        botonRefrescar.addActionListener(e -> cargar.run());
        cargar.run();

        contenido.add(bloque);
        add(base);
    }
}
