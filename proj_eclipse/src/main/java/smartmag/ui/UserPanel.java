package smartmag.ui;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import smartmag.data.TipoUtente;
import smartmag.data.Utente;

/**
 * JPanel per mostrare/inserimento dati utente
 */
public class UserPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tfMatricola;
	private JTextField tfNome;
	private JTextField tfCognome;
	private JPasswordField pwdField;
	private JComboBox<TipoUtente> comboRuolo;

	/**
	 * Crea il pannello con i campi inizialmente vuoti e abilitando le
	 * modifiche.
	 */
	public UserPanel() {
		this(new Utente(null, null, null, null, null), true);
	}

	/**
	 * Crea il pannello con i campi dell'utente passato e abilitando o meno le
	 * modifiche.
	 * 
	 * @param utente   utente con i dati da caricare nei campi.
	 * @param editable true per abilitare modifiche, altrimenti false.
	 */
	public UserPanel(Utente utente, boolean editable) {
		setLayout(new MigLayout("", "[][grow]", "[][][][][]"));

		JLabel lblMatricola = new JLabel("Matricola");
		add(lblMatricola, "cell 0 0,alignx trailing");

		tfMatricola = new JTextField();
		lblMatricola.setLabelFor(tfMatricola);
		add(tfMatricola, "cell 1 0,growx");
		tfMatricola.setColumns(10);
		tfMatricola.setText(utente.getMatricola());
		tfMatricola.setEditable(editable);

		JLabel lblNome = new JLabel("Nome");
		add(lblNome, "cell 0 1,alignx trailing");

		tfNome = new JTextField();
		lblNome.setLabelFor(tfNome);
		tfNome.setColumns(10);
		add(tfNome, "cell 1 1,growx");
		tfNome.setText(utente.getNome());
		tfNome.setEditable(editable);

		JLabel lblCognome = new JLabel("Cognome");
		add(lblCognome, "cell 0 2,alignx trailing");

		tfCognome = new JTextField();
		lblCognome.setLabelFor(tfCognome);
		tfCognome.setColumns(10);
		add(tfCognome, "cell 1 2,growx");
		tfCognome.setText(utente.getCognome());
		tfCognome.setEditable(editable);

		JLabel lblPassword = new JLabel("Password");
		add(lblPassword, "cell 0 3,alignx trailing");

		pwdField = new JPasswordField();
		lblPassword.setLabelFor(pwdField);
		add(pwdField, "cell 1 3,growx");
		pwdField.setText(utente.getPassword());
		pwdField.setEditable(editable);

		JLabel lblRuolo = new JLabel("Ruolo");
		add(lblRuolo, "cell 0 4,alignx trailing");

		comboRuolo = new JComboBox<>(TipoUtente.values());
		lblRuolo.setLabelFor(comboRuolo);
		add(comboRuolo, "cell 1 4,growx");
		comboRuolo.setSelectedItem(utente.getTipo());
		comboRuolo.setEnabled(editable);
	}

	/**
	 * Restituisce un'istanza di Utente con i dati contenuti nei campi del
	 * JPanel
	 */
	public Utente getUtente() {
		return new Utente(tfMatricola.getText(), tfNome.getText(),
				tfCognome.getText(), new String(pwdField.getPassword()),
				(TipoUtente) comboRuolo.getSelectedItem());
	}
}
