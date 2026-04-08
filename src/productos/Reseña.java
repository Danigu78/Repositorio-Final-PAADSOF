package productos;

import usuarios.Cliente;
import tienda.Estadistica;
import java.time.*;

import excepciones.*;

/**
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Reseña {
	private String idReseña;
	private Cliente autor;
	private ProductoVenta producto;
	private double puntuacion; // 0-10
	private String comentario;
	private LocalDate fecha;

	/**
	 * Constructor de la clase Reseña
	 *
	 * @param autor      el cliente que escribe la reseña
	 * @param productoV  el producto sobre el que se hace la reseña
	 * @param puntuacion la nota dada al producto
	 * @param comentario el comentario que acompaña a la reseña
	 */
	public Reseña(Cliente autor, ProductoVenta productoV, double puntuacion, String comentario) {
		Estadistica est = Estadistica.getInstancia();
		this.idReseña = "RESEÑA-" + String.valueOf(est.getnReseñas());
		est.setnReseñas(est.getnReseñas() + 1);
		this.autor = autor;
		this.producto = null;

		if (puntuacion < 0 || puntuacion > 10) {
			throw new ProductoInvalidoException("La puntuación de la reseña debe estar entre 0 y 10.");
		}
		this.puntuacion = puntuacion;

		this.comentario = (comentario == null) ? "" : comentario;
		this.fecha = LocalDate.now();

		if (productoV != null) {
			productoV.addReseña(this);
		}
	}

	/**
	 * Recupera la fecha en la que se creó la reseña
	 *
	 * @return la fecha de creación
	 */
	public LocalDate getFecha() {
		return this.fecha;
	}

	/**
	 * Devuelve la puntuación de la reseña
	 *
	 * @return la nota asignada al producto
	 */
	public double getPuntuacion() {
		return this.puntuacion;
	}

	/**
	 * Asocia la reseña a un producto
	 *
	 * @param p el producto al que pertenece la reseña
	 * @return true si se asigna correctamente
	 */
	public boolean setProducto(ProductoVenta p) {
		if (p == null) {
			throw new ProductoInvalidoException("El producto de la reseña no puede ser null.");
		}
		this.producto = p;
		return true;
	}

	/**
	 * Devuelve una representación sencilla de la reseña
	 *
	 * @return un texto con el autor, la fecha, la puntuación y el comentario
	 */
	@Override
	public String toString() {
		return "[" + this.idReseña + "] " + (this.autor != null ? this.autor.getNickname() : "null") + " | "
				+ this.fecha + " | " + this.puntuacion + " | " + this.comentario + " |";
	}

	/**
	 * Recupera el autor de la reseña
	 *
	 * @return el cliente que la ha escrito
	 */
	public Cliente getAutor() {
		return autor;
	}

	/**
	 * Recupera el producto asociado a la reseña
	 *
	 * @return el producto valorado
	 */
	public ProductoVenta getProducto() {
		return producto;
	}
}
