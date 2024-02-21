package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.jooq.exception.IntegrityConstraintViolationException;

import smartmag.Prodotto;
import smartmag.models.ProdModel;
import smartmag.ui.utils.BasicWindow;

public class ProdottiTableFrame extends BasicWindow {

	private static final long serialVersionUID = 1L;
	private ProdModel prodModel;
	private ProdottiTableModel tableModel;
	private JTable table;

	public ProdottiTableFrame() {
		super("Lista Prodotti", 250, 350, true);

		this.setLayout(new BorderLayout());

		prodModel = new ProdModel();
		tableModel = new ProdottiTableModel(prodModel);

		table = new JTable(tableModel);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(new EmptyBorder(5, 5, 0, 5));
		table.setFillsViewportHeight(true);
		table.setRowHeight(20);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel btnsPanel = new JPanel();
		getContentPane().add(btnsPanel, BorderLayout.SOUTH);

		// Open btn
		JButton btnOpen = new JButton("Open info (fetch from DB)");
		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Prodotto selezionato
				Prodotto p = getSelectedProd();

				// Non fare nulla se non selezionato
				if (p == null)
					return; // TODO: improve

				// Fetch prod dal db
				p = prodModel.getProdottoById(p.getId());

				// Stampa info
				// TODO: apri frame con ProdPanel e btns per modifica/del
//				JOptionPane.showMessageDialog(ProdottiTableFrame.this,
//						p.toString());
				BasicWindow w = new BasicWindow("Prodotto " + p.getId(), 250,
						300, true);
				w.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				ProdPanel prodPanel = new ProdPanel(p, false); // TODO: improve
																// editable
				prodPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				w.setContentPane(prodPanel);
				w.setVisible(true);
			}
		});
		btnsPanel.add(btnOpen);

		JButton btnNewProd = new JButton("Nuovo Prodotto");
		btnNewProd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Apri un nuovo NewProdDialog
				Prodotto newProd = NewProdDialog.showNewProdDialog(
						ProdottiTableFrame.this,
						prodModel.getNextAvailableId());
				if (newProd != null) {
					try {
						prodModel.createProdotto(newProd);
						tableModel.addProd(newProd);
					} catch (IntegrityConstraintViolationException e1) {
						// TODO Auto-generated catch block
//						e1.printStackTrace();
						JOptionPane.showMessageDialog(ProdottiTableFrame.this,
								e1, "Errore integrità DB",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnsPanel.add(btnNewProd);

		this.pack();
		this.setLocationRelativeTo(null);

	}

	private Prodotto getSelectedProd() {
		int rowIndex = table.getSelectedRow();
		if (rowIndex < 0)
			return null;
		return tableModel.getProdAt(rowIndex);
	}
}
