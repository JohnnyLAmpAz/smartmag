package smartmag.data;

import java.util.Objects;

/**
 * Classe contenitore dei dati di una movimentazione.
 */
public class Movimentazione {

	/**
	 * Costante di testo che rappresenta la zona di carico/scarico
	 */
	public static final String ZSC = "C/S";

	private StatoMovimentazione stato;
	private int quantità;
	private Ordine ordine;
	private Prodotto prodotto;
	private Box box;
	private Utente magazziniere;

	public Movimentazione(StatoMovimentazione stato, int quantità,
			Ordine ordine, Prodotto prodotto, Box box) {
		super();
		this.stato = stato;
		this.quantità = quantità;
		this.ordine = ordine;
		this.prodotto = prodotto;
		this.box = box;
		this.magazziniere = null;
	}

	public Movimentazione(StatoMovimentazione stato, int quantità,
			Ordine ordine, Prodotto prodotto, Box box, Utente magazziniere) {
		this(stato, quantità, ordine, prodotto, box);
		this.magazziniere = magazziniere;
	}

	public StatoMovimentazione getStato() {
		return stato;
	}

	public void setStato(StatoMovimentazione stato) {
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
		return "Movimentazione [stato=" + stato + ", quantità=" + quantità
				+ ", ordine=" + ordine + ", prodotto=" + prodotto + ", box="
				+ box + ", magazziniere=" + magazziniere + ", origine="
				+ getOrigine() + ", destinazione=" + getDestinazione() + "]";
	}

	public Boolean isValid() {
		if (quantità < 0 || stato == null || ordine == null || prodotto == null
				|| box == null || magazziniere == null
				|| !magazziniere.isValid() || !box.isValid()
				|| !prodotto.isValid() || !ordine.isValid()
				|| (magazziniere.getTipo() != TipoUtente.MAGAZZINIERE
						&& magazziniere.getTipo() != TipoUtente.QUALIFICATO))
			return false;
		return true;
	}

	public String getDestinazione() {
		if (ordine.getTipo() == TipoOrdine.IN)
			return box.getIndirizzo();
		else
			return ZSC;
	}

	public String getOrigine() {
		if (ordine.getTipo() == TipoOrdine.IN)
			return ZSC;
		else
			return box.getIndirizzo();
	}

}
