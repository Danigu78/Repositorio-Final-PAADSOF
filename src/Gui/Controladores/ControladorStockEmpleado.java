package Gui.Controladores;

import usuarios.Empleado;

/** Controlador de la sección de stock del empleado. */
public class ControladorStockEmpleado {

	private final Empleado empleado;

	public ControladorStockEmpleado(Empleado empleado) {
		this.empleado = empleado;
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
		return ok ? ResultadoOperacion.ok("Fichero cargado correcto")
				: ResultadoOperacion.error("Error al cargar del fichero");
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
}