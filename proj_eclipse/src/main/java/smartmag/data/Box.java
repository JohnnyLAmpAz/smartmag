package smartmag.data;

import java.util.Objects;

import smartmag.models.BoxModel;

public class Box {

	private String indirizzo;
	private Prodotto prodotto;
	private int quantita;

	/**
	 * Indica se la stringa passata è indirizzo di box valido o meno.
	 * 
	 * @param addr Indirizzo da valutare
	 * @return true se valido, false se altrimenti.
	 */
	public static boolean validateAddress(String addr) {

		// Regular expression pattern per l'indirizzo
		String regexInd = "^[A-Z]+(-([1-9]\\d*|0)){2}$";
		return addr.matches(regexInd);
	}

	public Box(String indirizzo, int quantità, Prodotto prodotto) {
		this.indirizzo = indirizzo;
		this.quantita = quantità;
		this.prodotto = prodotto;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public Prodotto getProd() {
		return prodotto;
	}

	public void setProd(Prodotto prod) {
		this.prodotto = prod;
	}

	public int getQuantità() {
		return quantita;
	}

	public void setQuantità(int quantita) {
		this.quantita = quantita;
	}

	@Override
	public int hashCode() {
		return Objects.hash(indirizzo, prodotto, quantita);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Box other = (Box) obj;
		return Objects.equals(indirizzo, other.indirizzo)
				&& Objects.equals(prodotto, other.prodotto)
				&& quantita == other.quantita;
	}

	@Override
	public String toString() {
		int disponQta = BoxModel.getBoxModel(this).calcDisponibilita();
		return "Box [indirizzo=" + indirizzo + ", prodotto=" + prodotto
				+ ", quantità=" + quantita + ", disponibilità=" + disponQta
				+ "]";
	}

	public Boolean isValid() {

		if (indirizzo != null && validateAddress(indirizzo) && quantita >= 0) {
			if (prodotto != null && prodotto.isValid())
				return true;
			if (prodotto == null && quantita == 0)
				return true;
		}
		return false;
	}

	public Box clone() {
		Box b = new Box(this.indirizzo, this.quantita, this.prodotto.clone());
		return b;
	}
}
