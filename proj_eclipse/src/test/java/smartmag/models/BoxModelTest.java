package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Box;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;

public class BoxModelTest extends BaseTest {

	private Prodotto p;
	private Prodotto p2;
	private Prodotto p3;
	private Prodotto p4;
	private Prodotto p5;
	private Prodotto p6;

	private ArrayList<Prodotto> prods;
	private Ordine ordineOut;

	@Override
	protected void postSetUp() {
		p = new Prodotto(1, "ProdottoTest", "Descrizione", 1, 5);
		p2 = new Prodotto(2, "ProdottoTest2", "Descrizione2", 2, 10);
		p3 = new Prodotto(3, "ProdottoTest3", "Descrizione3", 3, 20);
		p4 = new Prodotto(4, "ProdottoTest4", "Descrizione4", 4, 30);
		p5 = new Prodotto(5, "ProdottoTest5", "Descrizione5", 5, 40);
		p6 = new Prodotto(6, "ProdottoTest6", "Descrizione6", 6, 50);
		try {
			ProductModel.createProdotto(p);
			ProductModel.createProdotto(p2);
			ProductModel.createProdotto(p3);
			ProductModel.createProdotto(p4);
			ProductModel.createProdotto(p5);
			ProductModel.createProdotto(p6);

		} catch (SQLIntegrityConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		prods = new ArrayList<Prodotto>();
		prods.add(p);
		prods.add(p2);
		prods.add(p3);
		HashMap<Prodotto, Integer> listaSpesa = new HashMap<>();
		for (Prodotto prodotto : prods) {
			listaSpesa.put(prodotto, prodotto.getId());
		}
		ordineOut = new Ordine(0, TipoOrdine.OUT, StatoOrdine.IN_ATTESA,
				LocalDate.now());
		ordineOut.setProdotti(listaSpesa);
		try {
			OrderModel.create(ordineOut);
		} catch (SQLIntegrityConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * verifica che il costruttore crei un'istanza di BoxModel nel modo corretto
	 * 
	 */
	@Test
	public void testCostruttoreConBoxRecord() {
		Box box = new Box("A-1-1", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		assertNotNull(b);
		assertEquals("A-1-1", b.getBox().getIndirizzo());
	}

	/**
	 * verifica che il metodo createBox generi un eccezione se il record é gia
	 * presente nel database
	 * 
	 */
	@Test
	public void testMetodoCreateBox() {
		Box box = new Box("A-1-2", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box); // inserisco il box nel db
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.createBox(b.getBox()));
	}

	/**
	 * verifica che il metodo createBox crei correttamente il box nel database
	 * senza generare eccezioni
	 */
	@Test
	public void testMetodoCreateBox2() {
		Box box = new Box("A-1-3", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		assertDoesNotThrow(() -> {
			BoxModel.createBox(box);
		});

		assertEquals("A-1-3", b.getBox().getIndirizzo());
		assertEquals(10, b.getBox().getQuantità());
		assertEquals(p, b.getBox().getProd());

	}

	/**
	 * verifica il corretto funzionamento del metodo calcDisponibilita
	 * 
	 */
	@Test
	public void testMetodoCalcDisponibilitá() {
		Box box = new Box("A-1-4", 15, p);
		Box box2 = new Box("A-2-4", 10, p2);
		Box box3 = new Box("A-3-4", 18, p3);

		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel b1 = BoxModel.getBoxModel(box2);
		BoxModel b2 = BoxModel.getBoxModel(box3);
		BoxModel.createBox(box);
		BoxModel.createBox(box2);
		BoxModel.createBox(box3);

		MovimenModel.generateOrderMovimsOfOrder(ordineOut.getId());

		assertEquals(14, b.calcDisponibilita());
		assertEquals(8, b1.calcDisponibilita());
		assertEquals(15, b2.calcDisponibilita());
	}

	/**
	 * verifica che il metodo cambiaProdotto cambi effettivamente il prodotto
	 * nel Box
	 */
	@Test
	public void testCambiaProdotto() {

		Box box = new Box("A-1-5", 0, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		assertEquals(0, b.getBox().getQuantità());
		b.cambiaProdotto(p2);
		assertEquals(p2, b.getBox().getProd());
	}

	/**
	 * verifica che il metodo cambiaProdottgeneri un eccezione se il box
	 * contiene gia un prodotto con quantita>0
	 */
	@Test
	public void testCambiaProdotto2() {

		Box box = new Box("A-1-5", 5, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		assertThrows(IllegalArgumentException.class,
				() -> b.cambiaProdotto(p2));
	}

	/**
	 * verifica che il metodo cambiaProdotto generi un'eccezione se il nuovo
	 * prodotto é null
	 */
	@Test
	public void testCambiaProdotto3() {

		Box box = new Box("A-2-5", 5, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		Prodotto pn = null;
		assertThrows(IllegalArgumentException.class,
				() -> b.cambiaProdotto(pn));
	}

	/**
	 * verifica che il metodo cambiaProdotto generi un'eccezione se il nuovo
	 * prodotto non é valido
	 */
	public void testCambiaProdotto4() {

		Box box = new Box("A-2-5", 5, p);
		BoxModel.createBox(box);
		BoxModel b = BoxModel.getBoxModel(box);
		Prodotto p2 = new Prodotto(-1, "cambioprod", "descr3", 4, 6);
		assertDoesNotThrow(() -> ProductModel.createProdotto(p2));
		assertThrows(IllegalArgumentException.class,
				() -> b.cambiaProdotto(p2));
	}

	/**
	 * verifica il funzionamento del metodo rifornisci
	 */
	@Test
	public void testRifornisci() {
		Box box = new Box("A-1-6", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.rifornisci(5);
		assertEquals(15, b.getBox().getQuantità());
	}

	/**
	 * verifica che il metodo rifornisci generi eccezione se quantita é negativa
	 * o uguale a zero
	 */
	@Test
	public void testRifornisci2() {
		Box box = new Box("A-1-6", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		assertThrows(IllegalArgumentException.class, () -> b.rifornisci(-5));
		assertThrows(IllegalArgumentException.class, () -> b.rifornisci(0));

	}

	/**
	 * verifica che il metodo preleva generi eccezione se si prova a prelevare
	 * una quantita maggiore di quella disponibile
	 */
	@Test
	public void testPreleva() {
		Box box = new Box("A-1-2", 5, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		assertThrows(IllegalArgumentException.class, () -> b.preleva(10));
	}

	/**
	 * verifica che il metodo preleva generi eccezione se si prova a prelevare
	 * una quantita minore o uguale a zero
	 */
	@Test
	public void testPreleva3() {
		Box box = new Box("A-1-2", 5, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		assertThrows(IllegalArgumentException.class, () -> b.preleva(-10));
		assertThrows(IllegalArgumentException.class, () -> b.preleva(0));

	}

	/**
	 * verifica il funzionamento del metodo preleva
	 */
	@Test
	public void testPreleva2() {
		Box box = new Box("A-1-7", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.preleva(5);
		assertEquals(5, b.getBox().getQuantità());
	}

	/**
	 * verifica il funzionamento del metodo setQuantita
	 */
	@Test
	public void testSetQuantita() {
		Box box = new Box("A-1-8", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		b.setQuantita(30);
		assertEquals(30, b.getBox().getQuantità());
	}

	/**
	 * verifica che il metodo setQuantita generi un eccezione se si inserisce
	 * una quantita negativa
	 */
	@Test
	public void testSetQuantita2() {
		Box box = new Box("A-1-8", 10, p);
		BoxModel b = BoxModel.getBoxModel(box);
		BoxModel.createBox(box);
		assertThrows(IllegalArgumentException.class, () -> b.setQuantita(-2));

	}

	/**
	 * verifica il corretto funzionamento del metodo getBoxModelByAddress
	 */
	@Test
	public void testGetBoxModelByAddress() {
		String add = "A-1-1";
		String add2 = "A-2-1";
		Box b = new Box(add, 12, p);
		Box b2 = new Box(add2, 2, p2);
		BoxModel bm = BoxModel.getBoxModel(b);
		BoxModel bm2 = BoxModel.getBoxModel(b2);
		assertEquals(bm, BoxModel.getBoxModelByAddr(add));
		assertEquals(bm2, BoxModel.getBoxModelByAddr(add2));
	}

	/**
	 * verifica che il metodo getBoxModelByAddress generi un eccezione se
	 * l'indirizzo passato non é del formato corretto
	 */
	@Test
	public void testGetBoxModelByAddress2() {
		String add = "A-2-2";
		Box b = new Box(add, 12, p);
		BoxModel bm = BoxModel.getBoxModel(b);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.getBoxModelByAddr("A22"));
	}

	/**
	 * verifica il corretto funzionameno del metodo getAllBoxModels, in
	 * particolare la mappa restituita non deve contenere modelli con record
	 * null
	 */
	@Test
	public void testGetAllBoxMOdels() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Box box3 = new Box("A-2-5", 5, p2);
		BoxModel bm3 = BoxModel.getBoxModel(box3);
		BoxModel.createBox(box3);
		Box box4 = new Box("A-4-4", 15, p3);
		BoxModel bm4 = BoxModel.getBoxModel(box4);
		Box box5 = new Box("A-4-3", 10, p2);
		BoxModel bm5 = BoxModel.getBoxModel(box5);
		TreeMap<String, BoxModel> tm = new TreeMap<>();
		tm.put(bm1.getBox().getIndirizzo(), bm1);
		tm.put(bm2.getBox().getIndirizzo(), bm2);
		tm.put(bm3.getBox().getIndirizzo(), bm3);

		assertEquals(true, tm.equals(BoxModel.getAllBoxModels()));
	}

	/**
	 * verifica il corretto funzionameno del metodo getAllBoxModels, in
	 * particolare la mappa restituita non deve contenere modelli con record
	 * null
	 */
	@Test
	public void testGetAllBoxMOdels2() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Box box3 = new Box("A-2-5", 5, p2);
		BoxModel bm3 = BoxModel.getBoxModel(box3);
		BoxModel.createBox(box3);
		Box box4 = new Box("A-1-4", 15, p3);
		BoxModel bm4 = BoxModel.getBoxModel(box4);
		Box box5 = new Box("A-1-3", 10, p2);
		BoxModel bm5 = BoxModel.getBoxModel(box5);
		TreeMap<String, BoxModel> tm = new TreeMap<>();
		tm.put(bm1.getBox().getIndirizzo(), bm1);
		tm.put(bm2.getBox().getIndirizzo(), bm2);
		tm.put(bm3.getBox().getIndirizzo(), bm3);
		tm.put(bm4.getBox().getIndirizzo(), bm4);

		assertEquals(false, tm.equals(BoxModel.getAllBoxModels()));

	}

	/**
	 * verifica il corretto funzionamento del metodo findBoxWithProduct
	 */
	@Test
	public void testFindBoxesWithProd() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Box box3 = new Box("A-2-5", 5, p2);
		BoxModel bm3 = BoxModel.getBoxModel(box3);
		BoxModel.createBox(box3);
		Box box4 = new Box("A-1-4", 15, p3);
		BoxModel bm4 = BoxModel.getBoxModel(box4);
		BoxModel.createBox(box4);
		Box box5 = new Box("A-1-3", 10, p3);
		BoxModel bm5 = BoxModel.getBoxModel(box5);
		BoxModel.createBox(box5);
		ArrayList<BoxModel> ms = new ArrayList<BoxModel>();
		ms.add(bm4);
		ms.add(bm5);
		ArrayList<BoxModel> ps = BoxModel.findBoxesWithProd(p3);
		assertEquals(true, ms.containsAll(ps));
	}

	/**
	 * verifica il corretto funzionamento del metodo findBoxWithProduct
	 */
	@Test
	public void testFindBoxesWithProd2() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Box box3 = new Box("A-2-5", 5, p2);
		BoxModel bm3 = BoxModel.getBoxModel(box3);
		BoxModel.createBox(box3);
		Box box4 = new Box("A-1-4", 15, p3);
		BoxModel bm4 = BoxModel.getBoxModel(box4);
		BoxModel.createBox(box4);
		Box box5 = new Box("A-1-3", 10, p2);
		BoxModel bm5 = BoxModel.getBoxModel(box5);
		BoxModel.createBox(box5);
		ArrayList<BoxModel> ls = new ArrayList<BoxModel>();
		ls.add(bm1);
		assertEquals(true, ls.equals(BoxModel.findBoxesWithProd(p)));
	}

	/**
	 * verifica che il metodo findBoxWithProduct generi un eccezione se il
	 * prodotto é null
	 */
	@Test
	public void testFindBoxesWithProd3() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Prodotto pn = null;
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.findBoxesWithProd(pn));
	}

	/**
	 * verifica che il metodo findBoxWithProduct generi un eccezione se il
	 * prodotto é non valido
	 */
	@Test
	public void testFindBoxesWithProd4() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Prodotto pn = new Prodotto(-1, "", "", 0, 0);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.findBoxesWithProd(pn));
	}

	/**
	 * verifica che il metodo findBoxesWithProduct generi un eccezione se il
	 * prodotto non é presente nel database
	 */
	@Test
	public void testFindBoxesWithProd5() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Prodotto nnn = new Prodotto(19, "aa", "bb", 20, 10);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.findBoxesWithProd(nnn));
	}

	/**
	 * verifica il corretto funzionamento del metodo esistenzaBoxModel
	 */
	@Test
	public void testEsistenzaBoxModel() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		assertEquals(true, BoxModel.esistenzaBoxModel("A-1-8"));

	}

	/**
	 * verifica che il metodo esistenzaBoxModel generi un eccezione se viene
	 * passata un indirizzo in un formato non corretto
	 */
	@Test
	public void testEsistenzaBoxModel2() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.esistenzaBoxModel("A18"));
	}

	/**
	 * controlla corretto funzionamento metodo isBoxAtAddrFree
	 */
	@Test
	public void testIsBoxAtAddrFree() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		assertEquals(false, BoxModel.isBoxAtAddrFree("A-1-8"));
	}

	/**
	 * controlla corretto funzionamento metodo isBoxAtAddrFree
	 */
	@Test
	public void testIsBoxAtAddrFree2() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		assertEquals(true, BoxModel.isBoxAtAddrFree("A-2-8"));
	}

	/**
	 * controlla che isBoxAtAddrFree generi un eccezione se si inserisce un
	 * indirizzo nel formato non corretto
	 */
	@Test
	public void testIsBoxAtAddrFree3() {
		Box box1 = new Box("A-1-8", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("A-1-6", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.isBoxAtAddrFree("A28"));
	}

	/**
	 * controlla corretto funzionamento metodo findFreeAddr
	 */
	@Test
	public void testFindFreeAddr() {
		Box box1 = new Box("A-0-0", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		assertEquals("B-0-0", BoxModel.findFreeAddr());
	}

	/**
	 * controlla corretto funzionamento metodo findFreeAddr
	 */
	@Test
	public void testFindFreeAddr2() {
		Box box1 = new Box("A-0-0", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("B-0-0", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		assertEquals("C-0-0", BoxModel.findFreeAddr());
	}

	/**
	 * verifica il corretto funzionamento del metodo assignRandomBoToProd
	 * andando a controllare che venga generata un eecezione se si prova a
	 * creare il box corretto nel db perché giá creato dal metodo
	 */
	@Test
	public void testAssignRandomBoxToProd() {
		Box box1 = new Box("A-0-0", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("B-0-0", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		BoxModel.assignRandomBoxToProd(p2);
		Box bp = new Box("C-0-0", 0, p2);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.createBox(bp));
	}

	/**
	 * verifica che il metodo assignRandomBoToProd generi eccezioni se il
	 * pordotto=null o non é valido
	 * 
	 */
	@Test
	public void testAssignRandomBoxToProd2() {
		Box box1 = new Box("A-0-0", 10, p);
		BoxModel bm1 = BoxModel.getBoxModel(box1);
		BoxModel.createBox(box1);
		Box box2 = new Box("B-0-0", 10, p4);
		BoxModel bm2 = BoxModel.getBoxModel(box2);
		BoxModel.createBox(box2);
		Prodotto pn = null;
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.assignRandomBoxToProd(pn));
		Prodotto pn2 = new Prodotto(15, "ss", "dd", 30, 40);
		assertThrows(IllegalArgumentException.class,
				() -> BoxModel.assignRandomBoxToProd(pn2));
	}
}
