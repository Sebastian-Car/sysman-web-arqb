/*
 * PlanillaPrimaDiciembreControladorUrlEnum
 *
 * 1.0
 *
 * 19/10/2017
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
public enum InformeTiempoServicioControladorUrlEnum {

    URL28520("INFORMETIEMPOSERVICIOCONTROLADORURL28520", "210076"),

    URL28521("INFORMETIEMPOSERVICIOCONTROLADORURL28521", "210092"),

    URL28522("INFORMETIEMPOSERVICIOCONTROLADORURL28522", "210094");

    private final String key;
    private final String value;

    private InformeTiempoServicioControladorUrlEnum(String key, String value) {
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
