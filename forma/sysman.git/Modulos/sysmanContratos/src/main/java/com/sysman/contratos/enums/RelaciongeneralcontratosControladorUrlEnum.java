/*
 * RelaciongeneralcontratosControladorUrlEnum
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
public enum RelaciongeneralcontratosControladorUrlEnum {

    URL2741("RELACIONGENERALCONTRATOSCONTROLADORURL2741", "4001"),

    URL3138("RELACIONGENERALCONTRATOSCONTROLADORURL3138", "7001");

    private final String key;
    private final String value;

    private RelaciongeneralcontratosControladorUrlEnum(String key,
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
