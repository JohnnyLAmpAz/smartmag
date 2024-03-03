package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.BOX;
import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTO;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.Record;

import ingsw_proj_magazzino.db.generated.tables.records.BoxRecord;
import ingsw_proj_magazzino.db.generated.tables.records.ProdottoRecord;
import smartmag.data.Box;
import smartmag.data.Prodotto;

public class BoxModel extends BaseModel {

	/**
	 * mappa per implementare unicitá delle istanze dei modelli di ogni box
	 */
	private static TreeMap<String, BoxModel> instances;
	static {
		instances = new TreeMap<String, BoxModel>();
		Map<String, Record> res = DSL.select().from(BOX).fetchMap(BOX.ID);
		res.forEach((id, r) -> instances.put(id, new BoxModel((BoxRecord) r)));
	}

	private Box box;
	private BoxRecord record;

	private BoxModel(Box b, BoxRecord br) {
		if (b == null) {
			if (br == null)
				throw new IllegalArgumentException("BoxRecord nullo");
			b = boxFromRecord(br);
		} else if (br == null) {
			if (b == null || !b.isValid())
				throw new IllegalArgumentException("utente nullo");
			br = fetchBoxByIndirizzo(b.getIndirizzo());
		}

		if (instances.containsKey(b.getIndirizzo())) {
			throw new IllegalArgumentException("modello giá creato");
		}
		this.box = b;
		this.record = br;
		instances.put(b.getIndirizzo(), this);
		notifyChangeListeners(null);
	}

	private BoxModel(Box b) {
		this(b, fetchBoxByIndirizzo(b.getIndirizzo()));
	}

	private BoxModel(BoxRecord r) {
		this(boxFromRecord(r), r);
	}

	/**
	 * controlla che ci sia un record del box nel db
	 * 
	 * @return true se é presente, false se non lo é
	 */
	private boolean isSavedInDb() {
		refreshFromDb();
		return record != null;
	}

	/**
	 * restituisce l'oggetto Box rappresentato dal modello
	 * 
	 * @return
	 */
	protected Box getBox() {
		return box.clone();
	}

	/**
	 * restituisce il record
	 * 
	 * @return
	 */
	protected BoxRecord getRecord() {
		return record;
	}

	public static BoxModel createBox(Box b)
			throws SQLIntegrityConstraintViolationException {
		BoxModel bm = getBoxModel(b);
		if (bm == null)
			bm = new BoxModel(b);
		if (bm.isSavedInDb())
			throw new IllegalArgumentException("box già presente");
		bm.create();
		return bm;
	}

	/**
	 * permette di creare il record del box nel database, solo se non é giá
	 * presente
	 * 
	 * @throws SQLIntegrityConstraintViolationException se il record é giá
	 *                                                  presente nel db
	 */
	public void create() throws SQLIntegrityConstraintViolationException {
		if (isSavedInDb()) {
			throw new SQLIntegrityConstraintViolationException(
					"il box#" + box.getIndirizzo() + " esiste giá");
		}
		BoxRecord r = DSL.newRecord(BOX);
		r.setId(box.getIndirizzo());
		copyBoxIntoRecord(box, r);
		r.store();
		this.record = r;
		notifyChangeListeners(null);
	}

	/**
	 * aggiorna i dati del record e del box del modello usando quelli presenti
	 * nel db
	 */
	private void refreshFromDb() {
		this.record = fetchBoxByIndirizzo(box.getIndirizzo());
		if (this.record != null) {
			Box b = boxFromRecord(this.record);
			this.box.setIndirizzo(b.getIndirizzo());
			this.box.setQuantità(b.getQuantità());
			this.box.setProd(b.getProd());
			notifyChangeListeners(null);
		}
	}

	/**
	 * permette di cambiare il prodotto di un box solo se il prodotto giá
	 * presente nel box é effettivamente finito e il nuovo prodotto esiste nel
	 * db
	 * 
	 * @param p nuovo prodotto da assegnare al box
	 */
	private void cambiaProdotto(Prodotto p) {

		if (box != null && box.isValid() && box.getQuantità() == 0) {
			if (p != null && p.isValid()
					&& ProductModel.fetchProdById(p.getId()) != null) {
				box.setProd(p);
				record.setProdotto(p.getId());
				;
				record.update();
				notifyChangeListeners(null);
			} else
				throw new IllegalArgumentException("prodotto non valido");
		} else
			throw new IllegalArgumentException(
					"attenzione: il box deve essere valido e vuoto");
	}

	/**
	 * cancella il record dell'ordine
	 */
	protected void deleteRecord() {
		if (isSavedInDb()) {
			record.delete();
			record = null;
			instances.remove(box.getIndirizzo());
			notifyChangeListeners(null);
		}
	}

	/**
	 * incrementa la quantita di prodotto in un box
	 * 
	 * @param quantita numero di unita di prodotto aggiunte
	 */
	protected void rifornisci(int quantita) {

		int qi = this.box.getQuantità();
		this.box.setQuantità(qi + quantita);
		record.setQta(box.getQuantità());
		record.update();
		notifyChangeListeners(null);
	}

	/**
	 * diminuisce la quantita di prodotto in un box solo se la richiesta puo
	 * essere soddisfatta
	 * 
	 * @param qta numero di unita di prodotto da prelevare
	 */
	protected void preleva(int qta) {
		if (box.getQuantità() > qta) {
			box.setQuantità(this.box.getQuantità() - qta);
			record.setQta(box.getQuantità());
			record.update();
			notifyChangeListeners(null);
		} else {
			throw new IllegalArgumentException(
					"qta richiesta maggiore di quella disponibile");
		}

	}

	/**
	 * imposta la quantita di prodotto alla quantita passata come parametro
	 * 
	 * @param qta
	 */
	private void setQuantita(int qta) {
		this.box.setQuantità(qta);
		record.setQta(box.getQuantità());
		record.update();
		notifyChangeListeners(null);
	}

	// metodi statici

	/**
	 * restituisce il modello del box passato come parametro e lo salva
	 * all'interno dell'hashmap che contiene i modelli di tutti i box
	 * 
	 * @param b box del quale si vuole ottenere il modello
	 * @return modello del box
	 */
	public static BoxModel getBoxModel(Box b) {
		if (b != null && b.isValid()) {
			if (!instances.containsKey(b.getIndirizzo())) {
				if (ProductModel.fetchProdById(b.getProd().getId()) != null) {
					BoxModel bm = new BoxModel(b);
					return bm;
				} else {
					throw new IllegalArgumentException(
							"prodotto non esistente");
				}

			} else {
				return instances.get(b.getIndirizzo());
			}
		} else
			throw new IllegalArgumentException("box non valido");
	}

	/**
	 * recupera dal db il record relativo al box corrispondente all'indirizzo
	 * inserito
	 * 
	 * @param indirizzo identificativo del box
	 * @return
	 */
	private static BoxRecord fetchBoxByIndirizzo(String indirizzo) {
		BoxRecord r = (BoxRecord) DSL.select().from(BOX)
				.where(BOX.ID.eq(indirizzo)).fetchOne(); // SELECT
		return r;
	}

	/**
	 * retituisce un oggetto Box a partire dal suo record
	 * 
	 * @param r record del box
	 * @return oggetto Box
	 */
	private static Box boxFromRecord(BoxRecord r) {
		if (r == null)
			return null;

		String indirizzo = r.getId();
		int IDprodotto = r.getProdotto();
		int qta = r.getQta();
		ProdottoRecord pr = (ProdottoRecord) DSL.select().from(PRODOTTO)
				.where(PRODOTTO.ID.eq(IDprodotto));
		Prodotto p = ProductModel.prodottoFromRecord(pr);
		return new Box(indirizzo, qta, p);
	}

	/**
	 * imposta i valori del record prendendoli dal rispettivo oggetto Box
	 * 
	 * @param b Box da cui vengono presi i valori
	 * @param r record in cui vengono salvati i valori
	 */
	private static void copyBoxIntoRecord(Box b, BoxRecord r) {
		if (DSL.select().from(PRODOTTO)
				.where(PRODOTTO.ID.eq(b.getProd().getId())) != null) {
			r.setProdotto(b.getProd().getId());
			r.setQta(b.getQuantità());
		} else
			throw new IllegalArgumentException(
					"prodotto non presente nel database");

	}

	@SuppressWarnings("unchecked")
	public static TreeMap<String, BoxModel> getAllBoxModels() {

		TreeMap<String, BoxModel> bm = (TreeMap<String, BoxModel>) instances
				.clone();
		return treeMapFilter(bm);
	}

	public static TreeMap<String, BoxModel> treeMapFilter(
			TreeMap<String, BoxModel> m) {
		TreeMap<String, BoxModel> filtrata = new TreeMap<String, BoxModel>();
		for (Map.Entry<String, BoxModel> entry : m.entrySet()) {
			if (entry.getValue().record != null) {
				filtrata.put(entry.getKey(), entry.getValue());
			}
		}
		return filtrata;
	}

}