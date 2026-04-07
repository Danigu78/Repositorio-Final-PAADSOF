package pruebas;

import productos.*;
import tienda.*;
import usuarios.*;

/**
 * Clase de prueba para validar el funcionamiento de los filtros de búsqueda. Se
 * encarga de verificar que tanto los filtros de productos de venta como los de
 * segunda mano se filtren correctamente por precio, puntuación, categoría y
 * estado de conservación.
 *
 * Prueba que se hizo antes del demostrador como una especie de test
 * paracomprobar que la logica es correcta, similar a los junit
 * 
 * @author Daniel
 * @version 1.0
 */
public class PruebaFiltros {

	static int correctos = 0;
	static int fallos = 0;

	/**
	 * Compara el resultado de una prueba con el valor esperado y actualiza los
	 * contadores globales de éxito y error.
	 *
	 * @param nombre    Identificador o descripción de la prueba ejecutada.
	 * @param condicion Resultado booleano de la validación.
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
	 * Método principal que coordina la ejecución de las pruebas de filtrado.
	 * Instancia un entorno con productos de diversas características y comprueba la
	 * precisión de FiltroVenta y FiltroSegundaMano ante criterios combinados y
	 * valores límite.
	 *
	 * @param args Argumentos de configuración de la línea de comandos.
	 */
	public static void main(String[] args) {

		/*
		 * Montamos productos de venta con distintos precios y puntuaciones, y productos
		 * de segunda mano con distintas valoraciones y estados, para poder cubrir todos
		 * los criterios de ambos filtros.
		 *
		 * Productos de venta: libro1 precio=10 puntuacion=9 categoria1 libro2 precio=20
		 * puntuacion=5 categoria1 figura1 precio=35 puntuacion=8 categoria2 juego1
		 * precio=50 puntuacion=3 categoria3
		 *
		 * Productos segunda mano: usado1 tasacion=15 PERFECTO visible=true
		 * bloqueado=false usado2 tasacion=8 MUY_BUENO visible=true bloqueado=false
		 * usado3 tasacion=4 MUY_USADO visible=true bloqueado=false usado4 tasacion=10
		 * MUY_BUENO visible=true bloqueado=true usado5 sin valoracion visible=false
		 * bloqueado=true
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		Empleado empleado1 = new Empleado("empleado1", "Clave1@");
		empleado1.asignarPermiso(TipoPermisos.VALORACION_PRODUCTOS);
		tienda.getUsuarios().add(empleado1);

		Categoria categoria1 = new Categoria("categoria1", "desc");
		Categoria categoria2 = new Categoria("categoria2", "desc");
		Categoria categoria3 = new Categoria("categoria3", "desc");
		tienda.getCategorias().add(categoria1);
		tienda.getCategorias().add(categoria2);
		tienda.getCategorias().add(categoria3);

		Comic libro1 = new Comic("libro1", "d", "img1", 10.00, 20, 200, "editorial1", 2001);
		Comic libro2 = new Comic("libro2", "d", "img2", 20.00, 10, 400, "editorial2", 2002);
		Figura figura1 = new Figura("figura1", "d", "img3", 35.00, 15, 20, 15, 12, "mat1", "marca1");
		JuegoMesa juego1 = new JuegoMesa("juego1", "d", "img4", 50.00, 12, 2, 4, 8, 99, "tipo1");
		libro1.addCategoria(categoria1);
		libro2.addCategoria(categoria1);
		figura1.addCategoria(categoria2);
		juego1.addCategoria(categoria3);
		tienda.añadirProducto(libro1);
		tienda.añadirProducto(libro2);
		tienda.añadirProducto(figura1);
		tienda.añadirProducto(juego1);

		// Puntuaciones
		Cliente cliente1 = new Cliente("cliente1", "Clave2@", "00000000X");
		new Reseña(cliente1, libro1, 9.0, "");
		new Reseña(cliente1, libro2, 5.0, "");
		new Reseña(cliente1, figura1, 8.0, "");
		new Reseña(cliente1, juego1, 3.0, "");

		// Productos segunda mano
		Cliente cliente2 = new Cliente("cliente2", "Clave3@", "99999999Z");
		tienda.getUsuarios().add(cliente2);

		Producto2Mano usado1 = new Producto2Mano(cliente2, "usado1", "d", "img5");
		Producto2Mano usado2 = new Producto2Mano(cliente2, "usado2", "d", "img6");
		Producto2Mano usado3 = new Producto2Mano(cliente2, "usado3", "d", "img7");
		Producto2Mano usado4 = new Producto2Mano(cliente2, "usado4", "d", "img8");
		Producto2Mano usado5 = new Producto2Mano(cliente2, "usado5", "d", "img9");

		usado1.valorar(15.0, EstadoProducto.PERFECTO, empleado1);
		usado2.valorar(8.0, EstadoProducto.MUY_BUENO, empleado1);
		usado3.valorar(4.0, EstadoProducto.MUY_USADO, empleado1);
		usado4.valorar(10.0, EstadoProducto.MUY_BUENO, empleado1);

		// Publicamos los validos y dejamos bloqueado y sin valoracion fuera
		tienda.publicarParaIntercambio(usado1);
		tienda.publicarParaIntercambio(usado2);
		tienda.publicarParaIntercambio(usado3);
		// usado4: visible pero bloqueado manualmente
		usado4.setVisible(true);
		// usado5: sin valoracion, bloqueado y no visible (estado inicial)

		System.out.println("\tMontaje listo.");

		System.out.println("\n============= FiltroVenta - estado inicial =============");

		/*
		 * Con filtro por defecto (sin restricciones) todos los productos con stock
		 * pasan. Comprobamos tambien toString y resetear.
		 */
		FiltroVenta fv = new FiltroVenta();

		check("Filtro por defecto: libro1 (precio 10) pasa", fv.productoCumpleFiltro(libro1));
		check("Filtro por defecto: figura1 (precio 35) pasa", fv.productoCumpleFiltro(figura1));
		check("Filtro con null devuelve false", !fv.productoCumpleFiltro(null));
		check("toString contiene 'FiltroVenta'", fv.toString().contains("FiltroVenta"));

		System.out.println("\n============= FiltroVenta - precio =============");

		/*
		 * Filtramos por rango de precio y comprobamos que solo pasan los productos
		 * dentro del rango, incluyendo los extremos.
		 */
		fv.setPrecioMinimo(15);
		fv.setPrecioMaximo(40);

		check("precio 10 (libro1) no pasa rango [15,40]", !fv.productoCumpleFiltro(libro1));
		check("precio 20 (libro2) pasa rango [15,40]", fv.productoCumpleFiltro(libro2));
		check("precio 35 (figura1) pasa rango [15,40]", fv.productoCumpleFiltro(figura1));
		check("precio 50 (juego1) no pasa rango [15,40]", !fv.productoCumpleFiltro(juego1));

		// Extremos del rango
		fv.setPrecioMinimo(20);
		fv.setPrecioMaximo(35);
		check("precio exactamente igual al minimo (20) pasa", fv.productoCumpleFiltro(libro2));
		check("precio exactamente igual al maximo (35) pasa", fv.productoCumpleFiltro(figura1));

		System.out.println("\n============= FiltroVenta - puntuacion =============");

		fv.resetear();
		fv.setPuntuacionMinima(7.0);

		check("puntuacion 9 (libro1) pasa minimo 7", fv.productoCumpleFiltro(libro1));
		check("puntuacion 8 (figura1) pasa minimo 7", fv.productoCumpleFiltro(figura1));
		check("puntuacion 5 (libro2) no pasa minimo 7", !fv.productoCumpleFiltro(libro2));
		check("puntuacion 3 (juego1) no pasa minimo 7", !fv.productoCumpleFiltro(juego1));

		System.out.println("\n============= FiltroVenta - categorias =============");

		fv.resetear();
		fv.añadirCategoria(categoria1);

		check("libro1 (categoria1) pasa filtro por categoria categoria1", fv.productoCumpleFiltro(libro1));
		check("libro2 (categoria1) pasa filtro por categoria categoria1", fv.productoCumpleFiltro(libro2));
		check("figura1 (categoria2) no pasa filtro por categoria1", !fv.productoCumpleFiltro(figura1));
		check("juego1 (categoria3) no pasa filtro por categoria1", !fv.productoCumpleFiltro(juego1));

		// Añadir segunda categoria: pasan categoria1 O categoria2
		fv.añadirCategoria(categoria2);
		check("figura1 pasa con categorias [categoria1, categoria2]", fv.productoCumpleFiltro(figura1));
		check("juego1 sigue sin pasar con categorias [categoria1, categoria2]", !fv.productoCumpleFiltro(juego1));

		// Eliminar una categoria
		fv.eliminarCategoria(categoria1);
		check("libro1 no pasa tras eliminar categoria1 del filtro", !fv.productoCumpleFiltro(libro1));
		check("figura1 sigue pasando solo con categoria2", fv.productoCumpleFiltro(figura1));

		// Añadir categoria null no falla
		fv.añadirCategoria(null);
		check("añadir null no añade nada al filtro", fv.getCategorias().size() == 1);

		System.out.println("\n============= FiltroVenta - combinado =============");

		/*
		 * Precio [10,20], puntuacion >= 7, categoria1. Solo libro1 (precio 10,
		 * puntuacion 9, categoria1) debe pasar. libro2 tiene puntuacion 5 y no llega al
		 * minimo.
		 */
		fv.resetear();
		fv.setPrecioMinimo(10);
		fv.setPrecioMaximo(20);
		fv.setPuntuacionMinima(7.0);
		fv.añadirCategoria(categoria1);

		check("Solo libro1 pasa el filtro combinado", fv.productoCumpleFiltro(libro1));
		check("libro2 no pasa (puntuacion 5 < 7)", !fv.productoCumpleFiltro(libro2));
		check("figura1 no pasa (precio 35 > 20)", !fv.productoCumpleFiltro(figura1));

		System.out.println("\n============= FiltroVenta - control errores setters =============");

		fv.resetear();
		fv.setPrecioMinimo(-5);
		check("setPrecioMinimo negativo no cambia el valor", fv.getPrecioMinimo() == 0);

		fv.setPrecioMaximo(100);
		fv.setPrecioMinimo(200);
		check("setPrecioMinimo > precioMaximo no cambia el valor", fv.getPrecioMinimo() == 0);

		fv.setPrecioMaximo(50);
		fv.setPrecioMinimo(100);
		check("setPrecioMaximo < precioMinimo no cambia el valor", fv.getPrecioMaximo() == 50);

		fv.setPuntuacionMinima(-1);
		check("setPuntuacionMinima negativa no cambia el valor", fv.getPuntuacionMinima() == 0);

		fv.setPuntuacionMinima(11);
		check("setPuntuacionMinima > 10 no cambia el valor", fv.getPuntuacionMinima() == 0);

		System.out.println("\n============= FiltroVenta - resetear =============");

		fv.setPrecioMinimo(10);
		fv.setPrecioMaximo(30);
		fv.setPuntuacionMinima(5);
		fv.añadirCategoria(categoria1);
		fv.resetear();

		check("Tras resetear, precioMinimo = 0", fv.getPrecioMinimo() == 0);
		check("Tras resetear, precioMaximo = MAX", fv.getPrecioMaximo() == Double.MAX_VALUE);
		check("Tras resetear, puntuacionMinima = 0", fv.getPuntuacionMinima() == 0);
		check("Tras resetear, categorias vacia", fv.getCategorias().isEmpty());
		check("Tras resetear, libro1 vuelve a pasar", fv.productoCumpleFiltro(libro1));

		System.out.println("\n============= FiltroSegundaMano - estado inicial =============");

		/*
		 * Con filtro por defecto pasan todos los productos visibles, no bloqueados y
		 * con valoracion. Los bloqueados y sin valoracion no.
		 */
		FiltroSegundaMano fsm = new FiltroSegundaMano();

		check("Filtro por defecto: usado1 pasa", fsm.cumpleFiltro(usado1));
		check("Filtro por defecto: usado2 pasa", fsm.cumpleFiltro(usado2));
		check("Filtro por defecto: usado3 pasa", fsm.cumpleFiltro(usado3));
		check("usado5 no pasa (sin valoracion)", !fsm.cumpleFiltro(usado5));
		check("null devuelve false", !fsm.cumpleFiltro(null));
		check("toString contiene 'FiltroSegundaMano'", fsm.toString().contains("FiltroSegundaMano"));

		System.out.println("\n============= FiltroSegundaMano - precio =============");

		fsm.setValorMinimo(5);
		fsm.setValorMaximo(12);

		check("tasacion 15 (usado1) no pasa rango [5,12]", !fsm.cumpleFiltro(usado1));
		check("tasacion 8  (usado2) pasa rango [5,12]", fsm.cumpleFiltro(usado2));
		check("tasacion 4  (usado3) no pasa rango [5,12]", !fsm.cumpleFiltro(usado3));

		// Extremos
		fsm.setValorMinimo(8);
		fsm.setValorMaximo(15);
		check("tasacion exactamente igual al minimo (8) pasa", fsm.cumpleFiltro(usado2));
		check("tasacion exactamente igual al maximo (15) pasa", fsm.cumpleFiltro(usado1));

		System.out.println("\n============= FiltroSegundaMano - estado =============");

		/*
		 * estadoMinimo indica la calidad minima: PERFECTO(0) es el mejor, DAÑADO(5) el
		 * peor aceptado. Un producto pasa si su estado tiene ordinal <=
		 * estadoMinimo.ordinal() (es igual o mejor).
		 */
		fsm.resetear();
		fsm.setEstadoMinimo(EstadoProducto.MUY_BUENO); // acepta PERFECTO y MUY_BUENO

		check("PERFECTO pasa con estadoMinimo MUY_BUENO", fsm.cumpleFiltro(usado1));
		check("MUY_BUENO pasa con estadoMinimo MUY_BUENO", fsm.cumpleFiltro(usado2));
		check("MUY_USADO no pasa con estadoMinimo MUY_BUENO", !fsm.cumpleFiltro(usado3));

		fsm.setEstadoMinimo(EstadoProducto.PERFECTO); // solo acepta PERFECTO
		check("Solo PERFECTO pasa con estadoMinimo PERFECTO", fsm.cumpleFiltro(usado1));
		check("MUY_BUENO no pasa con estadoMinimo PERFECTO", !fsm.cumpleFiltro(usado2));

		fsm.setEstadoMinimo(null); // sin restriccion de estado
		check("Con estadoMinimo null todos los estados pasan", fsm.cumpleFiltro(usado3));

		System.out.println("\n============= FiltroSegundaMano - control errores setters =============");

		fsm.resetear();
		fsm.setValorMinimo(-1);
		check("setValorMinimo negativo no cambia el valor", fsm.getValorMinimo() == 0);

		fsm.setValorMaximo(20);
		fsm.setValorMinimo(30);
		check("setValorMinimo > valorMaximo no cambia el valor", fsm.getValorMinimo() == 0);

		fsm.setValorMinimo(5);
		fsm.setValorMaximo(3);
		check("setValorMaximo < valorMinimo no cambia el valor", fsm.getValorMaximo() == 20);

		System.out.println("\n============= FiltroSegundaMano - resetear =============");

		fsm.setValorMinimo(5);
		fsm.setValorMaximo(10);
		fsm.setEstadoMinimo(EstadoProducto.PERFECTO);
		fsm.resetear();

		check("Tras resetear, valorMinimo = 0", fsm.getValorMinimo() == 0);
		check("Tras resetear, valorMaximo = MAX", fsm.getValorMaximo() == Double.MAX_VALUE);
		check("Tras resetear, estadoMinimo = null", fsm.getEstadoMinimo() == null);
		check("Tras resetear, usado3 vuelve a pasar", fsm.cumpleFiltro(usado3));

		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}