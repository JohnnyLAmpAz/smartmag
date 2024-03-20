package smartmag.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import smartmag.BaseTest;
import smartmag.data.Box;
import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;
import smartmag.data.TipoUtente;
import smartmag.data.Utente;

class MovimenModelTest extends BaseTest {

	/**
	 * Magazziniere Mario
	 */
	static final Utente mario = new Utente("m.rossi", "Mario", "Rossi", "123",
			TipoUtente.MAGAZZINIERE);

	/**
	 * Prodotti
	 */
	static final ArrayList<Prodotto> prods = new ArrayList<Prodotto>();
	static {
		prods.add(new Prodotto(1, "A", "AAA", 2.4f, 25));
		prods.add(new Prodotto(2, "B", "BBB", 2.4f, 25));
		prods.add(new Prodotto(3, "C", "CCC", 2.4f, 25));
	}

	/**
	 * Box
	 */
	static final ArrayList<Box> boxes = new ArrayList<>();
	static {

		// Assegna i prodotti in due box differenti
		for (int i = 0; i < prods.size(); i++) {
			Prodotto p = prods.get(i);
			boxes.add(new Box("A-" + p.getId() + "-" + 0, 10, p));
			boxes.add(new Box("A-" + p.getId() + "-" + 1, 10, p));
		}
	}

	/**
	 * Ordine di rifornimento; ID 0
	 */
	static final Ordine ordineIn = new Ordine(0, TipoOrdine.IN,
			StatoOrdine.IN_ATTESA, LocalDate.now());
	static {
		HashMap<Prodotto, Integer> listaSpesa = new HashMap<>();
		for (Prodotto prodotto : prods) {
			listaSpesa.put(prodotto, prodotto.getId());
		}
		ordineIn.setProdotti(listaSpesa);
	}

	/**
	 * Ordine in uscita; ID 1
	 */
	static final Ordine ordineOut = new Ordine(1, TipoOrdine.OUT,
			StatoOrdine.IN_ATTESA, LocalDate.now());
	static {
		HashMap<Prodotto, Integer> listaSpesa = new HashMap<>();
		for (Prodotto prodotto : prods) {
			listaSpesa.put(prodotto, prodotto.getId());
		}
		ordineOut.setProdotti(listaSpesa);
	}

	@Override
	protected void postSetUp() {

		// Creo record utili (validi)
		UtenteModel.createUtente(mario);
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

	}
}
