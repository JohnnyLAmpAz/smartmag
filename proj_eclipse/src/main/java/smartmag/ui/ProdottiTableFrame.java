package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLIntegrityConstraintViolationException;

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
import smartmag.models.BoxModel;
import smartmag.models.ProductModel;
import smartmag.models.ui.ProductTableModel;
import smartmag.ui.utils.BasicWindow;

public class ProdottiTableFrame extends BasicWindow {

	private static final long serialVersionUID = 1L;
	private ProductModel prodModel;
	private ProductTableModel tableModel = new ProductTableModel();
	private JTable table;
	private TableRowSorter<ProductTableModel> sorter;
	private JTextField tfFilter;

	/**
	 * creazione frame per visualizzazione prodotti e operazioni su di essi
	 */
	public ProdottiTableFrame() {
		super("Lista Prodotti", 300, 350, true);

		getContentPane().setLayout(new BorderLayout());

		table = new JTable(tableModel);
		sorter = new TableRowSorter<ProductTableModel>(tableModel);
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
		JButton btnRefresh = new JButton("Aggiorna");
		btnRefresh.setToolTipText("Aggiorna tutti i dati dal DB");
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ProductModel.refreshDataFromDb();
				tableModel.stateChanged(null);
				BoxModel.refreshDataFromDb();
			}
		});
		btnsPanel.add(btnRefresh);

		// Info btn
		JButton btnOpen = new JButton("Info");
		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Prodotto selezionato
				try {
					Prodotto p = tableModel
							.getProductModelAt(table.getSelectedRow())
							.getProdotto();

					BasicWindow w = new BasicWindow("Prodotto " + p.getId(),
							250, 300, true);
					w.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					w.setLocationRelativeTo(ProdottiTableFrame.this);
					ProdPanel prodPanel = new ProdPanel(p, false);
					prodPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
					w.setContentPane(prodPanel);
					w.setVisible(true);
				} catch (IndexOutOfBoundsException e3) {
					JOptionPane.showMessageDialog(ProdottiTableFrame.this,
							"nessun prodotto selezionato",
							"Errore selezione prodotto",
							JOptionPane.ERROR_MESSAGE);
				}

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
						ProductModel.getNextAvailableId());
				if (newProd != null) {
					try {
						prodModel = ProductModel.getProductModelOf(newProd);
						prodModel.create();
					} catch (IntegrityConstraintViolationException
							| SQLIntegrityConstraintViolationException
							| IllegalArgumentException e1) {
						int id = newProd.getId();
						Prodotto fetchProd = ProductModel.getProdById(id);
						if (fetchProd != null) {
							JOptionPane.showMessageDialog(
									ProdottiTableFrame.this,
									"L'ID %d è già in uso dal prodotto: "
											+ fetchProd.toString(),
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

		// posiziona

		JButton btnAddProd = new JButton("Posiziona/Colloca");
		btnAddProd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					prodModel = ProductModel.getProductModelOf(

							tableModel.getProductModelAt(table.getSelectedRow())
									.getProdotto());
					AssegnaProdottoDialog dialog = new AssegnaProdottoDialog(
							ProdottiTableFrame.this, prodModel.getProdotto());
					dialog.setVisible(true);
				} catch (IndexOutOfBoundsException e4) {
					JOptionPane.showMessageDialog(ProdottiTableFrame.this,
							"nessun prodotto selezionato",
							"Errore selezione prodotto",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnsPanel.add(btnAddProd);

		// modifica

		JButton btnEditProd = new JButton("Modifica");
		btnEditProd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				prodModel = ProductModel.getProductModelOf(
						tableModel.getProductModelAt(table.getSelectedRow())
								.getProdotto());
				Prodotto editProd = EditProdDialog.showEditProdDialog(
						ProdottiTableFrame.this,
						tableModel.getProductModelAt(table.getSelectedRow())
								.getProdotto());
				if (editProd != null) {
					try {
						prodModel.updateProdotto(editProd);
						prodModel.setProdotto(editProd);
					} catch (IntegrityConstraintViolationException
							| SQLIntegrityConstraintViolationException
							| IllegalArgumentException e1) {
						{
							int id = editProd.getId();
							if (ProductModel.checkId(id)) {
								JOptionPane.showMessageDialog(
										ProdottiTableFrame.this,
										"L'ID %d è già in uso ");
							} else {
								JOptionPane.showMessageDialog(
										ProdottiTableFrame.this, e1.toString(),
										"Errore integrità DB",
										JOptionPane.ERROR_MESSAGE);
							}
						}

					}
				}
			}
		});
		btnsPanel.add(btnEditProd);

		// Delete btn
		JButton btnCanc = new JButton("Elimina");
		btnCanc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Prodotto selezionato
				ProductModel pm = tableModel
						.getProductModelAt(table.getSelectedRow());
				Prodotto p = pm.getProdotto();

				// Non fare nulla se non selezionato
				if (p == null)
					return;

				String msg = "Vuoi davvero eliminare il prodotto #%d-%s ?"
						.formatted(p.getId(), p.getNome());
				int res = JOptionPane.showConfirmDialog(ProdottiTableFrame.this,
						msg);
				if (res == JOptionPane.YES_OPTION) {

					// Elimina prodotto
					pm.deleteProdotto();
				}
			}
		});
		btnsPanel.add(btnCanc);

		this.pack();
		this.setLocationRelativeTo(null);

	}

	private void newFilter() {
		RowFilter<ProductTableModel, Object> rf = null;
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
