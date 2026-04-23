package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;

import usuarios.Empleado;

public abstract class AbstractPanelEmpleadoSection extends JPanel {
    private static final long serialVersionUID = 1L;

    protected final VentanaPrincipal ventana;
    protected final Empleado empleado;

    protected AbstractPanelEmpleadoSection(VentanaPrincipal ventana, Empleado empleado) {
        this.ventana = ventana;
        this.empleado = empleado;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
    }

    protected JPanel crearPanelBase(String titulo) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(VentanaPrincipal.COLOR_FONDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(
                VentanaPrincipal.escalar(22),
                VentanaPrincipal.escalar(22),
                VentanaPrincipal.escalar(22),
                VentanaPrincipal.escalar(22)));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(VentanaPrincipal.FUENTE_TITULO);
        labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        labelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(labelTitulo);
        contenido.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
        scroll.setBackground(VentanaPrincipal.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.putClientProperty("contenido", contenido);
        return wrapper;
    }

    protected JPanel getContenido(JPanel base) {
        return (JPanel) base.getClientProperty("contenido");
    }

    protected JPanel crearBloque(String titulo) {
        JPanel bloque = new JPanel(new GridBagLayout());
        bloque.setAlignmentX(Component.CENTER_ALIGNMENT);
        bloque.setMaximumSize(new Dimension(1180, Integer.MAX_VALUE));
        bloque.setBackground(new Color(34, 34, 34));
        bloque.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, VentanaPrincipal.COLOR_ACENTO),
                        BorderFactory.createLineBorder(new Color(68, 68, 68), 1)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 14, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel label = new JLabel(titulo);
        label.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        label.setForeground(VentanaPrincipal.COLOR_ACENTO);
        bloque.add(label, gbc);
        return bloque;
    }

    protected GridBagConstraints gbcCampo(int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);
        return gbc;
    }

    protected GridBagConstraints gbcBoton(int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(12, 0, 6, 0);
        return gbc;
    }

    protected GridBagConstraints gbcFiltro(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        return gbc;
    }

    protected JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(VentanaPrincipal.FUENTE_NORMAL);
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        return label;
    }

    protected JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(0, 40));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campo.setBackground(new Color(24, 24, 24));
        campo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        campo.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(85, 85, 85), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return campo;
    }

    protected JTextField crearCampoCompacto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(0, 34));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        campo.setBackground(new Color(24, 24, 24));
        campo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        campo.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(85, 85, 85), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return campo;
    }

    protected JTextArea crearArea() {
        JTextArea area = new JTextArea(5, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(24, 24, 24));
        area.setForeground(VentanaPrincipal.COLOR_TEXTO);
        area.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        return area;
    }

    protected <T> JComboBox<T> crearCombo(T[] valores) {
        JComboBox<T> combo = new JComboBox<>(valores);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(new Color(24, 24, 24));
        combo.setForeground(VentanaPrincipal.COLOR_TEXTO);
        combo.setPreferredSize(new Dimension(0, 34));
        combo.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1));
        return combo;
    }

    protected JScrollPane estilizarScroll(Component comp) {
        JScrollPane scroll = new JScrollPane(comp);
        scroll.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1));

        Color fondo = Color.WHITE;
        if (comp instanceof JComponent) {
            fondo = ((JComponent) comp).getBackground();
        }

        scroll.getViewport().setBackground(fondo);
        scroll.setBackground(fondo);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    protected JButton crearBotonAccion(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
        boton.setForeground(new Color(18, 18, 18));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(210, 40));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(VentanaPrincipal.COLOR_ACENTO2);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
            }
        });

        return boton;
    }

    protected JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(new Color(70, 70, 70));
        boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 34));
        boton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(new Color(90, 90, 90));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(new Color(70, 70, 70));
            }
        });

        return boton;
    }

    protected JPanel crearCampoFormulario(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(campo);
        return panel;
    }

    protected void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    protected void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void escucharCambios(JTextField campo, Runnable accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { accion.run(); }
            @Override public void removeUpdate(DocumentEvent e) { accion.run(); }
            @Override public void changedUpdate(DocumentEvent e) { accion.run(); }
        });
    }

    protected String normalizarTexto(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
    }

    protected Double leerDoubleSeguro(String texto) {
        if (texto == null || texto.trim().isBlank()) return null;
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }

    protected Integer leerEnteroSeguro(String texto) {
        if (texto == null || texto.trim().isBlank()) return null;
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            return null;
        }
    }

    protected boolean contieneTexto(String base, String buscado) {
        if (buscado == null || buscado.isBlank()) return true;
        if (base == null) return false;
        return base.toLowerCase(Locale.ROOT).contains(buscado);
    }

    protected String valorTexto(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    protected Double valorDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            String txt = String.valueOf(value).replace("€", "").replace(",", ".").trim();
            if (txt.isBlank()) return null;
            return Double.parseDouble(txt);
        } catch (Exception e) {
            return null;
        }
    }

    protected Integer valorInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            String txt = String.valueOf(value).trim();
            if (txt.isBlank()) return null;
            return Integer.parseInt(txt);
        } catch (Exception e) {
            return null;
        }
    }
}
