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

import smartmag.data.Box;
import smartmag.models.BoxModel;
import smartmag.ui.table_models.BoxTableModel;
import smartmag.ui.utils.BasicWindow;

public class BoxTableFrame extends BasicWindow {

	private static final long serialVersionUID = 1L;
	private BoxModel boxModel;
	private BoxTableModel tableModel = new BoxTableModel();
	private JTable table;
	private TableRowSorter<BoxTableModel> sorter;
	private JTextField tfFilter;

	/**
	 * creazione frame per la gestione dei box
	 */
	public BoxTableFrame() {
		super("Lista Box", 300, 350, true);

		getContentPane().setLayout(new BorderLayout());

		table = new JTable(tableModel);
		sorter = new TableRowSorter<BoxTableModel>(tableModel);
		table.setRowSorter(sorter);

		// Cerca
		JPanel filterPanel = new JPanel(new BorderLayout());
		filterPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		tfFilter = new JTextField();
		tfFilter.setToolTipText("Pattern di ricerca per filtrare i box");
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

		// Info btn
		JButton btnOpen = new JButton("Info");
		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Prodotto selezionato
				try {
					Box b = tableModel.getBoxModelAt(table.getSelectedRow())
							.getBox();

					BasicWindow w = new BasicWindow("Box " + b.getIndirizzo(),
							250, 300, true);
					w.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					w.setLocationRelativeTo(BoxTableFrame.this);
					BoxPanel boxPanel = new BoxPanel(b, false, false);
					boxPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
					w.setContentPane(boxPanel);
					w.setVisible(true);
				} catch (IndexOutOfBoundsException e3) {
					JOptionPane.showMessageDialog(BoxTableFrame.this,
							"nessun box selezionato", "Errore selezione box",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		btnsPanel.add(btnOpen);

		JButton btnEditProd = new JButton("Modifica");
		btnEditProd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boxModel = tableModel.getBoxModelAt(table.getSelectedRow());
				Box editBox = EditBoxDialog.showEditBoxDialog(
						BoxTableFrame.this,
						tableModel.getBoxModelAt(table.getSelectedRow())
								.getBox());
				if (editBox != null) {
					boxModel.setQuantita(editBox.getQuantità());
				}
			}
		});
		btnsPanel.add(btnEditProd);

	}

	/**
	 * metodo che permette di filtrare attraverso i diversi parametri i vari box
	 * nella tabella
	 */
	private void newFilter() {
		RowFilter<? super BoxTableModel, ? super Integer> rf = null;
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
