package smartmag;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import smartmag.db.Db;
import smartmag.models.BaseModel;

/**
 * Superclasse astratta per le classi contenenti i casi di test che fa in modo
 * di preparare un DB vuoto apposito per ogni caso di test e di cancellarne il
 * file dopo averli eseguiti tutti.
 */
public abstract class BaseTest {

	/**
	 * Percorso del file DB SQLITE da usare nei casi di test
	 */
	private static final String DB_PATH = "db/testing.sqlite";

	/**
	 * Eseguito prima di ciascun caso di test, imposta un <b>database vuoto</b>
	 * (no record) <b>apposito per i test</b> (diverso da quello di default).
	 */
	@BeforeEach
	void setUpDb() {
		BaseModel.setDifferentDbPath(DB_PATH);
		try {
			Db.getInstance().clearRecords();
		} catch (IOException e) {
			fail(e);
			return;
		}
		postSetUp();
	}

	/**
	 * Metodo eseguito prima di ogni caso di test, subito dopo aver resettato il
	 * DB.
	 */
	protected abstract void postSetUp();

	/**
	 * Dopo l'esecuzione di tutti i casi di test, eliminiamo il file del
	 * database utilizzato
	 */
	@AfterAll
	static void deleteDb() {
		try {
			Db.deleteDbFile();
		} catch (IOException | SQLException e) {
			fail(e);
		}
	}
}
