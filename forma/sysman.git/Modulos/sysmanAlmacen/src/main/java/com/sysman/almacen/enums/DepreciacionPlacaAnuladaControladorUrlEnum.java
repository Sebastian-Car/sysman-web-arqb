/*
 * DepreciacionPlacaAnuladaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DepreciacionPlacaAnuladaControladorUrlEnum {

    /**
     * 4002 getAnosHastaActualQuery
     */
    URL8508("DEPRECIACIONPLACAANULADACONTROLADORURL8508", "4002"),

    /**
     * 7001 getMesesTodospornumeroQuery
     */
    URL8996("DEPRECIACIONPLACAANULADACONTROLADORURL8996", "7001"),

    /**
     * 20040 getCentrocostosNumTodosPorCodigoAnoQuery
     */
    URL10184("DEPRECIACIONPLACAANULADACONTROLADORURL10184", "20040"),

    /**
     * 112004
     * getInventariosPagTodosPorNombreCodigoEnDneMayorIgualCodigoQuery
     */
    URL10851("DEPRECIACIONPLACAANULADACONTROLADORURL10851", "112004"),

    /**
     * 20042
     * getApropiacionesinicialesNumApropiacionesInicialesDiferentesQuery
     */
    URL9428("DEPRECIACIONPLACAANULADACONTROLADORURL9428", "20042"),

    /**
     * 112002 getInventariosPagTodosPorNombreCodigoEnDneQuery
     */
    URL11633("DEPRECIACIONPLACAANULADACONTROLADORURL11633", "112002");

    private final String key;
    private final String value;

    private DepreciacionPlacaAnuladaControladorUrlEnum(String key,
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
