package smartmag.data;

import java.util.Objects;

public class Box {

	private String indirizzo;
	private Prodotto prodotto;
	private int quantità;

	public Box(String indirizzo, int quantità, Prodotto prodotto) {
		this.indirizzo = indirizzo;
		this.quantità = quantità;
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
		return quantità;
	}

	public void setQuantità(int quantità) {
		this.quantità = quantità;
	}

	@Override
	public int hashCode() {
		return Objects.hash(indirizzo, prodotto, quantità);
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
				&& quantità == other.quantità;
	}

	@Override
	public String toString() {
		return "Box [indirizzo=" + indirizzo + ", prodotto=" + prodotto
				+ ", quantità=" + quantità + "]";
	}

	public Boolean isValid() {
		String[] parti = indirizzo.split("-");
		if (parti.length == 3 && parti[0].matches("[a-zA-Z]+")
				&& parti[1].matches("\\d+") && parti[2].matches("\\d+"))
			if (this.quantità > 0 && this.prodotto != null)
				return true;
		return false;
	}
}
