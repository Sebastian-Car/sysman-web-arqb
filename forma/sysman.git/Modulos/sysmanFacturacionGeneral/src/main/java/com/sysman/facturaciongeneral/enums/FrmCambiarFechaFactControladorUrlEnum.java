/**
 * 
 */
package com.sysman.facturaciongeneral.enums;

/**
 * 
 */
public enum FrmCambiarFechaFactControladorUrlEnum {

	URL0001("FRMCAMBIARFECHAFACTCONTROLADOR0001", "59003"), 
	
	URL4002("FRMCAMBIARFECHAFACTCONTROLADOR4002", "4002"),
	
	URL665015("FRMCAMBIARFECHAFACTCONTROLADOR665015", "665015"),
	
	URL661080("FRMCAMBIARFECHAFACTCONTROLADOR0080", "661080"),
	
	URL661082("FRMCAMBIARFECHAFACTCONTROLADOR0082", "661082"),
	
	URL661031("FRMCAMBIARFECHAFACTCONTROLADOR0031", "661031");

	private final String key;
	private final String value;

	private FrmCambiarFechaFactControladorUrlEnum(String key, String value) {
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
