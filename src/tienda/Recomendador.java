package tienda;

import java.io.Serializable;
import java.util.*;

import excepciones.PesosInvalidosException;
import excepciones.RecomendadorNoActivoException;
import productos.*;
import usuarios.*;
import ventas.*;

/**
 * Clase que gestiona el sistema de sugerencias personalizadas para clientes.
 * 
 * @author Antonino Albarrán
 * @version 1.0
 */
public class Recomendador implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Cantidad máxima de sugerencias que se mostrarán al usuario. */
	private int limiteMaximo = 5;

	/** Indica si el motor de recomendaciones está habilitado o no. */
	private boolean activo = true;

	/** Peso del criterio basado en la calificación media de los productos. */
	private double pesoValoracion = 0.34;

	/**
	 * Peso del criterio basado en el historial de compras de otros usuarios
	 * similares.
	 */
	private double pesoCompras = 0.33;

	/** Peso del criterio basado en la categoría favorita del cliente. */
	private double pesoCategorias = 0.33;

	/**
	 * Muestra por pantalla las sugerencias generadas para un cliente
	 *
	 * @param cliente el cliente para el que se quieren obtener sugerencias
	 * @throws RecomendadorNoActivoException si el recomendador está desactivado
	 */
	public void imprimirSugerencias(Cliente cliente) throws RecomendadorNoActivoException {
		List<ProductoVenta> sugerencias = generarSugerencias(cliente);
		if (sugerencias.isEmpty()) {
			System.out.println("  No hay sugerencias para " + cliente.getNickname());
			return;
		}
		System.out.println("  Sugerencias para " + cliente.getNickname() + " (" + sugerencias.size() + "):");
		for (ProductoVenta p : sugerencias) {
			System.out.println("   - " + p.resumen());
		}
	}

	/**
	 * Genera una lista de productos recomendados para un cliente
	 *
	 * @param cliente el cliente para el que se calculan las sugerencias
	 * @return la lista de productos recomendados
	 * @throws RecomendadorNoActivoException si el recomendador no está activo
	 */
	public List<ProductoVenta> generarSugerencias(Cliente cliente) throws RecomendadorNoActivoException {
		if (!activo) {
			throw new RecomendadorNoActivoException();
		}

		if (cliente == null) {
			return new ArrayList<>();
		}

		Set<String> excluidos = obtenerIdsExcluidos(cliente);
		// cogemos toda la informacion
		List<ProductoVenta> porValoracion = recomendarPorValoracion(cliente, limiteMaximo, excluidos);
		List<ProductoVenta> porCompras = recomendarPorCompras(cliente, limiteMaximo, excluidos);
		List<ProductoVenta> porCategorias = recomendarPorCategorias(cliente, limiteMaximo, excluidos);

		Map<String, Double> puntuaciones = new HashMap<>();
		Map<String, ProductoVenta> porId = new HashMap<>();
		// añadimos puntuaciones
		acumularPuntuaciones(porValoracion, pesoValoracion, puntuaciones, porId);
		acumularPuntuaciones(porCompras, pesoCompras, puntuaciones, porId);
		acumularPuntuaciones(porCategorias, pesoCategorias, puntuaciones, porId);
		// los resultados son ordenados segun su puntuacion, como siempre
		List<ProductoVenta> resultado = new ArrayList<>(porId.values());
		resultado.sort(
				Comparator.comparingDouble((ProductoVenta p) -> puntuaciones.getOrDefault(p.getId(), 0.0)).reversed());

		return resultado.subList(0, Math.min(limiteMaximo, resultado.size()));// deuvleve solo la lista desde la
																				// posicion 0 hasta el limite o menos,
																				// si no
																				// hay n productos
	}

	/**
	 * Recomienda productos según su valoración media
	 *
	 * @param cliente   el cliente para el que se genera la recomendación
	 * @param n         el número máximo de productos a devolver
	 * @param excluidos los ids de productos que no deben recomendarse
	 * @return la lista de productos recomendados por valoración
	 */
	private List<ProductoVenta> recomendarPorValoracion(Cliente cliente, int n, Set<String> excluidos) {
		List<ProductoVenta> candidatos = new ArrayList<>();
		for (ProductoVenta p : Tienda.getInstancia().getStockVentas()) {
			if (!excluidos.contains(p.getId()) && p.getStockDisponible() > 0) {
				candidatos.add(p);
			}
		}
		// ordenamos la lista (sort) en base a (Comparator.comparingDOuble) de un
		// double, que es la ountuacion media.
		// el reserved solo hace q el q mayor puntuacion tenga este Aprimero
		candidatos.sort(Comparator.comparingDouble(ProductoVenta::getMediaPuntuacion).reversed());
		return candidatos.subList(0, Math.min(n, candidatos.size()));// devuelve la li
	}

	/**
	 * Recomienda productos basándose en compras parecidas de otros clientes
	 *
	 * @param cliente   el cliente para el que se quiere recomendar
	 * @param n         el número máximo de resultados
	 * @param excluidos los productos que no deben incluirse
	 * @return la lista de productos recomendados por historial de compras
	 */
	private List<ProductoVenta> recomendarPorCompras(Cliente cliente, int n, Set<String> excluidos) {

		Set<String> compradosPorCliente = new HashSet<>();
		for (Pedido ped : cliente.getHistorialPedidos()) {
			if (ped.getEstado() != EstadoPedido.CANCELADO) {
				for (LineaPedido l : ped.getLineas()) {
					compradosPorCliente.add(l.getProducto().getId());
				}
			}
		}

		// Contamos cuantas veces aparece cada producto en pedidos de otros
		// clientes que tienen al menos una compra en comun con el nuestro
		Map<String, Integer> frecuencia = new HashMap<>();
		Map<String, ProductoVenta> porId = new HashMap<>(); // relacion de id y PV
		// si un usuario ha comprado un producto que ha comprado el nuestro, se tienen
		// en cuenta todas las cosas que ha comprado
		for (UsuarioRegistrado u : Tienda.getInstancia().getUsuarios()) {
			// q u sea ciente y sea distinto del cliente en cuestion
			if (!(u instanceof Cliente) || u.equals(cliente))
				continue;
			Cliente otro = (Cliente) u;

			boolean tieneEnComun = false;
			for (Pedido ped : otro.getHistorialPedidos()) {
				if (ped.getEstado() == EstadoPedido.CANCELADO)
					continue;
				for (LineaPedido l : ped.getLineas()) {
					if (compradosPorCliente.contains(l.getProducto().getId())) {
						// encontramos que un cliente ha comprado un producto que ha comprado nuestro
						// cliente
						tieneEnComun = true;
						break;
					}
				}
				if (tieneEnComun)
					break;
			}
			if (!tieneEnComun)
				continue;

			for (Pedido ped : otro.getHistorialPedidos()) {
				if (ped.getEstado() == EstadoPedido.CANCELADO)
					continue;
				for (LineaPedido l : ped.getLineas()) {
					ProductoVenta p = l.getProducto();
					// miramos si un producto comprado por este nuevo cliente es valido
					if (!excluidos.contains(p.getId()) && p.getStockDisponible() > 0) {
						frecuencia.merge(p.getId(), 1, Integer::sum);// si no existe, crea una entrada en el hashmap, si
																		// existe, suma uno
						porId.put(p.getId(), p);// relacion nueva
					}
				}
			}
		}
		// nos quedamos con los productos que hemos encontrado
		List<ProductoVenta> candidatos = new ArrayList<>(porId.values());
		candidatos.sort(Comparator.comparingInt((ProductoVenta p) -> frecuencia.getOrDefault(p.getId(), 0)).reversed());
		// ordena nuestra lista de candidatos segun el valor del mapa ( sufrecuecia) de
		// mayor a menor
		return candidatos.subList(0, Math.min(n, candidatos.size()));
	}

	/**
	 * Recomienda productos de la categoría favorita del cliente
	 *
	 * @param cliente   el cliente del que se toma la categoría favorita
	 * @param n         el número máximo de resultados
	 * @param excluidos los productos que no se deben recomendar
	 * @return la lista de productos sugeridos por categoría
	 */
	private List<ProductoVenta> recomendarPorCategorias(Cliente cliente, int n, Set<String> excluidos) {
		Categoria favorita = cliente.determinarCategoriaFavorita();
		if (favorita == null) {

			return new ArrayList<>();
		}

		List<ProductoVenta> candidatos = new ArrayList<>();
		for (ProductoVenta p : Tienda.getInstancia().getStockVentas()) {
			if (!excluidos.contains(p.getId()) && p.getStockDisponible() > 0 && p.getCategorias().contains(favorita)) {
				candidatos.add(p);// no esta excluido, esta disponble y esta en la cateoria buscada
			}
		}

		candidatos.sort(Comparator.comparingDouble(ProductoVenta::getMediaPuntuacion).reversed()); // ademas, ordenado
																									// por puntuacion
		return candidatos.subList(0, Math.min(n, candidatos.size()));
	}

	/**
	 * Obtiene los ids de productos que no deben recomendarse al cliente
	 *
	 * @param cliente el cliente del que se revisan compras y carrito
	 * @return el conjunto de ids excluidos
	 */
	private Set<String> obtenerIdsExcluidos(Cliente cliente) {
		Set<String> ids = new HashSet<>();
		for (Pedido ped : cliente.getHistorialPedidos()) {
			if (ped.getEstado() != EstadoPedido.CANCELADO) {
				for (LineaPedido l : ped.getLineas()) {
					ids.add(l.getProducto().getId());// todos los productos pedidos pa dentro
				}
			}
		}
		if (cliente.getCarritoActual() != null) {
			for (LineaCarrito l : cliente.getCarritoActual().getLineas()) {
				ids.add(l.getProducto().getId());// todos los productos en carrito pa dentro
			}
		}
		return ids;
	}

	/**
	 * Suma puntuaciones a los productos de una lista según su posición y su peso
	 *
	 * @param lista        la lista de productos a puntuar
	 * @param peso         el peso que tiene ese criterio
	 * @param puntuaciones el mapa donde se guardan las puntuaciones acumuladas
	 * @param porId        el mapa auxiliar para relacionar ids con productos
	 */
	private void acumularPuntuaciones(List<ProductoVenta> lista, double peso, Map<String, Double> puntuaciones,
			Map<String, ProductoVenta> porId) {
		if (peso == 0)
			return; // si no tiene peso, ni lo tenemos en cuenta
		int n = lista.size();
		for (int i = 0; i < n; i++) {
			ProductoVenta p = lista.get(i);
			puntuaciones.merge(p.getId(), peso * (n - i), Double::sum);// lo mismo que antes, añade una entrada al mapa
																		// o si ya esta, añade el valor dado
			porId.put(p.getId(), p);
		}
	}

	/**
	 * Cambia la configuración general del recomendador
	 *
	 * @param limite el número máximo de sugerencias
	 * @param estado indica si el recomendador queda activo o no
	 */
	public void setConfiguracion(int limite, boolean estado) {
		if (limite <= 0) {
			System.out.println("El limite debe ser mayor que 0.");
			return;
		}
		this.limiteMaximo = limite;
		this.activo = estado;
	}

	/**
	 * Cambia los pesos usados para calcular las recomendaciones
	 *
	 * @param pesoValoracion el peso del criterio de valoración
	 * @param pesoCompras    el peso del criterio de compras
	 * @param pesoCategorias el peso del criterio de categorías
	 * @throws PesosInvalidosException si los pesos no son válidos
	 */
	public void setPesos(double pesoValoracion, double pesoCompras, double pesoCategorias)
			throws PesosInvalidosException {
		if (pesoValoracion < 0 || pesoCompras < 0 || pesoCategorias < 0
				|| (pesoValoracion + pesoCompras + pesoCategorias == 0)) {
			throw new PesosInvalidosException(pesoValoracion, pesoCompras, pesoCategorias);
		}
		double suma = pesoValoracion + pesoCompras + pesoCategorias;
		if (suma == 0) {
			System.out.println("Al menos un peso debe ser mayor que 0.");
			return;
		}
		// normalizamos los pesos
		this.pesoValoracion = pesoValoracion / suma;
		this.pesoCompras = pesoCompras / suma;
		this.pesoCategorias = pesoCategorias / suma;
	}

	/**
	 * Devuelve el límite máximo de sugerencias
	 *
	 * @return el número máximo de productos sugeridos
	 */
	public int getLimiteMaximo() {
		return limiteMaximo;
	}

	/**
	 * Indica si el recomendador está activo
	 *
	 * @return true si está activo, false en caso contrario
	 */
	public boolean isActivo() {
		return activo;
	}

	/**
	 * Recupera el peso asignado a la valoración
	 *
	 * @return el peso del criterio de valoración
	 */
	public double getPesoValoracion() {
		return pesoValoracion;
	}

	/**
	 * Recupera el peso asignado a las compras
	 *
	 * @return el peso del criterio de compras
	 */
	public double getPesoCompras() {
		return pesoCompras;
	}

	/**
	 * Recupera el peso asignado a las categorías
	 *
	 * @return el peso del criterio de categorías
	 */
	public double getPesoCategorias() {
		return pesoCategorias;
	}
}