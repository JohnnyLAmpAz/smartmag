package smartmag;

import java.util.Objects;

import org.jooq.tools.StringUtils;

import smartmag.db.utils.StrUtils;

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
		this.nome = nome == null || nome.isBlank() ? null : nome.strip();
	}

	public void setDescr(String descr) {
		this.descr = descr == null || descr.isBlank() ? null : descr.strip();
	}

	public void setPeso(float peso) {
		this.peso = peso;
	}

	public void setSoglia(int soglia) {
		this.soglia = soglia;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("ID: %d, Nome: %s".formatted(id,
				StrUtils.shortenStr(nome, 25)));
		if (descr != null) {
			sb.append(", Descr: %s".formatted(StrUtils.shortenStr(descr, 25)));
		}
		if (peso != 0f) {
			sb.append(", Peso: %.3fkg".formatted(peso));
		}
		sb.append(", Soglia: %d".formatted(soglia));

		return sb.toString();
	}

	public Boolean isValid() {
		if (id < 0 || nome == null || nome.isBlank() || peso < 0 || soglia < 0)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(descr, id, nome, peso, soglia);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prodotto other = (Prodotto) obj;
		return id == other.id;
	}
}
