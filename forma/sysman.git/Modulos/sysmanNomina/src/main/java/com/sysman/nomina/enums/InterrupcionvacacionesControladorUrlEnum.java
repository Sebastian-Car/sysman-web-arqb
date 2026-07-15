/*
 * NovedadesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InterrupcionvacacionesControladorUrlEnum {

    URL28529("INTERRPCIONVACACIONESCONTROLADORURL28529", "625001"),

    URL15494("INTERRPCIONVACACIONESCONTROLADORURL15494", "471036"),

    URL17692("INTERRPCIONVACACIONESCONTROLADORURL17692", "471038"),

    URL17693("INTERRPCIONVACACIONESCONTROLADORURL17693", "210038");

    private final String key;
    private final String value;

    private InterrupcionvacacionesControladorUrlEnum(String key, String value) {
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
