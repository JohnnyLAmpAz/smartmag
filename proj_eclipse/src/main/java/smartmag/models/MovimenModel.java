package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.MOVIMENTAZIONE;

import java.util.TreeMap;

import ingsw_proj_magazzino.db.generated.tables.records.MovimentazioneRecord;
import smartmag.data.Box;
import smartmag.data.MovimId;
import smartmag.data.Movimentazione;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoMovimentazione;
import smartmag.data.Utente;

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
		this.record = fetchMovimRecord(movim.getMovimId());
		if (this.record != null) {
			this.movim = movimFromRecord(this.record);
		}
		notifyChangeListeners(null);
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
				.and(MOVIMENTAZIONE.BOX.eq(pk.getBoxId())).fetchOne();
	}

	/**
	 * Dato un record di Movimentazione, estrapola un oggetto Movimentazione da
	 * esso.
	 * 
	 * @param r record
	 * @return istanza di Movimentazione
	 */
	private static Movimentazione movimFromRecord(MovimentazioneRecord r) {
		StatoMovimentazione stato = StatoMovimentazione.valueOf(r.getStato());
		int qta = r.getQta();
		Ordine ordine = OrderModel.getOrderModelById(r.getOrdine()).getOrdine();
		Prodotto prodotto = ProductModel.getProductModelById(r.getProd())
				.getProdotto();
		Box box = BoxModel.getBoxModelByAddr(r.getBox()).getBox();
		UtenteModel um = UtenteModel.getUtenteModelOf(r.getMagazziniere());
		Utente magazziniere = um == null ? null : um.getUtente();

		return new Movimentazione(stato, qta, ordine, prodotto, box,
				magazziniere);
	}
}