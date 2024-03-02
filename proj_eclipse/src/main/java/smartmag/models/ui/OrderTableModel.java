package smartmag.models.ui;

import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import smartmag.data.Ordine;
import smartmag.models.OrderModel;

public class OrderTableModel extends AbstractTableModel
		implements ChangeListener {

	private static final long serialVersionUID = 1L;

	private static TreeMap<Integer, OrderModel> ordini;
	private String[] columnNames;

	public OrderTableModel() {
		refreshDataFromModel();
		columnNames = new String[] { "Ordine", "Tipo", "Data emissione",
				"Data completamento", "Stato" };
	}

	private void refreshDataFromModel() {
		ordini = OrderModel.getAllOrderModels();
	}

	@Override
	public int getRowCount() {
		return ordini.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Restituisce il valore contenuto nella cella desiderata della tabella
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Ordine o = null;
		int i = 0;

		for (Integer idOrdine : ordini.keySet()) {
			if (rowIndex == i) {
				o = ordini.get(idOrdine).getOrdine();
				break;
			}
			i++;
		}
		switch (columnIndex) {
		case 0:
			return o.getId();
		case 1:
			return o.getTipo();
		case 2:
			return o.getDataEmissione();
		case 3:
			return o.getDataCompletamento();
		case 4:
			return o.getStato();
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
		refreshDataFromModel();
		fireTableDataChanged(); // aggiorna la tabella se il modello ha subito
								// variazioni
	}
}
