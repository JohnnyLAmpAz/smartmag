package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTIORDINI;

import java.util.HashMap;

import ingsw_proj_magazzino.db.generated.tables.records.ProdottiordiniRecord;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;

public class ProductOrderModel extends BaseModel {

	private Ordine ordine;
	private Prodotto prodotto;
	private ProdottiordiniRecord productOrderRecord;
	private static HashMap<String, ProductOrderModel> instances = new HashMap<String, ProductOrderModel>();

	// Costruttore
	private ProductOrderModel(Ordine o, Prodotto p) {
		this.ordine = o;
		this.prodotto = p;
		this.productOrderRecord = fetchProductOrderByIds(o, p);
	}

	private void refresh() {
		this.productOrderRecord = fetchProductOrderByIds(ordine, prodotto);

		if (this.productOrderRecord != null) {
			int orderId = ordineIdFromRecord(this.productOrderRecord);
			int prodId = prodIdFromRecord(this.productOrderRecord);
			int qta = qtaFromRecord(this.productOrderRecord);
			this.productOrderRecord.setOrdine(orderId);
			this.productOrderRecord.setProd(prodId);
			this.productOrderRecord.setQta(qta);
		}

		// TODO: event
	}

	// Static methods ======

	// nella tab PRODOTTIORDINI, la colonna ORDINE e PRODOTTO contengono l'Id
	private static ProdottiordiniRecord fetchProductOrderByIds(Ordine o,
			Prodotto p) {
		ProdottiordiniRecord r = (ProdottiordiniRecord) DSL.select()
				.from(PRODOTTIORDINI)
				.where(PRODOTTIORDINI.ORDINE.eq(o.getId())
						.and(PRODOTTIORDINI.PROD.eq(p.getId())))
				.fetchOne(); // SELECT
		return r;
	}

	private static void copyProdottoOrdineIntoRecord(int q, Prodotto p,
			Ordine o, ProdottiordiniRecord r) {
		r.setOrdine(o.getId());
		r.setProd(p.getId());
		r.setQta(q);
	}

	private static int ordineIdFromRecord(ProdottiordiniRecord r) {
		if (r == null)
			return -1;
		return r.getOrdine();
	}

	private static int prodIdFromRecord(ProdottiordiniRecord r) {
		if (r == null)
			return -1;
		return r.getProd();
	}

	private static int qtaFromRecord(ProdottiordiniRecord r) {
		if (r == null)
			return -1;
		return r.getQta();
	}

	public static ProductOrderModel getProductOrderModelOf(Ordine o,
			Prodotto p) {

		if (o != null && o.isValid() && p != null && p.isValid()) {
			String chiave;
			chiave = p.getId() + "-" + o.getId(); // poichè non si può fare un
													// hashmap con 2 chiavi
			if (!instances.containsKey(chiave)) {
				ProductOrderModel pom = new ProductOrderModel(o, p);
				instances.put(chiave, pom);
				return pom;
			} else {
				return instances.get(chiave);
			}
		} else
			throw new IllegalArgumentException("Non valido!");
	}

}
