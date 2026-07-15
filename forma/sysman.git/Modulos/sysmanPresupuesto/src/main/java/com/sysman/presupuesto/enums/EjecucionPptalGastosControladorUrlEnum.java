/*
 * EjecucionPptalGastosControladorUrlEnum
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
public enum EjecucionPptalGastosControladorUrlEnum {

    URL7993("EJECUCIONPPTALGASTOSCONTROLADORURL7993", "20013"),

    URL10036("EJECUCIONPPTALGASTOSCONTROLADORURL10036", "23019"),

    URL9413("EJECUCIONPPTALGASTOSCONTROLADORURL9413", "23010"),

    URL8700("EJECUCIONPPTALGASTOSCONTROLADORURL8700", "20015"),

    URL5600("EJECUCIONPPTALGASTOSCONTROLADORURL5600", "4001"),

    URL4686("EJECUCIONPPTALGASTOSCONTROLADORURL4686", "7013"),

    URL5111("EJECUCIONPPTALGASTOSCONTROLADORURL5111", "7004"),

    URL5941("EJECUCIONPPTALGASTOSCONTROLADORURL5941", "94036"),

    URL6882("EJECUCIONPPTALGASTOSCONTROLADORURL6882", "94034");

    private final String key;
    private final String value;

    private EjecucionPptalGastosControladorUrlEnum(String key, String value) {
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
