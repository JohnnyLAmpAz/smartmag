package smartmag.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import smartmag.models.UtenteModel;
import smartmag.ui.table_models.UsersTableModel;

/**
 * JPanel con tabella di tutti gli Utenti all'interno di uno ScrollPane
 */
public class UsersTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;
	private UsersTableModel tableModel = new UsersTableModel();

	/**
	 * Crea il pannello
	 */
	public UsersTablePanel() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable(tableModel);
		scrollPane.setViewportView(table);

	}

	public UtenteModel getSelectedUserModel() {
		int i = table.getSelectedRow();
		if (i < 0)
			return null;
		return tableModel.getUserModelAt(i);
	}

}
