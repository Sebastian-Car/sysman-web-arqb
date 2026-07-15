/*
 * TarifasespecialesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum TarifasespecialesControladorUrlEnum {

    URL3144("TARIFASESPECIALESCONTROLADORURL3144", "4001"),

    URL3483("TARIFASESPECIALESCONTROLADORURL3483", "381011"),

    URL4236("TARIFASESPECIALESCONTROLADORURL4236", "381011"),

    URL11200("TARIFASESPECIALESCONTROLADORURL11200", "381011"),

    URL5929("TARIFASESPECIALESCONTROLADORURL5929", "367192"),

    URL5037("TARIFASESPECIALESCONTROLADORURL5037", "367192");

    private final String key;
    private final String value;

    private TarifasespecialesControladorUrlEnum(String key, String value) {
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
