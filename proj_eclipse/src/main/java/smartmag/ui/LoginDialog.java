package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import smartmag.data.Utente;
import smartmag.db.Db;
import smartmag.models.UtenteModel;

// TODO: maybe better JFrame instead of JDialog
public class LoginDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Utente utente;
	private JTextField tfMatricola;
	private JPasswordField passwordField;
	private JLabel lblPasswd;
	private JLabel lblMatr;
	private JPanel panel;

	/**
	 * Create the dialog.
	 */
	private LoginDialog(Frame parent) {

		super(parent, "Login", true); // true per modalità modale

		// Set Icon
		ImageIcon logo = new ImageIcon("img/icon.png");
		this.setIconImage(logo.getImage());

		this.utente = null;

		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton exitButton = new JButton("Esci");
				exitButton.setActionCommand("exit");
				buttonPane.add(exitButton);
				exitButton.addActionListener(this);
			}
			{
				JButton loginButton = new JButton("Login");
				loginButton.setActionCommand("login");
				buttonPane.add(loginButton);
				getRootPane().setDefaultButton(loginButton);
				loginButton.addActionListener(this);
			}
		}
		{
			panel = new JPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			panel.add(contentPanel);
			contentPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
			contentPanel.setLayout(new MigLayout("", "[][grow]", "[][]"));
			{
				lblMatr = new JLabel("Matricola");
				contentPanel.add(lblMatr, "cell 0 0,alignx trailing");
			}
			{
				tfMatricola = new JTextField();
				lblMatr.setLabelFor(tfMatricola);
				contentPanel.add(tfMatricola, "cell 1 0,growx");
				tfMatricola.setColumns(20);
			}
			{
				lblPasswd = new JLabel("Password");
				contentPanel.add(lblPasswd, "cell 0 1,alignx trailing");
			}
			{
				passwordField = new JPasswordField();
				passwordField.setColumns(20);
				lblPasswd.setLabelFor(passwordField);
				contentPanel.add(passwordField, "cell 1 1,growx");
			}
		}
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	/**
	 * Apre un dialog di login e restituisce l'utente se le credenziali sono
	 * valide. Se viene premuto il pulsante "Esci", l'applicazione viene
	 * terminata.
	 * 
	 * @return L'utente autenticato.
	 */
	public static Utente showLoginDialog(Frame parent) {
		LoginDialog dialog = new LoginDialog(parent);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		return dialog.utente;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("login".equals(e.getActionCommand())) {
			String matr = tfMatricola.getText();
			if (matr == null || matr.isBlank())
				return;
			utente = UtenteModel.login(matr,
					new String(passwordField.getPassword()));

			// Successful login
			if (utente != null) {
				setVisible(false);
				dispose();
			}

			// Wrong credentials
			else {
				JOptionPane.showMessageDialog(this, "Credenziali non valide!",
						"Accesso negato", JOptionPane.ERROR_MESSAGE);
			}
		} else if ("exit".equals(e.getActionCommand())) {
			System.exit(0);
		}
	}

	// TODO: to UnitTest
	public static void main(String[] args) throws IOException {
		Db.getInstance();
		System.out.println(LoginDialog.showLoginDialog(null));
	}

}
