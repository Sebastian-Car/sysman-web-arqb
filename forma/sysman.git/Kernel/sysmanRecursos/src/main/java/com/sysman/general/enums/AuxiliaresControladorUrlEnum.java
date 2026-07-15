/*
 * AuxiliaresControladorUrlEnum
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
public enum AuxiliaresControladorUrlEnum {

    URL4212("AUXILIARESCONTROLADORURL4212",
                    "32001"),

    URL3785("AUXILIARESCONTROLADORURL3785",
                    " listaAnio = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT \" + \" NUMERO\" + \" FROM \" + \" ANO\" + \" WHERE\" + \" COMPANIA = '\" + compania + \"' \" + \" AND\" + \" NUMERO NOT IN 0 \" + \" ORDER BY COMPANIA,\" + \" NUMERO DESC\");"),

    URL3193("AUXILIARESCONTROLADORURL3193", "4001"),

    URL001("AUXILIARESCONTROLADORURL001", "1697001"),
    
    URL1766001("AUXILIARESCONTROLADORURLURL1766001", "1766001"),
    URL23061("CENTROSCOSTOSCONTROLADORURL20084",
            "23061");

    private final String key;
    private final String value;

    private AuxiliaresControladorUrlEnum(String key, String value) {
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
