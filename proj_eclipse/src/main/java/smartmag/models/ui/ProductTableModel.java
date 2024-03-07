package smartmag.models.ui;

import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import smartmag.data.Prodotto;
import smartmag.models.ProductModel;

public class ProductTableModel extends AbstractTableModel
		implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private String[] columnNames;
	private TreeMap<Integer, ProductModel> prodotti;

	public ProductTableModel() {
		refreshFromModel();
		this.columnNames = new String[] { "Id", "Nome", "Descrizione", "Peso",
				"Soglia" };
		ProductModel.addChangeListener(this);
	}

	private void refreshFromModel() {
		prodotti = ProductModel.getAllProductModels();
	}

	@Override
	public int getRowCount() {
		return prodotti.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Prodotto p = getProductModelAt(rowIndex).getProdotto();
		return switch (columnIndex) {
		case 0:
			yield p.getId();
		case 1:
			yield p.getNome();
		case 2:
			yield p.getDescr();
		case 3:
			yield p.getPeso();
		case 4:
			yield p.getSoglia();
		default:
			throw new IndexOutOfBoundsException("campo inesistente");
		};
	}

	/**
	 * Restituisce l'istanza del modello Prodotto di indice (riga) specificato.
	 * Utile per ricavare il prodotto selezionato).
	 * 
	 * @param index indice posizione prodotto
	 * @return modello prodotto
	 */
	public ProductModel getProductModelAt(int rowIndex) {
		ProductModel pm = null;
		int i = 0;

		for (Integer id : prodotti.keySet()) {
			if (i == rowIndex) {
				pm = prodotti.get(id);
			}
			i++;
		}
		if (pm == null) {
			throw new IndexOutOfBoundsException("riga inesistente");
		}

		return pm;
	}

	public String getColumnName(int column) {
		return columnNames[column];

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		refreshFromModel();
		fireTableDataChanged();
	}

}
