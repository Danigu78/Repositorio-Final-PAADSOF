package Gui.Controladores.Gestor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import Gui.Gestor.SubpanelCategoriasGestor;
import Gui.Controladores.empleado.ControladorProductosEmpleado;
import productos.Categoria;
import productos.ProductoVenta;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;

/**
 * Controlador de la gestión de categorías para el gestor.
 */
public class ControladorCategoriasGestor implements ActionListener {

	public static final String CREAR_CATEGORIA = "crearCategoria";
	public static final String ANADIR_CATEGORIA_PRODUCTO = "anadirCategoriaProducto";
	public static final String QUITAR_CATEGORIA_PRODUCTO = "quitarCategoriaProducto";
	public static final String ELIMINAR_CATEGORIA = "eliminarCategoria";

	private SubpanelCategoriasGestor vista;
	private Gestor gestor;
	private Tienda tienda;

	private ControladorProductosEmpleado productos = new ControladorProductosEmpleado();

	public ControladorCategoriasGestor(SubpanelCategoriasGestor vista, Gestor gestor) {
		this.vista = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(CREAR_CATEGORIA)) {
			vista.procesarCrearCategoria();

		} else if (cmd.equals(ANADIR_CATEGORIA_PRODUCTO)) {
			vista.procesarAnadirCategoriaProducto();

		} else if (cmd.equals(QUITAR_CATEGORIA_PRODUCTO)) {
			vista.procesarQuitarCategoriaProducto();
		} else if (cmd.startsWith(ELIMINAR_CATEGORIA + ":")) {
		    vista.procesarEliminarCategoria(
		        cmd.substring(ELIMINAR_CATEGORIA.length() + 1));
		}
	}

	public boolean crearCategoria(String nombre, String descripcion) {
		boolean ok = gestor.crearCategoria(nombre, descripcion);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
		boolean ok = gestor.añadirProductoACategoria(idProducto, nombreCat);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
		boolean ok = gestor.eliminarProductoDeCategoria(idProducto, nombreCat);

		if (ok) {
			GuardadoTienda.guardar(tienda);
		}

		return ok;
	}

	public List<Categoria> getCategorias() {
		return tienda.getCategorias();
	}

	public List<ProductoVenta> getProductosOrdenados() {
		return productos.obtenerProductosOrdenadosPorStock();
	}

	public String obtenerTipoProductoVenta(ProductoVenta producto) {
		return productos.obtenerTipoProductoVenta(producto);
	}

	public String obtenerTextoCategorias(ProductoVenta producto) {
		return productos.obtenerTextoCategorias(producto);
	}

	public String formatearPrecio(double precio) {
		return productos.formatearPrecio(precio);
	}

	public String formatearPuntuacion(double puntuacion) {
		return productos.formatearPuntuacion(puntuacion);
	}
	
	

	public boolean eliminarCategoria(String nombreCat) {
	   
	    Tienda tienda = Tienda.getInstancia();
	    Categoria cat = tienda.buscarCategoriaPorNombre(nombreCat);
	    if (cat != null) {
	        
	        for (ProductoVenta p : tienda.getStockVentas()) {
	            p.getCategorias().remove(cat);
	        }
	    }
	    boolean ok = gestor.eliminarCategoria(nombreCat);
	    if (ok) GuardadoTienda.guardar(tienda);
	    return ok;
	}
}