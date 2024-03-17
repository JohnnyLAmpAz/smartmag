package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.MOVIMENTAZIONE;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
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
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;
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
	 * Restituisce una mappa di tutti i modelli delle movimentazioni gestite.
	 * 
	 * @return mappa (copia di instances) indicizzata dalla PK (ordine, box)
	 *         delle movimentazioni
	 */
	public static TreeMap<MovimId, MovimenModel> getAllMovimenModels() {
		return new TreeMap<>(instances);
	}

	/**
	 * @return Una mappa di tutti i modelli delle movimentazioni non ancora
	 *         assegnate. Corrisponde alla lista di movimentazioni da mostrare
	 *         ad un magazziniere.
	 */
	public static TreeMap<MovimId, MovimenModel> getAvailableMovimenModels() {
		TreeMap<MovimId, MovimenModel> res = new TreeMap<MovimId, MovimenModel>();
		for (Map.Entry<MovimId, MovimenModel> entry : instances.entrySet()) {
			MovimId pk = entry.getKey();
			MovimenModel mm = entry.getValue();

			// Seleziono solo le movimentazioni non ancora assegnate
			if (mm.movim.getStato() == StatoMovim.NON_ASSEGNATA)
				res.put(pk, mm);
		}
		return res;
	}

	/**
	 * Se già creato, restituisce il modello della movimentazione con ID
	 * specificato, altrimenti null.
	 * 
	 * @param key Tupla (ordineId, boxAddr) che identifica una movimentazione
	 * @return Modello della movimentazione cercata, oppure null se non presente
	 */
	public static MovimenModel getModelByKey(MovimId key) {
		if (instances.containsKey(key))
			return instances.get(key);
		return null;
	}

	/**
	 * Restituisce una mappa dei modelli delle movimentazioni relative ad un
	 * ordine con ID specificato.
	 * 
	 * @param orderId
	 * @return
	 */
	public static TreeMap<MovimId, MovimenModel> getMovimsModelsOfOrder(
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

	// TODO: could have zona C/S report

	/**
	 * Restituisce le movimentazioni che interessano il box specificato, di
	 * ordini in entrata o uscita.
	 * 
	 * @param boxAddr Indirizzo del Box d'interesse
	 * @return Mappa dei modelli movimentazioni da/verso il box specificato
	 */
	public static TreeMap<MovimId, MovimenModel> getMovimsOnBox(
			String boxAddr) {
		if (boxAddr == null || !Box.validateAddress(boxAddr))
			throw new IllegalArgumentException("Indirizzo Box non valido!");

		TreeMap<MovimId, MovimenModel> res = new TreeMap<>();
		for (Map.Entry<MovimId, MovimenModel> entry : instances.entrySet()) {
			MovimId pk = entry.getKey();
			MovimenModel mm = entry.getValue();

			// Seleziono solo movimentazioni su box cercato
			Movimentazione m = mm.getMovim();
			Box box = m.getBox();
			if (box.getIndirizzo().equals(boxAddr)) {

				// Se il prodotto non corrisponde c'è un problema di integrità
				// dei dati...
				if (!m.getProdotto().equals(box.getProd()))
					throw new InternalError("Errore di integrità: i prodotti "
							+ "di movimentazione e box non corrispondono!");

				res.put(pk, mm);
			}
		}
		return res;
	}

	/**
	 * Calcola la quantità di prodotto riservata per le movimentazioni già
	 * generate riguardanti il box specificato.
	 * 
	 * @param boxAddr Indirizzo del box d'interesse
	 * @return Quantità di prodotto da considerare non disponibile.
	 */
	protected static int calcReservedQtOfBox(String boxAddr) {
		int reserved = 0;
		TreeMap<MovimId, MovimenModel> mmm = getMovimsOnBox(boxAddr);
		for (Map.Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimId pk = entry.getKey();
			MovimenModel mm = entry.getValue();
			Movimentazione m = mm.getMovim();

			// Se si tratta di un ordine in uscita, se non è ancora stata
			// prelevata la qta e se non è annullata, sommo la qta al tot
			if (m.getOrdine().isOutgoing() && !m.isQtaPrelevata()
					&& m.getStato() != StatoMovim.ANNULLATA)
				reserved += m.getQuantità();
		}

		return reserved;
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

	// TODO to test
	public static void main(String[] args) {

		System.out.println("\nPRODOTTI:");
		TreeMap<Integer, ProductModel> map = ProductModel.getAllProductModels();
		map.forEach((id, m) -> System.out.println(m.getProdotto().toString()));
		System.out.println("\nORDINI:");
		TreeMap<Integer, OrderModel> omm = OrderModel.getAllOrderModels();
		omm.forEach((id, om) -> System.out.println(om.getOrdine().toString()));

		if (!MovimenModel.generatedMovimsOfOrder(5)) {
			TreeMap<MovimId, MovimenModel> orderMovimsOf = MovimenModel
					.generateOrderMovimsOfOrder(5);
			PrintUtils.printMovimsMap(orderMovimsOf);
		} else {
			PrintUtils.printMovimsMap(MovimenModel.getMovimsModelsOfOrder(5));
		}

		System.out.println("\nMOVIMENTAZIONI:");
		PrintUtils.printMovimsMap(MovimenModel.getAllMovimenModels());

		System.out.println("\nBOX:");
		TreeMap<String, BoxModel> bb = BoxModel.getAllBoxModels();
		bb.forEach((addr, bm) -> System.out.println(bm.getBox().toString()));
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
	 * Restituisce true se è stata generata almeno una movimentazione per
	 * l'ordine di ID specificato; false se altrimenti.
	 * 
	 * @param orderId ID ordine
	 * @return true se ne esiste anche solo una
	 */
	protected static boolean generatedMovimsOfOrder(int orderId) {
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
	protected static TreeMap<MovimId, MovimenModel> generateOrderMovimsOfOrder(
			int orderId) {
		if (generatedMovimsOfOrder(orderId))
			throw new IllegalArgumentException("Movimentazioni gia' generate");

		TreeMap<MovimId, MovimenModel> gen = new TreeMap<MovimId, MovimenModel>();
		OrderModel orderModel = OrderModel.getOrderModelById(orderId);
		Ordine o = orderModel.getOrdine();

		// Check stato ordine
		if (o.getStato() != StatoOrdine.IN_ATTESA)
			throw new IllegalArgumentException("Stato ordine non valido");

		// Scorro la lista della spesa dell'ordine
		for (Map.Entry<Prodotto, Integer> entry : o.getProdotti().entrySet()) {
			Prodotto p = entry.getKey();
			Integer qta = entry.getValue();

			// Controllo disponibilità prodotto se ordine in uscita
			if (o.getTipo() == TipoOrdine.OUT
					&& ProductModel.calcDispTotById(p.getId()) < qta)
				throw new IllegalArgumentException(
						"Disponibilità non sufficiente!");

			// Movimentazione da generare per questo prodotto
			MovimenModel mm = null;

			// Contatore qta necessaria
			int qtaCounter = qta;

			// Trovo lista dei box contenenti il prodotto dell'iterazione
			ArrayList<BoxModel> boxes = BoxModel.findBoxesWithProd(p);
			for (BoxModel bm : boxes) {

				// Se si tratta di un ordine in uscita controllo disponibilità
				if (o.getTipo() == TipoOrdine.OUT) {
					int disptaBox = bm.calcDisponibilita();
					if (disptaBox > 0) {
						int delta = disptaBox > qtaCounter ? qtaCounter
								: disptaBox;
						Movimentazione m = new Movimentazione(
								StatoMovim.NON_ASSEGNATA, delta, o, p,
								bm.getBox());
						mm = new MovimenModel(m);
						gen.put(mm.getKey(), mm);
						qtaCounter -= delta;
						if (qtaCounter == 0)
							break;
					}
				}

				// Se invece è un rifornimento
				else {
					Movimentazione m = new Movimentazione(
							StatoMovim.NON_ASSEGNATA, qta, o, p, bm.getBox());
					mm = new MovimenModel(m);
					gen.put(mm.getKey(), mm);
					qtaCounter = 0;
					break;
				}
			}
			if (qtaCounter != 0) {
				if (o.getTipo() == TipoOrdine.OUT)
					throw new InternalError(
							"Disponibilità sembrava OK ma nope...");

				// Se non è stato trovato un box assegnato al prodotto da
				// rifornire, ne assegno uno vuoto
				else {
					// Assegna prodotto ad un box libero random
					BoxModel bm = BoxModel.assignRandomBoxToProd(p);
					Objects.requireNonNull(bm,
							"Prodotto non assegnato ad alcun box");
					Box b = bm.getBox();
					Movimentazione m = new Movimentazione(
							StatoMovim.NON_ASSEGNATA, qta, o, p, b);
					mm = new MovimenModel(m);
					gen.put(mm.getKey(), mm);
					qtaCounter = 0;
				}
			}
		}

		// Cambia stato ordine da IN_ATTESA a IN_SVOLGIMENTO
		orderModel.setStato(StatoOrdine.IN_SVOLGIMENTO);

		// Restituisco la mappa delle movimentazioni generate per l'ordine
		return gen;
	}

	/**
	 * Restituisce la mappa delle movimentazioni generate dell'ordine
	 * specificato.
	 * 
	 * @param orderId ID ordine
	 * @return mappa delle movims
	 */
	protected static TreeMap<MovimId, MovimenModel> getGeneratedMovimsOfOrder(
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

	/**
	 * Movimentazione relativa al modello
	 */
	private Movimentazione movim;

	/**
	 * Record della movimentazione
	 */
	private MovimentazioneRecord record;

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
	 * Se possibile, segna la movimentazione come annullata
	 */
	public void annulla() {

		// Check stato
		if (!movim.isAnnullabile())
			throw new IllegalArgumentException(
					"Non è possibile annullare la movimentazione!");

		// Annulla
		movim.setStato(StatoMovim.ANNULLATA);
		record.setStato(StatoMovim.ANNULLATA.name());
		record.store();
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
	 * Assegna la movimentazione al magazziniere specificato
	 * 
	 * @param u utente magazziniere
	 */
	public void assignToWorker(Utente u) {

		// Check utente
		if (u == null || !u.isValid() || !u.isForkLifter())
			throw new IllegalArgumentException("Utente non valido");

		// Check movim
		if (movim.getStato() != StatoMovim.NON_ASSEGNATA)
			throw new IllegalStateException(
					"La movimentazione è già stata presa in carico");

		movim.setMagazziniere(u);
		record.setMagazziniere(u.getMatricola());
		movim.setStato(StatoMovim.PRESA_IN_CARICO);
		record.setStato(StatoMovim.PRESA_IN_CARICO.name());
		record.store();
		notifyChangeListeners(null);
	}

	/**
	 * Contrassegna la movimentazione come PRELEVATA e, se si tratta di un
	 * ordine in uscita, aggiorna la quantità del box.
	 */
	public void markAsLoaded() {
		// Check movim
		if (movim.getStato() != StatoMovim.PRESA_IN_CARICO)
			throw new IllegalStateException();

		// Preleva da box (se ordine in uscita)
		if (movim.getOrdine().isOutgoing())
			BoxModel.getBoxModel(movim.getBox()).preleva(movim.getQuantità());

		movim.setStato(StatoMovim.PRELEVATA);
		record.setStato(StatoMovim.PRELEVATA.name());
		record.store();
		notifyChangeListeners(null);
	}

	/**
	 * Contrassegna la movimentazione come COMPLETATA e, se si tratta di un
	 * ordine in entrata, aggiorna la quantità del box.
	 */
	public void markAsCompleted() {
		// Check movim
		if (movim.getStato() != StatoMovim.PRELEVATA)
			throw new IllegalStateException();

		// Deposita in box (se ordine in entrata)
		Ordine ordine = movim.getOrdine();
		if (!ordine.isOutgoing())
			BoxModel.getBoxModel(movim.getBox())
					.rifornisci(movim.getQuantità());

		movim.setStato(StatoMovim.COMPLETATA);
		record.setStato(StatoMovim.COMPLETATA.name());
		record.store();

		// Se tutte le movimentazioni di un ordine sono state completate,
		// contrassegna anch'esso come come COMPLETATO
		TreeMap<MovimId, MovimenModel> movims = getGeneratedMovimsOfOrder(
				ordine.getId());
		boolean completed = true;
		for (Map.Entry<MovimId, MovimenModel> entry : movims.entrySet()) {
			MovimId pk = entry.getKey();
			MovimenModel mm = entry.getValue();
			if (mm.getMovim().getStato() != StatoMovim.COMPLETATA) {
				completed = false;
				break;
			}
		}
		if (completed)
			OrderModel.getOrderModelOf(ordine).markAsCompleted();

		notifyChangeListeners(null);

		// TODO: accountability; campo dataCompletamento?
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
}