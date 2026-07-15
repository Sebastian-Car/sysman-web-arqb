/*
 * TipoClaseEventoSstsControladorUrlEnum
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
public enum TipoClaseEventoSstsControladorUrlEnum {

    URL4132("TIPOCLASEEVENTOSSTSCONTROLADORURL4132", "727001"),

    URL4846("TIPOCLASEEVENTOSSTSCONTROLADORURL4846", "727001");

    private final String key;
    private final String value;

    private TipoClaseEventoSstsControladorUrlEnum(String key, String value) {
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
