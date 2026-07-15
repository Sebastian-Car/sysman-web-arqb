/*
 * PrepararSigPeriodoControladorUrlEnum
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
public enum PrepararSigPeriodoControladorUrlEnum {

    URL8038("PREPARARSIGPERIODOCONTROLADORURL8038", "214084"),

    URL8039("PREPARARSIGPERIODOCONTROLADORURL8039", "240001"),

    URL8040("PREPARARSIGPERIODOCONTROLADORURL8040", "237001"),

    URL9512("PREPARARSIGPERIODOCONTROLADORURL9512", " ");

    private final String key;
    private final String value;

    private PrepararSigPeriodoControladorUrlEnum(String key, String value) {
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
