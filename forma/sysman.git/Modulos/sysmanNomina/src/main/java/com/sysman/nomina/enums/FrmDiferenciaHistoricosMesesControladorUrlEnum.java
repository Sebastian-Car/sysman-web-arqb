package com.sysman.nomina.enums;

public enum FrmDiferenciaHistoricosMesesControladorUrlEnum {

	URL4326("BALANCEAPERTURANIIFCONTROLADORURL4326", "29003"),

	URL5411("BALANCEAPERTURANIIFCONTROLADORURL5411", "29005"),

	URL3815("BALANCEAPERTURANIIFCONTROLADORURL3815", "471028"),

	URL3309("BALANCEAPERTURANIIFCONTROLADORURL3309", "4001");

	private final String key;
	private final String value;

	private FrmDiferenciaHistoricosMesesControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
