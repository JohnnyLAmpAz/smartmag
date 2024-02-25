package smartmag.models;

import java.util.ArrayList;
import java.util.HashMap;

import smartmag.data.Prodotto;

public class ProductListModel extends BaseModel {

	private HashMap<Prodotto, ProductModel> lista;

	public ProductListModel() {
		this.lista = new HashMap<Prodotto, ProductModel>();
	}

	/**
	 * Ritorna una lista di <i>cloni</i> dei prodotti gestiti.
	 */
	public ArrayList<Prodotto> getProdotti() {
		ArrayList<Prodotto> ls = new ArrayList<Prodotto>();
		lista.forEach((p, pm) -> ls.add(p.clone()));
		return ls;
	}

	public ProductModel getProductModel(Prodotto p) {

		if (lista.containsKey(p)) {
			return lista.get(p);
		}
		throw new IllegalArgumentException(
				"product model non presente in lista");
	}

	public void addProdotto(Prodotto p) {
		if (!lista.containsKey(p)) {
			ProductModel pm = ProductModel.getProductModelOf(p);
			lista.put(p, pm);
		}
	}

	public void removeProdotto(Prodotto p) {
		if (lista.containsKey(p)) {
			lista.remove(p);
		}
	}

}
