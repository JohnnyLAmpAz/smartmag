package smartmag.models;

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
	protected static final DSLContext DSL = Db.getInstance().getDslContext();

}