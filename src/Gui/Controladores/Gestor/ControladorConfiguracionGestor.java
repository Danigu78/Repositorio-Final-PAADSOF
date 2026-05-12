package Gui.Controladores.Gestor;

import Gui.Gestor.*;

import excepciones.PesosInvalidosException;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de configuración y perfil del gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorConfiguracionGestor implements ActionListener {

	/** Gestor autenticado. */
	private Gestor gestor;

	/** Instancia de la tienda. */
	private Tienda tienda;

	/** Vista del panel de configuración. */
	private SubpanelConfiguracionGestor vistaConfig;

	/** Vista del panel de perfil. */
	private SubpanelPerfilGestor vistaPerfil;

	/**
	 * Constructor para el panel de configuración.
	 *
	 * @param vista  Vista de configuración
	 * @param gestor Gestor autenticado
	 */
	public ControladorConfiguracionGestor(SubpanelConfiguracionGestor vista, Gestor gestor) {
		this.vistaConfig = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Constructor para el panel de perfil.
	 *
	 * @param vista  Vista de perfil
	 * @param gestor Gestor autenticado
	 */
	public ControladorConfiguracionGestor(SubpanelPerfilGestor vista, Gestor gestor) {
		this.vistaPerfil = vista;
		this.gestor = gestor;
		this.tienda = Tienda.getInstancia();
	}

	/**
	 * Gestiona los eventos de la vista.
	 *
	 * @param e Evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("guardarConfig") && vistaConfig != null) {
			vistaConfig.procesarGuardar();
		} else if (cmd.equals("guardarPerfil") && vistaPerfil != null) {
			vistaPerfil.procesarGuardar();
		}
	}

	/**
	 * Configura los tiempos máximos del sistema.
	 *
	 * @param tOferta  Tiempo máximo de oferta
	 * @param tCarrito Tiempo máximo del carrito
	 * @param tPago    Tiempo máximo de pago
	 * @return true si la configuración se guarda correctamente
	 */
	public boolean configurarTiempos(int tOferta, int tCarrito, int tPago) {
		boolean ok = gestor.configurarTiemposSistema(tOferta, tCarrito, tPago);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Establece el precio de tasación de la tienda.
	 *
	 * @param precio Nuevo precio de tasación
	 * @return true si se actualiza correctamente
	 */
	public boolean setPrecioTasacion(double precio) {
		boolean ok = gestor.setPrecioTasacion(precio);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Configura los pesos del recomendador.
	 *
	 * @param pesoValoracion Peso de las valoraciones
	 * @param pesoCompras    Peso de las compras
	 * @param pesoCategorias Peso de las categorías
	 * @return true si los pesos son válidos y se guardan correctamente
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
	 * Modifica los datos del perfil del gestor.
	 *
	 * @param nuevoNickname Nuevo nickname
	 * @param nuevaPassword Nueva contraseña
	 * @return true si la modificación se realiza correctamente
	 */
	public boolean modificarPerfil(String nuevoNickname, String nuevaPassword) {
		boolean ok = gestor.modificarPerfil(nuevoNickname, nuevaPassword);
		if (ok)
			GuardadoTienda.guardar(tienda);
		return ok;
	}

	/**
	 * Devuelve el tiempo máximo del carrito.
	 *
	 * @return Tiempo máximo del carrito
	 */
	public int getTiempoCarrito() {
		return tienda.getTiempoMaxCarrito();
	}

	/**
	 * Devuelve el tiempo máximo de las ofertas.
	 *
	 * @return Tiempo máximo de oferta
	 */
	public int getTiempoOferta() {
		return tienda.getTiempoMaxOferta();
	}

	/**
	 * Devuelve el tiempo máximo de pago.
	 *
	 * @return Tiempo máximo de pago
	 */
	public int getTiempoPago() {
		return tienda.getTiempoMaxPago();
	}

	/**
	 * Devuelve el precio de tasación actual.
	 *
	 * @return Precio de tasación
	 */
	public double getPrecioTasacion() {
		return tienda.getPrecioTasacion();
	}

	/**
	 * Devuelve el peso de valoración del recomendador.
	 *
	 * @return Peso de valoración
	 */
	public double getPesoValoracion() {
		return tienda.getRecomendador().getPesoValoracion();
	}

	/**
	 * Devuelve el peso de compras del recomendador.
	 *
	 * @return Peso de compras
	 */
	public double getPesoCompras() {
		return tienda.getRecomendador().getPesoCompras();
	}

	/**
	 * Devuelve el peso de categorías del recomendador.
	 *
	 * @return Peso de categorías
	 */
	public double getPesoCategorias() {
		return tienda.getRecomendador().getPesoCategorias();
	}

	/**
	 * Devuelve el nickname del gestor.
	 *
	 * @return Nickname del gestor
	 */
	public String getNickname() {
		return gestor.getNickname();
	}
}