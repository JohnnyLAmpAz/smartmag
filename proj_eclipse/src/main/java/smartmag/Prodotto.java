package smartmag;

public class Prodotto {

	private int id;
	private String nome;
	private String descr;
	private float peso;
	private int soglia;

	public Prodotto(int id, String nome, String descr, float peso, int soglia) {
		this.id = id;
		this.setNome(nome);
		this.setDescr(descr);
		this.peso = peso;
		this.soglia = soglia;
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getDescr() {
		return descr;
	}

	public float getPeso() {
		return peso;
	}

	public int getSoglia() {
		return soglia;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome == null ? null : nome.trim();
	}

	public void setDescr(String descr) {
		this.descr = descr == null ? null : descr.trim();
	}

	public void setPeso(float peso) {
		this.peso = peso;
	}

	public void setSoglia(int soglia) {
		this.soglia = soglia;
	}

	@Override
	public String toString() {
		return "ID: " + id + ", Nome: " + nome + ", Descrizione: " + descr
				+ ", Peso: " + peso + "kg" + ", Soglia: " + soglia + ".";
	}

	public Boolean isValid() {
		if (id < 0 || nome.isBlank() || soglia < 0)
			return false;
		return true;
	}
}
