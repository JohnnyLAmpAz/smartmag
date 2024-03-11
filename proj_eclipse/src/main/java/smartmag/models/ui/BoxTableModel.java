package smartmag.models.ui;

import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import smartmag.data.Box;
import smartmag.models.BoxModel;

public class BoxTableModel extends AbstractTableModel
		implements ChangeListener {

	private String[] columnNames;
	private TreeMap<String, BoxModel> box;

	public BoxTableModel() {
		refreshFromModel();
		this.columnNames = new String[] { "Id", "IDProdotto", "Quantitá" };
		BoxModel.addChangeListener(this);
	}

	public void refreshFromModel() {
		box = BoxModel.getAllBoxModels();
		fireTableDataChanged();
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
		return switch (columnIndex) {
		case 0:
			yield b.getIndirizzo();
		case 1:
			yield b.getProd().getId();
		case 2:
			yield b.getQuantità();

		default:
			throw new IndexOutOfBoundsException("campo inesistente");
		};
	}

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

}
