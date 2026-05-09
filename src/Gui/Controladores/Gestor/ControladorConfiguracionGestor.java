package Gui.controladores.gestor;

import Gui.gestor.SubpanelConfiguracionGestor;
import Gui.gestor.SubpanelPerfilGestor;
import tienda.GuardadoTienda;
import tienda.Tienda;
import usuarios.Gestor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de configuración y perfil del gestor.
 * Implementa ActionListener según el patrón MVC de los apuntes.
 *
 * @author Antonino
 * @version 1.0
 */
public class ControladorConfiguracionGestor implements ActionListener {

    private Gestor gestor;
    private Tienda tienda;
    // La vista puede ser configuración o perfil
    private SubpanelConfiguracionGestor vistaConfig;
    private SubpanelPerfilGestor vistaPerfil;

    public ControladorConfiguracionGestor(SubpanelConfiguracionGestor vista, Gestor gestor) {
        this.vistaConfig = vista;
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    public ControladorConfiguracionGestor(SubpanelPerfilGestor vista, Gestor gestor) {
        this.vistaPerfil = vista;
        this.gestor = gestor;
        this.tienda = Tienda.getInstancia();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("guardarConfig") && vistaConfig != null) {
            vistaConfig.procesarGuardar();
        } else if (cmd.equals("guardarPerfil") && vistaPerfil != null) {
            vistaPerfil.procesarGuardar();
        }
    }

    public boolean configurarTiempos(int tOferta, int tCarrito, int tPago) {
        boolean ok = gestor.configurarTiemposSistema(tOferta, tCarrito, tPago);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean setPrecioTasacion(double precio) {
        boolean ok = gestor.setPrecioTasacion(precio);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public boolean modificarPerfil(String nuevoNickname, String nuevaPassword) {
        boolean ok = gestor.modificarPerfil(nuevoNickname, nuevaPassword);
        if (ok) GuardadoTienda.guardar(tienda);
        return ok;
    }

    public int getTiempoCarrito() { return tienda.getTiempoMaxCarrito(); }
    public int getTiempoOferta()  { return tienda.getTiempoMaxOferta(); }
    public int getTiempoPago()    { return tienda.getTiempoMaxPago(); }
    public double getPrecioTasacion() { return tienda.getPrecioTasacion(); }
    public String getNickname()   { return gestor.getNickname(); }
}