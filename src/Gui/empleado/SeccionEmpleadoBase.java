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
 * Guarda la ventana y el empleado.
 */
public abstract class SeccionEmpleadoBase extends PanelBaseInterfaz {

	private static final long serialVersionUID = 1L;

	protected final Empleado empleado;

	protected SeccionEmpleadoBase(VentanaPrincipal ventana, Empleado empleado) {
		super(ventana);
		this.empleado = empleado;
	}

	protected TableRowSorter<DefaultTableModel> ponerOrdenacionTabla(JTable tabla, DefaultTableModel modelo) {
		TableRowSorter<DefaultTableModel> ordenador = new TableRowSorter<>(modelo);
		tabla.setRowSorter(ordenador);
		return ordenador;
	}

	protected JTextField crearBuscadorTabla(TableRowSorter<DefaultTableModel> ordenador) {
		JTextField campo = crearCampoCompacto();
		campo.setPreferredSize(new Dimension(VentanaPrincipal.escalar(260), VentanaPrincipal.escalar(34)));
		escucharCambios(campo, () -> filtrarTabla(ordenador, campo.getText()));
		return campo;
	}

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
