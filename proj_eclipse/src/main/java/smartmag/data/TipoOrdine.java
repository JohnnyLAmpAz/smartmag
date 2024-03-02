package smartmag.data;

/**
 * Tipo ordine in entrata (rifornimento) o uscita
 */
public enum TipoOrdine {

	IN, OUT;

	@Override
	public String toString() {
		switch (this) {
		case IN:
			return "IN";
		case OUT:
			return "OUT";
		default:
			throw new IllegalArgumentException();
		}
	}
}
