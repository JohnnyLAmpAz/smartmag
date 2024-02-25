package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.ORDINE;
import static org.jooq.impl.DSL.max;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ingsw_proj_magazzino.db.generated.tables.records.OrdineRecord;
import smartmag.data.Ordine;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;

public class OrderModel extends BaseModel {

	// l'integer nell'HashMap è l'id dell'ordine
	private static HashMap<Integer, OrderModel> instances = new HashMap<Integer, OrderModel>();
	private Ordine ordine;
	private OrdineRecord orderRecord;

	// Costruttore
	private OrderModel(Ordine order) {
		this.ordine = order;
		this.orderRecord = fetchOrderById(order.getId());
	}

	// aggiunte eccezione a causa delle conversioni
	private void refresh() throws ParseException {
		this.orderRecord = fetchOrderById(ordine.getId());
		if (this.orderRecord != null) {
			Ordine o = ordineFromRecord(this.orderRecord);
			this.orderRecord.setTipo(o.getTipo().name()); // aggiunto .name per
			// convertire
			// ENUM->String
			this.orderRecord.setStato(o.getStato().name()); // aggiunto .name
			// per convertire
			// ENUM->String
			this.orderRecord.setDataem(o.getDataEmissione().toString());
			this.orderRecord.setDataco(o.getDataCompletamento().toString());
		}

		// TODO: event
	}

	public boolean isSavedInDb() throws ParseException {
		refresh();
		return orderRecord != null;
	}

	public void createOrdine()
			throws SQLIntegrityConstraintViolationException, ParseException {

		if (isSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + ordine.getId() + " esiste già!");

		OrdineRecord r = DSL.newRecord(ORDINE);
		r.setId(ordine.getId());
		copyOrdineIntoRecord(ordine, r);
		r.store(); // INSERT
		this.orderRecord = r;

		// TODO: event
	}

	public void updateOrdine(Ordine o)
			throws SQLIntegrityConstraintViolationException, ParseException {
		if (!isSavedInDb())
			throw new SQLIntegrityConstraintViolationException(
					"Ordine #" + o.getId() + " non esiste!");

		orderRecord = (OrdineRecord) fetchOrderById(o.getId());
		copyOrdineIntoRecord(o, orderRecord);
		orderRecord.store(); // UPDATE con UpdatableRecord
	}

	public void deleteOrdine() throws ParseException {
		if (isSavedInDb()) {
			orderRecord.delete(); // DELETE con UpdatableRecord
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

	// restituisce un ordine partendo dal suo record
	private static Ordine ordineFromRecord(OrdineRecord r)
			throws ParseException {
		if (r == null)
			return null;
		// c'è un controllo sull'inserimento corretto della data?
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

	// incrementa id ordine
	public static int getNextAvailableId() {
		Integer max = DSL.select(max(ORDINE.ID)).from(ORDINE).fetchOne()
				.value1();
		if (max == null)
			return 0;
		return max + 1;
	}
}
