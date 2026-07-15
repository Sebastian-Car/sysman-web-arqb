/*
 * TarifasfgControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RptIncentivosControladorUrlEnum {

    URL6170("RPTINCENTIVOSCONTROLADORURL6170","685009"),
    URL4507("RPTINCENTIVOSCONTROLADORURL4507","685011"),
    URL1547("RPTINCENTIVOSCONTROLADORURL1547","685013"),
    URL4986("RPTINCENTIVOSCONTROLADORURL4986","685015");

    private final String key;
    private final String value;

    private RptIncentivosControladorUrlEnum(String key, String value) {
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

