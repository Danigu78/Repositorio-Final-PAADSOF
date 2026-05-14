package tienda;

import java.util.*;
import java.io.*;

import intercambios.Oferta;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;
import usuarios.UsuarioNoRegistrado;
import usuarios.UsuarioRegistrado;
import ventas.Carrito;
import ventas.ComprobadorTiempos;
import ventas.Descuento;
import ventas.Pedido;
import ventas.Regalo;
import productos.*;

/**
 * Clase principal que gestiona el funcionamiento global del sistema
 * (Singleton).
 * 
 * @author Antonino Albarrán, Lucas y Daniel
 * @version 1.0
 */
public class Tienda implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Nombre comercial de la tienda. */
	private String nombre;

	/** Lista de todos los usuarios registrados (Clientes, Empleados y Gestor). */
	private List<UsuarioRegistrado> usuarios;

	/** Inventario de productos disponibles para la venta. */
	private List<ProductoVenta> stockVentas;

	/** Catálogo de productos de segunda mano disponibles para intercambio. */
	private List<Producto2Mano> catalogoIntercambio;

	/** Registro histórico de todos los pedidos realizados. */
	private List<Pedido> historialVentas;

	/** Productos recibidos que aún no han sido evaluados por un empleado. */
	private List<Producto2Mano> pendientes_Tasacion;

	/** Lista de promociones y descuentos aplicables actualmente. */
	private List<Descuento> descuentosActivos = new ArrayList<>();

	/** Registro de todos los descuentos creados en el sistema. */
	private List<Descuento> historialDescuentos = new ArrayList<>();

	/** Registro de ofertas de intercambio que se han completado con éxito. */
	private List<Oferta> intercambiosFinalizados = new ArrayList<>();

	/** Clasificaciones disponibles para organizar los productos. */
	private List<Categoria> categorias = new ArrayList<>();

	/** Motor de sugerencias personalizadas para los clientes. */
	private Recomendador recomendador;

	/**
	 * Tiempo máximo (minutos) que un producto puede estar reservado en un carrito.
	 */
	private int tiempoMaxCarrito;

	/** Tiempo máximo (minutos) de validez para una oferta de intercambio. */
	private int tiempoMaxOferta;

	/** Tiempo máximo (minutos) para completar el pago de un pedido. */
	private int tiempoMaxPago;

	/** Tarifa estándar cobrada por el servicio de tasación. */
	private double precioValoracion;

	/** Lista de usuarios que han iniciado sesión actualmente. */
	private transient List<UsuarioRegistrado> usuariosConSesionActiva = new ArrayList<>();

	/** Registro global de avisos y notificaciones generadas. */
	private List<Notificacion> historialNotificaciones = new ArrayList<>();

	/**
	 * Historial completo de todos los productos de segunda mano que han pasado por
	 * la tienda.
	 */
	private List<Producto2Mano> historialProductos2Mano = new ArrayList<Producto2Mano>();

	/** Instancia única de la tienda (Patrón Singleton). */
	private static Tienda instancia;

	/** Componente encargado de monitorizar la caducidad de carritos y ofertas. */
	private transient ComprobadorTiempos comprobadorTiempos;

	private Estadistica estadistica;

	/**
	 * Constructor del objeto que es Singleton de la tienda
	 */
	private Tienda() {
		this.estadistica = Estadistica.getInstancia();

		this.nombre = "CheckPoint";
		this.usuarios = new ArrayList<>();
		this.stockVentas = new ArrayList<>();
		this.catalogoIntercambio = new ArrayList<>();
		this.historialVentas = new ArrayList<>();
		this.pendientes_Tasacion = new ArrayList<>();
		this.descuentosActivos = new ArrayList<>();
		this.historialDescuentos = new ArrayList<>();
		this.intercambiosFinalizados = new ArrayList<>();
		this.categorias = new ArrayList<>();
		this.recomendador = new Recomendador();
		this.tiempoMaxCarrito = 0;
		this.tiempoMaxOferta = 0;
		this.tiempoMaxPago = 0;
		this.precioValoracion = 10;

		Gestor gestor = new Gestor();
		this.usuarios.add(gestor);

		this.usuariosConSesionActiva = new ArrayList<>();
		this.usuariosConSesionActiva.add(gestor);

		this.historialNotificaciones = new ArrayList<>();
		this.historialProductos2Mano = new ArrayList<>();
	}

	/**
	 * Recupera la instancia del objeto único de tienda
	 *
	 * @return la instancia de tienda (Singleton)
	 */
	public static Tienda getInstancia() {
		if (instancia == null)
			instancia = new Tienda();
		return instancia;
	}

	/**
	 * Recupera el gestor de la tienda
	 *
	 * @return el objeto gestor de la tienda
	 */
	public Gestor getGestor() {
		if (usuarios != null && !usuarios.isEmpty()) {
			UsuarioRegistrado u = usuarios.get(0);
			if (u instanceof Gestor) {
				return (Gestor) u;
			}
		}
		System.err.println("Error: No se ha encontrado al Gestor en el sistema.");
		return null;
	}

	/**
	 * Regustra un nuevo Usuario No Registrado
	 *
	 * @return devuelve el usuario no registrado
	 */
	public UsuarioNoRegistrado nuevoUsuarioNoRegistrado() {
		return new UsuarioNoRegistrado();
	}

	/**
	 * Comprueba si existe un usuario en base al nick proporcionado
	 *
	 * @param nickname del usuario a buscar
	 * @return true si existe el usuario, false en cualquier otro caso
	 */
	public boolean existeUsuarioConNickname(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			System.out.println("El nickname no puede estar vacio.");
			return false;
		}
		for (UsuarioRegistrado u : usuarios) {
			if (u.getNickname().equalsIgnoreCase(nickname)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recupera el objeto cliente en base a su id
	 *
	 * @param id del usuario a buscar
	 * @return el objeto cliente si existe, null en cualquier otro caso
	 */
	public Cliente buscarCLientePorId(String id) {
		if (id == null || id.isBlank()) {
			System.out.println("El id no puede estar vacio");
			return null;
		}
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente && u.getId().equals(id)) {
				return (Cliente) u;
			}
		}
		System.out.println("No existe ningún cliente con id: " + id);
		return null;
	}

	public List<Producto2Mano> verCartera(String nombre) {
		Cliente cl = buscarClientePorNickname(nombre);
		if (cl == null) {
			return null;
		}
		return cl.getCarteraIntercambio();

	}

	/**
	 * Busca clientes en base al nick dado devolviendo su objeto
	 *
	 * @param nickname del cliente a buscar
	 * @return el cliente si existe, null en cualquier otro caso
	 */
	public Cliente buscarClientePorNickname(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			return null;
		}
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente && u.getNickname().equalsIgnoreCase(nickname)) {
				return (Cliente) u;
			}
		}
		System.err.println("No existe ningún cliente con nickname: " + nickname);
		return null;
	}

	/**
	 * Devuelve si existe o no un usuario con el dni proporcionado
	 *
	 * @param dni del usuario a buscar
	 * @return true si el usuario existe, false en cualquier otro caso
	 */
	public boolean existeUsuarioConDNI(String dni) {
		if (dni == null || dni.isBlank())
			return false;
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente) {
				Cliente c = (Cliente) u;
				if (c.getDni().equalsIgnoreCase(dni)) {
					return true;
				}
			}
		}
		return false;
	}

	private String normalizarId(String id) {
		if (id == null) {
			return "";
		}
		return id.trim().toUpperCase();
	}

	/**
	 * Imprime el catálogo de productos con stock disponible en la tienda
	 *
	 * @return la lista de todos los productos con stock
	 */

	public List<ProductoVenta> buscarProductoVenta() {
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta p : stockVentas) {
			if (p.getStockDisponible() > 0) {
				productos.add(p);
			}
		}
		this.imprimirCatalogo();
		return productos;
	}

	/**
	 * Imprime el catálogo de productos de la tienda
	 */
	public void imprimirCatalogo() {
		System.out.println("   CATÁLOGO DE PRODUCTOS (" + stockVentas.size() + ") ");
		for (ProductoVenta p : stockVentas) {
			System.out.println("  " + p.resumen());
		}
	}

	/**
	 * Buscar un producto venta en base al id proporcionado
	 *
	 * @param idProducto del producto a buscar
	 * @return el producto venta si existe, null en cualquier otro caso
	 */
	public ProductoVenta buscarProductoVentaPorId(String idProducto) {
		String idBuscado = normalizarId(idProducto);
		if (idBuscado.isBlank()) {
			return null;
		}

		for (ProductoVenta p : stockVentas) {
			if (p != null && p.getId() != null && normalizarId(p.getId()).equals(idBuscado)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Busca la categoría de un producto en base al nombre de este
	 *
	 * @param nombre el nombre del producto
	 * @return la categoría al que pertenece, null en cualquier otro caso
	 */
	public Categoria buscarCategoriaPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank())
			return null;
		for (Categoria c : this.categorias) {
			if (!c.isEliminada() && c.getNombre().equalsIgnoreCase(nombre))
				return c;
		}
		return null;
	}

	/**
	 * Lista todos los productos con el nombre especificado(completo o parcial)
	 *
	 * @param nombre el nombre parcial o completo a buscar
	 * @return la lista de los productos que coincida, null en cualquier otro caso
	 */
	public List<ProductoVenta> buscarproductoPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre no puede estar vacio.");
			return null;
		}
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta p : stockVentas) {
			if (p.getStockDisponible() > 0 && p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
				productos.add(p);
			}
		}
		return productos;
	}

	/**
	 * Busca un pack en base a su nombre
	 *
	 * @param nombre del pack a buscar
	 * @return el pack si existe, sino null
	 */
	public Pack buscarPackPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank())
			return null;
		for (ProductoVenta p : stockVentas) {
			if (p instanceof Pack && p.getNombre().equalsIgnoreCase(nombre)) {
				return (Pack) p;
			}
		}
		System.out.println(" No existe ningún pack con el nombre: " + nombre);
		return null;
	}

	/**
	 * Imprime todos los usuarios con sesión activa, especificando el tipo
	 */
	public void imprimirUsuariosConSesionActiva() {
		System.out.println("  Usuarios con sesion activa: " + usuariosConSesionActiva.size());
		for (UsuarioRegistrado u : usuariosConSesionActiva) {
			String tipo = u instanceof Gestor ? "GESTOR" : u instanceof Empleado ? "EMPLEADO" : "CLIENTE";
			System.out.println("   - [" + tipo + "] " + u.getNickname() + " | id: " + u.getId());
		}
	}

	/**
	 * Busca productos en base a la categoría a la que pertenecen
	 *
	 * @param nombreCategoria el nombre de la categoría por la cual se busca
	 * @return la lista de los productos que pertenecen a esa categoría
	 */
	public List<ProductoVenta> buscarProductoPorCategoria(String nombreCategoria) {
		Categoria cat = buscarCategoriaPorNombre(nombreCategoria);
		if (cat == null) {
			return new ArrayList<>();
		}
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta productoVenta : cat.getProductos()) {
			productos.add(productoVenta);
		}
		return productos;
	}

	/**
	 * Devuelve los productos de segunda mano que están disponibles
	 *
	 * @return la lista de productos visibles y no bloqueados
	 */
	// BuscarSegundaMano
	public List<Producto2Mano> buscarSegundaMano() {
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : catalogoIntercambio) {
			if (p.isVisible() && !p.isBloqueado())
				resultado.add(p);
		}
		return resultado;
	}

	/**
	 * Busca productos de segunda mano por nombre
	 *
	 * @param nombre el nombre que se quiere buscar
	 * @return la lista de productos que coinciden
	 */
	public List<Producto2Mano> buscarSegundaManoPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank())
			return new ArrayList<>();
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : catalogoIntercambio) {
			if (p.isVisible() && !p.isBloqueado() && p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
				resultado.add(p);
			}
		}
		return resultado;
	}

	/**
	 * Busca un producto de segunda mano a partir de su id
	 *
	 * @param id el id del producto
	 * @return el producto encontrado o null si no existe
	 */
	// Busca por id iterando la lista — robusto aunque haya huecos en los indices
	public Producto2Mano buscarSegundaManoPorId(String id) {
		String idBuscado = normalizarId(id);
		if (idBuscado.isBlank()) {
			return null;
		}

		for (Producto2Mano p : catalogoIntercambio) {
			if (p != null && p.getId() != null && normalizarId(p.getId()).equals(idBuscado)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Busca productos de venta aplicando un filtro
	 *
	 * @param filtro el filtro que se quiere usar en la búsqueda
	 * @return la lista de productos que cumplen el filtro
	 */
	// BuscarConFiltros
	public List<ProductoVenta> buscarProductosFiltrados(FiltroVenta filtro) {
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta productoVenta : stockVentas) {
			if (productoVenta.getStockDisponible() > 0 && filtro.productoCumpleFiltro(productoVenta)) {
				productos.add(productoVenta);
			}
		}
		return productos;
	}

	/**
	 * Busca productos de segunda mano según el filtro indicado
	 *
	 * @param filtro el filtro que se aplica a la búsqueda
	 * @return la lista de productos que lo cumplen
	 */
	public List<Producto2Mano> buscarSegundaManoFiltrado(FiltroSegundaMano filtro) {
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : catalogoIntercambio) {
			if (filtro.cumpleFiltro(p)) {
				resultado.add(p);
			}
		}
		return resultado;
	}

	/**
	 * Intenta iniciar sesión con el nickname, la contraseña y el tipo indicado
	 *
	 * @param nickname el nombre del usuario
	 * @param password la contraseña introducida
	 * @param tipo     el tipo de usuario con el que se quiere acceder
	 * @return el usuario si el acceso es correcto, null en caso contrario
	 */
	public UsuarioRegistrado login(String nickname, String password, String tipo) {
		for (UsuarioRegistrado u : usuarios) {
			if (u.getNickname().equals(nickname)) {
				switch (tipo.toUpperCase()) {
				case "EMPLEADO":
					if (u instanceof Empleado) {
						if (((Empleado) u).isDespedido()) {
							System.out.println("Este empleado está dado de baja.");
							return null;
						}
						return u.login(password) ? u : null;
					}
					break;
				case "CLIENTE":
					if (u instanceof Cliente) {
						return u.login(password) ? u : null;
					}
					break;
				case "GESTOR":
					if (u instanceof Gestor) {
						return u.login(password) ? u : null;
					}
					break;
				default:
					System.out.println("Tipo de usuario no reconocido.");
					return null;
				}
			}
		}
		System.out.println("Usuario no encontrado o tipo incorrecto.");
		return null;
	}

	/**
	 * Inicia sesión directamente como gestor
	 *
	 * @param nickname el nickname del gestor
	 * @param password la contraseña del gestor
	 * @return el gestor si el acceso va bien, null si falla
	 */
	// metodos para evitar casteos en el main
	public Gestor loginGestor(String nickname, String password) {
		UsuarioRegistrado u = login(nickname, password, "GESTOR");
		if (u instanceof Gestor) {
			return (Gestor) u;
		}
		return null;
	}

	/**
	 * Inicia sesión directamente como empleado
	 *
	 * @param nickname el nickname del empleado
	 * @param password la contraseña del empleado
	 * @return el empleado si se identifica bien, null en caso contrario
	 */
	public Empleado loginEmpleado(String nickname, String password) {
		UsuarioRegistrado u = login(nickname, password, "EMPLEADO");
		if (u instanceof Empleado) {
			return (Empleado) u;
		}
		return null;
	}

	/**
	 * Inicia sesión directamente como cliente
	 *
	 * @param nickname el nickname del cliente
	 * @param password la contraseña del cliente
	 * @return el cliente si el acceso es correcto, null si no lo es
	 */
	public Cliente loginCliente(String nickname, String password) {
		UsuarioRegistrado u = login(nickname, password, "CLIENTE");
		if (u instanceof Cliente) {
			return (Cliente) u;
		}
		return null;
	}

	/**
	 * Guarda una notificación en el historial de la tienda
	 *
	 * @param n la notificación que se quiere registrar
	 */
	// Notificaciones
	public void registrarNotificacion(Notificacion n) {
		historialNotificaciones.add(n);
	}

	/**
	 * Devuelve las notificaciones que todavía no han sido leídas
	 *
	 * @return la lista de notificaciones pendientes de leer
	 */
	public List<Notificacion> getNotificacionesNoLeidas() {
		List<Notificacion> resultado = new ArrayList<>();
		for (Notificacion n : historialNotificaciones) {
			if (!n.isLeida())
				resultado.add(n);
		}
		return resultado;
	}

	/**
	 * Recupera las notificaciones de un tipo concreto
	 *
	 * @param tipo el tipo de notificación que se quiere buscar
	 * @return la lista de notificaciones de ese tipo
	 */
	public List<Notificacion> getNotificacionesPorTipo(TipoNotificacion tipo) {
		List<Notificacion> resultado = new ArrayList<>();
		for (Notificacion n : historialNotificaciones) {
			if (n.getTipo() == tipo)
				resultado.add(n);
		}
		return resultado;
	}

	/**
	 * Envía una notificación de descuento a los clientes que la tengan activada
	 *
	 * @param d el descuento que se quiere comunicar
	 */
	public void notificarDescuento(Descuento d) {
		for (Cliente c : obtenerClientesTienda()) {
			if (c.getPreferencias().debeRecibirNotificacion(TipoNotificacion.DESCUENTO)) {
				c.recibirNotificacionTipo("Nuevo descuento disponible " + d.getNombre(), TipoNotificacion.DESCUENTO);
			}
		}
	}

	/**
	 * Registra un nuevo cliente si los datos son válidos
	 *
	 * @param nickname el nickname del nuevo cliente
	 * @param password la contraseña elegida
	 * @param dni      el dni del cliente
	 * @return el cliente creado o null si no se pudo registrar
	 */
	public Cliente registrarNuevoCliente(String nickname, String password, String dni) {
		if (nickname == null || nickname.isBlank() || password == null || password.isBlank() || dni == null
				|| dni.isBlank()) {
			return null;
		}
		nickname = nickname.trim();
		dni = dni.trim();
		if (!dniTieneFormatoValido(dni)) {
			System.out.println("El DNI no tiene el formato correcto (8 dígitos y 1 letra).");
			return null;
		}
		if (this.existeUsuarioConDNI(dni)) {
			System.out.println("Error: Ya existe un cliente registrado con el DNI: " + dni);
			return null;
		}
		if (this.existeUsuarioConNickname(nickname)) {
			System.out.println("Ya existe un usuario con el nickname: " + nickname);
			return null;
		}
		if (!UsuarioRegistrado.validarPassword(password))
			return null;

		Cliente nuevo = new Cliente(nickname, password, dni);
		this.usuarios.add(nuevo);
		nuevo.recibirNotificacionTipo(
				"¡Bienvenido a CheckPoint, " + nickname
						+ "! Te has registrado correctamente, ahora podrás consultar nuestra tienda.",
				TipoNotificacion.CONFIRMACION_RESERVA_CARRITO);
		return nuevo;
	}

	private boolean dniTieneFormatoValido(String dni) {
		if (dni.length() != 9) {
			return false;
		}
		for (int i = 0; i < 8; i++) {
			if (!Character.isDigit(dni.charAt(i))) {
				return false;
			}
		}
		return Character.isLetter(dni.charAt(8));
	}

	/**
	 * Guarda un intercambio ya completado y retira sus productos del catálogo
	 *
	 * @param oferta la oferta que ha terminado realizándose
	 */
	public void registrarIntercambioFinalizado(Oferta oferta) {
		this.intercambiosFinalizados.add(oferta);
		this.catalogoIntercambio.removeAll(oferta.getProductosOfertados());
		this.catalogoIntercambio.removeAll(oferta.getProductosSolicitados());
	}

	/**
	 * Obtiene la lista de empleados de la tienda
	 *
	 * @return la lista de empleados registrados
	 */
	public List<Empleado> obtenerEmpleadosTienda() {
		List<Empleado> listaEmpleados = new ArrayList<>();
		for (UsuarioRegistrado usuario : usuarios) {
			if (usuario instanceof Empleado) {
				listaEmpleados.add((Empleado) usuario);
			}
		}
		return listaEmpleados;
	}

	/**
	 * Obtiene la lista de clientes de la tienda
	 *
	 * @return la lista de clientes registrados
	 */
	public List<Cliente> obtenerClientesTienda() {
		List<Cliente> listaClientes = new ArrayList<>();
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente) {
				listaClientes.add((Cliente) u);
			}
		}
		return listaClientes;
	}

	/**
	 * Añade un producto al stock de venta
	 *
	 * @param nuevo el producto que se quiere añadir
	 */
	public void añadirProducto(ProductoVenta nuevo) {
		if (this.getStockVentas().contains(nuevo))
			return;
		this.getStockVentas().add(nuevo);

		for (Categoria c : nuevo.getCategorias()) {
			for (Cliente cl : obtenerClientesTienda()) {
				cl.notificarProductoNuevoCategoria("Nuevo producto en " + c.getNombre() + ": " + nuevo.getNombre(),
						c.getNombre());
			}
		}
	}

	/**
	 * Registra una solicitud de tasación para un producto de segunda mano
	 *
	 * @param p el producto que queda pendiente de valorar
	 */
	public void solicitarTasacion(Producto2Mano p) {
		this.pendientes_Tasacion.add(p);
		if (!historialProductos2Mano.contains(p)) {
			historialProductos2Mano.add(p);
		}
		for (Empleado empleado : this.obtenerEmpleadosTienda()) {
			if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
				empleado.recibirNotificacion("Hay un nuevo producto para valorar: " + p.getNombre());
			}
		}
	}

	/**
	 * Añade un descuento a la tienda
	 *
	 * @param d el descuento que se quiere registrar
	 */
	// - DESCUENTOS
	public void agregarDescuento(Descuento d) {
		if (d == null) {
			return;
		}
		if (d.estaActivo() && !contieneDescuento(this.descuentosActivos, d)) {
			this.descuentosActivos.add(d);
		}
		if (!contieneDescuento(this.historialDescuentos, d)) {
			this.historialDescuentos.add(d);
		}
	}

	/**
	 * Elimina de la lista activa los descuentos que ya han terminado
	 */
	public void limpiarDescuentosCaducados() {
		List<Descuento> descuentos_finalizados = new ArrayList<>();
		for (Descuento d : this.descuentosActivos) {
			if (!d.estaActivo()) {
				descuentos_finalizados.add(d);
			}
		}
		this.descuentosActivos.removeAll(descuentos_finalizados);
	}

	private void actualizarDescuentosActivos() {
		limpiarDescuentosCaducados();
		for (Descuento descuento : this.historialDescuentos) {
			if (descuento != null && descuento.estaActivo() && !contieneDescuento(this.descuentosActivos, descuento)) {
				this.descuentosActivos.add(descuento);
			}
		}
	}

	private boolean contieneDescuento(List<Descuento> descuentos, Descuento descuento) {
		if (descuentos == null || descuento == null) {
			return false;
		}
		for (Descuento actual : descuentos) {
			if (actual == descuento) {
				return true;
			}
			if (actual != null && actual.getId() != null && actual.getId().equals(descuento.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Publica un producto de segunda mano para intercambio
	 *
	 * @param p el producto que se quiere poner en el catálogo de intercambio
	 */
	public void publicarParaIntercambio(Producto2Mano p) {
		if (p.getValoracion() != null && !this.getCatalogoIntercambio().contains(p)) {
			p.setBloqueado(false);
			this.catalogoIntercambio.add(p);
		}
	}

	// buscar productos de segunda mano, pero que no esten bloqueados
	/*
	 * public List<Producto2Mano> buscarSegundaMano(String query) {
	 * List<Producto2Mano> -ultados = new ArrayList<>(); for (Producto2Mano p :
	 * catalogoIntercambio) { // AHORA FILTRAMOS TAMBIÉN POR VISIBLE if
	 * (p.isVisible() && !p.isBloqueado() &&
	 * p.getNombre().toLowerCase().contains(query.toLowerCase())) {
	 * resultados.add(p); } } return resultados; }
	 */

	/**
	 * Aplica al carrito el primer descuento activo que corresponda
	 *
	 * @param carrito el carrito sobre el que se revisan los descuentos
	 */
	public void aplicarDescuentoPrioritario(Carrito carrito) {
		if (carrito == null) {
			return;
		}
		actualizarDescuentosActivos();

		for (Descuento descuento : this.descuentosActivos) {
			if (!descuento.estaActivo()) {
				continue;
			}

			if (descuento instanceof Regalo) {
				Regalo regalo = (Regalo) descuento;
				if (regalo.aplicaRegalo(carrito)) {
					carrito.setDescuentoAplicado(descuento);
					return;
				}
			} else {
				double totalConDescuento = descuento.aplicarDescuento(carrito);
				if (totalConDescuento < carrito.calcularSubtotal()) {
					carrito.setDescuentoAplicado(descuento);
					return;
				}
			}
		}
		carrito.setDescuentoAplicado(null);
	}

	/**
	 * Muestra por pantalla los descuentos que están activos
	 */
	public void imprimirDescuentosActivos() {
		if (descuentosActivos.isEmpty()) {
			System.out.println("  No hay descuentos activos.");
			return;
		}
		System.out.println("  Descuentos activos (" + descuentosActivos.size() + "):");
		for (Descuento d : descuentosActivos) {
			System.out.println("   [" + d.getId() + "] " + d.getNombre() + " | activo: " + d.estaActivo() + " | desde: "
					+ d.getFechaInicio().toLocalDate() + " " + d.getFechaInicio().toLocalTime().withNano(0) + " hasta: "
					+ d.getFechaFin().toLocalDate() + " " + d.getFechaFin().toLocalTime().withNano(0));
		}
	}

	/**
	 * Muestra por pantalla el historial completo de descuentos
	 */
	public void imprimirHistorialDescuentos() {
		if (historialDescuentos.isEmpty()) {
			System.out.println("  No hay descuentos en el historial.");
			return;
		}
		System.out.println("  Historial de descuentos (" + historialDescuentos.size() + "):");
		for (Descuento d : historialDescuentos) {
			System.out.println("   [" + d.getId() + "] " + d.getNombre() + " | activo: " + d.estaActivo() + " | desde: "
					+ d.getFechaInicio().toLocalDate() + " " + d.getFechaInicio().toLocalTime().withNano(0) + " hasta: "
					+ d.getFechaFin().toLocalDate() + " " + d.getFechaFin().toLocalTime().withNano(0));
		}
	}

	/**
	 * Guarda una venta en el historial de la tienda
	 *
	 * @param pedido el pedido que se quiere registrar
	 */
	// --- GESTIÓN DE VENTAS NUEVAS
	public void registrarVenta(Pedido pedido) {
		this.historialVentas.add(pedido);
	}

	/**
	 * Devuelve la lista de usuarios registrados en la tienda
	 *
	 * @return la lista de usuarios
	 */
	public List<UsuarioRegistrado> getUsuarios() {
		return usuarios;
	}

	/**
	 * Sustituye la lista de usuarios actual
	 *
	 * @param usuarios la nueva lista de usuarios
	 */
	public void setUsuarios(List<UsuarioRegistrado> usuarios) {
		this.usuarios = usuarios;
	}

	/**
	 * Recupera los productos que están pendientes de tasación
	 *
	 * @return la lista de productos pendientes de valorar
	 */
	// getPendientesTasacion eliminado por ser duplicado de getPendientes_Tasacion
	public List<Producto2Mano> getPendientesTasacion() {
		return pendientes_Tasacion;
	}

	/**
	 * Cambia la lista de productos pendientes de tasación
	 *
	 * @param pendientes_Tasacion la nueva lista de productos pendientes
	 */
	public void setPendientes_Tasacion(List<Producto2Mano> pendientes_Tasacion) {
		this.pendientes_Tasacion = pendientes_Tasacion;
	}

	/**
	 * Devuelve el recomendador de la tienda
	 *
	 * @return el recomendador actual
	 */
	public Recomendador getRecomendador() {
		return this.recomendador;
	}

	/**
	 * Cambia el recomendador de la tienda
	 *
	 * @param recomendador el nuevo recomendador
	 */
	public void setRecomendador(Recomendador recomendador) {
		this.recomendador = recomendador;
	}

	/**
	 * Recupera el nombre de la tienda
	 *
	 * @return el nombre de la tienda
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Cambia el nombre de la tienda
	 *
	 * @param nombre el nuevo nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Devuelve el catálogo de productos para intercambio
	 *
	 * @return la lista de productos del catálogo de intercambio
	 */
	public List<Producto2Mano> getCatalogoIntercambio() {
		return catalogoIntercambio;
	}

	/**
	 * Sustituye el catálogo de intercambio actual
	 *
	 * @param catalogoIntercambio la nueva lista de productos
	 */
	public void setCatalogoIntercambio(List<Producto2Mano> catalogoIntercambio) {
		this.catalogoIntercambio = catalogoIntercambio;
	}

	/**
	 * Recupera el historial de ventas de la tienda
	 *
	 * @return la lista de ventas registradas
	 */
	public List<Pedido> getHistorialVentas() {
		return historialVentas;
	}

	/**
	 * Cambia el historial de ventas
	 *
	 * @param historialVentas la nueva lista de ventas
	 */
	public void setHistorialVentas(List<Pedido> historialVentas) {
		this.historialVentas = historialVentas;
	}

	/**
	 * Devuelve los descuentos que están activos ahora mismo
	 *
	 * @return la lista de descuentos activos
	 */
	public List<Descuento> getDescuentosActivos() {
		actualizarDescuentosActivos();
		return descuentosActivos;
	}

	/**
	 * Cambia la lista de descuentos activos
	 *
	 * @param descuentosActivos la nueva lista de descuentos
	 */
	public void setDescuentosActivos(List<Descuento> descuentosActivos) {
		this.descuentosActivos = descuentosActivos;
	}

	/**
	 * Recupera los intercambios que ya se han completado
	 *
	 * @return la lista de intercambios finalizados
	 */
	public List<Oferta> getIntercambiosFinalizados() {
		return intercambiosFinalizados;
	}

	/**
	 * Cambia la lista de intercambios finalizados
	 *
	 * @param intercambiosFinalizados la nueva lista de intercambios
	 */
	public void setIntercambiosFinalizados(List<Oferta> intercambiosFinalizados) {
		this.intercambiosFinalizados = intercambiosFinalizados;
	}

	/**
	 * Devuelve las categorías registradas en la tienda
	 *
	 * @return la lista de categorías
	 */
	public List<Categoria> getCategorias() {
		return categorias;
	}

	/**
	 * Sustituye la lista de categorías
	 *
	 * @param categorias la nueva lista de categorías
	 */
	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	/**
	 * Cambia la instancia única de la tienda
	 *
	 * @param nuevaInstancia la nueva instancia
	 */
	public static void setInstancia(Tienda nuevaInstancia) {
		if (nuevaInstancia == null) {
			instancia = new Tienda();
		} else {
			instancia = nuevaInstancia;
		}
	}

	/**
	 * Recupera el stock de productos de venta
	 *
	 * @return la lista de productos en stock
	 */
	public List<ProductoVenta> getStockVentas() {
		return stockVentas;
	}

	/**
	 * Cambia la lista de productos en stock
	 *
	 * @param stockVentas la nueva lista de productos
	 */
	public void setStockVentas(List<ProductoVenta> stockVentas) {
		this.stockVentas = stockVentas;
	}

	/**
	 * Devuelve el tiempo máximo permitido para un carrito
	 *
	 * @return el tiempo máximo del carrito
	 */
	public int getTiempoMaxCarrito() {
		return tiempoMaxCarrito;
	}

	/**
	 * Cambia el tiempo máximo de un carrito
	 *
	 * @param tiempoMaxCarrito el nuevo tiempo máximo
	 */
	public void setTiempoMaxCarrito(int tiempoMaxCarrito) {
		this.tiempoMaxCarrito = tiempoMaxCarrito;
	}

	/**
	 * Recupera el tiempo máximo de una oferta
	 *
	 * @return el tiempo máximo de la oferta
	 */
	public int getTiempoMaxOferta() {
		return tiempoMaxOferta;
	}

	/**
	 * Cambia el tiempo máximo de una oferta
	 *
	 * @param tiempoMaxOferta el nuevo tiempo máximo
	 */
	public void setTiempoMaxOferta(int tiempoMaxOferta) {
		this.tiempoMaxOferta = tiempoMaxOferta;
	}

	/**
	 * Devuelve el tiempo máximo permitido para pagar un pedido
	 *
	 * @return el tiempo máximo de pago
	 */
	public int getTiempoMaxPago() {
		return tiempoMaxPago;
	}

	/**
	 * Cambia el tiempo máximo para realizar el pago
	 *
	 * @param tiempoMaxPago el nuevo tiempo máximo
	 */
	public void setTiempoMaxPago(int tiempoMaxPago) {
		this.tiempoMaxPago = tiempoMaxPago;
	}

	/**
	 * Recupera el historial completo de descuentos
	 *
	 * @return la lista del historial de descuentos
	 */
	public List<Descuento> getHistorialDescuentos() {
		return historialDescuentos;
	}

	/**
	 * Cambia el historial de descuentos
	 *
	 * @param historialDescuentos la nueva lista del historial
	 */
	public void setHistorialDescuentos(List<Descuento> historialDescuentos) {
		this.historialDescuentos = historialDescuentos;
	}

	/**
	 * Indica si los tiempos del sistema están configurados
	 *
	 * @return true si los tres tiempos son mayores que 0, false en caso contrario
	 */
	public boolean isSistemaTiemposConfigurando() {
		return this.tiempoMaxCarrito > 0 && this.tiempoMaxOferta > 0 && this.tiempoMaxPago > 0;
	}

	/**
	 * Devuelve los usuarios que tienen la sesión iniciada
	 *
	 * @return la lista de usuarios con sesión activa
	 */
	public List<UsuarioRegistrado> getUsuariosConSesionActiva() {
		return usuariosConSesionActiva;
	}

	/**
	 * Cambia la lista de usuarios con sesión activa
	 *
	 * @param usuariosConSesionActiva la nueva lista de usuarios conectados
	 */
	public void setUsuariosConSesionActiva(List<UsuarioRegistrado> usuariosConSesionActiva) {
		this.usuariosConSesionActiva = usuariosConSesionActiva;
	}

	/**
	 * Recupera el historial de notificaciones
	 *
	 * @return la lista de notificaciones registradas
	 */
	public List<Notificacion> getHistorialNotificaciones() {
		return historialNotificaciones;
	}

	/**
	 * Devuelve el precio actual de la tasación
	 *
	 * @return el precio de la tasación
	 */
	public double getPrecioTasacion() {
		return precioValoracion;
	}

	/**
	 * Cambia el precio de la tasación
	 *
	 * @param precioTasacion el nuevo precio
	 */
	public void setPrecioTasacion(double precioTasacion) {
		if (precioTasacion <= 5) {
			return;
		}
		this.precioValoracion = precioTasacion;
	}

	/**
	 * Deja la tienda vacía y preparada para empezar de nuevo. Se usa sobre todo
	 * para pruebas o para reiniciar los datos de ejemplo.
	 */
	public void vaciarTienda() {
		Estadistica est = Estadistica.getInstancia();
		this.estadistica = est;

		est.setnProductosVentas(1);
		est.setnUsuarioRegistrado(1);
		est.setnUsuarioNoRegistrado(1);
		est.setnProducto2Mano(1);
		est.setnVentas(1);
		est.setnDescuentos(1);
		est.setnIntercambiosFinalizados(1);
		est.setnCategorias(1);
		est.setnCarritos(1);
		est.setnReseñas(1);
		est.setnTasacionesCobradas(0);
		est.setnNotificaciones(1);

		if (this.usuarios == null) {
			this.usuarios = new ArrayList<>();
		} else {
			this.usuarios.clear();
		}

		Gestor gestor = new Gestor();
		this.usuarios.add(gestor);

		if (this.usuariosConSesionActiva == null) {
			this.usuariosConSesionActiva = new ArrayList<>();
		} else {
			this.usuariosConSesionActiva.clear();
		}

		this.usuariosConSesionActiva.add(gestor);

		if (this.stockVentas != null) {
			this.stockVentas.clear();
		}

		if (this.catalogoIntercambio != null) {
			this.catalogoIntercambio.clear();
		}

		if (this.pendientes_Tasacion != null) {
			this.pendientes_Tasacion.clear();
		}

		if (this.categorias != null) {
			this.categorias.clear();
		}

		if (this.descuentosActivos != null) {
			this.descuentosActivos.clear();
		}

		if (this.historialDescuentos != null) {
			this.historialDescuentos.clear();
		}

		if (this.historialVentas != null) {
			this.historialVentas.clear();
		}

		if (this.historialNotificaciones != null) {
			this.historialNotificaciones.clear();
		}

		if (this.intercambiosFinalizados != null) {
			this.intercambiosFinalizados.clear();
		}

		if (this.historialProductos2Mano != null) {
			this.historialProductos2Mano.clear();
		}

		this.recomendador = new Recomendador();

		this.tiempoMaxCarrito = 0;
		this.tiempoMaxOferta = 0;
		this.tiempoMaxPago = 0;
		this.precioValoracion = 10;

		if (this.comprobadorTiempos != null) {
			this.comprobadorTiempos.cerrarGestorTiempo();
			this.comprobadorTiempos = null;
		}
	}

	/**
	 * Busca varias categorías a partir de sus nombres
	 *
	 * @param nombres los nombres de las categorías que se quieren buscar
	 * @return la lista de categorías encontradas
	 */
	public ArrayList<Categoria> seleccionarCategorias(String... nombres) {
		ArrayList<Categoria> lista = new ArrayList<>();
		for (String nombre : nombres) {
			Categoria c = buscarCategoriaPorNombre(nombre);
			if (c != null)
				lista.add(c);
		}
		return lista;
	}

	/**
	 * Devuelve el historial de productos de segunda mano
	 *
	 * @return la lista de productos guardados en el historial
	 */
	public List<Producto2Mano> getHistorialProductos2Mano() {
		return historialProductos2Mano;
	}

	/**
	 * Cambia el historial de productos de segunda mano
	 *
	 * @param historialProductos2Mano la nueva lista del historial
	 */
	public void setHistorialProductos2Mano(List<Producto2Mano> historialProductos2Mano) {
		this.historialProductos2Mano = historialProductos2Mano;
	}

	/**
	 * Devuelve el comprobador de tiempos de la tienda
	 *
	 * @return el comprobador de tiempos actual
	 */
	public ComprobadorTiempos getComprobadorTiempos() {
		if (comprobadorTiempos == null) {
			comprobadorTiempos = new ComprobadorTiempos();
		}
		return comprobadorTiempos;
	}

	/**
	 * Crea de nuevo el comprobador de tiempos
	 */
	public void reiniciarComprobadorTiempos() {
		if (this.comprobadorTiempos != null) {
			this.comprobadorTiempos.cerrarGestorTiempo();
		}
		this.comprobadorTiempos = new ComprobadorTiempos();
	}

	public Empleado registrarNuevoEmpleado(String nickname, String password) {
		if (nickname == null || nickname.isBlank() || password == null || password.isBlank()) {
			return null;
		}
		nickname = nickname.trim();
		if (!UsuarioRegistrado.validarPassword(password)) {
			return null;
		}

		for (UsuarioRegistrado u : usuarios) {
			if (u.getNickname().equalsIgnoreCase(nickname)) {
				System.out.println("Ese nickname ya está en uso.");
				return null;
			}
		}

		Empleado nuevo = new Empleado(nickname, password);

		nuevo.asignarPermiso(TipoPermisos.GESTION_STOCK);
		nuevo.asignarPermiso(TipoPermisos.GESTION_CATEGORIAS);
		nuevo.asignarPermiso(TipoPermisos.GESTION_PACKS);
		nuevo.asignarPermiso(TipoPermisos.MODIFICAR_PRODUCTO);
		nuevo.asignarPermiso(TipoPermisos.GESTION_PEDIDOS);
		nuevo.asignarPermiso(TipoPermisos.VALORACION_PRODUCTOS);
		nuevo.asignarPermiso(TipoPermisos.CONFIRMACION_INTERCAMBIO);
		nuevo.asignarPermiso(TipoPermisos.ENTREGA_PEDIDOS);

		usuarios.add(nuevo);
		return nuevo;
	}

	/**
	 * Método llamado automáticamente cuando se guarda la tienda en fichero.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		inicializarCamposNulos();
		this.estadistica = Estadistica.getInstancia();
		out.defaultWriteObject();
	}

	/**
	 * Método llamado automáticamente cuando se carga la tienda desde fichero.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		inicializarCamposNulos();
		actualizarDescuentosActivos();

		if (this.estadistica == null) {
			this.estadistica = Estadistica.getInstancia();
		} else {
			Estadistica.setInstancia(this.estadistica);
		}

		this.usuariosConSesionActiva = new ArrayList<>();
		this.comprobadorTiempos = null;

		cerrarSesionesUsuarios();
	}

	/**
	 * Evita que las listas principales queden a null al guardar/cargar.
	 */
	private void inicializarCamposNulos() {
		if (this.nombre == null) {
			this.nombre = "CheckPoint";
		}

		if (this.usuarios == null) {
			this.usuarios = new ArrayList<>();
		}

		if (this.stockVentas == null) {
			this.stockVentas = new ArrayList<>();
		}

		if (this.catalogoIntercambio == null) {
			this.catalogoIntercambio = new ArrayList<>();
		}

		if (this.historialVentas == null) {
			this.historialVentas = new ArrayList<>();
		}

		if (this.pendientes_Tasacion == null) {
			this.pendientes_Tasacion = new ArrayList<>();
		}

		if (this.descuentosActivos == null) {
			this.descuentosActivos = new ArrayList<>();
		}

		if (this.historialDescuentos == null) {
			this.historialDescuentos = new ArrayList<>();
		}

		if (this.intercambiosFinalizados == null) {
			this.intercambiosFinalizados = new ArrayList<>();
		}

		if (this.categorias == null) {
			this.categorias = new ArrayList<>();
		}

		if (this.historialNotificaciones == null) {
			this.historialNotificaciones = new ArrayList<>();
		}

		if (this.historialProductos2Mano == null) {
			this.historialProductos2Mano = new ArrayList<>();
		}

		if (this.recomendador == null) {
			this.recomendador = new Recomendador();
		}

		if (this.usuariosConSesionActiva == null) {
			this.usuariosConSesionActiva = new ArrayList<>();
		}
	}

	/**
	 * Al cargar la tienda, nadie debería seguir con sesión iniciada.
	 */
	private void cerrarSesionesUsuarios() {
		if (this.usuarios == null) {
			return;
		}

		for (UsuarioRegistrado usuario : this.usuarios) {
			if (usuario != null) {
				usuario.setSesionIniciada(false);
			}
		}
	}

	/**
	 * Devuelve solo las categorías activas (no eliminadas). Lo usan los combos y
	 * filtros de la GUI.
	 *
	 * @return lista de categorías activas
	 */
	public List<Categoria> getCategoriasActivas() {
		List<Categoria> activas = new ArrayList<>();
		for (Categoria c : categorias) {
			if (!c.isEliminada())
				activas.add(c);
		}
		return activas;
	}
}
