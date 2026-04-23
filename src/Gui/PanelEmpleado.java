package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import tienda.FiltroSegundaMano;
import tienda.FiltroVenta;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.TipoPermisos;
import productos.Categoria;
import productos.EstadoProducto;
import productos.LineaPack;
import productos.Producto2Mano;
import productos.ProductoVenta;
import intercambios.Oferta;

public class PanelEmpleado extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String SEC_STOCK = "STOCK";
	private static final String SEC_CATEGORIAS = "CATEGORIAS";
	private static final String SEC_PACKS = "PACKS";
	private static final String SEC_MODIFICAR = "MODIFICAR";
	private static final String SEC_PEDIDOS = "PEDIDOS";
	private static final String SEC_ENTREGA = "ENTREGA";
	private static final String SEC_TASACION = "TASACION";
	private static final String SEC_INTERCAMBIOS = "INTERCAMBIOS";
	private static final String SEC_NOTIFICACIONES = "NOTIFICACIONES";
	private static final int COL_VENTA_ID = 0;
	private static final int COL_VENTA_NOMBRE = 1;
	private static final int COL_VENTA_TIPO = 2;
	private static final int COL_VENTA_CATEGORIAS = 3;
	private static final int COL_VENTA_PRECIO = 4;
	private static final int COL_VENTA_STOCK = 5;
	private static final int COL_VENTA_PUNTUACION = 6;
	private static final int COL_2MANO_ID = 0;
	private static final int COL_2MANO_NOMBRE = 1;
	private static final int COL_2MANO_VALOR = 2;
	private static final int COL_2MANO_ESTADO = 3;
	private static final int COL_2MANO_VISIBLE = 4;
	private static final int COL_2MANO_BLOQUEADO = 5;
	private VentanaPrincipal ventana;
	private Empleado empleado;
	private CardLayout cardSecciones;
	private JPanel panelSecciones;
	private JLabel labelEmpleado;
	private JPanel barraNave;
	private JButton botonActivo;

	private static class TablaVentaData {
		JTable tabla;
		DefaultTableModel modelo;
		TableRowSorter<DefaultTableModel> sorter;

		TablaVentaData(JTable tabla, DefaultTableModel modelo, TableRowSorter<DefaultTableModel> sorter) {
			this.tabla = tabla;
			this.modelo = modelo;
			this.sorter = sorter;
		}
	}

	private static class TablaSegundaManoData {
		JTable tabla;
		DefaultTableModel modelo;
		TableRowSorter<DefaultTableModel> sorter;

		TablaSegundaManoData(JTable tabla, DefaultTableModel modelo, TableRowSorter<DefaultTableModel> sorter) {
			this.tabla = tabla;
			this.modelo = modelo;
			this.sorter = sorter;
		}
	}

	private static class SelectorVenta {
		JPanel bloque;
		JTable tabla;

		SelectorVenta(JPanel bloque, JTable tabla) {
			this.bloque = bloque;
			this.tabla = tabla;
		}
	}

	private static class SelectorSegundaMano {
		JPanel bloque;
		JTable tabla;

		SelectorSegundaMano(JPanel bloque, JTable tabla) {
			this.bloque = bloque;
			this.tabla = tabla;
		}
	}

	public PanelEmpleado(VentanaPrincipal ventana) {
		this.ventana = ventana;
		setLayout(new BorderLayout());
		setBackground(VentanaPrincipal.COLOR_FONDO);
	}

	public void actualizarEmpleado(Empleado empleado) {
		this.empleado = empleado;
		removeAll();
		inicializarUI();
		revalidate();
		repaint();
	}

	private void inicializarUI() {
		barraNave = crearBarraNavegacion();
		add(barraNave, BorderLayout.NORTH);
		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);
		boolean primero = true;
		String primeraSeccion = null;
		if (empleado.tienePermiso(TipoPermisos.GESTION_STOCK)) {
			panelSecciones.add(crearPanelStock(), SEC_STOCK);
			if (primero) {
				primeraSeccion = SEC_STOCK;
				primero = false;
			}
		}
		if (empleado.tienePermiso(TipoPermisos.GESTION_CATEGORIAS)) {
			panelSecciones.add(crearPanelCategorias(), SEC_CATEGORIAS);
			if (primero) {
				primeraSeccion = SEC_CATEGORIAS;
				primero = false;
			}
		}
		if (empleado.tienePermiso(TipoPermisos.GESTION_PACKS)) {
			panelSecciones.add(crearPanelPacks(), SEC_PACKS);
			if (primero) {
				primeraSeccion = SEC_PACKS;
				primero = false;
			}
		}
		if (empleado.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) {
			panelSecciones.add(crearPanelModificar(), SEC_MODIFICAR);
			if (primero) {
				primeraSeccion = SEC_MODIFICAR;
				primero = false;
			}
		}
		if (empleado.tienePermiso(TipoPermisos.GESTION_PEDIDOS)) {
			panelSecciones.add(crearPanelPedidos(), SEC_PEDIDOS);
			if (primero) {
				primeraSeccion = SEC_PEDIDOS;
				primero = false;
			}
		}
		if (empleado.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS)) {
			panelSecciones.add(crearPanelEntregas(), SEC_ENTREGA);
			if (primero) {
				primeraSeccion = SEC_ENTREGA;
				primero = false;
			}
		}
		if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
			panelSecciones.add(crearPanelTasacion(), SEC_TASACION);
			if (primero) {
				primeraSeccion = SEC_TASACION;
				primero = false;
			}
		}
		if (empleado.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			panelSecciones.add(crearPanelIntercambios(), SEC_INTERCAMBIOS);
			if (primero) {
				primeraSeccion = SEC_INTERCAMBIOS;
				primero = false;
			}
		}
		panelSecciones.add(crearPanelNotificaciones(), SEC_NOTIFICACIONES);
		if (primero) {
			primeraSeccion = SEC_NOTIFICACIONES;
		}
		add(panelSecciones, BorderLayout.CENTER);
		if (primeraSeccion != null) {
			cardSecciones.show(panelSecciones, primeraSeccion);
		}
	}

	private JPanel crearBarraNavegacion() {
		JPanel barra = new JPanel(new BorderLayout());
		barra.setBackground(VentanaPrincipal.COLOR_BARRA);
		barra.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
				BorderFactory.createEmptyBorder(0, VentanaPrincipal.escalar(15), 0, VentanaPrincipal.escalar(15))));
		barra.setPreferredSize(new Dimension(0, VentanaPrincipal.escalar(58)));
		JLabel labelLogo = new JLabel("🎮 CheckPoint - Empleado");
		labelLogo.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
		labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
		barra.add(labelLogo, BorderLayout.WEST);
		JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
		panelPestanas.setBackground(VentanaPrincipal.COLOR_BARRA);
		panelPestanas.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), 0, 0, 0));
		botonActivo = null;
		if (empleado.tienePermiso(TipoPermisos.GESTION_STOCK)) {
			agregarPestana(panelPestanas, "Stock", SEC_STOCK);
		}
		if (empleado.tienePermiso(TipoPermisos.GESTION_CATEGORIAS)) {
			agregarPestana(panelPestanas, "Categorías", SEC_CATEGORIAS);
		}
		if (empleado.tienePermiso(TipoPermisos.GESTION_PACKS)) {
			agregarPestana(panelPestanas, "Packs", SEC_PACKS);
		}
		if (empleado.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) {
			agregarPestana(panelPestanas, "Modificar", SEC_MODIFICAR);
		}
		if (empleado.tienePermiso(TipoPermisos.GESTION_PEDIDOS)) {
			agregarPestana(panelPestanas, "Pedidos", SEC_PEDIDOS);
		}
		if (empleado.tienePermiso(TipoPermisos.ENTREGA_PEDIDOS)) {
			agregarPestana(panelPestanas, "Entregas", SEC_ENTREGA);
		}
		if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
			agregarPestana(panelPestanas, "Tasaciones", SEC_TASACION);
		}
		if (empleado.tienePermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			agregarPestana(panelPestanas, "Intercambios", SEC_INTERCAMBIOS);
		}
		agregarPestana(panelPestanas, "Notificaciones", SEC_NOTIFICACIONES);
		barra.add(panelPestanas, BorderLayout.CENTER);
		JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, VentanaPrincipal.escalar(10), 0));
		panelDerecha.setBackground(VentanaPrincipal.COLOR_BARRA);
		panelDerecha.setBorder(BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(12), 0, 0, 0));
		labelEmpleado = new JLabel("👤 " + (empleado != null ? empleado.getNickname() : "Empleado"));
		labelEmpleado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		labelEmpleado.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
		panelDerecha.add(labelEmpleado);
		JButton botonLogout = new JButton("🚪 Salir");
		botonLogout.setFont(VentanaPrincipal.FUENTE_PEQUENA);
		botonLogout.setForeground(new Color(220, 80, 80));
		botonLogout.setBackground(VentanaPrincipal.COLOR_BARRA);
		botonLogout.setBorderPainted(false);
		botonLogout.setFocusPainted(false);
		botonLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botonLogout.addActionListener(e -> ventana.logout());
		panelDerecha.add(botonLogout);
		barra.add(panelDerecha, BorderLayout.EAST);
		return barra;
	}

	private void agregarPestana(JPanel panel, String texto, String seccion) {
		JButton boton = new JButton(texto);
		boton.setUI(new BasicButtonUI());
		boton.setFont(new Font("Segoe UI", Font.PLAIN, VentanaPrincipal.escalar(13)));
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
		boton.setBackground(VentanaPrincipal.COLOR_BARRA);
		boton.setOpaque(true);
		boton.setContentAreaFilled(true);
		boton.setFocusPainted(false);
		boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
				BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
						VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (boton != botonActivo) {
					boton.setBackground(VentanaPrincipal.COLOR_BARRA_HOVER);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (boton != botonActivo) {
					boton.setBackground(VentanaPrincipal.COLOR_BARRA);
				}
			}
		});
		boton.addActionListener(e -> {
			activarPestana(boton);
			cardSecciones.show(panelSecciones, seccion);
		});
		if (botonActivo == null) {
			botonActivo = boton;
			marcarActivo(boton);
		}
		panel.add(boton);
	}

	private void marcarActivo(JButton boton) {
		boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
		boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
		boton.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_ACENTO, 1),
						BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
								VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));
	}

	private void activarPestana(JButton boton) {
		if (botonActivo != null) {
			botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO_BARRA);
			botonActivo.setBackground(VentanaPrincipal.COLOR_BARRA);
			botonActivo.setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
							BorderFactory.createEmptyBorder(VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14),
									VentanaPrincipal.escalar(8), VentanaPrincipal.escalar(14))));
		}
		botonActivo = boton;
		marcarActivo(boton);
	}

	private JPanel crearPanelBase(String titulo) {
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

	private JPanel getContenido(JPanel base) {
		return (JPanel) base.getClientProperty("contenido");
	}

	private JPanel crearBloque(String titulo) {
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

	private GridBagConstraints gbcCampo(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(6, 0, 6, 0);
		return gbc;
	}

	private GridBagConstraints gbcBoton(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets = new Insets(12, 0, 6, 0);
		return gbc;
	}

	private GridBagConstraints gbcFiltro(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		return gbc;
	}

	private JLabel crearLabel(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(VentanaPrincipal.FUENTE_NORMAL);
		label.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		return label;
	}

	private JTextField crearCampo() {
		JTextField campo = new JTextField();
		campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		campo.setPreferredSize(new Dimension(0, 40));
		campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		campo.setBackground(new Color(24, 24, 24));
		campo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		campo.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		return campo;
	}

	private JTextField crearCampoCompacto() {
		JTextField campo = new JTextField();
		campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		campo.setPreferredSize(new Dimension(0, 34));
		campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
		campo.setBackground(new Color(24, 24, 24));
		campo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		campo.setCaretColor(VentanaPrincipal.COLOR_ACENTO);
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1),
				BorderFactory.createEmptyBorder(6, 10, 6, 10)));
		return campo;
	}

	private JTextArea crearArea() {
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

	private <T> JComboBox<T> crearCombo(T[] valores) {
		JComboBox<T> combo = new JComboBox<>(valores);
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		combo.setBackground(new Color(24, 24, 24));
		combo.setForeground(VentanaPrincipal.COLOR_TEXTO);
		combo.setPreferredSize(new Dimension(0, 34));
		combo.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1));
		return combo;
	}

	private JScrollPane estilizarScroll(Component comp) {
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

	private JButton crearBotonAccion(String texto) {
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
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				boton.setBackground(VentanaPrincipal.COLOR_ACENTO2);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				boton.setBackground(VentanaPrincipal.COLOR_ACENTO);
			}
		});
		return boton;
	}

	private JButton crearBotonSecundario(String texto) {
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
		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				boton.setBackground(new Color(90, 90, 90));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				boton.setBackground(new Color(70, 70, 70));
			}
		});
		return boton;
	}

	private JPanel crearCampoFormulario(String etiqueta, JComponent campo) {
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

	private void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje);
	}

	private void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private JPanel crearPanelStock() {
		JPanel base = crearPanelBase("Gestión de Stock");
		JPanel contenido = getContenido(base);
		JTextField campoId = crearCampo();
		JTextField campoCantidad = crearCampo();
		SelectorVenta selector = crearSelectorProductosVenta("Productos actuales",
				"Filtra por texto, categoría, precio, stock y puntuación. Pulsa una fila para cargar su ID.", true,
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
		return base;
	}

	private JPanel crearPanelCategorias() {
		JPanel base = crearPanelBase("Gestión de Categorías");
		JPanel contenido = getContenido(base);
		JTextField campoId = crearCampo();
		JTextField campoCategoria = crearCampo();
		SelectorVenta selector = crearSelectorProductosVenta("Buscar producto para categoría",
				"Selecciona una fila para cargar el ID del producto antes de añadir o quitar categorías.", true,
				campoId);
		JPanel bloque = crearBloque("Añadir o eliminar producto de categoría");
		JButton botonAnadir = crearBotonAccion("Añadir a categoría");
		JButton botonEliminar = crearBotonAccion("Eliminar de categoría");
		bloque.add(crearLabel("ID producto"), gbcCampo(1));
		bloque.add(campoId, gbcCampo(2));
		bloque.add(crearLabel("Nombre categoría"), gbcCampo(3));
		bloque.add(campoCategoria, gbcCampo(4));
		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonAnadir);
		filaBotones.add(botonEliminar);
		bloque.add(filaBotones, gbcBoton(5));
		botonAnadir.addActionListener(e -> {
			String id = campoId.getText().trim();
			String categoria = campoCategoria.getText().trim();
			if (id.isBlank() || categoria.isBlank()) {
				mostrarError("Introduce un ID y una categoría.");
				return;
			}
			boolean ok = empleado.añadirProductoACategoria(id, categoria);
			if (ok) {
				recargarTablaProductos(selector.tabla);
				mostrarMensaje("Producto añadido a la categoría.");
			} else {
				mostrarError("No se pudo añadir el producto a la categoría.");
			}
		});
		botonEliminar.addActionListener(e -> {
			String id = campoId.getText().trim();
			String categoria = campoCategoria.getText().trim();
			if (id.isBlank() || categoria.isBlank()) {
				mostrarError("Introduce un ID y una categoría.");
				return;
			}
			boolean ok = empleado.eliminarProductoDeCategoria(id, categoria);
			if (ok) {
				recargarTablaProductos(selector.tabla);
				mostrarMensaje("Producto eliminado de la categoría.");
			} else {
				mostrarError("No se pudo eliminar el producto de la categoría.");
			}
		});
		contenido.add(selector.bloque);
		contenido.add(Box.createVerticalStrut(18));
		contenido.add(bloque);
		return base;
	}

	private JPanel crearPanelPacks() {
		JPanel base = crearPanelBase("Gestión de Packs");
		JPanel contenido = getContenido(base);
		JTextField campoIdProductoLinea = crearCampo();
		JTextField campoIdProducto = crearCampo();
		SelectorVenta selector = crearSelectorProductosVenta("Catálogo para construir packs",
				"Filtra y selecciona productos. El ID seleccionado se cargará para añadir líneas o modificar packs.",
				true, campoIdProductoLinea, campoIdProducto);
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
				boolean ok = empleado.crearPack(campoNombre.getText().trim(), areaDescripcionPack.getText().trim(),
						campoImagen.getText().trim(), precio, stock, lineas);
				if (ok) {
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
				boolean ok = empleado.añadirProductoaPack(campoIdProducto.getText().trim(),
						campoIdPack.getText().trim(), unidades);
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
				boolean ok = empleado.modificarUnidadesProductoEnPack(campoIdProducto.getText().trim(),
						campoIdPack.getText().trim(), unidades);
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
		return base;
	}

	private JPanel crearPanelModificar() {
		JPanel base = crearPanelBase("Modificar Productos");
		JPanel contenido = getContenido(base);
		JTextField campoId = crearCampo();
		SelectorVenta selector = crearSelectorProductosVenta("Buscar producto a modificar",
				"Selecciona una fila para cargar el ID y luego modifica descripción o imagen.", true, campoId);
		JPanel bloque = crearBloque("Modificar descripción o imagen");
		JTextArea areaDescripcion = crearArea();
		JTextField campoImagen = crearCampo();
		JButton botonDescripcion = crearBotonAccion("Guardar descripción");
		JButton botonImagen = crearBotonAccion("Guardar imagen");
		bloque.add(crearLabel("ID producto"), gbcCampo(1));
		bloque.add(campoId, gbcCampo(2));
		bloque.add(crearLabel("Nueva descripción"), gbcCampo(3));
		bloque.add(estilizarScroll(areaDescripcion), gbcCampo(4));
		bloque.add(botonDescripcion, gbcBoton(5));
		bloque.add(crearLabel("Nueva ruta de imagen"), gbcCampo(6));
		bloque.add(campoImagen, gbcCampo(7));
		bloque.add(botonImagen, gbcBoton(8));
		botonDescripcion.addActionListener(e -> {
			String id = campoId.getText().trim();
			String descripcion = areaDescripcion.getText().trim();
			if (id.isBlank() || descripcion.isBlank()) {
				mostrarError("Introduce un ID y una descripción.");
				return;
			}
			boolean ok = empleado.modificarDescripcionProducto(id, descripcion);
			if (ok) {
				mostrarMensaje("Descripción modificada correctamente.");
			} else {
				mostrarError("No se pudo modificar la descripción.");
			}
		});
		botonImagen.addActionListener(e -> {
			String id = campoId.getText().trim();
			String imagen = campoImagen.getText().trim();
			if (id.isBlank() || imagen.isBlank()) {
				mostrarError("Introduce un ID y una ruta de imagen.");
				return;
			}
			boolean ok = empleado.modificarImagenProducto(id, imagen);
			if (ok) {
				mostrarMensaje("Imagen modificada correctamente.");
			} else {
				mostrarError("No se pudo modificar la imagen.");
			}
		});
		contenido.add(selector.bloque);
		contenido.add(Box.createVerticalStrut(18));
		contenido.add(bloque);
		return base;
	}

	private JPanel crearPanelPedidos() {
		JPanel base = crearPanelBase("Gestión de Pedidos");
		JPanel contenido = getContenido(base);
		JPanel bloque = crearBloque("Preparar pedido");
		JTextField campoPedido = crearCampo();
		JButton boton = crearBotonAccion("Preparar pedido");
		bloque.add(crearLabel("ID pedido"), gbcCampo(1));
		bloque.add(campoPedido, gbcCampo(2));
		bloque.add(boton, gbcBoton(3));
		boton.addActionListener(e -> {
			String id = campoPedido.getText().trim();
			if (id.isBlank()) {
				mostrarError("Introduce un ID de pedido.");
				return;
			}
			boolean ok = empleado.prepararPedido(id);
			if (ok) {
				mostrarMensaje("Pedido preparado correctamente.");
			} else {
				mostrarError("No se pudo preparar el pedido.");
			}
		});
		contenido.add(bloque);
		return base;
	}

	private JPanel crearPanelEntregas() {
		JPanel base = crearPanelBase("Entrega de Pedidos");
		JPanel contenido = getContenido(base);
		JPanel bloque = crearBloque("Entregar pedido");
		JTextField campoCodigo = crearCampo();
		JButton boton = crearBotonAccion("Entregar pedido");
		bloque.add(crearLabel("Código de recogida"), gbcCampo(1));
		bloque.add(campoCodigo, gbcCampo(2));
		bloque.add(boton, gbcBoton(3));
		boton.addActionListener(e -> {
			String codigo = campoCodigo.getText().trim();
			if (codigo.isBlank()) {
				mostrarError("Introduce un código de recogida.");
				return;
			}
			boolean ok = empleado.entregarPedido(codigo);
			if (ok) {
				mostrarMensaje("Pedido entregado correctamente.");
			} else {
				mostrarError("No se pudo entregar el pedido.");
			}
		});
		contenido.add(bloque);
		return base;
	}

	private JPanel crearPanelTasacion() {
		JPanel base = crearPanelBase("Tasación de Productos");
		JPanel contenido = getContenido(base);
		JTextField campoId = crearCampo();
		SelectorSegundaMano selector = crearSelectorProductosSegundaMano("Productos de segunda mano",
				"Filtra por texto, valor tasado y estado mínimo. Pulsa una fila para cargar su ID en la tasación.",
				true, campoId);
		JPanel bloque = crearBloque("Tasar producto");
		JTextField campoPrecio = crearCampo();
		JComboBox<EstadoProducto> comboEstado = crearCombo(EstadoProducto.values());
		JButton boton = crearBotonAccion("Tasar");
		bloque.add(crearLabel("ID producto"), gbcCampo(1));
		bloque.add(campoId, gbcCampo(2));
		bloque.add(crearLabel("Precio tasado"), gbcCampo(3));
		bloque.add(campoPrecio, gbcCampo(4));
		bloque.add(crearLabel("Estado"), gbcCampo(5));
		bloque.add(comboEstado, gbcCampo(6));
		bloque.add(boton, gbcBoton(7));
		boton.addActionListener(e -> {
			try {
				String id = campoId.getText().trim();
				Double precio = leerDoubleSeguro(campoPrecio.getText());
				EstadoProducto estado = (EstadoProducto) comboEstado.getSelectedItem();
				if (id.isBlank() || precio == null || precio < 0 || estado == null) {
					mostrarError("Completa correctamente ID, precio y estado.");
					return;
				}
				empleado.tasarProducto(id, precio, estado);
				recargarTablaProductos2Mano(selector.tabla);
				mostrarMensaje("Tasación enviada.");
			} catch (Exception ex) {
				mostrarError("No se pudo tasar el producto.");
			}
		});
		contenido.add(selector.bloque);
		contenido.add(Box.createVerticalStrut(18));
		contenido.add(bloque);
		return base;
	}

	private JPanel crearPanelIntercambios() {
		JPanel base = crearPanelBase("Confirmación de Intercambios");
		JPanel contenido = getContenido(base);
		JPanel bloque = crearBloque("Confirmar intercambio");
		JTextField campoTextoOferta = crearCampo();
		JButton boton = crearBotonAccion("Confirmar");
		JLabel ayuda = crearLabel("Escribe ID o texto identificativo de la oferta");
		ayuda.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		bloque.add(ayuda, gbcCampo(1));
		bloque.add(campoTextoOferta, gbcCampo(2));
		bloque.add(boton, gbcBoton(3));
		boton.addActionListener(e -> {
			Oferta oferta = buscarOfertaPorTexto(campoTextoOferta.getText().trim());
			if (oferta == null) {
				mostrarError("No se encontró ninguna oferta que coincida.");
				return;
			}
			boolean ok = empleado.confirmarIntercambio(oferta);
			if (ok) {
				mostrarMensaje("Intercambio confirmado.");
			} else {
				mostrarError("No se pudo confirmar el intercambio.");
			}
		});
		contenido.add(bloque);
		return base;
	}

	private JPanel crearPanelNotificaciones() {
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
		return base;
	}

	private SelectorVenta crearSelectorProductosVenta(String titulo, String ayuda, boolean incluirRefrescar,
			JTextField... camposIdDestino) {
		JPanel bloque = crearBloque(titulo);
		TablaVentaData data = crearTablaProductosVentaData();
		JLabel ayudaLabel = crearLabel(ayuda);
		ayudaLabel.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		JScrollPane scrollTabla = estilizarScroll(data.tabla);
		scrollTabla.setPreferredSize(new Dimension(1050, 300));
		conectarSeleccionId(data.tabla, COL_VENTA_ID, camposIdDestino);
		bloque.add(ayudaLabel, gbcCampo(1));
		bloque.add(crearPanelFiltrosVenta(data.sorter), gbcCampo(2));
		bloque.add(scrollTabla, gbcCampo(3));
		if (incluirRefrescar) {
			JButton botonRefrescar = crearBotonAccion("Refrescar lista");
			botonRefrescar.addActionListener(e -> recargarTablaProductos(data.tabla));
			bloque.add(botonRefrescar, gbcBoton(4));
		}
		return new SelectorVenta(bloque, data.tabla);
	}

	private SelectorSegundaMano crearSelectorProductosSegundaMano(String titulo, String ayuda, boolean incluirRefrescar,
			JTextField... camposIdDestino) {
		JPanel bloque = crearBloque(titulo);
		TablaSegundaManoData data = crearTablaProductos2ManoData();
		JLabel ayudaLabel = crearLabel(ayuda);
		ayudaLabel.setForeground(VentanaPrincipal.COLOR_TEXTO2);
		JScrollPane scrollTabla = estilizarScroll(data.tabla);
		scrollTabla.setPreferredSize(new Dimension(1050, 300));
		conectarSeleccionId(data.tabla, COL_2MANO_ID, camposIdDestino);
		bloque.add(ayudaLabel, gbcCampo(1));
		bloque.add(crearPanelFiltros2Mano(data.sorter), gbcCampo(2));
		bloque.add(scrollTabla, gbcCampo(3));
		if (incluirRefrescar) {
			JButton botonRefrescar = crearBotonAccion("Refrescar lista");
			botonRefrescar.addActionListener(e -> recargarTablaProductos2Mano(data.tabla));
			bloque.add(botonRefrescar, gbcBoton(4));
		}
		return new SelectorSegundaMano(bloque, data.tabla);
	}

	private void conectarSeleccionId(JTable tabla, int columnaId, JTextField... camposDestino) {
		tabla.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}
			int filaVista = tabla.getSelectedRow();
			if (filaVista < 0) {
				return;
			}
			int filaModelo = tabla.convertRowIndexToModel(filaVista);
			Object valor = tabla.getModel().getValueAt(filaModelo, columnaId);
			if (valor == null) {
				return;
			}
			String id = String.valueOf(valor);
			for (JTextField campo : camposDestino) {
				if (campo != null) {
					campo.setText(id);
				}
			}
		});
	}

	private TablaVentaData crearTablaProductosVentaData() {
		String[] columnas = { "ID", "Nombre", "Tipo", "Categorías", "Precio", "Stock", "Puntuación" };
		DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case COL_VENTA_PRECIO:
				case COL_VENTA_PUNTUACION:
					return Double.class;
				case COL_VENTA_STOCK:
					return Integer.class;
				default:
					return String.class;
				}
			}
		};
		cargarModeloProductosVenta(modelo);
		JTable tabla = new JTable(modelo);
		tabla.setRowSorter(new TableRowSorter<>(modelo));
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaBase(tabla);
		DefaultTableCellRenderer textoRenderer = crearRendererTabla(SwingConstants.LEFT, false, false);
		DefaultTableCellRenderer precioRenderer = crearRendererTabla(SwingConstants.RIGHT, true, false);
		DefaultTableCellRenderer enteroRenderer = crearRendererTabla(SwingConstants.CENTER, false, false);
		DefaultTableCellRenderer decimalRenderer = crearRendererTabla(SwingConstants.CENTER, false, true);
		tabla.setDefaultRenderer(String.class, textoRenderer);
		tabla.setDefaultRenderer(Integer.class, enteroRenderer);
		tabla.setDefaultRenderer(Double.class, decimalRenderer);
		tabla.getColumnModel().getColumn(COL_VENTA_PRECIO).setCellRenderer(precioRenderer);
		tabla.getColumnModel().getColumn(COL_VENTA_STOCK).setCellRenderer(enteroRenderer);
		tabla.getColumnModel().getColumn(COL_VENTA_PUNTUACION).setCellRenderer(decimalRenderer);
		tabla.getColumnModel().getColumn(COL_VENTA_ID).setPreferredWidth(90);
		tabla.getColumnModel().getColumn(COL_VENTA_NOMBRE).setPreferredWidth(240);
		tabla.getColumnModel().getColumn(COL_VENTA_TIPO).setPreferredWidth(120);
		tabla.getColumnModel().getColumn(COL_VENTA_CATEGORIAS).setPreferredWidth(250);
		tabla.getColumnModel().getColumn(COL_VENTA_PRECIO).setPreferredWidth(100);
		tabla.getColumnModel().getColumn(COL_VENTA_STOCK).setPreferredWidth(80);
		tabla.getColumnModel().getColumn(COL_VENTA_PUNTUACION).setPreferredWidth(90);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
		tabla.setRowSorter(sorter);
		return new TablaVentaData(tabla, modelo, sorter);
	}

	private TablaSegundaManoData crearTablaProductos2ManoData() {
		String[] columnas = { "ID", "Nombre", "Valor tasación", "Estado", "Visible", "Bloqueado" };
		DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case COL_2MANO_VALOR:
					return Double.class;
				default:
					return String.class;
				}
			}
		};
		cargarModeloProductos2Mano(modelo);
		JTable tabla = new JTable(modelo);
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estilizarTablaBase(tabla);
		DefaultTableCellRenderer textoRenderer = crearRendererTabla(SwingConstants.LEFT, false, false);
		DefaultTableCellRenderer precioRenderer = crearRendererTabla(SwingConstants.RIGHT, true, false);
		DefaultTableCellRenderer centroRenderer = crearRendererTabla(SwingConstants.CENTER, false, false);
		tabla.setDefaultRenderer(String.class, textoRenderer);
		tabla.setDefaultRenderer(Double.class, precioRenderer);
		tabla.getColumnModel().getColumn(COL_2MANO_VALOR).setCellRenderer(precioRenderer);
		tabla.getColumnModel().getColumn(COL_2MANO_ESTADO).setCellRenderer(centroRenderer);
		tabla.getColumnModel().getColumn(COL_2MANO_VISIBLE).setCellRenderer(centroRenderer);
		tabla.getColumnModel().getColumn(COL_2MANO_BLOQUEADO).setCellRenderer(centroRenderer);
		tabla.getColumnModel().getColumn(COL_2MANO_ID).setPreferredWidth(90);
		tabla.getColumnModel().getColumn(COL_2MANO_NOMBRE).setPreferredWidth(260);
		tabla.getColumnModel().getColumn(COL_2MANO_VALOR).setPreferredWidth(120);
		tabla.getColumnModel().getColumn(COL_2MANO_ESTADO).setPreferredWidth(140);
		tabla.getColumnModel().getColumn(COL_2MANO_VISIBLE).setPreferredWidth(90);
		tabla.getColumnModel().getColumn(COL_2MANO_BLOQUEADO).setPreferredWidth(100);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
		tabla.setRowSorter(sorter);
		return new TablaSegundaManoData(tabla, modelo, sorter);
	}

	private void estilizarTablaBase(JTable tabla) {
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tabla.setRowHeight(30);
		tabla.setBackground(Color.WHITE);
		tabla.setForeground(VentanaPrincipal.COLOR_TEXTO);
		tabla.setGridColor(new Color(225, 225, 225));
		tabla.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
		tabla.setSelectionForeground(VentanaPrincipal.COLOR_TEXTO);
		tabla.setFillsViewportHeight(true);
		tabla.setShowVerticalLines(true);
		tabla.setShowHorizontalLines(true);
		tabla.setOpaque(true);
		JTableHeader header = tabla.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(232, 232, 232));
		header.setForeground(VentanaPrincipal.COLOR_TEXTO);
		header.setBorder(BorderFactory.createLineBorder(VentanaPrincipal.COLOR_BORDE));
		header.setReorderingAllowed(false);
		header.setResizingAllowed(true);
	}

	private DefaultTableCellRenderer crearRendererTabla(int alignment, boolean formatoEuro, boolean formatoDecimal) {
		return new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				c.setOpaque(true);
				c.setHorizontalAlignment(alignment);
				if (formatoEuro) {
					if (value instanceof Number) {
						c.setText(String.format(Locale.US, "%.2f €", ((Number) value).doubleValue()).replace('.', ','));
					} else {
						c.setText(value == null ? "Sin valorar" : String.valueOf(value));
					}
				} else if (formatoDecimal) {
					if (value instanceof Number) {
						c.setText(String.format(Locale.US, "%.2f", ((Number) value).doubleValue()).replace('.', ','));
					} else {
						c.setText(value == null ? "" : String.valueOf(value));
					}
				} else {
					c.setText(value == null ? "" : String.valueOf(value));
				}
				if (isSelected) {
					c.setBackground(VentanaPrincipal.COLOR_ACENTO);
					c.setForeground(VentanaPrincipal.COLOR_TEXTO);
				} else {
					c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(246, 246, 246));
					c.setForeground(VentanaPrincipal.COLOR_TEXTO);
				}
				c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
				return c;
			}
		};
	}

	private JPanel crearPanelFiltrosVenta(TableRowSorter<DefaultTableModel> sorter) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);

		JTextField campoBuscar = crearCampoCompacto();
		JComboBox<String> comboTipo = crearCombo(new String[] { "Todos", "Comic", "Juego", "Figura", "Pack" });

		JTextField campoPrecioMin = crearCampoCompacto();
		JTextField campoPrecioMax = crearCampoCompacto();
		JTextField campoStockMin = crearCampoCompacto();
		JTextField campoStockMax = crearCampoCompacto();
		JTextField campoPuntuacionMin = crearCampoCompacto();

		List<String> nombresCategorias = obtenerNombresCategoriasVenta();
		String[] valoresCombo = new String[nombresCategorias.size() + 1];
		valoresCombo[0] = "Todas";
		for (int i = 0; i < nombresCategorias.size(); i++) {
			valoresCombo[i + 1] = nombresCategorias.get(i);
		}
		JComboBox<String> comboCategoria = crearCombo(valoresCombo);

		JButton botonLimpiar = crearBotonSecundario("Limpiar filtros");

		panel.add(crearCampoFormulario("Buscar (ID, nombre...)", campoBuscar), gbcFiltro(0, 0));
		panel.add(crearCampoFormulario("Tipo", comboTipo), gbcFiltro(1, 0));
		panel.add(crearCampoFormulario("Categoría", comboCategoria), gbcFiltro(2, 0));
		panel.add(crearCampoFormulario("Precio mínimo", campoPrecioMin), gbcFiltro(3, 0));
		panel.add(crearCampoFormulario("Precio máximo", campoPrecioMax), gbcFiltro(4, 0));

		panel.add(crearCampoFormulario("Stock mínimo", campoStockMin), gbcFiltro(0, 1));
		panel.add(crearCampoFormulario("Stock máximo", campoStockMax), gbcFiltro(1, 1));
		panel.add(crearCampoFormulario("Puntuación mínima", campoPuntuacionMin), gbcFiltro(2, 1));
		panel.add(crearCampoFormulario(" ", botonLimpiar), gbcFiltro(3, 1));

		Runnable aplicar = () -> aplicarFiltroVenta(sorter, campoBuscar, comboTipo, comboCategoria, campoPrecioMin,
				campoPrecioMax, campoStockMin, campoStockMax, campoPuntuacionMin);

		escucharCambios(campoBuscar, aplicar);
		escucharCambios(campoPrecioMin, aplicar);
		escucharCambios(campoPrecioMax, aplicar);
		escucharCambios(campoStockMin, aplicar);
		escucharCambios(campoStockMax, aplicar);
		escucharCambios(campoPuntuacionMin, aplicar);

		comboTipo.addActionListener(e -> aplicar.run());
		comboCategoria.addActionListener(e -> aplicar.run());

		botonLimpiar.addActionListener(e -> {
			campoBuscar.setText("");
			comboTipo.setSelectedIndex(0);
			campoPrecioMin.setText("");
			campoPrecioMax.setText("");
			campoStockMin.setText("");
			campoStockMax.setText("");
			campoPuntuacionMin.setText("");
			comboCategoria.setSelectedIndex(0);
			sorter.setRowFilter(null);
		});

		aplicar.run();
		return panel;
	}

	private JPanel crearPanelFiltros2Mano(TableRowSorter<DefaultTableModel> sorter) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
		JTextField campoBuscar = crearCampoCompacto();
		JTextField campoValorMin = crearCampoCompacto();
		JTextField campoValorMax = crearCampoCompacto();
		JComboBox<Object> comboEstado = new JComboBox<>();
		comboEstado.addItem("Cualquiera");
		for (EstadoProducto estado : EstadoProducto.values()) {
			comboEstado.addItem(estado);
		}
		comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		comboEstado.setBackground(new Color(24, 24, 24));
		comboEstado.setForeground(VentanaPrincipal.COLOR_TEXTO);
		comboEstado.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1));
		JCheckBox checkIncluirSinValorar = new JCheckBox("Incluir sin valorar");
		checkIncluirSinValorar.setOpaque(false);
		checkIncluirSinValorar.setForeground(VentanaPrincipal.COLOR_TEXTO);
		checkIncluirSinValorar.setSelected(true);
		JButton botonLimpiar = crearBotonSecundario("Limpiar filtros");
		panel.add(crearCampoFormulario("Buscar (ID, nombre, estado...)", campoBuscar), gbcFiltro(0, 0));
		panel.add(crearCampoFormulario("Valor mínimo", campoValorMin), gbcFiltro(1, 0));
		panel.add(crearCampoFormulario("Valor máximo", campoValorMax), gbcFiltro(2, 0));
		panel.add(crearCampoFormulario("Estado mínimo", comboEstado), gbcFiltro(3, 0));
		panel.add(crearCampoFormulario(" ", checkIncluirSinValorar), gbcFiltro(0, 1));
		panel.add(crearCampoFormulario(" ", botonLimpiar), gbcFiltro(1, 1));
		Runnable aplicar = () -> aplicarFiltroSegundaMano(sorter, campoBuscar, campoValorMin, campoValorMax,
				comboEstado, checkIncluirSinValorar);
		escucharCambios(campoBuscar, aplicar);
		escucharCambios(campoValorMin, aplicar);
		escucharCambios(campoValorMax, aplicar);
		comboEstado.addActionListener(e -> aplicar.run());
		checkIncluirSinValorar.addActionListener(e -> aplicar.run());
		botonLimpiar.addActionListener(e -> {
			campoBuscar.setText("");
			campoValorMin.setText("");
			campoValorMax.setText("");
			comboEstado.setSelectedIndex(0);
			checkIncluirSinValorar.setSelected(true);
			aplicar.run();
		});
		aplicar.run();
		return panel;
	}

	private void aplicarFiltroVenta(TableRowSorter<DefaultTableModel> sorter, JTextField campoBuscar,
			JComboBox<String> comboTipo, JComboBox<String> comboCategoria, JTextField campoPrecioMin,
			JTextField campoPrecioMax, JTextField campoStockMin, JTextField campoStockMax,
			JTextField campoPuntuacionMin) {

		final String texto = normalizarTexto(campoBuscar.getText());
		final String tipoSeleccionado = comboTipo.getSelectedItem() != null
				? normalizarTexto(comboTipo.getSelectedItem().toString())
				: "todos";

		final Double precioMin = leerDoubleSeguro(campoPrecioMin.getText());
		final Double precioMax = leerDoubleSeguro(campoPrecioMax.getText());
		final Double puntuacionMin = leerDoubleSeguro(campoPuntuacionMin.getText());
		final Integer stockMin = leerEnteroSeguro(campoStockMin.getText());
		final Integer stockMax = leerEnteroSeguro(campoStockMax.getText());

		final String categoriaSeleccionada = comboCategoria.getSelectedItem() != null
				? normalizarTexto(comboCategoria.getSelectedItem().toString())
				: "todas";

		sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
			@Override
			public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {

				String id = valorTexto(entry.getValue(COL_VENTA_ID));
				String nombre = valorTexto(entry.getValue(COL_VENTA_NOMBRE));
				String tipo = valorTexto(entry.getValue(COL_VENTA_TIPO));
				String categorias = valorTexto(entry.getValue(COL_VENTA_CATEGORIAS));

				Double precio = valorDouble(entry.getValue(COL_VENTA_PRECIO));
				Integer stock = valorInteger(entry.getValue(COL_VENTA_STOCK));
				Double puntuacion = valorDouble(entry.getValue(COL_VENTA_PUNTUACION));

				if (!texto.isBlank()) {
					boolean coincide = contieneTexto(id, texto) || contieneTexto(nombre, texto)
							|| contieneTexto(tipo, texto) || contieneTexto(categorias, texto);

					if (!coincide) {
						return false;
					}
				}

				if (!"todos".equals(tipoSeleccionado)) {
					if (!contieneTexto(tipo, tipoSeleccionado)) {
						return false;
					}
				}

				if (!"todas".equals(categoriaSeleccionada)) {
					if (!contieneTexto(categorias, categoriaSeleccionada)) {
						return false;
					}
				}

				if (precioMin != null && (precio == null || precio < precioMin)) {
					return false;
				}
				if (precioMax != null && (precio == null || precio > precioMax)) {
					return false;
				}

				if (stockMin != null && (stock == null || stock < stockMin)) {
					return false;
				}
				if (stockMax != null && (stock == null || stock > stockMax)) {
					return false;
				}

				if (puntuacionMin != null && (puntuacion == null || puntuacion < puntuacionMin)) {
					return false;
				}

				return true;
			}
		});
	}

	private String valorTexto(Object value) {
		return value == null ? "" : String.valueOf(value).trim();
	}

	private Double valorDouble(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		try {
			String txt = String.valueOf(value).replace("€", "").replace(",", ".").trim();
			if (txt.isBlank()) {
				return null;
			}
			return Double.parseDouble(txt);
		} catch (Exception e) {
			return null;
		}
	}

	private Integer valorInteger(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		try {
			String txt = String.valueOf(value).trim();
			if (txt.isBlank()) {
				return null;
			}
			return Integer.parseInt(txt);
		} catch (Exception e) {
			return null;
		}
	}

	private void aplicarFiltroSegundaMano(TableRowSorter<DefaultTableModel> sorter, JTextField campoBuscar,
			JTextField campoValorMin, JTextField campoValorMax, JComboBox<Object> comboEstado,
			JCheckBox checkIncluirSinValorar) {
		String texto = normalizarTexto(campoBuscar.getText());
		Double valorMin = leerDoubleSeguro(campoValorMin.getText());
		Double valorMax = leerDoubleSeguro(campoValorMax.getText());
		boolean incluirSinValorar = checkIncluirSinValorar.isSelected();
		FiltroSegundaMano filtro = new FiltroSegundaMano();
		if (valorMin != null) {
			filtro.setValorMinimo(valorMin);
		}
		if (valorMax != null) {
			filtro.setValorMaximo(valorMax);
		}
		Object sel = comboEstado.getSelectedItem();
		if (sel instanceof EstadoProducto) {
			filtro.setEstadoMinimo((EstadoProducto) sel);
		}
		sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
			@Override
			public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
				String id = String.valueOf(entry.getValue(COL_2MANO_ID));
				Producto2Mano producto = buscarProducto2ManoPorId(id);
				if (producto == null) {
					return false;
				}
				if (!texto.isBlank() && !coincideBusquedaProducto2Mano(producto, texto)) {
					return false;
				}
				if (!producto.isVisible() || producto.isBloqueado()) {
					return false;
				}
				if (producto.getValoracion() == null) {
					return incluirSinValorar;
				}
				return filtro.cumpleFiltro(producto);
			}
		});
	}

	private void escucharCambios(JTextField campo, Runnable accion) {
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

	private String normalizarTexto(String texto) {
		return texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
	}

	private Double leerDoubleSeguro(String texto) {
		if (texto == null || texto.trim().isBlank()) {
			return null;
		}
		try {
			return Double.parseDouble(texto.trim().replace(",", "."));
		} catch (Exception e) {
			return null;
		}
	}

	private Integer leerEnteroSeguro(String texto) {
		if (texto == null || texto.trim().isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(texto.trim());
		} catch (Exception e) {
			return null;
		}
	}

	private boolean contieneTexto(String base, String buscado) {
		if (buscado == null || buscado.isBlank()) {
			return true;
		}
		if (base == null) {
			return false;
		}
		return base.toLowerCase(Locale.ROOT).contains(buscado);
	}

	private boolean coincideBusquedaProductoVenta(ProductoVenta producto, String texto) {
		return contieneTexto(producto.getId(), texto) || contieneTexto(producto.getNombre(), texto)
				|| contieneTexto(producto.getClass().getSimpleName(), texto)
				|| contieneTexto(obtenerTextoCategorias(producto), texto);
	}

	private boolean coincideBusquedaProducto2Mano(Producto2Mano producto, String texto) {
		return contieneTexto(producto.getId(), texto) || contieneTexto(producto.getNombre(), texto)
				|| contieneTexto(obtenerEstadoTextoProducto2Mano(producto), texto);
	}

	private List<String> obtenerNombresCategoriasVenta() {
		TreeSet<String> nombres = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
			if (p.getCategorias() == null) {
				continue;
			}
			for (Categoria c : p.getCategorias()) {
				if (c != null && c.getNombre() != null && !c.getNombre().isBlank()) {
					nombres.add(c.getNombre().trim());
				}
			}
		}
		return new ArrayList<>(nombres);
	}

	private Categoria buscarCategoriaPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			return null;
		}
		for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
			if (p.getCategorias() == null) {
				continue;
			}
			for (Categoria c : p.getCategorias()) {
				if (c != null && c.getNombre() != null && c.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
					return c;
				}
			}
		}
		return null;
	}

	private String obtenerTextoCategorias(ProductoVenta p) {
		if (p == null || p.getCategorias() == null || p.getCategorias().isEmpty()) {
			return "-";
		}
		List<String> nombres = new ArrayList<>();
		for (Categoria c : p.getCategorias()) {
			if (c != null && c.getNombre() != null && !c.getNombre().isBlank()) {
				nombres.add(c.getNombre().trim());
			}
		}
		nombres.sort(String.CASE_INSENSITIVE_ORDER);
		return nombres.isEmpty() ? "-" : String.join(", ", nombres);
	}

	private String obtenerTipoProductoVenta(ProductoVenta p) {
		if (p == null) {
			return "";
		}

		String tipo = p.getClass().getSimpleName();

		if ("JuegoMesa".equalsIgnoreCase(tipo)) {
			return "Juego";
		}
		if ("Comic".equalsIgnoreCase(tipo)) {
			return "Comic";
		}
		if ("Figura".equalsIgnoreCase(tipo)) {
			return "Figura";
		}
		if ("Pack".equalsIgnoreCase(tipo)) {
			return "Pack";
		}

		return tipo;
	}

	private List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
		ArrayList<ProductoVenta> productos = new ArrayList<>(Tienda.getInstancia().getStockVentas());
		productos.sort(Comparator.comparingInt(ProductoVenta::getStockDisponible)
				.thenComparing(ProductoVenta::getNombre, String.CASE_INSENSITIVE_ORDER));
		return productos;
	}

	private void cargarModeloProductosVenta(DefaultTableModel modelo) {
		modelo.setRowCount(0);
		for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
			modelo.addRow(new Object[] { p.getId(), p.getNombre(), obtenerTipoProductoVenta(p),
					obtenerTextoCategorias(p), p.getPrecioOficial(), p.getStockDisponible(), p.getMediaPuntuacion() });
		}
	}

	private void recargarTablaProductos(JTable tabla) {
		DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
		cargarModeloProductosVenta(modelo);
	}

	private List<Producto2Mano> obtenerProductosSegundaManoOrdenados() {
		ArrayList<Producto2Mano> productos = new ArrayList<>(obtenerProductosSegundaMano());
		productos.sort(Comparator.comparing((Producto2Mano p) -> p.getValoracion() != null)
				.thenComparing(Producto2Mano::getNombre, String.CASE_INSENSITIVE_ORDER));
		return productos;
	}

	private List<Producto2Mano> obtenerProductosSegundaMano() {
		return new ArrayList<>(Tienda.getInstancia().buscarSegundaMano());
	}

	private void recogerProductos2Mano(Object resultado, Map<String, Producto2Mano> mapa) {
		if (resultado == null) {
			return;
		}
		if (resultado instanceof Producto2Mano) {
			Producto2Mano p = (Producto2Mano) resultado;
			if (p.getId() != null) {
				mapa.putIfAbsent(p.getId(), p);
			}
			return;
		}
		if (resultado instanceof Collection<?>) {
			for (Object o : (Collection<?>) resultado) {
				recogerProductos2Mano(o, mapa);
			}
			return;
		}
		if (resultado.getClass().isArray()) {
			int n = Array.getLength(resultado);
			for (int i = 0; i < n; i++) {
				recogerProductos2Mano(Array.get(resultado, i), mapa);
			}
		}
	}

	private Producto2Mano buscarProducto2ManoPorId(String id) {
		if (id == null || id.isBlank()) {
			return null;
		}
		for (Producto2Mano p : obtenerProductosSegundaMano()) {
			if (p.getId() != null && p.getId().equalsIgnoreCase(id.trim())) {
				return p;
			}
		}
		return null;
	}

	private Double obtenerValorTasacionProducto2Mano(Producto2Mano p) {
		if (p == null || p.getValoracion() == null) {
			return null;
		}
		return p.getValoracion().getPrecioTasacion();
	}

	private String obtenerEstadoTextoProducto2Mano(Producto2Mano p) {
		if (p == null || p.getValoracion() == null) {
			return "Sin valorar";
		}
		EstadoProducto estado = p.getValoracion().getEstadoProducto();
		return estado == null ? "Sin estado" : estado.name();
	}

	private void cargarModeloProductos2Mano(DefaultTableModel modelo) {
		modelo.setRowCount(0);
		for (Producto2Mano p : obtenerProductosSegundaManoOrdenados()) {
			modelo.addRow(new Object[] { p.getId(), p.getNombre(), obtenerValorTasacionProducto2Mano(p),
					obtenerEstadoTextoProducto2Mano(p), p.isVisible() ? "Sí" : "No", p.isBloqueado() ? "Sí" : "No" });
		}
	}

	private void recargarTablaProductos2Mano(JTable tabla) {
		DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
		cargarModeloProductos2Mano(modelo);
	}

	private ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
		ArrayList<LineaPack> lineas = new ArrayList<>();
		String[] filas = texto.split("\\r?\\n");
		for (String fila : filas) {
			if (fila == null || fila.isBlank()) {
				continue;
			}
			String[] partes = fila.split(";");
			if (partes.length != 2) {
				throw new IllegalArgumentException("Cada línea debe tener formato ID;UNIDADES");
			}
			String idProducto = partes[0].trim();
			int unidades = Integer.parseInt(partes[1].trim());
			ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
			if (producto == null) {
				throw new IllegalArgumentException("No existe producto con id " + idProducto);
			}
			lineas.add(crearLineaPack(producto, unidades));
		}
		return lineas;
	}

	private LineaPack crearLineaPack(ProductoVenta producto, int unidades) throws Exception {
		for (Constructor<?> c : LineaPack.class.getConstructors()) {
			Class<?>[] tipos = c.getParameterTypes();
			if (tipos.length == 2 && tipos[0].isAssignableFrom(producto.getClass())
					&& (tipos[1] == int.class || tipos[1] == Integer.class)) {
				return (LineaPack) c.newInstance(producto, unidades);
			}
			if (tipos.length == 2 && ProductoVenta.class.isAssignableFrom(tipos[0])
					&& (tipos[1] == int.class || tipos[1] == Integer.class)) {
				return (LineaPack) c.newInstance(producto, unidades);
			}
		}
		throw new IllegalStateException("No se encontró constructor válido para LineaPack.");
	}

	private Oferta buscarOfertaPorTexto(String texto) {
		if (texto == null || texto.isBlank()) {
			return null;
		}
		Tienda tienda = Tienda.getInstancia();
		try {
			for (Method m : tienda.getClass().getMethods()) {
				if (m.getParameterCount() != 0) {
					continue;
				}
				Object resultado = m.invoke(tienda);
				if (resultado == null) {
					continue;
				}
				if (resultado instanceof Collection<?>) {
					Oferta encontrada = buscarEnColeccion((Collection<?>) resultado, texto);
					if (encontrada != null) {
						return encontrada;
					}
				} else if (resultado.getClass().isArray()) {
					Object[] array = (Object[]) resultado;
					for (Object o : array) {
						if (o instanceof Oferta && coincideOferta((Oferta) o, texto)) {
							return (Oferta) o;
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private Oferta buscarEnColeccion(Collection<?> col, String texto) {
		for (Object o : col) {
			if (o instanceof Oferta && coincideOferta((Oferta) o, texto)) {
				return (Oferta) o;
			}
		}
		return null;
	}

	private boolean coincideOferta(Oferta oferta, String texto) {
		if (oferta == null) {
			return false;
		}
		String buscado = texto.trim().toLowerCase(Locale.ROOT);
		if (String.valueOf(oferta).toLowerCase(Locale.ROOT).contains(buscado)) {
			return true;
		}
		String[] getters = { "getId", "getCodigo", "getIdOferta" };
		for (String getter : getters) {
			try {
				Method m = oferta.getClass().getMethod(getter);
				Object valor = m.invoke(oferta);
				if (valor != null && String.valueOf(valor).trim().equalsIgnoreCase(texto.trim())) {
					return true;
				}
			} catch (Exception ignored) {
			}
		}
		return false;
	}
}