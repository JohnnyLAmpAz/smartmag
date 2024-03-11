package smartmag;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import smartmag.data.Utente;
import smartmag.ui.LoginDialog;
import smartmag.ui.UsersMngmtPanel;

public class Prova extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Utente utente;
	private JLabel lblWelcome;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmLogout;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Prova frame = new Prova();
				showLogin(frame);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Prova() {
		setTitle("Prova SmartMag");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		// Logout menu item
		mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				showLogin(Prova.this);
			}
		});
		mnFile.add(mntmLogout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		UsersMngmtPanel usersMngmtPanel = new UsersMngmtPanel();
		tabbedPane.addTab("Users Management", null, usersMngmtPanel, null);

		lblWelcome = new JLabel("Benvenuto!");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblWelcome, BorderLayout.SOUTH);
	}

	private static void showLogin(Prova frame) {
		frame.utente = LoginDialog.showLoginDialog(frame);

		if (frame.utente == null)
			System.exit(0);

		frame.lblWelcome.setText("Benvenuto " + frame.utente.getNome() + "!");
		frame.setVisible(true);
	}
}
