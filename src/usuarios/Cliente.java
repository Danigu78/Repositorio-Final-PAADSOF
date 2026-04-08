package usuarios;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.*;
import tienda.*;
import productos.*;
import intercambios.*;

import ventas.*;

/**
 * Clase que representa a un cliente registrado en el sistema. Un cliente puede
 * realizar pedidos, gestionar su carrito, intercambiar productos, escribir
 * reseñas y recibir notificaciones.
 *
 * @author Daniel
 * @version 1.0
 */
public class Cliente extends UsuarioRegistrado {
	// private double saldoPuntos;

	private List<Pedido> historialPedidos;
	private String dni;
	private Carrito carritoActual;
	private List<Producto2Mano> carteraIntercambio;
	private List<Oferta> ofertasPendientes;
	private List<Oferta> historialIntercambios;
	private List<Reseña> reseñas;
	protected List<Notificacion> notificaciones;
	private PreferenciaNotificacion preferencias;

	/**
	 * Constructor de la clase Cliente. Inicializa las listas y preferencias del
	 * cliente.
	 *
	 * @param nickname nombre de usuario del cliente
	 * @param password contraseña del cliente
	 * @param dni      documento identificativo del cliente
	 */
	public Cliente(String nickname, String password, String dni) {
		super(nickname, password);
		this.dni = dni;
		this.historialPedidos = new ArrayList<>();
		this.carteraIntercambio = new ArrayList<>();
		this.ofertasPendientes = new ArrayList<>();
		this.historialIntercambios = new ArrayList<>();
		this.reseñas = new ArrayList<>();
		this.preferencias = new PreferenciaNotificacion();
		this.notificaciones = new ArrayList<>();
	}

	/**
	 * Permite al cliente subir un producto de segunda mano a su cartera.
	 *
	 * @param nombre        nombre del producto
	 * @param descripString descripción del producto
	 * @param imagen        ruta o identificador de la imagen
	 */
	public void subirProducto(String nombre, String descripString, String imagen) {
		Producto2Mano product = new Producto2Mano(this, nombre, descripString, imagen);
		carteraIntercambio.add(product);
		System.out.println("Producto subido correctamente a tu cartera personal de objetos de sgeunda mano.");
	}

	/**
	 * Cuenta el número de pedidos completados del cliente. Se consideran
	 * completados los pedidos pagados, listos para recoger o entregados.
	 *
	 * @return número de pedidos completados
	 */
	public int contarPedidosCompletados() {
		int count = 0;
		for (Pedido p : historialPedidos) {
			if (p.getEstado() == EstadoPedido.PAGADO || p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER
					|| p.getEstado() == EstadoPedido.ENTREGADO) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Cuenta el número de pedidos cancelados del cliente.
	 *
	 * @return número de pedidos cancelados
	 */
	public int contarPedidosCancelados() {
		int count = 0;
		for (Pedido p : historialPedidos) {
			if (p.getEstado() == EstadoPedido.CANCELADO) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Devuelve el número total de intercambios realizados.
	 *
	 * @return número de intercambios
	 */
	public int contarIntercambios() {
		return historialIntercambios.size();
	}

	/**
	 * Comprueba si un producto está en la cartera del cliente.
	 *
	 * @param p producto a comprobar
	 * @return true si el producto está en la cartera, false en caso contrario
	 */
	public boolean tieneProductoenSuCartera(Producto2Mano p) {
		if (p == null) {
			return false;
		}
		return this.carteraIntercambio.contains(p);
	}

	/**
	 * Solicita la tasación de un producto de segunda mano. Realiza el pago
	 * correspondiente y añade el producto a la lista de pendientes.
	 *
	 * @param p         producto a tasar
	 * @param tarjeta   número de tarjeta
	 * @param CVV       código de seguridad
	 * @param caducidad fecha de caducidad de la tarjeta
	 * @return true si la solicitud se realiza correctamente, false en caso
	 *         contrario
	 */
	public boolean solicitarTasacion(Producto2Mano p, String tarjeta, int CVV, Date caducidad) {
		if (p == null) {
			System.out.println("El producto no puede ser null");
			return false;
		}
		if (!tieneProductoenSuCartera(p)) {
			System.out.println("El producto no está en la cartera del cliente " + this.getNickname());
			return false;
		}
		if (Tienda.getInstancia().getPendientesTasacion().contains(p)) {
			System.out.println("El producto ya está pendiente de tasacion.");
			return false;
		}
		if (p.isVisible()) {
			System.out.println("El producto ya ha sido tasado");
			return false;
		}
		Pago pagoValoracionPago = new Pago(tarjeta, Tienda.getInstancia().getPrecioTasacion(), caducidad, CVV);
		if (!pagoValoracionPago.getExito()) {
			this.recibirNotificacionTipo(
					"Pago no aceptado, no se ha podido solicitar la valoracion del producto." + p.getNombre(),
					TipoNotificacion.Pago_FALLIDO);
			return false;
		}
		Estadistica.getInstancia().setnTasacionesCobradas(Estadistica.getInstancia().getnTasacionesCobradas() + 1);
		Tienda.getInstancia().solicitarTasacion(p);
		this.recibirNotificacionTipo("Pago correcto. Tasación solicitada. Esperando a que un empleado tase el producto",
				TipoNotificacion.PAGO_EXITOSO);
		return true;
	}

	/**
	 * Obtiene las ofertas pendientes que el cliente debe aceptar o rechazar.
	 *
	 * @return lista de ofertas para decidir
	 */
	public List<Oferta> getOfertasParaDecidir() {
		List<Oferta> paraDecidir = new ArrayList<>();
		for (Oferta o : ofertasPendientes) {
			// Si el destino de la oferta soy yo, es que tengo que contestar por lo que son
			// ofertas pendientes
			if (o.getDestino().equals(this)) {//
				paraDecidir.add(o);
			}
		}
		return paraDecidir;
	}

	/**
	 * Obtiene las ofertas enviadas por el cliente que aún no han sido respondidas
	 * por los otros clientes.
	 *
	 * @return lista de ofertas en espera
	 */
	public List<Oferta> getOfertasEnEspera() {
		List<Oferta> enEspera = new ArrayList<>();
		for (Oferta o : ofertasPendientes) {
			if (o.getOrigen().equals(this)) {// Si yo soy el origen de la oferta, la añado a las ofertas que he hecho
												// que no me han contestado.
				enEspera.add(o);
			}
		}
		return enEspera;
	}

	/**
	 * Permite proponer una oferta de intercambio a otro cliente.
	 *
	 * @param destinatario cliente al que se le envía la oferta
	 * @param misProductos productos ofrecidos
	 * @param susProductos productos solicitados
	 * @return true si la oferta se crea correctamente, false en caso contrario
	 */
	public boolean proponerOferta(Cliente destinatario, List<Producto2Mano> misProductos,
			List<Producto2Mano> susProductos) {
		if (!Tienda.getInstancia().isSistemaTiemposConfigurando()) {
			System.out.println("El sistema no está configurado aún. Contacte con el gestor.");
			return false;
		}
		if (destinatario == null || misProductos == null || susProductos == null) {
			System.out.println("Los parámetros no pueden ser null.");
			return false;
		}
		if (destinatario.equals(this)) {
			System.out.println("No puedes hacerte una oferta a ti mismo.");
			return false;
		}
		if (misProductos.isEmpty() || susProductos.isEmpty()) {
			System.out.println("Debes ofrecer al menos un producto.");
			return false;
		}
		for (Producto2Mano p : misProductos) {

			if (!tieneProductoenSuCartera(p)) {
				System.out.println("El producto " + p.getId() + " no está en tu cartera.");
				return false;
			}
			if (p.isBloqueado()) {
				throw new ProductoBloqueadoException(p.getId());
			}
		}
		for (Producto2Mano p : susProductos) {
			if (!destinatario.tieneProductoenSuCartera(p)) {
				System.out.println("El producto " + p.getId() + " no está en la cartera del destinatario.");
				return false;
			}
			if (p.isBloqueado()) {
				throw new ProductoBloqueadoException(p.getId());
			}
		}
		try {
			Oferta nuevaOferta = new Oferta(this, destinatario, misProductos, susProductos);

			// Si llegamos aquí es que no ha saltado la excepción
			this.ofertasPendientes.add(nuevaOferta);
			destinatario.getOfertasPendientes().add(nuevaOferta);

			destinatario.recibirNotificacionTipo("Has recibido una propuesta de intercambio de " + this.getNickname(),
					TipoNotificacion.OFERTA_RECIBIDA);

			// Bloqueamos mis productos para que no se usen en otra oferta mientras esta
			// esté pendiente
			for (Producto2Mano p : misProductos) {
				p.setBloqueado(true);
			}

			return true;

		} catch (ProductoNoTasadoException e) {
			// Capturamos el error si algún producto no tiene estado de tasación
			System.out.println("Error: No se pudo crear la oferta. " + e.getMessage());
			return false;
		}
	}

	/**
	 * Acepta una oferta de intercambio si está disponible.
	 *
	 * @param oferta oferta a aceptar
	 */
	public void confirmarIntercambio(Oferta oferta) {
		if (this.getOfertasParaDecidir().contains(oferta)) {
			try {
				oferta.aceptarOferta();
			} catch (OfertaNoDisponibleException e) {
				System.out.println(e.getMessage());
			}
			return;
		}
		System.out.println("Esta oferta no se encuentra  disponible");
	}

	/**
	 * Devuelve la lista de intercambios realizados con otro cliente.
	 *
	 * @param c cliente con el que se quieren ver los intercambios
	 * @return lista de ofertas intercambiadas o null si el cliente es null
	 */
	public List<Oferta> verIntercambioscon(Cliente c) {
		List<Oferta> intercambios = new ArrayList<>();
		if (c == null) {
			return null;
		}
		for (Oferta o : historialIntercambios) {// comprobamos que el cliente c sea el origen o el destino de la oferta
			if ((o.getDestino() == c && o.getOrigen() == this) || (o.getOrigen() == c && o.getDestino() == this)) {
				intercambios.add(o);
			}
		}
		return intercambios;
	}

	/**
	 * Muestra por pantalla las ofertas enviadas pendientes de respuesta.
	 */
	public void verMisOfertasEnviadas() {
		List<Oferta> enviadas = getOfertasEnEspera();
		if (enviadas.isEmpty()) {
			System.out.println("  " + getNickname() + " no tiene ofertas enviadas pendientes.");
			return;
		}
		System.out.println("  Ofertas enviadas de " + getNickname() + " (" + enviadas.size() + "):");
		for (Oferta o : enviadas) {
			o.imprimirResumen();
		}
	}

	/**
	 * Muestra por pantalla las ofertas que el cliente debe responder.
	 */
	public void verMisOfertasPorResponder() {
		List<Oferta> porResponder = getOfertasParaDecidir();
		if (porResponder.isEmpty()) {
			System.out.println("  " + getNickname() + " no tiene ofertas por responder.");
			return;
		}
		System.out.println("  Ofertas por responder de " + getNickname() + " (" + porResponder.size() + "):");
		for (Oferta o : porResponder) {
			o.imprimirResumen();
		}
	}

	/**
	 * Muestra el historial de intercambios finalizados del cliente.
	 */
	public void verMiHistorialIntercambios() {
		if (historialIntercambios.isEmpty()) {
			System.out.println("  " + getNickname() + " no tiene intercambios finalizados.");
			return;
		}
		System.out.println(
				"  Historial de intercambios de " + getNickname() + " (" + historialIntercambios.size() + "):");
		for (Oferta o : historialIntercambios) {
			System.out.println(
					"   [" + o.getId() + "] " + o.getOrigen().getNickname() + " <-> " + o.getDestino().getNickname());

			// Si soy el origen, yo di los ofertados y recibí los solicitados
			// Si soy el destino, yo di los solicitados y recibí los ofertados
			boolean soyOrigen = o.getOrigen().equals(this);

			String dados = "";
			String recibidos = "";
			for (Producto2Mano p : (soyOrigen ? o.getProductosOfertados() : o.getProductosSolicitados())) {
				if (!dados.equals(""))
					dados += ", ";
				dados += p.getNombre();
			}
			for (Producto2Mano p : (soyOrigen ? o.getProductosSolicitados() : o.getProductosOfertados())) {
				if (!recibidos.equals(""))
					recibidos += ", ";
				recibidos += p.getNombre();
			}
			System.out.println("   Entregados: " + dados);
			System.out.println("   Recibidos:  " + recibidos);
		}
	}

	/**
	 * Comprueba si un producto ha sido pedido y entregado al cliente.
	 *
	 * @param p producto a comprobar
	 * @return true si ha sido entregado, false en caso contrario
	 */
	public boolean productoHasidoPedidoYentregado(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		for (Pedido ped : historialPedidos) {
			if (ped.productoPertenece(p) == true && ped.getEstado() == EstadoPedido.ENTREGADO) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Permite escribir una reseña sobre un producto comprado previamente.
	 *
	 * @param p     producto a reseñar
	 * @param pts   puntuación otorgada
	 * @param texto comentario de la reseña
	 * @return true si se crea correctamente, false en caso contrario
	 */
	public boolean escribirReseña(ProductoVenta p, int pts, String texto) {
		if (this.productoHasidoPedidoYentregado(p)) {
			// Comprobar si ya tiene una reseña de ese producto
			for (Reseña r : this.reseñas) {
				if (r.getProducto().equals(p)) {
					throw new ReseñaDuplicadaException();
				}
			}
			Reseña res = new Reseña(this, p, pts, texto);
			this.reseñas.add(res);
			System.out.println("Reseña creada y añadida con exito ");
			return true;
		}
		System.out.println("No ha sido posible crear la reseña.");
		return false;
	}

	/**
	 * Determina la categoría más frecuente en los pedidos del cliente.
	 *
	 * @return categoría favorita o null si no hay datos
	 */
	public Categoria determinarCategoriaFavorita() {
		int maxApariciones = 0;
		Categoria favorita = null;

		Map<Categoria, Integer> contador = new HashMap<>();

		for (Pedido p : this.getHistorialPedidos()) {
			for (LineaPedido linea : p.getLineas()) {
				for (Categoria cat : linea.getProducto().getCategorias()) {
					int n = contador.getOrDefault(cat, 0) + 1; // getOrdefault, devuelve el numero de la categoria cat
																// si existe, y cero sino. Si la categoría ya estaba en
																// el mapa, le suma 1; si es la primera vez que la ve,
																// empieza en 1.
					contador.put(cat, n); // a la clave cat le metemos el nuevo numero de apariciones

					if (n > maxApariciones) {
						maxApariciones = n;
						favorita = cat; // guardamos la categoria. Se ira actualizando cada vez que se supere el numero
										// de apariciones
					}
				}
			}
		}
		return favorita;
	}

	/**
	 * Recibe una notificación si está habilitada en las preferencias del cliente.
	 *
	 * @param mensaje contenido de la notificación
	 * @param tipo    tipo de notificación
	 */

	public void recibirNotificacionTipo(String mensaje, TipoNotificacion tipo) {
		if (!this.preferencias.debeRecibirNotificacion(tipo)) {
			return;
		}
		this.notificaciones.add(new Notificacion(mensaje, tipo));
		System.out.println("[Notificación Cliente: " + nickname + "]: " + mensaje);

	}

	/**
	 * Notifica al cliente sobre un nuevo producto en una categoría de interés.
	 *
	 * @param mensaje         contenido de la notificación
	 * @param nombreCategoria categoría del producto
	 */
	public void notificarProductoNuevoCategoria(String mensaje, String nombreCategoria) {
		if (!this.preferencias.NotificacionesProductosNUevosCategoriasInteres(nombreCategoria)) {
			return;
		}
		if (this.notificaciones == null)
			this.notificaciones = new ArrayList<>();
		this.notificaciones.add(new Notificacion(mensaje, TipoNotificacion.CATEGORIA_INTERES));
		System.out.println("[Notificación Cliente]: " + mensaje);
		return;
	}

	/**
	 * Obtiene las notificaciones no leídas del cliente.
	 *
	 * @return lista de notificaciones no leídas
	 */
	public List<Notificacion> getNotificacionesNoLeidas() {
		List<Notificacion> resultado = new ArrayList<>();
		for (Notificacion n : notificaciones) {
			if (!n.isLeida())
				resultado.add(n);
		}
		return resultado;
	}

	/**
	 * Marca una notificación como leída si pertenece al cliente.
	 *
	 * @param n notificación a marcar como leída
	 */
	public void verNotificacion(Notificacion n) {
		if (n == null) {
			return;
		}
		if (!this.notificaciones.contains(n)) {
			System.out.println("No se puede leer una notificacion que no es tuya");
			return;
		}
		n.marcarComoLeida();
		return;
	}

	/**
	 * Obtiene las notificaciones de un tipo concreto.
	 *
	 * @param tipo tipo de notificación
	 * @return lista de notificaciones del tipo indicado
	 */
	public List<Notificacion> getNotificacionesdeTipo(TipoNotificacion tipo) {
		List<Notificacion> notif = new ArrayList<>();
		for (Notificacion n : notificaciones) {
			if (n.getTipo() == tipo)
				notif.add(n);
		}
		return notif;
	}

	/**
	 * Elimina una notificación del cliente.
	 *
	 * @param n notificación a eliminar
	 * @return true si se elimina correctamente, false en caso contrario
	 */
	public boolean eliminarNotifacion(Notificacion n) {
		if (n == null) {
			return false;
		}
		if (!this.notificaciones.contains(n)) {
			System.out.println("No se puede borrar una notificacion que no es tuya");
			return false;
		}
		this.notificaciones.remove(n);
		return true;
	}

	/**
	 * Elimina una oferta pendiente rechazándola previamente.
	 *
	 * @param o oferta a eliminar
	 * @return true si se elimina correctamente, false en caso contrario
	 */
	public boolean eliminarOfertadeOfertasPendientes(Oferta o) {
		if (o == null || !this.getOfertasPendientes().contains(o)) {
			return false;
		}
		try {
			o.rechazar();
		} catch (OfertaNoDisponibleException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Elimina un producto de la cartera de intercambio del cliente.
	 *
	 * @param p producto a eliminar
	 * @return true si se elimina correctamente, false en caso contrario
	 */
	public boolean eliminarProductodeCategoria(Producto2Mano p) {
		if (!this.getCarteraIntercambio().contains(p)) {
			return false;
		}
		this.getCarteraIntercambio().remove(p);
		return true;
	}

	/**
	 * Añade un producto al carrito del cliente.
	 *
	 * @param p        producto a añadir
	 * @param cantidad unidades del producto
	 * @return true si se añade correctamente, false en caso contrario
	 */
	public boolean añadirProductoCarrito(ProductoVenta p, int cantidad) {
		if (!Tienda.getInstancia().isSistemaTiemposConfigurando()) {
			System.out.println(
					"Error. Los parametros del sistema de tiempos no está configurados. Hay que esperar hasta que el gestor de la tienda lo configure");
			return false;
		}

		if (p == null) {
			System.out.println(" El producto no puede ser null.");
			return false;
		}
		if (cantidad <= 0) {
			System.out.println(" La cantidad debe ser mayor que 0.");
			return false;
		}
		if (p.getStockDisponible() < cantidad) {
			System.out.println(" No hay suficientes unidades en la tienda del producto [" + p.getId() + "] "
					+ p.getNombre() + " en la tienda.");
			return false;
		}
		if (this.carritoActual == null) {
			this.carritoActual = new Carrito(this);
			Tienda.getInstancia().getComprobadorTiempos().registrarCarrito(this.getId(), this.carritoActual);
		}

		this.getCarritoActual().añadirProducto(p, cantidad);
		return true;
	}

	/**
	 * Convierte el carrito actual en un pedido pendiente de pago.
	 *
	 * @return true si la operación se realiza correctamente, false en caso
	 *         contrario
	 */
	public boolean reservarCarrito() {
		{
			if (!Tienda.getInstancia().isSistemaTiemposConfigurando()) {
				System.out
						.println("El sistema  de tiempos no está configurado aún. Espere a que el gestor lo configure");
				return false;
			}
			if (carritoActual == null || carritoActual.estaVacio()) {
				System.out.println("No tienes productos en el carrito. Añade productos para poder comprarlo");
				return false;
			}
			if (carritoActual.estaCaducado()) {
				carritoActual.vaciarCarrito();
				this.carritoActual = null;
				System.out.println("El carrito ha caducado, no se puede reservar");
				return false;
			}
			Pedido pedido = new Pedido(this, this.carritoActual);
			this.getHistorialPedidos().add(pedido);
			Tienda.getInstancia().registrarVenta(pedido);
			Tienda.getInstancia().getComprobadorTiempos().registrarPedido(this.getId(), pedido);
			this.carritoActual = null;
			System.out.println("El cliente " + this.getNickname()
					+ " ha reservado correctamente su carrito. Debe pagarlo antes de que se cumplan "
					+ Tienda.getInstancia().getTiempoMaxPago() + " minutos.");
			this.recibirNotificacionTipo(
					"Pedido creado correctamente. Tienes " + Tienda.getInstancia().getTiempoMaxPago()
							+ " minutos para pagarlo y completar la reserva.",
					TipoNotificacion.CONFIRMACION_RESERVA_CARRITO);
			return true;
		}
	}

	/**
	 * Realiza el pago de un pedido.
	 *
	 * @param p             pedido a pagar
	 * @param numeroTarjeta número de tarjeta
	 * @param fechaTarjeta  fecha de caducidad
	 * @param CVV           código de seguridad
	 * @return true si el pago es correcto, false en caso contrario
	 */
	public boolean pagarCarrito(Pedido p, String numeroTarjeta, Date fechaTarjeta, int CVV) {
		if (p == null) {
			System.out.println("El pedido no puede ser null");
			return false;
		}

		if (!this.historialPedidos.contains(p)) {
			System.out.println("Este pedido no es tuyo");
			return false;
		}
		if (p.isCaducado()) {
			p.cancelarPedido();
			System.out.println(
					"El tiempo maximo para pagar el pedido ya expiro. Se han devuelto los productos del pedido al stock de la tienda. Disculpe las molestias.");
			return false;
		}
		if (p.getEstado() != EstadoPedido.PENDIENTE_PAGO) {
			System.out.println("Este pedido no está pendiente de pago");
			return false;
		}
		return p.pagar(numeroTarjeta, CVV, fechaTarjeta);
	}

	/**
	 * Solicita la recogida de un pedido listo para recoger.
	 *
	 * @param codigoRecogida código de recogida del pedido
	 * @return true si la solicitud es correcta, false en caso contrario
	 */
	public boolean solicitarRecogidaPedido(String codigoRecogida) {
		Tienda tienda = Tienda.getInstancia();

		for (Pedido ped : tienda.getHistorialVentas()) {
			if (ped.getCliente().equals(this) && codigoRecogida.equals(ped.getCodigoRecogida())
					&& ped.getEstado() == EstadoPedido.LISTO_PARA_RECOGER) {
				ped.setRecogida_solicitada(true);
				return true;
			}
		}
		System.out.println("Error en la solicitud de recogida de pedido");
		return false;
	}

	/**
	 * Configura las preferencias de notificación del cliente.
	 *
	 * @param tipo  tipo de notificación
	 * @param valor true para activar, false para desactivar
	 * @return true si se modifica correctamente
	 */

	public boolean configurarPreferenciaNotificacion(TipoNotificacion tipo, boolean valor) {
		if (tipo == null) {
			System.out.println("El tipo de notificación no puede ser null.");
			return false;
		}
		this.preferencias.modificarPreferencia(tipo, valor);
		return true;
	}

	/**
	 * Añade una categoría de interés para recibir notificaciones.
	 *
	 * @param nombreCategoria nombre de la categoría
	 * @return true si se añade correctamente
	 */
	public boolean añadirCategoriaInteresParaRecibirInfo(String nombreCategoria) {
		return this.preferencias.añadirCategoriaInteres(nombreCategoria);
	}

	/**
	 * Elimina una categoría de interés del cliente.
	 *
	 * @param nombreCategoria nombre de la categoría
	 * @return true si se elimina correctamente
	 */
	public boolean eliminarCategoriaInteres(String nombreCategoria) {
		return this.preferencias.eliminarCategoriaInteres(nombreCategoria);
	}

	/**
	 * Devuelve las preferencias de notificación del cliente.
	 *
	 * @return objeto de preferencias
	 */
	public PreferenciaNotificacion getPreferencias() {
		return preferencias;
	}

	/**
	 * Modifica el perfil del cliente (nickname y contraseña).
	 *
	 * @param nuevoNickname nuevo nombre de usuario
	 * @param nuevoPass     nueva contraseña
	 * @return true si la modificación es correcta, false en caso contrario
	 */
	public boolean modificarPerfil(String nuevoNickname, String nuevoPass) {

		if (nuevoNickname == null || nuevoNickname.isBlank()) {
			System.out.println("El nuevo nickname no puede estar vacío");
			return false;
		}
		// puede cquerer cambiar solo la contraseÑa y dejarse el mismo nombre, entonces
		// si el ya tiene ese nombre va a existir un usuario con ese nombre que es el.
		if (!nuevoNickname.equalsIgnoreCase(this.getNickname())
				&& Tienda.getInstancia().existeUsuarioConNickname(nuevoNickname)) {
			System.out.println("Error: El nickname '" + nuevoNickname + "' ya está siendo usado por otro usuario.");
			return false;
		}
		// Validar que la nueva contraseña cumpla la seguridad
		if (!validarPassword(nuevoPass)) {
			return false;
		}
		this.setNickname(nuevoNickname);
		this.setPassword(nuevoPass); // Recuerda tener estos métodos en la clase padre

		System.out.println(" Perfil del cliente actualizado con éxito. Tu nickname actual es: " + this.getNickname());
		return true;
	}

	/**
	 * Muestra por pantalla las preferencias de notificación del cliente.
	 */
	public void verMisPreferencias() {
		System.out.println("Preferencias de " + this.getNickname() + ": ");
		System.out.println(this.preferencias);
	}

	/**
	 * Muestra el historial de pedidos del cliente.
	 */
	public void verHistorialPedidos() {
		if (historialPedidos.isEmpty()) {
			System.out.println("  " + getNickname() + " no tiene pedidos.");
			return;
		}
		System.out.println("  Historial de pedidos de " + getNickname() + " (" + historialPedidos.size() + "):");
		for (Pedido p : historialPedidos) {
			System.out.println("   [" + p.getIdPedido() + "]" + " | total: " + String.format("%.2f", p.getTotal()) + "€"
					+ " | estado: " + p.getEstado());
			for (LineaPedido l : p.getLineas()) {
				System.out.println("     -> " + l.getProducto().getNombre() + " x" + l.getCantidad());
			}
		}
	}

	/**
	 * Muestra los productos de la cartera de intercambio del cliente.
	 */
	public void verMiCartera() {
		if (carteraIntercambio.isEmpty()) {
			System.out.println("  " + getNickname() + " no tiene productos en su cartera.");
			return;
		}
		System.out.println("  Cartera de " + getNickname() + " (" + carteraIntercambio.size() + " productos):");
		for (Producto2Mano p : carteraIntercambio) {
			if (p.getValoracion() == null) {
				System.out.println(
						"   [" + p.getId() + "] " + p.getNombre() + " | sin  tasacion, no visible para los demas");
			} else {
				System.out.println("   [" + p.getId() + "] " + p.getNombre() + " | precio: "
						+ p.getValoracion().getPrecioTasacion() + "€" + " | estado: "
						+ p.getValoracion().getEstadoProducto() + " | visible: " + p.isVisible() + " | bloqueado: "
						+ p.isBloqueado());
			}
		}
	}

	/**
	 * Crea una lista de productos de segunda mano a partir de un número variable de
	 * argumentos.
	 *
	 * @param productos productos a añadir
	 * @return lista de productos
	 */
	public List<Producto2Mano> crearListaProductos2Mano(Producto2Mano... productos) {
		List<Producto2Mano> lista = new ArrayList<>();
		for (Producto2Mano p : productos) {
			lista.add(p);
		}
		return lista;
	}

	/**
	 * Muestra todas las notificaciones del cliente, separando leídas y no leídas.
	 */
	public void verMisNotificaciones() {
		if (notificaciones.isEmpty()) {
			System.out.println("  " + getNickname() + " no tiene notificaciones.");
			return;
		}

		List<Notificacion> noLeidas = new ArrayList<>();
		List<Notificacion> leidas = new ArrayList<>();
		for (Notificacion n : notificaciones) {
			if (!n.isLeida())
				noLeidas.add(n);
			else
				leidas.add(n);
		}

		System.out.println("  Notificaciones de " + getNickname() + " (" + notificaciones.size() + " total | "
				+ noLeidas.size() + " no leidas):");

		System.out.println("   No leidas ");
		if (noLeidas.isEmpty()) {
			System.out.println("  ninguna");
		} else {
			for (Notificacion n : noLeidas) {
				System.out.println("  " + n);
				n.marcarComoLeida();
			}
		}

		System.out.println("   Leidas ");
		if (leidas.isEmpty()) {
			System.out.println("  ninguna");
		} else {
			for (Notificacion n : leidas) {
				System.out.println("  " + n);
			}
		}
	}

	/**
	 * Muestra las notificaciones de un tipo concreto.
	 *
	 * @param tipo tipo de notificación
	 */
	public void verMisNotificacionesPorTipo(TipoNotificacion tipo) {
		List<Notificacion> noLeidas = new ArrayList<>();
		List<Notificacion> leidas = new ArrayList<>();

		for (Notificacion n : notificaciones) {
			if (n.getTipo() == tipo) {
				if (!n.isLeida())
					noLeidas.add(n);
				else
					leidas.add(n);
			}
		}

		if (noLeidas.isEmpty() && leidas.isEmpty()) {
			System.out.println("  " + getNickname() + " no tiene notificaciones de tipo " + tipo);
			return;
		}

		System.out.println("  Notificaciones de tipo " + tipo + " de " + getNickname() + " ("
				+ (noLeidas.size() + leidas.size()) + " total | " + noLeidas.size() + " no leidas):");

		System.out.println("   No leidas ");
		if (noLeidas.isEmpty()) {
			System.out.println("  ninguna");
		} else {
			for (Notificacion n : noLeidas) {
				System.out.println("  " + n);
				n.marcarComoLeida();
			}
		}

		System.out.println("   Leidas ");
		if (leidas.isEmpty()) {
			System.out.println("  ninguna");
		} else {
			for (Notificacion n : leidas) {
				System.out.println("  " + n);
			}
		}
	}

	/**
	 * Muestra el contenido del carrito actual del cliente.
	 */
	public void imprimirCarritoActual() {
		if (carritoActual == null) {
			System.out.println("  No hay carrito activo.");
			return;
		}
		carritoActual.imprimirCarrito();
	}

	/**
	 * Recupera el historial completo de pedidos del cliente
	 *
	 * @return la lista de todos los pedidos realizados
	 */
	public List<Pedido> getHistorialPedidos() {
		return this.historialPedidos;
	}

	/**
	 * Devuelve el carrito actual del cliente.
	 *
	 * @return carrito actual
	 */
	public Carrito getCarritoActual() {
		return carritoActual;
	}

	/**
	 * Devuelve la cartera de intercambio del cliente.
	 *
	 * @return lista de productos de segunda mano
	 */
	public List<Producto2Mano> getCarteraIntercambio() {
		return carteraIntercambio;
	}

	/**
	 * Devuelve las ofertas pendientes del cliente.
	 *
	 * @return lista de ofertas pendientes
	 */
	public List<Oferta> getOfertasPendientes() {
		return ofertasPendientes;
	}

	/**
	 * Devuelve el historial de intercambios del cliente.
	 *
	 * @return lista de intercambios
	 */
	public List<Oferta> getHistorialIntercambios() {
		return historialIntercambios;
	}

	/**
	 * Devuelve las reseñas realizadas por el cliente.
	 *
	 * @return lista de reseñas
	 */
	public List<Reseña> getReseñas() {
		return reseñas;
	}

	/**
	 * Devuelve las notificaciones del cliente.
	 *
	 * @return lista de notificaciones
	 */
	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	/**
	 * Modifica el carrito actual del cliente
	 * 
	 * @param carritoActual
	 */
	public void setCarritoActual(Carrito carritoActual) {
		this.carritoActual = carritoActual;
	}

	/**
	 * Devuelve el DNI del cliente.
	 *
	 * @return dni del cliente
	 */
	public String getDni() {
		return dni;
	}

	/**
	 * Establece el DNI del cliente.
	 *
	 * @param dni nuevo dni
	 */
	public void setDni(String dni) {
		this.dni = dni;
	}

	/**
	 * Establece las preferencias de notificación del cliente.
	 *
	 * @param preferencias nuevas preferencias
	 */
	public void setPreferencias(PreferenciaNotificacion preferencias) {
		this.preferencias = preferencias;
	}

}