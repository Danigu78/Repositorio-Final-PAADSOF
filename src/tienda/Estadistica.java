package tienda;

public class Estadistica {

	private static Estadistica instancia;

	private Estadistica() {
	}

	/**
	 * Devuelve la única instancia de la clase Estadistica
	 *
	 * @return la instancia compartida de Estadistica
	 */
	public static Estadistica getInstancia() {
		if (instancia == null)
			instancia = new Estadistica();
		return instancia;
	}

	private int nProductosVentas = 1;
	private int nUsuarioRegistrado = 1;
	private int nUsuarioNoRegistrado = 1;
	private int nProducto2Mano = 1;
	private int nVentas = 1;
	private int nDescuentos = 1;
	private int nIntercambiosFinalizados = 1;
	private int nCategorias = 1;
	private int nCarritos = 1;
	private int nReseñas = 1;
	private int nTasacionesCobradas = 0;
	private int nNotificaciones = 1;

	/**
	 * Recupera el contador de productos de venta
	 *
	 * @return el número actual de productos de venta
	 */
	public int getnProductosVentas() {
		return nProductosVentas;
	}

	/**
	 * Cambia el contador de productos de venta
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnProductosVentas(int n) {
		this.nProductosVentas = n;
	}

	/**
	 * Recupera el contador de usuarios registrados
	 *
	 * @return el número actual de usuarios registrados
	 */
	public int getnUsuarioRegistrado() {
		return nUsuarioRegistrado;
	}

	/**
	 * Cambia el contador de usuarios registrados
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnUsuarioRegistrado(int n) {
		this.nUsuarioRegistrado = n;
	}

	/**
	 * Recupera el contador de usuarios no registrados
	 *
	 * @return el número actual de usuarios no registrados
	 */
	public int getnUsuarioNoRegistrado() {
		return nUsuarioNoRegistrado;
	}

	/**
	 * Actualiza el contador de usuarios no registrados
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnUsuarioNoRegistrado(int n) {
		this.nUsuarioNoRegistrado = n;
	}

	/**
	 * Recupera el contador de productos de segunda mano
	 *
	 * @return el número actual de productos de segunda mano
	 */
	public int getnProducto2Mano() {
		return nProducto2Mano;
	}

	/**
	 * Cambia el contador de productos de segunda mano
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnProducto2Mano(int n) {
		this.nProducto2Mano = n;
	}

	/**
	 * Recupera el contador de ventas
	 *
	 * @return el número actual de ventas
	 */
	public int getnVentas() {
		return nVentas;
	}

	/**
	 * Cambia el contador de ventas
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnVentas(int n) {
		this.nVentas = n;
	}

	/**
	 * Recupera el contador de descuentos
	 *
	 * @return el número actual de descuentos
	 */
	public int getnDescuentos() {
		return nDescuentos;
	}

	/**
	 * Cambia el contador de descuentos
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnDescuentos(int n) {
		this.nDescuentos = n;
	}

	/**
	 * Recupera el contador de intercambios finalizados
	 *
	 * @return el número actual de intercambios finalizados
	 */
	public int getnIntercambiosFinalizados() {
		return nIntercambiosFinalizados;
	}

	/**
	 * Cambia el contador de intercambios finalizados
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnIntercambiosFinalizados(int n) {
		this.nIntercambiosFinalizados = n;
	}

	/**
	 * Recupera el contador de categorías
	 *
	 * @return el número actual de categorías
	 */
	public int getnCategorias() {
		return nCategorias;
	}

	/**
	 * Cambia el contador de categorías
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnCategorias(int n) {
		this.nCategorias = n;
	}

	/**
	 * Recupera el contador de carritos
	 *
	 * @return el número actual de carritos
	 */
	public int getnCarritos() {
		return nCarritos;
	}

	/**
	 * Cambia el contador de carritos
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnCarritos(int n) {
		this.nCarritos = n;
	}

	/**
	 * Recupera el contador de reseñas
	 *
	 * @return el número actual de reseñas
	 */
	public int getnReseñas() {
		return nReseñas;
	}

	/**
	 * Cambia el contador de reseñas
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnReseñas(int n) {
		this.nReseñas = n;
	}

	/**
	 * Recupera el número de tasaciones cobradas
	 *
	 * @return el número actual de tasaciones cobradas
	 */
	public int getnTasacionesCobradas() {
		return nTasacionesCobradas;
	}

	/**
	 * Cambia el número de tasaciones cobradas
	 *
	 * @param n el nuevo valor del contador
	 */
	public void setnTasacionesCobradas(int n) {
		this.nTasacionesCobradas = n;
	}

	/**
	 * Recupera el contador de notificaciones
	 *
	 * @return el número actual de notificaciones
	 */
	public int getnNotificaciones() {
		return nNotificaciones;
	}

	/**
	 * Cambia el contador de notificaciones
	 *
	 * @param nNotificaciones el nuevo valor del contador
	 */
	public void setnNotificaciones(int nNotificaciones) {
		this.nNotificaciones = nNotificaciones;
	}
}