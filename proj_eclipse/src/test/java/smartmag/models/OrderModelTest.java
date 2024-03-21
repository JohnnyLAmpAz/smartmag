package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;

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

	/**
	 * Verifica che l'ordine creato sia uguale a quello restituito
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testGetOrdine()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId() + 20,
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		Ordine o1 = new Ordine(OrderModel.getNextAvailableOrderId() + 21,
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		o1.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);
		assertEquals(o, om.getOrdine());
		assertNotEquals(o1, om.getOrdine());
	}

	/**
	 * Verifica se tutti i modelli degli ordini restituiti corrispondono a
	 * quelli generati
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testGetAllOrderModels()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);

		Ordine o1 = new Ordine(OrderModel.getNextAvailableOrderId() + 1,
				TipoOrdine.IN, StatoOrdine.IN_ATTESA, demStatic, null);
		o1.setProdotti(prodottiStatic);
		OrderModel om1 = OrderModel.create(o1);
		TreeMap<Integer, OrderModel> mapOm = new TreeMap<>();
		mapOm.put(o.getId(), om);
		mapOm.put(o1.getId(), om1);

		assertEquals(mapOm, OrderModel.getAllOrderModels());

	}

	/**
	 * Testa se lo stato dell'ordine viene cambiato in completato
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testMarkAsCompleted()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);
		om.markAsCompleted();
		assertTrue(om.getOrdine().getStato().equals(StatoOrdine.COMPLETATO));
	}

	/**
	 * Testa anche il metodo isPreparabile, setStato di OrderModel e isValid di
	 * Ordine indirettamente
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testApprova()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.IN, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);
		assertDoesNotThrow(() -> om.approva());

		// prova con ordine non preparabile poichè di tipo OUT
		Ordine o1 = new Ordine(OrderModel.getNextAvailableOrderId() + 1,
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o1.setProdotti(prodottiStatic);
		OrderModel om1 = OrderModel.create(o1);
		assertThrows(IllegalStateException.class, () -> om1.approva());
		om1.setStato(StatoOrdine.COMPLETATO);
		assertThrows(IllegalStateException.class, () -> om1.approva());
		assertThrows(IllegalArgumentException.class,
				() -> MovimenModel.generateOrderMovimsOfOrder(o.getId()));
	}

	/**
	 * Viene testata la modifica di un ordine, viene usato un ordine di test
	 * valido e uno non valido
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testUpdateOrdine()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.IN, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);
		Ordine o1 = new Ordine(o.getId(), TipoOrdine.IN,
				StatoOrdine.IN_SVOLGIMENTO, demStatic, null);
		o1.setProdotti(prodottiStatic);
		om.updateOrdine(o1);
		assertEquals(o1, om.getOrdine());
		Ordine o2 = new Ordine(OrderModel.getNextAvailableOrderId() + 120,
				TipoOrdine.IN, StatoOrdine.IN_SVOLGIMENTO, demStatic, null);
		o2.setProdotti(prodottiStatic);
		assertThrows(NullPointerException.class, () -> om.updateOrdine(o2));
	}

	/**
	 * Viene testato il metodo che cancella un ordine
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testDeleteOrdine()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);

		OrderModel om = OrderModel.create(o);
		assertTrue(om.orderIsSavedInDb());
		om.deleteOrdine();
		assertFalse(om.orderIsSavedInDb());
	}

	/**
	 * Viene testato il metodo che permette di modificare le quantità di un
	 * prodotto relativo ad un ordine.
	 * 
	 * @throws SQLIntegrityConstraintViolationException
	 * @throws ParseException
	 */
	@Test
	void testUpdateQtaProdottoOrdine()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);

		Prodotto pMod = new Prodotto(ProductModel.getNextAvailableId(), "pmod",
				"mod", 110, 122);
		HashMap<Prodotto, Integer> listaProds = om.getOrdine().getProdotti();
		HashMap<Prodotto, Integer> listaProdsMod = new HashMap<>();
		listaProds.forEach((t, u) -> {
			try {
				om.updateQtaProdottoOrdine(t, 200);
				listaProdsMod.put(t, 200);
			} catch (SQLIntegrityConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		assertEquals(listaProdsMod, om.getOrdine().getProdotti());
	}

	/**
	 * verifica che il modello dell'ordine appena creato corrisponde a quello
	 * che restituisc il metodo utilizzando solo l'id dell'ordine
	 */
	@Test
	void testGetOrderModelById()
			throws SQLIntegrityConstraintViolationException, ParseException {
		Ordine o = new Ordine(OrderModel.getNextAvailableOrderId(),
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);
		Ordine o1 = new Ordine(OrderModel.getNextAvailableOrderId() + 1,
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
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
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		o.setProdotti(prodottiStatic);
		OrderModel om = OrderModel.create(o);

		assertEquals(om, OrderModel.getOrderModelOf(o));

	}

	// @Test
	// void testGetNextAvailableOrderId() {
	// fail("Not yet implemented");
	// }

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
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
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
				TipoOrdine.OUT, StatoOrdine.IN_ATTESA, demStatic, null);
		onv.setProdotti(prodottiStatic);
		/*
		 * verifica che venga lanciata un'eccezione se l'ordine non è valido
		 */
		assertThrows(IntegrityConstraintViolationException.class,
				() -> OrderModel.create(onv));
	}

	@Override
	protected void postSetUp() {

		demStatic = LocalDate.of(2024, 04, 04);

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
