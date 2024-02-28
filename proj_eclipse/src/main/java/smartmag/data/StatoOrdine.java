package smartmag.data;

/**
 * Stato ordine
 */
public enum StatoOrdine {

	IN_ATTESA("in_attesa"), IN_SVOLGIMENTO("in_svolgimento"),
	COMPLETATO("completato");

	private final String stato;

	StatoOrdine(String s) {
		this.stato = s;
	}

	@Override
	public String toString() {
		return this.stato;
	}
}
