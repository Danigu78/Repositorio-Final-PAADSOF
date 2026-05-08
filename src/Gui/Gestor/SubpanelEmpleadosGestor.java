package Gui.Gestor;

import Gui.Controladores.Gestor.ControladorEmpleadosGestor;
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
 *
 * @author Antonino
 * @version 1.0
 */
public class SubpanelEmpleadosGestor extends AbstractPanelGestor {

    private ControladorEmpleadosGestor controlador;
    private JPanel panelLista;

    private JTextField campoNick;
    private JPasswordField campoPass;
    private List<JCheckBox> checks;
    private JTextField campoBusquedaEmpleados;
    private Map<String, JPanel> combosPermisosPanel = new HashMap<>();
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

        // Wrapper para anclar contenido arriba
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(VentanaPrincipal.COLOR_FONDO);
        wrapper.add(panelLista, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
        add(scroll, BorderLayout.CENTER);

        setControlador(controlador);
        actualizarLista();
    }

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

        JLabel titulo = new JLabel("Dar de alta nuevo empleado");
        titulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        titulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(titulo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(crearLabel("Nickname:"), gbc);
        campoNick = crearCampoColumnas(15);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campoNick, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(crearLabel("Contraseña:"), gbc);
        campoPass = crearCampoPasswordGestor();
        gbc.gridx = 3; gbc.weightx = 1;
        panel.add(campoPass, gbc);

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
        combosPermisosPanel.clear();

        JLabel labelTitulo = new JLabel("Empleados registrados:");
        labelTitulo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        labelTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(
            VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(15),
            VentanaPrincipal.escalar(5), 0));
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLista.add(labelTitulo);

        // Barra de búsqueda
        JPanel barraBusqueda = new JPanel(new FlowLayout(
            FlowLayout.LEFT, VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(8)));
        barraBusqueda.setBackground(VentanaPrincipal.COLOR_FONDO);
        barraBusqueda.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraBusqueda.setMaximumSize(new Dimension(
            VentanaPrincipal.escalar(450), VentanaPrincipal.escalar(50)));
        barraBusqueda.add(crearLabel("Buscar:"));
        campoBusquedaEmpleados = crearCampoCompacto();
        campoBusquedaEmpleados.setPreferredSize(new Dimension(
            VentanaPrincipal.escalar(200), VentanaPrincipal.escalar(28)));
        // escucharCambios() de AbstractPanelSection
        escucharCambios(campoBusquedaEmpleados, this::filtrarEmpleados);
        barraBusqueda.add(campoBusquedaEmpleados);
        panelLista.add(barraBusqueda);

        for (Empleado e : controlador.getEmpleados()) {
            JPanel fila = crearFilaEmpleado(e);
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelLista.add(fila);
        }

        panelLista.revalidate();
        panelLista.repaint();
    }

    /**
     * Filtra empleados usando gestor.buscarEmpleadoPorNombre() a través del controlador.
     */
    private void filtrarEmpleados() {
        String texto = campoBusquedaEmpleados.getText().trim();
        // Mantenemos título y barra (2 primeros componentes)
        while (panelLista.getComponentCount() > 2)
            panelLista.remove(panelLista.getComponentCount() - 1);

        List<Empleado> empleados = controlador.buscarEmpleados(texto);
        if (empleados.isEmpty()) {
            JLabel labelVacio = crearLabel("No se encontraron empleados.");
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
            VentanaPrincipal.escalar(150)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(VentanaPrincipal.escalar(3), VentanaPrincipal.escalar(8),
            VentanaPrincipal.escalar(3), VentanaPrincipal.escalar(8));
        gbc.anchor = GridBagConstraints.WEST;

        JLabel labelNick = new JLabel(empleado.getNickname());
        labelNick.setFont(VentanaPrincipal.FUENTE_BOTON);
        labelNick.setForeground(empleado.isDespedido()
            ? new Color(150, 50, 50) : VentanaPrincipal.COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fila.add(labelNick, gbc);

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

        // Permisos actuales visibles
        StringBuilder sb = new StringBuilder("Permisos: ");
        if (empleado.getPermisos().isEmpty()) sb.append("ninguno");
        else for (TipoPermisos p : empleado.getPermisos())
            sb.append(p.name()).append("  ");
        JLabel labelPermisos = crearLabel(sb.toString());
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        fila.add(labelPermisos, gbc);
        gbc.gridwidth = 1;

        if (!empleado.isDespedido()) {
            JPanel panelBotones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, VentanaPrincipal.escalar(5), 0));
            panelBotones.setBackground(VentanaPrincipal.COLOR_TARJETA);

            String[] nombresPermisos = new String[TipoPermisos.values().length];
            for (int i = 0; i < TipoPermisos.values().length; i++)
                nombresPermisos[i] = TipoPermisos.values()[i].name();
            JPanel panelComboPermiso = crearComboConBuscador(
                nombresPermisos, VentanaPrincipal.escalar(160));
            combosPermisosPanel.put(empleado.getId(), panelComboPermiso);
            panelBotones.add(panelComboPermiso);

            JButton botonAsignar = crearBotonNaranja("+ Permiso");
            botonAsignar.setActionCommand("asignarPermiso:" + empleado.getId());
            botonAsignar.addActionListener(controlador);
            panelBotones.add(botonAsignar);

            JButton botonRetirar = crearBotonNaranja("- Permiso");
            botonRetirar.setActionCommand("retirarPermiso:" + empleado.getId());
            botonRetirar.addActionListener(controlador);
            panelBotones.add(botonRetirar);

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

    public void procesarAlta() {
        String nick = campoNick.getText().trim();
        String pass = new String(campoPass.getPassword());
        List<TipoPermisos> permisos = new ArrayList<>();
        for (int i = 0; i < checks.size(); i++)
            if (checks.get(i).isSelected())
                permisos.add(TipoPermisos.values()[i]);
        if (nick.isEmpty() || pass.isEmpty()) {
            mostrarError("Rellena el nickname y la contraseña.");
            return;
        }
        if (controlador.darDeAlta(nick, pass, permisos)) {
            mostrarMensaje("Empleado '" + nick + "' dado de alta correctamente.");
            campoNick.setText("");
            campoPass.setText("");
            for (JCheckBox cb : checks) cb.setSelected(false);
            actualizarLista();
        } else {
            mostrarError("No se pudo dar de alta. Comprueba los datos.");
        }
    }

    public void confirmarBaja(String id) {
        String nick = controlador.getNicknameEmpleado(id);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Seguro que quieres dar de baja a " + nick + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controlador.darDeBaja(id)) {
                mostrarMensaje("Empleado dado de baja.");
                actualizarLista();
            } else {
                mostrarError("No se pudo dar de baja.");
            }
        }
    }

    public void procesarAsignarPermiso(String id) {
        JPanel panelCombo = combosPermisosPanel.get(id);
        if (panelCombo == null) return;
        JComboBox<String> combo = getComboDePanel(panelCombo);
        if (combo == null || combo.getSelectedItem() == null) return;
        TipoPermisos p = TipoPermisos.valueOf((String) combo.getSelectedItem());
        if (controlador.asignarPermiso(id, p)) {
            mostrarMensaje("Permiso asignado.");
            actualizarLista();
        } else {
            mostrarError("No se pudo asignar el permiso.");
        }
    }

    public void procesarRetirarPermiso(String id) {
        JPanel panelCombo = combosPermisosPanel.get(id);
        if (panelCombo == null) return;
        JComboBox<String> combo = getComboDePanel(panelCombo);
        if (combo == null || combo.getSelectedItem() == null) return;
        TipoPermisos p = TipoPermisos.valueOf((String) combo.getSelectedItem());
        if (controlador.retirarPermiso(id, p)) {
            mostrarMensaje("Permiso retirado.");
            actualizarLista();
        } else {
            mostrarError("No se pudo retirar el permiso.");
        }
    }
}