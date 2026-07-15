/*
 * FrmNodosControladorUrlEnum
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
public enum FrmNodosControladorUrlEnum {

    URL003("FRMNODOSCONTROLADORURL003", "1037007"),

    URL001("FRMNODOSCONTROLADORURL001", "1037005"),

    URL9097("FRMNODOSCONTROLADORURL9097", "1036001"),

    URL6936("FRMNODOSCONTROLADORURL6936", "1032003"),

    URL7996("FRMNODOSCONTROLADORURL7996", "988003"),

    URL0002("FRMNODOSCONTROLADORURL0002", "104065"),

    URL0003("FRMNODOSCONTROLADORURL0003", "63003"),

    URL8542("FRMNODOSCONTROLADORURL8542", "62002"),

    URL0001("FRMNODOSCONTROLADORURL0001", "58001");

    private final String key;
    private final String value;

    private FrmNodosControladorUrlEnum(String key, String value) {
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
