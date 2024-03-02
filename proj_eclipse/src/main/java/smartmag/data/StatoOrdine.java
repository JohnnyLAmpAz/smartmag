package smartmag.data;

/**
 * Stato ordine
 */
public enum StatoOrdine {

	IN_ATTESA("IN_ATTESA"), IN_SVOLGIMENTO("IN_SVOLGIMENTO"),
	COMPLETATO("COMPLETATO");

	private final String stato;

	StatoOrdine(String s) {
		this.stato = s;
	}

	@Override
	public String toString() {
		return this.stato;
	}
}
