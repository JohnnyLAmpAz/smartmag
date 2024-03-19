package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Box;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;

public class BoxModelTest extends BaseTest {

	private Prodotto p;

	@Override
	protected void postSetUp() {
		p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		try {
			ProductModel.createProdotto(p);
		} catch (SQLIntegrityConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * verifica che il costruttore crei un'istanza di BoxModel nel modo corretto
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testCostruttoreConBoxRecord()
			throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-1-1", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		assertNotNull(b);
		assertEquals("A-1-1", b.getBox().getIndirizzo());
	}

	/**
	 * verifica che il metodo createBox generi un eccezione se il record é gia
	 * presente nel database
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testMetodoCreateBox()
			throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-1-2", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box); // inserisco il box nel db
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.createBox(b.getBox()));
	}

	/**
	 * verifica che il metodo createBox crei correttamente il box nel database
	 * senza generare eccezioni
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testMetodoCreateBox2()
			throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-1-3", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		assertDoesNotThrow(() -> {
			BoxModel.createBox(box);
		});

		assertEquals("A-1-3", b.getBox().getIndirizzo());
		assertEquals(10, b.getBox().getQuantità());
		assertEquals(p.getId(), b.getBox().getProd());

	}

	/**
	 * verifica il corretto funzionamento del metodo calcDisponibilita
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testMetodoCalcDisponibilitá()
			throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-1-4", 15, p);
		OrderModel om = OrderModel.getOrderModelOf(new Ordine(0, TipoOrdine.OUT,
				StatoOrdine.IN_SVOLGIMENTO, LocalDate.now()));
		om.inserisciProdotto(p, 10);
		BoxModel b = BoxModel.getBoxModel(box);
		assertEquals(5, b.calcDisponibilita());
	}

	/**
	 * verifica che il metodo cambiaProdotto cambi effettivamente il prodotto
	 * nel Box
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testCambiaProdotto()
			throws SQLIntegrityConstraintViolationException {

		Box box = new Box("A-1-5", 0, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		Prodotto p2 = new Prodotto(2, "newprod", "newdescr", 4, 10);
		ProductModel.createProdotto(p2);
		assertEquals(0, b.getBox().getQuantità());
		b.cambiaProdotto(p2);
		assertEquals(p2, b.getBox().getProd());
	}

	/**
	 * verifica che il metodo cambiaProdottgeneri un eccezione se il box
	 * contiene gia un prodotto con quantita>0
	 * 
	 * 
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testCambiaProdotto2()
			throws SQLIntegrityConstraintViolationException {

		Box box = new Box("A-2-5", 5, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		Prodotto p2 = new Prodotto(2, "newprod", "newdescr", 4, 10);
		ProductModel.createProdotto(p2);
		assertThrows(IllegalArgumentException.class,
				() -> b.cambiaProdotto(p2));
	}

	/**
	 * verifica che il metodo cambiaProdotto generi un'eccezione se il nuovo
	 * prodotto é null
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testCambiaProdotto3()
			throws SQLIntegrityConstraintViolationException {

		Box box = new Box("A-2-5", 5, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		Prodotto p2 = null;
		ProductModel.createProdotto(p2);
		assertThrows(IllegalArgumentException.class,
				() -> b.cambiaProdotto(p2));
	}

	/**
	 * verifica che il metodo cambiaProdotto generi un'eccezione se il nuovo
	 * prodotto non é valido
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	public void testCambiaProdotto4()
			throws SQLIntegrityConstraintViolationException {

		Box box = new Box("A-2-5", 5, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		Prodotto p2 = new Prodotto(-1, "cambioprod", "descr3", 4, 6);
		ProductModel.createProdotto(p2);
		assertThrows(IllegalArgumentException.class,
				() -> b.cambiaProdotto(p2));
	}

	/**
	 * verifica il funzionamento del metodo rifornisci
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testRifornisci()
			throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-1-6", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.rifornisci(5);
		assertEquals(15, b.getBox().getQuantità());
	}

	/**
	 * verifica che il metodo preleva generi eccezione se si prova a prelevare
	 * una quantita maggiore di quell disponibile
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testPreleva2() throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-2-7", 5, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		assertThrows(IllegalArgumentException.class, () -> b.preleva(10));
	}

	/**
	 * verifica il funzionamento del metodo preleva
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testPreleva() throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-1-7", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.preleva(5);
		assertEquals(5, b.getBox().getQuantità());
	}

	/**
	 * verifica il funzionamento del metodo setQuantita
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testSetQuantita()
			throws SQLIntegrityConstraintViolationException {
		Box box = new Box("A-1-8", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.setQuantita(30);
		assertEquals(30, b.getBox().getQuantità());
	}

}
