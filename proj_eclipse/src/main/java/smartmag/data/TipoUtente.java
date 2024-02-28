package smartmag.data;

/**
 * Rappresenta i tipi di utenti presenti in magazzino
 */

//("MAGAZZ", "MAGAZZ_QUAL", "RESP_ORDINI", "MANAGER")),

public enum TipoUtente {

	MAGAZZINIERE("MAGAZZ"), QUALIFICATO("MAGAZZ_QUAL"), MANAGER("MANAGER"),
	RESPONSABILE("RESP_ORDINI");

	private TipoUtente(String recordValue) {
		this.recordValue = recordValue;
	}

	private String recordValue;

	public String getRecordValue() {
		return recordValue;
	}

	public static TipoUtente parse(String s) {
		return switch (s) {
			case "MAGAZZ":
				yield TipoUtente.MAGAZZINIERE;
			case "MAGAZZ_QUAL":
				yield TipoUtente.QUALIFICATO;
			case "MANAGER":
				yield TipoUtente.MANAGER;
			case "RESP_ORDINI":
				yield TipoUtente.RESPONSABILE;
			default:
				yield null;
		};
	}
}
