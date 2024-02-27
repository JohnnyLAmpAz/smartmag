package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.ORDINE;
import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTIORDINI;
import static org.jooq.impl.DSL.max;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	private static HashMap<Integer, OrderModel> instances = new HashMap<Integer, OrderModel>();
	private Ordine ordine;
	private OrdineRecord orderRecord;
	private ArrayList<ProdottiordiniRecord> listaProdottiOrdiniRecord;

	// Costruttore
	private OrderModel(Ordine order) {
		this.ordine = order;
		this.orderRecord = fetchOrderRecordById(order.getId());
		// la prima volta che si crea il modello, poichè la lista dei prodotti è
		// vuota, la prende dal db e la aggiorna anche nell'oggetto ordine
		this.ordine.setProdotti(getListaProdottiFromDb());
		// aggiorno la lista dei prodottiOrdiniRecord
		this.listaProdottiOrdiniRecord = fetchProductOrderRecordListByOrder(
				order);
	}

	// aggiunte eccezioni a causa delle conversioni
	private void refreshOrderFromDb() throws ParseException {
		this.orderRecord = fetchOrderRecordById(ordine.getId());
		if (this.orderRecord != null) {
			Ordine o = ordineFromRecord(this.orderRecord);
			this.ordine.setTipo(o.getTipo());
			// convertire
			// ENUM->String
			this.ordine.setStato(o.getStato()); // aggiunto .name
			// per convertire
			// ENUM->String
			this.ordine.setDataEmissione(o.getDataEmissione());
			this.ordine.setDataCompletamento(o.getDataCompletamento());
		}

		// TODO: event
	}

	public boolean orderIsSavedInDb() throws ParseException {
		refreshOrderFromDb();
		return orderRecord != null;
	}

	// TODO public boolean productOrderIsSavedInDb(int idO, int idP) {
	// }

	public void createOrdine()
			throws SQLIntegrityConstraintViolationException, ParseException {

		if (orderIsSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + ordine.getId() + " esiste già!");

		OrdineRecord or = DSL.newRecord(ORDINE);
		or.setId(ordine.getId());
		copyOrdineIntoRecord(ordine, or);
		or.store(); // INSERT
		this.orderRecord = or;

		// TODO: event
	}

	public void updateOrdine(Ordine o)
			throws SQLIntegrityConstraintViolationException, ParseException {
		if (!orderIsSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + o.getId() + " non esiste!");

		orderRecord = (OrdineRecord) fetchOrderRecordById(o.getId());
		copyOrdineIntoRecord(o, orderRecord);
		orderRecord.store(); // UPDATE con UpdatableRecord
	}

	public void deleteOrdine() throws ParseException {
		if (orderIsSavedInDb()) {
			orderRecord.delete(); // DELETE con UpdatableRecord
		}
	}

	public boolean productOrderIsSavedInDb(int idO, int idP) {
		for (ProdottiordiniRecord por : listaProdottiOrdiniRecord) {
			if (idO == por.getProd() && idP == por.getProd())
				return true;
		}
		return false;
	}

	public void createProdottoOrdine() {
		HashMap<Prodotto, Integer> prodotti = ordine.getProdotti();

		for (Map.Entry<Prodotto, Integer> entry : prodotti.entrySet()) {
			Prodotto p = entry.getKey();
			int qta = entry.getValue();
			ProdottiordiniRecord por = DSL.newRecord(PRODOTTIORDINI);
			copyProdOrderIntoRecord(this.ordine, p, qta, por);
			por.store();
			this.listaProdottiOrdiniRecord.add(por);
		}
	}

	public void updateQtaProdottoOrdine(Ordine o, Prodotto p, int qta)
			throws SQLIntegrityConstraintViolationException, ParseException {
		// TODO metodo prodottoOrdineIsSavedInDb da fare !!!
		// TODO metodo refreshProductOrderFromDb
		if (!productOrderIsSavedInDb(o.getId(), p.getId()))
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + o.getId() + " non esiste!");

		ProdottiordiniRecord por = (ProdottiordiniRecord) fetchProductOrderRecordById(
				o.getId(), p.getId());
		int index = this.listaProdottiOrdiniRecord.indexOf(por);
		this.listaProdottiOrdiniRecord.set(index, por);
		copyProdOrderIntoRecord(o, p, qta, por);
		por.store(); // UPDATE con UpdatableRecord
	}

	public void deleteProductOrder(Ordine o, Prodotto p) throws ParseException {
		// TODO metodo prodottoOrdineIsSavedInDb da fare !!!
		// TODO metodo refreshProductOrderFromDb
		if (!productOrderIsSavedInDb(o.getId(), p.getId())) {
			ProdottiordiniRecord por = (ProdottiordiniRecord) fetchProductOrderRecordById(
					o.getId(), p.getId());
			por.delete();
		}
	}

	// Static methods ======

	// restituisce il modello
	private static OrderModel getOrderModelOf(Ordine o) {

		if (o != null && o.isValid()) {
			if (!instances.containsKey(o.getId())) {
				OrderModel om = new OrderModel(o);
				instances.put(o.getId(), om);
				return om;
			} else {
				return instances.get(o.getId());
			}
		} else
			throw new IllegalArgumentException("Ordine non valido!");
	}

	// metodo che estrae dal DB il record ORDINE corrispondente all'id inserito
	private static OrdineRecord fetchOrderRecordById(int id) {
		OrdineRecord r = (OrdineRecord) DSL.select().from(ORDINE)
				.where(ORDINE.ID.eq(id))
				.fetchOne(); // SELECT
		return r;
	}

	private static ProdottiordiniRecord fetchProductOrderRecordById(int idO,
			int idP) {

		ProdottiordiniRecord r = (ProdottiordiniRecord) DSL.select()
				.from(PRODOTTIORDINI)
				.where(PRODOTTIORDINI.ORDINE.eq(idO)
						.and(PRODOTTIORDINI.PROD.eq(idP)))
				.fetchOne(); // SELECT
		return r;
	}

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

	private static HashMap<Prodotto, Integer> getListaProdottiFromDb() {
		Result<Record> result = DSL.select().from(PRODOTTIORDINI).fetch();
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

	// restituisce un ordine partendo dal suo record
	private static Ordine ordineFromRecord(OrdineRecord r)
			throws ParseException {
		if (r == null)
			return null;

		Integer id = r.getId();

		TipoOrdine tipo = TipoOrdine.valueOf(r.getTipo());
		StatoOrdine stato = StatoOrdine.valueOf(r.getStato());
		Date dataEmissione = new SimpleDateFormat("dd/MM/yyy")
				.parse(r.getDataem());
		Date dataCompletamento = new SimpleDateFormat("dd/MM/yyy")
				.parse(r.getDataco());
		return new Ordine(id, tipo, stato, dataEmissione, dataCompletamento);
	}

	private static void copyOrdineIntoRecord(Ordine o, OrdineRecord r) {
		r.setTipo(o.getTipo().name());
		r.setStato(o.getStato().name());
		r.setDataem(o.getDataEmissione().toString());
		r.setDataco(o.getDataCompletamento().toString());
	}

	private static void copyProdOrderIntoRecord(Ordine o, Prodotto p, int qta,
			ProdottiordiniRecord r) {
		r.setOrdine(o.getId());
		r.setProd(p.getId());
		r.setQta(qta);
	}

	// incrementa id ordine
	private static int getNextAvailableOrderId() {
		Integer max = DSL.select(max(ORDINE.ID)).from(ORDINE).fetchOne()
				.value1();
		if (max == null)
			return 0;
		return max + 1;
	}

}
