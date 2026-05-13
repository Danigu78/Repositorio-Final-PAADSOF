package Gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Panel base con ayudas visuales comunes para cliente, empleado y gestor. Asi
 * las pantallas reutilizan el mismo estilo.
 *
 * @author Daniel y Lucas
 * @version 1.0
 */
public abstract class PanelBaseInterfaz extends JPanel {

	private static final long serialVersionUID = 1L;
	protected final VentanaPrincipal ventana;
	/**
	 * Constructor base del panel.
	 *
	 * @param ventana ventana principal de la aplicación
	 */
	protected PanelBaseInterfaz(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);
	}


	/**
	 * Crea un panel base con scroll y título superior.
	 * Guarda el panel de contenido en clientProperty "contenido".
	 *
	 * @param titulo título de la sección
	 * @return panel contenedor listo para añadir componentes
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
	 * Obtiene el panel de contenido interno de un wrapper creado con crearPanelBase.
	 *
	 * @param base panel base
	 * @return panel de contenido interno
	 */
	protected JPanel getContenido(JPanel base) {
		return (JPanel) base.getClientProperty("contenido");
	}

	/**
	 * Crea un bloque visual con título usando GridBagLayout.
	 *
	 * @param titulo título del bloque
	 * @return panel bloque estilizado
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
	 * Genera constraints estándar para campos.
	 *
	 * @param y posición vertical en GridBagLayout
	 * @return GridBagConstraints configurado
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
	 * Genera constraints estándar para botones.
	 *
	 * @param y posición vertical en GridBagLayout
	 * @return GridBagConstraints configurado
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
	 * Genera constraints estándar para filtros.
	 *
	 * @param x posición horizontal
	 * @param y posición vertical
	 * @return GridBagConstraints configurado
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
	/**
	 * Crea una etiqueta estándar.
	 *
	 * @param texto texto a mostrar
	 * @return JLabel configurado
	 */
	protected JLabel crearLabel(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_NORMAL);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		return label;
	}
	/**
	 * Crea un campo de texto estándar.
	 *
	 * @return JTextField configurado
	 */
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
	/**
	 * Crea un campo de texto compacto.
	 *
	 * @return JTextField compacto configurado
	 */
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

	/**
	 * Crea un área de texto multilínea.
	 *
	 * @return JTextArea configurado
	 */
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
	/**
	 * Crea un JComboBox genérico.
	 *
	 * @param valores array de valores
	 * @return JComboBox con los valores
	 */
	protected <T> JComboBox<T> crearCombo(T[] valores) {
		JComboBox<T> combo = new JComboBox<>(valores);
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		combo.setBackground(Color.WHITE);
		combo.setForeground(Color.BLACK);
		combo.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(34)));
		combo.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE, 1));
		return combo;
	}
	/**
	 * Estiliza un JScrollPane alrededor de un componente, aplicando bordes,
	 * colores de fondo coherentes y configuración de scroll vertical y horizontal.
	 *
	 * @param comp componente que se insertará dentro del JScrollPane
	 * @return JScrollPane estilizado con el componente añadido
	 */
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

/**
 * Crea un botón de acción principal con estilo estándar (color acento).
 *
 * @param texto texto que se mostrará en el botón
 * @return JButton configurado como botón de acción principal
 */
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
	/**
	 * Crea un botón secundario con el mismo estilo base que el botón de acción,
	 * pero pensado para acciones menos importantes.
	 *
	 * @param texto texto que se mostrará en el botón
	 * @return JButton configurado como botón secundario
	 */

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
	/**
	 * Crea un botón de acción de tipo "peligro", usado para acciones destructivas
	 * como eliminar o cancelar.
	 *
	 * @param texto texto que se mostrará en el botón
	 * @return JButton configurado como botón de peligro
	 */
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
	/**
	 * Crea un panel de formulario con etiqueta y campo alineados en columna.
	 *
	 * @param etiqueta texto descriptivo del campo
	 * @param campo componente de entrada (texto, combo, etc.)
	 * @return JPanel que contiene el campo con su etiqueta
	 */
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
	/**
	 * Muestra un mensaje informativo en un cuadro de diálogo.
	 *
	 * @param mensaje texto del mensaje a mostrar
	 */
	protected void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}
	/**
	 * Muestra un mensaje de error en un cuadro de diálogo.
	 *
	 * @param mensaje texto del error a mostrar
	 */
	protected void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
	/**
	 * Añade un listener a un campo de texto que ejecuta una acción cada vez que
	 * cambia su contenido.
	 *
	 * @param campo JTextField al que se le añade el listener
	 * @param accion acción a ejecutar cuando el texto cambie
	 */
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
	/**
	 * Convierte un texto a formato normalizado (trim + minúsculas).
	 *
	 * @param texto cadena de entrada
	 * @return texto normalizado o cadena vacía si es null
	 */
	protected String normalizarTexto(String texto) {
		if (texto == null)
			return "";
		return texto.trim().toLowerCase(Locale.ROOT);
	}

/**
 * Convierte un String a Double de forma segura.
 *
 * @param texto cadena a convertir
 * @return Double convertido o null si no es válido
 */
	protected Double leerDoubleSeguro(String texto) {
		if (texto == null || texto.trim().isBlank())
			return null;
		try {
			return Double.parseDouble(texto.trim().replace(",", "."));
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * Convierte un String a Integer de forma segura.
	 *
	 * @param texto cadena a convertir
	 * @return Integer convertido o null si no es válido
	 */
	protected Integer leerEnteroSeguro(String texto) {
		if (texto == null || texto.trim().isBlank())
			return null;
		try {
			return Integer.parseInt(texto.trim());
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * Comprueba si una cadena contiene otra de forma insensible a mayúsculas/minúsculas.
	 *
	 * @param base texto donde se realiza la búsqueda
	 * @param buscado texto que se quiere buscar dentro de base
	 * @return true si buscado está contenido en base o si buscado es null/vacío; false en caso contrario
	 */
	protected boolean contieneTexto(String base, String buscado) {
		if (buscado == null || buscado.isBlank())
			return true;
		if (base == null)
			return false;
		return base.toLowerCase(Locale.ROOT).contains(buscado.toLowerCase(Locale.ROOT));
	}

	/**
	 * Convierte cualquier objeto a String de forma segura y lo recorta.
	 *
	 * @param value objeto a convertir en texto
	 * @return representación en String sin espacios laterales, o cadena vacía si es null
	 */
	protected String valorTexto(Object value) {
		if (value == null)
			return "";
		return String.valueOf(value).trim();
	}
	/**
	 * Convierte un objeto a Double de forma segura.
	 * Acepta números, Strings con coma o punto decimal y valores con símbolo €.
	 *
	 * @param value objeto a convertir
	 * @return Double convertido o null si el valor no es válido
	 */
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
	/**
	 * Convierte un objeto a Integer de forma segura.
	 *
	 * @param value objeto a convertir
	 * @return Integer convertido o null si el valor no es válido
	 */
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
	 * Crea un botón de acción principal con estilo naranja y efecto hover.
	 *
	 * @param texto texto que se mostrará en el botón
	 * @return JButton configurado como botón naranja principal
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
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(15),
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
	 * Crea un botón con estilo outline naranja (bordes visibles y fondo neutro).
	 *
	 * @param texto texto que se mostrará en el botón
	 * @return JButton configurado como botón outline
	 */
	protected JButton crearBotonOutline(String texto) {
		JButton boton = new JButton(texto);
		boton.setFont(VentanaPrincipal.FUENTE_BOTON);
		boton.setBackground(VentanaPrincipal.COLOR_PANEL);
		boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
		boton.setBorderPainted(true);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12),
								VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12))));
		return boton;
	}

	/**
	 * Crea un botón de volver con borde naranja y efecto hover.
	 *
	 * @param texto texto que se mostrará en el botón
	 * @return JButton configurado como botón de volver
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
				BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO, VentanaPrincipal.escalar(2)),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(15),
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
		barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, VentanaPrincipal.COLOR_BORDE));
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
				java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(url);
				if (img != null) {
					java.awt.Image imgEscalada = img.getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH);
					label.setIcon(new ImageIcon(imgEscalada));
				} else {
					label.setText("Sin imagen");
					label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
				}
			} else {
				label.setText("Sin imagen");
				label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			}
		} catch (IOException e) {
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
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10),
				VentanaPrincipal.escalar(5), VentanaPrincipal.escalar(10)));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				boton.setBackground(new Color(200, 60, 60));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				boton.setBackground(new Color(180, 50, 50));
			}
		});
		return boton;
	}

	/**
	 * Crea un combo con buscador integrado. El campo de texto filtra los items en
	 * tiempo real.
	 */
	protected JPanel crearComboConBuscador(String[] items, int ancho) {
		JPanel panel = new JPanel(new BorderLayout(0, VentanaPrincipal.escalar(2)));
		panel.setBackground(VentanaPrincipal.COLOR_PANEL);
		panel.setOpaque(false);

		JTextField campoBusqueda = crearCampoCompacto();
		campoBusqueda.setPreferredSize(new Dimension(ancho, VentanaPrincipal.escalar(28)));

		DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(items);
		JComboBox<String> combo = new JComboBox<>(modelo);
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		combo.setBackground(Color.WHITE);
		combo.setPreferredSize(new Dimension(ancho, VentanaPrincipal.escalar(28)));

		escucharCambios(campoBusqueda, () -> {
			String texto = campoBusqueda.getText().trim().toLowerCase(java.util.Locale.ROOT);
			modelo.removeAllElements();
			for (String item : items) {
				if (item.toLowerCase(java.util.Locale.ROOT).contains(texto))
					modelo.addElement(item);
			}
			if (modelo.getSize() > 0)
				combo.setSelectedIndex(0);
		});

		panel.add(campoBusqueda, BorderLayout.NORTH);
		panel.add(combo, BorderLayout.CENTER);
		panel.putClientProperty("combo", combo);
		return panel;
	}

	/**
	 * Recupera el JComboBox de un panel creado con crearComboConBuscador.
	 */
	@SuppressWarnings("unchecked")
	protected JComboBox<String> getComboDePanel(JPanel panel) {
		return (JComboBox<String>) panel.getClientProperty("combo");
	}

	/**
	 * Crea la barra de navegación superior común a cliente, empleado y gestor. Logo
	 * a la izquierda, pestañas en el centro, usuario + logout a la derecha. Las
	 * pestañas se registran en el listener con su actionCommand.
	 *
	 * @param textoLogo     Texto del logo ( CheckPoint - Gestor")
	 * @param nombreUsuario Nickname del usuario logueado
	 * @param pestanas      Array de {textoBoton, actionCommand} para cada pestaña
	 * @param listener      ActionListener que gestiona los clicks de las pestañas
	 * @return Panel de la barra de navegación
	 */
	protected JPanel crearBarraNavegacion(String textoLogo, String nombreUsuario, String[][] pestanas,
			ActionListener listener) {
		JPanel barra = new JPanel(new BorderLayout());
		barra.setBackground(VentanaPrincipal.COLOR_PANEL);
		barra.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, VentanaPrincipal.escalar(15))));
		barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));

		JLabel labelLogo = new JLabel(textoLogo);
		labelLogo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		barra.add(labelLogo, BorderLayout.WEST);

		JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
		panelPestanas.setBackground(VentanaPrincipal.COLOR_PANEL);
		panelPestanas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));

		// Inicializamos sin botón activo
		barra.putClientProperty("botonActivo", null);
		barra.putClientProperty("panelPestanas", panelPestanas);

		for (String[] pestana : pestanas) {
			JButton boton = crearBotonPestana(barra, pestana[0], pestana[1], listener);
			panelPestanas.add(boton);
		}
		barra.add(panelPestanas, BorderLayout.CENTER);

		JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(10), 0));
		panelDerecha.setBackground(VentanaPrincipal.COLOR_PANEL);
		panelDerecha.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), 0, 0, 0));

		JLabel labelUsuario = new JLabel(" " + nombreUsuario);
		labelUsuario.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelUsuario.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		panelDerecha.add(labelUsuario);

		JPanel separador = new JPanel();
		separador.setBackground(VentanaPrincipal.COLOR_ACENTO);
		separador.setPreferredSize(new Dimension(VentanaPrincipal.escalar(3), VentanaPrincipal.escalar(25)));
		panelDerecha.add(separador);

		JButton botonLogout = crearBotonRojo(" Salir");
		botonLogout.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		botonLogout.addActionListener(e -> ventana.logout());
		panelDerecha.add(botonLogout);

		barra.add(panelDerecha, BorderLayout.EAST);
		barra.putClientProperty("labelUsuario", labelUsuario);
		return barra;
	}

	/**
	 * Crea un botón de pestaña para la barra de navegación. El primer botón creado
	 * queda marcado como activo automáticamente.
	 *
	 * @param barra    La barra donde se guardará el estado del botón activo
	 * @param texto    Texto visible del botón
	 * @param cmd      ActionCommand del botón
	 * @param listener ActionListener a registrar en el botón
	 * @return Botón configurado
	 */
	private JButton crearBotonPestana(JPanel barra, String texto, String cmd, ActionListener listener) {
		JButton boton = new JButton(texto);
		boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		boton.setBackground(VentanaPrincipal.COLOR_PANEL);
		boton.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
				VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12)));
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setActionCommand(cmd);

		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (boton != barra.getClientProperty("botonActivo"))
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (boton != barra.getClientProperty("botonActivo"))
					boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			}
		});

		boton.addActionListener(listener);

		// El primer botón queda activo automáticamente
		if (barra.getClientProperty("botonActivo") == null)
			marcarBotonBarraActivo(barra, boton);

		return boton;
	}

	/**
	 * Marca un botón de la barra como activo y desmarca el anterior.
	 *
	 * @param barra La barra de navegación
	 * @param boton El botón a marcar como activo
	 */
	protected void marcarBotonBarraActivo(JPanel barra, JButton boton) {
		JButton anterior = (JButton) barra.getClientProperty("botonActivo");
		if (anterior != null) {
			anterior.setForeground(VentanaPrincipal.COLOR_TEXTO2);
			anterior.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8),
					VentanaPrincipal.escalar(12), VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12)));
		}
		barra.putClientProperty("botonActivo", boton);
		boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
		boton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(12),
						VentanaPrincipal.escalar(6), VentanaPrincipal.escalar(12))));
	}

	/**
	 * Busca el botón con el actionCommand indicado en la barra y lo marca activo.
	 * Método de conveniencia para usar desde actionPerformed.
	 *
	 * @param barra La barra de navegación
	 * @param cmd   El actionCommand del botón a marcar
	 */
	protected void marcarBotonBarraActivoPorCmd(JPanel barra, String cmd) {
		JPanel panelPestanas = (JPanel) barra.getClientProperty("panelPestanas");
		if (panelPestanas == null)
			return;
		for (Component c : panelPestanas.getComponents()) {
			if (c instanceof JButton) {
				JButton b = (JButton) c;
				if (cmd.equals(b.getActionCommand())) {
					marcarBotonBarraActivo(barra, b);
					return;
				}
			}
		}
	}

	/**
	 * Actualiza el nombre de usuario que se muestra en la barra de navegación.
	 *
	 * @param barra       La barra de navegación
	 * @param nuevoNombre El nuevo nombre a mostrar
	 */
	protected void actualizarUsuarioBarra(JPanel barra, String nuevoNombre) {
		JLabel label = (JLabel) barra.getClientProperty("labelUsuario");
		if (label != null)
			label.setText("" + nuevoNombre);
	}
}
