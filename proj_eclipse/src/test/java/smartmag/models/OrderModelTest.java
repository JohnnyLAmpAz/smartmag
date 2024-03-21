package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.jooq.exception.IntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;

class OrderModelTest extends BaseTest {

	public static HashMap<Prodotto, Integer> prodottiStatic;
	public static LocalDate demStatic;
	public static LocalDate dcoStatic;

	static {
		demStatic = LocalDate.of(2024, 04, 04);
		dcoStatic = LocalDate.of(2024, 02, 01);

		Prodotto p = new Prodotto(ProductModel.getNextAvailableId(), "scala2",
				"bassa", 10, 2);
		Prodotto p1 = new Prodotto(ProductModel.getNextAvailableId() + 1,
				"scala3", "alta", 100, 2);
		Prodotto p2 = new Prodotto(ProductModel.getNextAvailableId() + 2,
				"scala4", "bassa", 20, 3);
		Prodotto p3 = new Prodotto(ProductModel.getNextAvailableId() + 3,
				"scala5", "media", 30, 4);
		prodottiStatic = new HashMap<>();
		prodottiStatic.put(p, 2);
		prodottiStatic.put(p1, 3);
		prodottiStatic.put(p2, 4);
		prodottiStatic.put(p3, 5);

	}

	@Test
	void testGetOrdine() {
		fail("Not yet implemented");
	}

	@Test
	void testApprova() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateOrdine() {
		fail("Not yet implemented");
	}

	@Test
	void testInserisciProdotto() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateStatoOrdine() {
		fail("Not yet implemented");
	}

	@Test
	void testSetStato() {
		fail("Not yet implemented");
	}

	@Test
	void testMarkAsCompleted() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteOrdine() {
		fail("Not yet implemented");
	}

	@Test
	void testOrderIsSavedInDb() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateQtaProdottoOrdine()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, demStatic, dcoStatic);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);

		ArrayList<Prodotto> p = new ArrayList<>();
		om.getOrdine().getProdotti().forEach((t, u) -> p.add(t));
		Prodotto pMod = p.getFirst();
		// TODO non crea il record prodottoOrdini!!!
		om.updateQtaProdottoOrdine(pMod, 222);

	}

	/**
	 * verifica che il modello dell'ordine appena creato corrisponde a quello
	 * che restituisc il metodo utilizzando solo l'id dell'ordine
	 */
	@Test
	void testGetOrderModelById()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, demStatic, dcoStatic);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);
		Ordine o1 = new Ordine(OrderModel.getNextAvailableOrderId() + 1,
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, demStatic, dcoStatic);
		o1.setProdotti(prodottiStatic);
		OrderModel om1 = OrderModel.create(o1);

		assertEquals(om, OrderModel.getOrderModelById(o.getId()));
		assertNotEquals(om1, OrderModel.getOrderModelById(o.getId()));
	}

	/**
	 * verifica che il modello dell'ordine appena creato corrisponde a quello
	 * che restituisc il metodo
	 */
	@Test
	void testGetOrderModelOf()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, demStatic, dcoStatic);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);

		assertEquals(om, OrderModel.getOrderModelOf(o));

	}

//	@Test
//	void testGetNextAvailableOrderId() {
//		fail("Not yet implemented");
//	}

	/**
	 * Verifica il metodo create, prima con un ordine valido e poi con un ordine
	 * non valido. Con l'ordine non valido, ci si aspetta un'eccezione
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testCreate()
			throws SQLIntegrityConstraintViolationException, ParseException {

		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, demStatic, dcoStatic);
		o.setProdotti(prodottiStatic);

		OrderModel om = OrderModel.create(o);
		/**
		 * Verifica che l'ordine immesso sia presente nel DB
		 */
		assertEquals(om, OrderModel.getOrderModelOf(o));
		/**
		 * Deve lanciare un'eccezione poichè si cerca di creare un record di un
		 * ordine già esistente
		 */
		assertThrows(SQLIntegrityConstraintViolationException.class,
				() -> OrderModel.create(o));

		Prodotto pnv = new Prodotto(ProductModel.getNextAvailableId() + 4,
				"scala5", "media", 30, 4);
		prodottiStatic.put(pnv, 0);
		ProductModel.createProdotto(pnv);
		Ordine onv = new Ordine(OrderModel.getNextAvailableOrderId() + 1,
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, demStatic, dcoStatic);
		onv.setProdotti(prodottiStatic);
		/*
		 * verifica che venga lanciata un'eccezione se l'ordine non è valido
		 */
		assertThrows(IntegrityConstraintViolationException.class,
				() -> OrderModel.create(onv));
	}

	@Test
	void testGetAllOrderModels() {
		fail("Not yet implemented");
	}

	@Override
	protected void postSetUp() {

		prodottiStatic.forEach((t, u) -> {
			try {
				ProductModel.createProdotto(t);
			} catch (SQLIntegrityConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}

}
