package smartmag.models;

import static ingsw_proj_magazzino.db.generated.Tables.BOX;
import static ingsw_proj_magazzino.db.generated.Tables.PRODOTTO;

import java.util.HashMap;

import ingsw_proj_magazzino.db.generated.tables.records.BoxRecord;
import ingsw_proj_magazzino.db.generated.tables.records.ProdottoRecord;
import smartmag.data.Box;
import smartmag.data.Prodotto;

public class BoxModel extends BaseModel {

	private static HashMap<String, BoxModel> instances = new HashMap<String, BoxModel>();
	private Box box;
	private BoxRecord record;

	public static BoxModel getBoxModel(Box b) {
		if (b != null && b.isValid()) {
			if (!instances.containsKey(b.getIndirizzo())) {
				if ((DSL.select().from(PRODOTTO)
						.where(PRODOTTO.ID.eq(b.getProd().getId()))) != null) {
					BoxModel bm = new BoxModel(b);
					instances.put(b.getIndirizzo(), bm);
					return bm;
				} else {
					throw new IllegalArgumentException(
							"prodotto non esistente");
				}

			} else {
				return instances.get(b.getIndirizzo());
			}
		} else
			throw new IllegalArgumentException("box non valido");
	}

	// costruttore
	private BoxModel(Box b) {
		this.record = fetchBoxByIndirizzo(b.getIndirizzo());
		this.box = b;
	}

	public boolean isSavedInDb() {
		refreshFromDb();
		return record != null;
	}

	protected Box getBox() {
		return box;
	}

	// metodo per settare il box
	protected void setBox(Box b) {
		if (b != null && b.isValid() && (DSL.select().from(PRODOTTO)
				.where(PRODOTTO.ID.eq(b.getProd().getId())) != null)) {
			this.box = b;
		} else
			throw new IllegalArgumentException("box o prodotto non valido");

	}

	public BoxRecord getRecord() {
		return record;
	}

	public void createBox() {
		BoxRecord r = DSL.newRecord(BOX);
		r.setId(box.getIndirizzo());
		copyBoxIntoRecord(box, r);
		r.store();
		this.record = r;
	}

	private void refreshFromDb() {
		this.record = fetchBoxByIndirizzo(box.getIndirizzo());
		if (this.record != null) {
			Box b = boxFromRecord(this.record);
			this.box.setIndirizzo(b.getIndirizzo());
			this.box.setQuantità(b.getQuantità());
			this.box.setProd(b.getProd());
		}

		// TODO: event
	}

	// metodo che permette di cambiare il prodotto di un box solo se il prodotto
	// é effettivamente finito e il nuovo prodotto esiste nel db
	public void cambiaProdotto(Prodotto p) {

		if (box != null && box.isValid() && box.getQuantità() == 0) {
			if (p != null && p.isValid() && (DSL.select().from(PRODOTTO) // forse
																			// basta
																			// solo
																			// ultimo
																			// check
																			// sul
																			// prodotto
					.where(PRODOTTO.ID.eq(p.getId()))) != null) {
				box.setProd(p);
			} else
				throw new IllegalArgumentException("prodotto non valido");
		} else
			throw new IllegalArgumentException(
					"attenzione: il box deve essere valido e vuoto");
	}

	public void rifornisci(int quantita) {

		int qi = this.box.getQuantità();
		this.box.setQuantità(qi + quantita);
		record.setQta(box.getQuantità());
		record.update();
	}

	public void preleva(int qta) {
		if (box.getQuantità() > qta) {
			box.setQuantità(this.box.getQuantità() - qta);
			record.setQta(box.getQuantità());
			record.update();
		} else {
			throw new IllegalArgumentException(
					"qta richiesta maggiore di quella disponibile");
		}
	}

	public void setQuantita(int qta) {
		this.box.setQuantità(qta);
		record.setQta(box.getQuantità());
		record.update();
	}

	// metodi statici

	private static BoxRecord fetchBoxByIndirizzo(String indirizzo) {
		BoxRecord r = (BoxRecord) DSL.select().from(BOX)
				.where(BOX.ID.eq(indirizzo)).fetchOne(); // SELECT
		return r;
	}

	private static Box boxFromRecord(BoxRecord r) {
		if (r == null)
			return null;

		String indirizzo = r.getId();
		int IDprodotto = r.getProdotto();
		int qta = r.getQta();
		ProdottoRecord pr = (ProdottoRecord) DSL.select().from(PRODOTTO)
				.where(PRODOTTO.ID.eq(IDprodotto));
		Prodotto p = ProductModel.prodottoFromRecord(pr);
		return new Box(indirizzo, qta, p);
	}

	private static void copyBoxIntoRecord(Box b, BoxRecord r) {
		if (DSL.select().from(PRODOTTO)
				.where(PRODOTTO.ID.eq(b.getProd().getId())) != null) {
			r.setProdotto(b.getProd().getId());
			r.setQta(b.getQuantità());
		} else
			throw new IllegalArgumentException(
					"prodotto non presente nel database");

	}

}
