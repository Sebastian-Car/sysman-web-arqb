/*
 * InformerevisionmedidorControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum InformerevisionmedidorControladorUrlEnum {

    URL5007("INFORMEREVISIONMEDIDORCONTROLADORURL5007","214031"),
    URL5426("INFORMEREVISIONMEDIDORCONTROLADORURL5426","366010"),
    URL6135("INFORMEREVISIONMEDIDORCONTROLADORURL6135","366012");
    private final String key;
    private final String value;

    private  InformerevisionmedidorControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
