/*
 * FcregistrocuentasxpagarControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FcregistrocuentasxpagarControladorUrlEnum {

    URL9701("FCREGISTROCUENTASXPAGARCONTROLADORURL9701", "20013"),

    URL6992("FCREGISTROCUENTASXPAGARCONTROLADORURL6992", "7007"),

    URL10414("FCREGISTROCUENTASXPAGARCONTROLADORURL10414", "20015"),

    URL11981("FCREGISTROCUENTASXPAGARCONTROLADORURL11981", "34003"),

    URL7524("FCREGISTROCUENTASXPAGARCONTROLADORURL7524", "45018"),

    URL11246("FCREGISTROCUENTASXPAGARCONTROLADORURL11246", "34001"),

    URL5994("FCREGISTROCUENTASXPAGARCONTROLADORURL5994", "4002"),

    URL6470("FCREGISTROCUENTASXPAGARCONTROLADORURL6470", "7007"),

    URL8534("FCREGISTROCUENTASXPAGARCONTROLADORURL8534", "45020");

    private final String key;
    private final String value;

    private FcregistrocuentasxpagarControladorUrlEnum(String key,
        String value) {
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
