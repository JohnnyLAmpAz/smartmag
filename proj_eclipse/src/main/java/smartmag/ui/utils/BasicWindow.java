package smartmag.ui.utils;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class BasicWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	public BasicWindow(String title, int x, int y, boolean resizable) {
		this.setTitle(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(resizable);

		// Set Icon
		ImageIcon logo = new ImageIcon("img/icon.png");
		this.setIconImage(logo.getImage());

		this.setSize(x, y);
		this.setLocationRelativeTo(null); // Center the window on the screen
	}
}
