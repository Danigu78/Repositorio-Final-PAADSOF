package Gui.Controladores.empleado;

import Gui.empleado.SeccionPacksEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import productos.Categoria;
import productos.LineaPack;
import productos.Pack;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;
import utilidades.RutasImagen;

/**
 * Controlador de gestión de packs.
 * 
 * @author Lucas
 * @version 1.0
 */
public class ControladorPacksEmpleado implements ActionListener {

	/** Acción para refrescar la lista de packs. */
	public static final String REFRESCAR_PACKS = "packs.refrescar";
	
	/** Acción para visualizar el contenido escrito de un pack. */
	public static final String VER_CONTENIDO = "packs.verContenido";
	
	/** Acción para limpiar el formulario de creación. */
	public static final String LIMPIAR_CREAR = "packs.limpiarCrear";
	
	/** Acción para crear un nuevo pack. */
	public static final String CREAR_PACK = "packs.crear";
	
	/** Acción para seleccionar una imagen para el pack. */
	public static final String SELECCIONAR_IMAGEN = "packs.seleccionarImagen";
	
	/** Acción para visualizar la imagen del pack. */
	public static final String VER_IMAGEN = "packs.verImagen";
	
	/** Acción para visualizar un pack concreto. */
	public static final String VER_PACK = "packs.ver";
	
	/** Acción para añadir un producto a un pack. */
	public static final String ANADIR_PRODUCTO = "packs.anadirProducto";
	
	/** Acción para cambiar las unidades de un producto dentro de un pack. */
	public static final String CAMBIAR_UNIDADES = "packs.cambiarUnidades";
	
	/** Acción para eliminar un producto de un pack. */
	public static final String QUITAR_PRODUCTO = "packs.quitarProducto";
	
	/** Acción para modificar el precio de un pack. */
	public static final String CAMBIAR_PRECIO = "packs.cambiarPrecio";
	
	/** Acción para eliminar un pack. */
	public static final String ELIMINAR_PACK = "packs.eliminar";

	/** Empleado asociado al controlador. */
	private final Empleado empleado;
	
	/** Controlador auxiliar de productos. */
	private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();
	
	/** Vista asociada al controlador. */
	private SeccionPacksEmpleado vista;

	/**
	 * Constructor del controlador.
	 *
	 * @param empleado empleado que gestionará los packs
	 */
	public ControladorPacksEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asocia la vista al controlador.
	 *
	 * @param vista vista de packs
	 */
	public void setVista(SeccionPacksEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona las acciones generadas desde la interfaz.
	 *
	 * @param e evento recibido desde la interfaz
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (REFRESCAR_PACKS.equals(accion)) {
			vista.dejarSoloPacks();
		} else if (VER_CONTENIDO.equals(accion)) {
			vista.verContenidoEscrito();
		} else if (LIMPIAR_CREAR.equals(accion)) {
			vista.limpiarFormularioCrear();
		} else if (CREAR_PACK.equals(accion)) {
			vista.crearPack();
		} else if (SELECCIONAR_IMAGEN.equals(accion)) {
			vista.seleccionarImagenPack();
		} else if (VER_IMAGEN.equals(accion)) {
			vista.verImagenPack();
		} else if (VER_PACK.equals(accion)) {
			vista.verPack();
		} else if (ANADIR_PRODUCTO.equals(accion)) {
			vista.anadirProductoAPack();
		} else if (CAMBIAR_UNIDADES.equals(accion)) {
			vista.cambiarUnidadesPack();
		} else if (QUITAR_PRODUCTO.equals(accion)) {
			vista.quitarProductoDelPack();
		} else if (CAMBIAR_PRECIO.equals(accion)) {
			vista.cambiarPrecioPack();
		} else if (ELIMINAR_PACK.equals(accion)) {
			vista.eliminarPack();
		}
	}

	/**
	 * Obtiene los nombres de todas las categorías de productos.
	 *
	 * @return lista de nombres de categorías
	 */
	public ArrayList<String> getNombresCategorias() {
		return new ArrayList<>(productos.obtenerNombresCategoriasVenta());
	}

	/**
	 * Construye las líneas de un pack a partir de un texto.
	 *
	 * @param texto texto con los productos y cantidades
	 * @return lista de líneas del pack
	 */
	public ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
		return productos.construirLineasPack(texto);
	}

	/**
	 * Busca un pack por su identificador.
	 *
	 * @param idPack identificador del pack
	 * @return pack encontrado o null si no existe
	 */
	public Pack buscarPack(String idPack) {
		if (idPack == null || idPack.trim().isBlank()) {
			return null;
		}

		ProductoVenta producto = productos.buscarProductoVentaPorId(idPack.trim());

		if (producto instanceof Pack) {
			return (Pack) producto;
		}

		return null;
	}

	/**
	 * Crea un nuevo pack de productos.
	 *
	 * @param nombre nombre del pack
	 * @param descripcion descripción del pack
	 * @param imagen ruta de la imagen
	 * @param precioTexto precio del pack en texto
	 * @param stockTexto stock disponible en texto
	 * @param textoLineas productos incluidos en el pack
	 * @param categoriasTexto categorías asociadas
	 * @return resultado de la operación
	 */
	public ResultadoOperacion crearPack(String nombre, String descripcion, String imagen, String precioTexto,
			String stockTexto, String textoLineas, String categoriasTexto) {

		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}

		if (nombre == null || nombre.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el nombre del pack.");
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
			ArrayList<LineaPack> lineas = construirLineasPack(textoLineas);

			if (lineas.size() < 2) {
				return ResultadoOperacion.error("Un pack debe tener al menos dos productos.");
			}

			ArrayList<Categoria> categorias = leerCategorias(categoriasTexto);

			boolean ok = empleado.crearPack(nombre.trim(), descripcion.trim(), normalizarRutaImagen(imagen), precio,
					stock, lineas, categorias);
			guardarSiExito(ok);

			if (ok) {
				return ResultadoOperacion.ok("Pack creado correctamente.");
			}

			return ResultadoOperacion.error("No se pudo crear el pack.");

		} catch (Exception e) {
			return ResultadoOperacion.error("No se pudo crear el pack: " + e.getMessage());
		}
	}

	/**
	 * Añade un producto a un pack existente.
	 *
	 * @param idProducto ID del producto
	 * @param idPack ID del pack
	 * @param unidadesTexto unidades a añadir
	 * @return resultado de la operación
	 */
	public ResultadoOperacion anadirProductoAPack(String idProducto, String idPack, String unidadesTexto) {
		Integer unidades = leerEntero(unidadesTexto);

		ResultadoOperacion validacion = validarProductoPack(idProducto, idPack, unidades);

		if (!validacion.isExito()) {
			return validacion;
		}

		try {
			boolean ok = empleado.añadirProductoaPack(idProducto.trim(), idPack.trim(), unidades);
			guardarSiExito(ok);

			if (ok) {
				return ResultadoOperacion.ok("Producto añadido al pack.");
			}

			return ResultadoOperacion.error("No se pudo añadir el producto.");

		} catch (Exception e) {
			return ResultadoOperacion.error("No se pudo añadir el producto: " + e.getMessage());
		}
	}

	/**
	 * Modifica las unidades de un producto dentro de un pack.
	 *
	 * @param idProducto ID del producto
	 * @param idPack ID del pack
	 * @param unidadesTexto nuevo número de unidades
	 * @return resultado de la operación
	 */
	public ResultadoOperacion cambiarUnidadesPack(String idProducto, String idPack, String unidadesTexto) {
		Integer unidades = leerEntero(unidadesTexto);

		ResultadoOperacion validacion = validarProductoPack(idProducto, idPack, unidades);

		if (!validacion.isExito()) {
			return validacion;
		}

		try {
			boolean ok = empleado.modificarUnidadesProductoEnPack(idProducto.trim(), idPack.trim(), unidades);
			guardarSiExito(ok);

			if (ok) {
				return ResultadoOperacion.ok("Unidades modificadas correctamente.");
			}

			return ResultadoOperacion.error("No se pudieron modificar las unidades.");

		} catch (Exception e) {
			return ResultadoOperacion.error("No se pudieron modificar las unidades: " + e.getMessage());
		}
	}

	/**
	 * Elimina un producto de un pack.
	 *
	 * @param idPack ID del pack
	 * @param idProducto ID del producto
	 * @return resultado de la operación
	 */
	public ResultadoOperacion quitarProductoDelPack(String idPack, String idProducto) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}

		if (idPack == null || idPack.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del pack.");
		}

		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto.");
		}

		try {
			boolean ok = empleado.eliminarProductoDePack(idPack.trim(), idProducto.trim());
			guardarSiExito(ok);

			if (ok) {
				return ResultadoOperacion.ok("Producto quitado del pack.");
			}

			return ResultadoOperacion.error("No se pudo quitar el producto.");

		} catch (Exception e) {
			return ResultadoOperacion.error("No se pudo quitar el producto: " + e.getMessage());
		}
	}

	/**
	 * Cambia el precio de un pack.
	 *
	 * @param idPack ID del pack
	 * @param precioTexto nuevo precio
	 * @return resultado de la operación
	 */
	public ResultadoOperacion cambiarPrecioPack(String idPack, String precioTexto) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}

		if (idPack == null || idPack.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del pack.");
		}

		Double precio = leerDouble(precioTexto);

		if (precio == null || precio <= 0) {
			return ResultadoOperacion.error("Escribe un precio válido.");
		}

		try {
			boolean ok = empleado.modificarPrecioPack(idPack.trim(), precio);
			guardarSiExito(ok);

			if (ok) {
				return ResultadoOperacion.ok("Precio modificado correctamente.");
			}

			return ResultadoOperacion.error("No se pudo modificar el precio.");

		} catch (Exception e) {
			return ResultadoOperacion.error("No se pudo modificar el precio: " + e.getMessage());
		}
	}

	/**
	 * Elimina un pack existente.
	 *
	 * @param idPack ID del pack
	 * @return resultado de la operación
	 */
	public ResultadoOperacion eliminarPack(String idPack) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}

		if (idPack == null || idPack.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del pack.");
		}

		try {
			boolean ok = empleado.eliminarPack(idPack.trim());
			guardarSiExito(ok);

			if (ok) {
				return ResultadoOperacion.ok("Pack eliminado correctamente.");
			}

			return ResultadoOperacion.error("No se pudo eliminar el pack.");

		} catch (Exception e) {
			return ResultadoOperacion.error("No se pudo eliminar el pack: " + e.getMessage());
		}
	}

	/**
	 * Guarda el estado de la tienda si la operación fue correcta.
	 *
	 * @param ok indica si la operación tuvo éxito
	 */
	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Genera un texto descriptivo con toda la información de un pack.
	 *
	 * @param pack pack del que se desea obtener la información
	 * @return texto descriptivo del pack o mensaje de error si no existe
	 */
	public String crearTextoPack(Pack pack) {
		if (pack == null) {
			return "No existe ningún pack con ese ID.";
		}

		StringBuilder texto = new StringBuilder();

		texto.append("Pack: ").append(pack.getId()).append(" - ").append(pack.getNombre()).append("\n");
		texto.append("Categorías: ").append(crearTextoCategorias(pack)).append("\n");
		texto.append("Precio pack: ").append(productos.formatearPrecio(pack.getPrecioOficial())).append("\n");
		texto.append("Stock pack: ").append(pack.getStockDisponible()).append("\n");
		texto.append("Productos por separado: ").append(productos.formatearPrecio(pack.calcularSumaProductos()))
				.append("\n");

		double ahorro = pack.calcularSumaProductos() - pack.getPrecioOficial();
		texto.append("Ahorro: ").append(productos.formatearPrecio(ahorro)).append("\n\n");

		texto.append("Productos incluidos:\n");

		if (pack.getLineas().isEmpty()) {
			texto.append("Sin productos.");
		} else {
			texto.append(crearTextoLineas(new ArrayList<>(pack.getLineas())));
		}

		return texto.toString();
	}

	/**
	 * Genera un texto con las líneas de productos de un pack.
	 * 
	 * @param lineas líneas del pack
	 * @return texto con la información de las líneas del pack
	 */
	public String crearTextoLineas(ArrayList<LineaPack> lineas) {
		StringBuilder texto = new StringBuilder();

		if (lineas == null || lineas.isEmpty()) {
			return "Sin productos.";
		}

		for (LineaPack linea : lineas) {
			ProductoVenta producto = linea.getProducto();

			texto.append("- ").append(producto.getId()).append(" | ");
			texto.append(producto.getNombre()).append(" | ");
			texto.append("unidades: ").append(linea.getUnidades()).append(" | ");
			texto.append("subtotal: ").append(productos.formatearPrecio(linea.getSubtotal())).append("\n");
		}

		return texto.toString();
	}

	/**
	 * Formatea un precio usando el formato estándar de productos.
	 *
	 * @param precio precio a formatear
	 * @return precio formateado
	 */
	public String formatearPrecio(double precio) {
		return productos.formatearPrecio(precio);
	}

	/**
	 * Valida los datos necesarios para modificar productos de un pack.
	 *
	 * @param idProducto identificador del producto
	 * @param idPack identificador del pack
	 * @param unidades número de unidades
	 * @return resultado de la validación
	 */
	private ResultadoOperacion validarProductoPack(String idProducto, String idPack, Integer unidades) {
		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}

		if (idPack == null || idPack.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del pack.");
		}

		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto.");
		}

		if (unidades == null || unidades <= 0) {
			return ResultadoOperacion.error("Escribe unidades válidas.");
		}

		return ResultadoOperacion.ok("Datos válidos");
	}

	/**
	 * Convierte un texto separado por comas en una lista de categorías.
	 *
	 * @param texto texto con nombres de categorías separados por comas
	 * @return lista de categorías encontradas
	 * @throws Exception si alguna categoría no existe
	 */
	private ArrayList<Categoria> leerCategorias(String texto) throws Exception {
		ArrayList<Categoria> categorias = new ArrayList<>();

		if (texto == null || texto.trim().isBlank()) {
			return categorias;
		}

		String[] partes = texto.split(",");

		for (String parte : partes) {
			String nombre = parte.trim();

			if (nombre.isBlank()) {
				continue;
			}

			Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombre);

			if (categoria == null) {
				throw new Exception("No existe la categoría: " + nombre);
			}

			if (!categorias.contains(categoria)) {
				categorias.add(categoria);
			}
		}

		return categorias;
	}

	/**
	 * Genera un texto con las categorías asociadas a un producto.
	 *
	 * @param producto producto del que se obtienen las categorías
	 * @return texto con las categorías o mensaje indicando ausencia
	 */
	private String crearTextoCategorias(ProductoVenta producto) {
		if (producto == null || producto.getCategorias() == null || producto.getCategorias().isEmpty()) {
			return "Sin categorías";
		}

		String texto = "";

		for (Categoria categoria : producto.getCategorias()) {
			if (!texto.isEmpty()) {
				texto += ", ";
			}

			texto += categoria.getNombre();
		}

		return texto;
	}


	/**
	 * Normaliza el nombre de un archivo de imagen.
	 *
	 * @param imagen ruta o nombre original de la imagen
	 * @return nombre normalizado
	 */
	private String normalizarRutaImagen(String imagen) {
		return RutasImagen.normalizarNombreArchivo(imagen);
	}

	/**
	 * Convierte un texto a entero.
	 *
	 * @param texto texto a convertir
	 * @return número entero o null si el formato es inválido
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
	 * Convierte un texto a número decimal.
	 *
	 * @param texto texto a convertir
	 * @return número decimal o null si el formato es inválido
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
