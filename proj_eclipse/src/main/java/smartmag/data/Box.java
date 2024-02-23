package smartmag.data;

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

	public Boolean isValid() {
		String[] parti = indirizzo.split("-");
		if (parti.length == 3 && parti[0].matches("[a-zA-Z]+") && parti[1].matches("\\d+") && parti[2].matches("\\d+")) 
			if ( this.quantità > 0 && this.prodotto != null )
				return true;
		return false;
	}

}



