/*
 * ParametrosdeentradasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ParametrosdeentradasControladorUrlEnum {

    URL21911("PARAMETROSDEENTRADASCONTROLADORURL21911", "631001"),

    URL10917("PARAMETROSDEENTRADASCONTROLADORURL10917", "2005"),

    URL12394("PARAMETROSDEENTRADASCONTROLADORURL12394", "1001"),

    URL14200("PARAMETROSDEENTRADASCONTROLADORURL14200", "59017"),

    URL13125("PARAMETROSDEENTRADASCONTROLADORURL13125", "59015"),

    URL10095("PARAMETROSDEENTRADASCONTROLADORURL10095", "5001"),

    URL16289("PARAMETROSDEENTRADASCONTROLADORURL16289", "151015"),

    URL20297("PARAMETROSDEENTRADASCONTROLADORURL20297", "59019"),

    URL20298("PARAMETROSDEENTRADASCONTROLADORURL20298", "944001");

    private final String key;
    private final String value;

    private ParametrosdeentradasControladorUrlEnum(String key, String value) {
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
