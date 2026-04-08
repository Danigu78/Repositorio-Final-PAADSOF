package pruebas;

import java.util.*;

import excepciones.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

/**
 * Clase de prueba para validar el motor de recomendaciones de la tienda. Se
 * encarga de verificar que el sistema sugiera productos de forma coherente
 * basándose en tres criterios configurables: valoraciones globales, historial
 * de compras comunes entre usuarios y categorías favoritas. Prueba que se hizo
 * antes del demostrador como una especie de test para comprobar que la logica
 * es correcta, similar a los junit
 *
 * @author Lucas y Antonino
 * @version 1.0
 */
public class PruebaRecomendador {

	static int correctos = 0;
	static int fallos = 0;

	/**
	 * Evalúa una condición lógica y actualiza los contadores de éxito o fallo.
	 * Imprime el resultado de la comprobación detallando si la funcionalidad
	 * responde según lo esperado.
	 *
	 * @param nombre    Descripción del test o comportamiento que se está validando.
	 * @param condicion Resultado booleano de la evaluación del test.
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
	 * Método principal que coordina las pruebas del recomendador. Simula un entorno
	 * con múltiples productos reseñados y compras cruzadas entre usuarios para
	 * verificar que los pesos de ponderación, los límites de sugerencias y las
	 * exclusiones por productos ya adquiridos funcionan correctamente.
	 *
	 * @param args Argumentos de configuración de la línea de comandos.
	 */
	public static void main(String[] args) {

		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();
		Recomendador rec = tienda.getRecomendador();

		Gestor gestor = null;
		for (UsuarioRegistrado u : tienda.getUsuarios()) {
			if (u instanceof Gestor) {
				gestor = (Gestor) u;
				break;
			}
		}

		gestor.configurarTiemposSistema(60, 60, 60);

		List<TipoPermisos> permisosT = new ArrayList<>();
		permisosT.add(TipoPermisos.VALORACION_PRODUCTOS);
		permisosT.add(TipoPermisos.GESTION_STOCK);
		gestor.darDeAltaEmpleados_Permisos("empleado1", "Clave1@1234", permisosT);
		Empleado empleado1 = tienda.loginEmpleado("empleado1", "Clave1@1234");

		Categoria categoria1 = new Categoria("categoria1", "desc");
		Categoria categoria2 = new Categoria("categoria2", "desc");
		Categoria categoria3 = new Categoria("categoria3", "desc");
		tienda.getCategorias().add(categoria1);
		tienda.getCategorias().add(categoria2);
		tienda.getCategorias().add(categoria3);

		ArrayList<Categoria> cats1 = new ArrayList<>(Arrays.asList(categoria1));
		ArrayList<Categoria> cats2 = new ArrayList<>(Arrays.asList(categoria2));
		ArrayList<Categoria> cats3 = new ArrayList<>(Arrays.asList(categoria3));

		empleado1.añadirProducto_nuevo("C", "producto1", "desc", "imagen1", 12.50, 20, cats1, 200, "dato1", 2001, 0, 0,
				0, null, null, 0, 0, 0, 0, null);
		empleado1.añadirProducto_nuevo("C", "producto2", "desc", "imagen2", 15.00, 10, cats1, 400, "dato2", 2002, 0, 0,
				0, null, null, 0, 0, 0, 0, null);
		empleado1.añadirProducto_nuevo("F", "producto3", "desc", "imagen3", 35.00, 15, cats2, 0, null, 0, 20, 15, 12,
				"dato3", "dato4", 0, 0, 0, 0, null);
		empleado1.añadirProducto_nuevo("F", "producto4", "desc", "imagen4", 40.00, 8, cats2, 0, null, 0, 18, 12, 10,
				"dato5", "dato6", 0, 0, 0, 0, null);
		empleado1.añadirProducto_nuevo("J", "producto5", "desc", "imagen5", 45.00, 12, cats3, 0, null, 0, 0, 0, 0, null,
				null, 2, 4, 8, 99, "dato7");

		ProductoVenta producto1 = tienda.getStockVentas().get(0);
		ProductoVenta producto2 = tienda.getStockVentas().get(1);
		ProductoVenta producto3 = tienda.getStockVentas().get(2);
		ProductoVenta producto4 = tienda.getStockVentas().get(3);
		ProductoVenta producto5 = tienda.getStockVentas().get(4);

		System.out.println("COMPROBACION DE QUE LOS PRODUCTOS SE HAN AÑADIDO CORRECTAMENTE A LA TIENDA:");
		System.out.println("\n=== PRODUCTOS EN LA TIENDA ===");
		for (ProductoVenta p : tienda.getStockVentas()) {
			System.out.println(p);
		}

		// CORRECCIÓN: contraseñas con mayúscula, minúscula, número y carácter especial
		tienda.registrarNuevoCliente("cliente1", "Clave2@1234", "11111111A");
		tienda.registrarNuevoCliente("cliente2", "Clave3@1234", "22222222B");
		tienda.registrarNuevoCliente("cliente3", "Clave4@1234", "33333333C");
		Cliente cliente1 = tienda.loginCliente("cliente1", "Clave2@1234");
		Cliente cliente2 = tienda.loginCliente("cliente2", "Clave3@1234");
		Cliente cliente3 = tienda.loginCliente("cliente3", "Clave4@1234");

		new Reseña(cliente1, producto1, 9.0, "texto1");
		new Reseña(cliente1, producto2, 7.0, "texto2");
		new Reseña(cliente2, producto3, 8.0, "texto3");
		new Reseña(cliente2, producto4, 6.0, "texto4");
		new Reseña(cliente3, producto5, 5.0, "texto5");

		// cliente1 compro producto1 -> categoria favorita categoria1
		cliente1.añadirProductoCarrito(producto1, 1);
		cliente1.reservarCarrito();
		Pedido pedido1 = cliente1.getHistorialPedidos().get(0);
		cliente1.pagarCarrito(pedido1, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L),
				123);

		// cliente2 compro producto1 (en comun con cliente1) y producto3
		cliente2.añadirProductoCarrito(producto1, 1);
		cliente2.reservarCarrito();
		Pedido pedido2 = cliente2.getHistorialPedidos().get(0);
		cliente2.pagarCarrito(pedido2, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L),
				123);

		cliente2.añadirProductoCarrito(producto3, 1);
		cliente2.reservarCarrito();
		Pedido pedido3 = cliente2.getHistorialPedidos().get(1);
		cliente2.pagarCarrito(pedido3, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L),
				123);

		// cliente3 no hace nada

		System.out.println("\n============= recomendarPorValoracion =============");

		try {
			rec.setPesos(1, 0, 0);
			List<ProductoVenta> sugerencias = rec.generarSugerencias(cliente1);

			check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
			boolean resultado = true;
			for (Producto p : sugerencias) {
				if (p.getNombre().equals("producto1")) {
					resultado = false;
					break;
				}
			}
			check("No incluye producto1 (ya comprado por cliente1)", resultado);
			check("El primero es producto3 (puntuacion 8.0, la mas alta disponible)",
					sugerencias.get(0).getNombre().equals("producto3"));
			check("No supera el limite maximo", sugerencias.size() <= rec.getLimiteMaximo());
		} catch (Exception e) {
			fallos++;
		}

		System.out.println("\n============= recomendarPorCompras =============");

		try {
			rec.setPesos(0, 1, 0);
			List<ProductoVenta> sugerencias = rec.generarSugerencias(cliente1);

			check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
			boolean resultado = true;
			for (Producto p : sugerencias) {
				if (p.getNombre().equals("producto1")) {
					resultado = false;
					break;
				}
			}
			check("No incluye producto1 (ya comprado por cliente1)", resultado);

			resultado = false;
			for (Producto p : sugerencias) {
				if (p.getNombre().equals("producto3")) {
					resultado = true;
					break;
				}
			}
			check("Incluye producto3 (cliente2 lo compro y tiene producto1 en comun con cliente1)", resultado);
		} catch (Exception e) {
			fallos++;
		}

		System.out.println("\n============= recomendarPorCategorias =============");

		try {
			rec.setPesos(0, 0, 1);
			List<ProductoVenta> sugerencias = rec.generarSugerencias(cliente1);

			check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
			boolean resultado = false;
			for (Producto p : sugerencias) {
				if (p.getNombre().equals("producto2")) {
					resultado = true;
					break;
				}
			}
			check("Incluye producto2 (misma categoria favorita, no comprado)", resultado);

			resultado = true;
			for (Producto p : sugerencias) {
				if (p.getNombre().equals("producto1")) {
					resultado = false;
					break;
				}
			}
			check("No incluye producto1 (ya comprado)", resultado);
			check("Solo incluye un elemento, producto2", sugerencias.size() == 1);
		} catch (Exception e) {
			fallos++;
		}

		System.out.println("\n============= recomendador ponderado =============");

		try {
			rec.setPesos(1, 1, 1);
			List<ProductoVenta> sugerencias = rec.generarSugerencias(cliente1);

			check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
			boolean resultado = true;
			for (Producto p : sugerencias) {
				if (p.getNombre().equals("producto1")) {
					resultado = false;
					break;
				}
			}
			check("No incluye producto1 (ya comprado)", resultado);
			check("No supera el limite maximo", sugerencias.size() <= rec.getLimiteMaximo());
		} catch (Exception e) {
			fallos++;
		}

		System.out.println("\n============= cliente3 sin historial (caso extremo) =============");

		try {
			rec.setConfiguracion(rec.getLimiteMaximo(), true);
			rec.setPesos(0, 0, 1);
			check("Sin historial, recomendador por categorias devuelve lista vacia",
					rec.generarSugerencias(cliente3).isEmpty());

			rec.setPesos(1, 0, 0);
			List<ProductoVenta> sugerencias = rec.generarSugerencias(cliente3);
			check("Sin historial, recomendador por valoracion devuelve productos", !sugerencias.isEmpty());
			check("Sin historial no hay excluidos, devuelve hasta el limite",
					sugerencias.size() <= rec.getLimiteMaximo());

			rec.setPesos(0, 1, 0);
			check("Sin historial, recomendador por compras comun devuelve lista vacia",
					rec.generarSugerencias(cliente3).isEmpty());
		} catch (Exception e) {
			fallos++;
		}

		System.out.println("\n============= exclusion por carrito =============");

		try {
			cliente1.añadirProductoCarrito(producto2, 1);
			rec.setPesos(1, 0, 0);
			List<ProductoVenta> sugerencias = rec.generarSugerencias(cliente1);
			boolean resultado = true;
			for (Producto p : sugerencias) {
				if (p.getNombre().equals("producto2")) {
					resultado = false;
					break;
				}
			}
			check("producto2 en carrito no aparece en sugerencias", resultado);
			cliente1.setCarritoActual(null);
		} catch (Exception e) {
			fallos++;
		}

		System.out.println("\n============= CONTROL DE ERRORES =============");

		int limiteAntes = rec.getLimiteMaximo();
		rec.setConfiguracion(0, true);
		check("setConfiguracion con limite 0 no cambia el limite", rec.getLimiteMaximo() == limiteAntes);

		rec.setConfiguracion(-3, true);
		check("setConfiguracion con limite negativo no cambia el limite", rec.getLimiteMaximo() == limiteAntes);

		rec.getPesoValoracion();
		rec.getPesoCompras();

		try {
			rec.setPesos(-1, 0.5, 0.5);
			check("setPesos con peso negativo no cambia los pesos", false);
		} catch (PesosInvalidosException e) {
			check("setPesos con peso negativo lanza PesosInvalidosException", true);
		}

		try {
			rec.setPesos(0, 0, 0);
			check("setPesos todos a 0 no cambia los pesos", false);
		} catch (PesosInvalidosException e) {
			check("setPesos todos a 0 lanza PesosInvalidosException", true);
		}

		try {
			rec.setConfiguracion(1, true);
			rec.setPesos(1, 0, 0);
			check("Con limite 1 solo devuelve 1 sugerencia", rec.generarSugerencias(cliente1).size() == 1);
		} catch (Exception e) {
			fallos++;
		}

		try {
			rec.setConfiguracion(5, false);
			rec.generarSugerencias(cliente1);
			check("Con recomendador desactivado devuelve lista vacia", false);
		} catch (RecomendadorNoActivoException e) {
			check("Con recomendador desactivado lanza RecomendadorNoActivoException", true);
		}
		rec.setConfiguracion(5, true);
		try {
			check("generarSugerencias con cliente null devuelve lista vacia", rec.generarSugerencias(null).isEmpty());
		} catch (Exception e) {
			fallos++;
		}

		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}