/*
 * FrmDNodosControladorUrlEnum
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
public enum FrmDNodosControladorUrlEnum {

    URL4334("FRMDNODOSCONTROLADORURL4334", "1035001"),

    URL3984("FRMDNODOSCONTROLADORURL3984", "1032003"),

    URL5169("FRMDNODOSCONTROLADORURL5169", "988005");

    private final String key;
    private final String value;

    private FrmDNodosControladorUrlEnum(String key, String value) {
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
