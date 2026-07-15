package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */

public enum InformeGeneralControladoControladorEnum {
	
	CODIGOELEMENTO("CODIGOELEMENTO"),

    ELEMENTOINICIAL("ELEMENTODESDE"),
    
    TIPOELEMENTO("TIPOELEMENTO"),
    
    ELEMENTO("M");

    private final String value;

    private InformeGeneralControladoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
