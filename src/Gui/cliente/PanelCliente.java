package Gui.cliente;

import javax.swing.*;

import Gui.PanelAbstractoGeneral;
import Gui.VentanaPrincipal;
import Gui.Controladores.cliente.ControladorPanelCliente;

import java.awt.*;
import usuarios.Cliente;
import ventas.Pedido;

/**
 * Panel principal del cliente en CheckPoint. Extiende AbstractPanelSection para
 * reutilizar helpers visuales y la barra de navegación común. Sigue el patrón
 * MVC de los apuntes — delega la navegación en ControladorPanelCliente.
 *
 * @author Daniel
 * @version 1.0
 */
public class PanelCliente extends PanelAbstractoGeneral {

	private static final String SEC_CATALOGO = "CATALOGO";
	private static final String SEC_CARRITO = "CARRITO";
	private static final String SEC_PEDIDOS = "PEDIDOS";
	private static final String SEC_SEGUNDA_MANO = "SEGUNDA_MANO";
	private static final String SEC_INTERCAMBIOS = "INTERCAMBIOS";
	private static final String SEC_NOTIFICACIONES = "NOTIFICACIONES";
	private static final String SEC_PERFIL = "PERFIL";
	private static final String SEC_CARTERA = "MI_CARTERA";
	private static final String SEC_DESCUENTOS = "DESCUENTOS";

	/** Controlador del panel — gestiona la navegación entre secciones. */
	private ControladorPanelCliente controlador;

	/** Cliente actualmente logueado. */
	private Cliente cliente;

	/** CardLayout para alternar entre secciones. */
	private CardLayout cardSecciones;

	/** Panel contenedor de todas las secciones. */
	private JPanel panelSecciones;

	/** Barra de navegación — guardada para marcarBotonBarraActivoPorCmd(). */
	private JPanel barra;

	/** Subpaneles del cliente. */
	private SubpanelCatalogo subpanelCatalogo;
	private SubpanelCarrito subpanelCarrito;
	private SubpanelPedidos subpanelPedidos;
	private SubpanelSegundaMano subpanelSegundaMano;
	private SubpanelIntercambios subpanelIntercambios;
	private SubpanelNotificaciones subpanelNotificaciones;
	private SubpanelPerfil subpanelPerfil;
	private SubpanelPago subpanelPago;
	private SubpanelCartera subpanelCartera;
	private SubpanelDescuentos subpanelDescuentos;

	/**
	 * Constructor del panel cliente.
	 *
	 * @param ventana La ventana principal
	 */
	public PanelCliente(VentanaPrincipal ventana) {
		super(ventana);
		inicializarUI();
	}

	/**
	 * Construye y organiza todos los componentes del panel cliente. Crea el
	 * controlador y lo registra en la barra de navegación.
	 */
	private void inicializarUI() {
		controlador = new ControladorPanelCliente(this);

		String[][] pestañas = { { "Catálogo", SEC_CATALOGO }, { "Carrito", SEC_CARRITO },
				{ "Mis Pedidos", SEC_PEDIDOS }, { "Segunda Mano", SEC_SEGUNDA_MANO }, { "Mi Cartera", SEC_CARTERA },
				{ "Intercambios", SEC_INTERCAMBIOS }, { "Notificaciones", SEC_NOTIFICACIONES },
				{ "Mi Perfil", SEC_PERFIL }, { "Descuentos", SEC_DESCUENTOS } };

		// crearBarraNavegacion() de AbstractPanelSection
		barra = crearBarraNavegacion("🎮 CheckPoint", "Usuario", pestañas, controlador);
		add(barra, BorderLayout.NORTH);

		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

		subpanelCatalogo = new SubpanelCatalogo(ventana);
		subpanelCarrito = new SubpanelCarrito(ventana);
		subpanelPedidos = new SubpanelPedidos(ventana);
		subpanelSegundaMano = new SubpanelSegundaMano(ventana);
		subpanelIntercambios = new SubpanelIntercambios(ventana);
		subpanelNotificaciones = new SubpanelNotificaciones(ventana);
		subpanelPerfil = new SubpanelPerfil(ventana);
		subpanelPago = new SubpanelPago(ventana, this);
		subpanelCartera = new SubpanelCartera(ventana);
		subpanelDescuentos = new SubpanelDescuentos(ventana);

		subpanelPedidos.setPanelCliente(this);
		subpanelCartera.setPanelCliente(this);
		subpanelPerfil.setPanelCliente(this);

		panelSecciones.add(subpanelCatalogo, SEC_CATALOGO);
		panelSecciones.add(subpanelCarrito, SEC_CARRITO);
		panelSecciones.add(subpanelPedidos, SEC_PEDIDOS);
		panelSecciones.add(subpanelSegundaMano, SEC_SEGUNDA_MANO);
		panelSecciones.add(subpanelIntercambios, SEC_INTERCAMBIOS);
		panelSecciones.add(subpanelNotificaciones, SEC_NOTIFICACIONES);
		panelSecciones.add(subpanelPerfil, SEC_PERFIL);
		panelSecciones.add(subpanelPago, "PAGO");
		panelSecciones.add(subpanelCartera, SEC_CARTERA);
		panelSecciones.add(subpanelDescuentos, SEC_DESCUENTOS);

		add(panelSecciones, BorderLayout.CENTER);
		mostrarSeccion(SEC_CATALOGO);
		marcarBotonBarraActivoPorCmd(barra, SEC_CATALOGO);
	}

	/**
	 * Muestra la sección indicada en el área de contenido principal. Lo llama el
	 * controlador desde actionPerformed.
	 *
	 * @param seccion Identificador de la sección
	 */
	public void mostrarSeccion(String seccion) {
		cardSecciones.show(panelSecciones, seccion);
	}

	/**
	 * Marca la pestaña activa en la barra de navegación. Lo llama el controlador
	 * desde actionPerformed.
	 *
	 * @param cmd ActionCommand de la pestaña a marcar
	 */
	public void marcarPestaña(String cmd) {
		// marcarBotonBarraActivoPorCmd() de AbstractPanelSection
		marcarBotonBarraActivoPorCmd(barra, cmd);
	}

	/**
	 * Actualiza los datos de una sección cuando el usuario navega a ella. Lo llama
	 * el controlador desde actionPerformed.
	 *
	 * @param seccion Identificador de la sección a actualizar
	 */
	public void actualizarSeccion(String seccion) {
		if (cliente == null)
			return;
		switch (seccion) {
		case SEC_CATALOGO:
			subpanelCatalogo.actualizar(cliente);
			break;
		case SEC_CARRITO:
			subpanelCarrito.actualizar(cliente);
			break;
		case SEC_PEDIDOS:
			subpanelPedidos.actualizar(cliente);
			break;
		case SEC_SEGUNDA_MANO:
			subpanelSegundaMano.actualizar(cliente);
			break;
		case SEC_CARTERA:
			subpanelCartera.actualizar(cliente);
			break;
		case SEC_INTERCAMBIOS:
			subpanelIntercambios.actualizar(cliente);
			break;
		case SEC_NOTIFICACIONES:
			subpanelNotificaciones.actualizar(cliente);
			break;
		case SEC_PERFIL:
			subpanelPerfil.actualizar(cliente);
			break;
		case SEC_DESCUENTOS:
			subpanelDescuentos.actualizar(cliente);
			break;
		}
	}

	/**
	 * Actualiza el panel con los datos del cliente que acaba de hacer login.
	 * Actualiza el nombre en la barra y carga todas las secciones.
	 *
	 * @param cliente El cliente que ha iniciado sesión
	 */
	public void actualizarCliente(Cliente cliente) {
		this.cliente = cliente;
		// actualizarUsuarioBarra() de AbstractPanelSection
		actualizarUsuarioBarra(barra, cliente.getNickname());
		subpanelCatalogo.actualizar(cliente);
		subpanelCarrito.actualizar(cliente);
		subpanelPedidos.actualizar(cliente);
		subpanelSegundaMano.actualizar(cliente);
		subpanelIntercambios.actualizar(cliente);
		subpanelNotificaciones.actualizar(cliente);
		subpanelPerfil.actualizar(cliente);
		subpanelCartera.actualizar(cliente);
		subpanelDescuentos.actualizar(cliente);
		mostrarSeccion(SEC_CATALOGO);
		marcarBotonBarraActivoPorCmd(barra, SEC_CATALOGO);
	}

	/**
	 * Navega al subpanel de pago con el pedido indicado. Lo llama SubpanelPedidos.
	 *
	 * @param pedido  El pedido a pagar
	 * @param cliente El cliente logueado
	 */
	public void mostrarPago(Pedido pedido, Cliente cliente) {
		subpanelPago.mostrarPago(pedido, cliente);
		mostrarSeccion("PAGO");
	}

	/**
	 * Actualiza los datos de la sección de pedidos. Lo llama SubpanelPago tras
	 * pagar correctamente.
	 */
	public void actualizarSeccionPedidos() {
		subpanelPedidos.actualizar(cliente);
	}

	/**
	 * Vuelve a la cartera y la actualiza. Lo llama SubpanelPagoTasacion tras pagar
	 * la tasación.
	 */
	public void volverACartera() {
		mostrarSeccion(SEC_CARTERA);
		subpanelCartera.actualizar(cliente);
	}

	/**
	 * Actualiza el nombre de usuario en la barra de navegación. Lo llama
	 * SubpanelPerfil tras cambiar el nickname.
	 *
	 * @param nombre El nuevo nombre a mostrar
	 */
	public void actualizarNombreUsuario(String nombre) {
		// actualizarUsuarioBarra() de AbstractPanelSection
		actualizarUsuarioBarra(barra, nombre);
	}
}