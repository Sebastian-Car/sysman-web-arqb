/*
 * AcummensualControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum GeneracionPlanosDaviviendaControladorUrlEnum {

    URL001("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL001", "72076"),

    URL002("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL002", "72078"),

    URL003("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL003", "15063"),

    URL004("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL004", "36002"),

    URL005("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL005", "16151"),

    URL006("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL006", "16153"),

    URL007("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL007", "4001"),

    URL008("GENERACIONPLANOSBANCOLOMBIACONTROLADORURL008", "72080");

    private final String key;
    private final String value;

    private GeneracionPlanosDaviviendaControladorUrlEnum(String key,
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
