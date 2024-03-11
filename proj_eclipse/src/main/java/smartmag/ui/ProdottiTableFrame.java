package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import org.jooq.exception.IntegrityConstraintViolationException;

import smartmag.data.Prodotto;
import smartmag.models.ProdModel;
import smartmag.ui.utils.BasicWindow;

public class ProdottiTableFrame extends BasicWindow {

	private static final long serialVersionUID = 1L;
	private ProdModel prodModel;
	private ProdTableModel tableModel;
	private JTable table;
	private TableRowSorter<ProdTableModel> sorter;
	private JTextField tfFilter;

	public ProdottiTableFrame() {
		super("Lista Prodotti", 300, 350, true);

		this.setLayout(new BorderLayout());

		prodModel = new ProdModel();
		tableModel = new ProdTableModel(prodModel);

		table = new JTable(tableModel);
		sorter = new TableRowSorter<ProdTableModel>(tableModel);
		table.setRowSorter(sorter);

		// Cerca
		JPanel filterPanel = new JPanel(new BorderLayout());
		filterPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		tfFilter = new JTextField();
		tfFilter.setToolTipText("Pattern di ricerca per filtrare i prodotti.");
		filterPanel.add(tfFilter);
		tfFilter.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}
		});
		getContentPane().add(filterPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(new EmptyBorder(5, 5, 0, 5));
		table.setFillsViewportHeight(true);
		table.setRowHeight(20);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel btnsPanel = new JPanel();
		getContentPane().add(btnsPanel, BorderLayout.SOUTH);

		// Refresh btn
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setToolTipText("Refreshes all data from DB");
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.refreshData();
			}
		});
		btnsPanel.add(btnRefresh);

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

				// Se non esiste, aggiorna e basta
				if (p == null) {
					tableModel.refreshData();
					return;
				}

				// Stampa info
				// TODO: apri frame con ProdPanel e btns per modifica/del
//				JOptionPane.showMessageDialog(ProdottiTableFrame.this,
//						p.toString());
				BasicWindow w = new BasicWindow("Prodotto " + p.getId(), 250,
						300, true);
				w.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				w.setLocationRelativeTo(ProdottiTableFrame.this);
				ProdPanel prodPanel = new ProdPanel(p, false); // TODO: improve
																// editable
				prodPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				w.setContentPane(prodPanel);
				w.setVisible(true);
			}
		});
		btnsPanel.add(btnOpen);

		// New btn
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
						int id = newProd.getId();
						Prodotto fetchProd = prodModel.getProdottoById(id);
						if (fetchProd != null) {
							JOptionPane.showMessageDialog(
									ProdottiTableFrame.this,
									"L'ID %d è già in uso dal prodotto:\n%s"
											.formatted(id,
													fetchProd.toString()),
									"ID prodotto già utilizzato",
									JOptionPane.ERROR_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(
									ProdottiTableFrame.this, e1.toString(),
									"Errore integrità DB",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		btnsPanel.add(btnNewProd);

		// Delete btn
		JButton btnCanc = new JButton("Elimina");
		btnCanc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Prodotto selezionato
				Prodotto p = getSelectedProd();

				// Non fare nulla se non selezionato
				if (p == null)
					return; // TODO: improve

				String msg = "Vuoi davvero eliminare il prodotto #%d-%s ?"
						.formatted(p.getId(), p.getNome());
				int res = JOptionPane.showConfirmDialog(ProdottiTableFrame.this,
						msg);
				if (res == JOptionPane.YES_OPTION) {

					// Elimina prodotto
					tableModel.deleteProdotto(p);
				}
			}
		});
		btnsPanel.add(btnCanc);

		this.pack();
		this.setLocationRelativeTo(null);

	}

	private Prodotto getSelectedProd() {
		int index = table.getSelectedRow();
		if (index < 0)
			return null;
		return tableModel.getProdAt(table.convertRowIndexToModel(index));
	}

	private void newFilter() {
		RowFilter<ProdTableModel, Object> rf = null;
		// If current expression doesn't parse, don't update.
		try {
			// Filtra per ID, Nome e Descrizione
			rf = RowFilter.regexFilter(tfFilter.getText(), 0, 1, 2);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}
}
