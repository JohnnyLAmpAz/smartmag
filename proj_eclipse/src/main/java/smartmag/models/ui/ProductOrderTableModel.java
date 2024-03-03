package smartmag.models.ui;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import ingsw_proj_magazzino.db.generated.tables.records.ProdottiordiniRecord;
import smartmag.data.Ordine;
import smartmag.models.OrderModel;

public class ProductOrderTableModel extends AbstractTableModel
		implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private String[] columnNames;
	private Ordine order;
	// ArrayList mantiene l'ordine di inserimento
	private ArrayList<ProdottiordiniRecord> listaProdOrdRecord;

	public ProductOrderTableModel(OrderModel o) {
		this.order = o.getOrdine();
		listaProdOrdRecord = o.getListaProdottiOrdini();

		columnNames = new String[] { "ID ordine", "ID prodotto",
				"Nome prodotto", "Quantità" };

	}

	@Override
	public int getRowCount() {
		// il numero delle righe è dato dal numero di ProdottiordiniRecord
		// relativi a quell'ordine
		return listaProdOrdRecord.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ProdottiordiniRecord por1 = null;
		int i = 0;

		for (ProdottiordiniRecord por : listaProdOrdRecord) {
			if (rowIndex == i) {
				por1 = por;
				break;
			}
			i++;
		}
		switch (columnIndex) {
		case 0:
			return por1.getOrdine();
		case 1:
			return por1.getProd();
		case 2:
			return por1.getProd(); // TODO da sostituire con metodo che ritorna
									// il nome!
		case 3:
			return por1.getQta();
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		fireTableDataChanged(); // aggiorna la tabella se il modello ha subito
		// variazioni
	}

}
