package usuarios;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import excepciones.*;
import intercambios.*;
import ventas.*;
import tienda.*;
import productos.*;

/**
 * Clase que representa a un empleado del sistema de la tienda.
 * 
 * Un empleado puede gestionar el inventario, tramitar pedidos, realizar
 * tasaciones de productos, confirmar intercambios, administrar categorías y
 * packs, así como gestionar notificaciones.
 * 
 * Dependiendo de sus permisos, podrá realizar distintas tareas dentro del
 * sistema. Además, un empleado puede ser despedido, en cuyo caso no podrá
 * operar.
 * 
 * @author Daniel Gonzalez Ureta
 * @version 1.0
 */
public class Empleado extends UsuarioRegistrado {
	/** Lista de notificaciones del empleado */
	protected List<Notificacion> notificaciones;
	/** Lista de notificaciones del empleado */
	private Set<TipoPermisos> permisos;
	/** Indica si el empleado ha sido despedido */
	private boolean despedido;
	/** Valoraciones realizadas por el empleado */
	private List<Valoracion> valoraciones;

	/**
	 * Constructor de la clase Empleado
	 * 
	 * @param nickname Nombre de usuario
	 * @param password Contraseña
	 */
	public Empleado(String nickname, String password) {
		super(nickname, password);
		this.valoraciones = new ArrayList<>();
		this.permisos = new TreeSet<>();
		this.despedido = false;
		this.notificaciones = new ArrayList<>();
	}
	/*
	 * @Override public void mostrarPanelPrincipal() { int i = 5;
	 * System.out.println("--- PANEL DE CONTROL: EMPLEADO ---");
	 * 
	 * System.out.println("1. Gestionar Inventario (Añadir/Modificar productos)");
	 * System.out.println("2. Tramitar Pedidos Pendientes");
	 * 
	 * System.out.println("3. Marcar Pedido como Entregado (Recogida en tienda)");
	 * 
	 * System.out.println("4. Generar Informe de Ventas Mensual"); if
	 * (this.permisos.contains(TipoPermisos.VALORACION_PRODUCTOS)) {
	 * System.out.println(i + ". Tasación de Productos (Peticiones de clientes)");
	 * i++; }
	 * 
	 * if (this.permisos.contains(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
	 * System.out.println(i + ". Confirmar Intercambios Acordados"); i++; }
	 * 
	 * System.out.println(i + ". Cerrar Sesión");
	 * 
	 * }
	 */

	/**
	 * Comprueba si el empleado puede realizar una tarea según su permiso.
	 * 
	 * @param permiso Permiso requerido para la tarea
	 * @return true si el empleado tiene permiso y no ha sido despedido
	 */
	public boolean puedeRealizarTarea(TipoPermisos permiso) {
		if (isDespedido()) {
			System.out.println("El empleado " + this.getNickname() + " ha sido despedido y no puede realizar tareas.");
			return false;
		}
		if (!sesionIniciada) {
			System.out.println("El empleado " + this.getNickname() + " no tiene sesion iniciada.");
			return false;
		}
		return this.permisos.contains(permiso);
	}

	/**
	 * Busca un producto de segunda mano pendiente de tasación por su ID.
	 * 
	 * @param idProducto ID del producto a buscar
	 * @return el producto si se encuentra, null en caso contrario
	 */
	private Producto2Mano buscarProductoPendientePorId(String idProducto) {
		if (idProducto == null || idProducto.isBlank()) {
			System.out.println("El id del producto no puede estar vacío.");
			return null;
		}
		for (Producto2Mano p : Tienda.getInstancia().getPendientesTasacion()) {
			if (p.getId().equals(idProducto))
				return p;
		}
		System.out.println("No existe ningún producto pendiente de tasación con id: " + idProducto);
		return null;
	}

	/**
	 * Busca un pedido por su ID en el historial de ventas de la tienda.
	 * 
	 * @param idPedido ID del pedido
	 * @return el pedido si se encuentra, null si no existe
	 */
	private Pedido buscarPedidoPorId(String idPedido) {
		if (idPedido == null || idPedido.isBlank()) {
			System.out.println("El id del pedido no puede estar vacío.");
			return null;
		}
		for (Pedido p : Tienda.getInstancia().getHistorialVentas()) {
			if (p.getIdPedido().equals(idPedido))
				return p;
		}
		System.out.println("No existe ningún pedido con id: " + idPedido);
		return null;
	}

	/**
	 * Actualiza el stock de un producto existente según los datos de una línea de
	 * fichero.
	 * 
	 * @param tipoProducto Tipo del producto (C, J, F)
	 * @param id           ID del producto
	 * @param partes       Array con los campos de la línea
	 * @param numLinea     Número de línea en el fichero
	 * @param linea        Contenido completo de la línea
	 * @throws FicheroFormatoInvalidoException Si ocurre algún error de formato o
	 *                                         tipo
	 */
	private void actualizarStockDesdeLinea(String tipoProducto, String id, String[] partes, int numLinea, String linea)
			throws FicheroFormatoInvalidoException {
		ProductoVenta existente = this.buscarProductoPorId(id);
		if (existente == null) {
			throw new FicheroFormatoInvalidoException(numLinea, linea, "No existe ningún producto con ID: " + id);
		}
		boolean errorTipo = false;
		if (tipoProducto.equals("C") && !(existente instanceof Comic))
			errorTipo = true;
		else if (tipoProducto.equals("J") && !(existente instanceof JuegoMesa))
			errorTipo = true;
		else if (tipoProducto.equals("F") && !(existente instanceof Figura))
			errorTipo = true;

		if (errorTipo) {
			throw new FicheroFormatoInvalidoException(numLinea, linea, "ERROR: El producto " + id + " no es de tipo "
					+ tipoProducto + " (es un " + existente.getClass().getSimpleName() + ")");
		}
		try {
			int unidades = Integer.parseInt(partes[3].trim());
			if (unidades <= 0) {
				throw new NumberFormatException();
			}
			existente.setStockDisponible(existente.getStockDisponible() + unidades);

		} catch (NumberFormatException e) {
			throw new FicheroFormatoInvalidoException(numLinea, linea, "Unidades inválidas");
		}
	}

	/**
	 * Procesa la creación de un nuevo producto a partir de los datos del fichero.
	 * 
	 * @param tipo     Tipo de producto (C, J, F)
	 * @param nombre   Nombre del producto
	 * @param partes   Array con los campos de la línea del fichero
	 * @param numLinea Número de línea
	 * @param linea    Contenido completo de la línea
	 * @return true si el producto se creó o se actualizó correctamente
	 * @throws FicheroFormatoInvalidoException Si los datos no son válidos
	 */
	private boolean procesarNuevoProducto(String tipo, String nombre, String[] partes, int numLinea, String linea)
			throws FicheroFormatoInvalidoException {
		// Verificación de longitud total (las 21 columnas)
		if (partes.length < 21) {
			throw new FicheroFormatoInvalidoException(numLinea, linea, "Faltan columnas. Deben ser 21.");
		}
		try {
			int unidadesIniciales = Integer.parseInt(partes[3].trim());

			String descripcion = partes[4].trim();
			String rutaImagen = partes[5].trim();
			if (rutaImagen.isEmpty())
				rutaImagen = "default.png";

			double precio = Double.parseDouble(partes[6].trim());

			if (precio <= 0 || unidadesIniciales < 0) {
				throw new NumberFormatException();
			}
			ArrayList<Categoria> categorias = new ArrayList<>();
			if (!partes[7].isBlank()) {
				for (String nombreCat : partes[7].split(",")) {
					Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCat);
					if (categoria != null) {
						categorias.add(categoria);
					}
				}
			}
			for (ProductoVenta p : Tienda.getInstancia().getStockVentas()) {
				// Comprobamos los campos "tontorrones" que harían que sea el mismo producto
				if (p.getNombre().equalsIgnoreCase(nombre) && p.getPrecioOficial() == precio
						&& p.getDescripcion().equalsIgnoreCase(descripcion)) {

					// Si además el tipo coincide (Comic, Juego, etc.)
					if ((tipo.equals("C") && p instanceof Comic) || (tipo.equals("J") && p instanceof JuegoMesa)
							|| (tipo.equals("F") && p instanceof Figura)) {

						System.err.println("[INFO] Detectado producto idéntico: " + nombre + ". Sumando stock.");
						p.setStockDisponible(p.getStockDisponible() + unidadesIniciales);
						return true; // Terminamos aquí, no creamos un nuevo PV-X
					}
				}
			}
			boolean creado;
			switch (tipo) {

			case "C":
				if (partes[8].isBlank() || partes[9].isBlank() || partes[10].isBlank()) {
					throw new FicheroFormatoInvalidoException(numLinea, linea,
							"Faltan datos obligatorios del Cómic (Páginas, Editorial o Año)");
				}

				creado = añadirProducto_nuevo("C", nombre, descripcion, rutaImagen, precio, unidadesIniciales,
						categorias, Integer.parseInt(partes[8].trim()), // numpaginas
						partes[9].trim(), // editorial
						Integer.parseInt(partes[10].trim()), // añoPublicacion
						0, 0, 0, null, null, 0, 0, 0, 0, null);
				break;

			case "J":
				if (partes.length < 15)
					throw new FicheroFormatoInvalidoException(numLinea, linea, "Faltan campos Juego");

				creado = añadirProducto_nuevo("J", nombre, descripcion, rutaImagen, precio, unidadesIniciales,
						categorias, 0, null, 0, 0, 0, 0, null, null, Integer.parseInt(partes[11].trim()),
						Integer.parseInt(partes[12].trim()), Integer.parseInt(partes[13].trim()),
						Integer.parseInt(partes[14].trim()), partes[15].trim());
				break;

			case "F":
				if (partes[16].isBlank() || partes[17].isBlank() || partes[18].isBlank() || partes[19].isBlank()
						|| partes[20].isBlank()) {
					throw new FicheroFormatoInvalidoException(numLinea, linea,
							"Faltan datos obligatorios de la Figura (Dimensiones, Material o Marca)");
				}

				creado = añadirProducto_nuevo("F", nombre, descripcion, rutaImagen, precio, unidadesIniciales,
						categorias, 0, null, 0, Double.parseDouble(partes[16].trim()),
						Double.parseDouble(partes[17].trim()), Double.parseDouble(partes[18].trim()), partes[19].trim(),
						partes[20].trim(), 0, 0, 0, 0, null);
				break;

			default:
				throw new TipoProductoDesconocidoException(numLinea, linea, tipo);
			}
			if (!creado) {
				throw new FicheroFormatoInvalidoException(numLinea, linea, "Error interno al crear producto");
			}
			return true;

		} catch (NumberFormatException e) {
			throw new FicheroFormatoInvalidoException(numLinea, linea, "Error en formato numérico");
		}
	}

	/**
	 * Realiza la tasación de un producto de segunda mano.
	 * 
	 * @param idProducto ID del producto
	 * @param precio     Precio tasado
	 * @param estado     Estado del producto
	 */
	public void tasarProducto(String idProducto, double precio, EstadoProducto estado) {

		if (!puedeRealizarTarea(TipoPermisos.VALORACION_PRODUCTOS)) {
			return;
		}

		Producto2Mano p = buscarProductoPendientePorId(idProducto);
		if (p == null) {
			System.out.println("El producto " + idProducto + " no está pendiente de tasación.");
			return;
		}

		boolean aceptado;
		try {
			aceptado = p.valorar(precio, estado, this);
		} catch (ValoracionInvalidaException e) {
			System.out.println("  Error en tasacion: " + e.getMessage());
			return;
		}

		Tienda.getInstancia().getPendientesTasacion().remove(p);

		if (!aceptado && estado == EstadoProducto.NO_ACEPTADO) {
			p.getPropietario().recibirNotificacionTipo(
					"El producto " + p.getNombre() + " ha sido rechazado al no cumplir las expectativas.",
					TipoNotificacion.VALORACION_COMPLETADA);
		} else if (aceptado) {
			Tienda.getInstancia().publicarParaIntercambio(p);
			p.getPropietario().recibirNotificacionTipo(
					"El producto " + p.getNombre() + " ha sido tasado con éxito.Ya es visible para los demas clientes",
					TipoNotificacion.VALORACION_COMPLETADA);
		}
	}

	/**
	 * Confirma un intercambio entre usuarios si el empleado tiene permisos.
	 * 
	 * @param o Oferta a confirmar
	 * @return true si la confirmación fue exitosa
	 */
	public boolean confirmarIntercambio(Oferta o) {
		if (!puedeRealizarTarea(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			System.out.println(
					"El empleado " + this.getNickname() + " no tiene permisos para hacer confirmacion de intercambios");
			return false;
		}
		if (o.getEstado() != EstadoOferta.ACEPTADA) {
			this.recibirNotificacion("La oferta no ha sido aceptada por ambos usuarios por lo que no se puede aceptar");
			return false;
		}
		try {
			o.aceptarYEjecutar();
			this.recibirNotificacion("Has confirmado el intercambio entre los usuarios " + o.getOrigen().getNickname()
					+ " y " + o.getDestino().getNickname());
		} catch (OfertaNoDisponibleException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Añade un nuevo producto a la tienda según su tipo y atributos.
	 * 
	 * @param letra           Tipo de producto (C, J, F)
	 * @param nombre          Nombre del producto
	 * @param descripcion     Descripción del producto
	 * @param imagen          Ruta de imagen
	 * @param precioOficial   Precio oficial
	 * @param Stock           Stock inicial
	 * @param categorias      Categorías asociadas
	 * @param numpaginas      Número de páginas (solo cómics)
	 * @param editorial       Editorial (solo cómics)
	 * @param añoPublicacion  Año de publicación (solo cómics)
	 * @param altura          Altura del producto (solo figuras)
	 * @param ancho           Ancho del producto (solo figuras)
	 * @param largo           Largo del producto (solo figuras)
	 * @param material        Material (solo figuras)
	 * @param marca           Marca (solo figuras)
	 * @param minNumjugadores Mínimo jugadores (solo juegos)
	 * @param maxNumjugadores Máximo jugadores (solo juegos)
	 * @param minEdad         Edad mínima (solo juegos)
	 * @param maxEdad         Edad máxima (solo juegos)
	 * @param Estilo          Estilo del juego (solo juegos)
	 * @return true si el producto se creó correctamente
	 */
	public boolean añadirProducto_nuevo(String letra, String nombre, String descripcion, String imagen,
			double precioOficial, int Stock, ArrayList<Categoria> categorias, int numpaginas, String editorial,
			int añoPublicacion, double altura, double ancho, double largo, String material, String marca,
			int minNumjugadores, int maxNumjugadores, int minEdad, int maxEdad, String Estilo) {

		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {

			return false;
		}
		Tienda tienda = Tienda.getInstancia();

		// 1. Validar atributos básicos
		if (nombre == null || precioOficial <= 0 || Stock <= 0 || descripcion == null || imagen == null) {
			System.out.println("Los atributos de producto deben aparecer correctamente");
			return false;
		}
		if (categorias == null) {
			return false;
		}
		boolean flag = true;

		for (Categoria c : categorias) {
			if (!tienda.getCategorias().contains(c)) {
				flag = false;
				break;
			}
		}

		if (!flag) {
			System.out.println("Las categorias que se introduzcan deben existir en la tienda");
			return false;
		}

		// Validar letra ANTES de comprobar existencia
		if (letra == null || letra.length() != 1) {
			this.recibirNotificacion(
					"El tipo de producto que has intentado crear no es correcta. Deben ser Comics(C), Figuras(F) o Juegos(J)");
			return false;
		}

		switch (letra.toUpperCase()) {
		case "C":
			if (numpaginas <= 0 || editorial == null || añoPublicacion <= 0) {
				System.out.println("Estas añadiendo un comic, los atributos deben cumplir las condiciones necesarias");
				return false;
			}
			ProductoVenta comic = new Comic(nombre, descripcion, imagen, precioOficial, Stock, numpaginas, editorial,
					añoPublicacion);
			tienda.añadirProducto(comic);
			for (Categoria cats : categorias) {
				try {
					cats.addProducto(comic);
				} catch (ProductoYaEnCategoriaException e) {
					System.out.println("  Aviso: " + e.getMessage());
				}
			}
			this.recibirNotificacion("Has añadido el comic " + comic.getNombre() + " a la tienda");
			return true;

		case "J":
			if (minEdad <= 0) {
				System.out.println("La edad minima del juego tiene que ser mayor que 0");
				return false;
			}
			if (maxEdad <= 0 || maxEdad > 100) {
				System.out.println("La edad maxima del juego debe estar entre 1 y 100 años");
				return false;
			}
			if (minNumjugadores <= 0) {
				System.out.println("El juego tendrá mínimo 1 jugador");
				return false;
			}
			if (maxNumjugadores <= 0) {
				System.out.println("El juego debe tener por lo menos un jugador");
				return false;
			}
			ProductoVenta juego = new JuegoMesa(nombre, descripcion, imagen, precioOficial, Stock, minNumjugadores,
					maxNumjugadores, minEdad, maxEdad, Estilo);
			tienda.añadirProducto(juego);
			for (Categoria cats : categorias) {
				try {
					cats.addProducto(juego);
				} catch (ProductoYaEnCategoriaException e) {
					System.out.println("  Aviso: " + e.getMessage());
				}
			}
			this.recibirNotificacion("Has añadido el juego " + juego.getNombre() + " a la tienda");
			return true; // <-- faltaba el return Y el break

		case "F":
			if (altura <= 0 || ancho <= 0 || largo <= 0) {
				System.out.println("Las dimensiones deben ser positivas");
				return false;
			}
			if (material == null) {
				System.out.println("Las figuras deben tener material");
				return false;
			}
			if (marca == null) {
				System.out.println("Las figuras deben tener marca");
				return false;
			}
			ProductoVenta figura = new Figura(nombre, descripcion, imagen, precioOficial, Stock, altura, ancho, largo,
					material, marca);
			tienda.añadirProducto(figura);
			for (Categoria cats : categorias) {
				try {
					cats.addProducto(figura);
				} catch (ProductoYaEnCategoriaException e) {
					System.out.println("  Aviso: " + e.getMessage());
				}
			}
			this.recibirNotificacion("Has añadido la figura " + figura.getNombre() + " a la tienda");
			return true;

		default:
			this.recibirNotificacion(
					"El tipo de producto que has intentado crear no es correcta. Deben ser Comics(C), Figuras(F) o Juegos(J)");
			return false;
		}
	}

	/**
	 * Reposición de stock para un producto existente.
	 * 
	 * @param id       ID del producto
	 * @param cantidad Cantidad a añadir
	 * @return true si se repuso correctamente
	 */
	public boolean reponerStockProducto(String id, int cantidad) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {
			System.out.println("No tienes permiso para trabajar con productos");
			return false;
		}
		if (cantidad <= 0) {
			System.out.println("La cantidad a añadir tiene que ser mayor que 0");
			return false;
		}

		Tienda tienda = Tienda.getInstancia();
		for (ProductoVenta p : tienda.getStockVentas()) {
			if (p.getId().equals(id)) {
				// Caso en el que hayamos encontrado un producto que ya exista
				int unidades = p.getStockDisponible();
				unidades = unidades + cantidad;
				p.setStockDisponible(unidades);
				System.out.println("Se han añadiendo las unidades correctamente. Ahora el producto " + p.getId()
						+ " tiene " + p.getStockDisponible() + " unidades en stock.");
				return true;
			}
		}
		System.out.println("Este producto no existe en la lista de productos de venta de la tienda");
		return false;
	}

	/**
	 * Carga productos desde un fichero de texto.
	 * 
	 * @param path Ruta del fichero
	 * @return true si se cargaron los productos correctamente
	 */
	public boolean cargarProductosFicheroTexto(String path) {

		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {
			return false;
		}

		if (path == null || path.isBlank()) {
			System.err.println("La ruta no puede estar vacía");
			return false;
		}

		int productosNuevos = 0;
		int stockActualizado = 0;
		int numLinea = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String linea;
			boolean esCabecera = true;

			while ((linea = br.readLine()) != null) {
				numLinea++;

				if (esCabecera) {
					esCabecera = false;
					continue;
				}

				if (linea.isBlank())
					continue;

				try {
					String[] partes = linea.split(";", -1);

					if (partes.length < 4) {
						throw new FicheroFormatoInvalidoException(numLinea, linea, "Columnas insuficientes");
					}

					String tipo = partes[0].trim().toUpperCase();
					String id = partes[1].trim();
					String nombre = partes[2].trim();

					if (nombre.isBlank()) {
						throw new FicheroFormatoInvalidoException(numLinea, linea, "Nombre vacío");
					}

					// Caso en el que el id aparece.El producto ya existe luegop actualizamos el
					// stock
					if (!id.isEmpty()) {
						actualizarStockDesdeLinea(tipo, id, partes, numLinea, linea);
						stockActualizado++;
						continue;
					}

					// cuando no aparece el id creamos el producto
					// Comprobamos que tenga los atributos comunes de todos los productos venta
					if (partes.length < 7) {
						throw new FicheroFormatoInvalidoException(numLinea, linea,
								"Faltan comunes para crear los productos");
					}

					if (procesarNuevoProducto(tipo, nombre, partes, numLinea, linea)) {
						productosNuevos++;
					}

				} catch (FicheroFormatoInvalidoException e) {
					System.err.println(e.getMessage());
				}
			}

			this.recibirNotificacion("Has añadido productos al sistema desde un fichero de texto. Creados: "
					+ productosNuevos + ", Actualizados: " + stockActualizado);
			System.out.println("El empleado " + this.getId()
					+ " ha  añadido productos al sistema desde un fichero de texto. Creados: " + productosNuevos
					+ ", Actualizados: " + stockActualizado);
			return true;

		} catch (IOException e) {
			System.err.println("Error al leer fichero: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Prepara un pedido para su recogida en tienda.
	 * 
	 * @param idPedido ID del pedido
	 * @return true si el pedido se preparó correctamente
	 */
	public boolean prepararPedido(String idPedido) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PEDIDOS))
			return false;

		Pedido ped = buscarPedidoPorId(idPedido);
		if (ped == null)
			return false;

		if (ped.getEstado() != EstadoPedido.PAGADO) {
			System.out.println("El pedido " + idPedido + " no se ha podido preparar.");
			return false;
		}

		boolean ok = ped.marcarPreparado();
		if (ok) {
			ped.getCliente().recibirNotificacionTipo("Tu pedido con codigo de recogida " + ped.getCodigoRecogida()
					+ " está preparado. Puedes recogerlo.", TipoNotificacion.PEDIDO_LISTO);
		}
		System.out.println(
				"Pedido preparado correctamente por el empleado " + this.getId() + ". Está listo para recoger.");
		return ok;
	}

	/**
	 * Entrega un pedido al cliente mediante código de recogida.
	 * 
	 * @param codigoRecogida Código de recogida del pedido
	 * @return true si el pedido se entregó correctamente
	 */
	public boolean entregarPedido(String codigoRecogida) {

		if (!puedeRealizarTarea(TipoPermisos.ENTREGA_PEDIDOS)) {
			System.out.println("No tienes permiso para entregar con pedidos");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		for (Pedido ped : tienda.getHistorialVentas()) {
			if (codigoRecogida != null && codigoRecogida.equals(ped.getCodigoRecogida())
					&& (ped.getEstado() == EstadoPedido.LISTO_PARA_RECOGER) && ped.isRecogida_solicitada()) {
				ped.marcarEntregado();
				ped.getCliente().recibirNotificacionTipo(
						"Tu pedido con codigo de recogida " + ped.getCodigoRecogida() + " ha sido entregado con exito",
						TipoNotificacion.PEDIDO_ENTREGADO);
				this.recibirNotificacion("Has entregado corrrectamente el pedido con codigo de recogida"
						+ ped.getCodigoRecogida() + " al cliente " + ped.getCliente().getNickname() + ".");
				return true;
			}
		}
		System.out.println("No se ha podido entregar el pedido correctamente");
		return false;
	}

	/**
	 * Añade un producto a una categoría existente.
	 * 
	 * @param idProducto ID del producto
	 * @param nombreCat  Nombre de la categoría
	 * @return true si el producto se añadió correctamente
	 */
	public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
		if (idProducto == null || nombreCat == null) {
			System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.GESTION_CATEGORIAS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene el permiso de gestion de categorias.");
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
			boolean añadido = c.addProducto(p);
			if (añadido) {
				System.out.println(
						"Se ha añadido el producto con id " + idProducto + " a la categoria " + nombreCat + ".");
				for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {
					cliente.notificarProductoNuevoCategoria(
							"Nuevo producto en la categoria " + c.getNombre() + ": " + p.getNombre() + ".", nombreCat);
				}
			}
			return añadido;
		} catch (ProductoYaEnCategoriaException e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Elimina un producto de una categoría existente.
	 * 
	 * @param idProducto ID del producto
	 * @param nombreCat  Nombre de la categoría
	 * @return true si se eliminó correctamente
	 */
	public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
		if (idProducto == null || nombreCat == null) {
			System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.GESTION_CATEGORIAS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene el permiso de gestion de categorias.");
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

		boolean eliminado = c.deleteProducto(p);
		if (eliminado) {
			System.out.println(
					"Se ha eliminado el producto con id " + idProducto + " de la categoria " + nombreCat + ".");
			for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {

				cliente.notificarProductoNuevoCategoria(
						"Se ha eliminado el producto " + p.getNombre() + " de la categoria " + nombreCat + ".",
						nombreCat);
			}
		}
		return eliminado;
	}

	/**
	 * Crea un pack de productos en la tienda.
	 * 
	 * @param nombre        Nombre del pack
	 * @param descripcion   Descripción
	 * @param imagen        Ruta de imagen
	 * @param precioOficial Precio del pack
	 * @param stock         Stock del pack
	 * @param lineas        Lista de líneas de productos del pack
	 * @return true si el pack se creó correctamente
	 */
	public boolean crearPack(String nombre, String descripcion, String imagen, double precioOficial, int stock,
			ArrayList<LineaPack> lineas) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene permiso para trabajar con packs.");
			return false;
		}
		if (nombre == null || nombre.isBlank() || descripcion == null || descripcion.isBlank() || imagen == null) {
			System.out.println("El nombre, descripcion o imagen no pueden estar vacios");
			return false;
		}
		if (stock <= 0) {
			System.out.println("El stock debe ser mayor que 0");
			return false;
		}
		if (lineas == null || lineas.size() <= 1) {
			System.out.println("Para crear un pack minimo tiene que haber dos productos distintos");
			return false;
		}
		Pack p = new Pack(nombre, descripcion, imagen, precioOficial, stock, lineas);
		Tienda.getInstancia().añadirProducto(p);
		System.out.println("El empleado " + id + " ha creado correctamente el pack con id " + p.getId() + ".");
		this.recibirNotificacion("Has creado el pack " + nombre + " correctamente.");
		return true;
	}

	/**
	 * Añade un producto a un pack existente en la tienda.
	 *
	 * @param idProducto el ID del producto a añadir
	 * @param idPack     el ID del pack al que se añadirá el producto
	 * @param unidades   la cantidad de unidades a añadir
	 * @return true si el producto se añadió correctamente, false en caso contrario
	 */
	public boolean añadirProductoaPack(String idProducto, String idPack, int unidades) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene permiso para trabajar con packs.");
			return false;
		}
		if (idPack == null || idPack.isBlank()) {
			System.out.println("el id del pack no puede estar vacio");
			return false;
		}
		if (idProducto == null || idProducto.isBlank()) {
			System.out.println("el id del producto no puede estar vacio");
			return false;
		}
		if (unidades <= 0) {
			System.out.println("El minimo de unidades tiene que ser 1.");
			return false;
		}
		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idPack);
		if (pack == null) {

			System.out.println("No existe ningun pack de productos en la tienda con id " + idPack + ".");
			return false;
		}
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (producto == null) {
			System.out.println("No exxiste ningun producto en el catalogo de la tienda con id " + idProducto + ".");
			return false;
		}
		try {
			return ((Pack) pack).addProducto(producto, unidades);
		} catch (ProductoYaEnPackException e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		} catch (ProductoInvalidoException e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		} catch (StockInsuficienteParaPackException e) {
			System.out.println("  Error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Modifica la cantidad de unidades de un producto dentro de un pack.
	 *
	 * @param idProducto     el ID del producto a modificar
	 * @param idPack         el ID del pack donde se encuentra el producto
	 * @param nuevasUnidades el nuevo número de unidades del producto
	 * @return true si la modificación se realizó correctamente, false en caso
	 *         contrario
	 */
	public boolean modificarUnidadesProductoEnPack(String idProducto, String idPack, int nuevasUnidades) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene permiso para trabajar con packs.");
			return false;
		}
		if (idPack == null || idPack.isBlank()) {
			System.out.println("El id del pack no puede estar vacio.");
			return false;
		}
		if (idProducto == null || idProducto.isBlank()) {
			System.out.println("El id del producto no puede estar vacio.");
			return false;
		}
		if (nuevasUnidades <= 0) {
			System.out.println("Las unidades deben ser mayor que 0.");
			return false;
		}
		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idPack);
		if (pack == null || !(pack instanceof Pack)) {
			System.out.println("No existe ningun pack con id: " + idPack);
			return false;
		}
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (producto == null) {
			System.out.println("No existe ningun producto con id: " + idProducto);
			return false;
		}
		return ((Pack) pack).modificarUnidades(producto, nuevasUnidades);
	}

	/**
	 * Elimina un producto de un pack existente.
	 *
	 * @param idPack     el ID del pack
	 * @param idProducto el ID del producto a eliminar
	 * @return true si se eliminó correctamente, false si no existe el pack o el
	 *         producto
	 */
	public boolean eliminarProductoDePack(String idPack, String idProducto) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS))
			return false;

		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idPack);
		if (pack == null || !(pack instanceof Pack)) {
			System.out.println("No existe ningún pack con id: " + idPack);
			return false;
		}
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (producto == null) {
			System.out.println("No existe ningún producto con id: " + idProducto);
			return false;
		}
		return ((Pack) pack).eliminarLinea(producto);
	}

	/**
	 * Modifica el precio oficial de un pack.
	 *
	 * @param idPack      el ID del pack
	 * @param nuevoPrecio el nuevo precio a establecer
	 * @return true si se modificó correctamente, false si no existe el pack
	 */
	public boolean modificarPrecioPack(String idPack, double nuevoPrecio) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS))
			return false;

		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idPack);
		if (pack == null || !(pack instanceof Pack)) {
			System.out.println("No existe ningún pack con id: " + idPack);
			return false;
		}
		return ((Pack) pack).setPrecioOficial(nuevoPrecio);
	}

	/**
	 * Elimina un pack de la tienda, liberando el stock de los productos que
	 * contiene.
	 *
	 * @param idpack el ID del pack a eliminar
	 * @return true si se eliminó correctamente, false si no existe el pack o no se
	 *         tienen permisos
	 */
	public boolean eliminarPack(String idpack) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS)) {
			System.out.println("El empleado " + nickname + " no pude modificar packs");
		}
		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idpack);
		if (pack == null || !(pack instanceof Pack)) {
			System.out.println("No existe ningún pack con id: " + idpack);
			return false;
		}
		// Liberamos el stock de los productos del pack
		((Pack) pack).getLineas().forEach(lp -> {
			lp.getProducto().setStockDisponible(
					lp.getProducto().getStockDisponible() + lp.getUnidades() * pack.getStockDisponible());
		});
		Tienda.getInstancia().getStockVentas().remove(pack);
		System.out.println("Pack " + idpack + " eliminado correctamente.");
		return true;
	}

	/**
	 * Modifica la descripción de un producto.
	 *
	 * @param idProducto  el ID del producto a modificar
	 * @param descripcion la nueva descripción
	 * @return true si se modificó correctamente, false si no se tiene permiso o el
	 *         producto no existe
	 */
	public boolean modificarDescripcionProducto(String idProducto, String descripcion) {
		if (idProducto == null || descripcion == null) {
			System.out.println("El id del producto o la descripción no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.MODIFICAR_PRODUCTO))
			return false;

		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (p == null)
			return false;

		p.setDescripcion(descripcion);
		System.out.println("Descripción del producto " + idProducto + " modificada correctamente.");
		return true;
	}

	/**
	 * Modifica la ruta de la imagen de un producto.
	 *
	 * @param idProducto el ID del producto a modificar
	 * @param imagen     la nueva ruta de la imagen
	 * @return true si se modificó correctamente, false si no se tiene permiso o el
	 *         producto no existe
	 */
	public boolean modificarImagenProducto(String idProducto, String imagen) {
		if (idProducto == null || imagen == null) {
			System.out.println("El id del producto o la imagen no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.MODIFICAR_PRODUCTO))
			return false;

		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (p == null)
			return false;

		p.setImagenRuta(imagen);
		System.out.println("Imagen del producto " + idProducto + " modificada correctamente.");
		return true;
	}

	/**
	 * Muestra todas las notificaciones del empleado, separando las leídas de las no
	 * leídas. Marca como leídas las notificaciones que se muestran como no leídas.
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
	 * Muestra las notificaciones del empleado filtradas por tipo, separando las
	 * leídas de las no leídas. Marca como leídas las notificaciones que se muestran
	 * como no leídas.
	 *
	 * @param tipo el tipo de notificación a mostrar
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
	 * Asigna un permiso al empleado.
	 *
	 * @param p el permiso a asignar
	 */
	public void asignarPermiso(TipoPermisos p) {
		this.permisos.add(p);
	}

	/**
	 * Quita un permiso al empleado.
	 *
	 * @param p el permiso a quitar
	 */
	public void quitarPermiso(TipoPermisos p) {
		this.permisos.remove(p);
	}

	/**
	 * Comprueba si el empleado tiene un permiso específico.
	 *
	 * @param p el permiso a verificar
	 * @return true si el empleado tiene el permiso, false en caso contrario
	 */
	public boolean tienePermiso(TipoPermisos p) {
		return this.permisos.contains(p);
	}

	/**
	 * Recibe una notificación para el empleado.
	 *
	 * @param mensaje el mensaje de la notificación
	 */
	public void recibirNotificacion(String mensaje) {
		this.notificaciones.add(new Notificacion(mensaje));
		System.out.println("[Notificación Empleado]: " + mensaje);
	}

	/**
	 * Obtiene la lista de notificaciones del empleado.
	 *
	 * @return lista de notificaciones
	 */
	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	/**
	 * Establece la lista de notificaciones del empleado.
	 *
	 * @param notificaciones lista de notificaciones a asignar
	 */
	public void setNotificaciones(List<Notificacion> notificaciones) {
		this.notificaciones = notificaciones;
	}

	/**
	 * Obtiene el conjunto de permisos del empleado.
	 *
	 * @return conjunto de permisos
	 */
	public Set<TipoPermisos> getPermisos() {
		return permisos;
	}

	/**
	 * Establece el conjunto de permisos del empleado.
	 *
	 * @param permisos conjunto de permisos a asignar
	 */
	public void setPermisos(Set<TipoPermisos> permisos) {
		this.permisos = permisos;
	}

	/**
	 * Obtiene la lista de valoraciones asociadas al empleado.
	 *
	 * @return lista de valoraciones
	 */
	public List<Valoracion> getValoraciones() {
		return valoraciones;
	}

	/**
	 * Establece la lista de valoraciones del empleado.
	 *
	 * @param valoraciones lista de valoraciones a asignar
	 */
	public void setValoraciones(List<Valoracion> valoraciones) {
		this.valoraciones = valoraciones;
	}

	/**
	 * Comprueba si el empleado está despedido.
	 *
	 * @return true si el empleado está despedido, false en caso contrario
	 */
	public boolean isDespedido() {
		return despedido;
	}

	/**
	 * Establece el estado de despido del empleado.
	 *
	 * @param despedido true si se desea marcar al empleado como despedido, false en
	 *                  caso contrario
	 */
	public void setDespedido(boolean despedido) {
		this.despedido = despedido;
	}

	/**
	 * Devuelve una representación en cadena del empleado.
	 *
	 * @return cadena con el ID y nickname del empleado
	 */
	@Override
	public String toString() {
		return "Empleado [id=" + getId() + ", nickname=" + getNickname() + "]";
	}
}
