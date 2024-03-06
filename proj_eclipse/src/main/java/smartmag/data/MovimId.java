package smartmag.data;

import smartmag.utils.Tupla;

/**
 * Tupla immutable che contiene l'identificativo di una movimentazione: ID
 * Ordine e ID Box
 */
public class MovimId {

	private Tupla<Integer, String> id;

	public MovimId(Integer ordineId, String boxId) {
		id = new Tupla<Integer, String>(ordineId, boxId);
	}

	public Integer getOrdineId() {
		return id.x;
	}

	public String getBoxId() {
		return id.y;
	}
}
