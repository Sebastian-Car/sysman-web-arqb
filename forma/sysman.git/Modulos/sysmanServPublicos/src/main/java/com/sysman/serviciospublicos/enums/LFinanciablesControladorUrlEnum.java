/*
 * LFinanciablesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LFinanciablesControladorUrlEnum {

    URL8972("LFINANCIABLESCONTROLADORURL8972",
                    "213006"),

    URL8339("LFINANCIABLESCONTROLADORURL8339",
                    "213004"),

    URL12364("LFINANCIABLESCONTROLADORURL12364",
                    "215036"),

    URL7020("LFINANCIABLESCONTROLADORURL7020",
                    "227007"),

    URL9703("LFINANCIABLESCONTROLADORURL9703",
                    "214065"),

    URL6617("LFINANCIABLESCONTROLADORURL6617",
                    "227001"),

    URL7480("LFINANCIABLESCONTROLADORURL7480",
                    "227003"),

    URL7876("LFINANCIABLESCONTROLADORURL7876",
                    "227007"),

    URL11814("LFINANCIABLESCONTROLADORURL11814",
                    "215034");

    private final String key;
    private final String value;

    private LFinanciablesControladorUrlEnum(String key, String value) {
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
