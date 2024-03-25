package smartmag.ui.table_models;

import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import smartmag.data.Prodotto;
import smartmag.models.OrderModel;

public class ProductOrderTableModel extends AbstractTableModel
		implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private static final String[] COLUMN_NAMES = new String[] { "ID prodotto",
			"Nome prodotto", "Quantità" };

	/**
	 * Modello dell'ordine. Con questo si ha accesso all'ordine e di coseguenza
	 * alla "lista della spesa".
	 */
	private OrderModel orderModel;

	/**
	 * Mappa della "lista della spesa" (Prodotto => Quantità) relativa
	 * all'ordine del modello specificato. Si tratta di un clone di quella
	 * effettiva del modello.
	 */
	private HashMap<Prodotto, Integer> listaSpesa;

	/**
	 * Costruisce il modello di una tabella relativa alla "lista della spesa"
	 * dell'ordine di cui si specifica il modello.
	 * 
	 * @param o Modello dell'ordine d'interesse
	 */
	public ProductOrderTableModel(OrderModel o) {
		if (o == null)
			throw new IllegalArgumentException(
					"La tabella deve essere associata ad un modello di un ordine!");
		this.orderModel = o;
		refreshData();
	}

	public ProductOrderTableModel(HashMap<Prodotto, Integer> prodotti) {
		listaSpesa = prodotti;
	}

	/**
	 * Aggiorna la lista della spesa attingendo dal modello
	 */
	private void refreshData() {
		this.listaSpesa = this.orderModel.getOrdine().getProdotti();
	}

	@Override
	public int getRowCount() {
		return listaSpesa.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Prodotto prod = null;
		Integer qta = -1;

		// Seleziono il prodotto (in base a indice riga) iterando la mappa
		int i = 0;
		for (Map.Entry<Prodotto, Integer> entry : listaSpesa.entrySet()) {
			if (i != rowIndex) {
				i++;
				continue;
			}

			prod = entry.getKey();
			qta = entry.getValue();
			break;
		}

		if (prod == null)
			throw new IndexOutOfBoundsException();

		// Restituisco il campo richiesto
		// | ID prodotto | Nome prodotto | Quantità |
		switch (columnIndex) {
			case 0:
				return prod.getId();
			case 1:
				return prod.getNome();
			case 2:
				return qta;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		// Aggiorna listaSpesa
		refreshData();

		// aggiorna la tabella se il modello ha subito variazioni
		fireTableDataChanged();
	}
}
