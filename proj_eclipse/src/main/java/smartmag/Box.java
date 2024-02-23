package smartmag;



public class Box {
	public class Prodotto{}
	
	String indirizzo;
	Prodotto prod;
	int quantità;
		
	public Box(String indirizzo, int quantità, Prodotto prodotto) {
		this.indirizzo = indirizzo;
		this.quantità = quantità;
		this.prod = prodotto;	
	}
	
	
}



