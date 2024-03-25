package smartmag.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import smartmag.data.StatoMovim;
import smartmag.models.MovimenModel;
import smartmag.ui.table_models.MovimenTableModel;

/**
 * JPanel con tabella di tutte le Movimentazioni all'interno di uno ScrollPane
 */
public class MovimTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	JTable table;
	private MovimenTableModel tableModel = new MovimenTableModel();

	/**
	 * Crea il pannello
	 */
	public MovimTablePanel() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSorter(new MySorter(tableModel));

		scrollPane.setViewportView(table);
	}

	public MovimenModel getSelectedMovimModel() {
		int i = table.getSelectedRow();
		if (i < 0)
			return null;
		i = table.getRowSorter().convertRowIndexToModel(i);
		return tableModel.getMovimenModelAt(i);
	}
}

class MySorter extends TableRowSorter<MovimenTableModel> {

	public MySorter() {
		this(null);
	}

	public MySorter(MovimenTableModel model) {
		super(model);

		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();

		// Ordina per stato, ordine, prod, qta
		sortKeys.add(new RowSorter.SortKey(4, SortOrder.DESCENDING));
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
		setSortKeys(sortKeys);

		// Per ordinare enum stato movim in base a ordine definizione (ordinal);
		setComparator(4, (o1, o2) -> {
			StatoMovim s1 = (StatoMovim) o1;
			StatoMovim s2 = (StatoMovim) o2;
			return s1.compareTo(s2);
		});

		sort();
	}
}
