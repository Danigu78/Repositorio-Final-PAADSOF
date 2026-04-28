package productos;

import excepciones.ProductoInvalidoException;

/**
 * Clase que representa un cómic como un tipo específico de producto de venta.
 * Extiende de {@link ProductoVenta} añadiendo información bibliográfica propia
 * del formato editorial.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Comic extends ProductoVenta {

	private static final long serialVersionUID = 1L;
	/**
	 * Cantidad total de páginas que componen la obra.
	 */
	private int numeroPaginas;
	/**
	 * Nombre de la empresa editorial encargada de la publicación del cómic.
	 */
	private String editorial;
	/**
	 * Año en el que la obra fue publicada por primera vez o la edición específica.
	 */
	private int añoPublicacion;

	/**
	 * Constructor de la clase Comic
	 *
	 * @param nombre          el nombre del cómic
	 * @param descripcion     breve descripción del cómic
	 * @param imagenRuta      la ruta de la imagen asociada al cómic
	 * @param precioOficial   el precio oficial del cómic
	 * @param stockDisponible la cantidad disponible en stock
	 * @param numeroPaginas   el número de páginas del cómic
	 * @param editorial       la editorial que publica el cómic
	 * @param añoPublicacion  el año en el que fue publicado
	 */
	public Comic(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			int numeroPaginas, String editorial, int añoPublicacion) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);

		if (numeroPaginas <= 0) {
			throw new ProductoInvalidoException("El número de páginas debe ser mayor que 0.");
		}
		if (editorial == null || editorial.isBlank()) {
			throw new ProductoInvalidoException("La editorial no puede estar vacía.");
		}
		if (añoPublicacion <= 0) {
			throw new ProductoInvalidoException("El año de publicación no es válido.");
		}

		this.numeroPaginas = numeroPaginas;
		this.editorial = editorial;
		this.añoPublicacion = añoPublicacion;
	}

	/**
	 * Devuelve un string con información del objeto relevante
	 *
	 * @return la cadena de caracteres con la información del cómic
	 */
	@Override
	public String toString() {
		return super.toString() + " | Paginas : " + this.numeroPaginas + " | Editorial: " + this.editorial
				+ " AñoPublicacion: " + this.añoPublicacion + " |";
	}
}