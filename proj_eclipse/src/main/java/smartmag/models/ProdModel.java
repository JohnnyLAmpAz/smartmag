package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTO;

import java.util.ArrayList;

import static org.jooq.impl.DSL.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import ingsw_proj_magazzino.db.generated.tables.records.ProdottoRecord;
import smartmag.Prodotto;
import smartmag.db.Db;

public class ProdModel {

	private DSLContext dsl;

	public ProdModel() {
		this.dsl = Db.getInstance().getDslContext();
	}

	public ArrayList<Prodotto> getProdotti() {
		Result<Record> result = dsl.select().from(PRODOTTO).fetch();

		ArrayList<Prodotto> pp = new ArrayList<Prodotto>();
		for (Record r : result) {
			pp.add(prodottoFromRecord(r));
		}
		return pp;
	}

	public Prodotto getProdottoById(int id) {
		Record r = fetchProdottoById(id);
		return prodottoFromRecord(r);
	}

	public void createProdotto(Prodotto p) {
		ProdottoRecord r = dsl.newRecord(PRODOTTO);
		r.setId(p.getId());
		copyProdottoIntoRecord(p, r);
		r.store(); // INSERT
	}

	public void deleteProdotto(Prodotto p) {
		ProdottoRecord r = (ProdottoRecord) fetchProdottoById(p.getId());
		r.delete(); // DELETE con UpdatableRecord
	}

	public void updateProdotto(Prodotto p) {
		ProdottoRecord r = (ProdottoRecord) fetchProdottoById(p.getId());
		copyProdottoIntoRecord(p, r);
		r.store(); // UPDATE con UpdatableRecord
	}

	private Record fetchProdottoById(int id) {
		Record r = dsl.select().from(PRODOTTO).where(PRODOTTO.ID.eq(id))
				.fetchOne(); // SELECT
		return r;
	}

	public int getNextAvailableId() {
		Integer max = dsl.select(max(PRODOTTO.ID)).from(PRODOTTO).fetchOne()
				.value1();
		if (max == null)
			return 0;
		return max + 1;
	}

	private static Prodotto prodottoFromRecord(Record r) {
		if (r == null)
			return null;

		Integer id = r.getValue(PRODOTTO.ID);
		String nome = r.getValue(PRODOTTO.NOME);
		String descr = r.getValue(PRODOTTO.DESCRIZIONE);
		Float peso = r.getValue(PRODOTTO.PESO);
		Integer soglia = r.getValue(PRODOTTO.SOGLIA);
		return new Prodotto(id, nome, descr, peso, soglia);
	}

	private static void copyProdottoIntoRecord(Prodotto p, ProdottoRecord r) {
		r.setNome(p.getNome());
		r.setDescrizione(p.getDescr());
		r.setPeso(p.getPeso());
		r.setSoglia(p.getSoglia());
	}
}
