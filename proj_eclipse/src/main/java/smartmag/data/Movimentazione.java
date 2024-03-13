package smartmag.data;

import java.util.Objects;

/**
 * Classe contenitore dei dati di una movimentazione.
 */
public class Movimentazione {

	/**
	 * Costante di testo che rappresenta la zona di carico/scarico
	 */
	public static final String ZCS = "C/S";

	private StatoMovim stato;
	private int quantità;
	private Ordine ordine;
	private Prodotto prodotto;
	private Box box;
	private Utente magazziniere;

	public Movimentazione(StatoMovim stato, int quantità, Ordine ordine,
			Prodotto prodotto, Box box) {
		super();
		this.stato = stato;
		this.quantità = quantità;
		this.ordine = ordine;
		this.prodotto = prodotto;
		this.box = box;
		this.magazziniere = null;
	}

	public Movimentazione(StatoMovim stato, int quantità, Ordine ordine,
			Prodotto prodotto, Box box, Utente magazziniere) {
		this(stato, quantità, ordine, prodotto, box);
		this.magazziniere = magazziniere;
	}

	public StatoMovim getStato() {
		return stato;
	}

	public void setStato(StatoMovim stato) {
		this.stato = stato;
	}

	public int getQuantità() {
		return quantità;
	}

	public void setQuantità(int quantità) {
		this.quantità = quantità;
	}

	public Ordine getOrdine() {
		return ordine;
	}

	public void setOrdine(Ordine ordine) {
		this.ordine = ordine;
	}

	public Prodotto getProdotto() {
		return prodotto;
	}

	public void setProdotto(Prodotto prodotto) {
		this.prodotto = prodotto;
	}

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public Utente getMagazziniere() {
		return magazziniere;
	}

	public void setMagazziniere(Utente magazziniere) {
		this.magazziniere = magazziniere;
	}

	/**
	 * Restituisce un oggetto MovimId che costituisce la chiave primaria della
	 * movimentazione.
	 * 
	 * @return
	 */
	public MovimId getMovimId() {
		if (!isValid())
			return null;
		return new MovimId(ordine.getId(), box.getIndirizzo());
	}

	/**
	 * Indica se i prodotti sono stati prelevati o meno dal magazziniere.
	 * 
	 * @return true se la qta è già stata prelevata dall'origine
	 */
	public boolean isQtaPrelevata() {
		return stato.qtaPrelevata;
	}

	/**
	 * Indica se la movimentazione, in base allo stato in cui si trovo, può
	 * essere annullata o meno.
	 * 
	 * @return true se annullabile, alrimenti false.
	 */
	public boolean isAnnullabile() {
		return !isQtaPrelevata() && stato != StatoMovim.ANNULLATA;
	}

	@Override
	public int hashCode() {
		return Objects.hash(box, magazziniere, ordine, prodotto, quantità,
				stato);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movimentazione other = (Movimentazione) obj;
		return Objects.equals(box, other.box)
				&& Objects.equals(magazziniere, other.magazziniere)
				&& Objects.equals(ordine, other.ordine)
				&& Objects.equals(prodotto, other.prodotto)
				&& quantità == other.quantità && stato == other.stato;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("ORDER#%d ".formatted(ordine.getId()));
		s.append("{" + stato.toString());
		if (magazziniere != null)
			s.append("@" + magazziniere.getMatricola());
		s.append("}");
		s.append(" [");
		s.append(ordine.getTipo() == TipoOrdine.IN ? ZCS : box.getIndirizzo());
		s.append(" -> ");
		s.append(ordine.getTipo() == TipoOrdine.OUT ? ZCS : box.getIndirizzo());
		s.append("]");
		s.append(" %d x PROD#%d_%s".formatted(quantità, prodotto.getId(),
				prodotto.getNome()));

		return s.toString();
	}

	public Boolean isValid() {
		if (quantità <= 0 || stato == null || ordine == null
				|| !ordine.isValid() || prodotto == null || !prodotto.isValid()
				|| box == null || !box.isValid()) {
			if (stato != StatoMovim.NON_ASSEGNATA) {
				if (magazziniere == null || !magazziniere.isValid()
						|| (magazziniere.getTipo() != TipoUtente.MAGAZZINIERE
								&& magazziniere
										.getTipo() != TipoUtente.QUALIFICATO))
					return false;
			}
		}
		return true;
	}

	public String getDestinazione() {
		if (ordine.getTipo() == TipoOrdine.IN)
			return box.getIndirizzo();
		else
			return ZCS;
	}

	public String getOrigine() {
		if (ordine.getTipo() == TipoOrdine.IN)
			return ZCS;
		else
			return box.getIndirizzo();
	}

	@Override
	public Movimentazione clone() {
		Movimentazione m = new Movimentazione(stato, quantità, null, null, null,
				null);
		if (ordine != null)
			m.setOrdine(ordine);
		if (prodotto != null)
			m.setProdotto(prodotto);
		if (box != null)
			m.setBox(box);
		if (magazziniere != null)
			m.setMagazziniere(magazziniere);

		return m;
	}
}
