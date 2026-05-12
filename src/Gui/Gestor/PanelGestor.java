package Gui.Gestor;

import Gui.PanelBaseInterfaz;
import Gui.VentanaPrincipal;
import Gui.Controladores.Gestor.ControladorPanelGestor;

import javax.swing.*;
import java.awt.*;
import usuarios.Gestor;

/**
 * Panel principal del gestor 
 *
 * @author Antonino
 * @version 1.0
 */
public class PanelGestor extends PanelBaseInterfaz {

	/** Serial UID de la clase. */
	private static final long serialVersionUID = 1L;

	/** Identificador de la sección empleados. */
	private static final String SEC_EMPLEADOS = "EMPLEADOS";

	/** Identificador de la sección categorías. */
	private static final String SEC_CATEGORIAS = "CATEGORIAS";

	/** Identificador de la sección productos y descuentos. */
	private static final String SEC_PRODUCTOS_DESCUENTOS = "PRODUCTOS_DESCUENTOS";

	/** Identificador de la sección estadísticas. */
	private static final String SEC_ESTADISTICAS = "ESTADISTICAS";

	/** Identificador de la sección configuración. */
	private static final String SEC_CONFIGURACION = "CONFIGURACION";

	/** Identificador de la sección perfil. */
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

	/** Subpanel de empleados. */
	private SubpanelEmpleadosGestor subpanelEmpleados;

	/** Subpanel de categorías. */
	private SubpanelCategoriasGestor subpanelCategorias;

	/** Subpanel de productos y descuentos. */
	private SubpanelProductosDescuentosGestor subpanelProductosDescuentos;

	/** Subpanel de estadísticas. */
	private SubpanelEstadisticasGestor subpanelEstadisticas;

	/** Subpanel de configuración. */
	private SubpanelConfiguracionGestor subpanelConfiguracion;

	/** Subpanel de perfil. */
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
	 * @param gestor Gestor autenticado
	 */
	public void actualizarGestor(Gestor gestor) {
		this.gestor = gestor;
		removeAll();
		inicializarUI();
		revalidate();
		repaint();
	}

	/**
	 * Muestra la sección indicada en el área de contenido principal.
	 *
	 * @param seccion Identificador de la sección
	 */
	public void mostrarSeccion(String seccion) {
		cardSecciones.show(panelSecciones, seccion);
	}

	/**
	 * Marca la pestaña activa en la barra de navegación.
	 *
	 * @param cmd ActionCommand de la pestaña a marcar
	 */
	public void marcarPestaña(String cmd) {
		marcarBotonBarraActivoPorCmd(barra, cmd);
	}

	/**
	 * Construye la interfaz del panel gestor.
	 */
	private void inicializarUI() {
		controlador = new ControladorPanelGestor(this);

		String[][] pestañas = {
			{ "Empleados", SEC_EMPLEADOS },
			{ "Categorías", SEC_CATEGORIAS },
			{ "Productos y Descuentos", SEC_PRODUCTOS_DESCUENTOS },
			{ "Estadísticas", SEC_ESTADISTICAS },
			{ "Configuración", SEC_CONFIGURACION },
			{ "Mi Perfil", SEC_PERFIL }
		};
		
		barra = crearBarraNavegacion(
				" CheckPoint - Gestor",
				gestor != null ? gestor.getNickname() : "Gestor",
				pestañas,
				controlador
		);
		
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
		ventana.refrescarCategoriasCatalogo();
	}
}