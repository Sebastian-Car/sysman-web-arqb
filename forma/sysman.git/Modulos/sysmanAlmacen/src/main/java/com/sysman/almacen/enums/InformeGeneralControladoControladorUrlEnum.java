package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */

public enum InformeGeneralControladoControladorUrlEnum {
	
	URL112044("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL4740", "112044"),

    URL112046("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL5626", "112046");
    

    private final String key;
    private final String value;

    private InformeGeneralControladoControladorUrlEnum(String key,
        String value) {
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
