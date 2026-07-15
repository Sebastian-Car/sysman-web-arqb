/*
 * RegistrovigenciasfutschipsControladorUrlEnum
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
public enum RegistrovigenciasfutschipsControladorUrlEnum {

    URL7112("REGISTROVIGENCIASFUTSCHIPSCONTROLADORURL7112", "7012"),

    URL6537("REGISTROVIGENCIASFUTSCHIPSCONTROLADORURL6537", "7007"),

    URL9367("REGISTROVIGENCIASFUTSCHIPSCONTROLADORURL9367", "430008"),

    URL8389("REGISTROVIGENCIASFUTSCHIPSCONTROLADORURL8389", "430006"),

    URL7757("REGISTROVIGENCIASFUTSCHIPSCONTROLADORURL7757", "4007");

    private final String key;
    private final String value;

    private RegistrovigenciasfutschipsControladorUrlEnum(String key,
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
