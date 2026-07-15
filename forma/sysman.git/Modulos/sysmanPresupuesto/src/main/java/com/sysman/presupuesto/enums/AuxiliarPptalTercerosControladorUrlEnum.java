/*
 * AuxiliarPptalTercerosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AuxiliarPptalTercerosControladorUrlEnum {

    URL5264("AUXILIARPPTALTERCEROSCONTROLADORURL5264", "25008"),

    URL3640("AUXILIARPPTALTERCEROSCONTROLADORURL3640", "14001"),

    URL4377("AUXILIARPPTALTERCEROSCONTROLADORURL4377", "14026"),

    URL5993("AUXILIARPPTALTERCEROSCONTROLADORURL5993", "25012");

    private final String key;
    private final String value;

    private AuxiliarPptalTercerosControladorUrlEnum(String key, String value) {
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
