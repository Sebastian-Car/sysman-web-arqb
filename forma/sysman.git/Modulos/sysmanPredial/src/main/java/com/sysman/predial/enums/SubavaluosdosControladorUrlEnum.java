/*
 * SubavaluosdosControladorUrlEnum
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
public enum SubavaluosdosControladorUrlEnum {

    URL0001("SUBAVALUOSDOSCONTROLADORURL0001", "385023"),

    URL0002("SUBAVALUOSDOSCONTROLADORURL0002", "385027"),

    URL0003("SUBAVALUOSDOSCONTROLADORURL0003", "385026"),

    URL0004("SUBAVALUOSDOSCONTROLADORURL0004", "38500D"),

    URL0005("SUBAVALUOSDOSCONTROLADORURL0005", "385031"),

    URL4979("SUBAVALUOSDOSCONTROLADORURL4979", "376002"),

    URL5659("SUBAVALUOSDOSCONTROLADORURL5659", "376012"),

    URL4703("SUBAVALUOSDOSCONTROLADORURL4703", "4013");

    private final String key;
    private final String value;

    private SubavaluosdosControladorUrlEnum(String key, String value) {
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
