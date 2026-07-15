/*
 * RptclaseControladorUrlEnum
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
public enum RptclaseControladorUrlEnum {

    URL4586("RPTCLASECONTROLADORURL4586", "367088"),

    URL5550("RPTCLASECONTROLADORURL5550", "380005"),

    URL3306("RPTCLASECONTROLADORURL3306", "380001"),

    URL3739("RPTCLASECONTROLADORURL3739", "367086");

    private final String key;
    private final String value;

    private RptclaseControladorUrlEnum(String key, String value) {
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
