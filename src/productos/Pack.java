package productos;

import java.util.ArrayList;
import java.io.*;

import excepciones.*;

/**
 * Clase que representa un conjunto de productos vendidos como una única unidad.
 * Un Pack es un {@link ProductoVenta} compuesto por múltiples
 * {@link LineaPack}. Incluye lógica para validar que el precio del pack sea
 * inferior a la suma de sus componentes y gestiona automáticamente el stock de
 * los productos incluidos.
 * 
 * @author Lucas Manuel Blanco Rodríguez
 * @version 1.0
 */
public class Pack extends ProductoVenta {

	private static final long serialVersionUID = 1L;

	/**
	 * Lista de relaciones entre productos y cantidades que componen el pack.
	 */
	private ArrayList<LineaPack> lineas;

	/**
	 * Constructor de la clase Pack.
	 *
	 * @param nombre          el nombre del pack
	 * @param descripcion     una breve descripción del pack
	 * @param imagenRuta      la ruta de la imagen asociada
	 * @param precioOficial   el precio fijado para el pack
	 * @param stockDisponible las unidades disponibles del pack
	 */
	public Pack(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.lineas = new ArrayList<>();
	}

	/**
	 * Constructor de la clase Pack con sus líneas ya incluidas.
	 *
	 * @param nombre          el nombre del pack
	 * @param descripcion     una breve descripción del pack
	 * @param imagenRuta      la ruta de la imagen asociada
	 * @param precioOficial   el precio fijado para el pack
	 * @param stockDisponible las unidades disponibles del pack
	 * @param lineas          las líneas de productos que formarán parte del pack
	 */
	public Pack(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			ArrayList<LineaPack> lineas) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.lineas = new ArrayList<>();

		if (lineas != null) {
			validarLineasIniciales(lineas);

			if (!precioValidoParaLineas(lineas)) {
				throw new ProductoInvalidoException(
						"El precio del pack debe ser al menos un euro menor que la suma de sus productos.");
			}

			this.lineas.addAll(lineas);
			descontarStockLineas(lineas, stockDisponible);
		}
	}

	/**
	 * Añade una línea nueva al pack.
	 *
	 * @param lp la línea que se quiere incorporar
	 * @return true si se añade correctamente
	 */
	public boolean addLinea(LineaPack lp) {
		if (lp == null) {
			throw new ProductoInvalidoException("La línea del pack no puede ser null.");
		}

		if (lp.getProducto() == null) {
			throw new ProductoInvalidoException("El producto de la línea no puede ser null.");
		}

		if (lp.getUnidades() <= 0) {
			throw new ProductoInvalidoException("Las unidades deben ser mayores que 0.");
		}

		if (contieneProducto(lp.getProducto())) {
			throw new ProductoYaEnPackException(
					"El producto " + lp.getProducto().getNombre() + " ya está incluido en el pack.");
		}

		if (lp.getProducto() == this) {
			throw new ProductoInvalidoException("Un pack no puede contenerse a sí mismo.");
		}

		int stockNecesario = lp.getUnidades() * this.stockDisponible;

		if (lp.getProducto().getStockDisponible() < stockNecesario) {
			throw new StockInsuficienteParaPackException(
					"No hay stock suficiente para añadir " + lp.getProducto().getNombre() + " al pack.");
		}

		this.lineas.add(lp);

		if (!precioPackValido()) {
			this.lineas.remove(lp);
			throw new ProductoInvalidoException(
					"El precio del pack debe ser al menos un euro menor que la suma de sus productos.");
		}

		lp.getProducto().setStockDisponible(lp.getProducto().getStockDisponible() - stockNecesario);

		return true;
	}

	/**
	 * Quita del pack la línea asociada a un producto.
	 *
	 * @param p el producto cuya línea se quiere eliminar
	 * @return true si se elimina, false si no se encuentra o el producto es null
	 */
	public boolean eliminarLinea(ProductoVenta p) {
		if (p == null) {
			return false;
		}

		LineaPack lineaAEliminar = null;

		for (LineaPack lp : this.lineas) {
			if (lp.getProducto().getId().equals(p.getId())) {
				lineaAEliminar = lp;
				break;
			}
		}

		if (lineaAEliminar == null) {
			return false;
		}

		this.lineas.remove(lineaAEliminar);

		if (!precioPackValido()) {
			this.lineas.add(lineaAEliminar);
			throw new ProductoInvalidoException(
					"Al quitar este producto, el pack dejaría de ser al menos un euro más barato.");
		}

		lineaAEliminar.getProducto().setStockDisponible(lineaAEliminar.getProducto().getStockDisponible()
				+ lineaAEliminar.getUnidades() * this.stockDisponible);

		return true;
	}

	/**
	 * Cambia las unidades de un producto dentro del pack.
	 *
	 * @param p              el producto a modificar
	 * @param nuevasUnidades la nueva cantidad de unidades
	 * @return true si el cambio se realiza correctamente
	 */
	public boolean modificarUnidades(ProductoVenta p, int nuevasUnidades) {
		if (p == null) {
			throw new ProductoInvalidoException("El producto no puede ser null.");
		}

		if (nuevasUnidades < 0) {
			throw new ProductoInvalidoException("Las nuevas unidades no pueden ser negativas.");
		}

		if (nuevasUnidades == 0) {
			return eliminarLinea(p);
		}

		LineaPack lineaActual = null;

		for (LineaPack lp : this.lineas) {
			if (lp.getProducto().getId().equals(p.getId())) {
				lineaActual = lp;
				break;
			}
		}

		if (lineaActual == null) {
			return false;
		}

		int unidadesActuales = lineaActual.getUnidades();
		int diferencia = nuevasUnidades - unidadesActuales;

		if (diferencia == 0) {
			return true;
		}

		if (diferencia > 0) {
			int stockNecesario = diferencia * this.stockDisponible;

			if (p.getStockDisponible() < stockNecesario) {
				throw new StockInsuficienteParaPackException(
						"No hay stock suficiente para modificar las unidades de " + p.getNombre() + " en el pack.");
			}
		}

		lineaActual.setUnidades(nuevasUnidades);

		if (!precioPackValido()) {
			lineaActual.setUnidades(unidadesActuales);
			throw new ProductoInvalidoException(
					"El precio del pack debe ser al menos un euro menor que la suma de sus productos.");
		}

		int ajusteStock = diferencia * this.stockDisponible;
		p.setStockDisponible(p.getStockDisponible() - ajusteStock);

		return true;
	}

	/**
	 * Añade un producto al pack indicando cuántas unidades tendrá.
	 *
	 * @param p        el producto que se quiere añadir
	 * @param unidades las unidades de ese producto dentro del pack
	 * @return true si se añade correctamente
	 */
	public boolean addProducto(ProductoVenta p, int unidades) {
		if (p == null) {
			throw new ProductoInvalidoException("El producto no puede ser null.");
		}

		if (unidades <= 0) {
			throw new ProductoInvalidoException("Las unidades deben ser mayores que 0.");
		}

		if (p == this) {
			throw new ProductoInvalidoException("Un pack no puede contenerse a sí mismo.");
		}

		if (contieneProducto(p)) {
			throw new ProductoYaEnPackException("El producto " + p.getNombre() + " ya está incluido en el pack.");
		}

		LineaPack lp = new LineaPack(p, unidades);
		return addLinea(lp);
	}

	/**
	 * Añade un producto al pack con una sola unidad.
	 *
	 * @param p el producto que se quiere incorporar
	 * @return true si se añade correctamente
	 */
	public boolean addProducto_conunaUnidad(ProductoVenta p) {
		if (p == null) {
			throw new ProductoInvalidoException("El producto no puede ser null.");
		}

		if (p == this) {
			throw new ProductoInvalidoException("Un pack no puede contenerse a sí mismo.");
		}

		if (contieneProducto(p)) {
			throw new ProductoYaEnPackException("El producto " + p.getNombre() + " ya está incluido en el pack.");
		}

		return addProducto(p, 1);
	}

	/**
	 * Comprueba si un producto ya forma parte del pack.
	 *
	 * @param p el producto a buscar
	 * @return true si ya está incluido, false en caso contrario
	 */
	public boolean contieneProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}

		for (LineaPack lp : this.lineas) {
			if (lp.getProducto().getId().equals(p.getId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Suma el importe de todos los productos que componen el pack.
	 *
	 * @return el total de los subtotales de sus líneas
	 */
	public double calcularSumaProductos() {
		double suma = 0;

		for (LineaPack lp : this.lineas) {
			suma += lp.getSubtotal();
		}

		return suma;
	}

	/**
	 * Obtiene el precio final del pack.
	 *
	 * @return el precio actual del pack
	 */
	public double calcularPrecioFinal() {
		return this.precioOficial;
	}

	/**
	 * Cambia el precio oficial del pack.
	 *
	 * @param nuevoPrecio el nuevo precio que se quiere establecer
	 * @return true si se actualiza correctamente
	 */
	public boolean setPrecioOficial(double nuevoPrecio) {
		if (nuevoPrecio <= 0) {
			throw new ProductoInvalidoException("El precio del pack debe ser mayor que 0.");
		}

		double precioAnterior = this.precioOficial;
		this.precioOficial = nuevoPrecio;

		if (!precioPackValido()) {
			this.precioOficial = precioAnterior;
			throw new ProductoInvalidoException(
					"El precio del pack debe ser al menos un euro menor que la suma de sus productos.");
		}

		return true;
	}

	/**
	 * Aumenta el stock del pack y descuenta el stock necesario de sus productos.
	 *
	 * @param cantidad cantidad de packs que se quieren añadir
	 * @return true si se pudo aumentar el stock
	 */
	public boolean aumentarStockPack(int cantidad) {
		if (cantidad <= 0) {
			return false;
		}

		if (this.lineas.isEmpty()) {
			System.out.println("No se puede aumentar el stock de un pack sin productos.");
			return false;
		}

		for (LineaPack lp : this.lineas) {
			ProductoVenta producto = lp.getProducto();
			int stockNecesario = lp.getUnidades() * cantidad;

			if (producto.getStockDisponible() < stockNecesario) {
				System.out.println("No hay stock suficiente de " + producto.getNombre() + " para crear " + cantidad
						+ " packs más.");
				return false;
			}
		}

		for (LineaPack lp : this.lineas) {
			ProductoVenta producto = lp.getProducto();
			int stockNecesario = lp.getUnidades() * cantidad;

			producto.setStockDisponible(producto.getStockDisponible() - stockNecesario);
		}

		this.stockDisponible += cantidad;
		return true;
	}

	/**
	 * Reduce el stock del pack y devuelve al stock los productos reservados para
	 * esos packs.
	 *
	 * @param cantidad cantidad de packs que se quieren retirar
	 * @return true si se pudo retirar el stock
	 */
	public boolean retirarStockPack(int cantidad) {
		if (cantidad <= 0) {
			return false;
		}

		if (this.stockDisponible < cantidad) {
			System.out.println("No se puede retirar más stock del pack del que hay disponible.");
			return false;
		}

		for (LineaPack lp : this.lineas) {
			ProductoVenta producto = lp.getProducto();
			int stockADevolver = lp.getUnidades() * cantidad;

			producto.setStockDisponible(producto.getStockDisponible() + stockADevolver);
		}

		this.stockDisponible -= cantidad;
		return true;
	}

	/**
	 * Recupera el precio oficial del pack.
	 *
	 * @return el precio final del pack
	 */
	@Override
	public double getPrecioOficial() {
		return calcularPrecioFinal();
	}

	/**
	 * Devuelve un resumen del pack con su precio, stock y productos incluidos.
	 *
	 * @return una cadena con los datos principales del pack
	 */
	@Override
	public String toString() {
		String textoLineas = this.lineas.isEmpty() ? "sin productos" : "";

		for (LineaPack lp : this.lineas) {
			textoLineas += lp.getProducto().getNombre() + " x" + lp.getUnidades() + " = " + lp.getSubtotal() + "€; ";
		}

		return super.toString() + " | Precio pack: " + this.precioOficial + "€" + " | Stock pack: "
				+ this.stockDisponible + " | Suma productos: " + this.calcularSumaProductos() + "€" + " | Líneas: "
				+ textoLineas + "|";
	}

	/**
	 * Recupera las líneas que forman el pack.
	 *
	 * @return la lista de líneas del pack
	 */
	public ArrayList<LineaPack> getLineas() {
		return lineas;
	}

	/**
	 * Sustituye las líneas actuales del pack.
	 *
	 * Este método no descuenta stock. Se usa para casos internos o de consulta.
	 *
	 * @param lineas la nueva lista de líneas
	 */
	public void setLineas(ArrayList<LineaPack> lineas) {
		if (lineas == null) {
			this.lineas = new ArrayList<>();
		} else {
			this.lineas = lineas;
		}
	}

	/**
	 * Muestra por pantalla un pequeño resumen de precios del pack.
	 */
	public void resumenPrecios() {
		System.out.println("Resumen de precios:");
		System.out.println(" Suma productos: " + this.calcularSumaProductos() + "€");
		System.out.println(" Precio actual:  " + this.calcularPrecioFinal() + "€");

		double ahorro = this.calcularSumaProductos() - this.calcularPrecioFinal();
		System.out.printf(" Ahorro total:   %.2f€\n", ahorro);
	}

	/**
	 * Comprueba que el precio del pack sea al menos 1 euro menor que la suma de sus
	 * productos.
	 *
	 * @return true si el precio es válido
	 */
	private boolean precioPackValido() {
		if (this.lineas == null || this.lineas.isEmpty()) {
			return true;
		}

		return this.precioOficial <= calcularSumaProductos() - 1;
	}

	private boolean precioValidoParaLineas(ArrayList<LineaPack> lineasComprobar) {
		if (lineasComprobar == null || lineasComprobar.isEmpty()) {
			return true;
		}

		return this.precioOficial <= calcularSumaProductos(lineasComprobar) - 1;
	}

	private double calcularSumaProductos(ArrayList<LineaPack> lineasComprobar) {
		double suma = 0;

		for (LineaPack lp : lineasComprobar) {
			suma += lp.getSubtotal();
		}

		return suma;
	}

	private void validarLineasIniciales(ArrayList<LineaPack> lineasComprobar) {
		ArrayList<String> idsUsados = new ArrayList<>();

		for (LineaPack lp : lineasComprobar) {
			if (lp == null) {
				throw new ProductoInvalidoException("La línea del pack no puede ser null.");
			}

			if (lp.getProducto() == null) {
				throw new ProductoInvalidoException("El producto de la línea no puede ser null.");
			}

			if (lp.getUnidades() <= 0) {
				throw new ProductoInvalidoException("Las unidades deben ser mayores que 0.");
			}

			if (lp.getProducto() == this) {
				throw new ProductoInvalidoException("Un pack no puede contenerse a sí mismo.");
			}

			if (idsUsados.contains(lp.getProducto().getId())) {
				throw new ProductoYaEnPackException(
						"El producto " + lp.getProducto().getNombre() + " ya está incluido en el pack.");
			}

			int stockNecesario = lp.getUnidades() * this.stockDisponible;

			if (lp.getProducto().getStockDisponible() < stockNecesario) {
				throw new StockInsuficienteParaPackException(
						"No hay stock suficiente para añadir " + lp.getProducto().getNombre() + " al pack.");
			}

			idsUsados.add(lp.getProducto().getId());
		}
	}

	private void descontarStockLineas(ArrayList<LineaPack> lineasComprobar, int stockPack) {
		for (LineaPack lp : lineasComprobar) {
			ProductoVenta producto = lp.getProducto();
			int stockNecesario = lp.getUnidades() * stockPack;

			producto.setStockDisponible(producto.getStockDisponible() - stockNecesario);
		}
	}

	/**
	 * Método llamado automáticamente cuando se guarda un Pack en fichero.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		inicializarCamposNulos();
		out.defaultWriteObject();
	}

	/**
	 * Método llamado automáticamente cuando se carga un Pack desde fichero.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		inicializarCamposNulos();
	}

	/**
	 * Evita que la lista de líneas del pack quede a null al guardar/cargar.
	 */
	private void inicializarCamposNulos() {
		if (this.lineas == null) {
			this.lineas = new ArrayList<>();
		}
	}
}