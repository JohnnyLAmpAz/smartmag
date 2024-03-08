package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import smartmag.models.MovimenModel;
import smartmag.models.UtenteModel;

/**
 * JPanel della gestione delle movimentazioni volto all'uso dei Magazzinieri.
 */
public class MovimenMngmtPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MovimTablePanel tablePanel;

	/**
	 * Create the panel.
	 */
	public MovimenMngmtPanel() {
		setLayout(new BorderLayout(0, 0));

		tablePanel = new MovimTablePanel();
		add(tablePanel);

		JPanel btnPanel = new JPanel();
		add(btnPanel, BorderLayout.SOUTH);

		// Pulsante per prendere in carico una movimentazione
		JButton btnAdd = new JButton("Prendi in carico");
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MovimenModel mm = tablePanel.getSelectedMovimModel();
				UtenteModel um = UtenteModel.getUtenteModelOf("l.brivio1");
				mm.assignToWorker(um.getUtente());
			}
		});
		btnPanel.add(btnAdd);
	}

	// TODO: to UnitTest?
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new MovimenMngmtPanel());
		frame.setBounds(0, 0, 450, 350);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
