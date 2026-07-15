/*
 * AcumFinanciablesControladorUrlEnum
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
public enum AcumFinanciablesControladorUrlEnum {

    URL6877("ACUMFINANCIABLESCONTROLADORURL6877",
                    "214022"),

    URL6085("ACUMFINANCIABLESCONTROLADORURL6085",
                    " listaAnoFinal = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT ANO\" + \" FROM SP_PERIODO\" + \" WHERE COMPANIA='\" + compania + \"'\" + \" GROUP BY ANO\");"),

    URL5272("ACUMFINANCIABLESCONTROLADORURL5272",
                    "227001"),

    URL6478("ACUMFINANCIABLESCONTROLADORURL6478",
                    "227002"),

    URL5673("ACUMFINANCIABLESCONTROLADORURL5673",
                    "227002");

    private final String key;
    private final String value;

    private AcumFinanciablesControladorUrlEnum(String key, String value) {
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
