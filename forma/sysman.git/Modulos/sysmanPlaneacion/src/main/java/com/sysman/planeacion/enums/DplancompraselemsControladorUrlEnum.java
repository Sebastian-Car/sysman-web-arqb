/*
 * DplancompraselemsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DplancompraselemsControladorUrlEnum {

    /**
     * 112108 getInventariosPagElementosConMovimientoMaxValorQuery
     */
    URL6472("DPLANCOMPRASELEMSCONTROLADORURL6472", "112108"),

    /**
     * 61021 getResponsablesPagPorDependenciaQuery
     */
    URL8810("DPLANCOMPRASELEMSCONTROLADORURL8810", "61021"),

    /**
     * 108002 getTipoadjudicacionesConMovimientoQuery
     */
    URL5995("DPLANCOMPRASELEMSCONTROLADORURL5995", "108002"),

    /**
     * 114008 getDetallesplancomprasVlrProgramadoPorDependenciaQuery
     */
    URL33283("DPLANCOMPRASELEMSCONTROLADORURL33283", "114008");

    private final String key;
    private final String value;

    private DplancompraselemsControladorUrlEnum(String key, String value) {
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
