package smartmag.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import smartmag.data.Prodotto;
import smartmag.models.ProductModel;

public class ProdTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "ID", "Nome", "Descrizione", "Peso",
			"Soglia" };
	private List<Prodotto> prodotti;

	private ProductModel model;

	public ProdTableModel(ProductModel model) {
		this.model = model;
		this.prodotti = ProductModel.getAllProducts();
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
		Prodotto p = prodotti.get(rowIndex);
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
				throw new IllegalArgumentException(
						"Unexpected columnIndex value: " + columnIndex);
		};
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public void refreshData() {
		this.prodotti = ProductModel.getAllProducts();
		fireTableDataChanged();
	}

	// TODO: maybe better ways?
	public void addProd(Prodotto p) {
		this.prodotti.add(p);
		this.fireTableDataChanged();
	}

	public Prodotto getProdAt(int rowIndex) {
		return prodotti.get(rowIndex);
	}

	public void deleteProdotto(int rowIndex) {
		deleteProdotto(getProdAt(rowIndex));
	}

	public void deleteProdotto(Prodotto p) {

		prodotti.remove(p);
		ProductModel pm = ProductModel.getProductModelOf(p);
		if (pm != null)
			pm.deleteProdotto();
		fireTableDataChanged();
	}
}
