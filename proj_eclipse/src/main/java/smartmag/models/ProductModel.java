package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTO;
import static org.jooq.impl.DSL.max;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.Record;

import ingsw_proj_magazzino.db.generated.tables.records.ProdottoRecord;
import smartmag.data.Prodotto;

public class ProductModel extends BaseModel {

	private static TreeMap<Integer, ProductModel> instances;
	static {
		instances = new TreeMap<Integer, ProductModel>();
		Map<Integer, Record> res = DSL.select().from(PRODOTTO)
				.fetchMap(PRODOTTO.ID);
		res.forEach((id, r) -> instances.put(id,
				new ProductModel((ProdottoRecord) r)));
		// notify?)
	}

	private Prodotto prodotto;
	private ProdottoRecord record;

	private ProductModel(Prodotto p, ProdottoRecord r) {
		if (p == null) {
			if (r == null) {
				throw new IllegalArgumentException("ProductRecord nullo");
			}
			p = prodottoFromRecord(r);
		}
		if (r == null) {
			if (p == null || !p.isValid()) {
				throw new IllegalArgumentException("prodotto nullo");
			}
			r = fetchProdById(p.getId());
		}

		if (instances.containsKey(p.getId())) {
			throw new IllegalArgumentException("modello giá creato");
		}
		this.prodotto = p;
		this.record = r;
		instances.put(p.getId(), this);
		// notify?
	}

	private ProductModel(Prodotto p) {
		this(p, fetchProdById(p.getId()));
		// notifica?
	}

	private ProductModel(ProdottoRecord r) {
		this(prodottoFromRecord(r), r);
		// notifica?)
	}

	public ProductModel createProdotto(Prodotto p)
			throws SQLIntegrityConstraintViolationException {
		ProductModel pm = getProductModelOf(p);
		if (pm == null) {
			pm = new ProductModel(p);
		}
		if (pm.isSavedInDb()) {
			throw new IllegalArgumentException("prodotto già presente");
		}
		pm.create();
		return pm;
	}

	public void create() throws SQLIntegrityConstraintViolationException {

		if (isSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Prodotto #" + prodotto.getId() + " esiste già!");

		ProdottoRecord r = DSL.newRecord(PRODOTTO);
		r.setId(prodotto.getId());
		copyProdottoIntoRecord(prodotto, r);
		r.store(); // INSERT
		this.record = r;
		// notify
	}

	public Prodotto getProdotto() {
		return prodotto;
	}

	protected void setProdotto(Prodotto p) {

		if (p != null && p.isValid())
			this.prodotto = p;
		else
			throw new IllegalArgumentException("Prodotto non valido!");
	}

	public boolean isSavedInDb() {
		refreshFromDb();
		return record != null;
	}

	public ProdottoRecord getRecord() {
		return record;
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
			record.delete();// DELETE con UpdatableRecord
			record = null;
			// notify

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

	public static ProductModel getProductModelOf(Prodotto p) {

		if (p != null && p.isValid()) {
			if (!instances.containsKey(p.getId())) {
				ProductModel pm = new ProductModel(p);
				return pm;
			} else {
				return instances.get(p.getId());
			}
		} else
			throw new IllegalArgumentException("Prodotto non valido!");
	}

	static ProdottoRecord fetchProdById(int id) {
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

	public static TreeMap<Integer, ProductModel> getAllProductModels() {
		TreeMap<Integer, ProductModel> pm = (TreeMap<Integer, ProductModel>) instances
				.clone();
		return treeMapFilter(pm);
	}

	public static TreeMap<Integer, ProductModel> treeMapFilter(
			TreeMap<Integer, ProductModel> m) {
		TreeMap<Integer, ProductModel> filtrata = new TreeMap<Integer, ProductModel>();
		for (Map.Entry<Integer, ProductModel> entry : m.entrySet()) {
			if (entry.getValue().record != null) {
				filtrata.put(entry.getKey(), entry.getValue());
			}
		}
		return filtrata;
	}
}
