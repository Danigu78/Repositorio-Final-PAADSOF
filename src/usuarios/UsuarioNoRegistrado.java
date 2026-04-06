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

public class UsuarioNoRegistrado {
	protected String sessionId;
	private FiltroVenta filtroVenta;
	

	public UsuarioNoRegistrado() {
		Estadistica est = Estadistica.getInstancia();
		this.sessionId = "INVITADO-" + String.valueOf(est.getnUsuarioNoRegistrado());
		est.setnUsuarioNoRegistrado(est.getnUsuarioNoRegistrado() + 1);
	}

	public List<ProductoVenta> buscarProductos() {
		return Tienda.getInstancia().buscarProductoVenta();
	}

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
		
	public Cliente registrarse(String nickname, String password, String dni) {
		return Tienda.getInstancia().registrarNuevoCliente(nickname, password, dni);
	}

	public String getSessionId() {
		return sessionId;
	}
}