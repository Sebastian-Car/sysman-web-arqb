/*
 * ComprobanteDiferidoControladorUrlEnum
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
public enum ComprobanteDiferidoControladorUrlEnum {

    URL4465("COMPROBANTEDIFERIDOCONTROLADORURL4465", "14001"),

    URL5040("COMPROBANTEDIFERIDOCONTROLADORURL5040", "14031"),

    URL3778("COMPROBANTEDIFERIDOCONTROLADORURL3778", "59003"),

    URL4162("COMPROBANTEDIFERIDOCONTROLADORURL4162", "4009"),

    URL88001("COMPROBANTEDIFERIDOCONTROLADORURL88001", "88001"),

    URL1865001("COMPROBANTEDIFERIDOCONTROLADORURL1865001", "1865001"),

    URL1865002("COMPROBANTEDIFERIDOCONTROLADORURL18650012", "1865002"),;

    private final String key;
    private final String value;

    private ComprobanteDiferidoControladorUrlEnum(String key, String value) {
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
