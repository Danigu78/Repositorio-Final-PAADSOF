package ventas;

import java.time.*;
import java.util.*;
import tienda.Estadistica;
import tienda.Tienda;
import tienda.TipoNotificacion;
import usuarios.Cliente;
import productos.ProductoVenta;

public class Carrito {
	private final String idCarrito;
	private final List<LineaCarrito> lineas;
	private final LocalDateTime fechaCreacion;
	private Descuento descuentoAplicado;
	private final Cliente propietario;

	/**
	 * Constructor de la clase Carrito
	 *
	 * @param propietario el cliente al que pertenece el carrito
	 */
	public Carrito(Cliente propietario) {
		Estadistica est = Estadistica.getInstancia();
		this.idCarrito = "CARRITO-" + String.valueOf(est.getnCarritos());
		est.setnCarritos(est.getnCarritos() + 1);
		this.lineas = new ArrayList<>();
		this.fechaCreacion = LocalDateTime.now();
		this.descuentoAplicado = null;
		this.propietario = propietario;
	}

	/**
	 * Añade un producto al carrito
	 *
	 * @param p        el producto que se quiere meter en el carrito
	 * @param cantidad la cantidad de unidades a añadir
	 * @return true si se añade bien, false si no se puede hacer
	 */
	public boolean añadirProducto(ProductoVenta p, int cantidad) {
		if (this.estaCaducado() == true) {
			caducar();
			return false;
		}

		if (p == null || cantidad < 1 || p.getStockDisponible() < cantidad) {
			return false;
		}

		for (LineaCarrito l : this.lineas) {
			if (l.productoPertence(p)) {
				l.setCantidad(l.getCantidad() + cantidad);
				p.setStockDisponible(p.getStockDisponible() - cantidad);
				Tienda.getInstancia().aplicarDescuentoPrioritario(this);
				return true;
			}
		}

		LineaCarrito nuevaLinea = new LineaCarrito(p, cantidad);
		this.lineas.add(nuevaLinea);
		p.setStockDisponible(p.getStockDisponible() - cantidad);
		Tienda.getInstancia().aplicarDescuentoPrioritario(this);
		return true;
	}

	/**
	 * Calcula el total del carrito teniendo en cuenta si hay descuento
	 *
	 * @return el importe final del carrito
	 */
	public double getTotal() {
		if (this.descuentoAplicado == null) {
			return calcularSubtotal();
		}

		if (this.descuentoAplicado instanceof Regalo) {
			return calcularSubtotal();
		}

		return this.descuentoAplicado.aplicarDescuento(this);
	}

	/**
	 * Elimina un producto del carrito
	 *
	 * @param p el producto que se quiere quitar
	 * @return true si se elimina correctamente, false en caso contrario
	 */
	public boolean eliminarProducto(ProductoVenta p) {
		if (this.estaCaducado() == true) {
			caducar();
			return false;
		}

		if (p == null) {
			return false;
		}

		LineaCarrito lineaAEliminar = null;

		for (LineaCarrito l : this.lineas) {
			if (l.productoPertence(p)) {
				p.setStockDisponible(p.getStockDisponible() + l.getCantidad());
				lineaAEliminar = l;
				break;
			}
		}

		if (lineaAEliminar != null) {
			this.lineas.remove(lineaAEliminar);
			Tienda.getInstancia().aplicarDescuentoPrioritario(this);
			return true;
		}

		return false;
	}

	/**
	 * Cambia la cantidad de un producto que ya está en el carrito
	 *
	 * @param p             el producto cuya cantidad se quiere modificar
	 * @param nuevaCantidad la nueva cantidad de unidades
	 * @return true si el cambio se hace correctamente, false en cualquier otro caso
	 */
	public boolean cambiarCantidadProducto(ProductoVenta p, int nuevaCantidad) {
		if (this.estaCaducado() == true) {
			caducar();
			return false;
		}

		if (p == null || nuevaCantidad < 0) {
			return false;
		}

		if (nuevaCantidad == 0) {
			return this.eliminarProducto(p);
		}

		for (LineaCarrito l : this.lineas) {
			if (l.productoPertence(p)) {
				int cantidadActual = l.getCantidad();
				int diferencia = nuevaCantidad - cantidadActual;

				if (diferencia > 0 && p.getStockDisponible() < diferencia) {
					return false;
				}

				p.setStockDisponible(p.getStockDisponible() - diferencia);
				l.setCantidad(nuevaCantidad);
				Tienda.getInstancia().aplicarDescuentoPrioritario(this);
				return true;
			}
		}

		return false;
	}

	/**
	 * Vacía el carrito y devuelve el stock a sus productos
	 */
	public void vaciarCarrito() {
		for (LineaCarrito l : this.lineas) {
			ProductoVenta p = l.getProducto();
			p.setStockDisponible(p.getStockDisponible() + l.getCantidad());
		}
		this.lineas.clear();
		this.descuentoAplicado = null;
	}

	/**
	 * Suma el subtotal de todas las líneas del carrito
	 *
	 * @return el importe acumulado antes de descuentos
	 */
	public double calcularSubtotal() {
		double suma = 0;

		for (LineaCarrito l : this.lineas) {
			suma += l.getSubtotal();
		}

		return suma;
	}

	/**
	 * Comprueba si el carrito ya ha caducado
	 *
	 * @return true si ha pasado el tiempo máximo, false si sigue activo
	 */
	public boolean estaCaducado() {
		int tiempoMax = Tienda.getInstancia().getTiempoMaxCarrito();
		if (tiempoMax == 0)
			return false;
		return LocalDateTime.now().isAfter(this.fechaCreacion.plusMinutes(tiempoMax));
	}

	/**
	 * Caduca el carrito, lo vacía y avisa al propietario si existe
	 */
	public void caducar() {
		vaciarCarrito();
		if (this.propietario != null) {
			this.propietario.recibirNotificacionTipo("Tu carrito ha caducado y los productos han sido liberados.",
					TipoNotificacion.CARRITO_CADUCADO);
			this.propietario.setCarritoActual(null);
		}
	}

	/**
	 * Devuelve el identificador del carrito
	 *
	 * @return el id del carrito
	 */
	public String getIdCarrito() {
		return this.idCarrito;
	}

	/**
	 * Recupera las líneas que tiene el carrito
	 *
	 * @return una copia de la lista de líneas
	 */
	public List<LineaCarrito> getLineas() {
		return new ArrayList<>(this.lineas);
	}

	/**
	 * Recupera la fecha de creación del carrito
	 *
	 * @return la fecha y hora en la que se creó
	 */
	public LocalDateTime getFechaCreacion() {
		return this.fechaCreacion;
	}

	/**
	 * Devuelve el descuento aplicado al carrito
	 *
	 * @return el descuento actual o null si no hay ninguno
	 */
	public Descuento getDescuentoAplicado() {
		return this.descuentoAplicado;
	}

	/**
	 * Asigna un descuento al carrito
	 *
	 * @param descuento el descuento que se quiere aplicar
	 */
	public void setDescuentoAplicado(Descuento descuento) {
		this.descuentoAplicado = descuento;
	}

	/**
	 * Indica si el carrito no tiene productos
	 *
	 * @return true si está vacío, false en caso contrario
	 */
	public boolean estaVacio() {
		return this.lineas.isEmpty();
	}

	/**
	 * Recupera el propietario del carrito
	 *
	 * @return el cliente dueño del carrito
	 */
	public Cliente getPropietario() {
		return propietario;
	}

	/**
	 * Muestra por pantalla el contenido del carrito
	 */
	public void imprimirCarrito() {
		if (lineas.isEmpty()) {
			System.out.println("  Carrito vacio.");
			return;
		}
		System.out.println("  Carrito [" + idCarrito + "]:");
		for (LineaCarrito l : lineas) {
			System.out.println("   -> " + l.getProducto().getNombre() + " x" + l.getCantidad() + " | "
					+ l.getProducto().getPrecioOficial() + "€/ud" + " | subtotal: " + l.getSubtotal() + "€");
		}
		System.out.println("  Subtotal: " + calcularSubtotal() + "€");
		System.out.println("  Descuento: " + (descuentoAplicado != null ? descuentoAplicado.getNombre() : "ninguno"));
		System.out.println("  Total: " + getTotal() + "€");
	}
}