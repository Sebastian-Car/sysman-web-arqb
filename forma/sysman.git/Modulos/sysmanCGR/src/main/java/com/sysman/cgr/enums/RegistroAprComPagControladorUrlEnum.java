/*
 * RegistroAprComPagControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.cgr.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RegistroAprComPagControladorUrlEnum {

    /**
     * 4001 getAnosTodosPorNumeroDescQuery
     */
    URL7684("REGISTROAPRCOMPAGCONTROLADORURL7684", "4001"),

    /**
     * 7012 getMesesNumeroNombreMayorNumeroQuery
     */
    URL7055("REGISTROAPRCOMPAGCONTROLADORURL7055", "7012"),

    /**
     * 45018 getPlanespresupuestalesPagTodasCuentasDebitoQuery
     */
    URL8028("REGISTROAPRCOMPAGCONTROLADORURL8028", "45018"),

    /**
     * 45020 getPlanespresupuestalesPagTodasCuentasDebitoFinalQuery
     */
    URL9002("REGISTROAPRCOMPAGCONTROLADORURL9002", "45020"),

    /**
     * 7007 getMesesPorAnioNo0Y13Query
     */
    URL6474("REGISTROAPRCOMPAGCONTROLADORURL6474", "7007");

    private final String key;
    private final String value;

    private RegistroAprComPagControladorUrlEnum(String key, String value) {
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
