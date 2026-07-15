/*
 * ImportarItemsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ImportarItemsControladorUrlEnum {

    URL3293("IMPORTARITEMSCONTROLADORURL3293", "188001"), 
    
    URL5417("IMPORTARITEMSCONTROLADORURL5417", "184001"),
    
    URL7548("IMPORTARITEMSCONTROLADORURL7548", "188013");

    private final String key;
    private final String value;

    private ImportarItemsControladorUrlEnum(String key, String value) {
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
