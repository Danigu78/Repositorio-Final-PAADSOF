package Gui.Controladores;

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

/** Controlador de la sección de edición de productos. */
public class ControladorModificarEmpleado {

	private static final String TIPO_COMIC = "Comic";
	private static final String TIPO_JUEGO = "Juego";
	private static final String TIPO_FIGURA = "Figura";
	private static final String TIPO_PACK = "Pack";

	private final Empleado empleado;
	private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

	public ControladorModificarEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public ProductoVenta buscarProducto(String idProducto) {
		return productos.buscarProductoVentaPorId(idProducto);
	}

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

	public ResultadoOperacion guardarProducto(String idProducto, String nombre, String descripcion, String imagen,
			String paginasTexto, String editorial, String anioTexto, String minJugadoresTexto,
			String maxJugadoresTexto, String minEdadTexto, String maxEdadTexto, String estilo, String alturaTexto,
			String anchoTexto, String largoTexto, String material, String marca) {

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

	private void guardarDatosBasicos(ProductoVenta producto, String nombre, String descripcion, String imagen) {
		producto.setNombre(nombre.trim());
		producto.setDescripcion(descripcion.trim());
		producto.setImagenRuta(imagen);
	}

	private void guardar() {
		GuardadoTienda.guardar(Tienda.getInstancia());
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
