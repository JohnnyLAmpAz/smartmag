package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Box;
import smartmag.data.MovimId;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoMovim;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;
import smartmag.data.TipoUtente;
import smartmag.data.Utente;

/**
 * Contiene i casti di unit testing riguardanti il modello delle Movimentazioni.
 * 
 * Principalmente vengono testate le funzionalità chiave che potrebbero minare
 * l'integrità e la coerenza dei dati gestiti secondo la business logic.
 */
class MovimenModelTest extends BaseTest {

	/**
	 * Magazziniere Mario
	 */
	static Utente magazziniere;

	/**
	 * Responsabile Ordini Luigi
	 */
	static Utente responsabile;

	/**
	 * Prodotti
	 */
	static ArrayList<Prodotto> prods;

	/**
	 * Box
	 */
	static ArrayList<Box> boxes;

	/**
	 * Ordine di rifornimento; ID 0
	 */
	static Ordine ordineIn;

	/**
	 * Ordine in uscita; ID 1
	 */
	static Ordine ordineOut;

	/**
	 * Lista spesa ordine
	 */
	static HashMap<Prodotto, Integer> listaSpesa;

	@Override
	protected void postSetUp() {

		// Inizializzo oggetti utili
		magazziniere = new Utente("m.rossi", "Mario", "Rossi", "123",
				TipoUtente.MAGAZZINIERE);
		responsabile = new Utente("l.verdi", "Luigi", "Verdi", "123",
				TipoUtente.RESPONSABILE);
		prods = new ArrayList<Prodotto>();
		prods.add(new Prodotto(1, "A", "AAA", 2.4f, 25));
		prods.add(new Prodotto(2, "B", "BBB", 2.4f, 25));
		prods.add(new Prodotto(3, "C", "CCC", 2.4f, 25));
		prods.add(new Prodotto(4, "D", "DDD", 2.4f, 25));
		boxes = new ArrayList<>();
		// Assegna i prodotti in due box differenti
		for (int i = 0; i < prods.size(); i++) {
			Prodotto p = prods.get(i);
			boxes.add(new Box("A-" + p.getId() + "-" + 0, 0, p));
			boxes.add(new Box("A-" + p.getId() + "-" + 1, 3, p));
			boxes.add(new Box("A-" + p.getId() + "-" + 2, 100, p));
		}
		ordineIn = new Ordine(0, TipoOrdine.IN, StatoOrdine.IN_ATTESA,
				LocalDate.now());
		listaSpesa = new HashMap<>();
		for (Prodotto prodotto : prods) {
			listaSpesa.put(prodotto, prodotto.getId());
		}
		ordineIn.setProdotti(listaSpesa);
		ordineOut = new Ordine(1, TipoOrdine.OUT, StatoOrdine.IN_ATTESA,
				LocalDate.now());
		listaSpesa = new HashMap<>();
		for (Prodotto prodotto : prods) {
			listaSpesa.put(prodotto, prodotto.getId());
		}
		ordineOut.setProdotti(listaSpesa);

		// Creo record utili (validi)
		UtenteModel.createUtente(magazziniere);
		UtenteModel.createUtente(responsabile);
		try {
			for (Prodotto prodotto : prods) {
				ProductModel.createProdotto(prodotto);
			}
			for (Box box : boxes) {
				BoxModel.createBox(box);
			}
			OrderModel.create(ordineIn);
			OrderModel.create(ordineOut);
		} catch (SQLIntegrityConstraintViolationException | ParseException e) {
			throw new InternalError("C'è qualquadra che non cosa...", e);
		}
	}

	/**
	 * Testa la parte di generazione movimentazioni dato un ordine. In
	 * particolare, interessa i metodi anyGeneratedMovimsOfOrder(),
	 * generateOrderMovimsOfOrder() e getGeneratedMovimsOfOrder().
	 */
	@Test
	void testMovimsOfOrderGeneration() {

		// Inizialmente nessuna movimentazione generata per ordineIn e ordineOut
		assertFalse(MovimenModel.anyGeneratedMovimsOfOrder(ordineIn.getId()));
		assertEquals(0, MovimenModel.getGeneratedMovimsOfOrder(ordineIn.getId())
				.size());
		assertFalse(MovimenModel.anyGeneratedMovimsOfOrder(ordineOut.getId()));
		assertEquals(0, MovimenModel
				.getGeneratedMovimsOfOrder(ordineOut.getId()).size());

		// Se cerco di generare movimentazioni di un ordine non esistente, mi
		// aspetto eccezione
		assertThrows(NullPointerException.class,
				() -> MovimenModel.generateOrderMovimsOfOrder(9999));

		// Su ordini il cui stato != IN_ATTESA, mi aspetto eccezione
		OrderModel.getOrderModelById(ordineOut.getId())
				.setStato(StatoOrdine.IN_SVOLGIMENTO);
		assertThrows(IllegalArgumentException.class, () -> MovimenModel
				.generateOrderMovimsOfOrder(ordineOut.getId()));
		OrderModel.getOrderModelById(ordineOut.getId())
				.setStato(StatoOrdine.IN_ATTESA);

		// Svuoto ogni box
		for (Entry<String, BoxModel> entry : BoxModel.getAllBoxModels()
				.entrySet()) {
			BoxModel bm = entry.getValue();
			bm.setQuantita(0);
		}

		// Se non ho la disponibilità NON mi aspetto eccezione se provo a
		// generare movimentazioni di ordine in ingresso
		MovimenModel.generateOrderMovimsOfOrder(ordineIn.getId());
		assertTrue(MovimenModel.getGeneratedMovimsOfOrder(ordineIn.getId())
				.size() > 0);

		// Se non ho la disponibilità mi aspetto eccezione se provo a generare
		// movimentazioni di ordine in uscita
		assertThrows(IllegalArgumentException.class, () -> MovimenModel
				.generateOrderMovimsOfOrder(ordineOut.getId()));

		// Rifornisco i box
		for (Entry<String, BoxModel> entry : BoxModel.getAllBoxModels()
				.entrySet()) {
			BoxModel bm = entry.getValue();
			bm.setQuantita(100);
		}

		// Con le qta inserite (sufficienti), deve generare movimentazioni
		// ordine in uscita
		MovimenModel.generateOrderMovimsOfOrder(ordineOut.getId());
		assertTrue(MovimenModel.getGeneratedMovimsOfOrder(ordineOut.getId())
				.size() > 0);

		// Mi assicuro che l'ordine sia stato impostato a IN_SVOLGIMENTO
		assertEquals(StatoOrdine.IN_SVOLGIMENTO, OrderModel
				.getOrderModelById(ordineIn.getId()).getOrdine().getStato());
		assertEquals(StatoOrdine.IN_SVOLGIMENTO, OrderModel
				.getOrderModelById(ordineOut.getId()).getOrdine().getStato());
	}

	/**
	 * Caso di test che verifica i corretti passaggi di stato delle
	 * movimentazioni
	 */
	@Test
	void testStatesMovims() {

		// Genero Movimentazioni di ordineIn e ordineOut
		MovimenModel.generateOrderMovimsOfOrder(ordineIn.getId());
		MovimenModel.generateOrderMovimsOfOrder(ordineOut.getId());
		assertTrue(MovimenModel.getGeneratedMovimsOfOrder(ordineIn.getId())
				.size() > 0);
		assertTrue(MovimenModel.getGeneratedMovimsOfOrder(ordineOut.getId())
				.size() > 0);

		// Controllo che tutte le movimentazioni generate siano in stato
		// NON_ASSEGNATE e senza magazziniere assegnato.
		TreeMap<MovimId, MovimenModel> mmm = MovimenModel.getAllMovimenModels();
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertEquals(StatoMovim.NON_ASSEGNATA, mm.getMovim().getStato());
			assertEquals(null, mm.getMovim().getMagazziniere());
		}

		// Se provo a segnarle come PRELEVATE o COMPLETATE, mi aspetto eccezione
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertThrows(IllegalStateException.class, () -> mm.markAsLoaded());
			assertThrows(IllegalStateException.class,
					() -> mm.markAsCompleted());
			assertEquals(StatoMovim.NON_ASSEGNATA, mm.getMovim().getStato());
		}

		// Se provo ad assegnare la movimentazione ad un utente che NON è un
		// magazziniere, mi aspetto eccezione.
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertThrows(IllegalArgumentException.class,
					() -> mm.assignToWorker(responsabile));
		}

		// Di nuovo, devono essere ancora NON_ASSEGNATE
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertEquals(StatoMovim.NON_ASSEGNATA, mm.getMovim().getStato());
			assertEquals(null, mm.getMovim().getMagazziniere());
		}

		// Se provo ad assegnare la movimentazione ad un utente che è un
		// magazziniere, non mi aspetto eccezione.
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertDoesNotThrow(() -> mm.assignToWorker(magazziniere));
		}

		// E ora devono risultare come PRESE_IN_CARICO e con il magazziniere
		// assegnato
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertEquals(StatoMovim.PRESA_IN_CARICO, mm.getMovim().getStato());
			assertEquals(magazziniere, mm.getMovim().getMagazziniere());
		}

		// Se provo a segnarle COMPLETATE o ad assegnarle nuovamente mi aspetto
		// eccezione
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertThrows(IllegalStateException.class,
					() -> mm.assignToWorker(magazziniere));
			assertThrows(IllegalStateException.class,
					() -> mm.markAsCompleted());
			assertEquals(StatoMovim.PRESA_IN_CARICO, mm.getMovim().getStato());
		}

		// Le prelevo controllando che la qta del box venga modificata se OUT
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();

			BoxModel bm = BoxModel.getBoxModelByAddr(mm.getKey().getBoxAddr());
			int initQta = bm.getBox().getQuantità();
			int deltaQta = mm.getMovim().getQuantità();
			assertDoesNotThrow(() -> mm.markAsLoaded());
			assertEquals(StatoMovim.PRELEVATA, mm.getMovim().getStato());

			// Controllo la qta: se ordine OUT => prelevata; se IN => solamente
			// caricata sul muletto (qta box non alterata)
			if (mm.getMovim().getOrdine().getTipo() == TipoOrdine.OUT)
				assertEquals(initQta - deltaQta, bm.getBox().getQuantità());
			else
				assertEquals(initQta, bm.getBox().getQuantità());

		}

		// Una volta prelevate, non possono essere riassegnate o riprelevate
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();
			assertThrows(IllegalStateException.class,
					() -> mm.assignToWorker(magazziniere));
			assertThrows(IllegalStateException.class, () -> mm.markAsLoaded());
			assertEquals(StatoMovim.PRELEVATA, mm.getMovim().getStato());
		}

		// Le completo controllando che la qta del box venga modificata se IN
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();

			BoxModel bm = BoxModel.getBoxModelByAddr(mm.getKey().getBoxAddr());
			int initQta = bm.getBox().getQuantità();
			int deltaQta = mm.getMovim().getQuantità();
			assertDoesNotThrow(() -> mm.markAsCompleted());
			assertEquals(StatoMovim.COMPLETATA, mm.getMovim().getStato());

			// Controllo la qta: se ordine OUT => depositata in zona C/S (qta
			// box non alterata); se IN => box rifornito
			if (mm.getMovim().getOrdine().getTipo() == TipoOrdine.IN)
				assertEquals(initQta + deltaQta, bm.getBox().getQuantità());
			else
				assertEquals(initQta, bm.getBox().getQuantità());

		}

		// Una volta completate tutte, gli ordini devono essere stati
		// contrassegnati come completati
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();

			Ordine o = OrderModel.getOrderModelById(mm.getKey().getOrdineId())
					.getOrdine();
			assertEquals(StatoOrdine.COMPLETATO, o.getStato());
			assertNotNull(o.getDataCompletamento());
		}

		// Annullamento (solo se NON_ASSEGNATE o PRESE_IN_CARICO)
		Ordine ordineDaAnnullare = new Ordine(2, TipoOrdine.OUT,
				StatoOrdine.IN_ATTESA, LocalDate.now());
		ordineDaAnnullare.setProdotti(listaSpesa);
		try {
			OrderModel.create(ordineDaAnnullare);
		} catch (SQLIntegrityConstraintViolationException | ParseException e) {
			fail("???", e);
		}
		MovimenModel.generateOrderMovimsOfOrder(ordineDaAnnullare.getId());
		mmm = MovimenModel.getGeneratedMovimsOfOrder(ordineDaAnnullare.getId());
		int i = 0;
		for (Entry<MovimId, MovimenModel> entry : mmm.entrySet()) {
			MovimenModel mm = entry.getValue();

			// Devo lavorare su almeno 4 movimentazioni
			if (mmm.size() < 4)
				fail("Movimentazioni insufficienti.");

			switch (i) {

				// La prima la assegno, la annullo e ci provo di nuovo
				case 0: {
					mm.assignToWorker(magazziniere);
					assertDoesNotThrow(() -> mm.annulla());
					assertEquals(StatoMovim.ANNULLATA,
							mm.getMovim().getStato());
					assertThrows(IllegalStateException.class,
							() -> mm.annulla());
					assertEquals(StatoMovim.ANNULLATA,
							mm.getMovim().getStato());
					break;
				}

				// La seconda la prelevo pure (non annullabile)
				case 1: {
					mm.assignToWorker(magazziniere);
					mm.markAsLoaded();
					assertThrows(IllegalStateException.class,
							() -> mm.annulla());
					assertNotEquals(StatoMovim.ANNULLATA,
							mm.getMovim().getStato());
					break;
				}

				// La terza la completo (non annullabile)
				case 2: {
					mm.assignToWorker(magazziniere);
					mm.markAsLoaded();
					mm.markAsCompleted();
					assertThrows(IllegalStateException.class,
							() -> mm.annulla());
					assertNotEquals(StatoMovim.ANNULLATA,
							mm.getMovim().getStato());
					break;
				}

				// Le altre non le assegno
				default:
					assertDoesNotThrow(() -> mm.annulla());
			}

			i++;
		}

	}
}
