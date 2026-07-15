/*
 * ExtractoporterceroControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ExtractoporterceroControladorUrlEnum {

    URL7712("EXTRACTOPORTERCEROCONTROLADORURL7712", "14067"),

    URL8423("EXTRACTOPORTERCEROCONTROLADORURL8423", "14033"),

    URL5790("EXTRACTOPORTERCEROCONTROLADORURL5790", "29007"),

    URL4409("EXTRACTOPORTERCEROCONTROLADORURL4409", "7003"),

    URL6690("EXTRACTOPORTERCEROCONTROLADORURL6690", "29009"),

    URL3927("EXTRACTOPORTERCEROCONTROLADORURL3927", "4001");

    private final String key;
    private final String value;

    private ExtractoporterceroControladorUrlEnum(String key, String value) {
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
