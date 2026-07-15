/*
 * ConfiguracionDescuentosPlanContableControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.sia.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfiguracionDescuentosPlanContableControladorUrlEnum {

    URL12866("CONFIGURACIONDESCUENTOSPLANCONTABLECONTROLADORURL12866", "16171"),

    URL15229("CONFIGURACIONDESCUENTOSPLANCONTABLECONTROLADORURL15229", ""),

    URL7294("CONFIGURACIONDESCUENTOSPLANCONTABLECONTROLADORURL7294", "4001"),

    URL14120("CONFIGURACIONDESCUENTOSPLANCONTABLECONTROLADORURL14120", "16167"), // 16169

    URL6081("CONFIGURACIONDESCUENTOSPLANCONTABLECONTROLADORURL6081", "16167");

    private final String key;
    private final String value;

    private ConfiguracionDescuentosPlanContableControladorUrlEnum(String key,
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
