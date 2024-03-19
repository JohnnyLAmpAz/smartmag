package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Prodotto;

public class ProductModelTest extends BaseTest {

	/**
	 * verifica che il modello del prodotto venga creato correttamente
	 * 
	 */
	@Test
	public void testCreateProdotto() {
		Prodotto p = new Prodotto(0, "newprod", "descr", 5, 10);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p));
		ProductModel pm = ProductModel.getProductModelOf(p);
		assertEquals(0, pm.getProdotto().getId());
		assertEquals("newprod", pm.getProdotto().getNome());
		assertEquals("descr", pm.getProdotto().getDescr());
		assertEquals(5, pm.getProdotto().getPeso());
		assertEquals(10, pm.getProdotto().getSoglia());
	}

	/**
	 * verifica che il metodo genera un eccezione se si tenta di creare 2 volte
	 * lo stesso prodotto
	 * 
	 */
	@Test
	public void testCreateProdotto2() {
		Prodotto p = new Prodotto(1, "newprod2", "descr2", 3, 14);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p));
		assertThrows(IllegalArgumentException.class,
				() -> ProductModel.createProdotto(p));
	}

	/**
	 * verificare create se prodotto p=null
	 */
	@Test
	public void testCreateProdotto3() {

	}

	/**
	 * verifica il corretto funzionamento del metodo setProdotto
	 */
	@Test
	public void testSetProdotto() {
		Prodotto p = new Prodotto(2, "newprod3", "descr3", 4, 15);
		ProductModel pm = ProductModel.getProductModelOf(p);
		Prodotto p2 = new Prodotto(3, "newprod4", "descr4", 5, 16);
		pm.setProdotto(p2);
		assertEquals(p2, pm.getProdotto());
	}

	/**
	 * verifica che il metodo setProdotto genera un eccezione se viene inserito
	 * un prodotto null
	 */
	@Test
	public void testSetProdotto2() {
		Prodotto p = new Prodotto(2, "newprod3", "descr3", 4, 15);
		ProductModel pm = ProductModel.getProductModelOf(p);
		Prodotto p2 = null;
		assertThrows(IllegalArgumentException.class, () -> pm.setProdotto(p2));
	}

	/**
	 * verifica che il metodo setProdotto genera un eccezione se viene inserito
	 * un prodotto non valido
	 */
	@Test
	public void testSetProdotto3() {
		Prodotto p = new Prodotto(2, "newprod3", "descr3", 4, 15);
		ProductModel pm = ProductModel.getProductModelOf(p);
		Prodotto p2 = new Prodotto(-2, "", "", 0, 0);
		assertThrows(IllegalArgumentException.class, () -> pm.setProdotto(p2));
	}

	@Override
	protected void postSetUp() {
		// TODO Auto-generated method stub

	}

}
