package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Comparator;
import java.util.List;

import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import tienda.Tienda;
import usuarios.Empleado;
import usuarios.TipoPermisos;
import productos.EstadoProducto;
import productos.ProductoVenta;
import productos.LineaPack;
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

	private VentanaPrincipal ventana;
	private Empleado empleado;

	private CardLayout cardSecciones;
	private JPanel panelSecciones;
	private JLabel labelEmpleado;
	private JPanel barraNave;
	private JButton botonActivo;

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
		boton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
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

	private JScrollPane estilizarScroll(Component comp) {
		JScrollPane scroll = new JScrollPane(comp);
		scroll.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1));
		scroll.getViewport().setBackground(new Color(24, 24, 24));
		scroll.setBackground(new Color(24, 24, 24));
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

	private void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje);
	}

	private void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private JPanel crearPanelStock() {
		JPanel base = crearPanelBase("Gestión de Stock");
		JPanel contenido = getContenido(base);

		// Tabla principal
		JPanel bloqueTabla = crearBloque("Productos actuales");
		JTable tablaProductos = crearTablaProductos();
		JScrollPane scrollTabla = estilizarScroll(tablaProductos);
		scrollTabla.setPreferredSize(new Dimension(1050, 320));

		JLabel infoOrden = crearLabel("Listado ordenado de menor a mayor según la cantidad en stock.");
		infoOrden.setForeground(VentanaPrincipal.COLOR_TEXTO2);

		JButton botonRefrescarTabla = crearBotonAccion("Refrescar lista");

		bloqueTabla.add(infoOrden, gbcCampo(1));
		bloqueTabla.add(scrollTabla, gbcCampo(2));
		bloqueTabla.add(botonRefrescarTabla, gbcBoton(3));

		botonRefrescarTabla.addActionListener(e -> recargarTablaProductos(tablaProductos));

		// Reponer stock
		JPanel bloqueReponer = crearBloque("Reponer stock");
		JTextField campoId = crearCampo();
		JTextField campoCantidad = crearCampo();
		JButton botonReponer = crearBotonAccion("Reponer");

		bloqueReponer.add(crearLabel("ID producto"), gbcCampo(1));
		bloqueReponer.add(campoId, gbcCampo(2));
		bloqueReponer.add(crearLabel("Cantidad"), gbcCampo(3));
		bloqueReponer.add(campoCantidad, gbcCampo(4));
		bloqueReponer.add(botonReponer, gbcBoton(5));

		botonReponer.addActionListener(e -> {
			try {
				String id = campoId.getText().trim();
				int cantidad = Integer.parseInt(campoCantidad.getText().trim());
				boolean ok = empleado.reponerStockProducto(id, cantidad);
				if (ok) {
					recargarTablaProductos(tablaProductos);
					mostrarMensaje("Stock repuesto correctamente.");
					campoCantidad.setText("");
				} else {
					mostrarError("No se pudo reponer el stock.");
				}
			} catch (Exception ex) {
				mostrarError("Datos inválidos.");
			}
		});

		// Cargar desde fichero
		JPanel bloqueFichero = crearBloque("Cargar productos desde fichero");
		JTextField campoRuta = crearCampo();
		JButton botonCargar = crearBotonAccion("Cargar fichero");

		bloqueFichero.add(crearLabel("Ruta del fichero"), gbcCampo(1));
		bloqueFichero.add(campoRuta, gbcCampo(2));
		bloqueFichero.add(botonCargar, gbcBoton(3));

		botonCargar.addActionListener(e -> {
			boolean ok = empleado.cargarProductosFicheroTexto(campoRuta.getText().trim());
			if (ok) {
				recargarTablaProductos(tablaProductos);
				mostrarMensaje("Fichero procesado correctamente.");
			} else {
				mostrarError("No se pudo cargar el fichero.");
			}
		});

		contenido.add(bloqueTabla);
		contenido.add(Box.createVerticalStrut(18));
		contenido.add(bloqueReponer);
		contenido.add(Box.createVerticalStrut(18));
		contenido.add(bloqueFichero);

		return base;
	}

	private JPanel crearPanelCategorias() {
		JPanel base = crearPanelBase("Gestión de Categorías");
		JPanel contenido = getContenido(base);

		JPanel bloque = crearBloque("Añadir o eliminar producto de categoría");
		JTextField campoId = crearCampo();
		JTextField campoCategoria = crearCampo();
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
			boolean ok = empleado.añadirProductoACategoria(campoId.getText().trim(), campoCategoria.getText().trim());
			if (ok) {
				mostrarMensaje("Producto añadido a la categoría.");
			} else {
				mostrarError("No se pudo añadir el producto a la categoría.");
			}
		});

		botonEliminar.addActionListener(e -> {
			boolean ok = empleado.eliminarProductoDeCategoria(campoId.getText().trim(),
					campoCategoria.getText().trim());
			if (ok) {
				mostrarMensaje("Producto eliminado de la categoría.");
			} else {
				mostrarError("No se pudo eliminar el producto de la categoría.");
			}
		});

		contenido.add(bloque);
		return base;
	}

	private JPanel crearPanelPacks() {
		JPanel base = crearPanelBase("Gestión de Packs");
		JPanel contenido = getContenido(base);

		JPanel bloqueCrear = crearBloque("Crear pack");
		JTextField campoNombre = crearCampo();
		JTextField campoDescripcion = crearCampo();
		JTextField campoImagen = crearCampo();
		JTextField campoPrecio = crearCampo();
		JTextField campoStock = crearCampo();
		JTextArea areaLineas = crearArea();
		JButton botonCrear = crearBotonAccion("Crear pack");

		bloqueCrear.add(crearLabel("Nombre"), gbcCampo(1));
		bloqueCrear.add(campoNombre, gbcCampo(2));
		bloqueCrear.add(crearLabel("Descripción"), gbcCampo(3));
		bloqueCrear.add(campoDescripcion, gbcCampo(4));
		bloqueCrear.add(crearLabel("Imagen"), gbcCampo(5));
		bloqueCrear.add(campoImagen, gbcCampo(6));
		bloqueCrear.add(crearLabel("Precio"), gbcCampo(7));
		bloqueCrear.add(campoPrecio, gbcCampo(8));
		bloqueCrear.add(crearLabel("Stock"), gbcCampo(9));
		bloqueCrear.add(campoStock, gbcCampo(10));
		bloqueCrear.add(crearLabel("Líneas del pack (una por línea: ID;UNIDADES)"), gbcCampo(11));
		bloqueCrear.add(estilizarScroll(areaLineas), gbcCampo(12));
		bloqueCrear.add(botonCrear, gbcBoton(13));

		botonCrear.addActionListener(e -> {
			try {
				ArrayList<LineaPack> lineas = construirLineasPack(areaLineas.getText());
				boolean ok = empleado.crearPack(campoNombre.getText().trim(), campoDescripcion.getText().trim(),
						campoImagen.getText().trim(), Double.parseDouble(campoPrecio.getText().trim()),
						Integer.parseInt(campoStock.getText().trim()), lineas);
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
		JTextField campoIdProducto = crearCampo();
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
				boolean ok = empleado.añadirProductoaPack(campoIdProducto.getText().trim(),
						campoIdPack.getText().trim(), Integer.parseInt(campoUnidades.getText().trim()));
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
				boolean ok = empleado.modificarUnidadesProductoEnPack(campoIdProducto.getText().trim(),
						campoIdPack.getText().trim(), Integer.parseInt(campoUnidades.getText().trim()));
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
			boolean ok = empleado.eliminarProductoDePack(campoIdPack.getText().trim(),
					campoIdProducto.getText().trim());
			if (ok) {
				mostrarMensaje("Producto eliminado del pack.");
			} else {
				mostrarError("No se pudo eliminar el producto del pack.");
			}
		});

		botonModificarPrecio.addActionListener(e -> {
			try {
				boolean ok = empleado.modificarPrecioPack(campoIdPack.getText().trim(),
						Double.parseDouble(campoNuevoPrecio.getText().trim()));
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
			boolean ok = empleado.eliminarPack(campoIdPack.getText().trim());
			if (ok) {
				mostrarMensaje("Pack eliminado correctamente.");
			} else {
				mostrarError("No se pudo eliminar el pack.");
			}
		});

		contenido.add(bloqueCrear);
		contenido.add(Box.createVerticalStrut(18));
		contenido.add(bloqueGestion);

		return base;
	}

	private JPanel crearPanelModificar() {
		JPanel base = crearPanelBase("Modificar Productos");
		JPanel contenido = getContenido(base);

		JPanel bloque = crearBloque("Modificar descripción o imagen");
		JTextField campoId = crearCampo();
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
			boolean ok = empleado.modificarDescripcionProducto(campoId.getText().trim(),
					areaDescripcion.getText().trim());
			if (ok) {
				mostrarMensaje("Descripción modificada correctamente.");
			} else {
				mostrarError("No se pudo modificar la descripción.");
			}
		});

		botonImagen.addActionListener(e -> {
			boolean ok = empleado.modificarImagenProducto(campoId.getText().trim(), campoImagen.getText().trim());
			if (ok) {
				mostrarMensaje("Imagen modificada correctamente.");
			} else {
				mostrarError("No se pudo modificar la imagen.");
			}
		});

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
			boolean ok = empleado.prepararPedido(campoPedido.getText().trim());
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
			boolean ok = empleado.entregarPedido(campoCodigo.getText().trim());
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

		JPanel bloque = crearBloque("Tasar producto");
		JTextField campoId = crearCampo();
		JTextField campoPrecio = crearCampo();
		JComboBox<EstadoProducto> comboEstado = new JComboBox<>(EstadoProducto.values());
		comboEstado.setFont(VentanaPrincipal.FUENTE_NORMAL);
		comboEstado.setBackground(new Color(22, 22, 22));
		comboEstado.setForeground(VentanaPrincipal.COLOR_TEXTO);

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
				empleado.tasarProducto(campoId.getText().trim(), Double.parseDouble(campoPrecio.getText().trim()),
						(EstadoProducto) comboEstado.getSelectedItem());
				mostrarMensaje("Tasación enviada.");
			} catch (Exception ex) {
				mostrarError("No se pudo tasar el producto.");
			}
		});

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

		String buscado = texto.trim().toLowerCase();

		if (String.valueOf(oferta).toLowerCase().contains(buscado)) {
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

	private List<ProductoVenta> obtenerProductosOrdenadosPorStock() {
		ArrayList<ProductoVenta> productos = new ArrayList<>(Tienda.getInstancia().getStockVentas());
		productos.sort(Comparator.comparingInt(ProductoVenta::getStockDisponible)
				.thenComparing(ProductoVenta::getNombre, String.CASE_INSENSITIVE_ORDER));
		return productos;
	}

	private JTable crearTablaProductos() {
		String[] columnas = { "ID", "Nombre", "Tipo", "Precio", "Stock" };

		DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
			modelo.addRow(new Object[] { p.getId(), p.getNombre(), p.getClass().getSimpleName(),
					String.format("%.2f €", p.getPrecioOficial()), p.getStockDisponible() });
		}

		JTable tabla = new JTable(modelo);
		tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tabla.setRowHeight(30);
		tabla.setBackground(new Color(26, 26, 26));
		tabla.setForeground(VentanaPrincipal.COLOR_TEXTO);
		tabla.setGridColor(new Color(60, 60, 60));
		tabla.setSelectionBackground(VentanaPrincipal.COLOR_ACENTO);
		tabla.setSelectionForeground(new Color(18, 18, 18));
		tabla.setFillsViewportHeight(true);
		tabla.setShowVerticalLines(true);
		tabla.setShowHorizontalLines(true);

		JTableHeader header = tabla.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(55, 55, 55));
		header.setForeground(VentanaPrincipal.COLOR_ACENTO);
		header.setReorderingAllowed(false);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);

				if (isSelected) {
					c.setBackground(VentanaPrincipal.COLOR_ACENTO);
					c.setForeground(new Color(18, 18, 18));
				} else {
					c.setBackground(row % 2 == 0 ? new Color(30, 30, 30) : new Color(24, 24, 24));
					c.setForeground(VentanaPrincipal.COLOR_TEXTO);
				}

				c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
				return c;
			}
		};

		for (int i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
			tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}

		tabla.getColumnModel().getColumn(0).setPreferredWidth(90); // ID
		tabla.getColumnModel().getColumn(1).setPreferredWidth(320); // Nombre
		tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Tipo
		tabla.getColumnModel().getColumn(3).setPreferredWidth(120); // Precio
		tabla.getColumnModel().getColumn(4).setPreferredWidth(90); // Stock

		return tabla;
	}

	private void recargarTablaProductos(JTable tabla) {
		DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
		modelo.setRowCount(0);

		for (ProductoVenta p : obtenerProductosOrdenadosPorStock()) {
			modelo.addRow(new Object[] { p.getId(), p.getNombre(), p.getClass().getSimpleName(),
					String.format("%.2f €", p.getPrecioOficial()), p.getStockDisponible() });
		}
	}
}