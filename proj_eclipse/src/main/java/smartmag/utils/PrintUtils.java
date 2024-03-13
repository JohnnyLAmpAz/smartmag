package smartmag.utils;

import java.util.Map;
import java.util.TreeMap;

import smartmag.data.MovimId;
import smartmag.data.Movimentazione;
import smartmag.models.MovimenModel;

public abstract class PrintUtils {

	/**
	 * Stampa sullo standard output una mappa di modelli movimentazioni
	 * 
	 * @param map mappa modelli movim
	 */
	public static void printMovimsMap(TreeMap<MovimId, MovimenModel> map) {
		for (Map.Entry<MovimId, MovimenModel> entry : map.entrySet()) {
			MovimId pk = entry.getKey();
			MovimenModel mm = entry.getValue();
			Movimentazione m = mm.getMovim();
			System.out.println(m);
		}
	}

}
