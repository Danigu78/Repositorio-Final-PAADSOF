package tienda;

import java.io.*;

/**
 * Clase encargada de guardar y cargar los datos de la tienda.
 */
public class GuardadoTienda {

	private static final String RUTA_FICHERO = "datos_tienda.dat";

	/**
	 * Guarda el estado actual de la tienda en un fichero.
	 *
	 * @param tienda tienda que se quiere guardar
	 */
	public static void guardar(Tienda tienda) {
		if (tienda == null) {
			return;
		}

		try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(RUTA_FICHERO))) {
			salida.writeObject(tienda);
		} catch (IOException e) {
			System.out.println("No se pudo guardar la tienda: " + e.getMessage());
		}
	}

	/**
	 * Carga la tienda desde fichero. Si no existe ningún fichero guardado, devuelve
	 * la tienda actual.
	 *
	 * @return tienda cargada o una nueva instancia si no hay datos guardados
	 */
	public static Tienda cargar() {
		File fichero = new File(RUTA_FICHERO);

		if (!fichero.exists()) {
			return Tienda.getInstancia();
		}

		try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(fichero))) {
			Tienda tienda = (Tienda) entrada.readObject();
			Tienda.setInstancia(tienda);
			return tienda;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("No se pudo cargar la tienda: " + e.getMessage());
			return Tienda.getInstancia();
		}
	}
}
