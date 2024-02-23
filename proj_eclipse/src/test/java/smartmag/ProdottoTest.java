package smartmag;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProdottoTest {

	@Test
	void testIsValid() {

		Prodotto p;

		// Id negativo
		p = new Prodotto(-1, "NOME", "DESCR", 1.5f, 10);
		assertFalse(p.isValid());

		// Nome vuoto o assente
		p = new Prodotto(0, "\n \t", "DESCR", 1.5f, 10);
		assertFalse(p.isValid());
		p = new Prodotto(1, null, "DESCR", 1.5f, 10);
		assertFalse(p.isValid());

		// Soglia negativa
		p = new Prodotto(2, "NOME", "DESCR", 1.5f, -1);
		assertFalse(p.isValid());

		// Accettabili
		p = new Prodotto(0, "NOME", null, 0f, 0);
		assertTrue(p.isValid());
		p = new Prodotto(1, "NOME", "DESCR", 0f, 10);
		assertTrue(p.isValid());
		p = new Prodotto(2, "NOME", "DESCR", 10.934f, 0);
		assertTrue(p.isValid());
	}

}
