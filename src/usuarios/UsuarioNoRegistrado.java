package usuarios;

import java.util.List;
import java.util.ArrayList;
import tienda.Estadistica;
import tienda.FiltroSegundaMano;
import tienda.Tienda;
import tienda.FiltroVenta;
import productos.Categoria;
import productos.Producto2Mano;
import productos.ProductoVenta;

/**
 * Representa a un usuario no registrado (invitado). Puede consultar productos y
 * registrarse en el sistema.
 * 
 * @author Daniel Gonzalez Ureta
 * @version 1.0
 */

public class UsuarioNoRegistrado {
	/** Identificador de sesión del usuario */
	protected String sessionId;
	/** Identificador de sesión del usuario */
	private FiltroVenta filtroVenta;

	/**
	 * Constructor del usuario no registrado. Genera un identificador de sesión
	 * único.
	 */
	public UsuarioNoRegistrado() {
		Estadistica est = Estadistica.getInstancia();
		this.sessionId = "INVITADO-" + String.valueOf(est.getnUsuarioNoRegistrado());
		est.setnUsuarioNoRegistrado(est.getnUsuarioNoRegistrado() + 1);
	}

	/**
	 * Devuelve todos los productos de venta.
	 *
	 * @return lista de productos
	 */
	public List<ProductoVenta> buscarProductos() {
		return Tienda.getInstancia().buscarProductoVenta();
	}

	/**
	 * Busca productos por nombre.
	 *
	 * @param nombre nombre del producto
	 * @return lista de productos encontrados
	 */
	public List<ProductoVenta> buscarProductosPorNombre(String nombre) {
		List<ProductoVenta> resultado = Tienda.getInstancia().buscarproductoPorNombre(nombre);
		if (resultado == null || resultado.isEmpty()) {
			System.out.println("  No se encontraron productos con el nombre '" + nombre + "'");
			return resultado;
		}
		System.out.println("  Resultados para '" + nombre + "' (" + resultado.size() + "):");
		for (ProductoVenta p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	/**
	 * Busca un producto por su id.
	 *
	 * @param id identificador del producto
	 * @return producto encontrado o null si no existe
	 */
	public ProductoVenta buscarProductoPorId(String id) {
		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(id);
		if (p == null) {
			System.out.println("  No se encontro ningun producto con id '" + id + "'");
			return null;
		}
		System.out.println("  Producto encontrado:");
		System.out.println("  " + p.resumen());
		return p;
	}

	/**
	 * Busca productos por categoría.
	 *
	 * @param nombreCategoria nombre de la categoría
	 * @return lista de productos encontrados
	 */
	public List<ProductoVenta> buscarProductosPorCategoria(String nombreCategoria) {
		List<ProductoVenta> resultado = Tienda.getInstancia().buscarProductoPorCategoria(nombreCategoria);
		if (resultado == null || resultado.isEmpty()) {
			System.out.println("  No se encontraron productos en la categoria '" + nombreCategoria + "'");
			return resultado;
		}
		System.out.println("  Resultados categoria '" + nombreCategoria + "' (" + resultado.size() + "):");
		for (ProductoVenta p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	/**
	 * Busca productos aplicando un filtro.
	 *
	 * @return lista de productos filtrados
	 */
	public List<ProductoVenta> buscarProductosVentaFiltrados() {
		List<ProductoVenta> resultado = Tienda.getInstancia().buscarProductosFiltrados(filtroVenta);
		if (resultado.isEmpty()) {
			System.out.println("  Ningun producto cumple el filtro: " + filtroVenta);
			return resultado;
		}
		for (ProductoVenta p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	/**
	 * Registra un nuevo cliente en el sistema.
	 *
	 * @param nickname nombre de usuario
	 * @param password contraseña
	 * @param dni      documento de identidad
	 * @return cliente registrado
	 */
	public Cliente registrarse(String nickname, String password, String dni) {
		return Tienda.getInstancia().registrarNuevoCliente(nickname, password, dni);
	}

	/**
	 * Devuelve el id de sesión del usuario.
	 *
	 * @return id de sesión
	 */
	public String getSessionId() {
		return sessionId;
	}
}