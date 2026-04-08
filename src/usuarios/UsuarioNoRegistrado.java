package usuarios;

import java.util.*;
import tienda.*;
import productos.*;

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
		this.filtroVenta = new FiltroVenta();
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
	 * Filtra los productos del catálogo según un rango de precios específico.
	 * @param min el precio mínimo a mostrar
	 * @param max el precio máximo a mostrar
	 */
	public void filtrarPorPrecio(double min, double max) {
		filtroVenta.resetear();
		filtroVenta.setPrecioMinimo(min);
		filtroVenta.setPrecioMaximo(max);
		System.out.println("  Filtro aplicado (Invitado): " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}
	/**
	 * Busca y muestra productos que pertenecen a una categoría determinada.
	 * Si la categoría no existe en el sistema, se cancela la operación.
	 * @param nombreCategoria el nombre de la categoría por la que filtrar
	 */
	public void filtrarPorCategoria(String nombreCategoria) {
		filtroVenta.resetear();
		Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (c == null) {
			System.out.println("  Categoria '" + nombreCategoria + "' no encontrada.");
			return;
		}
		filtroVenta.añadirCategoria(c);
		System.out.println("  Filtro aplicado (Invitado): " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}
	/**
	 * Filtra el catálogo para mostrar solo productos con una valoración mínima.
	 * @param puntuacionMinima la nota mínima de corte 
	 */
	public void filtrarPorPuntuacion(double puntuacionMinima) {
		filtroVenta.resetear();
		filtroVenta.setPuntuacionMinima(puntuacionMinima);
		System.out.println("  Filtro aplicado (Invitado): " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}
	/**
	 * Realiza un filtrado avanzado combinando múltiples criterios simultáneamente.
	 * @param precioMin      el precio mínimo permitido
	 * @param precioMax      el precio máximo permitido
	 * @param puntuacionMin  la puntuación mínima requerida
	 * @param categorias     uno o varios nombres de categorías 
	 */
	public void filtrarProductos(double precioMin, double precioMax, double puntuacionMin, String... categorias) {
		filtroVenta.resetear();
		filtroVenta.setPrecioMinimo(precioMin);
		filtroVenta.setPrecioMaximo(precioMax);
		filtroVenta.setPuntuacionMinima(puntuacionMin);
		for (String nombreCat : categorias) {
			Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCat);
			if (c != null)
				filtroVenta.añadirCategoria(c);
		}
		System.out.println("  Filtro aplicado (Invitado): " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
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