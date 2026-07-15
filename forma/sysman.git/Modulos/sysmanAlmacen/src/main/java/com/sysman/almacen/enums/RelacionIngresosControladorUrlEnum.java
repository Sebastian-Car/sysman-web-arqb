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
public enum RelacionIngresosControladorUrlEnum {

    URL7682("RELACIONINGRESOSCONTROLADORURL7682", "4013"),

    URL5558("RELACIONINGRESOSCONTROLADORURL5558", "141085"),

    URL8655("RELACIONINGRESOSCONTROLADORURL8655", "141058"),

    URL6612("RELACIONINGRESOSCONTROLADORURL6612", "141083");

    private final String key;
    private final String value;

    private RelacionIngresosControladorUrlEnum(String key, String value) {
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
