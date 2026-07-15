/*
 * LisdispabiertascuentasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LisdispabiertascuentasControladorUrlEnum {

    URL7992("LISDISPABIERTASCUENTASCONTROLADORURL7992", "13014"),
    /**
     * 94052 getVistaplanespresupuestalesPagPorNaturalezaDInicialQuery
     */
    URL4725("LISDISPABIERTASCUENTASCONTROLADORURL4725", "94052"),

    URL7282("LISDISPABIERTASCUENTASCONTROLADORURL7282", "13010"),
    /**
     * 94054 getVistaplanespresupuestalesPagPorNaturalezaDFinalQuery
     */
    URL5957("LISDISPABIERTASCUENTASCONTROLADORURL5957", "94054");

    private final String key;
    private final String value;

    private LisdispabiertascuentasControladorUrlEnum(String key, String value) {
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
