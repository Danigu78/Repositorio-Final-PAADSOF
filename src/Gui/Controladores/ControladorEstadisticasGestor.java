package Gui.Controladores;

import java.time.LocalDate;
import usuarios.Gestor;
import usuarios.Cliente;
import java.util.List;
import excepciones.AñoInvalidoException;
import excepciones.RangoFechasInvalidoException;

/**
 * Controlador de estadísticas para el gestor.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorEstadisticasGestor {

    private Gestor gestor;

    /**
     * Constructor del controlador de estadísticas.
     *
     * @param gestor El gestor logueado
     */
    public ControladorEstadisticasGestor(Gestor gestor) {
        this.gestor = gestor;
    }

    /**
     * Devuelve los clientes con más compras.
     *
     * @return Lista de clientes ordenada por compras
     */
    public List<Cliente> getTopCompras() {
        return gestor.verClientesTopCompras();
    }

    /**
     * Devuelve los clientes con más intercambios.
     *
     * @return Lista de clientes ordenada por intercambios
     */
    public List<Cliente> getTopIntercambios() {
        return gestor.verClientesTopIntercambios();
    }

    /**
     * Devuelve los ingresos totales por ventas.
     *
     * @return Total de ingresos por ventas
     */
    public double getIngresosVentas() {
        return gestor.consultarIngresosVenta();
    }

    /**
     * Devuelve los ingresos totales por tasaciones.
     *
     * @return Total de ingresos por tasaciones
     */
    public double getIngresosTasaciones() {
        return gestor.consultarIngresosTasacion();
    }

    /**
     * Devuelve los ingresos por meses del año actual.
     *
     * @return Array con los ingresos de cada mes
     */
    public double[] getIngresosPorMeses() {
        try {
            return gestor.consultarIngresosPorMesesActual();
        } catch (AñoInvalidoException | RangoFechasInvalidoException e) {
            return new double[12];
        }
    }

    /**
     * Devuelve los ingresos en un rango de fechas.
     *
     * @param inicio Fecha de inicio
     * @param fin    Fecha de fin
     * @return Total de ingresos en el rango
     */
    public double getIngresosRango(LocalDate inicio, LocalDate fin) {
        try {
            return gestor.consultarIngresosRango(inicio, fin);
        } catch (RangoFechasInvalidoException e) {
            return 0;
        }
    }
}