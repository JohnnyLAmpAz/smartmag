package smartmag.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class BasicWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	public BasicWindow(String title, int x, int y, boolean resizable) {
		// TODO Auto-generated constructor stub
		this.setTitle(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(resizable);

		// Set Icon
		ImageIcon logo = new ImageIcon("img/icon.png");
		this.setIconImage(logo.getImage());

		this.setSize(x, y);
		this.setLocationRelativeTo(null);
	}
}