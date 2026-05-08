package Gui.Gestor;

import Gui.Controladores.ControladorEmpleadosGestor;
import Gui.VentanaPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;

/**
 * Subpanel de gestión de empleados para el gestor.
 * Extiende AbstractPanelGestor para reutilizar helpers visuales.
 * Sigue el patrón MVC de los apuntes.
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelEmpleadosGestor extends AbstractPanelGestor {

    private ControladorEmpleadosGestor controlador;
    private JPanel panelLista;

    // Campos del formulario — atributos para que el controlador pueda leerlos
    private JTextField campoNick;
    private JPasswordField campoPass;
    private List<JCheckBox> checks;

    // Combos de permisos por empleado — mapa id→combo para que el controlador los lea
    private Map<String, JComboBox<TipoPermisos>> combosPermisos = new HashMap<>();

    // Botón alta — atributo para registrar el controlador
    private JButton botonAlta;

    public SubpanelEmpleadosGestor(VentanaPrincipal ventana, Gestor gestor) {
        super(ventana, gestor);
        this.controlador = new ControladorEmpleadosGestor(this, gestor);
        inicializarUI();
    }

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

        setControlador(controlador);
        actualizarLista();
    }

    /**
     * Registra el controlador en los botones — patrón de los apuntes.
     */
    public void setControlador(ActionListener c) {
        if (botonAlta != null) {
            for (ActionListener al : botonAlta.getActionListeners())
                botonAlta.removeActionListener(al);
            botonAlta.addActionListener(c);
        }
    }

    private JPanel crearFormularioAlta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VentanaPrincipal.COLOR_PANEL);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(20),
                VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(20))));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(8),
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

        // Nickname — crearLabel() de AbstractPanelSection
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(crearLabel("Nickname:"), gbc);
        // crearCampoColumnas() de AbstractPanelGestor
        campoNick = crearCampoColumnas(15);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campoNick, gbc);

        // Contraseña — crearLabel() de AbstractPanelSection
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(crearLabel("Contraseña:"), gbc);
        // crearCampoPasswordGestor() de AbstractPanelGestor
        campoPass = crearCampoPasswordGestor();
        gbc.gridx = 3; gbc.weightx = 1;
        panel.add(campoPass, gbc);

        // Permisos
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.weightx = 1;
        panel.add(crearLabel("Permisos:"), gbc);

        gbc.gridy = 3;
        JPanel panelPermisos = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), 0));
        panelPermisos.setBackground(VentanaPrincipal.COLOR_PANEL);
        checks = new ArrayList<>();
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

        // Botón — crearBotonNaranja() de AbstractPanelSection
        botonAlta = crearBotonNaranja("Dar de alta");
        botonAlta.setActionCommand("darDeAlta");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.insets = new Insets(VentanaPrincipal.escalar(10),
            VentanaPrincipal.escalar(8), 0, VentanaPrincipal.escalar(8));
        panel.add(botonAlta, gbc);

        return panel;
    }

    private void actualizarLista() {
        panelLista.removeAll();
        combosPermisos.clear();

        JLabel labelTitulo = new JLabel("Empleados registrados:");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        labelTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(15), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(10), 0));
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLista.add(labelTitulo);

        List<Empleado> empleados = controlador.getEmpleados();
        if (empleados.isEmpty()) {
            // crearLabel() de AbstractPanelSection
            JLabel labelVacio = crearLabel("No hay empleados registrados.");
            labelVacio.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                0, VentanaPrincipal.escalar(15), 0, 0));
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

    private JPanel crearFilaEmpleado(Empleado empleado) {
        JPanel fila = new JPanel(new GridBagLayout());
        fila.setBackground(VentanaPrincipal.COLOR_TARJETA);
        fila.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE),
            javax.swing.BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
                VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15))));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,
            VentanaPrincipal.escalar(120)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(VentanaPrincipal.escalar(3),
            VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(3),
            VentanaPrincipal.escalar(8));
        gbc.anchor = GridBagConstraints.WEST;

        JLabel labelNick = new JLabel(empleado.getNickname());
        labelNick.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelNick.setForeground(empleado.isDespedido()
            ? new Color(150, 50, 50) : VentanaPrincipal.COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fila.add(labelNick, gbc);

        // crearLabel() de AbstractPanelSection
        JLabel labelId = crearLabel("ID: " + empleado.getId());
        gbc.gridx = 1;
        fila.add(labelId, gbc);

        JLabel labelEstado = new JLabel(
            empleado.isDespedido() ? "DESPEDIDO" : "Activo");
        labelEstado.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        labelEstado.setForeground(empleado.isDespedido()
            ? new Color(200, 50, 50) : new Color(50, 200, 50));
        gbc.gridx = 2;
        fila.add(labelEstado, gbc);

        StringBuilder sb = new StringBuilder("Permisos: ");
        if (empleado.getPermisos().isEmpty()) {
            sb.append("ninguno");
        } else {
            for (TipoPermisos p : empleado.getPermisos())
                sb.append(p.name()).append("  ");
        }
        JLabel labelPermisos = crearLabel(sb.toString());
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        fila.add(labelPermisos, gbc);
        gbc.gridwidth = 1;

        if (!empleado.isDespedido()) {
            JPanel panelBotones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, VentanaPrincipal.escalar(5), 0));
            panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

            // crearCombo() de AbstractPanelSection
            JComboBox<TipoPermisos> comboPermiso = crearCombo(TipoPermisos.values());
            combosPermisos.put(empleado.getId(), comboPermiso);
            panelBotones.add(comboPermiso);

            // crearBotonNaranja() de AbstractPanelSection
            JButton botonAsignar = crearBotonNaranja("+ Permiso");
            botonAsignar.setActionCommand("asignarPermiso:" + empleado.getId());
            botonAsignar.addActionListener(controlador);
            panelBotones.add(botonAsignar);

            JButton botonRetirar = crearBotonNaranja("- Permiso");
            botonRetirar.setActionCommand("retirarPermiso:" + empleado.getId());
            botonRetirar.addActionListener(controlador);
            panelBotones.add(botonRetirar);

            // crearBotonRojo() de AbstractPanelSection
            JButton botonBaja = crearBotonRojo("Dar de baja");
            botonBaja.setActionCommand("darDeBaja:" + empleado.getId());
            botonBaja.addActionListener(controlador);
            panelBotones.add(botonBaja);

            gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            fila.add(panelBotones, gbc);
        }

        return fila;
    }

    // ── Métodos que llama el controlador ──────────────────────────────────

    /**
     * Lee los campos del formulario y da de alta al empleado.
     * Lo llama el controlador.
     */
    public void procesarAlta() {
        String nick = campoNick.getText().trim();
        String pass = new String(campoPass.getPassword());
        List<TipoPermisos> permisos = new ArrayList<>();
        for (int i = 0; i < checks.size(); i++) {
            if (checks.get(i).isSelected())
                permisos.add(TipoPermisos.values()[i]);
        }
        if (nick.isEmpty() || pass.isEmpty()) {
            mostrarError("Rellena el nickname y la contraseña.");
            return;
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
    }

    /**
     * Muestra confirmación y da de baja al empleado.
     * Lo llama el controlador.
     */
    public void confirmarBaja(String id) {
        Empleado emp = controlador.getEmpleados().stream()
            .filter(e -> e.getId().equals(id))
            .findFirst().orElse(null);
        if (emp == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Seguro que quieres dar de baja a " + emp.getNickname() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controlador.darDeBaja(id)) {
                mostrarExito("Empleado dado de baja.");
                actualizarLista();
            } else {
                mostrarError("No se pudo dar de baja.");
            }
        }
    }

    /**
     * Asigna el permiso seleccionado en el combo al empleado.
     * Lo llama el controlador.
     */
    public void procesarAsignarPermiso(String id) {
        JComboBox<TipoPermisos> combo = combosPermisos.get(id);
        if (combo == null) return;
        TipoPermisos p = (TipoPermisos) combo.getSelectedItem();
        if (controlador.asignarPermiso(id, p)) {
            mostrarExito("Permiso asignado.");
            actualizarLista();
        } else {
            mostrarError("No se pudo asignar el permiso.");
        }
    }

    /**
     * Retira el permiso seleccionado en el combo al empleado.
     * Lo llama el controlador.
     */
    public void procesarRetirarPermiso(String id) {
        JComboBox<TipoPermisos> combo = combosPermisos.get(id);
        if (combo == null) return;
        TipoPermisos p = (TipoPermisos) combo.getSelectedItem();
        if (controlador.retirarPermiso(id, p)) {
            mostrarExito("Permiso retirado.");
            actualizarLista();
        } else {
            mostrarError("No se pudo retirar el permiso.");
        }
    }
}