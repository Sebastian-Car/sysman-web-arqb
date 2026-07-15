/*
 * NominaresumentotalControladorUrlEnum
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
public enum NominaresumentotalControladorUrlEnum {

    URL5682("NOMINARESUMENTOTALCONTROLADORURL5682", "471010"),

    URL6322("NOMINARESUMENTOTALCONTROLADORURL6322", "471002"),

    URL10155("NOMINARESUMENTOTALCONTROLADORURL10155", "537002"),

    URL7253("NOMINARESUMENTOTALCONTROLADORURL7253", "7024"),

    URL10936("NOMINARESUMENTOTALCONTROLADORURL10936", "614001"),

    URL11560("NOMINARESUMENTOTALCONTROLADORURL11560", "629001");

    private final String key;
    private final String value;

    private NominaresumentotalControladorUrlEnum(String key, String value) {
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
