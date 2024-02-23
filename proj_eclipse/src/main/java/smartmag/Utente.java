package smartmag;

import java.util.EnumSet;

/**
 * Rappresenta un utente presente in magazzino
 */

public class Utente {
	
	
	private String matricola;
	private String nome;
	private String cognome;
	private String password;
	private TipoUtente tipo;
	
	public Utente(String matricola, String nome, String cognome, String password, TipoUtente tipo) {
		
		this.matricola=matricola;
		this.nome=nome;
		this.cognome=cognome;	
		this.password=password;
		this.tipo=tipo;
	
	}
		
	/**
	 * Verifica i requisiti di un utente:  Nome non vuoto,
	 * Cognome non vuoto, Matricola non vuota, Password non vuota, tipo
	 * contenuto tra i possibili tipi dispoibili.
	 * 
	 * NON controlla se la matricola è già stata utilizzata o meno da altri utenti
	 * 
	 * @return validità dell'utente
	 */
	public boolean isvalid() {
		
		EnumSet<TipoUtente> enumset= EnumSet.allOf(TipoUtente.class);
		if(nome==null || cognome==null || matricola==null || password==null 
				|| nome.isBlank()|| cognome.isBlank()|| password.isBlank()|| matricola.isBlank() || !enumset.contains(tipo) ) {
			return false;
		}
		return true;
	}

	
	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public void setTipo(TipoUtente tipo) {
		this.tipo = tipo;
	}


	public String getMatricola() {
		return matricola;
	}



	public String getNome() {
		return nome;
	}



	public String getCognome() {
		return cognome;
	}



	public String getPassword() {
		return password;
	}



	public TipoUtente getTipo() {
		return tipo;
	}
	
	
}
