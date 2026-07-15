/*
 * NatSubRenunciasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NatSubComisionesControladorUrlEnum {

    URL210("NATSUBCOMISIONESCONTROLADORURL210", "1001"),

    URL234("NATSUBCOMISIONESCONTROLADORURL234", "2001"),

    URL261("NATSUBCOMISIONESCONTROLADORURL261", "5001"),

    URL282("NATSUBCOMISIONESCONTROLADORURL282", "463003");

    private final String key;
    private final String value;

    private NatSubComisionesControladorUrlEnum(String key, String value) {
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
