package Gui.empleado;

import Gui.VentanaPrincipal;
import Gui.PanelBaseInterfaz;
import java.awt.Dimension;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import usuarios.Empleado;

/**
 * Base de las pantallas del empleado.
 *
 * @author Lucas
 * @version 1.0
 */
public abstract class SeccionEmpleadoBase extends PanelBaseInterfaz {

	private static final long serialVersionUID = 1L;

	/** Empleado que está usando la interfaz. */
	protected final Empleado empleado;

	/**
	 * Constructor base de las secciones del empleado.
	 *
	 * @param ventana ventana principal de la aplicación
	 * @param empleado empleado autenticado
	 */
	protected SeccionEmpleadoBase(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana);
		this.empleado = empleado;
	}

	/**
	 * Habilita ordenación en una tabla.
	 *
	 * @param tabla tabla a ordenar
	 * @param modelo modelo de la tabla
	 * @return sorter asociado a la tabla
	 */
	protected TableRowSorter<DefaultTableModel> ponerOrdenacionTabla(JTable tabla, DefaultTableModel modelo) {
		TableRowSorter<DefaultTableModel> ordenador = new TableRowSorter<>(modelo);
		tabla.setRowSorter(ordenador);
		return ordenador;
	}

	/**
	 * Crea un campo de búsqueda asociado a una tabla ordenable.
	 *
	 * @param ordenador sorter de la tabla
	 * @return campo de búsqueda configurado
	 */
	protected JTextField crearBuscadorTabla(TableRowSorter<DefaultTableModel> ordenador) {
		JTextField campo = crearCampoCompacto();
		campo.setPreferredSize(new Dimension(VentanaPrincipal.escalar(260), VentanaPrincipal.escalar(34)));
		escucharCambios(campo, () -> filtrarTabla(ordenador, campo.getText()));
		return campo;
	}

	/**
	 * Aplica un filtro de búsqueda sobre la tabla.
	 *
	 * @param ordenador sorter de la tabla
	 * @param texto texto introducido por el usuario
	 */
	private void filtrarTabla(TableRowSorter<DefaultTableModel> ordenador, String texto) {
		if (ordenador == null) {
			return;
		}

		String busqueda = texto == null ? "" : texto.trim();

		if (busqueda.isBlank()) {
			ordenador.setRowFilter(null);
		} else {
			ordenador.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busqueda)));
		}
	}
}
