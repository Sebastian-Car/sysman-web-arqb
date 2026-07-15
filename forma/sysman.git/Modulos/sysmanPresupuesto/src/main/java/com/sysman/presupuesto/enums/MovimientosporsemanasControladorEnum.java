/**
 * 
 */
package com.sysman.presupuesto.enums;

/**
 * @author dcastiblanco
 *
 */
public enum MovimientosporsemanasControladorEnum {
	
	PARAM3("CUENTAINICIAL");
	
	 private final String value;

    private MovimientosporsemanasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
