/*
 * RevisainterfacefactsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum RevisainterfacefactsControladorUrlEnum {

    URL6922("REVISAINTERFACEFACTSCONTROLADORURL6922", "748003"),

    URL8446("REVISAINTERFACEFACTSCONTROLADORURL8446", "16122"),

    URL10250("REVISAINTERFACEFACTSCONTROLADORURL10250", "4001"),

    URL6969("REVISAINTERFACEFACTSCONTROLADORURL6969", "16008"),

    URL7171("REVISAINTERFACEFACTSCONTROLADORURL7171", "20003"),

    URL9874("REVISAINTERFACEFACTSCONTROLADORURL9874", "747001");

    private final String key;
    private final String value;

    private RevisainterfacefactsControladorUrlEnum(String key, String value) {
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
