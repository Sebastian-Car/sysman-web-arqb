/*
 * InventarioIndividualControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
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
public enum InventarioIndividualControladorUrlEnum {

    URL6302("INVENTARIOINDIVIDUALCONTROLADORURL6302", "61012"),
    /**
     * getResponsablesPagTodosporcompaniaydocumentoQuery
     */
    URL2855("PAZYSALVOTERCEROCONTROLADORURL2855", "61036"),
    ;

    private final String key;
    private final String value;

    private InventarioIndividualControladorUrlEnum(String key, String value) {
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
