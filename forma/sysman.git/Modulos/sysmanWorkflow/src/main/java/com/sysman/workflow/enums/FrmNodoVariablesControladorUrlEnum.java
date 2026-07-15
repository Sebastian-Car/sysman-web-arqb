/*
 * FrmNodoVariablesControladorUrlEnum
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
public enum FrmNodoVariablesControladorUrlEnum {

    URL001("FRMNODOVARIABLESCONTROLADORURL001", "1035005"),

    URL5515("FRMNODOVARIABLESCONTROLADORURL5515", "1032003"),

    URL0001("FRMNODOVARIABLESCONTROLADORURL0001", "210126"),

    URL0004("FRMNODOVARIABLESCONTROLADORURL0004", "141092"),

    URL0003("FRMNODOVARIABLESCONTROLADORURL0003", "112032"),

    URL0002("FRMNODOVARIABLESCONTROLADORURL0002", "62002");

    private final String key;
    private final String value;

    private FrmNodoVariablesControladorUrlEnum(String key, String value) {
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
