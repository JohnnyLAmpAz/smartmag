package smartmag.data;

/**
 * Stato ordine
 */
public enum StatoOrdine {

	IN_ATTESA, IN_SVOLGIMENTO, COMPLETATO;

	@Override
	public String toString() {
		switch (this) {
		case IN_ATTESA:
			return "IN_ATTESA";
		case IN_SVOLGIMENTO:
			return "IN_SVOLGIMENTO";
		case COMPLETATO:
			return "COMPLETATO";
		default:
			throw new IllegalArgumentException();
		}
	}

}
