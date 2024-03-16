package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import smartmag.data.TipoUtente;
import smartmag.data.Utente;
import smartmag.db.Db;
import smartmag.ui.utils.BasicWindow;

/**
 * Finestra principale ed entry point dell'applicazione. Gestisce le varie view
 * mostrate all'utente partendo dal login.
 */
public class MainWindow extends BasicWindow {

	private static final long serialVersionUID = 1L;
	private static final MainWindow mainFrame = new MainWindow();

	private Utente utente;
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu mnSession;
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private JLabel lblWelcome;
	private JLabel lblRole;

	private MainWindow() {
		super("SmartMag", 650, 500, true);

		utente = null;

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnSession = new JMenu("Sessione");
		menuBar.add(mnSession);

		// Logout menu item
		JMenuItem mntmLogout = new JMenuItem("Logout", KeyEvent.VK_O);

		// Set Ctrl+Q shortcut for logout
		mntmLogout.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mntmLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showLogin();
			}
		});
		mnSession.add(mntmLogout);

		// Quit menu item
		JMenuItem mntmQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
		mntmQuit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnSession.add(mntmQuit);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		panel = new JPanel();
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		lblWelcome = new JLabel("Benvenuto!");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblWelcome, BorderLayout.WEST);

		lblRole = new JLabel("Ruolo: ");
		panel.add(lblRole, BorderLayout.EAST);
	}

	/**
	 * Rigenera l'interfaccia della finestra per il nuovo utente autenticato.
	 * 
	 * @param u Utente autenticato
	 */
	private void newLoggedInUser() {

		// Svuota interfaccia
		tabbedPane.removeAll();

		// Costruisco interfaccia a seconda del ruolo utente
		switch (utente.getTipo()) {

			// MANAGER
			case TipoUtente.MANAGER: {

				// Gestione Prodotti
				ProdottiTableFrame ptf = new ProdottiTableFrame();
				tabbedPane.addTab("Gestione Prodotti", null,
						ptf.getContentPane(), null);

				// Gestione Box
				BoxTableFrame btf = new BoxTableFrame();
				tabbedPane.addTab("Gestione Box", null, btf.getContentPane(),
						null);

				// Gestione Utenti
				UsersMngmtPanel usersMngmtPanel = new UsersMngmtPanel();
				tabbedPane.addTab("Gestione Utenti", null, usersMngmtPanel,
						null);

				break;
			}

			// RESPONSABILE
			case TipoUtente.RESPONSABILE: {

				// Gestione Ordini
				TabellaOrdini to = new TabellaOrdini();
				JPanel ordersMngmtPanel = to.getContentPane();
				tabbedPane.addTab("Gestione Ordini", null, ordersMngmtPanel,
						null);
				break;
			}

			// MAGAZZINIERI
			case TipoUtente.QUALIFICATO:
			case TipoUtente.MAGAZZINIERE: {

				// Gestione Ordini
				tabbedPane.addTab("Movimentazioni", null,
						new MovimenMngmtPanel(), null);

				// UI specifica per MAGAZZINIERE QUALIFICATO
				if (utente.getTipo() == TipoUtente.QUALIFICATO) {
					// TODO
				}

				break;
			}

			default:
				throw new InternalError("Utente non valido");
		}

		lblWelcome.setText("Benvenuto %s!".formatted(utente.getNome()));
		lblRole.setText("Ruolo: " + utente.getTipo().name());
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

		// Disabilita print logo e consigli di jOOQ
		System.setProperty("org.jooq.no-logo", "true");
		System.setProperty("org.jooq.no-tips", "true");

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
