package Gui.controladores.empleado;

import Gui.empleado.SeccionStockEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import productos.Categoria;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;
import utilidades.RutasImagen;

/** Controlador de la sección de stock del empleado. */
public class ControladorStockEmpleado implements ActionListener {

	public static final String SUMAR_STOCK = "stock.sumar";
	public static final String RESTAR_STOCK = "stock.restar";
	public static final String SELECCIONAR_FICHERO = "stock.seleccionarFichero";
	public static final String CARGAR_FICHERO = "stock.cargarFichero";
	public static final String LIMPIAR_PRODUCTO = "stock.limpiarProducto";
	public static final String CREAR_PRODUCTO = "stock.crearProducto";
	public static final String SELECCIONAR_IMAGEN = "stock.seleccionarImagen";
	public static final String VER_IMAGEN = "stock.verImagen";

	private final Empleado empleado;
	private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();
	private SeccionStockEmpleado vista;

	public ControladorStockEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public void setVista(SeccionStockEmpleado vista) {
		this.vista = vista;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (SUMAR_STOCK.equals(accion)) {
			vista.reponerStock();
		} else if (RESTAR_STOCK.equals(accion)) {
			vista.retirarStock();
		} else if (SELECCIONAR_FICHERO.equals(accion)) {
			vista.seleccionarFichero();
		} else if (CARGAR_FICHERO.equals(accion)) {
			vista.cargarFichero();
		} else if (LIMPIAR_PRODUCTO.equals(accion)) {
			vista.limpiarFormularioProducto();
		} else if (CREAR_PRODUCTO.equals(accion)) {
			vista.crearProducto();
		} else if (SELECCIONAR_IMAGEN.equals(accion)) {
			vista.seleccionarImagenProducto();
		} else if (VER_IMAGEN.equals(accion)) {
			vista.verImagenProducto();
		}
	}

	public ResultadoOperacion reponerStock(String idProducto, String unidadesTexto) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto");
		}

		Integer unidades = leerEntero(unidadesTexto);
		if (unidades == null || unidades <= 0) {
			return ResultadoOperacion.error("La cantidad debe ser positiva");
		}

		boolean ok = empleado.reponerStockProducto(idProducto.trim(), unidades);
		guardarSiExito(ok);
		return ok ? ResultadoOperacion.ok("Stock repuesto correctamente")
				: ResultadoOperacion.error("No se pudo reponer el stock");
	}

	public ResultadoOperacion retirarStock(String idProducto, String unidadesTexto) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}

		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto");
		}

		Integer unidades = leerEntero(unidadesTexto);

		if (unidades == null || unidades <= 0) {
			return ResultadoOperacion.error("La cantidad debe ser positiva");
		}

		boolean ok = empleado.retirarStockProducto(idProducto.trim(), unidades);
		guardarSiExito(ok);

		return ok ? ResultadoOperacion.ok("Stock retirado correctamente")
				: ResultadoOperacion.error("No se pudo retirar el stock. Comprueba que hay unidades suficientes.");
	}

	public ResultadoOperacion cargarProductosDesdeFichero(String rutaFichero) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (rutaFichero == null || rutaFichero.trim().isBlank()) {
			return ResultadoOperacion.error("No has escrito una ruta del fichero");
		}

		boolean ok = empleado.cargarProductosFicheroTexto(rutaFichero.trim());
		guardarSiExito(ok);
		return ok ? ResultadoOperacion.ok("Fichero cargado correcto")
				: ResultadoOperacion.error("Error al cargar del fichero");
	}

	public List<String> getNombresCategorias() {
		return productos.obtenerNombresCategoriasVenta();
	}

	public ResultadoOperacion crearProducto(String tipo, String nombre, String descripcion, String imagen,
			String precioTexto, String stockTexto, String categoriasTexto, String numPaginasTexto, String editorial,
			String anioTexto, String minJugadoresTexto, String maxJugadoresTexto, String minEdadTexto,
			String maxEdadTexto, String estilo, String alturaTexto, String anchoTexto, String largoTexto,
			String material, String marca) {

		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (tipo == null || tipo.trim().isBlank()) {
			return ResultadoOperacion.error("Selecciona un tipo de producto.");
		}
		if (nombre == null || nombre.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el nombre del producto.");
		}
		if (descripcion == null || descripcion.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe una descripción.");
		}
		if (imagen == null || imagen.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe la ruta de la imagen.");
		}

		Double precio = leerDouble(precioTexto);
		if (precio == null || precio <= 0) {
			return ResultadoOperacion.error("Escribe un precio válido.");
		}

		Integer stock = leerEntero(stockTexto);
		if (stock == null || stock <= 0) {
			return ResultadoOperacion.error("Escribe un stock válido.");
		}

		try {
			ArrayList<Categoria> categorias = leerCategorias(categoriasTexto);
			String letra = obtenerLetraTipo(tipo);

			Integer numPaginas = 0;
			Integer anio = 0;
			Integer minJugadores = 0;
			Integer maxJugadores = 0;
			Integer minEdad = 0;
			Integer maxEdad = 0;
			Double altura = 0.0;
			Double ancho = 0.0;
			Double largo = 0.0;

			if ("C".equals(letra)) {
				numPaginas = leerEntero(numPaginasTexto);
				anio = leerEntero(anioTexto);
				if (numPaginas == null || numPaginas <= 0 || editorial == null || editorial.trim().isBlank()
						|| anio == null || anio <= 0) {
					return ResultadoOperacion.error("Completa páginas, editorial y año del cómic.");
				}
			} else if ("J".equals(letra)) {
				minJugadores = leerEntero(minJugadoresTexto);
				maxJugadores = leerEntero(maxJugadoresTexto);
				minEdad = leerEntero(minEdadTexto);
				maxEdad = leerEntero(maxEdadTexto);
				if (minJugadores == null || minJugadores <= 0 || maxJugadores == null || maxJugadores <= 0
						|| minEdad == null || minEdad <= 0 || maxEdad == null || maxEdad <= 0
						|| estilo == null || estilo.trim().isBlank()) {
					return ResultadoOperacion.error("Completa jugadores, edades y estilo del juego.");
				}
			} else if ("F".equals(letra)) {
				altura = leerDouble(alturaTexto);
				ancho = leerDouble(anchoTexto);
				largo = leerDouble(largoTexto);
				if (altura == null || altura <= 0 || ancho == null || ancho <= 0 || largo == null || largo <= 0
						|| material == null || material.trim().isBlank()
						|| marca == null || marca.trim().isBlank()) {
					return ResultadoOperacion.error("Completa dimensiones, material y marca de la figura.");
				}
			} else {
				return ResultadoOperacion.error("Tipo de producto no válido.");
			}

			boolean ok = empleado.añadirProducto_nuevo(letra, nombre.trim(), descripcion.trim(),
					normalizarRutaImagen(imagen), precio, stock, categorias, numPaginas, limpiar(editorial), anio,
					altura, ancho, largo, limpiar(material), limpiar(marca), minJugadores, maxJugadores, minEdad,
					maxEdad, limpiar(estilo));
			guardarSiExito(ok);

			return ok ? ResultadoOperacion.ok("Producto creado correctamente.")
					: ResultadoOperacion.error("No se pudo crear el producto.");
		} catch (Exception e) {
			return ResultadoOperacion.error("No se pudo crear el producto: " + e.getMessage());
		}
	}

	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	private ArrayList<Categoria> leerCategorias(String texto) throws Exception {
		ArrayList<Categoria> categorias = new ArrayList<>();
		if (texto == null || texto.trim().isBlank()) {
			return categorias;
		}

		String[] partes = texto.split(",");
		for (String parte : partes) {
			String nombreCategoria = parte.trim();
			if (nombreCategoria.isBlank()) {
				continue;
			}

			Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
			if (categoria == null) {
				throw new Exception("No existe la categoría: " + nombreCategoria);
			}
			if (!categorias.contains(categoria)) {
				categorias.add(categoria);
			}
		}
		return categorias;
	}

	private String obtenerLetraTipo(String tipo) {
		if ("Comic".equalsIgnoreCase(tipo)) {
			return "C";
		}
		if ("Juego".equalsIgnoreCase(tipo)) {
			return "J";
		}
		if ("Figura".equalsIgnoreCase(tipo)) {
			return "F";
		}
		return "";
	}

	private String limpiar(String texto) {
		return texto == null ? null : texto.trim();
	}

	private String normalizarRutaImagen(String imagen) {
		return RutasImagen.normalizarNombreArchivo(imagen);
	}

	private Integer leerEntero(String texto) {
		if (texto == null || texto.trim().isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(texto.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Double leerDouble(String texto) {
		if (texto == null || texto.trim().isBlank()) {
			return null;
		}
		try {
			return Double.parseDouble(texto.trim().replace(",", "."));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
