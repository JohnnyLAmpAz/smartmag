package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLIntegrityConstraintViolationException;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Box;
import smartmag.data.Prodotto;

public class BoxModelTest extends BaseTest {

	private BoxModel b;

	/**
	 * verifica che il costruttore crei un'istanza di BoxModel nel modo corretto
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testCostruttoreConBoxRecord()
			throws SQLIntegrityConstraintViolationException {
		Prodotto p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		ProductModel.createProdotto(p);
		Box box = new Box("A-1-1", 10, p);
		b = BoxModel.getBoxModel(box);
		assertNotNull(b);
		assertEquals("A-1-1", b.getBox().getIndirizzo());
	}

	/**
	 * verifica che il metodo createBox crei in modo corretto il record nel
	 * database e che si generi un eccezione se il record é gia presente
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testMetodoCreateBox()
			throws SQLIntegrityConstraintViolationException {
		Prodotto p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		ProductModel.createProdotto(p);
		Box box = new Box("A-1-1", 10, p);
		b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.createBox(b.getBox()));
	}

	/**
	 * verifica il corretto funzionamento del metodo calcDisponibilita
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testMetodoCalcDisponibilitá()
			throws SQLIntegrityConstraintViolationException {
		Prodotto p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		ProductModel.createProdotto(p);
		Box box = new Box("A-1-1", 15, p);
		b = BoxModel.getBoxModel(box);
		assertEquals(15, b.calcDisponibilita());
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
		Prodotto p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		ProductModel.createProdotto(p);
		Box box = new Box("A-1-1", 0, p);
		BoxModel.createBox(box);
		b = BoxModel.getBoxModel(box);
		Prodotto p2 = new Prodotto(2, "newprod", "newdescr", 4, 10);
		ProductModel.createProdotto(p2);
		b.cambiaProdotto(p2);
		assertEquals(p2, b.getBox().getProd());
	}

	/**
	 * verifica il funzionamento del metodo rifornisci
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testRifornisci()
			throws SQLIntegrityConstraintViolationException {
		Prodotto p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		ProductModel.createProdotto(p);
		Box box = new Box("A-1-1", 10, p);
		b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.rifornisci(5);
		assertEquals(15, b.getBox().getQuantità());
	}

	/**
	 * verifica il funzionamento del metodo preleva
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testPreleva() throws SQLIntegrityConstraintViolationException {
		Prodotto p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		ProductModel.createProdotto(p);
		Box box = new Box("A-1-1", 10, p);
		b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.preleva(5);
		assertEquals(10, b.getBox().getQuantità());
	}

	/**
	 * verifica il funzionamento del metodo setQuantita
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 */
	@Test
	public void testSetQuantita()
			throws SQLIntegrityConstraintViolationException {
		Prodotto p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		ProductModel.createProdotto(p);
		Box box = new Box("A-1-1", 10, p);
		b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.setQuantita(30);
		assertEquals(30, b.getBox().getQuantità());
	}

}
