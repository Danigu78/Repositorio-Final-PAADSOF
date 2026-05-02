package Gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import usuarios.Empleado;

/**
 * Panel de gestión de stock para el empleado.
 * 
 * Desde aquí el empleado puede ver los productos de la tienda, reponer unidades
 * de un producto concreto y cargar productos nuevos desde un fichero de texto.
 */
public class SeccionStockEmpleado extends AbstractPanelEmpleadoVentaSection {

	private static final long serialVersionUID = 1L;

	/* Campos que se usan en la parte de reponer stock */
	private JTextField campoIdProducto;
	private JTextField campoUnidades;

	/* Aquí se guarda la ruta del fichero elegido por el empleado */
	private JTextField campoFichero;

	/*
	 * Selector común de productos de venta. Nos interesa guardarlo para poder
	 * refrescar la tabla después de hacer cambios.
	 */
	private SelectorVenta selectorProductos;

	/**
	 * Crea la sección de stock asociada a una ventana y a un empleado concreto.
	 *
	 * @param ventana  ventana principal de la aplicación
	 * @param empleado empleado que está usando esta pantalla
	 */
	public SeccionStockEmpleado(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana, empleado);
		construirUI();
	}

	/**
	 * Monta toda la interfaz de esta pantalla.
	 */
	private void construirUI() {
		setLayout(new BorderLayout());

		JPanel panelBase = crearPanelBase("Gestión de Stock");
		JPanel contenido = getContenido(panelBase);

		// Creamos los campos una vez y luego los reutilizamos en los paneles.
		campoIdProducto = crearCampo();
		campoUnidades = crearCampo();
		campoFichero = crearCampo();

		/*
		 * Esta tabla ya viene preparada desde la clase padre. Además, al pulsar una
		 * fila, mete el ID del producto en campoIdProducto.
		 */
		selectorProductos = crearSelectorProductosVenta("Productos actuales",
				"Filtra los productos y pulsa una fila para cargar su ID.", true, campoIdProducto);

		contenido.add(selectorProductos.bloque);
		contenido.add(Box.createVerticalStrut(VentanaPrincipal.escalar(18)));
		contenido.add(crearBloqueAccionesStock());

		add(panelBase, BorderLayout.CENTER);
	}

	/**
	 * Crea el bloque inferior de acciones.
	 * 
	 * Está dividido en dos columnas: una para reponer stock y otra para cargar
	 * productos desde fichero.
	 *
	 * @return panel con las acciones de stock
	 */
	private JPanel crearBloqueAccionesStock() {
		JPanel bloque = crearBloque("Acciones de stock");

		// Dos columnas sencillas: izquierda reponer, derecha cargar fichero.
		JPanel panelAcciones = new JPanel(new GridLayout(1, 2, VentanaPrincipal.escalar(25), 0));
		panelAcciones.setOpaque(false);

		panelAcciones.add(crearPanelReponerStock());
		panelAcciones.add(crearPanelCargarFichero());

		bloque.add(panelAcciones, gbcCampo(1));

		return bloque;
	}

	/**
	 * Crea la parte visual para reponer unidades de un producto.
	 *
	 * @return panel de reposición de stock
	 */
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

		// Metemos el botón en una fila para que no se estire raro.
		JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		filaBoton.setOpaque(false);
		filaBoton.add(botonReponer);

		panel.add(filaBoton);

		botonReponer.addActionListener(e -> reponerStock());

		return panel;
	}

	/**
	 * Crea la zona para seleccionar y cargar un fichero de productos.
	 *
	 * @return panel de carga de fichero
	 */
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

	/**
	 * Intenta reponer stock del producto indicado.
	 */
	private void reponerStock() {
		String idProducto = campoIdProducto.getText().trim();
		Integer unidades = leerEnteroSeguro(campoUnidades.getText());

		// Sin ID no sabemos qué producto tocar.
		if (idProducto.isBlank()) {
			mostrarError("Escribe el ID del producto");
			return;
		}

		// Aquí evitamos cantidades negativas, cero o texto raro.
		if (unidades == null || unidades <= 0) {
			mostrarError("La cantidad debe ser positiva");
			return;
		}

		boolean repuesto = empleado.reponerStockProducto(idProducto, unidades);

		if (repuesto) {
			recargarTablaProductos(selectorProductos.tabla);
			campoUnidades.setText("");
			mostrarMensaje("Stock repuesto correctamente");
		} else {
			mostrarError("No se pudo reponer el stock");
		}
	}

	/**
	 * Abre el explorador de archivos para elegir el fichero de productos.
	 */
	private void seleccionarFichero() {
		JFileChooser selectorFichero = new JFileChooser(); // Permite abrir ficheros del sistema

		int opcion = selectorFichero.showOpenDialog(this);

		// Si el usuario cancela, simplemente no hacemos nada.
		if (opcion == JFileChooser.APPROVE_OPTION && selectorFichero.getSelectedFile() != null) {
			campoFichero.setText(selectorFichero.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Carga los productos del fichero escrito o seleccionado.
	 */
	private void cargarFichero() {
		String rutaFichero = campoFichero.getText().trim();

		if (rutaFichero.isBlank()) {
			mostrarError("No has escrito una ruta del fichero");
			return;
		}

		/*
		 * La lógica de verdad está en Empleado. Aquí solo validamos un poco, llamamos
		 * al método y actualizamos la pantalla.
		 */
		boolean cargado = empleado.cargarProductosFicheroTexto(rutaFichero);

		if (cargado) {
			recargarTablaProductos(selectorProductos.tabla);
			mostrarMensaje("Fichero cargado correcto");
		} else {
			mostrarError("Error al cargar del fichero");
		}
	}
}