/*
 * RelacionPrestamoBienesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RelacionPrestamoBienesControladorUrlEnum {

    URL7682("RELACIONPRESTAMOBIENESCONTROLADORURL7682", "141056"),

    URL5558("RELACIONPRESTAMOBIENESCONTROLADORURL5558", "141085"),

    URL8655("RELACIONPRESTAMOBIENESCONTROLADORURL8655", "141058"),

    URL6612("RELACIONPRESTAMOBIENESCONTROLADORURL6612", "141083");

    private final String key;
    private final String value;

    private RelacionPrestamoBienesControladorUrlEnum(String key, String value) {
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
