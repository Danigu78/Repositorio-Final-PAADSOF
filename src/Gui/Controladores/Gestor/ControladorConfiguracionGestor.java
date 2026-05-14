package Gui.Controladores.Gestor;

import Gui.Gestor.*;

import excepciones.PesosInvalidosException;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de configuración y perfil del gestor. Gestiona las operaciones
 * relacionadas con la configuración global del sistema y la modificación del
 * perfil del gestor desde las vistas correspondientes.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorConfiguracionGestor implements ActionListener {

	/** Gestor logueado que realiza las operaciones. */
	private Gestor gestor;

	/** Instancia única de la tienda. */
	private Tienda tienda;

	/** Vista de configuración del gestor. */
	private SubpanelConfiguracionGestor vistaConfig;

	/** Vista de perfil del gestor. */
	private SubpanelPerfilGestor vistaPerfil;

	/**
	 * Constructor del controlador para la vista de configuración.
	 *
	 * @param vista  Vista de configuración
	 * @param gestor Gestor logueado
	 */
	public ControladorConfiguracionGestor(SubpanelConfiguracionGestor vista, Gestor gestor) {
		this.vistaConfig = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Constructor del controlador para la vista de perfil.
	 *
	 * @param vista  Vista de perfil
	 * @param gestor Gestor logueado
	 */
	public ControladorConfiguracionGestor(SubpanelPerfilGestor vista, Gestor gestor) {
		this.vistaPerfil = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos lanzados desde las vistas de configuración y perfil.
	 *
	 * @param e Evento recibido
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if ("guardarConfig".equals(cmd) && vistaConfig != null) {
			vistaConfig.procesarGuardar();
		} else if ("guardarPerfil".equals(cmd) && vistaPerfil != null) {
			vistaPerfil.procesarGuardar();
		}
	}

	/**
	 * Configura los tiempos máximos del sistema.
	 *
	 * @param tOferta  Tiempo máximo de oferta
	 * @param tCarrito Tiempo máximo del carrito
	 * @param tPago    Tiempo máximo de pago
	 * @return true si la configuración se aplica correctamente
	 */
	public boolean configurarTiempos(int tOferta, int tCarrito, int tPago) {
		boolean ok = gestor.configurarTiemposSistema(tOferta, tCarrito, tPago);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Configura el precio de tasación del sistema.
	 *
	 * @param precio Nuevo precio de tasación
	 * @return true si el precio se actualiza correctamente
	 */
	public boolean setPrecioTasacion(double precio) {
		boolean ok = gestor.setPrecioTasacion(precio);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Configura los pesos del recomendador de productos.
	 *
	 * @param pesoValoracion Peso de las valoraciones
	 * @param pesoCompras    Peso de las compras
	 * @param pesoCategorias Peso de las categorías
	 * @return true si los pesos se actualizan correctamente
	 */
	public boolean setPesosRecomendador(double pesoValoracion, double pesoCompras, double pesoCategorias) {
		try {
			tienda.getRecomendador().setPesos(pesoValoracion, pesoCompras, pesoCategorias);
			GuardadoTienda.guardar(tienda);
			return true;
		} catch (PesosInvalidosException e) {
			return false;
		}
	}

	/**
	 * Configura el número máximo de productos recomendados que se muestran al
	 * cliente.
	 *
	 * @param limite número máximo de productos recomendados
	 * @return true si el límite se actualiza correctamente
	 */
	public boolean setLimiteRecomendados(int limite) {
		if (limite <= 0) {
			return false;
		}
		tienda.getRecomendador().setConfiguracion(limite, tienda.getRecomendador().isActivo());
		GuardadoTienda.guardar(tienda);
		return true;
	}

	/**
	 * Modifica el perfil del gestor.
	 *
	 * @param nuevoNickname Nuevo nickname
	 * @param nuevaPassword Nueva contraseña
	 * @return true si el perfil se modifica correctamente
	 */
	public boolean modificarPerfil(String nuevoNickname, String nuevaPassword) {
		boolean ok = gestor.modificarPerfil(nuevoNickname, nuevaPassword);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Devuelve el tiempo máximo configurado para el carrito.
	 *
	 * @return Tiempo máximo del carrito
	 */
	public int getTiempoCarrito() {
		return tienda.getTiempoMaxCarrito();
	}

	/**
	 * Devuelve el tiempo máximo configurado para las ofertas.
	 *
	 * @return Tiempo máximo de oferta
	 */
	public int getTiempoOferta() {
		return tienda.getTiempoMaxOferta();
	}

	/**
	 * Devuelve el tiempo máximo configurado para el pago.
	 *
	 * @return Tiempo máximo de pago
	 */
	public int getTiempoPago() {
		return tienda.getTiempoMaxPago();
	}

	/**
	 * Devuelve el precio de tasación configurado.
	 *
	 * @return Precio de tasación
	 */
	public double getPrecioTasacion() {
		return tienda.getPrecioTasacion();
	}

	/**
	 * Devuelve el peso asignado a las valoraciones en el recomendador.
	 *
	 * @return Peso de valoraciones
	 */
	public double getPesoValoracion() {
		return tienda.getRecomendador().getPesoValoracion();
	}

	/**
	 * Devuelve el peso asignado a las compras en el recomendador.
	 *
	 * @return Peso de compras
	 */
	public double getPesoCompras() {
		return tienda.getRecomendador().getPesoCompras();
	}

	/**
	 * Devuelve el peso asignado a las categorías en el recomendador.
	 *
	 * @return Peso de categorías
	 */
	public double getPesoCategorias() {
		return tienda.getRecomendador().getPesoCategorias();
	}

	/**
	 * Devuelve el número máximo de productos recomendados configurado.
	 *
	 * @return límite de productos recomendados
	 */
	public int getLimiteRecomendados() {
		return tienda.getRecomendador().getLimiteMaximo();
	}

	/**
	 * Devuelve el nickname actual del gestor.
	 *
	 * @return Nickname del gestor
	 */
	public String getNickname() {
		return gestor.getNickname();
	}
}
