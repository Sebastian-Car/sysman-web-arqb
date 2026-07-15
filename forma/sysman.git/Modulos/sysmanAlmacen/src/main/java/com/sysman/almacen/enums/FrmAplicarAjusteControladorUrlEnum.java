/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * 
 */
public enum FrmAplicarAjusteControladorUrlEnum {

	URL139034("APLICARAJUSTECONTROLADORURL139034", "139034"),
	 
	URL62029("APLICARAJUSTECONTROLADORURL62029", "62029"),
	
	URL61016("APLICARAJUSTECONTROLADORURL61016", "61016");
	
	
	private final String key;
    private final String value;

    private FrmAplicarAjusteControladorUrlEnum(String key, String value) {
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




