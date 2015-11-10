package br.com.dbcorp.melhoreministerio.dto;

public enum Avaliacao {
	NAO_AVALIADO("A", "Não Avaliado"), PASSOU("P", "Passou"), SUBSTITUIDO("S", "Substituido"), NAO_PASSOU("F", "Não Passou");
	
	String sigla;
	String label;
	
	private Avaliacao(String sigla, String label) {
		this.sigla = sigla;
		this.label = label;
	}
	
	public static Avaliacao getByDescription(String label) {
		switch (label) {
		case "Passou":
			return PASSOU;
		case "Não Passou":
			return NAO_PASSOU;
		case "Substituido":
			return SUBSTITUIDO;
		case "Não Avaliado":
		case "Selecionar...":
			return NAO_AVALIADO;
		default:
			throw new RuntimeException("Opção inválida");
		}
	}
	
	public static Avaliacao getByInitials(String sigla) {
		switch (sigla) {
		case "P":
			return PASSOU;
		case "F":
			return NAO_PASSOU;
		case "S":
			return SUBSTITUIDO;
		case "A":
			return NAO_AVALIADO;
		default:
			throw new RuntimeException("Opção inválida");
		}
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getSigla() {
		return sigla;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
}
