package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.ORDINE;
import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTIORDINI;
import static org.jooq.impl.DSL.max;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.Record;
import org.jooq.Result;

import ingsw_proj_magazzino.db.generated.tables.records.OrdineRecord;
import ingsw_proj_magazzino.db.generated.tables.records.ProdottiordiniRecord;
import ingsw_proj_magazzino.db.generated.tables.records.ProdottoRecord;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;

public class OrderModel extends BaseModel {

	// l'integer nell'HashMap è l'id dell'ordine
	private static TreeMap<Integer, OrderModel> instances;

	// quando viene generato istances la aggiorna prendendo i modelli dal DB
	static {
		instances = new TreeMap<Integer, OrderModel>();
		Map<Integer, Record> res = DSL.select().from(ORDINE)
				.fetchMap(ORDINE.ID);
		res.forEach((id, r) -> {
			try {
				instances.put(id, new OrderModel(
						ordineFromOrdineRecord((OrdineRecord) r)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private Ordine ordine;
	private OrdineRecord orderRecord;
	private ArrayList<ProdottiordiniRecord> listaProdottiOrdiniRecord;

	/**
	 * Metodo costruttore per la classe OrderModel. Costruisce il modello
	 * partendo da un ordine e lo salva all'interno di un hashMap che contiene
	 * tutti i modelli degli ordini
	 * 
	 * @param order ordine del quale si vuole generare il modello
	 */
	private OrderModel(Ordine order) {
		this.ordine = order;
		this.orderRecord = fetchOrderRecordById(order.getId());
		// la prima volta che si crea il modello, poichè la lista dei prodotti è
		// vuota, la prende dal db e la aggiorna anche nell'oggetto ordine
		// this.ordine.setProdotti(fetchListaProdottiFromDb(order.getId()));
		// aggiorno la lista dei prodottiOrdiniRecord
		this.listaProdottiOrdiniRecord = fetchProductOrderRecordListByOrder(
				order);
		if (listaProdottiOrdiniRecord == null)
			listaProdottiOrdiniRecord = new ArrayList<>();

		if (!instances.containsKey(order.getId()))
			instances.put(order.getId(), this); // carico OrderModel in hashmap
	}

	/**
	 * Restituisce il clone dell'ordine del modello
	 */
	public Ordine getOrdine() {
		return ordine.clone();
	}

	/**
	 * Se preparabile, genera movimentazioni e cambia stato da IN_ATTESA a
	 * IN_SVOLGIMENTO.
	 */
	public void approva() {
		if (ordine.getStato() != StatoOrdine.IN_ATTESA)
			throw new IllegalStateException("Ordine non IN_ATTESA!");
		if (MovimenModel.generatedMovimsOfOrder(ordine.getId()))
			throw new IllegalStateException("Movimentazioni già generate!");
		if (!isPreparabile())
			throw new IllegalStateException("Ordine non preparabile!");
		MovimenModel.generateOrderMovimsOfOrder(ordine.getId());
	}

	/**
	 * Crea un nuovo record dell'ordine usando l'ordine del modello
	 */
	private void createOrdineRecord()
			throws SQLIntegrityConstraintViolationException, ParseException {

		if (orderIsSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + ordine.getId() + " esiste già!");

		OrdineRecord or = DSL.newRecord(ORDINE);
		or.setId(ordine.getId());
		copyOrdineIntoRecord(ordine, or);
		or.store(); // INSERT
		this.orderRecord = or;

		// evento per notificare il cambiamento
		notifyChangeListeners(null);
	}

	/**
	 * Cerca se esiste il record dell'ordine, se esiste lo aggiorna utilizzando
	 * i valori dell'ordine passato al metodo
	 * 
	 * @param o ordine del quale si vuole aggiornare il record
	 */
	public void updateOrdine(Ordine o)
			throws SQLIntegrityConstraintViolationException, ParseException {
		if (!orderIsSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + o.getId() + " non esiste!");
		// TODO check values
		orderRecord = (OrdineRecord) fetchOrderRecordById(o.getId());
		copyOrdineIntoRecord(o, orderRecord);
		orderRecord.store(); // UPDATE con UpdatableRecord
		this.ordine = o;
		updateProdottoOrdineRecord();
		// evento per notificare il cambiamento
		notifyChangeListeners(null);
	}

	public void inserisciProdotto(Prodotto p, int q)
			throws SQLIntegrityConstraintViolationException {

		fetchListaProdottiFromDb(this.ordine.getId());

		if (p.isValid() && q > 0) {
			createProdottoOrdineRecord(p, q);
		} else
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + p.getId() + " prodotto non valido o qta < 0");
	}

	/**
	 * Aggiorna lo stato dell'ordine sia nel record che nel parametro ordine che
	 * è stato passato al metodo
	 * 
	 * @param o        ordine del quale si vuole modificare lo stato
	 * @param newStato nuovo stato dell'ordine
	 * @throws ParseException se newStato != ENUM
	 */
	protected void updateStatoOrdine(Ordine o, String newStato)
			throws SQLIntegrityConstraintViolationException, ParseException {
		if (!orderIsSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + o.getId() + " non esiste!");

		o.setStato(StatoOrdine.valueOf(newStato));
		updateOrdine(o);
	}

	/**
	 * Imposta lo stato dell'ordine
	 */
	protected void setStato(StatoOrdine s) {
		ordine.setStato(s);
		orderRecord.setStato(s.name());
		orderRecord.store();
		notifyChangeListeners(null);
	}

	/**
	 * Contrassegna l'ordine come completato, modificando la data di
	 * completamento.
	 */
	protected void markAsCompleted() {
		LocalDate now = LocalDate.now();
		StatoOrdine stato = StatoOrdine.COMPLETATO;
		ordine.setStato(stato);
		orderRecord.setStato(stato.name());
		ordine.setDataCompletamento(now);
		orderRecord.setDataco(now.toString());
		orderRecord.store();
		notifyChangeListeners(null);
	}

	/**
	 * cancella il record dell'ordine nel modello poichè nel DB è stato messo
	 * DELETE ON CASCADE, cancella anche i record id prodotti ordini
	 */
	public void deleteOrdine() throws ParseException {
		if (orderIsSavedInDb()) {
			for (ProdottiordiniRecord por : listaProdottiOrdiniRecord) {
				if (por != null)
					por.delete();
			}
			orderRecord.delete(); // DELETE con UpdatableRecord
			orderRecord = null;
			listaProdottiOrdiniRecord.clear();
			notifyChangeListeners(null); // evento per notificare il cambiamento
		}
	}

	/**
	 * Aggiorna l'ordine prendendo i dati dal record di quell'ordine
	 */
	// aggiunte eccezioni a causa delle conversioni
	private void refreshOrderFromDb() throws ParseException {
		this.orderRecord = fetchOrderRecordById(ordine.getId());
		if (this.orderRecord != null) {
			Ordine o = ordineFromOrdineRecord(this.orderRecord);
			this.ordine.setTipo(o.getTipo());
			this.ordine.setStato(o.getStato());
			this.ordine.setDataEmissione(o.getDataEmissione());
			this.ordine.setDataCompletamento(o.getDataCompletamento());
			this.ordine.setProdotti(o.getProdotti());
		}
		// TODO: event
	}

	/**
	 * Controlla che ci sia un record di quell'ordine nel DB
	 * 
	 * @return ritorna 1 se è presente e 0 se non esiste
	 */
	public boolean orderIsSavedInDb() throws ParseException {
		refreshOrderFromDb();
		return orderRecord != null;
	}

	/**
	 * crea un nuovo record di prodottoOrdine ottenendo i prodotti e le quantità
	 * dall'ordine del modello
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	private void createProdottoOrdineRecord()
			throws SQLIntegrityConstraintViolationException {
		HashMap<Prodotto, Integer> prodotti = ordine.getProdotti();

		for (Map.Entry<Prodotto, Integer> entry : prodotti.entrySet()) {
			if (productOrderIsSavedInDb(entry.getKey().getId()))
				throw new SQLIntegrityConstraintViolationException(
						"Prodotto #" + entry.getKey().getId()
								+ " record prodotto esiste già!");

			Prodotto p = entry.getKey();
			int qta = entry.getValue();
			ProdottiordiniRecord por = DSL.newRecord(PRODOTTIORDINI);
			copyProdOrderIntoRecord(this.ordine, p, qta, por);
			por.store();
			this.listaProdottiOrdiniRecord.add(por);
		}
		notifyChangeListeners(null); // evento per notificare il cambiamento
	}

	private void updateProdottoOrdineRecord()
			throws SQLIntegrityConstraintViolationException {
		listaProdottiOrdiniRecord = new ArrayList<ProdottiordiniRecord>();

		HashMap<Prodotto, Integer> prodotti = ordine.getProdotti();

		for (Map.Entry<Prodotto, Integer> entry : prodotti.entrySet()) {

			Prodotto p = entry.getKey();
			ProdottiordiniRecord por = fetchProductOrderRecordById(
					ordine.getId(), p.getId());
			int qta = entry.getValue();

			if (por == null)
				por = DSL.newRecord(PRODOTTIORDINI);

			copyProdOrderIntoRecord(this.ordine, p, qta, por);
			por.store();
			this.listaProdottiOrdiniRecord.add(por);
		}
		notifyChangeListeners(null); // evento per notificare il cambiamento
	}

	/**
	 * crea un nuovo record di prodottoOrdine dai prodotti e dalle quantità
	 * passate al metodo
	 * 
	 * @param p   prodotto da aggiungere al record
	 * @param qta quantità da aggiungere al record
	 */
	private void createProdottoOrdineRecord(Prodotto p, int qta) {
		ProdottiordiniRecord por = DSL.newRecord(PRODOTTIORDINI);
		copyProdOrderIntoRecord(this.ordine, p, qta, por);
		por.store();
		this.listaProdottiOrdiniRecord.add(por);
		notifyChangeListeners(null); // evento per notificare il cambiamento
	}

	/**
	 * Aggiorna la quantità nel record prodottoOrdine
	 * 
	 * @param o   ordine
	 * @param p   prodotto
	 * @param qta nuova quantità da inserire nel record
	 */
	public void updateQtaProdottoOrdine(Prodotto p, int qta)
			throws SQLIntegrityConstraintViolationException, ParseException {
		if (!productOrderIsSavedInDb(p.getId()))
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + this.ordine.getId() + " non esiste!");

		ProdottiordiniRecord por = (ProdottiordiniRecord) fetchProductOrderRecordById(
				this.ordine.getId(), p.getId());
		int index = this.listaProdottiOrdiniRecord.indexOf(por);
		this.listaProdottiOrdiniRecord.set(index, por);
		copyProdOrderIntoRecord(this.ordine, p, qta, por);
		por.store(); // UPDATE con UpdatableRecord
		notifyChangeListeners(null); // evento per notificare il cambiamento
	}

	/**
	 * Verifica se vi è la disponibilità per tutti gli elementi della "lista
	 * della spesa" se si tratta di un ordine in uscita.
	 * 
	 * @return true se ci sono abbastanza prodotti, false se altrimenti
	 */
	public boolean isPreparabile() {
		if (ordine.getTipo() == TipoOrdine.IN)
			return true;
		for (Map.Entry<Prodotto, Integer> entry : ordine.getProdotti()
				.entrySet()) {
			Prodotto p = entry.getKey();
			Integer qta = entry.getValue();
			if (ProductModel.calcDispTotById(p.getId()) < qta)
				return false;
		}
		return true;
	}

	/**
	 * Verifica che il record relativo a quel prodottoOrdine esista per farlo ha
	 * bisogno dell'id ordine e dell' id prodotto
	 * 
	 * @param idO identificativo dell'ordine
	 * @param idP identificativo del prodotto
	 * @return ritorna vero se esiste
	 */
	public boolean productOrderIsSavedInDb(int idP) {
		for (ProdottiordiniRecord por : listaProdottiOrdiniRecord) {
			if (por == null)
				return false;
			if (this.ordine.getId() == por.getProd() && idP == por.getProd())
				return true;
			// TODO aggiornare dati
		}
		return false;
	}

	// Static methods for order ======
	/**
	 * Restituisce il modello dell'ordine con ID specificato. Se non è stato mai
	 * creato, restituisce null.
	 * 
	 * @param orderId ID dell'ordine di cui si vuole ottenere il modello
	 * @return modello dell'ordine
	 */
	public static OrderModel getOrderModelById(int orderId) {
		if (!instances.containsKey(orderId))
			return null;
		return instances.get(orderId);
	}

	/**
	 * Restituisce il modello dell'ordine che è stato passato al metodo
	 * 
	 * @param o ordine del quale si vuole ottenere il modello
	 * @return modello dell'ordine
	 */
	public static OrderModel getOrderModelOf(Ordine o) {

		if (o != null && o.isValid()) {
			if (!instances.containsKey(o.getId())) {
				OrderModel om = new OrderModel(o);
				return om;
			} else {
				return instances.get(o.getId());
			}
		} else
			throw new IllegalArgumentException("Ordine non valido!");
	}

	/**
	 * Estrae dal DB il record ORDINE corrispondente all'id inserito
	 * 
	 * @param id identificativo dell'ordine
	 * @return
	 */
	private static OrdineRecord fetchOrderRecordById(int id) {
		OrdineRecord r = (OrdineRecord) DSL.select().from(ORDINE)
				.where(ORDINE.ID.eq(id)).fetchOne(); // SELECT
		return r;
	}

	/**
	 * Restituisce un ordine partendo dal suo record
	 * 
	 * @param r record dell'ordine
	 * @return ritorna l'oggetto ordine
	 */
	private static Ordine ordineFromOrdineRecord(OrdineRecord or)
			throws ParseException {
		if (or == null)
			return null;

		Integer id = or.getId();

		TipoOrdine tipo = TipoOrdine.valueOf(or.getTipo());
		StatoOrdine stato = StatoOrdine.valueOf(or.getStato());

		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
		LocalDate dataEmissione = LocalDate.parse(or.getDataem(), formatter);
		LocalDate dataCompletamento;

		if (or.getDataco() == null)
			dataCompletamento = null;
		else
			dataCompletamento = LocalDate.parse(or.getDataco(), formatter);

		HashMap<Prodotto, Integer> listaProdotti = fetchListaProdottiFromDb(
				or.getId());
		Ordine ord = new Ordine(id, tipo, stato, dataEmissione,
				dataCompletamento);
		ord.setProdotti(listaProdotti);
		return ord;
	}

	/**
	 * Copia i valori contenuti nell'ordine all'interno del record di
	 * quell'ordine (tranne id)
	 * 
	 * @param o ordine
	 * @param r record dell'ordine
	 */
	private static void copyOrdineIntoRecord(Ordine o, OrdineRecord r) {
		r.setTipo(o.getTipo().name());
		r.setStato(o.getStato().name());

		LocalDate emDate = o.getDataEmissione();
		LocalDate coDate = o.getDataCompletamento();

		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

		String formattedDateEm = emDate.format(formatter);
		String formattedDateCo = null;

		if (coDate != null)
			formattedDateCo = coDate.format(formatter);

		r.setDataem(formattedDateEm);
		r.setDataco(formattedDateCo);
	}

	/**
	 * Ritorna un id per l'ordine che non è presente nel DB
	 * 
	 * @return
	 */
	public static int getNextAvailableOrderId() {
		Integer max = DSL.select(max(ORDINE.ID)).from(ORDINE).fetchOne()
				.value1();
		if (max == null)
			return 0;
		return max + 1;
	}

	/**
	 * Copia i valori di ordine,prodotto e quantità nel record prodottiOrdine
	 * 
	 * @param o   ordine
	 * @param p   prodotto
	 * @param qta quantità
	 * @param r   prodottiOrdiniRecord
	 */
	private static void copyProdOrderIntoRecord(Ordine o, Prodotto p, int qta,
			ProdottiordiniRecord r) {
		r.setOrdine(o.getId());
		r.setProd(p.getId());
		r.setQta(qta);
	}

	// Static methods for productOrder ======

	/**
	 * Cerca nel DB e restituisce la lista di tutti i prodotti relativi
	 * all'ordine del quale si è passato l'id al metodo
	 */
	private static HashMap<Prodotto, Integer> fetchListaProdottiFromDb(int id) {
		Result<Record> result = DSL.select().from(PRODOTTIORDINI)
				.where(PRODOTTIORDINI.ORDINE.eq(id)).fetch();
		HashMap<Prodotto, Integer> prodotti = new HashMap<>();
		ProdottoRecord pr;
		Prodotto p;

		for (Record r : result) {
			ProdottiordiniRecord por = r.into(ProdottiordiniRecord.class);
			pr = ProductModel.fetchProdById(por.getProd());
			p = ProductModel.prodottoFromRecord(pr);
			prodotti.put(p, por.getQta());
		}
		return prodotti;
	}

	/**
	 * Ritorna la lista di tutti i record di ProdottiOrdini presenti nel DB
	 * relativi all'ordine passato al metodo
	 * 
	 * @param o ordine del quale si vuole ottenere la lista dei record
	 *          ProdottiOrdini
	 */
	private static ArrayList<ProdottiordiniRecord> fetchProductOrderRecordListByOrder(
			Ordine o) {
		HashMap<Prodotto, Integer> prodotti = o.getProdotti();
		Prodotto p;
		ArrayList<ProdottiordiniRecord> lista = new ArrayList<>();

		for (Map.Entry<Prodotto, Integer> entry : prodotti.entrySet()) {
			p = entry.getKey();
			lista.add(fetchProductOrderRecordById(o.getId(), p.getId()));
		}
		return lista;
	}

	/**
	 * Restituisce il record di prodottiOrdini relativo a id ordine e prodotto
	 * passati al metodo
	 * 
	 * @param idO identificativo dell'ordine
	 * @param idP identificativo del prodotto
	 */
	private static ProdottiordiniRecord fetchProductOrderRecordById(int idO,
			int idP) {

		ProdottiordiniRecord r = (ProdottiordiniRecord) DSL.select()
				.from(PRODOTTIORDINI).where(PRODOTTIORDINI.ORDINE.eq(idO)
						.and(PRODOTTIORDINI.PROD.eq(idP)))
				.fetchOne(); // SELECT
		return r;
	}

	// Generic Static Methods ======

	/**
	 * Crea il modello dell'ordine se non esiste già, inoltre crea sia il record
	 * dell'ordine che il record di prodottoOrdine
	 * 
	 * @param o ordine del quale si vuole creare il modello
	 * @return ritorna il modello dell'ordine appena creato
	 */
	public static OrderModel create(Ordine o)
			throws SQLIntegrityConstraintViolationException, ParseException {

		if (!o.isValid())
			throw new IllegalArgumentException("Ordine non valido");
		OrderModel om = getOrderModelOf(o);
		om.createOrdineRecord();
		om.createProdottoOrdineRecord();
		notifyChangeListeners(null); // evento per notificare il cambiamento
		return om;
	}

	/**
	 * Restituisce una copia della TreeMap di tutti i modelli degli ordini nel
	 * DB La TreeMap è una mappa ordinata degli elementi
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static TreeMap<Integer, OrderModel> getAllOrderModels() {
		TreeMap<Integer, OrderModel> tm = (TreeMap<Integer, OrderModel>) instances
				.clone();
		return treeMapFilter(tm);
	}

	private static TreeMap<Integer, OrderModel> treeMapFilter(
			TreeMap<Integer, OrderModel> om) {
		TreeMap<Integer, OrderModel> filtrata = new TreeMap<Integer, OrderModel>();
		for (Map.Entry<Integer, OrderModel> entry : om.entrySet()) {
			if (entry.getValue().orderRecord != null) {
				filtrata.put(entry.getKey(), entry.getValue());
			}
		}
		return filtrata;
	}

	public static void main(String[] args)
			throws SQLIntegrityConstraintViolationException, ParseException {
		LocalDate dem = LocalDate.parse("2024-03-05");
		LocalDate dco = LocalDate.of(2024, 04, 04);
		Prodotto p = new Prodotto(ProductModel.getNextAvailableId(), "scala2",
				"marcia", 10, 2);
		Prodotto p1 = new Prodotto(ProductModel.getNextAvailableId() + 1,
				"scala3", "marcia", 10, 2);
		Prodotto p2 = new Prodotto(ProductModel.getNextAvailableId() + 2,
				"scala4", "marcia", 10, 2);
		Prodotto p3 = new Prodotto(ProductModel.getNextAvailableId() + 3,
				"scala5", "marcia", 10, 2);
		HashMap<Prodotto, Integer> prodotti = new HashMap<>();
		prodotti.put(p, p.getId());
		prodotti.put(p1, p1.getId());
		prodotti.put(p2, p2.getId());
		prodotti.put(p3, p3.getId());
		System.out.println(dem);
		System.out.println(dco);

		ProductModel.createProdotto(p);
		ProductModel.createProdotto(p1);
		ProductModel.createProdotto(p2);
		ProductModel.createProdotto(p3);
		ArrayList<Prodotto> prods = ProductModel.getAllProducts();
		System.out.println(prods);

		Ordine o = new Ordine(getNextAvailableOrderId(), TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, dem, dco);
		o.setProdotti(prodotti);
		System.out.println(o.isValid());
		System.out.println(dco + "    " + dem);
		create(o);
		OrderModel om = new OrderModel(o);
		getAllOrderModels();
		instances.forEach(
				(id, om1) -> System.out.println(om1.getOrdine().getId()));
	}

}
