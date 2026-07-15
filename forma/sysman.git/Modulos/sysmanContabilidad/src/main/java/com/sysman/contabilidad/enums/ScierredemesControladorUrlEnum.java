/*
 * ScierredemesControladorUrlEnum
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
public enum ScierredemesControladorUrlEnum {
    // Listas
    URL9650("SCIERREDEMESCONTROLADORURL9650", "4001"),

    URL7752("SCIERREDEMESCONTROLADORURL7752", "16035"),

    URL11796("SCIERREDEMESCONTROLADORURL11796", "14036"),

    URL13426("SCIERREDEMESCONTROLADORURL13426", "14036"),

    URL13676("SCIERREDEMESCONTROLADORURL13676", "116002"),

    URL13629("SCIERREDEMESCONTROLADORURL13629", "116004"),

    URL6972("SCIERREDEMESCONTROLADORURL6972", "16035"),

    URL8533("SCIERREDEMESCONTROLADORURL8533", "16038"),

    URL12713("SCIERREDEMESCONTROLADORURL12713", "15017"),

    URL12734("SCIERREDEMESCONTROLADORURL12734", "4014"),

    URL12735("SCIERREDEMESCONTROLADORURL12735", "116008"),

    URL12737("SCIERREDEMESCONTROLADORURL12737", "116006"),

    URL15755("SCIERREDEMESCONTROLADORURL15755", "116007");

    private final String key;
    private final String value;

    private ScierredemesControladorUrlEnum(String key, String value) {
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
