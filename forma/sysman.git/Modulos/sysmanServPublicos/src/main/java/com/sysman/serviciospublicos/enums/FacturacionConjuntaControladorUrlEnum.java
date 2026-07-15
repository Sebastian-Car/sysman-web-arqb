/*
 * FacturacionConjuntaControladorUrlEnum
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
public enum FacturacionConjuntaControladorUrlEnum {

    URL8695("FACTURACIONCONJUNTACONTROLADORURL8695",
                    "227007"),

    URL6132("FACTURACIONCONJUNTACONTROLADORURL6132",
                    "213059"),

    URL7503("FACTURACIONCONJUNTACONTROLADORURL7503",
                    "214031"),

    URL8108("FACTURACIONCONJUNTACONTROLADORURL8108",
                    "227001"),

    URL6940("FACTURACIONCONJUNTACONTROLADORURL6940",
                    "213061");

    private final String key;
    private final String value;

    private FacturacionConjuntaControladorUrlEnum(String key, String value) {
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
