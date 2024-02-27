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

	private Connection conn;
	private DSLContext dslContext;

	public static final String DB_FILE_PATH = "db/db.sqlite";
	public static final String DB_URL_FISTPART = "jdbc:sqlite:";
	public static final SQLDialect SQL_DIALECT = SQLDialect.SQLITE;
	public static final String SQL_DDL_DB_FILE = "db/config/db_creation.sql";

	private static Db db;

	private Db(String dbPath) throws IOException {

		// Crea connessione al Db (se non esiste crealo)
		try {
			conn = DriverManager.getConnection(DB_URL_FISTPART + dbPath);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dslContext = DSL.using(conn, SQL_DIALECT);

		// Se non esiste il file del Db creane uno nuovo e il suo schema
		if (!(new File(dbPath).exists())) {
			executeSqlScript(SQL_DDL_DB_FILE);
		}
	}

	public static Db getInstance() throws IOException {
		return getInstance(DB_FILE_PATH);
	}

	public static Db getInstance(String dbPath) throws IOException {
		if (db == null)
			return new Db(dbPath);
		return db;
	}

	/**
	 * Si connette al database (se esiste, altrimenti lo crea ex-novo) ed esegue
	 * i comandi SQL contenuti nel file al percorso specificato.
	 * 
	 * @param sqlScriptPath percorso del file contenente i comandi SQL
	 * @throws IOException se si verifica un errore nella lettura del file
	 */
	public void executeSqlScript(String sqlScriptPath) throws IOException {

		Connection conn;
		try {
			conn = getInstance().getConn();

			Statement stmt = conn.createStatement();

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

			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block (DB)
			e.printStackTrace();
		}
	}

	public Connection getConn() {
		return conn;
	}

	public DSLContext getDslContext() {
		return dslContext;
	}
}
