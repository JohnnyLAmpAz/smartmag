package smartmag.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import smartmag.models.MovimenModel;
import smartmag.models.ui.MovimenTableModel;

/**
 * JPanel con tabella di tutte le Movimentazioni all'interno di uno ScrollPane
 */
public class MovimTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;
	private MovimenTableModel tableModel = new MovimenTableModel();

	/**
	 * Crea il pannello
	 */
	public MovimTablePanel() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable(tableModel);
		scrollPane.setViewportView(table);
	}

	public MovimenModel getSelectedUserModel() {
		int i = table.getSelectedRow();
		if (i < 0)
			return null;
		return tableModel.getMovimenModelAt(i);
	}

}