package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.MOVIMENTAZIONE;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.Record;
import org.jooq.Result;

import ingsw_proj_magazzino.db.generated.tables.records.MovimentazioneRecord;
import smartmag.data.Box;
import smartmag.data.MovimId;
import smartmag.data.Movimentazione;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoMovim;
import smartmag.data.Utente;
import smartmag.utils.PrintUtils;

/**
 * Modello delle movimentazioni
 */
public class MovimenModel extends BaseModel {

	/**
	 * Mappa delle istanze modelli per implementare il Singleton Pattern
	 */
	private static final TreeMap<MovimId, MovimenModel> instances;
	static {
		instances = new TreeMap<MovimId, MovimenModel>();
		loadModelsFromDb();
	}

	/**
	 * Movimentazione relativa al modello
	 */
	private Movimentazione movim;

	/**
	 * Record della movimentazione
	 */
	private MovimentazioneRecord record;

	/**
	 * Costruisce un'istanza del modello sulla base di un record non nullo
	 * 
	 * @param record
	 * 
	 * @throws IllegalArgumentException se record è null
	 */
	private MovimenModel(MovimentazioneRecord record) {
		if (record == null)
			throw new IllegalArgumentException();
		this.record = record;
		this.movim = movimFromRecord(record);
		instances.put(new MovimId(record.getOrdine(), record.getBox()), this);
		notifyChangeListeners(null);
	}

	/**
	 * Costruisce il modello di una nuova movimentazione con stato
	 * NON_ASSEGNATA.
	 * 
	 * @param m Movimentazione
	 * @throws IllegalArgumentException se la movimentazione è già stata creata
	 */
	private MovimenModel(Movimentazione m) {
		if (instances.containsKey(m.getMovimId()))
			throw new IllegalArgumentException("Movimentazione già creata!");
		if (m == null || !m.isValid()
				|| m.getStato() != StatoMovim.NON_ASSEGNATA
				|| m.getMagazziniere() != null)
			throw new IllegalArgumentException("Movimentazione non valida!");

		this.movim = m;

		// Creo nuovo record
		MovimentazioneRecord mr = DSL.newRecord(MOVIMENTAZIONE);
		mr.setOrdine(m.getOrdine().getId());
		mr.setBox(m.getBox().getIndirizzo());
		mr.setProd(m.getProdotto().getId());
		mr.setQta(m.getQuantità());
		mr.setStato(m.getStato().name());
		Utente u = m.getMagazziniere();
		mr.setMagazziniere(u == null ? null : u.getMatricola());

		// Salvo
		mr.store();
		this.record = mr;
		instances.put(new MovimId(record.getOrdine(), record.getBox()), this);
		notifyChangeListeners(null);
	}

	/**
	 * Verifica se esiste un record della movimentazione gestita
	 * 
	 * @return true se esiste un record, altrimenti false
	 */
	private boolean isSavedInDb() {
		refreshFromDb();
		return this.record != null;
	}

	/**
	 * Aggiorna il contenuto del modello dal DB
	 */
	private void refreshFromDb() {
		refreshFromRecord(fetchMovimRecord(movim.getMovimId()));
	}

	/**
	 * Aggiorna il contenuto del modello tramite il record passato
	 */
	private void refreshFromRecord(MovimentazioneRecord mr) {
		this.record = mr;
		if (this.record != null) {
			this.movim = movimFromRecord(this.record);
		}
		notifyChangeListeners(null);
	}

	/**
	 * Restituisce la PK (ordineId, boxAddr) della movimentazione gestita
	 * 
	 * @return PK
	 */
	public MovimId getKey() {
		return movimIdFromRecord(record);
	}

	/**
	 * Restituisce una copia della movimentazione gestita
	 * 
	 * @return clone di movim
	 */
	public Movimentazione getMovim() {
		return movim.clone();
	}

	/**
	 * Recupera dal db il record di una movimentazione identificata dalla chiave
	 * passata.
	 * 
	 * @param pk ID movimentazione cercata
	 * @return record se trovato, altrimenti null
	 */
	private static MovimentazioneRecord fetchMovimRecord(MovimId pk) {
		return (MovimentazioneRecord) DSL.select().from(MOVIMENTAZIONE)
				.where(MOVIMENTAZIONE.ORDINE.eq(pk.getOrdineId()))
				.and(MOVIMENTAZIONE.BOX.eq(pk.getBoxAddr())).fetchOne();
	}

	/**
	 * Dato un record di Movimentazione, estrapola un oggetto Movimentazione da
	 * esso.
	 * 
	 * @param r record
	 * @return istanza di Movimentazione
	 */
	private static Movimentazione movimFromRecord(MovimentazioneRecord r) {
		StatoMovim stato = StatoMovim.valueOf(r.getStato());
		int qta = r.getQta();
		Ordine ordine = OrderModel.getOrderModelById(r.getOrdine()).getOrdine();
		Prodotto prodotto = ProductModel.getProductModelById(r.getProd())
				.getProdotto();
		Box box = BoxModel.getBoxModelByAddr(r.getBox()).getBox();
		UtenteModel um = r.getMagazziniere() == null ? null
				: UtenteModel.getUtenteModelOf(r.getMagazziniere());
		Utente magazziniere = um == null ? null : um.getUtente();

		return new Movimentazione(stato, qta, ordine, prodotto, box,
				magazziniere);
	}

	/**
	 * Dato un record di movimentazione, restituisce la sua PK
	 * 
	 * @param mr record movimentazione
	 * @return PK (ordineId, boxAddr)
	 */
	private static MovimId movimIdFromRecord(MovimentazioneRecord mr) {
		return new MovimId(mr.getOrdine(), mr.getBox());
	}

	/**
	 * Carica dal Db tutte le movimentazioni istanziandone i modelli e
	 * salvandoli nella mappa dei singleton.
	 */
	public static void loadModelsFromDb() {
		Result<Record> res = DSL.select().from(MOVIMENTAZIONE).fetch();
		for (Record record : res) {
			MovimentazioneRecord mr = (MovimentazioneRecord) record;
			MovimId pk = movimIdFromRecord(mr);
			if (instances.containsKey(pk))
				instances.get(pk).refreshFromRecord(mr);
			else {
				instances.put(pk, new MovimenModel(mr));
			}
		}
		notifyChangeListeners(null);
	}

	/**
	 * Restituisce una mappa di tutti i modelli delle movimentazioni gestite.
	 * 
	 * @return mappa indicizzata dalla PK (ordine, box) delle movimentazioni
	 */
	public static TreeMap<MovimId, MovimenModel> getAllMovimenModels() {
		return new TreeMap<>(instances);
	}

	/**
	 * Se già creato, restituisce il modello della movimentazione con ID
	 * specificato, altrimenti null.
	 * 
	 * @param id Tupla (ordineId, boxAddr) che identifica una movimentazione
	 * @return Modello della movimentazione cercata, oppure null se non presente
	 */
	public static MovimenModel getModelOf(MovimId id) {
		if (instances.containsKey(id))
			return instances.get(id);
		return null;
	}

	/**
	 * Restituisce true se è stata generata almeno una movimentazione per il
	 * prodotto di ID specificato; false se altrimenti.
	 * 
	 * @param orderId ID ordine
	 * @return true se ne esiste anche solo una
	 */
	protected static boolean generatedMovimsOf(int orderId) {
		for (Map.Entry<MovimId, MovimenModel> entry : instances.entrySet()) {
			MovimId pk = entry.getKey();
			if (pk.getOrdineId() == orderId)
				return true;
		}
		return false;
	}

	/**
	 * Dato un ordine, genera le movimentazioni relative ad esso, checkando che
	 * le disponibilità.
	 * 
	 * @param orderId ID Ordine di cui generare le movimentazioni
	 * @return Mappa delle movimentazioni generate
	 */
	protected static TreeMap<MovimId, MovimenModel> generateOrderMovimsOf(
			int orderId) {
		if (generatedMovimsOf(orderId))
			throw new IllegalArgumentException("Movimentazioni gia' generate");

		TreeMap<MovimId, MovimenModel> gen = new TreeMap<MovimId, MovimenModel>();
		Ordine o = OrderModel.getOrderModelById(orderId).getOrdine();

		// Scorro la lista della spesa dell'ordine
		for (Map.Entry<Prodotto, Integer> entry : o.getProdotti().entrySet()) {
			Prodotto p = entry.getKey();
			Integer qta = entry.getValue();

			// Movimentazione da generare per questo prodotto
			MovimenModel mm = null;

			// Trovo lista dei box contenenti il prodotto dell'iterazione
			ArrayList<BoxModel> boxes = BoxModel.findBoxesWithProd(p);
			for (BoxModel bm : boxes) {

				// TODO check disponibilita' tenendo in considerazione le
				// movimentazioni
				// già generate

				// Prendo il primo box con qta sufficiente
				if (bm.getBox().getQuantità() >= qta) {
					Movimentazione m = new Movimentazione(
							StatoMovim.NON_ASSEGNATA, qta, o, p, bm.getBox());
					mm = new MovimenModel(m);
					gen.put(mm.getKey(), mm);
				}

				// TODO qta spans over multi movim
			}

			// Se non ho trovato box con quantità desiderata
			if (mm == null) {

				// TODO gestire in modo adeguato la disponibilità
				throw new IllegalArgumentException(
						"La quantità richiesta non è disponibile!");
			}
		}

		// Restituisco la mappa delle movimentazioni generate per l'ordine
		return gen;
	}

	/**
	 * Restituisce una mappa dei modelli delle movimentazioni relative ad un
	 * ordine con ID specificato.
	 * 
	 * @param orderId
	 * @return
	 */
	public static TreeMap<MovimId, MovimenModel> getMovimsModelsOf(
			int orderId) {
		TreeMap<MovimId, MovimenModel> res = new TreeMap<MovimId, MovimenModel>();
		for (Map.Entry<MovimId, MovimenModel> entry : instances.entrySet()) {
			MovimId pk = entry.getKey();
			MovimenModel mm = entry.getValue();
			if (pk.getOrdineId() == orderId)
				res.put(pk, mm);
		}
		return res;
	}

	// TODO to test
	public static void main(String[] args) {

		System.out.println("\nPRODOTTI:");
		TreeMap<Integer, ProductModel> map = ProductModel.getAllProductModels();
		map.forEach((id, m) -> System.out.println(m.getProdotto().toString()));
		System.out.println("\nORDINI:");
		TreeMap<Integer, OrderModel> omm = OrderModel.getAllOrderModels();
		omm.forEach((id, om) -> System.out.println(om.getOrdine().toString()));

		if (!MovimenModel.generatedMovimsOf(1)) {
			TreeMap<MovimId, MovimenModel> orderMovimsOf = MovimenModel
					.generateOrderMovimsOf(1);
			PrintUtils.printMovimsMap(orderMovimsOf);
		} else {
			PrintUtils.printMovimsMap(MovimenModel.getMovimsModelsOf(1));
		}
	}
}