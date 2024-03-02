package smartmag.models.ui;

import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import smartmag.data.Utente;
import smartmag.models.UtenteModel;

public class UsersTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private TreeMap<String, UtenteModel> utenti;
	private String[] columnNames;

	public UsersTableModel() {
		utenti = UtenteModel.getAllUserModels();
		columnNames = new String[] { "Matricola", "Nome", "Cognome", "Ruolo" };
	}

	@Override
	public int getRowCount() {
		return utenti.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		// Riga (Utente)
		Utente u = null;
		int i = 0;
		for (String matr : utenti.keySet()) {
			if (rowIndex == i) {
				u = utenti.get(matr).getUtente();
				break;
			}
			i++;
		}
		if (u == null)
			throw new IndexOutOfBoundsException("Riga non presente!");

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

}
