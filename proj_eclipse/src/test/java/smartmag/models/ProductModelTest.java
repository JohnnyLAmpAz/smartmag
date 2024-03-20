package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Box;
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
		assertEquals(pm, ProductModel.getProductModelOf(p));
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

	/**
	 * verifica il corretto funzionamento di calcDispTotper qta>0
	 */
	@Test
	public void testCalcDispTot() {
		Prodotto p1 = new Prodotto(3, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(4, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		Box b1 = new Box("A-1-1", 10, p1);
		Box b2 = new Box("A-1-2", 30, p1);
		Box b3 = new Box("A-1-3", 0, p2);
		Box b4 = new Box("A-1-4", 15, p1);
		Box b5 = new Box("A-1-5", 13, p2);
		BoxModel.createBox(b1);
		BoxModel.createBox(b2);
		BoxModel.createBox(b3);
		BoxModel.createBox(b4);
		BoxModel.createBox(b5);
		assertEquals(55, pm1.calcDispTot());
	}

	/**
	 * verifica il corretto funzionamento di calcDispTotper qta=0
	 */
	@Test
	public void testCalcDispTot2() {
		Prodotto p1 = new Prodotto(3, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(4, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		Box b1 = new Box("A-1-1", 10, p1);
		Box b2 = new Box("A-1-2", 30, p1);
		Box b3 = new Box("A-1-3", 0, p2);
		Box b4 = new Box("A-1-4", 15, p1);
		Box b5 = new Box("A-1-5", 0, p2);
		BoxModel.createBox(b1);
		BoxModel.createBox(b2);
		BoxModel.createBox(b3);
		BoxModel.createBox(b4);
		BoxModel.createBox(b5);
		assertEquals(0, pm2.calcDispTot());
	}

	/**
	 * verifica il corretto funzionamento di calcDispTotper prodotto non
	 * presente in nessun box
	 */
	@Test
	public void testCalcDispTot3() {
		Prodotto p1 = new Prodotto(3, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(4, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		Box b1 = new Box("A-1-1", 10, p1);
		Box b2 = new Box("A-1-2", 30, p1);
		Box b4 = new Box("A-1-3", 15, p1);
		BoxModel.createBox(b1);
		BoxModel.createBox(b2);
		BoxModel.createBox(b4);

		assertEquals(0, pm2.calcDispTot());
	}

	/**
	 * controlla il corretto funzionamento di calcDispTotById siaper qta>0 che
	 * per qta=0
	 */
	@Test
	public void testCalcDispById() {
		Prodotto p1 = new Prodotto(3, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(4, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		Box b1 = new Box("A-1-1", 10, p1);
		Box b2 = new Box("A-1-2", 30, p1);
		Box b3 = new Box("A-1-3", 0, p2);
		Box b4 = new Box("A-1-4", 10, p1);
		Box b5 = new Box("A-1-5", 0, p2);
		BoxModel.createBox(b1);
		BoxModel.createBox(b2);
		BoxModel.createBox(b3);
		BoxModel.createBox(b4);
		BoxModel.createBox(b5);
		assertEquals(0, ProductModel.calcDispTotById(4));
		assertEquals(50, ProductModel.calcDispTotById(3));
	}

	/**
	 * testa il calcolo relativo prossimo id disponibile (con prodotti presenti)
	 */
	@Test
	public void testGetNextAvailableId() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(2, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		Prodotto p3 = new Prodotto(3, "newprod4", "descr4", 4, 15);
		Prodotto p4 = new Prodotto(4, "newprod5", "descr5", 6, 5);
		ProductModel pm3 = ProductModel.getProductModelOf(p3);
		assertDoesNotThrow(() -> pm3.create());
		ProductModel pm4 = ProductModel.getProductModelOf(p4);
		assertDoesNotThrow(() -> pm4.create());
		assertEquals(5, ProductModel.getNextAvailableId());
	}

	/**
	 * testa il calcolo relativo prossimo id disponibile (senza prodotti
	 * presenti)
	 */
	@Test
	public void testGetNextAvailableId2() {
		assertEquals(0, ProductModel.getNextAvailableId());
	}

	/**
	 * testa il corretto funzionamento del metodo testUpdateProdotto
	 */
	@Test
	public void testUpdateProdotto() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p1));
		ProductModel pm = ProductModel.getProductModelOf(p1);
		Prodotto p2 = new Prodotto(1, "newprod5", "descr5", 6, 5);
		assertDoesNotThrow(() -> pm.updateProdotto(p2));
		assertEquals(p2, pm.getProdotto());
	}

	/**
	 * testa che il metodo UpdateProdotto lanci un eccezione se si passa un
	 * prodotto=null
	 */
	@Test
	public void testUpdateProdotto2() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p1));
		ProductModel pm = ProductModel.getProductModelOf(p1);
		Prodotto p2 = null;
		assertThrows(NullPointerException.class, () -> pm.updateProdotto(p2));
	}

	/**
	 * testa che il metodo UpdateProdotto lanci un eccezione se si passa un
	 * prodotto non valido
	 */
	@Test
	public void testUpdateProdotto3() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p1));
		ProductModel pm = ProductModel.getProductModelOf(p1);
		Prodotto p2 = new Prodotto(-1, "", "", 2, 4);
		assertThrows(IllegalArgumentException.class,
				() -> pm.updateProdotto(p2));
	}

	/**
	 * verifica che getProductmodelOf generi il modello del prodotto
	 * corettamente
	 */
	@Test
	public void testGetProductModelOf() {
		Prodotto p = new Prodotto(1, "newprod", "descr", 5, 10);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p));
		ProductModel pm = ProductModel.getProductModelOf(p);
		assertEquals(p, pm.getProdotto());
	}

	/**
	 * verifica che getProductmodelOf generi un eccezione se si richiedeil
	 * modello di un prodotto=null
	 */
	@Test
	public void testGetProductModelOf2() {
		Prodotto p = null;
		assertThrows(IllegalArgumentException.class,
				() -> ProductModel.getProductModelOf(p));
	}

	/**
	 * verifica che getProductmodelOf generi un eccezione se si richiede il
	 * modello di un prodotto non valido
	 */
	@Test
	public void testGetProductModelOf3() {
		Prodotto p = new Prodotto(-1, "", "dscr", -5, 10);
		assertThrows(IllegalArgumentException.class,
				() -> ProductModel.getProductModelOf(p));
	}

	/**
	 * verifica il corretto funzionamento del metodo getProductModelById
	 */
	@Test
	public void testGetProductModelById() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p1));
		ProductModel pm = ProductModel.getProductModelOf(p1);
		ProductModel pm2 = ProductModel.getProductModelById(1);
		assertEquals(pm, pm2);
	}

	/**
	 * controll che il metodo getProductModelById generi un eccezione se si
	 * richiede un modello non esistente
	 */
	@Test
	public void testGetProductModelById2() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p1));
		assertEquals(null, ProductModel.getProductModelById(3));
	}

	/**
	 * verifica il corretto funzionamento del metodo getAllProductModels,in
	 * partiolare viene restituita una mappa con i soli Productmodel con record
	 * diverso da null
	 */
	@Test
	public void testGetAllProductModels() {

		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(2, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		Prodotto p3 = new Prodotto(3, "newprod4", "descr4", 4, 15);
		Prodotto p4 = new Prodotto(4, "newprod5", "descr5", 6, 5);
		ProductModel pm3 = ProductModel.getProductModelOf(p3);
		assertDoesNotThrow(() -> pm3.create());
		ProductModel pm4 = ProductModel.getProductModelOf(p4);
		assertDoesNotThrow(() -> pm4.create());
		Prodotto p5 = new Prodotto(5, "newprod4", "descr4", 4, 15);
		ProductModel pm5 = ProductModel.getProductModelOf(p5);
		Prodotto p6 = new Prodotto(6, "newprod5", "descr5", 6, 5);
		ProductModel pm6 = ProductModel.getProductModelOf(p6);
		TreeMap<Integer, ProductModel> tm = new TreeMap<>();
		tm.put(pm1.getProdotto().getId(), pm1);
		tm.put(pm2.getProdotto().getId(), pm2);
		tm.put(pm3.getProdotto().getId(), pm3);
		tm.put(pm4.getProdotto().getId(), pm4);
		assertEquals(true, tm.equals(ProductModel.getAllProductModels()));
	}

	// test2 getALllProductModels con mappa vuota o che sia diversa da una mappa
	// che contiene i null?

	/**
	 * verifica il corretto funzionamento del metodo getAllProducts,in
	 * partiolare viene restituita una mappa con i soli prodotti presenti a
	 * database
	 *
	 */
	@Test
	public void testGetAllProducts() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(2, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		Prodotto p3 = new Prodotto(3, "newprod4", "descr4", 4, 15);
		Prodotto p4 = new Prodotto(4, "newprod5", "descr5", 6, 5);
		ProductModel pm3 = ProductModel.getProductModelOf(p3);
		assertDoesNotThrow(() -> pm3.create());
		ProductModel pm4 = ProductModel.getProductModelOf(p4);
		assertDoesNotThrow(() -> pm4.create());
		Prodotto p5 = new Prodotto(5, "newprod4", "descr4", 4, 15);
		ProductModel pm5 = ProductModel.getProductModelOf(p5);
		Prodotto p6 = new Prodotto(6, "newprod5", "descr5", 6, 5);
		ProductModel pm6 = ProductModel.getProductModelOf(p6);
		ArrayList<Prodotto> tm = new ArrayList<>();
		tm.add(p1);
		tm.add(p2);
		tm.add(p3);
		tm.add(p4);
		assertEquals(true, tm.equals(ProductModel.getAllProducts()));
	}

	// test2 getAlllProducts con mappa vuota o che sia diversa da una mappa
	// che contiene i null?

	/**
	 * verifica il corretto funzionamento del metodo getProdFromId()
	 */
	@Test
	public void testGetProdFromId() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(2, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		assertEquals(p1, ProductModel.getProdottoFromId(1));
		assertEquals(p2, ProductModel.getProdottoFromId(2));
	}

	/**
	 * verifica che il metodo generi un eccezione se il prodotto cercato non
	 * esiste
	 */
	@Test
	public void testGetProdFromId2() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(2, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		assertThrows(IllegalArgumentException.class,
				() -> ProductModel.getProdottoFromId(5));
	}

	/**
	 * verifica corretto funzionamento metodo checkID, sia per risultati veri
	 * che falsi
	 */
	@Test
	public void testCheckId() {
		Prodotto p1 = new Prodotto(1, "newprod4", "descr4", 4, 15);
		Prodotto p2 = new Prodotto(2, "newprod5", "descr5", 6, 5);
		ProductModel pm1 = ProductModel.getProductModelOf(p1);
		assertDoesNotThrow(() -> pm1.create());
		ProductModel pm2 = ProductModel.getProductModelOf(p2);
		assertDoesNotThrow(() -> pm2.create());
		assertEquals(true, ProductModel.checkId(1));
		assertEquals(true, ProductModel.checkId(2));
		assertEquals(false, ProductModel.checkId(4));
		assertEquals(false, ProductModel.checkId(6));
	}

	@Override
	protected void postSetUp() {
		// TODO Auto-generated method stub

	}

}
