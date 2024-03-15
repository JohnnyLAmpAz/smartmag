package smartmag.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class Db {

	public static final String DB_FILE_DEFAULT_PATH = "db/db.sqlite";
	public static final String DB_URL_FISTPART = "jdbc:sqlite:";
	public static final SQLDialect SQL_DIALECT = SQLDialect.SQLITE;
	public static final String SQL_DDL_DB_FILE = "db/config/db_creation.sql";
	public static final String SQL_CLEAR_DB_FILE = "db/config/drop_tables.sql";

	private static Db db;

	private Connection conn;
	private DSLContext dslContext;
	private String dbPath;

	private Db(String dbPath) throws IOException {

		this.dbPath = dbPath;

		// Crea connessione al Db (se non esiste crealo)
		try {
			conn = DriverManager.getConnection(DB_URL_FISTPART + dbPath);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.dslContext = DSL.using(conn, SQL_DIALECT);
		this.dslContext.selectZero(); // Force connection to load
	}

	public static Db getInstance() throws IOException {
		if (db != null)
			return db;
		return getInstance(DB_FILE_DEFAULT_PATH);
	}

	public static Db getInstance(String dbPath) throws IOException {

		// Singleton (nuova istanza se null o percorso diverso)
		if (db == null || !db.dbPath.equals(dbPath)) {

			boolean created = !(new File(dbPath).exists());
			db = new Db(dbPath);

			// Se non esisteva il file del Db, crea il suo schema
			if (created)
				db.executeSqlScript(SQL_DDL_DB_FILE);
		}
		return db;
	}

	/**
	 * Cancella il file del db per intero.
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void deleteDbFile() throws IOException, SQLException {

		// TODO FIX: problema flush su file di jooq?
		try {
			if (db != null && db.conn != null && !db.conn.isClosed())
				db.conn.close();
			File f = FileUtils.getFile(db.dbPath);
			if (f.exists())
				FileUtils.delete(f);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(1);
		}
		db = null;
	}

	/**
	 * Svuota il database dal suo contenuto.
	 * 
	 * @throws IOException
	 */
	public void clearRecords() throws IOException {

		// Cancella tabelle per intero
		executeSqlScript(SQL_CLEAR_DB_FILE);

		// Ricrea tabelle
		executeSqlScript(SQL_DDL_DB_FILE);
	}

	/**
	 * Si connette al database (se esiste, altrimenti lo crea ex-novo) ed esegue
	 * i comandi SQL contenuti nel file al percorso specificato.
	 * 
	 * @param sqlScriptPath percorso del file contenente i comandi SQL
	 * @throws IOException se si verifica un errore nella lettura del file
	 */
	private void executeSqlScript(String sqlScriptPath) throws IOException {

		try (Statement stmt = conn.createStatement()) {

			// Leggi i comandi SQL
			File sqlFile = FileUtils.getFile(sqlScriptPath);
			String[] sqlStmts = FileUtils
					.readFileToString(sqlFile, Charset.defaultCharset())
					.split(";");

			// Preparali
			for (String sql : sqlStmts) {
				if (!sql.isBlank()) {
					stmt.addBatch(sql);
				}
			}

			// Eseguili
			stmt.executeBatch();

			System.out
					.println("SQL Script %s executed successfully on db file %s"
							.formatted(sqlScriptPath, dbPath));

		} catch (SQLException e) {
			// TODO Auto-generated catch block (DB)
			e.printStackTrace();
		}
	}

	/**
	 * Restituisce il DSL context di jOOQ per interfacciarsi al DB.
	 * 
	 * @return DSL
	 */
	public DSLContext getDslContext() {
		return dslContext;
	}

	/**
	 * @return percorso del db
	 */
	public String getPath() {
		return dbPath;
	}

	/**
	 * Esegue lo script SQL DDL
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		getInstance();
	}
}
