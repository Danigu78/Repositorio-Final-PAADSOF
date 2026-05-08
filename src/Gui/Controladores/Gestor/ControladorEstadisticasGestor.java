package Gui.Controladores.Gestor;

import Gui.Gestor.SubpanelEstadisticasGestor;
import usuarios.Cliente;
import usuarios.Gestor;
import excepciones.AñoInvalidoException;
import excepciones.RangoFechasInvalidoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de estadísticas para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorEstadisticasGestor implements ActionListener {

    private SubpanelEstadisticasGestor vista;
    private Gestor gestor;

    public ControladorEstadisticasGestor(SubpanelEstadisticasGestor vista, Gestor gestor) {
        this.vista = vista;
        this.gestor = gestor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("consultarAño")) {
            vista.procesarConsultarAño();
        } else if (cmd.equals("consultarRango")) {
            vista.procesarConsultarRango();
        } else if (cmd.equals("consultarRangoVentas")) {
            vista.procesarConsultarRangoVentas();
        } else if (cmd.equals("consultarRangoTasacion")) {
            vista.procesarConsultarRangoTasacion();
        }
    }

    public List<Cliente> getTopCompras() {
        return gestor.verClientesTopCompras();
    }

    public List<Cliente> getTopIntercambios() {
        return gestor.verClientesTopIntercambios();
    }

    public List<Cliente> getTopCancelados() {
        return gestor.verClientesConMasPedidosCancelados();
    }

    public double getIngresosVentas() {
        return gestor.consultarIngresosVenta();
    }

    public double getIngresosTasaciones() {
        return gestor.consultarIngresosTasacion();
    }

    public double[] getIngresosPorMeses() {
        try {
            return gestor.consultarIngresosPorMesesActual();
        } catch (AñoInvalidoException | RangoFechasInvalidoException e) {
            return new double[12];
        }
    }

    public double[] getIngresosPorAño(int año) {
        try {
            return gestor.consultarIngresosPorMeses(año);
        } catch (AñoInvalidoException | RangoFechasInvalidoException e) {
            return new double[12];
        }
    }

    public double getIngresosRango(LocalDate inicio, LocalDate fin) {
        try {
            return gestor.consultarIngresosRango(inicio, fin);
        } catch (RangoFechasInvalidoException e) {
            return 0;
        }
    }

    public double getIngresosVentasRango(LocalDate inicio, LocalDate fin) {
        try {
            return gestor.consultarIngresosRango(inicio, fin);
        } catch (RangoFechasInvalidoException e) {
            return 0;
        }
    }

    public double getIngresosTasacionRango(LocalDate inicio, LocalDate fin) {
        try {
            return gestor.consultarIngresosTasacionRango(inicio, fin);
        } catch (RangoFechasInvalidoException e) {
            return 0;
        }
    }
}