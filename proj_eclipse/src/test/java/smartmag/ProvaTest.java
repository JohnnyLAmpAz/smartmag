package smartmag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProvaTest {

	@Test
	void testGetProjName() {
		assertEquals("SmartMag", Prova.getProjName());
	}
}
