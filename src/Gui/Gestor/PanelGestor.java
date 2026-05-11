package Gui.Gestor;

import Gui.PanelAbstractoGeneral;
import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorPanelGestor;

import javax.swing.*;
import java.awt.*;
import usuarios.Gestor;

/**
 * Panel principal del gestor en CheckPoint. Extiende AbstractPanelSection para
 * reutilizar helpers visuales y la barra de navegación común. Sigue el patrón
 * MVC de los apuntes — delega la navegación en ControladorPanelGestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class PanelGestor extends PanelAbstractoGeneral {

	private static final String SEC_EMPLEADOS = "EMPLEADOS";
	private static final String SEC_CATEGORIAS = "CATEGORIAS";
	private static final String SEC_PRODUCTOS_DESCUENTOS = "PRODUCTOS_DESCUENTOS";
	private static final String SEC_ESTADISTICAS = "ESTADISTICAS";
	private static final String SEC_CONFIGURACION = "CONFIGURACION";
	private static final String SEC_PERFIL = "PERFIL";

	/** Controlador del panel — gestiona la navegación entre secciones. */
	private ControladorPanelGestor controlador;

	/** Gestor logueado. */
	private Gestor gestor;

	/** CardLayout para alternar entre secciones. */
	private CardLayout cardSecciones;

	/** Panel contenedor de todas las secciones. */
	private JPanel panelSecciones;

	/** Barra de navegación — guardada para marcarBotonBarraActivoPorCmd(). */
	private JPanel barra;

	/** Subpaneles del gestor. */
	private SubpanelEmpleadosGestor subpanelEmpleados;
	private SubpanelCategoriasGestor subpanelCategorias;
	private SubpanelProductosDescuentosGestor subpanelProductosDescuentos;
	private SubpanelEstadisticasGestor subpanelEstadisticas;
	private SubpanelConfiguracionGestor subpanelConfiguracion;
	private SubpanelPerfilGestor subpanelPerfil;

	/**
	 * Constructor del panel gestor.
	 *
	 * @param ventana La ventana principal
	 */
	public PanelGestor(VentanaPrincipal ventana) {
		super(ventana);
	}

	/**
	 * Actualiza el panel con el gestor logueado y construye la interfaz.
	 *
	 * @param gestor El gestor logueado
	 */
	public void actualizarGestor(Gestor gestor) {
		this.gestor = gestor;
		removeAll();
		inicializarUI();
		revalidate();
		repaint();
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
	 * Construye la interfaz del panel gestor. Crea el controlador y lo registra en
	 * la barra de navegación.
	 */
	private void inicializarUI() {
		controlador = new ControladorPanelGestor(this);

		String[][] pestañas = { { "Empleados", SEC_EMPLEADOS }, { "Categorías", SEC_CATEGORIAS },
				{ "Productos y Descuentos", SEC_PRODUCTOS_DESCUENTOS }, { "Estadísticas", SEC_ESTADISTICAS },
				{ "Configuración", SEC_CONFIGURACION }, { "Mi Perfil", SEC_PERFIL } };

		// crearBarraNavegacion() de AbstractPanelSection
		barra = crearBarraNavegacion("🎮 CheckPoint - Gestor", gestor != null ? gestor.getNickname() : "Gestor",
				pestañas, controlador);
		add(barra, BorderLayout.NORTH);

		cardSecciones = new CardLayout();
		panelSecciones = new JPanel(cardSecciones);
		panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

		subpanelEmpleados = new SubpanelEmpleadosGestor(ventana, gestor);
		subpanelCategorias = new SubpanelCategoriasGestor(ventana, gestor, this);
		subpanelProductosDescuentos = new SubpanelProductosDescuentosGestor(ventana, gestor);
		subpanelEstadisticas = new SubpanelEstadisticasGestor(ventana, gestor);
		subpanelConfiguracion = new SubpanelConfiguracionGestor(ventana, gestor);
		subpanelPerfil = new SubpanelPerfilGestor(ventana, gestor);

		panelSecciones.add(subpanelEmpleados, SEC_EMPLEADOS);
		panelSecciones.add(subpanelCategorias, SEC_CATEGORIAS);
		panelSecciones.add(subpanelProductosDescuentos, SEC_PRODUCTOS_DESCUENTOS);
		panelSecciones.add(subpanelEstadisticas, SEC_ESTADISTICAS);
		panelSecciones.add(subpanelConfiguracion, SEC_CONFIGURACION);
		panelSecciones.add(subpanelPerfil, SEC_PERFIL);

		add(panelSecciones, BorderLayout.CENTER);
		cardSecciones.show(panelSecciones, SEC_EMPLEADOS);
		marcarBotonBarraActivoPorCmd(barra, SEC_EMPLEADOS);
	}
	/**
	 * Refresca los filtros de categorías en el subpanel de productos y descuentos.
	 * Lo llama SubpanelCategoriasGestor tras crear o eliminar una categoría.
	 */
	public void refrescarFiltrosCategorias() {
	    if (subpanelProductosDescuentos != null)
	        subpanelProductosDescuentos.refrescarFiltrosCategorias();
	}
}