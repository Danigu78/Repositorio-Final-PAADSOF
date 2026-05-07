package Gui;

import java.awt.*;

import javax.swing.*;

import Gui.Controladores.ControladorStockEmpleado;
import Gui.Controladores.ResultadoOperacion;
import usuarios.Empleado;

/**
 * Panel de gestión de stock para el empleado.
 * 
 * Desde aquí se pueden consultar productos, reponer stock y cargar productos
 * desde un fichero.
 */
public class SeccionStockEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	private JTextField campoIdProducto;
	private JTextField campoUnidades;
	private JTextField campoFichero;
	private ControladorStockEmpleado controlador;

	/* La guardamos para poder refrescarla después de cambiar algo */
	private SelectorVenta tablaProductos;

	public SeccionStockEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		this.controlador = new ControladorStockEmpleado(empleado);
		construirUI();
	}

	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Gestión de Stock");
		JPanel contenido = getContenido(panelBase);

		campoIdProducto = crearCampo();
		campoUnidades = crearCampo();
		campoFichero = crearCampo();

		// La tabla es solo para mirar el stock. El ID se escribe a mano abajo.
		tablaProductos = crearSelectorProductosVenta("Productos actuales",
				"Filtra los productos para consultar el stock disponible.", true);

		// No queremos que al pinchar una fila parezca que hace algo especial.
		tablaProductos.tabla.setRowSelectionAllowed(false);
		tablaProductos.tabla.setCellSelectionEnabled(false);

		contenido.add(tablaProductos.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAccionesStock());

		add(panelBase, BorderLayout.CENTER);
	}

	private JPanel crearBloqueAccionesStock() {
		JPanel bloque = crearBloque("Acciones de stock");

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(40), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelReponerStock());
		panelAcciones.add(crearPanelCargarFichero());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	// Panel de la izquierda: sirve para sumar unidades a un producto
	private JPanel crearPanelReponerStock() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(10), 0);

		JLabel titulo = crearLabel("Ajustar unidades");
		gbc.gridy = 0;
		panel.add(titulo, gbc);

		JPanel campoId = crearCampoFormulario("ID producto", campoIdProducto);
		ajustarAnchoFormulario(campoId);
		gbc.gridy = 1;
		panel.add(campoId, gbc);

		JPanel campoCantidad = crearCampoFormulario("Cantidad", campoUnidades);
		ajustarAnchoFormulario(campoCantidad);
		gbc.gridy = 2;
		panel.add(campoCantidad, gbc);

		JButton botonSumar = crearBotonAccion("Sumar stock");
		JButton botonRestar = crearBotonSecundario("Restar stock");

		ajustarBotonStock(botonSumar);
		ajustarBotonStock(botonRestar);

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonSumar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonRestar);

		gbc.gridy = 3;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, 0, 0);
		panel.add(filaBotones, gbc);

		gbc.gridy = 4;
		gbc.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), gbc);

		botonSumar.addActionListener(e -> reponerStock());
		botonRestar.addActionListener(e -> retirarStock());

		return panel;
	}

	// Panel de la derecha: cargar productos desde un txt
	private JPanel crearPanelCargarFichero() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 0, VentanaPrincipal.escalar(10), 0);

		JLabel titulo = crearLabel("Cargar productos desde fichero");
		gbc.gridy = 0;
		panel.add(titulo, gbc);

		JPanel campoRuta = crearCampoFormulario("Ruta del fichero", campoFichero);
		ajustarAnchoFormulario(campoRuta);
		gbc.gridy = 1;
		panel.add(campoRuta, gbc);

		JButton botonExaminar = crearBotonSecundario("Abrir...");
		JButton botonCargar = crearBotonAccion("Cargar fichero");

		ajustarBotonStock(botonExaminar);
		ajustarBotonStock(botonCargar);

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);
		filaBotones.add(botonExaminar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonCargar);

		gbc.gridy = 2;
		gbc.insets = new Insets(VentanaPrincipal.escalar(2), 0, 0, 0);
		panel.add(filaBotones, gbc);

		gbc.gridy = 3;
		gbc.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), gbc);

		botonExaminar.addActionListener(e -> seleccionarFichero());
		botonCargar.addActionListener(e -> cargarFichero());

		return panel;
	}

	private void ajustarAnchoFormulario(JPanel panelFormulario) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(520), VentanaPrincipal.escalar(68));
		panelFormulario.setPreferredSize(tamano);
		panelFormulario.setMaximumSize(tamano);
		panelFormulario.setMinimumSize(tamano);
	}

	private void ajustarBotonStock(JButton boton) {
		Dimension tamano = new Dimension(VentanaPrincipal.escalar(210), VentanaPrincipal.escalar(42));
		boton.setPreferredSize(tamano);
		boton.setMaximumSize(tamano);
		boton.setMinimumSize(tamano);
	}

	private void reponerStock() {
		ResultadoOperacion resultado = controlador.reponerStock(campoIdProducto.getText(), campoUnidades.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			campoUnidades.setText("");
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private void retirarStock() {
		ResultadoOperacion resultado = controlador.retirarStock(campoIdProducto.getText(), campoUnidades.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			campoUnidades.setText("");
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}

	private void seleccionarFichero() {
		JFileChooser selectorFichero = new JFileChooser(); // Para abrir archivos del ordenador

		int opcion = selectorFichero.showOpenDialog(this);

		// Si cancela, no hacemos nada.
		if (opcion == JFileChooser.APPROVE_OPTION && selectorFichero.getSelectedFile() != null) {
			campoFichero.setText(selectorFichero.getSelectedFile().getAbsolutePath());
		}
	}

	private void cargarFichero() {
		ResultadoOperacion resultado = controlador.cargarProductosDesdeFichero(campoFichero.getText());

		if (resultado.isExito()) {
			recargarTablaProductos(tablaProductos.tabla);
			mostrarMensaje(resultado.getMensaje());
		} else {
			mostrarError(resultado.getMensaje());
		}
	}
}