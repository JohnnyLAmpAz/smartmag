package smartmag.db.utils;

import org.jooq.tools.StringUtils;

public abstract class StrUtils {

	/**
	 * Tronca una stringa alla lunghezza specificata sostituendo tabulazioni e
	 * LF con spazi. Se la lunghezza risulta maggiore di quella effettiva della
	 * stringa allora viene restituita l'intera stringa fornita.
	 * 
	 * @param str Stringa da troncare
	 * @param len Lunghezza troncamento
	 */
	public static String shortenStr(String str, int len) {
		str = str.replaceAll("\\s+", " ").strip();
		return StringUtils.abbreviate(str, len);
	}

}
