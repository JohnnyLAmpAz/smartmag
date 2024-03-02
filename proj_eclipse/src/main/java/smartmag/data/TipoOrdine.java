package smartmag.data;

/**
 * Tipo ordine in entrata (rifornimento) o uscita
 */
public enum TipoOrdine {

<<<<<<< Updated upstream
	IN, OUT;
=======
	IN("IN"), OUT("OUT");

	private final String tipo;

	TipoOrdine(String s) {
		this.tipo = s;
	}

	@Override
	public String toString() {
		return this.tipo;
	}
>>>>>>> Stashed changes
}
