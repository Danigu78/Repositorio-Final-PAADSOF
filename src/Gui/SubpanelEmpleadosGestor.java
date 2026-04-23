package Gui;

import Gui.Controladores.ControladorEmpleadosGestor;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;

/**
 * Subpanel de gestión de empleados para el gestor.
 * Permite dar de alta/baja empleados y gestionar sus permisos.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelEmpleadosGestor extends JPanel {

    private VentanaPrincipal ventana;
    private Gestor gestor;
    private ControladorEmpleadosGestor controlador;
    private JPanel panelLista;

    /**
     * Constructor del subpanel de empleados.
     *
     * @param ventana La ventana principal
     * @param gestor  El gestor logueado
     */
    public SubpanelEmpleadosGestor(VentanaPrincipal ventana, Gestor gestor) {
        this.ventana = ventana;
        this.gestor = gestor;
        this.controlador = new ControladorEmpleadosGestor(gestor);
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    /**
     * Construye la interfaz con formulario de alta arriba y lista de empleados abajo.
     */
    private void inicializarUI() {
        add(crearFormularioAlta(), BorderLayout.NORTH);

        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setBackground(VentanaPrincipal.COLOR_FONDO);

        JScrollPane scroll = new JScrollPane(panelLista);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        add(scroll, BorderLayout.CENTER);

        actualizarLista();
    }

    /**
     * Crea el formulario para dar de alta un nuevo empleado.
     * Usa GridBagLayout para que todo se adapte bien a la pantalla.
     *
     * @return Panel con el formulario de alta
     */
    private JPanel crearFormularioAlta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(20),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(20))
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(
            VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(8),
            VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(8));
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("Dar de alta nuevo empleado");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(titulo, gbc);
        gbc.gridwidth = 1;

        // Nickname
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(crearEtiqueta("Nickname:"), gbc);
        JTextField campoNick = crearCampo(15);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campoNick, gbc);

        // Contraseña
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(crearEtiqueta("Contraseña:"), gbc);
        JPasswordField campoPass = new JPasswordField(15);
        campoPass.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campoPass.setForeground(Color.BLACK);
        campoPass.setBackground(Color.WHITE);
        campoPass.setCaretColor(Color.BLACK);
        campoPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))
        ));
        gbc.gridx = 3; gbc.weightx = 1;
        panel.add(campoPass, gbc);

        // Permisos — en una fila entera con checkboxes que se adaptan
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.weightx = 1;
        panel.add(crearEtiqueta("Permisos:"), gbc);

        gbc.gridy = 3;
        JPanel panelPermisos = new JPanel(new FlowLayout(FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
        panelPermisos.setBackground(VentanaPrincipal.COLOR_PANEL);
        List<JCheckBox> checks = new ArrayList<>();
        for (TipoPermisos p : TipoPermisos.values()) {
            JCheckBox cb = new JCheckBox(p.name());
            cb.setFont(VentanaPrincipal.FUENTE_PEQUENA);
            cb.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            cb.setBackground(VentanaPrincipal.COLOR_PANEL);
            panelPermisos.add(cb);
            checks.add(cb);
        }
        panel.add(panelPermisos, gbc);
        gbc.gridwidth = 1;

        // Botón dar de alta
        JButton botonAlta = crearBoton("Dar de alta");
        botonAlta.addActionListener(e -> {
            String nick = campoNick.getText().trim();
            String pass = new String(campoPass.getPassword());
            List<TipoPermisos> permisos = new ArrayList<>();
            for (int i = 0; i < checks.size(); i++) {
                if (checks.get(i).isSelected()) {
                    permisos.add(TipoPermisos.values()[i]);
                }
            }
            if (controlador.darDeAlta(nick, pass, permisos)) {
                mostrarExito("Empleado '" + nick + "' dado de alta correctamente.");
                campoNick.setText("");
                campoPass.setText("");
                checks.forEach(cb -> cb.setSelected(false));
                actualizarLista();
            } else {
                mostrarError("No se pudo dar de alta. Comprueba los datos.");
            }
        });
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.insets = new Insets(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8));
        panel.add(botonAlta, gbc);

        return panel;
    }

    /**
     * Actualiza la lista de empleados mostrada en el panel central.
     */
    private void actualizarLista() {
        panelLista.removeAll();

        JLabel labelTitulo = new JLabel("Empleados registrados:");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(10), 0));
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLista.add(labelTitulo);

        List<Empleado> empleados = controlador.getEmpleados();
        if (empleados.isEmpty()) {
            JLabel labelVacio = new JLabel("No hay empleados registrados.");
            labelVacio.setFont(VentanaPrincipal.FUENTE_NORMAL);
            labelVacio.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            labelVacio.setBorder(BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, 0));
            labelVacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelLista.add(labelVacio);
        } else {
            for (Empleado e : empleados) {
                JPanel fila = crearFilaEmpleado(e);
                fila.setAlignmentX(Component.LEFT_ALIGNMENT);
                panelLista.add(fila);
            }
        }

        panelLista.revalidate();
        panelLista.repaint();
    }

    /**
     * Crea una fila visual para un empleado con sus datos y botones de acción.
     * Usa GridBagLayout para que no se corten los elementos.
     *
     * @param empleado El empleado a mostrar
     * @return Panel con la fila del empleado
     */
    private JPanel crearFilaEmpleado(Empleado empleado) {
        JPanel fila = new JPanel(new GridBagLayout());
        fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
        fila.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))
        ));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(120)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(VentanaPrincipal.escalar(3), VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(3), VentanaPrincipal.escalar(8));
        gbc.anchor = GridBagConstraints.WEST;

        // Nombre e id
        JLabel labelNick = new JLabel(empleado.getNickname());
        labelNick.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelNick.setForeground(empleado.isDespedido() ? new Color(150, 50, 50) : VentanaPrincipal.COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fila.add(labelNick, gbc);

        JLabel labelId = new JLabel("ID: " + empleado.getId());
        labelId.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelId.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridx = 1;
        fila.add(labelId, gbc);

        JLabel labelEstado = new JLabel(empleado.isDespedido() ? "DESPEDIDO" : "Activo");
        labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelEstado.setForeground(empleado.isDespedido() ? new Color(200, 50, 50) : new Color(50, 200, 50));
        gbc.gridx = 2;
        fila.add(labelEstado, gbc);

        // Permisos en segunda fila
        StringBuilder sb = new StringBuilder("Permisos: ");
        if (empleado.getPermisos().isEmpty()) {
            sb.append("ninguno");
        } else {
            for (TipoPermisos p : empleado.getPermisos()) {
                sb.append(p.name()).append("  ");
            }
        }
        JLabel labelPermisos = new JLabel(sb.toString());
        labelPermisos.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelPermisos.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        fila.add(labelPermisos, gbc);
        gbc.gridwidth = 1;

        // Botones solo si no está despedido
        if (!empleado.isDespedido()) {
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(5), 0));
            panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

            JComboBox<TipoPermisos> comboPermiso = new JComboBox<>(TipoPermisos.values());
            comboPermiso.setFont(VentanaPrincipal.FUENTE_PEQUENA);
            panelBotones.add(comboPermiso);

            JButton botonAsignar = crearBoton("+ Permiso");
            botonAsignar.addActionListener(e -> {
                TipoPermisos p = (TipoPermisos) comboPermiso.getSelectedItem();
                if (controlador.asignarPermiso(empleado.getId(), p)) {
                    mostrarExito("Permiso asignado.");
                    actualizarLista();
                } else {
                    mostrarError("No se pudo asignar el permiso.");
                }
            });
            panelBotones.add(botonAsignar);

            JButton botonRetirar = crearBoton("- Permiso");
            botonRetirar.addActionListener(e -> {
                TipoPermisos p = (TipoPermisos) comboPermiso.getSelectedItem();
                if (controlador.retirarPermiso(empleado.getId(), p)) {
                    mostrarExito("Permiso retirado.");
                    actualizarLista();
                } else {
                    mostrarError("No se pudo retirar el permiso.");
                }
            });
            panelBotones.add(botonRetirar);

            JButton botonBaja = crearBotonRojo("Dar de baja");
            botonBaja.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que quieres dar de baja a " + empleado.getNickname() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (controlador.darDeBaja(empleado.getId())) {
                        mostrarExito("Empleado dado de baja.");
                        actualizarLista();
                    } else {
                        mostrarError("No se pudo dar de baja.");
                    }
                }
            });
            panelBotones.add(botonBaja);

            gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            fila.add(panelBotones, gbc);
        }

        return fila;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    private JTextField crearCampo(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE),
            BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8),
                VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(8))
        ));
        return campo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(VentanaPrincipal.FUENTE_BOTON);
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12),
            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12)));
        return boton;
    }

    private JButton crearBotonRojo(String texto) {
        JButton boton = crearBoton(texto);
        boton.setBackground(new Color(180, 50, 50));
        return boton;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}