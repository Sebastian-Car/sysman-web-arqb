/**
 * 
 */
package com.sysman.almacen.enums;

/**
 * @author dcastiblanco
 *Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum InformeDevoControInvenControladorEnum {
	
	ELEMENTOINICIAL("ELEMENTOINICIAL");
	
	private final String value;

    private InformeDevoControInvenControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}