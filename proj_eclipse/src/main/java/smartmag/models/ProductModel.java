package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTO;
import static org.jooq.impl.DSL.max;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;

import ingsw_proj_magazzino.db.generated.tables.records.ProdottoRecord;
import smartmag.data.Prodotto;

public class ProductModel extends BaseModel {

	private static HashMap<Integer, ProductModel> instances = new HashMap<Integer, ProductModel>();

	private Prodotto prodotto;
	private ProdottoRecord record;

	public static ProductModel getProductModelOf(Prodotto p) {

		if (p != null && p.isValid()) {
			if (!instances.containsKey(p.getId())) {
				ProductModel pm = new ProductModel(p);
				instances.put(p.getId(), pm);
				return pm;
			} else {
				return instances.get(p.getId());
			}
		} else
			throw new IllegalArgumentException("Prodotto non valido!");
	}

	public boolean isSavedInDb() {
		refreshFromDb();
		return record != null;
	}

	private ProductModel(Prodotto p) {
		this.prodotto = p;
		this.record = fetchProdById(p.getId());
	}

	protected Prodotto getProdotto() {
		return prodotto;
	}

	protected void setProdotto(Prodotto p) {

		if (p != null && p.isValid())
			this.prodotto = p;
		else
			throw new IllegalArgumentException("Prodotto non valido!");
	}

	public ProdottoRecord getRecord() {
		return record;
	}

	public void createProdotto()
			throws SQLIntegrityConstraintViolationException {

		if (isSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Prodotto #" + prodotto.getId() + " esiste già!");

		ProdottoRecord r = DSL.newRecord(PRODOTTO);
		r.setId(prodotto.getId());
		copyProdottoIntoRecord(prodotto, r);
		r.store(); // INSERT
		this.record = r;

		// TODO: event
	}

	// Refresh (from DB) record and Prodotto obj
	private void refreshFromDb() {
		this.record = fetchProdById(prodotto.getId());
		if (this.record != null) {
			Prodotto p = prodottoFromRecord(this.record);
			this.prodotto.setNome(p.getNome());
			this.prodotto.setDescr(p.getDescr());
			this.prodotto.setPeso(p.getPeso());
			this.prodotto.setSoglia(p.getSoglia());
		}

		// TODO: event
	}

	public void deleteProdotto() {
		if (isSavedInDb()) {
			record.delete(); // DELETE con UpdatableRecord
		}
	}

	public void updateProdotto(Prodotto p)
			throws SQLIntegrityConstraintViolationException {
		if (!isSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Prodotto #" + p.getId() + " non esiste!");

		record = (ProdottoRecord) fetchProdById(p.getId());
		copyProdottoIntoRecord(p, record);
		record.store(); // UPDATE con UpdatableRecord
	}

	// Metodi statici

	private static ProdottoRecord fetchProdById(int id) {
		ProdottoRecord r = (ProdottoRecord) DSL.select().from(PRODOTTO)
				.where(PRODOTTO.ID.eq(id)).fetchOne(); // SELECT
		return r;
	}

	static Prodotto prodottoFromRecord(ProdottoRecord r) {
		if (r == null)
			return null;

		Integer id = r.getId();
		String nome = r.getNome();
		String descr = r.getDescrizione();
		var val = r.getPeso();
		Float peso = val == null ? 0f : (Float) val;
		Integer soglia = r.getSoglia();
		return new Prodotto(id, nome, descr, peso, soglia);
	}

	private static void copyProdottoIntoRecord(Prodotto p, ProdottoRecord r) {
		r.setNome(p.getNome());
		r.setDescrizione(p.getDescr());
		r.setPeso(p.getPeso());
		r.setSoglia(p.getSoglia());
	}

	public static int getNextAvailableId() {
		Integer max = DSL.select(max(PRODOTTO.ID)).from(PRODOTTO).fetchOne()
				.value1();
		if (max == null)
			return 0;
		return max + 1;
	}
}
