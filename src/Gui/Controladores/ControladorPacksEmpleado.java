package Gui.Controladores;

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
 */
public class ControladorPacksEmpleado {

	private final Empleado empleado;
	private final ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

	public ControladorPacksEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public ArrayList<String> getNombresCategorias() {
		return new ArrayList<>(productos.obtenerNombresCategoriasVenta());
	}

	public ArrayList<LineaPack> construirLineasPack(String texto) throws Exception {
		return productos.construirLineasPack(texto);
	}

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

	private void guardarSiExito(boolean ok) {
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	public String crearTextoPack(Pack pack) {
		if (pack == null) {
			return "No existe ningún pack con ese ID.";
		}

		StringBuilder texto = new StringBuilder();

		texto.append("Pack: ").append(pack.getId()).append(" - ").append(pack.getNombre()).append("\n");
		texto.append("Categorías: ").append(crearTextoCategorias(pack)).append("\n");
		texto.append("Precio pack: ").append(productos.formatearPrecio(pack.getPrecioOficial())).append("\n");
		texto.append("Stock pack: ").append(pack.getStockDisponible()).append("\n");
		texto.append("Productos por separado: ")
				.append(productos.formatearPrecio(pack.calcularSumaProductos())).append("\n");

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

	public String formatearPrecio(double precio) {
		return productos.formatearPrecio(precio);
	}

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
