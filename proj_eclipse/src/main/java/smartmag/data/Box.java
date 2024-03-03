package smartmag.data;

import java.util.Objects;

public class Box {

	private String indirizzo;
	private Prodotto prodotto;
	private int quantita;

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
		return "Box [indirizzo=" + indirizzo + ", prodotto=" + prodotto
				+ ", quantità=" + quantita + "]";
	}

	public Boolean isValid() {
		String regexInd = "^[A-Z]+(-([1-9]\\d*|0)){2}$";
		if (indirizzo != null && indirizzo.matches(regexInd) && quantita >= 0) {
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
