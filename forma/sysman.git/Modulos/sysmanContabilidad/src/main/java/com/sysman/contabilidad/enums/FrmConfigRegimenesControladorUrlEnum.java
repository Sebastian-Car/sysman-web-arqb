/**
 * 
 */
package com.sysman.contabilidad.enums;

/**
 * 
 */
public enum FrmConfigRegimenesControladorUrlEnum {

	URL4001("FRMCONFIGREGIMENESCONTROLADORURLENUM", "4001"),

	URL22001("FRMCONFIGREGIMENESCONTROLADORURLENUM", "22001"),

	URL8013("FRMCONFIGREGIMENESCONTROLADORURLENUM", "8013"),

	URL59029("FRMCONFIGREGIMENESCONTROLADORURLENUM", "59029");

	private final String key;
	private final String value;

	private FrmConfigRegimenesControladorUrlEnum(String key, String value) {
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
