package smartmag;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

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

	public boolean isValid() {
		if (id < 0 || tipo == null || stato == null || dataEmissione == null
				|| (stato == StatoOrdine.COMPLETATO
						&& dataCompletamento.after(new Date())))
			return false;
		return true;
	}

}
