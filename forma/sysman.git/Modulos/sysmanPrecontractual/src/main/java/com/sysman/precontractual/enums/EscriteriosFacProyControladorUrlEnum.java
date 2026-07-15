/*
 * EscriteriosFacProyControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EscriteriosFacProyControladorUrlEnum {

    URL10077("ESCRITERIOSFACPROYCONTROLADORURL10077",
                    "483001"),

    URL4860("ESCRITERIOSFACPROYCONTROLADORURL4860",
                    "480001"),

    URL11385("ESCRITERIOSFACPROYCONTROLADORURL11385",
                    "48000C"),

    URL15446("ESCRITERIOSFACPROYCONTROLADORURL15446",
                    "480002"),

    URL10152("ESCRITERIOSFACPROYCONTROLADORURL10152",
                    "483002"),

    URL8348("ESCRITERIOSFACPROYCONTROLADORURL8348",
                    "48000U"),

    URL7106("ESCRITERIOSFACPROYCONTROLADORURL7106",
                    "481002"),

    URL6976("ESCRITERIOSFACPROYCONTROLADORURL6976",
                    "481001");

    private final String key;
    private final String value;

    private EscriteriosFacProyControladorUrlEnum(String key, String value) {
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
