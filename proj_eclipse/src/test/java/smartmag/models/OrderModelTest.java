package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;

import org.jooq.exception.IntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;

class OrderModelTest extends BaseTest {

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
	void testUpdateQtaProdottoOrdine() {
		fail("Not yet implemented");
	}

	@Test
	void testIsPreparabile() {
		fail("Not yet implemented");
	}

	@Test
	void testProductOrderIsSavedInDb() {
		fail("Not yet implemented");
	}

	@Test
	void testGetOrderModelById() {
		fail("Not yet implemented");
	}

	@Test
	void testGetOrderModelOf() {
		fail("Not yet implemented");
	}

	@Test
	void testGetNextAvailableOrderId() {
		fail("Not yet implemented");
	}

	@Test
	void testCreate()
			throws SQLIntegrityConstraintViolationException, ParseException {

		LocalDate dem = LocalDate.parse("2024-03-05");
		LocalDate dco = LocalDate.of(2024, 04, 04);
		Prodotto p = new Prodotto(ProductModel.getNextAvailableId(), "scala2",
				"bassa", 10, 2);
		Prodotto p1 = new Prodotto(ProductModel.getNextAvailableId() + 1,
				"scala3", "alta", 100, 2);
		Prodotto p2 = new Prodotto(ProductModel.getNextAvailableId() + 2,
				"scala4", "bassa", 20, 3);
		Prodotto p3 = new Prodotto(ProductModel.getNextAvailableId() + 3,
				"scala5", "media", 30, 4);
		HashMap<Prodotto, Integer> prodotti = new HashMap<>();
		prodotti.put(p, 2);
		prodotti.put(p1, 3);
		prodotti.put(p2, 4);
		prodotti.put(p3, 5);

		ProductModel.createProdotto(p);
		ProductModel.createProdotto(p1);
		ProductModel.createProdotto(p2);
		ProductModel.createProdotto(p3);

		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, dem, dco);
		o.setProdotti(prodotti);

		OrderModel om = OrderModel.create(o);
		/**
		 * Verifica che l'ordine immesso sia presente nel DB
		 */
		assertEquals(om, OrderModel.getOrderModelOf(o));
		Prodotto pnv = new Prodotto(ProductModel.getNextAvailableId() + 4,
				"scala5", "media", 30, 4);
		prodotti.put(pnv, 0);
		ProductModel.createProdotto(pnv);
		Ordine onv = new Ordine(OrderModel.getNextAvailableOrderId() + 1,
				TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, dem, dco);
		onv.setProdotti(prodotti);
		assertThrows(IntegrityConstraintViolationException.class,
				() -> OrderModel.create(onv));
	}

	@Test
	void testGetAllOrderModels() {
		fail("Not yet implemented");
	}

	@Override
	protected void postSetUp() {
		// TODO Auto-generated method stub

	}

}
