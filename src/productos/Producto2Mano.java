package productos;

import Excepcion.ProductoInvalidoException;
import Excepcion.ValoracionInvalidaException;
import usuarios.Cliente;
import usuarios.Empleado;
import tienda.Estadistica;

public class Producto2Mano extends Producto {
	private Valoracion valoracion = null;
	private Cliente propietario = null;
	private boolean bloqueado = true;
	private boolean visible = false;

	/**
	 * Constructor de la clase Producto2Mano
	 *
	 * @param nombre      el nombre del producto
	 * @param descripcion una breve descripción del producto
	 * @param imagenRuta  la ruta de la imagen asociada
	 * @param valoracion  la valoración inicial del producto
	 * @param propietario el cliente propietario del producto
	 * @param bloqueado   indica si el producto está bloqueado
	 * @param visible     indica si el producto puede mostrarse
	 */
	public Producto2Mano(String nombre, String descripcion, String imagenRuta, Valoracion valoracion,
			Cliente propietario, boolean bloqueado, boolean visible) {
		super(nombre, descripcion, imagenRuta);

		if (propietario == null) {
			throw new ProductoInvalidoException("El producto de segunda mano debe tener un propietario.");
		}

		Estadistica est = Estadistica.getInstancia();
		this.id = "P2M-" + String.valueOf(est.getnProducto2Mano());
		this.valoracion = valoracion;
		this.propietario = propietario;
		this.bloqueado = bloqueado;
		this.visible = visible;
		est.setnProducto2Mano(est.getnProducto2Mano() + 1);
	}

	/**
	 * Constructor de la clase Producto2Mano con los datos básicos
	 *
	 * @param propietario el dueño del producto
	 * @param nombre      el nombre del producto
	 * @param descripcion una breve descripción del producto
	 * @param imagenRuta  la ruta de la imagen asociada
	 */
	public Producto2Mano(Cliente propietario, String nombre, String descripcion, String imagenRuta) {
		super(nombre, descripcion, imagenRuta);

		if (propietario == null) {
			throw new ProductoInvalidoException("El producto de segunda mano debe tener un propietario.");
		}

		Estadistica est = Estadistica.getInstancia();
		this.id = "P2M" + String.valueOf(est.getnProducto2Mano());
		est.setnProducto2Mano(est.getnProducto2Mano() + 1);
		this.valoracion = null;
		this.propietario = propietario;
		this.bloqueado = true;
		this.visible = false;
	}

	/**
	 * Realiza la valoración del producto
	 *
	 * @param precioTasacion el precio estimado tras la valoración
	 * @param estado         el estado en el que se encuentra el producto
	 * @param empleado       el empleado que realiza la tasación
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
		this.bloqueado = true;
		return true;
	}

	/**
	 * Indica si el producto está bloqueado
	 *
	 * @return true si está bloqueado, false en caso contrario
	 */
	public boolean isBloqueado() {
		return this.bloqueado;
	}

	/**
	 * Recupera la valoración del producto
	 *
	 * @return la valoración actual
	 */
	public Valoracion getValoracion() {
		return this.valoracion;
	}

	/**
	 * Cambia el estado de bloqueo del producto
	 *
	 * @param bloqueado el nuevo valor de bloqueo
	 */
	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	/**
	 * Indica si el producto es visible
	 *
	 * @return true si está visible, false en caso contrario
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * Recupera el propietario del producto
	 *
	 * @return el cliente propietario
	 */
	public Cliente getPropietario() {
		return this.propietario;
	}

	/**
	 * Asigna una valoración al producto
	 *
	 * @param v la nueva valoración
	 */
	public void setValoracion(Valoracion v) {
		this.valoracion = v;
	}

	/**
	 * Cambia la visibilidad del producto
	 *
	 * @param v el nuevo valor de visibilidad
	 */
	public void setVisible(boolean v) {
		this.visible = v;
	}

	/**
	 * Devuelve una descripción breve del producto con sus datos principales
	 *
	 * @return un texto con la información más importante
	 */
	@Override
	public String toString() {
		String nickPropietario = (this.propietario != null) ? this.propietario.getNickname() : "Sin propietario";
		String estadoValoracion = (this.valoracion != null) ? this.valoracion.getEstadoProducto().toString()
				: "Sin valorar";

		return super.toString() + " | Propietario: " + nickPropietario + " | Estado: " + estadoValoracion
				+ " | Disponible: " + (!this.bloqueado ? "Sí" : "No") + " |";
	}

	/**
	 * Genera un resumen más completo del producto de segunda mano
	 *
	 * @return una cadena con el propietario, la valoración y su estado actual
	 */
	public String resumen() {
		String valoracionStr = (valoracion == null) ? "Sin valorar"
				: valoracion.getPrecioTasacion() + "€ - " + valoracion.getEstadoProducto() + " (tasado por: "
						+ valoracion.getEmpleado().getNickname() + ")";
		return "[" + id + "] " + getNombre() + " | Propietario: "
				+ (propietario != null ? propietario.getNickname() : "ninguno") + " | Valor aproximado: "
				+ valoracionStr + " | Visible: " + visible + " | Bloqueado: " + bloqueado;
	}

	/**
	 * Recupera el estado de la valoración del producto
	 *
	 * @return el estado asociado a la valoración
	 */
	public Object getEstado() {
		return this.valoracion.getEstadoValoracion();
	}

	
}