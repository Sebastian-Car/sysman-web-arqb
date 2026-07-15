package com.sysman.presupuesto.enums;

public enum FinformecontmesControladorUrlEnum {
	
	URL6064("FEJECUCIONPPPTALSCONTROLADORURL6064", "94130"), /*  Cinicial */

    URL7124("FEJECUCIONPPPTALSCONTROLADORURL7124", "94132"), /*  Cfinal */
	
	URL5447("FINFORMECONTRALORIA6186","7001"), /*Mes*/

	URL5047("FINFORMECONTRALORIA5295","4001");  /*Ano*/

	private final String key;
	private final String value;

	private FinformecontmesControladorUrlEnum(String key, String value) {
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
