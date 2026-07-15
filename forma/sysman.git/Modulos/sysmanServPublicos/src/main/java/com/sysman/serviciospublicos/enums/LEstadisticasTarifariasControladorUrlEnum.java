/*
 * LEstadisticasTarifariasControladorUrlEnum
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
public enum LEstadisticasTarifariasControladorUrlEnum {

    URL9147("LESTADISTICASTARIFARIASCONTROLADORURL9147", "227030"),

    URL9972("LESTADISTICASTARIFARIASCONTROLADORURL9972", "227032"),

    URL8288("LESTADISTICASTARIFARIASCONTROLADORURL8288", "227031"),

    URL6848("LESTADISTICASTARIFARIASCONTROLADORURL6848", "214029"),

    URL7523("LESTADISTICASTARIFARIASCONTROLADORURL7523", "227029");

    private final String key;
    private final String value;

    private LEstadisticasTarifariasControladorUrlEnum(String key,
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
