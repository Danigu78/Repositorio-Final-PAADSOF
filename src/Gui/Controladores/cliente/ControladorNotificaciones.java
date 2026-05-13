package Gui.Controladores.cliente;

import Gui.cliente.SubpanelNotificaciones;
import tienda.GuardadoTienda;
import tienda.Notificacion;
import tienda.TipoNotificacion;
import tienda.Tienda;
import usuarios.Cliente;
import productos.Categoria;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del subpanel de notificaciones del cliente. Gestiona la bandeja,
 * las preferencias de notificación y las categorías de interés. 
 * 
 * @author Daniel
 * @version 1.0
 */
public class ControladorNotificaciones implements ActionListener {

	/** Vista del subpanel de notificaciones. */
	private SubpanelNotificaciones vista;

	/** Cliente logueado. */
	private Cliente cliente;

	/**
	 * Constructor del controlador de notificaciones.
	 *
	 * @param vista   El subpanel de notificaciones
	 * @param cliente El cliente logueado
	 */
	public ControladorNotificaciones(SubpanelNotificaciones vista, Cliente cliente) {
		this.vista = vista;
		this.cliente = cliente;
	}

	/**
	 * Gestiona los eventos de los botones de la vista.
	 *
	 * @param e El evento de acción
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e == null)
			return;
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		switch (cmd) {
		case "refrescar":
			vista.cargarNotificaciones();
			break;
		case "ver":
			vista.verNotificacionSeleccionada();
			break;
		case "marcarTodas":
			marcarTodasComoLeidas();
			break;
		case "activar":
			cambiarPreferencia(true);
			break;
		case "desactivar":
			cambiarPreferencia(false);
			break;
		case "añadirCategoria":
			añadirCategoriaInteres();
			break;
		case "quitarCategoria":
			quitarCategoriaInteres();
			break;
		case "filtrar":
			vista.cargarNotificaciones();
			break;

		}
	}

	/**
	 * Activa o desactiva el tipo de notificación seleccionado en el combo.
	 *
	 * @param activar true para activar, false para desactivar
	 */
	private void cambiarPreferencia(boolean activar) {
		TipoNotificacion tipo = vista.getTipoSeleccionado();
		if (tipo == null) {
			vista.mostrarError("Selecciona un tipo de notificación.");
			return;
		}
		cliente.configurarPreferenciaNotificacion(tipo, activar);
		GuardadoTienda.guardar(Tienda.getInstancia());
		vista.actualizarEstadoPreferencia(tipo, cliente.getPreferencias().debeRecibirNotificacion(tipo));
		vista.mostrarMensaje("Preferencia actualizada correctamente.");
	}

	/**
	 * Añade la categoría seleccionada a las categorías de interés del cliente.
	 */
	private void añadirCategoriaInteres() {
		String nombreCategoria = vista.getCategoriaSeleccionada();
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			vista.mostrarError("Selecciona una categoría.");
			return;
		}
		boolean ok = cliente.añadirCategoriaInteresParaRecibirInfo(nombreCategoria);
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
			vista.actualizarListaCategorias(getCategoriasTienda(), getCategoriasInteres());
			vista.mostrarMensaje("Categoría añadida a tus intereses.");
		} else {
			vista.mostrarError("No se pudo añadir la categoría. " + "Puede que ya estuviera en tu lista.");
		}
	}

	/**
	 * Quita la categoría seleccionada de las categorías de interés del cliente.
	 */
	private void quitarCategoriaInteres() {
		String nombreCategoria = vista.getCategoriaIntereSeleccionada();
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			vista.mostrarError("Selecciona una categoría de tu lista.");
			return;
		}
		boolean ok = cliente.eliminarCategoriaInteres(nombreCategoria);
		if (ok) {
			GuardadoTienda.guardar(Tienda.getInstancia());
			vista.actualizarListaCategorias(getCategoriasTienda(), getCategoriasInteres());
			vista.mostrarMensaje("Categoría eliminada de tus intereses.");
		} else {
			vista.mostrarError("No se pudo eliminar la categoría.");
		}
	}

	/**
	 * Devuelve los nombres de todas las categorías de la tienda.
	 *
	 * @return Lista de nombres de categorías
	 */
	public List<String> getCategoriasTienda() {
		List<String> nombres = new ArrayList<>();
		for (Categoria c : Tienda.getInstancia().getCategoriasActivas())
			nombres.add(c.getNombre());
		return nombres;
	}

	/**
	 * Devuelve los nombres de las categorías de interés del cliente.
	 *
	 * @return Lista de nombres de categorías de interés
	 */
	public List<String> getCategoriasInteres() {
		List<String> nombres = new ArrayList<>();
		for (Categoria c : cliente.getPreferencias().getCategoriasInteres())
			if (!c.isEliminada()) {
				nombres.add(c.getNombre());
			}

		return nombres;
	}

	/**
	 * Devuelve las notificaciones del cliente filtradas.
	 *
	 * @param filtro "Todas", "No vistas" o "Vistas"
	 * @return Lista filtrada
	 */
	public List<Notificacion> getNotificaciones(String filtro) {
		List<Notificacion> notificaciones = cliente.getNotificaciones();
		if (notificaciones == null)
			return new ArrayList<>();
		List<Notificacion> resultado = new ArrayList<>();
		for (Notificacion n : notificaciones) {
			if (n == null)
				continue;
			if ("No vistas".equals(filtro) && n.isLeida())
				continue;
			if ("Vistas".equals(filtro) && !n.isLeida())
				continue;
			resultado.add(n);
		}
		resultado.sort((n1, n2) -> {
			if (n1.getFechaEnvio() == null && n2.getFechaEnvio() == null)
				return 0;
			if (n1.getFechaEnvio() == null)
				return 1;
			if (n2.getFechaEnvio() == null)
				return -1;
			return n2.getFechaEnvio().compareTo(n1.getFechaEnvio());
		});
		return resultado;
	}

	/**
	 * Cuenta las notificaciones no leídas del cliente.
	 *
	 * @return Número de notificaciones no leídas
	 */
	public int contarNoLeidas() {
		int count = 0;
		List<Notificacion> notificaciones = cliente.getNotificaciones();
		if (notificaciones == null)
			return 0;
		for (Notificacion n : notificaciones) {
			if (n != null && !n.isLeida())
				count++;
		}
		return count;
	}

	/**
	 * Devuelve el estado actual de una preferencia.
	 *
	 * @param tipo El tipo de notificación
	 * @return true si está activada
	 */
	public boolean getEstadoPreferencia(TipoNotificacion tipo) {
		return cliente.getPreferencias().debeRecibirNotificacion(tipo);
	}

	/**
	 * Marca una notificación como leída usando el método del cliente.
	 *
	 * @param n La notificación a marcar
	 */
	public void marcarComoLeida(Notificacion n) {
		if (n != null) {
			cliente.verNotificacion(n);
			GuardadoTienda.guardar(Tienda.getInstancia());
		}
	}

	/**
	 * Marca todas las notificaciones del cliente como leídas.
	 */
	private void marcarTodasComoLeidas() {
		List<Notificacion> notificaciones = cliente.getNotificaciones();
		if (notificaciones == null || notificaciones.isEmpty()) {
			vista.mostrarError("No hay notificaciones.");
			return;
		}
		for (Notificacion n : notificaciones) {
			if (n != null)
				cliente.verNotificacion(n);
		}
		GuardadoTienda.guardar(Tienda.getInstancia());
		vista.cargarNotificaciones();
		vista.mostrarMensaje("Todas las notificaciones se han marcado como vistas.");
	}
}
