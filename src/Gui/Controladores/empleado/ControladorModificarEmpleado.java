package Gui.Controladores.empleado;

import Gui.empleado.SeccionModificarEmpleado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import productos.Comic;
import productos.Figura;
import productos.JuegoMesa;
import productos.Pack;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.TipoPermisos;
import utilidades.RutasImagen;

/**
 * Controlador de la sección de edición de productos
 */

public class ControladorModificarEmpleado implements ActionListener {

	/** Acción para cargar los datos de un producto. */
	public static final String CARGAR_DATOS = "modificar.cargar";
	
	/** Acción para guardar los cambios realizados. */
	public static final String GUARDAR_CAMBIOS = "modificar.guardar";
	
	/** Acción para seleccionar una imagen. */
	public static final String SELECCIONAR_IMAGEN = "modificar.seleccionarImagen";
	
	/** Acción para visualizar la imagen del producto. */
	public static final String VER_IMAGEN = "modificar.verImagen";

	/** Nombre interno para productos tipo cómic. */
	private static final String TIPO_COMIC = "Comic";
	
	/** Nombre interno para productos tipo juego de mesa. */
	private static final String TIPO_JUEGO = "Juego";
	
	/** Nombre interno para productos tipo figura. */
	private static final String TIPO_FIGURA = "Figura";
	
	/** Nombre interno para productos tipo pack. */
	private static final String TIPO_PACK = "Pack";

	/** Empleado autenticado. */
	private final Empleado empleado;
	
	/** Controlador auxiliar de productos. */
	private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();
	
	/** Vista asociada al controlador. */
	private SeccionModificarEmpleado vista;

	/**
	 * Constructor del controlador.
	 *
	 * @param empleado empleado autenticado
	 */
	public ControladorModificarEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	/**
	 * Asocia la vista al controlador.
	 *
	 * @param vista sección gráfica de modificación
	 */
	public void setVista(SeccionModificarEmpleado vista) {
		this.vista = vista;
	}

	/**
	 * Gestiona las acciones lanzadas desde la interfaz.
	 *
	 * @param e evento recibido
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (vista == null || e == null) {
			return;
		}

		String accion = e.getActionCommand();
		if (CARGAR_DATOS.equals(accion)) {
			vista.cargarDatosProducto();
		} else if (GUARDAR_CAMBIOS.equals(accion)) {
			vista.guardarCambios();
		} else if (SELECCIONAR_IMAGEN.equals(accion)) {
			vista.seleccionarImagen();
		} else if (VER_IMAGEN.equals(accion)) {
			vista.verImagenProducto();
		}
	}

	/**
	 * Busca un producto de venta por su ID.
	 *
	 * @param idProducto identificador del producto
	 * @return producto encontrado o null
	 */
	public ProductoVenta buscarProducto(String idProducto) {
		return productos.buscarProductoVentaPorId(idProducto);
	}

	/**
	 * Obtiene el tipo de un producto.
	 *
	 * @param producto producto a analizar
	 * @return nombre del tipo de producto
	 */
	public String obtenerTipoProducto(ProductoVenta producto) {
		if (producto instanceof Comic) {
			return TIPO_COMIC;
		}
		if (producto instanceof JuegoMesa) {
			return TIPO_JUEGO;
		}
		if (producto instanceof Figura) {
			return TIPO_FIGURA;
		}
		if (producto instanceof Pack) {
			return TIPO_PACK;
		}
		return "Producto";
	}

	/**
	 * Guarda los cambios realizados sobre un producto.
	 *
	 * @param idProducto identificador del producto
	 * @param nombre nuevo nombre del producto
	 * @param descripcion nueva descripción del producto
	 * @param imagen nueva imagen del producto
	 * @param paginasTexto número de páginas en formato texto
	 * @param editorial editorial del producto
	 * @param anioTexto año de publicación en formato texto
	 * @param minJugadoresTexto número mínimo de jugadores en formato texto
	 * @param maxJugadoresTexto número máximo de jugadores en formato texto
	 * @param minEdadTexto edad mínima recomendada en formato texto
	 * @param maxEdadTexto edad máxima recomendada en formato texto
	 * @param estilo tipo o estilo del juego
	 * @param alturaTexto altura de la figura en formato texto
	 * @param anchoTexto ancho de la figura en formato texto
	 * @param largoTexto largo de la figura en formato texto
	 * @param material material de la figura
	 * @param marca marca de la figura
	 * @return resultado de la operación
	 */
	public ResultadoOperacion guardarProducto(String idProducto, String nombre, String descripcion, String imagen,
			String paginasTexto, String editorial, String anioTexto, String minJugadoresTexto, String maxJugadoresTexto,
			String minEdadTexto, String maxEdadTexto, String estilo, String alturaTexto, String anchoTexto,
			String largoTexto, String material, String marca) {

		if (empleado == null) {
			return ResultadoOperacion.error("No hay empleado activo.");
		}
		if (empleado.isDespedido() || !empleado.tienePermiso(TipoPermisos.MODIFICAR_PRODUCTO)) {
			return ResultadoOperacion.error("El empleado no tiene permiso para editar productos.");
		}
		if (idProducto == null || idProducto.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el ID del producto.");
		}
		if (nombre == null || nombre.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe el nombre del producto.");
		}
		if (descripcion == null || descripcion.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe una descripción.");
		}
		if (imagen == null || imagen.trim().isBlank()) {
			return ResultadoOperacion.error("Escribe una ruta de imagen.");
		}

		ProductoVenta producto = buscarProducto(idProducto);
		if (producto == null) {
			return ResultadoOperacion.error("No existe ningún producto con ese ID.");
		}

		String imagenNormalizada = normalizarRutaImagen(imagen);

		try {
			if (producto instanceof Comic) {
				return guardarComic((Comic) producto, nombre, descripcion, imagenNormalizada, paginasTexto, editorial,
						anioTexto);
			}
			if (producto instanceof JuegoMesa) {
				return guardarJuego((JuegoMesa) producto, nombre, descripcion, imagenNormalizada, minJugadoresTexto,
						maxJugadoresTexto, minEdadTexto, maxEdadTexto, estilo);
			}
			if (producto instanceof Figura) {
				return guardarFigura((Figura) producto, nombre, descripcion, imagenNormalizada, alturaTexto, anchoTexto,
						largoTexto, material, marca);
			}

			guardarDatosBasicos(producto, nombre, descripcion, imagenNormalizada);
			guardar();
			return ResultadoOperacion.ok("Producto actualizado correctamente.");
		} catch (RuntimeException e) {
			return ResultadoOperacion.error("No se pudo actualizar el producto: " + e.getMessage());
		}
	}

	/**
	 * Guarda los datos específicos de un cómic.
	 *
	 * @param comic cómic a actualizar
	 * @return resultado de la operación
	 */
	private ResultadoOperacion guardarComic(Comic comic, String nombre, String descripcion, String imagen,
			String paginasTexto, String editorial, String anioTexto) {
		Integer paginas = leerEntero(paginasTexto);
		Integer anio = leerEntero(anioTexto);

		if (paginas == null || paginas <= 0 || editorial == null || editorial.trim().isBlank() || anio == null
				|| anio <= 0) {
			return ResultadoOperacion.error("Completa páginas, editorial y año del cómic.");
		}

		guardarDatosBasicos(comic, nombre, descripcion, imagen);
		comic.setNumeroPaginas(paginas);
		comic.setEditorial(editorial.trim());
		comic.setAñoPublicacion(anio);
		guardar();
		return ResultadoOperacion.ok("Cómic actualizado correctamente.");
	}

	/**
	 * Guarda los datos específicos de un juego de mesa.
	 *
	 * @param juego juego a actualizar
	 * @return resultado de la operación
	 */
	private ResultadoOperacion guardarJuego(JuegoMesa juego, String nombre, String descripcion, String imagen,
			String minJugadoresTexto, String maxJugadoresTexto, String minEdadTexto, String maxEdadTexto,
			String estilo) {
		Integer minJugadores = leerEntero(minJugadoresTexto);
		Integer maxJugadores = leerEntero(maxJugadoresTexto);
		Integer minEdad = leerEntero(minEdadTexto);
		Integer maxEdad = leerEntero(maxEdadTexto);

		if (minJugadores == null || minJugadores <= 0 || maxJugadores == null || maxJugadores < minJugadores
				|| minEdad == null || minEdad < 0 || maxEdad == null || maxEdad < minEdad || estilo == null
				|| estilo.trim().isBlank()) {
			return ResultadoOperacion.error("Completa jugadores, edades y estilo del juego con valores válidos.");
		}

		guardarDatosBasicos(juego, nombre, descripcion, imagen);
		juego.actualizarDatos(minJugadores, maxJugadores, minEdad, maxEdad, estilo.trim());
		guardar();
		return ResultadoOperacion.ok("Juego actualizado correctamente.");
	}

	/**
	 * Guarda los datos específicos de una figura.
	 *
	 * @param figura figura a actualizar
	 * @return resultado de la operación
	 */
	private ResultadoOperacion guardarFigura(Figura figura, String nombre, String descripcion, String imagen,
			String alturaTexto, String anchoTexto, String largoTexto, String material, String marca) {
		Double altura = leerDouble(alturaTexto);
		Double ancho = leerDouble(anchoTexto);
		Double largo = leerDouble(largoTexto);

		if (altura == null || altura <= 0 || ancho == null || ancho <= 0 || largo == null || largo <= 0
				|| material == null || material.trim().isBlank() || marca == null || marca.trim().isBlank()) {
			return ResultadoOperacion.error("Completa dimensiones, material y marca de la figura.");
		}

		guardarDatosBasicos(figura, nombre, descripcion, imagen);
		figura.setAltura(altura);
		figura.setAncho(ancho);
		figura.setLargo(largo);
		figura.setMaterial(material.trim());
		figura.setMarca(marca.trim());
		guardar();
		return ResultadoOperacion.ok("Figura actualizada correctamente.");
	}

	/**
	 * Actualiza los datos comunes de un producto.
	 *
	 * @param producto producto a modificar
	 * @param nombre nuevo nombre
	 * @param descripcion nueva descripción
	 * @param imagen nueva imagen
	 */
	private void guardarDatosBasicos(ProductoVenta producto, String nombre, String descripcion, String imagen) {
		producto.setNombre(nombre.trim());
		producto.setDescripcion(descripcion.trim());
		producto.setImagenRuta(imagen);
	}

	/**
	 * Guarda permanentemente el estado de la tienda.
	 */
	private void guardar() {
		GuardadoTienda.guardar(Tienda.getInstancia());
	}

	/**
	 * Normaliza la ruta de una imagen.
	 *
	 * @param imagen ruta original
	 * @return ruta normalizada
	 */
	private String normalizarRutaImagen(String imagen) {
		return RutasImagen.normalizarNombreArchivo(imagen);
	}


	/**
	 * Convierte un texto a entero.
	 *
	 * @param texto texto recibido
	 * @return entero convertido o null si es inválido
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
	 * @param texto texto recibido
	 * @return número convertido o null si es inválido
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
