package usuarios;

import java.util.*;

import productos.*;
import tienda.*;

/**
 * Representa a un usuario registrado. De esta clase derivan los principales
 * usuarios del sistema, los clientes, empleados y el gestor de la tienda.
 * 
 * @author Daniel Gonzalez Ureta
 * @version 1.0
 */
public abstract class UsuarioRegistrado {

	protected String id;
	protected String nickname;
	protected String password;
	protected boolean sesionIniciada;
	private FiltroVenta filtroVenta;
	private FiltroSegundaMano filtro2Mano;

	/**
	 * Constructor de la clase UsuarioRegistrado
	 *
	 * @param nickname el nombre con el que se identifica el usuario
	 * @param password la contraseña de acceso
	 */
	public UsuarioRegistrado(String nickname, String password) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "USERREG-" + String.valueOf(est.getnUsuarioRegistrado());
		this.nickname = nickname;
		this.password = password;
		est.setnUsuarioRegistrado(est.getnUsuarioRegistrado() + 1);
		this.sesionIniciada = false;
		this.filtro2Mano = new FiltroSegundaMano();
		this.filtroVenta = new FiltroVenta();
	}

	/**
	 * Cierra la sesión del usuario y reinicia sus filtros de búsqueda
	 */
	public void logout() {
		this.sesionIniciada = false;
		this.filtroVenta = new FiltroVenta();
		this.filtro2Mano = new FiltroSegundaMano();
		Tienda.getInstancia().getUsuariosConSesionActiva().remove(this);
		System.out.println("El usuario con id: " + id + " ha cerrado sesion correctamente.");
	}

	/**
	 * Comprueba si una contraseña cumple los requisitos mínimos
	 *
	 * @param pass la contraseña a validar
	 * @return true si es válida, false en caso contrario
	 */
	public static boolean validarPassword(String pass) {
		if (pass == null || pass.length() < 8// longitud minimo 8
				|| !pass.matches(".*[A-Z].*")// busca al menos una letra mayuscula
				|| !pass.matches(".*[a-z].*")// busca al menos una letra minuscula
				|| !pass.matches(".*\\d.*")// busca al menos un digito numerico
				|| !pass.matches(".*[^a-zA-Z0-9].*")) {// buscamos al menos unn caracter especial{
			System.out.println("La contraseña debe tener al menos 8 caracteres, "
					+ "una mayúscula, una minúscula, un número y un carácter especial.");
			return false;
		}
		return true;
	}

	/**
	 * Inicia sesión si la contraseña introducida es correcta
	 *
	 * @param password la contraseña con la que se intenta acceder
	 * @return true si el acceso se realiza bien, false si falla
	 */
	public boolean login(String password) {
		if (!this.password.equals(password)) {
			System.out.println("Contraseña Incorrecta.");
			return false;
		}
		this.sesionIniciada = true;
		Tienda.getInstancia().getUsuariosConSesionActiva().add(this);
		return true;
	}

	/**
	 * Muestra las reseñas de un producto
	 *
	 * @param p el producto del que se quieren ver las reseñas
	 * @return la lista de reseñas del producto
	 */
	public List<Reseña> verReseñasProducto(ProductoVenta p) {
		if (p == null) {
			System.out.println("El producto no puede ser null.");
			return null;
		}
		if (p.getReseñas().isEmpty()) {
			System.out.println("El producto '" + p.getNombre() + "' no tiene reseñas.");
			return new ArrayList<>();
		}
		System.out.println(" Reseñas de '" + p.getNombre() + "' " + " | Media: "
				+ String.format("%.1f", p.getMediaPuntuacion()) + "/10");
		for (Reseña r : p.getReseñas()) {
			System.out.println("  " + r);
		}
		return p.getReseñas();
	}

	/**
	 * Busca todos los productos de venta disponibles
	 *
	 * @return la lista de productos encontrados
	 */
	public List<ProductoVenta> buscarProductos() {
		return Tienda.getInstancia().buscarProductoVenta();
	}

	/**
	 * Busca productos de venta por nombre
	 *
	 * @param nombre el nombre que se quiere buscar
	 * @return la lista de productos que coinciden
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
	 * Busca un producto de venta a partir de su id
	 *
	 * @param id el id del producto
	 * @return el producto encontrado o null si no existe
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
	 * Busca productos de venta por categoría
	 *
	 * @param nombreCategoria el nombre de la categoría
	 * @return la lista de productos encontrados
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
	 * Busca productos aplicando el filtro de venta actual
	 *
	 * @return la lista de productos que cumplen el filtro
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
	 * Filtra productos de venta por rango de precio
	 *
	 * @param min el precio mínimo
	 * @param max el precio máximo
	 */
	public void filtrarPorPrecio(double min, double max) {
		filtroVenta.resetear();
		filtroVenta.setPrecioMinimo(min);
		filtroVenta.setPrecioMaximo(max);
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}

	/**
	 * Filtra productos de venta por categoría
	 *
	 * @param nombreCategoria el nombre de la categoría a aplicar
	 */
	public void filtrarPorCategoria(String nombreCategoria) {
		filtroVenta.resetear();
		Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (c == null) {
			System.out.println("  Categoria '" + nombreCategoria + "' no encontrada.");
			return;
		}
		filtroVenta.añadirCategoria(c);
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}

	/**
	 * Filtra productos de venta por puntuación mínima
	 *
	 * @param puntuacionMinima la puntuación mínima permitida
	 */
	public void filtrarPorPuntuacion(double puntuacionMinima) {
		filtroVenta.resetear();
		filtroVenta.setPuntuacionMinima(puntuacionMinima);
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}

	/**
	 * Aplica varios filtros a la vez sobre los productos de venta
	 *
	 * @param precioMin     el precio mínimo
	 * @param precioMax     el precio máximo
	 * @param puntuacionMin la puntuación mínima
	 * @param categorias    las categorías que se quieren usar en el filtro
	 */
	public List<ProductoVenta> filtrarProductos(double precioMin, double precioMax, double puntuacionMin,
			String... categorias) {
		filtroVenta.resetear();
		filtroVenta.setPrecioMinimo(precioMin);
		filtroVenta.setPrecioMaximo(precioMax);
		filtroVenta.setPuntuacionMinima(puntuacionMin);
		for (String nombreCat : categorias) {
			Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCat);
			if (c != null)
				filtroVenta.añadirCategoria(c);
		}
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		List<ProductoVenta> resultado = buscarProductosVentaFiltrados();
		filtroVenta.resetear();
		return resultado;
	}

	/**
	 * Busca todos los productos de segunda mano disponibles
	 *
	 * @return la lista de productos encontrados
	 */
	public List<Producto2Mano> buscarProductosSegundaMano() {
		List<Producto2Mano> resultado = Tienda.getInstancia().buscarSegundaMano();
		if (resultado.isEmpty()) {
			System.out.println("  No hay productos de segunda mano disponibles.");
			return resultado;
		}
		System.out.println("  Productos de segunda mano disponibles (" + resultado.size() + "):");
		for (Producto2Mano p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	/**
	 * Busca productos de segunda mano por nombre
	 *
	 * @param nombre el nombre que se quiere buscar
	 * @return la lista de coincidencias encontradas
	 */
	public List<Producto2Mano> buscarProducto2ManoNombre(String nombre) {
		List<Producto2Mano> resultado = Tienda.getInstancia().buscarSegundaManoPorNombre(nombre);
		if (resultado == null || resultado.isEmpty()) {
			System.out.println("  No se encontraron productos de segunda mano con el nombre '" + nombre + "'");
			return resultado;
		}
		System.out.println("  Resultados para '" + nombre + "' (" + resultado.size() + "):");
		for (Producto2Mano p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	/**
	 * Busca un producto de segunda mano por su id
	 *
	 * @param id el id del producto
	 * @return el producto encontrado o null si no existe
	 */
	public Producto2Mano buscarProducto2ManoPorid(String id) {
		Producto2Mano p = Tienda.getInstancia().buscarSegundaManoPorId(id);
		if (p == null) {
			System.out.println("  No se encontro ningun producto de segunda mano con id '" + id + "'");
			return null;
		}
		System.out.println("  Producto encontrado:");
		System.out.println("  " + p.resumen());
		return p;
	}

	/**
	 * Busca productos de segunda mano usando el filtro actual
	 *
	 * @return la lista de productos que cumplen el filtro
	 */
	public List<Producto2Mano> buscarProductos2ManoFiltrados() {
		List<Producto2Mano> resultado = Tienda.getInstancia().buscarSegundaManoFiltrado(filtro2Mano);
		if (resultado.isEmpty()) {
			System.out.println("  Ningun producto de segunda mano cumple el filtro: " + filtro2Mano);
			return resultado;
		}

		for (Producto2Mano p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	/**
	 * Filtra productos de segunda mano por valor
	 *
	 * @param min el valor mínimo
	 * @param max el valor máximo
	 */
	public void filtrar2ManoPorValor(double min, double max) {
		filtro2Mano.resetear();
		filtro2Mano.setValorMinimo(min);
		filtro2Mano.setValorMaximo(max);
		System.out.println("  Filtro aplicado: " + filtro2Mano);
		buscarProductos2ManoFiltrados();
		filtro2Mano.resetear();
	}

	/**
	 * Filtra productos de segunda mano por estado mínimo
	 *
	 * @param estadoMinimo el estado mínimo que se quiere exigir
	 */
	public void filtrar2ManoPorEstado(EstadoProducto estadoMinimo) {
		filtro2Mano.resetear();
		filtro2Mano.setEstadoMinimo(estadoMinimo);
		System.out.println("  Filtro aplicado: " + filtro2Mano);
		buscarProductos2ManoFiltrados();
		filtro2Mano.resetear();
	}

	/**
	 * Aplica varios filtros a la búsqueda de segunda mano
	 *
	 * @param min          el valor mínimo
	 * @param max          el valor máximo
	 * @param estadoMinimo el estado mínimo permitido
	 */
	public void filtrar2Mano(double min, double max, EstadoProducto estadoMinimo) {
		filtro2Mano.resetear();
		filtro2Mano.setValorMinimo(min);
		filtro2Mano.setValorMaximo(max);
		filtro2Mano.setEstadoMinimo(estadoMinimo);
		System.out.println("  Filtro aplicado: " + filtro2Mano);
		buscarProductos2ManoFiltrados();
		filtro2Mano.resetear();
	}

	/**
	 * Muestra la cartera visible de otro cliente
	 *
	 * @param nickname el nickname del cliente que se quiere consultar
	 * @return la lista de productos visibles de su cartera
	 */
	public List<Producto2Mano> verCarteraCliente(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			System.out.println("  El nickname no puede estar vacio.");
			return null;
		}
		Cliente c = Tienda.getInstancia().buscarClientePorNickname(nickname);
		if (c == null) {

			return new ArrayList<>();

		}
		if (c.getNickname().equalsIgnoreCase(this.nickname)) {
			System.out.println("  Para ver tu propia cartera usa verMiCartera().");
			return new ArrayList<>();
		}
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : c.getCarteraIntercambio()) {
			if (p.isVisible()) {
				resultado.add(p);
			}
		}
		if (resultado.isEmpty()) {
			System.out.println("  " + nickname + " no tiene productos visibles en su cartera.");
			return resultado;
		}
		System.out.println("  Cartera visible de " + nickname + " (" + resultado.size() + " productos):");
		for (Producto2Mano p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	/**
	 * Devuelve el nickname del usuario
	 *
	 * @return el nombre con el que se identifica
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Recupera la contraseña del usuario
	 *
	 * @return la contraseña guardada
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Cambia el nickname del usuario
	 *
	 * @param nickname el nuevo nickname
	 */
	protected void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Cambia la contraseña del usuario
	 *
	 * @param password la nueva contraseña
	 */
	protected void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Devuelve el identificador del usuario
	 *
	 * @return el id del usuario
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Indica si el usuario tiene la sesión iniciada
	 *
	 * @return true si la sesión está iniciada, false en caso contrario
	 */
	public boolean isSesionIniciada() {
		return sesionIniciada;
	}

	/**
	 * Cambia el estado de la sesión del usuario
	 *
	 * @param sesionIniciada el nuevo estado de la sesión
	 */
	public void setSesionIniciada(boolean sesionIniciada) {
		this.sesionIniciada = sesionIniciada;
	}

	/**
	 * Muestra el panel principal del usuario
	 */
	public void mostrarPanelPrincipal() {
		return;

	}

}