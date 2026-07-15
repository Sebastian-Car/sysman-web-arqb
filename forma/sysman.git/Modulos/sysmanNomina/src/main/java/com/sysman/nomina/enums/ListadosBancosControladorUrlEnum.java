/*
 * ListadosBancosControladorUrlEnum
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
public enum ListadosBancosControladorUrlEnum {

    URL5765("LISTADOSBANCOSCONTROLADORURL5765", "471008"),

    URL6657("LISTADOSBANCOSCONTROLADORURL6657", "7027"),

    URL8051("LISTADOSBANCOSCONTROLADORURL8051", "471010");

    private final String key;
    private final String value;

    private ListadosBancosControladorUrlEnum(String key, String value) {
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
