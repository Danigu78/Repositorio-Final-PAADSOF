package utilidades;

/** Utilidades para tratar rutas de imagen de productos. */
public final class RutasImagen {

	private RutasImagen() {
	}

	public static String normalizarNombreArchivo(String rutaImagen) {
		if (rutaImagen == null || rutaImagen.trim().isBlank()) {
			return "";
		}

		String ruta = rutaImagen.trim().replace('\\', '/');
		int posicionSeparador = ruta.lastIndexOf('/');
		return posicionSeparador >= 0 ? ruta.substring(posicionSeparador + 1) : ruta;
	}
}
