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

import ingsw_proj_magazzino.db.generated.tables.records.OrdineRecord;
import ingsw_proj_magazzino.db.generated.tables.records.ProdottiordiniRecord;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;

public class OrderModel extends BaseModel {

	// l'integer nell'HashMap è l'id dell'ordine
	private static HashMap<Integer, OrderModel> instances = new HashMap<Integer, OrderModel>();
	private Ordine ordine;
	private OrdineRecord orderRecord;
	private ProdottiordiniRecord prodOrderRecord;
	private ArrayList<ProdottiordiniRecord> prodottiRecord; // per inserire
															// tutti i record
															// dei prodotti nel
															// caso dovesse
															// servire

	// Costruttore
	private OrderModel(Ordine order) {
		this.ordine = order;
		this.orderRecord = fetchOrderById(order.getId());
	}

	// aggiunte eccezione a causa delle conversioni
	private void refreshOrderFromDb() throws ParseException {
		this.orderRecord = fetchOrderById(ordine.getId());
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

		orderRecord = (OrdineRecord) fetchOrderById(o.getId());
		copyOrdineIntoRecord(o, orderRecord);
		orderRecord.store(); // UPDATE con UpdatableRecord
	}

	public void deleteOrdine() throws ParseException {
		if (orderIsSavedInDb()) {
			orderRecord.delete(); // DELETE con UpdatableRecord
		}
	}

	public void createProdottoOrdine() {
		HashMap<Prodotto, Integer> prodotti = ordine.getProdotti();

		for (Map.Entry<Prodotto, Integer> entry : prodotti.entrySet()) {
			Prodotto p = entry.getKey();
			int qta = entry.getValue();
			ProdottiordiniRecord por = DSL.newRecord(PRODOTTIORDINI);
			copyProdOrderIntoRecord(this.ordine, p, qta, por);
			por.store();
			this.prodOrderRecord = por;
		}
	}

	public void updateProdottoOrdine(Ordine o, Prodotto p, int qta)
			throws SQLIntegrityConstraintViolationException, ParseException {
		if (!orderIsSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + o.getId() + " non esiste!");

		prodOrderRecord = (ProdottiordiniRecord) fetchProductOrderByIds(
				o.getId(), p.getId());
		copyProdOrderIntoRecord(o, p, qta, prodOrderRecord);
		prodOrderRecord.store(); // UPDATE con UpdatableRecord
	}

	public void deleteProductOrder() throws ParseException {
		if (orderIsSavedInDb()) {
			prodOrderRecord.delete(); // DELETE con UpdatableRecord
		}
	}

	// Static methods ======

	// restituisce il modello dell'ordine
	public static OrderModel getOrderModelOf(Ordine o) {

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
	private static OrdineRecord fetchOrderById(int id) {
		OrdineRecord r = (OrdineRecord) DSL.select().from(ORDINE)
				.where(ORDINE.ID.eq(id))
				.fetchOne(); // SELECT
		return r;
	}

	private static ProdottiordiniRecord fetchProductOrderByIds(int idO,
			int idP) {
		ProdottiordiniRecord r = (ProdottiordiniRecord) DSL.select()
				.from(PRODOTTIORDINI)
				.where(PRODOTTIORDINI.ORDINE.eq(idO)
						.and(PRODOTTIORDINI.PROD.eq(idP)))
				.fetchOne(); // SELECT
		return r;
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
	public static int getNextAvailableOrderId() {
		Integer max = DSL.select(max(ORDINE.ID)).from(ORDINE).fetchOne()
				.value1();
		if (max == null)
			return 0;
		return max + 1;
	}
}
