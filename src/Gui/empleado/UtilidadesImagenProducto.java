package Gui.empleado;

import Gui.VentanaPrincipal;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import utilidades.RutasImagen;

/** Utilidades de interfaz para imágenes de productos. */
public final class UtilidadesImagenProducto {

	private UtilidadesImagenProducto() {
	}

	public static String normalizarRutaImagen(String rutaImagen) {
		return RutasImagen.normalizarNombreArchivo(rutaImagen);
	}

	public static void mostrarImagenProducto(Component padre, String rutaImagen) {
		String nombreArchivo = normalizarRutaImagen(rutaImagen);

		if (nombreArchivo.isBlank()) {
			JOptionPane.showMessageDialog(padre, "Selecciona o escribe la imagen del producto.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			java.io.File archivo = new java.io.File("src/fotos", nombreArchivo);

			if (!archivo.exists() || !archivo.isFile() || !archivo.canRead()) {
				JOptionPane.showMessageDialog(padre, "No se pudo abrir la imagen:\n" + archivo.getAbsolutePath(),
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			java.awt.image.BufferedImage imagenOriginal = javax.imageio.ImageIO.read(archivo);

			if (imagenOriginal == null) {
				JOptionPane.showMessageDialog(padre,
						"El archivo existe, pero no es una imagen válida:\n" + archivo.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			Image imagenEscalada = imagenOriginal.getScaledInstance(VentanaPrincipal.escalar(420),
					VentanaPrincipal.escalar(320), Image.SCALE_SMOOTH);

			JLabel labelImagen = new JLabel(new ImageIcon(imagenEscalada));
			labelImagen.setHorizontalAlignment(JLabel.CENTER);

			JScrollPane scrollImagen = new JScrollPane(labelImagen);
			scrollImagen.setPreferredSize(new Dimension(VentanaPrincipal.escalar(470), VentanaPrincipal.escalar(370)));

			JOptionPane.showMessageDialog(padre, scrollImagen, "Imagen del producto", JOptionPane.PLAIN_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(padre, "Error al abrir la imagen:\n" + nombreArchivo, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
