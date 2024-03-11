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

	/**
	 * mappa per implementare unicitá delle istanze dei modelli di ogni prodotto
	 */
	private static TreeMap<Integer, ProductModel> instances;
	static {
		inizializza();
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

	}

	private ProductModel(Prodotto p) {
		this(p, fetchProdById(p.getId()));

	}

	private ProductModel(ProdottoRecord r) {
		this(prodottoFromRecord(r), r);

	}

	/**
	 * ottiene il modello di un prodotto e ne genera il record sfruttando il
	 * metodo create()
	 * 
	 * @param p
	 * @return
	 * @throws SQLIntegrityConstraintViolationException se il record é gia
	 *                                                  presntenel db
	 */
	public ProductModel createProdotto(Prodotto p)
			throws SQLIntegrityConstraintViolationException {
		ProductModel pm = getProductModelOf(p);
		if (pm.isSavedInDb()) {
			throw new IllegalArgumentException("prodotto già presente");
		}
		pm.create();
		return pm;
	}

	/**
	 * permette di creare il record del prodotto nel database, solo se non é giá
	 * presente
	 * 
	 * @throws SQLIntegrityConstraintViolationException se il record é giá
	 *                                                  presente nel db
	 */
	public void create() throws SQLIntegrityConstraintViolationException {

		if (isSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Prodotto #" + prodotto.getId() + " esiste già!");

		ProdottoRecord r = DSL.newRecord(PRODOTTO);
		r.setId(prodotto.getId());
		copyProdottoIntoRecord(prodotto, r);
		r.store(); // INSERT
		this.record = r;
		notifyChangeListeners(null);
	}

	/**
	 * restituisce una copia dell'oggetto Box rappresentato dal modello
	 * 
	 * @return
	 */
	public Prodotto getProdotto() {
		return prodotto.clone();
	}

	/**
	 * permette di impostare il prodotto del modello
	 * 
	 * @param p
	 */
	public void setProdotto(Prodotto p) {

		if (p != null && p.isValid())
			this.prodotto = p.clone();
		else
			throw new IllegalArgumentException("Prodotto non valido!");
	}

	/**
	 * controlla che ci sia un record del prodotto nel db
	 * 
	 * @return true se é presente, false se non lo é
	 */
	public boolean isSavedInDb() {
		refreshFromDb();
		return record != null;
	}

	/**
	 * permette di ottenre il record del modello
	 * 
	 */
	protected ProdottoRecord getRecord() {
		return record;
	}

	/**
	 * aggiorna il modello usando i dati presenti nel db
	 */
	private void refreshFromDb() {
		this.record = fetchProdById(prodotto.getId());
		if (this.record != null) {
			Prodotto p = prodottoFromRecord(this.record);
			this.prodotto.setNome(p.getNome());
			this.prodotto.setDescr(p.getDescr());
			this.prodotto.setPeso(p.getPeso());
			this.prodotto.setSoglia(p.getSoglia());
		}

		notifyChangeListeners(null);
	}

	/**
	 * elimina il record dal db senza peró cancellare il modello (il cui record
	 * viene settato a null)
	 */
	public void deleteProdotto() {
		if (isSavedInDb()) {
			record.delete();
			record = null;
			instances.remove(prodotto.getId());
			notifyChangeListeners(null);
		}
	}

	/**
	 * aggiorna il prodotto esistente sia nel db che nel modello usando i valori
	 * del parametro passato
	 * 
	 * @param p prodotto con i dati aggiornati
	 * @throws SQLIntegrityConstraintViolationException se il prodotto non
	 *                                                  esiste nel db
	 */
	public void updateProdotto(Prodotto p)
			throws SQLIntegrityConstraintViolationException {
		if (!isSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Prodotto #" + p.getId() + " non esiste!");

		if (p.getId() != this.prodotto.getId() && checkId(p.getId())) {
			throw new IllegalArgumentException("id gia usato");
		}
		record = (ProdottoRecord) fetchProdById(p.getId());
		copyProdottoIntoRecord(p, record);
		record.store(); // UPDATE con UpdatableRecord
		notifyChangeListeners(null);
	}

	// Metodi statici

	/**
	 * restituisce il modello del prodotto passato come parametro
	 * 
	 * @param p prodotto del quale si vuole ottenre il modello
	 * @return
	 */
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

	/**
	 * ricerca del record nel db a partire da id prodotto
	 * 
	 * @param id id del prodotto da cercare nel db
	 * @return
	 */
	static ProdottoRecord fetchProdById(int id) {
		ProdottoRecord r = (ProdottoRecord) DSL.select().from(PRODOTTO)
				.where(PRODOTTO.ID.eq(id)).fetchOne(); // SELECT
		return r;
	}

	/**
	 * restituisce un oggetto prodotto a partire dai dati presenti nel db
	 * 
	 * @param r record del prodotto da generare
	 * @return
	 */
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

	/**
	 * copia prodotto nel record del modello
	 * 
	 * @param p prodotto da copiare
	 * @param r record nelquale copiare i dati del prodotto
	 */
	private static void copyProdottoIntoRecord(Prodotto p, ProdottoRecord r) {
		r.setNome(p.getNome());
		r.setDescrizione(p.getDescr());
		r.setPeso(p.getPeso());
		r.setSoglia(p.getSoglia());
	}

	/**
	 * restituisce il prossimo id disponibile per un prodotto
	 * 
	 * @return
	 */
	public static int getNextAvailableId() {
		Integer max = DSL.select(max(PRODOTTO.ID)).from(PRODOTTO).fetchOne()
				.value1();
		if (max == null)
			return 0;
		return max + 1;
	}

	/**
	 * restituisce un clone filtrato della treemap istances in cui sono presenti
	 * solo i modelli con record non nullo
	 * 
	 * @return
	 */
	public static TreeMap<Integer, ProductModel> getAllProductModels() {
		@SuppressWarnings("unchecked")
		TreeMap<Integer, ProductModel> pm = (TreeMap<Integer, ProductModel>) instances
				.clone();
		return treeMapFilter(pm);
	}

	private static TreeMap<Integer, ProductModel> treeMapFilter(
			TreeMap<Integer, ProductModel> m) {
		TreeMap<Integer, ProductModel> filtrata = new TreeMap<Integer, ProductModel>();
		for (Map.Entry<Integer, ProductModel> entry : m.entrySet()) {
			if (entry.getValue().record != null) {
				filtrata.put(entry.getKey(), entry.getValue());
			}
		}
		return filtrata;
	}

	/**
	 * restituisceun prodotto a partire dal suo id
	 * 
	 * @param id identificativo del prodotto
	 * @return
	 */
	public static Prodotto getProdById(int id) {
		return instances.get(id).getProdotto();
	}

	/**
	 * crea la treemap instances inserendo il mofello di tutti i prodotti
	 * presenti nel db
	 */
	public static void inizializza() {
		instances = new TreeMap<Integer, ProductModel>();
		Map<Integer, Record> res = DSL.select().from(PRODOTTO)
				.fetchMap(PRODOTTO.ID);
		res.forEach((id, r) -> instances.put(id,
				new ProductModel((ProdottoRecord) r)));
	}

	/**
	 * controlla se un prodotto é presente nel db usando il suo id
	 * 
	 * @param id
	 * @return
	 */
	public static boolean checkId(int id) {

		inizializza();
		return instances.containsKey(id);
	}

	/**
	 * restituisce un prodotto dalla mappa instances usando il suo id
	 * 
	 * @param id
	 * @return
	 */
	public static Prodotto getProdottoFromId(int id) {
		return instances.get(id).getProdotto();
	}

}
