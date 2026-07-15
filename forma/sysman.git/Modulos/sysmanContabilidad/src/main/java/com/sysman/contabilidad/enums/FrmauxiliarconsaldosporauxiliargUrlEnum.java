package com.sysman.contabilidad.enums;

public enum FrmauxiliarconsaldosporauxiliargUrlEnum {
	URL001("AUXILIARCONSALDOPORAUXILIAR", "4013"),
	URL002("AUXILIARCONSALDOPORAUXILIAR","15005"),
	URL003("AUXILIARCONSALDOPORAUXILIAR","15003"),
    URL004("AUXILIARCONSALDOPORAUXILIAR", "16108"),
    URL005("AUXILIARCONSALDOPORAUXILIAR", "16110"),    
    URL006("AUXILIARCONSALDOPORAUXILIAR", "23048"),
    URL007("AUXILIARCONSALDOPORAUXILIAR", "23050");
  
	private final String key;
	private final String value;

	private FrmauxiliarconsaldosporauxiliargUrlEnum(String key, String value) {
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
