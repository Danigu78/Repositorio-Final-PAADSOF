package usuarios;

import java.io.*;
import java.time.*;
import java.util.*;

import excepciones.*;
import ventas.*;
import productos.*;
import tienda.*;

/**
 * Representa al gestor de la tienda. El gestor puede administrar empleados,
 * productos, descuentos, categorías y consultar estadísticas.
 * 
 * @author Daniel Gonzalez Ureta
 * @version 1.0
 */

public class Gestor extends UsuarioRegistrado implements Serializable {

	private static final long serialVersionUID = 1L;
	/** Nickname inicial del gestor */
	private static final String NICKNAME_INICIAL = "admin_Gestor";
	/** Contraseña inicial del gestor */
	private static final String PASSWORD_INICIAL = "Admin@1234";
	/** Motor encargado de calcular estadísticas */
	private transient MotorEstadistico motorEstadistico;

	/**
	 * Constructor del gestor. Inicializa el gestor con valores por defecto y sesión
	 * iniciada.
	 */
	public Gestor() {
		super(NICKNAME_INICIAL, PASSWORD_INICIAL);
		this.motorEstadistico = new MotorEstadistico();
		this.sesionIniciada = true;
	}

	/*
	 * // @Override public void mostrarPanelPrincipal() {
	 * System.out.println("--- PANEL DE CONTROL DEL GESTOR ---");
	 * System.out.println("1. Gestionar Empleados y Permisos");
	 * System.out.println("2. Configurar Parámetros del Sistema");
	 * System.out.println("3. Gestión de Descuentos y Precios");
	 * System.out.println("4. Ver Estadísticas de Rendimiento"); }
	 */
	/**
	 * Busca un empleado por su id.
	 *
	 * @param id identificador del empleado
	 * @return el empleado si existe, null en caso contrario
	 */
	private Empleado buscarEmpleadoporId(String id) {
		if (id == null || id.isBlank()) {
			return null;
		}
		for (UsuarioRegistrado u : Tienda.getInstancia().getUsuarios()) {
			if (u instanceof Empleado && u.getId().equals(id)) {
				return (Empleado) u;
			}
		}
		return null;
	}

	/**
	 * Valida un rango de fechas.
	 *
	 * @param inicio fecha de inicio
	 * @param fin    fecha de fin
	 * @return true si el rango es válido, false en caso contrario
	 */
	private boolean validarFechas(LocalDateTime inicio, LocalDateTime fin) {
		if (inicio == null || fin == null) {
			System.out.println("Las fechas no pueden ser null");
			return false;
		}
		if (!fin.isAfter(inicio)) {
			System.out.println("La fecha de fin debe ser posterior a la de inicio.");
			return false;
		}
		return true;
	}

	/**
	 * Busca una categoría por su nombre.
	 *
	 * @param name nombre de la categoría
	 * @return la categoría si existe, null si no se encuentra
	 */
	public Categoria buscarCategoriaPorNombre(String name) {
		if (name == null || name.isBlank()) {
			System.out.println("El nombre de la categoria no puede estar vacio");
			return null;
		}
		for (Categoria cat : Tienda.getInstancia().getCategorias()) {
			if (cat.getNombre().equals(name)) {
				return cat;
			}
		}
		System.out.println("No hay ninguna categoria de productos con ese nombre");
		return null;
	}

	/**
	 * Da de alta un nuevo empleado.
	 *
	 * @param nickname nombre del empleado
	 * @param password contraseña del empleado
	 * @return true si se crea correctamente, false si hay error
	 */
	public boolean darDeAltaEmpleados(String nickname, String password) {
		if (nickname == null || password == null) {
			System.out.println("Error en la creacion del empleado. Los parametros son null.");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		if (tienda.existeUsuarioConNickname(nickname)) {
			System.out.println("Error: El nickname '" + nickname + "' ya está en uso.");
			return false;
		}
		Empleado nuevoEmpleado = new Empleado(nickname, password);

		tienda.getUsuarios().add(nuevoEmpleado);
		System.out.println("Empleado con id " + nuevoEmpleado.getId() + " ha sido dado de alta en la aplicacion");
		return true;
	}

	/**
	 * Crea una lista de permisos.
	 *
	 * @param permisos permisos a añadir
	 * @return lista con los permisos indicados
	 */
	public List<TipoPermisos> crearListaPermisos(TipoPermisos... permisos) {
		List<TipoPermisos> lista = new ArrayList<>();
		for (TipoPermisos p : permisos) {
			lista.add(p);
		}
		return lista;
	}

	/**
	 * Da de alta un empleado con permisos.
	 *
	 * @param nickname nombre del empleado
	 * @param password contraseña
	 * @param permisos lista de permisos
	 * @return true si se crea correctamente
	 */
	public boolean darDeAltaEmpleados_Permisos(String nickname, String password, List<TipoPermisos> permisos) {
		if (nickname == null || password == null) {
			System.out.println("Error. El nombre y la contraseña que se le quiere asignar no pueden ser null");
			return false;
		}
		if (nickname.isBlank() || password.isBlank()) {
			System.out.println("Error. La contraseña o el nickname no pueden estar vacios");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		if (tienda.existeUsuarioConNickname(nickname)) {
			System.out.println("Error: El nickname '" + nickname + "' ya está en uso.");
			return false;
		}
		Empleado nuevo = new Empleado(nickname, password);

		if (permisos != null) {
			for (TipoPermisos per : permisos) {
				nuevo.asignarPermiso(per);
			}
		}

		tienda.getUsuarios().add(nuevo);
		System.out.println("Empleado '" + nickname + "' dado de alta con id " + nuevo.getId() + ".");
		return true;
	}

	/**
	 * Da de baja (despide) a un empleado.
	 *
	 * @param idEmpleado id del empleado
	 * @return true si se realiza correctamente
	 */
	public boolean darDeBajaAEmpleado(String idEmpleado) {
		Empleado e = buscarEmpleadoporId(idEmpleado);
		if (e == null) {
			System.out.println("El empleado no puede ser null");
			return false;
		}
		e.getPermisos().clear();
		e.setDespedido(true);
		if (Tienda.getInstancia().getUsuariosConSesionActiva().contains(e)) {
			e.logout();
			System.out.println("Se cierra la sesion del empleado " + e.getNickname() + ".");
		}
		System.out.println("El empleado " + e.getNickname() + "ha sido despedido de la tienda.");
		return true;
	}

	/**
	 * Asigna un permiso a un empleado.
	 *
	 * @param idEmpleado id del empleado
	 * @param permiso    permiso a asignar
	 * @return true si se asigna correctamente
	 */
	public boolean asignarPermiso(String idEmpleado, TipoPermisos permiso) {
		if (permiso == null) {
			System.out.println("El permiso no puede ser null.");
			return false;
		}
		Empleado empleado = buscarEmpleadoporId(idEmpleado);
		if (empleado == null) {
			System.out.println("No hay ningun empleado con ese id");
			return false;
		}
		if (empleado.getPermisos().contains(permiso)) {
			System.out.println("El empleado con id " + empleado.getId()
					+ " ya tiene el permiso que se le esta intentando asignar. ");
			return false;
		}
		empleado.asignarPermiso(permiso);
		return true;
	}

	/**
	 * Retira un permiso a un empleado.
	 *
	 * @param idEmpleado id del empleado
	 * @param permiso    permiso a retirar
	 * @return true si se retira correctamente
	 */
	public boolean retirarPermiso(String idEmpleado, TipoPermisos permiso) {
		Empleado empleado = buscarEmpleadoporId(idEmpleado);
		if (empleado == null) {
			System.out.println("No hay ningun empleado con ese id");
			return false;
		}
		if (!empleado.getPermisos().contains(permiso)) {
			System.out.println("El empleado con id " + empleado.getId()
					+ " no tiene el permiso que se le esta intentando retirar. ");
			return false;
		}
		empleado.quitarPermiso(permiso);
		return true;
	}

	// GESTION DE LOS TIEMPOS MAXIMOS DE LA APLICACION Y PRECIO VALORACION

	/**
	 * Configura los tiempos del sistema.
	 *
	 * @param tOferta  tiempo máximo de oferta
	 * @param tCarrito tiempo máximo de carrito
	 * @param tPago    tiempo máximo de pago
	 * @return true si se configura correctamente
	 */
	public boolean configurarTiemposSistema(int tOferta, int tCarrito, int tPago) {
		if (tOferta <= 0 || tCarrito <= 0 || tPago <= 0) {
			System.out.println("Todos los tiempos deben ser mayores que 0");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxCarrito(tCarrito);
		Tienda.getInstancia().setTiempoMaxOferta(tOferta);
		Tienda.getInstancia().setTiempoMaxPago(tPago);
		return true;
	}

	/**
	 * Establece el tiempo máximo de oferta.
	 *
	 * @param tiempo tiempo máximo
	 * @return true si se establece correctamente
	 */
	public boolean setTiempoMaxOferta(int tiempo) {
		if (tiempo <= 0) {
			System.out.println("El tiempo debe ser mayor que 0.");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxOferta(tiempo);
		return true;
	}

	/**
	 * Establece el tiempo máximo de oferta.
	 *
	 * @param tiempo tiempo máximo
	 * @return true si se establece correctamente
	 */
	public boolean setTiempoMaxCarrito(int tiempo) {
		if (tiempo <= 0) {
			System.out.println("El tiempo debe ser mayor que 0.");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxCarrito(tiempo);
		return true;
	}

	/**
	 * Establece el tiempo máximo de pago.
	 *
	 * @param tiempo tiempo máximo
	 * @return true si se establece correctamente
	 */
	public boolean setTiempoMaxPago(int tiempo) {
		if (tiempo <= 0) {
			System.out.println("El tiempo debe ser mayor que 0.");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxPago(tiempo);
		return true;
	}

	/**
	 * Establece el precio de tasación.
	 *
	 * @param precio precio de tasación
	 * @return true si se establece correctamente
	 */
	public boolean setPrecioTasacion(double precio) {
		if (precio <= 5) {
			System.out.println("El precio de tasación debe ser mayor que 5");
			return false;
		}
		Tienda.getInstancia().setPrecioTasacion(precio);
		return true;
	}

	// MODIFICACION DE LOS PRECIOS
	/**
	 * Modifica el precio de un producto.
	 *
	 * @param idProductoVenta id del producto
	 * @param nuevoPrecio     nuevo precio
	 * @return true si se modifica correctamente
	 */
	public boolean modificarPrecioProducto(String idProductoVenta, double nuevoPrecio) {
		if (idProductoVenta == null || idProductoVenta.isBlank()) {
			System.out.println("El id del producto no puede estar vacío.");
			return false;
		}
		if (nuevoPrecio <= 0) {
			System.out.println("El precio de los productos debe ser mayor que 0");
			return false;
		}
		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProductoVenta);
		if (p == null) {
			System.out.println("No existe ningun producto venta con id: " + idProductoVenta);
			return false;
		}
		if (p instanceof Pack) {// Si es un pack comprobamos que el precio sea al menos un euro mas barato que
								// el precio que tendria la suma individual de los productos en el pack
			Pack pack = (Pack) p;
			double sumaProductosindividuales = pack.calcularSumaProductos();
			if (nuevoPrecio >= sumaProductosindividuales - 1) {
				System.out.println(
						"Estas modificando el precio de un pack y el precio de los packs tiene que ser al menos un euro menor que el precio que sumarian individualmente los productos que contiene el pack.La suma indicidual de los productos es : "
								+ sumaProductosindividuales);
				return false;
			}
		}
		p.setPrecioOficial(nuevoPrecio);
		System.out.println("El precio del producto con id: " + idProductoVenta + " y nombre: " + p.getNombre()
				+ " ha sido modificado por el gestor correctamente. Ahora vale " + nuevoPrecio + ". ");
		return true;
	}

	// GESTION DE LOS DESCUENTOS
	/**
	 * Crea un descuento por cantidad.
	 *
	 * @param nombre         nombre del descuento
	 * @param idProducto     id del producto
	 * @param cantidadMinima cantidad mínima requerida
	 * @param porcentaje     porcentaje de descuento
	 * @param inicio         fecha de inicio
	 * @param fin            fecha de fin
	 * @return true si se crea correctamente
	 */
	public boolean crearDescuentoCantidad(String nombre, String idProducto, int cantidadMinima, double porcentaje,
			LocalDateTime inicio, LocalDateTime fin) {

		if (!validarFechas(inicio, fin)) {
			return false;
		}
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacio");
			return false;
		}
		if (cantidadMinima <= 1) {
			System.out.println("La cantidad minima para poder crear un descuento es de dos unidades");
			return false;
		}
		if (Tienda.getInstancia().buscarProductoVentaPorId(idProducto) == null) {
			return false;
		}

		Descuento d = new DescuentoCantidad(nombre, inicio, fin, cantidadMinima, porcentaje);
		Tienda.getInstancia().agregarDescuento(d);
		Tienda.getInstancia().notificarDescuento(d);
		System.out
				.println("Descuento por cantidad agregado correctamente sobre el producto con id:" + idProducto + ". ");
		return true;
	}

	/**
	 * Crea un descuento por cantidad.
	 *
	 * @param nombre         nombre del descuento
	 * @param idProducto     id del producto
	 * @param cantidadMinima cantidad mínima requerida
	 * @param porcentaje     porcentaje de descuento
	 * @param inicio         fecha de inicio
	 * @param fin            fecha de fin
	 * @return true si se crea correctamente
	 */
	public boolean crearDescuentoVolumen(String nombre, double precioMinimo, double porcentaje, LocalDateTime inicio,
			LocalDateTime fin) {
		if (!validarFechas(inicio, fin)) {
			return false;
		}
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacio");
			return false;
		}
		if (precioMinimo <= 20) {// Ponemos minimo 20 euros
			System.out.println(
					"Para crear un descuento por volumen de gasto el precio total de la compra debe ser al menos de 20 euros");
			return false;
		}
		Descuento desc = new DescuentoVolumen(nombre, inicio, fin, precioMinimo, porcentaje);
		Tienda.getInstancia().agregarDescuento(desc);
		Tienda.getInstancia().notificarDescuento(desc);
		System.out.println("Se ha creado un descuento por volumen de gasto superior a " + precioMinimo);
		return true;
	}

	/**
	 * Crea un descuento por categoría.
	 *
	 * @param nombre          nombre del descuento
	 * @param nombreCategoria categoría afectada
	 * @param porcentaje      porcentaje de descuento
	 * @param inicio          fecha de inicio
	 * @param fin             fecha de fin
	 * @return true si se crea correctamente
	 */
	public boolean crearDescuentoCategoria(String nombre, String nombreCategoria, double porcentaje,
			LocalDateTime inicio, LocalDateTime fin) {
		if (!validarFechas(inicio, fin)) {
			return false;
		}
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacio");
			return false;
		}
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			System.out
					.println("El nombre de la categoria sobre la que se va a plicar el descuento no puede estar vacio");
			return false;
		}
		Categoria cat = buscarCategoriaPorNombre(nombreCategoria);
		if (cat == null) {
			return false;
		}
		Descuento descuento = new DescuentoCategoria(nombreCategoria, inicio, fin, cat, porcentaje);
		Tienda.getInstancia().agregarDescuento(descuento);
		Tienda.getInstancia().notificarDescuento(descuento);
		System.out
				.println("Descuento para los productos de la categoria " + cat.getNombre() + " creado correctamente.");
		return true;
	}

	/**
	 * Crea un descuento de tipo regalo.
	 *
	 * @param nombre             nombre del descuento
	 * @param idProductoRegalado producto regalado
	 * @param gastoNecesario     gasto mínimo requerido
	 * @param inicio             fecha de inicio
	 * @param fin                fecha de fin
	 * @return true si se crea correctamente
	 */
	public boolean crearDescuentoRegalo(String nombre, String idProductoRegalado, double gastoNecesario,
			LocalDateTime inicio, LocalDateTime fin) {

		if (!validarFechas(inicio, fin))
			return false;
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacío.");
			return false;
		}

		if (gastoNecesario <= 35) {
			System.out.println("El gasto necesario debe ser mayor que 35.");
			return false;
		}

		ProductoVenta productoRegalado = Tienda.getInstancia().buscarProductoVentaPorId(idProductoRegalado);
		if (productoRegalado == null) {
			System.out.println("Error: El producto con ID " + idProductoRegalado + " no existe.");
			return false;
		}

		Descuento d = new Regalo(nombre, inicio, fin, gastoNecesario, productoRegalado);
		Tienda.getInstancia().agregarDescuento(d);
		Tienda.getInstancia().notificarDescuento(d);
		System.out.println("Descuento regalo '" + nombre + "' creado correctamente.");
		return true;
	}

	/**
	 * Elimina un descuento.
	 *
	 * @param idDescuento id del descuento
	 * @return true si se elimina correctamente
	 */
	public boolean eliminarDescuento(String idDescuento) {
		if (idDescuento == null || idDescuento.isBlank())
			return false;

		List<Descuento> lista = Tienda.getInstancia().getDescuentosActivos();
		boolean eliminado = lista.removeIf(d -> d.getId().equals(idDescuento));
		if (eliminado) {
			System.out.println("Descuento " + idDescuento + " eliminado correctamente.");
		} else {
			System.out.println("No se encontró el descuento.");
		}
		return eliminado;
	}

	// METODOS RELACIONADOS CON LAS CATEGORIAS
	/**
	 * Crea una nueva categoría.
	 *
	 * @param nombre      nombre de la categoría
	 * @param descripcion descripción
	 * @return true si se crea correctamente
	 */
	public boolean crearCategoria(String nombre, String descripcion) {
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre no puede estar vacio");
			return false;
		}
		if (descripcion == null || descripcion.isBlank()) {
			System.out.println("La descripcion no puede estar vacia");
			return false;
		}
		Categoria c = new Categoria(nombre, descripcion);
		Tienda.getInstancia().getCategorias().add(c);
		System.out.println("La categoria " + nombre + " ha sido creada y añadida correctamente.");
		return true;
	}

	/**
	 * Añade un producto a una categoría.
	 *
	 * @param idProducto id del producto
	 * @param nombreCat  nombre de la categoría
	 * @return true si se añade correctamente
	 */
	public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
		if (idProducto == null || nombreCat == null) {
			System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		ProductoVenta p = tienda.buscarProductoVentaPorId(idProducto);
		if (p == null) {
			System.out.println("No existe ningún producto con id: " + idProducto);
			return false;
		}
		Categoria c = tienda.buscarCategoriaPorNombre(nombreCat);
		if (c == null) {
			System.out.println("No existe ninguna categoría con nombre: " + nombreCat);
			return false;
		}
		try {
			return c.addProducto(p);
		} catch (ProductoYaEnCategoriaException e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Elimina un producto de una categoría.
	 *
	 * @param idProducto id del producto
	 * @param nombreCat  nombre de la categoría
	 * @return true si se elimina correctamente
	 */
	public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
		if (idProducto == null || nombreCat == null) {
			System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		ProductoVenta p = tienda.buscarProductoVentaPorId(idProducto);
		if (p == null) {
			System.out.println("No existe ningún producto con id: " + idProducto);
			return false;
		}
		Categoria c = tienda.buscarCategoriaPorNombre(nombreCat);
		if (c == null) {
			System.out.println("No existe ninguna categoría con nombre: " + nombreCat);
			return false;
		}
		return c.deleteProducto(p);
	}

	/**
	 * Modifica el perfil del gestor (nickname y contraseña).
	 * 
	 * @param nuevoNickname El nuevo nombre de usuario.
	 * @param nuevoPass     La nueva contraseña que debe cumplir requisitos de
	 *                      seguridad.
	 * @return true si se cambió con éxito, false si los datos no son válidos.
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

		System.out.println("Perfil del gestor actualizado con éxito.");
		return true;
	}

	// ESTADÍSTICAS

	/**
	 * Devuelve los clientes con más compras.
	 *
	 * @return lista de clientes
	 */
	public List<Cliente> verClientesTopCompras() {
		List<Cliente> lista = motorEstadistico.obtenerClientesConMasCompras();
		System.out.println("  Ranking por compras (" + lista.size() + " clientes):");
		for (Cliente c : lista) {
			System.out.println("   " + c.getNickname() + " | pedidos completados: " + c.contarPedidosCompletados());
		}
		return lista;
	}

	/**
	 * Devuelve los clientes con más intercambios.
	 *
	 * @return lista de clientes
	 */
	public List<Cliente> verClientesTopIntercambios() {
		List<Cliente> lista = motorEstadistico.obtenerClientesConMasIntercambios();
		System.out.println("  Ranking por intercambios (" + lista.size() + " clientes):");
		for (Cliente c : lista) {
			System.out.println("   " + c.getNickname() + " | intercambios: " + c.contarIntercambios());
		}
		return lista;
	}

	/**
	 * Devuelve los clientes con más pedidos cancelados.
	 *
	 * @return lista de clientes
	 */
	public List<Cliente> verClientesConMasPedidosCancelados() {
		return motorEstadistico.obtenerClientesConMasPedidosCaducados();
	}

	/**
	 * Consulta los ingresos en un rango de fechas.
	 *
	 * @param inicio fecha inicial
	 * @param fin    fecha final
	 * @return ingresos totales
	 * @throws RangoFechasInvalidoException si el rango es incorrecto
	 */
	public double consultarIngresosRango(LocalDate inicio, LocalDate fin) throws RangoFechasInvalidoException {
		double total = motorEstadistico.calcularIngresosRangoFechas(inicio, fin);
		System.out.println("  Ingresos desde " + inicio + " hasta " + fin + ": " + String.format("%.2f", total) + "€");
		return total;
	}

	/**
	 * Consulta los ingresos por meses de un año.
	 *
	 * @param año año a consultar
	 * @return array con ingresos por mes
	 * @throws AñoInvalidoException         si el año no es válido
	 * @throws RangoFechasInvalidoException si hay error en fechas
	 */
	public double[] consultarIngresosPorMeses(int año) throws AñoInvalidoException, RangoFechasInvalidoException {
		double[] porMeses = motorEstadistico.calcularIngresosMesesAño(año);
		String[] meses = { "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
		System.out.println("  Ingresos por mes del año " + año + ":");
		for (int i = 0; i < 12; i++) {
			if (porMeses[i] > 0) {
				System.out.println("   " + meses[i] + ": " + String.format("%.2f", porMeses[i]) + "€");
			}
		}
		return porMeses;
	}

	/**
	 * Consulta los ingresos por meses de un año.
	 *
	 * @param año año a consultar
	 * @return array con ingresos por mes
	 * @throws AñoInvalidoException         si el año no es válido
	 * @throws RangoFechasInvalidoException si hay error en fechas
	 */
	public double[] consultarIngresosPorMesesActual() throws AñoInvalidoException, RangoFechasInvalidoException {
		return consultarIngresosPorMeses(LocalDate.now().getYear());
	}

	/**
	 * Consulta los ingresos totales por ventas.
	 *
	 * @return ingresos totales
	 */
	public double consultarIngresosVenta() {
		double total = motorEstadistico.calcularIngresosVenta();
		System.out.println("  Ingresos totales ventas: " + String.format("%.2f", total) + "€");
		return total;
	}

	/**
	 * Consulta los ingresos por tasación.
	 *
	 * @return ingresos totales de tasación
	 */
	public double consultarIngresosTasacion() {
		double total = motorEstadistico.calcularIngresosTasacion();
		System.out.println("  Ingresos tasacion: " + String.format("%.2f", total) + "€");
		return total;
	}

	/**
	 * Método llamado automáticamente cuando se guarda un Gestor en fichero.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		inicializarCamposNulos();
		out.defaultWriteObject();
	}

	/**
	 * Método llamado automáticamente cuando se carga un Gestor desde fichero.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		inicializarCamposNulos();
	}

	/**
	 * Evita que el motor estadístico quede a null al guardar/cargar.
	 */
	private void inicializarCamposNulos() {
		if (this.motorEstadistico == null) {
			this.motorEstadistico = new MotorEstadistico();
		}
	}

}
