package smartmag.data;

import smartmag.utils.Tupla;

/**
 * Tupla immutable che contiene l'identificativo di una movimentazione: ID
 * Ordine e indirizzo Box
 */
public class MovimId implements Comparable<MovimId> {

	private Tupla<Integer, String> id;

	public MovimId(Integer ordineId, String boxAddr) {
		id = new Tupla<Integer, String>(ordineId, boxAddr);
	}

	public Integer getOrdineId() {
		return id.x;
	}

	public String getBoxAddr() {
		return id.y;
	}

	@Override
	public int compareTo(MovimId o) {
		int cmpOrdineId = getOrdineId().compareTo(o.getOrdineId());
		if (cmpOrdineId == 0)
			return getBoxAddr().compareTo(o.getBoxAddr());
		return cmpOrdineId;
	}

	@Override
	public String toString() {
		return "[ORDER#%d | BOX@%s]".formatted(getOrdineId(), getBoxAddr());
	}
}
