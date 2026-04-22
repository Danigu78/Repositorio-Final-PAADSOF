package Gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import usuarios.Cliente;

/**
 * Panel principal del cliente en CheckPoint.
 * 
 * <p>Contiene una barra de navegación superior con pestañas para acceder
 * a las diferentes secciones:</p>
 * <ul>
 *   <li>Catálogo de productos</li>
 *   <li>Carrito de compra</li>
 *   <li>Mis pedidos</li>
 *   <li>Segunda mano</li>
 *   <li>Intercambios</li>
 *   <li>Notificaciones</li>
 *   <li>Mi Perfil</li>
 * </ul>
 * 
 * <p>La barra superior tiene el logo a la izquierda, las pestañas en el centro
 * y el nombre del usuario con el botón de logout a la derecha.</p>
 * 
 * @author CheckPoint
 * @version 1.0
 */
public class PanelCliente extends JPanel {

    // ── Constantes de las secciones ───────────────────────────────────────────

    /** Identificador de la sección catálogo */
    private static final String SEC_CATALOGO = "CATALOGO";

    /** Identificador de la sección carrito */
    private static final String SEC_CARRITO = "CARRITO";

    /** Identificador de la sección pedidos */
    private static final String SEC_PEDIDOS = "PEDIDOS";

    /** Identificador de la sección segunda mano */
    private static final String SEC_SEGUNDA_MANO = "SEGUNDA_MANO";

    /** Identificador de la sección intercambios */
    private static final String SEC_INTERCAMBIOS = "INTERCAMBIOS";

    /** Identificador de la sección notificaciones */
    private static final String SEC_NOTIFICACIONES = "NOTIFICACIONES";

    /** Identificador de la sección perfil */
    private static final String SEC_PERFIL = "PERFIL";

    // ── Atributos ─────────────────────────────────────────────────────────────

    /** Referencia a la ventana principal para navegar */
    private VentanaPrincipal ventana;

    /** Cliente actualmente logueado */
    private Cliente cliente;

    /** Layout que gestiona el cambio entre secciones */
    private CardLayout cardSecciones;

    /** Panel contenedor de todas las secciones */
    private JPanel panelSecciones;

    /** Etiqueta con el nombre del usuario en la barra superior */
    private JLabel labelUsuario;

    /** Botón actualmente seleccionado en la barra de navegación */
    private JButton botonActivo;

    // ── Secciones ─────────────────────────────────────────────────────────────

    /** Panel del catálogo de productos */
    private SubpanelCatalogo subpanelCatalogo;

    /** Panel del carrito de compra */
    private SubpanelCarrito subpanelCarrito;

    /** Panel de mis pedidos */
    private SubpanelPedidos subpanelPedidos;

    /** Panel de productos de segunda mano */
    private SubpanelSegundaMano subpanelSegundaMano;

    /** Panel de intercambios */
    private SubpanelIntercambios subpanelIntercambios;

    /** Panel de notificaciones */
    private SubpanelNotificaciones subpanelNotificaciones;

    /** Panel del perfil del usuario */
    private SubpanelPerfil subpanelPerfil;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Constructor del panel cliente.
     * Construye la barra superior de navegación y crea todas las secciones.
     * 
     * @param ventana La ventana principal de la aplicación
     */
    public PanelCliente(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(VentanaPrincipal.COLOR_FONDO);
        inicializarUI();
    }

    // ── Inicialización ────────────────────────────────────────────────────────

    /**
     * Construye y organiza todos los componentes del panel cliente.
     * Crea la barra superior y el área de contenido con CardLayout.
     */
    private void inicializarUI() {
        // Barra superior de navegación
        JPanel barraNavegacion = crearBarraNavegacion();
        add(barraNavegacion, BorderLayout.NORTH);

        // Área de contenido (secciones)
        cardSecciones = new CardLayout();
        panelSecciones = new JPanel(cardSecciones);
        panelSecciones.setBackground(VentanaPrincipal.COLOR_FONDO);

        // Crear todas las secciones
        subpanelCatalogo      = new SubpanelCatalogo(ventana);
        subpanelCarrito       = new SubpanelCarrito(ventana);
        subpanelPedidos       = new SubpanelPedidos(ventana);
        subpanelSegundaMano   = new SubpanelSegundaMano(ventana);
        subpanelIntercambios  = new SubpanelIntercambios(ventana);
        subpanelNotificaciones= new SubpanelNotificaciones(ventana);
        subpanelPerfil        = new SubpanelPerfil(ventana);

        // Añadir secciones al CardLayout
        panelSecciones.add(subpanelCatalogo,       SEC_CATALOGO);
        panelSecciones.add(subpanelCarrito,        SEC_CARRITO);
        panelSecciones.add(subpanelPedidos,        SEC_PEDIDOS);
        panelSecciones.add(subpanelSegundaMano,    SEC_SEGUNDA_MANO);
        panelSecciones.add(subpanelIntercambios,   SEC_INTERCAMBIOS);
        panelSecciones.add(subpanelNotificaciones, SEC_NOTIFICACIONES);
        panelSecciones.add(subpanelPerfil,         SEC_PERFIL);

        add(panelSecciones, BorderLayout.CENTER);

        // Mostrar catálogo por defecto
        mostrarSeccion(SEC_CATALOGO);
    }

    /**
     * Crea la barra superior de navegación con el logo, las pestañas
     * y la información del usuario.
     * 
     * <p>La barra está dividida en tres zonas:</p>
     * <ul>
     *   <li>Izquierda: logo de CheckPoint</li>
     *   <li>Centro: botones de navegación (pestañas)</li>
     *   <li>Derecha: nombre de usuario y botón logout</li>
     * </ul>
     * 
     * @return El panel de la barra de navegación configurado
     */
    private JPanel crearBarraNavegacion() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);
        // Línea naranja en la parte inferior de la barra
        barra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
            BorderFactory.createEmptyBorder(0, 15, 0, 15)
        ));
        barra.setPreferredSize(new Dimension(0, 58));

        // ── Zona izquierda: logo ──────────────────────────────────────────────
        JLabel labelLogo = new JLabel("🎮 CheckPoint");
        labelLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelLogo.setForeground(VentanaPrincipal.COLOR_ACENTO);
        barra.add(labelLogo, BorderLayout.WEST);

        // ── Zona central: pestañas de navegación ──────────────────────────────
        JPanel panelPestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panelPestanas.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelPestanas.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Definir las pestañas: [texto visible, icono emoji, id de sección]
        String[][] pestanas = {
            {"Catálogo",        "🛍️", SEC_CATALOGO},
            {"Carrito",         "🛒", SEC_CARRITO},
            {"Mis Pedidos",     "📦", SEC_PEDIDOS},
            {"Segunda Mano",    "🔄", SEC_SEGUNDA_MANO},
            {"Intercambios",    "🤝", SEC_INTERCAMBIOS},
            {"Notificaciones",  "🔔", SEC_NOTIFICACIONES},
            {"Mi Perfil",       "👤", SEC_PERFIL}
        };

        // Crear un botón por cada pestaña
        for (String[] pestana : pestanas) {
            JButton boton = crearBotonPestana(pestana[1] + " " + pestana[0], pestana[2]);
            panelPestanas.add(boton);

            // El primer botón (Catálogo) empieza activo
            if (SEC_CATALOGO.equals(pestana[2])) {
                botonActivo = boton;
                marcarBotonActivo(boton);
            }
        }

        barra.add(panelPestanas, BorderLayout.CENTER);

        // ── Zona derecha: usuario y logout ────────────────────────────────────
        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelUsuario.setBackground(VentanaPrincipal.COLOR_PANEL);
        panelUsuario.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        // Etiqueta con el nombre del usuario (se actualiza al hacer login)
        labelUsuario = new JLabel("👤 Usuario");
        labelUsuario.setFont(VentanaPrincipal.FUENTE_NORMAL);
        labelUsuario.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        panelUsuario.add(labelUsuario);

        // Separador vertical decorativo
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 20));
        sep.setForeground(VentanaPrincipal.COLOR_BORDE);
        panelUsuario.add(sep);

        // Botón de logout
        JButton botonLogout = new JButton("🚪 Salir");
        botonLogout.setFont(VentanaPrincipal.FUENTE_PEQUENA);
        botonLogout.setForeground(new Color(220, 80, 80));
        botonLogout.setBackground(VentanaPrincipal.COLOR_PANEL);
        botonLogout.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        botonLogout.setFocusPainted(false);
        botonLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonLogout.addActionListener(e -> ventana.logout());
        // Efecto hover en el botón logout
        botonLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botonLogout.setForeground(new Color(255, 100, 100));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botonLogout.setForeground(new Color(220, 80, 80));
            }
        });
        panelUsuario.add(botonLogout);

        barra.add(panelUsuario, BorderLayout.EAST);

        return barra;
    }

    /**
     * Crea un botón de pestaña para la barra de navegación.
     * Al pulsar el botón, se navega a la sección correspondiente
     * y se actualiza el estilo para indicar cuál está activa.
     * 
     * @param texto   Texto del botón (con icono emoji)
     * @param seccion Identificador de la sección a mostrar al pulsar
     * @return El botón de pestaña configurado
     */
    private JButton crearBotonPestana(String texto, String seccion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
        boton.setBackground(VentanaPrincipal.COLOR_PANEL);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Al pasar el ratón por encima, iluminar si no está activo
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (boton != botonActivo) {
                    boton.setForeground(VentanaPrincipal.COLOR_TEXTO);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (boton != botonActivo) {
                    boton.setForeground(VentanaPrincipal.COLOR_TEXTO2);
                }
            }
        });

        // Al pulsar: activar esta pestaña y mostrar su sección
        boton.addActionListener(e -> {
            activarPestana(boton);
            mostrarSeccion(seccion);
            actualizarSeccion(seccion);
        });

        return boton;
    }

    /**
     * Aplica el estilo visual de "activo" a un botón de pestaña:
     * texto en color naranja y subrayado naranja en la parte inferior.
     * 
     * @param boton El botón al que aplicar el estilo activo
     */
    private void marcarBotonActivo(JButton boton) {
        boton.setForeground(VentanaPrincipal.COLOR_ACENTO);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, VentanaPrincipal.COLOR_ACENTO),
            BorderFactory.createEmptyBorder(8, 12, 6, 12)
        ));
    }

    /**
     * Marca un botón de pestaña como activo visualmente.
     * Primero desactiva el botón anterior y luego activa el nuevo.
     * 
     * @param boton El botón de pestaña a activar
     */
    private void activarPestana(JButton boton) {
        // Quitar el estilo activo del botón anterior
        if (botonActivo != null) {
            botonActivo.setForeground(VentanaPrincipal.COLOR_TEXTO2);
            botonActivo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }
        // Aplicar el estilo activo al nuevo botón
        botonActivo = boton;
        marcarBotonActivo(boton);
    }

    /**
     * Muestra la sección indicada en el área de contenido principal.
     * 
     * @param seccion Identificador de la sección a mostrar (usar las constantes SEC_*)
     */
    private void mostrarSeccion(String seccion) {
        cardSecciones.show(panelSecciones, seccion);
    }

    /**
     * Actualiza los datos de una sección cuando el usuario navega a ella.
     * De esta forma los datos siempre están frescos al entrar en cada sección.
     * 
     * @param seccion Identificador de la sección a actualizar
     */
    private void actualizarSeccion(String seccion) {
        if (cliente == null) return;
        switch (seccion) {
            case SEC_CATALOGO:        subpanelCatalogo.actualizar(cliente);       break;
            case SEC_CARRITO:         subpanelCarrito.actualizar(cliente);        break;
            case SEC_PEDIDOS:         subpanelPedidos.actualizar(cliente);        break;
            case SEC_SEGUNDA_MANO:    subpanelSegundaMano.actualizar(cliente);    break;
            case SEC_INTERCAMBIOS:    subpanelIntercambios.actualizar(cliente);   break;
            case SEC_NOTIFICACIONES:  subpanelNotificaciones.actualizar(cliente); break;
            case SEC_PERFIL:          subpanelPerfil.actualizar(cliente);         break;
        }
    }

    // ── Métodos públicos ──────────────────────────────────────────────────────

    /**
     * Actualiza el panel con los datos del cliente que acaba de hacer login.
     * Actualiza el nombre en la barra superior y carga todas las secciones.
     * 
     * @param cliente El cliente que ha iniciado sesión
     */
    public void actualizarCliente(Cliente cliente) {
        this.cliente = cliente;
        // Actualizar nombre en la barra superior
        labelUsuario.setText("👤 " + cliente.getNickname());
        // Cargar datos en todas las secciones
        subpanelCatalogo.actualizar(cliente);
        subpanelCarrito.actualizar(cliente);
        subpanelPedidos.actualizar(cliente);
        subpanelSegundaMano.actualizar(cliente);
        subpanelIntercambios.actualizar(cliente);
        subpanelNotificaciones.actualizar(cliente);
        subpanelPerfil.actualizar(cliente);
        // Mostrar catálogo por defecto al hacer login
        mostrarSeccion(SEC_CATALOGO);
    }
}