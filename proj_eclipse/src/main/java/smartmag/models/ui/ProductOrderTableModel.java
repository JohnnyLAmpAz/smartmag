package smartmag.models.ui;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import ingsw_proj_magazzino.db.generated.tables.records.ProdottiordiniRecord;
import smartmag.data.Ordine;
import smartmag.models.OrderModel;
import smartmag.models.ProductModel;

public class ProductOrderTableModel extends AbstractTableModel
		implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private String[] columnNames;
	private Ordine order;
	// ArrayList mantiene l'ordine di inserimento
	private ArrayList<ProdottiordiniRecord> listaProdOrdRecord;
	private static TreeMap<Integer, ProductModel> prodotti;

	public ProductOrderTableModel(OrderModel o) {
		refreshProductList();
		this.order = o.getOrdine();
		listaProdOrdRecord = o.getListaProdottiOrdini();

		columnNames = new String[] { "ID ordine", "ID prodotto",
				"Nome prodotto", "Quantità" };

	}

	private void refreshProductList() {
		prodotti = ProductModel.getAllProductModels();
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
		ProductModel pm = null;
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
			for (Map.Entry<Integer, ProductModel> entry : prodotti.entrySet()) {
				if (entry.getKey().equals(por1.getProd())) {
					pm = entry.getValue();
					break;
				}
			}
			return pm.getProdotto().getNome();
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
