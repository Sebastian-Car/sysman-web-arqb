/**
 * 
 */
package com.sysman.contabilidad.enums;

/**
 * @author lvega
 *
 */
public enum SubirmovcontablesControladorUrlEnum {

	URL34007("SUBIRMOVCONTABLESURLENUM34007", "34007"),
	URL59003("SUBIRMOVCONTABLESURLENUM59003", "59003"),
	URL20049("SUBIRMOVCONTABLESURLENUM20049", "20049"),
	URL23060("SUBIRMOVCONTABLESURLENUM23060", "23060"),
	URL13049("SUBIRMOVCONTABLESURLENUM13049", "13049"),
	URL659001("SUBIRMOVCONTABLESURLENUM659001", "659001"),
	URL12009("SUBIRMOVCONTABLESURLENUM12009", "12009"),
	URL8005("SUBIRMOVCONTABLESURLENUM8005", "8005");

	private final String key;
	private final String value;

	private SubirmovcontablesControladorUrlEnum(String key, String value) {
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