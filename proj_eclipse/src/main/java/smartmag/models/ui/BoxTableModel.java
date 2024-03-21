package smartmag.models.ui;

import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import smartmag.data.Box;
import smartmag.data.Prodotto;
import smartmag.models.BoxModel;

public class BoxTableModel extends AbstractTableModel
		implements ChangeListener {

	private String[] columnNames;
	private TreeMap<String, BoxModel> box;

	/**
	 * costruttore
	 */
	public BoxTableModel() {
		refreshFromModel();
		this.columnNames = new String[] { "Indirizzo", "Prodotto", "Quantitá" };
		BoxModel.addChangeListener(this);
	}

	/**
	 * aggiorna la lista di box
	 */
	private void refreshFromModel() {
		box = BoxModel.getAllBoxModels();
	}

	@Override
	public int getRowCount() {
		return box.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Box b = getBoxModelAt(rowIndex).getBox();
		Prodotto p = b.getProd();
		return switch (columnIndex) {
			case 0:
				yield b.getIndirizzo();
			case 1:
				yield "#%d_%s".formatted(p.getId(), p.getNome());
			case 2:
				yield b.getQuantità();

			default:
				throw new IndexOutOfBoundsException("campo inesistente");
		};
	}

	/**
	 * Restituisce l'istanza del modello Box di indice (riga) specificato. Utile
	 * per ricavare il box selezionato.
	 * 
	 * @param index indice posizione box
	 * @return modello box
	 */
	public BoxModel getBoxModelAt(int rowIndex) {
		BoxModel bm = null;
		int i = 0;

		for (String id : box.keySet()) {
			if (i == rowIndex) {
				bm = box.get(id);
			}
			i++;
		}
		if (bm == null) {
			throw new IndexOutOfBoundsException("riga inesistente");
		}

		return bm;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		refreshFromModel();
		fireTableDataChanged();
	}

	public String getColumnName(int column) {
		return columnNames[column];

	}

}
