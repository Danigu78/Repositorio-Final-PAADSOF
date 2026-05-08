package productos;

import usuarios.*;

import java.io.Serializable;
import java.util.List;

import excepciones.*;
import tienda.*;

/**
 * Clase que representa un producto de segunda mano aportado por un cliente.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Producto2Mano extends Producto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Resultado de la tasación oficial realizada por un empleado. */
	private Valoracion valoracion = null;

	/** Cliente que posee actualmente el artículo. */
	private Cliente propietario = null;

	/** Indica si el producto está reservado o bloqueado. */
	private boolean bloqueado = true;

	/** Indica si el producto aparece en el catálogo público de intercambios. */
	private boolean visible = false;

	/**
	 * Constructor completo de la clase Producto2Mano.
	 *
	 * @param nombre      nombre del producto
	 * @param descripcion descripción del producto
	 * @param imagenRuta  ruta de la imagen asociada
	 * @param valoracion  valoración inicial del producto
	 * @param propietario cliente propietario del producto
	 * @param bloqueado   indica si el producto está bloqueado
	 * @param visible     indica si el producto puede mostrarse
	 */
	public Producto2Mano(String nombre, String descripcion, String imagenRuta, Valoracion valoracion,
			Cliente propietario, boolean bloqueado, boolean visible) {

		this(nombre, descripcion, imagenRuta, valoracion, propietario, bloqueado, visible, null);
	}

	/**
	 * Constructor completo de la clase Producto2Mano, incluyendo categorías.
	 *
	 * @param nombre      nombre del producto
	 * @param descripcion descripción del producto
	 * @param imagenRuta  ruta de la imagen asociada
	 * @param valoracion  valoración inicial del producto
	 * @param propietario cliente propietario del producto
	 * @param bloqueado   indica si el producto está bloqueado
	 * @param visible     indica si el producto puede mostrarse
	 * @param categorias  categorías asociadas al producto
	 */
	public Producto2Mano(String nombre, String descripcion, String imagenRuta, Valoracion valoracion,
			Cliente propietario, boolean bloqueado, boolean visible, List<Categoria> categorias) {

		super(nombre, descripcion, imagenRuta);

		if (propietario == null) {
			throw new ProductoInvalidoException("El producto de segunda mano debe tener un propietario.");
		}

		Estadistica est = Estadistica.getInstancia();

		this.id = "P2M" + est.getnProducto2Mano();
		est.setnProducto2Mano(est.getnProducto2Mano() + 1);

		this.valoracion = valoracion;
		this.propietario = propietario;
		this.bloqueado = bloqueado;
		this.visible = visible;

		guardarCategorias(categorias);
	}

	/**
	 * Constructor con los datos básicos del producto.
	 *
	 * @param propietario cliente dueño del producto
	 * @param nombre      nombre del producto
	 * @param descripcion descripción del producto
	 * @param imagenRuta  ruta de la imagen asociada
	 */
	public Producto2Mano(Cliente propietario, String nombre, String descripcion, String imagenRuta) {
		this(propietario, nombre, descripcion, imagenRuta, null);
	}

	/**
	 * Constructor con los datos básicos del producto y sus categorías.
	 *
	 * @param propietario cliente dueño del producto
	 * @param nombre      nombre del producto
	 * @param descripcion descripción del producto
	 * @param imagenRuta  ruta de la imagen asociada
	 * @param categorias  categorías asociadas al producto
	 */
	public Producto2Mano(Cliente propietario, String nombre, String descripcion, String imagenRuta,
			List<Categoria> categorias) {

		super(nombre, descripcion, imagenRuta);

		if (propietario == null) {
			throw new ProductoInvalidoException("El producto de segunda mano debe tener un propietario.");
		}

		Estadistica est = Estadistica.getInstancia();

		this.id = "P2M-" + est.getnProducto2Mano();
		est.setnProducto2Mano(est.getnProducto2Mano() + 1);

		this.valoracion = null;
		this.propietario = propietario;
		this.bloqueado = true;
		this.visible = false;

		guardarCategorias(categorias);
	}

	/**
	 * Añade las categorías recibidas, evitando nulls y repetidos.
	 *
	 * @param categorias categorías que se quieren guardar
	 */
	private void guardarCategorias(List<Categoria> categorias) {
		if (categorias == null) {
			return;
		}

		for (Categoria categoria : categorias) {
			if (categoria != null && !this.categorias.contains(categoria)) {
				this.categorias.add(categoria);
			}
		}
	}

	/**
	 * Realiza la valoración del producto.
	 *
	 * @param precioTasacion precio estimado tras la valoración
	 * @param estado         estado en el que se encuentra el producto
	 * @param empleado       empleado que realiza la tasación
	 * @return true si el producto es aceptado, false si no se acepta
	 */
	public boolean valorar(double precioTasacion, EstadoProducto estado, Empleado empleado) {
		if (estado == null) {
			throw new ValoracionInvalidaException("El estado del producto no puede ser null.");
		}

		if (empleado == null) {
			throw new ValoracionInvalidaException("El empleado tasador no puede ser null.");
		}

		if (precioTasacion < 0) {
			throw new ValoracionInvalidaException("El precio de tasación no puede ser negativo.");
		}

		this.valoracion = new Valoracion(precioTasacion, estado, empleado);

		if (estado == EstadoProducto.NO_ACEPTADO) {
			this.visible = false;
			this.bloqueado = true;
			return false;
		}

		this.visible = true;
		this.bloqueado = false;
		return true;
	}

	/**
	 * Indica si el producto está bloqueado.
	 *
	 * @return true si está bloqueado, false en caso contrario
	 */
	public boolean isBloqueado() {
		return this.bloqueado;
	}

	/**
	 * Cambia el estado de bloqueo del producto.
	 *
	 * @param bloqueado nuevo valor de bloqueo
	 */
	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	/**
	 * Indica si el producto es visible.
	 *
	 * @return true si está visible, false en caso contrario
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * Cambia la visibilidad del producto.
	 *
	 * @param visible nuevo valor de visibilidad
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Recupera la valoración del producto.
	 *
	 * @return valoración actual
	 */
	public Valoracion getValoracion() {
		return this.valoracion;
	}

	/**
	 * Asigna una valoración al producto.
	 *
	 * @param valoracion nueva valoración
	 */
	public void setValoracion(Valoracion valoracion) {
		this.valoracion = valoracion;
	}

	/**
	 * Recupera el propietario del producto.
	 *
	 * @return cliente propietario
	 */
	public Cliente getPropietario() {
		return this.propietario;
	}

	/**
	 * Recupera el estado de la valoración del producto.
	 *
	 * @return estado asociado a la valoración, o null si no está valorado
	 */
	public Object getEstado() {
		if (this.valoracion == null) {
			return null;
		}

		return this.valoracion.getEstadoValoracion();
	}

	/**
	 * Devuelve las categorías en formato texto.
	 *
	 * @return texto con las categorías del producto
	 */
	private String textoCategorias() {
		if (this.categorias == null || this.categorias.isEmpty()) {
			return "ninguna";
		}

		String texto = "";

		for (Categoria categoria : this.categorias) {
			if (categoria == null) {
				continue;
			}

			if (!texto.equals("")) {
				texto += ", ";
			}

			texto += categoria.getNombre();
		}

		if (texto.isBlank()) {
			return "ninguna";
		}

		return texto;
	}

	/**
	 * Devuelve una descripción breve del producto con sus datos principales.
	 *
	 * @return texto con la información más importante
	 */
	@Override
	public String toString() {
		String nickPropietario = this.propietario != null ? this.propietario.getNickname() : "Sin propietario";

		String estadoValoracion = this.valoracion != null ? this.valoracion.getEstadoProducto().toString()
				: "Sin valorar";

		return super.toString() + " | Propietario: " + nickPropietario + " | Estado: " + estadoValoracion
				+ " | Categorías: " + textoCategorias() + " | Disponible: " + (!this.bloqueado ? "Sí" : "No") + " |";
	}

	/**
	 * Genera un resumen más completo del producto de segunda mano.
	 *
	 * @return cadena con el propietario, la valoración y su estado actual
	 */
	public String resumen() {
		String valoracionStr = valoracion == null ? "Sin valorar"
				: valoracion.getPrecioTasacion() + "€ - " + valoracion.getEstadoProducto() + " (tasado por: "
						+ valoracion.getEmpleado().getNickname() + ")";

		return "[" + id + "] " + getNombre() + " | Propietario: "
				+ (propietario != null ? propietario.getNickname() : "ninguno") + " | Valor aproximado: "
				+ valoracionStr + " | Categorías: " + textoCategorias() + " | Visible: " + visible + " | Bloqueado: "
				+ bloqueado;
	}
}