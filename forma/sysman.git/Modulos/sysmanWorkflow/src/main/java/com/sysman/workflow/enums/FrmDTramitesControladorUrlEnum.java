/*
 * FrmDTramitesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.workflow.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmDTramitesControladorUrlEnum {

    URL002("FRMDTRAMITESCONTROLADORURL002", "1047003"),

    URL4734("FRMDTRAMITESCONTROLADORURL4734", "1047001"),

    URL5123("FRMDTRAMITESCONTROLADORURL5123", "1038001"),

    URL003("FRMDTRAMITESCONTROLADORURL003", "778005"),

    URL6021("FRMDTRAMITESCONTROLADORURL6021", "778001"),

    URL001("FRMDTRAMITESCONTROLADORURL001", "47022"),

    URL1035012("FRMDTRAMITESCONTROLADORURL1035012", "1035012"),
    
    URL004("FRMDTRAMITESCONTROLADORURL004" ,"1825001"),
    
    URL1035010("FRMDTRAMITESCONTROLADORURL004" ,"1035010"),
    
    ;

    private final String key;
    private final String value;

    private FrmDTramitesControladorUrlEnum(String key, String value) {
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
