package smartmag.models;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jooq.DSLContext;

import smartmag.db.Db;

/**
 * Classe astratta di un modello (pattern MVC) che si interfaccia al DB tramite
 * DSLContext di jOOQ, disponibile come costante statica.
 */
public abstract class BaseModel {

	/**
	 * DSL di jOOQ per interagire col DB.
	 */
	protected static DSLContext DSL;
	static {
		try {
			DSL = Db.getInstance().getDslContext();
		} catch (IOException e) {
			throw new Error("Error occurred while reading DB file");
		}
	}

	/**
	 * Ottiene una nuova connessione al DB sqlite al persorso specificato
	 * 
	 * @param dbPath percorso del file sqlite
	 */
	public static void setDifferentDbPath(String dbPath) {
		try {

			// Nuova istanza Db su file diverso. Utile per testing
			DSL = Db.getInstance(dbPath).getDslContext();
		} catch (IOException e) {
			throw new Error("Error occurred while reading DB file");
		}
	}

	/**
	 * Lista di ChangeListeners che verranno notificati con
	 * notifyChangeListeners
	 */
	protected static ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

	/**
	 * Aggiunge un ChangeListeners
	 * 
	 * @param cl listener
	 */
	public static void addChangeListener(ChangeListener cl) {
		listeners.add(cl);
	}

	/**
	 * Rimuove un ChangeListener
	 * 
	 * @param cl listener
	 */
	public static void removeChangeListener(ChangeListener cl) {
		listeners.remove(cl);
	}

	/**
	 * Notifica tutti i ChangeListeners registrati. Si noti come i listener si
	 * registrano alla classe del modello stessa (lista statica); ciò perché un
	 * evento dovrebbe essere lanciato per una qualsiasi modifica che riguarda
	 * una <b>qualunque</b> istanza di questo modello. Questo per semplificare
	 * la gestione degli eventi.
	 * 
	 * @param e evento
	 */
	protected static void notifyChangeListeners(ChangeEvent e) {
		for (ChangeListener l : listeners) {
			l.stateChanged(e);
		}
	}

	/**
	 * Aggiorna le istanze gestite recuperando i dati dal database.
	 * 
	 * Da implementare nei modelli effettivi (sottoclassi). L'intenzione era di
	 * metterlo come abstract ma Java non permette metodi static abstract...
	 */
//	public abstract static void refreshDataFromDb() {
//	}
}