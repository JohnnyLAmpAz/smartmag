package smartmag.data;

public enum StatoMovim {

	/**
	 * Movimentazione generata dal sistema, non ancora presa in carico da un
	 * magazziniere
	 */
	NON_ASSEGNATA(false),

	/**
	 * Movimentazione presa in carico da un magazziniere, nessun prodotto
	 * spostato
	 */
	PRESA_IN_CARICO(false),

	/**
	 * Prodotti prelevati dalla posizione di origine (BOX se ordine IN,
	 * altrimenti C/S)
	 */
	PRELEVATA(true),

	/**
	 * Movimentazione portata a termine scaricando i prodotti prelevati alla
	 * posizione di destinazione (BOX se ordine OUT, altrimenti C/S)
	 */
	COMPLETATA(true),

	/**
	 * Movimentazione NON effettuata a causa di errore quantità o altri motivi
	 */
	ANNULLATA(false);

	/**
	 * Indica se i prodotti sono stati prelevati o meno dal magazziniere.
	 */
	final boolean qtaPrelevata;

	private StatoMovim(boolean qtaPrelevata) {
		this.qtaPrelevata = qtaPrelevata;
	}
}
