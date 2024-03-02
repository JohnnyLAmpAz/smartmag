package smartmag.models.ui;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import ingsw_proj_magazzino.db.generated.tables.records.ProdottiordiniRecord;
import smartmag.models.OrderModel;

public class ProductOrderTableModel extends AbstractTableModel
		implements ChangeListener {

	private static TreeMap<Integer, OrderModel> ordini;
	ArrayList<ProdottiordiniRecord> listaProdottiOrdini;
	private String[] columnNames;

	public ProductOrderTableModel(OrderModel o) {
		refreshDataFromModel();
		columnNames = new String[] { "ID ordine", "ID prodotto",
				"Nome prodotto", "Quantità" };

	}

	private void refreshDataFromModel() {
		ordini = OrderModel.getAllOrderModels();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		refreshDataFromModel();
		fireTableDataChanged(); // aggiorna la tabella se il modello ha subito
		// variazioni
	}

}
