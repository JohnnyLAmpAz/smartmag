package smartmag.models.ui;

import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import smartmag.data.Utente;
import smartmag.models.UtenteModel;

/**
 * JTableModel per tabelle di tutti gli Utenti. Implementando ChangeListener, si
 * registra all'UtenteModel per reagire a qualunque modifica che riguarda gli
 * utenti.
 */
public class UsersTableModel extends AbstractTableModel
		implements ChangeListener {

	private static final long serialVersionUID = 1L;

	/**
	 * Testate delle colonne
	 */
	private static final String[] COLUMN_NAMES = new String[] { "Matricola",
			"Nome", "Cognome", "Ruolo" };;

	/**
	 * Mappa degli utenti mostrati in tabella
	 */
	private TreeMap<String, UtenteModel> utenti;

	/**
	 * Recupera tutti gli utenti dal modello degli utenti e si registra come
	 * ChangeListener per i cambiamenti.
	 */
	public UsersTableModel() {
		refreshDataFromModel();
		UtenteModel.addChangeListener(this);
	}

	/**
	 * Aggiorna la mappa locale recuperando i dati degli utenti dal DB
	 * attraverso il modello, per poi notificare l'interfaccia del cambiamento
	 */
	private void refreshDataFromModel() {
		utenti = UtenteModel.getAllUserModels();

		// Notifica la finestra della variazione dei dati
		fireTableDataChanged();
	}

	/**
	 * Restituisce l'istanza del modello Utente di indice (riga) specificato.
	 * Utile per ricavare l'utente selezionato).
	 * 
	 * @param index indice posizione dell'utente
	 * @return modello dell'utente
	 */
	public UtenteModel getUserModelAt(int index) {
		// Riga (Utente)
		UtenteModel um = null;
		int i = 0;
		for (String matr : utenti.keySet()) {
			if (index == i) {
				um = utenti.get(matr);
				break;
			}
			i++;
		}
		if (um == null)
			throw new IndexOutOfBoundsException("Riga non presente!");
		return um;
	}

	@Override
	public int getRowCount() {
		return utenti.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		// Riga (Utente)
		Utente u = getUserModelAt(rowIndex).getUtente();

		// Colonna (campo dell'utente)
		return switch (columnIndex) {
			case 0:
				yield u.getMatricola();
			case 1:
				yield u.getNome();
			case 2:
				yield u.getCognome();
			case 3:
				yield u.getTipo();
			default:
				throw new IndexOutOfBoundsException(
						"Indice di campo non valido");
		};
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		// Aggiorna dati dal modello
		refreshDataFromModel();
	}

}
