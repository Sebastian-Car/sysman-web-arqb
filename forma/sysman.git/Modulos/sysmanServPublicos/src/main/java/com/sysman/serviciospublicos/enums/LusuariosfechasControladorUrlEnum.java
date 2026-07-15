/*
 * LusuariosfechasControladorUrlEnum
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
public enum LusuariosfechasControladorUrlEnum {

    URL6272("LUSUARIOSFECHASCONTROLADORURL6272",
                    "345001"),

    URL8360("LUSUARIOSFECHASCONTROLADORURL8360",
                    "47009"),

    URL7046("LUSUARIOSFECHASCONTROLADORURL7046",
                    "345003"),

    URL7826("LUSUARIOSFECHASCONTROLADORURL7826",
                    "47007");

    private final String key;
    private final String value;

    private LusuariosfechasControladorUrlEnum(String key, String value) {
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
