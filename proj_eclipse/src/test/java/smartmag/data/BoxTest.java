package smartmag.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class BoxTest {

	@Test
	void testIsValid() {

		ArrayList<Box> bb = new ArrayList<>();

		Prodotto p = new Prodotto(1, "ASD", null, 0, 0);

		// Prod non valido
		bb.add(new Box("AB-12-3", 0, new Prodotto(0, null, null, 0, 0)));
		bb.add(new Box("AB-12-3", 1, new Prodotto(0, null, null, 0, 0)));
		bb.add(new Box("AB-12-3", 14, null));

		bb.add(new Box(null, 0, p));
		bb.add(new Box("       ", 0, p));
		bb.add(new Box("AB", 0, p));
		bb.add(new Box("AB-12", 0, p));
		bb.add(new Box("aB-12-3", 0, p));
		bb.add(new Box("aB-12-3", 0, p));
		bb.add(new Box("AB-012-3", 0, p));
		bb.add(new Box("AB-12-03", 0, p));
		bb.add(new Box("AB-12-00", 0, p));
		bb.add(new Box("AB-12-3", -1, p));
		bb.add(new Box("AB-12-3 ", 0, null));
		bb.add(new Box("AB-12-3", 14, null));

		for (Box b : bb)
			assertFalse(b.isValid());

		bb.clear();

		bb.add(new Box("A-0-0", 0, null));
		bb.add(new Box("A-0-1", 1, p));
		bb.add(new Box("BF-12-30", 32, p));
		bb.add(new Box("AB-0-3", 0, null));
		bb.add(new Box("AB-20-3", 14, p));
		bb.add(new Box("ABC-41-3", 0, null));

		for (Box b : bb)
			assertTrue(b.isValid());
	}

}
