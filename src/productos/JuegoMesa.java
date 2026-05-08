package productos;

import excepciones.*;

/**
 * Clase que representa un juego de mesa como un tipo específico de producto de
 * venta. Extiende de {@link ProductoVenta} e incluye información sobre el
 * número de jugadores, rangos de edad recomendados y la tipología del juego.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class JuegoMesa extends ProductoVenta {

	private static final long serialVersionUID = 1L;
	/**
	 * Número mínimo de personas necesarias para poder jugar una partida.
	 */
	private int minJugadores;
	/**
	 * Número máximo de personas que pueden participar simultáneamente en una
	 * partida.
	 */
	private int maxJugadores;
	/**
	 * Edad mínima recomendada para comprender las reglas y mecánicas del juego.
	 */
	private int minEdad;
	/**
	 * Edad máxima recomendada .
	 */
	private int maxEdad;
	/**
	 * Clasificación o género del juego .
	 */
	private String tipoJuego;

	/**
	 * Constructor de la clase JuegoMesa
	 *
	 * @param nombre          el nombre del juego de mesa
	 * @param descripcion     breve descripción del juego
	 * @param imagenRuta      la ruta de la imagen asociada al juego
	 * @param precioOficial   el precio oficial del juego
	 * @param stockDisponible la cantidad disponible en stock
	 * @param minJugadores    el número mínimo de jugadores
	 * @param maxJugadores    el número máximo de jugadores
	 * @param minEdad         la edad mínima recomendada
	 * @param maxEdad         la edad máxima recomendada
	 * @param tipoJuego       el tipo o categoría del juego
	 */
	public JuegoMesa(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			int minJugadores, int maxJugadores, int minEdad, int maxEdad, String tipoJuego) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);

		if (minJugadores <= 0) {
			throw new ProductoInvalidoException("El número mínimo de jugadores debe ser mayor que 0.");
		}
		if (maxJugadores < minJugadores) {
			throw new ProductoInvalidoException("El número máximo de jugadores no puede ser menor que el mínimo.");
		}
		if (minEdad < 0) {
			throw new ProductoInvalidoException("La edad mínima no puede ser negativa.");
		}
		if (maxEdad < minEdad) {
			throw new ProductoInvalidoException("La edad máxima no puede ser menor que la edad mínima.");
		}
		if (tipoJuego == null || tipoJuego.isBlank()) {
			throw new ProductoInvalidoException("El tipo de juego no puede estar vacío.");
		}

		this.minJugadores = minJugadores;
		this.maxJugadores = maxJugadores;
		this.minEdad = minEdad;
		this.maxEdad = maxEdad;
		this.tipoJuego = tipoJuego;
	}

	/**
	 * Devuelve un string con información del objeto relevante
	 *
	 * @return la cadena de caracteres con la información del juego de mesa
	 */
	@Override
	public String toString() {
		return super.toString() + " | MinJugadores: " + this.minJugadores + " | MaxJugadores: " + this.maxJugadores
				+ " | EdadMin: " + this.minEdad + " | EdadMax: " + this.maxEdad + " | Tipo: " + this.tipoJuego + " |";
	}

	public int getMinJugadores() {
		return minJugadores;
	}

	public void setMinJugadores(int minJugadores) {
		validarJugadores(minJugadores, this.maxJugadores);
		this.minJugadores = minJugadores;
	}

	public int getMaxJugadores() {
		return maxJugadores;
	}

	public void setMaxJugadores(int maxJugadores) {
		validarJugadores(this.minJugadores, maxJugadores);
		this.maxJugadores = maxJugadores;
	}

	public int getMinEdad() {
		return minEdad;
	}

	public void setMinEdad(int minEdad) {
		validarEdades(minEdad, this.maxEdad);
		this.minEdad = minEdad;
	}

	public int getMaxEdad() {
		return maxEdad;
	}

	public void setMaxEdad(int maxEdad) {
		validarEdades(this.minEdad, maxEdad);
		this.maxEdad = maxEdad;
	}

	public String getTipoJuego() {
		return tipoJuego;
	}

	public void setTipoJuego(String tipoJuego) {
		if (tipoJuego == null || tipoJuego.isBlank()) {
			throw new ProductoInvalidoException("El tipo de juego no puede estar vacío.");
		}
		this.tipoJuego = tipoJuego;
	}

	public void actualizarDatos(int minJugadores, int maxJugadores, int minEdad, int maxEdad, String tipoJuego) {
		validarJugadores(minJugadores, maxJugadores);
		validarEdades(minEdad, maxEdad);
		if (tipoJuego == null || tipoJuego.isBlank()) {
			throw new ProductoInvalidoException("El tipo de juego no puede estar vacío.");
		}

		this.minJugadores = minJugadores;
		this.maxJugadores = maxJugadores;
		this.minEdad = minEdad;
		this.maxEdad = maxEdad;
		this.tipoJuego = tipoJuego;
	}

	private void validarJugadores(int minJugadores, int maxJugadores) {
		if (minJugadores <= 0) {
			throw new ProductoInvalidoException("El número mínimo de jugadores debe ser mayor que 0.");
		}
		if (maxJugadores < minJugadores) {
			throw new ProductoInvalidoException("El número máximo de jugadores no puede ser menor que el mínimo.");
		}
	}

	private void validarEdades(int minEdad, int maxEdad) {
		if (minEdad < 0) {
			throw new ProductoInvalidoException("La edad mínima no puede ser negativa.");
		}
		if (maxEdad < minEdad) {
			throw new ProductoInvalidoException("La edad máxima no puede ser menor que la edad mínima.");
		}
	}
}
