package smartmag.utils;

/**
 * Classe per gestire una tupla di due tipi generici
 * 
 * @param <X>
 * @param <Y>
 */
public class Tupla<X, Y> {
	public final X x;
	public final Y y;

	public Tupla(X x, Y y) {
		this.x = x;
		this.y = y;
	}
}