package smartmag.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;

public class Db {

	public static final String DB_FILE_PATH = "db/db.sqlite";
	public static final String DB_URL = "jdbc:sqlite:" + DB_FILE_PATH;

	// TODO singleton per la connessione al db

	/**
	 * Si connette al database (se esiste, altrimenti lo crea ex-novo) ed esegue
	 * i comandi SQL contenuti nel file al percorso specificato.
	 * 
	 * @param sqlScriptPath percorso del file contenente i comandi SQL
	 * @throws IOException se si verifica un errore nella lettura del file
	 */
	public static void executeSqlScript(String sqlScriptPath)
			throws IOException {

		Connection conn;
		try {
			// Crea connessione al Db (se non esiste crealo)
			conn = DriverManager.getConnection(DB_URL);

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

}
