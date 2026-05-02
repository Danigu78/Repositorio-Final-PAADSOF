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

import usuarios.Empleado;

/**
 * Clase base para las secciones del panel de empleado.
 * 
 * Aquí se guardan métodos comunes para crear campos, botones, bloques y leer
 * datos de forma segura. Así las secciones concretas quedan más limpias.
 */
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
		wrapper.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(22), VentanaPrincipal.escalar(22),
				VentanaPrincipal.escalar(22), VentanaPrincipal.escalar(22)));

		JPanel contenido = new JPanel();
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		contenido.setBackground(VentanaPrincipal.COLOR_FONDO);

		JLabel labelTitulo = new JLabel(titulo);
		labelTitulo.setFont(VentanaPrincipal.FUENTE_TITULO);
		labelTitulo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		labelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

		contenido.add(labelTitulo);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(20)));

		JScrollPane scroll = new JScrollPane(contenido);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(VentanaPrincipal.COLOR_FONDO);
		scroll.setBackground(VentanaPrincipal.COLOR_FONDO);
		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));

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
		bloque.setBackground(VentanaPrincipal.COLOR_TARJETA);

		bloque.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(18), VentanaPrincipal.escalar(18),
								VentanaPrincipal.escalar(18), VentanaPrincipal.escalar(18))));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(14), 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		JLabel label = new JLabel(titulo);
		label.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO);

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
		gbc.insets = new Insets(VentanaPrincipal.escalar(6), 0, VentanaPrincipal.escalar(6), 0);
		return gbc;
	}

	protected GridBagConstraints gbcBoton(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets = new Insets(VentanaPrincipal.escalar(12), 0, VentanaPrincipal.escalar(6), 0);
		return gbc;
	}

	protected GridBagConstraints gbcFiltro(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.insets = new Insets(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(6),
				VentanaPrincipal.escalar(6));
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
		campo.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(40)));
		campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(40)));
		campo.setBackground(Color.WHITE);
		campo.setForeground(Color.BLACK);
		campo.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
		campo.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12))));
		return campo;
	}

	protected JTextField crearCampoCompacto() {
		JTextField campo = new JTextField();
		campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		campo.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(34)));
		campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, VentanaPrincipal.escalar(34)));
		campo.setBackground(Color.WHITE);
		campo.setForeground(Color.BLACK);
		campo.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
		campo.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(10))));
		return campo;
	}

	protected JTextArea crearArea() {
		JTextArea area = new JTextArea(5, 20);
		area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setBackground(Color.WHITE);
		area.setForeground(Color.BLACK);
		area.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
		area.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(10), VentanaPrincipal.escalar(12)));
		return area;
	}

	protected <T> JComboBox<T> crearCombo(T[] valores) {
		JComboBox<T> combo = new JComboBox<>(valores);
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		combo.setBackground(Color.WHITE);
		combo.setForeground(Color.BLACK);
		combo.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(34)));
		combo.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1));
		return combo;
	}

	protected JScrollPane estilizarScroll(Component comp) {
		JScrollPane scroll = new JScrollPane(comp);
		scroll.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1));

		if (comp instanceof JComponent) {
			Color fondo = ((JComponent) comp).getBackground();
			scroll.getViewport().setBackground(fondo);
			scroll.setBackground(fondo);
		} else {
			scroll.getViewport().setBackground(Color.WHITE);
			scroll.setBackground(Color.WHITE);
		}

		scroll.getVerticalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));
		scroll.getHorizontalScrollBar().setUnitIncrement(VentanaPrincipal.escalar(16));

		return scroll;
	}

	protected JButton crearBotonAccion(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
		boton.setForeground(Color.BLACK);
		boton.setFocusPainted(false);
		boton.setBorderPainted(false);
		boton.setOpaque(true);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setPreferredSize(new Dimension(VentanaPrincipal.escalar(210), VentanaPrincipal.escalar(40)));
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(16),
				VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(16)));
		return boton;
	}

	protected JButton crearBotonSecundario(String texto) {
		JButton boton = new JButton(texto);

		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
		boton.setForeground(Color.BLACK);

		boton.setFocusPainted(false);
		boton.setBorderPainted(false);
		boton.setContentAreaFilled(true);
		boton.setOpaque(true);

		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		boton.setPreferredSize(new Dimension(VentanaPrincipal.escalar(160), VentanaPrincipal.escalar(36)));

		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(7), VentanaPrincipal.escalar(14),
				VentanaPrincipal.escalar(7), VentanaPrincipal.escalar(14)));

		return boton;
	}

	protected JButton crearBotonPeligro(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(new Color(180, 60, 60));
		boton.setForeground(Color.WHITE);
		boton.setFocusPainted(false);
		boton.setBorderPainted(false);
		boton.setOpaque(true);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setPreferredSize(new Dimension(VentanaPrincipal.escalar(170), VentanaPrincipal.escalar(36)));
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(7), VentanaPrincipal.escalar(14),
				VentanaPrincipal.escalar(7), VentanaPrincipal.escalar(14)));
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
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(4)));
		panel.add(campo);

		return panel;
	}

	protected void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	protected void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	protected void escucharCambios(JTextField campo, Runnable accion) {
		campo.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				accion.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				accion.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				accion.run();
			}
		});
	}

	protected String normalizarTexto(String texto) {
		if (texto == null) {
			return "";
		}
		return texto.trim().toLowerCase(Locale.ROOT);
	}

	protected Double leerDoubleSeguro(String texto) {
		if (texto == null || texto.trim().isBlank()) {
			return null;
		}

		try {
			return Double.parseDouble(texto.trim().replace(",", "."));
		} catch (Exception e) {
			return null;
		}
	}

	protected Integer leerEnteroSeguro(String texto) {
		if (texto == null || texto.trim().isBlank()) {
			return null;
		}

		try {
			return Integer.parseInt(texto.trim());
		} catch (Exception e) {
			return null;
		}
	}

	protected boolean contieneTexto(String base, String buscado) {
		if (buscado == null || buscado.isBlank()) {
			return true;
		}

		if (base == null) {
			return false;
		}

		return base.toLowerCase(Locale.ROOT).contains(buscado.toLowerCase(Locale.ROOT));
	}

	protected String valorTexto(Object value) {
		if (value == null) {
			return "";
		}
		return String.valueOf(value).trim();
	}

	protected Double valorDouble(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		try {
			String texto = String.valueOf(value).replace("€", "").replace(",", ".").trim();

			if (texto.isBlank()) {
				return null;
			}

			return Double.parseDouble(texto);
		} catch (Exception e) {
			return null;
		}
	}

	protected Integer valorInteger(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		try {
			String texto = String.valueOf(value).trim();

			if (texto.isBlank()) {
				return null;
			}

			return Integer.parseInt(texto);
		} catch (Exception e) {
			return null;
		}
	}
}