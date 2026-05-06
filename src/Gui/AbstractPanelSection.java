package Gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Clase base con helpers visuales compartidos entre empleado y cliente.
 * AbstractPanelEmpleadoSection y SubpanelNotificaciones la extienden para
 * reutilizar el mismo estilo visual.
 *
 * @author Daniel y Lucas
 * @version 1.0
 */
public abstract class AbstractPanelSection extends JPanel {

	private static final long serialVersionUID = 1L;
	protected final VentanaPrincipal ventana;

	protected AbstractPanelSection(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);
	}

	/**
	 * Crea el panel base con scroll y título. Guarda el panel de contenido en
	 * clientProperty "contenido".
	 */
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

	/**
	 * Devuelve el panel de contenido guardado en el wrapper.
	 */
	protected JPanel getContenido(JPanel base) {
		return (JPanel) base.getClientProperty("contenido");
	}

	/**
	 * Crea un bloque con título y GridBagLayout — mismo estilo empleado.
	 */
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

	/**
	 * GridBagConstraints para campos mismo estilo empleado.
	 */
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

	/**
	 * GridBagConstraints para botones — mismo estilo empleado.
	 */
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

	/**
	 * GridBagConstraints para filtros — mismo estilo empleado.
	 */
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
		boton.setPreferredSize(new Dimension(VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(36)));
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
		boton.setPreferredSize(new Dimension(VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(36)));
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
		boton.setPreferredSize(new Dimension(VentanaPrincipal.escalar(180), VentanaPrincipal.escalar(36)));
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
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		campo.setAlignmentX(Component.LEFT_ALIGNMENT);

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
		if (texto == null)
			return "";
		return texto.trim().toLowerCase(Locale.ROOT);
	}

	protected Double leerDoubleSeguro(String texto) {
		if (texto == null || texto.trim().isBlank())
			return null;
		try {
			return Double.parseDouble(texto.trim().replace(",", "."));
		} catch (Exception e) {
			return null;
		}
	}

	protected Integer leerEnteroSeguro(String texto) {
		if (texto == null || texto.trim().isBlank())
			return null;
		try {
			return Integer.parseInt(texto.trim());
		} catch (Exception e) {
			return null;
		}
	}

	protected boolean contieneTexto(String base, String buscado) {
		if (buscado == null || buscado.isBlank())
			return true;
		if (base == null)
			return false;
		return base.toLowerCase(Locale.ROOT).contains(buscado.toLowerCase(Locale.ROOT));
	}

	protected String valorTexto(Object value) {
		if (value == null)
			return "";
		return String.valueOf(value).trim();
	}

	protected Double valorDouble(Object value) {
		if (value == null)
			return null;
		if (value instanceof Number)
			return ((Number) value).doubleValue();
		try {
			String texto = String.valueOf(value).replace("€", "").replace(",", ".").trim();
			if (texto.isBlank())
				return null;
			return Double.parseDouble(texto);
		} catch (Exception e) {
			return null;
		}
	}

	protected Integer valorInteger(Object value) {
		if (value == null)
			return null;
		if (value instanceof Number)
			return ((Number) value).intValue();
		try {
			String texto = String.valueOf(value).trim();
			if (texto.isBlank())
				return null;
			return Integer.parseInt(texto);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * Crea un botón de acción naranja con hover — estilo principal.
	 */
	protected JButton crearBotonNaranja(String texto) {
	    JButton boton = new JButton(texto);
	    boton.setFont(VentanaPrincipal.FUENTE_BOTON);
	    boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
	    boton.setForeground(Color.WHITE);
	    boton.setOpaque(true);
	    boton.setBorderPainted(false);
	    boton.setFocusPainted(false);
	    boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    boton.setBorder(BorderFactory.createEmptyBorder(
	        VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
	        VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15)));
	    boton.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            boton.setBackground(VentanaPrincipal.COLOR_ACENTO.darker());
	        }
	        @Override
	        public void mouseExited(MouseEvent e) {
	            boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
	        }
	    });
	    return boton;
	}

	/**
	 * Crea un botón secundario con borde naranja — estilo outline.
	 */
	protected JButton crearBotonOutline(String texto) {
	    JButton boton = new JButton(texto);
	    boton.setFont(VentanaPrincipal.FUENTE_BOTON);
	    boton.setBackground(VentanaPrincipal.COLOR_PANEL);
	    boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
	    boton.setBorderPainted(true);
	    boton.setFocusPainted(false);
	    boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    boton.setBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO),
	        BorderFactory.createEmptyBorder(
	            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12),
	            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12))));
	    return boton;
	}

	/**
	 * Crea un botón de volver con borde naranja y hover.
	 */
	protected JButton crearBotonVolver(String texto) {
	    JButton boton = new JButton(texto);
	    boton.setFont(VentanaPrincipal.FUENTE_NORMAL);
	    boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
	    boton.setBackground(VentanaPrincipal.COLOR_PANEL);
	    boton.setOpaque(true);
	    boton.setContentAreaFilled(false);
	    boton.setBorderPainted(true);
	    boton.setFocusPainted(false);
	    boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    boton.setBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO,
	            VentanaPrincipal.escalar(2)),
	        BorderFactory.createEmptyBorder(
	            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(15),
	            VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(15))));
	    boton.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
	        }
	        @Override
	        public void mouseExited(MouseEvent e) {
	            boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
	        }
	    });
	    return boton;
	}

	/**
	 * Crea una barra superior con botón volver — estilo común.
	 */
	protected JPanel crearBarraVolver(String texto) {
	    JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    barra.setBackground(VentanaPrincipal.COLOR_PANEL);
	    barra.setBorder(BorderFactory.createMatteBorder(
	        0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));
	    JButton boton = crearBotonVolver(texto);
	    barra.add(boton);
	    // Devolvemos la barra — el botón se recupera con getBotonVolver()
	    barra.putClientProperty("botonVolver", boton);
	    return barra;
	}

	/**
	 * Devuelve el botón volver de una barra creada con crearBarraVolver().
	 */
	protected JButton getBotonVolver(JPanel barra) {
	    return (JButton) barra.getClientProperty("botonVolver");
	}

	/**
	 * Carga una imagen desde src/fotos/ en un JLabel con tamaño escalado.
	 */
	protected void cargarImagen(JLabel label, String nombre, int ancho, int alto) {
	    try {
	        java.net.URL url = getClass().getResource("/fotos/" + nombre);
	        if (url != null) {
	            java.awt.image.BufferedImage img =
	                javax.imageio.ImageIO.read(url);
	            if (img != null) {
	                java.awt.Image imgEscalada = img.getScaledInstance(
	                    ancho, alto, java.awt.Image.SCALE_SMOOTH);
	                label.setIcon(new ImageIcon(imgEscalada));
	            } else {
	                label.setText("Sin imagen");
	                label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
	            }
	        } else {
	            label.setText("Sin imagen");
	            label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
	        }
	    } catch (java.io.IOException e) {
	        label.setText("Sin imagen");
	        label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
	    }
	}
	protected JButton crearBotonRojo(String texto) {
	    JButton boton = new JButton(texto);
	    boton.setFont(VentanaPrincipal.FUENTE_BOTON);
	    boton.setBackground(new Color(180, 50, 50));
	    boton.setForeground(Color.WHITE);
	    boton.setOpaque(true);
	    boton.setBorderPainted(false);
	    boton.setFocusPainted(false);
	    boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    boton.setBorder(BorderFactory.createEmptyBorder(
	        VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10),
	        VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10)));
	    boton.addMouseListener(new MouseAdapter() {
	        @Override public void mouseEntered(MouseEvent e) {
	            boton.setBackground(new Color(200, 60, 60));
	        }
	        @Override public void mouseExited(MouseEvent e) {
	            boton.setBackground(new Color(180, 50, 50));
	        }
	    });
	    return boton;
	}
}
