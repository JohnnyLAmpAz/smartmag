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
	private static final TreeMap<String, UtenteModel> instances = fetchAllUsers();

	private Utente utente;
	private UtenteRecord record;

	private UtenteModel(Utente u) {
		if (u == null || !u.isValid())
			throw new IllegalArgumentException("Utente non valido!");
		this.utente = u;
		this.record = fetchUtenteByMatr(u.getMatricola());
	}

	private UtenteModel(UtenteRecord r) {
		if (r == null)
			throw new IllegalArgumentException("UtenteRecord non valido!");
		this.record = r;
		this.utente = utenteFromRecord(r);
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

	private static TreeMap<String, UtenteModel> fetchAllUsers() {
		TreeMap<String, UtenteModel> map = new TreeMap<String, UtenteModel>();
		Map<String, Record> res = DSL.select().from(UTENTE)
				.fetchMap(UTENTE.MATRICOLA);
		res.forEach(
				(matr, r) -> map.put(matr, new UtenteModel((UtenteRecord) r)));
		return map;
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
				instances.put(matr, um);
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
		UtenteModel m = new UtenteModel(u);
		if (m.isSavedInDb())
			throw new IllegalArgumentException("Utente già registrato");
		m.create();
		instances.put(u.getMatricola(), m);
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

	public static void main(String[] args) {
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
