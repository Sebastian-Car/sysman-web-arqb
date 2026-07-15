/**
 * 
 */
package com.sysman.contabilidad.enums;

/**
 * @author JhohanDavidAmayaSalc
 *
 */
public enum FrminforetencionesControladorUrlEnum {
	URL8001("FRMINFORETENCIONESCONTROLADORURL8001",
			"8001"),
	URL8007("FRMINFORETENCIONESCONTROLADORURL8007",
			"8007"),
	URL4001("FRMINFORETENCIONESCONTROLADORURL4001",
			"4001");
	

	private final String key;
	private final String value;

	private FrminforetencionesControladorUrlEnum(String key, String value) {
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
