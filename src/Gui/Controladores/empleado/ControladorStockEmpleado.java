package Gui.Controladores.empleado;

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

/**
 * Controlador de la sección de stock del empleado.
 * 
 * @author Lucas
 * @version 1.0
 */
public class ControladorStockEmpleado implements ActionListener {

	/** Acción para sumar stock a un producto. */
	public static final String SUMAR_STOCK = "stock.sumar";

	/** Acción para retirar stock de un producto. */
	public static final String RESTAR_STOCK = "stock.restar";

	/** Acción para eliminar un producto. */
	public static final String ELIMINAR_PRODUCTO = "stock.eliminarProducto";

	/** Acción para seleccionar un fichero. */
	public static final String SELECCIONAR_FICHERO = "stock.seleccionarFichero";

	/** Acción para cargar productos desde un fichero. */
	public static final String CARGAR_FICHERO = "stock.cargarFichero";

	/** Acción para limpiar el formulario de producto. */
	public static final String LIMPIAR_PRODUCTO = "stock.limpiarProducto";

	/** Acción para crear un producto. */
	public static final String CREAR_PRODUCTO = "stock.crearProducto";

	/** Acción para seleccionar la imagen de un producto. */
	public static final String SELECCIONAR_IMAGEN = "stock.seleccionarImagen";

	/** Acción para visualizar la imagen de un producto. */
	public static final String VER_IMAGEN = "stock.verImagen";

	/** Empleado que realiza la gestión del stock. */
	private final Empleado empleado;

	/** Controlador auxiliar para operaciones con productos. */
	private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

	/** Vista asociada a la sección de stock. */
	private SeccionStockEmpleado vista;

	/**
	 * Crea el controlador de stock del empleado.
	 * 
	 * @param empleado empleado que gestionará el stock
	 */
	public ControladorStockEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asigna la vista asociada al controlador.
	 * 
	 * @param vista vista de gestión de stock del empleado
	 */
	public void setVista(SeccionStockEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona las acciones realizadas desde la interfaz.
	 * 
	 * @param e evento producido en la interfaz
	 */
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
		} else if (ELIMINAR_PRODUCTO.equals(accion)) {
			vista.eliminarProducto();
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

	/**
	 * Añade unidades al stock de un producto.
	 * 
	 * @param idProducto    identificador del producto
	 * @param unidadesTexto texto con las unidades a añadir
	 * @return resultado de la operación
	 */
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

	/**
	 * Retira unidades del stock de un producto.
	 * 
	 * @param idProducto    identificador del producto
	 * @param unidadesTexto texto con las unidades a retirar
	 * @return resultado de la operación
	 */
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

	/**
	 * Elimina un producto del sistema.
	 * 
	 * @param idProducto identificador del producto
	 * @return resultado de la operación
	 */
	public ResultadoOperacion eliminarProducto(String idProducto) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto");
		}

		boolean ok = empleado.eliminarProductoVenta(idProducto.trim());
		guardarSiExito(ok);

		return ok ? ResultadoOperacion.ok("Producto eliminado correctamente")
				: ResultadoOperacion.error("No se pudo eliminar el producto");
	}

	/**
	 * Carga productos desde un fichero de texto.
	 * 
	 * @param rutaFichero ruta del fichero
	 * @return resultado de la operación
	 */
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

	/**
	 * Obtiene los nombres de las categorías disponibles.
	 * 
	 * @return lista de nombres de categorías
	 */
	public List<String> getNombresCategorias() {
		return productos.obtenerNombresCategoriasVenta();
	}

	/**
	 * Crea un nuevo producto en la tienda.
	 * 
	 * @param tipo              tipo de producto
	 * @param nombre            nombre del producto
	 * @param descripcion       descripción del producto
	 * @param imagen            ruta de la imagen
	 * @param precioTexto       precio del producto
	 * @param stockTexto        stock inicial
	 * @param categoriasTexto   categorías asociadas
	 * @param numPaginasTexto   número de páginas del cómic
	 * @param editorial         editorial del cómic
	 * @param anioTexto         año de publicación
	 * @param minJugadoresTexto jugadores mínimos
	 * @param maxJugadoresTexto jugadores máximos
	 * @param minEdadTexto      edad mínima
	 * @param maxEdadTexto      edad máxima
	 * @param estilo            estilo del juego
	 * @param alturaTexto       altura de la figura
	 * @param anchoTexto        ancho de la figura
	 * @param largoTexto        largo de la figura
	 * @param material          material de la figura
	 * @param marca             marca de la figura
	 * @return resultado de la operación
	 */
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
						|| minEdad == null || minEdad <= 0 || maxEdad == null || maxEdad <= 0 || estilo == null
						|| estilo.trim().isBlank()) {
					return ResultadoOperacion.error("Completa jugadores, edades y estilo del juego.");
				}
			} else if ("F".equals(letra)) {
				altura = leerDouble(alturaTexto);
				ancho = leerDouble(anchoTexto);
				largo = leerDouble(largoTexto);
				if (altura == null || altura <= 0 || ancho == null || ancho <= 0 || largo == null || largo <= 0
						|| material == null || material.trim().isBlank() || marca == null || marca.trim().isBlank()) {
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

	/**
	 * Guarda los cambios en disco si la operación fue correcta.
	 * 
	 * @param ok indica si la operación se realizó correctamente
	 */
	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Convierte un texto separado por comas en una lista de categorías.
	 * 
	 * @param texto texto con nombres de categorías
	 * @return lista de categorías encontradas
	 */
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

	/**
	 * Obtiene la letra identificadora según el tipo de producto.
	 * 
	 * @param tipo tipo de producto
	 * @return letra identificadora del producto
	 */
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

	/**
	 * Elimina espacios sobrantes de un texto.
	 * 
	 * @param texto texto original
	 * @return texto limpio
	 */
	private String limpiar(String texto) {
		return texto == null ? null : texto.trim();
	}

	/**
	 * Normaliza la ruta de una imagen.
	 * 
	 * @param imagen ruta original de la imagen
	 * @return ruta normalizada
	 */
	private String normalizarRutaImagen(String imagen) {
		return RutasImagen.normalizarNombreArchivo(imagen);
	}

	/**
	 * Convierte un texto en un número entero.
	 * 
	 * @param texto texto a convertir
	 * @return número entero o null si no es válido
	 */
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

	/**
	 * Convierte un texto en un número decimal.
	 * 
	 * @param texto texto a convertir
	 * @return número decimal o null si no es válido
	 */
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
