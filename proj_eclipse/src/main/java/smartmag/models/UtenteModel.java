package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.UTENTE;

import java.util.Map;
import java.util.TreeMap;

import org.jooq.Record;

import ingsw_proj_magazzino.db.generated.tables.records.UtenteRecord;
import smartmag.data.TipoUtente;
import smartmag.data.Utente;

/**
 * Modello degli Utenti
 */
public class UtenteModel extends BaseModel {

	/**
	 * Mappa delle istanze dei modelli per implementare pattern Singleton per
	 * ciascun utente (no + istanze modello di uno stesso utente)
	 */
	private static TreeMap<String, UtenteModel> instances;
	static {
		refreshDataFromDb();
	}

	/**
	 * Utente gestito
	 */
	private Utente utente;

	/**
	 * Record del DB. Se null vuol dire che non è salvato a DB.
	 */
	private UtenteRecord record;

	private UtenteModel(Utente u) {
		this(u, fetchUtenteRecordByMatr(u.getMatricola()));
	}

	private UtenteModel(UtenteRecord r) {
		this(utenteFromRecord(r), r);
	}

	private UtenteModel(Utente u, UtenteRecord ur) {

		if (u == null) {
			if (ur == null) {
				throw new IllegalArgumentException("UtenteRecord nullo!");
			}
			u = utenteFromRecord(ur);
		} else if (ur == null) {
			if (u == null || !u.isValid())
				throw new IllegalArgumentException("Utente nullo!");
			ur = fetchUtenteRecordByMatr(u.getMatricola());
		}

		if (instances.containsKey(u.getMatricola()))
			throw new IllegalArgumentException("Modello già creato!");

		this.utente = u;
		this.record = ur;
		instances.put(u.getMatricola(), this);
	}

	/**
	 * Verifica se il record dell'utente è presente o meno
	 */
	public boolean isSavedInDb() {
		refreshFromDb();
		return record != null;
	}

	private void create() {
		if (isSavedInDb())
			throw new RuntimeException("Utente già presente a DB!");
		UtenteRecord r = DSL.newRecord(UTENTE);
		r.setMatricola(utente.getMatricola());
		copyUtenteIntoRecord(utente, r);
		r.store();
		this.record = r;
	}

	/**
	 * Elimina il record nel DB e dalla lista delle istanze modello
	 */
	public void delete() {
		record.delete();
		record = null;
		instances.remove(utente.getMatricola());
		notifyChangeListeners(null);
	}

	/**
	 * Restituisce una copia dell'oggetto Utente legato al modello
	 * 
	 * @return copia dell'Utente
	 */
	public Utente getUtente() {
		return utente.clone();
	}

	/**
	 * Refreshes content of Utente from DB
	 */
	private void refreshFromDb() {
		this.record = fetchUtenteRecordByMatr(utente.getMatricola());
		if (this.record != null) {
			this.utente = utenteFromRecord(this.record);
		}

		notifyChangeListeners(null);
	}

	// Metodi statici

	/**
	 * Recupera da DB il record relativo ad un Utente in base alla sua matricola
	 * 
	 * @param matr matricola utente
	 * @return record
	 */
	private static UtenteRecord fetchUtenteRecordByMatr(String matr) {
		UtenteRecord r = (UtenteRecord) DSL.select().from(UTENTE)
				.where(UTENTE.MATRICOLA.eq(matr)).fetchOne(); // SELECT
		return r;
	}

	/**
	 * Ritorna l'unica istanza (singleton) del modello relativo ad un utente
	 * specifico. Se non è mai stato creato il suo modello e se non è presente
	 * nel DB allora ritorna null.
	 * 
	 * @param matr matricola dell'utente d'interesse
	 * @return Modello trovato o null
	 */
	public static UtenteModel getUtenteModelOf(String matr) {

		if (matr != null && !matr.isBlank()) {
			if (!instances.containsKey(matr))
				return null;
			return instances.get(matr);
		} else
			throw new IllegalArgumentException("Matricola non valida!");
	}

	private static Utente utenteFromRecord(UtenteRecord r) {
		return new Utente(r.getMatricola(), r.getNome(), r.getCognome(),
				r.getPassword(), TipoUtente.parse(r.getRuolo()));
	}

	private static void copyUtenteIntoRecord(Utente u, UtenteRecord r) {
		r.setNome(u.getNome());
		r.setCognome(u.getCognome());
		r.setPassword(u.getPassword());
		r.setRuolo(u.getTipo().getRecordValue());
	}

	/**
	 * Crea un modello di un oggetto utente e lo salva nel DB, a patto che non
	 * sia già stato creato in precedenza.
	 * 
	 * @param u Utente da salvare
	 * @return modello dell'utente appena creato
	 * @throws IllegalArgumentException se è già presente nel DB
	 */
	public static UtenteModel createUtente(Utente u)
			throws IllegalArgumentException {
		UtenteModel m = getUtenteModelOf(u.getMatricola());
		if (m == null)
			m = new UtenteModel(u);
		if (m.isSavedInDb())
			throw new IllegalArgumentException("Utente già registrato");
		m.create();
		notifyChangeListeners(null);
		return m;
	}

	/**
	 * Date le credenziali matricola e password, questo metodo restituisce
	 * l'utente se autenticato correttamente, altrimenti null.
	 * 
	 * @param matricola dell'utente
	 * @param password  dell'utente
	 * @return Utente | null
	 */
	public static Utente login(String matricola, String password) {
		UtenteModel um = getUtenteModelOf(matricola);
		if (um == null)
			return null;
		um.refreshFromDb();
		if (!um.isSavedInDb())
			return null;
		if (!um.utente.getPassword().equals(password))
			return null;
		return um.utente;
	}

	/**
	 * Restituisce una TreeMap di tutti i modelli degli utenti salvati a DB
	 */
	public static TreeMap<String, UtenteModel> getAllUserModels() {
		TreeMap<String, UtenteModel> m = new TreeMap<>();
		instances.forEach((matr, um) -> {
			if (um.record != null)
				m.put(matr, um);
		});
		return m;
	}

	public static void refreshDataFromDb() {
		instances = new TreeMap<String, UtenteModel>();
		Map<String, Record> res = DSL.select().from(UTENTE)
				.fetchMap(UTENTE.MATRICOLA);
		res.forEach((matr, r) -> instances.put(matr,
				new UtenteModel((UtenteRecord) r)));
		notifyChangeListeners(null);
	}
}
