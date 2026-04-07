package pruebas;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import Excepcion.*;
import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

/**
 * Clase de prueba para validar el motor estadístico y de ingresos del sistema.
 * Se encarga de verificar que el gestor genere correctamente los rankings de
 * clientes y calcule con precisión los ingresos por ventas y tasaciones, tanto
 * en periodos mensuales como en rangos de fechas específicos. Prueba que se
 * hizo antes del demostrador como una especie de test para comprobar que la
 * logica es correcta, similar a los junit
 * 
 * @author Lucas
 * @version 1.0
 */
public class PruebaMotorEstadistico {

	static int correctos = 0;
	static int fallos = 0;

	/**
	 * Valida un resultado esperado frente a uno obtenido y actualiza los contadores
	 * globales. Imprime el estado de la prueba por consola.
	 *
	 * @param nombre    Descripción de la prueba o funcionalidad validada.
	 * @param condicion Expresión booleana que determina si la prueba es correcta.
	 */
	static void check(String nombre, boolean condicion) {
		if (condicion) {
			System.out.println("\tCORRECTO -> " + nombre);
			correctos++;
		} else {
			System.out.println("\tFALLO -> " + nombre);
			fallos++;
		}
	}

	/**
	 * Método principal que ejecuta la batería de pruebas estadísticas. Simula un
	 * entorno complejo con múltiples clientes, pedidos pagados, cancelaciones,
	 * productos de segunda mano e intercambios para comprobar la fiabilidad de los
	 * informes generados por el gestor.
	 *
	 * @param args Argumentos de configuración de la línea de comandos.
	 */
	public static void main(String[] args) {

		/*
		 * Montamos la tienda con todos los datos necesarios para la prueba. Creamos un
		 * empleado tasador, cuatro clientes con distinto nivel de actividad, productos
		 * de venta, pedidos (algunos cancelados), productos de segunda mano tasados e
		 * intercambios finalizados. El gestor ya existe porque la Tienda lo crea en su
		 * constructor.
		 */

		Tienda.getInstancia().getHistorialVentas().clear();
		Tienda.getInstancia().getHistorialProductos2Mano().clear();
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		// El gestor ya existe en la tienda, lo recuperamos
		Gestor gestor = null;
		for (UsuarioRegistrado u : tienda.getUsuarios()) {
			if (u instanceof Gestor) {
				gestor = (Gestor) u;
				break;
			}
		}

		Empleado empleado1 = new Empleado("empleado1", "Clave1@");
		empleado1.asignarPermiso(TipoPermisos.VALORACION_PRODUCTOS);
		tienda.getUsuarios().add(empleado1);

		// cliente1: 3 pedidos validos + 1 cancelado, 2 intercambios
		// cliente2: 2 pedidos validos, 1 intercambio
		// cliente3: 1 pedido valido, 1 intercambio
		// cliente4: 0 pedidos validos + 1 cancelado, 0 intercambios
		Cliente cliente1 = new Cliente("cliente1", "Clave2@", "11111111A");
		Cliente cliente2 = new Cliente("cliente2", "Clave3@", "22222222B");
		Cliente cliente3 = new Cliente("cliente3", "Clave4@", "33333333C");
		Cliente cliente4 = new Cliente("cliente4", "Clave5@", "44444444D");
		tienda.getUsuarios().add(cliente1);
		tienda.getUsuarios().add(cliente2);
		tienda.getUsuarios().add(cliente3);
		tienda.getUsuarios().add(cliente4);

		Comic producto1 = new Comic("producto1", "desc1", "imagen1.png", 12.50, 20, 200, "editorial1", 2001);
		Comic producto2 = new Comic("producto2", "desc2", "imagen2.png", 15.00, 10, 400, "editorial2", 2002);
		Figura producto3 = new Figura("producto3", "desc3", "imagen3.png", 35.00, 15, 20, 15, 12, "mat1", "marca1");
		JuegoMesa producto4 = new JuegoMesa("producto4", "desc4", "imagen4.png", 45.00, 12, 2, 4, 8, 99, "tipo1");
		JuegoMesa producto5 = new JuegoMesa("producto5", "desc5", "imagen5.png", 38.00, 10, 2, 4, 8, 99, "tipo2");
		tienda.añadirProducto(producto1);
		tienda.añadirProducto(producto2);
		tienda.añadirProducto(producto3);
		tienda.añadirProducto(producto4);
		tienda.añadirProducto(producto5);

		// Pedidos validos: cliente1 25+35+45=105, cliente2 15+38=53, cliente3 12.50 ->
		// total
		// ventas 170.50
		Carrito carrito1 = new Carrito(cliente1);
		carrito1.añadirProducto(producto1, 2);
		Pedido pedido1 = new Pedido(cliente1, carrito1);
		cliente1.getHistorialPedidos().add(pedido1);
		tienda.registrarVenta(pedido1);

		Carrito carrito2 = new Carrito(cliente1);
		carrito2.añadirProducto(producto3, 1);
		Pedido pedido2 = new Pedido(cliente1, carrito2);
		cliente1.getHistorialPedidos().add(pedido2);
		tienda.registrarVenta(pedido2);

		Carrito carrito3 = new Carrito(cliente1);
		carrito3.añadirProducto(producto4, 1);
		Pedido pedido3 = new Pedido(cliente1, carrito3);
		cliente1.getHistorialPedidos().add(pedido3);
		tienda.registrarVenta(pedido3);

		Carrito carrito4 = new Carrito(cliente2);
		carrito4.añadirProducto(producto2, 1);
		Pedido pedido4 = new Pedido(cliente2, carrito4);
		cliente2.getHistorialPedidos().add(pedido4);
		tienda.registrarVenta(pedido4);

		Carrito carrito5 = new Carrito(cliente2);
		carrito5.añadirProducto(producto5, 1);
		Pedido pedido5 = new Pedido(cliente2, carrito5);
		cliente2.getHistorialPedidos().add(pedido5);
		tienda.registrarVenta(pedido5);

		Carrito carrito6 = new Carrito(cliente3);
		carrito6.añadirProducto(producto1, 1);
		Pedido pedido6 = new Pedido(cliente3, carrito6);
		cliente3.getHistorialPedidos().add(pedido6);
		tienda.registrarVenta(pedido6);

		// Pedidos cancelados
		Carrito carrito7 = new Carrito(cliente1);
		carrito7.añadirProducto(producto2, 1);
		Pedido pedido7 = new Pedido(cliente1, carrito7);
		cliente1.getHistorialPedidos().add(pedido7);
		tienda.registrarVenta(pedido7);
		pedido7.cancelarPedido();

		Carrito carrito8 = new Carrito(cliente4);
		carrito8.añadirProducto(producto1, 1);
		Pedido pedido8 = new Pedido(cliente4, carrito8);
		cliente4.getHistorialPedidos().add(pedido8);
		tienda.registrarVenta(pedido8);
		pedido8.cancelarPedido();

		// Tasaciones: 5+8 en catalogo, 3.50 en pendientes -> total 16.50
		// Tasaciones
		Producto2Mano usado1 = new Producto2Mano(cliente1, "usado1", "desc6", "imagen6.png");
		Producto2Mano usado2 = new Producto2Mano(cliente1, "usado2", "desc7", "imagen7.png");
		Producto2Mano usado3 = new Producto2Mano(cliente2, "usado3", "desc8", "imagen8.png");
		Producto2Mano usado4 = new Producto2Mano(cliente3, "usado4", "desc9", "imagen9.png");

		usado1.valorar(5.00, EstadoProducto.MUY_BUENO, empleado1);
		usado2.valorar(8.00, EstadoProducto.MUY_BUENO, empleado1);
		usado4.valorar(4.00, EstadoProducto.MUY_BUENO, empleado1);

		usado3.valorar(3.50, EstadoProducto.MUY_BUENO, empleado1);
		tienda.getHistorialProductos2Mano().clear(); // Limpieza de seguridad antes de usado3
		tienda.getHistorialProductos2Mano().add(usado3);

		tienda.publicarParaIntercambio(usado1);
		tienda.publicarParaIntercambio(usado2);

		tienda.publicarParaIntercambio(usado1);
		tienda.publicarParaIntercambio(usado2);

		// Intercambios: cliente1<->cliente2 (1), cliente1<->cliente3 (2)
		cliente1.getCarteraIntercambio().add(usado1);
		cliente1.getCarteraIntercambio().add(usado2);
		cliente2.getCarteraIntercambio().add(usado3);
		cliente3.getCarteraIntercambio().add(usado4);
		usado1.setBloqueado(false);
		Oferta oferta1 = new Oferta(cliente1, cliente2, Arrays.asList(usado1), Arrays.asList(usado3));
		oferta1.aceptarOferta();
		tienda.registrarIntercambioFinalizado(oferta1);
		usado2.setBloqueado(false);
		Oferta oferta2 = new Oferta(cliente1, cliente3, Arrays.asList(usado2), Arrays.asList(usado4));
		oferta2.aceptarOferta();
		tienda.registrarIntercambioFinalizado(oferta2);

		LocalDate hoy = LocalDate.now();
		int anioActual = hoy.getYear();
		System.out.println("Montaje listo.");

		/*
		 * Comprobamos que el ranking de compras ordena correctamente los clientes segun
		 * sus pedidos validos (PAGADO, LISTO_PARA_RECOGER, ENTREGADO). Los pedidos
		 * cancelados no deben contar. cliente4 tiene solo cancelados y debe quedar
		 * ultima.
		 */
		System.out.println("\n============= verClientesTopCompras =============");

		List<Cliente> topCompras = gestor.verClientesTopCompras();

		check("La lista no es null", topCompras != null);
		check("Hay 4 clientes en el ranking", topCompras.size() == 4);
		check("cliente1 lidera con 3 pedidos validos", topCompras.get(0).getNickname().equals("cliente1"));
		check("cliente2 es segundo con 2 pedidos", topCompras.get(1).getNickname().equals("cliente2"));
		check("cliente3 es tercero con 1 pedido", topCompras.get(2).getNickname().equals("cliente3"));
		check("cliente4 es ultima con 0 pedidos validos", topCompras.get(3).getNickname().equals("cliente4"));

		/*
		 * Comprobamos que el ranking de intercambios ordena segun el numero de
		 * intercambios finalizados en los que ha participado cada cliente, ya sea como
		 * origen o como destino.
		 */
		System.out.println("\n============= verClientesTopIntercambios =============");

		List<Cliente> topIntercambios = gestor.verClientesTopIntercambios();

		check("La lista no es null", topIntercambios != null);
		check("Hay 4 clientes en el ranking", topIntercambios.size() == 4);
		check("cliente1 lidera con 2 intercambios", topIntercambios.get(0).getNickname().equals("cliente1"));
		check("cliente4 es ultima con 0 intercambios",
				topIntercambios.get(topIntercambios.size() - 1).getNickname().equals("cliente4"));

		/*
		 * Comprobamos que el ranking de pedidos cancelados ordena correctamente.
		 * cliente1 y cliente4 tienen 1 cancelado cada una, cliente2 y cliente3 tienen
		 * 0.
		 */
		System.out.println("\n============= verClientesConMasPedidosCancelados =============");

		List<Cliente> topCancelados = gestor.verClientesConMasPedidosCancelados();

		check("La lista no es null", topCancelados != null);
		check("Hay 4 clientes en el ranking", topCancelados.size() == 4);
		check("El primero tiene 1 pedido cancelado", topCancelados.get(0).getHistorialPedidos().stream()
				.filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count() == 1);
		check("cliente2 tiene 0 cancelados", cliente2.getHistorialPedidos().stream()
				.filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count() == 0);
		check("cliente3 tiene 0 cancelados", cliente3.getHistorialPedidos().stream()
				.filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count() == 0);

		/*
		 * Comprobamos los ingresos totales por ventas (pedidos), sin contar tasaciones.
		 * cliente1 25+35+45=105, cliente2 15+38=53, cliente3 12.50 -> 170.50
		 */
		System.out.println("\n============= consultarIngresosVenta =============");

		double ingresosVenta = gestor.consultarIngresosVenta();

		check("Ingresos venta mayores que 0", ingresosVenta > 0);
		check("Ingresos venta = 170.50", ingresosVenta == 170.50);

		/*
		 * Comprobamos los ingresos por tasacion. El metodo usa nTasacionesCobradas
		 * multiplicado por el precio de tasacion configurado en la tienda (10 euros por
		 * defecto).
		 */
		System.out.println("\n============= consultarIngresosTasacion =============");

		double ingresosTasacion = gestor.consultarIngresosTasacion();

		check("Ingresos tasacion mayor o igual que 0", ingresosTasacion >= 0);

		/*
		 * Comprobamos calcularIngresosRangoFechas, que suma ventas y tasaciones del
		 * rango. Ventas = 170.50. Tasaciones en rango = solo usado3 (3.50), ya que
		 * calcularIngresosTasacion usa nTasacionesCobradas que se incrementa en el
		 * flujo normal de la tienda, no con valorar() directamente. Total esperado =
		 * 174.00. Un rango sin actividad devuelve 0.0.
		 */
		System.out.println("\n============= consultarIngresosRango =============");

		try {

			check("Rango anio completo  = 180.50", gestor.consultarIngresosRango(LocalDate.of(anioActual, 1, 1),
					LocalDate.of(anioActual, 12, 31)) == 180.5);
			check("Rango de un solo dia (hoy) = 180.50", gestor.consultarIngresosRango(hoy, hoy) == 180.5);
			check("Rango anio anterior (sin actividad) = 0.0",
					gestor.consultarIngresosRango(LocalDate.of(anioActual - 1, 1, 1),
							LocalDate.of(anioActual - 1, 12, 31)) == 0.0);
			check("Rango futuro (2099) = 0.0",
					gestor.consultarIngresosRango(LocalDate.of(2099, 1, 1), LocalDate.of(2099, 12, 31)) == 0.0);
			check("inicio == fin (hoy) es valido", gestor.consultarIngresosRango(hoy, hoy) > 0.0);
		} catch (RangoFechasInvalidoException e) {
			fallos++;
		}

		// Pruebas de excepciones en rango
		try {
			gestor.consultarIngresosRango(null, hoy);
			check("inicio null lanza excepcion", false);
		} catch (RangoFechasInvalidoException e) {
			check("inicio null lanza excepcion", true);
		}

		try {
			gestor.consultarIngresosRango(hoy, null);
			check("fin null lanza excepcion", false);
		} catch (RangoFechasInvalidoException e) {
			check("fin null lanza excepcion", true);
		}

		try {
			gestor.consultarIngresosRango(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 1, 1));
			check("fin < inicio lanza excepcion", false);
		} catch (RangoFechasInvalidoException e) {
			check("fin < inicio lanza excepcion", true);
		}

		/*
		 * Comprobamos que consultarIngresosPorMesesActual devuelve un array de 12
		 * posiciones donde solo el mes actual tiene ingresos, ya que todos los pedidos
		 * y tasaciones son de hoy. La suma de los 12 meses debe coincidir con el total
		 * del anio.
		 */
		System.out.println("\n============= consultarIngresosPorMesesActual =============");

		try {
			double[] porMesActual = gestor.consultarIngresosPorMesesActual();
			int mesIdx = hoy.getMonthValue() - 1;

			check("Array tiene 12 posiciones", porMesActual != null && porMesActual.length == 12);

			check("Mes actual tiene 180.5", porMesActual[mesIdx] == 180.5);

			double sumaMeses = 0.0;
			for (double v : porMesActual) {
				sumaMeses += v;
			}

			check("Suma de los 12 meses = 180.5", sumaMeses == 180.5);

			boolean otrosMesesVacios = true;
			for (int i = 0; i < 12; i++) {
				if (i != mesIdx && porMesActual[i] != 0.0) {
					otrosMesesVacios = false;
					break;
				}
			}
			check("El resto de meses tienen 0.0", otrosMesesVacios);
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			fallos++;
		}

		/*
		 * Comprobamos consultarIngresosPorMeses con un anio concreto. Con el anio
		 * actual debe dar el mismo resultado que el metodo anterior. Con el anio
		 * anterior y el 2099 todos los meses deben ser 0. Con un anio invalido (<= 0)
		 * debe devolver un array de 12 ceros.
		 */
		System.out.println("\n============= consultarIngresosPorMeses (año dado) =============");

		try {
			double[] porMesActual = gestor.consultarIngresosPorMesesActual();
			double[] porMesAnioActual = gestor.consultarIngresosPorMeses(anioActual);

			check("Array del año actual tiene 12 posiciones",
					porMesAnioActual != null && porMesAnioActual.length == 12);
			check("consultarIngresosPorMeses(anioActual) coincide con consultarIngresosPorMesesActual()",
					Arrays.equals(porMesActual, porMesAnioActual));

			double[] porMesAnioAnterior = gestor.consultarIngresosPorMeses(anioActual - 1);
			boolean anioAnteriorVacio = true;
			for (double v : porMesAnioAnterior) {
				if (v != 0.0) {
					anioAnteriorVacio = false;
					break;
				}
			}
			check("Todos los meses del anio anterior son 0.0", anioAnteriorVacio);

			double[] porMes2099 = gestor.consultarIngresosPorMeses(2099);
			boolean anio2099Vacio = true;
			for (double v : porMes2099) {
				if (v != 0.0) {
					anio2099Vacio = false;
					break;
				}
			}
			check("Todos los meses de 2099 son 0.0", anio2099Vacio);
		} catch (AñoInvalidoException | RangoFechasInvalidoException e) {
			fallos++;
		}

		// Pruebas de excepciones en anio
		try {
			gestor.consultarIngresosPorMeses(0);
			check("Año 0 lanza excepcion", false);
		} catch (AñoInvalidoException e) {
			check("Año 0 lanza excepcion", true);
		} catch (RangoFechasInvalidoException e) {
			fallos++;
		}

		try {
			gestor.consultarIngresosPorMeses(-99);
			check("Año negativo lanza excepcion", false);
		} catch (AñoInvalidoException e) {
			check("Año negativo lanza excepcion", true);
		} catch (RangoFechasInvalidoException e) {
			fallos++;
		}

		/*
		 * Imprimimos el resultado del test.
		 */
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}