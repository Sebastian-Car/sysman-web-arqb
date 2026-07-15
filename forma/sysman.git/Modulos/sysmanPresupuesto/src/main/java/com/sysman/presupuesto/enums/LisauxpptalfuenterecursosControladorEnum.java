/**
 * 
 */
package com.sysman.presupuesto.enums;

/**
 * @author dcastiblanco
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum LisauxpptalfuenterecursosControladorEnum {
	PARAM8("CODIGOINICIAL"),
	
	CODIGOINICIAL("CODIGOINICIAL"),
	
	PARAM3("CUENTAINICIAL");

    private final String value;

    private LisauxpptalfuenterecursosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


