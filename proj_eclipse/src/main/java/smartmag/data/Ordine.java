package smartmag.data;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Classe che rappresenta un Ordine di prodotti
 */
public class Ordine {

	private int id;
	private TipoOrdine tipo;
	private StatoOrdine stato;
	private Date dataEmissione;
	private Date dataCompletamento;
	private HashMap<Prodotto, Integer> prodotti;

	public Ordine(int id, TipoOrdine tipo, StatoOrdine stato,
			Date dataEmissione, Date dataCompletamento) {
		this.id = id;
		this.tipo = tipo;
		this.stato = stato;
		this.dataEmissione = dataEmissione;
		this.dataCompletamento = dataCompletamento;
	}

	public Ordine(int id, TipoOrdine tipo, StatoOrdine stato,
			Date dataEmissione) {
		this(id, tipo, stato, dataEmissione, null);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TipoOrdine getTipo() {
		return tipo;
	}

	public void setTipo(TipoOrdine tipo) {
		this.tipo = tipo;
	}

	public StatoOrdine getStato() {
		return stato;
	}

	public void setStato(StatoOrdine stato) {
		this.stato = stato;
	}

	public Date getDataEmissione() {
		return dataEmissione;
	}

	public void setDataEmissione(Date dataEmissione) {
		this.dataEmissione = dataEmissione;
	}

	public Date getDataCompletamento() {
		return dataCompletamento;
	}

	public void setDataCompletamento(Date dataCompletamento) {
		this.dataCompletamento = dataCompletamento;
	}

	public HashMap<Prodotto, Integer> getProdotti() {
		return prodotti;
	}

	public void setProdotti(HashMap<Prodotto, Integer> prodotti) {
		this.prodotti = prodotti;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataCompletamento, dataEmissione, id, prodotti,
				stato, tipo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ordine other = (Ordine) obj;
		return Objects.equals(dataCompletamento, other.dataCompletamento)
				&& Objects.equals(dataEmissione, other.dataEmissione)
				&& id == other.id && Objects.equals(prodotti, other.prodotti)
				&& stato == other.stato && tipo == other.tipo;
	}

	/**
	 * Verifica i requisiti di validità di un ordine: Id non negativo; tipo,
	 * stato e data emissione non nulli; data completamento non nulla se ordine
	 * completato; se comprende almeno un prodotto.
	 * 
	 * NON controlla se l'ID è già stato utilizzato o meno da altri ordini
	 * 
	 * @return validità dell'ordine
	 */
	public boolean isValid() {
		if (id < 0 || tipo == null || stato == null || dataEmissione == null
				|| (stato == StatoOrdine.COMPLETATO
						&& dataCompletamento.after(new Date()))
				|| prodotti == null || prodotti.size() < 1)
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (!isValid())
			return super.toString();

		String s = "Ordine #%d: %s [%s] del %s ".formatted(id, tipo.toString(),
				stato.toString(), dataEmissione.toString());
		if (prodotti != null) {
			for (Entry<Prodotto, Integer> entry : prodotti.entrySet())
				s += "(%d x %d)".formatted(entry.getValue(),
						entry.getKey().getId());
		}

		if (dataCompletamento != null)
			s += " " + dataCompletamento.toString();

		return s;
	}

	public static void main(String[] args) {
		Ordine o = new Ordine(0, TipoOrdine.IN, StatoOrdine.IN_ATTESA,
				new Date());
		HashMap<Prodotto, Integer> hm = new HashMap<Prodotto, Integer>();
		hm.put(new Prodotto(12, null, null, 0, 0), 2);
		hm.put(new Prodotto(25, null, null, 0, 0), 24);
		o.setProdotti(hm);
		System.out.println(o.toString());
	}

	public void inserisciProdotto(Prodotto ord, int q) throws IOException {
		if (ord.isValid() && q > 0)
			prodotti.put(ord, q);
		else
			throw new IOException("Prodotto non valido o quantità non <=0 ");
	}
}
