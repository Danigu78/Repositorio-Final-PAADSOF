package productos;

import Excepcion.ProductoInvalidoException;

/**
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Figura extends ProductoVenta {
	private double altura;
	private double ancho;
	private double largo;
	private String material;
	private String marca;

	/**
	 * Constructor de la clase Figura
	 *
	 * @param nombre          el nombre de la figura
	 * @param descripcion     breve descripción de la figura
	 * @param imagenRuta      la ruta de la imagen asociada a la figura
	 * @param precioOficial   el precio oficial de la figura
	 * @param stockDisponible la cantidad disponible en stock
	 * @param altura          la altura de la figura
	 * @param ancho           el ancho de la figura
	 * @param largo           el largo de la figura
	 * @param material        el material del que está hecha la figura
	 * @param marca           la marca de la figura
	 */
	public Figura(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			double altura, double ancho, double largo, String material, String marca) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);

		if (altura <= 0) {
			throw new ProductoInvalidoException("La altura debe ser mayor que 0.");
		}
		if (ancho <= 0) {
			throw new ProductoInvalidoException("El ancho debe ser mayor que 0.");
		}
		if (largo <= 0) {
			throw new ProductoInvalidoException("El largo debe ser mayor que 0.");
		}
		if (material == null || material.isBlank()) {
			throw new ProductoInvalidoException("El material no puede estar vacío.");
		}
		if (marca == null || marca.isBlank()) {
			throw new ProductoInvalidoException("La marca no puede estar vacía.");
		}

		this.altura = altura;
		this.ancho = ancho;
		this.largo = largo;
		this.material = material;
		this.marca = marca;
	}

	/**
	 * Devuelve un string con información del objeto relevante
	 *
	 * @return la cadena de caracteres con la información de la figura
	 */
	@Override
	public String toString() {
		return super.toString() + " | Altura: " + this.altura + " | Ancho: " + this.ancho + " | Largo: " + this.largo
				+ " | Material: " + this.material + " | Marca: " + this.marca + " |";
	}
}