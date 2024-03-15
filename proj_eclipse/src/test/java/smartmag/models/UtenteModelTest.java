package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.TipoUtente;
import smartmag.data.Utente;

class UtenteModelTest extends BaseTest {

	/**
	 * Testa il login degli utenti
	 */
	@Test
	void testLogin() {

		UtenteModel.createUtente(new Utente("ASD", "ASD", "ASD", "ASD",
				TipoUtente.MAGAZZINIERE));

		Utente u = UtenteModel.login("ASD", "ASD");
		assertTrue(u != null);
		assertEquals(u, UtenteModel.getUtenteModelOf("ASD").getUtente());

		// TODO molti altri casi! questo è una prova...
	}

}
