package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import smartmag.data.TipoUtente;
import smartmag.data.Utente;
import smartmag.db.Db;
import smartmag.ui.utils.BasicWindow;

/**
 * Finestra principale ed entry point dell'applicazione. Gestisce le varie view
 * mostrate all'utente partendo dal login..
 */
public class MainWindow extends BasicWindow {

	private static final long serialVersionUID = 1L;
	private static final MainWindow mainFrame = new MainWindow();

	private Utente utente;
	private JPanel contentPane;
	private JLabel lblWelcome;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmLogout;
	private JTabbedPane tabbedPane;

	private MainWindow() {
		super("SmartMag", 450, 300, true);

		utente = null;

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		// Logout menu item
		mntmLogout = new JMenuItem("Logout");

		// Mappa l'azione al listener di App
		mntmLogout.setActionCommand("logout");
		mntmLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showLogin();
			}
		});
		mnFile.add(mntmLogout);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);
		lblWelcome = new JLabel("Benvenuto!");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblWelcome, BorderLayout.SOUTH);
	}

	/**
	 * Rigenera l'interfaccia della finestra per il nuovo utente autenticato.
	 * 
	 * @param u Utente autenticato
	 */
	private void newLoggedInUser() {

		// Svuota interfaccia
		tabbedPane.removeAll();

		switch (utente.getTipo()) {
			case TipoUtente.MANAGER: {
				UsersMngmtPanel usersMngmtPanel = new UsersMngmtPanel();
				tabbedPane.addTab("Users Management", null, usersMngmtPanel,
						null);
			}
			default:
				// TODO
		}

		// TODO others panes

		lblWelcome.setText("Benvenuto %s!".formatted(utente.getNome()));
	}

	/**
	 * Mostra (di nuovo) il login per poi caricare finestra principale in base
	 * all'utente autenticato.
	 */
	private void showLogin() {

		setVisible(false);
		utente = LoginDialog.showLoginDialog(this);

		if (utente == null)
			System.exit(0);

		lblWelcome.setText("Benvenuto " + utente.getNome() + "!");

		// Carica interfaccia per utente loggato
		newLoggedInUser();
		setVisible(true);
	}

	/**
	 * Restituisce l'utente autenticato nell'app.
	 * 
	 * @return clone dell'utente loggato; null se non loggato.
	 */
	public static Utente getLoggedInUser() {
		if (mainFrame.utente == null)
			return null;
		return mainFrame.utente.clone();
	}

	/**
	 * Avvia applicazione SmartMag, partendo dal login utente.
	 */
	public static void main(String[] args) {

		// Load db
		try {
			Db.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Mostro login
				mainFrame.showLogin();
			}
		});
	}
}
