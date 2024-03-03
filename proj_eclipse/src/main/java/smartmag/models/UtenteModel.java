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
	private static final TreeMap<String, UtenteModel> instances;
	static {
		instances = new TreeMap<String, UtenteModel>();
		Map<String, Record> res = DSL.select().from(UTENTE)
				.fetchMap(UTENTE.MATRICOLA);
		res.forEach((matr, r) -> instances.put(matr,
				new UtenteModel((UtenteRecord) r)));
	}

	private Utente utente;
	private UtenteRecord record;

	private UtenteModel(Utente u) {
		this(u, fetchUtenteByMatr(u.getMatricola()));
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
			ur = fetchUtenteByMatr(u.getMatricola());
		}

		if (instances.containsKey(u.getMatricola()))
			throw new IllegalArgumentException("Modello già creato!");

		this.utente = u;
		this.record = ur;
		instances.put(u.getMatricola(), this);
	}

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

	public void delete() {
		record.delete();
	}

	/**
	 * Restituisce una copia dell'oggetto Utente legato al record
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
		this.record = fetchUtenteByMatr(utente.getMatricola());
		if (this.record != null) {
			this.utente = utenteFromRecord(this.record);
		}

		// TODO: event
	}

	// Metodi statici

	/**
	 * Recupera da DB il record relativo ad un Utente in base alla sua matricola
	 * 
	 * @param matr matricola utente
	 * @return record
	 */
	private static UtenteRecord fetchUtenteByMatr(String matr) {
		UtenteRecord r = (UtenteRecord) DSL.select().from(UTENTE)
				.where(UTENTE.MATRICOLA.eq(matr)).fetchOne(); // SELECT
		return r;
	}

	/**
	 * Ritorna l'unica istanza (singleton) del modello relativo ad un utente
	 * specifico
	 * 
	 * @param matr Utente
	 * @return Modello
	 */
	public static UtenteModel getUtenteModelOf(String matr) {

		if (matr != null && !matr.isBlank()) {
			if (!instances.containsKey(matr)) {
				UtenteRecord r = fetchUtenteByMatr(matr);
				if (r == null)
					return null;
				UtenteModel um = new UtenteModel(r);
				return um;
			} else {
				return instances.get(matr);
			}
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

	public static UtenteModel createUtente(Utente u)
			throws IllegalArgumentException {
		UtenteModel m = getUtenteModelOf(u.getMatricola());
		if (m == null)
			m = new UtenteModel(u);
		if (m.isSavedInDb())
			throw new IllegalArgumentException("Utente già registrato");
		m.create();
		return m;
	}

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
	 * Restituisce una TreeMap di tutti i modelli degli utenti come copia di
	 * instances
	 */
	@SuppressWarnings("unchecked")
	public static TreeMap<String, UtenteModel> getAllUserModels() {
		return (TreeMap<String, UtenteModel>) instances.clone();
	}

	public static void main(String[] args) {

		BaseModel.setDifferentDbPath("db/test.sqlite");

		UtenteModel.getAllUserModels()
				.forEach((matr, um) -> System.out.println(um.getUtente()));

		System.out.println(login("m.rossi", "1234"));
		UtenteModel m = UtenteModel.getUtenteModelOf("m.rossi");
		if (m != null)
			System.out.println(m.utente);
		m = UtenteModel.getUtenteModelOf("l.verdi");
		if (m != null)
			m.delete();
		m = UtenteModel.createUtente(new Utente("l.verdi", "Luigi", "Verdi",
				"1234", TipoUtente.RESPONSABILE));
		System.out.println(m == UtenteModel.getUtenteModelOf("l.verdi"));

	}
}
