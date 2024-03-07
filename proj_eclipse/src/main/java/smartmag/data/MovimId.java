package smartmag.data;

import smartmag.utils.Tupla;

/**
 * Tupla immutable che contiene l'identificativo di una movimentazione: ID
 * Ordine e ID Box
 */
public class MovimId implements Comparable<MovimId> {

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

	@Override
	public int compareTo(MovimId o) {
		int cmpOrdineId = getOrdineId().compareTo(o.getOrdineId());
		if (cmpOrdineId == 0)
			return getBoxId().compareTo(o.getBoxId());
		return cmpOrdineId;
	}
}
