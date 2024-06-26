package smartmag.ui.table_models;

import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import smartmag.data.MovimId;
import smartmag.data.Movimentazione;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.Utente;
import smartmag.models.MovimenModel;

/**
 * TableModel per JTable che mostra le movimentazioni
 */
public class MovimenTableModel extends AbstractTableModel
		implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private static final String[] COLUMN_NAMES = new String[] { "ID Ordine",
			"Origine → Destinazione", "Prodotto", "Quantità", "Stato",
			"Magazziniere" };

	private TreeMap<MovimId, MovimenModel> mmm;

	public MovimenTableModel() {
		MovimenModel.addChangeListener(this);
		refreshData();
	}

	private void refreshData() {
		mmm = MovimenModel.getAllMovimenModels();
		fireTableDataChanged();
	}

	public MovimenModel getMovimenModelAt(int rowIndex) {
		// Riga
		MovimenModel mm = null;
		int i = 0;
		for (MovimId pk : mmm.keySet()) {
			if (rowIndex == i) {
				mm = mmm.get(pk);
				break;
			}
			i++;
		}
		if (mm == null)
			throw new IndexOutOfBoundsException("Riga non presente!");
		return mm;
	}

	@Override
	public int getRowCount() {
		return mmm.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		// Riga
		MovimenModel mm = getMovimenModelAt(rowIndex);

		// Colonna
		// { "ID Ordine", "Origine → Destinazione", "Prodotto", "Quantità",
		// "Stato", "Magazziniere" }
		Movimentazione m = mm.getMovim();
		Ordine o = m.getOrdine();
		Prodotto p = m.getProdotto();
		return switch (columnIndex) {
			case 0:
				// Ordine
				yield o.getId();
			case 1:
				// Orig -> Dest
				String orig = !o.isOutgoing() ? Movimentazione.ZCS
						: m.getBox().getIndirizzo();
				String dest = o.isOutgoing() ? Movimentazione.ZCS
						: m.getBox().getIndirizzo();
				yield orig + " → " + dest;
			case 2:
				// Prodotto
				yield "#%d_%s".formatted(p.getId(), p.getNome());
			case 3:
				// Quantità
				yield m.getQuantità();
			case 4:
				// Stato
				yield m.getStato();
			case 5:
				// Magazziniere
				Utente worker = m.getMagazziniere();
				yield worker == null ? null : worker.getMatricola();
			default:
				throw new IndexOutOfBoundsException(
						"Indice di campo non valido");
		};
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		refreshData();
	}

	public static String[] getColumnNames() {
		return COLUMN_NAMES;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

}
