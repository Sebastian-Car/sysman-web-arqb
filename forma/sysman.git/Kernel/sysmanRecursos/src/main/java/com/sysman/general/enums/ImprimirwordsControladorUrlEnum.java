/*
 * ImprimirwordsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ImprimirwordsControladorUrlEnum {

    /**
     * 100006 getModelovariablesPorPlantillaUsuarioQuery
     */
    URL10399("IMPRIMIRWORDSCONTROLADORURL10399", "100006"),

    /**
     * 104058 getModeloplantillasPlantillaPorCodigoYFechaQuery
     */
    URL31970("IMPRIMIRWORDSCONTROLADORURL31970", "104058");

    private final String key;
    private final String value;

    private ImprimirwordsControladorUrlEnum(String key, String value) {
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
