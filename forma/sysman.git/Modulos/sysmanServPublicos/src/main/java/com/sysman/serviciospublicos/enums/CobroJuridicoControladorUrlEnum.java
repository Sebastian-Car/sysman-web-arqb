/*
 * CobroJuridicoControladorUrlEnum
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
public enum CobroJuridicoControladorUrlEnum {

    URL8448("COBROJURIDICOCONTROLADORURL8448", "214007"),

    URL8867("COBROJURIDICOCONTROLADORURL8867", "104011"),

    URL10536("COBROJURIDICOCONTROLADORURL10536", "213022"),

    URL9420("COBROJURIDICOCONTROLADORURL9420", "213020"),

    URL001("COBROJURIDICOCONTROLADORURL001", "104014");
    ;

    private final String key;
    private final String value;

    private CobroJuridicoControladorUrlEnum(String key, String value) {
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
