/*
 * ControlPagosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ControlPagosControladorUrlEnum {

    URL5289("CONTROLPAGOSCONTROLADORURL5289", "82058"),

    URL7595("CONTROLPAGOSCONTROLADORURL7595", "82050"),

    URL6611("CONTROLPAGOSCONTROLADORURL6611", "73019"),

    URL8430("CONTROLPAGOSCONTROLADORURL8430", "73021");

    private final String key;
    private final String value;

    private ControlPagosControladorUrlEnum(String key, String value) {
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
