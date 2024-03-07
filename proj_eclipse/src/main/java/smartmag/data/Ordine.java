package smartmag.data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Classe che rappresenta un Ordine di prodotti
 */
public class Ordine implements Comparable<Ordine> {

	private int id;
	private TipoOrdine tipo;
	private StatoOrdine stato;
	private LocalDate dataEmissione;
	private LocalDate dataCompletamento;
	private HashMap<Prodotto, Integer> prodotti;

	public Ordine(int id, TipoOrdine tipo, StatoOrdine stato,
			LocalDate dataEmissione, LocalDate dataCompletamento,
			HashMap<Prodotto, Integer> prodotti) {
		this.id = id;
		this.tipo = tipo;
		this.stato = stato;
		this.dataEmissione = dataEmissione;
		this.dataCompletamento = dataCompletamento;
		this.prodotti = prodotti;
	}

	public Ordine(int id, TipoOrdine tipo, StatoOrdine stato,
			LocalDate dataEmissione, LocalDate dataCompletamento) {
		this(id, tipo, stato, dataEmissione, dataCompletamento, null);

	}

	public Ordine(int id, TipoOrdine tipo, StatoOrdine stato,
			LocalDate dataEmissione) {
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

	public LocalDate getDataEmissione() {
		return dataEmissione;
	}

	public void setDataEmissione(LocalDate dataEmissione) {
		this.dataEmissione = dataEmissione;
	}

	public LocalDate getDataCompletamento() {
		return dataCompletamento;
	}

	public void setDataCompletamento(LocalDate dataCompletamento) {
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
						&& dataCompletamento.compareTo(LocalDate.now()) > 0)
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
				s += "(%d x #%d:%s)".formatted(entry.getValue(),
						entry.getKey().getId(), entry.getKey().getNome());
		}

		if (dataCompletamento != null)
			s += " " + dataCompletamento.toString();

		return s;
	}

	public static void main(String[] args) {
		Ordine o = new Ordine(0, TipoOrdine.IN, StatoOrdine.IN_ATTESA,
				LocalDate.now());
		HashMap<Prodotto, Integer> hm = new HashMap<Prodotto, Integer>();
		hm.put(new Prodotto(12, null, null, 0, 0), 2);
		hm.put(new Prodotto(25, null, null, 0, 0), 24);
		o.setProdotti(hm);
		System.out.println(o.toString());
	}

	@Override
	public int compareTo(Ordine o) {
		return Integer.compare(id, o.getId());
	}

	@SuppressWarnings("unchecked")
	public Ordine clone() {

		Ordine o = new Ordine(id, tipo, stato, dataEmissione, dataCompletamento,
				null);

		if (prodotti != null)
			o.setProdotti((HashMap<Prodotto, Integer>) prodotti.clone());

		return o;
	}

}
