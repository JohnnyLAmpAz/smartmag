package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import smartmag.data.Movimentazione;
import smartmag.data.StatoMovim;
import smartmag.data.Utente;
import smartmag.models.MovimenModel;

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
	private String matrMag;

	/**
	 * Create the panel.
	 */
	public MovimenMngmtPanel() {
		this.matrMag = MainWindow.getLoggedInUser().getMatricola();
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
				mm.assignToWorker(MainWindow.getLoggedInUser());
			}
		});
		btnPanel.add(btnAssign);

		// Pulsante per contrassegnare una movimentazione come PRELEVATA
		btnLoad = new JButton("Preleva");
		btnLoad.setEnabled(false);
		stati = new ArrayList<StatoMovim>();
		stati.add(StatoMovim.PRESA_IN_CARICO);
		tablePanel.table.getSelectionModel()
				.addListSelectionListener(new TableSelectionListener(tablePanel,
						btnLoad, stati, matrMag));
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

		tablePanel.table.getSelectionModel()
				.addListSelectionListener(new TableSelectionListener(tablePanel,
						btnCompleted, stati, matrMag));
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
		tablePanel.table.getSelectionModel()
				.addListSelectionListener(new TableSelectionListener(tablePanel,
						btnAnnulla, stati, matrMag));
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
}

/**
 * Table selection listener per attivare un componente solo se lo stato della
 * movimentazione selezionata rientra tra quelli indicati e se l'utente
 * corrisponde a quello indicato (se diverso da null).
 */
class TableSelectionListener implements ListSelectionListener {

	MovimTablePanel mtp;
	Component c;
	ArrayList<StatoMovim> s;
	String matr;

	/**
	 * @param mtp     istanza modello tabella movimentazioni
	 * @param c       componente da attivare/disattivare
	 * @param s       lista di stati per cui viene attivato {@code c}
	 * @param matrMag matricola utente. se null non viene controllato
	 */
	public TableSelectionListener(MovimTablePanel mtp, Component c,
			ArrayList<StatoMovim> s, String matrMag) {
		this.mtp = mtp;
		this.c = c;
		this.s = s;
		this.matr = matrMag;
	}

	public TableSelectionListener(MovimTablePanel mtp, Component c,
			ArrayList<StatoMovim> s) {
		this(mtp, c, s, null);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		MovimenModel mm = mtp.getSelectedMovimModel();
		if (mm == null) {
			c.setEnabled(false);
			return;
		}
		Movimentazione movim = mm.getMovim();
		Utente assignedMag = movim.getMagazziniere();
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		int[] ii = lsm.getSelectedIndices();
		if (ii.length != 1 || !s.contains(movim.getStato())) {
			c.setEnabled(false);
		} else if (matr != null && assignedMag != null
				&& !assignedMag.getMatricola().equals(matr)) {
			c.setEnabled(false);
		} else
			c.setEnabled(true);
	}
}
