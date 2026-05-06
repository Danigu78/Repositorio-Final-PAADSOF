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

		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelReponerStock());
		panelAcciones.add(crearPanelCargarFichero());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	// Panel de la izquierda: sirve para sumar unidades a un producto
	private JPanel crearPanelReponerStock() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Reponer unidades");
		panel.add(titulo);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearCampoFormulario("ID producto", campoIdProducto));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(10)));

		panel.add(crearCampoFormulario("Cantidad a añadir", campoUnidades));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));

		JButton botonReponer = crearBotonAccion("Reponer stock");

		JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBoton.setOpaque(false);
		filaBoton.add(botonReponer);

		panel.add(filaBoton);

		botonReponer.addActionListener(e -> reponerStock());

		return panel;
	}

	// Panel de la derecha: cargar productos desde un txt
	private JPanel crearPanelCargarFichero() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel titulo = crearLabel("Cargar productos desde fichero");
		panel.add(titulo);
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(8)));

		panel.add(crearCampoFormulario("Ruta del fichero", campoFichero));
		panel.add(Box.createVerticalStrut(VentanaPrincipal.escalar(12)));

		JButton botonExaminar = crearBotonSecundario("Abrir...");
		JButton botonCargar = crearBotonAccion("Cargar fichero");

		JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBotones.setOpaque(false);

		filaBotones.add(botonExaminar);
		filaBotones.add(Box.createHorizontalStrut(VentanaPrincipal.escalar(10)));
		filaBotones.add(botonCargar);

		panel.add(filaBotones);

		botonExaminar.addActionListener(e -> seleccionarFichero());
		botonCargar.addActionListener(e -> cargarFichero());

		return panel;
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