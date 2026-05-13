package Gui.Controladores.cliente;

import Gui.cliente.SubpanelSegundaMano;
import productos.EstadoProducto;
import productos.Producto2Mano;
import tienda.FiltroSegundaMano;
import tienda.Tienda;
import usuarios.Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de segunda mano.
 *
 * @author Daniel
 * @version 1.0
 */
public class ControladorSegundaMano implements ActionListener {

	/** Vista del subpanel de segunda mano. */
	private SubpanelSegundaMano vista;

	/** Cliente logueado. */
	private Cliente cliente;

	/** Instancia de la tienda. */
	private Tienda tienda;

	/** Producto seleccionado en el catálogo. */
	@SuppressWarnings("unused")
	private Producto2Mano productoSeleccionado;

	/**
	 * Constructor del controlador de segunda mano.
	 *
	 * @param vista   El subpanel de segunda mano
	 * @param cliente El cliente logueado
	 */
	public ControladorSegundaMano(SubpanelSegundaMano vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos de los botones de la vista.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if (cmd != null && cmd.startsWith("ver:")) {
			String idProducto = cmd.substring(4);
			for (Producto2Mano p : tienda.buscarSegundaMano()) {
				if (p.getId().equals(idProducto)) {
					productoSeleccionado = p;
					vista.verProducto2Mano(p);
					return;
				}
			}
		}
	}

	/**
	 * Devuelve todos los productos de segunda mano visibles excluyendo los del
	 * propio cliente. Los sin tasar no son visibles y no se incluyen.
	 *
	 * @return Lista de productos disponibles
	 */
	public List<Producto2Mano> obtenerTodos() {
		List<Producto2Mano> todos = tienda.buscarSegundaMano();
		if (cliente == null)
			return todos;
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : todos) {
			if (!p.isVisible())
				continue;
			if (!p.getPropietario().getNickname().equals(cliente.getNickname()))
				resultado.add(p);
		}
		return resultado;
	}

	/**
	 * Filtra productos usando FiltroSegundaMano con nombre, precio y estado.
	 * Excluye los sin tasar y los del propio cliente.
	 *
	 * @param nombre    Nombre a buscar
	 * @param precioMin Precio mínimo
	 * @param precioMax Precio máximo
	 * @param estadoMin Estado mínimo del producto
	 * @return Lista filtrada
	 */
	public List<Producto2Mano> filtrar(String nombre, double precioMin, double precioMax, EstadoProducto estadoMin) {
		FiltroSegundaMano filtro = new FiltroSegundaMano();
		filtro.setValorMinimo(precioMin);
		filtro.setValorMaximo(precioMax);
		if (estadoMin != null)
			filtro.setEstadoMinimo(estadoMin);

		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : tienda.buscarSegundaManoFiltrado(filtro)) {
			if (!p.isVisible())
				continue;
			if (cliente != null && p.getPropietario().getNickname().equals(cliente.getNickname()))
				continue;
			if (nombre == null || nombre.isBlank() || p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
				resultado.add(p);
		}
		return resultado;
	}

	/**
	 * Devuelve la cartera visible de un usuario por su nickname. Devuelve null si
	 * el usuario no existe.
	 *
	 * @param nickname El nickname del usuario
	 * @return Lista de productos o null si no existe el usuario
	 */
	public List<Producto2Mano> verCarteraDeUsuario(String nickname) {
		if (nickname == null || nickname.isBlank())
			return new ArrayList<>();
		List<Producto2Mano> cartera = tienda.verCartera(nickname);
		if (cartera == null)
			return null;
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : cartera) {
			if (p.isVisible() && !p.isBloqueado())
				resultado.add(p);
		}
		return resultado;
	}

	/**
	 * Devuelve los nombres de los estados para el combo.
	 *
	 * @return Array de nombres de estados
	 */
	public String[] getEstados() {
		return new String[] { "Cualquiera", EstadoProducto.PERFECTO.toString(), EstadoProducto.MUY_BUENO.toString(),
				EstadoProducto.USO_LIGERO.toString(), EstadoProducto.USO_EVIDENTE.toString(),
				EstadoProducto.MUY_USADO.toString(), EstadoProducto.DAÑADO.toString() };
	}

	/**
	 * Convierte el texto del combo al enum EstadoProducto. Devuelve null si es
	 * "Cualquiera".
	 *
	 * @param texto El texto seleccionado en el combo
	 * @return El EstadoProducto correspondiente o null
	 */
	public EstadoProducto textoAEstado(String texto) {
		if (texto == null || texto.equals("Cualquiera"))
			return null;
		for (EstadoProducto e : EstadoProducto.values()) {
			if (e.toString().equals(texto))
				return e;
		}
		return null;
	}

	/**
	 * Indica si hay cliente logueado.
	 *
	 * @return true si hay cliente
	 */
	public boolean hayCliente() {
		return cliente != null;
	}

	/**
	 * Ordena una lista por nombre A-Z.
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	public List<Producto2Mano> ordenarPorNombreAsc(List<Producto2Mano> productos) {
		List<Producto2Mano> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
		return resultado;
	}

	/**
	 * Ordena una lista por nombre Z-A.
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	public List<Producto2Mano> ordenarPorNombreDesc(List<Producto2Mano> productos) {
		List<Producto2Mano> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> b.getNombre().compareToIgnoreCase(a.getNombre()));
		return resultado;
	}

	/**
	 * Ordena una lista por precio tasado menor a mayor. Todos los productos tienen
	 * valoración
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	public List<Producto2Mano> ordenarPorPrecioAsc(List<Producto2Mano> productos) {
		List<Producto2Mano> resultado = new ArrayList<>(productos);
		resultado.sort(
				(a, b) -> Double.compare(a.getValoracion().getPrecioTasacion(), b.getValoracion().getPrecioTasacion()));
		return resultado;
	}

	/**
	 * Ordena una lista por precio tasado mayor a menor.
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	public List<Producto2Mano> ordenarPorPrecioDesc(List<Producto2Mano> productos) {
		List<Producto2Mano> resultado = new ArrayList<>(productos);
		resultado.sort(
				(a, b) -> Double.compare(b.getValoracion().getPrecioTasacion(), a.getValoracion().getPrecioTasacion()));
		return resultado;
	}

	/**
	 * Ordena una lista por estado del producto de mejor a peor.
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	public List<Producto2Mano> ordenarPorEstadoDesc(List<Producto2Mano> productos) {
		List<Producto2Mano> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> Integer.compare(b.getValoracion().getEstadoProducto().ordinal(),
				a.getValoracion().getEstadoProducto().ordinal()));
		return resultado;
	}

	/**
	 * Ordena una lista por estado del producto de peor a mejor.
	 *
	 * @param productos La lista a ordenar
	 * @return Lista ordenada
	 */
	public List<Producto2Mano> ordenarPorEstadoAsc(List<Producto2Mano> productos) {
		List<Producto2Mano> resultado = new ArrayList<>(productos);
		resultado.sort((a, b) -> Integer.compare(a.getValoracion().getEstadoProducto().ordinal(),
				b.getValoracion().getEstadoProducto().ordinal()));
		return resultado;
	}
}
