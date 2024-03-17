package smartmag.ui.utils;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class BasicWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Icona del logo
	 */
	public static final ImageIcon logo = new ImageIcon("./img/logo_alpha.png");

	/**
	 * Immagine con scritta logo
	 */
	public static final ImageIcon scrittaLogo = new ImageIcon(
			"./img/smartmag.png");

	public BasicWindow(String title, int x, int y, boolean resizable) {
		this.setTitle(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(resizable);

		// Set Icon
		this.setIconImage(logo.getImage());

		this.setSize(x, y);
		this.setLocationRelativeTo(null); // Center the window on the screen
	}
}
