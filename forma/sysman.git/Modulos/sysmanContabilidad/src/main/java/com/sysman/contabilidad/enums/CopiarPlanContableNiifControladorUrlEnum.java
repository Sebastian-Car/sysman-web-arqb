/*
 * CopiarPlanContableNiifControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CopiarPlanContableNiifControladorUrlEnum {
    URL5422("COPIARPLANCONTABLENIIFCONTROLADORURL5422", "16056"),

    URL5423("COPIARPLANCONTABLENIIFCONTROLADORURL5423", "4014"),

    URL5470("COPIARPLANCONTABLENIIFCONTROLADORURL5470", "4016"),

    URL5120("COPIARPLANCONTABLENIIFCONTROLADORURL5120", "4013"),

    URL001("COPIARPLANCONTABLENIIFCONTROLADORURL001", "16112"),

    URL002("COPIARPLANCONTABLENIIFCONTROLADORURL002", "20048");

    private final String key;
    private final String value;

    private CopiarPlanContableNiifControladorUrlEnum(String key, String value) {
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
