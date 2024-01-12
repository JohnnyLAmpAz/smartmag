package ingsw_proj_magazzino.db.utils;

import java.io.IOException;
import java.sql.SQLException;

import ingsw_proj_magazzino.db.Db;

public abstract class CreateDb {
	
	public static final String SQL_DDL_DB_FILE = "db/config/db_creation.sql";

	public static void main(String[] args) throws SQLException, IOException {

		Db.executeSqlScript(SQL_DDL_DB_FILE);
		System.out.println("SQL script " + SQL_DDL_DB_FILE + " executed successfully");
	}
}
