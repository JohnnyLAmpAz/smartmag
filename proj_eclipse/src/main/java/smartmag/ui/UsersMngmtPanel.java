package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import smartmag.models.UtenteModel;

/**
 * JPanel della gestione degli utenti (dipendenti) volto all'uso del Manager.
 */
public class UsersMngmtPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private UsersTablePanel tablePanel;

	/**
	 * Crea il pannello della gestione utenti.
	 */
	public UsersMngmtPanel() {
		setLayout(new BorderLayout(0, 0));

		tablePanel = new UsersTablePanel();
		add(tablePanel);

		JPanel btnPanel = new JPanel();
		add(btnPanel, BorderLayout.SOUTH);

		// Pulsante per aggiungere un utente
		JButton btnAdd = new JButton("Aggiungi");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLocationRelativeTo(null);

				JPanel content = new JPanel(new BorderLayout());
				UserPanel userPanel = new UserPanel();
				content.add(userPanel, BorderLayout.CENTER);
				JButton addBtn = new JButton("Crea");
				addBtn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent ae) {
						UtenteModel.createUtente(userPanel.getUtente());
						frame.setVisible(false);
					}
				});
				content.add(addBtn, BorderLayout.SOUTH);
				frame.setContentPane(content);
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
		btnPanel.add(btnAdd);

		// Pulsante per eliminare utente selezionato
		JButton btnDel = new JButton("Elimina");
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UtenteModel selected = tablePanel.getSelectedUserModel();
				if (selected != null) {
					int res = JOptionPane.showConfirmDialog(
							UsersMngmtPanel.this,
							"Sei sicuro di voler eliminare il seguente utente?\n"
									+ selected.getUtente(),
							"ATTENZIONE", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if (res == JOptionPane.OK_OPTION)

						// Elimina attraverso il modello
						selected.delete();
				}
			}
		});
		btnPanel.add(btnDel);

	}

	// TODO: to UnitTest?
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new UsersMngmtPanel());
		frame.setBounds(0, 0, 450, 350);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
