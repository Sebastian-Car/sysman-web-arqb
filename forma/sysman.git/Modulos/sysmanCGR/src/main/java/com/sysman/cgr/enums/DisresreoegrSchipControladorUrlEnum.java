/*
 * DisresreoegrSchipControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.cgr.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DisresreoegrSchipControladorUrlEnum {

    URL6241("DISRESREOEGRSCHIPCONTROLADORURL6241", "4002"),

    URL7731("DISRESREOEGRSCHIPCONTROLADORURL7731", "45020"),

    URL6711("DISRESREOEGRSCHIPCONTROLADORURL6711", "45018");

    private final String key;
    private final String value;

    private DisresreoegrSchipControladorUrlEnum(String key, String value) {
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
