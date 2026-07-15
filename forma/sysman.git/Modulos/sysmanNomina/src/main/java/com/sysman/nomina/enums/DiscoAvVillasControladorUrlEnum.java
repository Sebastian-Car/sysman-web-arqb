/*
 * DiscoAvVillasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
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
public enum DiscoAvVillasControladorUrlEnum {

    /**
     * 471002 getPeriodosAniosPorCompaniaQuery
     */
    URL5654("DISCOAVVILLASCONTROLADORURL5654", "471002"),

    /**
     * 7024 getMesesPorPeriodosYCompaniaQuery
     */
    URL6123("DISCOAVVILLASCONTROLADORURL6123", "7024"),

    /**
     * 537002 getProcesosdenominaPorCompaniaDifCeroQuery
     */
    URL7582("DISCOAVVILLASCONTROLADORURL7582", "537002"),

    /**
     * 459001 getBancosnominaPagTodosPorBancoQuery
     */
    URL8420("DISCOAVVILLASCONTROLADORURL8420", "459001"),

    /**
     * 471025 getPeriodosCodigoNombrePorAnoYMesQuery
     */
    URL24268("DISCOAVVILLASCONTROLADORURL24268", "471025");

    private final String key;
    private final String value;

    private DiscoAvVillasControladorUrlEnum(String key, String value) {
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
