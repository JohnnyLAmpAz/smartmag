package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import smartmag.data.StatoMovim;
import smartmag.models.MovimenModel;
import smartmag.models.UtenteModel;

/**
 * JPanel della gestione delle movimentazioni volto all'uso dei Magazzinieri.
 */
public class MovimenMngmtPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MovimTablePanel tablePanel;
	private JButton btnAssign;
	private JButton btnLoad;
	private JButton btnCompleted;
	private JButton btnAnnulla;

	/**
	 * Create the panel.
	 */
	public MovimenMngmtPanel() {
		setLayout(new BorderLayout(0, 0));

		tablePanel = new MovimTablePanel();
		add(tablePanel);

		JPanel btnPanel = new JPanel();
		add(btnPanel, BorderLayout.SOUTH);

		// Pulsante per PRENDERE IN CARICO una movimentazione
		btnAssign = new JButton("Prendi in carico");
		btnAssign.setEnabled(false);
		ArrayList<StatoMovim> stati = new ArrayList<StatoMovim>();
		stati.add(StatoMovim.NON_ASSEGNATA);
		tablePanel.table.getSelectionModel().addListSelectionListener(
				new TableSelectionListener(tablePanel, btnAssign, stati));
		btnAssign.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MovimenModel mm = tablePanel.getSelectedMovimModel();
				if (mm == null)
					return;
				// TODO: usa l'utente loggato
				UtenteModel um = UtenteModel.getUtenteModelOf("l.brivio1");
				mm.assignToWorker(um.getUtente());
			}
		});
		btnPanel.add(btnAssign);

		// Pulsante per contrassegnare una movimentazione come PRELEVATA
		btnLoad = new JButton("Preleva");
		btnLoad.setEnabled(false);
		stati = new ArrayList<StatoMovim>();
		stati.add(StatoMovim.PRESA_IN_CARICO);
		tablePanel.table.getSelectionModel().addListSelectionListener(
				new TableSelectionListener(tablePanel, btnLoad, stati));
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MovimenModel mm = tablePanel.getSelectedMovimModel();
				if (mm == null)
					return;
				mm.markAsLoaded();
			}
		});
		btnPanel.add(btnLoad);

		// Pulsante per contrassegnare una movimentazione come COMPLETATA
		btnCompleted = new JButton("Segna COMPLETATA");
		btnCompleted.setEnabled(false);
		stati = new ArrayList<StatoMovim>();
		stati.add(StatoMovim.PRELEVATA);

		tablePanel.table.getSelectionModel().addListSelectionListener(
				new TableSelectionListener(tablePanel, btnCompleted, stati));
		btnCompleted.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MovimenModel mm = tablePanel.getSelectedMovimModel();
				if (mm == null)
					return;
				mm.markAsCompleted();
			}
		});
		btnPanel.add(btnCompleted);

		// Pulsante per contrassegnare una movimentazione come ANNULLATA
		btnAnnulla = new JButton("Annulla");
		btnAnnulla.setEnabled(false);
		stati = new ArrayList<StatoMovim>();
		stati.add(StatoMovim.NON_ASSEGNATA);
		stati.add(StatoMovim.PRESA_IN_CARICO);
		tablePanel.table.getSelectionModel().addListSelectionListener(
				new TableSelectionListener(tablePanel, btnAnnulla, stati));
		btnAnnulla.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MovimenModel mm = tablePanel.getSelectedMovimModel();
				if (mm == null)
					return;
				mm.annulla();
			}
		});
		btnPanel.add(btnAnnulla);
	}

	// TODO: to UnitTest?
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new MovimenMngmtPanel());
		frame.setBounds(0, 0, 500, 350);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

/**
 * Table selection listener per attivare un componente solo se lo stato della
 * movimentazione selezionata corrisponde a quello specificato.
 */
class TableSelectionListener implements ListSelectionListener {

	MovimTablePanel mtp;
	Component c;
	ArrayList<StatoMovim> s;

	/**
	 * @param mtp istanza modello tabella movimentazioni
	 * @param c   componente da attivare/disattivare
	 * @param s   l'unico StatoMovimentazione per cui viene attivato {@code c}
	 */
	public TableSelectionListener(MovimTablePanel mtp, Component c,
			ArrayList<StatoMovim> s) {
		this.mtp = mtp;
		this.c = c;
		this.s = s;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		int[] ii = lsm.getSelectedIndices();
		if (ii.length != 1 || !s
				.contains(mtp.getSelectedMovimModel().getMovim().getStato())) {
			c.setEnabled(false);
		} else
			c.setEnabled(true);
	}
}
